package com.matchpick.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.matchpick.app.R
import com.matchpick.app.data.local.FixtureEntity
import com.matchpick.app.ui.components.NumberFlowText
import com.matchpick.app.ui.components.spatialGlassHighlight
import com.matchpick.app.ui.theme.MatchPickPalette
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    avatarUrl: String,
    currentStreak: Int,
    bestStreak: Int,
    totalPoints: Long,
    isNotificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    currentLanguage: String,
    onChangeLanguage: (String) -> Unit,
    isAmoledTheme: Boolean,
    onToggleAmoled: (Boolean) -> Unit,
    pickedFixtures: List<FixtureEntity> = emptyList(),
    isGuest: Boolean = false,
    onSignInClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {},
    showPrivacyOptions: Boolean = false,
    onPrivacyOptionsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = 680.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.profile_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MatchPickPalette.TextPrimary
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MatchPickPalette.EmeraldDeep,
                border = BorderStroke(0.5.dp, MatchPickPalette.NeonEmerald)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MatchPickPalette.NeonEmerald,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.profile_tier_badge),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MatchPickPalette.NeonEmerald
                    )
                }
            }
        }

        if (isGuest) {
            Button(
                onClick = onSignInClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatchPickPalette.NeonEmerald,
                    contentColor = androidx.compose.ui.graphics.Color(0xFF03170E)
                )
            ) {
                Text(text = stringResource(R.string.profile_sign_in), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Luxury Player Passport Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .spatialGlassHighlight(
                    accentColor = MatchPickPalette.NeonEmerald,
                    cornerRadius = 22.dp,
                    glowAlpha = 0.12f
                ),
            shape = RoundedCornerShape(22.dp),
            color = MatchPickPalette.SurfaceDark.copy(alpha = 0.92f),
            shadowElevation = 14.dp,
            border = BorderStroke(1.dp, MatchPickPalette.HeroBorderBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MatchPickPalette.GlassCardGradient)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = MatchPickPalette.SurfaceElevated,
                    border = BorderStroke(1.5.dp, MatchPickPalette.NeonEmerald)
                ) {
                    if (avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = userName,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.firstOrNull()?.uppercase() ?: "A",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = MatchPickPalette.NeonEmerald
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = MatchPickPalette.NeonEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MatchPickPalette.TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // XP Progress Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.season_pass_title, 14),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MatchPickPalette.ElectricCyan,
                            fontSize = 10.sp
                        )
                        Text(
                            text = stringResource(R.string.xp_progress_label, totalPoints, 500),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MatchPickPalette.NeonEmerald,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    LinearProgressIndicator(
                        progress = { (totalPoints.toFloat() / 500f).coerceIn(0.25f, 0.92f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MatchPickPalette.NeonEmerald,
                        trackColor = MatchPickPalette.SurfaceElevated
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2x2 Clean Bento Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = stringResource(R.string.stat_points),
                value = "$totalPoints PTS",
                valueColor = MatchPickPalette.NeonEmerald,
                iconRes = R.drawable.ic_target_pro,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.stat_streak),
                value = "$currentStreak",
                valueColor = MatchPickPalette.VipGold,
                iconRes = R.drawable.ic_flame_pro,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = stringResource(R.string.stat_best),
                value = "$bestStreak",
                valueColor = MatchPickPalette.TextPrimary,
                iconRes = R.drawable.ic_trophy_pro,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.profile_accuracy_label),
                value = "84.2%",
                valueColor = MatchPickPalette.ElectricCyan,
                iconRes = R.drawable.ic_shield_check_pro,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Past & Active Predictions History Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
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
                    Text(
                        text = stringResource(R.string.my_predictions_history_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.TextPrimary
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MatchPickPalette.EmeraldDeep,
                        border = BorderStroke(0.5.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "${pickedFixtures.size}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MatchPickPalette.NeonEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (pickedFixtures.isEmpty()) {
                    Text(
                        text = stringResource(R.string.my_predictions_empty),
                        style = MaterialTheme.typography.bodySmall,
                        color = MatchPickPalette.TextMuted
                    )
                } else {
                    val dateFormat = remember { SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()) }
                    pickedFixtures
                        .sortedByDescending { it.matchTimestamp }
                        .take(15)
                        .forEachIndexed { idx, fix ->
                            if (idx > 0) {
                                HorizontalDivider(
                                    color = MatchPickPalette.BorderSubtle,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${fix.homeTeamName} vs ${fix.awayTeamName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MatchPickPalette.TextPrimary
                                    )
                                    val dateText = if (fix.matchTimestamp > 0L) {
                                        "${fix.leagueName} • ${dateFormat.format(Date(fix.matchTimestamp * 1000L))}"
                                    } else {
                                        fix.leagueName
                                    }
                                    Text(
                                        text = dateText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MatchPickPalette.TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MatchPickPalette.EmeraldDeep,
                                    border = BorderStroke(0.5.dp, MatchPickPalette.NeonEmerald)
                                ) {
                                    Text(
                                        text = stringResource(R.string.your_pick_badge, fix.userPick ?: "-"),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MatchPickPalette.NeonEmerald
                                    )
                                }
                            }
                        }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Settings Container (Apple HIG grouped list)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MatchPickPalette.SurfaceDark,
            border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MatchPickPalette.GlassCardGradient)
                    .padding(16.dp)
            ) {
                SettingsRow(
                    icon = Icons.Default.Notifications,
                    title = stringResource(R.string.settings_notifications_title),
                    subtitle = stringResource(R.string.settings_notifications_subtitle)
                ) {
                    Switch(
                        checked = isNotificationsEnabled,
                        onCheckedChange = onToggleNotifications,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF031A0E),
                            checkedTrackColor = MatchPickPalette.NeonEmerald,
                            uncheckedThumbColor = MatchPickPalette.TextSecondary,
                            uncheckedTrackColor = MatchPickPalette.SurfaceElevated
                        )
                    )
                }

                HorizontalDivider(
                    color = MatchPickPalette.BorderSubtle,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                SettingsRow(
                    icon = Icons.Default.Language,
                    title = stringResource(R.string.settings_language_title),
                    subtitle = stringResource(if (currentLanguage == "tr") R.string.language_tr_active else R.string.language_en_active)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val isEn = currentLanguage == "en"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isEn) MatchPickPalette.EmeraldDeep else MatchPickPalette.SurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isEn) MatchPickPalette.EmeraldPrimary.copy(alpha = 0.8f) else MatchPickPalette.BorderSubtle,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onChangeLanguage("en") }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "EN",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isEn) MatchPickPalette.EmeraldPrimary else MatchPickPalette.TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        val isTr = currentLanguage == "tr"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isTr) MatchPickPalette.EmeraldDeep else MatchPickPalette.SurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isTr) MatchPickPalette.EmeraldPrimary.copy(alpha = 0.8f) else MatchPickPalette.BorderSubtle,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onChangeLanguage("tr") }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isTr) MatchPickPalette.EmeraldPrimary else MatchPickPalette.TextSecondary
                            )
                        }
                    }
                }

                HorizontalDivider(
                    color = MatchPickPalette.BorderSubtle,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                SettingsRow(
                    icon = Icons.Default.DarkMode,
                    title = stringResource(R.string.settings_dark_mode_title),
                    subtitle = stringResource(R.string.settings_dark_mode_subtitle)
                ) {
                    Switch(
                        checked = isAmoledTheme,
                        onCheckedChange = onToggleAmoled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF031A0E),
                            checkedTrackColor = MatchPickPalette.NeonEmerald,
                            uncheckedThumbColor = MatchPickPalette.TextSecondary,
                            uncheckedTrackColor = MatchPickPalette.SurfaceElevated
                        )
                    )
                }

                HorizontalDivider(
                    color = MatchPickPalette.BorderSubtle,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

                SettingsRow(
                    icon = Icons.Default.Security,
                    title = stringResource(R.string.settings_privacy_policy_title),
                    subtitle = stringResource(R.string.settings_privacy_policy_subtitle),
                    onClick = {
                        val url = if (currentLanguage == "tr") {
                            "https://cynorra.github.io/matchpick/kvkk.html"
                        } else {
                            "https://cynorra.github.io/matchpick/privacy-policy.html"
                        }
                        uriHandler.openUri(url)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = MatchPickPalette.NeonEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (showPrivacyOptions) {
                    HorizontalDivider(
                        color = MatchPickPalette.BorderSubtle,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    SettingsRow(
                        icon = Icons.Default.Security,
                        title = stringResource(R.string.settings_ad_privacy_title),
                        subtitle = stringResource(R.string.settings_ad_privacy_subtitle),
                        onClick = onPrivacyOptionsClick
                    ) {}
                }

                HorizontalDivider(
                    color = MatchPickPalette.BorderSubtle,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                SettingsRow(
                    icon = Icons.Default.DeleteOutline,
                    title = stringResource(R.string.settings_delete_account_title),
                    subtitle = stringResource(R.string.settings_delete_account_subtitle),
                    onClick = onDeleteAccountClick
                ) {}

                if (!isGuest) {
                HorizontalDivider(
                    color = MatchPickPalette.BorderSubtle,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onLogoutClick)
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = MatchPickPalette.CrimsonLive,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.btn_logout),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MatchPickPalette.CrimsonLive
                    )
                }
                }
            }
        }

        Spacer(modifier = Modifier.height(72.dp))
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    valueColor: Color,
    iconRes: Int? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.spatialGlassHighlight(
            accentColor = valueColor,
            cornerRadius = 18.dp,
            glowAlpha = 0.10f
        ),
        shape = RoundedCornerShape(18.dp),
        color = MatchPickPalette.SurfaceDark.copy(alpha = 0.90f),
        shadowElevation = 8.dp,
        border = BorderStroke(0.8.dp, MatchPickPalette.GlassCardBorderBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatchPickPalette.GlassCardGradient)
                .padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MatchPickPalette.TextMuted,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = valueColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                NumberFlowText(
                    value = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick)
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = MatchPickPalette.SurfaceElevated,
                border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MatchPickPalette.NeonEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MatchPickPalette.TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MatchPickPalette.TextSecondary
                )
            }
        }
        trailingContent()
    }
}
