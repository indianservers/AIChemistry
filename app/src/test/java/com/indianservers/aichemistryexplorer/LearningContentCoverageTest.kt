package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningContentCoverageTest {

    @Test
    fun undergraduateChemistryHasTheCompleteLevelFourHierarchy() {
        val modules = curriculum.filter {
            it.level == "Undergraduate" && it.category == "Undergraduate Chemistry"
        }
        assertEquals(levelFourTopics.keys.toList(), modules.map { it.title })
        assertEquals(81, modules.sumOf { it.lessons.size })
        modules.forEach { module ->
            assertEquals(module.title, module.subcategory)
            assertEquals(levelFourTopics.getValue(module.title), module.lessons.map { it.title })
            assertEquals(levelFourFiveModuleIcons["Undergraduate|${module.title}"], module.icon)
            module.lessons.forEach { lesson ->
                val authored = levelFourFiveConceptContent["Undergraduate|${module.title}|${lesson.title}"]
                assertTrue("${module.title}/${lesson.title} needs authored content", authored != null)
                assertEquals(authored?.summary, lesson.concept)
                assertEquals(authored?.workedExample, lesson.workedExample)
                assertTrue(lesson.theory.length >= 400)
                assertTrue(lesson.workedExample.startsWith("Example:"))
                assertTrue(lesson.commonMistake.length >= 60)
                assertTrue(lesson.realWorldApplication.length >= 60)
            }
        }
    }

    @Test
    fun postgraduateChemistryHasTheCompleteLevelFiveHierarchy() {
        val modules = curriculum.filter {
            it.level == "Postgraduate" && it.category == "Postgraduate Chemistry"
        }
        assertEquals(levelFiveTopics.keys.toList(), modules.map { it.title })
        assertEquals(69, modules.sumOf { it.lessons.size })
        modules.forEach { module ->
            assertEquals(module.title, module.subcategory)
            assertEquals(levelFiveTopics.getValue(module.title), module.lessons.map { it.title })
            assertEquals(levelFourFiveModuleIcons["Postgraduate|${module.title}"], module.icon)
            module.lessons.forEach { lesson ->
                val authored = levelFourFiveConceptContent["Postgraduate|${module.title}|${lesson.title}"]
                assertTrue("${module.title}/${lesson.title} needs authored content", authored != null)
                assertEquals(authored?.summary, lesson.concept)
                assertEquals(authored?.workedExample, lesson.workedExample)
                assertTrue(lesson.theory.length >= 400)
                assertTrue(lesson.workedExample.startsWith("Example:"))
                assertTrue(lesson.commonMistake.length >= 60)
                assertTrue(lesson.realWorldApplication.length >= 60)
            }
        }
    }

    @Test
    fun higherSecondaryPhysicalChemistryHasTheCompleteLevelThreeHierarchy() {
        val expected = linkedMapOf(
            "Thermodynamics" to listOf(
                "System", "Surroundings", "Work", "Heat", "Internal energy", "Enthalpy", "Entropy",
                "Gibbs free energy", "Hess Law", "Spontaneity"
            ),
            "Thermochemistry" to listOf(
                "Heat capacity", "Specific heat", "Calorimetry", "Bond enthalpy", "Lattice energy", "Born-Haber cycle"
            ),
            "Chemical Equilibrium" to listOf(
                "Dynamic equilibrium", "Equilibrium constant", "Le Chatelier principle", "Ionic equilibrium", "Buffer systems"
            ),
            "Chemical Kinetics" to listOf(
                "Rate laws", "Order", "Molecularity", "Arrhenius equation", "Activation energy", "Catalysis"
            ),
            "Electrochemistry" to listOf(
                "Redox", "Galvanic cells", "Electrolysis", "Nernst equation", "EMF", "Batteries", "Fuel cells", "Corrosion"
            ),
            "Surface Chemistry" to listOf(
                "Adsorption", "Catalysis", "Colloids", "Emulsions", "Micelles"
            ),
            "Solid State" to listOf(
                "Crystal systems", "Unit cell", "Packing", "Defects", "Semiconductors"
            ),
            "Nuclear Chemistry" to listOf(
                "Radioactivity", "Alpha decay", "Beta decay", "Gamma rays", "Half-life", "Nuclear fission", "Nuclear fusion"
            )
        )

        val modules = curriculum.filter {
            it.level == "Class 11–12" && it.category == "Physical Chemistry" && it.title in expected
        }
        assertEquals(expected.keys.toList(), modules.map { it.title })
        assertEquals(52, modules.sumOf { it.lessons.size })
        modules.forEach { module ->
            assertEquals(module.title, module.subcategory)
            assertEquals(expected.getValue(module.title), module.lessons.map { it.title })
            assertTrue(module.icon in levelThreeModuleIcons.values)
            module.lessons.forEach { lesson ->
                val authored = levelThreeConceptContent["${module.title}|${lesson.title}"]
                assertTrue("${module.title}/${lesson.title} needs authored content", authored != null)
                assertEquals(authored?.summary, lesson.concept)
                assertEquals(authored?.workedExample, lesson.workedExample)
                assertTrue(lesson.theory.length >= 400)
                assertTrue(lesson.workedExample.startsWith("Example:"))
                assertTrue(lesson.commonMistake.isNotBlank())
                assertTrue(lesson.realWorldApplication.isNotBlank())
            }
        }
    }

    @Test
    fun highSchoolChemistryHasTheCompleteLevelTwoHierarchy() {
        val expected = linkedMapOf(
            "Atomic Structure" to listOf(
                "Quantum numbers", "Orbitals", "Electron configuration", "Aufbau", "Hund rule", "Pauli principle"
            ),
            "Chemical Bonding" to listOf(
                "Ionic bond", "Covalent bond", "Coordinate bond", "Metallic bond", "Hydrogen bond",
                "Van der Waals forces", "Lewis structures", "VSEPR", "Hybridization", "Molecular geometry"
            ),
            "Mole Concept" to listOf(
                "Avogadro number", "Moles", "Stoichiometry", "Empirical formula", "Molecular formula",
                "Limiting reagent", "Percentage yield"
            ),
            "States of Matter" to listOf(
                "Gas laws", "Ideal gas equation", "Kinetic theory", "Liquids", "Surface tension", "Viscosity",
                "Vapour pressure", "Solids", "Crystal lattice"
            ),
            "Solutions" to listOf(
                "Types", "Solubility", "Concentration", "Molarity", "Molality", "Normality", "Mole fraction",
                "ppm", "Dilution"
            ),
            "Acids Bases Salts" to listOf(
                "Arrhenius", "Bronsted", "Lewis", "pH", "Buffers", "Indicators", "Hydrolysis", "Salt chemistry"
            )
        )

        val modules = curriculum.filter {
            it.level == "Class 9–10" && it.category == "High School Chemistry"
        }
        assertEquals(expected.keys.toList(), modules.map { it.title })
        modules.forEach { module ->
            assertEquals(module.title, module.subcategory)
            assertEquals(expected.getValue(module.title), module.lessons.map { it.title })
            assertTrue(module.icon in levelTwoModuleIcons.values)
            module.lessons.forEach { lesson ->
                val authored = levelTwoConceptContent[lesson.title]
                assertTrue("${module.title}/${lesson.title} needs authored content", authored != null)
                assertEquals(authored?.summary, lesson.concept)
                assertEquals(authored?.workedExample, lesson.workedExample)
                assertTrue(lesson.theory.length >= 400)
                assertTrue(lesson.workedExample.startsWith("Example:"))
                assertTrue(lesson.commonMistake.isNotBlank())
                assertTrue(lesson.realWorldApplication.isNotBlank())
            }
        }
    }

    @Test
    fun introductionToChemistryHasTheCompleteLevelOneHierarchy() {
        val expected = linkedMapOf(
            "Matter Around Us" to listOf(
                "What is matter?", "States of matter", "Properties of solids", "Liquids", "Gases", "Plasma",
                "Bose-Einstein Condensate", "Physical changes", "Chemical changes", "Classification of matter",
                "Mixtures", "Pure substances"
            ),
            "Measurement in Chemistry" to listOf(
                "SI Units", "Derived Units", "Scientific notation", "Significant figures", "Precision", "Accuracy",
                "Errors", "Density", "Temperature scales", "Laboratory measurements"
            ),
            "Atoms" to listOf(
                "Discovery of atom", "Dalton theory", "Thomson model", "Rutherford model", "Bohr model",
                "Modern atomic model", "Subatomic particles", "Atomic number", "Mass number", "Isotopes",
                "Isobars", "Isotones", "Isoelectronic species"
            ),
            "Molecules" to listOf(
                "Elements", "Compounds", "Molecules", "Atomicity", "Molecular mass", "Formula mass", "Valency",
                "Chemical formula"
            ),
            "Periodic Table" to listOf(
                "History", "Mendeleev table", "Modern periodic table", "Groups", "Periods", "Metals",
                "Nonmetals", "Metalloids", "Periodic trends", "Electronic configuration basics"
            ),
            "Chemical Reactions" to listOf(
                "Combination", "Decomposition", "Displacement", "Double displacement", "Neutralization",
                "Combustion", "Oxidation", "Reduction", "Redox basics"
            )
        )

        val modules = curriculum.filter {
            it.level == "Class 6–8" && it.category == "Introduction to Chemistry"
        }
        assertEquals(expected.keys.toList(), modules.map { it.title })
        modules.forEach { module ->
            assertEquals(module.title, module.subcategory)
            assertEquals(expected.getValue(module.title), module.lessons.map { it.title })
            assertTrue(module.icon in levelOneModuleIcons.values)
            module.lessons.forEach { lesson ->
                val authored = levelOneConceptContent[lesson.title]
                assertTrue("${module.title}/${lesson.title} needs authored content", authored != null)
                assertEquals(authored?.summary, lesson.concept)
                assertEquals(authored?.workedExample, lesson.workedExample)
                assertTrue(lesson.theory.length >= 220)
                assertTrue(lesson.workedExample.startsWith("Example:"))
                assertTrue(lesson.commonMistake.isNotBlank())
                assertTrue(lesson.realWorldApplication.isNotBlank())
            }
        }
    }

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
