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

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        val currentUid = auth.currentUser?.uid ?: ""
        val generatedRefCode = currentUid.take(6).uppercase()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, referralCode = generatedRefCode) }
            val (list, currentUserModel) = fetchRanks(currentUid)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    topUsers = list,
                    currentUser = currentUserModel
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

                var userPosition = 999
                var currentUserModel = LeaderboardUserUiModel(
                    rank = userPosition,
                    userId = currentUid,
                    name = auth.currentUser?.displayName ?: "You",
                    avatarUrl = auth.currentUser?.photoUrl?.toString() ?: "",
                    points = 0L,
                    streak = 0,
                    country = "GLOBAL"
                )

                val mappedList = snapshot.documents.mapIndexed { index, doc ->
                    val rank = index + 1
                    val uid = doc.getString("uid") ?: doc.id
                    val name = doc.getString("displayName") ?: "Striker"
                    val avatar = doc.getString("photoUrl") ?: ""
                    val points = doc.getLong("totalPoints") ?: 0L
                    val streak = doc.getLong("currentStreak")?.toInt() ?: 0
                    val country = doc.getString("country") ?: "US"

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
            } catch (e: Exception) {
                Pair(emptyList(), LeaderboardUserUiModel(999, currentUid, "You", "", 0, 0, "US"))
            }
        }
}

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val topUsers: List<LeaderboardUserUiModel> = emptyList(),
    val currentUser: LeaderboardUserUiModel = LeaderboardUserUiModel(0, "", "", "", 0, 0, ""),
    val referralCode: String = ""
)
