package com.barcodebite.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = FreshGreen,
    onPrimary = Cloud,
    secondary = LeafGreen,
    onSecondary = Cloud,
    background = Cloud,
    onBackground = Charcoal,
    surface = SoftMint,
    onSurface = Graphite,
)

private val DarkColors = darkColorScheme(
    primary = LeafGreen,
    onPrimary = Charcoal,
    secondary = FreshGreen,
    onSecondary = Charcoal,
    background = Charcoal,
    onBackground = Cloud,
    surface = Graphite,
    onSurface = Cloud,
)

@Composable
fun BarcodeBiteTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = BarcodeBiteTypography,
        content = content,
    )
}
