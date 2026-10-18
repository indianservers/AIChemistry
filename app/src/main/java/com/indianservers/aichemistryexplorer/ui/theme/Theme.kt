package com.indianservers.aichemistryexplorer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ChemistryColors = darkColorScheme(
    primary = Color(0xFF1EDCFF),
    secondary = Color(0xFF9A45FF),
    tertiary = Color(0xFF25E6A1),
    background = Color(0xFF020711),
    surface = Color(0xFF071321),
    onPrimary = Color(0xFF020711),
    onBackground = Color(0xFFEAF7FF),
    onSurface = Color(0xFFEAF7FF),
    outline = Color(0xFF18324A)
)

@Composable
fun AIChemistryExplorerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ChemistryColors, typography = Typography, content = content)
}
