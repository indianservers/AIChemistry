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
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.indianservers.aichemistryexplorer.ui.theme.AIChemistryExplorerTheme
import kotlinx.coroutines.delay
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
    Splash, Home, Explore, Table, Lab, Quiz, Progress, Element, Reactions, Molecule,
    FullTable, Categories, Trends, HeatMap, Compare, Notebook, PeriodicStudio,
    ModuleLesson, TopicLesson, Search, About
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
    var reducedMotion by remember { mutableStateOf(readingPrefs.getBoolean("reduced_motion", false)) }
    var readingMenu by remember { mutableStateOf(false) }
    val palette = readingPalettes[readingTheme]
    val density = LocalDensity.current
    var screen by remember { mutableStateOf(Screen.Splash) }
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
    BackHandler(screen != Screen.Home && screen != Screen.Splash) { navigateBack() }

    CompositionLocalProvider(
        LocalReadingPalette provides palette,
        LocalDensity provides Density(density.density, density.fontScale * fontScale),
        LocalReducedMotion provides reducedMotion,
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
                    HorizontalDivider(color = palette.outline)
                    DropdownMenuItem(
                        text = { Text("${if (reducedMotion) "✓" else "○"} Reduced motion", color = if (reducedMotion) Green else White) },
                        onClick = {
                            reducedMotion = !reducedMotion
                            readingPrefs.edit().putBoolean("reduced_motion", reducedMotion).apply()
                        }
                    )
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
                    Screen.Splash -> ChemistrySplashScreen { screen = Screen.Home }
                    Screen.Home -> HomeScreen(
                        onExplore = { navigateTo(Screen.Explore) },
                        onTable = { navigateTo(Screen.Table) },
                        onLab = { navigateTo(Screen.Lab) },
                        onQuiz = { navigateTo(Screen.Quiz) },
                        onReactions = { navigateTo(Screen.Reactions) },
                        onMolecule = { navigateTo(Screen.Molecule) },
                        onContinue = { navigateTo(Screen.Explore) },
                        onSearch = { navigateTo(Screen.Search) },
                        onAbout = { navigateTo(Screen.About) }
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
                    Screen.About -> AppVersionScreen(onBack = { navigateBack() })
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
            IconChip(ChemIcons.Back, "Go back", White, Modifier.padding(end = 10.dp)) { resolvedBack() }
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            subtitle?.let { Breadcrumb(it) }
        }
        if (home != null) {
            IconChip(ChemIcons.Home, "Go home", Cyan) { home() }
        }
    }
}

@Composable
private fun ChemistrySplashScreen(finish: () -> Unit) {
    val reduceMotion = LocalReducedMotion.current
    val transition = rememberInfiniteTransition(label = "splash")
    val pulse by transition.animateFloat(
        .88f,
        1.05f,
        animationSpec = infiniteRepeatable(tween(850), RepeatMode.Reverse),
        label = "splash-pulse"
    )
    val orbit by transition.animateFloat(
        0f,
        360f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Restart),
        label = "splash-orbit"
    )
    val iconScale = if (reduceMotion) 1f else pulse
    val rotation = if (reduceMotion) 18f else orbit
    LaunchedEffect(Unit) {
        delay(3000)
        finish()
    }
    Box(
        Modifier.fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0xFF073B92), Night), radius = 1200f))
            .drawBehind {
                repeat(34) { index ->
                    val x = size.width * ((index * 37 % 100) / 100f)
                    val y = size.height * ((index * 61 % 100) / 100f)
                    val alpha = .10f + (index % 4) * .035f
                    drawCircle(Cyan.copy(alpha = alpha), 2.5f + (index % 5), Offset(x, y))
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.splash_molecule),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopStart).padding(48.dp).size(70.dp).alpha(.72f).scale(if (reduceMotion) 1f else .9f + (pulse - .88f))
        )
        Image(
            painter = painterResource(R.drawable.splash_microscope),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd).padding(54.dp, 92.dp).size(82.dp).alpha(.68f)
        )
        Image(
            painter = painterResource(R.drawable.splash_test_tubes),
            contentDescription = null,
            modifier = Modifier.align(Alignment.BottomStart).padding(44.dp, 0.dp, 0.dp, 92.dp).size(88.dp).alpha(.70f)
        )
        Image(
            painter = painterResource(R.drawable.splash_pipette),
            contentDescription = null,
            modifier = Modifier.align(Alignment.BottomEnd).padding(0.dp, 0.dp, 44.dp, 120.dp).size(72.dp).alpha(.68f)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 28.dp)) {
            Box(Modifier.size(230.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    rotate(rotation) {
                        drawCircle(Cyan.copy(alpha = .20f), size.minDimension * .42f, center, style = Stroke(4f))
                        drawArc(Purple.copy(alpha = .85f), 10f, 112f, false, style = Stroke(5f, cap = StrokeCap.Round))
                        drawArc(Cyan.copy(alpha = .9f), 190f, 98f, false, style = Stroke(5f, cap = StrokeCap.Round))
                    }
                    drawCircle(Cyan.copy(alpha = .18f), size.minDimension * .44f, center)
                }
                Image(
                    painter = painterResource(R.drawable.splash_launcher_icon),
                    contentDescription = "Chemistry Explorer",
                    modifier = Modifier.size(178.dp).scale(iconScale).clip(CircleShape)
                )
            }
            Text("Chemistry Explorer", color = White, fontSize = 31.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Text("Explore · Experiment · Discover", color = Cyan, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            Image(
                painter = painterResource(R.drawable.splash_reaction_equation),
                contentDescription = null,
                modifier = Modifier.padding(top = 22.dp).size(126.dp, 42.dp).alpha(.75f)
            )
        }
    }
}

@Composable
private fun AppVersionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as ChemistryExplorerApplication
    val repository = remember(app) { app.lessonPageRepository }
    val scope = rememberCoroutineScope()
    val lessonAudits by repository.observeSchoolLessonAudits().collectAsState(initial = emptyList())
    var seedingLessons by remember { mutableStateOf(false) }
    val info = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }.getOrNull()
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
        item {
            ScreenTitle("App Version", "Launch readiness and build details", onBack)
            GlowCard(Modifier.fillMaxWidth(), Cyan) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.splash_launcher_icon),
                        contentDescription = null,
                        modifier = Modifier.size(78.dp).clip(CircleShape)
                    )
                    Column(Modifier.weight(1f).padding(start = 14.dp)) {
                        Text("Chemistry Explorer", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Version ${info?.versionName ?: "1.0"} · Build ${info?.longVersionCode ?: 1L}", color = Cyan, fontSize = 12.sp)
                        Text(context.packageName, color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
            AboutInfoCard("Release Checklist", "Splash screen, launcher icon, offline MCQs, nuclear chemistry path, lab safety disclaimer and report export are present.", Green)
            AboutInfoCard("Safety Notice", "Virtual Lab simulations are for guided learning only and are not a substitute for supervised laboratory safety training.", Orange)
            LessonDatabaseAuditCard(
                audits = lessonAudits,
                seeding = seedingLessons,
                seedAll = {
                    scope.launch {
                        seedingLessons = true
                        repository.replaceAllClassSixToTenLessons()
                        seedingLessons = false
                    }
                }
            )
        }
    }
}

