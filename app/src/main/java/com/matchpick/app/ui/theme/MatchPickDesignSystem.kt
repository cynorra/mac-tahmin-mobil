package com.matchpick.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 2026+ MatchPick Cyber-Sports Spatial Glass Design System
 */
object MatchPickPalette {
    val DeepObsidian = Color(0xFF04070E)
    val MidnightVoid = Color(0xFF070B14)
    val SurfaceDark = Color(0xFF0C1222)
    val SurfaceElevated = Color(0xFF131C33)
    val SurfaceGlass = Color(0xFF17233E)

    val NeonEmerald = Color(0xFF00F59B)
    val EmeraldGlow = Color(0xFF00FFAA)
    val EmeraldDeep = Color(0xFF06281C)
    val ElectricCyan = Color(0xFF00E5FF)
    val CyanDeep = Color(0xFF04222C)

    val NeuralViolet = Color(0xFFA855F7)
    val NeuralMagenta = Color(0xFFEC4899)
    val NeuralDeep = Color(0xFF1C1036)

    val VipGold = Color(0xFFFFB800)
    val VipAmber = Color(0xFFFF7A00)
    val VipDeep = Color(0xFF281A05)

    val CrimsonLive = Color(0xFFFF2E54)
    val TextPrimary = Color(0xFFF8FAFC)
    val TextSecondary = Color(0xFF94A3B8)
    val TextMuted = Color(0xFF64748B)
    val BorderSubtle = Color(0x2864748B)

    val HeroCardGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0D232A),
            Color(0xFF111830),
            Color(0xFF191233)
        )
    )

    val GlassCardGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF141E36),
            Color(0xFF0A0F1D)
        )
    )

    val NeonBrandGradient = Brush.linearGradient(
        colors = listOf(NeonEmerald, ElectricCyan)
    )

    val VipCrownGradient = Brush.linearGradient(
        colors = listOf(VipGold, VipAmber)
    )

    val NeuralAiGradient = Brush.linearGradient(
        colors = listOf(NeuralViolet, ElectricCyan)
    )
}

val MatchPickDarkColorScheme = darkColorScheme(
    primary = MatchPickPalette.NeonEmerald,
    onPrimary = Color(0xFF02180E),
    primaryContainer = MatchPickPalette.EmeraldDeep,
    onPrimaryContainer = Color(0xFF6BFFB8),
    secondary = MatchPickPalette.ElectricCyan,
    onSecondary = Color(0xFF001F26),
    secondaryContainer = MatchPickPalette.CyanDeep,
    onSecondaryContainer = Color(0xFF6BF0FF),
    tertiary = MatchPickPalette.NeuralViolet,
    background = MatchPickPalette.DeepObsidian,
    surface = MatchPickPalette.SurfaceDark,
    surfaceVariant = MatchPickPalette.SurfaceElevated,
    outline = MatchPickPalette.TextMuted,
    outlineVariant = Color(0xFF22304A)
)

val MatchPickTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        letterSpacing = 0.2.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        letterSpacing = 0.3.sp
    )
)
