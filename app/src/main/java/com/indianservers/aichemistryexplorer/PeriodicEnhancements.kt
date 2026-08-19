package com.indianservers.aichemistryexplorer

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val studioProperties = listOf(
    "Atomic radius", "Electronegativity", "Ionization energy", "Electron affinity",
    "Melting point", "Boiling point", "Density", "Metallic character", "Reactivity"
)

private fun Modifier.atomicModelTransform(
    onTransform: (zoom: Float, rotation: Float) -> Unit
): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Final)
        do {
            val event = awaitPointerEvent(PointerEventPass.Final)
            if (event.changes.count { it.pressed } >= 2) {
                onTransform(event.calculateZoom(), event.calculateRotation())
            }
        } while (event.changes.any { it.pressed })
    }
}

@Composable
internal fun PeriodicDiscoveryStudioScreen(
    elements: List<Element>,
    onBack: () -> Unit,
    onSelect: (Element) -> Unit
) {
    var section by remember { mutableStateOf("Visual Lab") }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Discovery Studio", "Visualize · learn · play", onBack)
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Visual Lab", "Learn", "Play").forEach {
                StudioPill(it, section == it, Modifier.weight(1f)) { section = it }
            }
        }
        when (section) {
            "Visual Lab" -> PeriodicVisualLab(elements, onSelect)
            "Learn" -> PeriodicGuidedLearning(elements, onSelect)
            else -> PeriodicGames(elements, onSelect)
        }
    }
}

@Composable
private fun PeriodicVisualLab(elements: List<Element>, onSelect: (Element) -> Unit) {
    var property by remember { mutableStateOf(studioProperties.first()) }
    var graphMode by remember { mutableStateOf("Period") }
    var period by remember { mutableIntStateOf(2) }
    var group by remember { mutableIntStateOf(1) }
    val graphedElements = remember(period, group, graphMode, elements) {
        if (graphMode == "Period") elements.filter { periodOf(it.number) == period }
        else elements.filter { groupOf(it.number) == group }
    }
    val accent = when (studioProperties.indexOf(property) % 4) {
        0 -> Cyan
        1 -> Purple
        2 -> Green
        else -> Orange
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        item {
            Text("PROPERTY MAPS", color = Muted, fontSize = 10.sp)
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 9.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                studioProperties.forEach { StudioPill(it, property == it) { property = it } }
            }
        }
        item {
            GlowCard(Modifier.fillMaxWidth(), accent) {
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    StudioPill("By period", graphMode == "Period") { graphMode = "Period" }
                    StudioPill("By group", graphMode == "Group") { graphMode = "Group" }
                }
                Text(
                    "$property across ${graphMode.lowercase()} ${if (graphMode == "Period") period else group}",
                    color = White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp)
                )
                Text("Tap a point to open its element profile.", color = Muted, fontSize = 10.sp)
                Spacer(Modifier.height(12.dp))
                TrendGraph(graphedElements, property, accent, onSelect)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(graphMode, color = Muted, fontSize = 10.sp, modifier = Modifier.width(48.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        val range = if (graphMode == "Period") 1..7 else 1..18
                        range.forEach { value ->
                            StudioPill(
                                "$value",
                                if (graphMode == "Period") period == value else group == value
                            ) {
                                if (graphMode == "Period") period = value else group = value
                            }
                        }
                    }
                }
            }
        }
        item {
            Text("INTERACTIVE EXPLORERS", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
            listOf(
                Triple("Drag-through trend explorer", "Move across the graph to compare changing relative values.", Cyan),
                Triple("Group relationship network", "Open any family to see shared valence patterns and behavior.", Purple),
                Triple("Physical-state simulator", "Element profiles show expected state and appearance at room temperature.", Green),
                Triple("Export-ready study view", "Use Android screenshot or sharing from an element profile to keep a study card.", Orange)
            ).forEach { (title, body, color) -> MiniFeatureCard(title, body, color) }
        }
    }
}

@Composable
private fun TrendGraph(elements: List<Element>, property: String, accent: Color, onSelect: (Element) -> Unit) {
    if (elements.isEmpty()) return
    Box(Modifier.fillMaxWidth().height(180.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val left = 18f
            val bottom = size.height - 28f
            val usableWidth = size.width - 36f
            val usableHeight = size.height - 45f
            drawLine(Border, Offset(left, 8f), Offset(left, bottom), 2f)
            drawLine(Border, Offset(left, bottom), Offset(size.width - 8f, bottom), 2f)
            val points = elements.mapIndexed { index, element ->
                val x = left + if (elements.size == 1) usableWidth / 2 else usableWidth * index / (elements.size - 1)
                val y = bottom - usableHeight * studioTrend(element, property)
                Offset(x, y)
            }
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(path, accent, style = Stroke(4f))
            points.forEach { drawCircle(accent, 7f, it) }
        }
        Row(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(start = 18.dp), horizontalArrangement = Arrangement.SpaceAround) {
            elements.forEach {
                Text(it.symbol, color = accent, fontSize = 8.sp, modifier = Modifier.clickable { onSelect(it) }.padding(4.dp))
            }
        }
    }
}

