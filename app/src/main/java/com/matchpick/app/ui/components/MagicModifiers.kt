package com.matchpick.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tactile spring press effect inspired by iOS and modern micro-interactions (Radix / Emil Kowalski).
 * Smoothly scales the component down to [scaleDown] when pressed and springs back on release.
 */
fun Modifier.bounceClickable(
    scaleDown: Float = 0.95f,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "BounceScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
        }
}

/**
 * Animated Shimmering Border Beam inspired by Magic UI (https://magicui.design) & Aceternity UI.
 * Sweeps an iridescent gradient along the card border to give a premium luxury finish.
 */
fun Modifier.magicBorderBeam(
    strokeWidth: Dp = 1.2.dp,
    durationMs: Int = 3000,
    colors: List<Color> = listOf(
        Color(0x0000E5FF),
        Color(0xFF00E5FF),
        Color(0xFFFFB020),
        Color(0x00FFB020)
    )
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "MagicBorder")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "BorderProgress"
    )

    this.drawWithContent {
        drawContent()
        val width = size.width
        val height = size.height

        val xOffset = width * progress
        val brush = Brush.linearGradient(
            colors = colors,
            start = Offset(xOffset - width * 0.4f, 0f),
            end = Offset(xOffset + width * 0.4f, height)
        )

        drawRoundRect(
            brush = brush,
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
            style = Stroke(width = strokeWidth.toPx())
        )
    }
}

/**
 * Ambient background radial glow effect (Aceternity UI Background Glow).
 */
fun Modifier.ambientGlow(
    color: Color,
    radiusRatio: Float = 0.8f,
    alpha: Float = 0.15f
): Modifier = this.drawBehind {
    val center = Offset(size.width / 2f, size.height / 2f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha),
                Color.Transparent
            ),
            center = center,
            radius = size.maxDimension * radiusRatio
        ),
        radius = size.maxDimension * radiusRatio,
        center = center
    )
}
