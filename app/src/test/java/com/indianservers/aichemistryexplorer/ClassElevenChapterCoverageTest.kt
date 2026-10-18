package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClassElevenChapterCoverageTest {
    private val expected = linkedMapOf(
        "Classification and Periodicity: Senior Secondary" to 8,
        "Chemical Bonding and Molecular Structure" to 12,
        "Hydrocarbons: Senior Secondary" to 8,
        "Organic Purification and Analysis" to 8,
        "Gaseous State: Formative" to 8,
        "s-Block Elements: Formative" to 4,
        "p-Block Elements: Formative" to 4
    )

    @Test
    fun dedicatedClassElevenChaptersHaveCompleteAuthoredHierarchy() {
        expected.forEach { (title, count) ->
            val module = curriculum.single { it.level == "Class 11–12" && it.title == title }
            assertEquals("$title lesson count", count, module.lessons.size)
            assertEquals("$title lesson titles must be unique", count, module.lessons.map { it.title }.distinct().size)
            assertTrue("$title needs a chapter icon", module.icon != "•")
            module.lessons.forEach { lesson ->
                val authored = seniorSecondaryClassElevenContent["$title|${lesson.title}"]
                assertTrue("$title/${lesson.title} needs authored content", authored != null)
                assertTrue(lesson.concept.startsWith(authored?.summary.orEmpty()))
                assertTrue(lesson.theory.length >= 650)
                assertTrue(lesson.workedExample.contains(authored?.workedExample.orEmpty()))
                assertTrue(lesson.formula.isNotBlank())
                assertTrue(lesson.advancedConcepts.size >= 3)
            }
        }
        assertEquals(52, expected.values.sum())
        assertEquals(52, seniorSecondaryClassElevenContent.size)
    }

    @Test
    fun chaptersExposeRequiredBoardSpecificIdeas() {
        val titles = expected.keys.flatMap { moduleTitle ->
            curriculum.single { it.title == moduleTitle && it.level == "Class 11–12" }.lessons.map { it.title }
        }
        listOf(
            "Nomenclature of elements above atomic number 100",
            "Molecular-orbital theory",
            "Electrophilic substitution in benzene",
            "Qualitative detection of nitrogen sulfur and halogens",
            "van der Waals equation",
            "Anomalous behaviour of lithium and beryllium",
            "Oxidation states and inert-pair effect"
        ).forEach { required -> assertTrue("Missing $required", required in titles) }
    }
}
