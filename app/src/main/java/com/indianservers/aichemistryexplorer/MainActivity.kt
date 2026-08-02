package com.indianservers.aichemistryexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text as MaterialText
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.indianservers.aichemistryexplorer.ui.theme.AIChemistryExplorerTheme
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

internal val Night = Color(0xFF020711)
internal val Panel = Color(0xFF071321)
internal val Panel2 = Color(0xFF0A1728)
internal val Cyan = Color(0xFF1EDCFF)
internal val Purple = Color(0xFF9A45FF)
internal val Green = Color(0xFF25E6A1)
internal val Orange = Color(0xFFFF9B3D)
internal val Muted = Color(0xFF7890A9)
internal val White = Color(0xFFEAF7FF)
internal val Border = Color(0xFF18324A)

enum class Screen {
    Home, Explore, Table, Lab, Quiz, Progress, Element, Reactions, Molecule,
    FullTable, Categories, Trends, HeatMap, Compare, Notebook, PeriodicStudio,
    ModuleLesson, TopicLesson, Search
}

data class Element(
    val number: Int, val symbol: String, val name: String, val mass: String,
    val category: String, val color: Color, val radius: String, val boiling: String
)

private val elements = listOf(
    Element(1, "H", "Hydrogen", "1.008", "Non-metal", Cyan, "53 pm", "-252.9 °C"),
    Element(2, "He", "Helium", "4.003", "Noble gas", Purple, "31 pm", "-268.9 °C"),
    Element(3, "Li", "Lithium", "6.94", "Alkali metal", Color(0xFF4D8BFF), "167 pm", "1,342 °C"),
    Element(4, "Be", "Beryllium", "9.012", "Alkaline earth", Orange, "112 pm", "2,469 °C"),
    Element(5, "B", "Boron", "10.81", "Metalloid", Green, "87 pm", "4,000 °C"),
    Element(6, "C", "Carbon", "12.011", "Non-metal", Cyan, "67 pm", "4,027 °C"),
    Element(7, "N", "Nitrogen", "14.007", "Non-metal", Cyan, "56 pm", "-195.8 °C"),
    Element(8, "O", "Oxygen", "15.999", "Non-metal", Cyan, "48 pm", "-183.0 °C"),
    Element(9, "F", "Fluorine", "18.998", "Halogen", Green, "42 pm", "-188.1 °C"),
    Element(10, "Ne", "Neon", "20.180", "Noble gas", Purple, "38 pm", "-246.1 °C"),
    Element(11, "Na", "Sodium", "22.990", "Alkali metal", Color(0xFF4D8BFF), "190 pm", "883 °C"),
    Element(12, "Mg", "Magnesium", "24.305", "Alkaline earth", Orange, "145 pm", "1,091 °C"),
    Element(13, "Al", "Aluminium", "26.982", "Post-transition", Color(0xFFE34C9A), "118 pm", "2,519 °C"),
    Element(14, "Si", "Silicon", "28.085", "Metalloid", Green, "111 pm", "3,265 °C"),
    Element(15, "P", "Phosphorus", "30.974", "Non-metal", Cyan, "98 pm", "280.5 °C"),
    Element(16, "S", "Sulfur", "32.06", "Non-metal", Cyan, "88 pm", "444.7 °C"),
    Element(17, "Cl", "Chlorine", "35.45", "Halogen", Green, "79 pm", "-34.0 °C"),
    Element(18, "Ar", "Argon", "39.948", "Noble gas", Purple, "71 pm", "-185.8 °C"),
    Element(19, "K", "Potassium", "39.098", "Alkali metal", Color(0xFF4D8BFF), "243 pm", "759 °C"),
    Element(20, "Ca", "Calcium", "40.078", "Alkaline earth", Orange, "194 pm", "1,484 °C"),
    Element(21, "Sc", "Scandium", "44.956", "Transition metal", Color(0xFF1CC6A1), "184 pm", "2,836 °C"),
    Element(22, "Ti", "Titanium", "47.867", "Transition metal", Color(0xFF1CC6A1), "176 pm", "3,287 °C"),
    Element(23, "V", "Vanadium", "50.942", "Transition metal", Color(0xFF1CC6A1), "171 pm", "3,407 °C"),
    Element(24, "Cr", "Chromium", "51.996", "Transition metal", Color(0xFF1CC6A1), "166 pm", "2,671 °C"),
    Element(25, "Mn", "Manganese", "54.938", "Transition metal", Color(0xFF1CC6A1), "161 pm", "2,061 °C"),
    Element(26, "Fe", "Iron", "55.845", "Transition metal", Color(0xFF1CC6A1), "156 pm", "2,862 °C"),
    Element(27, "Co", "Cobalt", "58.933", "Transition metal", Color(0xFF1CC6A1), "152 pm", "2,927 °C"),
    Element(28, "Ni", "Nickel", "58.693", "Transition metal", Color(0xFF1CC6A1), "149 pm", "2,913 °C"),
    Element(29, "Cu", "Copper", "63.546", "Transition metal", Orange, "145 pm", "2,562 °C"),
    Element(30, "Zn", "Zinc", "65.38", "Transition metal", Color(0xFF1CC6A1), "142 pm", "907 °C"),
    Element(35, "Br", "Bromine", "79.904", "Halogen", Green, "94 pm", "58.8 °C"),
    Element(47, "Ag", "Silver", "107.868", "Transition metal", Color(0xFF1CC6A1), "165 pm", "2,162 °C"),
    Element(53, "I", "Iodine", "126.904", "Halogen", Green, "115 pm", "184.3 °C"),
    Element(79, "Au", "Gold", "196.967", "Transition metal", Orange, "174 pm", "2,970 °C")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AIChemistryExplorerTheme { ChemistryApp() } }
    }
}

