package com.indianservers.aichemistryexplorer

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

internal enum class ChemistryTool(
    val title: String,
    val icon: String,
    val summary: String,
    val color: Color
) {
    EQUATION("Equation balancer", "⇌", "Balance equations with atom-by-atom working.", Orange),
    FORMULA("Formula & valency", "±", "Build neutral ionic formulae from ion charges.", Cyan),
    LEWIS("Lewis structure editor", "••", "Arrange bonds and account for valence electrons.", Purple),
    VSEPR("VSEPR builder", "◇", "Explore electron domains, shapes and bond angles.", Green),
    STOICHIOMETRY("Mole workspace", "n", "Use balanced ratios for mole calculations.", Cyan),
    NOMENCLATURE("Nomenclature trainer", "C", "Practise systematic organic names with feedback.", Purple),
    PREDICTOR("Product predictor", "→", "Predict common products and reaction classes.", Orange),
    SOLUBILITY("Solubility curves", "↗", "Graph temperature-dependent solubility.", Green),
    TRENDS("Periodic trends", "Z", "Compare radius, electronegativity and ionisation.", Cyan),
    UNITS("Units & significant figures", "≋", "Check conversions, precision and reported answers.", Orange)
}

@Composable
internal fun ChemistryToolsScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf<ChemistryTool?>(null) }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle(
            selected?.title ?: "Interactive Chemistry Tools",
            selected?.summary ?: "10 reusable workspaces · available from every lesson",
            back = { if (selected == null) onBack() else selected = null }
        )
        if (selected == null) ChemistryToolsHub { selected = it } else ToolContent(selected!!)
    }
}

