package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScalableCurriculumCoverageTest {

    private fun firstScene(level: String, topic: String, subtopic: String): Chemistry3DScene =
        scalableCurriculum.first {
            it.level == level && it.majorTopic == topic && it.subtopic == subtopic
        }.visualizations3d.first().scene

    private fun firstMotif(level: String, topic: String, subtopic: String): ChemistryVisualMotif =
        scalableCurriculum.first {
            level.isNotBlank() && it.majorTopic == topic && it.subtopic == subtopic
        }.visualizations3d.first().motif

    @Test
    fun curriculumMeetsRequestedExpansionScale() {
        assertTrue(scalableCurriculum.size in 600..700)
        assertEquals(638, scalableCurriculum.size)
        assertEquals(2_200, scalableCurriculum.sumOf { it.microConcepts.size })
        assertTrue(scalableCurriculum.sumOf { it.simulations.size } >= 1_500)
        assertEquals(1_914, scalableCurriculum.sumOf { it.simulations.size })
        assertTrue(scalableCurriculum.sumOf { it.visualizations3d.size } >= 3_000)
        assertEquals(3_190, scalableCurriculum.sumOf { it.visualizations3d.size })
    }

    @Test
    fun everyExpansionHasStableUniqueReusableAssets() {
        assertEquals(scalableCurriculum.size, scalableCurriculum.map { it.id }.distinct().size)
        assertTrue(scalableCurriculum.all {
            it.id.isNotBlank() && it.majorTopic.isNotBlank() && it.subtopic.isNotBlank()
        })

        val microConcepts = scalableCurriculum.flatMap { it.microConcepts }
        assertEquals(microConcepts.size, microConcepts.map { it.id }.distinct().size)
        assertTrue(microConcepts.all {
            it.title.isNotBlank() && it.explanation.length >= 80 &&
                it.example.isNotBlank() && it.masteryPrompt.length >= 60
        })

        val simulations = scalableCurriculum.flatMap { it.simulations }
        assertEquals(simulations.size, simulations.map { it.id }.distinct().size)
        assertEquals(3, simulations.map { it.mode }.distinct().size)
        assertTrue(simulations.all {
            it.objective.length >= 60 && it.controlLabel.isNotBlank() &&
                it.lowState.isNotBlank() && it.highState.isNotBlank() && it.observationGuide.length >= 60
        })

        val visualizations = scalableCurriculum.flatMap { it.visualizations3d }
        assertEquals(visualizations.size, visualizations.map { it.id }.distinct().size)
        assertEquals(5, visualizations.map { it.mode }.distinct().size)
        assertTrue(visualizations.map { it.scene }.distinct().size >= 15)
        assertTrue(visualizations.map { it.motif }.distinct().size >= 55)
        assertTrue(visualizations.map { it.motionStyle }.distinct().size >= 15)
        assertTrue(visualizations.all {
            it.description.length >= 60 && it.interactionHint.length >= 40 &&
                it.scientificFocus.length >= 60 && it.conceptTitle.isNotBlank() &&
                it.entityLabels.size >= 4 && it.stageLabels.size >= 3 &&
                it.axisLabels.size >= 3 && it.keyRelationship.isNotBlank() &&
                it.motionSpeed in .28f..1.4f && it.motionIntensity in .5f..1f
        })
        assertTrue(scalableCurriculum.all { expansion ->
            expansion.visualizations3d.map { it.scene }.distinct().size == 5 &&
                expansion.visualizations3d.all { visual ->
                    visual.conceptTitle == expansion.subtopic &&
                        visual.description.contains(expansion.subtopic)
                }
        })
        assertTrue(scalableCurriculum.all { expansion ->
            expansion.visualizations3d.map { it.variantSeed }.distinct().size == 5 &&
                expansion.visualizations3d.map { it.motif }.distinct().size == 1 &&
                expansion.visualizations3d.all { it.motif == expansion.visualMotif } &&
                expansion.visualizations3d.map { it.motionStyle }.distinct().size >= 4
        })
        assertEquals(
            scalableCurriculum.size,
            scalableCurriculum.map { it.visualizations3d.first().variantSeed }.distinct().size
        )
        assertTrue(scalableCurriculum.flatMap { it.visualizations3d }.none {
            "chemical species, molecular structure, interaction or force" in it.description.lowercase()
        })
    }

    @Test
    fun representativeConceptsUseChemicallyCorrectSceneFamilies() {
        assertEquals(Chemistry3DScene.ATOMIC_ORBITAL, firstScene("Class 6–8", "Atoms", "Bohr model"))
        assertEquals(Chemistry3DScene.MOLECULAR_GEOMETRY, firstScene("Class 9–10", "Chemical Bonding", "VSEPR"))
        assertEquals(Chemistry3DScene.SURFACE_INTERFACE, firstScene("Class 11–12", "Surface Chemistry", "Adsorption"))
        assertEquals(Chemistry3DScene.MOLECULAR_GEOMETRY, firstScene("Undergraduate", "Advanced Inorganic Chemistry", "Crystal field theory"))
        assertEquals(Chemistry3DScene.PROCESS_FLOW, firstScene("Undergraduate", "Analytical Chemistry", "Redox titrations"))
        assertEquals(Chemistry3DScene.ELECTROCHEMICAL_CELL, firstScene("Class 11–12", "Electrochemistry", "Galvanic cells"))
        assertEquals(Chemistry3DScene.BIOPOLYMER, firstScene("Undergraduate", "Biochemistry", "DNA"))
        assertEquals(Chemistry3DScene.NUCLEAR_TRANSFORMATION, firstScene("Class 11–12", "Nuclear Chemistry", "Alpha decay"))
        assertEquals(Chemistry3DScene.SEPARATION_COLUMN, firstScene("Undergraduate", "Separation Techniques", "HPLC"))
        assertEquals(Chemistry3DScene.SPECTRAL_RESPONSE, firstScene("Undergraduate", "Spectroscopy", "IR"))
        assertEquals(Chemistry3DScene.ENERGY_SURFACE, firstScene("Class 11–12", "Thermodynamics", "Enthalpy"))
        assertEquals(Chemistry3DScene.REACTION_COORDINATE, firstScene("Class 11–12", "Chemical Kinetics", "Rate laws"))
        assertEquals(Chemistry3DScene.ENVIRONMENTAL_CYCLE, firstScene("Undergraduate", "Environmental Chemistry", "Air pollution"))
        assertEquals(Chemistry3DScene.PROCESS_FLOW, firstScene("Undergraduate", "Industrial Chemistry", "Cement"))
        assertEquals(Chemistry3DScene.DATA_SURFACE, firstScene("Undergraduate", "Computational Chemistry", "DFT"))
        assertEquals(Chemistry3DScene.MOLECULAR_GEOMETRY, firstScene("Undergraduate", "Medicinal Chemistry", "Drug discovery"))
    }

    @Test
    fun ambiguousNamesUseTopicSpecificChemistryMotifs() {
        assertEquals(ChemistryVisualMotif.ENVIRONMENTAL_CYCLE, firstMotif("Undergraduate", "Environmental Chemistry", "Air pollution"))
        assertEquals(ChemistryVisualMotif.SPECTROSCOPY, firstMotif("Undergraduate", "Spectroscopy", "IR"))
        assertEquals(ChemistryVisualMotif.ACID_BASE, firstMotif("Class 9–10", "Acids Bases Salts", "pH"))
        assertEquals(ChemistryVisualMotif.MOLECULAR_FORMULA, firstMotif("Class 9–10", "Mole Concept", "Molecular formula"))
        assertEquals(ChemistryVisualMotif.COORDINATION, firstMotif("Undergraduate", "Advanced Inorganic Chemistry", "Crystal field theory"))
        assertEquals(ChemistryVisualMotif.BOND_GEOMETRY, firstMotif("Class 9–10", "Chemical Bonding", "VSEPR"))
        assertEquals(ChemistryVisualMotif.CHROMATOGRAPHY, firstMotif("Undergraduate", "Separation Techniques", "HPLC"))
        assertEquals(ChemistryVisualMotif.NUCLEAR, firstMotif("Class 11–12", "Nuclear Chemistry", "Nuclear fusion"))
        assertEquals(ChemistryVisualMotif.NUCLEIC_ACID, firstMotif("Undergraduate", "Biochemistry", "DNA"))
        assertEquals(ChemistryVisualMotif.QUANTUM_CHEMISTRY, firstMotif("Undergraduate", "Computational Chemistry", "DFT"))
        assertEquals(ChemistryVisualMotif.MAGNETIC_RESONANCE, firstMotif("Undergraduate", "Spectroscopy", "NMR"))
        assertEquals(ChemistryVisualMotif.MASS_SPECTROMETRY, firstMotif("Undergraduate", "Spectroscopy", "Mass spectrometry"))
        assertEquals(ChemistryVisualMotif.MICELLE_ASSEMBLY, firstMotif("Class 11–12", "Surface Chemistry", "Micelles"))
        assertEquals(ChemistryVisualMotif.GALVANIC_BATTERY, firstMotif("Class 11–12", "Electrochemistry", "Batteries"))
        assertEquals(ChemistryVisualMotif.CORROSION_FRONT, firstMotif("Class 11–12", "Electrochemistry", "Corrosion"))
        assertEquals(ChemistryVisualMotif.SEMICONDUCTOR_BANDS, firstMotif("Class 11–12", "Solid State", "Semiconductors"))
    }
}
