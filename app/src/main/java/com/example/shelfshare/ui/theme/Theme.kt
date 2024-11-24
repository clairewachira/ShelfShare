package com.example.shelfshare.ui.theme

import android.app.Activity
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

// Primary Color Palette
private val BookPrimaryLight = Color(0xFF4A6741)     // Deep sage green
private val BookPrimaryDark = Color(0xFF8BC34A)      // Bright light green

// Secondary Color Palette
private val BookSecondaryLight = Color(0xFF8D6E63)   // Warm brown
private val BookSecondaryDark = Color(0xFFD7CCC8)    // Light warm brown

// Tertiary Color Palette
private val BookTertiaryLight = Color(0xFF5D4037)    // Dark brown
private val BookTertiaryDark = Color(0xFFA1887F)     // Soft terracotta

// Background and Surface Colors
private val BookBackgroundLight = Color(0xFFFAFAFA)  // Very light grey
private val BookBackgroundDark = Color(0xFF121212)   // Almost black

// On Colors (text and icons)
private val BookOnPrimaryLight = Color(0xFFFFFFFF)   // White
private val BookOnPrimaryDark = Color(0xFF000000)    // Black

private val BookOnBackgroundLight = Color(0xFF212121)  // Very dark grey
private val BookOnBackgroundDark = Color(0xFFE0E0E0)   // Light grey

private val DarkColorScheme = darkColorScheme(
    primary = BookPrimaryDark,
    onPrimary = BookOnPrimaryDark,
    secondary = BookSecondaryDark,
    tertiary = BookTertiaryDark,
    background = BookBackgroundDark,
    surface = Color(0xFF1E1E1E),  // Slightly lighter than background
    onBackground = BookOnBackgroundDark,
    onSurface = BookOnBackgroundDark
)

private val LightColorScheme = lightColorScheme(
    primary = BookPrimaryLight,
    onPrimary = BookOnPrimaryLight,
    secondary = BookSecondaryLight,
    tertiary = BookTertiaryLight,
    background = BookBackgroundLight,
    surface = Color(0xFFF5F5F5),  // Very light grey
    onBackground = BookOnBackgroundLight,
    onSurface = BookOnBackgroundLight
)

@Composable
fun ShelfShareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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