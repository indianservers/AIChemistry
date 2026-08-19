package com.indianservers.aichemistryexplorer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.max

internal data class QuizQuestion(
    val id: String,
    val level: String,
    val classLabel: String,
    val category: String,
    val lessonTitle: String,
    val difficulty: String,
    val skill: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val hint: String,
    val formula: String?,
    val estimatedSeconds: Int
)

private data class QuizFilters(
    val level: String = "All",
    val classLabel: String = "All",
    val category: String = "All",
    val difficulty: String = "All",
    val count: Int = 20,
    val secondsPerQuestion: Int = 45,
    val weakTopicMode: Boolean = false
)

private data class QuizAttempt(
    val question: QuizQuestion,
    val selectedIndex: Int,
    val timeLeft: Int
) {
    val correct: Boolean get() = selectedIndex == question.correctIndex
}

private enum class QuizState { Setup, Playing, Result }

@Composable
internal fun ReusableQuizModulePage() {
    val bank = remember { reusableChemistryQuizBank() }
    var filters by remember { mutableStateOf(QuizFilters()) }
    var state by remember { mutableStateOf(QuizState.Setup) }
    var session by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var attempts by remember { mutableStateOf<List<QuizAttempt>>(emptyList()) }
    var secondsLeft by remember { mutableIntStateOf(filters.secondsPerQuestion) }

    val filtered = remember(bank, filters) { bank.filteredBy(filters) }

    Box(Modifier.fillMaxSize()) {
        QuizOrbitBackdrop()
        when (state) {
            QuizState.Setup -> QuizSetupScreen(
                bank = bank,
                filteredCount = filtered.size,
                filters = filters,
                onFilters = { filters = it },
                onStart = {
                    session = filtered.prepareSession(filters, attempts)
                    index = 0
                    selected = null
                    secondsLeft = filters.secondsPerQuestion
                    attempts = emptyList()
                    state = QuizState.Playing
                }
            )

            QuizState.Playing -> {
                if (session.isEmpty()) {
                    state = QuizState.Setup
                } else {
                    val current = session[index]
                    LaunchedEffect(index, state) {
                        secondsLeft = filters.secondsPerQuestion
                        while (state == QuizState.Playing && selected == null && secondsLeft > 0) {
                            delay(1_000)
                            secondsLeft = max(0, secondsLeft - 1)
                        }
                        if (state == QuizState.Playing && selected == null && secondsLeft == 0) {
                            selected = -1
                            attempts = attempts + QuizAttempt(current, -1, 0)
                        }
                    }
                    QuizPlayingScreen(
                        question = current,
                        index = index,
                        total = session.size,
                        selected = selected,
                        secondsLeft = secondsLeft,
                        score = attempts.count { it.correct },
                        onSelect = { answer ->
                            if (selected == null) {
                                selected = answer
                                attempts = attempts + QuizAttempt(current, answer, secondsLeft)
                            }
                        },
                        onNext = {
                            if (index < session.lastIndex) {
                                index++
                                selected = null
                            } else {
                                state = QuizState.Result
                            }
                        },
                        onQuit = { state = QuizState.Setup }
                    )
                }
            }

            QuizState.Result -> QuizResultScreen(
                attempts = attempts,
                total = session.size,
                onRetryWeak = {
                    val weak = attempts.filterNot { it.correct }.map { it.question.lessonTitle }.toSet()
                    filters = filters.copy(weakTopicMode = true, count = 20)
                    session = bank.filter { it.lessonTitle in weak }.ifEmpty { filtered }.shuffledStable().take(filters.count)
                    index = 0
                    selected = null
                    secondsLeft = filters.secondsPerQuestion
                    attempts = emptyList()
                    state = QuizState.Playing
                },
                onNewQuiz = { state = QuizState.Setup }
            )
        }
    }
}

