package com.matchpick.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.matchpick.app.R
import com.matchpick.app.core.analytics.AnalyticsTracker
import com.matchpick.app.data.local.FixtureEntity
import com.matchpick.app.ui.screens.feed.CommunityFeedScreen
import com.matchpick.app.ui.screens.feed.FeedViewModel
import com.matchpick.app.ui.screens.feed.PostUiModel
import com.matchpick.app.ui.screens.leaderboard.LeaderboardScreen
import com.matchpick.app.ui.screens.leaderboard.LeaderboardUserUiModel
import com.matchpick.app.ui.screens.leaderboard.LeaderboardViewModel
import com.matchpick.app.ui.screens.official.OfficialPickUiModel
import com.matchpick.app.ui.screens.official.OfficialPicksScreen
import com.matchpick.app.ui.screens.official.RewardedAdController
import com.matchpick.app.ui.screens.predict.PredictScreen
import com.matchpick.app.ui.screens.predict.PredictViewModel
import com.matchpick.app.ui.screens.profile.ProfileScreen

sealed class Screen(val route: String, val titleRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Predict : Screen("predict", R.string.nav_predict, Icons.Default.SportsSoccer)
    data object Feed : Screen("feed", R.string.nav_feed, Icons.Default.Forum)
    data object Official : Screen("official", R.string.nav_official, Icons.Default.Verified)
    data object Leaderboard : Screen("leaderboard", R.string.nav_leaderboard, Icons.Default.Leaderboard)
    data object Profile : Screen("profile", R.string.nav_profile, Icons.Default.Person)
}

