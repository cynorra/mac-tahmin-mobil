package com.matchpick.app.ui.screens.predict

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MatchPickPalette.DeepObsidian)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = 640.dp)
        ) {
            // 1. Sleek Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_app_logo),
                        contentDescription = "MatchPick Pro",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(0.5.dp, MatchPickPalette.BorderSubtle, RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MatchPick",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.3).sp
                            ),
                            color = MatchPickPalette.TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(5.dp),
                            color = MatchPickPalette.EmeraldDeep,
                            border = BorderStroke(0.5.dp, MatchPickPalette.EmeraldPrimary.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "PRO",
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MatchPickPalette.EmeraldPrimary,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                // Sleek Streak & Points Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MatchPickPalette.SurfaceDark,
                    border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_flame_pro),
                            contentDescription = null,
                            tint = MatchPickPalette.VipGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$userStreak",
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

            // 2. Streamlined Vertical Match List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Compact Date & League Filters
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

                // Empty State
                if (filteredFixtures.isEmpty()) {
                    item(key = "empty_fixtures") {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = MatchPickPalette.SurfaceDark,
                            border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "⚽", fontSize = 36.sp)
                                Text(
                                    text = stringResource(R.string.empty_fixtures_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MatchPickPalette.TextPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = stringResource(R.string.empty_fixtures_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MatchPickPalette.TextMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    // Item 0: Hero Spotlight Derby Card
                    item(key = "hero_spotlight") {
                        val heroFixture = filteredFixtures.first()
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            SpotlightDerbyHeroCard(
                                fixture = heroFixture,
                                onPickSelected = { pick -> onPredictSelected(heroFixture.id, pick) },
                                onShare = { onShareClicked(heroFixture) },
                                onOpenTelemetry = { telemetrySheetFixture = heroFixture }
                            )
                        }
                    }

                    // Remaining Fixtures
                    itemsIndexed(
                        items = filteredFixtures.drop(1),
                        key = { _, it -> it.id },
                        contentType = { _, _ -> "FixtureItem" }
                    ) { index, fixture ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            FixtureCard(
                                fixture = fixture,
                                onPickSelected = { pick -> onPredictSelected(fixture.id, pick) },
                                onShare = { onShareClicked(fixture) },
                                onCardClicked = { telemetrySheetFixture = fixture }
                            )
                        }

                        // Non-intrusive Inline Banner Ad every 3 fixtures
                        if (index > 0 && (index + 1) % 3 == 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AdaptiveBannerAd(
                                    adUnitId = AdConfig.BANNER_AD_UNIT_ID
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
 * 2026+ Spotlight Derby Hero Card (Ezan Vakti Pro Hero Dial Caliber)
 */
@Composable
fun SpotlightDerbyHeroCard(
    fixture: FixtureEntity,
    onPickSelected: (String) -> Unit,
    onShare: () -> Unit,
    onOpenTelemetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(
                    MatchPickPalette.NeonEmerald.copy(alpha = 0.45f),
                    MatchPickPalette.BorderSubtle,
                    Color(0x10FFFFFF)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatchPickPalette.GlassCardGradient)
                .padding(18.dp)
        ) {
            // Hero Badge Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MatchPickPalette.VipDeep,
                    border = BorderStroke(0.6.dp, MatchPickPalette.VipGold.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_crown_luxury),
                            contentDescription = null,
                            tint = MatchPickPalette.VipGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.marquee_derby_badge).uppercase(),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp,
                            color = MatchPickPalette.VipGold
                        )
                    }
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(32.dp)
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

            // Marquee Matchup Arena with Circular Win Probability Dial
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Club
                HeroTeamColumn(
                    name = fixture.homeTeamName,
                    logoUrl = fixture.homeTeamLogo,
                    probability = fixture.votesHomePercent,
                    modifier = Modifier.weight(1f),
                    alignment = Alignment.CenterHorizontally
                )

                // Central AI Probability Circular Gauge & Telemetry Pill
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .wrapContentWidth()
                ) {
                    // Neural Dial (Only Confidence & Label - Generous breathing room)
                    Box(
                        modifier = Modifier.size(78.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 3.5.dp.toPx()
                            val diameter = size.minDimension - strokeWidth
                            val topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2f, strokeWidth / 2f)
                            val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)

                            // Background Track
                            drawArc(
                                color = Color(0xFF141F30),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth)
                            )

                            // Confidence Arc with Smooth Caps
                            val sweepAngle = (fixture.officialConfidence / 100f).coerceIn(0f, 1f) * 360f
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(
                                        MatchPickPalette.NeonEmerald,
                                        Color(0xFF00E5FF),
                                        MatchPickPalette.NeonEmerald
                                    )
                                ),
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.ai_confidence_title),
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp,
                                color = MatchPickPalette.TextMuted,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "%${fixture.officialConfidence}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = MatchPickPalette.TextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dedicated, non-overlapping Tactical Pick Pill
                    fixture.officialPick?.let { pick ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MatchPickPalette.EmeraldDeep,
                            border = BorderStroke(0.6.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.45f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_sparkle_pro),
                                    contentDescription = null,
                                    tint = MatchPickPalette.NeonEmerald,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.pick_label, pick),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.3.sp,
                                    color = MatchPickPalette.NeonEmerald,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Away Club
                HeroTeamColumn(
                    name = fixture.awayTeamName,
                    logoUrl = fixture.awayTeamLogo,
                    probability = fixture.votesAwayPercent,
                    modifier = Modifier.weight(1f),
                    alignment = Alignment.CenterHorizontally
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1 - X - 2 Tactile Segmented Dock
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

            Spacer(modifier = Modifier.height(12.dp))

            // Expandable / Tappable Telemetry Pill
            Surface(
                onClick = onOpenTelemetry,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MatchPickPalette.SurfaceElevated,
                border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MatchPickPalette.NeonEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = fixture.aiAnalysis.ifEmpty { stringResource(R.string.ai_telemetry_hint) },
                            style = MaterialTheme.typography.labelSmall,
                            color = MatchPickPalette.TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MatchPickPalette.TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroTeamColumn(
    name: String,
    logoUrl: String,
    probability: Int,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Column(
        horizontalAlignment = alignment,
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.size(54.dp),
            shape = CircleShape,
            color = MatchPickPalette.SurfaceElevated,
            border = BorderStroke(1.dp, MatchPickPalette.BorderSubtle)
        ) {
            if (logoUrl.isNotBlank()) {
                AsyncImage(
                    model = logoUrl,
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = name.firstOrNull()?.uppercase() ?: "T",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.NeonEmerald
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MatchPickPalette.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.probability_label, probability),
            style = MaterialTheme.typography.labelSmall,
            color = MatchPickPalette.TextMuted,
            fontSize = 11.sp
        )
    }
}

/**
 * World-Class Match Card for Regular Fixtures
 */
@Composable
fun FixtureCard(
    fixture: FixtureEntity,
    onPickSelected: (String) -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
    onCardClicked: (() -> Unit)? = null
) {
    Surface(
        onClick = { onCardClicked?.invoke() },
        modifier = modifier.fillMaxWidth(),
        enabled = onCardClicked != null,
        shape = RoundedCornerShape(20.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(0.6.dp, MatchPickPalette.BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatchPickPalette.GlassCardGradient)
                .padding(14.dp)
        ) {
            // Header: League & Stadium + Share
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
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MatchPickPalette.SurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MatchPickPalette.TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Two Club Emblems & VS Kickoff
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
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MatchPickPalette.SurfaceElevated,
                        border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                    ) {
                        Text(
                            text = stringResource(R.string.versus),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MatchPickPalette.TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LiveCountdownBadge()
                }

                TeamEmblemColumn(
                    name = fixture.awayTeamName,
                    logoUrl = fixture.awayTeamLogo,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1 - X - 2 Odds Buttons
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MatchPickPalette.NeonEmerald,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.app_pick_badge, pick, fixture.officialConfidence),
                            style = MaterialTheme.typography.labelSmall,
                            color = MatchPickPalette.TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
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
 * Tactile Odds Button with Luxury Emerald Glass Selected State
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

    Surface(
        onClick = { onClick(choice) },
        modifier = modifier
            .scale(scale)
            .height(52.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) MatchPickPalette.EmeraldDeep else MatchPickPalette.SurfaceElevated,
        border = BorderStroke(
            if (isSelected) 1.dp else 0.5.dp,
            if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.BorderSubtle
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isSelected) MatchPickPalette.LuxurySelectedGradient else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = choice,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format(Locale.US, "%.2f", odds),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else MatchPickPalette.TextSecondary,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = if (isSelected) "✓ " + stringResource(R.string.pick_selected) else "%$votePercent",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp,
                color = if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.TextMuted,
                maxLines = 1
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
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = MatchPickPalette.SurfaceElevated,
            border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
        ) {
            if (logoUrl.isNotBlank()) {
                AsyncImage(
                    model = logoUrl,
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = name.firstOrNull()?.uppercase() ?: "T",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.NeonEmerald
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
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
                    .background(MatchPickPalette.VipGold)
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