@Composable
private fun PeriodicGuidedLearning(elements: List<Element>, onSelect: (Element) -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val tour = listOf(
        "Periods are horizontal rows. Electron shells generally increase as you move downward.",
        "Groups are vertical families. Main-group members share related valence-electron patterns.",
        "Atomic radius grows toward the lower-left; effective nuclear attraction pulls it smaller toward the upper-right.",
        "Ionization energy and electronegativity generally rise toward the upper-right.",
        "The f-block is displayed below to keep the table compact, but belongs within periods 6 and 7."
    )
    val featured = elements[(step * 17 + 7) % elements.size]
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        item {
            GlowCard(Modifier.fillMaxWidth(), Purple) {
                Text("GUIDED TABLE TOUR · ${step + 1}/${tour.size}", color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(tour[step], color = White, fontSize = 15.sp, modifier = Modifier.padding(vertical = 12.dp))
                LinearProgressIndicator(
                    progress = { (step + 1) / tour.size.toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                    color = Purple,
                    trackColor = Border
                )
                Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Previous", color = if (step > 0) Cyan else Muted, modifier = Modifier.clickable(enabled = step > 0) { step-- }.padding(8.dp))
                    Text(if (step == tour.lastIndex) "Restart" else "Next", color = Cyan, modifier = Modifier.clickable { step = (step + 1) % tour.size }.padding(8.dp))
                }
            }
        }
        item {
            Text("WHY ARE THEY SIMILAR?", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
            MiniFeatureCard(
                featured.category,
                "${featured.name} belongs to the ${featured.category.lowercase()} family. Members share related outer-electron structure, so they often form similar ions and compounds.",
                featured.color
            ) { onSelect(featured) }
        }
        item {
            Text("LEARNING PATHS", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))
            listOf(
                Triple("Foundations", "Symbols, atomic numbers, periods and groups", Cyan),
                Triple("Patterns", "Radius, attraction, ionization and metallic behavior", Green),
                Triple("Families", "Compare alkali metals, halogens, noble gases and transition metals", Purple),
                Triple("Applications", "Connect elements to biology, industry, materials and safety", Orange)
            ).forEach { (a, b, c) -> MiniFeatureCard(a, b, c) }
        }
    }
}

@Composable
private fun PeriodicGames(elements: List<Element>, onSelect: (Element) -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("periodic_progress", 0) }
    var mode by remember { mutableStateOf("Identify") }
    var question by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(prefs.getInt("score", 0)) }
    var feedback by remember { mutableStateOf("Choose the correct answer") }
    val answer = elements[(question * 29 + 8) % elements.size]
    val choices = remember(question) {
        listOf(answer, elements[(answer.number + 22) % elements.size], elements[(answer.number + 57) % elements.size], elements[(answer.number + 91) % elements.size]).distinctBy { it.number }
    }
    val orderedChoices = remember(question) { choices.shuffled() }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            listOf("Identify", "Match", "Daily").forEach { StudioPill(it, mode == it) { mode = it; feedback = "Choose the correct answer" } }
        }
        Spacer(Modifier.height(16.dp))
        GlowCard(Modifier.fillMaxWidth(), Cyan) {
            Text(if (mode == "Daily") "DAILY ELEMENT CHALLENGE" else "${mode.uppercase()} ELEMENT", color = Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                when (mode) {
                    "Match" -> "Which element matches atomic number ${answer.number}?"
                    "Daily" -> "Today’s focus: identify ${answer.category.lowercase()} ${answer.symbol}."
                    else -> "Which element has the symbol ${answer.symbol}?"
                },
                color = White, fontSize = 18.sp, modifier = Modifier.padding(vertical = 14.dp)
            )
            orderedChoices.forEach { choice ->
                Text(
                    if (mode == "Match") choice.name else "${choice.name} · ${choice.number}",
                    color = White,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .background(Panel2, RoundedCornerShape(9.dp)).clickable {
                            if (choice.number == answer.number) {
                                score += 10
                                prefs.edit().putInt("score", score).apply()
                                feedback = "Correct · +10 XP"
                            } else feedback = "Not quite — try again"
                        }.padding(12.dp)
                )
            }
            Text(feedback, color = if (feedback.startsWith("Correct")) Green else Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
            Button(
                onClick = { question++; feedback = "Choose the correct answer" },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                colors = ButtonDefaults.buttonColors(Cyan)
            ) { Text("Next challenge", color = Night, fontWeight = FontWeight.Bold) }
        }
        Text("ACHIEVEMENTS", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
        listOf(
            Triple("Element Scout", score >= 30, "Earn 30 XP"),
            Triple("Pattern Finder", score >= 100, "Earn 100 XP"),
            Triple("Element Expert", score >= 250, "Earn 250 XP")
        ).forEach { (title, earned, goal) ->
            MiniFeatureCard(if (earned) "✓ $title" else "◇ $title", "$goal · Current $score XP", if (earned) Green else Muted)
        }
        Text("Open today’s element", color = Cyan, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().clickable { onSelect(answer) }.padding(14.dp))
    }
}

@Composable
internal fun ElementKnowledgeLab(element: Element) {
    var tab by remember { mutableStateOf("Atomic") }
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        listOf("Atomic", "Story", "Uses & safety", "Builder", "Notes & AI").forEach {
            StudioPill(it, tab == it) { tab = it }
        }
    }
    when (tab) {
        "Atomic" -> ElementAtomicLab(element)
        "Story" -> ElementStory(element)
        "Uses & safety" -> ElementUsesAndSafety(element)
        "Builder" -> ElementCompoundBuilder(element)
        else -> ElementNotesAndAssistant(element)
    }
}

