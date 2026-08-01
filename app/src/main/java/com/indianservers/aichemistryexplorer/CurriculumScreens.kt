package com.indianservers.aichemistryexplorer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

@Composable
internal fun CurriculumModuleScreen(
    moduleTitle: String,
    completedLessons: Set<String>,
    onBack: () -> Unit,
    onLesson: (String) -> Unit
) {
    val module = curriculum.firstOrNull { it.title == moduleTitle } ?: curriculum.first()
    val completed = module.lessons.count { "${module.title}/${it.title}" in completedLessons }
    var modulePage by remember(module.title) { mutableIntStateOf(0) }
    val pageNames = listOf("Overview", "Lessons", "Formulas", "Progress")
    Column(Modifier.fillMaxSize()) {
        ScreenTitle(module.title, "${pageNames[modulePage]} · $completed/5 complete", onBack)
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
                else -> ModuleProgressPage(module, completed)
            }
        }
    }
}

@Composable
private fun ModuleOverviewPage(module: CurriculumModule, completed: Int, openLessons: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        GlowCard(Modifier.fillMaxWidth(), module.color) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(66.dp).background(module.color.copy(.14f), CircleShape), contentAlignment = Alignment.Center) {
                    Text(module.icon, color = module.color, fontSize = 34.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Tag("CORE MODULE", module.color)
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
                    Box(
                        Modifier.size(42.dp).background((if (done) Green else module.color).copy(.12f), RoundedCornerShape(11.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text(if (done) "✓" else "${index + 1}", color = if (done) Green else module.color, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(lesson.title, color = White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("4 pages · Concept · Visual Lab · Challenge", color = Muted, fontSize = 9.sp)
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
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(132.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                drawArc(Border, -90f, 360f, false, style = Stroke(12f, cap = StrokeCap.Round))
                drawArc(Brush.sweepGradient(listOf(Purple, module.color)), -90f, 360f * completed / 5f, false, style = Stroke(12f, cap = StrokeCap.Round))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${completed * 20}%", color = White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text("MASTERY", color = Muted, fontSize = 8.sp)
            }
        }
        Text(if (completed == 5) "Module mastered" else "${5 - completed} lessons remaining", color = module.color, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 13.dp))
        Row(Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            ModuleStat("LESSONS", "$completed/5", module.color, Modifier.weight(1f))
            ModuleStat("XP", "${completed * 100}", Orange, Modifier.weight(1f))
            ModuleStat("LEVEL", if (completed >= 4) "Expert" else if (completed >= 2) "Growing" else "Starter", Green, Modifier.weight(1f))
        }
        GlowCard(Modifier.fillMaxWidth().padding(top = 18.dp), module.color) {
            Text("MASTERY MILESTONES", color = Muted, fontSize = 9.sp)
            listOf("Complete all concept pages", "Run every visual lab", "Pass five knowledge checks").forEachIndexed { index, goal ->
                Row(Modifier.fillMaxWidth().padding(top = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (completed > index) "✓" else "○", color = if (completed > index) Green else Muted)
                    Text(goal, color = White, fontSize = 11.sp, modifier = Modifier.padding(start = 10.dp))
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
    var page by remember(moduleTitle, lessonTitle) { mutableIntStateOf(0) }
    var answer by remember(moduleTitle, lessonTitle) { mutableIntStateOf(-1) }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle(lesson.title, "${module.title} · Page ${page + 1}/4", onBack)
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            (0..3).forEach { index ->
                Box(
                    Modifier.weight(1f).padding(horizontal = 3.dp).height(5.dp)
                        .background(if (index <= page) module.color else Border, CircleShape)
                )
            }
        }
        AnimatedContent(page, modifier = Modifier.weight(1f), label = "curriculum-page") { current ->
            when (current) {
                0 -> LessonConceptPage(module, lesson)
                1 -> InteractiveVisualPage(module, lesson)
                2 -> LessonChallengePage(module, lesson, answer) { answer = it }
                else -> LessonSummaryPage(module, lesson, answer == lesson.correct, onComplete, onQuiz)
            }
        }
        if (page < 3) {
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
                    onClick = { page++ }, enabled = page != 2 || answer >= 0, modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(module.color, disabledContainerColor = Border), shape = RoundedCornerShape(11.dp)
                ) { Text(if (page == 2) "View summary" else "Next page", color = Night, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun LessonConceptPage(module: CurriculumModule, lesson: CurriculumLesson) {
    var formulaOpen by remember(module.title, lesson.title) { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Tag("CONCEPT", module.color)
        ChemistryLessonVisual(module.color, module.icon, .48f)
        Text(lesson.title, color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(lesson.concept, color = Muted, fontSize = 14.sp, lineHeight = 22.sp, modifier = Modifier.padding(top = 9.dp, bottom = 16.dp))
        GlowCard(Modifier.fillMaxWidth().clickable { formulaOpen = !formulaOpen }, module.color) {
            Text("KEY RELATIONSHIP", color = Muted, fontSize = 9.sp)
            Text(lesson.formula, color = module.color, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 7.dp))
            Text(if (formulaOpen) formulaMeaning(module.title) else "Tap to explain variables and meaning", color = if (formulaOpen) White else Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 7.dp))
        }
        Row(Modifier.fillMaxWidth().padding(top = 17.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniFact("READ", "2 min", module.color, Modifier.weight(1f))
            MiniFact("VISUAL", "Interactive", Green, Modifier.weight(1f))
            MiniFact("XP", "+50", Orange, Modifier.weight(1f))
        }
    }
}

@Composable
private fun InteractiveVisualPage(module: CurriculumModule, lesson: CurriculumLesson) {
    var control by remember(module.title, lesson.title) { mutableFloatStateOf(.45f) }
    var running by remember(module.title, lesson.title) { mutableStateOf(true) }
    val labels = controlLabels(module.title)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Tag("VISUAL LAB", Green)
        Text("Manipulate the model", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        Text("Move the control and observe how the particle model and response graph change.", color = Muted, fontSize = 12.sp)
        ChemistryLessonVisual(module.color, module.icon, if (running) control else .05f)
        GlowCard(Modifier.fillMaxWidth(), module.color) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(labels.first, color = White, fontSize = 12.sp)
                Text("${(control * 100).toInt()} ${labels.second}", color = module.color, fontWeight = FontWeight.Bold)
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
            Text(observation(module.title, control), color = White, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
        }
        Row(Modifier.fillMaxWidth().padding(top = 11.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(if (running) "Ⅱ  Pause" else "▶  Play", "↺  Reset", "↻  Replay").forEachIndexed { index, label ->
                Text(
                    label, color = if (index == 0) Cyan else Muted, fontSize = 10.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).background(Panel, RoundedCornerShape(9.dp)).border(1.dp, Border, RoundedCornerShape(9.dp))
                        .clickable {
                            when (index) {
                                0 -> running = !running
                                1 -> { control = .45f; running = false }
                                else -> { control = .15f; running = true }
                            }
                        }.padding(vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun LessonChallengePage(module: CurriculumModule, lesson: CurriculumLesson, selected: Int, choose: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Tag("KNOWLEDGE CHECK", Orange)
        Text(lesson.question, color = White, fontSize = 21.sp, fontWeight = FontWeight.Bold, lineHeight = 29.sp, modifier = Modifier.padding(vertical = 18.dp))
        lesson.answers.forEachIndexed { index, option ->
            val correct = selected >= 0 && index == lesson.correct
            val wrong = selected == index && index != lesson.correct
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
            GlowCard(Modifier.fillMaxWidth().padding(top = 8.dp), if (selected == lesson.correct) Green else Orange) {
                Text(if (selected == lesson.correct) "Correct · +50 XP" else "Not quite", color = if (selected == lesson.correct) Green else Orange, fontWeight = FontWeight.Bold)
                Text(if (selected == lesson.correct) "You’ve understood this concept." else "The highlighted answer is correct. Review the key relationship before continuing.", color = Muted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun LessonSummaryPage(
    module: CurriculumModule,
    lesson: CurriculumLesson,
    correct: Boolean,
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
        ) { Text(if (correct) "✓" else "★", color = Green, fontSize = 54.sp) }
        Text("Lesson complete", color = White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text("${if (correct) 100 else 50} XP earned", color = Orange, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
        GlowCard(Modifier.fillMaxWidth().padding(top = 20.dp), module.color) {
            Text("REMEMBER", color = Muted, fontSize = 9.sp)
            Text(lesson.concept, color = White, fontSize = 12.sp, lineHeight = 19.sp, modifier = Modifier.padding(vertical = 8.dp))
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
    val transition = rememberInfiniteTransition(label = "lesson-motion")
    val phase by transition.animateFloat(0f, 6.28f, infiniteRepeatable(tween(3000), RepeatMode.Restart), label = "phase")
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
    "Atomic Structure" -> "Electron energy" to "eV"
    "Periodic Table" -> "Position across period" to "%"
    "Chemical Bonding" -> "Bond polarity" to "%"
    "States of Matter" -> "Temperature" to "K"
    "Organic Chemistry" -> "Reaction progress" to "%"
    "Chemical Reactions" -> "Reactant concentration" to "%"
    "Thermodynamics" -> "Thermal energy" to "kJ"
    "Equilibrium and Solutions" -> "Added reactant" to "%"
    "Kinetics and Electrochemistry" -> "Activation input" to "mV"
    else -> "Measured signal" to "%"
}

private fun observation(module: String, value: Float): String = when (module) {
    "Atomic Structure" -> if (value > .65f) "The electron occupies a higher-energy region farther from the nucleus." else "The electron remains in a compact, lower-energy region."
    "Periodic Table" -> if (value > .65f) "Moving right generally increases effective nuclear attraction and electronegativity." else "The left side is dominated by larger, more metallic atoms."
    "Chemical Bonding" -> if (value > .65f) "Greater electronegativity difference shifts electron density and increases ionic character." else "Electron density is shared more evenly, producing covalent character."
    "States of Matter" -> if (value > .65f) "Particles move faster and overcome more intermolecular attraction." else "Particles cluster as kinetic energy falls below attractive forces."
    "Thermodynamics" -> if (value > .65f) "More thermal energy increases molecular motion and accessible microstates." else "Lower thermal energy restricts motion and favors ordered states."
    "Equilibrium and Solutions" -> if (value > .65f) "The system responds to the concentration stress by favoring product formation." else "Only a small equilibrium shift is produced at this setting."
    "Kinetics and Electrochemistry" -> if (value > .65f) "More particles overcome the activation barrier, increasing the observed rate." else "Few collisions have enough energy to react successfully."
    "Analytical and Applied Chemistry" -> "The response grows with concentration; calibration converts this signal into a measured amount."
    else -> if (value > .65f) "Frequent energetic collisions push the transformation toward products." else "The molecular system changes slowly under these conditions."
}

private fun formulaMeaning(module: String): String = when (module) {
    "Atomic Structure" -> "Symbols describe energy, frequency, particle counts or quantum states. Units are shown in the lesson context."
    "Periodic Table" -> "The relationship summarizes how effective nuclear attraction and shielding change across the table."
    "Chemical Bonding" -> "Bond notation connects shared or transferred valence electrons with structure and bond strength."
    "States of Matter" -> "P is pressure, V volume, n amount, R the gas constant and T absolute temperature where applicable."
    "Organic Chemistry" -> "n represents the number of carbon atoms or repeating units; functional notation identifies reactive groups."
    "Chemical Reactions" -> "Coefficients express mole ratios while preserving every atom and overall charge."
    "Thermodynamics" -> "Δ means final minus initial; q is heat, w work, H enthalpy, S entropy and G Gibbs energy."
    "Equilibrium and Solutions" -> "Concentrations are equilibrium molar values; stoichiometric powers follow the balanced equation."
    "Kinetics and Electrochemistry" -> "k is the rate constant, E potential, I current, t time and concentration powers are reaction orders."
    else -> "Signal, concentration and calibration constants connect an instrumental response to the amount of analyte."
}