@Composable
private fun ChemistryToolsHub(open: (ChemistryTool) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp, 4.dp, 18.dp, 28.dp)) {
        item {
            Text("Choose a workspace", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Your inputs stay local to the tool while it is open. Every result includes reasoning or a check.", color = Muted, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
        }
        items(ChemistryTool.entries.size) { index ->
            val tool = ChemistryTool.entries[index]
            GlowCard(
                Modifier.fillMaxWidth().padding(top = 9.dp).clickable { open(tool) }
                    .semantics { contentDescription = "Open ${tool.title}. ${tool.summary}" },
                tool.color
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).background(tool.color.copy(.15f), CircleShape), contentAlignment = Alignment.Center) {
                        Text(tool.icon, color = tool.color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(tool.title, color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(tool.summary, color = Muted, fontSize = 10.sp, lineHeight = 15.sp)
                    }
                    Text("›", color = tool.color, fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
private fun ToolContent(tool: ChemistryTool) = when (tool) {
    ChemistryTool.EQUATION -> EquationBalancerTool()
    ChemistryTool.FORMULA -> FormulaValencyTool()
    ChemistryTool.LEWIS -> LewisEditorTool()
    ChemistryTool.VSEPR -> VseprBuilderTool()
    ChemistryTool.STOICHIOMETRY -> StoichiometryTool()
    ChemistryTool.NOMENCLATURE -> NomenclatureTool()
    ChemistryTool.PREDICTOR -> ProductPredictorTool()
    ChemistryTool.SOLUBILITY -> SolubilityCurveTool()
    ChemistryTool.TRENDS -> PeriodicTrendTool()
    ChemistryTool.UNITS -> UnitCheckerTool()
}

@Composable
private fun ToolScroll(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp, 4.dp, 18.dp, 30.dp), content = content)
}

@Composable
private fun ChemistryInput(value: String, onChange: (String) -> Unit, label: String, numeric: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        textStyle = TextStyle(color = White),
        keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Decimal else KeyboardType.Text),
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
    )
}

@Composable
private fun ActionButton(label: String, color: Color, action: () -> Unit) {
    Button(
        onClick = action,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
    ) { Text(label, color = Night, fontWeight = FontWeight.Bold) }
}

@Composable
private fun ResultCard(title: String, result: String, color: Color, supporting: String? = null) {
    GlowCard(Modifier.fillMaxWidth().padding(top = 12.dp), color) {
        Text(title.uppercase(), color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(result, color = White, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
        supporting?.let { Text(it, color = Muted, fontSize = 10.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 5.dp)) }
    }
}

@Composable
private fun <T> ChoiceStrip(items: List<T>, selected: T, label: (T) -> String, color: Color, choose: (T) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 7.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { item ->
            Text(
                label(item),
                color = if (item == selected) Night else Muted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.background(if (item == selected) color else Panel, RoundedCornerShape(8.dp))
                    .border(1.dp, if (item == selected) color else Border, RoundedCornerShape(8.dp))
                    .clickable { choose(item) }.padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun EquationBalancerTool() = ToolScroll {
    var equation by remember { mutableStateOf("Fe + O2 -> Fe2O3") }
    var result by remember { mutableStateOf(balanceChemicalEquation(equation)) }
    Text("Enter formulae with + between substances and -> between sides.", color = Muted, fontSize = 11.sp)
    ChemistryInput(equation, { equation = it }, "Unbalanced equation")
    ActionButton("Balance and explain", Orange) { result = balanceChemicalEquation(equation) }
    if (result.error != null) ResultCard("Check the equation", result.error!!, Orange) else {
        ResultCard("Balanced equation", result.balancedEquation, Green)
        Text("BALANCING STEPS", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp))
        result.steps.forEachIndexed { index, step ->
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.Top) {
                Text("${index + 1}", color = Night, textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.size(24.dp).background(Orange, CircleShape).padding(top = 4.dp))
                Text(step, color = White, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.weight(1f).padding(start = 9.dp))
            }
        }
    }
}

@Composable
private fun FormulaValencyTool() = ToolScroll {
    var cation by remember { mutableStateOf(commonCations.first()) }
    var anion by remember { mutableStateOf(commonAnions.first()) }
    val result = buildIonicFormula(cation, anion)
    Text("Select a positive and negative ion. Charges are simplified to the smallest neutral ratio.", color = Muted, fontSize = 11.sp)
    Text("POSITIVE ION", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 13.dp))
    ChoiceStrip(commonCations, cation, { "${it.symbol} ${chargeTextUi(it.charge)}" }, Cyan) { cation = it }
    Text("NEGATIVE ION", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 13.dp))
    ChoiceStrip(commonAnions, anion, { "${it.symbol} ${chargeTextUi(it.charge)}" }, Purple) { anion = it }
    ResultCard("Neutral formula", result.formula, Green, result.explanation)
}

private fun chargeTextUi(value: Int) = "${kotlin.math.abs(value)}${if (value > 0) "+" else "−"}"

@Composable
private fun LewisEditorTool() = ToolScroll {
    val elements = listOf("C", "N", "O", "P", "S")
    val ligands = listOf("H", "F", "Cl", "O")
    var central by remember { mutableStateOf("C") }
    var ligand by remember { mutableStateOf("H") }
    var count by remember { mutableIntStateOf(4) }
    var order by remember { mutableIntStateOf(1) }
    var charge by remember { mutableIntStateOf(0) }
    val summary = lewisSummary(central, ligand, count, order, charge)
    Text("Edit the central atom, surrounding atoms, bond order and overall charge.", color = Muted, fontSize = 11.sp)
    Text("CENTRAL ATOM", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
    ChoiceStrip(elements, central, { it }, Purple) { central = it }
    Text("TERMINAL ATOM", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
    ChoiceStrip(ligands, ligand, { it }, Cyan) { ligand = it }
    Stepper("Terminal atoms", count, 1, 6, Purple) { count = it }
    Stepper("Bond order", order, 1, 3, Orange) { order = it }
    Stepper("Overall charge", charge, -2, 2, Cyan) { charge = it }
    LewisDiagram(central, ligand, count, order, summary.remainingElectrons, Purple)
    ResultCard("Electron accounting", "${summary.totalValenceElectrons} valence e⁻", Green,
        "${summary.bondingElectrons} bonding e⁻ · ${summary.remainingElectrons} e⁻ remain. ${summary.formalGuidance}")
}

@Composable
private fun Stepper(label: String, value: Int, min: Int, max: Int, color: Color, update: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = White, fontSize = 11.sp, modifier = Modifier.weight(1f))
        Text("−", color = color, fontSize = 22.sp, modifier = Modifier.clickable { update((value - 1).coerceAtLeast(min)) }.padding(10.dp))
        Text(value.toString(), color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.size(34.dp).background(Panel, CircleShape).padding(top = 7.dp))
        Text("+", color = color, fontSize = 20.sp, modifier = Modifier.clickable { update((value + 1).coerceAtMost(max)) }.padding(10.dp))
    }
}

@Composable
private fun LewisDiagram(central: String, ligand: String, count: Int, order: Int, remaining: Int, color: Color) {
    Canvas(Modifier.fillMaxWidth().height(235.dp).padding(top = 10.dp).background(Panel, RoundedCornerShape(12.dp)).semantics {
        contentDescription = "Lewis structure with $central central, $count $ligand atoms, bond order $order, and $remaining remaining electrons"
    }) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val orbit = size.minDimension * .34f
        repeat(count) { index ->
            val angle = -PI / 2 + index * 2 * PI / count
            val end = Offset(center.x + cos(angle).toFloat() * orbit, center.y + sin(angle).toFloat() * orbit)
            val normal = Offset(-sin(angle).toFloat(), cos(angle).toFloat())
            repeat(order) { line ->
                val shift = normal * ((line - (order - 1) / 2f) * 6f)
                drawLine(White.copy(.72f), center + shift, end + shift, 3f, cap = StrokeCap.Round)
            }
            drawCircle(Cyan.copy(.2f), 24f, end)
            drawContext.canvas.nativeCanvas.drawText(ligand, end.x, end.y + 8f, Paint().apply { this.color = White.toArgb(); textSize = 24f; textAlign = Paint.Align.CENTER; isFakeBoldText = true })
        }
        drawCircle(color.copy(.24f), 31f, center)
        drawCircle(color, 31f, center, style = Stroke(2f))
        drawContext.canvas.nativeCanvas.drawText(central, center.x, center.y + 9f, Paint().apply { this.color = White.toArgb(); textSize = 28f; textAlign = Paint.Align.CENTER; isFakeBoldText = true })
        repeat((remaining / 2).coerceAtMost(8)) { pair ->
            val angle = pair * 2 * PI / (remaining / 2).coerceAtLeast(1)
            val dotCenter = Offset(center.x + cos(angle).toFloat() * 47f, center.y + sin(angle).toFloat() * 47f)
            drawCircle(Orange, 3.2f, dotCenter - Offset(2.5f, 0f)); drawCircle(Orange, 3.2f, dotCenter + Offset(2.5f, 0f))
        }
    }
}

