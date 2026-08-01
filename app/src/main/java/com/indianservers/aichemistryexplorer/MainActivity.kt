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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    FullTable, Categories, Trends, HeatMap, Compare, Notebook, ModuleLesson, TopicLesson, Search
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
    var screen by remember { mutableStateOf(Screen.Home) }
    var selected by remember { mutableStateOf(elements.first { it.symbol == "O" }) }
    var selectedModule by remember { mutableStateOf("Atomic Structure") }
    var selectedLesson by remember { mutableStateOf("Subatomic particles") }
    var completedLessons by remember { mutableStateOf(setOf<String>()) }
    var savedElements by remember { mutableStateOf(setOf<Int>()) }
    val allElements = remember { completePeriodicTable(elements) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val mainScreens = setOf(Screen.Home, Screen.Explore, Screen.Table, Screen.Lab, Screen.Quiz, Screen.Progress)
    BackHandler(screen !in mainScreens || screen != Screen.Home) { screen = Screen.Home }

    Scaffold(
        containerColor = Night,
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (screen in mainScreens) {
                BottomNav(screen) { screen = it }
            }
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF0B1830), Night),
                        center = Offset(600f, 100f),
                        radius = 1000f
                    )
                )
                .drawBehind {
                    val grid = Color(0xFF17304A).copy(alpha = .10f)
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
                        onExplore = { screen = Screen.Explore },
                        onTable = { screen = Screen.Table },
                        onReactions = { screen = Screen.Reactions },
                        onMolecule = { screen = Screen.Molecule },
                        onContinue = { screen = Screen.Explore },
                        onSearch = { screen = Screen.Search }
                    )
                    Screen.Search -> GlobalChemistrySearchScreen(
                        elements = allElements,
                        onBack = { screen = Screen.Home },
                        onElement = { selected = it; screen = Screen.Element },
                        onModule = { selectedModule = it; screen = Screen.ModuleLesson },
                        onDestination = { screen = it }
                    )
                    Screen.Explore -> ExploreScreen(
                        onOpen = { destination -> screen = destination },
                        onModule = { module -> selectedModule = module; screen = Screen.ModuleLesson }
                    )
                    Screen.ModuleLesson -> CurriculumModuleScreen(
                        moduleTitle = selectedModule,
                        completedLessons = completedLessons,
                        onBack = { screen = Screen.Explore },
                        onLesson = { lesson -> selectedLesson = lesson; screen = Screen.TopicLesson }
                    )
                    Screen.TopicLesson -> CurriculumLessonScreen(
                        moduleTitle = selectedModule,
                        lessonTitle = selectedLesson,
                        onBack = { screen = Screen.ModuleLesson },
                        onComplete = {
                            completedLessons = completedLessons + "$selectedModule/$selectedLesson"
                            screen = Screen.ModuleLesson
                        },
                        onQuiz = { screen = Screen.Quiz }
                    )
                    Screen.Table -> PeriodicHubScreen(
                        savedCount = savedElements.size,
                        onOpen = { screen = it },
                        onSelect = { selected = it; screen = Screen.Element }
                    )
                    Screen.FullTable -> EnhancedPeriodicTableScreen(
                        elements = allElements,
                        onBack = { screen = Screen.Table },
                        onSelect = { selected = it; screen = Screen.Element },
                        onCompare = { screen = Screen.Compare }
                    )
                    Screen.Categories -> ElementCategoriesScreen(
                        elements = allElements,
                        onBack = { screen = Screen.Table },
                        onSelect = { selected = it; screen = Screen.Element }
                    )
                    Screen.Trends -> PeriodicTrendsScreen(
                        onBack = { screen = Screen.Table },
                        onHeatMap = { screen = Screen.HeatMap }
                    )
                    Screen.HeatMap -> HeatMapScreen(allElements, onBack = { screen = Screen.Trends }) {
                        selected = it; screen = Screen.Element
                    }
                    Screen.Compare -> ElementComparisonScreen(allElements, onBack = { screen = Screen.Table }) {
                        selected = it; screen = Screen.Element
                    }
                    Screen.Notebook -> ElementNotebookScreen(
                        elements = allElements.filter { it.number in savedElements },
                        onBack = { screen = Screen.Table },
                        onSelect = { selected = it; screen = Screen.Element },
                        onRemove = { savedElements = savedElements - it.number }
                    )
                    Screen.Element -> ElementScreen(
                        element = selected,
                        isSaved = selected.number in savedElements,
                        onBack = { screen = Screen.Table },
                        onToggleSaved = {
                            savedElements = if (selected.number in savedElements) savedElements - selected.number else savedElements + selected.number
                            scope.launch { snackbar.showSnackbar(if (selected.number in savedElements) "${selected.symbol} saved to notebook" else "${selected.symbol} removed") }
                        }
                    ) { screen = Screen.Molecule }
                    Screen.Lab -> MultiPageLabScreen { scope.launch { snackbar.showSnackbar(it) } }
                    Screen.Quiz -> MultiPageQuizScreen()
                    Screen.Progress -> MultiPageProgressScreen()
                    Screen.Reactions -> ReactionsScreen(onBack = { screen = Screen.Explore })
                    Screen.Molecule -> MoleculeScreen(onBack = { screen = Screen.Explore })
                }
            }
        }
    }
}

