package com.wagdev.inventorymanagement.ui.theme

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

// Dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF283593), // Darker indigo for primary actions
    primaryContainer = Color(0xFF1A237E), // Deep indigo for containers
    secondary = Color(0xFF43A047), // Deeper green for secondary actions
    background = Color(0xFF121212), // Dark gray for background
    surface = Color(0xFF1E1E1E), // Lighter dark gray for surfaces
    onPrimary = Color(0xFFFFFFFF), // White for text/icons on primary color
    onSecondary = Color(0xFFFFFFFF), // White for text/icons on secondary color
    onBackground = Color(0xFFE0E0E0), // Light gray for text on dark background
    onSurface = Color(0xFFE0E0E0), // Light gray for text on surfaces
    error = Color(0xFFD32F2F), // Red for error messages
    onError = Color(0xFFFFFFFF) // White text/icons on error
)

// Light color scheme
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E88E5), // Bright blue for primary actions
    primaryContainer = Color(0xFFBBDEFB), // Light blue for containers
    secondary = Color(0xFF43A047), // Fresh green for secondary actions
    background = Color(0xFFFFFFFF), // Clean white background
    surface = Color(0xFFF5F5F5), // Light gray for surfaces
    onPrimary = Color(0xFFFFFFFF), // White for text/icons on primary color
    onSecondary = Color(0xFF000000), // Dark text/icons on secondary color
    onBackground = Color(0xFF000000), // Black text on light background
    onSurface = Color(0xFF000000), // Black text on surfaces
    error = Color(0xFFD32F2F), // Red for error messages
    onError = Color(0xFFFFFFFF) // White text/icons on error
)

@Composable
fun InventoryManagementTheme(
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
