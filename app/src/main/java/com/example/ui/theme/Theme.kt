package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DhayaGoldBright,
    onPrimary = DhayaNavyDark,
    primaryContainer = DhayaNavyLight,
    onPrimaryContainer = DhayaGoldLight,
    secondary = DhayaGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF253348),
    onSecondaryContainer = DhayaGoldLight,
    tertiary = UpiBlue,
    background = DhayaNavyDark,
    surface = NavySurface,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1B2B43),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF334E68)
)

private val LightColorScheme = lightColorScheme(
    primary = DhayaNavy,
    onPrimary = Color.White,
    primaryContainer = DhayaGoldLight,
    onPrimaryContainer = DhayaNavyDark,
    secondary = DhayaGoldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = DhayaNavy,
    tertiary = UpiBlue,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFEEF2F6),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use Dhaya Mobiles signature brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
