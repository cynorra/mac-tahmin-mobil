package com.matchpick.app.ui.screens.official

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.matchpick.app.R
import com.matchpick.app.core.ads.AdConfig
import com.matchpick.app.core.ads.AdaptiveBannerAd
import com.matchpick.app.core.util.findActivity
import com.matchpick.app.ui.components.LocalSonner
import com.matchpick.app.ui.components.NumberFlowText
import com.matchpick.app.ui.components.SonnerType
import com.matchpick.app.ui.components.bounceClickable
import com.matchpick.app.ui.components.magicBorderBeam
import com.matchpick.app.ui.components.spatialGlassHighlight
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
    var isAdLoadingState = mutableStateOf(false)
        private set

    // Pending show callbacks when user clicked before load completed
    private var pendingActivity: Activity? = null
    private var pendingOnReward: (() -> Unit)? = null
    private var pendingOnFailed: ((String) -> Unit)? = null
    private val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var pendingTimeoutRunnable: Runnable? = null

    fun loadAd(adUnitId: String = AdConfig.REWARDED_AD_UNIT_ID, onLoaded: (() -> Unit)? = null) {
        if (!com.matchpick.app.core.ads.AdMobManager.canRequestAds(context) && !com.matchpick.app.core.ads.AdMobManager.consentReady.value) return
        if (isAdLoadingState.value) {
            android.util.Log.d("GoogleAdsAnalytics", "⏳ [REWARD] Ad already loading, skipping duplicate request.")
            return
        }
        if (rewardedAd != null) {
            android.util.Log.d("GoogleAdsAnalytics", "⚡ [REWARD] Ad already loaded and ready in cache.")
            onLoaded?.invoke()
            return
        }

        isAdLoadingState.value = true
        android.util.Log.d("GoogleAdsAnalytics", "📡 [REWARD] Requesting Rewarded Ad: unit=$adUnitId")
        RewardedAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    isAdLoadingState.value = false
                    rewardedAd = ad
                    android.util.Log.d("GoogleAdsAnalytics", "✅ [REWARD] Rewarded ad loaded successfully: unit=$adUnitId")
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            android.util.Log.d("GoogleAdsAnalytics", "👁️ [CONVERSION] Rewarded Ad Impression: unit=$adUnitId")
                        }
                        override fun onAdClicked() {
                            android.util.Log.d("GoogleAdsAnalytics", "🎯 [CONVERSION] Rewarded Ad Clicked: unit=$adUnitId")
                        }
                        override fun onAdDismissedFullScreenContent() {
                            android.util.Log.d("GoogleAdsAnalytics", "👋 [REWARD] Rewarded ad dismissed.")
                            rewardedAd = null
                            loadAd(adUnitId)
                        }
                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            android.util.Log.e("GoogleAdsAnalytics", "❌ [REWARD] Rewarded ad failed to show: ${error.message}")
                            rewardedAd = null
                            loadAd(adUnitId)
                        }
                    }

                    // Auto-show immediately if user clicked while ad was fetching
                    pendingTimeoutRunnable?.let { mainHandler.removeCallbacks(it) }
                    val act = pendingActivity
                    val rewardCb = pendingOnReward
                    if (act != null && rewardCb != null) {
                        pendingActivity = null
                        pendingOnReward = null
                        pendingOnFailed = null
                        rewardedAd = null
                        ad.show(act) { rewardItem ->
                            android.util.Log.d("GoogleAdsAnalytics", "🏆 [REWARD] Rewarded ad reward earned: ${rewardItem.amount} ${rewardItem.type}")
                            rewardCb()
                        }
                    } else {
                        onLoaded?.invoke()
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isAdLoadingState.value = false
                    rewardedAd = null
                    android.util.Log.w("GoogleAdsAnalytics", "⚠️ Rewarded ad failed to load: code=${error.code}, msg=${error.message}")
                    pendingTimeoutRunnable?.let { mainHandler.removeCallbacks(it) }
                    val failCb = pendingOnFailed
                    pendingActivity = null
                    pendingOnReward = null
                    pendingOnFailed = null
                    failCb?.invoke(error.message)
                }
            }
        )
    }

    fun isAdLoaded(): Boolean = rewardedAd != null

    fun showAd(
        activity: Activity,
        onLoadingStarted: () -> Unit,
        onFailed: (String) -> Unit,
        onRewardEarned: () -> Unit
    ) {
        val currentAd = rewardedAd
        if (currentAd != null) {
            rewardedAd = null
            currentAd.show(activity) { rewardItem ->
                android.util.Log.d("GoogleAdsAnalytics", "🏆 [REWARD] Rewarded ad reward earned: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned()
            }
        } else {
            android.util.Log.d("GoogleAdsAnalytics", "⏳ [REWARD] Ad not cached yet, engaging fluid auto-show pipeline.")
            pendingActivity = activity
            pendingOnReward = onRewardEarned
            pendingOnFailed = onFailed
            onLoadingStarted()

            // 10 second timeout fallback
            val timeout = Runnable {
                if (pendingActivity != null) {
                    pendingActivity = null
                    pendingOnReward = null
                    val fail = pendingOnFailed
                    pendingOnFailed = null
                    isAdLoadingState.value = false
                    fail?.invoke(context.getString(R.string.ad_timeout_error))
                }
            }
            pendingTimeoutRunnable = timeout
            mainHandler.postDelayed(timeout, 10000L)

            loadAd(AdConfig.REWARDED_AD_UNIT_ID)
        }
    }
}

