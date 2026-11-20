package com.matchpick.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.matchpick.app.R
import com.matchpick.app.core.ads.AdConfig
import com.matchpick.app.core.ads.AdaptiveBannerAd
import com.matchpick.app.data.local.FixtureEntity
import com.matchpick.app.ui.theme.MatchPickPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailBottomSheet(
    fixture: FixtureEntity,
    onPickSelected: (String) -> Unit = {},
    onShare: () -> Unit = {},
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color(0xFF0A0F1E),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 6.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF334155))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 12.dp)
        ) {
            // Header Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF191233),
                    border = BorderStroke(1.dp, MatchPickPalette.NeuralViolet.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_sparkle_pro),
                            contentDescription = null,
                            tint = Color(0xFFD8B4FE),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.neural_radar_badge),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFD8B4FE)
                        )
                    }
                }

                Text(
                    text = fixture.leagueName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MatchPickPalette.NeonEmerald
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Match Teams Arena Header
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF11192E),
                border = BorderStroke(1.dp, Color(0x2800F59B))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = Color(0xFF1A2540),
                            border = BorderStroke(1.5.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.4f))
                        ) {
                            AsyncImage(
                                model = fixture.homeTeamLogo,
                                contentDescription = fixture.homeTeamName,
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = fixture.homeTeamName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.versus),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MatchPickPalette.ElectricCyan
                        )
                        if (fixture.stadiumName.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = fixture.stadiumName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MatchPickPalette.TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = Color(0xFF1A2540),
                            border = BorderStroke(1.5.dp, MatchPickPalette.ElectricCyan.copy(alpha = 0.4f))
                        ) {
                            AsyncImage(
                                model = fixture.awayTeamLogo,
                                contentDescription = fixture.awayTeamName,
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = fixture.awayTeamName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Neural Telemetry Gauges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TelemetryMetricBox(
                    label = stringResource(R.string.metric_xg_advantage),
                    value = "2.18 vs 1.34",
                    accentColor = MatchPickPalette.NeonEmerald,
                    modifier = Modifier.weight(1f)
                )
                TelemetryMetricBox(
                    label = stringResource(R.string.metric_press_intensity),
                    value = "8.4 PPDA",
                    accentColor = MatchPickPalette.ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
                TelemetryMetricBox(
                    label = stringResource(R.string.metric_win_probability),
                    value = "${fixture.officialConfidence}%",
                    accentColor = MatchPickPalette.NeuralViolet,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tactical Breakdown
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF11192E),
                border = BorderStroke(1.dp, Color(0x2264748B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.tactical_sheet_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = MatchPickPalette.NeonEmerald
                        )
                        fixture.officialPick?.let { pick ->
                            Text(
                                text = "AI: $pick (${fixture.officialConfidence}%)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFD8B4FE)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = fixture.aiAnalysis.ifEmpty { stringResource(R.string.tactical_metrics_fallback) },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { (fixture.officialConfidence / 100f).coerceIn(0.1f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MatchPickPalette.NeonEmerald,
                        trackColor = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Action Row: Predict & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("1", fixture.oddsHome, fixture.userPick == "1" || fixture.userPick == "HOME"),
                    Triple("X", fixture.oddsDraw, fixture.userPick == "X" || fixture.userPick == "DRAW"),
                    Triple("2", fixture.oddsAway, fixture.userPick == "2" || fixture.userPick == "AWAY")
                ).forEach { (choice, odds, isSelected) ->
                    Surface(
                        onClick = { onPickSelected(choice) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MatchPickPalette.NeonEmerald else Color(0xFF162035),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MatchPickPalette.NeonEmerald else Color(0x3364748B)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = choice,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color(0xFF041B11) else Color.White
                            )
                            Text(
                                text = String.format("%.2f", odds),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF041B11) else MatchPickPalette.TextSecondary
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(MatchPickPalette.NeonEmerald, MatchPickPalette.ElectricCyan)
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.btn_share),
                        tint = Color(0xFF041B11)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Integrated Sponsor Banner in Telemetry Sheet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                AdaptiveBannerAd(adUnitId = AdConfig.BANNER_AD_UNIT_ID)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TelemetryMetricBox(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF11192E),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MatchPickPalette.TextSecondary,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}