@Composable
fun ChemistryApp() {
    val context = LocalContext.current
    val periodicPrefs = remember { context.getSharedPreferences("periodic_progress", 0) }
    val readingPrefs = remember { context.getSharedPreferences("reading_preferences", 0) }
    var readingTheme by remember { mutableIntStateOf(readingPrefs.getInt("theme", 0).coerceIn(readingPalettes.indices)) }
    var fontScale by remember { mutableFloatStateOf(readingPrefs.getFloat("font_scale", 1f).coerceIn(.85f, 1.35f)) }
    var readingMenu by remember { mutableStateOf(false) }
    val palette = readingPalettes[readingTheme]
    val density = LocalDensity.current
    var screen by remember { mutableStateOf(Screen.Home) }
    var navigationHistory by remember { mutableStateOf(emptyList<Screen>()) }
    var selected by remember { mutableStateOf(elements.first { it.symbol == "O" }) }
    var selectedModule by remember { mutableStateOf("Atomic Structure") }
    var selectedLesson by remember { mutableStateOf("Subatomic particles") }
    var completedLessons by remember { mutableStateOf(setOf<String>()) }
    var savedElements by remember {
        mutableStateOf(
            periodicPrefs.getStringSet("saved_elements", emptySet()).orEmpty()
                .mapNotNull { it.toIntOrNull() }.toSet()
        )
    }
    val allElements = remember { completePeriodicTable(elements) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val mainScreens = setOf(Screen.Home, Screen.Explore, Screen.Table, Screen.Lab, Screen.Quiz, Screen.Progress)
    fun navigateTo(destination: Screen) {
        val result = pushNavigation(screen, navigationHistory, destination)
        screen = result.screen
        navigationHistory = result.history
    }
    fun navigateBack() {
        val result = popNavigation(navigationHistory)
        screen = result.screen
        navigationHistory = result.history
    }
    fun navigateHome() {
        val result = homeNavigation()
        screen = result.screen
        navigationHistory = result.history
    }
    BackHandler(screen != Screen.Home) { navigateBack() }

    CompositionLocalProvider(
        LocalReadingPalette provides palette,
        LocalDensity provides Density(density.density, density.fontScale * fontScale),
        LocalHomeAction provides { navigateHome() },
        LocalBackAction provides { navigateBack() }
    ) {
    Scaffold(
        containerColor = palette.background,
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(10.dp, CircleShape, ambientColor = Color.Black.copy(.45f), spotColor = Cyan.copy(.18f))
                        .clip(CircleShape)
                        .background(palette.surfaceAlt.copy(alpha = .94f))
                        .border(1.dp, White.copy(alpha = .22f), CircleShape)
                        .semantics { contentDescription = "Reading theme and text size" }
                        .clickable { readingMenu = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "◐",
                        color = Cyan,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(7.dp)
                            .size(5.dp)
                            .background(Green, CircleShape)
                    )
                }
                DropdownMenu(
                    expanded = readingMenu,
                    onDismissRequest = { readingMenu = false },
                    modifier = Modifier.background(palette.surface)
                ) {
                    Text("Reading theme", color = White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp, 5.dp))
                    readingPalettes.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = { Text(if (index == readingTheme) "✓ ${item.name}" else item.name, color = White) },
                            onClick = {
                                readingTheme = index
                                readingPrefs.edit().putInt("theme", index).apply()
                                readingMenu = false
                            }
                        )
                    }
                    HorizontalDivider(color = palette.outline)
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = {
                            fontScale = (fontScale - .1f).coerceAtLeast(.85f)
                            readingPrefs.edit().putFloat("font_scale", fontScale).apply()
                        }) { Text("A−", color = Cyan, fontSize = 17.sp) }
                        Text("${(fontScale * 100).toInt()}%", color = White, fontSize = 12.sp)
                        TextButton(onClick = {
                            fontScale = (fontScale + .1f).coerceAtMost(1.35f)
                            readingPrefs.edit().putFloat("font_scale", fontScale).apply()
                        }) { Text("A+", color = Cyan, fontSize = 17.sp) }
                    }
                }
            }
        },
        bottomBar = {
            if (screen in mainScreens) {
                BottomNav(screen) { navigateTo(it) }
            }
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(palette.glow, palette.background),
                        center = Offset(600f, 100f),
                        radius = 1000f
                    )
                )
                .drawBehind {
                    val grid = palette.outline.copy(alpha = .10f)
                    var x = 0f
                    while (x < size.width) { drawLine(grid, Offset(x, 0f), Offset(x, size.height), 1f); x += 72f }
                    var y = 0f
                    while (y < size.height) { drawLine(grid, Offset(0f, y), Offset(size.width, y), 1f); y += 72f }
                }
                .padding(padding)
                .statusBarsPadding()
        ) {
            AnimatedContent(screen, label = "screen") { current ->
                when (current) {
                    Screen.Home -> HomeScreen(
                        onExplore = { navigateTo(Screen.Explore) },
                        onTable = { navigateTo(Screen.Table) },
                        onReactions = { navigateTo(Screen.Reactions) },
                        onMolecule = { navigateTo(Screen.Molecule) },
                        onContinue = { navigateTo(Screen.Explore) },
                        onSearch = { navigateTo(Screen.Search) }
                    )
                    Screen.Search -> GlobalChemistrySearchScreen(
                        elements = allElements,
                        onBack = { navigateBack() },
                        onElement = { selected = it; navigateTo(Screen.Element) },
                        onModule = { selectedModule = it; navigateTo(Screen.ModuleLesson) },
                        onDestination = { navigateTo(it) }
                    )
                    Screen.Explore -> ExploreScreen(
                        onOpen = { destination -> navigateTo(destination) },
                        onModule = { module -> selectedModule = module; navigateTo(Screen.ModuleLesson) }
                    )
                    Screen.ModuleLesson -> CurriculumModuleScreen(
                        moduleTitle = selectedModule,
                        completedLessons = completedLessons,
                        onBack = { navigateBack() },
                        onLesson = { lesson -> selectedLesson = lesson; navigateTo(Screen.TopicLesson) }
                    )
                    Screen.TopicLesson -> CurriculumLessonScreen(
                        moduleTitle = selectedModule,
                        lessonTitle = selectedLesson,
                        onBack = { navigateBack() },
                        onComplete = {
                            completedLessons = completedLessons + "$selectedModule/$selectedLesson"
                            navigateBack()
                        },
                        onQuiz = { navigateTo(Screen.Quiz) }
                    )
                    Screen.Table -> PeriodicHubScreen(
                        savedCount = savedElements.size,
                        onOpen = { navigateTo(it) },
                        onSelect = { selected = it; navigateTo(Screen.Element) }
                    )
                    Screen.FullTable -> EnhancedPeriodicTableScreen(
                        elements = allElements,
                        onBack = { navigateBack() },
                        onSelect = { selected = it; navigateTo(Screen.Element) },
                        onCompare = { navigateTo(Screen.Compare) }
                    )
                    Screen.Categories -> ElementCategoriesScreen(
                        elements = allElements,
                        onBack = { navigateBack() },
                        onSelect = { selected = it; navigateTo(Screen.Element) }
                    )
                    Screen.Trends -> PeriodicTrendsScreen(
                        onBack = { navigateBack() },
                        onHeatMap = { navigateTo(Screen.HeatMap) }
                    )
                    Screen.HeatMap -> HeatMapScreen(allElements, onBack = { navigateBack() }) {
                        selected = it; navigateTo(Screen.Element)
                    }
                    Screen.Compare -> ElementComparisonScreen(allElements, onBack = { navigateBack() }) {
                        selected = it; navigateTo(Screen.Element)
                    }
                    Screen.Notebook -> ElementNotebookScreen(
                        elements = allElements.filter { it.number in savedElements },
                        onBack = { navigateBack() },
                        onSelect = { selected = it; navigateTo(Screen.Element) },
                        onRemove = {
                            savedElements = savedElements - it.number
                            periodicPrefs.edit().putStringSet("saved_elements", savedElements.map(Int::toString).toSet()).apply()
                        }
                    )
                    Screen.PeriodicStudio -> PeriodicDiscoveryStudioScreen(
                        elements = allElements,
                        onBack = { navigateBack() },
                        onSelect = { selected = it; navigateTo(Screen.Element) }
                    )
                    Screen.Element -> ElementScreen(
                        element = selected,
                        isSaved = selected.number in savedElements,
                        onBack = { navigateBack() },
                        onToggleSaved = {
                            savedElements = if (selected.number in savedElements) savedElements - selected.number else savedElements + selected.number
                            periodicPrefs.edit().putStringSet("saved_elements", savedElements.map(Int::toString).toSet()).apply()
                            scope.launch { snackbar.showSnackbar(if (selected.number in savedElements) "${selected.symbol} saved to notebook" else "${selected.symbol} removed") }
                        }
                    ) { navigateTo(Screen.Molecule) }
                    Screen.Lab -> VirtualLabScreen()
                    Screen.Quiz -> MultiPageQuizScreen()
                    Screen.Progress -> MultiPageProgressScreen()
                    Screen.Reactions -> ReactionsScreen(onBack = { navigateBack() })
                    Screen.Molecule -> MoleculeScreen(onBack = { navigateBack() })
                }
            }
        }
    }
    }
}

