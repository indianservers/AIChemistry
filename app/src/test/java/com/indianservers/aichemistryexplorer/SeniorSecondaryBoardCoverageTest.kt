package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeniorSecondaryBoardCoverageTest {
    private val expected = linkedMapOf(
        "Solutions: Senior Secondary" to listOf(
            "Ideal and non-ideal solutions",
            "Abnormal molar mass and van't Hoff factor",
            "Colligative-property numericals"
        ),
        "Electrochemistry" to listOf(
            "Electrolytic conductivity",
            "Molar conductivity and Kohlrausch law"
        ),
        "Chemical Kinetics" to listOf(
            "Integrated rate equations",
            "Half-life of reactions",
            "Collision theory"
        ),
        "d- and f-Block Chemistry" to listOf("Important compounds of transition elements"),
        "Coordination Compounds" to listOf("Bonding in metal carbonyls"),
        "Hydrocarbons and Halo Compounds" to listOf(
            "Preparation and reactions of alkanes",
            "Preparation and reactions of alkenes and alkynes",
            "Preparation and reactions of benzene",
            "Preparation and reactions of haloalkanes",
            "Preparation and reactions of haloarenes"
        ),
        "Oxygen and Nitrogen Compounds" to listOf(
            "Preparation and reactions of alcohols",
            "Preparation and reactions of phenols",
            "Preparation and reactions of ethers",
            "Preparation and reactions of aldehydes and ketones",
            "Preparation and reactions of carboxylic acids",
            "Preparation and reactions of amines",
            "Preparation and reactions of diazonium salts"
        ),
        "Biomolecules and Polymers" to listOf("Vitamins", "Hormones")
    )

    @Test
    fun missingBoardTopicsAreExplicitAuthoredLessons() {
        expected.forEach { (moduleTitle, lessonTitles) ->
            val module = curriculum.single { it.level == "Class 11–12" && it.title == moduleTitle }
            lessonTitles.forEach { lessonTitle ->
                val lesson = module.lessons.single { it.title == lessonTitle }
                assertTrue("$moduleTitle/$lessonTitle needs board content", lesson.theory.length >= 650)
                assertTrue("$moduleTitle/$lessonTitle needs a worked example", lesson.workedExample.startsWith("Worked example focus:"))
                assertTrue("$moduleTitle/$lessonTitle needs equations", lesson.formula.isNotBlank())
                assertTrue("$moduleTitle/$lessonTitle needs applications", lesson.realWorldApplication.length >= 100)
            }
        }
        assertEquals(24, expected.values.sumOf { it.size })
        assertEquals(24, seniorSecondaryBoardContent.size)
    }

    @Test
    fun quantitativeLessonsContainRequiredCalculationModels() {
        val formulas = expected.flatMap { (moduleTitle, lessonTitles) ->
            curriculum.single { it.level == "Class 11–12" && it.title == moduleTitle }
                .lessons.filter { it.title in lessonTitles }.map { it.formula }
        }.joinToString(" ")
        listOf("DeltaTf", "kappa", "Lambda_m", "Kohlrausch", "t1/2", "[A]t").forEach { symbol ->
            assertTrue("Missing calculation model: $symbol", symbol in formulas)
        }
    }
}
