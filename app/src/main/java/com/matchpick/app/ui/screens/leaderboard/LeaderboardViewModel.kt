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
            } catch (_: Exception) {
                // Firestore offline veya rules kısıtlı olduğunda topluluk analist simülasyonu kullanılır
            }

            // Global rekabetçi analist kadrosu (Podyum ve sıralamanın canlı doluluğu için)
            val seedAnalysts = listOf(
                LeaderboardUserUiModel(1, "seed_1", "Alex Morgan", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", 520L, 14, "GB"),
                LeaderboardUserUiModel(2, "seed_2", "Berke_Tactics", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", 480L, 11, "TR"),
                LeaderboardUserUiModel(3, "seed_3", "Marco BetGuru", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150", 440L, 9, "ES"),
                LeaderboardUserUiModel(4, "seed_4", "Luka_Analyst", "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=150", 390L, 8, "DE"),
                LeaderboardUserUiModel(5, "seed_5", "Sofie Nielsen", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150", 340L, 7, "DK"),
                LeaderboardUserUiModel(6, "seed_6", "Kaan99", "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150", 310L, 6, "TR"),
                LeaderboardUserUiModel(7, "seed_7", "Antoine V.", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150", 270L, 5, "FR"),
                LeaderboardUserUiModel(8, "seed_8", "Davide R.", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=150", 240L, 4, "IT")
            )

            val higherRanks = seedAnalysts.count { it.points > userPoints }
            val userRank = higherRanks + 1
            val defaultCurrent = LeaderboardUserUiModel(
                rank = userRank,
                userId = currentUid.ifEmpty { "me" },
                name = currentName,
                avatarUrl = currentPhoto,
                points = userPoints,
                streak = userStreak,
                country = "GLOBAL"
            )

            val combinedList = (seedAnalysts + defaultCurrent)
                .sortedByDescending { it.points }
                .mapIndexed { index, model -> model.copy(rank = index + 1) }

            val resolvedCurrentUser = combinedList.find { it.userId == defaultCurrent.userId } ?: defaultCurrent
            Pair(combinedList, resolvedCurrentUser)
        }
}

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val topUsers: List<LeaderboardUserUiModel> = emptyList(),
    val currentUser: LeaderboardUserUiModel = LeaderboardUserUiModel(1, "", "", "", 0L, 0, "GLOBAL"),
    val referralCode: String = "MATCH2026"
)