@Composable
internal fun ScreenTitle(title: String, subtitle: String? = null, back: (() -> Unit)? = null) {
    val home = LocalHomeAction.current
    val resolvedBack = back ?: LocalBackAction.current
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (resolvedBack != null) {
            Text("‹", color = White, fontSize = 38.sp, modifier = Modifier.clickable { resolvedBack() }.padding(end = 12.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            subtitle?.let { Text(it, color = Muted, fontSize = 12.sp) }
        }
        if (home != null) {
            Text(
                "⌂",
                color = Cyan,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.clip(CircleShape)
                    .background(LocalReadingPalette.current.surfaceAlt)
                    .border(1.dp, Cyan.copy(alpha = .45f), CircleShape)
                    .clickable { home() }
                    .padding(horizontal = 11.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun HomeScreen(
    onExplore: () -> Unit, onTable: () -> Unit, onReactions: () -> Unit,
    onMolecule: () -> Unit, onContinue: () -> Unit, onSearch: () -> Unit
) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth().padding(20.dp, 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("AI Chemistry Explorer", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Open learning · Class 6 to postgraduate", color = Muted, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "⌂", color = Cyan, fontSize = 23.sp,
                        modifier = Modifier.clip(CircleShape).background(LocalReadingPalette.current.surfaceAlt)
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                    Text("⌕", color = Cyan, fontSize = 27.sp, modifier = Modifier.clickable { onSearch() }.padding(9.dp))
                }
            }
        }
        item {
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(Color(0xFF091728)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF224566))
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AtomGraphic(108.dp, Cyan)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Tag("OPEN CURRICULUM", Purple)
                        Spacer(Modifier.height(8.dp))
                        Text("Chemistry without gaps", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Foundations, inorganic, organic, biochemistry, materials and drug discovery.", color = Muted, fontSize = 12.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("${curriculum.size} modules · ${curriculum.sumOf { it.lessons.size }} concepts", color = Cyan, fontSize = 10.sp)
                    }
                }
            }
        }
        item {
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(Cyan),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Browse the complete curriculum", color = Night, fontWeight = FontWeight.Bold) }
        }
        item { SectionHeader("Explore by discipline", "View all", onExplore) }
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TopicCard("Organic\nChemistry", "⌬", Purple, Modifier.weight(1f), onExplore)
                TopicCard("Periodic\nTable", "⚛", Cyan, Modifier.weight(1f), onTable)
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TopicCard("Chemical\nReactions", "⇌", Orange, Modifier.weight(1f), onReactions)
                TopicCard("Molecule\nViewer", "⬡", Green, Modifier.weight(1f), onMolecule)
            }
        }
    }
}

