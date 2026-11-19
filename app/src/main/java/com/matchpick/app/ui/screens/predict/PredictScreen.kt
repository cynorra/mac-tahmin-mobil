package com.matchpick.app.ui.screens.predict

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.matchpick.app.R
import com.matchpick.app.core.ads.AdaptiveBannerAd
import com.matchpick.app.data.local.FixtureEntity
import com.matchpick.app.ui.components.DateSelectorStrip
import com.matchpick.app.ui.components.LeagueFilterRow
import com.matchpick.app.ui.components.MatchDetailBottomSheet
import com.matchpick.app.ui.theme.MatchPickPalette
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PredictScreen(
    fixtures: List<FixtureEntity>,
    userStreak: Int,
    userPoints: Long,
    onPredictSelected: (fixtureId: String, pick: String) -> Unit,
    onShareClicked: (FixtureEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLeague by remember { mutableStateOf("ALL") }
    val todayIso = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var selectedDate by remember { mutableStateOf(todayIso) }
    var telemetrySheetFixture by remember { mutableStateOf<FixtureEntity?>(null) }

    val filteredFixtures = remember(fixtures, selectedLeague) {
        if (selectedLeague == "ALL") fixtures
        else fixtures.filter { it.leagueId.equals(selectedLeague, ignoreCase = true) }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(MatchPickPalette.DeepObsidian)) {
        val isWideScreen = maxWidth >= 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = if (isWideScreen) 1100.dp else 720.dp)
        ) {
            // 1. Sleek Luxury Single-Row Top Navigation Header
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
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MatchPickPalette.NeonEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MatchPickPalette.TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MatchPickPalette.EmeraldDeep,
                                border = BorderStroke(0.5.dp, MatchPickPalette.NeonEmerald)
                            ) {
                                Text(
                                    text = "PRO",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MatchPickPalette.NeonEmerald
                                )
                            }
                        }
                    }
                }

                // Sleek Streak & Points Pill (Inspired by Ezan Vakti badges)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MatchPickPalette.SurfaceDark,
                    border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🔥 $userStreak",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MatchPickPalette.VipGold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(12.dp)
                                .background(MatchPickPalette.BorderSubtle)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.points_label, userPoints),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MatchPickPalette.NeonEmerald
                        )
                    }
                }
            }

            // 2. Responsive Dual Pane or Scrollable Stream
            if (isWideScreen) {
                var activeFixtureId by remember { mutableStateOf<String?>(null) }
                val currentFixture = filteredFixtures.find { it.id == activeFixtureId } ?: filteredFixtures.firstOrNull()

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Filter Chips + Match List
                    Column(
                        modifier = Modifier
                            .weight(1.15f)
                            .fillMaxHeight()
                    ) {
                        DateSelectorStrip(
                            selectedDate = selectedDate,
                            onDateSelected = { selectedDate = it }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LeagueFilterRow(
                            selectedLeagueId = selectedLeague,
                            onLeagueSelected = { selectedLeague = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(
                                items = filteredFixtures,
                                key = { _, it -> it.id },
                                contentType = { _, _ -> "FixtureItem" }
                            ) { index, fixture ->
                                FixtureCard(
                                    fixture = fixture,
                                    isSpotlightMatch = index == 0,
                                    onPickSelected = { pick -> onPredictSelected(fixture.id, pick) },
                                    onShare = { onShareClicked(fixture) },
                                    isSelectedInMasterDetail = currentFixture?.id == fixture.id,
                                    onCardClicked = { activeFixtureId = fixture.id }
                                )
                            }
                        }
                    }

                    // Right Column: Tactical Detail Panel
                    Box(
                        modifier = Modifier
                            .weight(0.85f)
                            .fillMaxHeight()
                            .padding(bottom = 80.dp)
                    ) {
                        currentFixture?.let { fix ->
                            TacticalInsightPanel(
                                fixture = fix,
                                onPickSelected = { pick -> onPredictSelected(fix.id, pick) },
                                onShare = { onShareClicked(fix) }
                            )
                        }
                    }
                }
            } else {
                // Mobile Portrait: Seamless Scroll with Date & League Strips
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item(key = "header_date") {
                        DateSelectorStrip(
                            selectedDate = selectedDate,
                            onDateSelected = { selectedDate = it }
                        )
                    }

                    item(key = "header_leagues") {
                        LeagueFilterRow(
                            selectedLeagueId = selectedLeague,
                            onLeagueSelected = { selectedLeague = it }
                        )
                    }

                    itemsIndexed(
                        items = filteredFixtures,
                        key = { _, it -> it.id },
                        contentType = { _, _ -> "FixtureItem" }
                    ) { index, fixture ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            FixtureCard(
                                fixture = fixture,
                                isSpotlightMatch = index == 0,
                                onPickSelected = { pick -> onPredictSelected(fixture.id, pick) },
                                onShare = { onShareClicked(fixture) },
                                onCardClicked = { telemetrySheetFixture = fixture }
                            )
                        }

                        // Adaptive Banner Ad after 3rd fixture (Inline, non-intrusive)
                        if (index == 2) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AdaptiveBannerAd(
                                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                                )
                            }
                        }
                    }
                }
            }
        }

        // Mobile Telemetry Bottom Sheet
        telemetrySheetFixture?.let { selectedFix ->
            val liveFix = filteredFixtures.find { it.id == selectedFix.id } ?: selectedFix
            MatchDetailBottomSheet(
                fixture = liveFix,
                onPickSelected = { pick -> onPredictSelected(liveFix.id, pick) },
                onShare = {
                    telemetrySheetFixture = null
                    onShareClicked(liveFix)
                },
                onDismiss = { telemetrySheetFixture = null }
            )
        }
    }
}

