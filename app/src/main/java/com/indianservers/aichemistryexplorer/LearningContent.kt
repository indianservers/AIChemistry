package com.indianservers.aichemistryexplorer

internal data class KnowledgeCheck(
    val question: String,
    val options: List<String>,
    val correct: Int,
    val explanation: String,
    val hint: String = "Eliminate choices that ignore evidence, units or the lesson's stated model.",
    val skill: String = "Concept transfer",
    val difficulty: String = "Core",
    val reviewPage: String = "Understand",
    val optionFeedback: List<String> = emptyList()
)

internal data class ModuleFaq(val question: String, val answer: String)

private fun knowledgeQuestion(
    question: String,
    correctAnswer: String,
    distractors: List<String>,
    correctPosition: Int,
    explanation: String,
    hint: String = "Look for the option that connects the claim to evidence and conditions.",
    skill: String = "Concept transfer",
    difficulty: String = "Core",
    reviewPage: String = "Understand"
): KnowledgeCheck {
    val position = correctPosition.coerceIn(0, 3)
    val options = distractors.take(3).toMutableList()
    while (options.size < 3) options += "This does not follow from the lesson."
    options.add(position, correctAnswer)
    return KnowledgeCheck(question, options, position, explanation, hint, skill, difficulty, reviewPage)
}

/**
 * Every curriculum concept receives a deterministic 30-item bank. Questions
 * draw from that lesson's theory, relationship, worked example, misconception,
 * application and advanced extensions rather than using unrelated trivia.
 */
