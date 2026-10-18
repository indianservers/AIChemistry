package com.indianservers.aichemistryexplorer

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthoredAssessmentTest {
    private fun lesson(title: String) = CurriculumLesson(
        title = title,
        concept = "A focused concept explanation with particles, evidence and conditions.",
        formula = "model relationship",
        question = "Which statement follows from the concept?",
        answers = listOf("Correct", "Wrong one", "Wrong two", "Wrong three"),
        correct = 0,
        commonMistake = "Do not use a shortcut without checking the chemical entities and conserved quantities involved."
    )

    private fun module(title: String, lesson: CurriculumLesson) = CurriculumModule(
        title = title,
        icon = "T",
        color = Color.Cyan,
        description = "Test module",
        lessons = listOf(lesson),
        level = "Class 11–12"
    )

    @Test
    fun `equation lessons receive five authored diagnostic checks`() {
        val lesson = lesson("Balancing chemical equations")
        val checks = authoredMisconceptionChecksFor(module("Reaction equations", lesson), lesson)

        assertEquals(5, checks.size)
        assertTrue(checks.all { it.difficulty == "Diagnostic" })
        assertTrue(checks.all { it.options.size == 4 && it.optionFeedback.size == 4 })
        assertTrue(checks.any { "subscripts" in it.question.lowercase() || "subscripts" in it.explanation.lowercase() })
    }

    @Test
    fun `authored checks replace generic opening questions in school assessment`() {
        val lesson = lesson("Mole concept and molar mass")
        val checks = knowledgeChecksFor(module("Stoichiometry", lesson), lesson)

        assertEquals(15, checks.size)
        assertTrue(checks.take(5).all { it.skill in setOf("Misconception diagnosis", "Evidence discrimination", "Misconception repair") })
        assertFalse(checks.take(5).any { "which memory trick belongs" in it.question.lowercase() })
    }

    @Test
    fun `specific topic match wins over a short overlapping keyword`() {
        val lesson = lesson("Amino acids and proteins")
        val checks = authoredMisconceptionChecksFor(module("Biomolecules", lesson), lesson)

        assertTrue(checks.any { "enzyme" in it.question.lowercase() || "vitamin" in it.question.lowercase() })
        assertFalse(checks.any { "titration" in it.question.lowercase() })
    }

    @Test
    fun `major school chemistry domains have authored misconception profiles`() {
        val topics = listOf(
            "Balancing chemical equations", "Mole concept", "Gaseous state", "Chromatography",
            "Atomic structure", "Periodic trends", "Chemical bonding", "Colligative properties",
            "Acid-base titration", "Collision theory", "Electrochemistry", "Thermodynamics",
            "Organic nomenclature", "Metal extraction", "Vitamins and hormones", "Experimental design"
        )

        topics.forEach { title ->
            val lesson = lesson(title)
            assertTrue("No authored assessment for $title", authoredMisconceptionChecksFor(module(title, lesson), lesson).size >= 5)
        }
    }

    @Test
    fun `authored diagnostics cover most school curriculum lessons`() {
        val schoolLessons = curriculum.flatMap { module ->
            if (listOf("6", "7", "8", "9", "10", "11", "12").any(module.level::contains)) {
                module.lessons.map { module to it }
            } else emptyList()
        }
        val authored = schoolLessons.count { (module, lesson) -> authoredMisconceptionChecksFor(module, lesson).isNotEmpty() }

        assertTrue("Only $authored of ${schoolLessons.size} school lessons have authored diagnostics", authored * 2 >= schoolLessons.size)
    }
}