@Composable
internal fun ElementHeaderAtom(element: Element) {
    val shells = remember(element.number) { electronShellDistribution(element.number) }
    Box(Modifier.size(150.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val availableRadius = size.minDimension * .44f
            val shellStep = availableRadius / shells.size.coerceAtLeast(1)

            shells.forEachIndexed { shellIndex, electronCount ->
                val radius = shellStep * (shellIndex + 1)
                drawCircle(
                    color = element.color.copy(alpha = if (shellIndex == shells.lastIndex) .58f else .28f),
                    radius = radius,
                    center = center,
                    style = Stroke(if (shellIndex == shells.lastIndex) 2.6f else 1.5f)
                )
                repeat(electronCount) { electron ->
                    val angle = (2.0 * PI * electron / electronCount.coerceAtLeast(1)) -
                        PI / 2 + shellIndex * .23
                    val electronCenter = Offset(
                        center.x + radius * cos(angle).toFloat(),
                        center.y + radius * sin(angle).toFloat()
                    )
                    drawCircle(element.color.copy(alpha = .22f), 6.5f, electronCenter)
                    drawCircle(White, 3.1f, electronCenter)
                }
            }

            drawCircle(element.color.copy(alpha = .22f), size.minDimension * .105f, center)
            drawCircle(element.color, size.minDimension * .068f, center)
            drawCircle(White.copy(alpha = .88f), size.minDimension * .022f, center)
        }
        Text(
            element.symbol,
            color = Night,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            "${element.number} e⁻ · ${shells.size} occupied shells",
            color = Muted,
            fontSize = 7.sp,
            modifier = Modifier.align(Alignment.BottomCenter)
                .background(Panel2.copy(alpha = .9f), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun ElementAtomicLab(element: Element) {
    val sourcedMassNumber = remember(element.number) {
        elementFacts(element.number).atomicMass.toDoubleOrNull()?.let { kotlin.math.round(it).toInt() }
            ?: element.number * 2
    }
    var isotope by remember(element.number) { mutableIntStateOf(sourcedMassNumber) }
    var selectedOxidation by remember(element.number) { mutableStateOf(oxidationStates(element).first()) }
    Column(Modifier.fillMaxWidth().padding(18.dp)) {
        FeatureHeading("INTERACTIVE BOHR MODEL")
        BohrModel(element)
        FeatureHeading("ELECTRON CONFIGURATION")
        Text(elementFacts(element.number).electronConfiguration, color = White, fontSize = 13.sp)
        OrbitalBoxes(element.number)
        FeatureHeading("ISOTOPE EXPLORER")
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StudioPill("−", false) { isotope = (isotope - 1).coerceAtLeast(element.number) }
            Text("${element.symbol}-$isotope", color = element.color, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            StudioPill("+", false) { isotope++ }
            Text("${isotope - element.number} neutrons", color = Muted, fontSize = 10.sp)
        }
        Text("Mass-number model; changing A does not imply that the isotope is stable or experimentally known.", color = Muted, fontSize = 8.sp, modifier = Modifier.padding(top = 5.dp))
        FeatureHeading("COMMON OXIDATION STATES")
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            oxidationStates(element).forEach { state ->
                StudioPill(state, state == selectedOxidation) { selectedOxidation = state }
            }
        }
        Text("Selected oxidation state: $selectedOxidation", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 7.dp))
    }
}

@Composable
private fun BohrModel(element: Element) {
    val shells = electronShellDistribution(element.number)
    var paused by remember { mutableStateOf(false) }
    var selectedShell by remember { mutableIntStateOf(0) }
    var modelScale by remember { mutableFloatStateOf(1f) }
    var manualRotation by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    val reducedMotion = remember {
        context.getSharedPreferences("periodic_table_settings", 0).getBoolean("reduced_motion", false)
    }
    val transition = rememberInfiniteTransition(label = "electron orbit")
    val orbit by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (reducedMotion) 0f else 360f,
        animationSpec = infiniteRepeatable(tween(7000), RepeatMode.Restart),
        label = "electron shell rotation"
    )
    val displayedOrbit = (if (paused) 0f else orbit) + manualRotation
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(
            Modifier.fillMaxWidth().height(300.dp)
                .atomicModelTransform { zoomChange, rotationChange ->
                    modelScale = (modelScale * zoomChange).coerceIn(.65f, 1.45f)
                    manualRotation = (manualRotation + rotationChange) % 360f
                }
                .clickable { selectedShell = (selectedShell + 1) % shells.size.coerceAtLeast(1) }
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            drawCircle(element.color.copy(.22f), 24f * modelScale, center)
            drawCircle(element.color, 11f * modelScale, center)
            shells.forEachIndexed { shellIndex, count ->
                val radius = (27f + shellIndex * 14f) * modelScale
                drawCircle(
                    if (shellIndex == selectedShell) element.color else Border,
                    radius,
                    center,
                    style = Stroke(if (shellIndex == selectedShell) 4f else 2f)
                )
                repeat(count.coerceAtMost(18)) { electron ->
                    val direction = if (shellIndex % 2 == 0) 1 else -1
                    val angle = 2 * PI * electron / count.coerceAtLeast(1) - PI / 2 +
                        displayedOrbit * PI / 180 * direction
                    drawCircle(
                        element.color,
                        (if (shellIndex == selectedShell) 5.5f else 4.5f) * modelScale.coerceAtMost(1.2f),
                        Offset(center.x + radius * cos(angle).toFloat(), center.y + radius * sin(angle).toFloat())
                    )
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModelControl("−", "Zoom out") { modelScale = (modelScale - .1f).coerceAtLeast(.65f) }
            ModelControl("+", "Zoom in") { modelScale = (modelScale + .1f).coerceAtMost(1.45f) }
            ModelControl("↶", "Rotate left") { manualRotation = (manualRotation - 15f) % 360f }
            ModelControl("↷", "Rotate right") { manualRotation = (manualRotation + 15f) % 360f }
            ModelControl(if (paused) "▶" else "Ⅱ", if (paused) "Resume" else "Pause") { paused = !paused }
            ModelControl("⌂", "Reset") {
                modelScale = 1f
                manualRotation = 0f
                selectedShell = 0
                paused = false
            }
        }
        Text(
            "Shell ${selectedShell + 1}: ${shells.getOrElse(selectedShell) { 0 }} e⁻ · ${(modelScale * 100).toInt()}% · tap shell / pinch / twist",
            color = Muted, fontSize = 9.sp, modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun ModelControl(symbol: String, description: String, click: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            symbol,
            color = Cyan,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.size(36.dp).background(Panel2, RoundedCornerShape(9.dp))
                .border(1.dp, Cyan.copy(.45f), RoundedCornerShape(9.dp))
                .clickable(onClick = click).padding(top = 6.dp)
        )
        Text(description, color = Muted, fontSize = 7.sp, maxLines = 1)
    }
}

@Composable
private fun OrbitalBoxes(number: Int) {
    val configuration = elementFacts(number).electronConfiguration.substringBefore(" (")
    val core = Regex("""\[[A-Za-z]+]""").find(configuration)?.value
    val subshells = Regex("""(\d)([spdf])(\d+)""").findAll(configuration).map { match ->
        Triple(match.groupValues[1].toInt(), match.groupValues[2].single(), match.groupValues[3].toInt())
    }.toList()
    val context = LocalContext.current
    val reducedMotion = remember {
        context.getSharedPreferences("periodic_table_settings", 0).getBoolean("reduced_motion", false)
    }
    val transition = rememberInfiniteTransition(label = "orbital occupancy")
    val electronAlpha by transition.animateFloat(
        initialValue = if (reducedMotion) 1f else .55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "electron configuration pulse"
    )
    Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        core?.let {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.height(31.dp).border(1.dp, Purple).padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
                    Text(it, color = Purple, fontSize = 9.sp)
                }
                Text("filled core", color = Muted, fontSize = 7.sp)
            }
        }
        subshells.forEach { (shell, kind, occupation) ->
            val orbitalCount = when (kind) { 's' -> 1; 'p' -> 3; 'd' -> 5; else -> 7 }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(orbitalCount) { orbitalIndex ->
                        val arrows = when {
                            occupation > orbitalCount + orbitalIndex -> "↑↓"
                            occupation > orbitalIndex -> "↑"
                            else -> ""
                        }
                        Box(Modifier.size(26.dp, 31.dp).border(1.dp, if (arrows.isNotEmpty()) Cyan else Border), contentAlignment = Alignment.Center) {
                            Text(arrows, color = Cyan, fontSize = 8.sp, modifier = Modifier.alpha(if (arrows.isNotEmpty()) electronAlpha else 1f))
                        }
                    }
                }
                Text("$shell$kind${occupation.toSuperscript()}", color = Muted, fontSize = 8.sp)
            }
        }
    }
}