@Composable
private fun VseprBuilderTool() = ToolScroll {
    var bonded by remember { mutableIntStateOf(4) }
    var lonePairs by remember { mutableIntStateOf(0) }
    val result = vseprShape(bonded, lonePairs)
    Text("Each single, double or triple bond counts as one electron domain. Lone pairs repel more strongly.", color = Muted, fontSize = 11.sp)
    Stepper("Bonded domains (X)", bonded, 1, 6, Green) { next ->
        lonePairs = lonePairs.coerceIn(maxOf(0, 2 - next), minOf(3, 6 - next))
        bonded = next
    }
    Stepper("Lone pairs (E)", lonePairs, maxOf(0, 2 - bonded), minOf(3, 6 - bonded), Purple) { lonePairs = it }
    VseprDiagram(bonded, lonePairs, Green)
    ResultCard(result.notation, result.molecularShape, Green, "Electron geometry: ${result.electronGeometry} · Approximate angles: ${result.approximateAngle}")
}

@Composable
private fun VseprDiagram(bonded: Int, lonePairs: Int, color: Color) {
    val domains = bonded + lonePairs
    Canvas(Modifier.fillMaxWidth().height(225.dp).background(Panel, RoundedCornerShape(12.dp)).semantics { contentDescription = "$bonded bonded domains and $lonePairs lone pairs" }) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension * .34f
        repeat(domains) { index ->
            val angle = -PI / 2 + index * 2 * PI / domains
            val end = Offset(center.x + cos(angle).toFloat() * radius, center.y + sin(angle).toFloat() * radius)
            if (index < bonded) {
                drawLine(White.copy(.7f), center, end, 5f, cap = StrokeCap.Round)
                drawCircle(Cyan, 17f, end)
                drawCircle(White.copy(.65f), 4f, end - Offset(5f, 5f))
            } else {
                drawCircle(Purple.copy(.18f), 25f, end)
                drawCircle(Purple, 4f, end - Offset(6f, 0f)); drawCircle(Purple, 4f, end + Offset(6f, 0f))
            }
        }
        drawCircle(color, 27f, center)
        drawContext.canvas.nativeCanvas.drawText("A", center.x, center.y + 9f, Paint().apply { this.color = Night.toArgb(); textSize = 27f; textAlign = Paint.Align.CENTER; isFakeBoldText = true })
    }
}