internal fun knowledgeChecksFor(module: CurriculumModule, lesson: CurriculumLesson): List<KnowledgeCheck> {
    if (module.level.contains("6") || module.level.contains("7") || module.level.contains("8") ||
        module.level.contains("9") || module.level.contains("10") ||
        module.level.contains("11") || module.level.contains("12")
    ) {
        return schoolPhaseKnowledgeChecksFor(module, lesson)
    }
    val otherLessons = module.lessons.filterNot { it.title == lesson.title }.map { it.title }
    val unrelated = (otherLessons + listOf("Ignoring observations", "Changing definitions to fit an answer", "Using units interchangeably")).distinct()
    val firstStep = lesson.exampleSteps.firstOrNull() ?: "Identify the chemical quantities, evidence and assumptions involved."
    val advanced = lesson.advancedConcepts.firstOrNull() ?: "Connect the model with experimental evidence and its limits."
    return listOf(
        KnowledgeCheck(
            lesson.question,
            lesson.answers,
            lesson.correct,
            "This follows directly from the core concept: ${lesson.concept}",
            hint = "Return to the concept statement and identify the particle, quantity or relationship it names.",
            skill = "Recall",
            difficulty = "Core"
        ),
        knowledgeQuestion(
            "Which statement best summarizes ${lesson.title}?",
            lesson.concept,
            listOf("It is only a naming convention with no chemical meaning.", "It applies without conditions or assumptions.", "It replaces experimental evidence."),
            1,
            lesson.concept,
            hint = "A summary should preserve the lesson's main cause-and-effect idea.",
            skill = "Concept summary"
        ),
        knowledgeQuestion(
            "Which key relationship belongs to ${lesson.title}?",
            lesson.formula,
            unrelated.take(3),
            2,
            "The lesson identifies ${lesson.formula} as its key relationship.",
            hint = "Formula questions reward recognizing symbols and units, not just matching familiar words.",
            skill = "Formula recall"
        ),
        knowledgeQuestion(
            "Which action is the best starting point for a ${lesson.title} problem?",
            firstStep,
            listOf("Guess the final numerical answer.", "Ignore units until the end.", "Choose a formula from an unrelated topic."),
            0,
            "A defensible solution begins by identifying the relevant quantities and chemical reasoning.",
            hint = "The best first step makes later calculation or explanation traceable.",
            skill = "Problem setup"
        ),
        knowledgeQuestion(
            "Which statement describes a common mistake in ${lesson.title}?",
            lesson.commonMistake,
            listOf("Checking assumptions and units.", "Relating the model to evidence.", "Explaining why each step is valid."),
            3,
            lesson.commonMistake,
            hint = "The mistake usually violates assumptions, units, conservation, or evidence.",
            skill = "Misconception check"
        ),
        knowledgeQuestion(
            "Why is ${lesson.title} useful outside an exercise?",
            lesson.realWorldApplication,
            listOf("It removes the need for measurements.", "It guarantees every reaction gives one product.", "It makes chemical hazards irrelevant."),
            1,
            lesson.realWorldApplication,
            hint = "Applications should connect chemistry to measurement, materials, safety, medicine, energy or environment.",
            skill = "Application"
        ),
        knowledgeQuestion(
            "Which extension is genuinely connected to ${lesson.title}?",
            advanced,
            unrelated.takeLast(3),
            2,
            advanced,
            hint = "A genuine extension should deepen the same model instead of jumping to an unrelated topic.",
            skill = "Extension",
            difficulty = "Stretch"
        ),
        knowledgeQuestion(
            "What makes an explanation of ${lesson.title} scientifically strong?",
            "It connects the model with observations, states its assumptions and uses consistent units.",
            listOf("It relies on memorised words alone.", "It ignores exceptions.", "It changes the chemical equation to match the answer."),
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
        ),
        knowledgeQuestion(
            "Which evidence would most strongly support a claim about ${lesson.title}?",
            "A repeatable observation or measurement that directly tests the lesson relationship.",
            listOf("A single memorised definition.", "A result copied from an unrelated topic.", "A guess that matches the option wording."),
            0,
            "Strong chemistry answers use evidence that is relevant, repeatable and tied to the model.",
            hint = "Look for direct evidence, not just familiar vocabulary.",
            skill = "Evidence reasoning"
        ),
        knowledgeQuestion(
            "Which variable should be controlled first when testing ${lesson.title} experimentally?",
            "The condition that would otherwise change the measured chemical response.",
            listOf("The longest word in the procedure.", "The color of the notebook.", "A variable unrelated to the reaction or model."),
            2,
            "Control variables protect the interpretation of the measured response.",
            hint = "A control prevents a false explanation.",
            skill = "Experimental design"
        ),
        knowledgeQuestion(
            "Which answer format is strongest for an exam question on ${lesson.title}?",
            "Claim, relationship, substitution or evidence, then chemical interpretation.",
            listOf("Only the final option letter.", "A definition with no link to the question.", "A calculation with no unit or reasoning."),
            1,
            "A complete answer shows both the method and the chemical meaning.",
            hint = "Think of how a marker awards reasoning marks.",
            skill = "Exam technique"
        ),
        knowledgeQuestion(
            "What should be done if two methods give different conclusions about ${lesson.title}?",
            "Compare assumptions, calibration, uncertainty and whether both methods test the same idea.",
            listOf("Always accept the larger value.", "Ignore the method with more steps.", "Average them without checking conditions."),
            3,
            "Disagreement should be investigated before combining or choosing results.",
            hint = "Different methods can measure different things.",
            skill = "Method comparison",
            difficulty = "Stretch"
        ),
        knowledgeQuestion(
            "Which choice best connects ${lesson.title} with laboratory safety?",
            "Understand the chemicals, energy changes, exposure routes and waste before practical work.",
            listOf("Safety is unnecessary for virtual or small-scale chemistry.", "Only product color matters.", "Wear PPE after the experiment is complete."),
            0,
            "Chemical understanding includes hazards, controls and responsible waste handling.",
            hint = "Safe work starts before the first measurement.",
            skill = "Safety reasoning"
        ),
        knowledgeQuestion(
            "Which statement best describes uncertainty in ${lesson.title} data?",
            "Uncertainty shows the reasonable range of values supported by measurement and method limits.",
            listOf("Uncertainty means the result is useless.", "Uncertainty is always zero after averaging.", "Uncertainty only matters in physics."),
            2,
            "Uncertainty helps decide whether a result is meaningful and comparable.",
            hint = "Uncertainty is part of honest measurement.",
            skill = "Data analysis"
        ),
        knowledgeQuestion(
            "Which transfer question would best test deeper understanding of ${lesson.title}?",
            "Apply the same idea to a new substance, condition or observation and explain the result.",
            listOf("Repeat the lesson title exactly.", "Choose the first familiar term.", "Ignore the new condition."),
            1,
            "Transfer questions reveal whether the concept works beyond the original example.",
            hint = "Deep understanding survives a changed context.",
            skill = "Transfer"
        ),
        knowledgeQuestion(
            "Which comparison is most useful when learning ${lesson.title}?",
            "Compare a correct model with a tempting misconception and explain the difference.",
            listOf("Compare two unrelated spellings.", "Compare only font size.", "Compare answers without reasons."),
            3,
            "Contrasting correct reasoning with a misconception makes the boundary of the idea clearer.",
            hint = "Useful comparisons expose why one explanation works.",
            skill = "Misconception repair"
        ),
        knowledgeQuestion(
            "What should a student do after missing a ${lesson.title} MCQ?",
            "Read the explanation, identify the failed skill, and retry a similar question.",
            listOf("Skip the topic forever.", "Memorize only the correct letter.", "Assume the question was impossible."),
            0,
            "Targeted review turns a wrong answer into useful diagnostic feedback.",
            hint = "Wrong answers are most useful when you name the failed step.",
            skill = "Review strategy"
        ),
        knowledgeQuestion(
            "Which reasoning pattern is most reliable for ${lesson.title}?",
            "Start from particles or conservation, connect to the equation or model, then predict the observation.",
            listOf("Start from the answer choices and invent a rule.", "Use a rule from any chapter.", "Ignore particle-level reasoning."),
            2,
            "Chemistry reasoning connects invisible particles and conservation laws to visible evidence.",
            hint = "Particles, conservation and evidence are the backbone.",
            skill = "Reasoning chain"
        ),
        knowledgeQuestion(
            "Which sign that a learner has mastered ${lesson.title} is strongest?",
            "They can explain when the idea works, when it fails and how evidence would test it.",
            listOf("They remember only the title.", "They can copy one formula without units.", "They avoid all applications."),
            1,
            "Mastery includes use, limits and evidence, not just recall.",
            hint = "Mastery includes boundaries.",
            skill = "Mastery check",
            difficulty = "Stretch"
        ),
        knowledgeQuestion(
            "Which note belongs in a revision card for ${lesson.title}?",
            "Definition, key relationship, common mistake, example and one real application.",
            listOf("Only decorative drawings.", "Only the hardest equation from another topic.", "Only a list of answer letters."),
            3,
            "A useful revision card supports recall, transfer and error avoidance.",
            hint = "Good revision cards are compact but complete.",
            skill = "Revision"
        ),
        knowledgeQuestion(
            "What is the best way to connect ${lesson.title} with another lesson in ${module.title}?",
            "Identify the shared particle, energy, structure, equilibrium or measurement idea.",
            listOf("Assume every lesson is isolated.", "Connect topics only by alphabetical order.", "Use the same answer for every chapter."),
            0,
            "Cross-links make chemistry easier because the same principles reappear in new forms.",
            hint = "Look for the shared chemical principle.",
            skill = "Module connection"
        ),
        knowledgeQuestion(
            "Which statement best handles exceptions in ${lesson.title}?",
            "Use the general rule, then check whether special structure, conditions or non-ideal behavior changes it.",
            listOf("Rules never have exceptions.", "Exceptions mean the rule is worthless.", "Ignore conditions and phases."),
            2,
            "Many chemistry rules are trends or models whose reliability depends on conditions.",
            hint = "Exceptions usually have a chemical reason.",
            skill = "Exception handling",
            difficulty = "Stretch"
        ),
        knowledgeQuestion(
            "Which final reflection proves useful after completing ${lesson.title}?",
            "State what changed in your understanding and which evidence or formula you would use next time.",
            listOf("Write only 'done'.", "Forget the explanation after scoring.", "Record no error source or next step."),
            1,
            "Reflection helps convert a completed MCQ into durable learning.",
            hint = "Name the learning gain and next action.",
            skill = "Metacognition"
        )
    )
}

