package com.matchpick.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.matchpick.app.R
import kotlinx.coroutines.delay

@Composable
fun MatchCountdownBadge(
    matchTimestamp: Long,
    onMatchLocked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var remainingMillis by remember(matchTimestamp) {
        mutableLongStateOf((matchTimestamp * 1000) - System.currentTimeMillis())
    }

    LaunchedEffect(matchTimestamp) {
        while (remainingMillis > 0) {
            delay(1000L)
            remainingMillis = (matchTimestamp * 1000) - System.currentTimeMillis()
        }
        onMatchLocked()
    }

    val isLocked = remainingMillis <= 0

    AnimatedContent(
        targetState = isLocked,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "CountdownAnimation"
    ) { locked ->
        if (locked) {
            Text(
                text = stringResource(R.string.match_locked),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                modifier = modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        } else {
            val totalSeconds = remainingMillis / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            val formattedTime = if (hours > 0) {
                String.format("%02dh %02dm", hours, minutes)
            } else {
                String.format("%02dm %02ds", minutes, seconds)
            }

            Text(
                text = stringResource(R.string.countdown_locks_in, formattedTime),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (hours == 0L && minutes < 15) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outline,
                modifier = modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
