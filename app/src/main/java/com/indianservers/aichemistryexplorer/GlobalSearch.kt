package com.indianservers.aichemistryexplorer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class SearchItem(val type: String, val title: String, val subtitle: String, val color: Color)

@Composable
internal fun GlobalChemistrySearchScreen(
    elements: List<Element>,
    onBack: () -> Unit,
    onElement: (Element) -> Unit,
    onModule: (String) -> Unit,
    onDestination: (Screen) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val focus = remember { FocusRequester() }
    val modules = curriculum.map { SearchItem("MODULE", it.title, "5 interactive lessons", it.color) }
    val lessons = curriculum.flatMap { module ->
        module.lessons.map { SearchItem("LESSON", it.title, module.title, module.color) }
    }
    val tools = listOf(
        SearchItem("TOOL", "Periodic Table", "118 interactive elements", Cyan),
        SearchItem("TOOL", "Virtual Lab", "${virtualExperiments.size} guided experiments with theory and results", Green),
        SearchItem("TOOL", "Reaction Library", "Balanced reaction explorer", Orange),
        SearchItem("TOOL", "Molecule Viewer", "Dynamic molecular models", Purple)
    )
    val all = modules + lessons + tools
    val results = all.filter { it.title.contains(query, true) || it.subtitle.contains(query, true) }.take(18)
    val elementResults = elements.filter { it.name.contains(query, true) || it.symbol.equals(query, true) || it.number.toString() == query }.take(8)
    LaunchedEffect(Unit) { focus.requestFocus() }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Chemistry Search", "Lessons, elements, formulas and tools", onBack)
        OutlinedTextField(
            value = query, onValueChange = { query = it },
            placeholder = { Text("Try oxygen, pH, bonding…", color = Muted) },
            leadingIcon = { Text("⌕", color = Cyan, fontSize = 23.sp) },
            trailingIcon = { if (query.isNotBlank()) Text("×", color = Muted, fontSize = 23.sp, modifier = Modifier.clickable { query = "" }.padding(7.dp)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp).focusRequester(focus),
            shape = RoundedCornerShape(14.dp), singleLine = true
        )
        if (query.isBlank()) {
            Text("RECENT & RECOMMENDED", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(20.dp, 22.dp, 20.dp, 10.dp))
            listOf("Chemical Bonding", "Oxygen", "pH and Acids", "Periodic Trends").forEach { title ->
                Row(Modifier.fillMaxWidth().clickable { query = title }.padding(horizontal = 20.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("↗", color = Cyan); Text(title, color = White, modifier = Modifier.padding(start = 13.dp)); Spacer(Modifier.weight(1f)); Text("›", color = Muted)
                }
            }
            GlowCard(Modifier.fillMaxWidth().padding(18.dp), Purple) {
                Text("QUICK COMMANDS", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Search 118 elements and ${curriculum.sumOf { it.lessons.size }} curriculum concepts.",
                    color = White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
                if (elementResults.isNotEmpty()) {
                    item { Text("ELEMENTS", color = Muted, fontSize = 9.sp, modifier = Modifier.padding(bottom = 8.dp)) }
                    items(elementResults) { element ->
                        SearchResult("ELEMENT ${element.number}", element.name, element.symbol, element.color) { onElement(element) }
                    }
                }
                if (results.isNotEmpty()) {
                    item { Text("LEARNING & TOOLS", color = Muted, fontSize = 9.sp, modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) }
                    items(results) { item ->
                        SearchResult(item.type, item.title, item.subtitle, item.color) {
                            when {
                                item.type == "MODULE" -> onModule(item.title)
                                item.type == "LESSON" -> onModule(item.subtitle)
                                item.title == "Periodic Table" -> onDestination(Screen.Table)
                                item.title == "Virtual Lab" -> onDestination(Screen.Lab)
                                item.title == "Reaction Library" -> onDestination(Screen.Reactions)
                                else -> onDestination(Screen.Molecule)
                            }
                        }
                    }
                }
                if (elementResults.isEmpty() && results.isEmpty()) {
                    item {
                        Column(Modifier.fillMaxWidth().padding(top = 70.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⌬", color = Muted, fontSize = 48.sp)
                            Text("No chemistry result yet", color = White, fontWeight = FontWeight.Bold)
                            Text("Try a symbol, lesson or formula keyword.", color = Muted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResult(type: String, title: String, subtitle: String, color: Color, click: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { click() },
        colors = CardDefaults.cardColors(Panel), shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).background(color.copy(.12f), RoundedCornerShape(10.dp)).border(1.dp, color.copy(.4f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Text(if (type.startsWith("ELEMENT")) subtitle else type.take(1), color = color, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(if (type.startsWith("ELEMENT")) type else "$type · $subtitle", color = Muted, fontSize = 9.sp)
            }
            Text("›", color = color, fontSize = 23.sp)
        }
    }
}