private fun Int.toSuperscript(): String = toString().map {
    when (it) {
        '0' -> '⁰'; '1' -> '¹'; '2' -> '²'; '3' -> '³'; '4' -> '⁴'
        '5' -> '⁵'; '6' -> '⁶'; '7' -> '⁷'; '8' -> '⁸'; else -> '⁹'
    }
}.joinToString("")

@Composable
private fun ElementStory(element: Element) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.getDefault()
        }
        tts = engine
        onDispose { engine.shutdown() }
    }
    Column(Modifier.fillMaxWidth().padding(18.dp)) {
        FeatureHeading("PRONUNCIATION")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(element.name, color = White, fontSize = 18.sp, modifier = Modifier.weight(1f))
            StudioPill("▶ Listen", true) { tts?.speak(element.name, TextToSpeech.QUEUE_FLUSH, null, element.symbol) }
        }
        FeatureHeading("APPEARANCE & MATERIAL")
        InfoBlock(appearanceFor(element), element.color)
        FeatureHeading("DISCOVERY TIMELINE")
        InfoBlock("${discoveryFor(element)}\n\nScientist profile: ${scientistFor(element)}", Purple)
        FeatureHeading("NATURAL OCCURRENCE")
        InfoBlock(sourceFor(element), Green)
        FeatureHeading("ELEMENT RELATIONSHIPS")
        InfoBlock("Its closest chemical relatives are other ${element.category.lowercase()} elements. Similar outer-electron behavior produces related bonding and reactivity.", Cyan)
    }
}

