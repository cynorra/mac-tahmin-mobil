package com.matchpick.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.matchpick.app.R
import com.matchpick.app.core.analytics.AnalyticsTracker
import com.matchpick.app.data.local.FixtureEntity
import com.matchpick.app.ui.screens.feed.CommunityFeedScreen
import com.matchpick.app.ui.screens.feed.FeedViewModel
import com.matchpick.app.ui.screens.leaderboard.LeaderboardScreen
import com.matchpick.app.ui.screens.leaderboard.LeaderboardViewModel
import com.matchpick.app.ui.screens.official.OfficialPickUiModel
import com.matchpick.app.ui.screens.official.OfficialPicksScreen
import com.matchpick.app.ui.screens.official.RewardedAdController
import com.matchpick.app.ui.screens.predict.PredictScreen
import com.matchpick.app.ui.screens.predict.PredictViewModel
import com.matchpick.app.ui.screens.profile.ProfileScreen
import com.matchpick.app.ui.theme.MatchPickPalette

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
    val haptics = LocalHapticFeedback.current

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
    val routeOrder = remember { items.mapIndexed { index, screen -> screen.route to index }.toMap() }

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
            containerColor = MatchPickPalette.DeepObsidian,
            bottomBar = {
                Surface(
                    color = MatchPickPalette.SurfaceDark,
                    border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle),
                    shadowElevation = 8.dp
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp
                    ) {
                        items.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            val iconScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.08f else 1.0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "navIconScale"
                            )

                            NavigationBarItem(
                                icon = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            screen.icon,
                                            contentDescription = null,
                                            modifier = Modifier.scale(iconScale),
                                            tint = if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.TextMuted
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Box(
                                                modifier = Modifier
                                                    .width(12.dp)
                                                    .height(2.dp)
                                                    .clip(RoundedCornerShape(1.dp))
                                                    .background(MatchPickPalette.NeonEmerald)
                                            )
                                        }
                                    }
                                },
                                label = {
                                    Text(
                                        text = stringResource(screen.titleRes),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.TextMuted,
                                        fontSize = 11.sp
                                    )
                                },
                                selected = isSelected,
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                ),
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
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
                modifier = modifier.padding(innerPadding),
                enterTransition = {
                    val fromIdx = routeOrder[initialState.destination.route] ?: 0
                    val toIdx = routeOrder[targetState.destination.route] ?: 0
                    val direction = if (toIdx >= fromIdx) {
                        AnimatedContentTransitionScope.SlideDirection.Left
                    } else {
                        AnimatedContentTransitionScope.SlideDirection.Right
                    }
                    slideIntoContainer(
                        towards = direction,
                        animationSpec = tween(320, easing = FastOutSlowInEasing),
                        initialOffset = { it / 6 }
                    ) + fadeIn(
                        animationSpec = tween(280, easing = FastOutSlowInEasing)
                    ) + scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(320, easing = FastOutSlowInEasing)
                    )
                },
                exitTransition = {
                    val fromIdx = routeOrder[initialState.destination.route] ?: 0
                    val toIdx = routeOrder[targetState.destination.route] ?: 0
                    val direction = if (toIdx >= fromIdx) {
                        AnimatedContentTransitionScope.SlideDirection.Left
                    } else {
                        AnimatedContentTransitionScope.SlideDirection.Right
                    }
                    slideOutOfContainer(
                        towards = direction,
                        animationSpec = tween(260, easing = FastOutSlowInEasing),
                        targetOffset = { it / 7 }
                    ) + fadeOut(
                        animationSpec = tween(220, easing = FastOutSlowInEasing)
                    ) + scaleOut(
                        targetScale = 0.96f,
                        animationSpec = tween(260, easing = FastOutSlowInEasing)
                    )
                }
            ) {
                composable(Screen.Predict.route) {
                    PredictScreen(
                        fixtures = predictState.fixtures,
                        userStreak = predictState.streak,
                        userPoints = predictState.points,
                        onPredictSelected = { id, pick ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            predictViewModel.makePrediction(id, pick)
                        },
                        onShareClicked = onShareFixture
                    )
                }
                composable(Screen.Feed.route) {
                    CommunityFeedScreen(
                        posts = feedState.posts,
                        onSendPost = { text ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            feedViewModel.publishPost(text)
                        },
                        onLikePost = { id ->
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            feedViewModel.toggleLike(id)
                        },
                        onRegionSelected = { region -> feedViewModel.loadFeed(region) },
                        adUnitId = adUnitId
                    )
                }
                composable(Screen.Official.route) {
                    OfficialPicksScreen(
                        picks = officialPicks,
                        rewardedAdController = rewardedAdController,
                        onUnlockPick = { id ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
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
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            analyticsTracker.logReferralAction("clipboard_copy", code)
                        },
                        onShareReferral = { code ->
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
                        onToggleNotifications = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            isNotificationsEnabled = it
                        },
                        currentLanguage = currentLanguage,
                        onChangeLanguage = { lang ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
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