@Composable
private fun StoichiometryTool() = ToolScroll {
    var equation by remember { mutableStateOf("N2 + H2 -> NH3") }
    var balance by remember { mutableStateOf(balanceChemicalEquation(equation)) }
    var knownMoles by remember { mutableStateOf("2.50") }
    var knownCoefficient by remember { mutableIntStateOf(3) }
    var targetCoefficient by remember { mutableIntStateOf(2) }
    Text("Balance first, then use coefficients as mole ratios. Enter the coefficient for the known and target substances.", color = Muted, fontSize = 11.sp)
    ChemistryInput(equation, { equation = it }, "Reaction equation")
    ActionButton("Balance reaction", Cyan) { balance = balanceChemicalEquation(equation) }
    ResultCard("Working equation", balance.balancedEquation.ifBlank { balance.error ?: "Enter a valid equation" }, if (balance.error == null) Green else Orange)
    ChemistryInput(knownMoles, { knownMoles = it }, "Known amount (mol)", true)
    Stepper("Known coefficient", knownCoefficient, 1, 12, Cyan) { knownCoefficient = it }
    Stepper("Target coefficient", targetCoefficient, 1, 12, Purple) { targetCoefficient = it }
    knownMoles.toDoubleOrNull()?.let {
        val result = stoichiometricMoles(it, knownCoefficient, targetCoefficient)
        ResultCard("Target amount", "${"%.4g".format(result.targetMoles)} mol", Purple, result.explanation)
    }
}

@Composable
private fun NomenclatureTool() = ToolScroll {
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableIntStateOf(0) }
    val question = nomenclatureQuestions[index]
    Text("QUESTION ${index + 1}/${nomenclatureQuestions.size} · SCORE $score", color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    ResultCard("Name this structure", question.structure, Purple)
    question.options.forEach { option ->
        val chosen = selected == option
        val correct = option == question.answer
        val color = when { selected != null && correct -> Green; chosen -> Orange; else -> Border }
        Text(option, color = if (selected != null && correct) Green else White, fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).background(Panel, RoundedCornerShape(9.dp)).border(1.dp, color, RoundedCornerShape(9.dp))
                .clickable(enabled = selected == null) { selected = option; if (correct) score++ }.padding(12.dp))
    }
    selected?.let {
        ResultCard(if (it == question.answer) "Correct" else "Review", question.answer, if (it == question.answer) Green else Orange, question.explanation)
        ActionButton(if (index == nomenclatureQuestions.lastIndex) "Restart practice" else "Next structure", Purple) {
            index = if (index == nomenclatureQuestions.lastIndex) 0 else index + 1
            if (index == 0) score = 0
            selected = null
        }
    }
}

@Composable
private fun ProductPredictorTool() = ToolScroll {
    var reactants by remember { mutableStateOf("HCl + NaOH") }
    var prediction by remember { mutableStateOf<ReactionPrediction?>(null) }
    var attempted by remember { mutableStateOf(false) }
    Text("Predicts common school-level patterns. Always verify conditions, states and exceptions in the lesson.", color = Muted, fontSize = 11.sp)
    ChemistryInput(reactants, { reactants = it }, "Reactants or reaction conditions")
    ChoiceStrip(listOf("HCl + NaOH", "CH4 + O2", "Zn + HCl", "AgNO3 + NaCl", "CaCO3 + heat"), reactants, { it }, Orange) { reactants = it }
    ActionButton("Predict products", Orange) { prediction = predictReactionProducts(reactants); attempted = true }
    prediction?.let { ResultCard(it.type, it.products, Green, it.explanation) }
    if (attempted && prediction == null) ResultCard("Pattern not recognised", "Try a supported school reaction", Orange, "Use names or formulae for neutralisation, combustion, displacement, precipitation, metal–water or carbonate decomposition.")
}