/**
 * World-Class Match Card (Clean, Editorial, High-Impact)
 */
@Composable
fun FixtureCard(
    fixture: FixtureEntity,
    onPickSelected: (String) -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
    isSpotlightMatch: Boolean = false,
    isSelectedInMasterDetail: Boolean = false,
    onCardClicked: (() -> Unit)? = null
) {
    val cardBorder = when {
        isSelectedInMasterDetail -> BorderStroke(1.2.dp, MatchPickPalette.NeonEmerald)
        isSpotlightMatch -> BorderStroke(1.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.5f))
        else -> BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
    }

    Surface(
        onClick = { onCardClicked?.invoke() },
        modifier = modifier.fillMaxWidth(),
        enabled = onCardClicked != null,
        shape = RoundedCornerShape(20.dp),
        color = MatchPickPalette.SurfaceDark,
        border = cardBorder,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatchPickPalette.GlassCardGradient)
                .padding(16.dp)
        ) {
            // Header Row: League Name, Stadium & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MatchPickPalette.NeonEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = fixture.leagueName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.NeonEmerald
                    )
                    if (fixture.stadiumName.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "•", color = MatchPickPalette.TextMuted, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = fixture.stadiumName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MatchPickPalette.TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MatchPickPalette.SurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MatchPickPalette.TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Center Stage: Two Club Emblems & VS / Kickoff Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamEmblemColumn(
                    name = fixture.homeTeamName,
                    logoUrl = fixture.homeTeamLogo,
                    modifier = Modifier.weight(1f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MatchPickPalette.SurfaceElevated,
                        border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                    ) {
                        Text(
                            text = stringResource(R.string.versus),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MatchPickPalette.TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LiveCountdownBadge()
                }

                TeamEmblemColumn(
                    name = fixture.awayTeamName,
                    logoUrl = fixture.awayTeamLogo,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1 - X - 2 Tactile Odds Pills (High-End Tactile Springs)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OddsButton(
                    choice = stringResource(R.string.pick_home),
                    odds = fixture.oddsHome,
                    votePercent = fixture.votesHomePercent,
                    isSelected = fixture.userPick == "1" || fixture.userPick == "HOME",
                    isEnabled = !fixture.isLocked,
                    onClick = { onPickSelected("1") },
                    modifier = Modifier.weight(1f)
                )
                OddsButton(
                    choice = stringResource(R.string.pick_draw),
                    odds = fixture.oddsDraw,
                    votePercent = fixture.votesDrawPercent,
                    isSelected = fixture.userPick == "X" || fixture.userPick == "DRAW",
                    isEnabled = !fixture.isLocked,
                    onClick = { onPickSelected("X") },
                    modifier = Modifier.weight(1f)
                )
                OddsButton(
                    choice = stringResource(R.string.pick_away),
                    odds = fixture.oddsAway,
                    votePercent = fixture.votesAwayPercent,
                    isSelected = fixture.userPick == "2" || fixture.userPick == "AWAY",
                    isEnabled = !fixture.isLocked,
                    onClick = { onPickSelected("2") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Minimal Footer: AI Insight & Crowd Sentiment
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                fixture.officialPick?.let { pick ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MatchPickPalette.NeuralViolet,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.app_pick_badge, pick, fixture.officialConfidence),
                            style = MaterialTheme.typography.labelSmall,
                            color = MatchPickPalette.TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.tap_for_telemetry),
                    style = MaterialTheme.typography.labelSmall,
                    color = MatchPickPalette.TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            CrowdVotesBar(
                home = fixture.votesHomePercent,
                draw = fixture.votesDrawPercent,
                away = fixture.votesAwayPercent
            )
        }
    }
}

/**
 * Tactile Odds Button (Inspired by Apple Sports pill interactions)
 */
@Composable
private fun OddsButton(
    choice: String,
    odds: Float,
    votePercent: Int,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "oddsScale"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.SurfaceElevated,
        label = "oddsBg"
    )
    val textColor = if (isSelected) Color(0xFF031A0E) else MatchPickPalette.TextPrimary
    val oddsColor = if (isSelected) Color(0xFF031A0E) else MatchPickPalette.TextSecondary

    Surface(
        onClick = { onClick(choice) },
        modifier = modifier
            .scale(scale)
            .height(52.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(
            if (isSelected) 1.dp else 0.5.dp,
            if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.BorderSubtle
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = choice,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format(Locale.US, "%.2f", odds),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = oddsColor
                )
            }
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = if (isSelected) "✓ KİLİTLENDİ" else "%$votePercent",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = if (isSelected) Color(0xFF031A0E) else MatchPickPalette.TextMuted
            )
        }
    }
}

