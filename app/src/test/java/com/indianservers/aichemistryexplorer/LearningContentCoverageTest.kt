package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningContentCoverageTest {

    @Test
    fun everyConceptHasFifteenValidKnowledgeChecks() {
        curriculum.forEach { module ->
            module.lessons.forEach { lesson ->
                val checks = knowledgeChecksFor(module, lesson)
                assertEquals("${module.title}/${lesson.title}", 15, checks.size)
                assertEquals(15, checks.map { it.question }.distinct().size)
                checks.forEach { check ->
                    assertEquals(4, check.options.size)
                    assertTrue(check.correct in check.options.indices)
                    assertTrue(check.explanation.isNotBlank())
                }
            }
        }
    }

    @Test
    fun everyModuleHasTwentyCollapsibleFaqEntries() {
        curriculum.forEach { module ->
            val faqs = moduleFaqs(module)
            assertEquals(module.title, 20, faqs.size)
            assertEquals(20, faqs.map { it.question }.distinct().size)
            assertTrue(faqs.all { it.answer.isNotBlank() })
        }
    }

    @Test
    fun moleculeAndReactionLibrariesHaveUsefulChapterCoverage() {
        assertTrue(moleculeLibrary.size >= 75)
        assertTrue(moleculeLibrary.map { it.chapter }.distinct().size >= 10)
        assertTrue(moleculeLibrary.all {
            it.name.isNotBlank() && it.formula.isNotBlank() && it.learningPurpose.isNotBlank() &&
                it.theory.isNotBlank() && it.exampleChemistry.isNotBlank() && it.uses.isNotBlank()
        })
        assertEquals(moleculeLibrary.size, moleculeLibrary.map { it.name }.distinct().size)

        assertTrue(reactionLibrary.size >= 30)
        assertTrue(reactionLibrary.map { it.chapter }.distinct().size >= 8)
        assertTrue(reactionLibrary.all {
            ("→" in it.equation || "⇌" in it.equation) &&
                it.conditions.isNotBlank() && it.purpose.isNotBlank() && it.explanation.isNotBlank()
        })
    }

    @Test
    fun readingPreferencesProvideOriginalPlusEightThemes() {
        assertEquals(9, readingPalettes.size)
        assertEquals("Original", readingPalettes.first().name)
        assertEquals(9, readingPalettes.map { it.name }.distinct().size)
    }
}