@Composable
private fun SolubilityCurveTool() = ToolScroll {
    var series by remember { mutableStateOf(solubilitySeries.first()) }
    var temperature by remember { mutableFloatStateOf(40f) }
    var dissolved by remember { mutableStateOf("50") }
    val capacity = interpolateSolubility(series, temperature)
    val amount = dissolved.toFloatOrNull()
    Text("Values are grams of solute per 100 g water. Move temperature to interpolate between reference data.", color = Muted, fontSize = 11.sp)
    ChoiceStrip(solubilitySeries, series, { it.name }, Green) { series = it }
    SolubilityGraph(series, temperature)
    Text("Temperature · ${temperature.roundToInt()} °C", color = White, fontSize = 11.sp, modifier = Modifier.padding(top = 9.dp))
    Slider(temperature, { temperature = it }, valueRange = 0f..100f, colors = SliderDefaults.colors(thumbColor = Green, activeTrackColor = Green))
    ChemistryInput(dissolved, { dissolved = it }, "Solute added (g per 100 g water)", true)
    val state = when { amount == null -> "Enter an amount"; amount < capacity * .98f -> "Unsaturated"; amount <= capacity * 1.02f -> "Saturated"; else -> "${"%.1f".format(amount - capacity)} g remains undissolved" }
    ResultCard("${series.name} at ${temperature.roundToInt()} °C", "${"%.1f".format(capacity)} g / 100 g H₂O", Green, state)
}

@Composable
private fun SolubilityGraph(series: SolubilitySeries, selectedTemperature: Float) {
    Canvas(Modifier.fillMaxWidth().height(260.dp).padding(top = 10.dp).background(Panel, RoundedCornerShape(12.dp)).semantics { contentDescription = "Solubility curve for ${series.name}" }) {
        val left = 42f; val right = size.width - 16f; val top = 18f; val bottom = size.height - 34f
        repeat(6) { index ->
            val y = bottom - index * (bottom - top) / 5f
            drawLine(Border, Offset(left, y), Offset(right, y), 1f)
        }
        drawLine(White.copy(.55f), Offset(left, top), Offset(left, bottom), 2f)
        drawLine(White.copy(.55f), Offset(left, bottom), Offset(right, bottom), 2f)
        fun point(t: Float, value: Float) = Offset(left + t / 100f * (right - left), bottom - value.coerceAtMost(250f) / 250f * (bottom - top))
        val path = Path()
        series.points.forEachIndexed { index, item -> val p = point(item.temperature.toFloat(), item.gramsPer100gWater); if (index == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y); drawCircle(Green, 4f, p) }
        drawPath(path, Green, style = Stroke(4f, cap = StrokeCap.Round))
        val selected = point(selectedTemperature, interpolateSolubility(series, selectedTemperature))
        drawLine(Cyan.copy(.65f), Offset(selected.x, top), Offset(selected.x, bottom), 2f)
        drawCircle(Cyan, 7f, selected)
        val paint = Paint().apply { color = Muted.toArgb(); textSize = 22f }
        drawContext.canvas.nativeCanvas.drawText("0", left - 5f, bottom + 24f, paint)
        drawContext.canvas.nativeCanvas.drawText("Temperature (°C)", (left + right) / 2 - 65f, size.height - 6f, paint)
        drawContext.canvas.nativeCanvas.drawText("250", 3f, top + 7f, paint)
    }
}

private enum class TrendMetric(val label: String, val unit: String) { RADIUS("Atomic radius", "pm"), ELECTRONEGATIVITY("Electronegativity", "Pauling"), IONIZATION("1st ionisation", "kJ mol⁻¹") }

@Composable
private fun PeriodicTrendTool() = ToolScroll {
    var period by remember { mutableIntStateOf(2) }
    var metric by remember { mutableStateOf(TrendMetric.RADIUS) }
    var selected by remember { mutableStateOf(trendElements.first()) }
    val values = trendElements.filter { it.period == period }
    Text("Compare measured values across a period and relate them to effective nuclear charge and shielding.", color = Muted, fontSize = 11.sp)
    ChoiceStrip(listOf(2, 3), period, { "Period $it" }, Cyan) {
        period = it
        selected = trendElements.first { element -> element.period == it }
    }
    ChoiceStrip(TrendMetric.entries, metric, { it.label }, Purple) { metric = it }
    TrendGraph(values, metric, selected) { selected = it }
    val value = trendValue(selected, metric)
    ResultCard(selected.name, "${selected.symbol} · ${formatTrend(value)} ${metric.unit}", Cyan, trendExplanation(metric, period))
}

