package com.indianservers.aichemistryexplorer

/** Explicit editorial content shared by lesson pages, examples, revision and assessment. */
internal data class EditorialLessonRecord(
    val key: String,
    val provenance: String,
    val summary: String,
    val explanation: String,
    val example: String,
    val relationship: String,
    val correction: String,
    val application: String,
    val steps: List<String>,
    val connections: List<String>
)

internal fun editorialRecordFor(module: CurriculumModule, lesson: CurriculumLesson): EditorialLessonRecord =
    editorialLessonCatalog.getValue("${module.level}|${module.title}|${lesson.title}")

internal fun CurriculumModule.withEditorialLessons(): CurriculumModule = copy(lessons = lessons.map { original ->
    val record = editorialRecordFor(this, original)
    val alternatives = editorialLessonCatalog.values.filter {
        it.key.substringBeforeLast('|') == record.key.substringBeforeLast('|') && it.summary != record.summary
    }.plus(editorialLessonCatalog.values.filter { it.summary != record.summary })
    val correct = (record.key.hashCode().ushr(1) % 4)
    val options = alternatives.map { it.summary }.distinct().take(3).toMutableList().apply { add(correct, record.summary) }
    val originalQuestionIsSpecific = !original.question.startsWith("Which approach best demonstrates")
    original.copy(
        concept = record.summary,
        formula = record.relationship,
        theory = listOf(record.summary, record.explanation, "Worked case: ${record.example}",
            "Interpretation and limits: ${record.correction}", "Application: ${record.application}")
            .distinct().joinToString("\n\n"),
        workedExample = record.example,
        exampleSteps = record.steps,
        commonMistake = record.correction,
        realWorldApplication = record.application,
        advancedConcepts = record.connections,
        question = if (originalQuestionIsSpecific) original.question else "Which statement describes ${original.title} in $title?",
        answers = if (originalQuestionIsSpecific) original.answers else options,
        correct = if (originalQuestionIsSpecific) original.correct else correct
    )
})

internal fun lessonSpecificBrief(lesson: CurriculumLesson): SchoolLessonBrief {
    val paragraphs = lesson.theory.split("\n\n").filter { it.isNotBlank() }
    return SchoolLessonBrief(
        discoverTitle = lesson.title,
        hook = lesson.workedExample,
        whyItMatters = lesson.realWorldApplication,
        dailyLink = lesson.realWorldApplication,
        simpleMeaning = lesson.concept,
        analogy = paragraphs.getOrElse(1) { lesson.concept },
        keyWords = lesson.advancedConcepts.joinToString("; "),
        memoryTrick = lesson.formula,
        homeExample = lesson.realWorldApplication,
        schoolExample = lesson.workedExample,
        natureExample = paragraphs.getOrElse(1) { lesson.concept },
        activity = lesson.exampleSteps.mapIndexed { i, step -> "${i + 1}. $step" }.joinToString("\n"),
        prediction = lesson.question,
        reason = lesson.answers[lesson.correct] + " " + lesson.commonMistake,
        misconception = lesson.commonMistake,
        factOne = lesson.concept,
        factTwo = lesson.formula,
        factThree = lesson.workedExample,
        examAnswer = lesson.concept + " " + lesson.formula,
        takeaway = lesson.commonMistake
    )
}
