package com.indianservers.aichemistryexplorer

import org.junit.Assert.*
import org.junit.Test

class ElementsViewerTest {
    private val elements = completePeriodicTable(emptyList())

    @Test fun allCardsUseExistingScientificDataAndCachedElectronModels() {
        elements.forEach { element ->
            val facts = elementFacts(element.number)
            val properties = viewerProperties(element).toMap()
            assertEquals(element.name, 8, properties.size)
            assertEquals(facts.atomicMass + " u", properties["Atomic mass"])
            assertEquals(facts.electronConfiguration, properties["Electron configuration"])
            assertEquals(facts.oxidationStates, properties["Oxidation states"])
            assertEquals(facts.electronegativity, properties["Electronegativity"])
            val model = viewerAtomicModel(element.number)
            assertSame(model, viewerAtomicModel(element.number))
            assertEquals(element.name, element.number, model.shells.sum())
            assertTrue(model.shells.all { it > 0 })
            assertTrue(viewerApplications(element).isNotBlank())
        }
    }

    @Test fun navigationStopsAtBothEndsForEveryElement() {
        for (number in 1..118) {
            assertEquals((number + 1).coerceAtMost(118), viewerAdjacent(number, 1))
            assertEquals((number - 1).coerceAtLeast(1), viewerAdjacent(number, -1))
        }
    }

    @Test fun allElementsAreAvailableAndShellCountsConserveElectrons() {
        assertEquals((1..118).toList(), elements.map { it.number })
        elements.forEach { assertEquals(it.name, it.number, electronShellDistribution(it.number).sum()) }
    }

    @Test fun categoryFiltersDistinguishMetalsMetalloidsAndNonmetals() {
        fun matches(symbol: String, category: String) = viewerMatches(elements.first { it.symbol == symbol }, category, "")
        assertTrue(matches("Fe", "Metals"))
        assertFalse(matches("O", "Metals"))
        assertFalse(matches("Si", "Metals"))
        assertTrue(matches("Si", "Metalloids"))
        assertTrue(matches("Cl", "Nonmetals"))
        assertTrue(matches("He", "Noble Gases"))
        assertFalse(matches("O", "Noble Gases"))
    }

    @Test fun searchSupportsNameSymbolAndAtomicNumberAndUnknownTemperature() {
        val oxygen = elements.first { it.number == 8 }
        listOf("oxygen", "O", "8").forEach { assertTrue(viewerMatches(oxygen, "All Elements", it)) }
        assertFalse(viewerMatches(oxygen, "All Elements", "gold"))
        assertEquals("-218.8 °C", viewerTemperature("54.36"))
        assertEquals("Unknown", viewerTemperature("—"))
    }
}
