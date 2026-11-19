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
import androidx.compose.ui.graphics.Brush
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
                targetValue = if (isSelected) Color(0xFF0B2E22) else Color(0xFF0D1424),
                label = "dateBg"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) MatchPickPalette.NeonEmerald else Color(0x2264748B),
                label = "dateBorder"
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = bgColor,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onDateSelected(item.dateString) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = item.dayLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.TextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.dayNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.White else MatchPickPalette.TextSecondary
                    )
                    if (item.isToday) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .size(5.dp)
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

private fun generateSurroundingDates(): List<DateItem> {
    val calendar = Calendar.getInstance()
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    calendar.add(Calendar.DAY_OF_YEAR, -1)

    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val numberFormat = SimpleDateFormat("dd", Locale.getDefault())
    val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    return (0..5).map {
        val date = calendar.time
        val dateIso = isoFormat.format(date)
        val item = DateItem(
            dateString = dateIso,
            dayLabel = dayFormat.format(date),
            dayNumber = numberFormat.format(date),
            isToday = dateIso == todayStr
        )
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        item
    }
}
