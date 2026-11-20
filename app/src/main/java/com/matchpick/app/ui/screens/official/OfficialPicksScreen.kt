package com.matchpick.app.ui.screens.official

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
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
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            android.util.Log.d("GoogleAdsAnalytics", "👁️ [CONVERSION] Rewarded Ad Impression: unit=$adUnitId")
                        }
                        override fun onAdClicked() {
                            android.util.Log.d("GoogleAdsAnalytics", "🎯 [CONVERSION] Rewarded Ad Clicked: unit=$adUnitId")
                        }
                        override fun onAdDismissedFullScreenContent() {
                            rewardedAd = null
                            loadAd(adUnitId)
                        }
                    }
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    android.util.Log.w("GoogleAdsAnalytics", "Rewarded ad failed to load: ${error.message}")
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MatchPickPalette.DeepObsidian)
            .statusBarsPadding()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = 720.dp)
    ) {
        // Luxury Top Bar with Bespoke Geometric Crown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MatchPickPalette.VipDeep)
                    .border(1.dp, MatchPickPalette.VipGold.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_crown_luxury),
                    contentDescription = null,
                    tint = MatchPickPalette.VipGold,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.vip_header_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MatchPickPalette.TextPrimary
                )
                Text(
                    text = stringResource(R.string.vip_header_subtitle),
                    style = MaterialTheme.typography.labelSmall,
                    color = MatchPickPalette.TextSecondary
                )
            }
        }

        // Integrated Neural Intelligence Telemetry Ribbon
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(14.dp),
            color = MatchPickPalette.SurfaceDark,
            border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MatchPickPalette.GlassCardGradient)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "91.4%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MatchPickPalette.TextPrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(MatchPickPalette.BorderSubtle)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.vip_accuracy_label),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = MatchPickPalette.TextSecondary
                        )
                        Text(
                            text = stringResource(R.string.vip_accuracy_sublabel),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = MatchPickPalette.TextMuted
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MatchPickPalette.EmeraldDeep)
                        .border(0.5.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_shield_check_pro),
                        contentDescription = null,
                        tint = MatchPickPalette.NeonEmerald,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.vip_verified_badge),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        color = MatchPickPalette.NeonEmerald
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (picks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.widthIn(max = 420.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MatchPickPalette.VipDeep,
                        border = BorderStroke(1.dp, MatchPickPalette.VipGold.copy(alpha = 0.3f)),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_crown_luxury),
                                contentDescription = null,
                                tint = MatchPickPalette.VipGold,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.vip_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.vip_empty_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MatchPickPalette.TextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(picks, key = { it.fixtureId }) { pickItem ->
                    VipPickCard(
                        pick = pickItem,
                        onUnlock = {
                            activity?.let { act ->
                                rewardedAdController.showAd(act) {
                                    onUnlockPick(pickItem.fixtureId)
                                }
                            }
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
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
    val cardBorder = if (pick.isVipLocked) {
        BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
    } else {
        BorderStroke(0.8.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.45f))
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MatchPickPalette.SurfaceDark,
        border = cardBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatchPickPalette.GlassCardGradient)
                .padding(16.dp)
        ) {
            // Top Telemetry Header & League
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_crown_luxury),
                        contentDescription = null,
                        tint = MatchPickPalette.VipGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.hot_pick_badge).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp,
                        color = MatchPickPalette.VipGold
                    )
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatchPickPalette.TextMuted
                    )
                    Text(
                        text = pick.leagueName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MatchPickPalette.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (pick.isVipLocked) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MatchPickPalette.SurfaceElevated)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_sparkle_pro),
                            contentDescription = null,
                            tint = MatchPickPalette.VipGold,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%${pick.confidenceRate}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MatchPickPalette.TextPrimary
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MatchPickPalette.EmeraldDeep)
                            .border(0.5.dp, MatchPickPalette.NeonEmerald.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_shield_check_pro),
                            contentDescription = null,
                            tint = MatchPickPalette.NeonEmerald,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.vip_unlocked_badge),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            color = MatchPickPalette.NeonEmerald
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Teams Row
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
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = MatchPickPalette.SurfaceElevated,
                        border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                    ) {
                        AsyncImage(
                            model = pick.homeLogo,
                            contentDescription = pick.homeTeam,
                            modifier = Modifier.fillMaxSize().padding(6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = pick.homeTeam,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MatchPickPalette.TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MatchPickPalette.SurfaceElevated,
                    border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                ) {
                    Text(
                        text = stringResource(R.string.versus),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MatchPickPalette.TextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
                        fontWeight = FontWeight.SemiBold,
                        color = MatchPickPalette.TextPrimary,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = MatchPickPalette.SurfaceElevated,
                        border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle)
                    ) {
                        AsyncImage(
                            model = pick.awayLogo,
                            contentDescription = pick.awayTeam,
                            modifier = Modifier.fillMaxSize().padding(6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Confidence Indicator
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
                        text = stringResource(R.string.ai_confidence, pick.confidenceRate),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (pick.isVipLocked) MatchPickPalette.VipGold else MatchPickPalette.NeonEmerald,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (pick.confidenceRate / 100f).coerceIn(0.1f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (pick.isVipLocked) MatchPickPalette.VipGold else MatchPickPalette.NeonEmerald,
                    trackColor = MatchPickPalette.SurfaceElevated
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Unlock / Details Section
            if (pick.isVipLocked) {
                Button(
                    onClick = onUnlock,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatchPickPalette.VipGold
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_crown_luxury),
                        contentDescription = null,
                        tint = Color(0xFF03180D),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.rewarded_unlock_pick),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF03180D),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MatchPickPalette.EmeraldDeep,
                    border = BorderStroke(0.5.dp, MatchPickPalette.NeonEmerald)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MatchPickPalette.NeonEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = pick.recommendedPick,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MatchPickPalette.NeonEmerald
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = pick.analysisReason,
                            style = MaterialTheme.typography.bodySmall,
                            color = MatchPickPalette.TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