@Composable
private fun QuizSetupScreen(
    bank: List<QuizQuestion>,
    filteredCount: Int,
    filters: QuizFilters,
    onFilters: (QuizFilters) -> Unit,
    onStart: () -> Unit
) {
    val levels = listOf("All") + bank.map { it.level }.distinct().sorted()
    val classes = listOf("All") + bank.map { it.classLabel }.distinct().sortedWith(classSort())
    val categories = listOf("All") + bank.map { it.category }.distinct().sorted()
    val difficulties = listOf("All", "Easy", "Medium", "Hard")
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
    ) {
        item {
            QuizHeroCard(bank.size, filteredCount, filters)
        }
        item { QuizPicker("Level", levels, filters.level) { onFilters(filters.copy(level = it)) } }
        item { QuizPicker("Class", classes, filters.classLabel) { onFilters(filters.copy(classLabel = it)) } }
        item { QuizPicker("Category", categories, filters.category) { onFilters(filters.copy(category = it)) } }
        item { QuizPicker("Difficulty", difficulties, filters.difficulty) { onFilters(filters.copy(difficulty = it)) } }
        item {
            GlowCard(Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 10.dp), Cyan) {
                Text("SESSION SETTINGS", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                QuizStepper("Questions", filters.count, listOf(10, 20, 30, 50)) { onFilters(filters.copy(count = it)) }
                QuizStepper("Timer / question", filters.secondsPerQuestion, listOf(30, 45, 60, 90), "s") {
                    onFilters(filters.copy(secondsPerQuestion = it))
                }
                Row(
                    Modifier.fillMaxWidth().padding(top = 9.dp)
                        .background(if (filters.weakTopicMode) Orange.copy(.12f) else Panel, RoundedCornerShape(8.dp))
                        .border(1.dp, if (filters.weakTopicMode) Orange else Border, RoundedCornerShape(8.dp))
                        .clickable { onFilters(filters.copy(weakTopicMode = !filters.weakTopicMode)) }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (filters.weakTopicMode) "ON" else "OFF", color = if (filters.weakTopicMode) Orange else Muted, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 10.dp))
                    Column {
                        Text("Weak-topic mode", color = White, fontWeight = FontWeight.SemiBold)
                        Text("Prioritises topics commonly missed in this level/category.", color = Muted, fontSize = 10.sp)
                    }
                }
            }
        }
        item {
            Button(
                onClick = onStart,
                enabled = filteredCount > 0,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(Purple),
                shape = RoundedCornerShape(9.dp)
            ) {
                Text(if (filteredCount > 0) "Start ${filters.count}-question quiz" else "No questions for this filter", color = Night, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun QuizPlayingScreen(
    question: QuizQuestion,
    index: Int,
    total: Int,
    selected: Int?,
    secondsLeft: Int,
    score: Int,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit,
    onQuit: () -> Unit
) {
    val answered = selected != null
    val timedOut = selected == -1
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("<", color = White, fontSize = 28.sp, modifier = Modifier.clickable { onQuit() }.padding(end = 10.dp))
                Column(Modifier.weight(1f)) {
                    Text("${question.classLabel} · ${question.category}", color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(question.lessonTitle, color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 2)
                }
                QuizTimer(secondsLeft, question.estimatedSeconds)
            }
            LinearProgressIndicator(
                progress = { (index + 1f) / total },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).height(7.dp),
                color = if (secondsLeft <= 10) Orange else Purple,
                trackColor = Border
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuizPill("Q ${index + 1}/$total", Cyan, Modifier.weight(1f))
                QuizPill("Score $score", Green, Modifier.weight(1f))
                QuizPill(question.difficulty, Orange, Modifier.weight(1f))
            }
            GlowCard(Modifier.fillMaxWidth().padding(top = 12.dp), Purple) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AtomGraphic(46.dp, Purple)
                    Column(Modifier.weight(1f).padding(start = 10.dp)) {
                        Text(question.skill.uppercase(Locale.US), color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(question.question, color = White, fontSize = 19.sp, lineHeight = 27.sp, modifier = Modifier.padding(top = 6.dp))
                    }
                }
                question.formula?.takeIf { it.isNotBlank() }?.let {
                    QuizFormulaStrip(it)
                }
                Text("Hint: ${question.hint}", color = Muted, fontSize = 10.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 10.dp))
            }
        }
        items(question.options.indices.toList()) { optionIndex ->
            QuizAnswerOption(
                optionIndex = optionIndex,
                option = question.options[optionIndex],
                selected = selected,
                correctIndex = question.correctIndex,
                timedOut = timedOut,
                onSelect = { onSelect(optionIndex) }
            )
        }
        selected?.let {
            item {
                GlowCard(Modifier.fillMaxWidth().padding(top = 11.dp), if (it == question.correctIndex) Green else Orange) {
                    Text(if (it == question.correctIndex) "CORRECT" else if (timedOut) "TIME UP" else "EXPLANATION", color = if (it == question.correctIndex) Green else Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(question.explanation, color = White, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 6.dp))
                }
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(50.dp),
                    colors = ButtonDefaults.buttonColors(Purple),
                    shape = RoundedCornerShape(9.dp)
                ) { Text(if (index < total - 1) "Next question" else "Finish quiz", color = Night, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun QuizResultScreen(attempts: List<QuizAttempt>, total: Int, onRetryWeak: () -> Unit, onNewQuiz: () -> Unit) {
    val correct = attempts.count { it.correct }
    val percent = if (total == 0) 0 else correct * 100 / total
    val averageTime = attempts.takeIf { it.isNotEmpty() }?.map { it.question.estimatedSeconds - it.timeLeft }?.average()?.toInt() ?: 0
    val mastery = when {
        percent >= 90 -> "Excellent mastery"
        percent >= 75 -> "Strong progress"
        percent >= 50 -> "Almost there"
        else -> "Needs focused practice"
    }
    val weakTopics = attempts.filterNot { it.correct }.groupingBy { it.question.lessonTitle }.eachCount()
        .entries.sortedByDescending { it.value }.take(5)
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        item {
            GlowCard(Modifier.fillMaxWidth(), if (percent >= 80) Green else Orange) {
                Text("QUIZ COMPLETE", color = if (percent >= 80) Green else Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Text("$correct/$total", color = White, fontSize = 44.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
                Text("$mastery · $percent% · ${correct * 10} XP earned", color = Muted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuizMetric("Accuracy", "$percent%", if (percent >= 80) Green else Orange, Modifier.weight(1f))
                    QuizMetric("Avg time", "${averageTime}s", Cyan, Modifier.weight(1f))
                    QuizMetric("Missed", "${total - correct}", Orange, Modifier.weight(1f))
                }
            }
        }
        item {
            Text("WEAK TOPICS", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        }
        if (weakTopics.isEmpty()) {
            item { QuizPill("No weak topics found. Strong attempt.", Green, Modifier.fillMaxWidth()) }
        } else {
            items(weakTopics) { entry ->
                GlowCard(Modifier.fillMaxWidth().padding(bottom = 8.dp), Orange) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(entry.key, color = White, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text("${entry.value} miss", color = Orange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            Button(
                onClick = onRetryWeak,
                enabled = weakTopics.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(Orange),
                shape = RoundedCornerShape(9.dp)
            ) { Text("Retry weak topics", color = Night, fontWeight = FontWeight.Bold) }
            Button(
                onClick = onNewQuiz,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(Purple),
                shape = RoundedCornerShape(9.dp)
            ) { Text("New quiz", color = Night, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun QuizPicker(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    GlowCard(Modifier.fillMaxWidth().padding(bottom = 10.dp), Purple) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label.uppercase(Locale.US), color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(selected, color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
        }
        LazyRow(
            Modifier.fillMaxWidth().padding(top = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options) { option ->
                val active = option == selected
                Box(
                    Modifier
                        .background(if (active) Purple.copy(.24f) else Panel.copy(.92f), RoundedCornerShape(999.dp))
                        .border(1.dp, if (active) Purple else Border, RoundedCornerShape(999.dp))
                        .clickable { onSelect(option) }
                        .padding(horizontal = 13.dp, vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(option, color = if (active) White else Muted, fontSize = 10.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun QuizHeroCard(totalQuestions: Int, matchedQuestions: Int, filters: QuizFilters) {
    GlowCard(Modifier.fillMaxWidth().padding(bottom = 12.dp), Purple) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(82.dp), contentAlignment = Alignment.Center) {
                AtomGraphic(78.dp, Purple)
                Text("MCQ", color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text("SPECIAL QUIZ MODULE", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("600+ interactive chemistry MCQs", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 25.sp)
                Text("Choose a path, beat the timer, and turn missed topics into your next practice set.", color = Muted, fontSize = 10.sp, lineHeight = 15.sp)
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuizMetric("Bank", "$totalQuestions", Green, Modifier.weight(1f))
            QuizMetric("Matched", "$matchedQuestions", Cyan, Modifier.weight(1f))
            QuizMetric("XP", "+${filters.count * 10}", Orange, Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuizAnswerOption(
    optionIndex: Int,
    option: String,
    selected: Int?,
    correctIndex: Int,
    timedOut: Boolean,
    onSelect: () -> Unit
) {
    val answered = selected != null
    val isCorrect = answered && optionIndex == correctIndex
    val isWrong = selected == optionIndex && optionIndex != correctIndex
    val accent = when {
        isCorrect -> Green
        isWrong || timedOut -> Orange
        else -> Border
    }
    val badge = when {
        isCorrect -> "✓"
        isWrong -> "×"
        else -> answerLetter(optionIndex)
    }
    Row(
        Modifier.fillMaxWidth().padding(top = 9.dp)
            .background(accent.copy(if (answered) .13f else .07f), RoundedCornerShape(10.dp))
            .border(1.dp, accent, RoundedCornerShape(10.dp))
            .clickable(enabled = !answered) { onSelect() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(32.dp).background(accent.copy(.18f), CircleShape), contentAlignment = Alignment.Center) {
            Text(badge, color = if (answered) accent else Muted, fontWeight = FontWeight.Bold)
        }
        Text(option, color = White, modifier = Modifier.weight(1f).padding(start = 11.dp), lineHeight = 19.sp)
    }
}

@Composable
private fun QuizFormulaStrip(formula: String) {
    Row(
        Modifier.fillMaxWidth().padding(top = 10.dp)
            .background(Cyan.copy(.10f), RoundedCornerShape(8.dp))
            .border(1.dp, Cyan.copy(.45f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("FORMULA", color = Cyan, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 9.dp))
        Text(formula, color = White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun QuizStepper(label: String, value: Int, choices: List<Int>, suffix: String = "", onSelect: (Int) -> Unit) {
    Text(label, color = White, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 9.dp, bottom = 6.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        choices.forEach { choice ->
            val active = value == choice
            Box(
                Modifier.weight(1f)
                    .background(if (active) Cyan.copy(.18f) else Panel, RoundedCornerShape(8.dp))
                    .border(1.dp, if (active) Cyan else Border, RoundedCornerShape(8.dp))
                    .clickable { onSelect(choice) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("$choice$suffix", color = if (active) Cyan else Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun QuizMetric(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(color.copy(.10f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(.45f), RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label.uppercase(Locale.US), color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text(value, color = color, fontSize = 19.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun QuizPill(text: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier.background(color.copy(.12f), RoundedCornerShape(8.dp)).border(1.dp, color.copy(.55f), RoundedCornerShape(8.dp)).padding(9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
private fun QuizTimer(secondsLeft: Int, estimatedSeconds: Int) {
    val urgent = secondsLeft <= 10
    val accent = if (urgent) Orange else Cyan
    Box(Modifier.size(62.dp).background(accent.copy(.14f), CircleShape).border(1.dp, accent, CircleShape), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(54.dp)) {
            drawCircle(accent.copy(.18f), style = Stroke(width = 5f))
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = 360f * secondsLeft / estimatedSeconds.coerceAtLeast(1),
                useCenter = false,
                style = Stroke(width = 5f)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$secondsLeft", color = accent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("${estimatedSeconds}s", color = Muted, fontSize = 8.sp)
        }
    }
}

@Composable
private fun QuizOrbitBackdrop() {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val purple = Purple.copy(.10f)
        val cyan = Cyan.copy(.08f)
        drawCircle(purple, radius = w * .42f, center = Offset(w * .88f, h * .08f), style = Stroke(width = 2f))
        drawCircle(cyan, radius = w * .34f, center = Offset(w * .10f, h * .46f), style = Stroke(width = 2f))
        repeat(9) { i ->
            val x = w * ((i * 37 % 100) / 100f)
            val y = h * ((i * 19 % 100) / 100f)
            val color = if (i % 2 == 0) Cyan else Purple
            drawCircle(color.copy(.18f), radius = 4f + i % 3, center = Offset(x, y))
            if (i > 0) {
                val px = w * (((i - 1) * 37 % 100) / 100f)
                val py = h * (((i - 1) * 19 % 100) / 100f)
                drawLine(color.copy(.08f), Offset(px, py), Offset(x, y), strokeWidth = 1.4f)
            }
        }
    }
}

private fun List<QuizQuestion>.filteredBy(filters: QuizFilters): List<QuizQuestion> =
    filter { question ->
        (filters.level == "All" || question.level == filters.level) &&
            (filters.classLabel == "All" || question.classLabel == filters.classLabel) &&
            (filters.category == "All" || question.category == filters.category) &&
            (filters.difficulty == "All" || question.difficulty == filters.difficulty)
    }.let { filtered ->
        if (!filters.weakTopicMode) filtered else filtered.sortedWith(
            compareByDescending<QuizQuestion> { it.skill in listOf("Misconception", "Application", "Reasoning") }
                .thenBy { it.lessonTitle }
        )
    }

private fun List<QuizQuestion>.prepareSession(filters: QuizFilters, attempts: List<QuizAttempt>): List<QuizQuestion> {
    val weakTopics = attempts.filterNot { it.correct }.map { it.question.lessonTitle }.toSet()
    val sorted = if (filters.weakTopicMode && weakTopics.isNotEmpty()) {
        sortedByDescending { it.lessonTitle in weakTopics }
    } else {
        shuffledStable()
    }
    return sorted.take(filters.count)
}

private fun List<QuizQuestion>.shuffledStable(): List<QuizQuestion> =
    sortedBy { it.id.hashCode().let { hash -> if (hash == Int.MIN_VALUE) 0 else kotlin.math.abs(hash) } }

private fun classSort(): Comparator<String> = compareBy { label ->
    when {
        label == "All" -> 0
        label.startsWith("Class ") -> label.removePrefix("Class ").toIntOrNull() ?: 99
        label == "Intermediate I" -> 11
        label == "Intermediate II" -> 12
        else -> 90
    }
}

internal fun reusableChemistryQuizBank(): List<QuizQuestion> {
    val base = curriculum
        .filterNot { it.level.contains("Undergraduate", true) || it.level.contains("Postgraduate", true) }
        .flatMap { module -> module.lessons.map { lesson -> module to lesson } }
    return base.flatMapIndexed { lessonIndex, (module, lesson) ->
        val classLabel = classLabelFor(module, lessonIndex)
        val level = when {
            module.level.contains("6") || module.level.contains("7") || module.level.contains("8") -> "Foundation"
            module.level.contains("9") || module.level.contains("10") -> "Secondary"
            else -> "Senior Secondary"
        }
        val category = categoryFor(module)
        questionVariants(module, lesson, lessonIndex).mapIndexed { variantIndex, variant ->
            variant.copy(
                id = "rq_${lesson.title.quizSlug()}_${variantIndex + 1}",
                level = level,
                classLabel = classLabel,
                category = category
            )
        }
    }.takeIf { it.size >= 600 } ?: error("Reusable quiz bank must contain at least 600 questions")
}

private fun questionVariants(module: CurriculumModule, lesson: CurriculumLesson, lessonIndex: Int): List<QuizQuestion> {
    val options = lesson.answers.takeIf { it.size == 4 } ?: listOf("Correct idea", "Opposite idea", "Unrelated fact", "Unsafe shortcut")
    val correct = lesson.correct.coerceIn(0, options.lastIndex)
    val commonWrong = options.firstOrNullIndexed { index, _ -> index != correct } ?: "A common wrong answer"
    val formula = lesson.formula.takeUnless { it.isBlank() || it == "—" }
    val concept = lesson.concept.ensurePeriod()
    val variants = listOf(
        VariantSeed("Recall", "Easy", "Which option best matches ${lesson.title}?", options, correct, "The correct option matches the central idea: $concept", "Look for the option closest to the lesson definition.", 35),
        VariantSeed("Concept", "Easy", "In simple words, what is the main idea of ${lesson.title}?", listOf(concept, lesson.commonMistake, lesson.realWorldApplication, "It is only a name to memorise."), 0, "$concept This is the idea students should recognise first.", "Choose the sentence that explains the concept, not just an example.", 40),
        VariantSeed("Formula", "Medium", "Which formula or relationship belongs with ${lesson.title}?", listOf(lesson.formula, "mass = volume + colour", "pH = temperature × time", "rate = symbol ÷ name"), 0, "${lesson.formula} is the relationship linked with this lesson. Always check symbols and units.", "The correct option uses chemistry symbols from the lesson.", 45),
        VariantSeed("Misconception", "Medium", "Which mistake should you avoid in ${lesson.title}?", listOf(lesson.commonMistake, "Checking units before answering.", "Reading the observation carefully.", "Connecting the answer with evidence."), 0, "This is the common trap for the topic: ${lesson.commonMistake}", "The wrong habit is the answer here.", 45),
        VariantSeed("Application", "Medium", "Where can ${lesson.title} be useful?", listOf(lesson.realWorldApplication, "Only in spelling tests.", "Only when no observation is given.", "Only outside chemistry."), 0, lesson.realWorldApplication.ensurePeriod(), "Think of the option that connects chemistry with real use.", 45),
        VariantSeed("Reasoning", "Hard", "A student chooses \"$commonWrong\" for ${lesson.title}. What should they check first?", listOf("Compare it with the key idea and evidence.", "Keep the answer because it sounds familiar.", "Ignore the formula and unit.", "Change the question to fit the answer."), 0, "Reasoning improves when the answer is checked against the concept, formula and observation.", "Good chemistry answers can be defended with evidence.", 55),
        VariantSeed("Exam", "Hard", "Which exam answer style is strongest for ${lesson.title}?", listOf("State the idea, use the correct term, and connect it with the given situation.", "Write only the option letter.", "Use a long answer without the concept.", "Copy an unrelated definition."), 0, "Strong exam answers are short, accurate and connected to the question.", "Exam answers need concept plus context.", 50),
        VariantSeed("Transfer", "Hard", "If the example changes but the concept is still ${lesson.title}, what should remain the same?", listOf("The scientific reason behind the answer.", "The object name in the example.", "The page number in the book.", "The longest word in the option."), 0, "Transfer means the same idea works in a new situation.", "Look for what belongs to the concept, not the surface example.", 55)
    )
    return variants.mapIndexed { index, seed ->
        val shuffled = rotateOptions(seed.options, lessonIndex + index)
        val correctText = seed.options[seed.correctIndex]
        QuizQuestion(
            id = "",
            level = "",
            classLabel = "",
            category = "",
            lessonTitle = lesson.title,
            difficulty = seed.difficulty,
            skill = seed.skill,
            question = seed.question,
            options = shuffled,
            correctIndex = shuffled.indexOf(correctText).coerceAtLeast(0),
            explanation = seed.explanation,
            hint = seed.hint,
            formula = formula,
            estimatedSeconds = seed.estimatedSeconds
        )
    }
}

private data class VariantSeed(
    val skill: String,
    val difficulty: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val hint: String,
    val estimatedSeconds: Int
)

private fun rotateOptions(options: List<String>, by: Int): List<String> {
    val clean = options.map { it.ifBlank { "Not enough information" } }.distinct().let {
        if (it.size >= 4) it.take(4) else it + listOf("Opposite conclusion", "Unrelated observation", "Unsafe shortcut").take(4 - it.size)
    }
    val shift = by % clean.size
    return clean.drop(shift) + clean.take(shift)
}

private fun categoryFor(module: CurriculumModule): String = when {
    module.title.contains("Atomic", true) -> "Atomic Structure"
    module.title.contains("Periodic", true) -> "Periodic Table"
    module.title.contains("Bond", true) -> "Chemical Bonding"
    module.title.contains("Reaction", true) || module.title.contains("Redox", true) -> "Reactions"
    module.title.contains("Organic", true) || module.category.contains("Organic", true) -> "Organic Chemistry"
    module.title.contains("Lab", true) || module.title.contains("Practical", true) -> "Practical Chemistry"
    module.title.contains("Nuclear", true) -> "Nuclear Chemistry"
    module.title.contains("Solution", true) || module.title.contains("Physical", true) -> "Physical Chemistry"
    module.title.contains("Carbon", true) -> "Carbon Chemistry"
    module.title.contains("Matter", true) || module.title.contains("Mixture", true) -> "Matter and Mixtures"
    else -> module.category
}

private fun classLabelFor(module: CurriculumModule, lessonIndex: Int): String = when {
    module.title.startsWith("Class 6") -> "Class 6"
    module.title.startsWith("Class 7") -> "Class 7"
    module.title.startsWith("Class 8") -> "Class 8"
    module.title.startsWith("Class 9") -> "Class 9"
    module.title.startsWith("Class 10") -> "Class 10"
    module.title.contains("Intermediate First", true) -> "Intermediate I"
    module.title.contains("Intermediate Second", true) -> "Intermediate II"
    module.level.contains("6") || module.level.contains("7") || module.level.contains("8") -> listOf("Class 6", "Class 7", "Class 8")[lessonIndex % 3]
    module.level.contains("9") || module.level.contains("10") -> listOf("Class 9", "Class 10")[lessonIndex % 2]
    else -> listOf("Class 11", "Class 12", "Intermediate I", "Intermediate II")[lessonIndex % 4]
}

private fun String.ensurePeriod(): String = if (endsWith(".") || endsWith("!") || endsWith("?")) this else "$this."

private fun String.quizSlug(): String = lowercase(Locale.US)
    .replace(Regex("[^a-z0-9]+"), "-")
    .trim('-')

private inline fun <T> List<T>.firstOrNullIndexed(predicate: (Int, T) -> Boolean): T? {
    forEachIndexed { index, item -> if (predicate(index, item)) return item }
    return null
}
