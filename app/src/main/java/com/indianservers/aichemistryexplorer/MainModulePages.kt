package com.indianservers.aichemistryexplorer

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

private data class LabExperiment(
    val title: String,
    val subtitle: String,
    val color: Color,
    val difficulty: String
)

private val labExperiments = listOf(
    LabExperiment("Acid–Base Titration", "Determine unknown HCl concentration", Cyan, "Medium"),
    LabExperiment("Reaction Rate", "Observe concentration and temperature effects", Orange, "Easy"),
    LabExperiment("Electrochemical Cell", "Build a working Zn–Cu galvanic cell", Purple, "Medium"),
    LabExperiment("Flame Test", "Identify metal ions from emission colors", Green, "Easy"),
    LabExperiment("Solubility Lab", "Map precipitation and saturation", Color(0xFF4D8BFF), "Advanced")
)

@Composable
internal fun MultiPageLabScreen(onMessage: (String) -> Unit) {
    var page by remember { mutableIntStateOf(0) }
    var activeExperiment by remember { mutableIntStateOf(0) }
    var safetyCleared by remember { mutableStateOf(false) }
    val pages = listOf("Experiments", "Workspace", "Safety", "Results")
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Virtual Lab", "${pages[page]} · interactive chemistry")
        ModuleTabs(pages, page, Cyan) { page = it }
        when (page) {
            0 -> LabExperimentsPage { index -> activeExperiment = index; page = 1 }
            1 -> LabWorkspacePage(labExperiments[activeExperiment], safetyCleared, onMessage)
            2 -> LabSafetyPage { safetyCleared = it }
            else -> LabResultsPage()
        }
    }
}

@Composable
private fun LabExperimentsPage(open: (Int) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        item {
            GlowCard(Modifier.fillMaxWidth().padding(bottom = 14.dp), Cyan) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AtomGraphic(70.dp, Cyan)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Tag("5 LIVE EXPERIMENTS", Green)
                        Text("Choose your laboratory", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
                        Text("Every control produces a measurable result.", color = Muted, fontSize = 10.sp)
                    }
                }
            }
        }
        itemsIndexed(labExperiments) { index, experiment ->
            Card(
                Modifier.fillMaxWidth().padding(bottom = 9.dp).clickable { open(index) },
                colors = CardDefaults.cardColors(Panel),
                border = androidx.compose.foundation.BorderStroke(1.dp, experiment.color.copy(.42f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp).background(experiment.color.copy(.13f), RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) {
                        Text(if (index == 2) "ϟ" else "⚗", color = experiment.color, fontSize = 23.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(experiment.title, color = White, fontWeight = FontWeight.SemiBold)
                        Text(experiment.subtitle, color = Muted, fontSize = 10.sp)
                    }
                    Tag(experiment.difficulty.uppercase(), experiment.color)
                }
            }
        }
    }
}

@Composable
private fun LabWorkspacePage(experiment: LabExperiment, safetyCleared: Boolean, onMessage: (String) -> Unit) {
    var inputA by remember(experiment.title) { mutableFloatStateOf(.42f) }
    var inputB by remember(experiment.title) { mutableFloatStateOf(.58f) }
    var running by remember(experiment.title) { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        GlowCard(Modifier.fillMaxWidth(), experiment.color) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(68.dp).background(experiment.color.copy(.13f), CircleShape), contentAlignment = Alignment.Center) {
                    Text("⚗", color = experiment.color, fontSize = 34.sp)
                }
                Spacer(Modifier.width(13.dp))
                Column {
                    Tag(if (running) "RUNNING" else "READY", if (running) Green else experiment.color)
                    Text(experiment.title, color = White, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
                    Text(experiment.subtitle, color = Muted, fontSize = 10.sp)
                }
            }
        }
        GlowCard(Modifier.fillMaxWidth().padding(top = 12.dp), if (safetyCleared) Green else Orange) {
            Text("SAFETY GATE", color = if (safetyCleared) Green else Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(if (safetyCleared) "Checklist cleared for this session." else "Complete the safety page before treating results as valid.", color = White, fontSize = 11.sp, modifier = Modifier.padding(top = 5.dp))
        }
        LabControl("Primary concentration", inputA, "${(inputA * 2).format(2)} M") { inputA = it }
        LabControl("Temperature / volume", inputB, "${(280 + inputB * 70).toInt()} units") { inputB = it }
        ReactionDisplay(experiment.color, inputA, inputB, running)
        GlowCard(Modifier.fillMaxWidth().padding(top = 12.dp), Green) {
            Text("LIVE MEASUREMENT", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(
                if (running) "${((inputA + inputB) * 48.7f).format(2)} calculated response" else "Start the experiment to collect data",
                color = White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp)
            )
            Text(if (running) "Stable reading · uncertainty ±0.04" else "Controls remain editable before starting.", color = Muted, fontSize = 10.sp)
        }
        Button(
            onClick = {
                running = !running
                onMessage(if (running) "${experiment.title} started" else "Experiment paused")
            },
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            colors = ButtonDefaults.buttonColors(if (running) Purple else experiment.color),
            shape = RoundedCornerShape(12.dp)
        ) { Text(if (running) "Pause experiment" else "Start experiment", color = Night, fontWeight = FontWeight.Bold) }
        ResetTextButton("Reset controls") {
            inputA = .42f
            inputB = .58f
            running = false
        }
    }
}