@Composable
fun OfficialPicksScreen(
    picks: List<OfficialPickUiModel>,
    rewardedAdController: RewardedAdController,
    activity: Activity,
    onUnlockPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    androidx.compose.runtime.LaunchedEffect(Unit) {
        rewardedAdController.loadAd(AdConfig.REWARDED_AD_UNIT_ID)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
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
            val sonner = LocalSonner.current
            var loadingPickId by remember { mutableStateOf<String?>(null) }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(picks, key = { _, it -> it.fixtureId }) { index, pickItem ->
                    VipPickCard(
                        pick = pickItem,
                        isLoadingAd = loadingPickId == pickItem.fixtureId,
                        onUnlock = {
                            rewardedAdController.showAd(
                                activity = activity,
                                onLoadingStarted = {
                                    loadingPickId = pickItem.fixtureId
                                    sonner.show(
                                        message = context.getString(R.string.ad_loading_reward),
                                        type = SonnerType.VIP,
                                        durationMs = 4000L
                                    )
                                },
                                onFailed = { errorMsg ->
                                    loadingPickId = null
                                    sonner.show(
                                        message = context.getString(R.string.ad_error_reward),
                                        description = errorMsg,
                                        type = SonnerType.ERROR,
                                        durationMs = 4500L
                                    )
                                },
                                onRewardEarned = {
                                    loadingPickId = null
                                    onUnlockPick(pickItem.fixtureId)
                                    sonner.show(
                                        message = context.getString(R.string.vip_unlocked_success),
                                        type = SonnerType.SUCCESS,
                                        durationMs = 3500L
                                    )
                                }
                            )
                        }
                    )

                    // Non-intrusive banner divider after every 2 VIP picks
                    if (index > 0 && (index + 1) % 2 == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AdaptiveBannerAd(adUnitId = AdConfig.BANNER_AD_UNIT_ID)
                        }
                    }
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
    isLoadingAd: Boolean = false,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBorder = if (pick.isVipLocked) {
        BorderStroke(1.dp, MatchPickPalette.GlassCardBorderBrush)
    } else {
        BorderStroke(1.2.dp, MatchPickPalette.HeroBorderBrush)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .spatialGlassHighlight(
                accentColor = if (pick.isVipLocked) MatchPickPalette.VipGold else MatchPickPalette.NeonEmerald,
                cornerRadius = 22.dp,
                glowAlpha = 0.11f
            )
            .then(if (!pick.isVipLocked) Modifier.magicBorderBeam(strokeWidth = 1.2.dp, cornerRadius = 22.dp) else Modifier),
        shape = RoundedCornerShape(22.dp),
        color = MatchPickPalette.SurfaceDark.copy(alpha = 0.92f),
        shadowElevation = 12.dp,
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
                            text = stringResource(R.string.percentage_format, pick.confidenceRate),
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
                    enabled = !isLoadingAd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLoadingAd) MatchPickPalette.SurfaceElevated else MatchPickPalette.VipGold,
                        disabledContainerColor = MatchPickPalette.SurfaceElevated
                    )
                ) {
                    if (isLoadingAd) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MatchPickPalette.VipGold,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.ad_loading_indicator),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MatchPickPalette.VipGold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
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
