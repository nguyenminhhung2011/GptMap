package com.espressodev.gptmap.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import javax.annotation.concurrent.Immutable

val md_theme_light_primary = Color(0xFF006B56)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFF4CFDD1)
val md_theme_light_onPrimaryContainer = Color(0xFF002018)
val md_theme_light_secondary = Color(0xFF00639C)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFCEE5FF)
val md_theme_light_onSecondaryContainer = Color(0xFF001D33)
val md_theme_light_tertiary = Color(0xFF00658B)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFC5E7FF)
val md_theme_light_onTertiaryContainer = Color(0xFF001E2D)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFF8FDFF)
val md_theme_light_onBackground = Color(0xFF001F25)
val md_theme_light_surface = Color(0xFFF8FDFF)
val md_theme_light_onSurface = Color(0xFF001F25)
val md_theme_light_surfaceVariant = Color(0xFFf9fcf4)
val md_theme_light_onSurfaceVariant = Color(0xFF3F4945)
val md_theme_light_outline = Color(0xFF6F7975)
val md_theme_light_inverseOnSurface = Color(0xFFD6F6FF)
val md_theme_light_inverseSurface = Color(0xFF00363F)
val md_theme_light_inversePrimary = Color(0xFF10E0B6)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFF006B56)
val md_theme_light_outlineVariant = Color(0xFFBFC9C3)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFF10E0B6)
val md_theme_dark_onPrimary = Color(0xFF00382B)
val md_theme_dark_primaryContainer = Color(0xFF005140)
val md_theme_dark_onPrimaryContainer = Color(0xFF4CFDD1)
val md_theme_dark_secondary = Color(0xFF98CBFF)
val md_theme_dark_onSecondary = Color(0xFF003354)
val md_theme_dark_secondaryContainer = Color(0xFF004A77)
val md_theme_dark_onSecondaryContainer = Color(0xFFCEE5FF)
val md_theme_dark_tertiary = Color(0xFF7ED0FF)
val md_theme_dark_onTertiary = Color(0xFF00344A)
val md_theme_dark_tertiaryContainer = Color(0xFF004C6A)
val md_theme_dark_onTertiaryContainer = Color(0xFFC5E7FF)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF001F25)
val md_theme_dark_onBackground = Color(0xFFA6EEFF)
val md_theme_dark_surface = Color(0xFF001F25)
val md_theme_dark_onSurface = Color(0xFFA6EEFF)
val md_theme_dark_surfaceVariant = Color(0xFF181a17)
val md_theme_dark_onSurfaceVariant = Color(0xFFBFC9C3)
val md_theme_dark_outline = Color(0xFF89938E)
val md_theme_dark_inverseOnSurface = Color(0xFF001F25)
val md_theme_dark_inverseSurface = Color(0xFFA6EEFF)
val md_theme_dark_inversePrimary = Color(0xFF006B56)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFF10E0B6)
val md_theme_dark_outlineVariant = Color(0xFF3F4945)
val md_theme_dark_scrim = Color(0xFF000000)

val seed = Color(0xFF00DDB3)
val lightBottomColor = Color(0xFFf9fcf4)
val darkBottomBarColor = Color(0xFF181a17)


val LightGmColorsPalette = GmColorPalette(
    bottomBarColor = lightBottomColor
)

val DarkGmColorsPalette = GmColorPalette(
    bottomBarColor = darkBottomBarColor
)

val LocalGmColorsPalette = staticCompositionLocalOf { GmColorPalette() }

@Immutable
data class GmColorPalette(
    val bottomBarColor: Color = lightBottomColor
)

val gmColorsPalette: GmColorPalette
    @Composable
    get() = LocalGmColorsPalette.current
