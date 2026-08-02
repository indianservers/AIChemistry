package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VirtualLabCoverageTest {

    @Test
    fun catalogContainsSeventyExperimentsAcrossCoreTechniques() {
        assertEquals(70, virtualExperiments.size)
        assertEquals(13, virtualExperiments.map { it.technique }.distinct().size)
        assertTrue(virtualExperiments.map { it.category }.distinct().size >= 14)
        assertEquals(virtualExperiments.size, virtualExperiments.map { it.id }.distinct().size)
    }

    @Test
    fun everyExperimentHasScientificAndPracticalContent() {
        virtualExperiments.forEach { experiment ->
            assertTrue(experiment.title, experiment.objective.isNotBlank())
            assertTrue(experiment.title, experiment.equation.isNotBlank())
            assertTrue(experiment.title, experiment.apparatus.isNotBlank())
            assertTrue(experiment.title, experiment.reagents.isNotBlank())
            assertTrue(experiment.title, experiment.safety.isNotBlank())
            assertTrue(experiment.title, experiment.procedure.size >= 4)
            assertTrue(experiment.title, experiment.measuredQuantity.isNotBlank())
            assertTrue(experiment.title, experiment.analysis.isNotBlank())
            assertTrue(experiment.title, experiment.theory.length >= 80)
            assertTrue(experiment.title, experiment.expectedResult.contains(experiment.measuredQuantity))
        }
    }
}
