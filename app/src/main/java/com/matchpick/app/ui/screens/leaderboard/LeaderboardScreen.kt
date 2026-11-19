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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.matchpick.app.R
import com.matchpick.app.ui.theme.MatchPickPalette

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
    onCopyReferral: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            containerColor = MatchPickPalette.DeepObsidian,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF141128), MatchPickPalette.DeepObsidian)
                            )
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MatchPickPalette.VipCrownGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🏆", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
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
                                    color = MatchPickPalette.TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1A1432),
                            border = BorderStroke(1.dp, MatchPickPalette.VipGold.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "SEASON 26",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MatchPickPalette.VipGold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            },
            bottomBar = {
                // 2026+ Sticky Holographic User Position Bar
                Surface(
                    color = Color(0xFF0B1324),
                    border = BorderStroke(
                        1.2.dp,
                        Brush.linearGradient(
                            listOf(MatchPickPalette.NeonEmerald, MatchPickPalette.ElectricCyan)
                        )
                    ),
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                    shadowElevation = 12.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0A2E20),
                                border = BorderStroke(1.dp, MatchPickPalette.NeonEmerald)
                            ) {
                                Text(
                                    text = "#${currentUser.rank}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MatchPickPalette.NeonEmerald
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.your_position_label),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "🔥 ${currentUser.streak} ${stringResource(R.string.streak_label)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MatchPickPalette.VipGold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.points_label, currentUser.points),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MatchPickPalette.NeonEmerald
                        )
                    }
                }
            }
        ) { paddingValues ->
            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1.1f).fillMaxHeight(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(topUsers, key = { _, user -> user.userId }) { _, item ->
                            LeaderboardRow(user = item, isCurrentUser = item.rank == currentUser.rank)
                        }
                    }

                    Column(
                        modifier = Modifier.weight(0.9f).fillMaxHeight().padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ReferralHubCard(
                            referralCode = referralCode,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(referralCode))
                                onCopyReferral(referralCode)
                            },
                            onShare = { onShareReferral(referralCode) }
                        )

                        if (topUsers.size >= 3) {
                            PodiumSection(topThree = topUsers.take(3))
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ReferralHubCard(
                            referralCode = referralCode,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(referralCode))
                                onCopyReferral(referralCode)
                            },
                            onShare = { onShareReferral(referralCode) }
                        )
                    }

                    if (topUsers.size >= 3) {
                        item {
                            PodiumSection(topThree = topUsers.take(3))
                        }
                    }

                    itemsIndexed(topUsers.drop(3), key = { _, user -> user.userId }) { _, item ->
                        LeaderboardRow(user = item, isCurrentUser = item.rank == currentUser.rank)
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
        shape = RoundedCornerShape(24.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(
            1.2.dp,
            Brush.linearGradient(
                listOf(MatchPickPalette.NeonEmerald, MatchPickPalette.ElectricCyan, MatchPickPalette.NeuralViolet)
            )
        ),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF132238), Color(0xFF0A101E))
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎁", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.referral_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0A2E20),
                    border = BorderStroke(1.dp, MatchPickPalette.NeonEmerald)
                ) {
                    Text(
                        text = "+50 PTS",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = MatchPickPalette.NeonEmerald
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.referral_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MatchPickPalette.TextSecondary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0C1426),
                    border = BorderStroke(1.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = referralCode,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MatchPickPalette.NeonEmerald,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "VIP CODE",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MatchPickPalette.ElectricCyan
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF16223A))
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = stringResource(R.string.btn_copy),
                        tint = MatchPickPalette.NeonEmerald
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MatchPickPalette.NeonBrandGradient)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.btn_share),
                        tint = Color(0xFF03170E)
                    )
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
        shape = RoundedCornerShape(24.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(Color(0x44FFB800), Color(0x2200E5FF))
            )
        ),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF16132E), Color(0xFF0B101E))
                    )
                )
                .padding(horizontal = 12.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            PodiumPillar(
                user = second,
                rank = 2,
                height = 112.dp,
                pillarColor = Color(0xFF38BDF8),
                crown = "🥈"
            )
            PodiumPillar(
                user = first,
                rank = 1,
                height = 148.dp,
                pillarColor = MatchPickPalette.VipGold,
                crown = "👑"
            )
            PodiumPillar(
                user = third,
                rank = 3,
                height = 94.dp,
                pillarColor = Color(0xFFF97316),
                crown = "🥉"
            )
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
        modifier = Modifier.width(98.dp)
    ) {
        Text(text = crown, fontSize = if (rank == 1) 24.sp else 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            modifier = Modifier.size(if (rank == 1) 56.dp else 48.dp),
            shape = CircleShape,
            color = Color(0xFF162035),
            border = BorderStroke(2.dp, pillarColor),
            shadowElevation = 6.dp
        ) {
            AsyncImage(
                model = user.avatarUrl.ifEmpty { "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png" },
                contentDescription = user.name,
                modifier = Modifier.fillMaxSize().padding(3.dp).clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${user.points} PTS",
            style = MaterialTheme.typography.labelSmall,
            color = MatchPickPalette.NeonEmerald,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(pillarColor, pillarColor.copy(alpha = 0.25f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF04101C)
                )
                Text(
                    text = "🔥 ${user.streak}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF04101C).copy(alpha = 0.85f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun LeaderboardRow(
    user: LeaderboardUserUiModel,
    isCurrentUser: Boolean = false,
    modifier: Modifier = Modifier
) {
    val border = if (isCurrentUser) {
        BorderStroke(1.4.dp, MatchPickPalette.NeonEmerald)
    } else {
        BorderStroke(1.dp, Color(0x2264748B))
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = if (isCurrentUser) Color(0xFF0C2224) else MatchPickPalette.SurfaceDark,
        border = border,
        shadowElevation = if (isCurrentUser) 6.dp else 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${user.rank}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = if (isCurrentUser) MatchPickPalette.NeonEmerald else MatchPickPalette.TextSecondary,
                modifier = Modifier.width(38.dp)
            )
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = Color(0xFF19233A),
                border = BorderStroke(
                    1.dp,
                    if (isCurrentUser) MatchPickPalette.NeonEmerald else Color(0x3364748B)
                )
            ) {
                AsyncImage(
                    model = user.avatarUrl.ifEmpty { "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png" },
                    contentDescription = user.name,
                    modifier = Modifier.fillMaxSize().padding(3.dp).clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = MatchPickPalette.ElectricCyan,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Text(
                    text = "🔥 ${user.streak} ${stringResource(R.string.streak_label)} • ${user.country}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MatchPickPalette.VipGold,
                    fontSize = 11.sp
                )
            }
            Text(
                text = stringResource(R.string.points_label, user.points),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MatchPickPalette.NeonEmerald
            )
        }
    }
}
