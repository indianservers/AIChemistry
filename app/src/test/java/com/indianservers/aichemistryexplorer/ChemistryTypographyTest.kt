package com.indianservers.aichemistryexplorer

import androidx.compose.ui.text.style.BaselineShift
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChemistryTypographyTest {

    @Test
    fun asciiFormulaDigitsBecomeTypographicSubscripts() {
        val result = chemistryNotation("2H2 + O2 -> 2H2O")
        assertEquals("2H2 + O2 → 2H2O", result.text)
        assertTrue(result.spanStyles.count { it.item.baselineShift == BaselineShift.Subscript } >= 3)
    }

    @Test
    fun chargesExponentsOrbitalsAndLatexMarkersBecomeSuperscript() {
        val result = chemistryNotation("Fe^3+ + SO4^2- · 10^{-5} · [Ar]3d5 4s1 · sp3")
        assertEquals("Fe3+ + SO42- · 10-5 · [Ar]3d5 4s1 · sp3", result.text)
        assertTrue(result.spanStyles.count { it.item.baselineShift == BaselineShift.Superscript } >= 6)
        assertTrue(result.spanStyles.any { it.item.baselineShift == BaselineShift.Subscript })
    }

    @Test
    fun existingUnicodeNotationIsPreservedWithoutDoubleFormatting() {
        val source = "H₂SO₄ + 2OH⁻ → SO₄²⁻ + 2H₂O"
        val result = chemistryNotation(source)
        assertEquals(source, result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun scientificNamesAreNotMistakenForMolecularSubscripts() {
        val result = chemistryNotation("Cytochrome P450 metabolism")
        assertEquals("Cytochrome P450 metabolism", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun latexFractionAndNumericSlashUseFractionGlyph() {
        assertEquals("1⁄2", chemistryNotation("\\frac{1}{2}").text)
        assertEquals("Page 1⁄4", chemistryNotation("Page 1/4").text)
    }
}