@Composable
private fun LearningPulseCard(onContinue: () -> Unit) {
    GlowCard(Modifier.fillMaxWidth().padding(18.dp, 12.dp, 18.dp, 0.dp), Cyan) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    drawArc(Border, -90f, 360f, false, style = Stroke(8f, cap = StrokeCap.Round))
                    drawArc(Brush.sweepGradient(listOf(Purple, Cyan)), -90f, 252f, false, style = Stroke(8f, cap = StrokeCap.Round))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("70%", color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("GOAL", color = Muted, fontSize = 7.sp)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("TODAY'S MISSION", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("Master chemical bonding", color = White, fontWeight = FontWeight.SemiBold)
                Text("12 min · 2 lessons remaining", color = Muted, fontSize = 10.sp)
                Row(Modifier.padding(top = 9.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    listOf(.9f, .55f, .75f, .35f, .7f, .5f, .85f).forEachIndexed { index, value ->
                        Box(
                            Modifier.size(14.dp, 9.dp).background(
                                if (index == 6) Cyan else Purple.copy(alpha = .25f + value * .55f), RoundedCornerShape(3.dp)
                            )
                        )
                    }
                }
            }
            Text("›", color = Cyan, fontSize = 28.sp, modifier = Modifier.clickable { onContinue() }.padding(8.dp))
        }
    }
}

