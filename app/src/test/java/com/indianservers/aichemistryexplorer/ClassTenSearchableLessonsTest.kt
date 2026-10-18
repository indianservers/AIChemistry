package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClassTenSearchableLessonsTest {
    private val expected = linkedMapOf(
        "Class 10 Chemical Substances and Carbon" to listOf(
            "Balancing chemical equations",
            "State symbols and reaction observations",
            "Endothermic and exothermic reactions",
            "Precipitation reactions",
            "Preparation equations and uses of sodium hydroxide",
            "Preparation equations and uses of bleaching powder",
            "Preparation equations and uses of baking soda",
            "Preparation equations and uses of washing soda",
            "Preparation equations and uses of plaster of Paris",
            "Metal extraction by reactivity series",
            "Functional groups in carbon compounds",
            "IUPAC naming of carbon compounds",
            "Addition reactions of carbon compounds",
            "Substitution reactions of carbon compounds",
            "Oxidation reactions of carbon compounds",
            "Soap micelles and cleansing action",
            "Detergents and hard water"
        ),
        "Periodic Classification and Electrolysis" to listOf(
            "Döbereiner's triads",
            "Newlands' law of octaves",
            "Mendeleev's periodic classification"
        )
    )

    @Test
    fun boardTopicsAreIndividualAuthoredSearchIndexLessons() {
        expected.forEach { (moduleTitle, titles) ->
            val module = curriculum.single { it.level == "Class 9–10" && it.title == moduleTitle }
            titles.forEach { title ->
                val lesson = module.lessons.single { it.title == title }
                val authored = classTenBoardContent["$moduleTitle|$title"]
                assertTrue("$moduleTitle/$title needs authored content", authored != null)
                assertTrue(lesson.concept.startsWith(authored?.summary.orEmpty()))
                assertTrue(lesson.theory.length >= 650)
                assertTrue(lesson.workedExample.contains(authored?.workedExample.orEmpty()))
                assertTrue(lesson.formula.isNotBlank())
                assertTrue(lesson.advancedConcepts.size >= 3)
            }
        }
        assertEquals(20, expected.values.sumOf { it.size })
        assertEquals(20, classTenBoardContent.size)
    }

    @Test
    fun everyNewTitleIsUniqueWithinItsModuleAndAvailableToGlobalSearch() {
        val indexedTitles = curriculum.flatMap { module -> module.lessons.map { module.title to it.title } }
        expected.forEach { (moduleTitle, titles) ->
            assertEquals(titles.size, titles.distinct().size)
            titles.forEach { title ->
                assertEquals("Global search indexes every curriculum lesson", 1, indexedTitles.count { it == (moduleTitle to title) })
            }
        }
    }
}
