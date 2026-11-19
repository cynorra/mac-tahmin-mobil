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
            posts = getDefaultPosts("GLOBAL"),
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
            val finalPosts = fetchedPosts.ifEmpty { getDefaultPosts(region) }
            _uiState.update { it.copy(posts = finalPosts, isLoading = false) }
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
            val currentUid = auth.currentUser?.uid ?: "guest_user"

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
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun publishPost(content: String, attachedPick: String? = null) {
        if (content.isBlank()) return
        val user = auth.currentUser
        val authorId = user?.uid ?: "guest_${System.currentTimeMillis() % 10000}"
        val authorName = user?.displayName ?: "Misafir Analist"
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
        val uid = auth.currentUser?.uid ?: "guest_user"
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

    private fun getDefaultPosts(region: String): List<PostUiModel> {
        return if (region == "TR") {
            listOf(
                PostUiModel(
                    id = "tr_feed_1",
                    authorName = "Burak K.",
                    authorPhotoUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8637.png",
                    text = "Beşiktaş - Trabzonspor derbisinde Semih Kılıçsoy ilk 11 başlıyor. Karşılıklı Gol Var (KG Var) oranı 1.78 kaçmaz! 🔥",
                    timestampFormatted = "14:20",
                    likesCount = 28,
                    isLiked = false,
                    attachedPrediction = "Beşiktaş vs Trabzonspor -> KG Var"
                ),
                PostUiModel(
                    id = "tr_feed_2",
                    authorName = "Rıdvan Taktik",
                    authorPhotoUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8695.png",
                    text = "Fenerbahçe Kadıköy'de müthiş baskıyla başlıyor. İlk yarı 1.5 Üst taktiği banko duruyor.",
                    timestampFormatted = "13:45",
                    likesCount = 34,
                    isLiked = true,
                    attachedPrediction = "Fenerbahçe vs Adana Demirspor -> İY 1.5 Üst"
                ),
                PostUiModel(
                    id = "tr_feed_3",
                    authorName = "Aslan Yüreği",
                    authorPhotoUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8637.png",
                    text = "Galatasaray deplasmanda Osimhen ve Icardi ikilisiyle yine gol şov yapacaktır. Maç Sonucu 2 & 2.5 Üst!",
                    timestampFormatted = "12:10",
                    likesCount = 56,
                    isLiked = false,
                    attachedPrediction = "Sivasspor vs Galatasaray -> 2 & 2.5 Üst"
                )
            )
        } else {
            listOf(
                PostUiModel(
                    id = "global_feed_1",
                    authorName = "Liam Tactical",
                    authorPhotoUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/9825.png",
                    text = "Arsenal vs Man City title race battle. Rice and Rodri midfield duel will decide the whole match tempo. Under 2.5 goals looks very sharp.",
                    timestampFormatted = "15:45",
                    likesCount = 42,
                    isLiked = false,
                    attachedPrediction = "Arsenal vs Manchester City -> Under 2.5"
                ),
                PostUiModel(
                    id = "global_feed_2",
                    authorName = "Carlos Madrid",
                    authorPhotoUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png",
                    text = "El Clásico at Bernabéu! Vini Jr and Bellingham always deliver in big matches. Expecting early goals.",
                    timestampFormatted = "14:10",
                    likesCount = 68,
                    isLiked = true,
                    attachedPrediction = "Real Madrid vs FC Barcelona -> 1X & Over 2.5"
                ),
                PostUiModel(
                    id = "global_feed_3",
                    authorName = "Marco Interista",
                    authorPhotoUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8636.png",
                    text = "Inter Milan solid defense at San Siro: Clean sheet for Sommer is highly probable against Juventus.",
                    timestampFormatted = "11:30",
                    likesCount = 31,
                    isLiked = false,
                    attachedPrediction = "Inter Milan vs Juventus -> 1"
                )
            )
        }
    }
}

data class FeedUiState(
    val isLoading: Boolean = false,
    val posts: List<PostUiModel> = emptyList(),
    val currentRegion: String = "GLOBAL"
)
