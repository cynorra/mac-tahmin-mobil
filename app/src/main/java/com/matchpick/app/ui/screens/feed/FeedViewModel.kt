package com.matchpick.app.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed("GLOBAL")
    }

    fun loadFeed(region: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val posts = fetchPostsFromFirestore(region)
            _uiState.update { it.copy(posts = posts, isLoading = false, currentRegion = region) }
        }
    }

    private suspend fun fetchPostsFromFirestore(region: String): List<PostUiModel> = withContext(Dispatchers.IO) {
        try {
            val query = if (region == "GLOBAL") {
                firestore.collection("feed_posts")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(20)
            } else {
                firestore.collection("feed_posts")
                    .whereEqualTo("region", region)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(20)
            }

            val snapshot = query.get().await()
            val currentUid = auth.currentUser?.uid ?: ""

            snapshot.documents.mapNotNull { doc ->
                val id = doc.id
                val authorName = doc.getString("authorName") ?: "User"
                val authorPhoto = doc.getString("authorPhoto") ?: ""
                val text = doc.getString("text") ?: ""
                val likes = doc.getLong("likesCount")?.toInt() ?: 0
                val likedBy = doc.get("likedBy") as? List<String> ?: emptyList()
                val attachedPick = doc.getString("attachedPick")
                val timestamp = doc.getTimestamp("createdAt")?.toDate() ?: Date()

                val formattedDate = SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp)

                PostUiModel(
                    id = id,
                    authorName = authorName,
                    authorPhotoUrl = authorPhoto,
                    text = text,
                    timestampFormatted = formattedDate,
                    likesCount = likes,
                    isLiked = likedBy.contains(currentUid),
                    attachedPrediction = attachedPick
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun publishPost(content: String, attachedPick: String? = null) {
        val user = auth.currentUser ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val postMap = hashMapOf(
                "authorId" to user.uid,
                "authorName" to (user.displayName ?: "Striker_${user.uid.take(4)}"),
                "authorPhoto" to (user.photoUrl?.toString() ?: ""),
                "text" to content,
                "region" to _uiState.value.currentRegion,
                "attachedPick" to attachedPick,
                "likesCount" to 0L,
                "likedBy" to emptyList<String>(),
                "createdAt" to FieldValue.serverTimestamp()
            )

            firestore.collection("feed_posts").add(postMap).await()
            loadFeed(_uiState.value.currentRegion) // Listeyi tazele
        }
    }

    fun toggleLike(postId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val postRef = firestore.collection("feed_posts").document(postId)
            val currentPost = _uiState.value.posts.find { it.id == postId } ?: return@launch
            
            val isNowLiked = !currentPost.isLiked
            val delta = if (isNowLiked) 1L else -1L

            // UI'da anında göster (Optimistic Update)
            _uiState.update { state ->
                state.copy(posts = state.posts.map {
                    if (it.id == postId) it.copy(
                        isLiked = isNowLiked,
                        likesCount = (it.likesCount + delta).toInt().coerceAtLeast(0)
                    ) else it
                })
            }

            // Firestore senkronizasyonu
            if (isNowLiked) {
                postRef.update(
                    "likesCount", FieldValue.increment(1L),
                    "likedBy", FieldValue.arrayUnion(uid)
                ).await()
            } else {
                postRef.update(
                    "likesCount", FieldValue.increment(-1L),
                    "likedBy", FieldValue.arrayRemove(uid)
                ).await()
            }
        }
    }
}

data class FeedUiState(
    val isLoading: Boolean = false,
    val posts: List<PostUiModel> = emptyList(),
    val currentRegion: String = "GLOBAL"
)
