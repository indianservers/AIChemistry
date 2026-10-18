package com.indianservers.aichemistryexplorer

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import kotlinx.coroutines.launch

private val Ink = Color(0xFF081840)
private val Blue = Color(0xFF008EEB)
private val SoftInk = Color(0xFF76869C)

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

@Composable
internal fun ElementsViewerScreen(elements: List<Element>, onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("elements_viewer", 0) }
    val ordered = remember(elements) { elements.sortedBy { it.number } }
    val pager = rememberPagerState(initialPage = prefs.getInt("selected", 8).coerceIn(1, ordered.size) - 1) { ordered.size }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val reduced = LocalReducedMotion.current || remember(context) {
        android.provider.Settings.Global.getFloat(context.contentResolver, android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    var favorites by remember { mutableStateOf(prefs.getStringSet("favorites", emptySet()).orEmpty().toSet()) }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("All Elements") }
    var onlyFavorites by rememberSaveable { mutableStateOf(false) }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var rotate by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(pager) {
        snapshotFlow { pager.settledPage }.collect { page ->
            prefs.edit().putInt("selected", ordered[page].number).apply()
            if (!reduced) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }
    fun jump(page: Int) {
        if (page !in ordered.indices) return
        scope.launch {
            if (reduced) pager.scrollToPage(page)
            else pager.animateScrollToPage(page, animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = .9f))
        }
    }
    Column(Modifier.fillMaxSize().background(Color(0xFFFAFCFF))) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack, modifier = Modifier.semantics { contentDescription = "Back" }) { Text("‹", color = Blue, fontSize = 30.sp) }
            Column(Modifier.weight(1f)) {
                Text("Elements", color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text("Element %03d / %03d".format(ordered[pager.currentPage].number, ordered.size), color = SoftInk, fontSize = 11.sp)
            }
            TextButton(onClick = { searchOpen = true; onlyFavorites = false }) { Text("Search", color = Blue) }
            TextButton(onClick = { searchOpen = true; onlyFavorites = true }, modifier = Modifier.semantics { contentDescription = "Browse favorites" }) { Text("♡", color = Blue, fontSize = 24.sp) }
        }
        HorizontalPager(
            state = pager, reverseLayout = true, beyondViewportPageCount = 1,
            key = { ordered[it].number }, userScrollEnabled = !rotate,
            contentPadding = PaddingValues(horizontal = 14.dp), pageSpacing = 8.dp,
            modifier = Modifier.weight(1f).testTag("elementPager")
        ) { page ->
            val element = ordered[page]
            val facts = remember(element.number) { elementFacts(element.number) }
            val offset = ((pager.currentPage - page) + pager.currentPageOffsetFraction).coerceIn(-1f, 1f)
            Column(Modifier.fillMaxSize().graphicsLayer {
                if (!reduced) {
                    scaleX = 1f - abs(offset) * .035f; scaleY = scaleX
                    rotationY = offset * 5f; cameraDistance = 32 * density
                    shadowElevation = (5f + (1f - abs(offset)) * 5f) * density
                }
                shape = RoundedCornerShape(22.dp); clip = true
            }.background(Color.White).verticalScroll(rememberScrollState()).padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(element.symbol, color = Blue, fontSize = 54.sp, fontWeight = FontWeight.Black)
                        Text(element.name, color = Ink, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        Text(facts.groupBlock + " · " + facts.standardState, color = SoftInk, fontSize = 11.sp)
                        Text("${element.number}  ·  ${facts.atomicMass} u", color = Ink, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                    ViewerAtom(element, Modifier.weight(1f).height(150.dp), rotate, reduced || page != pager.settledPage)
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { rotate = !rotate }) { Text(if (rotate) "Done rotating" else "Rotate / zoom", color = Blue, fontSize = 12.sp) }
                    TextButton(onClick = {
                        val key = element.number.toString()
                        favorites = if (key in favorites) favorites - key else favorites + key
                        prefs.edit().putStringSet("favorites", favorites).apply()
                    }, modifier = Modifier.semantics { contentDescription = "${if (element.number.toString() in favorites) "Remove" else "Add"} ${element.name} favorite" }) {
                        Text(if (element.number.toString() in favorites) "♥ Saved" else "♡ Save", color = Blue, fontSize = 12.sp)
                    }
                }
                if (rotate) Text("Drag atom to rotate · Pinch to zoom · Done to resume swiping", color = SoftInk, fontSize = 10.sp)
                val properties = remember(element.number) { listOf(
                    "Atomic number" to element.number.toString(), "Atomic mass" to "${facts.atomicMass} u",
                    "Electron configuration" to facts.electronConfiguration, "Oxidation states" to facts.oxidationStates,
                    "Electronegativity" to facts.electronegativity, "Density" to (if (facts.densityGcm3 == "—") "Unknown" else "${facts.densityGcm3} g/cm³"),
                    "Melting point" to viewerTemperature(facts.meltingPointK), "Boiling point" to viewerTemperature(facts.boilingPointK)
                ) }
                properties.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth().padding(top = 5.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEach { (label, value) ->
                            Column(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF1F7FC)).padding(horizontal = 10.dp, vertical = 7.dp)) {
                                Text(label, color = SoftInk, fontSize = 10.sp)
                                Text(value, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                LazyRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    items(4) { index ->
                        val title = listOf("Overview", "Atomic Structure", "Properties", "Uses")[index]
                        TextButton(onClick = { tab = index }) { Text(title, color = if (tab == index) Blue else SoftInk, fontWeight = if (tab == index) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp) }
                    }
                }
                val detail = when (tab) {
                    1 -> "Neutral atom: ${element.number} protons and ${element.number} electrons. Shell populations: ${remember(element.number) { electronShellDistribution(element.number) }.joinToString(" · ")}. This is a stylized shell model; electrons occupy quantum orbitals, not fixed paths."
                    2 -> "Standard state: ${facts.standardState}. Atomic radius: ${facts.atomicRadiusPm} pm. Ionization energy: ${facts.ionizationEnergyEv} eV. Electron affinity: ${facts.electronAffinityEv} eV. Unknown values are marked —."
                    3 -> viewerUses(element).joinToString("\n") { "${it.second}: ${it.third}" }
                    else -> "${element.name} (${element.symbol}) · ${facts.groupBlock}. Discovered: ${facts.yearDiscovered}. Swipe right for the next atomic number, left for the previous."
                }
                Text(detail, color = Ink, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
            }
        }
        val current = pager.currentPage
        Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { jump(current - 1) }, enabled = current > 0, modifier = Modifier.weight(1f)) {
                Text(if (current > 0) "‹ ${ordered[current - 1].name}" else "First element", color = if (current > 0) Blue else SoftInk, fontSize = 11.sp)
            }
            Text("${current + 1} / ${ordered.size}", color = SoftInk, fontSize = 11.sp)
            TextButton(onClick = { jump(current + 1) }, enabled = current < ordered.lastIndex, modifier = Modifier.weight(1f)) {
                Text(if (current < ordered.lastIndex) "${ordered[current + 1].name} ›" else "Last element", color = if (current < ordered.lastIndex) Blue else SoftInk, fontSize = 11.sp)
            }
        }
    }
    if (searchOpen) {
        val matches = remember(query, filter, onlyFavorites, favorites) { ordered.filter {
            viewerMatches(it, filter, query) && (!onlyFavorites || it.number.toString() in favorites)
        } }
        AlertDialog(onDismissRequest = { searchOpen = false }, containerColor = Color.White,
            title = { Text(if (onlyFavorites) "Saved elements" else "Jump to element", color = Ink) },
            text = {
                Column {
                    OutlinedTextField(query, { query = it }, label = { Text("Name, symbol or number") }, singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Ink, unfocusedTextColor = Ink))
                    LazyRow { items(listOf("All Elements", "Metals", "Nonmetals", "Metalloids", "Noble Gases")) { label ->
                        TextButton(onClick = { filter = label }) { Text(label, color = if (filter == label) Blue else SoftInk, fontSize = 11.sp) }
                    } }
                    LazyColumn(Modifier.heightIn(max = 300.dp)) {
                        if (matches.isEmpty()) item { Text("No matching elements", color = SoftInk) }
                        items(matches, key = { it.number }) { item ->
                            TextButton(onClick = { jump(item.number - 1); searchOpen = false }, modifier = Modifier.fillMaxWidth()) {
                                Text("${item.number}  ${item.symbol}  ${item.name}", color = Ink, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }, confirmButton = { TextButton(onClick = { searchOpen = false }) { Text("Done", color = Blue) } })
    }
}

@Composable
private fun GlassPanel(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().shadow(10.dp, RoundedCornerShape(26.dp), ambientColor = Color(0xFF99BCD5), spotColor = Color(0xFFB7D4EA))
        .clip(RoundedCornerShape(26.dp)).background(Color.White.copy(.56f)).border(1.dp, Color.White.copy(.95f), RoundedCornerShape(26.dp)).padding(13.dp), content = content)
}

@Composable
private fun ViewerBadge(label: String, background: Color, foreground: Color) {
    Text(label, color = foreground, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.clip(CircleShape).background(background).padding(horizontal = 13.dp, vertical = 8.dp))
}

@Composable
private fun ViewerProperty(icon: String, label: String, value: String, modifier: Modifier) {
    Row(modifier.clip(RoundedCornerShape(18.dp)).background(Color.White.copy(.7f)).padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, color = Blue, fontSize = 26.sp, modifier = Modifier.padding(end = 9.dp))
        Column { Text(label, color = SoftInk, fontSize = 10.sp); Text(value, color = Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun ViewerMetric(icon: String, label: String, value: String, note: String, modifier: Modifier) {
    Column(modifier.heightIn(min = 112.dp).clip(RoundedCornerShape(18.dp)).background(Color.White.copy(.75f)).padding(9.dp)) {
        Text(icon, color = Blue, fontSize = 26.sp)
        Text(label, color = SoftInk, fontSize = 9.sp)
        Text(value, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        if (note.isNotEmpty()) Text(note, color = SoftInk, fontSize = 8.sp)
    }
}

private fun viewerUses(element: Element): List<Triple<String, String, String>> = when (element.symbol) {
    "O" -> listOf(Triple("🌿", "Breathing", "Essential for life"), Triple("✚", "Medical", "Supports healthcare"), Triple("🔥", "Combustion", "Supports burning"))
    "H" -> listOf(Triple("🚀", "Fuel", "Rocket propellant"), Triple("🌱", "Fertilizer", "Ammonia production"), Triple("⚡", "Energy", "Fuel cells"))
    "C" -> listOf(Triple("✎", "Graphite", "Pencils and electrodes"), Triple("💎", "Diamond", "Cutting tools"), Triple("⚙", "Materials", "Steel alloys"))
    "Fe" -> listOf(Triple("🏗", "Steel", "Buildings and bridges"), Triple("⚙", "Machines", "Tools and engines"), Triple("🧲", "Magnets", "Magnetic materials"))
    "Cu" -> listOf(Triple("⚡", "Wiring", "Electrical conductor"), Triple("♨", "Heat", "Heat exchangers"), Triple("⚙", "Alloys", "Brass and bronze"))
    "Si" -> listOf(Triple("▦", "Electronics", "Semiconductors"), Triple("☀", "Solar cells", "Photovoltaic energy"), Triple("◇", "Compounds", "Glass and ceramics"))
    "He" -> listOf(Triple("❄", "Cooling", "Cryogenic systems"), Triple("🎈", "Lifting", "Balloons"), Triple("⚙", "Shielding", "Inert atmosphere"))
    else -> if (element.number >= 104) listOf(Triple("⚛", "Research", "Nuclear synthesis"), Triple("◷", "Decay", "Short-lived isotopes"), Triple("⌕", "Discovery", "Atomic structure"))
        else listOf(Triple("⚛", "Research", "Element properties"), Triple("⌕", "Analysis", "Characteristic spectra"), Triple("◎", "Chemistry", "Compounds and reactions"))
}

@Composable
private fun ViewerAtom(element: Element, modifier: Modifier, interactive: Boolean = true, reducedMotion: Boolean = false) {
    var yaw by remember(element.number) { mutableFloatStateOf(.4f) }
    var tilt by remember(element.number) { mutableFloatStateOf(.25f) }
    var zoom by remember(element.number) { mutableFloatStateOf(1f) }
    var paused by remember { mutableStateOf(false) }
    var phase by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(paused, reducedMotion) {
        var last = 0L
        while (!paused && !reducedMotion) {
            withFrameNanos { now -> if (last != 0L) phase += ((now - last) / 1e9f) * .4f; last = now }
        }
    }
    val shells = remember(element.number) { electronShellDistribution(element.number) }
    Canvas(modifier.semantics { contentDescription = "Interactive 3D ${element.name} atom, ${element.number} electrons" }
        .pointerInput(element.number, interactive) {
            if (interactive) detectTransformGestures { _, pan, scale, _ -> yaw += pan.x * .012f; tilt = (tilt + pan.y * .012f).coerceIn(-1.5f, 1.5f); zoom = (zoom * scale).coerceIn(.65f, 1.65f) }
        }.then(if (interactive) Modifier.clickable { paused = !paused } else Modifier)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) * .44f * zoom
        fun project(x: Float, y: Float, z: Float): Pair<Offset, Float> {
            val xx = x * cos(yaw) + z * sin(yaw)
            val zz = -x * sin(yaw) + z * cos(yaw)
            val yy = y * cos(tilt) - zz * sin(tilt)
            val depth = y * sin(tilt) + zz * cos(tilt)
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
            val path = Path()
            repeat(181) { step ->
                val a = step * (2 * PI / 180).toFloat()
                val p = project(cos(a) * r, sin(a) * r * cos(inclination), sin(a) * r * sin(inclination)).first
                if (step == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
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
        val blue = listOf(Color.White, Color(0xFF44DDFF), Color(0xFF0089D8), Color(0xFF00428F))
        electrons.filter { it.depth < 0 }.sortedBy { it.depth }.forEach { sphere(it.point, radius * (.065f + it.depth * .009f), blue) }
        drawCircle(Brush.radialGradient(listOf(Color(0x77FF9EAD), Color.Transparent), center, radius * .58f), radius * .58f, center)
        sphere(center, radius * .34f, listOf(Color.White, Color(0xFFFF9CAA), Color(0xFFF22E51), Color(0xFF990B31)))
        electrons.filter { it.depth >= 0 }.sortedBy { it.depth }.forEach { sphere(it.point, radius * (.065f + it.depth * .009f), blue) }
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.WHITE; textSize = radius * .32f; textAlign = android.graphics.Paint.Align.CENTER; typeface = android.graphics.Typeface.DEFAULT_BOLD }
            drawText(element.symbol, center.x, center.y + radius * .11f, paint)
        }
    }
}
