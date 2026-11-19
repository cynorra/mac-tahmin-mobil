package com.matchpick.app.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Box(
        modifier = Modifier
            .width(280.dp)
            .height(500.dp) // 9:16 Story formatı
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Dark Slate
                        Color(0xFF1E293B),
                        Color(0xFF059669)  // Accent Green
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Text(
                text = stringResource(R.string.share_match_title),
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            // Karşılaşma ve Tahmin Rozeti
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = fixture.homeTeamLogo,
                            contentDescription = null,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = fixture.homeTeamName,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "VS",
                        color = Color(0xFFF59E0B),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = fixture.awayTeamLogo,
                            contentDescription = null,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = fixture.awayTeamName,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "MY PICK: $userPick",
                        color = Color(0xFF34D399),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                if (streakCount > 1) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🔥 CURRENT STREAK: $streakCount IN A ROW",
                        color = Color(0xFFFBBF24),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Alt Kısım: CTA & Dinamik Referans Rozeti
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.share_card_cta),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = "USE CODE: $referralCode (+25 PTS)",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
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

    // Dark Slate Background
    val bgPaint = Paint().apply {
        color = android.graphics.Color.rgb(15, 23, 42)
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // Title
    val titlePaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 42f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    canvas.drawText("MATCHPICK CHALLENGE", width / 2f, 160f, titlePaint)

    // Teams
    val matchText = "${fixture.homeTeamName}  VS  ${fixture.awayTeamName}"
    val matchPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 36f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    canvas.drawText(matchText, width / 2f, 400f, matchPaint)

    // Prediction Box
    val boxPaint = Paint().apply {
        color = android.graphics.Color.rgb(5, 150, 105)
        isAntiAlias = true
    }
    val boxRect = RectF(160f, 520f, width - 160f, 660f)
    canvas.drawRoundRect(boxRect, 24f, 24f, boxPaint)

    val pickPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 50f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    canvas.drawText("MY PICK: $userPick", width / 2f, 605f, pickPaint)

    // Streak
    if (streakCount > 0) {
        val streakPaint = Paint().apply {
            color = android.graphics.Color.rgb(245, 158, 11)
            textSize = 34f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("🔥 CURRENT STREAK: $streakCount IN A ROW", width / 2f, 740f, streakPaint)
    }

    // Referral CTA
    val ctaPaint = Paint().apply {
        color = android.graphics.Color.LTGRAY
        textSize = 30f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    canvas.drawText("Download MatchPick & Win!", width / 2f, 1050f, ctaPaint)

    val refPaint = Paint().apply {
        color = android.graphics.Color.rgb(52, 211, 153)
        textSize = 36f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    canvas.drawText("REFERRAL CODE: $referralCode (+25 PTS)", width / 2f, 1120f, refPaint)

    return bitmap
}
