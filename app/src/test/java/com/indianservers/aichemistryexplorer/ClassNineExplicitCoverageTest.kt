package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClassNineExplicitCoverageTest {
    private val expected = linkedMapOf(
        "Class 9 Matter and Mixtures" to listOf(
            "Crystallisation of dissolved solids",
            "Simple distillation of liquid mixtures",
            "Paper chromatography",
            "Sublimation as a separation method",
            "Centrifugation of mixtures",
            "Coagulation of colloids",
            "Concentration calculations",
            "Solubility curves and graphs"
        ),
        "Class 9 Atoms and Formulae" to listOf(
            "Formation of ionic and covalent compounds",
            "Properties of ionic versus covalent compounds",
            "Electron-dot structures"
        ),
        "School Practical Chemistry" to listOf(
            "Designing a fair chemistry investigation",
            "Representing chemistry data in tables and graphs",
            "Laboratory safety and chemical handling"
        )
    )

    @Test
    fun cbseClassNineRequirementsAreIndividualAuthoredLessons() {
        expected.forEach { (moduleTitle, titles) ->
            val module = curriculum.single { it.level == "Class 9–10" && it.title == moduleTitle }
            titles.forEach { title ->
                val lesson = module.lessons.single { it.title == title }
                val authored = classNineBoardContent["$moduleTitle|$title"]
                assertTrue("$moduleTitle/$title needs authored content", authored != null)
                assertTrue(lesson.concept.startsWith(authored?.summary.orEmpty()))
                assertTrue(lesson.theory.length >= 650)
                assertTrue(lesson.workedExample.contains(authored?.workedExample.orEmpty()))
                assertTrue(lesson.formula.isNotBlank())
                assertTrue(lesson.advancedConcepts.size >= 3)
            }
        }
        assertEquals(14, expected.values.sumOf { it.size })
        assertEquals(14, classNineBoardContent.size)
    }

    @Test
    fun newLessonsAreUniqueAndAvailableToGlobalSearch() {
        val indexed = curriculum.flatMap { module -> module.lessons.map { module.title to it.title } }
        expected.forEach { (moduleTitle, titles) ->
            assertEquals(titles.size, titles.distinct().size)
            titles.forEach { title ->
                assertEquals(1, indexed.count { it == (moduleTitle to title) })
            }
        }
    }

    @Test
    fun quantitativeLessonsExposeCalculationAndGraphRelationships() {
        val matter = curriculum.single { it.title == "Class 9 Matter and Mixtures" }
        val concentration = matter.lessons.single { it.title == "Concentration calculations" }
        val solubility = matter.lessons.single { it.title == "Solubility curves and graphs" }
        assertTrue("mass %" in concentration.formula)
        assertTrue("crystals expected" in solubility.formula)
        assertTrue("50 g crystals" in solubility.workedExample)
    }
}
