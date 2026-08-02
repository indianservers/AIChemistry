package com.indianservers.aichemistryexplorer

internal data class KnowledgeCheck(
    val question: String,
    val options: List<String>,
    val correct: Int,
    val explanation: String
)

internal data class ModuleFaq(val question: String, val answer: String)

private fun knowledgeQuestion(
    question: String,
    correctAnswer: String,
    distractors: List<String>,
    correctPosition: Int,
    explanation: String
): KnowledgeCheck {
    val position = correctPosition.coerceIn(0, 3)
    val options = distractors.take(3).toMutableList()
    while (options.size < 3) options += "This does not follow from the lesson."
    options.add(position, correctAnswer)
    return KnowledgeCheck(question, options, position, explanation)
}

/**
 * Every curriculum concept receives a deterministic 15-item bank. Questions
 * draw from that lesson's theory, relationship, worked example, misconception,
 * application and advanced extensions rather than using unrelated trivia.
 */
internal fun knowledgeChecksFor(module: CurriculumModule, lesson: CurriculumLesson): List<KnowledgeCheck> {
    val otherLessons = module.lessons.filterNot { it.title == lesson.title }.map { it.title }
    val unrelated = (otherLessons + listOf("Ignoring observations", "Changing definitions to fit an answer", "Using units interchangeably")).distinct()
    val firstStep = lesson.exampleSteps.firstOrNull() ?: "Identify the chemical quantities, evidence and assumptions involved."
    val advanced = lesson.advancedConcepts.firstOrNull() ?: "Connect the model with experimental evidence and its limits."
    return listOf(
        KnowledgeCheck(lesson.question, lesson.answers, lesson.correct, "This follows directly from the core concept: ${lesson.concept}"),
        knowledgeQuestion(
            "Which statement best summarizes ${lesson.title}?",
            lesson.concept,
            listOf("It is only a naming convention with no chemical meaning.", "It applies without conditions or assumptions.", "It replaces experimental evidence."),
            1,
            lesson.concept
        ),
        knowledgeQuestion(
            "Which key relationship belongs to ${lesson.title}?",
            lesson.formula,
            unrelated.take(3),
            2,
            "The lesson identifies ${lesson.formula} as its key relationship."
        ),
        knowledgeQuestion(
            "Which action is the best starting point for a ${lesson.title} problem?",
            firstStep,
            listOf("Guess the final numerical answer.", "Ignore units until the end.", "Choose a formula from an unrelated topic."),
            0,
            "A defensible solution begins by identifying the relevant quantities and chemical reasoning."
        ),
        knowledgeQuestion(
            "Which statement describes a common mistake in ${lesson.title}?",
            lesson.commonMistake,
            listOf("Checking assumptions and units.", "Relating the model to evidence.", "Explaining why each step is valid."),
            3,
            lesson.commonMistake
        ),
        knowledgeQuestion(
            "Why is ${lesson.title} useful outside an exercise?",
            lesson.realWorldApplication,
            listOf("It removes the need for measurements.", "It guarantees every reaction gives one product.", "It makes chemical hazards irrelevant."),
            1,
            lesson.realWorldApplication
        ),
        knowledgeQuestion(
            "Which extension is genuinely connected to ${lesson.title}?",
            advanced,
            unrelated.takeLast(3),
            2,
            advanced
        ),
        knowledgeQuestion(
            "What makes an explanation of ${lesson.title} scientifically strong?",
            "It connects the model with observations, states its assumptions and uses consistent units.",
            listOf("It relies on memorized words alone.", "It ignores exceptions.", "It changes the chemical equation to match the answer."),
            0,
            "Scientific explanations connect models to evidence and make their limits explicit."
        ),
        knowledgeQuestion(
            "Before applying “${lesson.formula}”, what should be checked?",
            "The meaning and units of every quantity and the conditions under which the relationship applies.",
            listOf("Only whether the symbols look familiar.", "Whether the final answer is large.", "Whether all substances have the same color."),
            3,
            "Equations are reliable only when variables, units and assumptions are handled consistently."
        ),
        knowledgeQuestion(
            "Which response shows understanding rather than memorization?",
            "Predicting an outcome from ${lesson.title} and explaining the particle-level reason.",
            listOf("Repeating the title without explanation.", "Selecting an answer by word length.", "Ignoring contradictory evidence."),
            1,
            "Transfer and explanation demonstrate understanding."
        ),
        knowledgeQuestion(
            "How should a model used in ${lesson.title} be treated?",
            "As a useful representation whose predictions and limits must be checked against evidence.",
            listOf("As a literal picture valid at every scale.", "As proof that experiments are unnecessary.", "As an equation that can never have exceptions."),
            2,
            "Chemical models are purposeful representations, not unrestricted literal descriptions."
        ),
        knowledgeQuestion(
            "Which practice best reduces errors in a worked example?",
            "Show each reasoning step, preserve units and check whether the result is chemically plausible.",
            listOf("Round every value before calculating.", "Skip balanced equations.", "Use the same sign for every charge."),
            0,
            "Transparent steps and a plausibility check catch both mathematical and chemical errors."
        ),
        knowledgeQuestion(
            "If evidence disagrees with a prediction from ${lesson.title}, what should happen?",
            "Recheck the data and assumptions, then refine or replace the model if the disagreement remains.",
            listOf("Discard the evidence immediately.", "Change the recorded result.", "Assume the prediction is always exact."),
            3,
            "Evidence tests models; persistent disagreement requires investigation."
        ),
        knowledgeQuestion(
            "Which statement correctly links ${lesson.title} to the wider module ${module.title}?",
            "${lesson.title} supplies one part of the evidence-and-model framework used throughout ${module.title}.",
            listOf("It is unrelated to every other lesson.", "It makes the remaining module unnecessary.", "It applies only to the spelling of chemical names."),
            1,
            "Concepts in a module reinforce one another rather than operating as isolated facts."
        ),
        knowledgeQuestion(
            "What is the best final check after solving a ${lesson.title} question?",
            "Confirm conservation laws, units, sign, magnitude and consistency with the stated chemical conditions.",
            listOf("Check only the number of decimal places.", "Replace the answer with the nearest option.", "Remove any result that contains a unit."),
            2,
            "A complete check covers both mathematical consistency and chemical plausibility."
        )
    )
}

