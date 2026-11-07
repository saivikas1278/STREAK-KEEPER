package com.example.streakkeeper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SwissBentoDarkColorScheme = darkColorScheme(
    primary = ButtonPrimaryBg,
    onPrimary = ButtonPrimaryText,
    primaryContainer = CardElevated,
    onPrimaryContainer = TextPrimary,
    secondary = TextSecondary,
    onSecondary = BackgroundPrimary,
    secondaryContainer = CardPrimary,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentInfo,
    onTertiary = BackgroundPrimary,
    background = BackgroundPrimary,
    onBackground = TextPrimary,
    surface = CardPrimary,
    onSurface = TextPrimary,
    surfaceVariant = CardElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderPrimary,
    outlineVariant = BorderSubtle
)

@Composable
fun StreakKeeperTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SwissBentoDarkColorScheme,
        typography = Typography,
        content = content
    )
}
