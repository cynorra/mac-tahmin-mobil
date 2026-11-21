package com.matchpick.app.ui.screens.predict

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.matchpick.app.core.analytics.AnalyticsTracker
import com.matchpick.app.data.local.FixtureEntity
import com.matchpick.app.data.local.LiveFixtureSeeder
import com.matchpick.app.data.repository.FixtureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PredictViewModel @Inject constructor(
    private val repository: FixtureRepository,
    private val liveFixtureSeeder: LiveFixtureSeeder,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(PredictUiState())
    val uiState: StateFlow<PredictUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            liveFixtureSeeder.seedLiveFixturesIfEmpty()
            loadUserStats()
        }
        loadFixtures("US")
        syncDailyData()
    }

    private suspend fun loadUserStats() {
        val uid = auth.currentUser?.uid
        var remotePoints: Long? = null
        var remoteStreak: Int? = null

        if (!uid.isNullOrEmpty()) {
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    remotePoints = doc.getLong("totalPoints")
                    remoteStreak = doc.getLong("currentStreak")?.toInt()
                }
            } catch (_: Exception) {}
        }

        // Yerel veritabanında kullanıcının yaptığı gerçek tekil tahmin sayısını hesapla
        val localCount = try {
            repository.getUserPredictionCount()
        } catch (_: Exception) { 0 }

        val legitimateLocalPoints = localCount * 10L

        // Misafir modunda veya aşırı şişirilmiş exploit durumunda istatistikleri doğrula
        val finalPoints = if (uid.isNullOrEmpty()) {
            legitimateLocalPoints
        } else {
            val remote = remotePoints ?: legitimateLocalPoints
            maxOf(legitimateLocalPoints, remote.coerceAtMost(maxOf(legitimateLocalPoints + 50L, 500L)))
        }

        val finalStreak = if (uid.isNullOrEmpty()) {
            localCount
        } else {
            (remoteStreak ?: localCount).coerceAtMost(maxOf(localCount, 1))
        }

        _uiState.update { it.copy(points = finalPoints, streak = finalStreak) }
    }

    fun loadFixtures(region: String) {
        viewModelScope.launch {
            repository.getFixtures(region).collect { list ->
                _uiState.update { it.copy(fixtures = list, isLoading = false, selectedCountry = region) }
            }
        }
    }

    private fun syncDailyData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.refreshDailyFixtures()
        }
    }

    /**
     * Maç Tahmini Kaydet / Güncelle.
     * Güvenlik & Anti-Exploit Politikası:
     * 1. Her maç için SADECE İLK tahminde +10 XP ve +1 seri kazandırılır.
     * 2. Kullanıcı aynı maça başka bir seçim yaptığında (1 -> X vb.) seçim güncellenir ancak XP/Seri VERİLMEZ.
     * 3. Zaten seçili olan tercihe tekrar basıldığında hiçbir işlem yapılmaz (No-op).
     * 4. Kilitli veya başlamış maçlara tahmin yapılamaz.
     */
    fun makePrediction(fixtureId: String, rawPick: String): PredictionOutcome {
        val pick = when (rawPick) {
            "HOME" -> "1"
            "DRAW" -> "X"
            "AWAY" -> "2"
            else -> rawPick
        }

        val currentState = _uiState.value
        val matchedFixture = currentState.fixtures.find { it.id == fixtureId }
            ?: return PredictionOutcome.ALREADY_SELECTED

        if (matchedFixture.isLocked) {
            return PredictionOutcome.LOCKED
        }

        // Zaten bu tercih seçiliyse hiçbir işlem yapma
        if (matchedFixture.userPick == pick) {
            return PredictionOutcome.ALREADY_SELECTED
        }

        val isFirstPredictionOnFixture = matchedFixture.userPick.isNullOrEmpty()
        val uid = auth.currentUser?.uid.orEmpty()
        val odds = when (pick) {
            "1" -> matchedFixture.oddsHome.toDouble()
            "X" -> matchedFixture.oddsDraw.toDouble()
            "2" -> matchedFixture.oddsAway.toDouble()
            else -> 2.10
        }

        // 1. Atomik UI State Güncellemesi (Tıklama yarış durumlarını ve çoklu tetiklemeyi önler)
        _uiState.update { state ->
            val newStreak = if (isFirstPredictionOnFixture) state.streak + 1 else state.streak
            val newPoints = if (isFirstPredictionOnFixture) state.points + 10L else state.points

            state.copy(
                streak = newStreak,
                points = newPoints,
                fixtures = state.fixtures.map {
                    if (it.id == fixtureId) it.copy(userPick = pick) else it
                }
            )
        }

        analyticsTracker.logPredictionSubmitted(
            fixtureId = fixtureId,
            pick = pick,
            odds = odds,
            league = matchedFixture.leagueName.ifEmpty { currentState.selectedCountry }
        )

        // SADECE maç için ilk tahminde XP ve seri kilometre taşı loglanır
        if (isFirstPredictionOnFixture) {
            analyticsTracker.logPointsEarned(10L, "prediction_submission")
            val newStreak = currentState.streak + 1
            if (newStreak > 0 && newStreak % 3 == 0) {
                analyticsTracker.logStreakMilestone(newStreak)
            }
        }

        // 2. Arka Plan Veritabanı ve Firestore Senkronizasyonu
        viewModelScope.launch {
            try {
                repository.savePrediction(fixtureId, pick, uid)
            } catch (e: Exception) {
                android.util.Log.w("MatchPickBackend", "Prediction local save fallback: ${e.message}")
            }

            if (uid.isNotEmpty()) {
                try {
                    val userUpdates = hashMapOf<String, Any>(
                        "uid" to uid,
                        "lastPredictionAt" to FieldValue.serverTimestamp()
                    )
                    // Puan/seri SADECE backend (score_calculator.py) tarafından yazılır; kurallar istemci yazmasını reddeder.
                    auth.currentUser?.displayName?.let { userUpdates["displayName"] = it }
                    firestore.collection("users").document(uid).set(userUpdates, SetOptions.merge()).await()
                } catch (_: Exception) {}
            }
        }

        return if (isFirstPredictionOnFixture) PredictionOutcome.AWARDED else PredictionOutcome.UPDATED
    }
}

enum class PredictionOutcome {
    AWARDED,          // İlk tahmin: +10 XP ve +1 seri kazanıldı
    UPDATED,          // Tercih güncellendi: XP verilmedi (maç başına 1 kez kuralı)
    ALREADY_SELECTED, // Zaten bu tercih seçili (no-op)
    LOCKED            // Maç kilitli / başlamış
}

data class PredictUiState(
    val isLoading: Boolean = false,
    val fixtures: List<FixtureEntity> = emptyList(),
    val streak: Int = 0,
    val points: Long = 0,
    val selectedCountry: String = "US"
)
