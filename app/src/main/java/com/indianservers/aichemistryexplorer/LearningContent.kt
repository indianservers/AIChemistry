package com.indianservers.aichemistryexplorer

internal data class KnowledgeCheck(
    val question: String,
    val options: List<String>,
    val correct: Int,
    val explanation: String,
    val hint: String = "",
    val skill: String = "Concept transfer",
    val difficulty: String = "Core",
    val reviewPage: String = "Understand",
    val optionFeedback: List<String> = emptyList()
)

internal data class ModuleFaq(val question: String, val answer: String)

/** Questions use this lesson's chemical content; no generic study-habit or unsafe-activity distractors. */
internal fun knowledgeChecksFor(module: CurriculumModule, lesson: CurriculumLesson): List<KnowledgeCheck> {
    val record = editorialLessonCatalog["${module.level}|${module.title}|${lesson.title}"]
    val fields = linkedMapOf(
        "explanation" to lesson.concept,
        "relationship" to lesson.formula,
        "worked case" to lesson.workedExample,
        "interpretation" to lesson.commonMistake,
        "application" to lesson.realWorldApplication
    )
    lesson.exampleSteps.forEachIndexed { index, text -> fields["case step ${index + 1}"] = text }
    record?.explanation?.split(Regex("(?<=[.!?])\\s+(?=[A-Z])"))?.filter { it.length >= 35 }
        ?.forEachIndexed { index, text -> fields["chemical detail ${index + 1}"] = text }
    val otherRecords = editorialLessonCatalog.values.filter {
        it.key.substringBeforeLast('|') == "${module.level}|${module.title}" && it.key != record?.key
    } + editorialLessonCatalog.values.filter { it.key != record?.key }
    val usedAnswers = mutableSetOf<String>()
    val targeted = fields.mapNotNull { (field, answer) ->
        if (answer.isBlank() || !usedAnswers.add(answer)) return@mapNotNull null
        val alternatives = otherRecords.map { other ->
            val value = when (field) {
                "relationship" -> other.relationship
                "worked case" -> other.example
                "interpretation" -> other.correction
                "application" -> other.application
                else -> other.summary
            }
            other to value
        }.filter { (_, value) -> value != answer && value.isNotBlank() }.distinctBy { it.second }.take(3)
        require(alternatives.size == 3) { "Insufficient distinct chemical distractors for ${lesson.title}" }
        val position = ((lesson.title + field).hashCode().ushr(1)) % 4
        val options = alternatives.map { it.second }.toMutableList().apply { add(position, answer) }
        val feedback = alternatives.map { (other, _) -> "This describes ${other.key.substringAfterLast('|')}: ${other.summary}" }
            .toMutableList().apply { add(position, answer) }
        KnowledgeCheck(
            question = "${lesson.title}: which $field matches the stated chemistry?",
            options = options, correct = position,
            explanation = "$answer\n${lesson.formula}", hint = lesson.formula,
            skill = when (field) { "worked case" -> "Worked example"; "interpretation" -> "Misconception check"; else -> "Lesson content" },
            reviewPage = when (field) { "worked case" -> "Observe"; "interpretation" -> "Think"; else -> "Understand" },
            optionFeedback = feedback
        )
    }
    val first = KnowledgeCheck(lesson.question, lesson.answers, lesson.correct,
        "${lesson.answers[lesson.correct]}\n${record?.explanation ?: lesson.concept}", hint = lesson.formula,
        skill = "Core concept", optionFeedback = lesson.answers.mapIndexed { index, option ->
            if (index == lesson.correct) lesson.concept else "$option does not answer this ${lesson.title} question. ${lesson.concept}"
        })
    return (listOf(first) + targeted).distinctBy { it.question }.take(15)
}

/** FAQ answers refer to the actual lessons, rather than category-wide advice. */
internal fun moduleFaqs(module: CurriculumModule): List<ModuleFaq> = List(minOf(20, module.lessons.size * 7)) { index ->
    val lesson = module.lessons[index % module.lessons.size]
    when (index / module.lessons.size % 7) {
        0 -> ModuleFaq("What does ${lesson.title} explain?", lesson.concept)
        1 -> ModuleFaq("Which worked case illustrates ${lesson.title}?", lesson.workedExample)
        2 -> ModuleFaq("Which relationship is used in ${lesson.title}?", lesson.formula)
        3 -> ModuleFaq("What interpretation needs care in ${lesson.title}?", lesson.commonMistake)
        4 -> ModuleFaq("Where is ${lesson.title} applied?", lesson.realWorldApplication)
        5 -> ModuleFaq("How is the ${lesson.title} case worked through?", lesson.exampleSteps.joinToString("\n"))
        else -> ModuleFaq("Which details support ${lesson.title}?", lesson.theory)
    }
}
