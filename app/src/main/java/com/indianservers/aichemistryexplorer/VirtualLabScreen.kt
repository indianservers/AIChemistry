package com.indianservers.aichemistryexplorer

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text as MaterialText
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sqrt

private data class LabTrial(val index: Int, val value: Double, val uncertainty: Double)

private data class LabNotebookNotes(
    val observations: String,
    val calculations: String,
    val result: String,
    val conclusion: String
)

@Composable
internal fun VirtualLabScreen() {
    var selected by remember { mutableStateOf<VirtualExperiment?>(null) }
    BackHandler(selected != null) { selected = null }
    selected?.let { ExperimentWorkbench(it) { selected = null } }
        ?: ExperimentCatalog { selected = it }
}

@Composable
private fun ExperimentCatalog(open: (VirtualExperiment) -> Unit) {
    val categories = remember { listOf("All") + virtualExperiments.map { it.category }.distinct() }
    var category by remember { mutableStateOf("All") }
    var query by remember { mutableStateOf("") }
    val filtered = virtualExperiments.filter {
        (category == "All" || it.category == category) &&
            (query.isBlank() || "${it.title} ${it.objective} ${it.technique}".contains(query, true))
    }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Virtual Chemistry Lab", "${virtualExperiments.size} guided, data-driven experiments")
        GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp), Orange) {
            Text("SAFETY DISCLAIMER", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(
                "Virtual Lab is for guided learning only and is not a substitute for supervised real laboratory safety training.",
                color = White,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
        OutlinedTextField(
            query, { query = it }, label = { Text("Search experiments or techniques") },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
        )
        Row(
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach {
                Text(
                    it, color = if (it == category) Night else White, fontSize = 11.sp,
                    modifier = Modifier.background(
                        if (it == category) Cyan else LocalReadingPalette.current.surfaceAlt, CircleShape
                    ).clickable { category = it }.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
        Text("${filtered.size} experiments · Tap to enter the workbench", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 20.dp))
        LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
            items(filtered, key = { it.id }) { experiment ->
                GlowCard(
                    Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable { open(experiment) },
                    techniqueColor(experiment.technique)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            techniqueSymbol(experiment.technique), color = techniqueColor(experiment.technique),
                            fontSize = 22.sp, textAlign = TextAlign.Center,
                            modifier = Modifier.width(48.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(experiment.title, color = White, fontWeight = FontWeight.Bold)
                            Text(experiment.objective, color = Muted, fontSize = 11.sp)
                            Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                Tag(experiment.level.uppercase(), techniqueColor(experiment.technique))
                                if (experiment.category == "Laboratory foundations") {
                                    Tag("FOUNDATION COMPETENCY", Green)
                                } else if (experiment.category == "General chemistry laboratories") {
                                    Tag("ACCURACY PROTOCOL", Green)
                                }
                                Text(experiment.measuredQuantity, color = White.copy(alpha = .72f), fontSize = 9.sp)
                            }
                        }
                        Text("›", color = Cyan, fontSize = 28.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExperimentWorkbench(experiment: VirtualExperiment, back: () -> Unit) {
    var page by remember(experiment.id) { mutableIntStateOf(0) }
    var actionIndex by remember(experiment.id) { mutableIntStateOf(0) }
    var safetyPassed by remember(experiment.id) { mutableStateOf(false) }
    var trials by remember(experiment.id) { mutableStateOf<List<LabTrial>>(emptyList()) }
    var observations by remember(experiment.id) { mutableStateOf("") }
    var calculations by remember(experiment.id) { mutableStateOf("") }
    var resultNote by remember(experiment.id) { mutableStateOf("") }
    var conclusion by remember(experiment.id) { mutableStateOf("") }
    var challengeMode by remember(experiment.id) { mutableStateOf(false) }
    var challengeGuess by remember(experiment.id) { mutableStateOf("") }
    val pages = listOf("Workbench", "Guide", "Data")
    val actions = remember(experiment.id) { workbenchActions(experiment) }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle(experiment.title, experiment.category, back)
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            pages.forEachIndexed { index, label ->
                Text(
                    label, color = if (page == index) Night else Muted, fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                        .background(if (page == index) Cyan else LocalReadingPalette.current.surface, RoundedCornerShape(9.dp))
                        .clickable { page = index }.padding(vertical = 10.dp)
                )
            }
        }
        when (page) {
            0 -> Workbench(experiment, actions, actionIndex, safetyPassed,
                passSafety = { safetyPassed = true },
                act = { if (safetyPassed && actionIndex < actions.size) actionIndex++ },
                reset = { safetyPassed = false; actionIndex = 0; trials = emptyList(); challengeGuess = "" },
                addTrial = {
                    trials = trials + makeLabTrial(experiment, actionIndex, actions.size, trials.size + 1)
                },
                analyze = { page = 2 })
            1 -> Method(experiment)
            else -> DataAnalysis(
                experiment = experiment,
                progress = actionIndex,
                total = actions.size,
                trials = trials,
                notes = LabNotebookNotes(observations, calculations, resultNote, conclusion),
                updateObservations = { observations = it },
                updateCalculations = { calculations = it },
                updateResult = { resultNote = it },
                updateConclusion = { conclusion = it },
                challengeMode = challengeMode,
                challengeGuess = challengeGuess,
                setChallengeMode = { challengeMode = it },
                setChallengeGuess = { challengeGuess = it },
                returnToBench = { page = 0 }
            )
        }
    }
}

@Composable
private fun Workbench(
    experiment: VirtualExperiment, actions: List<String>, actionIndex: Int, safe: Boolean,
    passSafety: () -> Unit, act: () -> Unit, reset: () -> Unit, addTrial: () -> Unit, analyze: () -> Unit
) {
    val complete = actionIndex >= actions.size
    LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
        item {
            GlowCard(Modifier.fillMaxWidth(), techniqueColor(experiment.technique)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Tag(
                        when (experiment.category) {
                            "Laboratory foundations" -> "FOUNDATION · ${experiment.technique.name.replace('_', ' ')}"
                            "General chemistry laboratories" -> "GENERAL CHEM · ${experiment.technique.name.replace('_', ' ')}"
                            else -> experiment.technique.name.replace('_', ' ')
                        },
                        techniqueColor(experiment.technique)
                    )
                    Text("$actionIndex/${actions.size} operations", color = Muted, fontSize = 10.sp)
                }
                Text(experiment.objective, color = White, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                Text(experiment.equation, color = Cyan, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
                Text(
                    "Simulation notice: not a substitute for real laboratory safety training.",
                    color = Orange,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            ApparatusBench(experiment.technique, actionIndex.toFloat() / actions.size.coerceAtLeast(1))
            RealisticWorkbenchDetails(experiment)
            Spacer(Modifier.height(12.dp))
            GlowCard(Modifier.fillMaxWidth(), if (safe) Green else Orange) {
                Text("SAFETY GATE", color = if (safe) Green else Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(experiment.safety, color = White, fontSize = 11.sp)
                if (safe) Text("✓ PPE worn · apparatus inspected", color = Green, fontSize = 11.sp, modifier = Modifier.padding(top = 7.dp))
                else Button(
                    passSafety, colors = ButtonDefaults.buttonColors(Orange),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) { Text("Wear PPE and inspect apparatus", color = Night, fontWeight = FontWeight.Bold) }
            }
            Text("ACTIVE OPERATION", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp))
            Text(
                when { !safe -> "Safety clearance is required."; complete -> "Run complete. Analyze the notebook."; else -> actions[actionIndex] },
                color = White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp)
            )
            LinearProgressIndicator(
                progress = { actionIndex.toFloat() / actions.size.coerceAtLeast(1) },
                modifier = Modifier.fillMaxWidth().height(7.dp),
                color = techniqueColor(experiment.technique), trackColor = Border
            )
            Button(
                onClick = if (complete) analyze else act, enabled = safe,
                colors = ButtonDefaults.buttonColors(if (complete) Green else Cyan),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) { Text(if (complete) "Analyze recorded data" else actionLabel(experiment.technique), color = Night, fontWeight = FontWeight.Bold) }
            if (safe) Button(
                onClick = addTrial,
                colors = ButtonDefaults.buttonColors(LocalReadingPalette.current.surfaceAlt),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) { Text("Record repeat trial", color = White, fontWeight = FontWeight.Bold) }
            TextButton(reset, modifier = Modifier.fillMaxWidth()) { Text("Reset apparatus and repeat", color = Muted) }
            if (actionIndex > 0) Notebook(experiment, actionIndex)
        }
    }
}

@Composable
private fun Method(experiment: VirtualExperiment) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
        item {
            Info("THEORY", experiment.theory, Purple)
            Info("WHAT TO DO", "${experiment.objective}\n\nChemical basis: ${experiment.equation}", Cyan)
            Info("APPARATUS", experiment.apparatus, Cyan)
            Info("REAGENTS", experiment.reagents, Purple)
            Info("SAFETY & WASTE", experiment.safety, Orange)
            Text("HOW TO DO IT", color = Green, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 9.dp))
        }
        items(experiment.procedure.withIndex().toList()) {
            Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.Top) {
                Text("${it.index + 1}", color = Night, fontWeight = FontWeight.Bold, modifier = Modifier.background(Cyan, CircleShape).padding(horizontal = 9.dp, vertical = 5.dp))
                Spacer(Modifier.width(10.dp))
                Text(it.value, color = White, fontSize = 12.sp, modifier = Modifier.weight(1f))
            }
        }
        item {
            Info("EXPECTED RESULT", experiment.expectedResult, Green)
            Info("CALCULATION", experiment.analysis, Purple)
            if (experiment.category == "Laboratory foundations") {
                Info(
                    "COMPETENCY EVIDENCE",
                    "Passing requires the stated target, acceptable controls, a traceable notebook and correct response to every critical safety checkpoint. Completion alone is not competency.",
                    Orange
                )
            } else if (experiment.category == "General chemistry laboratories") {
                Info(
                    "ACCURACY STANDARD",
                    "Accept the result only after method-specific calibration, blank/reference controls, independent replicates, correct units and uncertainty evaluation. A plausible-looking value is not sufficient.",
                    Orange
                )
            }
            Info("QUALITY CONTROL", qualityControl(experiment.technique), Green)
        }
    }
}

@Composable
private fun DataAnalysis(
    experiment: VirtualExperiment,
    progress: Int,
    total: Int,
    trials: List<LabTrial>,
    notes: LabNotebookNotes,
    updateObservations: (String) -> Unit,
    updateCalculations: (String) -> Unit,
    updateResult: (String) -> Unit,
    updateConclusion: (String) -> Unit,
    challengeMode: Boolean,
    challengeGuess: String,
    setChallengeMode: (Boolean) -> Unit,
    setChallengeGuess: (String) -> Unit,
    returnToBench: () -> Unit
) {
    val complete = progress >= total
    val result = simulatedResult(experiment, progress, total)
    val error = if (experiment.referenceValue == 0.0) 0.0 else abs((result - experiment.referenceValue) / experiment.referenceValue) * 100
    LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
        item {
            Notebook(experiment, progress)
            Spacer(Modifier.height(12.dp))
            LiveMeasurementGraph(experiment, progress, total, trials)
            Spacer(Modifier.height(12.dp))
            RepeatTrialPanel(experiment, trials, result)
            Spacer(Modifier.height(12.dp))
            GlowCard(Modifier.fillMaxWidth(), if (complete) Green else Orange) {
                Text(if (complete) "ANALYSIS COMPLETE" else "INCOMPLETE RUN", color = if (complete) Green else Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(experiment.measuredQuantity, color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
                Text("${labNumber(result)} ${experiment.unit}", color = White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                Text("Accepted/typical: ${labNumber(experiment.referenceValue)} ${experiment.unit}", color = Cyan, fontSize = 11.sp)
                if (complete) Text("Difference: ${labNumber(error)}%", color = if (error <= 3) Green else Orange, fontSize = 11.sp)
            }
            Spacer(Modifier.height(12.dp))
            Info("CALCULATION & INTERPRETATION", experiment.analysis, Purple)
            EditableLabNotebook(
                experiment = experiment,
                progress = progress,
                trials = trials,
                notes = notes,
                updateObservations = updateObservations,
                updateCalculations = updateCalculations,
                updateResult = updateResult,
                updateConclusion = updateConclusion
            )
            Spacer(Modifier.height(12.dp))
            UnknownSampleChallenge(
                experiment = experiment,
                enabled = challengeMode,
                guess = challengeGuess,
                setEnabled = setChallengeMode,
                setGuess = setChallengeGuess
            )
            Spacer(Modifier.height(12.dp))
            Info("MODEL LIMIT", "Readings include small deterministic instrument scatter. This teaches technique and analysis but does not replace supervised hands-on laboratory training.", Orange)
            if (!complete) Button(returnToBench, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Cyan)) {
                Text("Return and finish experiment", color = Night, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun Info(label: String, value: String, color: Color) {
    GlowCard(Modifier.fillMaxWidth().padding(bottom = 10.dp), color) {
        Text(label, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(value, color = White, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
private fun RealisticWorkbenchDetails(experiment: VirtualExperiment) {
    val details = when (experiment.technique) {
        LabTechnique.TITRATION -> listOf("Burette clamp", "White tile endpoint zone", "Conditioned pipette", "Swirl path")
        LabTechnique.CALORIMETRY -> listOf("Nested cup calorimeter", "Probe through lid", "Magnetic stir bar", "Heat-loss timer")
        LabTechnique.ELECTROCHEMISTRY -> listOf("Salt bridge", "Metal electrodes", "High-impedance voltmeter", "Polarity leads")
        LabTechnique.SPECTROSCOPY -> listOf("Blank cuvette", "Matched standards", "Wavelength selector", "Absorbance display")
        LabTechnique.SEPARATION -> listOf("Fraction receiver rack", "Condenser/column", "Flow control", "Purity check plate")
        else -> experiment.apparatus.split("?", "·").map { it.trim() }.filter { it.isNotBlank() }.take(4)
    }
    GlowCard(Modifier.fillMaxWidth().padding(top = 10.dp), techniqueColor(experiment.technique)) {
        Text("REALISTIC APPARATUS WORKBENCH", color = techniqueColor(experiment.technique), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            details.forEachIndexed { index, detail ->
                Column(
                    Modifier.width(124.dp)
                        .background(LocalReadingPalette.current.surfaceAlt, RoundedCornerShape(10.dp))
                        .border(1.dp, Border.copy(alpha = .75f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${index + 1}", color = Night, fontWeight = FontWeight.Bold, modifier = Modifier.background(Cyan, CircleShape).padding(horizontal = 8.dp, vertical = 3.dp))
                    Text(detail, color = White, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun LiveMeasurementGraph(experiment: VirtualExperiment, progress: Int, total: Int, trials: List<LabTrial>) {
    val rows = measurements(experiment, progress.coerceAtLeast(1)).take(8)
    GlowCard(Modifier.fillMaxWidth(), techniqueColor(experiment.technique)) {
        Text("LIVE MEASUREMENT GRAPH", color = techniqueColor(experiment.technique), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(experiment.measuredQuantity, color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp))
        Canvas(Modifier.fillMaxWidth().height(170.dp).padding(top = 8.dp)) {
            val left = 36f
            val top = 16f
            val width = size.width - 52f
            val height = size.height - 42f
            drawLine(Border, Offset(left, top), Offset(left, top + height), 2f)
            drawLine(Border, Offset(left, top + height), Offset(left + width, top + height), 2f)
            repeat(4) { tick ->
                val y = top + height * tick / 3f
                drawLine(Border.copy(alpha = .35f), Offset(left, y), Offset(left + width, y), 1f)
            }
            val count = rows.size.coerceAtLeast(2)
            val path = Path()
            rows.forEachIndexed { index, _ ->
                val x = left + width * index / (count - 1)
                val y = top + height - height * ((index + 1f) / count) * (progress.toFloat() / total.coerceAtLeast(1)).coerceIn(.15f, 1f)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                drawCircle(Cyan, 5f, Offset(x, y))
            }
            drawPath(path, techniqueColor(experiment.technique), style = Stroke(4f, cap = StrokeCap.Round))
            trials.takeLast(5).forEachIndexed { index, trial ->
                val x = left + width * (index + .5f) / 5f
                val spread = (trial.uncertainty / abs(experiment.referenceValue).coerceAtLeast(1.0)).toFloat().coerceIn(.03f, .22f)
                val y = top + height * (.65f - index * .08f).coerceIn(.18f, .78f)
                drawLine(Orange, Offset(x, y - height * spread), Offset(x, y + height * spread), 3f, StrokeCap.Round)
                drawCircle(Orange, 6f, Offset(x, y))
            }
        }
    }
}

@Composable
private fun RepeatTrialPanel(experiment: VirtualExperiment, trials: List<LabTrial>, fallback: Double) {
    val values = if (trials.isEmpty()) listOf(fallback) else trials.map { it.value }
    val mean = values.average()
    val stdev = if (values.size < 2) values.firstOrNull()?.let { abs(it) * .008 } ?: 0.0
        else sqrt(values.sumOf { (it - mean) * (it - mean) } / (values.size - 1))
    val relative = if (mean == 0.0) 0.0 else abs(stdev / mean) * 100
    GlowCard(Modifier.fillMaxWidth(), Orange) {
        Text("ERROR, UNCERTAINTY & REPEAT TRIALS", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text("Trials recorded: ${trials.size}", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
        Text("Mean ${labNumber(mean)} ${experiment.unit} ± ${labNumber(stdev)} ${experiment.unit}", color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text("Relative uncertainty: ${labNumber(relative)}%", color = if (relative <= 2.5) Green else Orange, fontSize = 11.sp)
        trials.takeLast(4).forEach {
            Row(Modifier.fillMaxWidth().padding(top = 5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Trial ${it.index}", color = Muted, fontSize = 10.sp)
                Text("${labNumber(it.value)} ± ${labNumber(it.uncertainty)} ${experiment.unit}", color = White, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun EditableLabNotebook(
    experiment: VirtualExperiment,
    progress: Int,
    trials: List<LabTrial>,
    notes: LabNotebookNotes,
    updateObservations: (String) -> Unit,
    updateCalculations: (String) -> Unit,
    updateResult: (String) -> Unit,
    updateConclusion: (String) -> Unit
) {
    val context = LocalContext.current
    GlowCard(Modifier.fillMaxWidth(), Green) {
        Text("LAB NOTEBOOK", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        NotebookField("Observations", notes.observations.ifBlank { "Endpoint/signal changes, color, precipitate, temperature drift..." }, updateObservations)
        NotebookField("Calculations", notes.calculations.ifBlank { experiment.analysis }, updateCalculations)
        NotebookField("Result", notes.result.ifBlank { "${experiment.measuredQuantity}: ${labNumber(simulatedResult(experiment, progress, workbenchActions(experiment).size))} ${experiment.unit}" }, updateResult)
        NotebookField("Conclusion", notes.conclusion.ifBlank { "Compare the experimental value with the accepted value and explain likely error sources." }, updateConclusion)
        Button(
            onClick = { exportLabReportPdf(context, experiment, progress, trials, notes) },
            colors = ButtonDefaults.buttonColors(Green),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) { Text("Download lab report PDF", color = Night, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun NotebookField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        minLines = 2,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
    )
}

@Composable
private fun UnknownSampleChallenge(
    experiment: VirtualExperiment,
    enabled: Boolean,
    guess: String,
    setEnabled: (Boolean) -> Unit,
    setGuess: (String) -> Unit
) {
    val answer = unknownAnswerFor(experiment)
    val options = unknownOptionsFor(experiment)
    GlowCard(Modifier.fillMaxWidth(), Purple) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("UNKNOWN SAMPLE CHALLENGE", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(if (enabled) "Use the recorded pattern to identify the hidden sample." else "Turn on challenge mode to hide the expected identity.", color = Muted, fontSize = 10.sp)
            }
            Text(
                if (enabled) "ON" else "OFF",
                color = if (enabled) Night else Muted,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.background(if (enabled) Purple else LocalReadingPalette.current.surfaceAlt, CircleShape)
                    .clickable { setEnabled(!enabled); setGuess("") }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            )
        }
        if (enabled) {
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    Text(
                        option,
                        color = if (guess == option) Night else White,
                        fontSize = 10.sp,
                        modifier = Modifier.background(if (guess == option) Purple else LocalReadingPalette.current.surfaceAlt, CircleShape)
                            .clickable { setGuess(option) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
            if (guess.isNotBlank()) {
                Text(
                    if (guess == answer) "Correct: the unknown matches $answer." else "Not yet: check the trend, control and endpoint evidence.",
                    color = if (guess == answer) Green else Orange,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 9.dp)
                )
            }
        }
    }
}

@Composable
private fun Notebook(experiment: VirtualExperiment, count: Int) {
    val rows = measurements(experiment, count)
    GlowCard(Modifier.fillMaxWidth().padding(top = 12.dp), Cyan) {
        Text("LIVE LAB NOTEBOOK", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        if (rows.isEmpty()) Text("No readings recorded.", color = Muted, fontSize = 11.sp)
        rows.forEachIndexed { index, row ->
            if (index > 0) HorizontalDivider(color = Border.copy(alpha = .6f))
            Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(row.first, color = Muted, fontSize = 10.sp)
                Text(row.second, color = White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun ApparatusBench(technique: LabTechnique, progress: Float) {
    val color = techniqueColor(technique)
    Canvas(
        Modifier.fillMaxWidth().height(230.dp).background(Color(0xFF050B12), RoundedCornerShape(18.dp))
            .border(1.dp, color.copy(alpha = .45f), RoundedCornerShape(18.dp))
    ) {
        val bench = size.height * .84f
        drawLine(Color(0xFF536878), Offset(size.width * .08f, bench), Offset(size.width * .92f, bench), 7f, StrokeCap.Round)
        when (technique) {
            LabTechnique.TITRATION -> {
                drawRect(Color(0xFFB9E9F5), Offset(size.width * .46f, size.height * .1f), androidx.compose.ui.geometry.Size(size.width * .05f, size.height * .5f), style = Stroke(4f))
                drawRect(color.copy(alpha = .55f), Offset(size.width * .465f, size.height * (.14f + .34f * progress)), androidx.compose.ui.geometry.Size(size.width * .04f, size.height * (.43f - .34f * progress)))
                drawLine(Color(0xFFB9E9F5), Offset(size.width * .485f, size.height * .6f), Offset(size.width * .485f, size.height * .7f), 4f)
                flask(Offset(size.width * .49f, bench), color, progress)
            }
            LabTechnique.ELECTROCHEMISTRY -> {
                repeat(2) { i ->
                    val x = size.width * (.2f + i * .35f)
                    drawRect(Color(0xFFB9E9F5), Offset(x, size.height * .38f), androidx.compose.ui.geometry.Size(size.width * .25f, size.height * .4f), style = Stroke(4f))
                    drawRect((if (i == 0) Cyan else Purple).copy(alpha = .35f), Offset(x + 3f, size.height * .56f), androidx.compose.ui.geometry.Size(size.width * .24f, size.height * .21f))
                    drawLine(if (i == 0) Orange else Color(0xFFB87333), Offset(x + size.width * .12f, size.height * .2f), Offset(x + size.width * .12f, size.height * .68f), 9f)
                }
            }
            LabTechnique.KINETICS, LabTechnique.GAS -> {
                flask(Offset(size.width * .31f, bench), color, progress)
                drawLine(Color(0xFFB9E9F5), Offset(size.width * .37f, size.height * .46f), Offset(size.width * .68f, size.height * .32f), 6f)
                drawRect(Color(0xFFB9E9F5), Offset(size.width * .67f, size.height * .2f), androidx.compose.ui.geometry.Size(size.width * .22f, size.height * .24f), style = Stroke(5f))
                drawRect(color.copy(alpha = .45f), Offset(size.width * .69f, size.height * .22f), androidx.compose.ui.geometry.Size(size.width * .17f * progress, size.height * .2f))
            }
            LabTechnique.SEPARATION -> {
                flask(Offset(size.width * .3f, bench), color, progress)
                drawLine(Color(0xFFB9E9F5), Offset(size.width * .34f, size.height * .38f), Offset(size.width * .7f, size.height * .27f), 12f)
                flask(Offset(size.width * .76f, bench), Cyan, progress)
            }
            else -> {
                flask(Offset(size.width * .38f, bench), color, progress)
                flask(Offset(size.width * .66f, bench), Purple, progress * .8f)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.flask(base: Offset, color: Color, progress: Float) {
    val w = size.width * .18f
    val h = size.height * .42f
    val p = Path().apply {
        moveTo(base.x - w * .15f, base.y - h); lineTo(base.x + w * .15f, base.y - h)
        lineTo(base.x + w * .15f, base.y - h * .55f); lineTo(base.x + w * .48f, base.y - h * .08f)
        quadraticBezierTo(base.x + w * .5f, base.y, base.x + w * .34f, base.y)
        lineTo(base.x - w * .34f, base.y); quadraticBezierTo(base.x - w * .5f, base.y, base.x - w * .48f, base.y - h * .08f)
        lineTo(base.x - w * .15f, base.y - h * .55f); close()
    }
    drawPath(p, Color(0xFFB9E9F5), style = Stroke(4f))
    val top = base.y - h * (.18f + .2f * progress)
    drawRect(color.copy(alpha = .45f), Offset(base.x - w * .34f, top), androidx.compose.ui.geometry.Size(w * .68f, base.y - top - 3f))
}

private fun makeLabTrial(e: VirtualExperiment, progress: Int, total: Int, index: Int): LabTrial {
    val base = simulatedResult(e, progress.coerceAtLeast(total / 2), total)
    val scatter = (((e.id.hashCode() + index * 37) % 13) - 6) / 1000.0
    val value = base * (1 + scatter)
    val uncertainty = abs(value) * when (e.technique) {
        LabTechnique.TITRATION, LabTechnique.SPECTROSCOPY -> .004
        LabTechnique.CALORIMETRY, LabTechnique.SEPARATION -> .012
        LabTechnique.ELECTROCHEMISTRY -> .006
        else -> .01
    }
    return LabTrial(index, value, uncertainty)
}

private fun unknownAnswerFor(e: VirtualExperiment): String = when (e.technique) {
    LabTechnique.TITRATION -> when {
        e.title.contains("vinegar", true) -> "Ethanoic acid"
        e.title.contains("hardness", true) -> "Ca2+/Mg2+ water"
        e.title.contains("iron", true) -> "Iron(II)"
        else -> "Strong acid"
    }
    LabTechnique.CALORIMETRY -> "Exothermic neutralization"
    LabTechnique.ELECTROCHEMISTRY -> "Zinc-copper cell"
    LabTechnique.SPECTROSCOPY -> "Colored transition-metal ion"
    LabTechnique.SEPARATION -> "Mixed volatile sample"
    LabTechnique.QUALITATIVE -> "Group-confirmed ion"
    else -> e.measuredQuantity
}

private fun unknownOptionsFor(e: VirtualExperiment): List<String> {
    val answer = unknownAnswerFor(e)
    val distractors = when (e.technique) {
        LabTechnique.TITRATION -> listOf("Strong acid", "Ethanoic acid", "Iron(II)", "Ca2+/Mg2+ water")
        LabTechnique.CALORIMETRY -> listOf("Exothermic neutralization", "Endothermic dissolution", "Heat-loss blank", "Cold reservoir")
        LabTechnique.ELECTROCHEMISTRY -> listOf("Zinc-copper cell", "Concentration cell", "Inert electrode pair", "Reversed leads")
        LabTechnique.SPECTROSCOPY -> listOf("Colored transition-metal ion", "Blank contamination", "Diluted unknown", "Turbid suspension")
        LabTechnique.SEPARATION -> listOf("Mixed volatile sample", "Single pure liquid", "Nonvolatile residue", "Emulsion")
        else -> listOf(answer, "Reference blank", "Contaminated sample", "Control standard")
    }
    return (listOf(answer) + distractors).distinct().take(4)
}

private fun exportLabReportPdf(
    context: Context,
    experiment: VirtualExperiment,
    progress: Int,
    trials: List<LabTrial>,
    notes: LabNotebookNotes
) {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas
    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 20f; isFakeBoldText = true; color = android.graphics.Color.rgb(10, 26, 42) }
    val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 11f; color = android.graphics.Color.rgb(30, 42, 56) }
    var y = 48f
    fun line(text: String, paint: Paint = bodyPaint) {
        text.chunked(88).forEach {
            canvas.drawText(it, 42f, y, paint)
            y += 17f
        }
        y += 3f
    }
    canvas.drawText("Virtual Chemistry Lab Report", 42f, y, titlePaint)
    y += 34f
    line("Experiment: ${experiment.title}")
    line("Objective: ${experiment.objective}")
    line("Safety: ${experiment.safety}")
    line("Result: ${notes.result.ifBlank { "${experiment.measuredQuantity}: ${labNumber(simulatedResult(experiment, progress, workbenchActions(experiment).size))} ${experiment.unit}" }}")
    line("Observations: ${notes.observations.ifBlank { "See recorded measurement notebook." }}")
    line("Calculations: ${notes.calculations.ifBlank { experiment.analysis }}")
    line("Conclusion: ${notes.conclusion.ifBlank { experiment.expectedResult }}")
    if (trials.isNotEmpty()) {
        line("Repeat trials:")
        trials.forEach { line("Trial ${it.index}: ${labNumber(it.value)} ± ${labNumber(it.uncertainty)} ${experiment.unit}") }
    }
    document.finishPage(page)

    val fileName = "Lab_Report_${experiment.id}.pdf"
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/AIChemistryExplorer")
    }
    runCatching {
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Could not create report file")
        context.contentResolver.openOutputStream(uri)?.use { document.writeTo(it) }
            ?: error("Could not open report file")
        Toast.makeText(context, "Saved $fileName to Downloads", Toast.LENGTH_SHORT).show()
    }.onFailure {
        Toast.makeText(context, "Report export failed: ${it.message}", Toast.LENGTH_LONG).show()
    }
    document.close()
}

private fun workbenchActions(e: VirtualExperiment): List<String> {
    val operations = when (e.technique) {
        LabTechnique.TITRATION -> listOf("Condition pipette and burette", "Transfer exact aliquot", "Read initial meniscus", "Deliver titrant", "Approach endpoint dropwise", "Read final meniscus and repeat")
        LabTechnique.CALORIMETRY -> listOf("Zero balance and measure", "Record initial temperature", "Combine and close lid", "Stir continuously", "Capture temperature extremum", "Record heat-loss notes")
        LabTechnique.KINETICS -> listOf("Prepare exact concentrations", "Equilibrate reagents", "Mix and start timer", "Record early point", "Record midpoint", "Record final interval")
        LabTechnique.EQUILIBRIUM -> listOf("Calibrate instrument", "Prepare standards and blank", "Prepare mixture", "Allow equilibration", "Record replicates", "Calculate equilibrium amounts")
        LabTechnique.ELECTROCHEMISTRY -> listOf("Polish electrodes", "Prepare half-cells", "Install salt bridge", "Connect voltmeter", "Confirm polarity", "Record stable open-circuit emf")
        LabTechnique.SOLUBILITY -> listOf("Calibrate glassware", "Equilibrate solid and solution", "Remove excess solid", "Measure concentration", "Repeat at controlled temperature", "Calculate Ksp or recovery")
        LabTechnique.SEPARATION -> listOf("Inspect apparatus", "Load sample quantitatively", "Establish flow or heating", "Collect first fraction", "Collect remaining fractions", "Measure purity and recovery")
        LabTechnique.GAS -> listOf("Leak-test apparatus", "Measure reactant", "Record T and P", "Generate or compress gas", "Equalize and read volume", "Apply corrections")
        LabTechnique.QUALITATIVE -> listOf("Prepare blank and reference", "Observe unknown", "Add primary reagent", "Record response", "Add confirmation reagent", "Match observation pattern")
        LabTechnique.SYNTHESIS -> listOf("Weigh reactants", "Assemble reaction", "Control time and temperature", "Quench or complete", "Isolate and purify", "Dry, weigh and characterize")
        LabTechnique.GRAVIMETRY -> listOf("Condition and weigh vessel", "Form precipitate or residue quantitatively", "Digest or heat under control", "Filter and wash", "Dry or ignite", "Cool in desiccator and weigh to constant mass")
        LabTechnique.SPECTROSCOPY -> listOf("Prepare blank and standards", "Select wavelength and blank instrument", "Rinse and orient cuvette", "Measure standards", "Measure unknown in calibration range", "Fit calibration and calculate concentration")
        LabTechnique.PHYSICAL_MEASUREMENT -> listOf("Calibrate instrument", "Condition sample and apparatus", "Control temperature", "Take first measurement", "Repeat independent measurements", "Report mean, range and uncertainty")
    }
    return e.procedure.take(1) + operations + e.procedure.drop(1)
}

private fun measurements(e: VirtualExperiment, count: Int): List<Pair<String, String>> =
    (1..count.coerceIn(0, 8)).map { i ->
        val f = i / 8.0
        when (e.technique) {
            LabTechnique.TITRATION -> {
                val endpoint = when (e.id) {
                    "vol-01" -> 25.00
                    "vol-02" -> 20.82
                    "vol-03" -> 8.80
                    "vol-04" -> 12.00
                    "vol-05" -> 20.00
                    else -> 15.40
                }
                val v = endpoint * f
                val response = when (e.id) {
                    "vol-01", "vol-02" -> "pH ${labNumber(2 + 11 / (1 + exp(-(v - endpoint) * 2.2)))}"
                    "vol-03" -> if (v >= endpoint * .99) "blue starch–iodine endpoint" else "iodine consumed"
                    "vol-04" -> if (v >= endpoint * .99) "clear blue endpoint" else "wine-red complex"
                    "vol-05" -> if (v >= endpoint * .99) "persistent pale pink" else "permanganate discharged"
                    else -> if (v >= endpoint * .99) "back-titration endpoint" else "excess acid remains"
                }
                "Burette ${labNumber(v)} mL" to response
            }
            LabTechnique.CALORIMETRY -> "t = ${i * 20} s" to "T = ${labNumber(22 + abs(e.referenceValue).coerceAtMost(600.0) / 45 * (1 - exp(-i / 2.5)) * if (e.referenceValue < 0) 1 else -1)} °C"
            LabTechnique.KINETICS -> "t = ${i * 20} s" to "signal = ${labNumber(100 * exp(-i * .28))}%"
            LabTechnique.ELECTROCHEMISTRY -> "Reading $i" to "${labNumber(e.referenceValue * (1 - .025 * exp(-i.toDouble())))} V"
            LabTechnique.GAS -> "Trial $i" to "P=${95 + i * 5} kPa · V=${labNumber(245.0 / (95 + i * 5))} L"
            LabTechnique.QUALITATIVE -> "Test $i" to if (i < 4) "Reference/blank observed" else "Characteristic response detected"
            LabTechnique.SEPARATION -> "Fraction $i" to "${labNumber(e.referenceValue * (.91 + i * .012))} ${e.unit}"
            else -> "Trial $i" to "${labNumber(e.referenceValue * (1 + (i - 4) * .003))} ${e.unit}"
        }
    }

private fun simulatedResult(e: VirtualExperiment, progress: Int, total: Int): Double {
    if (progress == 0) return 0.0
    val completion = (progress.toDouble() / total.coerceAtLeast(1)).coerceIn(0.0, 1.0)
    val scatter = ((e.id.hashCode() % 9) - 4) / 1000.0
    return e.referenceValue * (.82 + .18 * completion + scatter)
}

private fun qualityControl(t: LabTechnique): String = when (t) {
    LabTechnique.TITRATION -> "Read the meniscus at eye level, clear air bubbles, remove the funnel, and obtain concordant titres within 0.10 mL."
    LabTechnique.CALORIMETRY -> "Use an insulated covered vessel, measure total solution mass, stir, and account for heat exchange."
    LabTechnique.KINETICS -> "Change one variable, control temperature, mix reproducibly, and collect enough early-time points."
    LabTechnique.EQUILIBRIUM -> "Use blanks and standards, control temperature, allow equilibration, and run replicates."
    LabTechnique.ELECTROCHEMISTRY -> "Polish electrodes, prevent cross-contamination, use a salt bridge, and measure open-circuit emf."
    LabTechnique.SOLUBILITY -> "Control temperature, prove equilibrium, avoid evaporation, and exclude suspended solid."
    LabTechnique.SEPARATION -> "Never seal heated glassware; minimize transfer loss and evaluate purity as well as recovery."
    LabTechnique.GAS -> "Leak-test, equalize levels, record temperature and pressure, correct water vapor, and use consistent units."
    LabTechnique.QUALITATIVE -> "Use a blank and known reference, follow reagent order, and require a confirmation test."
    LabTechnique.SYNTHESIS -> "Identify the limiting reagent, control conditions, purify, dry to constant mass, and assess identity and yield."
    LabTechnique.GRAVIMETRY -> "Use quantitative transfer, controlled precipitation, complete washing, a desiccator and repeated heat–cool–weigh cycles until constant mass."
    LabTechnique.SPECTROSCOPY -> "Blank correctly, use clean matched cuvettes, keep wavelength/path length fixed, randomize replicates and never extrapolate beyond the calibration range."
    LabTechnique.PHYSICAL_MEASUREMENT -> "Calibrate against a traceable reference, control temperature, avoid parallax/bubbles, repeat independently and report measurement uncertainty."
}

private fun actionLabel(t: LabTechnique) = when (t) {
    LabTechnique.TITRATION -> "Use glassware / deliver titrant"
    LabTechnique.KINETICS -> "Start timer / record interval"
    LabTechnique.ELECTROCHEMISTRY -> "Connect instrument / record"
    LabTechnique.SEPARATION -> "Operate apparatus / collect"
    LabTechnique.QUALITATIVE -> "Add reagent / observe"
    LabTechnique.GRAVIMETRY -> "Filter / wash / heat / weigh"
    LabTechnique.SPECTROSCOPY -> "Blank / measure absorbance"
    LabTechnique.PHYSICAL_MEASUREMENT -> "Calibrate / measure / repeat"
    else -> "Perform operation and record"
}

private fun techniqueColor(t: LabTechnique) = listOf(Cyan, Orange, Green, Purple, Color(0xFF4D8BFF))[t.ordinal % 5]
private fun techniqueSymbol(t: LabTechnique) = listOf("▥", "ΔT", "⏱", "⇌", "⚡", "◇", "⌁", "PVT", "◉", "⚗", "⚖", "Aλ", "ρ")[t.ordinal]
private fun labNumber(v: Double) = when { v == 0.0 -> "0"; abs(v) < .001 -> "%.2e".format(v); abs(v) < 10 -> "%.3f".format(v); abs(v) < 100 -> "%.2f".format(v); else -> "%.1f".format(v) }
