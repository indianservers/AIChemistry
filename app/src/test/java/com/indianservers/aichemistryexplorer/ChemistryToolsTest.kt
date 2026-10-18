package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChemistryToolsTest {
    @Test
    fun `formula parser handles groups and hydrates`() {
        assertEquals(mapOf("Ca" to 1, "O" to 2, "H" to 2), parseChemicalFormula("Ca(OH)2"))
        assertEquals(mapOf("Cu" to 1, "S" to 1, "O" to 9, "H" to 10), parseChemicalFormula("CuSO4·5H2O"))
    }

    @Test
    fun `equation balancer solves common and combustion reactions`() {
        assertEquals("4Fe + 3O2 → 2Fe2O3", balanceChemicalEquation("Fe + O2 -> Fe2O3").balancedEquation)
        assertEquals("N2 + 3H2 → 2NH3", balanceChemicalEquation("N2 + H2 -> NH3").balancedEquation)
        assertEquals("C3H8 + 5O2 → 3CO2 + 4H2O", balanceChemicalEquation("C3H8 + O2 -> CO2 + H2O").balancedEquation)
        assertTrue(balanceChemicalEquation("H2 + O2").error != null)
    }

    @Test
    fun `valency builder simplifies charge ratios and groups polyatomic ions`() {
        val aluminium = commonCations.first { it.symbol == "Al" }
        val sulfate = commonAnions.first { it.symbol == "SO4" }
        val magnesium = commonCations.first { it.symbol == "Mg" }
        val oxide = commonAnions.first { it.symbol == "O" }

        assertEquals("Al2(SO4)3", buildIonicFormula(aluminium, sulfate).formula)
        assertEquals("MgO", buildIonicFormula(magnesium, oxide).formula)
    }

    @Test
    fun `vsepr engine reports canonical shapes`() {
        assertEquals("Tetrahedral", vseprShape(4, 0).molecularShape)
        assertEquals("Trigonal pyramidal", vseprShape(3, 1).molecularShape)
        assertEquals("Bent", vseprShape(2, 2).molecularShape)
        assertEquals("Square planar", vseprShape(4, 2).molecularShape)
    }

    @Test
    fun `stoichiometry applies coefficient ratio`() {
        val result = stoichiometricMoles(3.0, knownCoefficient = 3, targetCoefficient = 2)
        assertEquals(2.0, result.targetMoles, 1e-9)
    }

    @Test
    fun `predictor recognises reaction classes`() {
        assertEquals("NaCl + H₂O", predictReactionProducts("HCl + NaOH")?.products)
        assertEquals("Complete combustion", predictReactionProducts("CH4 + O2")?.type)
        assertNotNull(predictReactionProducts("AgNO3 + NaCl"))
        assertNull(predictReactionProducts("Xe + NaCl"))
    }

    @Test
    fun `solubility interpolates between reference points`() {
        val sodiumChloride = solubilitySeries.first { it.name == "NaCl" }
        assertEquals(36.3f, interpolateSolubility(sodiumChloride, 30f), .001f)
    }

    @Test
    fun `unit and significant figure checks preserve precision`() {
        assertEquals(3, significantFigures("0.00450"))
        assertEquals(1, significantFigures("100"))
        assertEquals(3, significantFigures("100."))
        assertEquals(25.0, convertUnit(.025, "g", "mg")!!.value, 1e-9)
        assertNull(convertUnit(1.0, "g", "mL"))
        assertTrue(approximatelyEqual(24.99, 25.0))
    }

    @Test
    fun `lewis editor accounts for charge and bonding electrons`() {
        val ammonium = lewisSummary("N", "H", ligandCount = 4, bondOrder = 1, charge = 1)
        assertEquals(8, ammonium.totalValenceElectrons)
        assertEquals(8, ammonium.bondingElectrons)
        assertEquals(0, ammonium.remainingElectrons)
    }
}
