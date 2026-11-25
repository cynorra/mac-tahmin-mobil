package com.matchpick.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        LeagueFilterItem("MY_PICKS", R.string.league_my_picks, "🎯", MatchPickPalette.VipGold),
        LeagueFilterItem("ucl", R.string.league_ucl, "🏆", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("epl", R.string.league_epl, "🦁", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("laliga", R.string.league_laliga, "🇪🇸", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("superlig", R.string.league_superlig, "🇹🇷", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("bundesliga", R.string.league_bundesliga, "🇩🇪", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("seriea", R.string.league_seriea, "🇮🇹", MatchPickPalette.NeonEmerald)
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp)
    ) {
        items(leagues, key = { it.id }) { item ->
            val isSelected = item.id == selectedLeagueId

            val containerColor by animateColorAsState(
                targetValue = if (isSelected) MatchPickPalette.EmeraldDeep else MatchPickPalette.SurfaceDark,
                label = "leagueBg"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.BorderSubtle,
                label = "leagueBorder"
            )

            Surface(
                onClick = { onLeagueSelected(item.id) },
                shape = RoundedCornerShape(20.dp),
                color = containerColor,
                border = BorderStroke(if (isSelected) 1.2.dp else 0.5.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.iconEmoji,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(item.titleRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MatchPickPalette.TextPrimary else MatchPickPalette.TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