@Composable
private fun TopicCard(title: String, icon: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier.height(132.dp).clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(Panel),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = .45f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = .12f)),
                contentAlignment = Alignment.Center
            ) { Text(icon, color = color, fontSize = 26.sp) }
            Spacer(Modifier.weight(1f))
            Text(title, color = White, fontWeight = FontWeight.SemiBold, lineHeight = 17.sp)
            Text("10 chapters", color = Muted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ExploreScreen(onOpen: (Screen) -> Unit, onModule: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("All levels") }
    var category by remember { mutableStateOf("All categories") }
    var subcategory by remember { mutableStateOf("All subcategories") }
    val levels = listOf("All levels", "Class 6–8", "Class 9–10", "Class 11–12", "Undergraduate", "Postgraduate")
    val levelModules = curriculum.filter { level == "All levels" || it.level == level }
    val categories = listOf("All categories") + levelModules.map { it.category }.distinct().sorted()
    val categoryModules = levelModules.filter { category == "All categories" || it.category == category }
    val subcategories = listOf("All subcategories") + categoryModules.map { it.subcategory }.distinct().sorted()
    val topics = categoryModules.filter {
        (subcategory == "All subcategories" || it.subcategory == subcategory) &&
            (query.isBlank() || it.title.contains(query, true) || it.description.contains(query, true) ||
                it.category.contains(query, true) || it.subcategory.contains(query, true) ||
                it.lessons.any { lesson -> lesson.title.contains(query, true) })
    }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Curriculum", "Class 6 to PG · category and subcategory")
        OutlinedTextField(
            value = query, onValueChange = { query = it },
            placeholder = { Text("Search modules or concepts...", color = Muted) },
            leadingIcon = { Text("⌕", color = Cyan, fontSize = 22.sp) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )
        CurriculumFilterRow(levels, level, Cyan) {
            level = it
            category = "All categories"
            subcategory = "All subcategories"
        }
        CurriculumFilterRow(categories, category, Purple) {
            category = it
            subcategory = "All subcategories"
        }
        CurriculumFilterRow(subcategories, subcategory, Green) { subcategory = it }
        Text(
            "${topics.size} modules · ${topics.sumOf { it.lessons.size }} concepts",
            color = Muted,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
            items(topics) { topic ->
                Card(
                    Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable { onModule(topic.title) },
                    colors = CardDefaults.cardColors(Panel),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                            AtomGraphic(52.dp, topic.color)
                            Text(
                                topic.icon,
                                color = White,
                                fontSize = if (topic.icon.length > 1) 10.sp else 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(topic.title, color = White, fontWeight = FontWeight.SemiBold)
                            Text("${topic.level} · ${topic.category}", color = topic.color, fontSize = 9.sp)
                            Text(topic.subcategory, color = Muted, fontSize = 11.sp)
                        }
                        Text("›", color = Muted, fontSize = 26.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CurriculumFilterRow(options: List<String>, selected: String, accent: Color, onSelect: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        options.forEach { option ->
            Text(
                option,
                color = if (selected == option) Night else Muted,
                fontSize = 9.sp,
                maxLines = 1,
                modifier = Modifier.clip(CircleShape)
                    .background(if (selected == option) accent else Panel)
                    .border(1.dp, if (selected == option) accent else Border, CircleShape)
                    .clickable { onSelect(option) }
                    .padding(horizontal = 11.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun PeriodicTableScreen(onSelect: (Element) -> Unit, onMessage: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("All") }
    val categories = listOf("All", "Non-metal", "Transition metal", "Halogen", "Noble gas")
    val filtered = elements.filter {
        (category == "All" || it.category == category) &&
            (query.isBlank() || it.name.contains(query, true) || it.symbol.contains(query, true) || it.number.toString() == query)
    }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Periodic Table", "Tap any element to inspect it")
        OutlinedTextField(
            value = query, onValueChange = { query = it },
            placeholder = { Text("Search element, symbol or number", color = Muted, fontSize = 13.sp) },
            leadingIcon = { Text("⌕", color = Cyan, fontSize = 22.sp) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
            shape = RoundedCornerShape(14.dp), singleLine = true
        )
        Row(
            Modifier.fillMaxWidth().padding(18.dp, 10.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            categories.take(4).forEach { name ->
                Text(
                    name.replace("Transition metal", "Metals"),
                    color = if (category == name) Night else Muted,
                    fontSize = 10.sp,
                    modifier = Modifier.clip(CircleShape)
                        .background(if (category == name) Cyan else Panel)
                        .clickable { category = name }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                )
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${filtered.size} elements", color = Muted, fontSize = 12.sp)
            Text("Heat map  ◫", color = Purple, fontSize = 12.sp, modifier = Modifier.clickable { onMessage("Electronegativity heat map enabled") })
        }
        Spacer(Modifier.height(10.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 4.dp)
        ) {
            items(filtered) { element ->
                ElementTile(element) { onSelect(element) }
            }
        }
    }
}

@Composable
private fun ElementTile(element: Element, onClick: () -> Unit) {
    Box(
        Modifier.padding(3.dp).aspectRatio(.9f).clip(RoundedCornerShape(9.dp))
            .background(element.color.copy(alpha = .11f))
            .border(1.dp, element.color.copy(alpha = .52f), RoundedCornerShape(9.dp))
            .clickable { onClick() }
    ) {
        Text(element.number.toString(), color = Muted, fontSize = 8.sp, modifier = Modifier.padding(5.dp))
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(element.symbol, color = element.color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(element.name, color = White, fontSize = 7.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ElementScreen(
    element: Element,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSaved: () -> Unit,
    onModel: () -> Unit
) {
    val facts = elementFacts(element.number)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) { ScreenTitle("Element Detail", element.category, onBack) }
            Text(
                if (isSaved) "★" else "☆", color = if (isSaved) Orange else Cyan, fontSize = 28.sp,
                modifier = Modifier.clickable { onToggleSaved() }.padding(18.dp)
            )
        }
        GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp), element.color) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(element.number.toString(), color = Muted)
                    Text(element.symbol, color = White, fontSize = 64.sp, lineHeight = 66.sp, fontWeight = FontWeight.Light)
                    Text(element.name, color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Tag(element.category.uppercase(), element.color)
                }
                ElementHeaderAtom(element)
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(18.dp, 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Overview", "Properties", "Uses").forEachIndexed { index, label ->
                Text(
                    label, color = if (index == 0) Night else Muted, fontSize = 11.sp,
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                        .background(if (index == 0) Cyan else Panel)
                        .padding(vertical = 9.dp), textAlign = TextAlign.Center
                )
            }
        }
        PropertyRow("Atomic number", element.number.toString())
        PropertyRow("Atomic mass", "${element.mass} u")
        PropertyRow("Atomic radius", element.radius)
        PropertyRow("Electron configuration", facts.electronConfiguration)
        PropertyRow("Standard state", facts.standardState)
        PropertyRow("Melting point", factOrDash(facts.meltingPointK, "K"))
        PropertyRow("Boiling point", element.boiling)
        PropertyRow("Density", factOrDash(facts.densityGcm3, "g/cm³"))
        PropertyRow("Electronegativity", facts.electronegativity)
        PropertyRow("First ionization energy", factOrDash(facts.ionizationEnergyEv, "eV"))
        Text("COMMON COMPOUNDS", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(20.dp, 18.dp, 20.dp, 8.dp))
        val compounds = validatedCompoundsFor(element)
        if (compounds.isEmpty()) {
            Text(
                "No curated common-compound examples are available in the offline dataset.",
                color = Muted, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 20.dp)
            )
        } else {
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                compounds.forEach {
                    Text(it, color = Cyan, fontSize = 12.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Panel).padding(10.dp))
                }
            }
        }
        ElementKnowledgeLab(element)
        Button(
            onClick = onModel,
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            colors = ButtonDefaults.buttonColors(Cyan),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Open atomic model", color = Night, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun PropertyRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Muted, fontSize = 12.sp)
        Text(value, color = White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
    HorizontalDivider(Modifier.padding(horizontal = 18.dp), color = Border.copy(alpha = .6f))
}

@Composable
private fun ReactionsScreen(onBack: () -> Unit) {
    val chapters = remember { listOf("All") + reactionLibrary.map { it.chapter }.distinct() }
    var chapter by remember { mutableStateOf("All") }
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<String?>(null) }
    val reactions = reactionLibrary.filter {
        (chapter == "All" || it.chapter == chapter) &&
            (query.isBlank() || listOf(it.title, it.equation, it.type, it.purpose)
                .any { field -> field.contains(query, ignoreCase = true) })
    }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Reaction Explorer", "${reactionLibrary.size} reactions · equations, conditions and purpose", onBack)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search reaction, equation or purpose") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
        )
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chapters.forEach { item ->
                Text(
                    item, color = if (chapter == item) Night else White, fontSize = 11.sp,
                    modifier = Modifier.clip(CircleShape)
                        .background(if (chapter == item) Cyan else LocalReadingPalette.current.surfaceAlt)
                        .clickable { chapter = item }.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
        Text(
            "${reactions.size} matching reactions · Tap a card to learn why it matters",
            color = Muted, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 20.dp, vertical = 3.dp)
        )
        LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
            items(reactions) { reaction ->
                GlowCard(
                    Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable {
                        selected = if (selected == reaction.title) null else reaction.title
                    }, reaction.color
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⇌", color = reaction.color, fontSize = 26.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(reaction.title, color = White, fontWeight = FontWeight.SemiBold)
                            Text(reaction.equation, color = White.copy(alpha = .88f), fontSize = 13.sp)
                            Text(reaction.type, color = reaction.color, fontSize = 10.sp)
                            if (selected == reaction.title) {
                                Spacer(Modifier.height(9.dp))
                                Text("WHY IT MATTERS", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(reaction.purpose, color = White, fontSize = 12.sp)
                                Spacer(Modifier.height(7.dp))
                                Text("CONDITIONS", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(reaction.conditions, color = White, fontSize = 12.sp)
                                Spacer(Modifier.height(7.dp))
                                Text(reaction.explanation, color = Muted, fontSize = 11.sp)
                            }
                        }
                        Tag(reaction.chapter.uppercase(), reaction.color)
                    }
                }
            }
        }
    }
}

@Composable
private fun MoleculeScreen(onBack: () -> Unit) {
    var model by remember { mutableStateOf(0) }
    val chapters = remember { moleculeLibrary.map { it.chapter }.distinct() }
    var chapter by remember { mutableStateOf("All") }
    var moleculeName by remember { mutableStateOf(moleculeLibrary.first().name) }
    var query by remember { mutableStateOf("") }
    var showAll by remember { mutableStateOf(true) }
    val available = moleculeLibrary.filter {
        (chapter == "All" || it.chapter == chapter) &&
            (query.isBlank() || "${it.name} ${it.formula} ${it.chapter} ${it.uses}".contains(query, true))
    }
    val molecule = moleculeLibrary.firstOrNull { it.name == moleculeName } ?: moleculeLibrary.first()
    val modes = listOf("Ball & stick", "Space fill", "Electron cloud")
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenTitle("Molecule Viewer", "${moleculeLibrary.size} molecules across ${chapters.size} categories", onBack)
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                if (it.isNotBlank()) { chapter = "All"; showAll = true }
            },
            label = { Text("Search name, formula, category or use") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("CATEGORIES", color = Muted, fontSize = 10.sp)
            Text(
                if (showAll) "Hide catalog" else "Show All (${moleculeLibrary.size})",
                color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.clip(CircleShape).border(1.dp, Cyan.copy(alpha = .55f), CircleShape)
                    .clickable { showAll = !showAll; if (showAll) chapter = "All" }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            )
        }
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (listOf("All") + chapters).forEach { item ->
                Text(
                    item, color = if (chapter == item) Night else White, fontSize = 11.sp,
                    modifier = Modifier.clip(CircleShape)
                        .background(if (chapter == item) Cyan else LocalReadingPalette.current.surfaceAlt)
                        .clickable {
                            chapter = item
                            val first = if (item == "All") moleculeLibrary.first()
                                else moleculeLibrary.first { it.chapter == item }
                            moleculeName = first.name
                        }.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
        if (showAll) {
            val grouped = available.groupBy { it.chapter }
            GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 5.dp), Cyan) {
                if (available.isEmpty()) {
                    Text("No molecule matches “$query”.", color = Muted, fontSize = 12.sp)
                }
                grouped.forEach { (group, molecules) ->
                    Text(
                        "$group · ${molecules.size}",
                        color = Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 7.dp, bottom = 5.dp)
                    )
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        molecules.forEach { item ->
                            Text(
                                "${item.name}  ${item.formula}",
                                color = if (item.name == molecule.name) Night else White,
                                fontSize = 10.sp,
                                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                    .background(if (item.name == molecule.name) Cyan else LocalReadingPalette.current.surfaceAlt)
                                    .clickable {
                                        moleculeName = item.name
                                        chapter = item.chapter
                                        showAll = false
                                        query = ""
                                    }.padding(horizontal = 10.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                available.forEach { item ->
                    Text(
                        item.name, color = if (item.name == molecule.name) Cyan else Muted, fontSize = 11.sp,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            .border(1.dp, if (item.name == molecule.name) Cyan else Border, RoundedCornerShape(8.dp))
                            .clickable { moleculeName = item.name }.padding(horizontal = 11.dp, vertical = 8.dp)
                    )
                }
            }
        }
        Text(molecule.name, color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(molecule.formula, color = Cyan, fontSize = 15.sp)
            Tag(molecule.chapter.uppercase(), Purple)
        }
        MoleculeGraphic(Modifier.fillMaxWidth().height(280.dp), model, molecule.layout)
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            modes.forEachIndexed { index, mode ->
                Text(
                    mode, color = if (model == index) Night else Muted, fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                        .background(if (model == index) Cyan else Panel)
                        .clickable { model = index }.padding(vertical = 10.dp)
                )
            }
        }
        GlowCard(Modifier.fillMaxWidth().padding(18.dp), Cyan) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoStat("Molar mass", molecule.molarMass)
                InfoStat("Geometry", molecule.geometry)
            }
            Spacer(Modifier.height(18.dp))
            Text("BONDING", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(molecule.bonding, color = White, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            Text("Schematic teaching model; atom sizes and distances are not to scale.", color = Muted, fontSize = 10.sp)
        }
        GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp), Purple) {
            Text("UNDERSTAND THE MOLECULE", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(molecule.theory, color = White, fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp))
            Spacer(Modifier.height(12.dp))
            Text("CHEMISTRY EXAMPLE", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(molecule.exampleChemistry, color = White, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
            Spacer(Modifier.height(12.dp))
            Text("WHERE IT IS USED", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(molecule.uses, color = White, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
        }
        val relatedMolecules = (molecule.related.mapNotNull { name -> moleculeLibrary.firstOrNull { it.name == name } } +
            moleculeLibrary.filter { it.chapter == molecule.chapter && it.name != molecule.name }).distinctBy { it.name }
        if (relatedMolecules.isNotEmpty()) {
            Text("RELATED MOLECULES", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                relatedMolecules.forEach { item ->
                    Text(
                        "${item.name} · ${item.formula}", color = Cyan, fontSize = 10.sp,
                        modifier = Modifier.clip(RoundedCornerShape(9.dp)).background(LocalReadingPalette.current.surfaceAlt)
                            .border(1.dp, Cyan.copy(alpha = .4f), RoundedCornerShape(9.dp))
                            .clickable { moleculeName = item.name; chapter = item.chapter }
                            .padding(horizontal = 11.dp, vertical = 9.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(22.dp))
    }
}

@Composable
private fun LabScreen(onMessage: (String) -> Unit) {
    var concentration by remember { mutableFloatStateOf(.35f) }
    var volume by remember { mutableFloatStateOf(.5f) }
    var running by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenTitle("Virtual Lab", "Acid–base titration")
        GlowCard(Modifier.fillMaxWidth().padding(18.dp), Cyan) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FlaskGraphic(130.dp, if (running) Purple else Cyan, running)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Tag(if (running) "REACTION ACTIVE" else "EXPERIMENT 01", if (running) Purple else Green)
                    Spacer(Modifier.height(8.dp))
                    Text("Acid–Base Titration", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Determine an unknown HCl concentration using NaOH.", color = Muted, fontSize = 11.sp)
                }
            }
        }
        LabSlider("NaOH concentration", concentration, "${String.format("%.2f", concentration)} M") { concentration = it }
        LabSlider("Sample volume", volume, "${(volume * 50).toInt()} mL") { volume = it }
        GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp), Green) {
            Text("CALCULATED ENDPOINT", color = Muted, fontSize = 10.sp)
            Text(
                if (running) "${String.format("%.1f", (volume * 50) / concentration)} mL NaOH" else "Ready for measurement",
                color = if (running) Green else White, fontSize = 22.sp, fontWeight = FontWeight.Bold
            )
            Text(if (running) "Indicator: pale pink · pH 7.1" else "Adjust the controls and begin.", color = Muted, fontSize = 11.sp)
        }
        Button(
            onClick = {
                running = !running
                onMessage(if (running) "Titration started — endpoint calculated" else "Experiment reset")
            },
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            colors = ButtonDefaults.buttonColors(if (running) Purple else Cyan),
            shape = RoundedCornerShape(12.dp)
        ) { Text(if (running) "Reset experiment" else "Start experiment", color = Night, fontWeight = FontWeight.Bold) }
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.SpaceAround) {
            listOf("⚗\nDistillation", "▥\nMeasurement", "⌬\nCrystallization").forEach {
                Text(it, color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun LabSlider(label: String, value: Float, display: String, onChange: (Float) -> Unit) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = White, fontSize = 13.sp)
            Text(display, color = Cyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Slider(
            value = value, onValueChange = onChange,
            valueRange = .1f..1f,
            colors = SliderDefaults.colors(thumbColor = Cyan, activeTrackColor = Cyan, inactiveTrackColor = Border)
        )
    }
}

@Composable
private fun ProgressScreen() {
    val topics = listOf("Organic Chemistry" to .85f, "Inorganic Chemistry" to .70f, "Physical Chemistry" to .65f, "Analytical Chemistry" to .60f)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenTitle("Your Progress", "Keep the momentum going")
        GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp), Cyan) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.fillMaxSize()) {
                        drawArc(Border, -90f, 360f, false, style = Stroke(12f, cap = StrokeCap.Round))
                        drawArc(Brush.sweepGradient(listOf(Purple, Cyan)), -90f, 259f, false, style = Stroke(12f, cap = StrokeCap.Round))
                    }
                    Text("72%", color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(22.dp))
                Column {
                    Text("Great job!", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("You’re on fire!  7 day streak", color = Cyan, fontSize = 12.sp)
                    Text("12 lessons completed this week", color = Muted, fontSize = 11.sp)
                }
            }
        }
        Text("TOPIC PROGRESS", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(20.dp, 24.dp, 20.dp, 10.dp))
        GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp), Purple) {
            topics.forEach { (name, value) ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name, color = White, fontSize = 12.sp)
                    Text("${(value * 100).toInt()}%", color = Cyan, fontSize = 12.sp)
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { value }, modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
                    color = Purple, trackColor = Border
                )
                Spacer(Modifier.height(14.dp))
            }
        }
        Text("ACHIEVEMENTS", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(20.dp, 24.dp, 20.dp, 10.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Achievement("✦", "Lab Master", Cyan, Modifier.weight(1f))
            Achievement("♛", "Quiz Champ", Orange, Modifier.weight(1f))
            Achievement("★", "Streak", Purple, Modifier.weight(1f))
        }
    }
}

