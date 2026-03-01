package com.example.periodtracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DeepPurple,
    onPrimary = White,
    primaryContainer = LightLavender,
    onPrimaryContainer = DeepPurple,
    secondary = MediumPurple,
    onSecondary = White,
    secondaryContainer = Lavender,
    onSecondaryContainer = DeepPurple,
    tertiary = PinkAccent,
    onTertiary = White,
    tertiaryContainer = PinkLight,
    onTertiaryContainer = TextDark,
    background = DeepPurple,
    onBackground = White,
    surface = LightLavender,
    onSurface = TextDark,
    surfaceVariant = LightLavender,
    onSurfaceVariant = TextMedium,
)

@Composable
fun PeriodTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ (overrides our colors - removed)
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}