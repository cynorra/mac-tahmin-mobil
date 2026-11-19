package com.matchpick.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matchpick.app.core.ads.AdMobManager
import com.matchpick.app.core.analytics.AnalyticsTracker
import com.matchpick.app.core.performance.FrameRateMonitor
import com.matchpick.app.core.social.SocialShareManager
import com.matchpick.app.core.sync.DailySyncWorker
import com.matchpick.app.data.local.FixtureEntity
import com.matchpick.app.ui.components.SharePreviewDialog
import com.matchpick.app.ui.navigation.MainNavigationHost
import com.matchpick.app.ui.screens.feed.FeedViewModel
import com.matchpick.app.ui.screens.leaderboard.LeaderboardViewModel
import com.matchpick.app.ui.screens.official.RewardedAdController
import com.matchpick.app.ui.screens.predict.PredictViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var socialShareManager: SocialShareManager
    @Inject lateinit var analyticsTracker: AnalyticsTracker
    @Inject lateinit var frameRateMonitor: FrameRateMonitor
    @Inject lateinit var rewardedAdController: RewardedAdController

    private val predictViewModel: PredictViewModel by viewModels()
    private val feedViewModel: FeedViewModel by viewModels()
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Frame rate ve jank izleme
        frameRateMonitor.startMonitoring(this)

        // AdMob Interstitial'ı belleğe al (Geçişlerde 0 ms bekleme)
        AdMobManager.loadInterstitial(this, "ca-app-pub-3940256099942544/1033173712")

        // WorkManager periyodik senkronizasyon planla
        DailySyncWorker.schedule(this)

        // Deep Link kontrolü
        handleAppLinks(intent)

        setContent {
            val isDark = isSystemInDarkTheme()
            val predictState by predictViewModel.uiState.collectAsStateWithLifecycle()
            val leaderboardState by leaderboardViewModel.uiState.collectAsStateWithLifecycle()

            var sharingFixture by remember { mutableStateOf<FixtureEntity?>(null) }

            MaterialTheme(
                colorScheme = if (isDark) DarkColors else LightColors
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainNavigationHost(
                        adUnitId = "ca-app-pub-3940256099942544/6300978111",
                        predictViewModel = predictViewModel,
                        feedViewModel = feedViewModel,
                        leaderboardViewModel = leaderboardViewModel,
                        rewardedAdController = rewardedAdController,
                        onShareFixture = { fixture ->
                            sharingFixture = fixture
                        }
                    )

                    // Sosyal Paylaşım Açılır Penceresi
                    sharingFixture?.let { fixture ->
                        SharePreviewDialog(
                            fixture = fixture,
                            userPick = fixture.userPick ?: "1",
                            streakCount = predictState.streak,
                            referralCode = leaderboardState.referralCode.ifEmpty { "MATCH2026" },
                            shareManager = socialShareManager,
                            onDismiss = { sharingFixture = null }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAppLinks(intent)
    }

    private fun handleAppLinks(intent: Intent?) {
        val data = intent?.data ?: return
        if (data.host == "matchpick.app" && data.path?.startsWith("/invite") == true) {
            val refCode = data.getQueryParameter("ref")
            if (!refCode.isNullOrEmpty()) {
                analyticsTracker.logShareEvent("app_link_opened", refCode)
            }
        }
    }
}

private val DarkColors = darkColorScheme(
    primary = Color(0xFF00F59B),
    onPrimary = Color(0xFF041B11),
    primaryContainer = Color(0xFF0B291D),
    onPrimaryContainer = Color(0xFF6BFFB8),
    secondary = Color(0xFF00E5FF),
    onSecondary = Color(0xFF001F26),
    secondaryContainer = Color(0xFF003640),
    onSecondaryContainer = Color(0xFF6BF0FF),
    tertiary = Color(0xFF8B5CF6),
    background = Color(0xFF070B14),
    surface = Color(0xFF0E1424),
    surfaceVariant = Color(0xFF162035),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF26334D)
)

private val LightColors = DarkColors // 2026+ dark-first sports app default
