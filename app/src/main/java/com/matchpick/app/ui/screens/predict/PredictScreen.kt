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
                                listOf(Color(0xFF0A1122), MatchPickPalette.DeepObsidian)
                            )
                        )
                        .statusBarsPadding()
                ) {
                    // 2026+ Spatial Glass Command Header
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
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MatchPickPalette.NeonBrandGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⚡",
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stringResource(R.string.app_name),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF191233),
                                        border = BorderStroke(0.8.dp, MatchPickPalette.NeuralViolet)
                                    ) {
                                        Text(
                                            text = "AI 26+",
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFD8B4FE)
                                        )
                                    }
                                }
                                Text(
                                    text = stringResource(R.string.app_tagline),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MatchPickPalette.TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Holographic Streak & Points Capsule
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = Color(0xFF0D162A),
                            border = BorderStroke(
                                1.2.dp,
                                Brush.linearGradient(
                                    listOf(MatchPickPalette.NeonEmerald, MatchPickPalette.VipGold)
                                )
                            ),
                            shadowElevation = 6.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = "🔥 $userStreak",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MatchPickPalette.VipGold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(12.dp)
                                        .background(Color(0xFF334155))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.points_label, userPoints),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MatchPickPalette.NeonEmerald
                                )
                            }
                        }
                    }

                    // 2026+ Spatial Date Strip
                    DateSelectorStrip(
                        selectedDate = selectedDate,
                        onDateSelected = { selectedDate = it }
                    )

                    // Spatial League Filter Row
                    LeagueFilterRow(
                        selectedLeagueId = selectedLeague,
                        onLeagueSelected = { selectedLeague = it }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            },
            bottomBar = {
                Surface(
                    color = MatchPickPalette.DeepObsidian,
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
                var activeFixtureId by remember { mutableStateOf<String?>(null) }
                val currentFixture = filteredFixtures.find { it.id == activeFixtureId } ?: filteredFixtures.firstOrNull()

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            onCardClicked = { telemetrySheetFixture = fixture }
                        )
                    }
                }
            }
        }

        // Neural Telemetry Modal Bottom Sheet on mobile tap
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
    val infiniteTransition = rememberInfiniteTransition(label = "cardShimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerSweep"
    )

    val cardBorder = when {
        isSelectedInMasterDetail -> BorderStroke(1.8.dp, MatchPickPalette.NeonEmerald)
        isSpotlightMatch -> BorderStroke(
            1.6.dp,
            Brush.linearGradient(
                colors = listOf(
                    MatchPickPalette.NeonEmerald,
                    MatchPickPalette.ElectricCyan,
                    MatchPickPalette.NeuralViolet,
                    MatchPickPalette.NeonEmerald
                ),
                start = androidx.compose.ui.geometry.Offset(shimmerOffset - 400f, 0f),
                end = androidx.compose.ui.geometry.Offset(shimmerOffset + 400f, 400f)
            )
        )
        else -> BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(Color(0x3800F59B), Color(0x14FFFFFF), Color(0x2800E5FF))
            )
        )
    }

    val cardBrush = if (isSpotlightMatch) {
        Brush.verticalGradient(
            listOf(Color(0xFF14253D), Color(0xFF0E162B), Color(0xFF0A0F1D))
        )
    } else {
        MatchPickPalette.GlassCardGradient
    }

    var appeared by remember { mutableStateOf(false) }
    val cardEnterScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.94f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "cardEnterScale"
    )
    LaunchedEffect(Unit) { appeared = true }

    Surface(
        onClick = { onCardClicked?.invoke() },
        modifier = modifier
            .fillMaxWidth()
            .scale(cardEnterScale),
        enabled = onCardClicked != null,
        shape = RoundedCornerShape(24.dp),
        color = MatchPickPalette.SurfaceDark,
        border = cardBorder,
        shadowElevation = if (isSpotlightMatch) 14.dp else 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBrush)
                .padding(16.dp)
        ) {
            // Optional Spotlight Hero Banner for #1 Match of the Day
            if (isSpotlightMatch) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0A2E22),
                    border = BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            listOf(MatchPickPalette.NeonEmerald, MatchPickPalette.ElectricCyan)
                        )
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚡", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(R.string.hero_match_of_the_day),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = MatchPickPalette.NeonEmerald,
                            letterSpacing = 0.6.sp
                        )
                    }
                }
            }

            // Header Row: League Name, Stadium & Share Action
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
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MatchPickPalette.NeonEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = fixture.leagueName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MatchPickPalette.NeonEmerald
                    )
                    if (fixture.stadiumName.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "•", color = MatchPickPalette.TextMuted)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = fixture.stadiumName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MatchPickPalette.TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF16223A))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MatchPickPalette.NeonEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Teams VS Center Stage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamEmblemColumn(
                    name = fixture.homeTeamName,
                    logoUrl = fixture.homeTeamLogo,
                    accentColor = MatchPickPalette.NeonEmerald,
                    modifier = Modifier.weight(1f)
                )

                // Holographic VS Core & Live Pulse
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF16223A),
                        border = BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                listOf(MatchPickPalette.ElectricCyan, MatchPickPalette.NeuralViolet)
                            )
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.versus),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = MatchPickPalette.ElectricCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                    LiveCountdownBadge()
                }

                TeamEmblemColumn(
                    name = fixture.awayTeamName,
                    logoUrl = fixture.awayTeamLogo,
                    accentColor = MatchPickPalette.ElectricCyan,
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

            // AI Neural Radar & Pick Badge
            fixture.officialPick?.let { pick ->
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF17112E),
                    border = BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            listOf(MatchPickPalette.NeuralViolet.copy(alpha = 0.6f), MatchPickPalette.ElectricCyan.copy(alpha = 0.4f))
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFD8B4FE),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.app_pick_badge, pick, fixture.officialConfidence),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFE9D5FF)
                            )
                        }

                        Text(
                            text = "xG • AI ➔",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = MatchPickPalette.ElectricCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1 - X - 2 Spatial Odds Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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
        }
    }
}

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
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "oddsScale"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MatchPickPalette.NeonEmerald else Color(0xFF141E34),
        label = "oddsBg"
    )
    val textColor = if (isSelected) Color(0xFF03170E) else Color.White
    val oddsColor = if (isSelected) Color(0xFF03170E).copy(alpha = 0.85f) else MatchPickPalette.TextSecondary
    val border = if (isSelected) {
        BorderStroke(1.5.dp, Color.White.copy(alpha = 0.6f))
    } else {
        BorderStroke(1.dp, Color(0x2864748B))
    }

    Surface(
        onClick = { onClick(choice) },
        modifier = modifier
            .scale(scale)
            .height(56.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = border,
        shadowElevation = if (isSelected) 10.dp else 0.dp
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
                    fontWeight = FontWeight.Black,
                    color = textColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format("%.2f", odds),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = oddsColor
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isSelected) "✓ LOCKED IN" else "$votePercent% Pick",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = if (isSelected) Color(0xFF03170E) else MatchPickPalette.TextMuted
            )
        }
    }
}

