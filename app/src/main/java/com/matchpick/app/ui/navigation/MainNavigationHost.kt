package com.matchpick.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.matchpick.app.R
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
                    fixtureId = "mock_1",
                    homeTeam = "Inter Miami",
                    homeLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/9779.png",
                    awayTeam = "LA Galaxy",
                    awayLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/6541.png",
                    recommendedPick = "1 (Home Win)",
                    confidenceRate = 78,
                    analysisReason = "Inter Miami is unbeaten in their last 6 home games with high xG and key players in form.",
                    isVipLocked = true
                ),
                OfficialPickUiModel(
                    fixtureId = "mock_2",
                    homeTeam = "Galatasaray",
                    homeLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8637.png",
                    awayTeam = "Fenerbahçe",
                    awayLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8695.png",
                    recommendedPick = "Over 2.5 Goals",
                    confidenceRate = 72,
                    analysisReason = "Derby clash with both teams averaging 2.3 goals scored per match this season.",
                    isVipLocked = true
                ),
                OfficialPickUiModel(
                    fixtureId = "mock_3",
                    homeTeam = "Arsenal",
                    homeLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/9825.png",
                    awayTeam = "Chelsea",
                    awayLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8455.png",
                    recommendedPick = "1X & Under 3.5 Goals",
                    confidenceRate = 68,
                    analysisReason = "Arsenal solid defensive structure at Emirates Stadium with 5 clean sheets in 7 games.",
                    isVipLocked = false
                )
            )
        )
    }

    var isNotificationsEnabled by remember { mutableStateOf(true) }
    var currentLanguage by remember { mutableStateOf("tr") }

    val items = listOf(
        Screen.Predict,
        Screen.Feed,
        Screen.Official,
        Screen.Leaderboard,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.titleRes)) },
                        selected = currentRoute == screen.route,
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
                CommunityFeedScreen(
                    posts = feedState.posts.ifEmpty {
                        listOf(
                            com.matchpick.app.ui.screens.feed.PostUiModel(
                                id = "demo_1",
                                authorName = "Alex",
                                authorPhotoUrl = "",
                                text = "Galatasaray vs Fenerbahçe derbisi için tahmininiz ne? Bence üst biter! 🔥",
                                timestampFormatted = "14:20",
                                likesCount = 12,
                                isLiked = false,
                                attachedPrediction = "Galatasaray vs Fenerbahçe -> Over 2.5"
                            ),
                            com.matchpick.app.ui.screens.feed.PostUiModel(
                                id = "demo_2",
                                authorName = "MessiFan",
                                authorPhotoUrl = "",
                                text = "Inter Miami will dominate tonight at home. 3-0 easy win.",
                                timestampFormatted = "15:45",
                                likesCount = 8,
                                isLiked = true,
                                attachedPrediction = "Inter Miami vs LA Galaxy -> 1"
                            )
                        )
                    },
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
                val demoLeaderboard = listOf(
                    com.matchpick.app.ui.screens.leaderboard.LeaderboardUserUiModel(1, "u1", "Cem Yılmaz", "", 1240, 9, "TR"),
                    com.matchpick.app.ui.screens.leaderboard.LeaderboardUserUiModel(2, "u2", "David B.", "", 1150, 7, "US"),
                    com.matchpick.app.ui.screens.leaderboard.LeaderboardUserUiModel(3, "u3", "Hakan Ş.", "", 980, 6, "TR"),
                    com.matchpick.app.ui.screens.leaderboard.LeaderboardUserUiModel(4, "u4", "Marcus R.", "", 850, 4, "GLOBAL"),
                    com.matchpick.app.ui.screens.leaderboard.LeaderboardUserUiModel(5, "u5", "Eren Şimşir", "", predictState.points, predictState.streak, "TR")
                )

                LeaderboardScreen(
                    topUsers = leaderboardState.topUsers.ifEmpty { demoLeaderboard },
                    currentUser = if (leaderboardState.currentUser.points > 0) leaderboardState.currentUser else demoLeaderboard[4],
                    referralCode = leaderboardState.referralCode.ifEmpty { "MATCH2026" },
                    onShareReferral = {}
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    userName = "Eren Şimşir",
                    userEmail = "user@matchpick.app",
                    avatarUrl = "https://images.fotmob.com/image_resources/logo/teamlogo/9779.png",
                    currentStreak = predictState.streak,
                    bestStreak = maxOf(predictState.streak, 7),
                    totalPoints = predictState.points,
                    isNotificationsEnabled = isNotificationsEnabled,
                    onToggleNotifications = { isNotificationsEnabled = it },
                    currentLanguage = currentLanguage,
                    onChangeLanguage = { currentLanguage = it }
                )
            }
        }
    }
}
