package com.matchpick.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.matchpick.app.ui.theme.MatchPickPalette
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CelebrationDialog(
    title: String,
    message: String,
    streakCount: Int,
    onClaimReward: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "celebrationPulse")
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloScale"
    )
    val burstProgress by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "burstProgress"
    )

    var entered by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (entered) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "cardEnterScale"
    )
    LaunchedEffect(Unit) { entered = true }

    Dialog(onDismissRequest = onClaimReward) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MatchPickPalette.SurfaceDark,
            border = BorderStroke(
                1.6.dp,
                Brush.linearGradient(
                    listOf(
                        MatchPickPalette.NeonEmerald,
                        MatchPickPalette.VipGold,
                        MatchPickPalette.ElectricCyan
                    )
                )
            ),
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .scale(cardScale)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF16223B),
                                Color(0xFF0A101E)
                            )
                        )
                    )
            ) {
                // Radial Particle Burst Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    val center = Offset(size.width / 2f, size.height * 0.48f)
                    val particleColors = listOf(
                        MatchPickPalette.NeonEmerald,
                        MatchPickPalette.VipGold,
                        MatchPickPalette.ElectricCyan,
                        MatchPickPalette.NeuralViolet
                    )
                    for (i in 0 until 12) {
                        val angleRad = Math.toRadians((i * 30).toDouble())
                        val distance = 36.dp.toPx() + (52.dp.toPx() * burstProgress)
                        val px = center.x + (cos(angleRad) * distance).toFloat()
                        val py = center.y + (sin(angleRad) * distance).toFloat()
                        drawCircle(
                            color = particleColors[i % particleColors.size].copy(alpha = 1f - burstProgress * 0.75f),
                            radius = (5.dp.toPx() * (1.1f - burstProgress * 0.5f)).coerceAtLeast(2f),
                            center = Offset(px, py)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .scale(haloScale)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            MatchPickPalette.VipGold.copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        Surface(
                            modifier = Modifier.size(68.dp),
                            shape = CircleShape,
                            color = Color(0xFF281A05),
                            border = BorderStroke(2.dp, MatchPickPalette.VipGold)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🔥", fontSize = 34.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MatchPickPalette.TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0A2E20),
                        border = BorderStroke(
                            1.2.dp,
                            Brush.linearGradient(
                                listOf(MatchPickPalette.NeonEmerald, MatchPickPalette.ElectricCyan)
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$streakCount STREAK • +10 XP BOOST",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black,
                                color = MatchPickPalette.NeonEmerald
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = onClaimReward,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatchPickPalette.NeonEmerald,
                            contentColor = Color(0xFF03170E)
                        )
                    ) {
                        Text(
                            text = "DEVAM ET • KEEP DOMINATING",
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