private fun trendValue(element: TrendElement, metric: TrendMetric): Float = when (metric) { TrendMetric.RADIUS -> element.atomicRadius.toFloat(); TrendMetric.ELECTRONEGATIVITY -> element.electronegativity ?: 0f; TrendMetric.IONIZATION -> element.ionizationEnergy.toFloat() }
private fun formatTrend(value: Float) = if (value < 10f) "%.2f".format(value) else value.roundToInt().toString()
private fun trendExplanation(metric: TrendMetric, period: Int) = when (metric) {
    TrendMetric.RADIUS -> "Across period $period, radius generally decreases because effective nuclear charge increases while the principal shell stays the same."
    TrendMetric.ELECTRONEGATIVITY -> "Across period $period, attraction for shared electrons generally increases toward the halogens."
    TrendMetric.IONIZATION -> "Ionisation energy generally rises across period $period, with subshell and electron-pairing exceptions."
}

@Composable
private fun TrendGraph(values: List<TrendElement>, metric: TrendMetric, selected: TrendElement, choose: (TrendElement) -> Unit) {
    Canvas(Modifier.fillMaxWidth().height(250.dp).padding(top = 10.dp).background(Panel, RoundedCornerShape(12.dp)).semantics { contentDescription = "${metric.label} graph across period ${selected.period}" }) {
        val max = values.maxOf { trendValue(it, metric) }.coerceAtLeast(1f)
        val bottom = size.height - 34f; val top = 20f; val width = (size.width - 28f) / values.size
        values.forEachIndexed { index, element ->
            val value = trendValue(element, metric)
            val barHeight = value / max * (bottom - top)
            val x = 16f + index * width
            val color = if (element == selected) Cyan else Purple.copy(.62f)
            drawRect(color, Offset(x + 5f, bottom - barHeight), androidx.compose.ui.geometry.Size(width - 10f, barHeight))
            drawContext.canvas.nativeCanvas.drawText(element.symbol, x + width / 2, bottom + 23f, Paint().apply { this.color = White.toArgb(); textSize = 21f; textAlign = Paint.Align.CENTER })
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        values.forEach { element -> Text(element.symbol, color = if (element == selected) Cyan else Muted, fontSize = 9.sp, modifier = Modifier.clickable { choose(element) }.padding(7.dp)) }
    }
}

@Composable
private fun UnitCheckerTool() = ToolScroll {
    val units = listOf("g", "kg", "mg", "mol", "mmol", "L", "mL", "cm3", "Pa", "kPa", "atm")
    var rawValue by remember { mutableStateOf("0.0250") }
    var from by remember { mutableStateOf("g") }
    var to by remember { mutableStateOf("mg") }
    var student by remember { mutableStateOf("25.0") }
    val number = rawValue.toDoubleOrNull()
    val converted = number?.let { convertUnit(it, from, to) }
    val sigFigs = significantFigures(rawValue)
    Text("Convert compatible chemistry units, enter your answer, and check both magnitude and reported precision.", color = Muted, fontSize = 11.sp)
    ChemistryInput(rawValue, { rawValue = it }, "Given value", true)
    Text("FROM", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 11.dp))
    ChoiceStrip(units, from, { it }, Cyan) { from = it }
    Text("TO", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 11.dp))
    ChoiceStrip(units, to, { it }, Purple) { to = it }
    ChemistryInput(student, { student = it }, "Your converted value", true)
    if (converted == null) ResultCard("Unit check", "Incompatible or unknown units", Orange, "Mass, amount, volume and pressure units cannot be mixed without an additional physical relationship.") else {
        val studentValue = student.toDoubleOrNull()
        val correct = studentValue != null && approximatelyEqual(studentValue, converted.value)
        val expectedSig = sigFigs ?: 0
        val studentSig = significantFigures(student) ?: 0
        ResultCard(
            if (correct && expectedSig == studentSig) "Answer accepted" else "Check your answer",
            "${"%.8g".format(converted.value)} ${converted.unit}",
            if (correct && expectedSig == studentSig) Green else Orange,
            "Given value: $expectedSig significant figures · Your answer: $studentSig. ${if (!correct) "The numerical conversion differs." else if (expectedSig != studentSig) "The magnitude is correct; adjust the significant figures." else "Magnitude, unit and precision agree."}"
        )
    }
}
