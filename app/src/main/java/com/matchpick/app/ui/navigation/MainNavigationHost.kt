package com.matchpick.app.ui.navigation

import android.app.Activity
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.google.firebase.auth.FirebaseAuth
import com.matchpick.app.R
import com.matchpick.app.core.analytics.AnalyticsTracker
import com.matchpick.app.data.local.FixtureEntity
import com.matchpick.app.ui.components.LocalSonner
import com.matchpick.app.ui.components.SonnerHost
import com.matchpick.app.ui.components.rememberSonnerState
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
    activity: Activity,
    adUnitId: String,
    predictViewModel: PredictViewModel,
    feedViewModel: FeedViewModel,
    leaderboardViewModel: LeaderboardViewModel,
    rewardedAdController: RewardedAdController,
    analyticsTracker: AnalyticsTracker,
    onShareFixture: (FixtureEntity) -> Unit,
    onRequestSignIn: () -> Unit = {},
    onLogout: () -> Unit = {},
    onRequestDelete: () -> Unit = {},
    showPrivacyOptions: Boolean = false,
    onPrivacyOptions: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val haptics = LocalHapticFeedback.current

    val predictState by predictViewModel.uiState.collectAsState()
    val feedState by feedViewModel.uiState.collectAsState()
    val leaderboardState by leaderboardViewModel.uiState.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE) }
    val initialUnlocked = remember { prefs.getStringSet("unlocked_vip_picks", emptySet())?.toSet() ?: emptySet() }
    var unlockedPickIds by remember { mutableStateOf(initialUnlocked) }

    val officialPicks = remember(predictState.fixtures, unlockedPickIds) {
        predictState.fixtures
            .filter { it.officialPick != null }
            .mapIndexed { index, fixture ->
                val pickLabel = when (fixture.officialPick) {
                    "1" -> "${fixture.homeTeamName} (1)"
                    "X" -> "Draw (X)"
                    "2" -> "${fixture.awayTeamName} (2)"
                    else -> fixture.officialPick ?: "Home Win"
                }
                // All VIP picks require watching a rewarded ad to unlock
                val isLocked = !unlockedPickIds.contains(fixture.id)
                OfficialPickUiModel(
                    fixtureId = fixture.id,
                    leagueName = fixture.leagueName,
                    homeTeam = fixture.homeTeamName,
                    homeLogo = fixture.homeTeamLogo,
                    awayTeam = fixture.awayTeamName,
                    awayLogo = fixture.awayTeamLogo,
                    recommendedPick = pickLabel,
                    confidenceRate = fixture.officialConfidence.coerceIn(60, 95),
                    analysisReason = if (fixture.aiAnalysis.isNotBlank()) fixture.aiAnalysis else "${fixture.leagueName} high-confidence tactical pick.",
                    isVipLocked = isLocked
                )
            }
    }

    var isNotificationsEnabled by remember { mutableStateOf(true) }
    var currentLanguage by remember {
        mutableStateOf(
            prefs.getString("pref_language", null)
                ?: if (java.util.Locale.getDefault().language.startsWith("en", ignoreCase = true)) "en" else "tr"
        )
    }

    val items = listOf(
        Screen.Predict,
        Screen.Feed,
        Screen.Official,
        Screen.Leaderboard,
        Screen.Profile
    )
    val routeOrder = remember { items.mapIndexed { index, screen -> screen.route to index }.toMap() }

    val locale = remember(currentLanguage) { java.util.Locale(currentLanguage) }
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val localizedConfig = remember(currentLanguage, configuration) {
        android.content.res.Configuration(configuration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
    }
    val localizedContext = remember(currentLanguage, context) {
        context.createConfigurationContext(localizedConfig)
    }

    val sonnerState = rememberSonnerState()

    CompositionLocalProvider(
        androidx.compose.ui.platform.LocalConfiguration provides localizedConfig,
        androidx.compose.ui.platform.LocalContext provides localizedContext,
        LocalSonner provides sonnerState
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = MatchPickPalette.DeepObsidian,
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    color = Color(0xFA0B111D),
                    border = BorderStroke(0.5.dp, MatchPickPalette.BorderSubtle),
                    shadowElevation = 16.dp
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(64.dp)
                    ) {
                        items.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            val iconScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.12f else 1.0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "navIconScale"
                            )

                            NavigationBarItem(
                                icon = {
                                    Box(
                                        modifier = if (isSelected) {
                                            Modifier
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(MatchPickPalette.EmeraldDeep)
                                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                        } else {
                                            Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                        },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            screen.icon,
                                            contentDescription = null,
                                            modifier = Modifier.scale(iconScale),
                                            tint = if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.TextMuted
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = stringResource(screen.titleRes),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MatchPickPalette.NeonEmerald else MatchPickPalette.TextMuted,
                                        fontSize = 10.sp
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
                    val sonner = LocalSonner.current
                    PredictScreen(
                        fixtures = predictState.fixtures,
                        userStreak = predictState.streak,
                        userPoints = predictState.points,
                        onPredictSelected = { id, pick ->
                            val outcome = predictViewModel.makePrediction(id, pick)
                            when (outcome) {
                                com.matchpick.app.ui.screens.predict.PredictionOutcome.AWARDED -> {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    sonner.show(
                                        message = localizedContext.getString(R.string.prediction_saved_title),
                                        description = localizedContext.getString(R.string.prediction_saved_desc),
                                        type = com.matchpick.app.ui.components.SonnerType.SUCCESS,
                                        durationMs = 2400L
                                    )
                                }
                                com.matchpick.app.ui.screens.predict.PredictionOutcome.UPDATED -> {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    sonner.show(
                                        message = localizedContext.getString(R.string.prediction_updated_title),
                                        description = localizedContext.getString(R.string.prediction_updated_desc),
                                        type = com.matchpick.app.ui.components.SonnerType.INFO,
                                        durationMs = 2200L
                                    )
                                }
                                com.matchpick.app.ui.screens.predict.PredictionOutcome.LOCKED -> {
                                    sonner.show(
                                        message = localizedContext.getString(R.string.match_locked),
                                        description = localizedContext.getString(R.string.match_locked_notice),
                                        type = com.matchpick.app.ui.components.SonnerType.ERROR,
                                        durationMs = 2500L
                                    )
                                }
                                com.matchpick.app.ui.screens.predict.PredictionOutcome.ALREADY_SELECTED -> Unit
                            }
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
                        activity = activity,
                        onUnlockPick = { id ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            val unlockedPick = officialPicks.find { it.fixtureId == id }
                            analyticsTracker.logVipPickUnlocked(
                                fixtureId = id,
                                recommendedPick = unlockedPick?.recommendedPick ?: "",
                                confidence = unlockedPick?.confidenceRate ?: 80
                            )
                            val newSet = unlockedPickIds + id
                            unlockedPickIds = newSet
                            prefs.edit().putStringSet("unlocked_vip_picks", newSet).apply()
                        }
                    )
                }
                composable(Screen.Leaderboard.route) {
                    val syncedCurrentUser = leaderboardState.currentUser.copy(
                        points = maxOf(leaderboardState.currentUser.points, predictState.points),
                        streak = maxOf(leaderboardState.currentUser.streak, predictState.streak)
                    )
                    LeaderboardScreen(
                        topUsers = leaderboardState.topUsers,
                        currentUser = syncedCurrentUser,
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
                    val firebaseUser = remember { FirebaseAuth.getInstance().currentUser }
                    val resolvedName = firebaseUser?.displayName?.takeIf { it.isNotBlank() }
                        ?: firebaseUser?.email?.substringBefore("@")?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.guest_analyst_name)
                    val resolvedEmail = firebaseUser?.email?.takeIf { it.isNotBlank() }
                        ?: if (firebaseUser?.isAnonymous == true) {
                            stringResource(R.string.guest_session_label)
                        } else "analyst@matchpick.app"
                    val resolvedAvatar = firebaseUser?.photoUrl?.toString().orEmpty()

                    val pickedList = remember(predictState.fixtures) {
                        predictState.fixtures.filter { !it.userPick.isNullOrEmpty() }
                    }

                    ProfileScreen(
                        userName = resolvedName,
                        userEmail = resolvedEmail,
                        avatarUrl = resolvedAvatar,
                        currentStreak = predictState.streak,
                        bestStreak = maxOf(predictState.streak, leaderboardState.currentUser.streak),
                        totalPoints = predictState.points,
                        pickedFixtures = pickedList,
                        isNotificationsEnabled = isNotificationsEnabled,
                        onToggleNotifications = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            isNotificationsEnabled = it
                        },
                        currentLanguage = currentLanguage,
                        onChangeLanguage = { lang ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            currentLanguage = lang
                            prefs.edit().putString("pref_language", lang).apply()
                            analyticsTracker.logLanguageSwitched(lang)
                        },
                        isAmoledTheme = MatchPickPalette.isAmoled,
                        isGuest = firebaseUser == null || firebaseUser.isAnonymous,
                        onSignInClick = onRequestSignIn,
                        onLogoutClick = onLogout,
                        onDeleteAccountClick = onRequestDelete,
                        showPrivacyOptions = showPrivacyOptions,
                        onPrivacyOptionsClick = onPrivacyOptions,
                        onToggleAmoled = { enabled ->
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            MatchPickPalette.isAmoled = enabled
                            prefs.edit().putBoolean("pref_amoled", enabled).apply()
                        }
                    )
                }
            }
        }

        // Sonner Polish Toast Container
        SonnerHost(
            state = sonnerState,
            alignment = Alignment.TopCenter
        )
    }
}
}