/** Creates at least 20 module-specific, collapsible FAQ entries. */
internal fun moduleFaqs(module: CurriculumModule): List<ModuleFaq> {
    require(module.lessons.isNotEmpty())
    return List(20) { index ->
        val lesson = module.lessons[index % module.lessons.size]
        when ((index / module.lessons.size) % 5) {
            0 -> ModuleFaq("What is ${lesson.title}?", lesson.concept)
            1 -> ModuleFaq("What is the key relationship for ${lesson.title}?", "${lesson.formula}. ${formulaMeaningForFaq(module.title)}")
            2 -> ModuleFaq("How is ${lesson.title} used in practice?", lesson.realWorldApplication)
            3 -> ModuleFaq("What mistake should I avoid in ${lesson.title}?", lesson.commonMistake)
            else -> ModuleFaq(
                "How should I study ${lesson.title} effectively?",
                "Explain the concept in your own words, reproduce the worked example, complete all 15 knowledge-check questions, and revisit any answer whose reasoning is unclear."
            )
        }
    }
}

private fun formulaMeaningForFaq(moduleTitle: String): String = when (moduleTitle) {
    "Atomic Structure" -> "Interpret every symbol using the stated atomic or quantum model."
    "Periodic Table" -> "Use the relationship as a trend or organizing rule while remembering documented exceptions."
    "Chemical Bonding" -> "Connect electron arrangement to geometry, energy and measurable properties."
    "States of Matter" -> "Keep temperature, pressure and phase conditions explicit."
    "Chemical Reactions" -> "Balance atoms and charge before drawing mechanistic conclusions."
    "Equilibrium and Solutions" -> "Distinguish equilibrium composition from the rates of forward and reverse change."
    "Kinetics and Electrochemistry" -> "Track sign conventions, units and the direction of electron transfer."
    else -> "Use consistent units, identify assumptions and compare the result with experimental evidence."
}
