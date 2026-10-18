package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveMolecularViewTest {
    @Test
    fun `lesson keywords select a chemically relevant molecule`() {
        assertEquals("C₆H₆", liveMolecularModelFor("Organic Chemistry", "Benzene and aromaticity").formula)
        assertEquals("C₂H₅OH", liveMolecularModelFor("Organic Chemistry", "Alcohols and ethanol").formula)
        assertEquals("CO₂", liveMolecularModelFor("Gaseous State", "Gas laws").formula)
        assertEquals("H₂O", liveMolecularModelFor("Solutions", "Hydrogen bonding").formula)
    }

    @Test
    fun `control labels choose the correct molecular response`() {
        assertEquals(MolecularResponse.ENERGY, molecularResponseFor("Molecular energy or interaction strength"))
        assertEquals(MolecularResponse.DENSITY, molecularResponseFor("Reactant concentration"))
        assertEquals(MolecularResponse.REACTION, molecularResponseFor("Reaction progress"))
        assertEquals(MolecularResponse.POLARITY, molecularResponseFor("Bond polarity"))
        assertEquals(MolecularResponse.MODEL, molecularResponseFor("Evidence quality"))
    }

    @Test
    fun `density view adds particles and closes spacing as control rises`() {
        val low = molecularSnapshot(MolecularResponse.DENSITY, 0f)
        val high = molecularSnapshot(MolecularResponse.DENSITY, 1f)

        assertTrue(high.particleCopies > low.particleCopies)
        assertTrue(high.spacing < low.spacing)
        assertTrue(high.motion > low.motion)
    }

    @Test
    fun `molecular control is clamped to a safe range`() {
        assertEquals(molecularSnapshot(MolecularResponse.ENERGY, 0f), molecularSnapshot(MolecularResponse.ENERGY, -5f))
        assertEquals(molecularSnapshot(MolecularResponse.ENERGY, 1f), molecularSnapshot(MolecularResponse.ENERGY, 5f))
    }
}