@Composable
private fun LabControl(label: String, value: Float, display: String, change: (Float) -> Unit) {
    Column(Modifier.padding(top = 16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = White, fontSize = 12.sp)
            Text(display, color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Slider(value, change, colors = SliderDefaults.colors(thumbColor = Cyan, activeTrackColor = Cyan, inactiveTrackColor = Border))
    }
}

@Composable
private fun ReactionDisplay(color: Color, first: Float, second: Float, active: Boolean) {
    Canvas(Modifier.fillMaxWidth().height(125.dp).padding(top = 8.dp)) {
        val count = 12
        repeat(count) { index ->
            val x = size.width * (.08f + (index % 6) * .17f)
            val y = size.height * (.25f + (index / 6) * .52f)
            val radius = 6f + if (index % 2 == 0) first * 8f else second * 8f
            drawCircle(if (index % 3 == 0) Orange else color, radius, Offset(x, y))
            if (active && index < count - 1) drawLine(color.copy(.28f), Offset(x, y), center, 2f, StrokeCap.Round)
        }
        drawCircle(color.copy(.16f), 38f, center)
        drawCircle(if (active) Green else Muted, 9f, center)
    }
}

@Composable
private fun LabSafetyPage(onCleared: (Boolean) -> Unit) {
    val rules = listOf(
        "Wear safety goggles and a laboratory coat",
        "Inspect glassware before beginning",
        "Confirm chemical labels and concentrations",
        "Keep incompatible reagents separated",
        "Dispose of products in the correct container",
        "Record observations before leaving"
    )
    var checked by remember { mutableStateOf(setOf<Int>()) }
    LaunchedEffect(checked) { onCleared(checked.size == rules.size) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        GlowCard(Modifier.fillMaxWidth(), Green) {
            Text("LAB SAFETY CHECK", color = Green, fontWeight = FontWeight.Bold)
            Text("Complete the checklist before entering the workspace.", color = Muted, fontSize = 11.sp)
            LinearProgressIndicator(
                progress = { checked.size / rules.size.toFloat() },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(6.dp),
                color = Green, trackColor = Border
            )
        }
        rules.forEachIndexed { index, rule ->
            Row(
                Modifier.fillMaxWidth().padding(top = 9.dp).background(Panel, RoundedCornerShape(11.dp))
                    .border(1.dp, if (index in checked) Green.copy(.5f) else Border, RoundedCornerShape(11.dp))
                    .clickable { checked = if (index in checked) checked - index else checked + index }.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = index in checked,
                    onCheckedChange = { checked = if (index in checked) checked - index else checked + index },
                    colors = CheckboxDefaults.colors(checkedColor = Green)
                )
                Text(rule, color = White, fontSize = 12.sp)
            }
        }
        if (checked.size == rules.size) {
            Text("✓ Safety clearance complete", color = Green, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(18.dp))
        }
    }
}

@Composable
private fun LabResultsPage() {
    val results = listOf(
        Triple("Acid–Base Titration", "Endpoint 24.80 mL · pH 7.1", "Today"),
        Triple("Reaction Rate", "k = 0.034 s⁻¹ · 298 K", "Yesterday"),
        Triple("Flame Test", "Unknown identified: Cu²⁺", "3 days ago")
    )
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                SmallMetric("RUNS", "12", Cyan, Modifier.weight(1f))
                SmallMetric("ACCURACY", "94%", Green, Modifier.weight(1f))
                SmallMetric("XP", "860", Orange, Modifier.weight(1f))
            }
            Text("RECENT REPORTS", color = Muted, fontSize = 9.sp, modifier = Modifier.padding(top = 22.dp, bottom = 8.dp))
        }
        itemsIndexed(results) { index, result ->
            GlowCard(Modifier.fillMaxWidth().padding(bottom = 9.dp), if (index == 0) Cyan else Purple) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(result.first, color = White, fontWeight = FontWeight.SemiBold)
                    Text(result.third, color = Muted, fontSize = 9.sp)
                }
                Text(result.second, color = if (index == 0) Cyan else Green, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}