private fun schoolPhaseKnowledgeChecksFor(module: CurriculumModule, lesson: CurriculumLesson): List<KnowledgeCheck> {
    val brief = schoolBriefFor(lesson)
    val otherLessons = module.lessons.filterNot { it.title == lesson.title }.map { it.title }
    val distractors = (otherLessons + listOf("Only memorise the heading", "Ignore the observation", "Use a college-level rule first")).distinct()
    val phaseChecks = listOf(
        knowledgeQuestion(
            "Discover: why does ${lesson.title} matter in daily life?",
            brief.whyItMatters,
            distractors.take(3),
            1,
            "The Discover page connects the topic to daily use: ${brief.whyItMatters}",
            hint = "Think about the first daily-life reason given in the lesson.",
            skill = "Discover purpose",
            difficulty = "Easy",
            reviewPage = "Discover"
        ),
        knowledgeQuestion(
            "Discover: which daily-life action matches this lesson?",
            brief.dailyLink,
            listOf("Copy a formula from another chapter.", "Skip examples and start the test.", "Use only the hardest word in the title."),
            2,
            "The lesson starts from an observation students can safely notice.",
            hint = "Look for the safe home or school observation.",
            skill = "Daily-life link",
            difficulty = "Easy",
            reviewPage = "Discover"
        ),
        knowledgeQuestion(
            "Understand: what is the simplest meaning of ${lesson.title}?",
            brief.simpleMeaning,
            listOf("It is unrelated to daily life.", "It can only be understood in college.", "It means every substance behaves the same way."),
            0,
            "The Understand page gives the simple meaning before adding examples.",
            hint = "Choose the answer that sounds like a clear teacher explanation.",
            skill = "Simple definition",
            difficulty = "Easy",
            reviewPage = "Understand"
        ),
        knowledgeQuestion(
            "Understand: which memory trick belongs to this lesson?",
            brief.memoryTrick,
            listOf("Long words are always correct.", "Never check examples.", "Change the definition for every question."),
            3,
            "This memory trick helps the learner recall the idea without fear.",
            hint = "Return to the bold memory trick on page 2.",
            skill = "Memory trick",
            difficulty = "Easy",
            reviewPage = "Understand"
        ),
        knowledgeQuestion(
            "Understand: which key words should be revised first?",
            brief.keyWords,
            distractors.takeLast(3),
            2,
            "These key words carry the lesson's main meaning.",
            hint = "Look for the key words line on page 2.",
            skill = "Vocabulary",
            difficulty = "Core",
            reviewPage = "Understand"
        ),
        knowledgeQuestion(
            "Observe: which home example fits ${lesson.title}?",
            brief.homeExample,
            listOf("A random spelling test.", "A result with no observation.", "A fact from an unrelated lesson."),
            1,
            "The Observe page uses a familiar home example to make the idea visible.",
            hint = "Think about home, kitchen, clothes, fuels, water or common objects.",
            skill = "Home observation",
            difficulty = "Easy",
            reviewPage = "Observe"
        ),
        knowledgeQuestion(
            "Observe: which school example supports this lesson?",
            brief.schoolExample,
            listOf("No chart, no demo and no reading.", "Only the lesson title.", "A guess without evidence."),
            3,
            "The school example shows how the idea can be checked in class.",
            hint = "Look for the classroom example from page 3.",
            skill = "School observation",
            difficulty = "Core",
            reviewPage = "Observe"
        ),
        knowledgeQuestion(
            "Observe: what mini activity should a learner try?",
            brief.activity.stripHtml(),
            listOf("Heat unknown chemicals alone.", "Taste laboratory chemicals.", "Ignore all safety instructions."),
            0,
            "The activity is simple, safe and directly connected to the topic.",
            hint = "The correct activity should be safe for school learners.",
            skill = "Mini activity",
            difficulty = "Core",
            reviewPage = "Observe"
        ),
        knowledgeQuestion(
            "Think: which prediction question belongs to ${lesson.title}?",
            brief.prediction,
            listOf("What is the longest word?", "Can we skip observations?", "Is every answer correct?"),
            2,
            "The Think page asks learners to predict before reading the reason.",
            hint = "Prediction means: what do you think will happen?",
            skill = "Prediction",
            difficulty = "Core",
            reviewPage = "Think"
        ),
        knowledgeQuestion(
            "Think: why does the prediction happen?",
            brief.reason,
            listOf("Because chemistry is only memorisation.", "Because observations do not matter.", "Because every topic has the same reason."),
            1,
            "The reason connects the observation to the chemistry idea.",
            hint = "Choose the answer that explains, not just names.",
            skill = "Reasoning",
            difficulty = "Core",
            reviewPage = "Think"
        ),
        knowledgeQuestion(
            "Think: which common mistake should be avoided?",
            brief.misconception,
            listOf("Using examples carefully.", "Checking the observation.", "Reading the question slowly."),
            3,
            "This misconception is the exact error warning from the Think page.",
            hint = "Wrong answers often hide in common mistakes.",
            skill = "Misconception",
            difficulty = "Core",
            reviewPage = "Think"
        ),
        knowledgeQuestion(
            "Remember: which fact belongs in quick revision?",
            brief.factOne,
            listOf("Only copy answer letters.", "Forget examples.", "Avoid the lesson topic."),
            0,
            "Quick revision starts with the most important fact.",
            hint = "Look for Key point 1 on page 5.",
            skill = "Revision fact",
            difficulty = "Easy",
            reviewPage = "Remember"
        ),
        knowledgeQuestion(
            "Remember: which exam-ready answer is best?",
            brief.examAnswer,
            listOf("A vague answer without the lesson word.", "An unrelated college-level definition.", "A sentence that ignores examples and observations."),
            2,
            "The exam-ready answer is short, correct and easy to write.",
            hint = "Exam answers should be clear, not fancy.",
            skill = "Exam answer",
            difficulty = "Core",
            reviewPage = "Remember"
        ),
        knowledgeQuestion(
            "Remember: what is the one-line takeaway?",
            brief.takeaway,
            listOf("Chemistry is impossible.", "Examples should be ignored.", "Only long theory matters."),
            1,
            "The takeaway helps students leave the lesson with confidence.",
            hint = "Find the final sentence on the Remember page.",
            skill = "Takeaway",
            difficulty = "Easy",
            reviewPage = "Remember"
        ),
        knowledgeQuestion(
            "Review strategy: if you miss a question on ${lesson.title}, which page should you revisit first?",
            "Go back to the page named in the weak-topic hint, then retry a similar question.",
            listOf("Skip the lesson forever.", "Memorise only the option letter.", "Open an unrelated module."),
            3,
            "Weak-topic review is useful only when it sends the learner back to the exact phase they missed.",
            hint = "Use the review page shown with the missed skill.",
            skill = "Weak-topic repair",
            difficulty = "Core",
            reviewPage = "Remember"
        )
    )
    val authored = authoredMisconceptionChecksFor(module, lesson)
    return (authored + phaseChecks).take(15)
}

