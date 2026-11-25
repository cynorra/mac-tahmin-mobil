package com.matchpick.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
    val badgeCode: String,
    val accentColor: Color
)

@Composable
fun LeagueFilterRow(
    selectedLeagueId: String,
    onLeagueSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val leagues = listOf(
        LeagueFilterItem("ALL", R.string.league_all, "ALL", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("MY_PICKS", R.string.league_my_picks, "PRO", MatchPickPalette.VipGold),
        LeagueFilterItem("ucl", R.string.league_ucl, "UCL", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("epl", R.string.league_epl, "ENG", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("laliga", R.string.league_laliga, "ESP", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("superlig", R.string.league_superlig, "TUR", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("bundesliga", R.string.league_bundesliga, "GER", MatchPickPalette.NeonEmerald),
        LeagueFilterItem("seriea", R.string.league_seriea, "ITA", MatchPickPalette.NeonEmerald)
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
                targetValue = if (isSelected) item.accentColor else MatchPickPalette.BorderSubtle,
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
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) item.accentColor else MatchPickPalette.TextMuted)
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
