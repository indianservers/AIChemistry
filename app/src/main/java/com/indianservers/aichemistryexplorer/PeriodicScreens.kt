package com.indianservers.aichemistryexplorer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.res.Configuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

private data class PeriodicTheme(val name: String, val colors: List<Color>)

private val periodicThemes = listOf(
    PeriodicTheme("Classic", emptyList()),
    PeriodicTheme("Neon", listOf(Color(0xFF00F5FF), Color(0xFF39FF88), Color(0xFFFFE600), Color(0xFFFF4FD8), Color(0xFF8B5CFF))),
    PeriodicTheme("Cyber", listOf(Color(0xFF00E5FF), Color(0xFF006CFF), Color(0xFF9C4DFF), Color(0xFFFF3CAC), Color(0xFFFFB000))),
    PeriodicTheme("Aurora", listOf(Color(0xFF43E8D8), Color(0xFF65F09A), Color(0xFFA7F06A), Color(0xFF79A7FF), Color(0xFFC77DFF))),
    PeriodicTheme("Plasma", listOf(Color(0xFFFF3D81), Color(0xFFFF6B35), Color(0xFFFFC857), Color(0xFFAE5BFF), Color(0xFF6C4DFF))),
    PeriodicTheme("Arctic", listOf(Color(0xFFE8FBFF), Color(0xFF83DFFF), Color(0xFF42B5FF), Color(0xFF738BFF), Color(0xFFA98BFF))),
    PeriodicTheme("Solar", listOf(Color(0xFFFFF275), Color(0xFFFFC247), Color(0xFFFF8A3D), Color(0xFFFF516D), Color(0xFFE650FF))),
    PeriodicTheme("Matrix", listOf(Color(0xFFB9FF66), Color(0xFF62FF75), Color(0xFF00E676), Color(0xFF00C853), Color(0xFF00A884)))
)

private val periodicCategories = listOf(
    "Non-metal", "Noble gas", "Alkali metal", "Alkaline earth", "Metalloid",
    "Halogen", "Post-transition", "Transition metal", "Lanthanide", "Actinide"
)

private fun themedColor(element: Element, theme: PeriodicTheme): Color {
    if (theme.colors.isEmpty()) return element.color
    val index = periodicCategories.indexOf(element.category).coerceAtLeast(0)
    return theme.colors[index % theme.colors.size]
}

private fun tablePeriod(number: Int): Int = periodicPeriod(number)

private fun tableGroup(number: Int): Int = periodicGroup(number) ?: 0

private fun highlightOpacity(element: Element, focused: Element?, mode: String): Float {
    if (focused == null || mode == "Off") return 1f
    val matches = when (mode) {
        "Family" -> element.category == focused.category
        "Period" -> tablePeriod(element.number) == tablePeriod(focused.number)
        "Group" -> {
            val focusedGroup = tableGroup(focused.number)
            if (focusedGroup == 0) element.category == focused.category
            else tableGroup(element.number) == focusedGroup
        }
        else -> true
    }
    return if (matches) 1f else .22f
}

