package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ForagedGreenPrimary,
    secondary = MintSecondary,
    tertiary = CaroteneOrangeAccent,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = HeavyCharcoal,
    onTertiary = Color.White,
    onBackground = HeavyCharcoal,
    onSurface = HeavyCharcoal
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkMint,
    secondary = DarkDeepGreen,
    tertiary = DarkOrangeAccent,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onSecondary = PaleText,
    onTertiary = Color.Black,
    onBackground = PaleText,
    onSurface = PaleText
)

@Composable
fun FoodRescueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
