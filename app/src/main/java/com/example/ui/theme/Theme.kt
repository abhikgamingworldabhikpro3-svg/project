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
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = RoyalBlue900,
    onPrimaryContainer = RoyalBlue100,
    secondary = Color(0xFF2DD4BF),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Teal700,
    onSecondaryContainer = Teal100,
    tertiary = Color(0xFFFBBF24),
    background = Slate950,
    surface = Slate900,
    surfaceVariant = Slate800,
    onBackground = Slate100,
    onSurface = Slate100,
    onSurfaceVariant = Slate200,
    outline = Slate700,
    error = Color(0xFFF87171)
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalBlue600,
    onPrimary = Color.White,
    primaryContainer = RoyalBlue100,
    onPrimaryContainer = RoyalBlue900,
    secondary = Teal600,
    onSecondary = Color.White,
    secondaryContainer = Teal100,
    onSecondaryContainer = Teal700,
    tertiary = Amber600,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    surfaceVariant = Slate100,
    onBackground = Slate900,
    onSurface = Slate900,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    error = Rose600
)

@Composable
fun TuitionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