@Composable
private fun TeamEmblemColumn(
    name: String,
    logoUrl: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = MatchPickPalette.SurfaceElevated,
            border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
        ) {
            AsyncImage(
                model = logoUrl,
                contentDescription = name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MatchPickPalette.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LiveCountdownBadge() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(MatchPickPalette.NeonEmerald)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.match_status_upcoming),
            style = MaterialTheme.typography.labelSmall,
            color = MatchPickPalette.TextSecondary,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun CrowdVotesBar(home: Int, draw: Int, away: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(home.toFloat().coerceAtLeast(1f))
                    .fillMaxHeight()
                    .background(MatchPickPalette.NeonEmerald)
            )
            Box(
                modifier = Modifier
                    .weight(draw.toFloat().coerceAtLeast(1f))
                    .fillMaxHeight()
                    .background(MatchPickPalette.NeuralViolet)
            )
            Box(
                modifier = Modifier
                    .weight(away.toFloat().coerceAtLeast(1f))
                    .fillMaxHeight()
                    .background(MatchPickPalette.ElectricCyan)
            )
        }
    }
}

@Composable
private fun TacticalInsightPanel(
    fixture: FixtureEntity,
    onPickSelected: (String) -> Unit,
    onShare: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MatchPickPalette.GlassCardGradient)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.analysis_reason_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MatchPickPalette.TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MatchPickPalette.EmeraldDeep,
                    border = BorderStroke(0.5.dp, MatchPickPalette.NeonEmerald)
                ) {
                    Text(
                        text = stringResource(R.string.neural_radar_badge),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MatchPickPalette.NeonEmerald
                    )
                }
            }

            Text(
                text = fixture.aiAnalysis.ifEmpty { stringResource(R.string.tactical_metrics_fallback) },
                style = MaterialTheme.typography.bodyMedium,
                color = MatchPickPalette.TextSecondary,
                lineHeight = 20.sp
            )

            HorizontalDivider(color = MatchPickPalette.BorderSubtle)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.confidence_rate_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MatchPickPalette.TextMuted
                )
                Text(
                    text = "${fixture.officialConfidence}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MatchPickPalette.NeonEmerald
                )
            }

            // Tactical Odds Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OddsButton(
                    choice = "1",
                    odds = fixture.oddsHome,
                    votePercent = fixture.votesHomePercent,
                    isSelected = fixture.userPick == "1" || fixture.userPick == "HOME",
                    isEnabled = !fixture.isLocked,
                    onClick = { onPickSelected("1") },
                    modifier = Modifier.weight(1f)
                )
                OddsButton(
                    choice = "X",
                    odds = fixture.oddsDraw,
                    votePercent = fixture.votesDrawPercent,
                    isSelected = fixture.userPick == "X" || fixture.userPick == "DRAW",
                    isEnabled = !fixture.isLocked,
                    onClick = { onPickSelected("X") },
                    modifier = Modifier.weight(1f)
                )
                OddsButton(
                    choice = "2",
                    odds = fixture.oddsAway,
                    votePercent = fixture.votesAwayPercent,
                    isSelected = fixture.userPick == "2" || fixture.userPick == "AWAY",
                    isEnabled = !fixture.isLocked,
                    onClick = { onPickSelected("2") },
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = onShare,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MatchPickPalette.NeonEmerald)
            ) {
                Text(
                    text = stringResource(R.string.btn_share),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF031A0E)
                )
            }
        }
    }
}
