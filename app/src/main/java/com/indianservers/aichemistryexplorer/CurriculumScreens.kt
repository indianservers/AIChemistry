package com.indianservers.aichemistryexplorer

import android.graphics.Paint
import android.speech.tts.TextToSpeech
import android.text.Html
import android.widget.TextView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
internal fun CurriculumModuleScreen(
    moduleTitle: String,
    completedLessons: Set<String>,
    onBack: () -> Unit,
    onLesson: (String) -> Unit
) {
    val module = curriculum.firstOrNull { it.title == moduleTitle } ?: curriculum.first()
    val context = LocalContext.current
    val lessonRepository = remember(context) {
        (context.applicationContext as ChemistryExplorerApplication).lessonPageRepository
    }
    LaunchedEffect(module.title) {
        lessonRepository.replaceClassSixToTenModule(module)
    }
    val completed = module.lessons.count { "${module.title}/${it.title}" in completedLessons }
    var modulePage by remember(module.title) { mutableIntStateOf(0) }
    val pageNames = listOf("Overview", "Lessons", "Formulas", "Progress", "FAQs")
    Column(Modifier.fillMaxSize()) {
        ScreenTitle(module.title, "${module.level} · ${module.category} · $completed/${module.lessons.size} complete", onBack)
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            pageNames.forEachIndexed { index, name ->
                Text(
                    name, color = if (modulePage == index) Night else Muted, fontSize = 9.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).background(if (modulePage == index) module.color else Panel, RoundedCornerShape(8.dp))
                        .border(1.dp, if (modulePage == index) module.color else Border, RoundedCornerShape(8.dp))
                        .clickable { modulePage = index }.padding(vertical = 9.dp)
                )
            }
        }
        AnimatedContent(modulePage, modifier = Modifier.weight(1f), label = "module-page") { page ->
            when (page) {
                0 -> ModuleOverviewPage(module, completed) { modulePage = 1 }
                1 -> ModuleLessonsPage(module, completedLessons, onLesson)
                2 -> ModuleFormulaPage(module)
                3 -> ModuleProgressPage(module, completed)
                else -> ModuleFaqPage(module)
            }
        }
    }
}

