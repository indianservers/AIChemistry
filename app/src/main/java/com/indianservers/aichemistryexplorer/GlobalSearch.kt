package com.indianservers.aichemistryexplorer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

private data class SearchItem(
    val type: String,
    val title: String,
    val subtitle: String,
    val color: Color,
    val destination: Screen? = null,
    val moduleTitle: String? = null,
    val element: Element? = null,
    val searchable: String = "$type $title $subtitle"
)

@Composable
internal fun GlobalChemistrySearchScreen(
    elements: List<Element>,
    onBack: () -> Unit,
    onElement: (Element) -> Unit,
    onModule: (String) -> Unit,
    onDestination: (Screen) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("All") }
    val focus = remember { FocusRequester() }
    val context = LocalContext.current
    var recentSearches by remember {
        mutableStateOf(
            context.getSharedPreferences("global_search", 0)
                .getString("recent", "")
                .orEmpty()
                .split("|")
                .filter { it.isNotBlank() }
        )
    }
    fun rememberSearch(text: String) {
        val clean = text.trim()
        if (clean.isBlank()) return
        recentSearches = (listOf(clean) + recentSearches.filterNot { it.equals(clean, true) }).take(6)
        context.getSharedPreferences("global_search", 0).edit().putString("recent", recentSearches.joinToString("|")).apply()
    }

    val modules = curriculum.map {
        SearchItem("LESSONS", it.title, "Module · ${it.lessons.size} interactive lessons", it.color, moduleTitle = it.title)
    }
    val lessons = curriculum.flatMap { module ->
        module.lessons.map { SearchItem("LESSONS", it.title, module.title, module.color, moduleTitle = module.title) }
    }
    val elementItems = elements.map {
        SearchItem(
            "ELEMENTS",
            it.name,
            "${it.symbol} · Atomic number ${it.number} · ${it.category}",
            it.color,
            element = it,
            searchable = "element ${it.name} ${it.symbol} ${it.number} ${it.category} ${elementSearchAliases(it)}"
        )
    }
    val labItems = virtualExperiments.map {
        SearchItem(
            "LABS",
            it.title,
            "${it.technique.name.replace('_', ' ')} · ${it.measuredQuantity}",
            searchTechniqueColor(it.technique),
            Screen.Lab,
            searchable = "lab experiment virtual ${it.title} ${it.objective} ${it.technique} ${it.reagents} ${it.equation}"
        )
    }
    val reactionItems = reactionLibrary.map {
        SearchItem(
            "REACTIONS",
            it.title,
            "${it.equation} · ${it.type}",
            it.color,
            Screen.Reactions,
            searchable = "reaction equation ${it.chapter} ${it.title} ${it.equation} ${it.type} ${it.conditions} ${it.purpose}"
        )
    }
    val moleculeItems = moleculeLibrary.map {
        SearchItem(
            "MOLECULES",
            it.name,
            "${it.formula} · ${it.geometry}",
            Purple,
            Screen.Molecule,
            searchable = "molecule formula ${it.name} ${it.formula} ${normalizeFormula(it.formula)} ${it.geometry} ${it.bonding} ${it.uses}"
        )
    }
    val quizItems = listOf(
        SearchItem("QUIZZES", "Practice Quizzes", "MCQ library · adaptive review", Orange, Screen.Quiz, searchable = "quiz mcq practice test exam weak topic"),
        SearchItem("QUIZZES", "Periodic Table Quiz", "Find elements directly on the table", Green, Screen.FullTable, searchable = "quiz periodic table element symbol atomic number"),
        SearchItem("QUIZZES", "Nuclear Chemistry Drill", "Decay equations · half-life · radiation safety", Purple, Screen.Quiz, searchable = "quiz nuclear chemistry radioactivity half life alpha beta gamma radiation safety")
    )
    val tools = listOf(
        SearchItem("LESSONS", "Explore Curriculum", "Navigator, prerequisites and learning path", Cyan, Screen.Explore),
        SearchItem("ELEMENTS", "Periodic Table", "118 interactive elements", Cyan, Screen.Table),
        SearchItem("LABS", "Virtual Lab", "${virtualExperiments.size} guided experiments with theory and results", Green, Screen.Lab),
        SearchItem("REACTIONS", "Reaction Library", "Balanced reaction explorer", Orange, Screen.Reactions),
        SearchItem("MOLECULES", "Molecule Viewer", "Dynamic molecular models", Purple, Screen.Molecule)
    )
    val filters = listOf("All", "Elements", "Lessons", "Labs", "Reactions", "Molecules", "Quizzes")
    val all = elementItems + modules + lessons + labItems + reactionItems + moleculeItems + quizItems + tools
    val command = quickCommandFor(query, elements)
    val formulaHint = formulaAwareHint(query)
    val resultItems = if (query.isBlank()) emptyList() else all
        .filter { activeFilter == "All" || it.type.equals(activeFilter, true) }
        .mapNotNull { item -> searchScore(item, query)?.let { score -> score to item } }
        .sortedByDescending { it.first }
        .map { it.second }
        .distinctBy { "${it.type}:${it.title}:${it.subtitle}" }
        .take(30)

    LaunchedEffect(Unit) { focus.requestFocus() }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Chemistry Search", "Lessons, elements, formulas and tools", onBack)
        SearchField(
            query = query,
            onQueryChange = { query = it },
            placeholder = "Try oxygen, H2SO4, open titration lab...",
            modifier = Modifier.padding(horizontal = 18.dp).focusRequester(focus)
        )
        SearchFilterRow(filters, activeFilter) { activeFilter = it }
        if (query.isBlank()) {
            Text("RECENT & RECOMMENDED", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(20.dp, 22.dp, 20.dp, 10.dp))
            val suggestions = (recentSearches + listOf("open nuclear chemistry", "half-life", "open titration lab", "compare Na K", "H2SO4", "NaCl", "oxigen")).distinct().take(7)
            suggestions.forEach { title ->
                Row(
                    Modifier.fillMaxWidth().clickable { query = title }.padding(horizontal = 20.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("↗", color = Cyan)
                    Text(title, color = White, modifier = Modifier.padding(start = 13.dp))
                    Spacer(Modifier.weight(1f))
                    Text("›", color = Muted)
                }
            }
            GlowCard(Modifier.fillMaxWidth().padding(18.dp), Purple) {
                Text("QUICK COMMANDS", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("Try open nuclear chemistry, half-life, compare Na K, H2SO4, NaCl or a typo like oxigen.", color = White, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
                if (command != null) {
                    item {
                        SearchResult(command.type, command.title, command.subtitle, command.color, query) {
                            rememberSearch(query)
                            command.element?.let(onElement) ?: command.moduleTitle?.let(onModule) ?: command.destination?.let(onDestination)
                        }
                    }
                }
                if (formulaHint != null) {
                    item {
                        GlowCard(Modifier.fillMaxWidth().padding(bottom = 10.dp), Cyan) {
                            Text("FORMULA-AWARE MATCH", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(formulaHint, color = White, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                        }
                    }
                }
                if (resultItems.isNotEmpty()) {
                    item { Text("${resultItems.size} RESULTS", color = Muted, fontSize = 9.sp, modifier = Modifier.padding(top = 5.dp, bottom = 8.dp)) }
                    items(resultItems) { item ->
                        SearchResult(item.type, item.title, item.subtitle, item.color, query) {
                            rememberSearch(query)
                            item.element?.let(onElement) ?: item.moduleTitle?.let(onModule) ?: item.destination?.let(onDestination)
                        }
                    }
                }
                if (resultItems.isEmpty() && command == null) {
                    item { EmptyState("No chemistry result yet", "Try a symbol, lesson or formula keyword.", ChemIcons.Search, Muted) }
                }
            }
        }
    }
}

@Composable
private fun SearchFilterRow(filters: List<String>, active: String, select: (String) -> Unit) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val selected = filter == active
            Text(
                filter,
                color = if (selected) Night else White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .background(if (selected) Cyan else LocalReadingPalette.current.surfaceAlt, CircleShape)
                    .clickable { select(filter) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun SearchResult(type: String, title: String, subtitle: String, color: Color, query: String, click: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { click() },
        colors = CardDefaults.cardColors(Panel), shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(42.dp).background(color.copy(.12f), RoundedCornerShape(10.dp)).border(1.dp, color.copy(.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(if (type == "ELEMENTS") subtitle.substringBefore(" ") else type.take(1), color = color, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                HighlightedText(title, query, White, 13)
                HighlightedText("$type · $subtitle", query, Muted, 9)
            }
            Text("›", color = color, fontSize = 23.sp)
        }
    }
}

@Composable
private fun HighlightedText(text: String, query: String, color: Color, size: Int) {
    val needle = bestHighlightNeedle(text, query)
    if (needle.isBlank()) {
        Text(text, color = color, fontSize = size.sp, fontWeight = if (size >= 13) FontWeight.SemiBold else null)
        return
    }
    val start = text.lowercase().indexOf(needle.lowercase())
    if (start < 0) {
        Text(text, color = color, fontSize = size.sp, fontWeight = if (size >= 13) FontWeight.SemiBold else null)
        return
    }
    MaterialText(
        buildAnnotatedString {
            append(text.substring(0, start))
            withStyle(SpanStyle(background = Cyan.copy(alpha = .32f), color = White, fontWeight = FontWeight.Bold)) {
                append(text.substring(start, start + needle.length))
            }
            append(text.substring(start + needle.length))
        },
        color = color,
        fontSize = size.sp,
        fontWeight = if (size >= 13) FontWeight.SemiBold else null
    )
}

private fun quickCommandFor(query: String, elements: List<Element>): SearchItem? {
    val q = normalizeSearch(query)
    if (q.startsWith("open ") && ("lab" in q || "experiment" in q)) {
        val lab = virtualExperiments.firstOrNull { normalizeSearch(it.title) in q || q.contains(normalizeSearch(it.technique.name.replace('_', ' '))) }
            ?: virtualExperiments.firstOrNull { "titration" in q && it.technique == LabTechnique.TITRATION }
        return SearchItem("COMMAND", "Open ${lab?.title ?: "Virtual Lab"}", "Launch matching lab workspace", Green, Screen.Lab)
    }
    if (q.startsWith("open ") && ("nuclear" in q || "radioactivity" in q || "halflife" in q || "half life" in q)) {
        return SearchItem("COMMAND", "Open Nuclear Chemistry", "Launch the 15-topic nuclear learning spine", Green, Screen.Explore, moduleTitle = "Nuclear Chemistry")
    }
    if (q.startsWith("compare ")) {
        val tokens = q.removePrefix("compare ").split(" ").filter { it.isNotBlank() }
        val matched = tokens.mapNotNull { token -> elements.firstOrNull { normalizeSearch(it.symbol) == token || normalizeSearch(it.name) == token } }
        if (matched.size >= 2) {
            return SearchItem("COMMAND", "Compare ${matched.joinToString(" / ") { it.symbol }}", "Open element comparison mode", Cyan, Screen.Compare)
        }
    }
    return null
}

private fun searchScore(item: SearchItem, query: String): Int? {
    val q = normalizeSearch(query)
    if (q.isBlank()) return null
    val haystack = normalizeSearch("${item.searchable} ${formulaAliasesForQuery(query)}")
    if (haystack.contains(q)) return 100 + min(q.length, 40)
    val tokens = q.split(" ").filter { it.length > 1 }
    val words = haystack.split(" ").filter { it.isNotBlank() }
    val hits = tokens.count { token ->
        words.any { word -> word.contains(token) || levenshtein(token, word).let { it <= if (token.length <= 5) 1 else 2 } }
    }
    return if (hits > 0) hits * 18 else null
}

private fun bestHighlightNeedle(text: String, query: String): String {
    val q = query.trim()
    if (q.isBlank()) return ""
    if (text.contains(q, true)) return q
    return q.split(" ").firstOrNull { it.length > 1 && text.contains(it, true) }.orEmpty()
}

private fun formulaAwareHint(query: String): String? = when (normalizeFormula(query).lowercase()) {
    "h2so4" -> "H2SO4 recognized as sulfuric acid: strong diprotic acid, dehydration reagent and battery electrolyte."
    "nacl" -> "NaCl recognized as sodium chloride: ionic lattice, chloride tests and solubility examples."
    "h2o" -> "H2O recognized as water: bent polar molecule, hydrogen bonding and neutral pH reference."
    "co2" -> "CO2 recognized as carbon dioxide: linear molecule and carbonate equilibrium partner."
    "ph" -> "pH recognized as acidity/basicity search: try acids, titration, buffers or indicators."
    "u235" -> "U-235 recognized as uranium-235: a fissile isotope used to teach fission, chain reactions and nuclear energy."
    "c14" -> "C-14 recognized as carbon-14: radioactive isotope used for radiocarbon dating and half-life calculations."
    else -> null
}

private fun formulaAliasesForQuery(query: String): String = when (normalizeFormula(query).lowercase()) {
    "h2so4" -> "sulfuric acid sulphuric acid sulfate battery acid acid base"
    "nacl" -> "sodium chloride salt chloride precipitation ionic"
    "h2o" -> "water neutral ph hydronium hydroxide"
    "co2" -> "carbon dioxide carbonate greenhouse"
    "ph" -> "acid base buffer indicator hydronium hydroxide titration"
    "u235" -> "uranium nuclear fission isotope half life chain reaction"
    "c14" -> "carbon 14 radiocarbon dating beta decay half life isotope"
    else -> ""
}

private fun elementSearchAliases(element: Element): String = when (element.symbol) {
    "O" -> "oxygen oxigen gas respiration oxide oxidizer"
    "Na" -> "sodium natrium salt nacl alkali"
    "K" -> "potassium kalium alkali flame lilac"
    "Cl" -> "chlorine chloride nacl halogen"
    "H" -> "hydrogen acid proton ph"
    "S" -> "sulfur sulphur sulfate h2so4"
    "U" -> "uranium radioactive nuclear fission u235 actinide fuel"
    "Pu" -> "plutonium radioactive nuclear fission actinide"
    "Ra" -> "radium radioactive alpha decay radiation"
    "C" -> "carbon c14 radiocarbon dating isotope organic"
    else -> ""
}

private fun searchTechniqueColor(t: LabTechnique) = listOf(Cyan, Orange, Green, Purple, Color(0xFF4D8BFF))[t.ordinal % 5]

private fun normalizeFormula(value: String): String = replaceSubscriptDigits(value)
    .filter { it.isLetterOrDigit() }

private fun normalizeSearch(value: String): String = replaceSubscriptDigits(value.lowercase())
    .replace(Regex("[^a-z0-9]+"), " ")
    .trim()

private fun replaceSubscriptDigits(value: String): String = value
    .replace("₀", "0").replace("₁", "1").replace("₂", "2").replace("₃", "3").replace("₄", "4")
    .replace("₅", "5").replace("₆", "6").replace("₇", "7").replace("₈", "8").replace("₉", "9")

private fun levenshtein(a: String, b: String): Int {
    if (a == b) return 0
    if (a.isEmpty()) return b.length
    if (b.isEmpty()) return a.length
    val costs = IntArray(b.length + 1) { it }
    for (i in 1..a.length) {
        var previous = costs[0]
        costs[0] = i
        for (j in 1..b.length) {
            val old = costs[j]
            costs[j] = minOf(costs[j] + 1, costs[j - 1] + 1, previous + if (a[i - 1] == b[j - 1]) 0 else 1)
            previous = old
        }
    }
    return costs[b.length]
}
