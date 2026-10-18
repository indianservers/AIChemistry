package com.indianservers.aichemistryexplorer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import kotlin.math.*

private val Ink = Color(0xFF081840)
private val Blue = Color(0xFF008EEB)
private val SoftInk = Color(0xFF586A82)

@Composable
internal fun ElementsViewerHomeCard(onOpen: () -> Unit) {
    Row(Modifier.padding(horizontal = 18.dp, vertical = 10.dp).fillMaxWidth()
        .shadow(10.dp, RoundedCornerShape(24.dp)).clip(RoundedCornerShape(24.dp))
        .background(Brush.linearGradient(listOf(Color(0xFFF4FCFF), Color(0xFFCEEFFF))))
        .clickable(onClick = onOpen).padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("⚛", color = Blue, fontSize = 48.sp)
        Column(Modifier.weight(1f).padding(start = 16.dp)) {
            Text("Elements Viewer", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
            Text("118 elements · Interactive 3D atoms", color = SoftInk, fontSize = 12.sp)
            Text("Explore. Learn. Discover.", color = Blue, fontSize = 11.sp, modifier = Modifier.padding(top = 7.dp))
        }
        Text("›", color = Blue, fontSize = 32.sp)
    }
}

internal fun viewerMatches(element: Element, filter: String, query: String): Boolean {
    val group = elementFacts(element.number).groupBlock.lowercase()
    val categoryMatch = when (filter) {
        "Metals" -> ("metal" in group && "nonmetal" !in group && "metalloid" !in group) || group in setOf("lanthanide", "actinide")
        "Nonmetals" -> "nonmetal" in group || "halogen" in group
        "Metalloids" -> "metalloid" in group
        "Noble Gases" -> "noble" in group
        else -> true
    }
    return categoryMatch && (query.isBlank() || element.name.contains(query, true) ||
        element.symbol.equals(query.trim(), true) || element.number.toString() == query.trim())
}

internal fun viewerTemperature(kelvin: String): String = kelvin.toDoubleOrNull()?.let {
    String.format(java.util.Locale.US, "%.1f °C", it - 273.15)
} ?: "Unknown"

internal fun viewerAdjacent(number: Int, delta: Int): Int = (number + delta).coerceIn(1, 118)


// Foundation controls avoid Material3's ChildSemanticsNode crash when a paged favorite changes.
@Composable
private fun ViewerAction(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 8.dp), content: @Composable RowScope.() -> Unit) {
    Row(modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick).padding(contentPadding),
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, content = content)
}

internal fun viewerProperties(element: Element): List<Pair<String, String>> {
    val facts = elementFacts(element.number)
    return listOf("Atomic number" to element.number.toString(), "Atomic mass" to "${facts.atomicMass} u",
        "Electron configuration" to facts.electronConfiguration, "Oxidation states" to facts.oxidationStates,
        "Electronegativity" to facts.electronegativity, "Density" to (if (facts.densityGcm3 == "—") "Unknown" else "${facts.densityGcm3} g/cm³"),
        "Melting point" to viewerTemperature(facts.meltingPointK), "Boiling point" to viewerTemperature(facts.boilingPointK))
}