@Composable
private fun ModuleOverviewPage(module: CurriculumModule, completed: Int, openLessons: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        GlowCard(Modifier.fillMaxWidth(), module.color) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                    AtomGraphic(68.dp, module.color)
                    Text(module.icon, color = White, fontSize = if (module.icon.length > 1) 12.sp else 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Tag(module.subcategory.uppercase(), module.color)
                    Text(module.description, color = White, fontSize = 14.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
        Text("LEARNING PATH", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 22.dp, bottom = 9.dp))
        module.lessons.forEachIndexed { index, lesson ->
            Row(Modifier.fillMaxWidth().padding(bottom = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(31.dp).background(module.color.copy(.13f), CircleShape), contentAlignment = Alignment.Center) {
                    Text("${index + 1}", color = module.color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Text(lesson.title, color = White, fontSize = 12.sp, modifier = Modifier.padding(start = 11.dp))
            }
        }
        GlowCard(Modifier.fillMaxWidth().padding(top = 10.dp), Green) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Estimated time", color = Muted, fontSize = 10.sp); Text("40 minutes", color = White, fontSize = 11.sp)
            }
            Row(Modifier.fillMaxWidth().padding(top = 7.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Potential reward", color = Muted, fontSize = 10.sp); Text("500 XP", color = Orange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Button(
            onClick = openLessons, modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(module.color), shape = RoundedCornerShape(11.dp)
        ) { Text(if (completed > 0) "Continue module" else "Start first lesson", color = Night, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun ModuleLessonsPage(module: CurriculumModule, completedLessons: Set<String>, onLesson: (String) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)) {
        itemsIndexed(module.lessons) { index, lesson ->
            val done = "${module.title}/${lesson.title}" in completedLessons
            Card(
                Modifier.fillMaxWidth().padding(bottom = 9.dp).clickable { onLesson(lesson.title) },
                colors = CardDefaults.cardColors(Panel),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (done) Green.copy(.55f) else Border),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp), contentAlignment = Alignment.Center) {
                        AtomGraphic(43.dp, if (done) Green else module.color)
                        Text(if (done) "✓" else "${index + 1}", color = White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(lesson.title, color = White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text("4 pages · Concept · Visual Lab · 15-question check", color = Muted, fontSize = 9.sp)
                    }
                    Text("›", color = module.color, fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
private fun ModuleFormulaPage(module: CurriculumModule) {
    var expanded by remember(module.title) { mutableIntStateOf(-1) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        item {
            Text("Tap a formula to reveal its lesson context.", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp))
        }
        itemsIndexed(module.lessons) { index, lesson ->
            GlowCard(
                Modifier.fillMaxWidth().padding(bottom = 9.dp).clickable { expanded = if (expanded == index) -1 else index },
                module.color
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(lesson.title.uppercase(), color = Muted, fontSize = 8.sp)
                        Text(lesson.formula, color = module.color, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
                    }
                    Text(if (expanded == index) "−" else "+", color = module.color, fontSize = 20.sp)
                }
                if (expanded == index) {
                    Text(lesson.concept, color = White, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 10.dp))
                }
            }
        }
    }
}

@Composable
private fun ModuleProgressPage(module: CurriculumModule, completed: Int) {
    val total = module.lessons.size.coerceAtLeast(1)
    val percent = completed * 100 / total
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(132.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                drawArc(Border, -90f, 360f, false, style = Stroke(12f, cap = StrokeCap.Round))
                drawArc(Brush.sweepGradient(listOf(Purple, module.color)), -90f, 360f * completed / total.toFloat(), false, style = Stroke(12f, cap = StrokeCap.Round))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$percent%", color = White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text("MASTERY", color = Muted, fontSize = 8.sp)
            }
        }
        Text(if (completed == total) "Module mastered" else "${total - completed} lessons remaining", color = module.color, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 13.dp))
        Row(Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            ModuleStat("LESSONS", "$completed/$total", module.color, Modifier.weight(1f))
            ModuleStat("XP", "${completed * 100}", Orange, Modifier.weight(1f))
            ModuleStat("LEVEL", if (completed >= 4) "Expert" else if (completed >= 2) "Growing" else "Starter", Green, Modifier.weight(1f))
        }
        GlowCard(Modifier.fillMaxWidth().padding(top = 18.dp), module.color) {
            Text("MASTERY MILESTONES", color = Muted, fontSize = 9.sp)
            listOf("Complete all concept pages", "Run every visual lab", "Pass every 15-question knowledge check").forEachIndexed { index, goal ->
                Row(Modifier.fillMaxWidth().padding(top = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (completed > index) "✓" else "○", color = if (completed > index) Green else Muted)
                    Text(goal, color = White, fontSize = 11.sp, modifier = Modifier.padding(start = 10.dp))
                }
            }
        }
    }
}

@Composable
private fun ModuleFaqPage(module: CurriculumModule) {
    val faqs = remember(module.title) { moduleFaqs(module) }
    var expanded by remember(module.title) { mutableIntStateOf(-1) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        item {
            Text(
                "20 frequently asked questions for ${module.title}. Tap a question to expand its answer.",
                color = Muted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        itemsIndexed(faqs) { index, faq ->
            Card(
                Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable {
                    expanded = if (expanded == index) -1 else index
                },
                colors = CardDefaults.cardColors(Panel),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (expanded == index) module.color.copy(.7f) else Border),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(13.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${index + 1}", color = module.color, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
                        Text(faq.question, color = White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text(if (expanded == index) "−" else "+", color = module.color, fontSize = 18.sp)
                    }
                    if (expanded == index) {
                        Text(faq.answer, color = Muted, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(start = 28.dp, top = 10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ModuleStat(label: String, value: String, color: Color, modifier: Modifier) {
    Column(modifier.background(Panel, RoundedCornerShape(11.dp)).border(1.dp, Border, RoundedCornerShape(11.dp)).padding(11.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(label, color = Muted, fontSize = 7.sp)
    }
}

@Composable
internal fun CurriculumLessonScreen(
    moduleTitle: String,
    lessonTitle: String,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onQuiz: () -> Unit
) {
    val module = curriculum.firstOrNull { it.title == moduleTitle } ?: curriculum.first()
    val lesson = module.lessons.firstOrNull { it.title == lessonTitle } ?: module.lessons.first()
    val context = LocalContext.current
    val repository = remember(context) {
        (context.applicationContext as ChemistryExplorerApplication).lessonPageRepository
    }
    val sqliteLessonId = remember(module.title, lesson.title) { lessonPageId(module.title, lesson.title) }
    val sqlitePages by repository.observeLessonPages(sqliteLessonId).collectAsState(initial = emptyList())
    LaunchedEffect(sqliteLessonId) {
        repository.replaceSelectedLesson(module, lesson)
    }
    var page by remember(moduleTitle, lessonTitle) { mutableIntStateOf(0) }
    val checks = remember(moduleTitle, lessonTitle) { knowledgeChecksFor(module, lesson) }
    var challengeIndex by remember(moduleTitle, lessonTitle) { mutableIntStateOf(0) }
    var challengeAnswers by remember(moduleTitle, lessonTitle) { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var challengeConfidence by remember(moduleTitle, lessonTitle) { mutableStateOf<Map<Int, String>>(emptyMap()) }
    val dbPages = remember(sqlitePages) { sqlitePages.takeIf { it.size == 5 } }
    val pageLabels = dbPages?.map { it.pageTitle } ?: listOf("Concept", "Visual", "Example", "Lab", "Challenge")
    Column(Modifier.fillMaxSize()) {
        ScreenTitle(lesson.title, "${module.title} · Page ${page + 1}/5", onBack)
        Breadcrumb("Curriculum › ${module.title} › ${lesson.title}", Modifier.padding(horizontal = 20.dp))
        LessonProgressStrip(page, 5, module.color, pageLabels)
        AnimatedContent(page, modifier = Modifier.weight(1f), label = "curriculum-page") { current ->
            val savedPages = dbPages
            if (savedPages != null) {
                DbLessonHtmlPage(
                    module = module,
                    lesson = lesson,
                    page = savedPages[current],
                    isLast = current == savedPages.lastIndex,
                    onComplete = onComplete,
                    onQuiz = onQuiz
                )
            } else {
                when (current) {
                0 -> LessonConceptPage(module, lesson)
                1 -> InteractiveVisualPage(module, lesson)
                2 -> WorkedExampleLessonPage(module, lesson)
                3 -> LessonLabLinkPage(module, lesson)
                else -> LessonChallengeWithRevisionPage(
                    module = module,
                    lesson = lesson,
                    checks = checks,
                    questionIndex = challengeIndex,
                    selected = challengeAnswers[challengeIndex] ?: -1,
                    confidence = challengeConfidence[challengeIndex].orEmpty(),
                    choose = { choice -> challengeAnswers = challengeAnswers + (challengeIndex to choice) },
                    setConfidence = { value -> challengeConfidence = challengeConfidence + (challengeIndex to value) },
                    next = { challengeIndex = (challengeIndex + 1).coerceAtMost(checks.lastIndex) },
                    previous = { challengeIndex = (challengeIndex - 1).coerceAtLeast(0) },
                    score = checks.indices.count { challengeAnswers[it] == checks[it].correct },
                    answered = challengeAnswers.size,
                    answers = challengeAnswers,
                    confidenceMap = challengeConfidence,
                    onComplete = onComplete,
                    onQuiz = onQuiz
                )
            }
        }
        }
        if (page < 4) {
            Row(
                Modifier.fillMaxWidth().background(Color(0xFF030A13)).padding(18.dp, 11.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (page > 0) {
                    Button(
                        onClick = { page-- }, modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(Panel), shape = RoundedCornerShape(11.dp)
                    ) { Text("Previous", color = White) }
                }
                Button(
                    onClick = { page++ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(module.color, disabledContainerColor = Border), shape = RoundedCornerShape(11.dp)
                ) {
                    Text(
                        "Next page",
                        color = Night, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DbLessonHtmlPage(
    module: CurriculumModule,
    lesson: CurriculumLesson,
    page: LessonPageEntity,
    isLast: Boolean,
    onComplete: () -> Unit,
    onQuiz: () -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Tag("SQLITE LESSON PAGE", module.color)
        GlowCard(Modifier.fillMaxWidth().padding(top = 10.dp), module.color) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(70.dp), contentAlignment = Alignment.Center) {
                    AtomGraphic(66.dp, module.color)
                    Text("${page.pageNo}", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(page.pageTitle, color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "${lesson.title} - saved in lesson_pages",
                        color = Muted,
                        fontSize = 10.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
            }
        }
        Card(
            Modifier.fillMaxWidth().padding(top = 14.dp),
            colors = CardDefaults.cardColors(Panel),
            border = androidx.compose.foundation.BorderStroke(1.dp, Border),
            shape = RoundedCornerShape(14.dp)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                factory = { viewContext ->
                    TextView(viewContext).apply {
                        setTextColor(White.toArgb())
                        textSize = 16f
                        setLineSpacing(8f, 1f)
                        includeFontPadding = true
                    }
                },
                update = { textView ->
                    textView.text = Html.fromHtml(page.pageContent, Html.FROM_HTML_MODE_COMPACT)
                }
            )
        }
        GlowCard(Modifier.fillMaxWidth().padding(top = 12.dp), Cyan) {
            Text("QUALITY CHECK", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(
                "Simple English, daily-life examples, practical observations, no unnecessary advanced theory.",
                color = White,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 7.dp)
            )
        }
        if (isLast) {
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(Green),
                    shape = RoundedCornerShape(11.dp)
                ) { Text("Complete", color = Night, fontWeight = FontWeight.Bold) }
                Button(
                    onClick = onQuiz,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(module.color),
                    shape = RoundedCornerShape(11.dp)
                ) { Text("Quiz", color = Night, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun LessonConceptPage(module: CurriculumModule, lesson: CurriculumLesson) {
    var formulaOpen by remember(module.title, lesson.title) { mutableStateOf(false) }
    var knowMoreOpen by remember(module.title, lesson.title) { mutableStateOf(false) }
    val expansion = remember(module.level, module.title, lesson.title) { learningExpansionFor(module, lesson) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Tag("THEORY + EXAMPLE", module.color)
        LessonVoiceControls(module, lesson)
        ChemistryLessonVisual(module.color, module.icon, .48f)
        Text(lesson.title, color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("THEORY", color = module.color, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Text(lesson.theory, color = White, fontSize = 13.sp, lineHeight = 21.sp, modifier = Modifier.padding(top = 7.dp, bottom = 16.dp))
        TapToDefineGlossary(module, lesson)
        GlowCard(Modifier.fillMaxWidth().padding(bottom = 12.dp), Cyan) {
            Text("MICRO-CONCEPT MAP · ${expansion.microConcepts.size}", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            expansion.microConcepts.forEachIndexed { index, micro ->
                Text("${index + 1}. ${micro.title}", color = White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 10.dp))
                Text(micro.explanation, color = Muted, fontSize = 10.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 3.dp))
                Text("Mastery: ${micro.masteryPrompt}", color = Cyan, fontSize = 9.sp, lineHeight = 14.sp, modifier = Modifier.padding(top = 3.dp))
            }
        }
        GlowCard(Modifier.fillMaxWidth().clickable { formulaOpen = !formulaOpen }, module.color) {
            Text("KEY RELATIONSHIP", color = Muted, fontSize = 9.sp)
            Text(lesson.formula, color = module.color, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 7.dp))
            FormulaVariables(lesson.formula, module.color)
            FormulaVariableCards(lesson.formula, module.color)
            Text(if (formulaOpen) formulaMeaning(module.title) else "Tap to explain variables and meaning", color = if (formulaOpen) White else Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 7.dp))
        }
        GlowCard(Modifier.fillMaxWidth().padding(top = 12.dp), Green) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AtomGraphic(54.dp, Green)
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("WORKED EXAMPLE", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(lesson.workedExample, color = White, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 6.dp))
                }
            }
            Text("REASONING STEPS", color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
            lesson.exampleSteps.forEachIndexed { index, step ->
                Row(Modifier.fillMaxWidth().padding(top = 7.dp), verticalAlignment = Alignment.Top) {
                    Box(
                        Modifier.size(22.dp).background(Green.copy(.14f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(step, color = White, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.weight(1f).padding(start = 9.dp))
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            GlowCard(Modifier.weight(1f), Orange) {
                Text("COMMON MISTAKE", color = Orange, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                Text(lesson.commonMistake, color = White, fontSize = 10.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 6.dp))
            }
            GlowCard(Modifier.weight(1f), Cyan) {
                Text("WHY IT MATTERS", color = Cyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                Text(lesson.realWorldApplication, color = White, fontSize = 10.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 6.dp))
            }
        }
        Button(
            onClick = { knowMoreOpen = !knowMoreOpen },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            colors = ButtonDefaults.buttonColors(if (knowMoreOpen) Purple else Panel),
            shape = RoundedCornerShape(11.dp)
        ) {
            Text(if (knowMoreOpen) "Hide advanced concepts" else "Know More · Advanced concepts", color = if (knowMoreOpen) White else Cyan)
        }
        if (knowMoreOpen) {
            GlowCard(Modifier.fillMaxWidth().padding(top = 9.dp), Purple) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AtomGraphic(64.dp, Purple)
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text("GO DEEPER", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        lesson.advancedConcepts.forEach { advanced ->
                            Text("• $advanced", color = White, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
                Text(
                    "These extensions connect the current lesson with undergraduate and postgraduate chemistry. Master the core idea first, then return here.",
                    color = Muted,
                    fontSize = 10.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 17.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniFact("READ", "6–8 min", module.color, Modifier.weight(1f))
            MiniFact("VISUAL", "Interactive", Green, Modifier.weight(1f))
            MiniFact("DEPTH", "Core +", Orange, Modifier.weight(1f))
        }
    }
}

@Composable
private fun LessonVoiceControls(module: CurriculumModule, lesson: CurriculumLesson) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var speaking by remember(module.title, lesson.title) { mutableStateOf(false) }
    DisposableEffect(Unit) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.getDefault()
        }
        tts = engine
        onDispose { engine.shutdown() }
    }
    Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LessonActionPill(if (speaking) "Stop voice" else "Read aloud", module.color, Modifier.weight(1f)) {
            if (speaking) {
                tts?.stop()
                speaking = false
            } else {
                speaking = true
                tts?.speak(lessonVoiceScript(module, lesson), TextToSpeech.QUEUE_FLUSH, null, lesson.title)
            }
        }
        LessonActionPill("Revision summary", Cyan, Modifier.weight(1f)) {
            tts?.speak(revisionSummary(module, lesson), TextToSpeech.QUEUE_FLUSH, null, "${lesson.title}-summary")
            speaking = true
        }
    }
}

@Composable
private fun LessonActionPill(label: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Text(
        label,
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier.clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = .10f))
            .border(1.dp, color.copy(alpha = .34f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 9.dp)
    )
}

@Composable
private fun TapToDefineGlossary(module: CurriculumModule, lesson: CurriculumLesson) {
    var selected by remember(module.title, lesson.title) { mutableStateOf<String?>(null) }
    val terms = remember(module.title, lesson.title) { glossaryTermsFor(module, lesson) }
    GlowCard(Modifier.fillMaxWidth().padding(bottom = 12.dp), Purple) {
        Text("TAP TO DEFINE", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            terms.forEach { term ->
                Text(
                    term,
                    color = if (selected == term) Night else White,
                    fontSize = 10.sp,
                    maxLines = 1,
                    modifier = Modifier.clip(CircleShape)
                        .background(if (selected == term) Purple else Panel2)
                        .border(1.dp, Purple.copy(alpha = .42f), CircleShape)
                        .clickable { selected = if (selected == term) null else term }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                )
            }
        }
        selected?.let {
            Text(glossaryDefinition(it, module, lesson), color = White, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 10.dp))
        }
    }
}

@Composable
private fun FormulaVariableCards(formula: String, accent: Color) {
    val variables = formulaVariablesFor(formula)
    if (variables.isEmpty()) return
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 9.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        variables.forEach { variable ->
            Column(
                Modifier.width(106.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(accent.copy(alpha = .08f))
                    .border(1.dp, accent.copy(alpha = .30f), RoundedCornerShape(9.dp))
                    .padding(9.dp)
            ) {
                Text(variable.symbol, color = accent, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text(variable.unit, color = White, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(variable.example, color = Muted, fontSize = 8.sp, lineHeight = 11.sp, maxLines = 2)
            }
        }
    }
}

private data class FormulaVariable(val symbol: String, val unit: String, val example: String)

@Composable
private fun WorkedExampleLessonPage(module: CurriculumModule, lesson: CurriculumLesson) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Tag("WORKED EXAMPLE", Green)
        Text(lesson.title, color = White, fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        GlowCard(Modifier.fillMaxWidth().padding(top = 14.dp), Green) {
            Text("MODEL ANSWER", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(lesson.workedExample, color = White, fontSize = 13.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 8.dp))
        }
        Text("REVEALABLE REASONING", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp, bottom = 6.dp))
        lesson.exampleSteps.forEachIndexed { index, step ->
            var open by remember(module.title, lesson.title, index) { mutableStateOf(index == 0) }
            GlowCard(Modifier.fillMaxWidth().padding(bottom = 9.dp).clickable { open = !open }, if (open) Green else Border) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(28.dp).background(Green.copy(alpha = .14f), CircleShape), contentAlignment = Alignment.Center) {
                        Text("${index + 1}", color = Green, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Step ${index + 1}", color = White, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(start = 10.dp))
                    Text(if (open) "-" else "+", color = Green, fontSize = 18.sp)
                }
                if (open) Text(step, color = Muted, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun LessonLabLinkPage(module: CurriculumModule, lesson: CurriculumLesson) {
    val expansion = remember(module.level, module.title, lesson.title) { learningExpansionFor(module, lesson) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Tag("LAB LINK", Cyan)
        Text("Connect the idea to evidence", color = White, fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        Text("This mini lab turns the lesson into a measurable investigation before the final challenge.", color = Muted, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 6.dp, bottom = 12.dp))
        AuthenticLaboratoryPanel(expansion.virtualLab, module.color, .58f)
        GlowCard(Modifier.fillMaxWidth().padding(top = 12.dp), Orange) {
            Text("LAB QUESTION", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("Which observation would prove the model is working, and which control would expose a false positive?", color = White, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 7.dp))
        }
    }
}

@Composable
private fun LessonChallengeWithRevisionPage(
    module: CurriculumModule,
    lesson: CurriculumLesson,
    checks: List<KnowledgeCheck>,
    questionIndex: Int,
    selected: Int,
    confidence: String,
    choose: (Int) -> Unit,
    setConfidence: (String) -> Unit,
    next: () -> Unit,
    previous: () -> Unit,
    score: Int,
    answered: Int,
    answers: Map<Int, Int>,
    confidenceMap: Map<Int, String>,
    onComplete: () -> Unit,
    onQuiz: () -> Unit
) {
    if (answered == checks.size) {
        LessonSummaryPage(module, lesson, checks, answers, confidenceMap, onComplete, onQuiz)
    } else {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        EnhancedLessonChallengePage(module, checks, questionIndex, selected, confidence, choose, setConfidence, next, previous)
        if (answered == checks.size) {
            LessonSummaryPage(module, lesson, checks, answers, confidenceMap, onComplete, onQuiz)
        } else {
            GlowCard(Modifier.fillMaxWidth().padding(18.dp), Cyan) {
                Text("REVISION SUMMARY", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(revisionSummary(module, lesson), color = White, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 7.dp))
                Text("${answered}/${checks.size} checks answered · $score correct so far", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
    }
}

private fun lessonVoiceScript(module: CurriculumModule, lesson: CurriculumLesson): String =
    "${lesson.title}. ${lesson.concept}. Key relationship: ${lesson.formula}. Common mistake: ${lesson.commonMistake}. Why this matters: ${lesson.realWorldApplication}."

private fun revisionSummary(module: CurriculumModule, lesson: CurriculumLesson): String =
    "In ${module.title}, remember ${lesson.concept} Use ${lesson.formula} with consistent units. Avoid this mistake: ${lesson.commonMistake}"

private fun glossaryTermsFor(module: CurriculumModule, lesson: CurriculumLesson): List<String> {
    val candidates = (lesson.title.split(' ', '-', ':') + lesson.concept.split(' ', ',', '.', ';') + module.category.split(' '))
        .map { it.trim().trim('(', ')') }
        .filter { it.length > 4 && it.none { ch -> ch.isDigit() } }
    return candidates.distinctBy { it.lowercase() }.take(8).ifEmpty { listOf("model", "evidence", "variable") }
}

private fun glossaryDefinition(term: String, module: CurriculumModule, lesson: CurriculumLesson): String {
    val lower = term.lowercase()
    return when {
        "electron" in lower -> "Electrons are negatively charged particles that control bonding, ions and many chemical properties."
        "energy" in lower -> "Energy tracks the capacity for change; chemistry often follows transfers between particles, fields and surroundings."
        "equilibrium" in lower -> "Equilibrium means forward and reverse processes continue at equal rates, so measured composition stays steady."
        "concentration" in lower -> "Concentration describes amount of solute per amount of solution, often in mol L-1."
        "oxidation" in lower -> "Oxidation is electron loss or an increase in oxidation state."
        "covalent" in lower -> "A covalent bond forms when atoms share electron pairs."
        else -> "$term is used here as part of ${module.category.lowercase()}. In this lesson, connect it to: ${lesson.concept}"
    }
}

private fun formulaVariablesFor(formula: String): List<FormulaVariable> {
    val text = formula.lowercase()
    return when {
        "pv" in text || "nrt" in text -> listOf(
            FormulaVariable("P", "pressure", "101 kPa"),
            FormulaVariable("V", "volume", "2.0 L"),
            FormulaVariable("n", "amount", "0.50 mol"),
            FormulaVariable("T", "kelvin", "298 K")
        )
        "ph" in text || "log" in text -> listOf(
            FormulaVariable("pH", "unitless", "pH 3 acid"),
            FormulaVariable("[H+]", "mol L-1", "1e-3 M"),
            FormulaVariable("log", "base 10", "inverse scale")
        )
        "q" in text || "mc" in text -> listOf(
            FormulaVariable("q", "joule", "heat change"),
            FormulaVariable("m", "gram", "sample mass"),
            FormulaVariable("c", "J g-1 K-1", "specific heat"),
            FormulaVariable("dT", "kelvin", "temperature change")
        )
        "rate" in text || "k[" in text -> listOf(
            FormulaVariable("rate", "mol L-1 s-1", "reaction speed"),
            FormulaVariable("k", "rate constant", "depends on T"),
            FormulaVariable("[A]", "mol L-1", "reactant level")
        )
        else -> formula.filter { it.isLetter() }.map { it.toString() }.distinct().take(4)
            .map { FormulaVariable(it, "variable", "track units") }
    }
}

@Composable
private fun InteractiveVisualPage(module: CurriculumModule, lesson: CurriculumLesson) {
    var control by remember(module.title, lesson.title) { mutableFloatStateOf(.45f) }
    var running by remember(module.title, lesson.title) { mutableStateOf(true) }
    var simulationIndex by remember(module.level, module.title, lesson.title) { mutableIntStateOf(0) }
    var visualIndex by remember(module.level, module.title, lesson.title) { mutableIntStateOf(0) }
    var cameraResetToken by remember(module.level, module.title, lesson.title) { mutableIntStateOf(0) }
    var cameraPreset by remember(module.level, module.title, lesson.title) { mutableIntStateOf(0) }
    var slowMotion by remember(module.level, module.title, lesson.title) { mutableStateOf(false) }
    var compareMode by remember(module.level, module.title, lesson.title) { mutableStateOf(false) }
    var challengeMode by remember(module.level, module.title, lesson.title) { mutableStateOf(false) }
    var reducedMotion by remember(module.level, module.title, lesson.title) { mutableStateOf(false) }
    var qualityLevel by remember(module.level, module.title, lesson.title) { mutableIntStateOf(2) }
    var capturedState by remember(module.level, module.title, lesson.title) { mutableStateOf<String?>(null) }
    val haptics = LocalHapticFeedback.current
    val expansion = remember(module.level, module.title, lesson.title) { learningExpansionFor(module, lesson) }
    val simulation = expansion.simulations[simulationIndex]
    val visual = expansion.visualizations3d[visualIndex]
    var inspectedEntity by remember(visual.id) { mutableStateOf(visual.entityLabels.first()) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Tag("VISUAL LAB", Green)
        Text("Manipulate the model", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        Text("${expansion.simulations.size} simulations · ${expansion.visualizations3d.size} procedural 3D views", color = Muted, fontSize = 12.sp)
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            expansion.simulations.forEachIndexed { index, item ->
                Text(
                    item.title, color = if (index == simulationIndex) Night else Muted, fontSize = 8.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                        .background(if (index == simulationIndex) module.color else Panel, RoundedCornerShape(8.dp))
                        .border(1.dp, if (index == simulationIndex) module.color else Border, RoundedCornerShape(8.dp))
                        .clickable { simulationIndex = index; control = .45f }
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }
        }
        Text(simulation.objective, color = White, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 9.dp))
        ChemistryLessonVisual(module.color, module.icon, if (running) control else .05f)
        GlowCard(Modifier.fillMaxWidth(), module.color) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(simulation.controlLabel, color = White, fontSize = 12.sp)
                Text("${(control * 100).toInt()}%", color = module.color, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = control, onValueChange = { control = it },
                colors = SliderDefaults.colors(thumbColor = module.color, activeTrackColor = module.color, inactiveTrackColor = Border)
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("LOW", color = Muted, fontSize = 8.sp)
                Text("Drag to experiment", color = Green, fontSize = 9.sp)
                Text("HIGH", color = Muted, fontSize = 8.sp)
            }
        }
        GlowCard(Modifier.fillMaxWidth().padding(top = 11.dp), Green) {
            Text("LIVE OBSERVATION", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(
                "${if (control < .5f) simulation.lowState else simulation.highState}. ${simulation.observationGuide}",
                color = White,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
        AuthenticLaboratoryPanel(expansion.virtualLab, module.color, control)
        GlowCard(Modifier.fillMaxWidth().padding(top = 11.dp), Purple) {
            Text("PROCEDURAL 3D MODEL SET", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                expansion.visualizations3d.forEachIndexed { index, item ->
                    Text(
                        "${index + 1}", color = if (index == visualIndex) Night else Muted, fontSize = 9.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                            .background(if (index == visualIndex) Purple else Panel, RoundedCornerShape(7.dp))
                            .clickable { visualIndex = index }
                            .padding(vertical = 7.dp)
                    )
                }
            }
            Text(visual.title, color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 9.dp))
            Text(
                "CHEMISTRY SCENE · ${visual.motif.name.replace('_', ' ')}",
                color = module.color,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 3.dp)
            )
            Text(visual.description, color = Muted, fontSize = 10.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 4.dp))
            Text(
                "ENTITIES · ${visual.entityLabels.take(5).joinToString(" · ")}",
                color = Cyan,
                fontSize = 8.sp,
                lineHeight = 13.sp,
                modifier = Modifier.padding(top = 7.dp)
            )
            Text(
                "MODEL · ${visual.keyRelationship}",
                color = module.color,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
            AnimatedContent(
                targetState = visual,
                transitionSpec = {
                    (fadeIn(tween(420)) + scaleIn(tween(520), initialScale = .92f)) togetherWith
                        (fadeOut(tween(260)) + scaleOut(tween(300), targetScale = 1.04f))
                },
                label = "chemistry-scene-transition"
            ) { activeVisual ->
                Procedural3DVisualization(
                    spec = activeVisual,
                    control = control,
                    accent = module.color,
                    running = running,
                    cameraResetToken = cameraResetToken,
                    cameraPreset = cameraPreset,
                    slowMotion = slowMotion,
                    compareMode = compareMode,
                    reducedMotion = reducedMotion,
                    qualityLevel = qualityLevel,
                    challengeMode = challengeMode,
                    onInspect = {
                        inspectedEntity = it
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                )
            }
            Row(
                Modifier.fillMaxWidth().padding(top = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                listOf("AUTO ORBIT", "DRAG", "PINCH", "TAP").forEach { command ->
                    Text(
                        command,
                        color = if (command == "TAP") module.color else Muted,
                        fontSize = 7.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                            .background(Panel.copy(.78f), RoundedCornerShape(6.dp))
                            .border(1.dp, Border, RoundedCornerShape(6.dp))
                            .padding(vertical = 5.dp)
                    )
                }
            }
            Text(
                if (challengeMode) "INSPECTING · HIDDEN" else "INSPECTING · $inspectedEntity · ${visual.motionStyle.name.replace('_', ' ')}",
                color = Green,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp)
            )
            val stageIndex = (control * (visual.stageLabels.size - 1).coerceAtLeast(1)).roundToInt()
                .coerceIn(0, visual.stageLabels.lastIndex)
            Text(
                if (challengeMode) {
                    "CHALLENGE · Identify the highlighted entity before revealing it"
                } else {
                    "STAGE ${stageIndex + 1}/${visual.stageLabels.size} · ${visual.stageLabels[stageIndex]}"
                },
                color = if (challengeMode) Orange else White,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 7.dp)
            )
            Slider(
                value = control,
                onValueChange = { control = it; running = false },
                colors = SliderDefaults.colors(
                    thumbColor = module.color,
                    activeTrackColor = module.color,
                    inactiveTrackColor = Border
                )
            )
            Text(
                "PROBE · ${visual.axisLabels[0]} ${(control * 100).roundToInt()}% · ${visual.axisLabels[1]} ${((1f - control) * 100).roundToInt()}%",
                color = Cyan,
                fontSize = 8.sp
            )
            Row(
                Modifier.fillMaxWidth().padding(top = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                listOf("OVERVIEW", "ACTIVE SITE", "EVIDENCE").forEachIndexed { index, preset ->
                    Text(
                        preset,
                        color = if (cameraPreset == index) Night else Muted,
                        fontSize = 7.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                            .background(if (cameraPreset == index) module.color else Panel, RoundedCornerShape(6.dp))
                            .clickable { cameraPreset = index; cameraResetToken++ }
                            .padding(vertical = 6.dp)
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(top = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                listOf(
                    "SLOW" to slowMotion,
                    "COMPARE" to compareMode,
                    "CHALLENGE" to challengeMode,
                    "REDUCE" to reducedMotion
                ).forEachIndexed { index, (label, enabled) ->
                    Text(
                        label,
                        color = if (enabled) Night else Muted,
                        fontSize = 7.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                            .background(if (enabled) listOf(Cyan, Purple, Orange, Green)[index] else Panel, RoundedCornerShape(6.dp))
                            .clickable {
                                when (index) {
                                    0 -> slowMotion = !slowMotion
                                    1 -> compareMode = !compareMode
                                    2 -> challengeMode = !challengeMode
                                    else -> reducedMotion = !reducedMotion
                                }
                            }
                            .padding(vertical = 6.dp)
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(top = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    "QUALITY ${listOf("ECO", "BALANCED", "ULTRA")[qualityLevel]}",
                    color = Muted,
                    fontSize = 7.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).background(Panel, RoundedCornerShape(6.dp))
                        .clickable { qualityLevel = (qualityLevel + 1) % 3 }
                        .padding(vertical = 6.dp)
                )
                Text(
                    "CAPTURE STATE",
                    color = Cyan,
                    fontSize = 7.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).background(Panel, RoundedCornerShape(6.dp))
                        .clickable {
                            capturedState = "${visual.conceptTitle} · stage ${stageIndex + 1} · ${(control * 100).roundToInt()}%"
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        .padding(vertical = 6.dp)
                )
            }
            capturedState?.let {
                Text("SNAPSHOT · $it", color = Purple, fontSize = 8.sp, modifier = Modifier.padding(top = 5.dp))
            }
            Text(
                "PATH · ${visual.stageLabels.take(4).joinToString(" → ")}",
                color = Muted,
                fontSize = 8.sp,
                lineHeight = 13.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
            Text(visual.interactionHint, color = Cyan, fontSize = 9.sp, modifier = Modifier.padding(top = 5.dp))
        }
        Row(Modifier.fillMaxWidth().padding(top = 11.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(if (running) "Ⅱ  Pause" else "▶  Play", "↺  Reset", "↻  Replay").forEachIndexed { index, label ->
                Text(
                    label, color = if (index == 0) Cyan else Muted, fontSize = 10.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).background(Panel, RoundedCornerShape(9.dp)).border(1.dp, Border, RoundedCornerShape(9.dp))
                        .clickable {
                            when (index) {
                                0 -> running = !running
                                1 -> { control = .45f; running = false; cameraResetToken++ }
                                else -> { control = .15f; running = true; cameraResetToken++ }
                            }
                        }.padding(vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun AuthenticLaboratoryPanel(
    lab: AuthenticLabSpec,
    accent: Color,
    control: Float
) {
    var expanded by remember(lab.id) { mutableStateOf(false) }
    var ppeChecks by remember(lab.id) { mutableStateOf(emptySet<Int>()) }
    var calibrationChecks by remember(lab.id) { mutableStateOf(emptySet<Int>()) }
    var procedureStep by remember(lab.id) { mutableIntStateOf(0) }
    var replicateCount by remember(lab.id) { mutableIntStateOf(0) }
    var notebook by remember(lab.id) { mutableStateOf(emptyList<String>()) }
    val ppeReady = ppeChecks.size == lab.ppe.size
    val calibrated = calibrationChecks.size == lab.calibrationSteps.size
    val unlocked = ppeReady && calibrated
    val primaryMeasurement = lab.measurements.first()
    val seed = lab.id.fold(19) { hash, character -> (hash * 31 + character.code) and 0x7fffffff }
    val measuredValue = 10f + (seed % 700) / 10f + control * 12f + replicateCount * .07f

    GlowCard(Modifier.fillMaxWidth().padding(top = 11.dp), Orange) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text("AUTHENTIC LABORATORY", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(lab.title, color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                Text(
                    "${lab.mode.name.replace('_', ' ')} · ${lab.procedure.size} controlled steps · ${lab.measurements.size} measurement channels",
                    color = Muted,
                    fontSize = 8.sp,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
            Text(
                if (expanded) "CLOSE" else "ENTER LAB",
                color = if (expanded) Muted else Night,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.background(if (expanded) Panel else Orange, RoundedCornerShape(7.dp))
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 9.dp, vertical = 7.dp)
            )
        }
        if (!expanded) {
            Text(lab.objective, color = Muted, fontSize = 10.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 7.dp))
            return@GlowCard
        }

        Text(lab.objective, color = White, fontSize = 10.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 8.dp))
        Text("1 · PPE AND RISK GATE", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 11.dp))
        lab.ppe.forEachIndexed { index, item ->
            val selected = index in ppeChecks
            Text(
                "${if (selected) "✓" else "○"} $item",
                color = if (selected) Green else Muted,
                fontSize = 9.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    .background(if (selected) Green.copy(.08f) else Panel.copy(.6f), RoundedCornerShape(6.dp))
                    .clickable {
                        ppeChecks = if (selected) ppeChecks - index else ppeChecks + index
                    }
                    .padding(7.dp)
            )
        }

        Text("2 · APPARATUS AND TRACEABILITY", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 11.dp))
        lab.apparatus.forEach { item ->
            Text(
                "${item.name} · ${item.specification}\nCHECK · ${item.preparationCheck}",
                color = White,
                fontSize = 9.sp,
                lineHeight = 14.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp).background(Panel.copy(.65f), RoundedCornerShape(6.dp)).padding(7.dp)
            )
        }

        Text("3 · REAGENTS / SAMPLES", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 11.dp))
        lab.materials.forEach { item ->
            Text(
                "${item.name} · ${item.identity} · ${item.workingAmount}\nHAZARD · ${item.hazard}\nWASTE · ${item.wasteStream}",
                color = Muted,
                fontSize = 8.sp,
                lineHeight = 13.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp).border(1.dp, Border, RoundedCornerShape(6.dp)).padding(7.dp)
            )
        }

        Text("4 · CALIBRATION GATE", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 11.dp))
        lab.calibrationSteps.forEachIndexed { index, item ->
            val selected = index in calibrationChecks
            Text(
                "${if (selected) "PASS" else "VERIFY"} · $item",
                color = if (selected) Green else Cyan,
                fontSize = 8.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    .clickable { calibrationChecks = if (selected) calibrationChecks - index else calibrationChecks + index }
                    .padding(vertical = 5.dp)
            )
        }

        Text(
            if (unlocked) "PROCEDURE UNLOCKED" else "PROCEDURE LOCKED · COMPLETE PPE AND CALIBRATION",
            color = if (unlocked) Green else Orange,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 9.dp)
        )
        val currentStep = lab.procedure[procedureStep]
        Text(
            "STEP ${procedureStep + 1}/${lab.procedure.size} · ${currentStep.instruction}",
            color = if (unlocked) White else Muted,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp).background(Panel, RoundedCornerShape(8.dp)).padding(9.dp)
        )
        Text("CHECKPOINT · ${currentStep.checkpoint}", color = Cyan, fontSize = 8.sp, modifier = Modifier.padding(top = 5.dp))
        Text("EXPECTED · ${currentStep.expectedObservation}", color = Green, fontSize = 8.sp, modifier = Modifier.padding(top = 3.dp))
        Text("IF FAILED · ${currentStep.failureConsequence}", color = Orange, fontSize = 8.sp, modifier = Modifier.padding(top = 3.dp))
        Row(Modifier.fillMaxWidth().padding(top = 7.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("PREVIOUS", "RECORD", "NEXT").forEachIndexed { index, label ->
                Text(
                    label,
                    color = if (unlocked) Cyan else Muted,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).background(Panel, RoundedCornerShape(7.dp))
                        .clickable(enabled = unlocked) {
                            when (index) {
                                0 -> procedureStep = (procedureStep - 1).coerceAtLeast(0)
                                1 -> notebook = notebook + "Step ${procedureStep + 1}: ${currentStep.expectedObservation}"
                                else -> procedureStep = (procedureStep + 1).coerceAtMost(lab.procedure.lastIndex)
                            }
                        }
                        .padding(vertical = 7.dp)
                )
            }
        }

        Text("5 · MEASUREMENT AND REPLICATES", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 11.dp))
        lab.measurements.forEach { measurement ->
            Text(
                "${measurement.quantity} · ${measurement.instrument}\n${measurement.workingRange} ${measurement.unit} · uncertainty ${measurement.uncertainty} · n=${measurement.replicateCount}",
                color = White,
                fontSize = 8.sp,
                lineHeight = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Text(
            "RUN REPLICATE ${replicateCount + 1}",
            color = if (unlocked) Night else Muted,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 7.dp)
                .background(if (unlocked) Green else Panel, RoundedCornerShape(7.dp))
                .clickable(enabled = unlocked) {
                    replicateCount++
                    notebook = notebook + "Replicate $replicateCount: ${"%.2f".format(measuredValue)} ${primaryMeasurement.unit}"
                }
                .padding(vertical = 8.dp)
        )
        if (replicateCount > 0) {
            Text(
                "LATEST · ${"%.2f".format(measuredValue)} ${primaryMeasurement.unit} · ${primaryMeasurement.uncertainty}",
                color = Green,
                fontSize = 9.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }

        Text("VARIABLES · ${lab.independentVariable} → ${lab.dependentVariable}", color = Cyan, fontSize = 8.sp, modifier = Modifier.padding(top = 9.dp))
        Text("CONTROLLED · ${lab.controlledVariables.joinToString(" · ")}", color = Muted, fontSize = 8.sp, lineHeight = 13.sp, modifier = Modifier.padding(top = 3.dp))
        Text("CALCULATION · ${lab.calculationModel}", color = accent, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))

        Text("6 · NOTEBOOK (${notebook.size} ENTRIES)", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 11.dp))
        notebook.takeLast(4).forEach {
            Text("• $it", color = White, fontSize = 8.sp, modifier = Modifier.padding(top = 3.dp))
        }
        Text("ACCEPTANCE · ${lab.acceptanceCriteria.take(3).joinToString(" · ")}", color = Green, fontSize = 8.sp, lineHeight = 13.sp, modifier = Modifier.padding(top = 8.dp))
        Text("WASTE · ${lab.wastePlan.joinToString(" · ")}", color = Orange, fontSize = 8.sp, lineHeight = 13.sp, modifier = Modifier.padding(top = 6.dp))
        Text("REPORT · ${lab.reportSections.joinToString(" → ")}", color = Muted, fontSize = 8.sp, lineHeight = 13.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

private data class VisualHitTarget(
    val position: Offset,
    val entityIndex: Int,
    val label: String
)

@Composable
private fun Procedural3DVisualization(
    spec: Visualization3DSpec,
    control: Float,
    accent: Color,
    running: Boolean,
    cameraResetToken: Int,
    cameraPreset: Int,
    slowMotion: Boolean,
    compareMode: Boolean,
    reducedMotion: Boolean,
    qualityLevel: Int,
    challengeMode: Boolean,
    onInspect: (String) -> Unit
) {
    val presetYaw = listOf(0f, .52f, -.38f)[cameraPreset.coerceIn(0, 2)]
    val presetPitch = listOf(0f, -.28f, .2f)[cameraPreset.coerceIn(0, 2)]
    val presetZoom = listOf(1f, 1.38f, 1.12f)[cameraPreset.coerceIn(0, 2)]
    var gestureYaw by remember(spec.id, cameraResetToken, cameraPreset) { mutableFloatStateOf(presetYaw) }
    var gesturePitch by remember(spec.id, cameraResetToken, cameraPreset) { mutableFloatStateOf(presetPitch) }
    var gestureZoom by remember(spec.id, cameraResetToken, cameraPreset) { mutableFloatStateOf(presetZoom) }
    var selectedEntity by remember(spec.id, cameraResetToken) { mutableIntStateOf(0) }
    var selectedLabel by remember(spec.id, cameraResetToken) { mutableStateOf(spec.entityLabels.first()) }
    var selectedNudge by remember(spec.id, cameraResetToken) { mutableStateOf(Offset.Zero) }
    val hitTargets = remember(spec.id) { mutableListOf<VisualHitTarget>() }
    val motion = rememberInfiniteTransition(label = "chemistry-motion-${spec.id}")
    val orbitPhase by motion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (
                    7_600 / spec.motionSpeed * if (slowMotion) 2.8f else 1f
                    ).toInt().coerceIn(3_200, 28_000),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "auto-orbit"
    )
    val glowPhase by motion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1_800 / spec.motionSpeed).toInt().coerceIn(850, 4_800),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "chemistry-pulse"
    )
    val liveOrbit = if (running && !reducedMotion) orbitPhase else 0f
    val liveGlow = if (running && !reducedMotion) glowPhase else .35f
    Canvas(
        Modifier.fillMaxWidth()
            .height(214.dp)
            .padding(top = 8.dp)
            .background(
                Brush.radialGradient(
                    listOf(accent.copy(.16f), Color(0xFF10182A).copy(.94f), Night),
                    radius = 620f
                ),
                RoundedCornerShape(18.dp)
            )
            .border(1.dp, accent.copy(.28f), RoundedCornerShape(18.dp))
            .pointerInput(spec.id, challengeMode) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    val nearest = hitTargets.minByOrNull {
                        val dx = it.position.x - centroid.x
                        val dy = it.position.y - centroid.y
                        dx * dx + dy * dy
                    }
                    val manipulatingEntity = nearest != null &&
                        (nearest.position - centroid).getDistance() < 52f &&
                        kotlin.math.abs(zoom - 1f) < .04f &&
                        kotlin.math.abs(rotation) < 1.2f
                    if (manipulatingEntity) {
                        selectedEntity = nearest.entityIndex
                        selectedLabel = nearest.label
                        selectedNudge = Offset(
                            (selectedNudge.x + pan.x).coerceIn(-38f, 38f),
                            (selectedNudge.y + pan.y).coerceIn(-38f, 38f)
                        )
                        onInspect(if (challengeMode) "Hidden entity ${nearest.entityIndex + 1}" else nearest.label)
                    } else {
                        gestureYaw += pan.x / size.width * 3.4f + rotation * .012f
                        gesturePitch = (gesturePitch + pan.y / size.height * 2.2f).coerceIn(-1.05f, 1.05f)
                        gestureZoom = (gestureZoom * zoom).coerceIn(.68f, 1.75f)
                    }
                }
            }
            .pointerInput(spec.id, selectedEntity, challengeMode) {
                detectTapGestures(
                    onDoubleTap = {
                        gestureYaw = presetYaw
                        gesturePitch = presetPitch
                        gestureZoom = presetZoom
                        selectedNudge = Offset.Zero
                    },
                    onTap = { tap ->
                        val nearest = hitTargets.minByOrNull { target ->
                            val dx = target.position.x - tap.x
                            val dy = target.position.y - tap.y
                            dx * dx + dy * dy
                        }
                        if (nearest != null) {
                            selectedEntity = nearest.entityIndex
                            selectedLabel = nearest.label
                            onInspect(if (challengeMode) "Hidden entity ${nearest.entityIndex + 1}" else nearest.label)
                        }
                    }
                )
            }
            .padding(7.dp)
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        hitTargets.clear()
        val concept = spec.conceptTitle.lowercase()
        val variant = spec.variantSeed
        val qualityFactor = listOf(.55f, .78f, 1f)[qualityLevel.coerceIn(0, 2)]
        fun seeded(index: Int, salt: Int = 0): Float {
            val mixed = ((variant.toLong() + index * 1103515245L + salt * 12345L) and 0x7fffffffL)
            return (mixed % 10_000L) / 10_000f
        }
        val ambientAngle = liveOrbit * 6.28318f * spec.motionIntensity
        val pulse = .5f + .5f * sin(liveGlow * 6.28318f)
        val angle = control * 6.28318f + ambientAngle + gestureYaw + seeded(0) * .65f
        val scale = size.minDimension * (.29f + seeded(1) * .035f) * gestureZoom
        drawCircle(accent.copy(.055f + pulse * .025f), scale * 1.42f, center)
        drawCircle(accent.copy(.12f), scale * 1.18f, center, style = Stroke(1.2f))
        val ambientCount = listOf(6, 12, 22)[qualityLevel.coerceIn(0, 2)]
        repeat(ambientCount) { index ->
            val starX = seeded(index, 88) * size.width
            val starY = seeded(index, 89) * size.height
            drawCircle(
                if (index % 3 == 0) accent.copy(.28f) else White.copy(.13f),
                .8f + seeded(index, 90) * 1.8f,
                Offset(starX, starY)
            )
        }
        fun project(x: Float, y: Float, z: Float): Offset {
            val rotatedX = x * cos(angle) - z * sin(angle)
            val rotatedZ = x * sin(angle) + z * cos(angle)
            val pitchedY = y * cos(gesturePitch) - rotatedZ * sin(gesturePitch)
            val pitchedZ = y * sin(gesturePitch) + rotatedZ * cos(gesturePitch)
            val perspective = 1f / (1.7f - pitchedZ * .22f)
            return Offset(
                center.x + rotatedX * scale * perspective,
                center.y + pitchedY * scale * perspective
            )
        }
        var drawnNodeIndex = 0
        fun node(point: Offset, color: Color, radius: Float = 8f) {
            val entityIndex = drawnNodeIndex++ % spec.entityLabels.size.coerceAtLeast(1)
            val entityLabel = spec.entityLabels[entityIndex]
            val selected = entityIndex == selectedEntity
            val displayPoint = if (selected) point + selectedNudge else point
            hitTargets += VisualHitTarget(displayPoint, entityIndex, entityLabel)
            val breathingRadius = radius * (1f + pulse * .08f * spec.motionIntensity)
            if (selected) {
                drawLine(White.copy(.22f), point, displayPoint, 2f)
                drawCircle(color.copy(.10f + pulse * .10f), breathingRadius + 15f + pulse * 5f, displayPoint)
                drawCircle(White.copy(.45f), breathingRadius + 9f, displayPoint, style = Stroke(2f))
            }
            drawCircle(color.copy(.08f), breathingRadius + 8f + pulse * 3f, displayPoint)
            drawCircle(Color.Black.copy(.30f), breathingRadius + 4f, displayPoint + Offset(2f, 3f))
            drawCircle(color.copy(.92f), breathingRadius, displayPoint)
            drawCircle(White.copy(.62f), breathingRadius * .27f, displayPoint - Offset(breathingRadius * .25f, breathingRadius * .25f))
        }
        fun arrow(from: Offset, to: Offset, color: Color = accent) {
            drawLine(color.copy(.75f), from, to, strokeWidth = 3f, cap = StrokeCap.Round)
            val direction = to - from
            val length = kotlin.math.sqrt(direction.x * direction.x + direction.y * direction.y).coerceAtLeast(1f)
            val ux = direction.x / length
            val uy = direction.y / length
            val left = Offset(to.x - ux * 10f - uy * 5f, to.y - uy * 10f + ux * 5f)
            val right = Offset(to.x - ux * 10f + uy * 5f, to.y - uy * 10f - ux * 5f)
            drawLine(color, left, to, 3f)
            drawLine(color, right, to, 3f)
        }

        when (spec.scene) {
            Chemistry3DScene.ATOMIC_ORBITAL -> {
                val nucleons = 6 + variant % 9
                repeat(nucleons) { index ->
                    val theta = index * .7854f + angle * .35f
                    node(project(cos(theta) * .16f, sin(theta * 1.7f) * .14f, sin(theta) * .16f), if (index % 2 == 0) Orange else Cyan, 8f)
                }
                val shellCount = when {
                    "bohr" in concept -> 3
                    "isotope" in concept || "nuclear" in concept -> 2
                    "quantum" in concept || "orbital" in concept -> 4
                    else -> 2 + variant % 3
                }
                List(shellCount) { .48f + it * .24f }.forEachIndexed { shell, radius ->
                    var previous: Offset? = null
                    repeat(49) { step ->
                        val theta = step / 48f * 6.28318f
                        val point = project(cos(theta) * radius, sin(theta) * radius * .28f, sin(theta) * radius)
                        previous?.let { drawLine(accent.copy(.18f + shell * .08f), it, point, 1.4f) }
                        previous = point
                    }
                    val electronTheta = angle * (shell + 1) + shell * 1.6f
                    node(project(cos(electronTheta) * radius, sin(electronTheta) * radius * .28f, sin(electronTheta) * radius), Green, 5f)
                }
            }
            Chemistry3DScene.ELECTRON_DENSITY -> {
                val densityPoints = ((28 + variant % 21) * qualityFactor).roundToInt().coerceAtLeast(16)
                val lobeCount = when {
                    "d orbital" in concept || "crystal field" in concept -> 4
                    "p orbital" in concept || "bond" in concept -> 2
                    else -> 1 + variant % 4
                }
                repeat(densityPoints) { index ->
                    val theta = index * 2.39996f + angle
                    val lobeIndex = index % (lobeCount * 2)
                    val lobeAngle = lobeIndex * 3.14159f / lobeCount
                    val lobe = if (index % 2 == 0) 1f else -1f
                    val r = .18f + (index % 9) * .075f +
                        sin(liveOrbit * 6.28318f + index * .7f) * .035f * spec.motionIntensity
                    val point = project(
                        cos(lobeAngle) * lobe * (.25f + r),
                        sin(theta) * r,
                        sin(lobeAngle) * lobe * (.25f + r) + cos(theta) * r * .35f
                    )
                    drawCircle(if (lobe > 0) Cyan.copy(.16f + index % 5 * .04f) else Purple.copy(.16f + index % 5 * .04f), 7f + index % 4 * 2f, point)
                }
                node(center, Orange, 9f)
                drawCircle(accent.copy(.18f), scale * .46f, center, style = Stroke(2f))
            }
            Chemistry3DScene.MOLECULAR_GEOMETRY -> {
                val vertices = when {
                    listOf("linear", "carbon dioxide", "sp hybrid").any { it in concept } ->
                        listOf(Triple(-1.1f, 0f, 0f), Triple(1.1f, 0f, 0f))
                    listOf("trigonal planar", "sp2", "carbonate").any { it in concept } ->
                        List(3) { i -> val t = i * 2.0944f; Triple(cos(t), sin(t), 0f) }
                    listOf("octahedral", "crystal field", "coordination").any { it in concept } ->
                        listOf(Triple(1f,0f,0f), Triple(-1f,0f,0f), Triple(0f,1f,0f), Triple(0f,-1f,0f), Triple(0f,0f,1f), Triple(0f,0f,-1f))
                    listOf("trigonal bipyramidal", "sp3d").any { it in concept } ->
                        listOf(Triple(0f,1.15f,0f), Triple(0f,-1.15f,0f), Triple(1f,0f,0f), Triple(-.5f,0f,.86f), Triple(-.5f,0f,-.86f))
                    "square planar" in concept ->
                        listOf(Triple(1f,0f,0f), Triple(-1f,0f,0f), Triple(0f,0f,1f), Triple(0f,0f,-1f))
                    spec.motif in setOf(ChemistryVisualMotif.MEDICINAL_BINDING, ChemistryVisualMotif.DRUG_ADME) ->
                        List(5) { i -> val t = i * 1.25664f; Triple(cos(t), sin(t) * .62f, sin(t * 2f) * .34f) }
                    else -> listOf(
                        Triple(1f, 1f, 1f), Triple(-1f, -1f, 1f),
                        Triple(-1f, 1f, -1f), Triple(1f, -1f, -1f)
                    )
                }
                val central = project(0f, 0f, 0f)
                vertices.forEachIndexed { index, (x, y, z) ->
                    val bondScale = .62f + seeded(index + 2) * .14f
                    val point = project(x * bondScale, y * bondScale, z * bondScale)
                    drawLine(accent.copy(.65f), central, point, 4f, cap = StrokeCap.Round)
                    node(point, if (index % 2 == 0) Cyan else Green, 10f)
                }
                node(central, Orange, 14f)
                drawCircle(Purple.copy(.15f), 24f + control * 8f, central)
                if (listOf("polar", "electroneg", "dipole", "hydrogen bond").any { it in concept }) {
                    val target = vertices.first()
                    arrow(central, project(target.first * .5f, target.second * .5f, target.third * .5f), Cyan)
                    drawCircle(Orange.copy(.18f), 22f, central, style = Stroke(2f))
                }
                if ("double bond" in concept || "triple bond" in concept) {
                    val order = if ("triple bond" in concept) 3 else 2
                    repeat(order - 1) { offset ->
                        drawLine(
                            White.copy(.35f),
                            central + Offset(0f, (offset + 1) * 4f),
                            project(vertices.first().first * .62f, vertices.first().second * .62f, vertices.first().third * .62f) + Offset(0f, (offset + 1) * 4f),
                            1.5f
                        )
                    }
                }
            }
            Chemistry3DScene.CRYSTAL_LATTICE -> {
                val points = mutableMapOf<Triple<Int, Int, Int>, Offset>()
                for (x in -1..1) for (y in -1..1) for (z in -1..1) {
                    val shear = if ("defect" in concept || "semiconductor" in concept) seeded(x + y * 3 + z * 7, 4) * .1f else 0f
                    points[Triple(x, y, z)] = project(x * .7f + shear, y * .7f, z * .7f)
                }
                points.forEach { (coordinate, point) ->
                    val (x, y, z) = coordinate
                    listOf(Triple(x + 1, y, z), Triple(x, y + 1, z), Triple(x, y, z + 1)).forEach { neighbor ->
                        points[neighbor]?.let { drawLine(accent.copy(.22f), point, it, 1.8f) }
                    }
                }
                points.entries.sortedBy { it.key.third }.forEachIndexed { index, entry ->
                    val defect = ("defect" in concept || "dop" in concept) && index == 13
                    if (!("vacancy" in concept && defect)) {
                        node(entry.value, if (defect) Orange else if (index % 2 == 0) Cyan else Purple, if (defect) 10f else 6f)
                    }
                }
                val sliceY = center.y - scale * .55f + control * scale * 1.1f
                drawLine(Cyan.copy(.35f), Offset(center.x - scale, sliceY), Offset(center.x + scale, sliceY), 2f)
                drawCircle(Cyan.copy(.06f), scale * .72f, Offset(center.x, sliceY))
            }
            Chemistry3DScene.PHASE_PARTICLES -> {
                val phaseBias = when {
                    "solid" in concept || "bose" in concept -> .18f
                    "liquid" in concept -> .48f
                    "gas" in concept || "plasma" in concept -> .9f
                    else -> .32f + seeded(3) * .42f
                }
                val spread = phaseBias + control * (if (phaseBias > .7f) .25f else .65f)
                val particleCount = ((20 + variant % 13) * qualityFactor).roundToInt().coerceAtLeast(12)
                repeat(particleCount) { index ->
                    val kineticMotion = liveOrbit * 6.28318f * (1f + index % 4 * .12f)
                    val x = sin(index * 12.9898f + kineticMotion) * spread
                    val y = sin(index * 7.233f + 1.2f + kineticMotion * .7f) * .82f
                    val z = cos(index * 5.117f + control + kineticMotion * .55f) * spread
                    val point = project(x, y, z)
                    if (phaseBias > .7f && !reducedMotion) {
                        val trail = Offset(
                            point.x - cos(index * 1.7f + kineticMotion) * (8f + spec.motionIntensity * 10f),
                            point.y - sin(index * 1.3f + kineticMotion) * (8f + spec.motionIntensity * 10f)
                        )
                        drawLine((if ("plasma" in concept) Orange else Cyan).copy(.24f), trail, point, 2f)
                    }
                    if (index > 0 && control < .42f) {
                        val previous = project(
                            sin((index - 1) * 12.9898f) * spread,
                            sin((index - 1) * 7.233f + 1.2f) * .82f,
                            cos((index - 1) * 5.117f + control) * spread
                        )
                        drawLine(Cyan.copy(.16f), previous, point, 1f)
                    }
                    node(point, if (index % 3 == 0) Green else accent, 5f + (index % 3))
                }
                drawRect(White.copy(.10f), topLeft = Offset(center.x - scale * .85f, center.y - scale * .66f), size = Size(scale * 1.7f, scale * 1.32f), style = Stroke(2f))
            }
            Chemistry3DScene.ENERGY_SURFACE -> {
                val barrierScale = when (spec.motif) {
                    ChemistryVisualMotif.KINETICS, ChemistryVisualMotif.ORGANIC_MECHANISM,
                    ChemistryVisualMotif.CATALYTIC_CYCLE, ChemistryVisualMotif.ENZYME_CATALYSIS,
                    ChemistryVisualMotif.COMBUSTION_FLAME -> .92f
                    ChemistryVisualMotif.THERMODYNAMICS, ChemistryVisualMotif.CALORIMETRY,
                    ChemistryVisualMotif.PHASE_TRANSITION -> .48f
                    ChemistryVisualMotif.PHOTOCHEMISTRY, ChemistryVisualMotif.SOLAR_CELL -> .75f
                    else -> .58f + seeded(4) * .28f
                }
                val productBias = (seeded(5) - .5f) * .42f
                repeat(7) { row ->
                    val path = Path()
                    repeat(34) { column ->
                        val x = column / 33f * 2.2f - 1.1f
                        val z = row / 6f * 1.5f - .75f
                        val barrier = barrierScale * kotlin.math.exp(-(3.2f + seeded(6) * 2f) * x * x)
                        val wells = -.22f * cos(x * (2.6f + seeded(7))) + z * z * .18f + productBias * x
                        val point = project(x, -(barrier + wells) + .25f, z)
                        if (column == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
                    }
                    drawPath(path, if (row == 3) Orange.copy(.9f) else accent.copy(.18f + row * .025f), style = Stroke(if (row == 3) 3f else 1.2f))
                }
                node(project(-.95f, .1f, 0f), Green, 8f)
                node(project(0f, -.52f, 0f), Orange, 8f)
                node(project(.95f, .2f, 0f), Cyan, 8f)
            }
            Chemistry3DScene.REACTION_COORDINATE -> {
                val stages = spec.stageLabels.take(5)
                val points = stages.indices.map { index ->
                    val x = if (stages.size == 1) 0f else -1.15f + index * (2.3f / (stages.size - 1))
                    val transitionBias = when (spec.motif) {
                        ChemistryVisualMotif.ORGANIC_MECHANISM, ChemistryVisualMotif.CATALYTIC_CYCLE -> .54f
                        ChemistryVisualMotif.KINETICS, ChemistryVisualMotif.ENZYME_CATALYSIS -> .46f
                        ChemistryVisualMotif.STOICHIOMETRY, ChemistryVisualMotif.LIMITING_REAGENT,
                        ChemistryVisualMotif.MOLECULAR_FORMULA -> .16f
                        else -> .27f + seeded(index, 8) * .22f
                    }
                    val y = -.22f + sin(index * (1.45f + seeded(8)) + control) * transitionBias
                    project(x, y, (index % 2) * .25f - .12f)
                }
                points.zipWithNext().forEach { (from, to) -> arrow(from, to, Purple) }
                points.forEachIndexed { index, point -> node(point, when (index) { 0 -> Green; points.lastIndex -> Cyan; else -> Orange }, 8f + index % 2 * 2f) }
                points.zipWithNext().forEachIndexed { index, (from, to) ->
                    val progress = (liveOrbit * (if (slowMotion) .7f else 1.8f) + index * .19f) % 1f
                    val moving = Offset(from.x + (to.x - from.x) * progress, from.y + (to.y - from.y) * progress)
                    drawCircle(Purple.copy(.18f), 9f, moving)
                    drawCircle(White.copy(.86f), 3f, moving)
                }
            }
            Chemistry3DScene.EQUILIBRIUM_CHAMBER -> {
                val left = Offset(center.x - scale * .82f, center.y - scale * .62f)
                drawRoundRect(accent.copy(.12f), left, Size(scale * 1.64f, scale * 1.24f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f), style = Stroke(2f))
                drawLine(White.copy(.14f), Offset(center.x, left.y), Offset(center.x, left.y + scale * 1.24f), 2f)
                val totalSpecies = 14 + variant % 11
                val equilibriumFraction = when (spec.motif) {
                    ChemistryVisualMotif.ACID_BASE, ChemistryVisualMotif.BUFFER_SYSTEM -> .3f + control * .38f
                    ChemistryVisualMotif.SOLVATION -> .52f + (control - .5f) * .18f
                    else -> .38f + seeded(9) * .24f + (control - .5f) * .2f
                }
                repeat(totalSpecies) { index ->
                    val side = if (index < (totalSpecies * equilibriumFraction).toInt()) -1f else 1f
                    val x = side * (.18f + (index % 5) * .12f)
                    val y = -.45f + (index % 6) * .18f
                    node(project(x, y, sin(index * 2.1f) * .28f), if (side < 0) Cyan else Orange, 5f)
                }
                arrow(Offset(center.x - 26f, center.y - 8f), Offset(center.x + 26f, center.y - 8f), Green)
                arrow(Offset(center.x + 26f, center.y + 12f), Offset(center.x - 26f, center.y + 12f), Purple)
            }
            Chemistry3DScene.SURFACE_INTERFACE -> {
                val surfaceY = center.y + scale * .28f
                if ("micelle" in concept || "emulsion" in concept) {
                    val radius = scale * (.42f + seeded(10) * .08f)
                    repeat(16) { index ->
                        val theta = index / 16f * 6.28318f + angle * .15f
                        val head = Offset(center.x + cos(theta) * radius, center.y + sin(theta) * radius * .72f)
                        val tail = Offset(center.x + cos(theta) * radius * .42f, center.y + sin(theta) * radius * .30f)
                        drawLine(Green.copy(.7f), head, tail, 3f)
                        node(head, if ("emulsion" in concept) Orange else Cyan, 5f)
                    }
                    drawCircle(Purple.copy(.12f), radius * .45f, center)
                    node(center, Purple, 8f)
                    return@Canvas
                }
                repeat(11) { index ->
                    val x = center.x - scale + index * scale * .2f
                    node(Offset(x, surfaceY + sin(index * .8f) * 3f), if (index % 2 == 0) Purple else accent, 6f)
                    if (index < 10) {
                        drawLine(
                            accent.copy(.35f),
                            Offset(x + 6f, surfaceY),
                            Offset(x + scale * .2f - 6f, surfaceY),
                            2f
                        )
                    }
                }
                val coverage = (2 + control * 6 + variant % 4).toInt()
                repeat(coverage) { index ->
                    val x = center.x - scale * .8f + index * (scale * 1.6f / coverage.coerceAtLeast(1))
                    val head = Offset(x, surfaceY - 23f - sin(index * 1.3f) * 5f)
                    drawLine(Green.copy(.65f), Offset(x, surfaceY - 5f), head, 3f)
                    node(head, if (index % 2 == 0) Green else Cyan, 6f)
                }
                repeat(9) { index ->
                    val theta = index * 2.39996f + angle
                    val point = Offset(
                        center.x + cos(theta) * scale * (.35f + index % 3 * .16f),
                        center.y - scale * .45f + sin(theta * 1.4f) * scale * .23f
                    )
                    drawCircle(Cyan.copy(.16f), 5f + index % 3 * 2f, point)
                }
                drawLine(White.copy(.18f), Offset(center.x - scale, surfaceY + 13f), Offset(center.x + scale, surfaceY + 13f), 2f)
            }
            Chemistry3DScene.ELECTROCHEMICAL_CELL -> {
                val top = center.y - scale * .58f
                val height = scale * 1.12f
                drawRoundRect(Cyan.copy(.10f), Offset(center.x - scale, top), Size(scale * 2f, height), androidx.compose.ui.geometry.CornerRadius(12f), style = Stroke(2f))
                val anodeX = center.x - scale * .58f
                val cathodeX = center.x + scale * .58f
                drawRect(Orange.copy(.75f), Offset(anodeX - 7f, top + 12f), Size(14f, height - 24f))
                drawRect(Cyan.copy(.75f), Offset(cathodeX - 7f, top + 12f), Size(14f, height - 24f))
                val ionCount = 10 + variant % 10
                repeat(ionCount) { index ->
                    val x = center.x - scale * .42f + (index % 5) * scale * .21f
                    val y = top + 22f + (index % 4) * height * .20f
                    node(Offset(x, y), if (index % 2 == 0) Purple else Green, 5f)
                }
                if ("electrolysis" in concept) {
                    arrow(Offset(cathodeX, top - 10f), Offset(anodeX, top - 10f), Orange)
                    drawCircle(Cyan.copy(.2f), 13f + control * 7f, Offset(cathodeX, top + 18f))
                } else {
                    arrow(Offset(anodeX, top - 10f), Offset(cathodeX, top - 10f), Green)
                }
                repeat(if (qualityLevel == 0) 2 else 5) { electron ->
                    val progress = (liveOrbit * 2f + electron / 5f) % 1f
                    val fromX = if ("electrolysis" in concept) cathodeX else anodeX
                    val toX = if ("electrolysis" in concept) anodeX else cathodeX
                    val electronPoint = Offset(fromX + (toX - fromX) * progress, top - 10f)
                    drawCircle(Green.copy(.16f), 8f, electronPoint)
                    drawCircle(Green, 3.5f, electronPoint)
                }
                if ("corrosion" in concept) {
                    repeat(7) { index ->
                        drawCircle(Orange.copy(.22f), 4f + index, Offset(anodeX + sin(index * 1.4f) * 15f, top + height * (.18f + index * .1f)))
                    }
                }
                drawLine(Purple.copy(.55f), Offset(anodeX, top + height * .6f), Offset(center.x, top + height * .35f), 3f)
                drawLine(Purple.copy(.55f), Offset(center.x, top + height * .35f), Offset(cathodeX, top + height * .6f), 3f)
            }
            Chemistry3DScene.SPECTRAL_RESPONSE -> {
                val baseline = center.y + scale * .58f
                drawLine(White.copy(.25f), Offset(center.x - scale, baseline), Offset(center.x + scale, baseline), 2f)
                val peakCount = when {
                    "nmr" in concept -> 4 + variant % 4
                    "mass" in concept -> 8 + variant % 7
                    "ir" in concept || "raman" in concept -> 10 + variant % 8
                    "xrd" in concept -> 6 + variant % 6
                    else -> 5 + variant % 8
                }
                val peaks = List(peakCount) { index ->
                    (.05f + seeded(index, 11) * .9f).coerceIn(.04f, .96f)
                }.sorted()
                peaks.forEachIndexed { index, position ->
                    val x = center.x - scale + position * scale * 2f
                    val abundance = .18f + seeded(index, 12) * .74f
                    val height = scale * abundance * (.7f + control * .3f)
                    val peakColor = when {
                        "mass" in concept -> if (abundance > .72f) Orange else accent
                        "nmr" in concept -> Green
                        "ir" in concept -> Purple
                        else -> if (index % 3 == 0) Orange else accent
                    }
                    drawLine(peakColor, Offset(x, baseline), Offset(x, baseline - height), 2f + index % 3, cap = StrokeCap.Round)
                    drawCircle(White.copy(.38f), 3f, Offset(x, baseline - height))
                    hitTargets += VisualHitTarget(
                        Offset(x, baseline - height),
                        index % spec.entityLabels.size,
                        "${spec.conceptTitle} peak ${index + 1}"
                    )
                }
                val scanX = center.x - scale + liveOrbit * scale * 2f
                drawLine(White.copy(.42f), Offset(scanX, baseline + 5f), Offset(scanX, baseline - scale), 1.5f)
                repeat(5) { depth ->
                    val y = baseline + depth * 3f
                    drawLine(accent.copy(.07f), Offset(center.x - scale, y), Offset(center.x + scale, y), 1f)
                }
            }
            Chemistry3DScene.SEPARATION_COLUMN -> {
                if ("tlc" in concept || "electrophores" in concept) {
                    val left = center.x - scale * .72f
                    val top = center.y - scale * .72f
                    drawRoundRect(accent.copy(.1f), Offset(left, top), Size(scale * 1.44f, scale * 1.45f), androidx.compose.ui.geometry.CornerRadius(8f), style = Stroke(2f))
                    drawLine(White.copy(.25f), Offset(left + 10f, top + scale * 1.25f), Offset(left + scale * 1.44f - 10f, top + scale * 1.25f), 2f)
                    val laneCount = 3 + variant % 3
                    repeat(laneCount) { lane ->
                        val x = left + scale * .24f + lane * scale * (1f / laneCount)
                        val spots = 1 + (variant + lane) % 3
                        repeat(spots) { spot ->
                            val travel = (.18f + seeded(lane * 5 + spot, 13) * .72f) * (if ("electrophores" in concept) control + .25f else 1f)
                            node(Offset(x, top + scale * (1.2f - travel)), listOf(Cyan, Orange, Green, Purple)[(lane + spot) % 4], 5f + spot)
                        }
                    }
                    arrow(Offset(left - 14f, top + scale * 1.15f), Offset(left - 14f, top + scale * .18f), Green)
                    return@Canvas
                }
                val columnWidth = scale * .62f
                val top = center.y - scale * .72f
                drawRoundRect(accent.copy(.10f), Offset(center.x - columnWidth / 2f, top), Size(columnWidth, scale * 1.45f), androidx.compose.ui.geometry.CornerRadius(columnWidth / 2f), style = Stroke(3f))
                val packingCount = if ("gc" in concept || "gas chromat" in concept) 11 else 18 + variant % 8
                repeat(packingCount) { index ->
                    val x = center.x + sin(index * 2.4f) * columnWidth * .28f
                    val y = top + 12f + (index % 9) * scale * .15f
                    node(Offset(x, y), if (index % 3 == 0) Purple else Cyan, 3.5f)
                }
                val bands = if ("ion exchange" in concept) listOf(.18f, .38f, .63f, .84f) else listOf(.26f, .52f, .78f)
                bands.forEachIndexed { index, fraction ->
                    val y = top + scale * 1.3f * (fraction + (control - .5f) * .12f * (index - 1))
                    drawLine(listOf(Green, Orange, Purple, Cyan)[index].copy(.75f), Offset(center.x - columnWidth * .32f, y), Offset(center.x + columnWidth * .32f, y), 7f, cap = StrokeCap.Round)
                    hitTargets += VisualHitTarget(
                        Offset(center.x, y),
                        index % spec.entityLabels.size,
                        "${spec.conceptTitle} analyte band ${index + 1}"
                    )
                }
                arrow(Offset(center.x, top - 18f), Offset(center.x, top + 8f), Green)
            }
            Chemistry3DScene.BIOPOLYMER -> {
                if ("protein" in concept || "enzyme" in concept) {
                    val points = List(34) { index ->
                        val t = index / 33f * 6.28318f * 2.2f
                        val fold = .38f + seeded(index, 14) * .3f
                        project(cos(t) * fold, sin(t * .63f) * .82f, sin(t) * fold)
                    }
                    points.zipWithNext().forEach { (a, b) -> drawLine(if ("enzyme" in concept) Green.copy(.7f) else Cyan.copy(.7f), a, b, 3f) }
                    points.forEachIndexed { index, point -> if (index % 3 == 0) node(point, if (index % 2 == 0) Orange else Purple, 4f) }
                    if ("enzyme" in concept) {
                        val pocket = project(.18f, -.08f, .22f)
                        drawCircle(Orange.copy(.2f), 22f, pocket)
                        node(pocket + Offset(control * 13f, -control * 8f), Cyan, 7f)
                    }
                    return@Canvas
                }
                var previousA: Offset? = null
                var previousB: Offset? = null
                val baseCount = if ("rna" in concept) 23 else 28 + variant % 7
                repeat(baseCount) { index ->
                    val t = index / (baseCount - 1f) * 4.4f - 2.2f
                    val theta = t * 3.2f + angle
                    val a = Offset(center.x + cos(theta) * scale * .43f, center.y + t * scale * .30f)
                    val b = Offset(center.x + cos(theta + 3.14159f) * scale * .43f, center.y + t * scale * .30f)
                    previousA?.let { drawLine(Cyan.copy(.7f), it, a, 3f) }
                    if ("rna" !in concept) previousB?.let { drawLine(Purple.copy(.7f), it, b, 3f) }
                    if ("rna" !in concept && index % 3 == 0) drawLine(Green.copy(.38f), a, b, 2f)
                    node(a, Cyan, 4f)
                    if ("rna" !in concept) node(b, Purple, 4f)
                    previousA = a
                    previousB = b
                }
            }
            Chemistry3DScene.POLYMER_CHAIN -> {
                val chains = when {
                    "cross-link" in concept || "thermoset" in concept -> 4
                    "conducting" in concept -> 2
                    else -> 2 + variant % 3
                }
                repeat(chains) { chain ->
                    val repeatUnits = 10 + variant % 7
                    val points = List(repeatUnits) { index ->
                        val x = -1.15f + index * (2.3f / (repeatUnits - 1))
                        val y = (if (index % 2 == 0) -.22f else .22f) + (chain - 1) * .48f
                        project(x, y, sin(index * .8f + chain + angle) * .34f)
                    }
                    points.zipWithNext().forEach { (a, b) -> drawLine(accent.copy(.55f), a, b, 3f) }
                    points.forEachIndexed { index, point -> node(point, if (index % 4 == 0) Orange else if (chain == 1) Cyan else Purple, 5f) }
                }
                if ("cross-link" in concept || "thermoset" in concept) {
                    repeat(4) { link ->
                        val x = center.x - scale * .72f + link * scale * .48f
                        drawLine(Green.copy(.65f), Offset(x, center.y - scale * .46f), Offset(x + 10f, center.y + scale * .46f), 2f)
                    }
                }
            }
            Chemistry3DScene.NUCLEAR_TRANSFORMATION -> {
                val parentCount = 14 + variant % 11
                repeat(parentCount) { index ->
                    val theta = index * 2.39996f
                    val r = .12f + (index % 5) * .09f
                    node(project(cos(theta) * r - .42f, sin(theta) * r, cos(theta * 1.7f) * r), if (index % 2 == 0) Orange else Cyan, 6f)
                }
                val emissionStart = project(-.08f, 0f, 0f)
                val emissionEnd = project(.62f + control * .48f, -.18f, .18f)
                if ("gamma" in concept) {
                    val wave = Path()
                    repeat(24) { index ->
                        val p = Offset(
                            emissionStart.x + (emissionEnd.x - emissionStart.x) * index / 23f,
                            emissionStart.y + sin(index * 1.5f) * 7f
                        )
                        if (index == 0) wave.moveTo(p.x, p.y) else wave.lineTo(p.x, p.y)
                    }
                    drawPath(wave, Green, style = Stroke(3f))
                } else {
                    arrow(emissionStart, emissionEnd, Green)
                    node(emissionEnd, if ("beta" in concept) Cyan else Green, if ("alpha" in concept) 9f else 6f)
                }
                val daughterCount = when {
                    "fission" in concept -> parentCount / 2
                    "fusion" in concept -> parentCount + 4
                    else -> (parentCount - if ("alpha" in concept) 4 else 1).coerceAtLeast(8)
                }
                repeat(daughterCount) { index ->
                    val theta = index * 2.39996f
                    val r = .11f + (index % 4) * .08f
                    node(project(cos(theta) * r + .72f, sin(theta) * r, sin(theta * 1.4f) * r), if (index % 2 == 0) Purple else Cyan, 5f)
                }
            }
            Chemistry3DScene.ENVIRONMENTAL_CYCLE -> {
                val compartmentCount = when {
                    "air" in concept || "atmospher" in concept -> 4
                    "water" in concept || "marine" in concept -> 6
                    "waste" in concept -> 5
                    else -> 4 + variant % 3
                }
                val points = List(compartmentCount) { index ->
                    val theta = angle * .16f + index * (6.28318f / compartmentCount) - 1.5708f
                    Offset(center.x + cos(theta) * scale * .82f, center.y + sin(theta) * scale * .58f)
                }
                val cycleColors = listOf(Cyan, Green, Orange, Purple, accent, White)
                points.forEachIndexed { index, point -> arrow(point, points[(index + 1) % points.size], cycleColors[index]) }
                points.forEachIndexed { index, point -> node(point, cycleColors[index], 8f + seeded(index, 15) * 5f) }
                node(center, White.copy(.8f), 8f)
            }
            Chemistry3DScene.PROCESS_FLOW -> {
                val stages = spec.stageLabels.take(5)
                val boxWidth = (size.width - 42f) / stages.size.coerceAtLeast(1)
                val points = stages.indices.map { index ->
                    Offset(21f + boxWidth * index + boxWidth / 2f, center.y + sin(index * 1.4f + control) * 25f)
                }
                points.zipWithNext().forEach { (from, to) -> arrow(Offset(from.x + 13f, from.y), Offset(to.x - 13f, to.y), Green) }
                points.forEachIndexed { index, point ->
                    val vesselWidth = when (spec.motif) {
                        ChemistryVisualMotif.ANALYTICAL_MEASUREMENT, ChemistryVisualMotif.TITRATION_CURVE -> 22f
                        ChemistryVisualMotif.CHEMICAL_ENGINEERING, ChemistryVisualMotif.INDUSTRIAL_PROCESS,
                        ChemistryVisualMotif.FLOW_CHEMISTRY -> 34f
                        ChemistryVisualMotif.MICROFLUIDICS -> 10f
                        ChemistryVisualMotif.LABORATORY -> 18f
                        else -> 15f + seeded(index, 16) * 8f
                    }
                    drawRoundRect(
                        listOf(Cyan, Purple, Orange, Green, accent)[index % 5].copy(.24f),
                        Offset(point.x - vesselWidth, point.y - 22f),
                        Size(vesselWidth * 2f, 44f),
                        androidx.compose.ui.geometry.CornerRadius(7f)
                    )
                    drawRoundRect(
                        listOf(Cyan, Purple, Orange, Green, accent)[index % 5],
                        Offset(point.x - vesselWidth, point.y - 22f),
                        Size(vesselWidth * 2f, 44f),
                        androidx.compose.ui.geometry.CornerRadius(7f),
                        style = Stroke(2f)
                    )
                }
                drawLine(Orange.copy(.4f), Offset(points.lastOrNull()?.x ?: center.x, center.y + 45f), Offset(points.firstOrNull()?.x ?: center.x, center.y + 45f), 2f)
            }
            Chemistry3DScene.DATA_SURFACE -> {
                val frequencyX = 1.7f + seeded(17) * 2.2f
                val frequencyZ = 2.1f + seeded(18) * 2.4f
                val crossTerm = (seeded(19) - .5f) * .5f
                repeat(9) { row ->
                    val path = Path()
                    repeat(24) { column ->
                        val x = column / 23f * 2.2f - 1.1f
                        val z = row / 8f * 1.6f - .8f
                        val response = sin(x * frequencyX + control * 2f) * .22f + cos(z * frequencyZ) * .17f + x * z * crossTerm
                        val point = project(x, -response, z)
                        if (column == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
                    }
                    drawPath(path, accent.copy(.16f + row * .035f), style = Stroke(1.5f))
                }
                repeat(6) { index ->
                    val x = -1f + index * .4f
                    val z = sin(index * 1.7f) * .6f
                    val response = sin(x * frequencyX + control * 2f) * .22f + cos(z * frequencyZ) * .17f + x * z * crossTerm
                    node(project(x, -response, z), if (index % 2 == 0) Green else Orange, 6f)
                }
            }
        }
        hitTargets.firstOrNull { it.entityIndex == selectedEntity }?.let { target ->
            val calloutX = (target.position.x + if (target.position.x < center.x) 34f else -154f)
                .coerceIn(8f, size.width - 150f)
            val calloutY = (target.position.y - 42f).coerceIn(10f, size.height - 48f)
            val callout = Offset(calloutX, calloutY)
            drawLine(accent.copy(.72f), target.position, Offset(callout.x + 8f, callout.y + 16f), 2f)
            drawRoundRect(
                Night.copy(.9f),
                topLeft = callout,
                size = Size(142f, 34f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
            )
            drawRoundRect(
                accent.copy(.75f),
                topLeft = callout,
                size = Size(142f, 34f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f),
                style = Stroke(1.5f)
            )
            val calloutText = if (challengeMode) "IDENTIFY THIS ENTITY" else selectedLabel.take(22)
            drawContext.canvas.nativeCanvas.drawText(
                calloutText,
                callout.x + 10f,
                callout.y + 22f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = (if (challengeMode) Orange else White).toArgb()
                    textSize = 20f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
            )
        }
        if (compareMode) {
            drawRect(Green.copy(.045f), Offset.Zero, Size(size.width / 2f, size.height))
            drawRect(Purple.copy(.045f), Offset(size.width / 2f, 0f), Size(size.width / 2f, size.height))
            drawLine(White.copy(.42f), Offset(center.x, 8f), Offset(center.x, size.height - 8f), 2f)
            val comparePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = White.copy(.68f).toArgb()
                textSize = 18f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            drawContext.canvas.nativeCanvas.drawText("INITIAL", 14f, 25f, comparePaint)
            drawContext.canvas.nativeCanvas.drawText("CURRENT", center.x + 14f, 25f, comparePaint)
        }
        if (running) {
            val scanY = 8f + liveGlow * (size.height - 16f)
            drawLine(
                Brush.horizontalGradient(listOf(Color.Transparent, accent.copy(.26f), White.copy(.32f), accent.copy(.26f), Color.Transparent)),
                Offset(8f, scanY),
                Offset(size.width - 8f, scanY),
                strokeWidth = 1.4f
            )
        }
        drawRoundRect(
            accent.copy(.16f + pulse * .08f),
            topLeft = Offset(2f, 2f),
            size = Size(size.width - 4f, size.height - 4f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f),
            style = Stroke(1.2f)
        )
    }
}

@Composable
private fun EnhancedLessonChallengePage(
    module: CurriculumModule,
    checks: List<KnowledgeCheck>,
    questionIndex: Int,
    selected: Int,
    confidence: String,
    choose: (Int) -> Unit,
    setConfidence: (String) -> Unit,
    next: () -> Unit,
    previous: () -> Unit
) {
    val check = checks[questionIndex]
    var showHint by remember(questionIndex) { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Tag("KNOWLEDGE CHECK · ${questionIndex + 1}/${checks.size}", Orange)
        LinearProgressIndicator(
            progress = { (questionIndex + 1) / checks.size.toFloat() },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            color = module.color,
            trackColor = Border
        )
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            LessonMcqPill(check.difficulty, module.color)
            LessonMcqPill(check.skill, Cyan)
            LessonMcqPill("Review: ${check.reviewPage}", Green)
            LessonMcqPill("${checks.size - questionIndex - 1} left", Muted)
        }
        Text(check.question, color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp, modifier = Modifier.padding(vertical = 18.dp))
        Text(
            if (showHint) check.hint else "Show hint",
            color = if (showHint) White else Cyan,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            modifier = Modifier.fillMaxWidth()
                .background(if (showHint) module.color.copy(alpha = .10f) else Panel, RoundedCornerShape(10.dp))
                .border(1.dp, if (showHint) module.color.copy(alpha = .40f) else Border, RoundedCornerShape(10.dp))
                .clickable { showHint = !showHint }
                .padding(10.dp)
        )
        ConfidenceSelector(confidence, setConfidence, Modifier.padding(top = 10.dp))
        check.options.forEachIndexed { index, option ->
            val correct = selected >= 0 && index == check.correct
            val wrong = selected == index && index != check.correct
            val accent = if (correct) Green else if (wrong) Color(0xFFFF6378) else Border
            Row(
                Modifier.fillMaxWidth().padding(top = 9.dp)
                    .background(accent.copy(.1f), RoundedCornerShape(12.dp))
                    .border(1.dp, accent, RoundedCornerShape(12.dp))
                    .clickable(enabled = selected < 0) { choose(index) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(28.dp).background(accent.copy(.12f), CircleShape), contentAlignment = Alignment.Center) {
                    Text(('A' + index).toString(), color = if (selected < 0) Muted else accent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Text(option, color = White, modifier = Modifier.weight(1f).padding(horizontal = 12.dp))
                if (correct) Text("✓", color = Green, fontWeight = FontWeight.Bold)
                if (wrong) Text("!", color = Color(0xFFFF6378), fontWeight = FontWeight.Bold)
            }
        }
        if (selected >= 0) {
            GlowCard(Modifier.fillMaxWidth().padding(top = 10.dp), if (selected == check.correct) Green else Orange) {
                Text(if (selected == check.correct) "CORRECT" else "REVIEW THIS REASONING", color = if (selected == check.correct) Green else Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(check.explanation, color = White, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 6.dp))
                if (selected != check.correct) {
                    Text("Chosen: ${check.options[selected]}", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
                    Text("Correct: ${check.options[check.correct]}", color = Green, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                    Text(remediationForCheck(check), color = Orange, fontSize = 10.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 6.dp))
                } else if (confidence == "Low") {
                    Text("Good answer, but low confidence: revisit ${check.reviewPage}, then explain it once in your own words.", color = Cyan, fontSize = 10.sp, modifier = Modifier.padding(top = 6.dp))
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (questionIndex > 0) {
                    Button(onClick = previous, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(Panel)) {
                        Text("Previous", color = White)
                    }
                }
                if (questionIndex < checks.lastIndex) {
                    Button(onClick = next, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(module.color)) {
                        Text("Next question", color = Night, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonMcqPill(text: String, color: Color) {
    Text(
        text,
        color = color,
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.background(color.copy(alpha = .09f), CircleShape)
            .border(1.dp, color.copy(alpha = .28f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    )
}

@Composable
private fun ConfidenceSelector(selected: String, select: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        listOf("Low", "Medium", "High").forEach { item ->
            val active = selected == item
            Text(
                item,
                color = if (active) Night else Muted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
                    .background(if (active) Cyan else Panel, RoundedCornerShape(10.dp))
                    .border(1.dp, if (active) Cyan else Border, RoundedCornerShape(10.dp))
                    .clickable { select(item) }
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun LessonSummaryPage(
    module: CurriculumModule,
    lesson: CurriculumLesson,
    checks: List<KnowledgeCheck>,
    answers: Map<Int, Int>,
    confidence: Map<Int, String>,
    onComplete: () -> Unit,
    onQuiz: () -> Unit
) {
    val score = checks.indices.count { answers[it] == checks[it].correct }
    val weakSkills = checks.mapIndexedNotNull { index, check ->
        val wrong = answers[index] != check.correct
        val lowConfidence = confidence[index] == "Low"
        "${check.skill} (${check.reviewPage})".takeIf { wrong || lowConfidence }
    }.distinct()
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(112.dp).background(Brush.radialGradient(listOf(Green.copy(.3f), Color.Transparent)), CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(if (score >= checks.size * .8f) "✓" else "★", color = Green, fontSize = 54.sp) }
        Text("Lesson MCQs complete", color = White, fontSize = 25.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text("$score/${checks.size} correct · ${score * 10 + confidence.values.count { it == "High" } * 2} XP earned", color = Orange, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
        GlowCard(Modifier.fillMaxWidth().padding(top = 18.dp), module.color) {
            Text("MASTERY SNAPSHOT", color = module.color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(lesson.concept, color = White, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 7.dp))
            Text("Key relationship: ${lesson.formula}", color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }
        GlowCard(Modifier.fillMaxWidth().padding(top = 12.dp), if (weakSkills.isEmpty()) Green else Orange) {
            Text(if (weakSkills.isEmpty()) "READY FOR NEXT LESSON" else "REVIEW NEXT", color = if (weakSkills.isEmpty()) Green else Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(
                if (weakSkills.isEmpty()) "Your answers and confidence are strong enough to move on."
                else "Focus on ${weakSkills.take(4).joinToString(", ")} before the full practice quiz.",
                color = White,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 7.dp)
            )
        }
        Button(
            onClick = onComplete, modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            colors = ButtonDefaults.buttonColors(module.color), shape = RoundedCornerShape(11.dp)
        ) { Text("Complete & return to module", color = Night, fontWeight = FontWeight.Bold) }
        Button(
            onClick = onQuiz, modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(Panel), shape = RoundedCornerShape(11.dp)
        ) { Text("Open full practice quiz", color = Cyan) }
    }
}

private fun remediationForCheck(check: KnowledgeCheck): String {
    val action = when (check.skill) {
        "Discover purpose", "Daily-life link" -> "revisit the opening example and say why the topic matters."
        "Simple definition", "Vocabulary", "Memory trick" -> "reread the simple meaning, key words and memory trick."
        "Home observation", "School observation", "Mini activity" -> "repeat the observation step and write what you see before explaining."
        "Prediction", "Reasoning", "Misconception" -> "compare your prediction with the reason and common mistake."
        "Revision fact", "Exam answer", "Takeaway", "Weak-topic repair" -> "rewrite the exam-ready answer in one short sentence."
        "Formula recall" -> "rewrite the formula, define each symbol, then test the units."
        "Problem setup" -> "identify given data, unknown, assumptions and the first valid relationship."
        "Misconception check" -> "compare the false shortcut with the lesson's common mistake callout."
        "Application" -> "connect the concept to a measurable real-world use, not a memorised phrase."
        "Extension" -> "revisit the advanced connection after the core explanation is stable."
        else -> "reread the concept, hint and explanation, then explain why the other options fail."
    }
    return "Remediation: open ${check.reviewPage} and $action"
}

@Composable
private fun LessonChallengePage(
    module: CurriculumModule,
    checks: List<KnowledgeCheck>,
    questionIndex: Int,
    selected: Int,
    choose: (Int) -> Unit,
    next: () -> Unit,
    previous: () -> Unit
) {
    val check = checks[questionIndex]
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Tag("KNOWLEDGE CHECK · ${questionIndex + 1}/${checks.size}", Orange)
        LinearProgressIndicator(
            progress = { (questionIndex + 1) / checks.size.toFloat() },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            color = module.color,
            trackColor = Border
        )
        Text(check.question, color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp, modifier = Modifier.padding(vertical = 18.dp))
        check.options.forEachIndexed { index, option ->
            val correct = selected >= 0 && index == check.correct
            val wrong = selected == index && index != check.correct
            val color = if (correct) Green else if (wrong) Color(0xFFFF6378) else Border
            Row(
                Modifier.fillMaxWidth().padding(bottom = 9.dp).background(color.copy(.1f), RoundedCornerShape(12.dp))
                    .border(1.dp, color, RoundedCornerShape(12.dp)).clickable(enabled = selected < 0) { choose(index) }.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(28.dp).background(color.copy(.12f), CircleShape), contentAlignment = Alignment.Center) {
                    Text(('A' + index).toString(), color = if (selected < 0) Muted else color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Text(option, color = White, modifier = Modifier.weight(1f).padding(horizontal = 12.dp))
                if (correct) Text("✓", color = Green, fontWeight = FontWeight.Bold)
            }
        }
        if (selected >= 0) {
            GlowCard(Modifier.fillMaxWidth().padding(top = 8.dp), if (selected == check.correct) Green else Orange) {
                Text(if (selected == check.correct) "Correct" else "Review this reasoning", color = if (selected == check.correct) Green else Orange, fontWeight = FontWeight.Bold)
                Text(check.explanation, color = Muted, fontSize = 11.sp, lineHeight = 17.sp)
            }
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (questionIndex > 0) {
                    Button(onClick = previous, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(Panel)) {
                        Text("Previous", color = White)
                    }
                }
                if (questionIndex < checks.lastIndex) {
                    Button(onClick = next, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(module.color)) {
                        Text("Next question", color = Night, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonSummaryPage(
    module: CurriculumModule,
    lesson: CurriculumLesson,
    score: Int,
    total: Int,
    onComplete: () -> Unit,
    onQuiz: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(112.dp).background(Brush.radialGradient(listOf(Green.copy(.3f), Color.Transparent)), CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(if (score >= total * .8f) "✓" else "★", color = Green, fontSize = 54.sp) }
        Text("Lesson complete", color = White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text("$score/$total correct · ${score * 10} XP earned", color = Orange, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
        GlowCard(Modifier.fillMaxWidth().padding(top = 20.dp), module.color) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AtomGraphic(56.dp, module.color)
                Column(Modifier.weight(1f).padding(start = 11.dp)) {
                    Text("REMEMBER", color = Muted, fontSize = 9.sp)
                    Text(lesson.concept, color = White, fontSize = 12.sp, lineHeight = 19.sp, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
            Text(lesson.formula, color = module.color, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onComplete, modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            colors = ButtonDefaults.buttonColors(module.color), shape = RoundedCornerShape(11.dp)
        ) { Text("Complete & return to module", color = Night, fontWeight = FontWeight.Bold) }
        Button(
            onClick = onQuiz, modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(Panel), shape = RoundedCornerShape(11.dp)
        ) { Text("Open full practice quiz", color = Cyan) }
    }
}

@Composable
private fun ChemistryLessonVisual(color: Color, icon: String, intensity: Float) {
    val reduceMotion = LocalReducedMotion.current
    val transition = rememberInfiniteTransition(label = "lesson-motion")
    val animatedPhase by transition.animateFloat(0f, 6.28f, infiniteRepeatable(tween(3000), RepeatMode.Restart), label = "phase")
    val phase = if (reduceMotion) .85f else animatedPhase
    Box(Modifier.fillMaxWidth().height(190.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Brush.radialGradient(listOf(color.copy(.22f), Color.Transparent), center, size.minDimension * .55f), size.minDimension * .48f)
            repeat(12) { index ->
                val x = size.width * (.12f + (index % 6) * .15f)
                val baseY = size.height * (.27f + (index / 6) * .46f)
                val y = baseY + sin(phase + index).toFloat() * (8f + intensity * 22f)
                drawLine(color.copy(.25f), center, Offset(x, y), 1.5f)
                drawCircle(if (index % 3 == 0) Orange else color, 5f + intensity * 5f, Offset(x, y))
                drawCircle(Color.White.copy(.7f), 2f, Offset(x - 2f, y - 2f))
            }
            drawArc(color.copy(.7f), 195f, 150f * intensity, false, style = Stroke(5f, cap = StrokeCap.Round))
        }
        Text(icon, color = color, fontSize = 45.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MiniFact(label: String, value: String, color: Color, modifier: Modifier) {
    Column(modifier.background(Panel, RoundedCornerShape(10.dp)).padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Text(value, color = White, fontSize = 11.sp)
    }
}

private fun controlLabels(module: String): Pair<String, String> = when (module) {
    "Atomic Structure", "Atomic Structure: Senior Secondary" -> "Electron energy" to "eV"
    "Periodic Table", "Periodic Table and Trends" -> "Position across period" to "%"
    "Chemical Bonding", "Chemical Bonding: Senior Secondary" -> "Bond polarity" to "%"
    "States of Matter", "States and Phases: Senior Secondary" -> "Temperature" to "K"
    "Organic Chemistry" -> "Reaction progress" to "%"
    "Chemical Reactions", "Reaction Equations and Redox" -> "Reactant concentration" to "%"
    "Equilibrium and Solutions" -> "Added reactant" to "%"
    "Kinetics and Electrochemistry" -> "Activation input" to "mV"
    else -> "Measured signal" to "%"
}

private fun observation(module: String, value: Float): String = when (module) {
    "Atomic Structure", "Atomic Structure: Senior Secondary" -> if (value > .65f) "The electron occupies a higher-energy region farther from the nucleus." else "The electron remains in a compact, lower-energy region."
    "Periodic Table", "Periodic Table and Trends" -> if (value > .65f) "Moving right generally increases effective nuclear attraction and electronegativity." else "The left side is dominated by larger, more metallic atoms."
    "Chemical Bonding", "Chemical Bonding: Senior Secondary" -> if (value > .65f) "Greater electronegativity difference shifts electron density and increases ionic character." else "Electron density is shared more evenly, producing covalent character."
    "States of Matter", "States and Phases: Senior Secondary" -> if (value > .65f) "Particles move faster and overcome more intermolecular attraction." else "Particles cluster as kinetic energy falls below attractive forces."
    "Equilibrium and Solutions" -> if (value > .65f) "The system responds to the concentration stress by favoring product formation." else "Only a small equilibrium shift is produced at this setting."
    "Kinetics and Electrochemistry" -> if (value > .65f) "More particles overcome the activation barrier, increasing the observed rate." else "Few collisions have enough energy to react successfully."
    "Analytical and Applied Chemistry" -> "The response grows with concentration; calibration converts this signal into a measured amount."
    else -> if (value > .65f) "Frequent energetic collisions push the transformation toward products." else "The molecular system changes slowly under these conditions."
}

private fun formulaMeaning(module: String): String = when (module) {
    "Atomic Structure", "Atomic Structure: Senior Secondary" -> "Symbols describe energy, frequency, particle counts or quantum states. Units are shown in the lesson context."
    "Periodic Table", "Periodic Table and Trends" -> "The relationship summarizes how effective nuclear attraction and shielding change across the table."
    "Chemical Bonding", "Chemical Bonding: Senior Secondary" -> "Bond notation connects shared or transferred valence electrons with structure and bond strength."
    "States of Matter", "States and Phases: Senior Secondary" -> "P is pressure, V volume, n amount, R the gas constant and T absolute temperature where applicable."
    "Organic Chemistry" -> "n represents the number of carbon atoms or repeating units; functional notation identifies reactive groups."
    "Chemical Reactions", "Reaction Equations and Redox" -> "Coefficients express mole ratios while preserving every atom and overall charge."
    "Equilibrium and Solutions" -> "Concentrations are equilibrium molar values; stoichiometric powers follow the balanced equation."
    "Kinetics and Electrochemistry" -> "k is the rate constant, E potential, I current, t time and concentration powers are reaction orders."
    else -> "Signal, concentration and calibration constants connect an instrumental response to the amount of analyte."
}

