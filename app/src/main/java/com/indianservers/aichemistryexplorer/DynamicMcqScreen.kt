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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.WorkInfo
import com.indianservers.aichemistryexplorer.contentpack.CatalogPack
import com.indianservers.aichemistryexplorer.contentpack.ContentCatalog
import com.indianservers.aichemistryexplorer.contentpack.McqPackEntity
import com.indianservers.aichemistryexplorer.contentpack.StoredQuestion
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Composable
internal fun DynamicMcqLibraryPage() {
    val app = LocalContext.current.applicationContext as ChemistryExplorerApplication
    val repository = remember(app) { app.chemistryMcqRepository }
    val installed by repository.observeInstalledPacks().collectAsState(initial = emptyList())
    val statuses = remember { mutableStateMapOf<String, String>() }
    val scope = rememberCoroutineScope()
    var catalog by remember { mutableStateOf<ContentCatalog?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var practice by remember { mutableStateOf<List<StoredQuestion>>(emptyList()) }

    fun refresh() {
        scope.launch {
            loading = true
            error = null
            runCatching { repository.refreshCatalog() }
                .onSuccess { catalog = it }
                .onFailure { error = it.message ?: "Unable to load the MCQ catalog" }
            loading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    if (practice.isNotEmpty()) {
        DownloadedQuiz(practice) { practice = emptyList() }
        return
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
    ) {
        item {
            GlowCard(Modifier.fillMaxWidth().padding(bottom = 12.dp), Cyan) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AtomGraphic(72.dp, Cyan)
                    Column(Modifier.weight(1f).padding(start = 13.dp)) {
                        Text("DOWNLOADABLE MCQ LIBRARY", color = Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("Choose only the class or level you need.", color = White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text("Packs download once, validate, and remain available offline.", color = Muted, fontSize = 10.sp)
                    }
                }
            }
        }
        if (loading) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
                    color = Cyan,
                    trackColor = Border
                )
            }
        }
        error?.let { message ->
            item {
                GlowCard(Modifier.fillMaxWidth().padding(bottom = 12.dp), Orange) {
                    Text("CATALOG UNAVAILABLE", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(message, color = White, fontSize = 11.sp, modifier = Modifier.padding(vertical = 7.dp))
                    Text(
                        "Retry",
                        color = Cyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { refresh() }.padding(vertical = 5.dp)
                    )
                }
            }
        }
        if (installed.isNotEmpty()) {
            item {
                Text("AVAILABLE OFFLINE", color = Green, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(installed, key = { "installed:${it.packId}" }) { pack ->
                InstalledPackCard(pack) {
                    scope.launch {
                        practice = repository.selectQuiz(
                            level = pack.level,
                            classNumber = pack.classNumber,
                            limit = 10
                        )
                    }
                }
            }
        }
        catalog?.let { available ->
            item {
                Text(
                    "ONLINE PACKS · ${available.totalQuestions} QUESTIONS",
                    color = Purple,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 14.dp, bottom = 8.dp)
                )
            }
            items(available.packs, key = { it.packId }) { pack ->
                val local = installed.firstOrNull { it.packId == pack.packId }
                CatalogPackCard(pack, local, statuses[pack.packId]) {
                    scope.launch {
                        repository.enqueueDownload(pack).filterNotNull().collect { work ->
                            statuses[pack.packId] = work.displayStatus()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstalledPackCard(pack: McqPackEntity, startPractice: () -> Unit) {
    GlowCard(Modifier.fillMaxWidth().padding(bottom = 9.dp), Green) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AtomGraphic(50.dp, Green)
            Column(Modifier.weight(1f).padding(start = 11.dp)) {
                Text(pack.title, color = White, fontWeight = FontWeight.SemiBold)
                Text("${pack.questionCount} questions · version ${pack.version}", color = Muted, fontSize = 10.sp)
            }
            Button(
                onClick = startPractice,
                colors = ButtonDefaults.buttonColors(Green),
                shape = RoundedCornerShape(9.dp)
            ) { Text("Practice", color = Night, fontSize = 10.sp) }
        }
    }
}

@Composable
private fun CatalogPackCard(
    pack: CatalogPack,
    installed: McqPackEntity?,
    status: String?,
    download: () -> Unit
) {
    val current = installed?.version == pack.version
    GlowCard(Modifier.fillMaxWidth().padding(bottom = 9.dp), if (current) Green else Purple) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AtomGraphic(50.dp, if (current) Green else Purple)
            Column(Modifier.weight(1f).padding(start = 11.dp)) {
                Text(pack.title, color = White, fontWeight = FontWeight.SemiBold)
                Text("${pack.questionCount} questions · ${formatBytes(pack.downloadBytes)}", color = Muted, fontSize = 10.sp)
                status?.let { Text(it, color = Cyan, fontSize = 9.sp, modifier = Modifier.padding(top = 4.dp)) }
            }
            Button(
                onClick = download,
                enabled = !current && status?.startsWith("Downloading") != true,
                colors = ButtonDefaults.buttonColors(if (current) Green else Purple),
                shape = RoundedCornerShape(9.dp)
            ) { Text(if (current) "Installed" else if (installed != null) "Update" else "Download", color = Night, fontSize = 9.sp) }
        }
    }
}

@Composable
private fun DownloadedQuiz(questions: List<StoredQuestion>, close: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var selected by remember(index) { mutableStateOf<String?>(null) }
    val stored = questions[index]
    val question = stored.question
    val options = stored.options.sortedBy { it.displayOrder }
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("‹", color = White, fontSize = 36.sp, modifier = Modifier.clickable { close() }.padding(end = 12.dp))
            Column(Modifier.weight(1f)) {
                Text(question.concept, color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${index + 1}/${questions.size} · ${question.difficulty}", color = Muted, fontSize = 10.sp)
            }
            AtomGraphic(48.dp, Purple)
        }
        LinearProgressIndicator(
            progress = { (index + 1f) / questions.size },
            modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp).height(6.dp),
            color = Purple,
            trackColor = Border
        )
        GlowCard(Modifier.fillMaxWidth(), Purple) {
            Text(question.category.uppercase(), color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(question.question, color = White, fontSize = 19.sp, lineHeight = 27.sp, modifier = Modifier.padding(top = 8.dp))
            question.formula?.let { Text(it, color = Cyan, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 9.dp)) }
        }
        options.forEach { option ->
            val answered = selected != null
            val correct = answered && option.optionId == question.correctOptionId
            val wrong = selected == option.optionId && !correct
            val accent = if (correct) Green else if (wrong) Orange else Border
            Row(
                Modifier.fillMaxWidth().padding(top = 9.dp)
                    .background(accent.copy(.10f), RoundedCornerShape(11.dp))
                    .border(1.dp, accent, RoundedCornerShape(11.dp))
                    .clickable(enabled = !answered) { selected = option.optionId }
                    .padding(13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(28.dp).background(accent.copy(.15f), CircleShape), contentAlignment = Alignment.Center) {
                    Text(option.optionId.uppercase(), color = if (answered) accent else Muted, fontWeight = FontWeight.Bold)
                }
                Text(option.text, color = White, modifier = Modifier.weight(1f).padding(start = 11.dp))
            }
        }
        selected?.let {
            GlowCard(Modifier.fillMaxWidth().padding(top = 11.dp), if (it == question.correctOptionId) Green else Orange) {
                Text(if (it == question.correctOptionId) "CORRECT" else "EXPLANATION", color = if (it == question.correctOptionId) Green else Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(question.explanation, color = White, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 6.dp))
            }
            Button(
                onClick = { if (index < questions.lastIndex) index++ else close() },
                modifier = Modifier.fillMaxWidth().padding(top = 11.dp),
                colors = ButtonDefaults.buttonColors(Purple)
            ) { Text(if (index < questions.lastIndex) "Next question" else "Finish") }
        }
        Spacer(Modifier.height(12.dp))
    }
}

private fun WorkInfo.displayStatus(): String = when (state) {
    WorkInfo.State.ENQUEUED -> "Waiting for network"
    WorkInfo.State.RUNNING -> when (val stage = progress.getString("stage")) {
        "downloading" -> {
            val done = progress.getLong("downloaded_bytes", 0)
            val total = progress.getLong("download_bytes", 0)
            "Downloading ${if (total > 0) done * 100 / total else 0}%"
        }
        "verifying" -> "Verifying download"
        "extracting" -> "Preparing content"
        "importing" -> "Importing ${progress.getInt("imported", 0)} questions"
        else -> stage ?: "Starting"
    }
    WorkInfo.State.SUCCEEDED -> "Installed"
    WorkInfo.State.FAILED -> outputData.getString("error") ?: "Download failed"
    WorkInfo.State.BLOCKED -> "Waiting for storage or network"
    WorkInfo.State.CANCELLED -> "Cancelled"
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
    bytes >= 1024 -> "${bytes / 1024} KB"
    else -> "$bytes B"
}