@Composable
internal fun ElementsViewerScreen(elements: List<Element>, onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("elements_viewer", 0) }
    val all = remember(elements) { elements.sortedBy { it.number } }
    var filter by rememberSaveable { mutableStateOf("All Elements") }
    var onlyFavorites by rememberSaveable { mutableStateOf(false) }
    var favorites by remember { mutableStateOf(prefs.getStringSet("favorites", emptySet()).orEmpty().toSet()) }
    val ordered = remember(all, filter, onlyFavorites, favorites) {
        all.filter { viewerMatches(it, filter, "") && (!onlyFavorites || it.number.toString() in favorites) }
    }
    val pager = rememberPagerState(initialPage = ordered.indexOfFirst { it.number == prefs.getInt("selected", 8) }.coerceAtLeast(0)) { ordered.size }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val reduced = viewerReducedMotion()
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var rotate by rememberSaveable { mutableStateOf(false) }
    var firstFilter by remember { mutableStateOf(true) }
    BackHandler(enabled = rotate) { rotate = false }
    LaunchedEffect(pager, ordered, reduced) {
        var first = true
        snapshotFlow { pager.settledPage }.collect { page ->
            ordered.getOrNull(page)?.let { prefs.edit().putInt("selected", it.number).apply() }
            if (!first && !reduced) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            first = false
        }
    }
    LaunchedEffect(filter, onlyFavorites) {
        if (!firstFilter && ordered.isNotEmpty()) pager.scrollToPage(0)
        firstFilter = false
    }
    fun jump(page: Int) {
        if (page !in ordered.indices) return
        scope.launch { if (reduced) pager.scrollToPage(page) else pager.animateScrollToPage(page,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = .9f)) }
    }
    fun save(element: Element) {
        val key = element.number.toString()
        val updated = if (key in favorites) favorites - key else favorites + key
        prefs.edit().putStringSet("favorites", updated.toSet()).apply()
        favorites = updated
    }
    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFF5FBFF), Color(0xFFE7F3FF))))) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            ViewerAction(onBack, Modifier.semantics { contentDescription = "Back" }) { Text("‹", color = Blue, fontSize = 30.sp, lineHeight = 32.sp) }
            Column(Modifier.weight(1f)) {
                Text("Chemistry", color = Ink, fontSize = 23.sp, lineHeight = 26.sp, fontWeight = FontWeight.Black)
                Text("Element %03d / %03d".format(ordered.getOrNull(pager.currentPage)?.number ?: 0, ordered.size), color = SoftInk, fontSize = 10.sp, lineHeight = 14.sp)
            }
            ViewerAction({ query = ""; searchOpen = true }) { ViewerIcon("search", Modifier.size(18.dp)); Text("Search", color = Blue, fontSize = 11.sp) }
            ViewerAction({ onlyFavorites = !onlyFavorites }, Modifier.semantics { contentDescription = "Browse favorites"; selected = onlyFavorites }) {
                Text(if (onlyFavorites) "♥" else "♡", color = Color(0xFFED7389), fontSize = 27.sp, lineHeight = 30.sp)
            }
        }
        LazyRow(Modifier.testTag("viewerFilters").fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(listOf("All Elements", "Metals", "Nonmetals", "Metalloids", "Noble Gases")) { label ->
                ViewerAction({ filter = label }, Modifier.clip(CircleShape).background(if (filter == label)
                    Brush.linearGradient(listOf(Color(0xFF39D8F3), Color(0xFF0086FF))) else Brush.linearGradient(listOf(Color.White, Color(0xFFF1F8FF))))
                    .border(1.dp, Color.White, CircleShape).semantics { selected = filter == label }, contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)) {
                    Text(label, color = if (filter == label) Color.White else Ink, fontSize = 11.sp, lineHeight = 14.sp)
                }
            }
        }
        if (ordered.isEmpty()) {
            Column(Modifier.weight(1f).padding(24.dp)) {
                Text("No matching elements", color = Ink, fontSize = 20.sp)
                Text("Change the category or turn off favorites to browse more elements.", color = SoftInk)
            }
        } else {
            HorizontalPager(state = pager, reverseLayout = true, beyondViewportPageCount = 1,
                key = { ordered.getOrNull(it)?.number ?: it }, userScrollEnabled = !rotate,
                contentPadding = PaddingValues(horizontal = 13.dp), pageSpacing = 8.dp,
                modifier = Modifier.weight(1f).testTag("elementPager")) { page ->
                val element = ordered.getOrNull(page) ?: return@HorizontalPager
                val facts = remember(element.number) { elementFacts(element.number) }
                Column(Modifier.fillMaxSize().testTag("elementCard${element.number}").graphicsLayer {
                    val offset = ((pager.currentPage - page) + pager.currentPageOffsetFraction).coerceIn(-1f, 1f)
                    if (!reduced) { scaleX = 1f - abs(offset) * .035f; scaleY = scaleX; rotationY = offset * 5f
                        cameraDistance = 32 * density; shadowElevation = (5f + (1f - abs(offset)) * 5f) * density }
                    shape = RoundedCornerShape(24.dp); clip = true
                }.background(Brush.verticalGradient(listOf(Color(0xFFF7FCFF), Color(0xFFEDF7FF))))
                    .border(2.dp, Color.White, RoundedCornerShape(24.dp)).verticalScroll(rememberScrollState()).padding(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(.8f)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                            Text("Atomic Number", color = Ink, fontSize = 10.sp, lineHeight = 13.sp)
                            Text("${element.number}", color = Ink, fontSize = 28.sp, lineHeight = 30.sp, fontWeight = FontWeight.Black)
                        }
                        ViewerAction({ save(element) }, Modifier.testTag("saveElement${element.number}")
                            .clip(RoundedCornerShape(15.dp)).background(Color.White)
                            .semantics { contentDescription = "${if (element.number.toString() in favorites) "Remove" else "Add"} ${element.name} favorite" }) {
                            Text(if (element.number.toString() in favorites) "♥" else "♡", color = Color(0xFFED7389), fontSize = 27.sp, lineHeight = 30.sp)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(.9f)) {
                            Text(element.symbol, fontSize = 76.sp, lineHeight = 78.sp, fontWeight = FontWeight.Black,
                                style = TextStyle(brush = Brush.linearGradient(listOf(Color(0xFF80E8FF), Blue, Color(0xFF0059B8))), shadow = Shadow(Color(0x440078BF), Offset(2f, 5f), 6f)))
                            Text(element.name, color = Ink, fontSize = 27.sp, lineHeight = 30.sp, fontWeight = FontWeight.Black)
                            Text(if (element.number == 8) "E S S E N T I A L  F O R  L I F E" else "E X P L O R E  ·  D I S C O V E R", color = Ink, fontSize = 7.sp, lineHeight = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(Modifier.weight(1.1f)) {
                            ViewerAtom(element, Modifier.fillMaxWidth().height(130.dp), rotate, reduced || page != pager.settledPage)
                            val molecule = remember(element.number) { moleculeLibrary.firstOrNull { it.name == element.name } }
                            if (molecule != null) Text(molecule.formula + " · " + molecule.geometry, color = SoftInk, fontSize = 8.sp, lineHeight = 11.sp)
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        ViewerBadge(facts.groupBlock, Color(0xFFDDF6FF), Blue, "category", Modifier.weight(1.3f))
                        ViewerBadge(facts.standardState, Color(0xFFFFE7ED), Color(0xFFD74F73), "gas", Modifier.weight(1f))
                        ViewerAction({ rotate = !rotate }, Modifier.weight(.9f), contentPadding = PaddingValues(2.dp)) {
                            Text(if (rotate) "Done rotating" else "Rotate / zoom", color = Blue, fontSize = 9.sp, lineHeight = 12.sp)
                        }
                    }
                    if (rotate) Text("Drag atom to rotate · Pinch to zoom", color = SoftInk, fontSize = 10.sp, lineHeight = 13.sp)
                    ViewerPropertyTile("Atomic mass", "${facts.atomicMass} u", "atom", Modifier.fillMaxWidth())
                    Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ViewerPropertyTile("Electron configuration", facts.electronConfiguration, "shell", Modifier.weight(1.35f))
                        ViewerPropertyTile("Oxidation states", facts.oxidationStates, "bond", Modifier.weight(1f))
                    }
                    Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ViewerMetricTile("Electronegativity", facts.electronegativity, "bolt", "Pauling scale", Modifier.weight(1f))
                        ViewerMetricTile("Melting point", viewerTemperature(facts.meltingPointK), "snow", "", Modifier.weight(1f))
                        ViewerMetricTile("Boiling point", viewerTemperature(facts.boilingPointK), "heat", "", Modifier.weight(1f))
                    }
                    ViewerUsesSection(element)
                    Row(Modifier.fillMaxWidth().padding(top = 5.dp)) {
                        repeat(4) { index ->
                            val title = listOf("Overview", "Atomic Structure", "Properties", "Uses")[index]
                            ViewerAction({ tab = index }, Modifier.weight(1f).semantics { selected = tab == index; role = Role.Tab }, contentPadding = PaddingValues(2.dp)) {
                                Text(title, color = if (tab == index) Blue else SoftInk, fontWeight = if (tab == index) FontWeight.Bold else FontWeight.Normal, fontSize = 10.sp, lineHeight = 13.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                    val detail = when (tab) {
                        1 -> "Neutral atom: ${element.number} protons and ${element.number} electrons. Shell populations: ${viewerAtomicModel(element.number).shells.joinToString(" · ")}. Stylized shell model: electrons occupy quantum orbitals, not fixed paths."
                        2 -> "Density: ${facts.densityGcm3} g/cm³. Atomic radius: ${facts.atomicRadiusPm} pm. Ionization energy: ${facts.ionizationEnergyEv} eV. Electron affinity: ${facts.electronAffinityEv} eV. Unknown values are marked —."
                        3 -> viewerApplications(element)
                        else -> "${element.name} (${element.symbol}) · ${facts.groupBlock}. Discovered: ${facts.yearDiscovered}. Swipe right for the next element, left for the previous."
                    }
                    Text(detail, color = Ink, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(3.dp))
                }
            }
            val current = pager.currentPage.coerceIn(0, ordered.lastIndex)
            Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                ViewerAction({ jump(current - 1) }, Modifier.weight(1f), enabled = current > 0) { Text(if (current > 0) "‹ ${ordered[current - 1].name}" else "First element", color = Blue, fontSize = 10.sp, lineHeight = 13.sp) }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { repeat(4) { Box(Modifier.size(if (it == current % 4) 6.dp else 4.dp).background(if (it == current % 4) Blue else Color(0xFFB9D5EC), CircleShape)) } }
                ViewerAction({ jump(current + 1) }, Modifier.weight(1f), enabled = current < ordered.lastIndex) { Text(if (current < ordered.lastIndex) "${ordered[current + 1].name} ›" else "Last element", color = Blue, fontSize = 10.sp, lineHeight = 13.sp) }
            }
        }
    }
    if (searchOpen) {
        val matches = remember(ordered, query) { ordered.filter { viewerMatches(it, "All Elements", query) } }
        AlertDialog(onDismissRequest = { searchOpen = false }, containerColor = Color.White,
            title = { Text("Jump to element", color = Ink) }, text = {
                Column {
                    OutlinedTextField(query, { query = it }, label = { Text("Name, symbol or number") }, singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Ink, unfocusedTextColor = Ink))
                    LazyColumn(Modifier.heightIn(max = 300.dp)) {
                        if (matches.isEmpty()) item { Text("No matching elements", color = SoftInk) }
                        items(matches, key = { it.number }) { item ->
                            ViewerAction({ jump(ordered.indexOfFirst { it.number == item.number }); searchOpen = false }, Modifier.fillMaxWidth()) { Text("${item.number}  ${item.symbol}  ${item.name}", color = Ink, modifier = Modifier.fillMaxWidth()) }
                        }
                    }
                }
            }, confirmButton = { ViewerAction({ searchOpen = false }) { Text("Done", color = Blue) } })
    }
}

