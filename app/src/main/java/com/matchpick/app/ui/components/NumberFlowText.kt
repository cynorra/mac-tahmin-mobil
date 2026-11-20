package com.matchpick.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * NumberFlow-style animated rolling digits component for Jetpack Compose.
 * Inspired by NumberFlow (https://number-flow.barvian.me) & modern fintech tickers.
 * Seamlessly rolls each character up or down when values change.
 */
@Composable
fun NumberFlowText(
    value: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        value.forEachIndexed { index, char ->
            NumberFlowChar(
                char = char,
                style = style,
                color = color,
                fontWeight = fontWeight
            )
        }
    }
}

@Composable
private fun NumberFlowChar(
    char: Char,
    style: TextStyle,
    color: Color,
    fontWeight: FontWeight?
) {
    if (char.isDigit()) {
        val digit = char.digitToInt()
        var prevDigit by remember { mutableIntStateOf(digit) }

        SideEffect {
            prevDigit = digit
        }

        val isIncreasing = digit >= prevDigit

        AnimatedContent(
            targetState = digit,
            transitionSpec = {
                if (isIncreasing) {
                    (slideInVertically { height -> height } + fadeIn())
                        .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                } else {
                    (slideInVertically { height -> -height } + fadeIn())
                        .togetherWith(slideOutVertically { height -> height } + fadeOut())
                }.using(SizeTransform(clip = false))
            },
            label = "NumberFlowDigit"
        ) { targetDigit ->
            Text(
                text = targetDigit.toString(),
                style = style,
                color = color,
                fontWeight = fontWeight
            )
        }
    } else {
        // Non-digit characters (e.g. '%', '.', ' ', 'P', 'T', 'S') stay static
        Text(
            text = char.toString(),
            style = style,
            color = color,
            fontWeight = fontWeight
        )
    }
}
