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
            topUsers = emptyList(),
            currentUser = LeaderboardUserUiModel(
                rank = 1,
                userId = "current_user",
                name = "Me",
                avatarUrl = "",
                points = 140,
                streak = 3,
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
            val defaultCurrent = LeaderboardUserUiModel(
                rank = 1,
                userId = if (currentUid.isNotEmpty()) currentUid else "current_user",
                name = currentName,
                avatarUrl = currentPhoto,
                points = 140,
                streak = 3,
                country = "GLOBAL"
            )

            try {
                val snapshot = firestore.collection("users")
                    .orderBy("totalPoints", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .await()

                if (snapshot.isEmpty) {
                    return@withContext Pair(emptyList(), defaultCurrent)
                }

                var currentUserModel: LeaderboardUserUiModel? = null

                val mappedList = snapshot.documents.mapIndexed { index, doc ->
                    val rank = index + 1
                    val uid = doc.getString("uid") ?: doc.id
                    val name = doc.getString("displayName") ?: "Striker #$rank"
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

                Pair(mappedList, currentUserModel ?: defaultCurrent.copy(rank = mappedList.size + 1))
            } catch (_: Exception) {
                Pair(emptyList(), defaultCurrent)
            }
        }
}

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val topUsers: List<LeaderboardUserUiModel> = emptyList(),
    val currentUser: LeaderboardUserUiModel = LeaderboardUserUiModel(1, "", "", "", 140, 3, "GLOBAL"),
    val referralCode: String = "MATCH2026"
)
