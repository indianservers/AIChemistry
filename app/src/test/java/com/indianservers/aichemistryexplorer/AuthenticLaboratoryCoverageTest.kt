package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthenticLaboratoryCoverageTest {

    private val labs by lazy { scalableCurriculum.map { it.virtualLab } }

    @Test
    fun everySubtopicHasOneTraceableLaboratoryInvestigation() {
        assertTrue(scalableCurriculum.size >= 700)
        assertEquals(scalableCurriculum.size, labs.size)
        assertEquals(labs.size, labs.map { it.id }.distinct().size)
        assertTrue(scalableCurriculum.all { expansion ->
            expansion.virtualLab.id.startsWith(expansion.id) &&
                expansion.virtualLab.title.contains(expansion.subtopic)
        })
        assertTrue(labs.map { it.mode }.distinct().size >= 7)
    }

    @Test
    fun everyLaboratoryMeetsTheAuthenticityContract() {
        assertTrue(labs.all { lab ->
            lab.objective.length >= 100 &&
                lab.ppe.size >= 4 &&
                lab.apparatus.size >= 4 &&
                lab.apparatus.all {
                    it.name.isNotBlank() && it.specification.isNotBlank() &&
                        it.preparationCheck.isNotBlank()
                } &&
                lab.materials.size >= 3 &&
                lab.materials.all {
                    it.identity.isNotBlank() && it.workingAmount.isNotBlank() &&
                        it.hazard.isNotBlank() && it.wasteStream.isNotBlank()
                } &&
                lab.calibrationSteps.size >= 3 &&
                lab.procedure.size >= 9 &&
                lab.procedure.all {
                    it.instruction.isNotBlank() && it.checkpoint.isNotBlank() &&
                        it.expectedObservation.isNotBlank() && it.failureConsequence.isNotBlank()
                } &&
                lab.measurements.size >= 2 &&
                lab.measurements.all {
                    it.unit.isNotBlank() && it.instrument.isNotBlank() &&
                        it.uncertainty.isNotBlank() && it.replicateCount >= 3
                } &&
                lab.controlledVariables.size >= 4 &&
                lab.positiveControl.isNotBlank() &&
                lab.negativeControl.isNotBlank() &&
                lab.calculationModel.isNotBlank() &&
                lab.acceptanceCriteria.size >= 5 &&
                lab.failureStates.size >= 4 &&
                lab.wastePlan.size >= 3 &&
                lab.reportSections.size >= 8
        })
    }

    @Test
    fun specialistTopicsReceiveTheCorrectLaboratoryFamily() {
        fun labFor(topic: String, subtopic: String) = scalableCurriculum.first {
            it.majorTopic == topic && it.subtopic == subtopic
        }.virtualLab

        assertEquals(LaboratoryMode.WET_CHEMISTRY, labFor("Acids Bases Salts", "pH").mode)
        assertEquals(LaboratoryMode.INSTRUMENTAL_ANALYSIS, labFor("Spectroscopy", "NMR").mode)
        assertEquals(LaboratoryMode.INSTRUMENTAL_ANALYSIS, labFor("Separation Techniques", "HPLC").mode)
        assertEquals(LaboratoryMode.COMPUTATIONAL, labFor("Computational Chemistry", "DFT").mode)
        assertEquals(LaboratoryMode.BIOCHEMICAL, labFor("Biochemistry", "DNA").mode)
        assertEquals(LaboratoryMode.MATERIALS_TESTING, labFor("Materials Chemistry", "Nanomaterials").mode)
        assertEquals(LaboratoryMode.ENVIRONMENTAL_FIELD, labFor("Environmental Chemistry", "Air pollution").mode)
    }
}
