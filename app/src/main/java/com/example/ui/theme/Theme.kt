package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK, SEPIA
}

private val DarkColorScheme = darkColorScheme(
    primary = GoldDark,
    onPrimary = Color(0xFF1E1402),
    primaryContainer = Color(0xFF3E2805),
    onPrimaryContainer = SoftGold,
    secondary = LotusTealLight,
    onSecondary = Color(0xFF042F2C),
    secondaryContainer = Color(0xFF0B4642),
    onSecondaryContainer = Color(0xFFCCFBF1),
    background = MidnightBg,
    onBackground = TextPrimaryDark,
    surface = MidnightSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = MidnightCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
    primary = SaffronPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEEBC8),
    onPrimaryContainer = SaffronDark,
    secondary = LotusTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = LotusTealDark,
    background = SandalwoodBg,
    onBackground = TextPrimaryLight,
    surface = SandalwoodSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = SandalwoodCard,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFE2E8F0)
)

private val SepiaColorScheme = lightColorScheme(
    primary = SepiaPrimary,
    onPrimary = Color(0xFFFFF7ED),
    primaryContainer = Color(0xFFFFEDD5),
    onPrimaryContainer = Color(0xFF7C2D12),
    secondary = Color(0xFF9A551E),
    onSecondary = Color(0xFFFFFBEB),
    secondaryContainer = Color(0xFFFDE68A),
    onSecondaryContainer = Color(0xFF78350F),
    background = SepiaBg,
    onBackground = TextPrimarySepia,
    surface = SepiaSurface,
    onSurface = TextPrimarySepia,
    surfaceVariant = SepiaCard,
    onSurfaceVariant = TextSecondarySepia,
    outline = Color(0xFFD6C7B2)
)

@Composable
fun GitaBanglaTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> systemInDark
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SEPIA -> false
    }

    val colorScheme = when (themeMode) {
        AppThemeMode.SEPIA -> SepiaColorScheme
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.SYSTEM -> if (systemInDark) DarkColorScheme else LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !isDark && themeMode != AppThemeMode.DARK
                controller.isAppearanceLightNavigationBars = !isDark && themeMode != AppThemeMode.DARK
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
