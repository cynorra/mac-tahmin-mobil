package com.matchpick.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
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
        containerColor = Color(0xFF070B14),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_title),
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF070B14)
                )
            )
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
            // Profile Card with Glowing Halo Avatar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF0E1424),
                border = BorderStroke(1.dp, Color(0x3300F59B))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF131A2E), Color(0xFF0B101D))
                            )
                        )
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.size(88.dp),
                            shape = CircleShape,
                            color = Color(0xFF0B291D),
                            border = BorderStroke(2.dp, Brush.linearGradient(listOf(Color(0xFF00F59B), Color(0xFF00E5FF))))
                        ) {
                            AsyncImage(
                                model = avatarUrl.ifEmpty { "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png" },
                                contentDescription = userName,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Stat Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = stringResource(R.string.stat_points),
                    value = "$totalPoints",
                    valueColor = Color(0xFF00F59B),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.stat_streak),
                    value = "🔥 $currentStreak",
                    valueColor = Color(0xFFFFB800),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.stat_best),
                    value = "🏆 $bestStreak",
                    valueColor = Color(0xFFC084FC),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Settings Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0E1424),
                border = BorderStroke(1.dp, Color(0x2264748B))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Match Reminders Switch
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
                                checkedTrackColor = Color(0xFF00F59B),
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }

                    HorizontalDivider(
                        color = Color(0x1564748B),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Language Selector
                    SettingsRow(
                        icon = Icons.Default.Language,
                        title = stringResource(R.string.settings_language_title),
                        subtitle = if (currentLanguage == "tr") "Türkçe aktif" else "English active"
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (currentLanguage == "en") Color(0xFF00F59B) else Color(0xFF1E293B)),
                                color = if (currentLanguage == "en") Color(0xFF00F59B) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                TextButton(
                                    onClick = { onChangeLanguage("en") },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "EN",
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentLanguage == "en") Color(0xFF041B11) else Color(0xFF94A3B8)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (currentLanguage == "tr") Color(0xFF00F59B) else Color(0xFF1E293B)),
                                color = if (currentLanguage == "tr") Color(0xFF00F59B) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                TextButton(
                                    onClick = { onChangeLanguage("tr") },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "TR",
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentLanguage == "tr") Color(0xFF041B11) else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        color = Color(0x1564748B),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Dark Theme Toggle
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
                                checkedTrackColor = Color(0xFF00F59B),
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }

                    HorizontalDivider(
                        color = Color(0x1564748B),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Logout Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = null,
                                tint = Color(0xFFEF4444)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.btn_logout),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0E1424),
        border = BorderStroke(1.dp, Color(0x2264748B))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
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
                color = Color(0xFF94A3B8),
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF00F59B)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp
                )
            }
        }
        action()
    }
}
