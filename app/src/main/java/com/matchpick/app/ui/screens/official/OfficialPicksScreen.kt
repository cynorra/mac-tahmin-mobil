package com.matchpick.app.ui.screens.official

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.matchpick.app.R
import com.matchpick.app.ui.theme.MatchPickPalette

@Immutable
data class OfficialPickUiModel(
    val fixtureId: String,
    val leagueName: String,
    val homeTeam: String,
    val homeLogo: String,
    val awayTeam: String,
    val awayLogo: String,
    val recommendedPick: String,
    val confidenceRate: Int,
    val analysisReason: String,
    val isVipLocked: Boolean = true
)

class RewardedAdController(private val context: Context) {
    private var rewardedAd: RewardedAd? = null

    fun loadAd(adUnitId: String) {
        RewardedAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    fun showAd(activity: Activity, onRewardEarned: () -> Unit) {
        rewardedAd?.let { ad ->
            ad.show(activity) { _ ->
                onRewardEarned()
                rewardedAd = null
                loadAd(ad.adUnitId)
            }
        } ?: run {
            onRewardEarned()
        }
    }
}

@Composable
fun OfficialPicksScreen(
    picks: List<OfficialPickUiModel>,
    rewardedAdController: RewardedAdController,
    onUnlockPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        containerColor = MatchPickPalette.DeepObsidian,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF141024), MatchPickPalette.DeepObsidian)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MatchPickPalette.VipCrownGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.vip_header_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = stringResource(R.string.vip_header_subtitle),
                                style = MaterialTheme.typography.labelSmall,
                                color = MatchPickPalette.TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2026+ Neural Accuracy Holographic Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF15122A),
                    border = BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            listOf(MatchPickPalette.VipGold, MatchPickPalette.NeuralViolet, MatchPickPalette.NeonEmerald)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MatchPickPalette.VipGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.vip_accuracy_banner),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFDE68A)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = MatchPickPalette.NeonEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(picks, key = { it.fixtureId }) { pickItem ->
                VipPickCard(
                    pick = pickItem,
                    onUnlock = {
                        activity?.let { act ->
                            rewardedAdController.showAd(act) {
                                onUnlockPick(pickItem.fixtureId)
                            }
                        } ?: onUnlockPick(pickItem.fixtureId)
                    }
                )
            }
        }
    }
}

@Composable
fun VipPickCard(
    pick: OfficialPickUiModel,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderBrush = if (pick.isVipLocked) {
        Brush.linearGradient(
            listOf(MatchPickPalette.VipGold, MatchPickPalette.NeuralViolet, Color(0x33FFB800))
        )
    } else {
        Brush.linearGradient(
            listOf(MatchPickPalette.NeonEmerald, MatchPickPalette.ElectricCyan)
        )
    }

    val cardBgBrush = if (pick.isVipLocked) {
        Brush.verticalGradient(listOf(Color(0xFF17132E), Color(0xFF0B0F1E)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF0E2326), Color(0xFF0A1120)))
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MatchPickPalette.SurfaceDark,
        border = BorderStroke(1.4.dp, borderBrush),
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBgBrush)
                .padding(18.dp)
        ) {
            // Top Badges & League Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF281A05),
                        border = BorderStroke(1.dp, MatchPickPalette.VipGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "👑", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.hot_pick_badge),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = MatchPickPalette.VipGold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pick.leagueName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (pick.isVipLocked) Color(0xFF1E1436) else Color(0xFF0A2E20),
                    border = BorderStroke(
                        1.dp,
                        if (pick.isVipLocked) MatchPickPalette.NeuralViolet else MatchPickPalette.NeonEmerald
                    )
                ) {
                    Text(
                        text = if (pick.isVipLocked) "%${pick.confidenceRate} ${stringResource(R.string.confidence_rate_label)}" else stringResource(R.string.vip_unlocked_badge),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = if (pick.isVipLocked) Color(0xFFD8B4FE) else MatchPickPalette.NeonEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Teams Arena Showcase
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(50.dp),
                        shape = CircleShape,
                        color = Color(0xFF17223B),
                        border = BorderStroke(1.2.dp, MatchPickPalette.VipGold.copy(alpha = 0.5f))
                    ) {
                        AsyncImage(
                            model = pick.homeLogo,
                            contentDescription = pick.homeTeam,
                            modifier = Modifier.fillMaxSize().padding(7.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = pick.homeTeam,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1A243B)
                ) {
                    Text(
                        text = stringResource(R.string.versus),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = MatchPickPalette.VipGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pick.awayTeam,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        modifier = Modifier.size(50.dp),
                        shape = CircleShape,
                        color = Color(0xFF17223B),
                        border = BorderStroke(1.2.dp, MatchPickPalette.ElectricCyan.copy(alpha = 0.5f))
                    ) {
                        AsyncImage(
                            model = pick.awayLogo,
                            contentDescription = pick.awayTeam,
                            modifier = Modifier.fillMaxSize().padding(7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Neural Confidence Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.metric_win_probability),
                        style = MaterialTheme.typography.labelSmall,
                        color = MatchPickPalette.TextMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${pick.confidenceRate}% Neural Confidence",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (pick.isVipLocked) MatchPickPalette.VipGold else MatchPickPalette.NeonEmerald,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                LinearProgressIndicator(
                    progress = { (pick.confidenceRate / 100f).coerceIn(0.1f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (pick.isVipLocked) MatchPickPalette.VipGold else MatchPickPalette.NeonEmerald,
                    trackColor = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Unlocked Content vs Cyber-Vault Locked Button with Smooth Morphing
            androidx.compose.animation.AnimatedContent(
                targetState = pick.isVipLocked,
                label = "vipVaultUnlockTransition"
            ) { locked ->
                if (locked) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF12182B),
                        border = BorderStroke(1.dp, Color(0x33FFB800)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔒 ${stringResource(R.string.vip_vault_locked_hint)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFDE68A),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onUnlock,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MatchPickPalette.VipGold
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF041B11),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.rewarded_unlock_pick),
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF041B11)
                                )
                            }
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF09261C),
                        border = BorderStroke(1.2.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.analysis_reason_label),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MatchPickPalette.NeonEmerald
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF051A12),
                                    border = BorderStroke(1.dp, MatchPickPalette.NeonEmerald)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MatchPickPalette.NeonEmerald,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = pick.recommendedPick,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Black,
                                            color = MatchPickPalette.NeonEmerald
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = pick.analysisReason,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE2E8F0),
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
