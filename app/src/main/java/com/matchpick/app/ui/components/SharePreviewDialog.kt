package com.matchpick.app.ui.components

import android.app.Activity
import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.matchpick.app.R
import com.matchpick.app.core.ads.AdMobManager
import com.matchpick.app.core.social.SocialShareManager
import com.matchpick.app.data.local.FixtureEntity

@Composable
fun SharePreviewDialog(
    fixture: FixtureEntity,
    userPick: String,
    streakCount: Int,
    referralCode: String,
    shareManager: SocialShareManager,
    onDismiss: () -> Unit
) {
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val activity = context as? Activity

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Kapatma Barı
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.share_match_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Kartın Render Edilen Canlı Önizlemesi
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    ShareableStoryCard(
                        fixture = fixture,
                        userPick = userPick,
                        streakCount = streakCount,
                        referralCode = referralCode,
                        onBitmapReady = { bitmap ->
                            generatedBitmap = bitmap
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Platform Seçim Butonları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Instagram Story
                    Button(
                        onClick = {
                            generatedBitmap?.let { bmp ->
                                shareManager.shareToInstagramStory(bmp)
                                activity?.let { act -> AdMobManager.showInterstitial(act) {} }
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Story", style = MaterialTheme.typography.labelMedium)
                    }

                    // WhatsApp
                    Button(
                        onClick = {
                            generatedBitmap?.let { bmp ->
                                val message = context.getString(
                                    R.string.share_match_body,
                                    fixture.homeTeamName,
                                    fixture.awayTeamName,
                                    referralCode,
                                    "https://matchpick.app/invite?ref=$referralCode"
                                )
                                shareManager.shareToWhatsApp(bmp, message)
                                activity?.let { act -> AdMobManager.showInterstitial(act) {} }
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WhatsApp", style = MaterialTheme.typography.labelMedium)
                    }

                    // Diğer (Generic Share Sheet)
                    IconButton(
                        onClick = {
                            generatedBitmap?.let { bmp ->
                                val link = "https://matchpick.app/invite?ref=$referralCode"
                                shareManager.shareGeneric(
                                    shareManager.saveBitmapToCache(bmp),
                                    link
                                )
                                onDismiss()
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "More")
                    }
                }
            }
        }
    }
}
