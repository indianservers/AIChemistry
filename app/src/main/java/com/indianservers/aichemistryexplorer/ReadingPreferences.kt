package com.indianservers.aichemistryexplorer

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal data class ReadingPalette(
    val name: String,
    val background: Color,
    val glow: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val outline: Color
)

internal val readingPalettes = listOf(
    ReadingPalette("Original", Night, Color(0xFF0B1830), Panel, Panel2, Border),
    ReadingPalette("Midnight blue", Color(0xFF040B18), Color(0xFF102A4B), Color(0xFF0A192A), Color(0xFF102238), Color(0xFF254565)),
    ReadingPalette("Graphite", Color(0xFF0D1014), Color(0xFF252B32), Color(0xFF171B20), Color(0xFF20262D), Color(0xFF39434D)),
    ReadingPalette("Forest night", Color(0xFF06100D), Color(0xFF14342A), Color(0xFF0C1C17), Color(0xFF122820), Color(0xFF285344)),
    ReadingPalette("Deep plum", Color(0xFF100817), Color(0xFF311944), Color(0xFF1C1026), Color(0xFF281536), Color(0xFF503064)),
    ReadingPalette("Warm cocoa", Color(0xFF120C09), Color(0xFF3B281C), Color(0xFF211610), Color(0xFF2D1F17), Color(0xFF5A4030)),
    ReadingPalette("Ocean teal", Color(0xFF031011), Color(0xFF12393C), Color(0xFF081D1F), Color(0xFF0E292C), Color(0xFF275457)),
    ReadingPalette("Indigo dusk", Color(0xFF090A18), Color(0xFF222654), Color(0xFF11142A), Color(0xFF191D3A), Color(0xFF363D71)),
    ReadingPalette("Soft slate", Color(0xFF0B1017), Color(0xFF243445), Color(0xFF121C27), Color(0xFF192634), Color(0xFF354B60))
)

internal val LocalReadingPalette = staticCompositionLocalOf { readingPalettes.first() }
internal val LocalHomeAction = staticCompositionLocalOf<(() -> Unit)?> { null }
internal val LocalBackAction = staticCompositionLocalOf<(() -> Unit)?> { null }
