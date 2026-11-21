package com.matchpick.app.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matchpick.app.R
import com.matchpick.app.core.auth.CredentialAuthManager
import com.matchpick.app.core.auth.SignInResult
import com.matchpick.app.ui.theme.MatchPickPalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 2026+ Cyber-Sports Animated Boot & Holographic Arena Intro Overlay
 */
@Composable
fun CinematicSplashIntro(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splashInfinite")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRot"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    var enterAnimated by remember { mutableStateOf(false) }
    val logoScale by animateFloatAsState(
        targetValue = if (enterAnimated) 1f else 0.65f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoEnterScale"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (enterAnimated) 1f else 0f,
        animationSpec = tween(550, easing = FastOutSlowInEasing),
        label = "contentAlpha"
    )
    val bootProgress by animateFloatAsState(
        targetValue = if (enterAnimated) 1f else 0.08f,
        animationSpec = tween(1450, easing = FastOutSlowInEasing),
        label = "bootProgress"
    )

    LaunchedEffect(Unit) {
        enterAnimated = true
        delay(1650L)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0E2236),
                        Color(0xFF081020),
                        MatchPickPalette.DeepObsidian
                    ),
                    radius = 1100f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Ambient Holographic Orbs Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MatchPickPalette.NeonEmerald.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.2f, size.height * 0.25f),
                    radius = size.minDimension * 0.55f
                ),
                center = Offset(size.width * 0.2f, size.height * 0.25f),
                radius = size.minDimension * 0.55f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MatchPickPalette.NeuralViolet.copy(alpha = 0.16f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.82f, size.height * 0.72f),
                    radius = size.minDimension * 0.6f
                ),
                center = Offset(size.width * 0.82f, size.height * 0.72f),
                radius = size.minDimension * 0.6f
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .scale(logoScale)
                .alpha(contentAlpha)
        ) {
            // 3D Orbiting Holographic Core Emblem
            Box(
                modifier = Modifier.size(136.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Rotating Cyber Ring
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(ringRotation)
                ) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color.Transparent,
                                MatchPickPalette.NeonEmerald,
                                MatchPickPalette.ElectricCyan,
                                MatchPickPalette.NeuralViolet,
                                Color.Transparent
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 290f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Pulsing Neon Halo
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    MatchPickPalette.NeonEmerald.copy(alpha = 0.28f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Inner Core Emblem
                Surface(
                    modifier = Modifier.size(86.dp),
                    shape = RoundedCornerShape(26.dp),
                    color = Color(0xFF0B1528),
                    border = BorderStroke(
                        2.dp,
                        Brush.linearGradient(
                            listOf(MatchPickPalette.NeonEmerald, MatchPickPalette.ElectricCyan)
                        )
                    ),
                    shadowElevation = 18.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0D2E22), Color(0xFF0D1830))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 40.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Title & 2026+ AI Tag
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MATCHPICK",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 3.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF191233),
                    border = BorderStroke(1.dp, MatchPickPalette.NeuralViolet)
                ) {
                    Text(
                        text = "2026+",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD8B4FE)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = MatchPickPalette.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Neural Telemetry Boot Progress Bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(220.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF16223A))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(bootProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MatchPickPalette.NeonEmerald,
                                        MatchPickPalette.ElectricCyan,
                                        MatchPickPalette.NeuralViolet
                                    )
                                )
                            )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.syncing_telemetry_progress, (bootProgress * 100).toInt()),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 1.1.sp,
                    color = MatchPickPalette.NeonEmerald
                )
            }
        }
    }
}

@Composable
fun AuthSplashScreen(
    authManager: CredentialAuthManager,
    webClientId: String,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isRegister by rememberSaveable { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    fun handle(result: SignInResult) {
        isLoading = false
        when (result) {
            is SignInResult.Success -> onAuthSuccess()
            is SignInResult.Failure -> errorMessage = result.error
            is SignInResult.Cancelled -> {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A1428), MatchPickPalette.DeepObsidian)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(MatchPickPalette.NeonBrandGradient),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚡", fontSize = 36.sp)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "MATCHPICK",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 4.sp
            )
            Text(
                text = stringResource(R.string.auth_slogan),
                style = MaterialTheme.typography.bodyLarge,
                color = MatchPickPalette.NeonEmerald,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.auth_email)) },
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.auth_password)) },
                singleLine = true,
                enabled = !isLoading,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(
                            text = stringResource(if (showPassword) R.string.auth_hide else R.string.auth_show),
                            color = MatchPickPalette.NeonEmerald,
                            fontSize = 12.sp
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(color = MatchPickPalette.NeonEmerald)
            } else {
                Button(
                    onClick = {
                        if (email.isBlank() || password.length < 6) {
                            errorMessage = context.getString(R.string.auth_invalid_input)
                            return@Button
                        }
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            handle(authManager.signInWithEmail(email, password, isRegister))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatchPickPalette.NeonEmerald,
                        contentColor = Color(0xFF03170E)
                    )
                ) {
                    Text(
                        text = stringResource(if (isRegister) R.string.auth_register else R.string.auth_sign_in),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }
                TextButton(onClick = { isRegister = !isRegister; errorMessage = null }) {
                    Text(
                        text = stringResource(if (isRegister) R.string.auth_have_account else R.string.auth_no_account),
                        color = MatchPickPalette.NeonEmerald
                    )
                }

                if (webClientId.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = {
                            isLoading = true
                            errorMessage = null
                            coroutineScope.launch {
                                handle(authManager.signInWithGoogle(context, webClientId))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MatchPickPalette.NeonEmerald)
                    ) {
                        Text(
                            text = stringResource(R.string.btn_continue_google),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (onDismiss != null) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(R.string.auth_continue_guest), color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}
