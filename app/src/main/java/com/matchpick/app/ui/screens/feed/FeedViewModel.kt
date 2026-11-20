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
import com.matchpick.app.core.analytics.AnalyticsTracker
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FeedUiState(
            posts = emptyList(),
            currentRegion = "GLOBAL"
        )
    )
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed("GLOBAL")
    }

    fun loadFeed(region: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentRegion = region) }
            val fetchedPosts = fetchPostsFromFirestore(region)
            _uiState.update { it.copy(posts = fetchedPosts, isLoading = false) }
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
            val currentUid = auth.currentUser?.uid.orEmpty()

            val list = snapshot.documents.mapNotNull { doc ->
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
            list
        } catch (e: Exception) {
            android.util.Log.w("FeedViewModel", "Firestore feed fetch error: ${e.message}")
            emptyList()
        }
    }

    fun publishPost(content: String, attachedPick: String? = null) {
        if (content.isBlank()) return
        val user = auth.currentUser
        val authorId = user?.uid ?: "guest_${System.currentTimeMillis() % 10000}"
        val authorName = user?.displayName ?: if (Locale.getDefault().language == "tr") "Misafir Analist" else "Guest Analyst"
        val authorPhoto = user?.photoUrl?.toString() ?: ""
        val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val newPost = PostUiModel(
            id = "local_${System.currentTimeMillis()}",
            authorName = authorName,
            authorPhotoUrl = authorPhoto,
            text = content,
            timestampFormatted = formattedTime,
            likesCount = 0,
            isLiked = false,
            attachedPrediction = attachedPick
        )

        // Optimistic UI update: hemen en üste ekle
        _uiState.update { it.copy(posts = listOf(newPost) + it.posts) }
        analyticsTracker.logCommunityPostPublished(_uiState.value.currentRegion, attachedPick != null)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val postMap = hashMapOf(
                    "authorId" to authorId,
                    "authorName" to authorName,
                    "authorPhoto" to authorPhoto,
                    "text" to content,
                    "region" to _uiState.value.currentRegion,
                    "attachedPick" to attachedPick,
                    "likesCount" to 0L,
                    "likedBy" to emptyList<String>(),
                    "createdAt" to FieldValue.serverTimestamp()
                )
                firestore.collection("feed_posts").add(postMap).await()
            } catch (_: Exception) {
                // UI durumu zaten eklendi
            }
        }
    }

    fun toggleLike(postId: String) {
        val uid = auth.currentUser?.uid.takeIf { !it.isNullOrBlank() } ?: "guest_user"
        val currentPost = _uiState.value.posts.find { it.id == postId } ?: return
        val isNowLiked = !currentPost.isLiked
        val delta = if (isNowLiked) 1 else -1

        // Optimistic update
        _uiState.update { state ->
            state.copy(posts = state.posts.map {
                if (it.id == postId) it.copy(
                    isLiked = isNowLiked,
                    likesCount = (it.likesCount + delta).coerceAtLeast(0)
                ) else it
            })
        }
        analyticsTracker.logCommunityLikeToggled(postId, isNowLiked)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val postRef = firestore.collection("feed_posts").document(postId)
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
            } catch (_: Exception) {
                // Misafir veya offline modunda yerel durum korunur
            }
        }
    }
}

data class FeedUiState(
    val isLoading: Boolean = false,
    val posts: List<PostUiModel> = emptyList(),
    val currentRegion: String = "GLOBAL"
)
