package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PeriodicAccuracyTest {

    private val elements by lazy { completePeriodicTable(emptyList()) }

    @Test
    fun authoritativeDatasetCoversAll118Elements() {
        assertEquals((1..118).toSet(), elementFactsByNumber.keys)
        assertEquals(118, elements.size)
        assertEquals(118, elements.map { it.symbol }.distinct().size)
        assertEquals((1..118).toList(), elements.map { it.number })
        assertTrue(elementFactsByNumber.values.all { it.atomicMass.toDoubleOrNull()?.let { mass -> mass > 0 } == true })
    }

    @Test
    fun tableLayoutContainsEveryElementExactlyOnce() {
        assertTrue(periodicRows.all { it.size == 18 })
        val main = periodicRows.flatten().filterNotNull()
        val displayed = main + lanthanideRow + actinideRow
        assertEquals((1..118).toSet(), displayed.toSet())
        assertEquals(118, displayed.size)
    }

    @Test
    fun periodsAndGroupsMatchKnownPositions() {
        assertEquals(1, periodicPeriod(1))
        assertEquals(2, periodicPeriod(6))
        assertEquals(4, periodicPeriod(26))
        assertEquals(6, periodicPeriod(72))
        assertEquals(7, periodicPeriod(118))

        assertEquals(1, periodicGroup(1))
        assertEquals(18, periodicGroup(2))
        assertEquals(14, periodicGroup(6))
        assertEquals(8, periodicGroup(26))
        assertEquals(4, periodicGroup(72))
        assertEquals(17, periodicGroup(117))
        assertEquals(18, periodicGroup(118))
        assertEquals(3, periodicGroup(57))
        assertNull(periodicGroup(58))
        assertEquals(3, periodicGroup(89))
        assertNull(periodicGroup(90))
    }

    @Test
    fun chemicalFamiliesHandleFBlockEndpointsCorrectly() {
        assertEquals("Non-metal", elements[0].category)
        assertEquals("Noble gas", elements[1].category)
        assertEquals("Lanthanide", elements[56].category)
        assertEquals("Lanthanide", elements[70].category)
        assertEquals("Actinide", elements[88].category)
        assertEquals("Actinide", elements[102].category)
        assertEquals("Transition metal", elements[71].category)
        assertEquals("Halogen", elements[116].category)
    }

    @Test
    fun sourcedConfigurationsIncludeKnownExceptions() {
        assertEquals("[Ar]3d5 4s1", elementFacts(24).electronConfiguration)
        assertEquals("[Ar]4s1 3d10", elementFacts(29).electronConfiguration)
        assertEquals("[Kr]4d10", elementFacts(46).electronConfiguration)
        assertEquals("[Xe]6s1 4f14 5d10", elementFacts(79).electronConfiguration)
        assertEquals("[Xe]6s2 4f14 5d2", elementFacts(72).electronConfiguration)
    }

    @Test
    fun shellDiagramsAlwaysContainAtomicNumberElectrons() {
        (1..118).forEach { number ->
            assertEquals("Z=$number", number, electronShellDistribution(number).sum())
            assertTrue("Z=$number", electronShellDistribution(number).isNotEmpty())
            assertTrue("Z=$number", electronShellDistribution(number).size <= periodicPeriod(number))
        }
        assertEquals(listOf(2, 8, 18, 32, 10, 2), electronShellDistribution(72))
    }

    @Test
    fun keyStatesAndMeasurementsAreSourced() {
        assertEquals("Gas", elementFacts(8).standardState)
        assertEquals("Liquid", elementFacts(35).standardState)
        assertEquals("Liquid", elementFacts(80).standardState)
        assertEquals("Solid", elementFacts(72).standardState)
        assertEquals("3.98", elementFacts(9).electronegativity)
        assertEquals("4876", elementFacts(72).boilingPointK)
        assertEquals("212", elementFacts(72).atomicRadiusPm)
    }

    @Test
    fun compoundGalleryNeverFabricatesFallbackFormulas() {
        assertEquals(listOf("HfO₂", "HfCl₄", "HfF₄", "HfC"), validatedCompoundsFor(elements[71]))
        assertTrue(validatedCompoundsFor(elements[117]).isEmpty())
        assertFalse(validatedCompoundsFor(elements[7]).contains("O₂"))
        assertFalse(validatedCompoundsFor(elements[16]).contains("Cl₂"))
        assertFalse(validatedCompoundsFor(elements[52]).contains("I₂"))
    }
}
