package com.matchpick.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matchpick.app.R
import com.matchpick.app.ui.theme.MatchPickPalette

data class LeagueFilterItem(
    val id: String,
    val titleRes: Int,
    val iconEmoji: String,
    val accentColor: Color
)

@Composable
fun LeagueFilterRow(
    selectedLeagueId: String,
    onLeagueSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val leagues = listOf(
        LeagueFilterItem("ALL", R.string.league_all, "⚡", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("epl", R.string.league_epl, "🦁", MatchPickPalette.ElectricCyan),
        LeagueFilterItem("laliga", R.string.league_laliga, "🇪🇸", MatchPickPalette.VipGold),
        LeagueFilterItem("superlig", R.string.league_superlig, "🇹🇷", MatchPickPalette.CrimsonLive),
        LeagueFilterItem("bundesliga", R.string.league_bundesliga, "🇩🇪", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("seriea", R.string.league_seriea, "🇮🇹", MatchPickPalette.NeuralViolet)
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
    ) {
        items(leagues, key = { it.id }) { item ->
            val isSelected = item.id == selectedLeagueId

            val containerColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFF0B2E22) else Color(0xFF0E1526),
                label = "leagueBg"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) MatchPickPalette.NeonEmerald else Color(0x2864748B),
                label = "leagueBorder"
            )
            val horizontalPad by animateDpAsState(
                targetValue = if (isSelected) 16.dp else 13.dp,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "leaguePad"
            )

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onLeagueSelected(item.id) },
                shape = RoundedCornerShape(22.dp),
                color = containerColor,
                border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
                shadowElevation = if (isSelected) 8.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = horizontalPad, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.iconEmoji,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = stringResource(item.titleRes),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                        color = if (isSelected) Color.White else MatchPickPalette.TextSecondary
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(MatchPickPalette.NeonEmerald, MatchPickPalette.ElectricCyan)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}
