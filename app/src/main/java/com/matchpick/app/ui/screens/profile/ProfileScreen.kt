package com.matchpick.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
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

    Scaffold(
        containerColor = MatchPickPalette.DeepObsidian,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0A1426), MatchPickPalette.DeepObsidian)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.profile_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF191233),
                    border = BorderStroke(1.dp, MatchPickPalette.NeuralViolet)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFD8B4FE),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(R.string.profile_tier_badge),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = Color(0xFFE9D5FF)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 2026+ Holographic Player Passport Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = MatchPickPalette.SurfaceDark,
                border = BorderStroke(
                    1.4.dp,
                    Brush.linearGradient(
                        listOf(
                            MatchPickPalette.NeonEmerald,
                            MatchPickPalette.ElectricCyan,
                            MatchPickPalette.NeuralViolet
                        )
                    )
                ),
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF15223C), Color(0xFF0A101F))
                            )
                        )
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.size(94.dp),
                            shape = CircleShape,
                            color = Color(0xFF0B291D),
                            border = BorderStroke(
                                2.5.dp,
                                Brush.linearGradient(
                                    listOf(MatchPickPalette.NeonEmerald, MatchPickPalette.ElectricCyan)
                                )
                            ),
                            shadowElevation = 8.dp
                        ) {
                            AsyncImage(
                                model = avatarUrl.ifEmpty { "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png" },
                                contentDescription = userName,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = MatchPickPalette.NeonEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MatchPickPalette.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // XP Level Progress Bar
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "SEASON PASS • LVL 14",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MatchPickPalette.ElectricCyan,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "$totalPoints / 500 XP",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MatchPickPalette.NeonEmerald,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (totalPoints.toFloat() / 500f).coerceIn(0.25f, 0.92f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MatchPickPalette.NeonEmerald,
                            trackColor = Color(0xFF1E293B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2x2 Spatial Bento Telemetry Grid
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
                    valueColor = Color(0xFFD8B4FE),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.profile_accuracy_label),
                    value = "⚡ 84.2%",
                    valueColor = MatchPickPalette.ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Spatial Glass Settings Vault
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MatchPickPalette.SurfaceDark,
                border = BorderStroke(1.dp, Color(0x2864748B)),
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MatchPickPalette.GlassCardGradient)
                        .padding(18.dp)
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
                                checkedThumbColor = Color(0xFF041B11),
                                checkedTrackColor = MatchPickPalette.NeonEmerald,
                                uncheckedThumbColor = MatchPickPalette.TextSecondary,
                                uncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }

                    HorizontalDivider(
                        color = Color(0x1864748B),
                        modifier = Modifier.padding(vertical = 14.dp)
                    )

                    SettingsRow(
                        icon = Icons.Default.Language,
                        title = stringResource(R.string.settings_language_title),
                        subtitle = stringResource(if (currentLanguage == "tr") R.string.language_tr_active else R.string.language_en_active)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (currentLanguage == "en") MatchPickPalette.NeonEmerald else Color(0xFF19243A))
                                    .clickable { onChangeLanguage("en") }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "EN",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = if (currentLanguage == "en") Color(0xFF03170E) else MatchPickPalette.TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (currentLanguage == "tr") MatchPickPalette.NeonEmerald else Color(0xFF19243A))
                                    .clickable { onChangeLanguage("tr") }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "TR",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = if (currentLanguage == "tr") Color(0xFF03170E) else MatchPickPalette.TextSecondary
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = Color(0x1864748B),
                        modifier = Modifier.padding(vertical = 14.dp)
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
                                checkedThumbColor = Color(0xFF041B11),
                                checkedTrackColor = MatchPickPalette.NeonEmerald,
                                uncheckedThumbColor = MatchPickPalette.TextSecondary,
                                uncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }

                    HorizontalDivider(
                        color = Color(0x1864748B),
                        modifier = Modifier.padding(vertical = 14.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = MatchPickPalette.CrimsonLive
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.btn_logout),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MatchPickPalette.CrimsonLive
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(1.dp, valueColor.copy(alpha = 0.35f)),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .background(MatchPickPalette.GlassCardGradient)
                .padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MatchPickPalette.TextSecondary,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    action: @Composable () -> Unit
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
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF15223A)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MatchPickPalette.NeonEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MatchPickPalette.TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
        action()
    }
}
