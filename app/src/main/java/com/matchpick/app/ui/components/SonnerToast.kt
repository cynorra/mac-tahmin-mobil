package com.matchpick.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matchpick.app.R
import com.matchpick.app.ui.theme.MatchPickPalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class SonnerType {
    SUCCESS,
    ERROR,
    WARNING,
    INFO,
    VIP
}

data class SonnerMessage(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val description: String? = null,
    val type: SonnerType = SonnerType.INFO,
    val durationMs: Long = 3500L
)

class SonnerState {
    var currentMessage by mutableStateOf<SonnerMessage?>(null)
        private set

    fun show(
        message: String,
        type: SonnerType = SonnerType.INFO,
        description: String? = null,
        durationMs: Long = 3500L
    ) {
        currentMessage = SonnerMessage(
            message = message,
            description = description,
            type = type,
            durationMs = durationMs
        )
    }

    fun dismiss() {
        currentMessage = null
    }
}

val LocalSonner = compositionLocalOf { SonnerState() }

@Composable
fun rememberSonnerState(): SonnerState = remember { SonnerState() }

/**
 * Sonner-style polished in-app toast banner.
 * Anchors at the top or bottom of the viewport with fluid spring physics,
 * glassmorphic micro-borders, and tactile dismiss gesture.
 */
@Composable
fun SonnerHost(
    state: SonnerState,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopCenter
) {
    val message = state.currentMessage
    val scope = rememberCoroutineScope()
    var dragOffset by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        contentAlignment = alignment
    ) {
        AnimatedVisibility(
            visible = message != null,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(animationSpec = tween(220)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            ) + fadeOut(animationSpec = tween(180))
        ) {
            if (message != null) {
                val progress = remember(message.id) { Animatable(1f) }

                LaunchedEffect(message.id) {
                    dragOffset = 0f
                    launch {
                        progress.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(
                                durationMillis = message.durationMs.toInt(),
                                easing = LinearEasing
                            )
                        )
                    }
                    delay(message.durationMs)
                    state.dismiss()
                }

                val accentColor = when (message.type) {
                    SonnerType.SUCCESS -> MatchPickPalette.NeonEmerald
                    SonnerType.ERROR -> MatchPickPalette.CrimsonLive
                    SonnerType.WARNING -> Color(0xFFFFB020)
                    SonnerType.INFO -> Color(0xFF00E5FF)
                    SonnerType.VIP -> MatchPickPalette.VipGold
                }

                val borderColor = accentColor.copy(alpha = 0.35f)
                val backgroundColor = Color(0xF20B1220)

                Surface(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .offset { IntOffset(dragOffset.roundToInt(), 0) }
                        .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = accentColor.copy(alpha = 0.25f))
                        .clip(RoundedCornerShape(16.dp))
                        .pointerInput(message.id) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (kotlin.math.abs(dragOffset) > 120f) {
                                        state.dismiss()
                                    } else {
                                        dragOffset = 0f
                                    }
                                },
                                onHorizontalDrag = { _, dragAmount ->
                                    dragOffset += dragAmount
                                }
                            )
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = backgroundColor,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon Capsule
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                when (message.type) {
                                    SonnerType.SUCCESS -> Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    SonnerType.ERROR -> Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    SonnerType.WARNING -> Icon(
                                        imageVector = Icons.Default.WarningAmber,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    SonnerType.INFO -> Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    SonnerType.VIP -> Icon(
                                        painter = painterResource(R.drawable.ic_crown_luxury),
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Message Content
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = message.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MatchPickPalette.TextPrimary,
                                    lineHeight = 18.sp
                                )
                                message.description?.let { desc ->
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MatchPickPalette.TextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = { state.dismiss() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = MatchPickPalette.TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        // Subtle countdown line at bottom
                        LinearProgressIndicator(
                            progress = { progress.value },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp),
                            color = accentColor.copy(alpha = 0.5f),
                            trackColor = Color.Transparent
                        )
                    }
                }
            }
        }
    }
}
