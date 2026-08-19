package com.indianservers.aichemistryexplorer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal object ChemIcons {
    const val Home = "⌂"
    const val Search = "⌕"
    const val Back = "‹"
    const val Next = "›"
    const val Lab = "⚗"
    const val Atom = "⚛"
    const val Table = "▦"
    const val Quiz = "◆"
    const val Progress = "◉"
    const val Check = "✓"
    const val Empty = "○"
    const val Reset = "↺"
    const val Play = "▶"
    const val Pause = "Ⅱ"
    const val Close = "×"
    const val Spark = "✦"
}

@Composable
internal fun IconChip(
    icon: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val bg = if (selected) color.copy(alpha = .18f) else LocalReadingPalette.current.surfaceAlt
    Box(
        modifier
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, color.copy(alpha = if (selected) .8f else .42f), CircleShape)
            .semantics {
                contentDescription = label
                role = Role.Button
            }
            .clickable { onClick() }
            .padding(horizontal = 11.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(icon, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
internal fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder, color = Muted, fontSize = 12.sp) },
        leadingIcon = { Text(ChemIcons.Search, color = Cyan, fontSize = 22.sp) },
        trailingIcon = {
            if (query.isNotBlank()) {
                Text(
                    ChemIcons.Close,
                    color = Muted,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .semantics { contentDescription = "Clear search" }
                        .clickable { onQueryChange("") }
                        .padding(8.dp)
                )
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
internal fun EmptyState(title: String, subtitle: String, icon: String = ChemIcons.Empty, color: Color = Muted) {
    Column(
        Modifier.fillMaxWidth().padding(top = 56.dp, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(64.dp).background(color.copy(.12f), CircleShape).border(1.dp, color.copy(.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = color, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        }
        Text(title, color = White, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp))
        Text(subtitle, color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
internal fun LoadingRows(label: String, color: Color = Cyan) {
    GlowCard(Modifier.fillMaxWidth().padding(vertical = 12.dp), color) {
        Text(label, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        repeat(3) {
            Spacer(
                Modifier
                    .fillMaxWidth(if (it == 0) .92f else if (it == 1) .72f else .84f)
                    .height(10.dp)
                    .padding(top = 10.dp)
                    .background(Border.copy(.7f), RoundedCornerShape(6.dp))
            )
        }
    }
}

@Composable
internal fun LessonProgressStrip(current: Int, total: Int, color: Color, labels: List<String> = emptyList()) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(total) { index ->
                Box(
                    Modifier.weight(1f).height(6.dp)
                        .background(if (index <= current) color else Border, CircleShape)
                        .semantics { contentDescription = "Lesson step ${index + 1} of $total" }
                )
            }
        }
        if (labels.isNotEmpty()) {
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                labels.take(total).forEachIndexed { index, label ->
                    Text(label, color = if (index == current) color else Muted, fontSize = 8.sp, maxLines = 1)
                }
            }
        }
    }
}

@Composable
internal fun Breadcrumb(text: String, modifier: Modifier = Modifier, color: Color = Muted) {
    Text(text, color = color, fontSize = 10.sp, maxLines = 1, modifier = modifier.padding(top = 3.dp))
}

@Composable
internal fun FormulaVariables(formula: String, accent: Color) {
    val tokens = formula.filter { it.isLetter() }.map { it.toString() }.distinct().take(5)
    if (tokens.isEmpty()) return
    Row(Modifier.fillMaxWidth().padding(top = 9.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        tokens.forEach { token ->
            Text(
                "$token: variable",
                color = accent,
                fontSize = 9.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(7.dp))
                    .background(accent.copy(.1f))
                    .border(1.dp, accent.copy(.35f), RoundedCornerShape(7.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            )
        }
    }
}

@Composable
internal fun ResetTextButton(label: String = "Reset", onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = Cyan)
    ) {
        Text("${ChemIcons.Reset} $label", color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

internal fun answerLetter(index: Int): String = ('A'.code + index).toChar().toString()
