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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.matchpick.app.ui.theme.MatchPickPalette

/**
 * Tactile spring press effect inspired by iOS 18 / visionOS and Emil Kowalski's micro-interactions.
 * Smoothly scales and subtly shifts elevation when pressed, springing back with organic physics.
 */
fun Modifier.bounceClickable(
    scaleDown: Float = 0.95f,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val currentOnClick by rememberUpdatedState(onClick)
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
                    onTap = { currentOnClick() }
                )
            }
        }
}

/**
 * Animated Shimmering Border Beam inspired by Magic UI & Linear.
 * Sweeps a soft, refined iridescent highlight along the card border.
 */
fun Modifier.magicBorderBeam(
    strokeWidth: Dp = 1.2.dp,
    cornerRadius: Dp = 22.dp,
    durationMs: Int = 4200,
    colors: List<Color> = listOf(
        Color(0x0012DBA0),
        Color(0x9912DBA0),
        Color(0x9922D3EE),
        Color(0x0022D3EE)
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
            start = Offset(xOffset - width * 0.45f, 0f),
            end = Offset(xOffset + width * 0.45f, height)
        )

        drawRoundRect(
            brush = brush,
            size = size,
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style = Stroke(width = strokeWidth.toPx())
        )
    }
}

/**
 * visionOS / Apple Sports style specular top-edge glass highlight + subtle inner radial illumination.
 * Makes cards feel like physical frosted glass catching overhead stadium light.
 */
fun Modifier.spatialGlassHighlight(
    accentColor: Color = MatchPickPalette.NeonEmerald,
    cornerRadius: Dp = 20.dp,
    glowAlpha: Float = 0.08f
): Modifier = this.drawWithContent {
    // 1. Subtle top-left radial ambient light inside the card
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                accentColor.copy(alpha = glowAlpha),
                Color.Transparent
            ),
            center = Offset(size.width * 0.2f, 0f),
            radius = size.width * 0.75f
        ),
        radius = size.width * 0.75f,
        center = Offset(size.width * 0.2f, 0f)
    )

    drawContent()

    // 2. Top-edge specular rim reflection
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.16f),
                Color.Transparent
            ),
            startY = 0f,
            endY = size.height * 0.35f
        ),
        size = size,
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = Stroke(width = 1.dp.toPx())
    )
}

/**
 * Ambient background radial glow effect.
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

/**
 * 2027+ Living Spatial Aurora Mesh Canvas.
 * Renders slow-breathing, ultra-soothing atmospheric light orbs behind the app canvas
 * so the screen never feels flat, cold, or suffocating.
 */
@Composable
fun SpatialAuroraBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SpatialAurora")
    val drift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuroraDrift"
    )

    val isAmoled = MatchPickPalette.isAmoled
    val emeraldAlpha = if (isAmoled) 0.10f else 0.15f
    val cyanAlpha = if (isAmoled) 0.08f else 0.12f
    val violetAlpha = if (isAmoled) 0.07f else 0.11f

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Top-Left Emerald Pitch Aurora Orb
        val orb1Center = Offset(
            x = w * (0.15f + 0.15f * drift),
            y = h * (0.08f + 0.05f * drift)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    MatchPickPalette.NeonEmerald.copy(alpha = emeraldAlpha),
                    Color.Transparent
                ),
                center = orb1Center,
                radius = w * 0.85f
            ),
            radius = w * 0.85f,
            center = orb1Center
        )

        // Top-Right Sky Cyan Spatial Orb
        val orb2Center = Offset(
            x = w * (0.85f - 0.12f * drift),
            y = h * (0.18f + 0.06f * (1f - drift))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    MatchPickPalette.ElectricCyan.copy(alpha = cyanAlpha),
                    Color.Transparent
                ),
                center = orb2Center,
                radius = w * 0.75f
            ),
            radius = w * 0.75f,
            center = orb2Center
        )

        // Mid-Bottom Indigo Depth Orb
        val orb3Center = Offset(
            x = w * (0.5f + 0.1f * (0.5f - drift)),
            y = h * (0.65f - 0.05f * drift)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    MatchPickPalette.NeuralViolet.copy(alpha = violetAlpha),
                    Color.Transparent
                ),
                center = orb3Center,
                radius = w * 0.9f
            ),
            radius = w * 0.9f,
            center = orb3Center
        )
    }
}
