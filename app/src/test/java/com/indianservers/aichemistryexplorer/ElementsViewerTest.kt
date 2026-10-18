package com.indianservers.aichemistryexplorer

import org.junit.Assert.*
import org.junit.Test

class ElementsViewerTest {
    private val elements = completePeriodicTable(emptyList())

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
