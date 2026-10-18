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

        assertTrue(lessons.size >= 700)
        assertEquals(scalableCurriculum.size, lessons.size)
        val incomplete = lessons.flatMap { lesson ->
            listOfNotNull(
                "${lesson.title}: theory is too short".takeIf { lesson.theory.length < 400 },
                "${lesson.title}: worked example is too short".takeIf { lesson.workedExample.length < 80 },
                "${lesson.title}: needs four learning steps".takeIf { lesson.exampleSteps.size < 4 },
                "${lesson.title}: learning step is too short".takeIf { lesson.exampleSteps.any { step -> step.length < 30 } },
                "${lesson.title}: misconception is too short".takeIf { lesson.commonMistake.length < 60 },
                "${lesson.title}: application is too short".takeIf { lesson.realWorldApplication.length < 60 },
                "${lesson.title}: needs three connections".takeIf { lesson.advancedConcepts.size < 3 }
            )
        }
        assertTrue(incomplete.joinToString("\n"), incomplete.isEmpty())
        assertTrue(lessons.none { it.title.isBlank() })
    }

    @Test
    fun curriculumCoversEveryLearningLevel() {
        val expected = setOf("Class 6–8", "Class 9–10", "Class 11–12", "Undergraduate", "Postgraduate")
        assertEquals(expected, curriculum.map { it.level }.toSet())
        assertEquals(curriculum.size, curriculum.map { it.level to it.title }.distinct().size)
    }
}