@Composable
private fun Achievement(icon: String, title: String, color: Color, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(Panel), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, color = color, fontSize = 30.sp)
            Text(title, color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Unlocked", color = color, fontSize = 9.sp)
        }
    }
}

@Composable
private fun BottomNav(current: Screen, onSelect: (Screen) -> Unit) {
    val nav = listOf(
        Triple(Screen.Home, "⌂", "Home"),
        Triple(Screen.Explore, "⌕", "Explore"),
        Triple(Screen.Table, "▦", "Table"),
        Triple(Screen.Lab, "⚗", "Lab"),
        Triple(Screen.Quiz, "◈", "Quiz"),
        Triple(Screen.Progress, "◉", "Progress")
    )
    Box(Modifier.fillMaxWidth().background(Night).navigationBarsPadding().padding(horizontal = 9.dp, vertical = 6.dp)) {
        Card(
            Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(Color(0xEE06101C)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Border)
        ) {
            Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                nav.forEach { (screen, icon, label) ->
                    val active = current == screen
                    val navColor by animateColorAsState(if (active) Cyan else Muted, label = "nav-color")
                    val navScale by animateFloatAsState(if (active) 1.08f else .94f, label = "nav-scale")
                    Column(
                        Modifier.scale(navScale).clip(RoundedCornerShape(12.dp))
                            .background(if (active) Cyan.copy(alpha = .09f) else Color.Transparent)
                            .clickable { onSelect(screen) }.padding(horizontal = 9.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(icon.toString(), color = navColor, fontSize = 18.sp)
                        Text(label.toString(), color = navColor, fontSize = 7.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, action: String?, onClick: (() -> Unit)?) {
    Row(
        Modifier.fillMaxWidth().padding(20.dp, 22.dp, 20.dp, 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        if (action != null && onClick != null) Text(action, color = Cyan, fontSize = 11.sp, modifier = Modifier.clickable { onClick() })
    }
}

@Composable
internal fun GlowCard(modifier: Modifier, glow: Color, content: @Composable ColumnScope.() -> Unit) {
    val palette = LocalReadingPalette.current
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(palette.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, glow.copy(alpha = .36f))
    ) { Column(Modifier.padding(16.dp), content = content) }
}

@Composable
internal fun Tag(text: String, color: Color) {
    Text(
        text, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.clip(CircleShape).background(color.copy(alpha = .12f)).padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun InfoStat(label: String, value: String) {
    Column {
        Text(label, color = Muted, fontSize = 10.sp)
        Text(value, color = White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun AtomGraphic(size: androidx.compose.ui.unit.Dp, color: Color) {
    val transition = rememberInfiniteTransition(label = "atom")
    val rotation by transition.animateFloat(
        0f, 360f,
        animationSpec = infiniteRepeatable(tween(8000), repeatMode = RepeatMode.Restart),
        label = "orbit"
    )
    Canvas(Modifier.size(size)) {
        val c = center
        drawCircle(Brush.radialGradient(listOf(color, Purple, Color.Transparent), c, this.size.minDimension * .28f), radius = this.size.minDimension * .24f, center = c)
        repeat(3) { orbit ->
            val angle = rotation + orbit * 60f
            rotate(angle, c) {
                drawOval(color.copy(alpha = .42f), topLeft = Offset(c.x - this.size.minDimension * .45f, c.y - this.size.minDimension * .18f),
                    size = androidx.compose.ui.geometry.Size(this.size.minDimension * .9f, this.size.minDimension * .36f), style = Stroke(2f))
                val rad = Math.toRadians(angle.toDouble())
                val electron = Offset(c.x + cos(rad).toFloat() * this.size.minDimension * .42f, c.y + sin(rad).toFloat() * this.size.minDimension * .12f)
                drawCircle(Color.White, 4f, electron)
                drawCircle(color, 8f, electron, style = Stroke(3f))
            }
        }
        drawCircle(Color(0xFFFF7448), this.size.minDimension * .07f, c)
        drawCircle(Color.White.copy(alpha = .85f), this.size.minDimension * .025f, c - Offset(6f, 6f))
    }
}

@Composable
private fun MoleculeGraphic(modifier: Modifier, mode: Int, layout: String) {
    val transition = rememberInfiniteTransition(label = "molecule")
    val drift by transition.animateFloat(
        -8f, 8f, animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse), label = "drift"
    )
    Canvas(modifier) {
        val ethanolPoints = listOf(
            Offset(size.width * .28f, size.height * .55f),
            Offset(size.width * .46f, size.height * .43f),
            Offset(size.width * .64f, size.height * .55f),
            Offset(size.width * .77f, size.height * .39f),
            Offset(size.width * .20f, size.height * .35f),
            Offset(size.width * .33f, size.height * .75f),
            Offset(size.width * .49f, size.height * .21f),
            Offset(size.width * .62f, size.height * .77f),
            Offset(size.width * .88f, size.height * .55f)
        )
        val (basePoints, bonds) = when (layout) {
            "diatomic" -> listOf(
                Offset(size.width * .38f, size.height * .5f), Offset(size.width * .62f, size.height * .5f)
            ) to listOf(0 to 1)
            "bent" -> listOf(
                Offset(size.width * .5f, size.height * .4f), Offset(size.width * .3f, size.height * .68f),
                Offset(size.width * .7f, size.height * .68f)
            ) to listOf(0 to 1, 0 to 2)
            "linear3", "linear4" -> listOf(
                Offset(size.width * .25f, size.height * .5f), Offset(size.width * .5f, size.height * .5f),
                Offset(size.width * .75f, size.height * .5f)
            ) to listOf(0 to 1, 1 to 2)
            "pyramidal", "trigonal" -> listOf(
                Offset(size.width * .5f, size.height * .45f), Offset(size.width * .5f, size.height * .18f),
                Offset(size.width * .25f, size.height * .7f), Offset(size.width * .75f, size.height * .7f)
            ) to listOf(0 to 1, 0 to 2, 0 to 3)
            "tetrahedral", "square" -> listOf(
                Offset(size.width * .5f, size.height * .5f), Offset(size.width * .28f, size.height * .25f),
                Offset(size.width * .72f, size.height * .25f), Offset(size.width * .28f, size.height * .75f),
                Offset(size.width * .72f, size.height * .75f)
            ) to listOf(0 to 1, 0 to 2, 0 to 3, 0 to 4)
            "bipyramidal", "octahedral" -> listOf(
                Offset(size.width * .5f, size.height * .5f), Offset(size.width * .5f, size.height * .13f),
                Offset(size.width * .5f, size.height * .87f), Offset(size.width * .2f, size.height * .5f),
                Offset(size.width * .8f, size.height * .5f), Offset(size.width * .72f, size.height * .27f),
                Offset(size.width * .28f, size.height * .73f)
            ) to listOf(0 to 1, 0 to 2, 0 to 3, 0 to 4, 0 to 5, 0 to 6)
            "ring" -> {
                val ringPoints = (0..5).map { i ->
                    val angle = Math.toRadians(i * 60.0 - 90.0)
                    Offset(
                        size.width * .5f + cos(angle).toFloat() * size.width * .25f,
                        size.height * .5f + sin(angle).toFloat() * size.height * .31f
                    )
                }
                ringPoints to (0..5).map { it to (it + 1) % 6 }
            }
            else -> ethanolPoints to listOf(0 to 1, 1 to 2, 2 to 3, 0 to 4, 0 to 5, 1 to 6, 2 to 7, 3 to 8)
        }
        val points = basePoints.mapIndexed { i, p -> p + Offset(if (i % 2 == 0) drift else -drift, 0f) }
        bonds.forEach { (a, b) -> drawLine(Color(0xFF8BA2B8), points[a], points[b], if (mode == 1) 12f else 7f, StrokeCap.Round) }
        points.forEachIndexed { i, p ->
            val atomColor = when (i) { 3 -> Color(0xFFFF4F48); 0, 1, 2 -> Color(0xFF445062); else -> Color.White }
            val radius = if (mode == 1) (if (i in 0..3) 29f else 20f) else (if (i in 0..3) 22f else 13f)
            if (mode == 2) drawCircle(atomColor.copy(alpha = .18f), radius * 2.1f, p)
            drawCircle(Brush.radialGradient(listOf(Color.White, atomColor), p - Offset(6f, 6f), radius * 1.6f), radius, p)
        }
    }
}

@Composable
private fun FlaskGraphic(size: androidx.compose.ui.unit.Dp, liquid: Color, active: Boolean) {
    val transition = rememberInfiniteTransition(label = "flask")
    val bubble by transition.animateFloat(
        0f, 1f, animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Restart), label = "bubble"
    )
    Canvas(Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val flask = Path().apply {
            moveTo(w * .43f, h * .12f); lineTo(w * .57f, h * .12f)
            lineTo(w * .57f, h * .48f); lineTo(w * .82f, h * .87f)
            quadraticBezierTo(w * .85f, h * .94f, w * .73f, h * .94f)
            lineTo(w * .27f, h * .94f)
            quadraticBezierTo(w * .15f, h * .94f, w * .18f, h * .87f)
            lineTo(w * .43f, h * .48f); close()
        }
        drawPath(flask, Color(0xFFB6D9EA), style = Stroke(5f))
        val liquidPath = Path().apply {
            moveTo(w * .25f, h * .78f); quadraticBezierTo(w * .5f, h * .71f, w * .75f, h * .78f)
            lineTo(w * .82f, h * .9f); lineTo(w * .18f, h * .9f); close()
        }
        drawPath(liquidPath, Brush.verticalGradient(listOf(liquid.copy(alpha = .55f), liquid)))
        if (active) repeat(4) { i ->
            drawCircle(liquid.copy(alpha = 1f - bubble), 4f + i, Offset(w * (.35f + i * .1f), h * (.78f - bubble * .35f)))
        }
    }
}
