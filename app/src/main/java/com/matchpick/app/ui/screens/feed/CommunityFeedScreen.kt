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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

    Scaffold(
        containerColor = MatchPickPalette.DeepObsidian,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0A1426), MatchPickPalette.DeepObsidian)
                        )
                    )
                    .statusBarsPadding()
            ) {
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MatchPickPalette.NeonBrandGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💬", fontSize = 19.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.nav_feed),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = stringResource(R.string.feed_trending_matches),
                                style = MaterialTheme.typography.labelSmall,
                                color = MatchPickPalette.ElectricCyan,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // 2026+ Segmented Pill Switcher (Global / Local)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF0E1629),
                        border = BorderStroke(1.dp, Color(0x2864748B))
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            listOf(
                                Triple(0, R.string.tab_global, "GLOBAL"),
                                Triple(1, R.string.tab_local, "TR")
                            ).forEach { (idx, labelRes, regionCode) ->
                                val active = selectedTab == idx
                                Surface(
                                    shape = RoundedCornerShape(15.dp),
                                    color = if (active) MatchPickPalette.NeonEmerald else Color.Transparent,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(15.dp))
                                        .clickable {
                                            selectedTab = idx
                                            onRegionSelected(regionCode)
                                        }
                                ) {
                                    Text(
                                        text = stringResource(labelRes),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = if (active) Color(0xFF03170E) else MatchPickPalette.TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Trending Derby Tags Strip
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(trendingTags) { tag ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF111A2E),
                            border = BorderStroke(1.dp, Color(0x2800E5FF)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (!postInput.contains(tag)) {
                                        postInput = if (postInput.isBlank()) "$tag " else "$postInput $tag "
                                    }
                                }
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MatchPickPalette.ElectricCyan,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        },
        bottomBar = {
            Surface(
                color = Color(0xFF0A101F),
                border = BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(Color(0x3300F59B), Color(0x2200E5FF))
                    )
                ),
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MatchPickPalette.CrimsonLive,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 6.dp)
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
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF131D34),
                                unfocusedContainerColor = Color(0xFF131D34),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        IconButton(
                            onClick = {
                                val validation = ClientContentModerator.validatePost(postInput)
                                if (validation is ModerationResult.Invalid) {
                                    errorMessage = validation.reason
                                } else if (postInput.isNotBlank()) {
                                    onSendPost(postInput.trim())
                                    postInput = ""
                                    errorMessage = null
                                }
                            },
                            enabled = postInput.isNotBlank(),
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (postInput.isNotBlank()) {
                                        MatchPickPalette.NeonBrandGradient
                                    } else {
                                        Brush.linearGradient(listOf(Color(0xFF1E293B), Color(0xFF1E293B)))
                                    }
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = stringResource(R.string.btn_publish),
                                tint = if (postInput.isNotBlank()) Color(0xFF03170E) else MatchPickPalette.TextMuted
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                    AdaptiveBannerAd(
                        adUnitId = adUnitId,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
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
        shape = RoundedCornerShape(22.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(Color(0x3300F59B), Color(0x15FFFFFF), Color(0x2200E5FF))
            )
        ),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatchPickPalette.GlassCardGradient)
                .padding(16.dp)
        ) {
            // Author row with Verified Analyst Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = Color(0xFF19243D),
                        border = BorderStroke(
                            1.5.dp,
                            Brush.linearGradient(
                                listOf(MatchPickPalette.NeonEmerald, MatchPickPalette.ElectricCyan)
                            )
                        )
                    ) {
                        AsyncImage(
                            model = post.authorPhotoUrl.ifEmpty { "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png" },
                            contentDescription = post.authorName,
                            modifier = Modifier.fillMaxSize().padding(3.dp).clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = MatchPickPalette.ElectricCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "${post.timestampFormatted} • PRO ANALYST",
                            style = MaterialTheme.typography.labelSmall,
                            color = MatchPickPalette.TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Like Capsule Button
                Surface(
                    onClick = onLike,
                    shape = RoundedCornerShape(14.dp),
                    color = if (post.isLiked) Color(0xFF2D1220) else Color(0xFF141E33),
                    border = BorderStroke(
                        1.dp,
                        if (post.isLiked) MatchPickPalette.CrimsonLive else Color(0x2864748B)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val heartColor by animateColorAsState(
                            targetValue = if (post.isLiked) MatchPickPalette.CrimsonLive else MatchPickPalette.TextSecondary,
                            label = "heartColor"
                        )
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = heartColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${post.likesCount}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = if (post.isLiked) MatchPickPalette.CrimsonLive else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post Content
            Text(
                text = post.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE2E8F0),
                lineHeight = 21.sp
            )

            // Attached Holographic Prediction Ticket
            post.attachedPrediction?.let { pick ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0A281E),
                    border = BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            listOf(MatchPickPalette.NeonEmerald, MatchPickPalette.ElectricCyan)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎯", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = pick,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = MatchPickPalette.NeonEmerald
                            )
                        }
                        Text(
                            text = "VERIFIED PICK",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = MatchPickPalette.ElectricCyan
                        )
                    }
                }
            }
        }
    }
}