private fun Modifier.twoFingerZoom(onZoom: (Float) -> Unit): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Final)
        do {
            val event = awaitPointerEvent(PointerEventPass.Final)
            if (event.changes.count { it.pressed } >= 2) {
                val change = event.calculateZoom()
                if (change != 1f) {
                    onZoom(change)
                }
            }
        } while (event.changes.any { it.pressed })
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

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
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            HubAction("✦", "Discovery", "Visualize & learn", Cyan, Modifier.weight(1f)) { onOpen(Screen.PeriodicStudio) }
            HubAction("◆", "Daily", "Play & earn XP", Orange, Modifier.weight(1f)) { onOpen(Screen.PeriodicStudio) }
        }
        Spacer(Modifier.height(18.dp))
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
    val home = LocalHomeAction.current
    var query by remember { mutableStateOf("") }
    var listMode by remember { mutableStateOf(false) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var quickElement by remember { mutableStateOf<Element?>(null) }
    val context = LocalContext.current
    val rootView = LocalView.current
    val tablePrefs = remember { context.getSharedPreferences("periodic_table_settings", 0) }
    var themeIndex by remember { mutableIntStateOf(tablePrefs.getInt("theme", 0).coerceIn(periodicThemes.indices)) }
    var themeMenuOpen by remember { mutableStateOf(false) }
    var showNames by remember { mutableStateOf(tablePrefs.getBoolean("show_names", true)) }
    var reducedMotion by remember { mutableStateOf(tablePrefs.getBoolean("reduced_motion", false)) }
    var colorSafe by remember { mutableStateOf(tablePrefs.getBoolean("color_safe", false)) }
    var immersive by remember { mutableStateOf(false) }
    var focusedElement by remember { mutableStateOf<Element?>(null) }
    var highlightMode by remember { mutableStateOf("Family") }
    var textScale by remember { mutableFloatStateOf(tablePrefs.getInt("text_scale", 100) / 100f) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scope = rememberCoroutineScope()
    DisposableEffect(immersive, rootView) {
        val window = context.findActivity()?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, rootView) }
        if (immersive) {
            controller?.hide(WindowInsetsCompat.Type.systemBars())
            controller?.systemBarsBehavior =
                androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {
            if (immersive) controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }
    val tableTheme = if (colorSafe) {
        PeriodicTheme("Color Safe", listOf(Color(0xFF56B4E9), Color(0xFFE69F00), Color(0xFF009E73), Color(0xFFF0E442), Color(0xFFCC79A7)))
    } else periodicThemes[themeIndex]
    val transition = rememberInfiniteTransition(label = "periodic glow")
    val glowAlpha by transition.animateFloat(
        initialValue = if (reducedMotion) .12f else .08f,
        targetValue = if (reducedMotion) .12f else .19f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "cell glow"
    )
    Column(Modifier.fillMaxSize()) {
        if (!immersive) {
            ScreenTitle("Periodic Table", if (isLandscape) "Landscape study view · 118 elements" else "118 elements · 7 periods · 18 groups", onBack)
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TabPill("Table", !listMode, Modifier.weight(1f)) { listMode = false }
                TabPill("List", listMode, Modifier.weight(1f)) { listMode = true }
                TabPill("Compare", false, Modifier.weight(1f), onCompare)
                Box {
                    TabPill("Theme", false, Modifier.width(62.dp)) { themeMenuOpen = true }
                }
            }
        }
        Box {
            if (immersive) {
                Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "‹ BACK", color = White, fontSize = 9.sp,
                        modifier = Modifier.background(Panel2, RoundedCornerShape(8.dp))
                            .clickable { immersive = false }.padding(10.dp)
                    )
                    if (home != null) {
                        Text(
                            "⌂ HOME", color = Cyan, fontSize = 9.sp,
                            modifier = Modifier.background(Panel2, RoundedCornerShape(8.dp))
                                .clickable { home() }.padding(10.dp)
                        )
                    }
                }
            }
            Box(Modifier.align(Alignment.TopEnd)) {
                DropdownMenu(
                    expanded = themeMenuOpen,
                    onDismissRequest = { themeMenuOpen = false },
                    containerColor = Panel2
                ) {
                    periodicThemes.forEachIndexed { index, theme ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val swatch = if (theme.colors.isEmpty()) Cyan else theme.colors.first()
                                    Box(Modifier.size(9.dp).background(swatch, CircleShape))
                                    Spacer(Modifier.width(9.dp))
                                    Text(theme.name, color = if (index == themeIndex) swatch else White)
                                }
                            },
                            onClick = {
                                themeIndex = index
                                tablePrefs.edit().putInt("theme", index).apply()
                                themeMenuOpen = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(if (showNames) "✓ Element names" else "Element names", color = White) },
                        onClick = {
                            showNames = !showNames
                            tablePrefs.edit().putBoolean("show_names", showNames).apply()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (reducedMotion) "✓ Reduced motion" else "Reduced motion", color = White) },
                        onClick = {
                            reducedMotion = !reducedMotion
                            tablePrefs.edit().putBoolean("reduced_motion", reducedMotion).apply()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (colorSafe) "✓ Color-safe palette" else "Color-safe palette", color = White) },
                        onClick = {
                            colorSafe = !colorSafe
                            tablePrefs.edit().putBoolean("color_safe", colorSafe).apply()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Text size ${(textScale * 100).toInt()}%  · tap to change", color = White) },
                        onClick = {
                            textScale = when {
                                textScale < .95f -> 1f
                                textScale < 1.1f -> 1.2f
                                else -> .85f
                            }
                            tablePrefs.edit().putInt("text_scale", (textScale * 100).toInt()).apply()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Export table PNG", color = White) },
                        onClick = {
                            exportPeriodicTablePng(context, elements)
                            themeMenuOpen = false
                        }
                    )
                }
            }
        }
        if (!immersive) {
            OutlinedTextField(
                query, { query = it },
                placeholder = { Text("Search elements", color = Muted) },
                leadingIcon = { Text("⌕", color = Cyan, fontSize = 22.sp) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = if (isLandscape) 4.dp else 10.dp),
                singleLine = true, shape = RoundedCornerShape(12.dp)
            )
        }
        if (query.isNotBlank() || listMode) {
            val results = elements.filter { it.symbol.contains(query, true) || it.name.contains(query, true) || it.number.toString() == query }
            LazyVerticalGrid(GridCells.Fixed(4), contentPadding = PaddingValues(14.dp), modifier = Modifier.fillMaxSize()) {
                items(results) {
                    PeriodicCell(
                        it, 78, { selected -> focusedElement = selected; quickElement = selected }, { onCompare() },
                        themedColor(it, tableTheme), glowAlpha, showNames, textScale = textScale,
                        opacity = highlightOpacity(it, focusedElement, highlightMode)
                    )
                }
            }
        } else {
            val horizontalScrollState = rememberScrollState()
            val verticalScrollState = rememberScrollState()
            val baseCellSize = if (isLandscape) 54 else 48
            val focusOn: (Element) -> Unit = { focused ->
                focusedElement = focused
                zoom = 1.45f
                quickElement = focused
                scope.launch {
                    delay(90)
                    val horizontalTarget = (((tableGroup(focused.number).takeIf { it > 0 } ?: 3) - 1) * baseCellSize * zoom - 160).toInt()
                        .coerceIn(0, horizontalScrollState.maxValue)
                    val verticalTarget = ((tablePeriod(focused.number) - 1) * (baseCellSize + 6) * zoom - 80).toInt()
                        .coerceIn(0, verticalScrollState.maxValue)
                    horizontalScrollState.animateScrollTo(horizontalTarget)
                    verticalScrollState.animateScrollTo(verticalTarget)
                }
            }
            Box(
                Modifier.fillMaxSize()
                    .verticalScroll(verticalScrollState)
                    .twoFingerZoom { gestureZoom -> zoom = (zoom * gestureZoom).coerceIn(.75f, 1.45f) }
            ) {
                Column(Modifier.horizontalScroll(horizontalScrollState).padding(if (isLandscape) 8.dp else 12.dp)) {
                    Row(Modifier.padding(start = 26.dp, bottom = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("SWIPE · PINCH", color = if (tableTheme.colors.isEmpty()) Cyan else tableTheme.colors.first(), fontSize = 8.sp)
                        Text("  ${(zoom * 100).toInt()}%", color = Muted, fontSize = 8.sp)
                        Spacer(Modifier.width(8.dp))
                        ZoomControl("−") { zoom = (zoom - .1f).coerceAtLeast(.75f) }
                        Spacer(Modifier.width(4.dp))
                        ZoomControl("+") { zoom = (zoom + .1f).coerceAtMost(1.45f) }
                        Spacer(Modifier.width(4.dp))
                        ZoomControl("⛶") { immersive = !immersive }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "HIGHLIGHT $highlightMode", color = White, fontSize = 7.sp,
                            modifier = Modifier.background(Panel2, RoundedCornerShape(7.dp)).clickable {
                                highlightMode = when (highlightMode) {
                                    "Family" -> "Period"
                                    "Period" -> "Group"
                                    "Group" -> "Off"
                                    else -> "Family"
                                }
                            }.padding(horizontal = 8.dp, vertical = 7.dp)
                        )
                    }
                    PeriodicMiniMap(horizontalScrollState, verticalScrollState, if (tableTheme.colors.isEmpty()) Cyan else tableTheme.colors.first())
                    Row(Modifier.padding(start = 26.dp)) {
                        (1..18).forEach { group ->
                            Text(
                                group.toString(),
                                color = if (focusedElement?.let { tableGroup(it.number) == group && highlightMode == "Group" } == true) Cyan else Muted,
                                fontSize = (8 * textScale).sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width((baseCellSize * zoom).dp)
                            )
                        }
                    }
                    periodicRows.forEachIndexed { period, row ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${period + 1}", color = if (focusedElement?.let { tablePeriod(it.number) == period + 1 && highlightMode == "Period" } == true) Cyan else Muted, fontSize = (9 * textScale).sp, modifier = Modifier.width(26.dp))
                            row.forEach { number ->
                                if (number == null) Spacer(Modifier.size((baseCellSize * zoom).dp, ((baseCellSize + 6) * zoom).dp))
                                else {
                                    val element = elements[number - 1]
                                    PeriodicCell(
                                        element, (baseCellSize * zoom).toInt(),
                                        { selected -> focusedElement = selected; quickElement = selected }, { onCompare() },
                                        themedColor(element, tableTheme), glowAlpha, showNames, focusOn,
                                        textScale, highlightOpacity(element, focusedElement, highlightMode)
                                    )
                                }
                            }
                        }
                    }
                    Text("Lanthanides", color = Muted, fontSize = 9.sp, modifier = Modifier.padding(start = 26.dp, top = 10.dp))
                    Row(Modifier.padding(start = 26.dp)) {
                        lanthanideRow.forEach {
                            val element = elements[it - 1]
                            PeriodicCell(
                                element, (baseCellSize * zoom).toInt(),
                                { selected -> focusedElement = selected; quickElement = selected }, { onCompare() },
                                themedColor(element, tableTheme), glowAlpha, showNames, focusOn,
                                textScale, highlightOpacity(element, focusedElement, highlightMode)
                            )
                        }
                    }
                    Text("Actinides", color = Muted, fontSize = 9.sp, modifier = Modifier.padding(start = 26.dp, top = 5.dp))
                    Row(Modifier.padding(start = 26.dp)) {
                        actinideRow.forEach {
                            val element = elements[it - 1]
                            PeriodicCell(
                                element, (baseCellSize * zoom).toInt(),
                                { selected -> focusedElement = selected; quickElement = selected }, { onCompare() },
                                themedColor(element, tableTheme), glowAlpha, showNames, focusOn,
                                textScale, highlightOpacity(element, focusedElement, highlightMode)
                            )
                        }
                    }
                    CategoryLegend(tableTheme)
                }
            }
        }
        quickElement?.let { element ->
            val accent = themedColor(element, tableTheme)
            ModalBottomSheet(onDismissRequest = { quickElement = null }, containerColor = Panel2) {
                Column(Modifier.fillMaxWidth().padding(20.dp, 4.dp, 20.dp, 28.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(62.dp).background(accent.copy(.14f), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
                            Text(element.symbol, color = accent, fontSize = 28.sp, fontWeight = FontWeight.Bold)
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
                    Button(onClick = { quickElement = null; onSelect(element) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(accent)) {
                        Text("Open complete element profile", color = Night, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PeriodicCell(
    element: Element,
    size: Int,
    click: (Element) -> Unit,
    longClick: ((Element) -> Unit)? = null,
    accent: Color = element.color,
    glowAlpha: Float = .12f,
    showName: Boolean = true,
    focus: ((Element) -> Unit)? = null,
    textScale: Float = 1f,
    opacity: Float = 1f
) {
    val haptic = LocalHapticFeedback.current
    val animatedAccent by animateColorAsState(accent, tween(500), label = "theme color")
    val animatedOpacity by animateFloatAsState(opacity, tween(350), label = "category highlight")
    Box(
        Modifier.alpha(animatedOpacity).padding(2.dp).size(size.dp, (size + 6).dp).background(animatedAccent.copy(glowAlpha), RoundedCornerShape(7.dp))
            .border(1.dp, animatedAccent.copy(.58f), RoundedCornerShape(7.dp)).combinedClickable(
                onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); click(element) },
                onDoubleClick = focus?.let { action -> { action(element) } },
                onLongClick = longClick?.let { action -> { haptic.performHapticFeedback(HapticFeedbackType.LongPress); action(element) } }
            )
    ) {
        Text(element.number.toString(), color = Muted, fontSize = ((if (size < 60) 6 else 8) * textScale).sp, modifier = Modifier.padding(3.dp))
        Text(
            element.symbol, color = animatedAccent, fontSize = ((if (size < 60) 13 else 20) * textScale).sp,
            fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center)
        )
        if (showName && size >= 60) Text(element.name, color = White, fontSize = (7 * textScale).sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.align(Alignment.BottomCenter).padding(3.dp))
    }
}

@Composable
private fun CategoryLegend(theme: PeriodicTheme = periodicThemes.first()) {
    val entries = listOf("Non-metal", "Transition metal", "Halogen", "Noble gas").map { category ->
        val sample = Element(0, "", "", "", category, categoryColor(category), "", "")
        (if (category == "Transition metal") "Transition" else category) to themedColor(sample, theme)
    }
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
private fun ZoomControl(label: String, click: () -> Unit) {
    Text(
        label,
        color = White,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.size(25.dp).background(Panel2, RoundedCornerShape(7.dp))
            .border(1.dp, Border, RoundedCornerShape(7.dp)).clickable(onClick = click).padding(top = 2.dp)
    )
}

@Composable
private fun PeriodicMiniMap(horizontal: ScrollState, vertical: ScrollState, accent: Color) {
    Row(
        Modifier.width(250.dp).padding(start = 26.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("MAP", color = Muted, fontSize = 7.sp)
        Canvas(
            Modifier.padding(start = 7.dp).width(92.dp).height(34.dp)
                .background(Panel2, RoundedCornerShape(5.dp)).border(1.dp, Border, RoundedCornerShape(5.dp))
        ) {
            val xFraction = if (horizontal.maxValue == 0) 0f else horizontal.value / horizontal.maxValue.toFloat()
            val yFraction = if (vertical.maxValue == 0) 0f else vertical.value / vertical.maxValue.toFloat()
            val viewportWidth = if (horizontal.maxValue == 0) size.width else size.width * .34f
            val viewportHeight = if (vertical.maxValue == 0) size.height else size.height * .55f
            val x = (size.width - viewportWidth) * xFraction
            val y = (size.height - viewportHeight) * yFraction
            repeat(7) { row ->
                drawLine(Border.copy(.65f), Offset(3f, 4f + row * (size.height - 8f) / 7f), Offset(size.width - 3f, 4f + row * (size.height - 8f) / 7f), 1f)
            }
            drawRect(accent.copy(.28f), Offset(x, y), Size(viewportWidth, viewportHeight))
            drawRect(accent, Offset(x, y), Size(viewportWidth, viewportHeight), style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
        }
        Text(" visible region", color = Muted, fontSize = 7.sp, modifier = Modifier.padding(start = 6.dp))
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
        Triple("Metallic Character", "Increases toward the lower-left corner.", Orange),
        Triple("Melting & Boiling", "Explore physical-state patterns and notable peaks.", Color(0xFFFF6B8A)),
        Triple("Density", "Generally rises toward the dense transition-metal region.", Color(0xFF7EA6FF)),
        Triple("Reactivity", "Peaks differ for metals and non-metals.", Color(0xFFFFD15C))
    )
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Periodic Trends", "See how properties change across the table", onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
            items(trends) { trend ->
                GlowCard(Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable {
                    onHeatMap()
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
                ) { Text("Open interactive property maps", color = Night, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
internal fun HeatMapScreen(elements: List<Element>, onBack: () -> Unit, onSelect: (Element) -> Unit) {
    var property by remember { mutableStateOf("Electronegativity") }
    var inspectedNumber by remember { mutableFloatStateOf(1f) }
    val properties = listOf(
        "Electronegativity", "Atomic radius", "Ionization", "Electron affinity",
        "Melting point", "Boiling point", "Density", "Metallic character", "Reactivity"
    )
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("$property Heat Map", if (property == "Electronegativity") "Pauling scale · low to high" else "Relative periodic trend", onBack)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            properties.forEach { item ->
                Text(item, color = if (property == item) Night else Muted, fontSize = 9.sp,
                    modifier = Modifier.background(if (property == item) Cyan else Panel, RoundedCornerShape(8.dp)).clickable { property = item }.padding(horizontal = 11.dp, vertical = 8.dp))
            }
        }
        val modeledProperty = property == "Metallic character" || property == "Reactivity"
        Text(
            if (modeledProperty) "Qualitative teaching model, not a measured numerical scale."
            else "Measured PubChem values; unavailable measurements are shown as —.",
            color = Muted, fontSize = 10.sp, modifier = Modifier.padding(20.dp, 9.dp)
        )
        val inspected = elements[(inspectedNumber.toInt() - 1).coerceIn(elements.indices)]
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp).background(Panel, RoundedCornerShape(10.dp)).padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.width(72.dp)) {
                Text(inspected.symbol, color = inspected.color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Drag to inspect", color = Muted, fontSize = 7.sp)
            }
            Slider(
                value = inspectedNumber,
                onValueChange = { inspectedNumber = it },
                valueRange = 1f..118f,
                steps = 116,
                modifier = Modifier.weight(1f)
            )
            val inspectedMeasured = measuredProperty(inspected.number, property)
            Text(
                inspectedMeasured?.let { "${String.format("%.3g", it)} ${propertyUnit(property)}" }
                    ?: if (modeledProperty) "${(relativeTrend(inspected.number, property) * 100).toInt()} relative" else "—",
                color = White, fontSize = 9.sp
            )
        }
        LazyVerticalGrid(GridCells.Fixed(6), contentPadding = PaddingValues(14.dp), modifier = Modifier.weight(1f)) {
            items(elements) { element ->
                val measured = measuredProperty(element.number, property)
                val relative = relativeTrend(element.number, property)
                val heat = normalizedMeasuredProperty(element.number, property)
                    ?: if (modeledProperty) relative else null
                val heatColor = heat?.let(::lerpHeat) ?: Muted
                val animatedHeatColor by animateColorAsState(heatColor, tween(500), label = "animated heat map")
                Box(
                    Modifier.padding(3.dp).height(58.dp).background(animatedHeatColor.copy(.22f), RoundedCornerShape(8.dp))
                        .border(1.dp, animatedHeatColor, RoundedCornerShape(8.dp)).clickable { inspectedNumber = element.number.toFloat(); onSelect(element) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(element.symbol, color = animatedHeatColor, fontWeight = FontWeight.Bold)
                        Text(
                            measured?.let { String.format("%.3g", it) }
                                ?: if (modeledProperty && heat != null) "${(heat * 100).toInt()} rel." else "—",
                            color = White, fontSize = 7.sp
                        )
                    }
                }
            }
        }
        val scaleValues = (1..118).mapNotNull { measuredProperty(it, property) }
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(if (modeledProperty) "low" else scaleValues.minOrNull()?.let { String.format("%.3g", it) } ?: "—", color = Muted, fontSize = 9.sp)
            Box(Modifier.weight(1f).height(7.dp).padding(horizontal = 8.dp).background(Brush.horizontalGradient(listOf(Color(0xFF315BE8), Green, Orange, Color(0xFFFF4D5E))), CircleShape))
            Text(if (modeledProperty) "high" else scaleValues.maxOrNull()?.let { String.format("%.3g", it) } ?: "—", color = Muted, fontSize = 9.sp)
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
        "Reactivity" -> qualitativeReactivity(number)
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
    var comparedNumbers by remember(elements) { mutableStateOf(emptyList<Int>()) }
    var query by remember { mutableStateOf("") }
    val compared = comparedNumbers.mapNotNull { number -> elements.firstOrNull { it.number == number } }
    val available = elements.filter { element ->
        element.number !in comparedNumbers &&
            (query.isBlank() || "${element.name} ${element.symbol} ${element.number} ${element.category}".contains(query, true))
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenTitle("Element Comparison", "${compared.size} selected · no comparison limit", onBack)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Add any element by name, symbol or number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${available.size} elements available", color = Muted, fontSize = 10.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                if (compared.isNotEmpty()) {
                    Text(
                        "Clear", color = Orange, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { comparedNumbers = emptyList() }.padding(4.dp)
                    )
                }
                if (available.isNotEmpty()) {
                    Text(
                        if (query.isBlank()) "Select all 118" else "Add matches",
                        color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            comparedNumbers = addComparisonElements(comparedNumbers, available.map { it.number })
                            query = ""
                        }.padding(4.dp)
                    )
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            available.forEach { element ->
                Text(
                    "${element.symbol} · ${element.name}",
                    color = White,
                    fontSize = 10.sp,
                    modifier = Modifier.clip(RoundedCornerShape(9.dp))
                        .background(LocalReadingPalette.current.surfaceAlt)
                        .border(1.dp, element.color.copy(alpha = .45f), RoundedCornerShape(9.dp))
                        .clickable {
                            comparedNumbers = addComparisonElements(comparedNumbers, listOf(element.number))
                            query = ""
                        }
                        .padding(horizontal = 11.dp, vertical = 9.dp)
                )
            }
        }
        Text("COMPARISON MATRIX", color = Muted, fontSize = 9.sp, modifier = Modifier.padding(18.dp, 15.dp, 18.dp, 6.dp))
        if (compared.isEmpty()) {
            Text(
                "Choose one or more elements above to start comparing.",
                color = Muted,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            )
        } else {
            ElementComparisonMatrix(
                elements = compared,
                onRemove = { element -> comparedNumbers = comparedNumbers - element.number },
                onOpen = onSelect
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Swipe the matrix sideways to compare every selected element. Tap a header for full details, or × to remove it.",
            color = Muted,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
        )
    }
}

@Composable
private fun ElementComparisonMatrix(
    elements: List<Element>,
    onRemove: (Element) -> Unit,
    onOpen: (Element) -> Unit
) {
    val properties = remember {
        listOf<Pair<String, (Element) -> String>>(
            "Atomic number" to { it.number.toString() },
            "Atomic mass" to { elementFacts(it.number).atomicMass + " u" },
            "Group" to { periodicGroup(it.number)?.toString() ?: "f-block" },
            "Period" to { periodicPeriod(it.number).toString() },
            "Classification" to { elementFacts(it.number).groupBlock },
            "State at 20 °C" to { elementFacts(it.number).standardState },
            "Electron configuration" to { elementFacts(it.number).electronConfiguration },
            "Oxidation states" to { elementFacts(it.number).oxidationStates },
            "Atomic radius" to { factOrDash(elementFacts(it.number).atomicRadiusPm, "pm") },
            "Electronegativity" to { elementFacts(it.number).electronegativity },
            "Ionization energy" to { factOrDash(elementFacts(it.number).ionizationEnergyEv, "eV") },
            "Electron affinity" to { factOrDash(elementFacts(it.number).electronAffinityEv, "eV") },
            "Melting point" to { factOrDash(elementFacts(it.number).meltingPointK, "K") },
            "Boiling point" to { factOrDash(elementFacts(it.number).boilingPointK, "K") },
            "Density" to { factOrDash(elementFacts(it.number).densityGcm3, "g/cm³") },
            "Discovered" to { elementFacts(it.number).yearDiscovered }
        )
    }
    val matrixScroll = rememberScrollState()
    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
        Column(Modifier.width(118.dp)) {
            ComparisonMatrixCell("PROPERTY", 92, isLabel = true)
            properties.forEach { (label) -> ComparisonMatrixCell(label, 66, isLabel = true) }
        }
        Row(Modifier.weight(1f).horizontalScroll(matrixScroll)) {
            elements.forEach { element ->
                Column(
                    Modifier.width(154.dp)
                        .border(1.dp, element.color.copy(alpha = .25f), RoundedCornerShape(8.dp))
                ) {
                    Box(
                        Modifier.fillMaxWidth().height(92.dp).clickable { onOpen(element) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(element.symbol, color = element.color, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                            Text(element.name, color = White, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Text(
                            "×",
                            color = Orange,
                            fontSize = 18.sp,
                            modifier = Modifier.align(Alignment.TopEnd).clickable { onRemove(element) }.padding(8.dp)
                        )
                    }
                    properties.forEach { (_, value) -> ComparisonMatrixCell(value(element), 66) }
                }
            }
        }
    }
}

@Composable
private fun ComparisonMatrixCell(value: String, heightDp: Int, isLabel: Boolean = false) {
    Box(
        Modifier.fillMaxWidth().height(heightDp.dp)
            .background(if (isLabel) LocalReadingPalette.current.surfaceAlt else Color.Transparent)
            .border(0.5.dp, Border)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = if (isLabel) Alignment.CenterStart else Alignment.Center
    ) {
        Text(
            value,
            color = if (isLabel) Muted else White,
            fontSize = if (isLabel) 9.sp else 10.sp,
            fontWeight = if (isLabel) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = if (isLabel) TextAlign.Start else TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
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