@Composable
internal fun MultiPageQuizScreen() {
    var page by remember { mutableIntStateOf(0) }
    val pages = listOf("Quiz", "Packs", "Review", "History", "League")
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Quiz Zone", "${pages[page]} · reusable chemistry practice")
        ModuleTabs(pages, page, Purple) { page = it }
        when (page) {
            0 -> ReusableQuizModulePage()
            1 -> DynamicMcqLibraryPage()
            2 -> QuizReviewPage()
            3 -> QuizHistoryPage()
            else -> QuizLeaguePage()
        }
    }
}

@Composable
private fun QuizReviewPage() {
    val cards = listOf(
        "Electronegativity" to "The ability of an atom to attract shared electrons.",
        "Activation energy" to "Minimum energy required for a successful reaction.",
        "Molarity" to "Moles of solute divided by liters of solution.",
        "Oxidation" to "Loss of electrons and increase in oxidation state."
    )
    var flipped by remember { mutableIntStateOf(-1) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        item { Text("Tap a card to reveal its definition.", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 10.dp)) }
        itemsIndexed(cards) { index, card ->
            GlowCard(
                Modifier.fillMaxWidth().height(105.dp).padding(bottom = 9.dp).clickable { flipped = if (flipped == index) -1 else index },
                if (flipped == index) Green else Purple
            ) {
                Text(if (flipped == index) "DEFINITION" else "TERM", color = Muted, fontSize = 8.sp)
                Text(if (flipped == index) card.second else card.first, color = if (flipped == index) White else Purple, fontSize = if (flipped == index) 13.sp else 19.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun QuizHistoryPage() {
    val attempts = listOf("Periodic Trends" to 90, "Chemical Bonding" to 80, "Inorganic Chemistry" to 70, "Organic Chemistry" to 85)
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        itemsIndexed(attempts) { _, attempt ->
            GlowCard(Modifier.fillMaxWidth().padding(bottom = 9.dp), if (attempt.second >= 80) Green else Orange) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(attempt.first, color = White, fontWeight = FontWeight.SemiBold)
                    Text("${attempt.second}%", color = if (attempt.second >= 80) Green else Orange, fontWeight = FontWeight.Bold)
                }
                LinearProgressIndicator(
                    progress = { attempt.second / 100f }, modifier = Modifier.fillMaxWidth().padding(top = 9.dp).height(5.dp),
                    color = if (attempt.second >= 80) Green else Orange, trackColor = Border
                )
            }
        }
    }
}

@Composable
private fun QuizLeaguePage() {
    val leaders = listOf("Maya" to 4280, "Arjun" to 3910, "ChemLearner" to 3650, "Noah" to 3420, "Sara" to 3180)
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        item {
            GlowCard(Modifier.fillMaxWidth().padding(bottom = 14.dp), Orange) {
                Text("WEEKLY CHEMISTRY LEAGUE", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("Top 10% · 2 days remaining", color = White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
        itemsIndexed(leaders) { index, leader ->
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp).background(Panel, RoundedCornerShape(11.dp))
                    .border(1.dp, Border, RoundedCornerShape(11.dp)).padding(13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${index + 1}", color = if (index < 3) Orange else Muted, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
                Text(leader.first, color = White, modifier = Modifier.weight(1f))
                Text("${leader.second} XP", color = Muted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
internal fun MultiPageProgressScreen() {
    var page by remember { mutableIntStateOf(0) }
    val pages = listOf("Overview", "Topics", "Awards", "Activity")
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Your Progress", "${pages[page]} · 72% overall")
        ModuleTabs(pages, page, Green) { page = it }
        when (page) {
            0 -> ProgressOverviewPage()
            1 -> ProgressTopicsPage()
            2 -> ProgressAwardsPage()
            else -> ProgressActivityPage()
        }
    }
}

@Composable
private fun ProgressOverviewPage() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        GlowCard(Modifier.fillMaxWidth(), Cyan) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressRing(72, Cyan)
                Spacer(Modifier.width(18.dp))
                Column {
                    Text("Strong momentum", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("7-day streak · 2,450 XP", color = Cyan, fontSize = 11.sp)
                    Text("12 lessons completed this week", color = Muted, fontSize = 10.sp)
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 13.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            SmallMetric("LESSONS", "34", Purple, Modifier.weight(1f))
            SmallMetric("QUIZZES", "18", Orange, Modifier.weight(1f))
            SmallMetric("LABS", "12", Green, Modifier.weight(1f))
        }
        GlowCard(Modifier.fillMaxWidth().padding(top = 13.dp), Green) {
            Text("NEXT MILESTONE", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("Complete 3 more lessons to reach Level 13", color = White, fontSize = 13.sp, modifier = Modifier.padding(vertical = 7.dp))
            LinearProgressIndicator(progress = { .78f }, modifier = Modifier.fillMaxWidth().height(5.dp), color = Green, trackColor = Border)
        }
    }
}

@Composable
private fun ProgressTopicsPage() {
    val topics = listOf("Atomic Structure" to .92f, "Periodic Table" to .84f, "Chemical Bonding" to .78f, "Inorganic Chemistry" to .65f, "Organic Chemistry" to .58f, "Electrochemistry" to .52f)
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        itemsIndexed(topics) { index, topic ->
            GlowCard(Modifier.fillMaxWidth().padding(bottom = 9.dp), if (index % 2 == 0) Cyan else Purple) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(topic.first, color = White, fontSize = 12.sp)
                    Text("${(topic.second * 100).toInt()}%", color = if (index % 2 == 0) Cyan else Purple, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                LinearProgressIndicator(
                    progress = { topic.second }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(5.dp),
                    color = if (index % 2 == 0) Cyan else Purple, trackColor = Border
                )
            }
        }
    }
}

@Composable
private fun ProgressAwardsPage() {
    val awards = listOf(
        Triple("Lab Master", "Complete 5 experiments", Cyan),
        Triple("Quiz Champion", "Score 90% in 10 quizzes", Orange),
        Triple("Element Expert", "Explore 50 elements", Purple),
        Triple("Perfect Week", "Study seven days in a row", Green),
        Triple("Reaction Pro", "Balance 25 equations", Color(0xFF4D8BFF)),
        Triple("Deep Thinker", "Complete all visual labs", Color(0xFFE34C9A))
    )
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        itemsIndexed(awards) { index, award ->
            Card(
                Modifier.fillMaxWidth().padding(bottom = 9.dp),
                colors = CardDefaults.cardColors(Panel), shape = RoundedCornerShape(13.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, award.third.copy(.45f))
            ) {
                Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(48.dp).background(award.third.copy(.13f), CircleShape), contentAlignment = Alignment.Center) {
                        Text(if (index < 4) "★" else "◇", color = award.third, fontSize = 24.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(award.first, color = White, fontWeight = FontWeight.SemiBold)
                        Text(award.second, color = Muted, fontSize = 10.sp)
                    }
                    Tag(if (index < 4) "EARNED" else "LOCKED", if (index < 4) Green else Muted)
                }
            }
        }
    }
}

@Composable
private fun ProgressActivityPage() {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val activity = listOf(.35f, .7f, .55f, .9f, .45f, .8f, 1f)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        GlowCard(Modifier.fillMaxWidth(), Purple) {
            Text("THIS WEEK", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth().height(150.dp).padding(top = 14.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                days.forEachIndexed { index, day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(25.dp, (activity[index] * 105).dp).background(Brush.verticalGradient(listOf(Cyan, Purple)), RoundedCornerShape(7.dp)))
                        Text(day, color = Muted, fontSize = 9.sp, modifier = Modifier.padding(top = 5.dp))
                    }
                }
            }
        }
        Text("RECENT ACTIVITY", color = Muted, fontSize = 9.sp, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
        listOf(
            "Completed Atomic Radius lesson" to "+100 XP",
            "Scored 90% in Periodic Trends" to "+180 XP",
            "Finished Acid–Base Titration" to "+120 XP",
            "Saved Copper to notebook" to "+20 XP"
        ).forEach { event ->
            Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(Cyan, CircleShape))
                Text(event.first, color = White, fontSize = 11.sp, modifier = Modifier.weight(1f).padding(start = 11.dp))
                Text(event.second, color = Green, fontSize = 10.sp)
            }
            HorizontalDivider(color = Border)
        }
    }
}

@Composable
private fun ModuleTabs(labels: List<String>, selected: Int, color: Color, select: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        labels.forEachIndexed { index, label ->
            Text(
                label, color = if (selected == index) Night else Muted, fontSize = 8.sp, textAlign = TextAlign.Center, maxLines = 1,
                modifier = Modifier.weight(1f).background(if (selected == index) color else Panel, RoundedCornerShape(8.dp))
                    .border(1.dp, if (selected == index) color else Border, RoundedCornerShape(8.dp))
                    .clickable { select(index) }.padding(vertical = 9.dp)
            )
        }
    }
}

@Composable
private fun SmallMetric(label: String, value: String, color: Color, modifier: Modifier) {
    Column(
        modifier.background(Panel, RoundedCornerShape(11.dp)).border(1.dp, color.copy(.35f), RoundedCornerShape(11.dp)).padding(11.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = color, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Muted, fontSize = 7.sp)
    }
}

@Composable
private fun ProgressRing(value: Int, color: Color) {
    Box(Modifier.size(98.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawArc(Border, -90f, 360f, false, style = Stroke(10f, cap = StrokeCap.Round))
            drawArc(Brush.sweepGradient(listOf(Purple, color)), -90f, value * 3.6f, false, style = Stroke(10f, cap = StrokeCap.Round))
        }
        Text("$value%", color = White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
    }
}

private fun Float.format(decimals: Int): String = "%.${decimals}f".format(this)
