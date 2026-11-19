package com.matchpick.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matchpick.app.ui.theme.MatchPickPalette
import java.text.SimpleDateFormat
import java.util.*

@Immutable
data class DateItem(
    val dateString: String, // "2026-11-18"
    val dayLabel: String,   // "Wed"
    val dayNumber: String,  // "18"
    val isToday: Boolean = false
)

@Composable
fun DateSelectorStrip(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateList = remember { generateSurroundingDates() }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(dateList, key = { it.dateString }) { item ->
            val isSelected = item.dateString == selectedDate
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) MatchPickPalette.EmeraldDeep else MatchPickPalette.SurfaceDark,
                label = "dateBg"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.BorderSubtle,
                label = "dateBorder"
            )

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = bgColor,
                border = BorderStroke(if (isSelected) 1.2.dp else 0.5.dp, borderColor),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onDateSelected(item.dateString) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = item.dayLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.TextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.dayNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MatchPickPalette.TextPrimary else MatchPickPalette.TextSecondary
                    )
                    if (item.isToday) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(MatchPickPalette.NeonEmerald)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(7.dp))
                    }
                }
            }
        }
    }
}

private fun generateSurroundingDates(): List<DateItem> {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayLabelFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val dayNumberFormat = SimpleDateFormat("d", Locale.getDefault())

    val list = mutableListOf<DateItem>()
    calendar.add(Calendar.DAY_OF_YEAR, -2)

    for (i in 0..6) {
        val dateString = dateFormat.format(calendar.time)
        val dayLabel = dayLabelFormat.format(calendar.time)
        val dayNumber = dayNumberFormat.format(calendar.time)
        list.add(
            DateItem(
                dateString = dateString,
                dayLabel = dayLabel,
                dayNumber = dayNumber,
                isToday = dateString == todayStr
            )
        )
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }
    return list
}
