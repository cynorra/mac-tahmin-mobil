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
        if (!uid.isNullOrEmpty()) {
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    val pts = doc.getLong("totalPoints") ?: 0L
                    val stk = doc.getLong("currentStreak")?.toInt() ?: 0
                    _uiState.update { it.copy(points = pts, streak = stk) }
                }
            } catch (_: Exception) {}
        }
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

    fun makePrediction(fixtureId: String, rawPick: String) {
        val pick = when (rawPick) {
            "HOME" -> "1"
            "DRAW" -> "X"
            "AWAY" -> "2"
            else -> rawPick
        }
        val uid = auth.currentUser?.uid.orEmpty()
        val matchedFixture = _uiState.value.fixtures.find { it.id == fixtureId }
        val odds = when (pick) {
            "1" -> matchedFixture?.oddsHome?.toDouble() ?: 2.10
            "X" -> matchedFixture?.oddsDraw?.toDouble() ?: 3.30
            "2" -> matchedFixture?.oddsAway?.toDouble() ?: 3.20
            else -> 2.10
        }

        analyticsTracker.logPredictionSubmitted(
            fixtureId = fixtureId,
            pick = pick,
            odds = odds,
            league = matchedFixture?.leagueName ?: _uiState.value.selectedCountry
        )
        analyticsTracker.logPointsEarned(10L, "prediction_submission")

        viewModelScope.launch {
            if (uid.isNotEmpty()) {
                repository.savePrediction(fixtureId, pick, uid)
                try {
                    val userUpdates = hashMapOf<String, Any>(
                        "uid" to uid,
                        "totalPoints" to FieldValue.increment(10L),
                        "currentStreak" to FieldValue.increment(1L),
                        "lastPredictionAt" to FieldValue.serverTimestamp()
                    )
                    auth.currentUser?.displayName?.let { userUpdates["displayName"] = it }
                    firestore.collection("users").document(uid).set(userUpdates, SetOptions.merge()).await()
                } catch (_: Exception) {}
            }

            _uiState.update { state ->
                val newStreak = state.streak + 1
                if (newStreak > 0 && newStreak % 3 == 0) {
                    analyticsTracker.logStreakMilestone(newStreak)
                }
                state.copy(
                    streak = newStreak,
                    points = state.points + 10,
                    fixtures = state.fixtures.map {
                        if (it.id == fixtureId) it.copy(userPick = pick) else it
                    }
                )
            }
        }
    }
}

data class PredictUiState(
    val isLoading: Boolean = false,
    val fixtures: List<FixtureEntity> = emptyList(),
    val streak: Int = 0,
    val points: Long = 0,
    val selectedCountry: String = "US"
)
