package com.matchpick.app.ui.screens.predict

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.matchpick.app.ui.components.LeagueFilterRow

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

    val filteredFixtures = remember(fixtures, selectedLeague) {
        if (selectedLeague == "ALL") fixtures
        else fixtures.filter { it.leagueId.equals(selectedLeague, ignoreCase = true) }
    }

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
                ) {
                    // Top Brand Bar
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
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF00F59B), Color(0xFF00E5FF))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⚡",
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = stringResource(R.string.app_tagline),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Streak and Points Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF111827),
                            border = BorderStroke(1.dp, Color(0x3300F59B))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "🔥 ${stringResource(R.string.streak_label)} $userStreak",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFB800)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "•",
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.points_label, userPoints),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00F59B)
                                )
                            }
                        }
                    }

                    // Modern League Filter Row
                    LeagueFilterRow(
                        selectedLeagueId = selectedLeague,
                        onLeagueSelected = { selectedLeague = it }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            },
            bottomBar = {
                // AdMob Adaptive Banner at the Bottom
                Surface(
                    color = Color(0xFF070B14),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AdaptiveBannerAd(
                        adUnitId = "ca-app-pub-3940256099942544/6300978111",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        ) { paddingValues ->
            if (isWideScreen) {
                // Responsive Dual-Pane View for Wide / Landscape Screens
                var activeFixtureId by remember { mutableStateOf<String?>(null) }
                val currentFixture = filteredFixtures.find { it.id == activeFixtureId } ?: filteredFixtures.firstOrNull()

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Match Fixtures List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = filteredFixtures,
                            key = { it.id },
                            contentType = { "FixtureItem" }
                        ) { fixture ->
                            FixtureCard(
                                fixture = fixture,
                                onPickSelected = { pick -> onPredictSelected(fixture.id, pick) },
                                onShare = { onShareClicked(fixture) },
                                isSelectedInMasterDetail = currentFixture?.id == fixture.id,
                                onCardClicked = { activeFixtureId = fixture.id }
                            )
                        }
                    }

                    // Right Column: Tactical Neural Insights & Breakdown
                    Box(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight()
                            .padding(vertical = 8.dp)
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
                // Standard Single-Column Mobile Layout
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = filteredFixtures,
                        key = { it.id },
                        contentType = { "FixtureItem" }
                    ) { fixture ->
                        FixtureCard(
                            fixture = fixture,
                            onPickSelected = { pick -> onPredictSelected(fixture.id, pick) },
                            onShare = { onShareClicked(fixture) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FixtureCard(
    fixture: FixtureEntity,
    onPickSelected: (String) -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
    isSelectedInMasterDetail: Boolean = false,
    onCardClicked: (() -> Unit)? = null
) {
    val cardBorder = if (isSelectedInMasterDetail) {
        BorderStroke(1.5.dp, Color(0xFF00F59B))
    } else {
        BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(Color(0x3300F59B), Color(0x15FFFFFF), Color(0x2200E5FF))
            )
        )
    }

    Surface(
        onClick = { onCardClicked?.invoke() },
        modifier = modifier.fillMaxWidth(),
        enabled = onCardClicked != null,
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0E1424),
        border = cardBorder,
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
            // Header Row: League Name, Stadium & Share Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00F59B))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = fixture.leagueName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00F59B)
                    )
                    if (fixture.stadiumName.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "•", color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = fixture.stadiumName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF00F59B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Teams VS Center Stage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team
                TeamEmblemColumn(
                    name = fixture.homeTeamName,
                    logoUrl = fixture.homeTeamLogo,
                    modifier = Modifier.weight(1f)
                )

                // VS & Countdown Badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, Color(0x3364748B))
                    ) {
                        Text(
                            text = stringResource(R.string.versus),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00E5FF)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    LiveCountdownBadge()
                }

                // Away Team
                TeamEmblemColumn(
                    name = fixture.awayTeamName,
                    logoUrl = fixture.awayTeamLogo,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Crowd Votes Distribution Bar
            CrowdVotesBar(
                home = fixture.votesHomePercent,
                draw = fixture.votesDrawPercent,
                away = fixture.votesAwayPercent
            )

            // AI Pick Badge
            fixture.officialPick?.let { pick ->
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E1435),
                    border = BorderStroke(1.dp, Color(0x448B5CF6))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🧠",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.app_pick_badge, pick, fixture.officialConfidence),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC084FC)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1 - X - 2 Odds Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OddsButton(
                    choice = stringResource(R.string.pick_home),
                    odds = fixture.oddsHome,
                    isSelected = fixture.userPick == "1" || fixture.userPick == "HOME",
                    isEnabled = !fixture.isLocked,
                    onClick = { onPickSelected("1") },
                    modifier = Modifier.weight(1f)
                )
                OddsButton(
                    choice = stringResource(R.string.pick_draw),
                    odds = fixture.oddsDraw,
                    isSelected = fixture.userPick == "X" || fixture.userPick == "DRAW",
                    isEnabled = !fixture.isLocked,
                    onClick = { onPickSelected("X") },
                    modifier = Modifier.weight(1f)
                )
                OddsButton(
                    choice = stringResource(R.string.pick_away),
                    odds = fixture.oddsAway,
                    isSelected = fixture.userPick == "2" || fixture.userPick == "AWAY",
                    isEnabled = !fixture.isLocked,
                    onClick = { onPickSelected("2") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OddsButton(
    choice: String,
    odds: Float,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) Color(0xFF00F59B) else Color(0xFF161F33)
    val textColor = if (isSelected) Color(0xFF041B11) else Color.White
    val oddsColor = if (isSelected) Color(0xFF041B11).copy(alpha = 0.8f) else Color(0xFF94A3B8)
    val border = if (isSelected) BorderStroke(1.dp, Color(0xFF00F59B)) else BorderStroke(1.dp, Color(0x2264748B))

    Surface(
        onClick = { onClick(choice) },
        modifier = modifier.height(52.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = border,
        shadowElevation = if (isSelected) 8.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = choice,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = textColor
            )
            Text(
                text = String.format("%.2f", odds),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = oddsColor,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun TeamEmblemColumn(name: String, logoUrl: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = Color(0xFF162035),
            border = BorderStroke(1.5.dp, Color(0x3300F59B))
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
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LiveCountdownBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF00F59B).copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.match_status_upcoming),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF94A3B8),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun CrowdVotesBar(home: Int, draw: Int, away: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.crowd_votes),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64748B),
                fontSize = 10.sp
            )
            Row {
                Text(text = "$home%", color = Color(0xFF00F59B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = " • ", color = Color(0xFF475569), fontSize = 10.sp)
                Text(text = "$draw%", color = Color(0xFFC084FC), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = " • ", color = Color(0xFF475569), fontSize = 10.sp)
                Text(text = "$away%", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
        ) {
            Box(modifier = Modifier.weight(home.toFloat().coerceAtLeast(1f)).fillMaxHeight().background(Color(0xFF00F59B)))
            Box(modifier = Modifier.weight(draw.toFloat().coerceAtLeast(1f)).fillMaxHeight().background(Color(0xFFC084FC)))
            Box(modifier = Modifier.weight(away.toFloat().coerceAtLeast(1f)).fillMaxHeight().background(Color(0xFF00E5FF)))
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
        color = Color(0xFF0E1424),
        border = BorderStroke(1.dp, Color(0x3300F59B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.analysis_reason_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00F59B)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = fixture.aiAnalysis.ifEmpty { stringResource(R.string.tactical_metrics_fallback) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0x2264748B))
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(R.string.confidence_rate_label), color = Color(0xFF94A3B8))
                    Text(text = "${fixture.officialConfidence}%", color = Color(0xFF00F59B), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OddsButton(
                        choice = "1",
                        odds = fixture.oddsHome,
                        isSelected = fixture.userPick == "1" || fixture.userPick == "HOME",
                        isEnabled = !fixture.isLocked,
                        onClick = { onPickSelected("1") },
                        modifier = Modifier.weight(1f)
                    )
                    OddsButton(
                        choice = "X",
                        odds = fixture.oddsDraw,
                        isSelected = fixture.userPick == "X" || fixture.userPick == "DRAW",
                        isEnabled = !fixture.isLocked,
                        onClick = { onPickSelected("X") },
                        modifier = Modifier.weight(1f)
                    )
                    OddsButton(
                        choice = "2",
                        odds = fixture.oddsAway,
                        isSelected = fixture.userPick == "2" || fixture.userPick == "AWAY",
                        isEnabled = !fixture.isLocked,
                        onClick = { onPickSelected("2") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Button(
                onClick = onShare,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F59B))
            ) {
                Text(
                    text = stringResource(R.string.btn_share),
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF041B11)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
