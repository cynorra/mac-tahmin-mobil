package com.matchpick.app.ui.screens.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.matchpick.app.R

@Immutable
data class LeaderboardUserUiModel(
    val rank: Int,
    val userId: String,
    val name: String,
    val avatarUrl: String,
    val points: Long,
    val streak: Int,
    val country: String
)

@Composable
fun LeaderboardScreen(
    topUsers: List<LeaderboardUserUiModel>,
    currentUser: LeaderboardUserUiModel,
    referralCode: String,
    onShareReferral: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            containerColor = Color(0xFF070B14),
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF070B14))
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFFB800), Color(0xFFFF8A00))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏆", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.leaderboard_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = stringResource(R.string.leaderboard_podium_title),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            },
            bottomBar = {
                // Sticky User Position Bar
                Surface(
                    color = Color(0xFF0E1424),
                    border = BorderStroke(1.dp, Color(0x3300F59B)),
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#${currentUser.rank}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00F59B)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.your_position_label),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = stringResource(R.string.points_label, currentUser.points),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00F59B)
                        )
                    }
                }
            }
        ) { paddingValues ->
            if (isWideScreen) {
                // Responsive Dual-Pane View for Wide / Landscape Screens
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Standings List
                    LazyColumn(
                        modifier = Modifier.weight(1.1f).fillMaxHeight(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(topUsers, key = { _, user -> user.userId }) { _, item ->
                            LeaderboardRow(user = item)
                        }
                    }

                    // Right Column: Podium & Referral Hub
                    Column(
                        modifier = Modifier.weight(0.9f).fillMaxHeight().padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ReferralHubCard(
                            referralCode = referralCode,
                            onCopy = { clipboardManager.setText(AnnotatedString(referralCode)) },
                            onShare = { onShareReferral(referralCode) }
                        )

                        if (topUsers.size >= 3) {
                            PodiumSection(topThree = topUsers.take(3))
                        }
                    }
                }
            } else {
                // Standard Single Column
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Referral Hub
                    item {
                        ReferralHubCard(
                            referralCode = referralCode,
                            onCopy = { clipboardManager.setText(AnnotatedString(referralCode)) },
                            onShare = { onShareReferral(referralCode) }
                        )
                    }

                    // Podium Top 3
                    if (topUsers.size >= 3) {
                        item {
                            PodiumSection(topThree = topUsers.take(3))
                        }
                    }

                    // Remaining Users (4+)
                    itemsIndexed(topUsers.drop(3), key = { _, user -> user.userId }) { _, item ->
                        LeaderboardRow(user = item)
                    }
                }
            }
        }
    }
}

@Composable
fun ReferralHubCard(
    referralCode: String,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0E1424),
        border = BorderStroke(1.dp, Color(0x4400F59B)),
        shadowElevation = 6.dp
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎁", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.referral_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.referral_desc),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF162035),
                    border = BorderStroke(1.dp, Color(0x3300F59B)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = referralCode,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00F59B),
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF162035))
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = stringResource(R.string.btn_copy), tint = Color(0xFF00F59B))
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF00F59B))
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = stringResource(R.string.btn_share), tint = Color(0xFF041B11))
                }
            }
        }
    }
}

@Composable
fun PodiumSection(topThree: List<LeaderboardUserUiModel>) {
    val first = topThree.getOrNull(0) ?: return
    val second = topThree.getOrNull(1) ?: return
    val third = topThree.getOrNull(2) ?: return

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0E1424),
        border = BorderStroke(1.dp, Color(0x2264748B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            PodiumPillar(user = second, rank = 2, height = 110.dp, pillarColor = Color(0xFF94A3B8), crown = "🥈")
            PodiumPillar(user = first, rank = 1, height = 145.dp, pillarColor = Color(0xFFFFB800), crown = "👑")
            PodiumPillar(user = third, rank = 3, height = 90.dp, pillarColor = Color(0xFFCD7F32), crown = "🥉")
        }
    }
}

@Composable
fun PodiumPillar(
    user: LeaderboardUserUiModel,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    pillarColor: Color,
    crown: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp)
    ) {
        Text(text = crown, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
            modifier = Modifier.size(46.dp),
            shape = CircleShape,
            color = Color(0xFF162035),
            border = BorderStroke(2.dp, pillarColor)
        ) {
            AsyncImage(
                model = user.avatarUrl.ifEmpty { "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png" },
                contentDescription = user.name,
                modifier = Modifier.fillMaxSize().padding(3.dp).clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1
        )
        Text(
            text = "${user.points} pts",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF00F59B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(pillarColor, pillarColor.copy(alpha = 0.4f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$rank",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.Black.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun LeaderboardRow(user: LeaderboardUserUiModel, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0E1424),
        border = BorderStroke(1.dp, Color(0x1564748B))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${user.rank}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                modifier = Modifier.width(36.dp)
            )
            Surface(
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = Color(0xFF1E293B)
            ) {
                AsyncImage(
                    model = user.avatarUrl.ifEmpty { "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png" },
                    contentDescription = user.name,
                    modifier = Modifier.fillMaxSize().padding(3.dp).clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "🔥 ${user.streak} ${stringResource(R.string.streak_label)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFFB800),
                    fontSize = 11.sp
                )
            }
            Text(
                text = stringResource(R.string.points_label, user.points),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF00F59B)
            )
        }
    }
}
