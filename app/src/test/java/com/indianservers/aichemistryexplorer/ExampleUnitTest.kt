package com.indianservers.aichemistryexplorer

import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun everyConceptHasLearningContent() {
        val lessons = curriculum.flatMap { it.lessons }

        assertEquals(365, lessons.size)
        assertTrue(lessons.all { it.theory.length >= 400 })
        assertTrue(lessons.all { it.workedExample.length >= 80 })
        assertTrue(lessons.all { it.exampleSteps.size >= 4 })
        assertTrue(lessons.all { it.exampleSteps.all { step -> step.length >= 30 } })
        assertTrue(lessons.all { it.commonMistake.length >= 60 })
        assertTrue(lessons.all { it.realWorldApplication.length >= 60 })
        assertTrue(lessons.all { it.advancedConcepts.size >= 3 })
        assertTrue(lessons.none { it.title.contains("Thermodynamics", ignoreCase = true) })
    }

    @Test
    fun curriculumCoversEveryLearningLevel() {
        val expected = setOf("Class 6–8", "Class 9–10", "Class 11–12", "Undergraduate", "Postgraduate")
        assertEquals(expected, curriculum.map { it.level }.toSet())
        assertEquals(curriculum.size, curriculum.map { it.title }.distinct().size)
    }
}