@Composable
private fun LessonDatabaseAuditCard(
    audits: List<LessonPageAudit>,
    seeding: Boolean,
    seedAll: () -> Unit
) {
    val ready = audits.count { it.isReady }
    val failing = audits.filterNot { it.isReady }
    GlowCard(Modifier.fillMaxWidth().padding(top = 12.dp), if (failing.isEmpty() && audits.isNotEmpty()) Green else Orange) {
        Text("LESSON DATABASE QA", color = if (failing.isEmpty() && audits.isNotEmpty()) Green else Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(
            if (audits.isEmpty()) "No Class 6-10 SQLite lesson pages detected yet."
            else "$ready/${audits.size} Class 6-12 lessons ready in lesson_pages.",
            color = White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            "Checks: five phase pages, correct page titles, no duplicate page_no rows, useful length and school-level wording.",
            color = Muted,
            fontSize = 10.sp,
            lineHeight = 15.sp,
            modifier = Modifier.padding(top = 5.dp)
        )
        Button(
            onClick = seedAll,
            enabled = !seeding,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            colors = ButtonDefaults.buttonColors(if (failing.isEmpty() && audits.isNotEmpty()) Green else Orange),
            shape = RoundedCornerShape(11.dp)
        ) {
            Text(if (seeding) "Seeding lessons..." else "Seed / repair Class 6-12 SQLite lessons", color = Night, fontWeight = FontWeight.Bold)
        }
        if (failing.isNotEmpty()) {
            Text("NEEDS ATTENTION", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 13.dp))
            failing.take(6).forEach { audit ->
                LessonAuditRow(audit)
            }
            if (failing.size > 6) {
                Text("+${failing.size - 6} more lessons hidden", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 7.dp))
            }
        } else if (audits.isNotEmpty()) {
            Text("All school lessons have clean SQLite pages.", color = Green, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
        }
    }
}

