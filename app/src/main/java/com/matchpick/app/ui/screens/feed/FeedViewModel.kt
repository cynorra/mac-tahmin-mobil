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
import com.matchpick.app.data.local.FixtureEntity
import com.matchpick.app.data.repository.FixtureRepository
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val analyticsTracker: AnalyticsTracker,
    private val fixtureRepository: FixtureRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FeedUiState(
            posts = emptyList(),
            currentRegion = "GLOBAL",
            trendingTags = emptyList()
        )
    )
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var cachedFixtures: List<FixtureEntity> = emptyList()

    init {
        loadFeed("GLOBAL")
        observeRealFixtures()
    }

    private fun observeRealFixtures() {
        viewModelScope.launch {
            try {
                fixtureRepository.getFixtures("").collect { fixtures ->
                    cachedFixtures = fixtures
                    recalculateTags(fixtures, _uiState.value.posts, _uiState.value.currentRegion)
                }
            } catch (_: Exception) {}
        }
    }

    fun loadFeed(region: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentRegion = region) }
            val fetchedPosts = fetchPostsFromFirestore(region)
            _uiState.update { it.copy(posts = fetchedPosts, isLoading = false) }
            recalculateTags(cachedFixtures, fetchedPosts, region)
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
        val updatedPosts = listOf(newPost) + _uiState.value.posts
        _uiState.update { it.copy(posts = updatedPosts) }
        recalculateTags(cachedFixtures, updatedPosts, _uiState.value.currentRegion)
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

    private fun recalculateTags(
        fixtures: List<FixtureEntity>,
        posts: List<PostUiModel>,
        region: String
    ) {
        val tagsSet = linkedSetOf<String>()

        // 1. Gerçek kullanıcı gönderilerindeki hashtag'leri dinamik topla
        val hashtagRegex = Regex("#[a-zA-Z0-9çğıöşüÇĞİÖŞÜ_]+")
        posts.forEach { post ->
            hashtagRegex.findAll(post.text).forEach { match ->
                val tag = match.value
                if (tag.length in 3..25) {
                    tagsSet.add(tag)
                }
            }
        }

        // 2. Bugünün veritabanındaki GERÇEK canlı maçlarından etiket üret (Asla sahte maç yok!)
        val relevantFixtures = if (region == "GLOBAL") {
            fixtures
        } else {
            fixtures.filter { it.countryCode.equals(region, ignoreCase = true) || it.countryCode == "GLOBAL" }
                .ifEmpty { fixtures }
        }

        relevantFixtures.take(6).forEach { fix ->
            val homeClean = fix.homeTeamName.split(" ").firstOrNull()?.replace(Regex("[^a-zA-Z0-9çğıöşüÇĞİÖŞÜ]"), "") ?: ""
            val awayClean = fix.awayTeamName.split(" ").firstOrNull()?.replace(Regex("[^a-zA-Z0-9çğıöşüÇĞİÖŞÜ]"), "") ?: ""
            if (homeClean.isNotEmpty() && awayClean.isNotEmpty()) {
                tagsSet.add("#${homeClean}v${awayClean}")
            }
            val leagueClean = fix.leagueName.replace(Regex("[^a-zA-Z0-9çğıöşüÇĞİÖŞÜ]"), "")
            if (leagueClean.isNotEmpty() && leagueClean.length <= 15) {
                tagsSet.add("#$leagueClean")
            }
        }

        _uiState.update { it.copy(trendingTags = tagsSet.toList()) }
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
    val currentRegion: String = "GLOBAL",
    val trendingTags: List<String> = emptyList()
)
