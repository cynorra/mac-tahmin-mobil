package com.matchpick.app.ui.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.matchpick.app.data.repository.FixtureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val fixtureRepository: FixtureRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LeaderboardUiState(
            topUsers = emptyList(),
            currentUser = LeaderboardUserUiModel(
                rank = 1,
                userId = "",
                name = "Me",
                avatarUrl = "",
                points = 0L,
                streak = 0,
                country = "GLOBAL"
            ),
            referralCode = "MATCH2026"
        )
    )
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        val user = auth.currentUser
        val currentUid = user?.uid ?: ""
        val currentName = user?.displayName ?: "Me"
        val currentPhoto = user?.photoUrl?.toString() ?: ""
        val generatedRefCode = if (currentUid.isNotEmpty()) currentUid.take(6).uppercase() else "MATCH2026"

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, referralCode = generatedRefCode) }
            val (list, currentUserModel) = fetchRanks(currentUid, currentName, currentPhoto)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    topUsers = list,
                    currentUser = currentUserModel
                )
            }
        }
    }

    private suspend fun fetchRanks(
        currentUid: String,
        currentName: String,
        currentPhoto: String
    ): Pair<List<LeaderboardUserUiModel>, LeaderboardUserUiModel> =
        withContext(Dispatchers.IO) {
            var userPoints = 0L
            var userStreak = 0

            if (currentUid.isNotEmpty()) {
                try {
                    val userDoc = firestore.collection("users").document(currentUid).get().await()
                    if (userDoc.exists()) {
                        userPoints = userDoc.getLong("totalPoints") ?: 0L
                        userStreak = userDoc.getLong("currentStreak")?.toInt() ?: 0
                    }
                } catch (_: Exception) {}
            }

            // Yerel Room veritabanından yapılan tahmin sayısını kontrol et
            if (userPoints == 0L) {
                try {
                    val count = fixtureRepository.getUserPredictionCount()
                    userPoints = count * 10L
                    userStreak = count
                } catch (_: Exception) {}
            }

            try {
                val snapshot = firestore.collection("users")
                    .orderBy("totalPoints", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .await()

                if (!snapshot.isEmpty) {
                    var currentUserModel: LeaderboardUserUiModel? = null

                    val mappedList = snapshot.documents.mapIndexed { index, doc ->
                        val rank = index + 1
                        val uid = doc.getString("uid") ?: doc.id
                        val name = doc.getString("displayName") ?: "Analyst #$rank"
                        val avatar = doc.getString("photoUrl") ?: ""
                        val points = doc.getLong("totalPoints") ?: 0L
                        val streak = doc.getLong("currentStreak")?.toInt() ?: 0
                        val country = doc.getString("country") ?: "GLOBAL"

                        val model = LeaderboardUserUiModel(
                            rank = rank,
                            userId = uid,
                            name = name,
                            avatarUrl = avatar,
                            points = points,
                            streak = streak,
                            country = country
                        )

                        if (uid == currentUid) {
                            currentUserModel = model
                        }
                        model
                    }

                    val finalCurrent = currentUserModel ?: LeaderboardUserUiModel(
                        rank = mappedList.size + 1,
                        userId = currentUid,
                        name = currentName,
                        avatarUrl = currentPhoto,
                        points = userPoints,
                        streak = userStreak,
                        country = "GLOBAL"
                    )
                    return@withContext Pair(mappedList, finalCurrent)
                }
            } catch (e: Exception) {
                android.util.Log.w("LeaderboardViewModel", "Firestore leaderboard fetch error: ${e.message}")
            }

            // Mock / sahte veriler kaldırıldı; sadece gerçek kullanıcı/sıralama verisi kullanılır
            val defaultCurrent = LeaderboardUserUiModel(
                rank = 1,
                userId = currentUid.ifEmpty { "me" },
                name = currentName,
                avatarUrl = currentPhoto,
                points = userPoints,
                streak = userStreak,
                country = "GLOBAL"
            )

            val realUsersList = if (userPoints > 0L) {
                listOf(defaultCurrent)
            } else {
                emptyList()
            }

            Pair(realUsersList, defaultCurrent)
        }
}

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val topUsers: List<LeaderboardUserUiModel> = emptyList(),
    val currentUser: LeaderboardUserUiModel = LeaderboardUserUiModel(1, "", "", "", 0L, 0, "GLOBAL"),
    val referralCode: String = "MATCH2026"
)