private fun String.stripHtml(): String =
    replace(Regex("<[^>]+>"), "")

/** Creates at least 20 module-specific, collapsible FAQ entries. */
internal fun moduleFaqs(module: CurriculumModule): List<ModuleFaq> {
    require(module.lessons.isNotEmpty())
    return List(20) { index ->
        val lesson = module.lessons[index % module.lessons.size]
        when (index % 20) {
            0 -> ModuleFaq("What is ${lesson.title}?", lesson.concept)
            1 -> ModuleFaq("What is the key relationship for ${lesson.title}?", "${lesson.formula}. ${formulaMeaningForFaq(module.title)}")
            2 -> ModuleFaq("How is ${lesson.title} used in practice?", lesson.realWorldApplication)
            3 -> ModuleFaq("What mistake should I avoid in ${lesson.title}?", lesson.commonMistake)
            4 -> ModuleFaq("What worked example illustrates ${lesson.title}?", lesson.workedExample)
            5 -> ModuleFaq(
                "What is the first step in a ${lesson.title} problem?",
                lesson.exampleSteps.firstOrNull() ?: "Identify the chemical species, quantities, evidence and assumptions."
            )
            6 -> ModuleFaq(
                "Which units and symbols matter in ${lesson.title}?",
                "Interpret ${lesson.formula}, define every symbol and preserve compatible units through the calculation."
            )
            7 -> ModuleFaq(
                "What evidence supports a conclusion about ${lesson.title}?",
                "Use calibrated experimental or computational evidence that directly tests the model, then report uncertainty and limitations."
            )
            8 -> ModuleFaq(
                "Which assumptions should be checked for ${lesson.title}?",
                "Check the model domain, chemical conditions, equilibrium or kinetic assumptions, sample quality and measurement calibration."
            )
            9 -> ModuleFaq(
                "How can I validate a ${lesson.title} result?",
                "Repeat the analysis where appropriate, test limiting cases and compare with an independent method, reference or observable trend."
            )
            10 -> ModuleFaq(
                "How does ${lesson.title} connect with ${module.title}?",
                "${lesson.title} provides one part of the structure, mechanism, measurement and application framework developed across ${module.title}."
            )
            11 -> ModuleFaq(
                "What advanced idea follows from ${lesson.title}?",
                lesson.advancedConcepts.firstOrNull() ?: "Extend the model by testing its molecular mechanism and quantitative limits."
            )
            12 -> ModuleFaq(
                "How should two approaches to ${lesson.title} be compared?",
                "Compare assumptions, required data, accuracy, uncertainty, computational or experimental cost and chemical interpretability."
            )
            13 -> ModuleFaq(
                "How should a ${lesson.title} answer be explained?",
                "State the claim, show the relationship or mechanism, connect it with evidence and explain why alternatives are less consistent."
            )
            14 -> ModuleFaq(
                "How should ${lesson.title} data be reported?",
                "Report units, significant figures, uncertainty, conditions, calibration or model details and enough method information for reproduction."
            )
            15 -> ModuleFaq(
                "What makes a ${lesson.title} model useful?",
                "A useful model explains observations, makes testable predictions and states the conditions under which its approximation remains valid."
            )
            16 -> ModuleFaq(
                "What limitation should be considered in ${lesson.title}?",
                "Every result depends on assumptions and evidence quality; identify interferences, non-ideal behaviour, sampling limits and model uncertainty."
            )
            17 -> ModuleFaq(
                "How can ${lesson.title} be studied safely?",
                "Plan hazards, controls, containment, waste handling and emergency response before practical work; use validated procedures and supervision."
            )
            18 -> ModuleFaq(
                "How should I revise ${lesson.title}?",
                "Explain the concept from memory, reproduce the example, solve all knowledge checks and revisit each incorrect reasoning step."
            )
            else -> ModuleFaq(
                "What is the final check for ${lesson.title}?",
                "Confirm conservation, signs, units, magnitude, chemical plausibility, uncertainty and consistency with the stated conditions."
            )
        }
    }
}

private fun formulaMeaningForFaq(moduleTitle: String): String = when (moduleTitle) {
    "Atomic Structure", "Atomic Structure: Senior Secondary" -> "Interpret every symbol using the stated atomic or quantum model."
    "Periodic Table", "Periodic Table and Trends" -> "Use the relationship as a trend or organizing rule while remembering documented exceptions."
    "Chemical Bonding", "Chemical Bonding: Senior Secondary" -> "Connect electron arrangement to geometry, energy and measurable properties."
    "States of Matter", "States and Phases: Senior Secondary" -> "Keep temperature, pressure and phase conditions explicit."
    "Chemical Reactions", "Reaction Equations and Redox" -> "Balance atoms and charge before drawing mechanistic conclusions."
    "Equilibrium and Solutions" -> "Distinguish equilibrium composition from the rates of forward and reverse change."
    "Kinetics and Electrochemistry" -> "Track sign conventions, units and the direction of electron transfer."
    else -> "Use consistent units, identify assumptions and compare the result with experimental evidence."
}
