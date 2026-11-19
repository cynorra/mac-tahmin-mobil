package com.matchpick.app.ui.screens.official

import android.app.Activity
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
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
            // Test veya çevrimdışı fallback: Kullanıcıyı mağdur etmemek için kilidi aç
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
        containerColor = Color(0xFF070B14),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF070B14))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFFB800), Color(0xFFFF8A00))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
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
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
        Brush.linearGradient(listOf(Color(0x66FFB800), Color(0x228B5CF6)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF00F59B), Color(0x3300E5FF)))
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.2.dp, borderBrush),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF141E33), Color(0xFF0C1322))
                    )
                )
                .padding(18.dp)
        ) {
            // Top Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF281C08),
                    border = BorderStroke(1.dp, Color(0xFFFFB800))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⭐", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.hot_pick_badge),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFB800)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (pick.isVipLocked) Color(0xFF1E1635) else Color(0xFF0B291D),
                    border = BorderStroke(1.dp, if (pick.isVipLocked) Color(0xFF8B5CF6) else Color(0xFF00F59B))
                ) {
                    Text(
                        text = if (pick.isVipLocked) "%${pick.confidenceRate} ${stringResource(R.string.confidence_rate_label)}" else stringResource(R.string.vip_unlocked_badge),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (pick.isVipLocked) Color(0xFFC084FC) else Color(0xFF00F59B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Teams VS
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
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0x3364748B))
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
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = stringResource(R.string.versus),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pick.awayTeam,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0x3364748B))
                    ) {
                        AsyncImage(
                            model = pick.awayLogo,
                            contentDescription = pick.awayTeam,
                            modifier = Modifier.fillMaxSize().padding(6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Unlocked Content vs Locked Paywall/Rewarded Button
            if (pick.isVipLocked) {
                Button(
                    onClick = onUnlock,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFB800)
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
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0B291D),
                    border = BorderStroke(1.dp, Color(0x4400F59B))
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
                                color = Color(0xFF00F59B)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF00F59B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = pick.recommendedPick,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF00F59B)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = pick.analysisReason,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
