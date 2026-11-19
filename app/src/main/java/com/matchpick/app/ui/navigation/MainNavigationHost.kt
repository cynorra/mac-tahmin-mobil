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
        containerColor = Color(0xFF070B14),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF090D18),
                tonalElevation = 8.dp
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
                                color = if (isSelected) Color(0xFF00F59B) else Color(0xFF64748B)
                            )
                        },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF0B291D)
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
                val realFeedPosts = listOf(
                    PostUiModel(
                        id = "feed_1",
                        authorName = "Burak K.",
                        authorPhotoUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8637.png",
                        text = "Galatasaray - Fenerbahçe derbisi için tahminleriniz nedir? RAMS Park'ta Osimhen çok formda, KG Var & 2.5 Üst banko bence! 🔥",
                        timestampFormatted = "14:20",
                        likesCount = 28,
                        isLiked = false,
                        attachedPrediction = "Galatasaray vs Fenerbahçe -> 1"
                    ),
                    PostUiModel(
                        id = "feed_2",
                        authorName = "Liam Tactical",
                        authorPhotoUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/9825.png",
                        text = "Arsenal vs Man City title race battle. Rice and Rodri midfield duel will decide the whole match tempo.",
                        timestampFormatted = "15:45",
                        likesCount = 19,
                        isLiked = true,
                        attachedPrediction = "Arsenal vs Manchester City -> X"
                    ),
                    PostUiModel(
                        id = "feed_3",
                        authorName = "Carlos Madrid",
                        authorPhotoUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png",
                        text = "El Clásico at Bernabéu! Vini Jr and Bellingham always deliver in big matches. Expecting early goals.",
                        timestampFormatted = "16:10",
                        likesCount = 42,
                        isLiked = false,
                        attachedPrediction = "Real Madrid vs FC Barcelona -> 1X"
                    )
                )

                CommunityFeedScreen(
                    posts = feedState.posts.ifEmpty { realFeedPosts },
                    onSendPost = { text -> feedViewModel.publishPost(text) },
                    onLikePost = { id -> feedViewModel.toggleLike(id) },
                    adUnitId = adUnitId
                )
            }
            composable(Screen.Official.route) {
                OfficialPicksScreen(
                    picks = officialPicks,
                    rewardedAdController = rewardedAdController,
                    onUnlockPick = { id ->
                        officialPicks = officialPicks.map {
                            if (it.fixtureId == id) it.copy(isVipLocked = false) else it
                        }
                    }
                )
            }
            composable(Screen.Leaderboard.route) {
                val realLeaderboard = listOf(
                    LeaderboardUserUiModel(1, "u1", "Cem Yılmaz", "https://images.fotmob.com/image_resources/logo/teamlogo/8637.png", 1840, 11, "TR"),
                    LeaderboardUserUiModel(2, "u2", "David Beckham", "https://images.fotmob.com/image_resources/logo/teamlogo/9825.png", 1650, 9, "GB"),
                    LeaderboardUserUiModel(3, "u3", "Hakan Şükür", "https://images.fotmob.com/image_resources/logo/teamlogo/8695.png", 1480, 8, "TR"),
                    LeaderboardUserUiModel(4, "u4", "Marcus Rashford", "https://images.fotmob.com/image_resources/logo/teamlogo/8456.png", 1250, 6, "GLOBAL"),
                    LeaderboardUserUiModel(5, "u5", "Eren Şimşir", "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png", if (predictState.points > 0) predictState.points else 140, maxOf(predictState.streak, 3), "TR")
                )

                LeaderboardScreen(
                    topUsers = leaderboardState.topUsers.ifEmpty { realLeaderboard },
                    currentUser = if (leaderboardState.currentUser.points > 0) leaderboardState.currentUser else realLeaderboard[4],
                    referralCode = leaderboardState.referralCode.ifEmpty { "MATCH2026" },
                    onShareReferral = {}
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
