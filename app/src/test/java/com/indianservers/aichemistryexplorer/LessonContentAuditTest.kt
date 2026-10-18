package com.indianservers.aichemistryexplorer

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import java.io.File

class LessonContentAuditTest {
    @Test fun exportEveryLessonForEditorialAudit() {
        val sources = levelOneConceptContent + levelTwoConceptContent + levelThreeConceptContent +
            levelFourFiveConceptContent + seniorSecondaryBoardContent + seniorSecondaryClassElevenContent +
            classTenBoardContent + classNineBoardContent + academicEditorialSources
        val sourcesJson = JSONObject()
        sources.forEach { (key, content) -> sourcesJson.put(key, JSONObject()
            .put("summary", content.summary).put("explanation", content.explanation).put("example", content.workedExample)
            .put("relationship", content.keyRelationship).put("mistake", content.commonMistake)
            .put("application", content.application).put("connections", JSONArray(content.connections))) }
        File("build/reports/lesson-content").mkdirs()
        File("build/reports/lesson-content/sources.json").writeText(sourcesJson.toString(2))
        val rows = curriculum.flatMap { module -> module.lessons.map { lesson ->
            val brief = schoolBriefFor(lesson)
            val key = "${module.title}|${lesson.title}"
            val authored = seniorSecondaryBoardContent[key] ?: seniorSecondaryClassElevenContent[key]
                ?: classTenBoardContent[key] ?: classNineBoardContent[key]
                ?: levelFourFiveConceptContent["${module.level}|$key"]
                ?: (if (module.level == "Class 6–8") levelOneConceptContent[lesson.title] else null)
                ?: (if (module.level == "Class 9–10") levelTwoConceptContent[lesson.title] else null)
                ?: levelThreeConceptContent[key]
            JSONObject().put("authored", authored?.let {
                JSONObject().put("summary", it.summary).put("explanation", it.explanation).put("example", it.workedExample)
                    .put("relationship", it.keyRelationship).put("mistake", it.commonMistake).put("application", it.application)
                    .put("connections", JSONArray(it.connections))
            } ?: JSONObject.NULL).put("level", module.level).put("module", module.title).put("category", module.category)
                .put("title", lesson.title).put("summary", lesson.concept).put("theory", lesson.theory)
                .put("formula", lesson.formula).put("example", lesson.workedExample)
                .put("steps", JSONArray(lesson.exampleSteps)).put("mistake", lesson.commonMistake)
                .put("application", lesson.realWorldApplication).put("connections", JSONArray(lesson.advancedConcepts))
                .put("hook", brief.hook).put("briefExample", brief.schoolExample).put("briefPrediction", brief.prediction)
                .put("question", lesson.question).put("answers", JSONArray(lesson.answers)).put("correct", lesson.correct)
        } }
        File("build/reports/lesson-content").mkdirs()
        File("build/reports/lesson-content/catalog.json").writeText(JSONArray(rows).toString(2))
    }
}
