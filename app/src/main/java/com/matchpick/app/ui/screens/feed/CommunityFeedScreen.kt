package com.matchpick.app.ui.screens.feed

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.matchpick.app.R
import com.matchpick.app.core.ads.AdaptiveBannerAd
import com.matchpick.app.core.moderation.ClientContentModerator
import com.matchpick.app.core.moderation.ModerationResult
import com.matchpick.app.ui.theme.MatchPickPalette

@Immutable
data class PostUiModel(
    val id: String,
    val authorName: String,
    val authorPhotoUrl: String,
    val text: String,
    val timestampFormatted: String,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val attachedPrediction: String? = null
)

@Composable
fun CommunityFeedScreen(
    posts: List<PostUiModel>,
    onSendPost: (String) -> Unit,
    onLikePost: (String) -> Unit,
    adUnitId: String,
    onRegionSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var postInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Global, 1: Local

    val trendingTags = remember(selectedTab) {
        if (selectedTab == 1) {
            listOf("🔥 #GSvFB Derbi", "⚡ #BJKvTS", "🎯 #KGVar Banko", "🧠 #SüperLigxG")
        } else {
            listOf("🔥 #ElClasico", "⚡ #ARSvMCI", "🎯 #Over2.5", "🧬 #NeuralPick")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MatchPickPalette.DeepObsidian)
            .statusBarsPadding()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = 720.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MatchPickPalette.NeonEmerald),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💬", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(R.string.nav_feed),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.feed_trending_matches),
                        style = MaterialTheme.typography.labelSmall,
                        color = MatchPickPalette.TextSecondary
                    )
                }
            }

            // Segmented Switcher (Global / Local)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MatchPickPalette.SurfaceElevated,
                border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
            ) {
                Row(modifier = Modifier.padding(3.dp)) {
                    listOf(
                        Triple(0, R.string.tab_global, "GLOBAL"),
                        Triple(1, R.string.tab_local, "TR")
                    ).forEach { (idx, labelRes, regionCode) ->
                        val active = selectedTab == idx
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (active) MatchPickPalette.NeonEmerald else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedTab = idx
                                    onRegionSelected(regionCode)
                                }
                        ) {
                            Text(
                                text = stringResource(labelRes),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (active) Color(0xFF031A0E) else MatchPickPalette.TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Trending Tags Strip
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(trendingTags) { tag ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MatchPickPalette.SurfaceDark,
                    border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            if (!postInput.contains(tag)) {
                                postInput = if (postInput.isBlank()) "$tag " else "$postInput $tag "
                            }
                        }
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MatchPickPalette.TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Feed Posts Stream
        if (posts.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.widthIn(max = 420.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MatchPickPalette.SurfaceElevated,
                        border = BorderStroke(1.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.4f)),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = MatchPickPalette.NeonEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.feed_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.feed_empty_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MatchPickPalette.TextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = posts,
                    key = { _, post -> post.id }
                ) { index, post ->
                    PostCard(
                        post = post,
                        onLike = { onLikePost(post.id) }
                    )

                    if (index > 0 && index % 4 == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AdaptiveBannerAd(adUnitId = adUnitId)
                        }
                    }
                }
            }
        }

        // Clean Post Input Bar (Floats above system nav bar)
        Surface(
            color = MatchPickPalette.SurfaceDark,
            border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MatchPickPalette.CrimsonLive,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = postInput,
                        onValueChange = {
                            postInput = it
                            errorMessage = null
                        },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.post_input_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MatchPickPalette.TextMuted
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MatchPickPalette.SurfaceElevated,
                            unfocusedContainerColor = MatchPickPalette.SurfaceElevated,
                            focusedTextColor = MatchPickPalette.TextPrimary,
                            unfocusedTextColor = MatchPickPalette.TextPrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            val trimmed = postInput.trim()
                            if (trimmed.isNotBlank()) {
                                when (val mod = ClientContentModerator.validatePost(trimmed)) {
                                    is ModerationResult.Valid -> {
                                        onSendPost(trimmed)
                                        postInput = ""
                                        errorMessage = null
                                    }
                                    is ModerationResult.Invalid -> {
                                        errorMessage = mod.reason
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MatchPickPalette.NeonEmerald)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(R.string.btn_publish),
                            tint = Color(0xFF031A0E),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: PostUiModel,
    onLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatchPickPalette.GlassCardGradient)
                .padding(14.dp)
        ) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = MatchPickPalette.SurfaceElevated,
                        border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                    ) {
                        if (post.authorPhotoUrl.isNotBlank()) {
                            AsyncImage(
                                model = post.authorPhotoUrl,
                                contentDescription = post.authorName,
                                modifier = Modifier.fillMaxSize().padding(2.dp).clip(CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = post.authorName.firstOrNull()?.uppercase() ?: "A",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MatchPickPalette.NeonEmerald
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MatchPickPalette.TextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = MatchPickPalette.NeonEmerald,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = post.timestampFormatted,
                            style = MaterialTheme.typography.labelSmall,
                            color = MatchPickPalette.TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Like Button
                Surface(
                    onClick = onLike,
                    shape = RoundedCornerShape(10.dp),
                    color = if (post.isLiked) Color(0xFF26101B) else MatchPickPalette.SurfaceElevated,
                    border = BorderStroke(
                        0.5.dp,
                        if (post.isLiked) MatchPickPalette.CrimsonLive.copy(alpha = 0.5f) else MatchPickPalette.BorderSubtle
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (post.isLiked) MatchPickPalette.CrimsonLive else MatchPickPalette.TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.likesCount}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (post.isLiked) MatchPickPalette.CrimsonLive else MatchPickPalette.TextSecondary
                        )
                    }
                }
            }

            // Attached Prediction Badge
            post.attachedPrediction?.let { pick ->
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MatchPickPalette.EmeraldDeep,
                    border = BorderStroke(0.5.dp, MatchPickPalette.NeonEmerald)
                ) {
                    Text(
                        text = "🎯 $pick",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.NeonEmerald,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Text
            Text(
                text = post.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MatchPickPalette.TextPrimary,
                lineHeight = 20.sp
            )
        }
    }
}
