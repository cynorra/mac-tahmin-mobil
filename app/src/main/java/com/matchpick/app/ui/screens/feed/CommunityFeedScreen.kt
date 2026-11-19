package com.matchpick.app.ui.screens.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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

@Immutable
data class PostUiModel(
    val id: String,
    val authorName: String,
    val authorPhotoUrl: String,
    val text: String,
    val timestampFormatted: String,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val attachedPrediction: String? = null // Örn: "Real Madrid vs Barcelona -> 1X"
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

    Scaffold(
        containerColor = Color(0xFF070B14),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF070B14))
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF00E5FF), Color(0xFF00F59B))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💬", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.nav_feed),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                // Global / Local Tab Bar
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF070B14),
                    contentColor = Color(0xFF00F59B),
                    divider = { HorizontalDivider(color = Color(0x1564748B)) }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            onRegionSelected("GLOBAL")
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.tab_global),
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) Color(0xFF00F59B) else Color(0xFF94A3B8)
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            onRegionSelected("TR")
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.tab_local),
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) Color(0xFF00F59B) else Color(0xFF94A3B8)
                            )
                        }
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color(0xFF0E1424),
                border = BorderStroke(1.dp, Color(0x2264748B)),
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color(0xFFEF4444),
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
                                    color = Color(0xFF64748B)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF162035),
                                unfocusedContainerColor = Color(0xFF162035),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(8.dp))

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
                                .background(if (postInput.isNotBlank()) Color(0xFF00F59B) else Color(0xFF1E293B))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = stringResource(R.string.btn_publish),
                                tint = if (postInput.isNotBlank()) Color(0xFF041B11) else Color(0xFF64748B)
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

                // Her 4 gönderide bir AdMob Adaptive Banner reklamı
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
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF0E1424),
        border = BorderStroke(1.dp, Color(0x2264748B)),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF131A2E), Color(0xFF0B101D))
                    )
                )
                .padding(16.dp)
        ) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    color = Color(0xFF1E293B),
                    border = BorderStroke(1.dp, Color(0x4400F59B))
                ) {
                    AsyncImage(
                        model = post.authorPhotoUrl.ifEmpty { "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png" },
                        contentDescription = post.authorName,
                        modifier = Modifier.fillMaxSize().padding(3.dp).clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = post.timestampFormatted,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Text
            Text(
                text = post.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE2E8F0),
                lineHeight = 20.sp
            )

            // Attached Prediction Badge
            post.attachedPrediction?.let { pick ->
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0B291D),
                    border = BorderStroke(1.dp, Color(0x3300F59B))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎯", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = pick,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F59B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Like action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onLike,
                    modifier = Modifier.size(36.dp)
                ) {
                    val heartColor by animateColorAsState(
                        targetValue = if (post.isLiked) Color(0xFFEF4444) else Color(0xFF64748B),
                        label = "heartColor"
                    )
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = heartColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${post.likesCount}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (post.isLiked) Color(0xFFEF4444) else Color(0xFF94A3B8)
                )
            }
        }
    }
}
