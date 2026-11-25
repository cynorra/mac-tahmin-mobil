package com.matchpick.app.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.matchpick.app.R
import com.matchpick.app.data.local.FixtureEntity
import com.matchpick.app.ui.theme.MatchPickPalette

@Composable
fun ShareableStoryCard(
    fixture: FixtureEntity,
    userPick: String,
    streakCount: Int,
    referralCode: String,
    onBitmapReady: (Bitmap) -> Unit
) {
    LaunchedEffect(fixture.id, userPick, streakCount, referralCode) {
        val bmp = createStoryBitmap(fixture, userPick, streakCount, referralCode)
        onBitmapReady(bmp)
    }

    Surface(
        modifier = Modifier
            .width(290.dp)
            .height(510.dp),
        shape = RoundedCornerShape(26.dp),
        color = MatchPickPalette.DeepObsidian,
        border = BorderStroke(
            1.8.dp,
            Brush.linearGradient(
                listOf(
                    MatchPickPalette.NeonEmerald,
                    MatchPickPalette.ElectricCyan,
                    MatchPickPalette.NeuralViolet
                )
            )
        ),
        shadowElevation = 16.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A1428),
                            Color(0xFF121C38),
                            Color(0xFF062E22)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF191233),
                        border = BorderStroke(1.dp, MatchPickPalette.NeuralViolet)
                    ) {
                        Text(
                            text = stringResource(R.string.story_header_badge),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color(0xFFE9D5FF),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = fixture.leagueName,
                        color = MatchPickPalette.NeonEmerald,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Match Duel & Holographic Pick Ticket
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                modifier = Modifier.size(60.dp),
                                shape = CircleShape,
                                color = Color(0xFF16223A),
                                border = BorderStroke(1.5.dp, MatchPickPalette.NeonEmerald)
                            ) {
                                AsyncImage(
                                    model = fixture.homeTeamLogo,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().padding(9.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = fixture.homeTeamName,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1A2540),
                            border = BorderStroke(1.dp, MatchPickPalette.ElectricCyan)
                        ) {
                            Text(
                                text = "VS",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = MatchPickPalette.ElectricCyan,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                modifier = Modifier.size(60.dp),
                                shape = CircleShape,
                                color = Color(0xFF16223A),
                                border = BorderStroke(1.5.dp, MatchPickPalette.ElectricCyan)
                            ) {
                                AsyncImage(
                                    model = fixture.awayTeamLogo,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().padding(9.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = fixture.awayTeamName,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0A2E20),
                        border = BorderStroke(1.5.dp, MatchPickPalette.NeonEmerald)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.story_locked_prediction),
                                color = MatchPickPalette.ElectricCyan,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.pick_label, userPick),
                                color = MatchPickPalette.NeonEmerald,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    if (streakCount > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.story_win_streak, streakCount),
                            color = MatchPickPalette.VipGold,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Bottom Referral Ticket
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.share_card_cta),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF070C18),
                        border = BorderStroke(1.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = stringResource(R.string.story_code_bonus, referralCode),
                            color = MatchPickPalette.NeonEmerald,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun createStoryBitmap(
    fixture: FixtureEntity,
    userPick: String,
    streakCount: Int,
    referralCode: String
): Bitmap {
    val width = 720
    val height = 1280
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Deep Cyber Obsidian Background
    val bgPaint = Paint().apply {
        color = android.graphics.Color.rgb(7, 11, 20)
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // Outer Neon Frame
    val framePaint = Paint().apply {
        color = android.graphics.Color.rgb(0, 245, 155)
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }
    canvas.drawRoundRect(RectF(32f, 32f, width - 32f, height - 32f), 40f, 40f, framePaint)

    // Title
    val titlePaint = Paint().apply {
        color = android.graphics.Color.rgb(0, 229, 255)
        textSize = 40f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    canvas.drawText("MATCHPICK ARENA 2026+", width / 2f, 150f, titlePaint)

    // League
    val leaguePaint = Paint().apply {
        color = android.graphics.Color.rgb(0, 245, 155)
        textSize = 28f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    canvas.drawText(fixture.leagueName.uppercase(), width / 2f, 205f, leaguePaint)

    // Teams
    val matchText = "${fixture.homeTeamName}  VS  ${fixture.awayTeamName}"
    val matchPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 38f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    canvas.drawText(matchText, width / 2f, 420f, matchPaint)

    // Prediction Box
    val boxPaint = Paint().apply {
        color = android.graphics.Color.rgb(10, 46, 32)
        isAntiAlias = true
    }
    val boxRect = RectF(140f, 520f, width - 140f, 670f)
    canvas.drawRoundRect(boxRect, 28f, 28f, boxPaint)
    canvas.drawRoundRect(boxRect, 28f, 28f, framePaint)

    val pickPaint = Paint().apply {
        color = android.graphics.Color.rgb(0, 245, 155)
        textSize = 52f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    canvas.drawText("MY PICK: $userPick", width / 2f, 615f, pickPaint)

    // Streak
    if (streakCount > 0) {
        val streakPaint = Paint().apply {
            color = android.graphics.Color.rgb(255, 184, 0)
            textSize = 34f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("CURRENT STREAK: $streakCount IN A ROW", width / 2f, 755f, streakPaint)
    }

    // Referral CTA
    val ctaPaint = Paint().apply {
        color = android.graphics.Color.LTGRAY
        textSize = 30f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    canvas.drawText("Predict & Dominate on MatchPick", width / 2f, 1060f, ctaPaint)

    val refPaint = Paint().apply {
        color = android.graphics.Color.rgb(0, 245, 155)
        textSize = 36f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    canvas.drawText("VIP CODE: $referralCode (+25 PTS)", width / 2f, 1130f, refPaint)

    return bitmap
}