@Composable
internal fun ScreenTitle(title: String, subtitle: String? = null, back: (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (back != null) {
            Text("‹", color = White, fontSize = 38.sp, modifier = Modifier.clickable { back() }.padding(end = 12.dp))
        }
        Column {
            Text(title, color = White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            subtitle?.let { Text(it, color = Muted, fontSize = 12.sp) }
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
                Modifier.fillMaxWidth().padding(20.dp, 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hello, Alex", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Ready to discover something new?", color = Muted, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⌕", color = Cyan, fontSize = 25.sp, modifier = Modifier.clickable { onSearch() }.padding(9.dp))
                    Box(
                        Modifier.size(42.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Purple, Cyan))),
                        contentAlignment = Alignment.Center
                    ) { Text("A", color = White, fontWeight = FontWeight.Bold) }
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
                        Tag("LEVEL 12", Purple)
                        Spacer(Modifier.height(8.dp))
                        Text("Master the elements", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Your chemistry universe is expanding.", color = Muted, fontSize = 12.sp)
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { .68f },
                            modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
                            color = Cyan, trackColor = Color(0xFF172942)
                        )
                        Text("2,450 / 3,600 XP", color = Cyan, fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
                    }
                }
            }
        }
        item { LearningPulseCard(onContinue) }
        item { SectionHeader("Explore by topic", "View all", onExplore) }
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
        item { SectionHeader("Continue learning", null, null) }
        item {
            GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp), Purple) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚭", fontSize = 30.sp, color = Purple)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Chemical Bonding", color = White, fontWeight = FontWeight.SemiBold)
                        Text("Chapter 4 · Ionic & covalent bonds", color = Muted, fontSize = 11.sp)
                        Spacer(Modifier.height(7.dp))
                        LinearProgressIndicator(
                            progress = { .45f }, modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = Purple, trackColor = Color(0xFF1B2037)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = onContinue,
                        colors = ButtonDefaults.buttonColors(Purple),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 13.dp, vertical = 4.dp)
                    ) { Text("Continue", fontSize = 11.sp) }
                }
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
    val topics = listOf(
        Triple("Atomic Structure", "Explore atoms, orbitals and electrons.", Cyan),
        Triple("Periodic Table", "Groups, blocks and repeating chemical trends.", Color(0xFF4D8BFF)),
        Triple("Chemical Bonding", "Ionic, covalent and metallic bonds.", Purple),
        Triple("States of Matter", "Gases, liquids, solids and phase behavior.", Color(0xFF73D0FF)),
        Triple("Organic Chemistry", "Carbon compounds, functional groups and mechanisms.", Purple),
        Triple("Chemical Reactions", "Equations, reaction types and redox change.", Orange),
        Triple("Thermodynamics", "Energy, enthalpy, entropy and spontaneity.", Color(0xFFC456FF)),
        Triple("Equilibrium and Solutions", "Concentration, pH, buffers and balance.", Green),
        Triple("Kinetics and Electrochemistry", "Rates, catalysts, cells and electrolysis.", Color(0xFF1DBBFF)),
        Triple("Analytical and Applied Chemistry", "Measurement, environment, medicine and industry.", Cyan)
    )
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Explore", "Concepts, reactions and compounds")
        OutlinedTextField(
            value = query, onValueChange = { query = it },
            placeholder = { Text("Search topics...", color = Muted) },
            leadingIcon = { Text("⌕", color = Cyan, fontSize = 22.sp) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
            items(topics.filter { it.first.contains(query, true) || it.second.contains(query, true) }) { topic ->
                Card(
                    Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable { onModule(topic.first) },
                    colors = CardDefaults.cardColors(Panel),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(54.dp).clip(CircleShape).background(topic.third.copy(alpha = .12f)),
                            contentAlignment = Alignment.Center
                        ) { Text(if (topic.first == "Chemical Reactions") "⇌" else "⚛", color = topic.third, fontSize = 25.sp) }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(topic.first, color = White, fontWeight = FontWeight.SemiBold)
                            Text(topic.second, color = Muted, fontSize = 11.sp)
                        }
                        Text("›", color = Muted, fontSize = 26.sp)
                    }
                }
            }
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
                AtomGraphic(150.dp, element.color)
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
        PropertyRow("Electron configuration", electronConfiguration(element.number))
        PropertyRow("Boiling point", element.boiling)
        PropertyRow("Electronegativity", if (element.symbol == "O") "3.44" else "—")
        Text("COMMON COMPOUNDS", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(20.dp, 18.dp, 20.dp, 8.dp))
        val compounds = when (element.symbol) {
            "O" -> listOf("H₂O", "CO₂", "O₂", "SiO₂")
            "C" -> listOf("CO₂", "CH₄", "CaCO₃", "C₂H₅OH")
            "Cu" -> listOf("CuO", "Cu₂O", "CuSO₄", "CuCl₂")
            "Fe" -> listOf("Fe₂O₃", "FeO", "FeS", "FeCl₃")
            "H" -> listOf("H₂O", "HCl", "NH₃", "CH₄")
            else -> listOf("${element.symbol}O", "${element.symbol}Cl", "${element.symbol}₂O", "${element.symbol}F")
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            compounds.forEach {
                Text(it, color = Cyan, fontSize = 12.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Panel).padding(10.dp))
            }
        }
        Button(
            onClick = onModel,
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            colors = ButtonDefaults.buttonColors(Cyan),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Open atomic model", color = Night, fontWeight = FontWeight.Bold) }
    }
}