@Composable
fun MainNavigationHost(
    adUnitId: String,
    predictViewModel: PredictViewModel,
    feedViewModel: FeedViewModel,
    leaderboardViewModel: LeaderboardViewModel,
    rewardedAdController: RewardedAdController,
    analyticsTracker: AnalyticsTracker,
    onShareFixture: (FixtureEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val predictState by predictViewModel.uiState.collectAsState()
    val feedState by feedViewModel.uiState.collectAsState()
    val leaderboardState by leaderboardViewModel.uiState.collectAsState()

    var officialPicks by remember {
        mutableStateOf(
            listOf(
                OfficialPickUiModel(
                    fixtureId = "laliga_rm_barca",
                    leagueName = "La Liga EA Sports",
                    homeTeam = "Real Madrid",
                    homeLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png",
                    awayTeam = "FC Barcelona",
                    awayLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8634.png",
                    recommendedPick = "1X & Over 2.5",
                    confidenceRate = 84,
                    analysisReason = "El Clásico showdown. Real Madrid is unbeaten in their last 18 home matches with high conversion in the final 20 minutes.",
                    isVipLocked = true
                ),
                OfficialPickUiModel(
                    fixtureId = "epl_ars_mci",
                    leagueName = "Premier League",
                    homeTeam = "Arsenal",
                    homeLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/9825.png",
                    awayTeam = "Manchester City",
                    awayLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8456.png",
                    recommendedPick = "Over 2.5 Goals",
                    confidenceRate = 79,
                    analysisReason = "Title clash showdown. Both teams have scored in 8 of their last 9 encounters with 3.2 xG combined.",
                    isVipLocked = true
                ),
                OfficialPickUiModel(
                    fixtureId = "superlig_gs_fb",
                    leagueName = "Trendyol Süper Lig",
                    homeTeam = "Galatasaray",
                    homeLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8637.png",
                    awayTeam = "Fenerbahçe",
                    awayLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8695.png",
                    recommendedPick = "1 (Home Win)",
                    confidenceRate = 74,
                    analysisReason = "Intercontinental Derby. Galatasaray holds 12 consecutive home league wins and an average of 2.4 goals per match at RAMS Park.",
                    isVipLocked = true
                ),
                OfficialPickUiModel(
                    fixtureId = "epl_liv_che",
                    leagueName = "Premier League",
                    homeTeam = "Liverpool",
                    homeLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8650.png",
                    awayTeam = "Chelsea",
                    awayLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8455.png",
                    recommendedPick = "Both Teams To Score",
                    confidenceRate = 78,
                    analysisReason = "High-octane clash at Anfield. Both defenses have conceded in 4 consecutive fixtures with explosive wide transitions.",
                    isVipLocked = false
                )
            )
        )
    }

    var isNotificationsEnabled by remember { mutableStateOf(true) }
    val initialLang = try {
        val tags = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales().toLanguageTags()
        if (tags.startsWith("en", ignoreCase = true)) "en" else "tr"
    } catch (_: Exception) { "tr" }
    var currentLanguage by remember { mutableStateOf(initialLang) }

    val items = listOf(
        Screen.Predict,
        Screen.Feed,
        Screen.Official,
        Screen.Leaderboard,
        Screen.Profile
    )
    val context = androidx.compose.ui.platform.LocalContext.current
    val locale = java.util.Locale(currentLanguage)
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val localizedConfig = remember(currentLanguage, configuration) {
        android.content.res.Configuration(configuration).apply {
            setLocale(locale)
        }
    }
    val localizedContext = remember(currentLanguage, context) {
        context.createConfigurationContext(localizedConfig)
    }

    CompositionLocalProvider(
        androidx.compose.ui.platform.LocalConfiguration provides localizedConfig,
        androidx.compose.ui.platform.LocalContext provides localizedContext
    ) {
        Scaffold(
        containerColor = Color(0xFF04070E),
        bottomBar = {
            androidx.compose.material3.Surface(
                color = Color(0xFF080D1A),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(
                            Color(0x3300F59B),
                            Color(0x2200E5FF),
                            Color(0x33A855F7)
                        )
                    )
                ),
                shadowElevation = 16.dp
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    items.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    screen.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFF00F59B) else Color(0xFF64748B)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(screen.titleRes),
                                    fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Black else androidx.compose.ui.text.font.FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF00F59B) else Color(0xFF94A3B8)
                                )
                            },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFF0A2E20)
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Predict.route,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Screen.Predict.route) {
                PredictScreen(
                    fixtures = predictState.fixtures,
                    userStreak = predictState.streak,
                    userPoints = predictState.points,
                    onPredictSelected = { id, pick ->
                        predictViewModel.makePrediction(id, pick)
                    },
                    onShareClicked = onShareFixture
                )
            }
            composable(Screen.Feed.route) {
                CommunityFeedScreen(
                    posts = feedState.posts,
                    onSendPost = { text -> feedViewModel.publishPost(text) },
                    onLikePost = { id -> feedViewModel.toggleLike(id) },
                    onRegionSelected = { region -> feedViewModel.loadFeed(region) },
                    adUnitId = adUnitId
                )
            }
            composable(Screen.Official.route) {
                OfficialPicksScreen(
                    picks = officialPicks,
                    rewardedAdController = rewardedAdController,
                    onUnlockPick = { id ->
                        val unlockedPick = officialPicks.find { it.fixtureId == id }
                        analyticsTracker.logVipPickUnlocked(
                            fixtureId = id,
                            recommendedPick = unlockedPick?.recommendedPick ?: "",
                            confidence = unlockedPick?.confidenceRate ?: 80
                        )
                        officialPicks = officialPicks.map {
                            if (it.fixtureId == id) it.copy(isVipLocked = false) else it
                        }
                    }
                )
            }
            composable(Screen.Leaderboard.route) {
                LeaderboardScreen(
                    topUsers = leaderboardState.topUsers,
                    currentUser = leaderboardState.currentUser,
                    referralCode = leaderboardState.referralCode,
                    onCopyReferral = { code ->
                        analyticsTracker.logReferralAction("clipboard_copy", code)
                    },
                    onShareReferral = { code ->
                        analyticsTracker.logReferralAction("native_share", code)
                        try {
                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, localizedContext.getString(R.string.share_referral_text, code))
                                type = "text/plain"
                            }
                            val shareIntent = android.content.Intent.createChooser(sendIntent, null).apply {
                                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            localizedContext.startActivity(shareIntent)
                        } catch (_: Exception) {}
                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    userName = "Eren Şimşir",
                    userEmail = "eren@matchpick.app",
                    avatarUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png",
                    currentStreak = maxOf(predictState.streak, 3),
                    bestStreak = maxOf(predictState.streak, 8),
                    totalPoints = if (predictState.points > 0) predictState.points else 140,
                    isNotificationsEnabled = isNotificationsEnabled,
                    onToggleNotifications = { isNotificationsEnabled = it },
                    currentLanguage = currentLanguage,
                    onChangeLanguage = { lang ->
                        currentLanguage = lang
                        analyticsTracker.logLanguageSwitched(lang)
                        try {
                            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                                androidx.core.os.LocaleListCompat.forLanguageTags(lang)
                            )
                        } catch (_: Exception) {}
                    }
                )
            }
        }
    }
}
}