@Composable
private fun ViewerBadge(label: String, background: Color, foreground: Color, icon: String, modifier: Modifier) {
    Row(modifier.clip(CircleShape).background(background).padding(horizontal = 7.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        ViewerIcon(icon, Modifier.size(16.dp), foreground)
        Text(label, color = foreground, fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ViewerPropertyTile(label: String, value: String, icon: String, modifier: Modifier) {
    Row(modifier.semantics(mergeDescendants = true) {}.clip(RoundedCornerShape(16.dp)).background(Color.White.copy(.8f))
        .border(1.dp, Color.White, RoundedCornerShape(16.dp)).padding(9.dp), verticalAlignment = Alignment.CenterVertically) {
        ViewerIcon(icon, Modifier.size(26.dp))
        Column(Modifier.weight(1f).padding(start = 7.dp)) {
            Text(label, color = SoftInk, fontSize = 9.sp, lineHeight = 12.sp)
            Text(value, color = Ink, fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ViewerMetricTile(label: String, value: String, icon: String, note: String, modifier: Modifier) {
    Column(modifier.semantics(mergeDescendants = true) {}.clip(RoundedCornerShape(16.dp)).background(Color.White.copy(.8f)).padding(8.dp)) {
        ViewerIcon(icon, Modifier.size(25.dp), if (icon == "bolt") Color(0xFFFFBF00) else Blue)
        Text(label, color = SoftInk, fontSize = 8.sp, lineHeight = 11.sp)
        Text(value, color = Ink, fontSize = 13.sp, lineHeight = 17.sp, fontWeight = FontWeight.Bold)
        if (note.isNotEmpty()) Text(note, color = SoftInk, fontSize = 7.sp, lineHeight = 10.sp)
    }
}

@Composable
private fun ViewerUsesSection(element: Element) {
    val compounds = remember(element.number) { validatedCompoundsFor(element) }
    Column(Modifier.fillMaxWidth().padding(top = 7.dp).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(.7f)).padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { ViewerIcon("bulb", Modifier.size(20.dp), Color(0xFFFFB600)); Text(if (element.number == 8) "Common Uses" else "From the chemistry library", color = Ink, fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold) }
        if (element.number == 8) {
            val strip = ImageBitmap.imageResource(R.drawable.viewer_uses_triptych)
            Row(Modifier.padding(top = 5.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                listOf("Breathing" to "Supports respiration", "Medical" to "Oxygen therapy", "Combustion" to "Supports burning").forEachIndexed { index, (title, subtitle) ->
                    Column(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Color.White)) {
                        Canvas(Modifier.fillMaxWidth().height(43.dp)) {
                            val panelWidth = strip.width / 3
                            val cropHeight = (panelWidth * size.height / size.width).roundToInt().coerceIn(1, strip.height)
                            drawImage(strip, srcOffset = IntOffset(index * panelWidth, (strip.height - cropHeight) / 2),
                                srcSize = IntSize(panelWidth, cropHeight), dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()))
                        }
                        Row(Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
                            ViewerIcon(listOf("lungs", "medical", "fire")[index], Modifier.size(18.dp), listOf(Blue, Blue, Color(0xFFFF5C35))[index])
                            Column(Modifier.padding(start = 4.dp)) {
                                Text(title, color = Ink, fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.Bold)
                                Text(subtitle, color = SoftInk, fontSize = 7.sp, lineHeight = 10.sp)
                            }
                        }
                    }
                }
            }
        } else {
            Row(Modifier.fillMaxWidth().padding(top = 5.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                (if (compounds.isEmpty()) listOf("Atomic structure", "Properties", "Discovery") else compounds.take(3)).forEach { label ->
                    Column(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Color(0xFFEDF8FF)).padding(7.dp)) {
                        ViewerIcon("bond", Modifier.size(24.dp)); Text(label, color = Ink, fontSize = 10.sp, lineHeight = 14.sp)
                    }
                }
            }
        }
    }
}

// Vector icons are rendered locally at any density, rather than depending on emoji fonts.
@Composable
private fun ViewerIcon(kind: String, modifier: Modifier, tint: Color = Blue) {
    Canvas(modifier) {
        val w = size.width; val h = size.height; val c = center; val stroke = max(1f, w * .06f)
        fun line(a: Offset, b: Offset) = drawLine(tint, a, b, stroke, StrokeCap.Round)
        when (kind) {
            "atom", "category" -> { repeat(3) { rotate(it * 60f) { drawOval(tint, Offset(w * .08f, h * .32f), Size(w * .84f, h * .36f), style = Stroke(stroke)) } }; drawCircle(tint, w * .08f, c) }
            "search" -> { drawCircle(tint, w * .28f, Offset(w * .4f, h * .4f), style = Stroke(stroke)); line(Offset(w * .62f, h * .62f), Offset(w * .92f, h * .92f)) }
            "shell" -> repeat(3) { drawOval(tint, Offset(w * .12f, h * (.17f + it * .22f)), Size(w * .76f, h * .25f), style = Stroke(stroke)) }
            "bond" -> { val points = listOf(Offset(w * .5f, h * .2f), Offset(w * .18f, h * .78f), Offset(w * .82f, h * .78f)); points.forEach { line(c, it); drawCircle(tint, w * .12f, it) } }
            "snow" -> repeat(3) { rotate(it * 60f) { line(Offset(w * .5f, h * .05f), Offset(w * .5f, h * .95f)); line(Offset(w * .3f, h * .2f), Offset(w * .5f, h * .35f)); line(Offset(w * .7f, h * .2f), Offset(w * .5f, h * .35f)) } }
            "heat" -> repeat(3) { i -> val p = Path().apply { moveTo(w * (.25f + i * .25f), h * .9f); cubicTo(w * (.05f + i * .25f), h * .65f, w * (.45f + i * .25f), h * .4f, w * (.25f + i * .25f), h * .1f) }; drawPath(p, tint, style = Stroke(stroke)) }
            "bolt" -> drawPath(Path().apply { moveTo(w * .6f, 0f); lineTo(w * .15f, h * .58f); lineTo(w * .48f, h * .58f); lineTo(w * .35f, h); lineTo(w * .88f, h * .4f); lineTo(w * .58f, h * .4f); close() }, tint)
            "medical" -> { drawCircle(tint, w * .46f, c); drawRect(Color.White, Offset(w * .43f, h * .2f), Size(w * .14f, h * .6f)); drawRect(Color.White, Offset(w * .2f, h * .43f), Size(w * .6f, h * .14f)) }
            "lungs" -> { line(Offset(w * .5f, h * .05f), Offset(w * .5f, h * .55f)); drawOval(tint, Offset(w * .08f, h * .25f), Size(w * .32f, h * .65f), style = Stroke(stroke)); drawOval(tint, Offset(w * .6f, h * .25f), Size(w * .32f, h * .65f), style = Stroke(stroke)); line(c, Offset(w * .3f, h * .5f)); line(c, Offset(w * .7f, h * .5f)) }
            "fire" -> drawPath(Path().apply { moveTo(w * .5f, 0f); cubicTo(w * .9f, h * .3f, w, h, w * .5f, h); cubicTo(0f, h, w * .1f, h * .4f, w * .5f, 0f); close() }, tint)
            "bulb" -> { drawCircle(tint, w * .31f, Offset(w * .5f, h * .36f)); drawRect(tint, Offset(w * .36f, h * .6f), Size(w * .28f, h * .3f)) }
            else -> { drawCircle(tint, w * .23f, Offset(w * .33f, h * .56f), style = Stroke(stroke)); drawCircle(tint, w * .25f, Offset(w * .62f, h * .42f), style = Stroke(stroke)); line(Offset(w * .12f, h * .8f), Offset(w * .86f, h * .8f)) }
        }
    }
}
internal fun viewerApplications(element: Element): String {
    if (element.number == 8) return "Oxygen supports respiration and combustion and is used in oxygen therapy. Source: Royal Society of Chemistry, https://periodic-table.rsc.org/element/8/oxygen and the existing oxygen molecular lesson."
    val compounds = validatedCompoundsFor(element)
    return "Element-specific uses are not included in the bundled property dataset." +
        if (compounds.isEmpty()) " No compound examples are available in the existing library."
        else "\nCompound examples: ${compounds.joinToString(", ")}. These are compounds, not uses of the pure element."
}

private data class ViewerPoint3(val x: Float, val y: Float, val z: Float)
internal data class ViewerAtomicModel(val shells: List<Int>) {
    private val guides = shells.mapIndexed { shell, _ ->
        val radius = .61f + .39f * (shell + 1) / shells.size
        val inclination = .38f + shell * .49f
        List(181) { step -> val angle = step * (2 * PI / 180).toFloat()
            ViewerPoint3(cos(angle) * radius, sin(angle) * radius * cos(inclination), sin(angle) * radius * sin(inclination)) }
    }
    internal fun guide(shell: Int, visit: (Float, Float, Float) -> Unit) { guides[shell].forEach { visit(it.x, it.y, it.z) } }
}
private val viewerAtomicModels = java.util.concurrent.ConcurrentHashMap<Int, ViewerAtomicModel>()
internal fun viewerAtomicModel(number: Int): ViewerAtomicModel = viewerAtomicModels.getOrPut(number) { ViewerAtomicModel(electronShellDistribution(number)) }

@Composable
private fun viewerReducedMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    fun disabled() = android.provider.Settings.Global.getFloat(resolver, android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    var systemReduced by remember(resolver) { mutableStateOf(disabled()) }
    DisposableEffect(resolver) {
        val observer = object : android.database.ContentObserver(android.os.Handler(android.os.Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) { systemReduced = disabled() }
        }
        resolver.registerContentObserver(android.provider.Settings.Global.getUriFor(android.provider.Settings.Global.ANIMATOR_DURATION_SCALE), false, observer)
        onDispose { resolver.unregisterContentObserver(observer) }
    }
    return LocalReducedMotion.current || systemReduced
}

@Composable
private fun ViewerAtom(element: Element, modifier: Modifier, interactive: Boolean = true, reducedMotion: Boolean = false) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val active = lifecycleState.isAtLeast(Lifecycle.State.RESUMED)
    var yaw by rememberSaveable(element.number) { mutableFloatStateOf(.4f) }
    var tilt by rememberSaveable(element.number) { mutableFloatStateOf(.25f) }
    var zoom by rememberSaveable(element.number) { mutableFloatStateOf(1f) }
    var paused by rememberSaveable(element.number) { mutableStateOf(false) }
    var phase by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(paused, reducedMotion, active) {
        var last = 0L
        while (!paused && !reducedMotion && active) {
            withFrameNanos { now -> if (last != 0L) phase += ((now - last) / 1e9f) * .4f; last = now }
        }
    }
    val model = remember(element.number) { viewerAtomicModel(element.number) }
    val shells = model.shells
    val guidePaths = remember(model) { List(shells.size) { Path() } }
    val guideProjection = remember(model) { FloatArray(5) { Float.NaN } }
    val labelPaint = remember { android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE; textAlign = android.graphics.Paint.Align.CENTER; typeface = android.graphics.Typeface.DEFAULT_BOLD
    } }
    val electronColors = remember { listOf(Color.White, Color(0xFF44DDFF), Color(0xFF0089D8), Color(0xFF00428F)) }
    Canvas(modifier.semantics { contentDescription = "${element.name} shell model, ${element.number} electrons; shell populations ${shells.joinToString()}. Schematic, not fixed electron paths."
        customActions = listOf(
            CustomAccessibilityAction("Rotate atom left") { yaw -= .25f; true },
            CustomAccessibilityAction("Rotate atom right") { yaw += .25f; true },
            CustomAccessibilityAction("Zoom in") { zoom = (zoom * 1.15f).coerceAtMost(1.65f); true },
            CustomAccessibilityAction("Zoom out") { zoom = (zoom / 1.15f).coerceAtLeast(.65f); true },
            CustomAccessibilityAction(if (paused) "Resume atom" else "Pause atom") { paused = !paused; true }) }
        .pointerInput(element.number, interactive) {
            if (interactive) detectTransformGestures { _, pan, scale, _ -> yaw += pan.x * .012f; tilt = (tilt + pan.y * .012f).coerceIn(-1.5f, 1.5f); zoom = (zoom * scale).coerceIn(.65f, 1.65f) }
        }.then(if (interactive) Modifier.clickable { paused = !paused } else Modifier)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) * .44f * zoom
        val yawCos = cos(yaw); val yawSin = sin(yaw)
        val tiltCos = cos(tilt); val tiltSin = sin(tilt)
        val rebuildGuides = guideProjection[0] != size.width || guideProjection[1] != size.height ||
            guideProjection[2] != yaw || guideProjection[3] != tilt || guideProjection[4] != zoom
        if (rebuildGuides) {
            guideProjection[0] = size.width; guideProjection[1] = size.height
            guideProjection[2] = yaw; guideProjection[3] = tilt; guideProjection[4] = zoom
        }
        fun project(x: Float, y: Float, z: Float): Pair<Offset, Float> {
            val xx = x * yawCos + z * yawSin
            val zz = -x * yawSin + z * yawCos
            val yy = y * tiltCos - zz * tiltSin
            val depth = y * tiltSin + zz * tiltCos
            val perspective = 1f / (1f - depth * .18f)
            return Pair(center + Offset(xx, yy) * radius * perspective, depth)
        }
        data class Electron(val point: Offset, val depth: Float)
        val electrons = mutableListOf<Electron>()
        drawOval(Brush.radialGradient(listOf(Color(0x4438BDFF), Color.Transparent), center + Offset(0f, radius), radius),
            topLeft = center + Offset(-radius, radius * .8f), size = androidx.compose.ui.geometry.Size(radius * 2, radius * .3f))
        shells.forEachIndexed { shell, count ->
            val r = .61f + .39f * (shell + 1) / shells.size
            val inclination = .38f + shell * .49f
            val path = guidePaths[shell]
            if (rebuildGuides) {
                path.reset()
                var first = true
                model.guide(shell) { x, y, z ->
                    val point = project(x, y, z).first
                    if (first) { path.moveTo(point.x, point.y); first = false } else path.lineTo(point.x, point.y)
                }
            }
            drawPath(path, Color(0xFFB5C8DE), style = Stroke(2.5f))
            drawPath(path, Color.White.copy(.85f), style = Stroke(.9f))
            repeat(count) { i ->
                val a = phase * (1f - shell * .08f) + i * (2 * PI / count).toFloat()
                val p = project(cos(a) * r, sin(a) * r * cos(inclination), sin(a) * r * sin(inclination))
                electrons += Electron(p.first, p.second)
            }
        }
        fun sphere(p: Offset, r: Float, colors: List<Color>) {
            drawCircle(Brush.radialGradient(colors, p - Offset(r * .36f, r * .4f), r * 1.4f), r, p)
            drawCircle(Color.White.copy(.8f), r, p, style = Stroke(1.3f))
            drawOval(Color.White.copy(.8f), p - Offset(r * .54f, r * .67f), androidx.compose.ui.geometry.Size(r * .65f, r * .3f))
        }
        val blue = electronColors
        electrons.filter { it.depth < 0 }.sortedBy { it.depth }.forEach { sphere(it.point, radius * (.065f + it.depth * .009f), blue) }
        drawCircle(Brush.radialGradient(listOf(Color(0x77FF9EAD), Color.Transparent), center, radius * .58f), radius * .58f, center)
        sphere(center, radius * .34f, listOf(Color.White, Color(0xFFFF9CAA), Color(0xFFF22E51), Color(0xFF990B31)))
        electrons.filter { it.depth >= 0 }.sortedBy { it.depth }.forEach { sphere(it.point, radius * (.065f + it.depth * .009f), blue) }
        drawContext.canvas.nativeCanvas.apply {
            labelPaint.textSize = radius * .32f
            drawText(element.symbol, center.x, center.y + radius * .11f, labelPaint)
        }
    }
}
