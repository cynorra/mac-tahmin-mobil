package com.matchpick.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matchpick.app.R
import com.matchpick.app.core.ads.AdConfig
import com.matchpick.app.core.ads.AdMobManager
import com.matchpick.app.core.analytics.AnalyticsTracker
import com.matchpick.app.core.feedback.FeedbackManager
import com.matchpick.app.core.performance.FrameRateMonitor
import com.matchpick.app.core.social.SocialShareManager
import com.matchpick.app.core.sync.DailySyncWorker
import com.matchpick.app.data.local.FixtureEntity
import com.matchpick.app.ui.components.CelebrationDialog
import com.matchpick.app.ui.components.SharePreviewDialog
import com.matchpick.app.ui.navigation.MainNavigationHost
import com.matchpick.app.ui.screens.auth.CinematicSplashIntro
import com.matchpick.app.ui.screens.feed.FeedViewModel
import com.matchpick.app.ui.screens.leaderboard.LeaderboardViewModel
import com.matchpick.app.ui.screens.official.RewardedAdController
import com.matchpick.app.ui.screens.predict.PredictViewModel
import com.matchpick.app.ui.theme.matchPickDarkColorScheme
import com.matchpick.app.ui.theme.MatchPickPalette
import com.matchpick.app.ui.theme.MatchPickTypography
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var socialShareManager: SocialShareManager
    @Inject lateinit var analyticsTracker: AnalyticsTracker
    @Inject lateinit var frameRateMonitor: FrameRateMonitor
    @Inject lateinit var rewardedAdController: RewardedAdController
    @Inject lateinit var feedbackManager: FeedbackManager
    @Inject lateinit var credentialAuthManager: com.matchpick.app.core.auth.CredentialAuthManager

    private val predictViewModel: PredictViewModel by viewModels()
    private val feedViewModel: FeedViewModel by viewModels()
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Frame rate ve jank izleme
        frameRateMonitor.startMonitoring(this)

        // UMP onayi (GDPR) sonrasi Interstitial + Rewarded on yukleme
        AdMobManager.gatherConsent(this) {
            AdMobManager.loadInterstitial(this, AdConfig.INTERSTITIAL_AD_UNIT_ID)
            rewardedAdController.loadAd(AdConfig.REWARDED_AD_UNIT_ID)
        }

        // WorkManager periyodik senkronizasyon planla
        DailySyncWorker.schedule(this)

        // Deep Link kontrolü
        handleAppLinks(intent)

        // Anonim / Misafir oturumunu başlat ve Google Ads Analytics oturumunu logla
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            try {
                auth.signInAnonymously().addOnSuccessListener { res ->
                    val uid = res.user?.uid ?: "anonymous_user"
                    analyticsTracker.logUserSessionStarted(uid, isGuest = true)
                }.addOnFailureListener {
                    analyticsTracker.logUserSessionStarted("anonymous_guest", isGuest = true)
                }
            } catch (_: Exception) {
                analyticsTracker.logUserSessionStarted("anonymous_guest", isGuest = true)
            }
        } else {
            val uid = auth.currentUser?.uid ?: "anonymous_guest"
            analyticsTracker.logUserSessionStarted(uid, isGuest = auth.currentUser?.isAnonymous == true)
        }

        MatchPickPalette.isAmoled = getSharedPreferences("app_settings", MODE_PRIVATE).getBoolean("pref_amoled", false)

        setContent {
            val predictState by predictViewModel.uiState.collectAsStateWithLifecycle()
            val leaderboardState by leaderboardViewModel.uiState.collectAsStateWithLifecycle()

            var showCinematicSplash by rememberSaveable { mutableStateOf(true) }
            var showAuth by rememberSaveable { mutableStateOf(false) }
            var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
            var privacyOptionsRequired by remember { mutableStateOf(AdMobManager.isPrivacyOptionsRequired(this@MainActivity)) }
            val authWebClientId = remember {
                val id = resources.getIdentifier("default_web_client_id", "string", packageName)
                if (id != 0) getString(id) else ""
            }
            LaunchedEffect(Unit) {
                // UMP durumu acilista asenkron guncellenir; birkac saniye yokla
                repeat(10) {
                    privacyOptionsRequired = AdMobManager.isPrivacyOptionsRequired(this@MainActivity)
                    kotlinx.coroutines.delay(1000)
                }
            }
            var sharingFixture by remember { mutableStateOf<FixtureEntity?>(null) }
            var celebratedStreak by rememberSaveable { mutableIntStateOf(0) }
            var activeStreakModal by remember { mutableStateOf<Int?>(null) }

            // Her 3 tahmin serisinde (6, 9, 12...) 2026+ Holografik Kutlama Modali ve Haptic Tetikle
            LaunchedEffect(predictState.streak) {
                val current = predictState.streak
                if (current > celebratedStreak && current % 3 == 0) {
                    celebratedStreak = current
                    activeStreakModal = current
                    try {
                        feedbackManager.playWinFeedback()
                    } catch (_: Exception) {}
                }
            }

            MaterialTheme(
                colorScheme = matchPickDarkColorScheme(),
                typography = MatchPickTypography
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MatchPickPalette.DeepObsidian
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MainNavigationHost(
                            activity = this@MainActivity,
                            adUnitId = AdConfig.BANNER_AD_UNIT_ID,
                            predictViewModel = predictViewModel,
                            feedViewModel = feedViewModel,
                            leaderboardViewModel = leaderboardViewModel,
                            rewardedAdController = rewardedAdController,
                            analyticsTracker = analyticsTracker,
                            onShareFixture = { fixture ->
                                sharingFixture = fixture
                            },
                            onRequestSignIn = { showAuth = true },
                            onLogout = {
                                credentialAuthManager.signOut()
                                recreate()
                            },
                            onRequestDelete = { showDeleteDialog = true },
                            showPrivacyOptions = privacyOptionsRequired,
                            onPrivacyOptions = { AdMobManager.showPrivacyOptions(this@MainActivity) }
                        )

                        if (showDeleteDialog) {
                            com.matchpick.app.ui.screens.auth.DeleteAccountDialog(
                                authManager = credentialAuthManager,
                                webClientId = authWebClientId,
                                onDeleted = {
                                    showDeleteDialog = false
                                    recreate()
                                },
                                onDismiss = { showDeleteDialog = false }
                            )
                        }

                        if (showAuth) {
                            com.matchpick.app.ui.screens.auth.AuthSplashScreen(
                                authManager = credentialAuthManager,
                                webClientId = authWebClientId,
                                onAuthSuccess = {
                                    showAuth = false
                                    recreate()
                                },
                                onDismiss = { showAuth = false }
                            )
                        }

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

                        // Seri Kilometre Taşı Kutlama Penceresi
                        activeStreakModal?.let { milestone ->
                            CelebrationDialog(
                                title = stringResource(R.string.celebration_default_title),
                                message = stringResource(R.string.celebration_default_desc, milestone),
                                streakCount = milestone,
                                onClaimReward = { activeStreakModal = null }
                            )
                        }

                        // 2026+ Sinematik Holografik Açılış Ekranı (Cold Boot Intro)
                        AnimatedVisibility(
                            visible = showCinematicSplash,
                            enter = fadeIn(animationSpec = tween(200)),
                            exit = fadeOut(
                                animationSpec = tween(480, easing = FastOutSlowInEasing)
                            ) + scaleOut(
                                targetScale = 1.12f,
                                animationSpec = tween(480, easing = FastOutSlowInEasing)
                            )
                        ) {
                            CinematicSplashIntro(
                                onFinished = {
                                    showCinematicSplash = false
                                }
                            )
                        }
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
