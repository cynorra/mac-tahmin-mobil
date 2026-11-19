package com.matchpick.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class LeagueFilter(val id: String, val name: String)

@Composable
fun LeagueFilterChips(
    selectedLeagueId: String,
    onLeagueSelected: (String) -> Unit,
    countryCode: String,
    modifier: Modifier = Modifier
) {
    val leagues = if (countryCode == "TR") {
        listOf(
            LeagueFilter("ALL", "Tümü"),
            LeagueFilter("superlig", "Süper Lig"),
            LeagueFilter("tff1", "1. Lig"),
            LeagueFilter("ucl", "Şampiyonlar Ligi"),
            LeagueFilter("epl", "Premier Lig")
        )
    } else {
        listOf(
            LeagueFilter("ALL", "All Leagues"),
            LeagueFilter("mls", "MLS"),
            LeagueFilter("epl", "Premier League"),
            LeagueFilter("ucl", "UEFA Champions"),
            LeagueFilter("laliga", "La Liga")
        )
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(leagues, key = { it.id }) { league ->
            val isSelected = league.id == selectedLeagueId
            FilterChip(
                selected = isSelected,
                onClick = { onLeagueSelected(league.id) },
                label = { Text(league.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}