private fun electronConfiguration(n: Int): String = when {
    n == 1 -> "1s¹"
    n == 2 -> "1s²"
    n in 3..10 -> "[He] 2s² 2p${(n - 4).coerceAtLeast(0)}"
    n in 11..18 -> "[Ne] 3s² 3p${(n - 12).coerceAtLeast(0)}"
    n in 19..30 -> "[Ar] 3d${(n - 20).coerceIn(0, 10)} 4s²"
    n == 35 -> "[Ar] 3d¹⁰ 4s² 4p⁵"
    n == 47 -> "[Kr] 4d¹⁰ 5s¹"
    n == 53 -> "[Kr] 4d¹⁰ 5s² 5p⁵"
    n == 79 -> "[Xe] 4f¹⁴ 5d¹⁰ 6s¹"
    else -> "Condensed orbital notation"
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
    var selected by remember { mutableStateOf<String?>(null) }
    val reactions = listOf(
        Triple("Combustion of methane", "CH₄ + 2O₂ → CO₂ + 2H₂O", Orange),
        Triple("Neutralization", "HCl + NaOH → NaCl + H₂O", Cyan),
        Triple("Synthesis of ammonia", "N₂ + 3H₂ ⇌ 2NH₃", Purple),
        Triple("Silver chloride precipitation", "AgNO₃ + NaCl → AgCl↓ + NaNO₃", Green),
        Triple("Hydrogen peroxide decomposition", "2H₂O₂ → 2H₂O + O₂", Color(0xFF3D8CFF))
    )
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Reactions", "Balanced equations & mechanisms", onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
            items(reactions) { reaction ->
                GlowCard(
                    Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable {
                        selected = if (selected == reaction.first) null else reaction.first
                    }, reaction.third
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (reaction.first.contains("Combustion")) "♨" else "⌬", color = reaction.third, fontSize = 26.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(reaction.first, color = White, fontWeight = FontWeight.SemiBold)
                            Text(reaction.second, color = White.copy(alpha = .8f), fontSize = 13.sp)
                            if (selected == reaction.first) {
                                Spacer(Modifier.height(9.dp))
                                Text(
                                    "Atoms are conserved on both sides. Tap again to collapse this reaction.",
                                    color = Muted, fontSize = 11.sp
                                )
                            }
                        }
                        Tag("BALANCED", Green)
                    }
                }
            }
        }
    }
}