@Composable
private fun ElementUsesAndSafety(element: Element) {
    Column(Modifier.fillMaxWidth().padding(18.dp)) {
        FeatureHeading("PHYSICAL STATE · ROOM TEMPERATURE")
        InfoBlock(stateFor(element), element.color)
        FeatureHeading("INDUSTRIAL & EVERYDAY USES")
        InfoBlock(usesFor(element), Cyan)
        FeatureHeading("BIOLOGICAL IMPORTANCE")
        InfoBlock(biologyFor(element), Green)
        FeatureHeading("SAFETY & HAZARDS")
        InfoBlock(hazardFor(element), if (element.category in listOf("Actinide", "Halogen", "Alkali metal")) Orange else Purple)
        FeatureHeading("COMMON COMPOUNDS")
        val compounds = validatedCompoundsFor(element)
        Text(
            if (compounds.isEmpty()) "No curated offline examples available." else compounds.joinToString("   "),
            color = if (compounds.isEmpty()) Muted else element.color,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ElementCompoundBuilder(element: Element) {
    var partner by remember(element.number) { mutableStateOf(compoundPartnersFor(element).first()) }
    var count by remember(element.number) { mutableIntStateOf(1) }
    val formula = remember(element.number, partner, count) { builtCompoundFormula(element, partner, count) }
    val prediction = remember(element.number, partner, count) { compoundPrediction(element, partner, formula) }
    Column(Modifier.fillMaxWidth().padding(18.dp)) {
        FeatureHeading("COMPOUND BUILDER")
        GlowCard(Modifier.fillMaxWidth(), element.color) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ElementBuilderTile(element.symbol, element.name, element.color, Modifier.weight(1f))
                Text("+", color = Muted, fontSize = 22.sp, modifier = Modifier.padding(horizontal = 8.dp))
                ElementBuilderTile(partner.symbol, partner.name, partner.color, Modifier.weight(1f))
            }
            Text(formula, color = element.color, fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 14.dp))
            Text(prediction, color = White, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 8.dp))
        }
        FeatureHeading("CHOOSE PARTNER")
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            compoundPartnersFor(element).forEach { option ->
                StudioPill("${option.symbol} ${option.name}", option.symbol == partner.symbol) {
                    partner = option
                }
            }
        }
        FeatureHeading("STOICHIOMETRY")
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StudioPill("-", false) { count = (count - 1).coerceAtLeast(1) }
            Text("$count unit${if (count == 1) "" else "s"}", color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            StudioPill("+", false) { count = (count + 1).coerceAtMost(6) }
        }
        FeatureHeading("CURATED EXAMPLES")
        val curated = validatedCompoundsFor(element)
        if (curated.isEmpty()) {
            InfoBlock("No curated offline examples yet. Use the builder to explore likely formula patterns and compare them with trusted references.", Muted)
        } else {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                curated.forEach { compound ->
                    Text(
                        compound,
                        color = element.color,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.background(element.color.copy(alpha = .09f), RoundedCornerShape(9.dp))
                            .border(1.dp, element.color.copy(alpha = .32f), RoundedCornerShape(9.dp))
                            .padding(horizontal = 11.dp, vertical = 9.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ElementBuilderTile(symbol: String, name: String, color: Color, modifier: Modifier) {
    Column(
        modifier.background(color.copy(alpha = .10f), RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = .35f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(symbol, color = color, fontSize = 28.sp, fontWeight = FontWeight.Black)
        Text(name, color = White, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private data class CompoundPartner(val symbol: String, val name: String, val color: Color, val charge: Int)

private fun compoundPartnersFor(element: Element): List<CompoundPartner> = when (element.category) {
    "Alkali metal", "Alkaline earth", "Transition metal", "Post-transition", "Lanthanide", "Actinide" -> listOf(
        CompoundPartner("O", "Oxygen", Cyan, -2),
        CompoundPartner("Cl", "Chlorine", Green, -1),
        CompoundPartner("S", "Sulfur", Orange, -2),
        CompoundPartner("OH", "Hydroxide", Purple, -1)
    )
    "Halogen" -> listOf(
        CompoundPartner("Na", "Sodium", Color(0xFF4D8BFF), 1),
        CompoundPartner("H", "Hydrogen", Cyan, 1),
        CompoundPartner("Ag", "Silver", Color(0xFFB8C7D9), 1),
        CompoundPartner("Ca", "Calcium", Orange, 2)
    )
    else -> listOf(
        CompoundPartner("H", "Hydrogen", Cyan, 1),
        CompoundPartner("O", "Oxygen", Cyan, -2),
        CompoundPartner("Na", "Sodium", Color(0xFF4D8BFF), 1),
        CompoundPartner("Cl", "Chlorine", Green, -1)
    )
}

private fun builtCompoundFormula(element: Element, partner: CompoundPartner, count: Int): String {
    val base = validatedCompoundsFor(element).firstOrNull { partner.symbol in it } ?: "${element.symbol}${partner.symbol}"
    return if (count == 1) base else "($base)$count"
}

private fun compoundPrediction(element: Element, partner: CompoundPartner, formula: String): String {
    val bond = when {
        element.category.contains("metal", true) && partner.charge < 0 -> "mostly ionic"
        element.category == "Halogen" && partner.charge > 0 -> "mostly ionic or polar"
        else -> "mostly covalent or polar covalent"
    }
    return "$formula is a teaching prediction from common valence patterns. Expect $bond bonding; verify real stability, hydration and oxidation state with experimental data."
}

@Composable
private fun ElementNotesAndAssistant(element: Element) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("element_notes", 0) }
    var note by remember(element.number) { mutableStateOf(prefs.getString("note_${element.number}", "") ?: "") }
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("Ask about properties, uses, safety, family or electron structure.") }
    var collections by remember(element.number) {
        mutableStateOf(prefs.getStringSet("collections_${element.number}", emptySet()).orEmpty().toSet())
    }
    Column(Modifier.fillMaxWidth().padding(18.dp)) {
        FeatureHeading("PERSONAL NOTES & COLLECTIONS")
        ElementCollectionChips(element, collections) { name ->
            collections = if (name in collections) collections - name else collections + name
            prefs.edit().putStringSet("collections_${element.number}", collections).apply()
        }
        OutlinedTextField(
            value = note,
            onValueChange = {
                note = it
                prefs.edit().putString("note_${element.number}", it).apply()
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Notes for ${element.symbol}") },
            minLines = 3
        )
        FeatureHeading("ELEMENT AI GUIDE")
        OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Why is ${element.name} important?") },
            singleLine = true
        )
        Button(
            onClick = { answer = localElementAnswer(element, question) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(Cyan)
        ) { Text("Ask", color = Night, fontWeight = FontWeight.Bold) }
        InfoBlock(answer, Cyan)
    }
}

@Composable
private fun ElementCollectionChips(element: Element, selected: Set<String>, toggle: (String) -> Unit) {
    val collections = listOf("Favorites", "Exam revision", "Lab useful", "Hazards", "Materials")
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        collections.forEach { name ->
            val active = name in selected
            Text(
                if (active) "$name saved" else name,
                color = if (active) Night else element.color,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.background(if (active) element.color else element.color.copy(alpha = .08f), CircleShape)
                    .border(1.dp, element.color.copy(alpha = .38f), CircleShape)
                    .clickable { toggle(name) }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            )
        }
    }
    val summary = if (selected.isEmpty()) "Not in any named collection yet." else "In collections: ${selected.joinToString(", ")}."
    Text(summary, color = Muted, fontSize = 9.sp, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun FeatureHeading(text: String) {
    Text(text, color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))
}

@Composable
private fun InfoBlock(text: String, color: Color) {
    Text(
        text, color = White, fontSize = 12.sp,
        modifier = Modifier.fillMaxWidth().background(color.copy(.08f), RoundedCornerShape(10.dp))
            .border(1.dp, color.copy(.32f), RoundedCornerShape(10.dp)).padding(12.dp)
    )
}

@Composable
private fun MiniFeatureCard(title: String, body: String, color: Color, click: (() -> Unit)? = null) {
    Card(
        Modifier.fillMaxWidth().padding(bottom = 8.dp).then(if (click != null) Modifier.clickable(onClick = click) else Modifier),
        colors = CardDefaults.cardColors(Panel),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(.35f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(13.dp)) {
            Text(title, color = color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(body, color = Muted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun StudioPill(label: String, active: Boolean, modifier: Modifier = Modifier, click: () -> Unit) {
    Text(
        label,
        color = if (active) Night else White,
        fontSize = 10.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.background(if (active) Cyan else Panel2, RoundedCornerShape(8.dp))
            .border(1.dp, if (active) Cyan else Border, RoundedCornerShape(8.dp))
            .clickable(onClick = click).padding(horizontal = 10.dp, vertical = 8.dp)
    )
}

private fun periodOf(number: Int): Int = periodicPeriod(number)

private fun groupOf(number: Int): Int = periodicGroup(number) ?: 0

private fun studioTrend(element: Element, property: String): Float {
    normalizedMeasuredProperty(element.number, property)?.let { return it.coerceIn(.05f, .98f) }
    val period = periodOf(element.number)
    val group = (periodicGroup(element.number) ?: 3) - 1
    val upperRight = ((7 - period) / 6f + group / 17f) / 2f
    return when (property) {
        "Metallic character" -> 1f - upperRight
        "Reactivity" -> qualitativeReactivity(element.number)
        else -> .05f
    }.coerceIn(.05f, .98f)
}

internal fun electronShellDistribution(number: Int): List<Int> {
    val shellCounts = IntArray(7)
    val configuration = elementFacts(number).electronConfiguration.substringBefore(" (")
    val coreAtomicNumbers = mapOf("He" to 2, "Ne" to 10, "Ar" to 18, "Kr" to 36, "Xe" to 54, "Rn" to 86)
    Regex("""\[([A-Za-z]+)]""").find(configuration)?.groupValues?.get(1)?.let { core ->
        coreAtomicNumbers[core]?.let { coreNumber ->
            electronShellDistribution(coreNumber).forEachIndexed { index, electrons -> shellCounts[index] += electrons }
        }
    }
    Regex("""(\d)[spdf](\d+)""").findAll(configuration).forEach { match ->
        val shell = match.groupValues[1].toInt()
        val electrons = match.groupValues[2].toInt()
        shellCounts[shell - 1] += electrons
    }
    return shellCounts.toList().dropLastWhile { it == 0 }
}

private fun oxidationStates(element: Element): List<String> =
    elementFacts(element.number).oxidationStates
        .takeUnless { it == "—" }
        ?.split(',')
        ?.map { it.trim().replace("-", "−") }
        ?.filter { it.isNotBlank() }
        ?: listOf("Not established")

private fun appearanceFor(element: Element): String = when (element.category) {
    "Noble gas" -> "A colorless monatomic gas under ordinary conditions; characteristic colors appear in an electrical discharge."
    "Halogen" -> "A strongly colored reactive non-metal. Halogen colors and physical states change down the group."
    "Alkali metal" -> "A soft, silvery metal with a fresh metallic surface that rapidly tarnishes in air."
    "Transition metal" -> "Typically a lustrous, dense solid metal; many of its compounds and ions are vividly colored."
    "Non-metal" -> "Its elemental form may be gaseous or a brittle solid, without typical metallic lustre."
    else -> "Usually encountered as a solid with properties intermediate between its neighboring chemical families."
}

private fun discoveryFor(element: Element): String {
    val year = elementFacts(element.number).yearDiscovered
    val context = when {
        year == "Ancient" -> "Known since antiquity; no single discoverer can be assigned."
        element.number > 92 -> "First reported in $year through nuclear-reaction experiments and identified from decay products."
        else -> "Discovery or first isolation is conventionally dated to $year."
    }
    return context
}

private fun scientistFor(element: Element): String = when (element.symbol) {
    "H" -> "Henry Cavendish characterized hydrogen as a distinct gas; Antoine Lavoisier later named it."
    "O" -> "Carl Wilhelm Scheele and Joseph Priestley independently prepared oxygen; Lavoisier explained its role in combustion."
    "N" -> "Daniel Rutherford isolated nitrogen in 1772 while studying air that would not support combustion."
    "C" -> "Known since antiquity; Antoine Lavoisier established carbon as an element."
    "Na", "K" -> "Humphry Davy isolated the metal by electrolysis in 1807."
    "Ca", "Mg" -> "Humphry Davy’s electrochemical work helped isolate and characterize the alkaline-earth metals."
    "Fe", "Cu", "Ag", "Au" -> "Known since antiquity; early metallurgists developed extraction and alloying long before modern atomic theory."
    "Cl" -> "Carl Wilhelm Scheele prepared chlorine; Humphry Davy later showed that it was an element."
    "He" -> "Pierre Janssen and Norman Lockyer detected helium in the solar spectrum before it was isolated on Earth."
    "Hf" -> "Dirk Coster and George de Hevesy identified hafnium by X-ray spectroscopy in zirconium minerals in 1923."
    "Ra", "Po" -> "Marie and Pierre Curie identified these radioactive elements through painstaking mineral separation."
    "U" -> "Martin Heinrich Klaproth identified uranium in 1789; later researchers revealed its radioactivity and nuclear behavior."
    else -> if (element.number > 92) {
        "Accelerator and nuclear-chemistry teams identified it collectively; credit is associated with the laboratories that confirmed its decay chain."
    } else {
        "Its identification emerged from mineral analysis, spectroscopy and international verification by the chemists of its period."
    }
}

private fun sourceFor(element: Element): String = when (element.category) {
    "Noble gas" -> "Recovered mainly from air separation or natural-gas streams."
    "Alkali metal", "Alkaline earth" -> "Occurs in minerals and salts because the free element is too reactive to persist in nature."
    "Actinide" -> "Found in trace radioactive minerals or produced in nuclear reactors and accelerators."
    else -> "Occurs in minerals, ores, biological matter or the atmosphere depending on its reactivity and abundance."
}

private fun stateFor(element: Element): String {
    val state = elementFacts(element.number).standardState
    return if (state.startsWith("Expected", ignoreCase = true)) {
        "$state. This is a prediction because bulk samples have not been characterized."
    } else {
        "$state under the reference conditions used by the PubChem periodic-table dataset."
    }
}

private fun usesFor(element: Element): String = when (element.symbol) {
    "H" -> "Fuel cells, ammonia synthesis, petroleum processing and chemical reduction."
    "C" -> "Steel, fuels, electrodes, filters, polymers and advanced carbon materials."
    "O" -> "Medicine, steelmaking, combustion, water treatment and life-support systems."
    "Si" -> "Semiconductors, glass, concrete, ceramics and silicones."
    "Fe" -> "Structural steel, machinery, transport, tools and magnetic materials."
    "Cu" -> "Electrical wiring, electronics, plumbing, alloys and heat exchangers."
    "Au" -> "Electronics, corrosion-resistant contacts, jewelry, dentistry and investment."
    else -> "Used according to its family in materials, chemical synthesis, electronics, energy, medicine or specialized research."
}

private fun biologyFor(element: Element): String = when (element.symbol) {
    "H", "C", "N", "O", "P", "S" -> "A major building block of biomolecules and essential biochemical processes."
    "Na", "K", "Mg", "Ca", "Fe", "Zn", "Cu", "I" -> "An essential nutrient at controlled levels; deficiency and excess can both be harmful."
    else -> "It has no universal biological role established for humans; exposure depends strongly on chemical form and dose."
}

private fun hazardFor(element: Element): String = when (element.category) {
    "Alkali metal" -> "The elemental metal can react violently with water. Handle only under appropriate laboratory controls."
    "Halogen" -> "Elemental halogens are reactive and may be corrosive or toxic. Avoid inhalation and skin contact."
    "Actinide" -> "Radioactive and potentially chemically toxic. Specialized containment and monitoring are required."
    "Noble gas" -> "Generally chemically inert, but concentrated gas can displace oxygen in enclosed spaces."
    else -> "Hazard depends on oxidation state, compound, route and dose. Consult the safety data for the actual material."
}

internal fun validatedCompoundsFor(element: Element): List<String> = when (element.symbol) {
    "H" -> listOf("H₂O", "HCl", "NH₃", "CH₄")
    "C" -> listOf("CO₂", "CH₄", "CaCO₃", "C₂H₅OH")
    "O" -> listOf("H₂O", "CO₂", "H₂O₂", "SiO₂")
    "Fe" -> listOf("Fe₂O₃", "FeO", "FeS", "FeCl₃")
    "Cu" -> listOf("CuO", "Cu₂O", "CuSO₄", "CuCl₂")
    "Na" -> listOf("NaCl", "NaOH", "Na₂CO₃", "NaHCO₃")
    "Mg" -> listOf("MgO", "MgCl₂", "MgSO₄", "Mg(OH)₂")
    "Al" -> listOf("Al₂O₃", "AlCl₃", "Al(OH)₃", "Al₂(SO₄)₃")
    "Si" -> listOf("SiO₂", "SiC", "SiCl₄", "Na₂SiO₃")
    "P" -> listOf("H₃PO₄", "P₄O₁₀", "PCl₃", "PCl₅")
    "S" -> listOf("SO₂", "SO₃", "H₂SO₄", "H₂S")
    "Cl" -> listOf("NaCl", "HCl", "KClO₃", "CaCl₂")
    "Ca" -> listOf("CaCO₃", "CaO", "CaCl₂", "Ca(OH)₂")
    "Hf" -> listOf("HfO₂", "HfCl₄", "HfF₄", "HfC")
    "Ag" -> listOf("AgNO₃", "AgCl", "Ag₂O", "Ag₂S")
    "I" -> listOf("KI", "HI", "AgI", "KIO₃")
    "Au" -> listOf("AuCl₃", "HAuCl₄", "AuCN", "Au₂S₃")
    else -> emptyList()
}

private fun localElementAnswer(element: Element, question: String): String {
    val q = question.lowercase()
    return when {
        q.isBlank() -> "Type a question about ${element.name}."
        "use" in q || "important" in q -> usesFor(element)
        "safe" in q || "hazard" in q || "toxic" in q -> hazardFor(element)
        "electron" in q || "configuration" in q || "shell" in q ->
            "${element.name} has ${element.number} electrons when neutral. Ground-state configuration: ${elementFacts(element.number).electronConfiguration}."
        "family" in q || "similar" in q || "group" in q -> "It is classified as ${element.category}. Related elements show similar valence behavior and recurring reaction patterns."
        "biology" in q || "body" in q || "life" in q -> biologyFor(element)
        "where" in q || "source" in q || "nature" in q -> sourceFor(element)
        else -> "${element.name} (${element.symbol}) has atomic number ${element.number}. It is a ${element.category.lowercase()}. Ask about its uses, safety, electrons, biology, source or family."
    }
}

internal fun exportPeriodicTablePng(context: Context, elements: List<Element>, share: Boolean = false) {
    val width = 1920
    val height = 1120
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    canvas.drawColor(Night.toArgb())
    paint.color = White.toArgb()
    paint.textSize = 54f
    paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
    canvas.drawText("Periodic Table of the Elements", 56f, 72f, paint)
    paint.textSize = 22f
    paint.typeface = android.graphics.Typeface.DEFAULT
    paint.color = Muted.toArgb()
    canvas.drawText("AI Chemistry Explorer · 118 elements", 58f, 108f, paint)
    val cellW = 96f
    val cellH = 92f
    val startX = 70f
    val startY = 145f
    fun drawCell(element: Element, column: Int, row: Int) {
        val x = startX + column * cellW
        val y = startY + row * cellH
        paint.style = Paint.Style.FILL
        paint.color = element.color.copy(alpha = .16f).toArgb()
        canvas.drawRoundRect(x + 3, y + 3, x + cellW - 4, y + cellH - 4, 10f, 10f, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = element.color.toArgb()
        canvas.drawRoundRect(x + 3, y + 3, x + cellW - 4, y + cellH - 4, 10f, 10f, paint)
        paint.style = Paint.Style.FILL
        paint.textSize = 15f
        paint.color = Muted.toArgb()
        canvas.drawText(element.number.toString(), x + 10, y + 21, paint)
        paint.textSize = 30f
        paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
        paint.color = element.color.toArgb()
        canvas.drawText(element.symbol, x + 11, y + 55, paint)
        paint.typeface = android.graphics.Typeface.DEFAULT
        paint.textSize = 11f
        paint.color = White.toArgb()
        canvas.drawText(element.name.take(12), x + 10, y + 76, paint)
    }
    periodicRows.forEachIndexed { row, numbers ->
        numbers.forEachIndexed { column, number -> number?.let { drawCell(elements[it - 1], column, row) } }
    }
    lanthanideRow.forEachIndexed { column, number -> drawCell(elements[number - 1], column + 3, 8) }
    actinideRow.forEachIndexed { column, number -> drawCell(elements[number - 1], column + 3, 9) }
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "periodic_table_${System.currentTimeMillis()}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AI Chemistry Explorer")
    }
    runCatching {
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.let { uri ->
            context.contentResolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            uri
        } ?: error("Unable to create image")
    }.onSuccess {
        Toast.makeText(context, "Periodic table PNG saved to Pictures", Toast.LENGTH_LONG).show()
        if (share) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, it)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share periodic table").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }.onFailure {
        Toast.makeText(context, "Could not export PNG", Toast.LENGTH_LONG).show()
    }
    bitmap.recycle()
}
