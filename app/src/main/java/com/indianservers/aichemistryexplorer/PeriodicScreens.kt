package com.indianservers.aichemistryexplorer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun PeriodicHubScreen(
    savedCount: Int,
    onOpen: (Screen) -> Unit,
    onSelect: (Element) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val complete = remember { completePeriodicTable(emptyList()) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenTitle("PERIODIC TABLE", "The building blocks of matter")
        Box(Modifier.fillMaxWidth().height(210.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier.size(190.dp).background(
                    Brush.radialGradient(listOf(Purple.copy(.15f), Color.Transparent)),
                    CircleShape
                )
            )
            AtomGraphic(190.dp, Purple)
        }
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search element, symbol or atomic number", color = Muted, fontSize = 12.sp) },
            leadingIcon = { Text("⌕", color = Cyan, fontSize = 22.sp) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
            singleLine = true,
            shape = RoundedCornerShape(13.dp)
        )
        if (query.isNotBlank()) {
            val matches = complete.filter {
                it.symbol.contains(query, true) || it.name.contains(query, true) || it.number.toString() == query
            }.take(5)
            Column(Modifier.padding(horizontal = 18.dp)) {
                matches.forEach {
                    Row(
                        Modifier.fillMaxWidth().clickable { onSelect(it) }.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(it.symbol, color = it.color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("  Atomic number ${it.number}", color = White, modifier = Modifier.weight(1f))
                        Text("›", color = Muted)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(18.dp, 16.dp, 18.dp, 8.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            HubAction("▦", "Explore", "118 elements", Cyan, Modifier.weight(1f)) { onOpen(Screen.FullTable) }
            HubAction("♛", "Quiz", "Challenge", Orange, Modifier.weight(1f)) { onOpen(Screen.Quiz) }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            HubAction("↗", "Trends", "Heat maps", Green, Modifier.weight(1f)) { onOpen(Screen.Trends) }
            HubAction("▤", "Notebook", "$savedCount saved", Purple, Modifier.weight(1f)) { onOpen(Screen.Notebook) }
        }
        Row(Modifier.fillMaxWidth().padding(18.dp, 9.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            HubAction("◫", "Categories", "10 families", Color(0xFFE34C9A), Modifier.weight(1f)) { onOpen(Screen.Categories) }
            HubAction("VS", "Compare", "Side by side", Color(0xFF4D8BFF), Modifier.weight(1f)) { onOpen(Screen.Compare) }
        }
    }
}

@Composable
private fun HubAction(icon: String, title: String, subtitle: String, color: Color, modifier: Modifier, click: () -> Unit) {
    Card(
        modifier.height(76.dp).clickable { click() },
        colors = CardDefaults.cardColors(Panel),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(.42f)),
        shape = RoundedCornerShape(13.dp)
    ) {
        Row(Modifier.fillMaxSize().padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(38.dp).background(color.copy(.12f), RoundedCornerShape(9.dp)), contentAlignment = Alignment.Center) {
                Text(icon, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.width(9.dp))
            Column {
                Text(title, color = White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(subtitle, color = Muted, fontSize = 9.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EnhancedPeriodicTableScreen(
    elements: List<Element>,
    onBack: () -> Unit,
    onSelect: (Element) -> Unit,
    onCompare: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var listMode by remember { mutableStateOf(false) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var quickElement by remember { mutableStateOf<Element?>(null) }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Periodic Table", "118 elements · 7 periods · 18 groups", onBack)
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TabPill("Table", !listMode, Modifier.weight(1f)) { listMode = false }
            TabPill("List", listMode, Modifier.weight(1f)) { listMode = true }
            TabPill("Compare", false, Modifier.weight(1f), onCompare)
        }
        OutlinedTextField(
            query, { query = it },
            placeholder = { Text("Search elements", color = Muted) },
            leadingIcon = { Text("⌕", color = Cyan, fontSize = 22.sp) },
            modifier = Modifier.fillMaxWidth().padding(18.dp, 10.dp),
            singleLine = true, shape = RoundedCornerShape(12.dp)
        )
        if (query.isNotBlank() || listMode) {
            val results = elements.filter { it.symbol.contains(query, true) || it.name.contains(query, true) || it.number.toString() == query }
            LazyVerticalGrid(GridCells.Fixed(4), contentPadding = PaddingValues(14.dp), modifier = Modifier.fillMaxSize()) {
                items(results) { PeriodicCell(it, 78, { quickElement = it }, { onCompare() }) }
            }
        } else {
            Column(
                Modifier.fillMaxSize().pointerInput(Unit) {
                    detectTransformGestures { _, _, gestureZoom, _ -> zoom = (zoom * gestureZoom).coerceIn(.75f, 1.45f) }
                }.horizontalScroll(rememberScrollState()).verticalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Row(Modifier.padding(start = 26.dp, bottom = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("PINCH TO ZOOM", color = Cyan, fontSize = 8.sp)
                    Text("  ${(zoom * 100).toInt()}%", color = Muted, fontSize = 8.sp)
                }
                Row(Modifier.padding(start = 26.dp)) {
                    (1..18).forEach { Text(it.toString(), color = Muted, fontSize = 8.sp, textAlign = TextAlign.Center, modifier = Modifier.width((48 * zoom).dp)) }
                }
                periodicRows.forEachIndexed { period, row ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${period + 1}", color = Muted, fontSize = 9.sp, modifier = Modifier.width(26.dp))
                        row.forEach { number ->
                            if (number == null) Spacer(Modifier.size((48 * zoom).dp, (54 * zoom).dp))
                            else PeriodicCell(elements[number - 1], (48 * zoom).toInt(), { quickElement = it }, { onCompare() })
                        }
                    }
                }
                Text("Lanthanides", color = Muted, fontSize = 9.sp, modifier = Modifier.padding(start = 26.dp, top = 10.dp))
                Row(Modifier.padding(start = 26.dp)) { lanthanideRow.forEach { PeriodicCell(elements[it - 1], (48 * zoom).toInt(), { quickElement = it }, { onCompare() }) } }
                Text("Actinides", color = Muted, fontSize = 9.sp, modifier = Modifier.padding(start = 26.dp, top = 5.dp))
                Row(Modifier.padding(start = 26.dp)) { actinideRow.forEach { PeriodicCell(elements[it - 1], (48 * zoom).toInt(), { quickElement = it }, { onCompare() }) } }
                CategoryLegend()
            }
        }
        quickElement?.let { element ->
            ModalBottomSheet(onDismissRequest = { quickElement = null }, containerColor = Panel2) {
                Column(Modifier.fillMaxWidth().padding(20.dp, 4.dp, 20.dp, 28.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(62.dp).background(element.color.copy(.14f), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
                            Text(element.symbol, color = element.color, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(element.name, color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text("${element.number} · ${element.category}", color = Muted, fontSize = 11.sp)
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Mass  ${element.mass}", color = White, fontSize = 11.sp)
                        Text("Radius  ${element.radius}", color = White, fontSize = 11.sp)
                        Text("Boiling  ${element.boiling}", color = White, fontSize = 11.sp)
                    }
                    Button(onClick = { quickElement = null; onSelect(element) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(element.color)) {
                        Text("Open complete element profile", color = Night, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PeriodicCell(element: Element, size: Int, click: (Element) -> Unit, longClick: ((Element) -> Unit)? = null) {
    val haptic = LocalHapticFeedback.current
    Box(
        Modifier.padding(2.dp).size(size.dp, (size + 6).dp).background(element.color.copy(.12f), RoundedCornerShape(7.dp))
            .border(1.dp, element.color.copy(.5f), RoundedCornerShape(7.dp)).combinedClickable(
                onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); click(element) },
                onLongClick = longClick?.let { action -> { haptic.performHapticFeedback(HapticFeedbackType.LongPress); action(element) } }
            )
    ) {
        Text(element.number.toString(), color = Muted, fontSize = if (size < 60) 6.sp else 8.sp, modifier = Modifier.padding(3.dp))
        Text(
            element.symbol, color = element.color, fontSize = if (size < 60) 13.sp else 20.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center)
        )
        if (size >= 70) Text(element.name, color = White, fontSize = 7.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.align(Alignment.BottomCenter).padding(3.dp))
    }
}

@Composable
private fun CategoryLegend() {
    val entries = listOf("Non-metal" to Cyan, "Transition" to Color(0xFF1CC6A1), "Halogen" to Green, "Noble gas" to Purple)
    Row(Modifier.padding(26.dp, 14.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
        entries.forEach { (name, color) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).background(color, CircleShape)); Spacer(Modifier.width(4.dp))
                Text(name, color = Muted, fontSize = 8.sp)
            }
        }
    }
}

@Composable
private fun TabPill(label: String, active: Boolean, modifier: Modifier, click: () -> Unit) {
    Text(
        label, color = if (active) Night else Muted, textAlign = TextAlign.Center, fontSize = 11.sp,
        modifier = modifier.background(if (active) Cyan else Panel, RoundedCornerShape(8.dp))
            .border(1.dp, if (active) Cyan else Border, RoundedCornerShape(8.dp))
            .clickable { click() }.padding(vertical = 9.dp)
    )
}

@Composable
internal fun ElementCategoriesScreen(elements: List<Element>, onBack: () -> Unit, onSelect: (Element) -> Unit) {
    val categories = listOf(
        "Alkali metal" to Color(0xFF4D8BFF), "Alkaline earth" to Orange,
        "Transition metal" to Color(0xFF1CC6A1), "Post-transition" to Color(0xFFE34C9A),
        "Metalloid" to Green, "Non-metal" to Cyan, "Halogen" to Green, "Noble gas" to Purple,
        "Lanthanide" to Color(0xFF8A6CFF), "Actinide" to Color(0xFFB64DDB)
    )
    var chosen by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Element Categories", chosen ?: "Choose a chemical family", onBack)
        if (chosen == null) {
            LazyVerticalGrid(GridCells.Fixed(2), contentPadding = PaddingValues(14.dp)) {
                items(categories) { (name, color) ->
                    val members = elements.filter { it.category == name }
                    Card(
                        Modifier.padding(5.dp).height(105.dp).clickable { chosen = name },
                        colors = CardDefaults.cardColors(color.copy(.09f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(.55f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(Modifier.fillMaxSize().padding(13.dp), verticalArrangement = Arrangement.SpaceBetween) {
                            Text("${members.size}", color = color, fontSize = 23.sp, fontWeight = FontWeight.Light)
                            Column {
                                Text(name, color = White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text(members.take(3).joinToString(" · ") { it.symbol }, color = Muted, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        } else {
            Text("‹ All categories", color = Cyan, modifier = Modifier.clickable { chosen = null }.padding(horizontal = 20.dp, vertical = 8.dp))
            LazyVerticalGrid(GridCells.Fixed(4), contentPadding = PaddingValues(14.dp)) {
                items(elements.filter { it.category == chosen }) { PeriodicCell(it, 78, onSelect) }
            }
        }
    }
}

@Composable
internal fun PeriodicTrendsScreen(onBack: () -> Unit, onHeatMap: () -> Unit) {
    val trends = listOf(
        Triple("Atomic Radius", "Decreases across a period; increases down a group.", Cyan),
        Triple("Ionization Energy", "Increases across a period; decreases down a group.", Purple),
        Triple("Electronegativity", "Rises toward fluorine in the upper-right corner.", Color(0xFF30CFFF)),
        Triple("Electron Affinity", "Generally becomes more negative across a period.", Green),
        Triple("Metallic Character", "Increases toward the lower-left corner.", Orange)
    )
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Periodic Trends", "See how properties change across the table", onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
            items(trends) { trend ->
                GlowCard(Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable {
                    if (trend.first == "Electronegativity") onHeatMap()
                }, trend.third) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(48.dp).background(trend.third.copy(.12f), CircleShape), contentAlignment = Alignment.Center) {
                            Text(if (trend.first == "Atomic Radius") "◎" else "↗", color = trend.third, fontSize = 23.sp)
                        }
                        Spacer(Modifier.width(13.dp))
                        Column(Modifier.weight(1f)) {
                            Text(trend.first, color = White, fontWeight = FontWeight.SemiBold)
                            Text(trend.second, color = Muted, fontSize = 11.sp)
                        }
                        Text("›", color = Muted, fontSize = 24.sp)
                    }
                }
            }
            item {
                Button(
                    onClick = onHeatMap, modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    colors = ButtonDefaults.buttonColors(Cyan), shape = RoundedCornerShape(12.dp)
                ) { Text("Open electronegativity heat map", color = Night, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
internal fun HeatMapScreen(elements: List<Element>, onBack: () -> Unit, onSelect: (Element) -> Unit) {
    var property by remember { mutableStateOf("Electronegativity") }
    val properties = listOf("Electronegativity", "Atomic radius", "Ionization", "Metallic character")
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("$property Heat Map", if (property == "Electronegativity") "Pauling scale · low to high" else "Relative periodic trend", onBack)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            properties.forEach { item ->
                Text(item, color = if (property == item) Night else Muted, fontSize = 9.sp,
                    modifier = Modifier.background(if (property == item) Cyan else Panel, RoundedCornerShape(8.dp)).clickable { property = item }.padding(horizontal = 11.dp, vertical = 8.dp))
            }
        }
        Text(if (property == "Electronegativity") "Measured values intensify toward fluorine." else "The map visualizes the general trend; known exceptions may occur.", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(20.dp, 9.dp))
        LazyVerticalGrid(GridCells.Fixed(6), contentPadding = PaddingValues(14.dp), modifier = Modifier.weight(1f)) {
            items(elements) { element ->
                val measured = if (property == "Electronegativity") paulingElectronegativity[element.symbol] else null
                val relative = relativeTrend(element.number, property)
                val heat = measured?.let { ((it - .7f) / 3.3f).coerceIn(0f, 1f) } ?: if (property != "Electronegativity") relative else null
                val heatColor = heat?.let(::lerpHeat) ?: Muted
                Box(
                    Modifier.padding(3.dp).height(58.dp).background(heatColor.copy(.22f), RoundedCornerShape(8.dp))
                        .border(1.dp, heatColor, RoundedCornerShape(8.dp)).clickable { onSelect(element) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(element.symbol, color = heatColor, fontWeight = FontWeight.Bold)
                        Text(measured?.let { String.format("%.2f", it) } ?: if (heat != null) "${(heat * 100).toInt()}" else "—", color = White, fontSize = 7.sp)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("0.7", color = Muted, fontSize = 9.sp)
            Box(Modifier.weight(1f).height(7.dp).padding(horizontal = 8.dp).background(Brush.horizontalGradient(listOf(Color(0xFF315BE8), Green, Orange, Color(0xFFFF4D5E))), CircleShape))
            Text("4.0", color = Muted, fontSize = 9.sp)
        }
    }
}

private fun relativeTrend(number: Int, property: String): Float {
    val rowIndex = periodicRows.indexOfFirst { row -> row.contains(number) }.let { if (it < 0) if (number in 58..71) 5 else 6 else it }
    val groupIndex = periodicRows.getOrNull(rowIndex)?.indexOf(number)?.let { if (it < 0) 2 else it } ?: 2
    val towardUpperRight = ((6 - rowIndex) / 6f + groupIndex / 17f) / 2f
    val towardLowerLeft = 1f - towardUpperRight
    return when (property) {
        "Atomic radius", "Metallic character" -> towardLowerLeft.coerceIn(0f, 1f)
        else -> towardUpperRight.coerceIn(0f, 1f)
    }
}

private fun lerpHeat(value: Float): Color = when {
    value < .33f -> Color(0xFF3977FF)
    value < .66f -> Color(0xFF25D7A1)
    value < .85f -> Orange
    else -> Color(0xFFFF4D6D)
}

@Composable
internal fun ElementComparisonScreen(elements: List<Element>, onBack: () -> Unit, onSelect: (Element) -> Unit) {
    var leftIndex by remember { mutableIntStateOf(7) }
    var rightIndex by remember { mutableIntStateOf(10) }
    val left = elements[leftIndex]
    val right = elements[rightIndex]
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenTitle("Element Comparison", "Tap an element card to view details", onBack)
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            ComparePicker(left, Modifier.weight(1f), { leftIndex = (leftIndex + 1) % elements.size }) { onSelect(left) }
            Text("VS", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(10.dp))
            ComparePicker(right, Modifier.weight(1f), { rightIndex = (rightIndex + 1) % elements.size }) { onSelect(right) }
        }
        CompareProperty("Atomic number", left.number.toString(), right.number.toString())
        CompareProperty("Atomic mass", left.mass, right.mass)
        CompareProperty("Atomic radius", left.radius, right.radius)
        CompareProperty("Boiling point", left.boiling, right.boiling)
        CompareProperty("Classification", left.category, right.category)
        Spacer(Modifier.height(16.dp))
        Text("Tap either ↻ button to cycle through all 118 elements.", color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ComparePicker(element: Element, modifier: Modifier, next: () -> Unit, open: () -> Unit) {
    Card(
        modifier.clickable { open() }, colors = CardDefaults.cardColors(Panel),
        border = androidx.compose.foundation.BorderStroke(1.dp, element.color.copy(.5f)), shape = RoundedCornerShape(14.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(element.symbol, color = element.color, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text(element.name, color = White, fontSize = 10.sp)
            }
            Text("↻", color = Cyan, fontSize = 21.sp, modifier = Modifier.clickable { next() }.padding(6.dp))
        }
    }
}

@Composable
private fun CompareProperty(label: String, left: String, right: String) {
    Column(Modifier.padding(horizontal = 18.dp)) {
        Text(label.uppercase(), color = Muted, fontSize = 9.sp, modifier = Modifier.padding(top = 13.dp, bottom = 6.dp))
        Row(Modifier.fillMaxWidth()) {
            Text(left, color = White, fontSize = 12.sp, modifier = Modifier.weight(1f))
            Text(right, color = White, fontSize = 12.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
        }
        HorizontalDivider(Modifier.padding(top = 10.dp), color = Border)
    }
}

@Composable
internal fun ElementNotebookScreen(
    elements: List<Element>,
    onBack: () -> Unit,
    onSelect: (Element) -> Unit,
    onRemove: (Element) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("My Notebook", "${elements.size} saved elements", onBack)
        if (elements.isEmpty()) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("☆", color = Cyan, fontSize = 58.sp)
                Text("Your notebook is ready", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Open any element and tap the star to save it.", color = Muted, fontSize = 12.sp)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(18.dp)) {
                items(elements, key = { it.number }) { element ->
                    Card(
                        Modifier.fillMaxWidth().padding(bottom = 9.dp).clickable { onSelect(element) },
                        colors = CardDefaults.cardColors(Panel), shape = RoundedCornerShape(13.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, element.color.copy(.35f))
                    ) {
                        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(48.dp).background(element.color.copy(.12f), RoundedCornerShape(9.dp)), contentAlignment = Alignment.Center) {
                                Text(element.symbol, color = element.color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(element.name, color = White, fontWeight = FontWeight.SemiBold)
                                Text("${element.number} · ${element.category}", color = Muted, fontSize = 10.sp)
                            }
                            Text("Remove", color = Color(0xFFFF6C7A), fontSize = 10.sp, modifier = Modifier.clickable { onRemove(element) }.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}
