package com.indianservers.aichemistryexplorer

import org.junit.Assert.*
import org.junit.Test

class LessonSpecificContentTest {
    private val genericPhrases = listOf(
        "This lesson begins with a simple observation from daily life",
        "Use a chart, safe demo or simple measurement to connect the idea with evidence",
        "If one condition changes, what visible change do you expect",
        "Worked example focus:", "Key check: after solving", "is a core part of",
        "It connects particle-level structure and chemical interactions with observable evidence",
        "This illustrates the key relationship; explain the particles",
        "Fix it by writing the claim in your own words", "Only memorise the heading",
        "What makes an explanation", "Which practice best reduces errors",
        "Connecting evidence, mechanism and application"
    )

    @Test fun everyRuntimeLessonHasAnExplicitRecordAndNoGenericContentFallback() {
        val keys = mutableSetOf<String>()
        curriculum.forEach { module -> module.lessons.forEach { lesson ->
            val key = "${module.level}|${module.title}|${lesson.title}"
            assertTrue(key, keys.add(key))
            val record = editorialRecordFor(module, lesson)
            assertEquals(key, record.key)
            assertTrue(record.provenance in setOf("new case", "existing authored record", "explicit topic alias"))
            assertEquals(record.example, lesson.workedExample)
            assertEquals(record.relationship, lesson.formula)
            assertEquals(record.correction, lesson.commonMistake)
            val brief = schoolBriefFor(lesson)
            assertEquals(lesson.workedExample, brief.hook)
            assertEquals(lesson.workedExample, brief.schoolExample)
            assertEquals(lesson.question, brief.prediction)
            val displayed = listOf(lesson.concept, lesson.theory, lesson.workedExample,
                lesson.exampleSteps.joinToString(" "), lesson.commonMistake, lesson.realWorldApplication,
                brief.hook, brief.schoolExample, brief.prediction,
                knowledgeChecksFor(module, lesson).joinToString(" ") { it.question + it.options.joinToString() })
            displayed.forEach { text -> genericPhrases.forEach { phrase ->
                assertFalse("$key still contains generic phrase: $phrase", text.contains(phrase, true))
            } }
        } }
        assertEquals(806, keys.size)
        assertEquals(keys, editorialLessonCatalog.keys)
    }

    @Test fun previouslyUnrelatedExamplesNowUseTheActualLessonChemistry() {
        val cases = mapOf(
            "Reactivity of metals" to listOf("zinc", "copper"),
            "Recycling metals" to listOf("aluminium", "steel"),
            "Quantum yield" to listOf("photons", "0.25"),
            "Water cycle and purification" to listOf("filter", "salt"),
            "Steady-state approximation" to listOf("k1", "k2"),
            "Photoelectric effect" to listOf("5.0 eV", "3.0 eV"),
            "Binding energy" to listOf("92", "7.67"),
            "Measurement uncertainty" to listOf("2.24%", "1%"),
            "Pharmacophore modelling" to listOf("scaffolds", "acceptor"),
            "Soil and nutrient analysis" to listOf("phosphorus", "extractable")
        )
        cases.forEach { (title, anchors) ->
            val lessons = curriculum.flatMap { it.lessons }.filter { it.title == title }
            assertTrue(title, lessons.isNotEmpty())
            lessons.forEach { lesson -> anchors.forEach { anchor ->
                assertTrue("$title needs its own example anchor $anchor", lesson.workedExample.contains(anchor, true))
            } }
        }
    }

    @Test fun allStoredSchoolPagesContainTheirOwnCurrentLessonContent() {
        val school = curriculum.filter { it.level.startsWith("Class") }
        val pages = school.flatMap { module -> module.lessons.flatMap { rewriteLessonPages(module, it) } }
        assertEquals(school.sumOf { it.lessons.size } * 5, pages.size)
        val audits = auditSchoolLessonPages(school, pages)
        assertTrue(audits.flatMap { audit -> audit.issues.map { "${audit.moduleTitle}/${audit.lessonTitle}: $it" } }.joinToString("\n"),
            audits.all { it.isReady })
    }

    @Test fun numericalCaseAnswersAreInternallyConsistent() {
        val examples = curriculum.flatMap { it.lessons }.associate { it.title to it.workedExample }
        val defect = examples.getValue("Mass defect")
        val mass = Regex("mass defect of ([0-9.]+) u").find(defect)!!.groupValues[1].toDouble()
        val energy = Regex("= ([0-9.]+) MeV").find(defect)!!.groupValues[1].toDouble()
        assertEquals(mass * 931.5, energy, .0001)
        val binding = examples.getValue("Binding energy")
        val division = Regex("([0-9.]+)/([0-9.]+) = ([0-9.]+) MeV").find(binding)!!.groupValues
        assertEquals(division[1].toDouble()/division[2].toDouble(), division[3].toDouble(), .005)
        val synthesis = examples.getValue("Multistep synthesis")
        val yields = Regex("([0-9.]+)%").findAll(synthesis).take(3).map { it.groupValues[1].toDouble()/100 }.toList()
        val reported = Regex("= ([0-9.]+)%").find(synthesis)!!.groupValues[1].toDouble()/100
        assertEquals(yields.reduce { a,b -> a*b }, reported, .0001)
    }
}
