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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.matchpick.app.R
import com.matchpick.app.ui.theme.MatchPickPalette

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
    modifier: Modifier = Modifier
) {
    var isAmoledTheme by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MatchPickPalette.DeepObsidian)
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

        Spacer(modifier = Modifier.height(14.dp))

        // Luxury Player Passport Card
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
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.stat_streak),
                value = "🔥 $currentStreak",
                valueColor = MatchPickPalette.VipGold,
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
                value = "🏆 $bestStreak",
                valueColor = MatchPickPalette.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.profile_accuracy_label),
                value = "⚡ 84.2%",
                valueColor = MatchPickPalette.ElectricCyan,
                modifier = Modifier.weight(1f)
            )
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
                        onCheckedChange = { isAmoledTheme = it },
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { }
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

        Spacer(modifier = Modifier.height(72.dp))
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
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
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
