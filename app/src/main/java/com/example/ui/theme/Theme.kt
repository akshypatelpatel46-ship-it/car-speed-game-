package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF1A1202),
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = SkyBlue,
    onSecondary = Color(0xFF041926),
    secondaryContainer = SteelMedium,
    onSecondaryContainer = TextPrimary,
    tertiary = RubyAccent,
    onTertiary = Color.White,
    background = CanvasBackground,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SteelDark,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
