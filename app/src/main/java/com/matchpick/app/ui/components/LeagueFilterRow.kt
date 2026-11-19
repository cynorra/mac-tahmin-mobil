package com.matchpick.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matchpick.app.R

data class LeagueFilterItem(val id: String, val titleRes: Int, val iconEmoji: String)

@Composable
fun LeagueFilterRow(
    selectedLeagueId: String,
    onLeagueSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val leagues = listOf(
        LeagueFilterItem("ALL", R.string.league_all, "⚡"),
        LeagueFilterItem("epl", R.string.league_epl, "🦁"),
        LeagueFilterItem("laliga", R.string.league_laliga, "🇪🇸"),
        LeagueFilterItem("superlig", R.string.league_superlig, "🇹🇷"),
        LeagueFilterItem("bundesliga", R.string.league_bundesliga, "🇩🇪"),
        LeagueFilterItem("seriea", R.string.league_seriea, "🇮🇹")
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
    ) {
        items(leagues, key = { it.id }) { item ->
            val isSelected = item.id == selectedLeagueId

            val borderColor = if (isSelected) Color(0xFF00F59B) else Color(0x3364748B)

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onLeagueSelected(item.id) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) Color(0xFF0B291D) else Color(0xFF111827),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.iconEmoji,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(item.titleRes),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = if (isSelected) Color(0xFF00F59B) else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