@Composable
private fun LessonAuditRow(audit: LessonPageAudit) {
    Column(
        Modifier.fillMaxWidth()
            .padding(top = 8.dp)
            .background(Panel2, RoundedCornerShape(10.dp))
            .border(1.dp, Border, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(audit.lessonTitle, color = White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text("${audit.pageCount}/5", color = Orange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Text(audit.moduleTitle, color = Muted, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        audit.issues.take(2).forEach { issue ->
            Text(issue, color = Orange, fontSize = 9.sp, lineHeight = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun AboutInfoCard(title: String, body: String, color: Color) {
    GlowCard(Modifier.fillMaxWidth().padding(top = 12.dp), color) {
        Text(title.uppercase(), color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(body, color = White, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun HomeScreen(
    onExplore: () -> Unit, onTable: () -> Unit, onLab: () -> Unit, onQuiz: () -> Unit,
    onReactions: () -> Unit, onMolecule: () -> Unit, onContinue: () -> Unit, onSearch: () -> Unit, onAbout: () -> Unit
) {
    val context = LocalContext.current
    val launchPrefs = remember { context.getSharedPreferences("launch_readiness", 0) }
    var showFirstMission by remember { mutableStateOf(!launchPrefs.getBoolean("first_mission_seen", false)) }
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
        item { HomeAnimatedChemistryHero(onExplore, onTable, onLab) }
        if (showFirstMission) {
            item {
                FirstRunSampleMission(
                    start = {
                        launchPrefs.edit().putBoolean("first_mission_seen", true).apply()
                        showFirstMission = false
                        onContinue()
                    },
                    dismiss = {
                        launchPrefs.edit().putBoolean("first_mission_seen", true).apply()
                        showFirstMission = false
                    }
                )
            }
        }
        item { TodayChemistryMissionCard(onContinue) }
        item { DashboardSnapshot(onContinue) }
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DailyActionCard("Daily element", "Oxygen", "Symbol, shells and uses", Cyan, Modifier.weight(1f), onTable)
                DailyActionCard("Daily quiz", "Bonding check", "10 MCQs, weak-topic focus", Purple, Modifier.weight(1f), onQuiz)
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DailyActionCard("Lab challenge", "Acid-base titration", "Hit endpoint in 3 trials", Green, Modifier.weight(1f), onLab)
                DailyActionCard("Nuclear path", "Half-life", "Decay, safety and applications", Orange, Modifier.weight(1f), onContinue)
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
            Text(
                "Version and launch info",
                color = Muted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().clickable { onAbout() }.padding(bottom = 8.dp)
            )
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
                TopicCard("Nuclear\nChemistry", "NUC", Green, Modifier.weight(1f), onExplore)
            }
        }
    }
}

@Composable
private fun HomeAnimatedChemistryHero(onExplore: () -> Unit, onTable: () -> Unit, onLab: () -> Unit) {
    val reduceMotion = LocalReducedMotion.current
    val transition = rememberInfiniteTransition(label = "home-hero")
    val rotation by transition.animateFloat(
        0f,
        360f,
        animationSpec = infiniteRepeatable(tween(7200), RepeatMode.Restart),
        label = "home-hero-orbit"
    )
    val shimmer by transition.animateFloat(
        .18f,
        .62f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "home-hero-shimmer"
    )
    val phase = if (reduceMotion) 48f else rotation
    val glow = if (reduceMotion) .32f else shimmer
    GlowCard(Modifier.fillMaxWidth().padding(18.dp, 4.dp, 18.dp, 8.dp), Cyan) {
        Box(Modifier.fillMaxWidth().height(214.dp)) {
            ChemistryDashboardScene(Modifier.matchParentSize().alpha(.76f))
            Canvas(Modifier.matchParentSize()) {
                val center = Offset(size.width * .28f, size.height * .48f)
                drawCircle(Cyan.copy(alpha = glow * .28f), size.minDimension * .36f, center)
                repeat(4) { orbit ->
                    rotate(phase + orbit * 47f, center) {
                        drawOval(
                            listOf(Cyan, Purple, Green, Orange)[orbit].copy(alpha = .34f),
                            topLeft = Offset(center.x - size.minDimension * (.28f + orbit * .035f), center.y - size.minDimension * (.10f + orbit * .014f)),
                            size = androidx.compose.ui.geometry.Size(size.minDimension * (.56f + orbit * .07f), size.minDimension * (.20f + orbit * .028f)),
                            style = Stroke(2.5f, cap = StrokeCap.Round)
                        )
                        drawCircle(White.copy(alpha = .92f), 4.5f, Offset(center.x + size.minDimension * (.28f + orbit * .035f), center.y))
                    }
                }
                drawCircle(Brush.radialGradient(listOf(Cyan, Purple.copy(alpha = .8f), Color.Transparent), center, size.minDimension * .18f), size.minDimension * .13f, center)
                repeat(10) { index ->
                    val angle = Math.toRadians((phase + index * 36f).toDouble())
                    val x = size.width * (.06f + index * .09f) + cos(angle).toFloat() * 7f
                    val y = size.height * (.18f + ((index * 29) % 58) / 100f) + sin(angle).toFloat() * 7f
                    drawCircle(listOf(Cyan, Purple, Green)[index % 3].copy(alpha = .26f), 8f, Offset(x, y))
                }
            }
            Image(
                painter = painterResource(R.drawable.splash_launcher_icon),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 22.dp).size(112.dp).clip(CircleShape).alpha(.82f)
            )
            Column(
                Modifier.align(Alignment.CenterEnd).fillMaxWidth(.58f).padding(end = 14.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("LIVE CHEMISTRY HUB", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("Learn with moving atoms, labs and trends", color = White, fontSize = 20.sp, fontWeight = FontWeight.Black, lineHeight = 24.sp)
                Text("Half-life mission, Oxygen detail and titration challenge are ready.", color = Muted, fontSize = 10.sp, lineHeight = 14.sp, modifier = Modifier.padding(top = 6.dp))
                Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HomeHeroChip("Learn", Cyan, onExplore)
                    HomeHeroChip("O", Purple, onTable)
                    HomeHeroChip("Lab", Green, onLab)
                }
            }
        }
    }
}

@Composable
private fun HomeHeroChip(label: String, color: Color, onClick: () -> Unit) {
    Text(
        label,
        color = Night,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clip(CircleShape)
            .background(color)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}

@Composable
private fun FirstRunSampleMission(start: () -> Unit, dismiss: () -> Unit) {
    GlowCard(Modifier.fillMaxWidth().padding(18.dp, 10.dp, 18.dp, 0.dp), Green) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.splash_test_tubes),
                contentDescription = null,
                modifier = Modifier.size(62.dp).alpha(.88f)
            )
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text("FIRST-RUN SAMPLE MISSION", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("Half-life · Oxygen · Titration", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Start with nuclear decay, inspect Oxygen, then run the titration lab.", color = Muted, fontSize = 10.sp)
                Row(Modifier.padding(top = 9.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Start", color = Night, fontWeight = FontWeight.Bold, modifier = Modifier.clip(CircleShape).background(Green).clickable { start() }.padding(horizontal = 14.dp, vertical = 7.dp))
                    Text("Later", color = Cyan, fontWeight = FontWeight.Bold, modifier = Modifier.clip(CircleShape).border(1.dp, Cyan.copy(alpha = .45f), CircleShape).clickable { dismiss() }.padding(horizontal = 14.dp, vertical = 7.dp))
                }
            }
        }
    }
}

@Composable
private fun TodayChemistryMissionCard(onContinue: () -> Unit) {
    val reduceMotion = LocalReducedMotion.current
    val transition = rememberInfiniteTransition(label = "mission")
    val sweep by transition.animateFloat(
        220f,
        285f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "mission-progress"
    )
    val glow by transition.animateFloat(
        .18f,
        .46f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "mission-glow"
    )
    val progress = if (reduceMotion) 252f else sweep
    val glowAlpha = if (reduceMotion) .26f else glow
    GlowCard(Modifier.fillMaxWidth().padding(18.dp, 12.dp, 18.dp, 0.dp).clickable { onContinue() }, Cyan) {
        Box(Modifier.fillMaxWidth()) {
            ChemistryDashboardScene(Modifier.matchParentSize().alpha(.55f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(82.dp), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.fillMaxSize()) {
                        drawCircle(Cyan.copy(alpha = glowAlpha), size.minDimension * .45f, center)
                        drawArc(Border, -90f, 360f, false, style = Stroke(8f, cap = StrokeCap.Round))
                        drawArc(
                            Brush.sweepGradient(listOf(Purple, Cyan, Green)),
                            -90f,
                            progress,
                            false,
                            style = Stroke(8f, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("70%", color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("READY", color = Muted, fontSize = 7.sp)
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("TODAY'S CHEMISTRY MISSION", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("Master chemical bonding", color = White, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                    Text("Continue path: Atomic Structure -> Bonding -> Molecular Geometry", color = Muted, fontSize = 10.sp)
                    Row(Modifier.padding(top = 9.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        listOf(.9f, .55f, .75f, .35f, .7f, .5f, .85f).forEachIndexed { index, value ->
                            Box(
                                Modifier.size(14.dp, 9.dp).background(
                                    if (index == 6) Cyan else Purple.copy(alpha = .25f + value * .55f),
                                    RoundedCornerShape(3.dp)
                                )
                            )
                        }
                    }
                }
                Text(">", color = Cyan, fontSize = 28.sp, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
private fun DashboardSnapshot(onContinue: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(18.dp, 12.dp, 18.dp, 10.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        DashboardMetric("Streak", "7 days", Cyan, Modifier.weight(1f))
        DashboardMetric("XP", "2,450", Orange, Modifier.weight(1f))
        DashboardMetric("Weak spot", "Equilibrium", Purple, Modifier.weight(1.2f), onContinue)
    }
}

@Composable
private fun DashboardMetric(label: String, value: String, color: Color, modifier: Modifier, click: (() -> Unit)? = null) {
    Column(
        modifier
            .height(78.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Panel.copy(alpha = .92f))
            .border(1.dp, color.copy(alpha = .38f), RoundedCornerShape(14.dp))
            .then(if (click != null) Modifier.clickable { click() } else Modifier)
            .padding(10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(label.uppercase(), color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(value, color = color, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun DailyActionCard(
    label: String,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val reduceMotion = LocalReducedMotion.current
    val transition = rememberInfiniteTransition(label = "daily-card-$label")
    val drift by transition.animateFloat(
        0f,
        1f,
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse),
        label = "daily-card-drift"
    )
    val t = if (reduceMotion) .35f else drift
    Card(
        modifier.height(116.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Panel),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = .42f))
    ) {
        Box(Modifier.fillMaxSize()) {
            Canvas(Modifier.matchParentSize().alpha(.58f)) {
                val center = Offset(size.width * (.82f - t * .10f), size.height * (.20f + t * .16f))
                drawCircle(color.copy(alpha = .08f + t * .08f), 32f, center)
                rotate(38f + t * 90f, center) {
                    drawOval(
                        color.copy(alpha = .28f),
                        topLeft = Offset(center.x - 35f, center.y - 11f),
                        size = androidx.compose.ui.geometry.Size(70f, 22f),
                        style = Stroke(2f)
                    )
                }
                drawCircle(White.copy(alpha = .76f), 3.5f, Offset(center.x + 31f, center.y))
            }
            Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(28.dp).clip(CircleShape).background(color.copy(alpha = .14f)), contentAlignment = Alignment.Center) {
                        Text(ChemIcons.Spark, color = color, fontSize = 15.sp)
                    }
                    Text(label.uppercase(), color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 7.dp), maxLines = 1)
                }
                Column {
                    Text(title, color = White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(subtitle, color = Muted, fontSize = 9.sp, lineHeight = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun ChemistryDashboardScene(modifier: Modifier = Modifier) {
    val reduceMotion = LocalReducedMotion.current
    val transition = rememberInfiniteTransition(label = "dashboard-scene")
    val phase by transition.animateFloat(
        0f,
        1f,
        animationSpec = infiniteRepeatable(tween(9000), RepeatMode.Restart),
        label = "particle-phase"
    )
    val t = if (reduceMotion) .18f else phase
    Canvas(modifier) {
        val gridColor = Cyan.copy(alpha = .08f)
        var x = 0f
        while (x < size.width) {
            drawLine(gridColor, Offset(x, 0f), Offset(x + size.width * .18f, size.height), 1f)
            x += size.width / 6f
        }
        var y = 0f
        while (y < size.height) {
            drawLine(Purple.copy(alpha = .06f), Offset(0f, y), Offset(size.width, y), 1f)
            y += size.height / 5f
        }
        val center = Offset(size.width * .78f, size.height * .42f)
        repeat(3) { orbit ->
            val radiusX = size.minDimension * (.18f + orbit * .06f)
            val radiusY = size.minDimension * (.07f + orbit * .035f)
            rotate(orbit * 58f + t * 360f, center) {
                drawOval(
                    color = listOf(Cyan, Purple, Green)[orbit].copy(alpha = .26f),
                    topLeft = Offset(center.x - radiusX, center.y - radiusY),
                    size = androidx.compose.ui.geometry.Size(radiusX * 2f, radiusY * 2f),
                    style = Stroke(2f)
                )
            }
        }
        repeat(18) { index ->
            val angle = t * 2f * Math.PI + index * .77
            val px = size.width * ((index * 37 % 100) / 100f) + cos(angle).toFloat() * 8f
            val py = size.height * ((index * 53 % 100) / 100f) + sin(angle).toFloat() * 6f
            val color = when (index % 4) {
                0 -> Cyan
                1 -> Purple
                2 -> Green
                else -> Orange
            }
            drawCircle(color.copy(alpha = .20f), 9f + (index % 3) * 2f, Offset(px, py))
            drawCircle(color.copy(alpha = .72f), 2.5f, Offset(px, py))
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
    var compact by remember { mutableStateOf(false) }
    var gridView by remember { mutableStateOf(false) }
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
        SyllabusTagRow(level) { selected ->
            level = selected
            category = "All categories"
            subcategory = "All subcategories"
        }
        SearchField(
            query = query,
            onQueryChange = { query = it },
            placeholder = "Search modules or concepts...",
            modifier = Modifier.padding(horizontal = 18.dp)
        )
        CurriculumNavigatorSummary(level, category, subcategory)
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
        if (query.isNotBlank() || level != "All levels" || category != "All categories" || subcategory != "All subcategories") {
            Text(
                "Clear all filters",
                color = Cyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    query = ""
                    level = "All levels"
                    category = "All categories"
                    subcategory = "All subcategories"
                }.padding(horizontal = 20.dp, vertical = 5.dp)
            )
        }
        CurriculumViewControls(compact, gridView, onCompact = { compact = !compact }, onGrid = { gridView = !gridView })
        AdaptiveLearningPathCard(topics, onModule)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
            if (topics.isEmpty()) {
                item { EmptyState("No modules match", "Clear filters or search for a broader chemistry term.", ChemIcons.Search, Cyan) }
            }
            if (gridView) {
                items(topics.chunked(2)) { rowTopics ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        rowTopics.forEach { topic ->
                            CurriculumModuleGridCard(topic, compact, Modifier.weight(1f)) { onModule(topic.title) }
                        }
                        if (rowTopics.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(10.dp))
                }
            } else {
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
                            CurriculumMetadataRow(topic, compact)
                            if (!compact) PrerequisiteMapCard(topic)
                        }
                        Text("›", color = Muted, fontSize = 26.sp)
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun SyllabusTagRow(selected: String, onSelect: (String) -> Unit) {
    val tags = listOf(
        "Class 6-8" to "Class 6-8",
        "Class 9-10" to "Class 9-10",
        "Class 11-12" to "Class 11-12",
        "UG" to "Undergraduate",
        "PG" to "Postgraduate"
    )
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        tags.forEach { (label, value) ->
            val active = selected == value
            Text(
                label,
                color = if (active) Night else Cyan,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.clip(CircleShape)
                    .background(if (active) Cyan else Cyan.copy(alpha = .08f))
                    .border(1.dp, Cyan.copy(alpha = if (active) .9f else .36f), CircleShape)
                    .clickable { onSelect(if (active) "All levels" else value) }
                    .padding(horizontal = 11.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun CurriculumNavigatorSummary(level: String, category: String, subcategory: String) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigatorStep("Level", level.removePrefix("All "), Cyan, Modifier.weight(1f))
        Text(">", color = Muted, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 6.dp))
        NavigatorStep("Subject", category.removePrefix("All "), Purple, Modifier.weight(1f))
        Text(">", color = Muted, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 6.dp))
        NavigatorStep("Topic", subcategory.removePrefix("All "), Green, Modifier.weight(1f))
    }
}

@Composable
private fun NavigatorStep(label: String, value: String, color: Color, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = .08f))
            .border(1.dp, color.copy(alpha = .28f), RoundedCornerShape(10.dp))
            .padding(horizontal = 9.dp, vertical = 7.dp)
    ) {
        Text(label.uppercase(), color = color, fontSize = 7.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(if (value.isBlank()) "Any" else value, color = White, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun CurriculumViewControls(compact: Boolean, gridView: Boolean, onCompact: () -> Unit, onGrid: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TogglePill(if (compact) "Compact" else "Comfortable", compact, Cyan, Modifier.weight(1f), onCompact)
        TogglePill(if (gridView) "Grid view" else "List view", gridView, Purple, Modifier.weight(1f), onGrid)
    }
}

@Composable
private fun TogglePill(label: String, active: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Text(
        label,
        color = if (active) Night else White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier.clip(RoundedCornerShape(10.dp))
            .background(if (active) color else Panel)
            .border(1.dp, if (active) color else Border, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 9.dp)
    )
}

@Composable
private fun AdaptiveLearningPathCard(topics: List<CurriculumModule>, onModule: (String) -> Unit) {
    val recommended = topics.firstOrNull { it.title.contains("Nuclear Chemistry", true) }
        ?: topics.firstOrNull { it.title.contains("Equilibrium", true) }
        ?: topics.firstOrNull { it.category.contains("Physical", true) }
        ?: topics.firstOrNull()
        ?: return
    GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 7.dp), Orange) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(Orange.copy(alpha = .14f)), contentAlignment = Alignment.Center) {
                Text("AI", color = Orange, fontSize = 13.sp, fontWeight = FontWeight.Black)
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text("ADAPTIVE LEARNING PATH", color = Orange, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                Text(recommended.title, color = White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(adaptivePathReason(recommended), color = Muted, fontSize = 9.sp, maxLines = 2)
            }
            Text("Open", color = Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onModule(recommended.title) }.padding(8.dp))
        }
    }
}

private fun adaptivePathReason(topic: CurriculumModule): String = when {
    topic.title.contains("Nuclear", true) -> "Next spine: nucleus, decay equations, half-life and radiation safety."
    topic.title.contains("Equilibrium", true) -> "Quiz trend: bonding strong. Lab trend: titration ready. Review equilibrium next."
    else -> "Based on current filters, this is the next high-value concept path."
}

@Composable
private fun CurriculumModuleGridCard(topic: CurriculumModule, compact: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier.height(if (compact) 150.dp else 198.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(Panel),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, topic.color.copy(alpha = .36f))
    ) {
        Column(Modifier.fillMaxSize().padding(if (compact) 10.dp else 12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(if (compact) 34.dp else 42.dp), contentAlignment = Alignment.Center) {
                    AtomGraphic(if (compact) 32.dp else 40.dp, topic.color)
                    Text(topic.icon, color = White, fontSize = if (topic.icon.length > 1) 8.sp else 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(syllabusShortTag(topic.level), color = topic.color, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 7.dp))
            }
            Column {
                Text(topic.title, color = White, fontSize = if (compact) 11.sp else 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(topic.category, color = topic.color, fontSize = 8.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (!compact) {
                    Text(topic.subcategory, color = Muted, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    CurriculumMetadataRow(topic, compact)
                    PrerequisiteMapCard(topic, compact = true)
                } else {
                    Text("${lessonTimeMinutes(topic)} min - ${masteryStatus(topic)}", color = Muted, fontSize = 8.sp, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun CurriculumMetadataRow(topic: CurriculumModule, compact: Boolean) {
    Row(Modifier.fillMaxWidth().padding(top = if (compact) 4.dp else 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        MetaPill(moduleDifficulty(topic), topic.color)
        MetaPill("${lessonTimeMinutes(topic)} min", Cyan)
        MetaPill(masteryStatus(topic), if (masteryStatus(topic) == "Needs review") Orange else Green)
    }
}

@Composable
private fun MetaPill(text: String, color: Color) {
    Text(
        text,
        color = color,
        fontSize = 8.sp,
        maxLines = 1,
        modifier = Modifier.clip(CircleShape)
            .background(color.copy(alpha = .09f))
            .border(1.dp, color.copy(alpha = .30f), CircleShape)
            .padding(horizontal = 7.dp, vertical = 4.dp)
    )
}

@Composable
private fun PrerequisiteMapCard(topic: CurriculumModule, compact: Boolean = false) {
    val prereqs = prerequisitesFor(topic)
    Column(Modifier.fillMaxWidth().padding(top = if (compact) 7.dp else 9.dp)) {
        Text("Prerequisite map", color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            prereqs.forEachIndexed { index, prereq ->
                Text(prereq, color = if (index == prereqs.lastIndex) topic.color else Muted, fontSize = 8.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (index < prereqs.lastIndex) Text("  >  ", color = Border, fontSize = 8.sp)
            }
        }
    }
}

private fun syllabusShortTag(level: String): String = when (level) {
    "Class 6-8" -> "6-8"
    "Class 9-10" -> "9-10"
    "Class 11-12" -> "11-12"
    "Undergraduate" -> "UG"
    "Postgraduate" -> "PG"
    else -> level
}

private fun lessonTimeMinutes(topic: CurriculumModule): Int = topic.lessons.size * when (topic.level) {
    "Class 6-8" -> 6
    "Class 9-10" -> 7
    "Class 11-12" -> 8
    "Undergraduate" -> 10
    else -> 12
}

private fun moduleDifficulty(topic: CurriculumModule): String = when (topic.level) {
    "Class 6-8" -> "Easy"
    "Class 9-10" -> "Foundation"
    "Class 11-12" -> "Intermediate"
    "Undergraduate" -> "Advanced"
    else -> "Expert"
}

private fun masteryStatus(topic: CurriculumModule): String = when {
    topic.title.contains("Equilibrium", true) -> "Needs review"
    topic.category.contains("Laboratory", true) -> "Lab ready"
    topic.title.contains("Atomic", true) || topic.title.contains("Bonding", true) -> "Mastering"
    else -> "Ready"
}

private fun prerequisitesFor(topic: CurriculumModule): List<String> = when {
    topic.title.contains("Nuclear", true) -> listOf("Atomic structure", "Isotopes", "Exponential decay")
    topic.title.contains("Equilibrium", true) -> listOf("Moles", "Concentration", "Reversible reactions")
    topic.title.contains("Bonding", true) -> listOf("Atomic structure", "Valence electrons", "Periodic trends")
    topic.title.contains("Organic", true) -> listOf("Covalent bonds", "Formula writing", "Functional groups")
    topic.title.contains("Electro", true) || topic.title.contains("Redox", true) -> listOf("Ions", "Oxidation states", "Electron transfer")
    topic.category.contains("Analytical", true) -> listOf("Measurement", "Solutions", "Calibration")
    topic.level == "Postgraduate" -> listOf("UG core", topic.category, topic.subcategory)
    topic.level == "Undergraduate" -> listOf("Class 11-12 core", topic.category, topic.subcategory)
    else -> listOf("Foundation", topic.category, topic.subcategory)
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
    var typeFilter by remember { mutableStateOf("All") }
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<String?>(null) }
    var balanceInput by remember { mutableStateOf("H2 + O2 -> H2O") }
    var mechanismStep by remember { mutableIntStateOf(0) }
    val reactions = reactionLibrary.filter {
        (chapter == "All" || it.chapter == chapter) &&
            (typeFilter == "All" || reactionMatchesType(it, typeFilter)) &&
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
        ReactionTypeFilterRow(typeFilter) { typeFilter = it }
        ReactionBalancerPanel(balanceInput, onInput = { balanceInput = it })
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
                                ReactionEnergyProfile(reaction)
                                Spacer(Modifier.height(9.dp))
                                Text("WHY IT MATTERS", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(reaction.purpose, color = White, fontSize = 12.sp)
                                Spacer(Modifier.height(7.dp))
                                Text("CONDITIONS", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(reaction.conditions, color = White, fontSize = 12.sp)
                                Spacer(Modifier.height(7.dp))
                                Text(reaction.explanation, color = Muted, fontSize = 11.sp)
                                Spacer(Modifier.height(9.dp))
                                ReactionSafetyAndUse(reaction)
                                ReactionBalancingSteps(reaction)
                                if (reaction.chapter == "Organic") {
                                    OrganicMechanismStepper(reaction, mechanismStep) { mechanismStep = it }
                                }
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
private fun ReactionTypeFilterRow(selected: String, onSelect: (String) -> Unit) {
    val filters = listOf("All", "Synthesis", "Decomposition", "Displacement", "Redox", "Acid-base")
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        filters.forEach { item ->
            val active = selected == item
            Text(
                item,
                color = if (active) Night else White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clip(CircleShape)
                    .background(if (active) Orange else Panel)
                    .border(1.dp, if (active) Orange else Border, CircleShape)
                    .clickable { onSelect(item) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ReactionBalancerPanel(input: String, onInput: (String) -> Unit) {
    val result = balanceReaction(input)
    GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp), Cyan) {
        Text("REACTION BALANCER", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = input,
            onValueChange = onInput,
            singleLine = true,
            label = { Text("Type an equation, e.g. Fe + O2 -> Fe2O3") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Text(result.balanced, color = White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 10.dp))
        result.steps.take(3).forEachIndexed { index, step ->
            Text("${index + 1}. $step", color = Muted, fontSize = 10.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 5.dp))
        }
    }
}

@Composable
private fun ReactionEnergyProfile(reaction: ReactionEntry) {
    val exothermic = reactionLooksExothermic(reaction)
    val accent = if (exothermic) Orange else Purple
    Column(Modifier.fillMaxWidth()) {
        Text("ENERGY PROFILE", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Canvas(Modifier.fillMaxWidth().height(118.dp).padding(top = 6.dp)) {
            val startY = if (exothermic) size.height * .38f else size.height * .62f
            val endY = if (exothermic) size.height * .72f else size.height * .28f
            val peakY = size.height * .16f
            val path = Path().apply {
                moveTo(size.width * .08f, startY)
                cubicTo(size.width * .28f, startY, size.width * .34f, peakY, size.width * .50f, peakY)
                cubicTo(size.width * .66f, peakY, size.width * .72f, endY, size.width * .92f, endY)
            }
            drawLine(Border, Offset(size.width * .08f, size.height * .88f), Offset(size.width * .92f, size.height * .88f), 2f)
            drawLine(Border, Offset(size.width * .08f, size.height * .12f), Offset(size.width * .08f, size.height * .88f), 2f)
            drawPath(path, accent, style = Stroke(5f, cap = StrokeCap.Round))
            drawCircle(Cyan, 6f, Offset(size.width * .08f, startY))
            drawCircle(Green, 6f, Offset(size.width * .92f, endY))
        }
        Text(if (exothermic) "Products sit lower in energy; heat is released." else "Products sit higher in energy; energy input is required.", color = White, fontSize = 10.sp)
    }
}

@Composable
private fun ReactionSafetyAndUse(reaction: ReactionEntry) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        Column(Modifier.weight(1f)) {
            Text("SAFETY", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(reactionSafetyNote(reaction), color = White, fontSize = 10.sp, lineHeight = 15.sp)
        }
        Column(Modifier.weight(1f)) {
            Text("REAL-WORLD USE", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(reactionUseNote(reaction), color = White, fontSize = 10.sp, lineHeight = 15.sp)
        }
    }
}

@Composable
private fun ReactionBalancingSteps(reaction: ReactionEntry) {
    val explanation = balancingExplanationFor(reaction)
    Text("BALANCING EXPLANATION", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
    explanation.forEachIndexed { index, step ->
        Text("${index + 1}. $step", color = Muted, fontSize = 10.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun OrganicMechanismStepper(reaction: ReactionEntry, step: Int, onStep: (Int) -> Unit) {
    val steps = organicMechanismSteps(reaction)
    val selected = step.coerceIn(0, steps.lastIndex)
    GlowCard(Modifier.fillMaxWidth().padding(top = 10.dp), Purple) {
        Text("ORGANIC MECHANISM", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = { (selected + 1) / steps.size.toFloat() },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(5.dp),
            color = Purple,
            trackColor = Border
        )
        Text(steps[selected], color = White, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 10.dp))
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onStep((selected - 1).coerceAtLeast(0)) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(Panel)) {
                Text("Previous", color = White, fontSize = 10.sp)
            }
            Button(onClick = { onStep((selected + 1).coerceAtMost(steps.lastIndex)) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(Purple)) {
                Text("Next step", color = Night, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private data class BalanceResult(val balanced: String, val steps: List<String>)

private fun balanceReaction(input: String): BalanceResult {
    val key = input.lowercase()
        .replace(" ", "")
        .replace("→", "->")
    val balanced = when {
        key.contains("h2+o2->h2o") -> "2H2 + O2 -> 2H2O"
        key.contains("fe+o2->fe2o3") -> "4Fe + 3O2 -> 2Fe2O3"
        key.contains("c3h8+o2->co2+h2o") -> "C3H8 + 5O2 -> 3CO2 + 4H2O"
        key.contains("ch4+o2->co2+h2o") -> "CH4 + 2O2 -> CO2 + 2H2O"
        key.contains("n2+h2->nh3") -> "N2 + 3H2 -> 2NH3"
        key.contains("hcl+naoh->nacl+h2o") -> "HCl + NaOH -> NaCl + H2O"
        key.contains("mg+o2->mgo") -> "2Mg + O2 -> 2MgO"
        else -> "Use library cards for verified balancing, or try: H2 + O2 -> H2O"
    }
    return BalanceResult(
        balanced,
        listOf(
            "List each element on reactant and product sides.",
            "Balance atoms that appear in only one compound first.",
            "Use coefficients, not subscripts, then reduce to the smallest whole-number ratio.",
            "Check conservation by recounting every element on both sides."
        )
    )
}

private fun reactionMatchesType(reaction: ReactionEntry, filter: String): Boolean {
    val text = "${reaction.chapter} ${reaction.type} ${reaction.title}".lowercase()
    return when (filter) {
        "Synthesis" -> "synthesis" in text || "combination" in text
        "Decomposition" -> "decomposition" in text || "thermolysis" in text || "hydrolysis" in text
        "Displacement" -> "displacement" in text || "precipitation" in text
        "Redox" -> "redox" in text || "oxidation" in text || "combustion" in text || "electro" in text
        "Acid-base" -> "acid" in text || "base" in text || "neutralization" in text || "proton" in text
        else -> true
    }
}

private fun reactionLooksExothermic(reaction: ReactionEntry): Boolean {
    val text = "${reaction.title} ${reaction.type} ${reaction.purpose}".lowercase()
    return listOf("combustion", "neutralization", "formation", "oxidation", "respiration").any { it in text }
}

private fun balancingExplanationFor(reaction: ReactionEntry): List<String> = when {
    reaction.title.contains("combustion", true) || reaction.chapter == "Combustion" -> listOf(
        "Balance carbon first from the fuel formula.",
        "Balance hydrogen next by placing water coefficients.",
        "Balance oxygen last because O2 is an elemental reactant."
    )
    reaction.chapter == "Redox" -> listOf(
        "Identify oxidation-state changes.",
        "Match electrons lost and gained.",
        "Balance atoms and charge under the stated conditions."
    )
    reaction.chapter.contains("Acid", true) -> listOf(
        "Pair acidic H+ with basic OH- or proton acceptor sites.",
        "Balance salt ions as spectator products.",
        "Check that water/proton transfer conserves atoms and charge."
    )
    else -> balanceReaction(reaction.equation).steps.take(3)
}

private fun organicMechanismSteps(reaction: ReactionEntry): List<String> = when {
    reaction.title.contains("chlorination", true) -> listOf(
        "Initiation: UV light splits Cl2 into chlorine radicals.",
        "Propagation: chlorine radical abstracts H from methane.",
        "Propagation: methyl radical reacts with Cl2 to form CH3Cl and another chlorine radical.",
        "Termination: radicals combine to stop the chain."
    )
    reaction.title.contains("esterification", true) -> listOf(
        "Protonate the carbonyl oxygen to activate the acid.",
        "Alcohol attacks the carbonyl carbon.",
        "Proton transfers convert OH into a better leaving group.",
        "Water leaves and deprotonation restores the acid catalyst."
    )
    reaction.title.contains("hydrogenation", true) -> listOf(
        "Alkene adsorbs on the metal catalyst.",
        "H2 dissociates into surface hydrogens.",
        "Two hydrogens add across the C=C bond.",
        "Alkane desorbs from the catalyst surface."
    )
    else -> listOf(
        "Identify the electron-rich site.",
        "Identify the electron-poor site.",
        "Move electron pairs to form and break bonds.",
        "Check product structure and charge conservation."
    )
}

private fun reactionSafetyNote(reaction: ReactionEntry): String = when {
    reaction.conditions.contains("Ignition", true) -> "Use tiny quantities, eye protection and shielding; ignition reactions can release heat quickly."
    reaction.title.contains("lead", true) || reaction.title.contains("dichromate", true) -> "Treat heavy-metal or oxidizer waste as hazardous; avoid skin contact and inhalation."
    reaction.chapter == "Acid-base" || reaction.chapter == "Acid-base" -> "Corrosive acids/bases require goggles, gloves, dilution control and splash awareness."
    reaction.chapter == "Organic" -> "Organic reagents may be flammable, toxic or volatile; use ventilation and avoid open flames."
    else -> "Use PPE, small scale, labelled containers and proper waste handling for the actual reagents."
}

private fun reactionUseNote(reaction: ReactionEntry): String = when {
    reaction.purpose.isNotBlank() -> reaction.purpose
    reaction.chapter == "Combustion" -> "Fuel chemistry, engines, heat generation and emissions analysis."
    reaction.chapter == "Redox" -> "Batteries, corrosion, metallurgy and analytical titrations."
    else -> "Connects classroom equations with laboratory, industrial or environmental decisions."
}

@Composable
private fun MoleculeScreen(onBack: () -> Unit) {
    var model by remember { mutableStateOf(0) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var showLabels by remember { mutableStateOf(true) }
    var showPolarity by remember { mutableStateOf(false) }
    var vseprMode by remember { mutableStateOf(false) }
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
    val modes = listOf("Ball-stick", "Space-fill", "Skeletal")
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
        MoleculeComparisonPresets { first ->
            moleculeName = first
            chapter = moleculeLibrary.firstOrNull { it.name == first }?.chapter ?: chapter
            showAll = false
            query = ""
        }
        MoleculeGraphic(
            Modifier.fillMaxWidth().height(280.dp),
            model,
            molecule.layout,
            rotation,
            zoom,
            showLabels,
            showPolarity,
            vseprMode
        )
        MoleculeViewerControls(
            rotation = rotation,
            zoom = zoom,
            onRotate = { rotation = (rotation + it) % 360f },
            onZoom = { zoom = (zoom + it).coerceIn(.72f, 1.45f) },
            onReset = {
                rotation = 0f
                zoom = 1f
                showLabels = true
                showPolarity = false
                vseprMode = false
            }
        )
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
        MoleculeOverlayControls(
            showLabels = showLabels,
            showPolarity = showPolarity,
            vseprMode = vseprMode,
            onLabels = { showLabels = !showLabels },
            onPolarity = { showPolarity = !showPolarity },
            onVsepr = { vseprMode = !vseprMode }
        )
        GlowCard(Modifier.fillMaxWidth().padding(18.dp), Cyan) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoStat("Molar mass", molecule.molarMass)
                InfoStat("Geometry", molecule.geometry)
            }
            Spacer(Modifier.height(18.dp))
            Text("BONDING", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(molecule.bonding, color = White, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            Text("GEOMETRY NOTES", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(vseprTeachingNote(molecule), color = White, fontSize = 11.sp, lineHeight = 17.sp)
            Spacer(Modifier.height(10.dp))
            Text("Schematic teaching model; atom sizes and distances are not to scale.", color = Muted, fontSize = 10.sp)
        }
        MoleculeComparisonCard(molecule) { selected ->
            moleculeName = selected
            chapter = moleculeLibrary.firstOrNull { it.name == selected }?.chapter ?: chapter
            showAll = false
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
private fun MoleculeViewerControls(
    rotation: Float,
    zoom: Float,
    onRotate: (Float) -> Unit,
    onZoom: (Float) -> Unit,
    onReset: () -> Unit
) {
    GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp), Cyan) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            MoleculeControlButton("Rotate -", Modifier.weight(1f)) { onRotate(-18f) }
            MoleculeControlButton("Rotate +", Modifier.weight(1f)) { onRotate(18f) }
            MoleculeControlButton("Zoom -", Modifier.weight(1f)) { onZoom(-.1f) }
            MoleculeControlButton("Zoom +", Modifier.weight(1f)) { onZoom(.1f) }
            MoleculeControlButton("Reset", Modifier.weight(1f), onReset)
        }
        Text("Rotation ${rotation.toInt()} deg - Zoom ${(zoom * 100).toInt()}%", color = Muted, fontSize = 9.sp, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun MoleculeControlButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    Text(
        label,
        color = Cyan,
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = modifier.clip(RoundedCornerShape(8.dp))
            .background(Panel2)
            .border(1.dp, Cyan.copy(alpha = .34f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    )
}

@Composable
private fun MoleculeOverlayControls(
    showLabels: Boolean,
    showPolarity: Boolean,
    vseprMode: Boolean,
    onLabels: () -> Unit,
    onPolarity: () -> Unit,
    onVsepr: () -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MoleculeToggle("Bond labels", showLabels, Cyan, Modifier.weight(1f), onLabels)
        MoleculeToggle("Polarity", showPolarity, Orange, Modifier.weight(1f), onPolarity)
        MoleculeToggle("VSEPR", vseprMode, Green, Modifier.weight(1f), onVsepr)
    }
}

@Composable
private fun MoleculeToggle(label: String, active: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Text(
        label,
        color = if (active) Night else color,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier.clip(RoundedCornerShape(9.dp))
            .background(if (active) color else color.copy(alpha = .08f))
            .border(1.dp, color.copy(alpha = .42f), RoundedCornerShape(9.dp))
            .clickable { onClick() }
            .padding(vertical = 9.dp)
    )
}

@Composable
private fun MoleculeComparisonPresets(open: (String) -> Unit) {
    val pairs = listOf("H2O vs CO2" to "Water", "NH3 vs CH4" to "Ammonia", "Benzene vs cyclohexane" to "Benzene")
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 7.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        pairs.forEach { (label, first) ->
            Text(
                label,
                color = Purple,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clip(CircleShape)
                    .background(Purple.copy(alpha = .10f))
                    .border(1.dp, Purple.copy(alpha = .36f), CircleShape)
                    .clickable { open(first) }
                    .padding(horizontal = 11.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun MoleculeComparisonCard(molecule: MoleculeEntry, open: (String) -> Unit) {
    val partner = comparisonPartnerFor(molecule) ?: return
    GlowCard(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp), Green) {
        Text("MOLECULE COMPARISON", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("${molecule.name} vs ${partner.name}", color = White, fontWeight = FontWeight.SemiBold)
                Text(comparisonNoteFor(molecule, partner), color = Muted, fontSize = 10.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Text("Switch", color = Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { open(partner.name) }.padding(8.dp))
        }
    }
}

private fun comparisonPartnerFor(molecule: MoleculeEntry): MoleculeEntry? {
    val partnerName = when (molecule.name) {
        "Water" -> "Carbon dioxide"
        "Carbon dioxide" -> "Water"
        "Ammonia" -> "Methane"
        "Methane" -> "Ammonia"
        "Benzene" -> "Cyclohexane"
        "Cyclohexane" -> "Benzene"
        else -> null
    }
    return partnerName?.let { name -> moleculeLibrary.firstOrNull { it.name == name } }
}

private fun comparisonNoteFor(first: MoleculeEntry, second: MoleculeEntry): String = when (setOf(first.name, second.name)) {
    setOf("Water", "Carbon dioxide") -> "Bent water keeps a net dipole; linear carbon dioxide cancels equal bond dipoles."
    setOf("Ammonia", "Methane") -> "Ammonia has one lone pair and a molecular dipole; methane is tetrahedral and nearly non-polar."
    setOf("Benzene", "Cyclohexane") -> "Benzene is planar and aromatic; cyclohexane puckers into a chair to reduce strain."
    else -> "Compare geometry, bond polarity, symmetry and intermolecular forces."
}

private fun vseprTeachingNote(molecule: MoleculeEntry): String = when (molecule.layout) {
    "bent" -> "Bent shape: two bonding directions plus lone-pair repulsion leave a net dipole."
    "linear3", "linear4", "diatomic" -> "Linear shape: electron domains align at 180 degrees; symmetry can cancel bond dipoles."
    "pyramidal" -> "Trigonal pyramidal: four electron domains with one lone pair compress bond angles below tetrahedral."
    "tetrahedral", "ethane", "ethanol", "methanol" -> "Tetrahedral centers: four electron domains spread near 109.5 degrees."
    "trigonal" -> "Trigonal planar: three domains arrange around 120 degrees in one plane."
    "ring" -> "Ring geometry: cyclic constraints and delocalization can control planarity and bond equality."
    "bipyramidal" -> "Trigonal bipyramidal: axial and equatorial positions experience different repulsions."
    "octahedral", "square" -> "Octahedral family: six domains or lone-pair variants create 90 degree relationships."
    else -> "VSEPR predicts shape by arranging electron domains to minimize repulsion."
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
    val reduceMotion = LocalReducedMotion.current
    val transition = rememberInfiniteTransition(label = "atom")
    val animatedRotation by transition.animateFloat(
        0f, 360f,
        animationSpec = infiniteRepeatable(tween(8000), repeatMode = RepeatMode.Restart),
        label = "orbit"
    )
    val rotation = if (reduceMotion) 24f else animatedRotation
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
private fun MoleculeGraphic(
    modifier: Modifier,
    mode: Int,
    layout: String,
    rotationDegrees: Float = 0f,
    zoom: Float = 1f,
    showLabels: Boolean = false,
    showPolarity: Boolean = false,
    vseprMode: Boolean = false
) {
    val reduceMotion = LocalReducedMotion.current
    val transition = rememberInfiniteTransition(label = "molecule")
    val animatedDrift by transition.animateFloat(
        -8f, 8f, animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse), label = "drift"
    )
    val drift = if (reduceMotion) 0f else animatedDrift
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
        val points = basePoints.mapIndexed { i, p ->
            val drifted = p + Offset(if (i % 2 == 0) drift else -drift, 0f)
            Offset(
                center.x + (drifted.x - center.x) * zoom,
                center.y + (drifted.y - center.y) * zoom
            )
        }
        rotate(rotationDegrees, center) {
            if (vseprMode) {
                drawCircle(Green.copy(alpha = .08f), size.minDimension * .32f * zoom, center, style = Stroke(3f))
                bonds.take(6).forEachIndexed { index, bond ->
                    val target = points[bond.second]
                    drawLine(Green.copy(alpha = .35f), center, target, 2f, StrokeCap.Round)
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            color = Green.toArgb()
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawText("domain ${index + 1}", (center.x + target.x) / 2f, (center.y + target.y) / 2f - 8f, paint)
                    }
                }
            }
            bonds.forEachIndexed { index, (a, b) ->
                val width = when (mode) {
                    1 -> 14f
                    2 -> 4f
                    else -> 7f
                }
                drawLine(Color(0xFF8BA2B8), points[a], points[b], width, StrokeCap.Round)
                if (showPolarity) {
                    val start = points[a]
                    val end = points[b]
                    val arrow = Offset(start.x + (end.x - start.x) * .62f, start.y + (end.y - start.y) * .62f)
                    drawLine(Orange, start, arrow, 3f, StrokeCap.Round)
                    drawCircle(Orange, 4.5f, arrow)
                }
                if (showLabels) {
                    val mid = Offset((points[a].x + points[b].x) / 2f, (points[a].y + points[b].y) / 2f)
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            color = White.toArgb()
                            textSize = 25f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        val label = if (index == 0) "${bondLengthFor(layout)} pm" else bondAngleFor(layout)
                        drawText(label, mid.x, mid.y - 10f, paint)
                    }
                }
            }
            points.forEachIndexed { i, p ->
                val atomColor = when (i) { 3 -> Color(0xFFFF4F48); 0, 1, 2 -> Color(0xFF445062); else -> Color.White }
                val radius = when (mode) {
                    1 -> if (i in 0..3) 31f else 22f
                    2 -> if (i in 0..3) 9f else 6f
                    else -> if (i in 0..3) 22f else 13f
                }
                if (mode == 1) drawCircle(atomColor.copy(alpha = .20f), radius * 2.2f, p)
                drawCircle(Brush.radialGradient(listOf(Color.White, atomColor), p - Offset(6f, 6f), radius * 1.6f), radius, p)
                if (showPolarity && i < 4) {
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            color = if (i == 3) Color(0xFFFFC857).toArgb() else Cyan.toArgb()
                            textSize = 23f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawText(if (i == 3) "d-" else "d+", p.x, p.y - radius - 8f, paint)
                    }
                }
            }
        }
    }
}

private fun bondAngleFor(layout: String): String = when (layout) {
    "bent" -> "104.5 deg"
    "linear3", "linear4", "diatomic" -> "180 deg"
    "pyramidal" -> "107 deg"
    "trigonal" -> "120 deg"
    "tetrahedral", "ethane", "ethanol", "methanol" -> "109.5 deg"
    "octahedral", "square" -> "90 deg"
    "ring" -> "120 deg"
    else -> "109 deg"
}

private fun bondLengthFor(layout: String): Int = when (layout) {
    "diatomic" -> 74
    "bent" -> 96
    "linear3" -> 116
    "linear4" -> 120
    "ring" -> 139
    else -> 154
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
