package com.matchpick.app.ui.screens.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MatchPickPalette.DeepObsidian)
    ) {
        val isWideScreen = maxWidth >= 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = if (isWideScreen) 1000.dp else 720.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MatchPickPalette.VipDeep)
                            .border(1.dp, MatchPickPalette.VipGold.copy(alpha = 0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_trophy_pro),
                            contentDescription = null,
                            tint = MatchPickPalette.VipGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.leaderboard_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MatchPickPalette.TextPrimary
                        )
                        Text(
                            text = stringResource(R.string.leaderboard_podium_title),
                            style = MaterialTheme.typography.labelSmall,
                            color = MatchPickPalette.TextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MatchPickPalette.SurfaceElevated,
                    border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                ) {
                    Text(
                        text = "SEASON 26",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.TextSecondary
                    )
                }
            }

            // User's Position Banner (Integrated Top Sticky)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                color = MatchPickPalette.EmeraldDeep,
                border = BorderStroke(0.8.dp, MatchPickPalette.NeonEmerald)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MatchPickPalette.NeonEmerald
                        ) {
                            Text(
                                text = "#${currentUser.rank}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF031A0E)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.your_position_label),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MatchPickPalette.TextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_flame_pro),
                                    contentDescription = null,
                                    tint = MatchPickPalette.VipGold,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${currentUser.streak} ${stringResource(R.string.streak_label)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MatchPickPalette.VipGold
                                )
                            }
                        }
                    }
                    Text(
                        text = stringResource(R.string.points_label, currentUser.points),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.NeonEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1.15f).fillMaxHeight(),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (topUsers.isEmpty()) {
                            item {
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = MatchPickPalette.SurfaceDark,
                                    border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MatchPickPalette.EmeraldDeep,
                                            border = BorderStroke(1.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.4f)),
                                            modifier = Modifier.size(52.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_trophy_pro),
                                                    contentDescription = null,
                                                    tint = MatchPickPalette.NeonEmerald,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = stringResource(R.string.leaderboard_empty_title),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MatchPickPalette.TextPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = stringResource(R.string.leaderboard_empty_desc),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MatchPickPalette.TextMuted,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            itemsIndexed(topUsers, key = { _, user -> user.userId }) { _, item ->
                                LeaderboardRow(user = item, isCurrentUser = item.rank == currentUser.rank)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.weight(0.85f).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
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

                    if (topUsers.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = MatchPickPalette.SurfaceDark,
                                border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MatchPickPalette.EmeraldDeep,
                                        border = BorderStroke(1.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.4f)),
                                        modifier = Modifier.size(52.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_trophy_pro),
                                                contentDescription = null,
                                                tint = MatchPickPalette.NeonEmerald,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(R.string.leaderboard_empty_title),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MatchPickPalette.TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = stringResource(R.string.leaderboard_empty_desc),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MatchPickPalette.TextMuted,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else if (topUsers.size >= 3) {
                        item {
                            PodiumSection(topThree = topUsers.take(3))
                        }
                    }

                    if (topUsers.isNotEmpty()) {
                        itemsIndexed(topUsers.drop(3), key = { _, user -> user.userId }) { _, item ->
                            LeaderboardRow(user = item, isCurrentUser = item.rank == currentUser.rank)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
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
        shape = RoundedCornerShape(18.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatchPickPalette.GlassCardGradient)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_gift_pro),
                        contentDescription = null,
                        tint = MatchPickPalette.NeonEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.referral_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MatchPickPalette.EmeraldDeep,
                    border = BorderStroke(0.5.dp, MatchPickPalette.NeonEmerald)
                ) {
                    Text(
                        text = "+50 PTS",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.NeonEmerald
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.referral_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MatchPickPalette.TextSecondary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MatchPickPalette.SurfaceElevated,
                    border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = referralCode,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MatchPickPalette.NeonEmerald,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "KOD",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MatchPickPalette.TextMuted
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MatchPickPalette.SurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = stringResource(R.string.btn_copy),
                        tint = MatchPickPalette.TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MatchPickPalette.NeonEmerald)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.btn_share),
                        tint = Color(0xFF031A0E),
                        modifier = Modifier.size(18.dp)
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
        shape = RoundedCornerShape(18.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatchPickPalette.GlassCardGradient)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            PodiumPillar(
                user = second,
                rank = 2,
                height = 96.dp,
                pillarColor = Color(0xFF94A3B8)
            )
            PodiumPillar(
                user = first,
                rank = 1,
                height = 124.dp,
                pillarColor = MatchPickPalette.VipGold
            )
            PodiumPillar(
                user = third,
                rank = 3,
                height = 84.dp,
                pillarColor = Color(0xFFD97706)
            )
        }
    }
}

@Composable
fun PodiumPillar(
    user: LeaderboardUserUiModel,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    pillarColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(92.dp)
    ) {
        if (rank == 1) {
            Icon(
                painter = painterResource(R.drawable.ic_crown_luxury),
                contentDescription = null,
                tint = MatchPickPalette.VipGold,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_trophy_pro),
                contentDescription = null,
                tint = pillarColor,
                modifier = Modifier.size(17.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            modifier = Modifier.size(if (rank == 1) 48.dp else 42.dp),
            shape = CircleShape,
            color = MatchPickPalette.SurfaceElevated,
            border = BorderStroke(1.5.dp, pillarColor)
        ) {
            if (user.avatarUrl.isNotBlank()) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = user.name,
                    modifier = Modifier.fillMaxSize().padding(6.dp)
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = user.name.firstOrNull()?.uppercase() ?: "A",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = pillarColor
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MatchPickPalette.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${user.points} PTS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MatchPickPalette.NeonEmerald,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            modifier = Modifier
                .width(64.dp)
                .height(height),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            color = MatchPickPalette.SurfaceElevated,
            border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = pillarColor
                )
            }
        }
    }
}

@Composable
fun LeaderboardRow(
    user: LeaderboardUserUiModel,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isCurrentUser) MatchPickPalette.NeonEmerald else MatchPickPalette.BorderSubtle

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (isCurrentUser) MatchPickPalette.EmeraldDeep else MatchPickPalette.SurfaceDark,
        border = BorderStroke(if (isCurrentUser) 1.dp else 0.5.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "#${user.rank}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentUser) MatchPickPalette.NeonEmerald else MatchPickPalette.TextMuted,
                    modifier = Modifier.width(32.dp)
                )
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = MatchPickPalette.SurfaceElevated,
                    border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                ) {
                    if (user.avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = user.avatarUrl,
                            contentDescription = user.name,
                            modifier = Modifier.fillMaxSize().padding(5.dp)
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = user.name.firstOrNull()?.uppercase() ?: "A",
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
                            text = user.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MatchPickPalette.TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (user.rank <= 3) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = MatchPickPalette.NeonEmerald,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_flame_pro),
                            contentDescription = null,
                            tint = MatchPickPalette.VipGold,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${user.streak} ${stringResource(R.string.streak_label)} • ${user.country}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MatchPickPalette.TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.points_label, user.points),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MatchPickPalette.NeonEmerald
            )
        }
    }
}
