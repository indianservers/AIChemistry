package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Test

class ElementComparisonSelectionTest {
    @Test
    fun `comparison accepts every periodic element without a four element cap`() {
        val selected = addComparisonElements(emptyList(), (1..118).toList())

        assertEquals(118, selected.size)
        assertEquals((1..118).toList(), selected)
    }

    @Test
    fun `comparison keeps selection order and ignores duplicates`() {
        val selected = addComparisonElements(listOf(8, 1, 79), listOf(1, 6, 8, 118))

        assertEquals(listOf(8, 1, 79, 6, 118), selected)
    }
}
