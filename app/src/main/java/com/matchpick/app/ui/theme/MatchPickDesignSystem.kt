package com.matchpick.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * MatchPick 2027+ Spatial Sports Design System
 * Inspired by Apple Sports (visionOS Spatial Glass), Linear, Raycast & FotMob Pro.
 *
 * Key Principles:
 * 1. Default "Spatial Slate" theme (isAmoled = false) provides soothing, deep midnight sapphire
 *    elevation layers (#080D1A -> #10192D -> #17233F) that prevent eye strain and flat suffocation.
 * 2. Even in OLED mode (isAmoled = true), cards use rich elevated charcoal-slate (#0E131F -> #151D2E)
 *    with luminous specular top-rim lighting so surfaces float with tangible depth.
 * 3. Crisp mint-emerald (#10E898), sky-cyan (#22D3EE), and warm champagne gold (#FBBF24) accents.
 */
object MatchPickPalette {
    /** Default false: Luminous Spatial Midnight Slate (soothing, layered, atmospheric). */
    var isAmoled by mutableStateOf(false)

    // Canvas & Layered Spatial Surfaces
    val DeepObsidian: Color get() = if (isAmoled) Color(0xFF030509) else Color(0xFF080E1C)
    val SurfaceDark: Color get() = if (isAmoled) Color(0xFF0D121E) else Color(0xFF10192E)
    val SurfaceElevated: Color get() = if (isAmoled) Color(0xFF151C2E) else Color(0xFF17233E)
    val SurfaceGlass: Color get() = if (isAmoled) Color(0xFF1B243A) else Color(0xFF1E2D4F)
    val SurfaceInteractive: Color get() = if (isAmoled) Color(0xFF212C46) else Color(0xFF24365E)

    // Primary Athletic Accents (Luminous Mint-Emerald, soothing & vibrant)
    val EmeraldPrimary = Color(0xFF12DBA0)
    val NeonEmerald = EmeraldPrimary
    val EmeraldGlow = Color(0xFF4ADE80)
    val EmeraldDeep = Color(0xFF093529)
    val EmeraldSubtle = Color(0xFF0D4736)

    // Supporting Functional Accents
    val ElectricCyan = Color(0xFF22D3EE)
    val CyanDeep = Color(0xFF0A2E42)

    val NeuralViolet = Color(0xFF818CF8)
    val NeuralDeep = Color(0xFF1D1E42)

    val VipGold = Color(0xFFFBBF24)
    val VipAmber = Color(0xFFF59E0B)
    val VipDeep = Color(0xFF332208)

    val CrimsonLive = Color(0xFFF43F5E)

    // Typography & Neutrals (High readability, warm-cool balanced)
    val TextPrimary = Color(0xFFF8FAFC)
    val TextSecondary = Color(0xFFCBD5E1)
    val TextMuted = Color(0xFF7E92B2)

    // Specular Micro-Borders (visionOS top-lit glass edges)
    val BorderSubtle = Color(0x2494A3B8)
    val BorderMedium = Color(0x3D94A3B8)
    val BorderSpecularTop = Color(0x48FFFFFF)
    val BorderEmerald = Color(0xAA12DBA0)

    // Multi-Stop Luminous Gradients
    val HeroCardGradient: Brush get() = Brush.verticalGradient(
        colors = if (isAmoled) {
            listOf(Color(0xFF132238), Color(0xFF0D1524), Color(0xFF090E18))
        } else {
            listOf(Color(0xFF1A2C4E), Color(0xFF121E38), Color(0xFF0E172C))
        }
    )

    val GlassCardGradient: Brush get() = Brush.verticalGradient(
        colors = if (isAmoled) {
            listOf(Color(0xFF141C2E), Color(0xFF0D121F))
        } else {
            listOf(Color(0xFF182542), Color(0xFF10192E))
        }
    )

    val GlassCardBorderBrush: Brush get() = Brush.verticalGradient(
        colors = listOf(
            Color(0x38FFFFFF),
            Color(0x1694A3B8),
            Color(0x0A12DBA0)
        )
    )

    val HeroBorderBrush: Brush get() = Brush.verticalGradient(
        colors = listOf(
            EmeraldPrimary.copy(alpha = 0.65f),
            ElectricCyan.copy(alpha = 0.35f),
            Color(0x18FFFFFF)
        )
    )

    val NeonBrandGradient = Brush.horizontalGradient(
        colors = listOf(NeonEmerald, ElectricCyan)
    )

    val SelectedOddsGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF114B3A),
            Color(0xFF0A2E23)
        )
    )

    val LuxurySelectedGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF13523F),
            Color(0xFF0B3126)
        )
    )

    val UnselectedOddsGradient: Brush get() = Brush.verticalGradient(
        colors = if (isAmoled) {
            listOf(Color(0xFF192236), Color(0xFF121928))
        } else {
            listOf(Color(0xFF1E2C4A), Color(0xFF152038))
        }
    )

    val DialTrackColor: Color get() = if (isAmoled) Color(0xFF182236) else Color(0xFF1E2D4A)

    val VipCrownGradient = Brush.linearGradient(
        colors = listOf(VipGold, VipAmber)
    )

    val NeuralAiGradient = Brush.horizontalGradient(
        colors = listOf(NeuralViolet, ElectricCyan)
    )
}

fun matchPickDarkColorScheme() = darkColorScheme(
    primary = MatchPickPalette.NeonEmerald,
    onPrimary = Color(0xFF031A10),
    primaryContainer = MatchPickPalette.EmeraldDeep,
    onPrimaryContainer = Color(0xFF6EE7B7),
    secondary = MatchPickPalette.ElectricCyan,
    onSecondary = Color(0xFF042230),
    secondaryContainer = MatchPickPalette.CyanDeep,
    onSecondaryContainer = Color(0xFF67E8F9),
    tertiary = MatchPickPalette.NeuralViolet,
    background = MatchPickPalette.DeepObsidian,
    surface = MatchPickPalette.SurfaceDark,
    surfaceVariant = MatchPickPalette.SurfaceElevated,
    outline = MatchPickPalette.BorderSubtle,
    outlineVariant = Color(0xFF23324D)
)

val MatchPickTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 25.sp,
        letterSpacing = (-0.4).sp,
        color = MatchPickPalette.TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        letterSpacing = (-0.3).sp,
        color = MatchPickPalette.TextPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = (-0.1).sp,
        color = MatchPickPalette.TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.5.sp,
        letterSpacing = 0.sp,
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
        fontSize = 13.5.sp,
        lineHeight = 20.sp,
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
        letterSpacing = 0.25.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 10.5.sp,
        letterSpacing = 0.3.sp,
        color = MatchPickPalette.TextMuted
    )
)