@Composable
private fun TeamEmblemColumn(
    name: String,
    logoUrl: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(62.dp),
                shape = CircleShape,
                color = Color(0xFF152038),
                border = BorderStroke(
                    1.5.dp,
                    Brush.linearGradient(
                        listOf(accentColor.copy(alpha = 0.7f), Color(0x22FFFFFF))
                    )
                ),
                shadowElevation = 6.dp
            ) {
                AsyncImage(
                    model = logoUrl,
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
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
        initialValue = 0.35f,
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
                .background(MatchPickPalette.NeonEmerald.copy(alpha = alpha))
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.crowd_votes),
                style = MaterialTheme.typography.labelSmall,
                color = MatchPickPalette.TextMuted,
                fontSize = 10.sp
            )
            Row {
                Text(text = "1: $home%", color = MatchPickPalette.NeonEmerald, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "  •  ", color = Color(0xFF475569), fontSize = 10.sp)
                Text(text = "X: $draw%", color = Color(0xFFC084FC), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "  •  ", color = Color(0xFF475569), fontSize = 10.sp)
                Text(text = "2: $away%", color = MatchPickPalette.ElectricCyan, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
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
                    .background(Color(0xFFC084FC))
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
        shape = RoundedCornerShape(24.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(1.dp, Color(0x4400F59B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.analysis_reason_label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MatchPickPalette.NeonEmerald
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF191233),
                        border = BorderStroke(1.dp, MatchPickPalette.NeuralViolet)
                    ) {
                        Text(
                            text = stringResource(R.string.neural_radar_badge),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFD8B4FE)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
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
                    Text(text = stringResource(R.string.confidence_rate_label), color = MatchPickPalette.TextSecondary)
                    Text(text = "${fixture.officialConfidence}%", color = MatchPickPalette.NeonEmerald, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(20.dp))
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
            }

            Button(
                onClick = onShare,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MatchPickPalette.NeonEmerald)
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
