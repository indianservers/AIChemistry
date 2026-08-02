package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VirtualLabCoverageTest {

    @Test
    fun catalogContainsCoreAndCompleteRequestedInventory() {
        assertEquals(70, coreVirtualExperiments.size)
        assertEquals(164, requestedLabInventory.size)
        assertEquals(164, requestedVirtualExperiments.size)
        assertEquals(234, virtualExperiments.size)
        assertEquals(13, virtualExperiments.map { it.technique }.distinct().size)
        assertTrue(virtualExperiments.map { it.category }.distinct().size >= 14)
        assertEquals(virtualExperiments.size, virtualExperiments.map { it.id }.distinct().size)
    }

    @Test
    fun requestedInventoryIsNumberedAndImplementedWithoutGaps() {
        assertEquals((1..164).toList(), requestedLabInventory.map { it.number })
        assertEquals(
            requestedLabInventory.map { it.title },
            requestedVirtualExperiments.map { it.title }
        )
        assertEquals(
            requestedLabInventory.map { "requested-${it.number.toString().padStart(3, '0')}" },
            requestedVirtualExperiments.map { it.id }
        )
        assertEquals(7, requestedLabInventory.map { it.category }.distinct().size)
    }

    @Test
    fun everyLaboratoryFoundationHasADistinctProtocolAndCompetency() {
        val foundations = requestedVirtualExperiments.filter {
            it.category == "Laboratory foundations"
        }
        assertEquals(20, foundations.size)
        assertEquals(20, foundations.map { it.objective }.distinct().size)
        assertEquals(20, foundations.map { it.equation }.distinct().size)
        assertEquals(20, foundations.map { it.apparatus }.distinct().size)
        assertEquals(20, foundations.map { it.reagents }.distinct().size)
        assertEquals(20, foundations.map { it.measuredQuantity }.distinct().size)
        assertEquals(20, foundations.map { it.analysis }.distinct().size)
        foundations.forEach { foundation ->
            assertEquals(foundation.title, 6, foundation.procedure.size)
            assertTrue(foundation.title, foundation.safety.length >= 100)
            assertTrue(foundation.title, foundation.objective.length >= 100)
            assertTrue(foundation.title, foundation.expectedResult.contains("controls pass"))
        }
    }

    @Test
    fun everyGeneralChemistryLabHasAnExactAccuracyProtocol() {
        val general = requestedVirtualExperiments.filter {
            it.category == "General chemistry laboratories"
        }
        assertEquals(42, general.size)
        assertEquals(42, general.map { it.title }.distinct().size)
        assertEquals(42, general.map { it.objective }.distinct().size)
        assertEquals(42, general.map { it.equation }.distinct().size)
        assertEquals(42, general.map { it.apparatus }.distinct().size)
        assertEquals(42, general.map { it.analysis }.distinct().size)
        general.forEach { laboratory ->
            assertEquals(laboratory.title, 6, laboratory.procedure.size)
            assertTrue(laboratory.title, laboratory.safety.length >= 100)
            assertTrue(laboratory.title, laboratory.theory.length >= 250)
            assertTrue(laboratory.title, laboratory.objective.contains("calibrated measurements"))
        }

        fun basis(title: String) = general.single { it.title == title }.equation
        assertTrue(basis("Ideal Gas Law introduction").contains("PV = nRT"))
        assertTrue(basis("Ethanol distillation").contains("azeotrope"))
        assertTrue(basis("Acid–base titration").contains("equivalence"))
        assertTrue(basis("Bomb calorimetry").contains("qv = ΔU"))
        assertTrue(basis("Spontaneity investigation").contains("ΔG = ΔH − TΔS"))
        assertTrue(basis("Nuclear-fission energy").contains("mass defect"))
        assertTrue(basis("Nuclear-fusion energy").contains("17.6 MeV"))
        assertTrue(basis("Photoelectric effect and solar panels").contains("E = hν"))
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
