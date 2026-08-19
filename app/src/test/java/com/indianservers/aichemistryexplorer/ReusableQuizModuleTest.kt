package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReusableQuizModuleTest {
    @Test
    fun reusableQuizBankHasRequiredCoverage() {
        val bank = reusableChemistryQuizBank()

        assertTrue(bank.size >= 600)
        assertTrue(bank.any { it.level == "Foundation" })
        assertTrue(bank.any { it.level == "Secondary" })
        assertTrue(bank.any { it.level == "Senior Secondary" })
        assertTrue(bank.any { it.category == "Nuclear Chemistry" })
        assertTrue(bank.map { it.classLabel }.toSet().containsAll(listOf("Class 6", "Class 10", "Class 11", "Class 12")))
    }

    @Test
    fun everyQuestionHasFourOptionsAndValidAnswer() {
        reusableChemistryQuizBank().forEach { question ->
            assertEquals(4, question.options.size)
            assertTrue(question.correctIndex in question.options.indices)
            assertTrue(question.question.length >= 12)
            assertTrue(question.explanation.length >= 40)
        }
    }
}
