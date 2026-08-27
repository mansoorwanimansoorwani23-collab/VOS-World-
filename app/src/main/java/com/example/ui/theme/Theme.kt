package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = VosPrimary,
    onPrimary = VosOnPrimary,
    primaryContainer = VosPrimaryContainer,
    onPrimaryContainer = VosOnPrimaryContainer,
    secondary = VosSecondary,
    onSecondary = VosOnSecondary,
    secondaryContainer = VosSecondaryContainer,
    onSecondaryContainer = VosOnSecondaryContainer,
    tertiary = VosTertiary,
    onTertiary = VosOnTertiary,
    tertiaryContainer = VosTertiaryContainer,
    onTertiaryContainer = VosOnTertiaryContainer,
    background = VosBackgroundDark,
    surface = VosSurfaceDark,
    surfaceVariant = VosSurfaceVariantDark,
    onBackground = VosOnSurfaceDark,
    onSurface = VosOnSurfaceDark,
    onSurfaceVariant = VosOnSurfaceVariantDark,
    error = VosError,
    onError = VosOnError
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006877),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA1EFFF),
    onPrimaryContainer = Color(0xFF001F25),
    secondary = Color(0xFF5E35B1),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE7F6),
    onSecondaryContainer = Color(0xFF21005D),
    tertiary = Color(0xFF008947),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB9F6CA),
    onTertiaryContainer = Color(0xFF00210E),
    background = VosBackgroundLight,
    surface = VosSurfaceLight,
    surfaceVariant = VosSurfaceVariantLight,
    onBackground = VosOnSurfaceLight,
    onSurface = VosOnSurfaceLight,
    onSurfaceVariant = VosOnSurfaceVariantLight,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our signature VOS cybernetic palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // VOS World looks best in sleek tech dark mode by default
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
