package com.matchpick.app.ui.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LeaderboardUiState(
            topUsers = getDefaultLeaderboard(),
            currentUser = getDefaultCurrentUser(""),
            referralCode = "MATCH2026"
        )
    )
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        val currentUid = auth.currentUser?.uid ?: ""
        val generatedRefCode = if (currentUid.isNotEmpty()) currentUid.take(6).uppercase() else "MATCH2026"

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, referralCode = generatedRefCode) }
            val (list, currentUserModel) = fetchRanks(currentUid)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    topUsers = list.ifEmpty { getDefaultLeaderboard() },
                    currentUser = if (currentUserModel.points > 0) currentUserModel else getDefaultCurrentUser(currentUid)
                )
            }
        }
    }

    private suspend fun fetchRanks(currentUid: String): Pair<List<LeaderboardUserUiModel>, LeaderboardUserUiModel> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("users")
                    .orderBy("totalPoints", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .await()

                if (snapshot.isEmpty) {
                    return@withContext Pair(getDefaultLeaderboard(), getDefaultCurrentUser(currentUid))
                }

                var userPosition = 999
                var currentUserModel = getDefaultCurrentUser(currentUid)

                val mappedList = snapshot.documents.mapIndexed { index, doc ->
                    val rank = index + 1
                    val uid = doc.getString("uid") ?: doc.id
                    val name = doc.getString("displayName") ?: "Striker"
                    val avatar = doc.getString("photoUrl") ?: ""
                    val points = doc.getLong("totalPoints") ?: 0L
                    val streak = doc.getLong("currentStreak")?.toInt() ?: 0
                    val country = doc.getString("country") ?: "TR"

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
                        userPosition = rank
                        currentUserModel = model
                    }
                    model
                }

                Pair(mappedList, currentUserModel)
            } catch (_: Exception) {
                Pair(getDefaultLeaderboard(), getDefaultCurrentUser(currentUid))
            }
        }

    companion object {
        fun getDefaultLeaderboard(): List<LeaderboardUserUiModel> = listOf(
            LeaderboardUserUiModel(
                rank = 1,
                userId = "u1",
                name = "Cem Yılmaz",
                avatarUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8637.png",
                points = 1840,
                streak = 11,
                country = "TR"
            ),
            LeaderboardUserUiModel(
                rank = 2,
                userId = "u2",
                name = "David Beckham",
                avatarUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/9825.png",
                points = 1650,
                streak = 9,
                country = "GB"
            ),
            LeaderboardUserUiModel(
                rank = 3,
                userId = "u3",
                name = "Hakan Şükür",
                avatarUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8695.png",
                points = 1480,
                streak = 8,
                country = "TR"
            ),
            LeaderboardUserUiModel(
                rank = 4,
                userId = "u4",
                name = "Marcus Rashford",
                avatarUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8456.png",
                points = 1250,
                streak = 6,
                country = "GB"
            ),
            LeaderboardUserUiModel(
                rank = 5,
                userId = "u5",
                name = "Eren Şimşir",
                avatarUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png",
                points = 980,
                streak = 5,
                country = "TR"
            ),
            LeaderboardUserUiModel(
                rank = 6,
                userId = "u6",
                name = "Kylian Mbappé",
                avatarUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png",
                points = 890,
                streak = 4,
                country = "FR"
            ),
            LeaderboardUserUiModel(
                rank = 7,
                userId = "u7",
                name = "Arda Güler",
                avatarUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png",
                points = 760,
                streak = 4,
                country = "TR"
            )
        )

        fun getDefaultCurrentUser(uid: String): LeaderboardUserUiModel = LeaderboardUserUiModel(
            rank = 5,
            userId = if (uid.isNotEmpty()) uid else "current_user",
            name = "Eren Şimşir",
            avatarUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png",
            points = 980,
            streak = 5,
            country = "TR"
        )
    }
}

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val topUsers: List<LeaderboardUserUiModel> = emptyList(),
    val currentUser: LeaderboardUserUiModel = LeaderboardUserUiModel(0, "", "", "", 0, 0, ""),
    val referralCode: String = ""
)