@Composable
private fun MoleculeScreen(onBack: () -> Unit) {
    var model by remember { mutableStateOf(0) }
    val modes = listOf("Ball & stick", "Space fill", "Electron cloud")
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenTitle("Molecule Viewer", "Interactive ethanol model", onBack)
        Text("Ethanol", color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp))
        Text("C₂H₅OH", color = Cyan, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 20.dp))
        MoleculeGraphic(Modifier.fillMaxWidth().height(320.dp), model)
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
                InfoStat("Molar mass", "46.07 g/mol")
                InfoStat("Boiling point", "78.37 °C")
            }
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoStat("Density", "0.789 g/cm³")
                InfoStat("Solubility", "Miscible")
            }
        }
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
private fun QuizScreen() {
    data class Question(val prompt: String, val answers: List<String>, val correct: Int, val explanation: String)
    val questions = remember {
        listOf(
            Question("Which element has the highest electronegativity?", listOf("Oxygen", "Fluorine", "Chlorine", "Nitrogen"), 1, "Fluorine attracts shared electrons more strongly than any other element."),
            Question("What is the hybridization of carbon in methane?", listOf("sp", "sp²", "sp³", "sp³d"), 2, "Four equivalent sigma bonds create tetrahedral sp³ hybridization."),
            Question("Which particle defines an element's identity?", listOf("Electron", "Neutron", "Proton", "Photon"), 2, "The number of protons is the atomic number and uniquely identifies an element."),
            Question("A solution with pH 3 is…", listOf("Basic", "Neutral", "Acidic", "Saturated"), 2, "Any aqueous solution below pH 7 is acidic.")
        )
    }
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    val q = questions[index]
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenTitle("Quiz Zone", "Level 12 · ${score * 100 + 1250} XP")
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = { (index + 1f) / questions.size },
                modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape),
                color = Cyan, trackColor = Border
            )
            Text("  ${index + 1}/${questions.size}", color = Muted, fontSize = 11.sp)
        }
        Spacer(Modifier.height(24.dp))
        GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp), Purple) {
            Tag("MEDIUM", Orange)
            Spacer(Modifier.height(15.dp))
            Text(q.prompt, color = White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp)
        }
        Column(Modifier.padding(18.dp, 12.dp)) {
            q.answers.forEachIndexed { answerIndex, answer ->
                val chosen = selected == answerIndex
                val correct = selected >= 0 && answerIndex == q.correct
                val color = when {
                    correct -> Green
                    chosen -> Color(0xFFFF5D76)
                    else -> Border
                }
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 9.dp).clip(RoundedCornerShape(12.dp))
                        .background(color.copy(alpha = if (chosen || correct) .13f else .28f))
                        .border(1.dp, color, RoundedCornerShape(12.dp))
                        .clickable(enabled = selected < 0) {
                            selected = answerIndex
                            if (answerIndex == q.correct) score++
                        }.padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(('A' + answerIndex).toString(), color = color, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(15.dp))
                    Text(answer, color = White, modifier = Modifier.weight(1f))
                    if (correct) Text("✓", color = Green, fontWeight = FontWeight.Bold)
                }
            }
        }
        if (selected >= 0) {
            GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp), if (selected == q.correct) Green else Orange) {
                Text(if (selected == q.correct) "Correct!" else "Good try", color = if (selected == q.correct) Green else Orange, fontWeight = FontWeight.Bold)
                Text(q.explanation, color = Muted, fontSize = 12.sp)
            }
            Button(
                onClick = {
                    index = (index + 1) % questions.size
                    selected = -1
                    if (index == 0) score = 0
                },
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                colors = ButtonDefaults.buttonColors(Purple),
                shape = RoundedCornerShape(12.dp)
            ) { Text(if (index == questions.lastIndex) "Finish quiz" else "Next question") }
        }
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
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(Panel),
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
private fun MoleculeGraphic(modifier: Modifier, mode: Int) {
    val transition = rememberInfiniteTransition(label = "molecule")
    val drift by transition.animateFloat(
        -8f, 8f, animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse), label = "drift"
    )
    Canvas(modifier) {
        val points = listOf(
            Offset(size.width * .28f, size.height * .55f),
            Offset(size.width * .46f, size.height * .43f),
            Offset(size.width * .64f, size.height * .55f),
            Offset(size.width * .77f, size.height * .39f),
            Offset(size.width * .20f, size.height * .35f),
            Offset(size.width * .33f, size.height * .75f),
            Offset(size.width * .49f, size.height * .21f),
            Offset(size.width * .62f, size.height * .77f),
            Offset(size.width * .88f, size.height * .55f)
        ).mapIndexed { i, p -> p + Offset(if (i % 2 == 0) drift else -drift, 0f) }
        val bonds = listOf(0 to 1, 1 to 2, 2 to 3, 0 to 4, 0 to 5, 1 to 6, 2 to 7, 3 to 8)
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
