package com.matchpick.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * MatchPick Luxury Sports Design System (Inspired by Apple Sports, FotMob Pro & Ezan Vakti Pro)
 * Focused, disciplined, breathing hierarchy with obsidian glass and athletic pitch emerald.
 */
object MatchPickPalette {
    // Canvas & Surfaces
    /** AMOLED modu: true iken arka planlar saf siyah. Compose state, değişince UI yeniden çizilir. */
    var isAmoled by mutableStateOf(true)

    val DeepObsidian: Color get() = if (isAmoled) Color(0xFF000000) else Color(0xFF070A10)
    val SurfaceDark: Color get() = if (isAmoled) Color(0xFF0A0A0C) else Color(0xFF0E1422)
    val SurfaceElevated: Color get() = if (isAmoled) Color(0xFF111114) else Color(0xFF141C2E)
    val SurfaceGlass: Color get() = if (isAmoled) Color(0xFF16161A) else Color(0xFF192238)
    val SurfaceInteractive: Color get() = if (isAmoled) Color(0xFF1B1B20) else Color(0xFF1E2942)

    // Primary Athletic Accents (Refined, eye-friendly pitch emerald, never radioactive)
    val EmeraldPrimary = Color(0xFF10B981)
    val NeonEmerald = EmeraldPrimary // Backward-compatible alias
    val EmeraldGlow = Color(0xFF34D399)
    val EmeraldDeep = Color(0xFF063321)
    val EmeraldSubtle = Color(0xFF09432B)

    // Supporting Functional Colors (Intentional, never over-saturated)
    val ElectricCyan = Color(0xFF06B6D4)
    val CyanDeep = Color(0xFF082736)

    val NeuralViolet = Color(0xFF6366F1)
    val NeuralDeep = Color(0xFF181836)

    val VipGold = Color(0xFFF59E0B)
    val VipAmber = Color(0xFFD97706)
    val VipDeep = Color(0xFF2B1D06)

    val CrimsonLive = Color(0xFFEF4444)

    // Typography & Neutrals
    val TextPrimary = Color(0xFFF8FAFC)
    val TextSecondary = Color(0xFF94A3B8)
    val TextMuted = Color(0xFF64748B)

    // Micro-Borders (Apple Glass style: light catches the edge at 8-12% opacity)
    val BorderSubtle = Color(0x18FFFFFF)
    val BorderMedium = Color(0x28FFFFFF)
    val BorderEmerald = Color(0x9910B981)

    // Gradients - Subtle and Luxurious
    val HeroCardGradient: Brush get() = Brush.verticalGradient(
        colors = if (isAmoled) listOf(Color(0xFF0A0A0C), Color(0xFF000000)) else listOf(Color(0xFF152238), Color(0xFF0F1727))
    )

    val GlassCardGradient: Brush get() = Brush.verticalGradient(
        colors = if (isAmoled) listOf(Color(0xFF111114), Color(0xFF0A0A0C)) else listOf(Color(0xFF121A2A), Color(0xFF0E1422))
    )

    val NeonBrandGradient = Brush.horizontalGradient(
        colors = listOf(NeonEmerald, ElectricCyan)
    )

    val SelectedOddsGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0E3824),
            Color(0xFF072215)
        )
    )

    val LuxurySelectedGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0E3B26),
            Color(0xFF062215)
        )
    )

    val DialTrackColor: Color get() = if (isAmoled) Color(0xFF16161A) else Color(0xFF131D2E)

    val VipCrownGradient = Brush.linearGradient(
        colors = listOf(VipGold, VipAmber)
    )

    val NeuralAiGradient = Brush.horizontalGradient(
        colors = listOf(NeuralViolet, ElectricCyan)
    )
}

fun matchPickDarkColorScheme() = darkColorScheme(
    primary = MatchPickPalette.NeonEmerald,
    onPrimary = Color(0xFF031A0E),
    primaryContainer = MatchPickPalette.EmeraldDeep,
    onPrimaryContainer = Color(0xFF69F0AE),
    secondary = MatchPickPalette.ElectricCyan,
    onSecondary = Color(0xFF031E2B),
    secondaryContainer = MatchPickPalette.CyanDeep,
    onSecondaryContainer = Color(0xFF67E8F9),
    tertiary = MatchPickPalette.NeuralViolet,
    background = MatchPickPalette.DeepObsidian,
    surface = MatchPickPalette.SurfaceDark,
    surfaceVariant = MatchPickPalette.SurfaceElevated,
    outline = MatchPickPalette.BorderSubtle,
    outlineVariant = Color(0xFF1E293B)
)

val MatchPickTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = (-0.3).sp,
        color = MatchPickPalette.TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = (-0.2).sp,
        color = MatchPickPalette.TextPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        letterSpacing = 0.1.sp,
        color = MatchPickPalette.TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        letterSpacing = 0.1.sp,
        color = MatchPickPalette.TextPrimary
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = MatchPickPalette.TextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        color = MatchPickPalette.TextSecondary
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 0.3.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 0.3.sp,
        color = MatchPickPalette.TextMuted
    )
)
