package com.indianservers.aichemistryexplorer

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Course(
    val title: String,
    val icon: String,
    val color: Color,
    val description: String,
    val outcomes: List<String>,
    val conceptTitle: String,
    val concept: String,
    val keyIdeas: List<Pair<String, String>>,
    val exampleTitle: String,
    val exampleSteps: List<String>,
    val question: String,
    val answers: List<String>,
    val correct: Int,
    val explanation: String
)

private val courses = listOf(
    Course(
        "Organic Chemistry", "⌬", Purple,
        "Understand how carbon builds the molecular world—from fuels to medicines.",
        listOf("Recognize functional groups", "Name simple organic compounds", "Predict common reaction patterns"),
        "Structure controls reactivity",
        "Carbon forms four covalent bonds. The arrangement of those bonds creates chains, rings and functional groups that determine physical properties and chemical behavior.",
        listOf("Alkanes" to "Only C–C single bonds; relatively unreactive.", "Alkenes" to "Contain C=C bonds that undergo addition.", "Alcohols" to "The –OH group enables hydrogen bonding."),
        "Naming CH₃CH₂OH",
        listOf("Find the longest chain: two carbons → ethane.", "Identify the –OH functional group → alcohol.", "Replace -e with -ol → ethanol."),
        "Which functional group defines an alcohol?",
        listOf("–COOH", "–OH", "–NH₂", "C=C"), 1,
        "Alcohols contain a hydroxyl group, –OH, bonded to a saturated carbon."
    ),
    Course(
        "Inorganic Chemistry", "◇", Green,
        "Explore metals, minerals, salts and the coordination compounds they form.",
        listOf("Classify inorganic compounds", "Assign oxidation states", "Understand coordination complexes"),
        "Oxidation state",
        "Oxidation states track electron ownership in compounds. Their sum equals the overall charge and helps identify oxidation and reduction.",
        listOf("Oxygen" to "Usually has oxidation state −2.", "Hydrogen" to "Usually +1 with non-metals.", "Free elements" to "Have oxidation state 0."),
        "Oxidation state of Mn in KMnO₄",
        listOf("K contributes +1.", "Four O atoms contribute 4 × −2 = −8.", "The neutral sum is zero: +1 + x − 8 = 0, so Mn = +7."),
        "What is the oxidation state of sulfur in SO₄²⁻?",
        listOf("+2", "+4", "+6", "−2"), 2,
        "Four oxygens total −8. To produce a −2 ion, sulfur must contribute +6."
    ),
    Course(
        "Physical Chemistry", "⚛", Orange,
        "Connect microscopic particles with energy, equilibrium and measurable change.",
        listOf("Apply gas relationships", "Interpret energy changes", "Connect rate with molecular collisions"),
        "Energy and spontaneity",
        "Thermodynamics predicts whether change is possible. Enthalpy tracks heat, entropy tracks energy dispersal, and Gibbs energy combines both effects.",
        listOf("ΔH < 0" to "The process releases heat.", "ΔS > 0" to "Energy or matter becomes more dispersed.", "ΔG < 0" to "The process is thermodynamically spontaneous."),
        "Freezing water at −10 °C",
        listOf("Freezing releases heat, so ΔH is negative.", "The liquid becomes ordered, so ΔS is negative.", "Below 0 °C the enthalpy term dominates and ΔG is negative."),
        "Which sign of ΔG indicates a spontaneous process?",
        listOf("Positive", "Negative", "Zero only", "Undefined"), 1,
        "A process is spontaneous under the stated conditions when ΔG is negative."
    ),
    Course(
        "Analytical Chemistry", "⌁", Cyan,
        "Learn how chemists separate, identify and quantify substances.",
        listOf("Choose analytical methods", "Use calibration curves", "Evaluate precision and accuracy"),
        "Signals become concentrations",
        "An instrument measures a signal, not concentration directly. Standards with known concentrations create a calibration relationship used to determine an unknown.",
        listOf("Accuracy" to "Closeness to the accepted value.", "Precision" to "Agreement among repeated measurements.", "Sensitivity" to "Change in signal per concentration unit."),
        "Using a calibration line",
        listOf("Calibration equation: signal = 2.0C + 0.1.", "Unknown signal is 1.1.", "C = (1.1 − 0.1) ÷ 2.0 = 0.50 mol/L."),
        "Repeated values that agree closely demonstrate…",
        listOf("Accuracy", "Precision", "Selectivity", "Sensitivity"), 1,
        "Precision describes repeatability. Values can be precise without being accurate."
    ),
    Course(
        "Atomic Structure", "◉", Cyan,
        "Travel from the nucleus to quantum orbitals and electron configurations.",
        listOf("Distinguish subatomic particles", "Read electron configurations", "Relate orbitals to the periodic table"),
        "Electrons occupy orbitals",
        "Orbitals are probability regions described by quantum numbers. Each orbital holds at most two electrons with opposite spin.",
        listOf("s subshell" to "One orbital; maximum 2 electrons.", "p subshell" to "Three orbitals; maximum 6 electrons.", "d subshell" to "Five orbitals; maximum 10 electrons."),
        "Electron configuration of oxygen",
        listOf("Oxygen has eight electrons.", "Fill 1s², then 2s².", "The remaining four enter 2p → 1s² 2s² 2p⁴."),
        "How many electrons fit in a p subshell?",
        listOf("2", "4", "6", "10"), 2,
        "A p subshell has three orbitals, each holding two electrons, for a total of six."
    ),
    Course(
        "Chemical Bonding", "⚭", Purple,
        "See why atoms bond and how bonding produces molecular shape and properties.",
        listOf("Compare ionic and covalent bonds", "Draw Lewis structures", "Predict molecular geometry"),
        "Atoms share or transfer electrons",
        "Ionic bonding follows electron transfer and electrostatic attraction. Covalent bonding shares electron pairs between nuclei.",
        listOf("Ionic" to "Typically metal + non-metal; forms a lattice.", "Covalent" to "Typically non-metals; forms molecules or networks.", "Metallic" to "Positive centers share delocalized electrons."),
        "Shape of water",
        listOf("Oxygen has two bonding pairs and two lone pairs.", "Four electron domains arrange tetrahedrally.", "Ignoring lone pairs gives a bent molecular shape."),
        "What is the molecular shape of CH₄?",
        listOf("Linear", "Bent", "Trigonal planar", "Tetrahedral"), 3,
        "Four bonding pairs repel equally around carbon, producing a tetrahedral geometry."
    ),
    Course(
        "Thermodynamics", "↯", Color(0xFFC456FF),
        "Track heat, work and energy as chemical systems transform.",
        listOf("Use the first law", "Read energy diagrams", "Calculate reaction enthalpy"),
        "Energy is conserved",
        "The first law states that a system's internal-energy change equals heat added to it plus work done on it: ΔU = q + w.",
        listOf("System" to "The part of the universe under study.", "Surroundings" to "Everything outside the system.", "State function" to "Depends only on initial and final states."),
        "Expansion against pressure",
        listOf("A gas expands by 2.0 L against 100 kPa.", "Work w = −PΔV.", "w = −200 J; energy leaves the system as work."),
        "For an exothermic reaction, ΔH is…",
        listOf("Positive", "Negative", "Always zero", "Temperature only"), 1,
        "An exothermic system releases heat, so its enthalpy change is negative."
    ),
    Course(
        "Electrochemistry", "ϟ", Color(0xFF1DBBFF),
        "Follow electrons through redox reactions, batteries and electrolysis.",
        listOf("Identify oxidation and reduction", "Read electrochemical cells", "Calculate cell potential"),
        "Redox transfers electrons",
        "Oxidation loses electrons and reduction gains them. In a galvanic cell, electrons travel from the anode to the cathode.",
        listOf("Anode" to "Oxidation occurs here.", "Cathode" to "Reduction occurs here.", "Salt bridge" to "Maintains electrical neutrality."),
        "Zn/Cu galvanic cell",
        listOf("Zn → Zn²⁺ + 2e⁻ at the anode.", "Cu²⁺ + 2e⁻ → Cu at the cathode.", "Electrons flow through the wire from Zn to Cu."),
        "Where does reduction occur?",
        listOf("Anode", "Cathode", "Salt bridge", "Electrolyte only"), 1,
        "Reduction always occurs at the cathode in both galvanic and electrolytic cells."
    ),
    Course(
        "Solutions & Equilibria", "≋", Green,
        "Understand concentration, acids, buffers and reversible chemical change.",
        listOf("Calculate concentration", "Apply equilibrium expressions", "Explain buffer action"),
        "Equilibrium is dynamic",
        "At equilibrium, forward and reverse reactions continue at equal rates. Concentrations remain constant, but molecules keep reacting.",
        listOf("K > 1" to "Products are favored at equilibrium.", "K < 1" to "Reactants are favored.", "Catalyst" to "Reaches equilibrium faster but does not change K."),
        "Diluting a solution",
        listOf("Start with 25 mL of 2.0 M solution.", "Dilute to a final volume of 100 mL.", "M₁V₁ = M₂V₂ gives M₂ = 0.50 M."),
        "What does a catalyst change at equilibrium?",
        listOf("The value of K", "Product concentration", "Time to equilibrium", "Reaction enthalpy"), 2,
        "A catalyst speeds both directions and reduces the time needed to reach equilibrium."
    )
)

@Composable
internal fun LearningModuleScreen(moduleTitle: String, onBack: () -> Unit, onPractice: () -> Unit) {
    val course = courses.firstOrNull { it.title == moduleTitle } ?: courses.first()
    var page by remember(moduleTitle) { mutableIntStateOf(0) }
    var selectedAnswer by remember(moduleTitle, page) { mutableIntStateOf(-1) }
    Column(Modifier.fillMaxSize()) {
        ScreenTitle(course.title, "Lesson ${page + 1} of 4", onBack)
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = { (page + 1) / 4f },
                modifier = Modifier.weight(1f).height(6.dp),
                color = course.color, trackColor = Border
            )
            Text("  ${(page + 1) * 25}%", color = course.color, fontSize = 10.sp)
        }
        AnimatedContent(page, modifier = Modifier.weight(1f), label = "lesson-page") { current ->
            when (current) {
                0 -> CourseOverview(course)
                1 -> ConceptPage(course)
                2 -> WorkedExamplePage(course)
                else -> CheckpointPage(course, selectedAnswer) { selectedAnswer = it }
            }
        }
        Row(
            Modifier.fillMaxWidth().background(Color(0xFF030A13)).padding(18.dp, 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (page > 0) {
                Button(
                    onClick = { page-- },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(Panel),
                    shape = RoundedCornerShape(11.dp)
                ) { Text("Previous", color = White) }
            }
            Button(
                onClick = {
                    if (page < 3) page++ else onPractice()
                },
                enabled = page < 3 || selectedAnswer >= 0,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(course.color, disabledContainerColor = Border),
                shape = RoundedCornerShape(11.dp)
            ) { Text(if (page < 3) "Next page" else "Practice quiz", color = Night, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun CourseOverview(course: Course) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        GlowCard(Modifier.fillMaxWidth(), course.color) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(62.dp).background(course.color.copy(.12f), CircleShape), contentAlignment = Alignment.Center) {
                    Text(course.icon, color = course.color, fontSize = 32.sp)
                }
                Spacer(Modifier.size(14.dp))
                Column {
                    Tag("MODULE OVERVIEW", course.color)
                    Spacer(Modifier.height(8.dp))
                    Text(course.description, color = White, fontSize = 16.sp, lineHeight = 23.sp)
                }
            }
        }
        Text("WHAT YOU’LL LEARN", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
        course.outcomes.forEachIndexed { index, outcome ->
            LessonRow("${index + 1}", outcome, course.color)
        }
        Text("4 pages · 8 min · Interactive checkpoint", color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(20.dp))
    }
}

@Composable
private fun ConceptPage(course: Course) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        ConceptVisual(course.color, course.icon)
        Text(course.conceptTitle, color = White, fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp))
        Text(course.concept, color = Muted, fontSize = 14.sp, lineHeight = 22.sp, modifier = Modifier.padding(top = 8.dp, bottom = 18.dp))
        course.keyIdeas.forEach { (title, body) ->
            GlowCard(Modifier.fillMaxWidth().padding(bottom = 9.dp), course.color) {
                Text(title, color = course.color, fontWeight = FontWeight.Bold)
                Text(body, color = White.copy(.78f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun WorkedExamplePage(course: Course) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Tag("WORKED EXAMPLE", Orange)
        Text(course.exampleTitle, color = White, fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 14.dp))
        course.exampleSteps.forEachIndexed { index, step ->
            Row(Modifier.fillMaxWidth().padding(bottom = 13.dp)) {
                Box(Modifier.size(34.dp).background(course.color.copy(.14f), CircleShape), contentAlignment = Alignment.Center) {
                    Text("${index + 1}", color = course.color, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.size(12.dp))
                GlowCard(Modifier.weight(1f), course.color) {
                    Text(step, color = White, fontSize = 13.sp, lineHeight = 20.sp)
                }
            }
        }
        Text("✓ Example complete", color = Green, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun CheckpointPage(course: Course, selected: Int, onSelect: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Tag("CHECKPOINT", course.color)
        Text(course.question, color = White, fontSize = 21.sp, fontWeight = FontWeight.Bold, lineHeight = 29.sp, modifier = Modifier.padding(vertical = 18.dp))
        course.answers.forEachIndexed { index, answer ->
            val correct = selected >= 0 && index == course.correct
            val wrong = selected == index && index != course.correct
            val borderColor = if (correct) Green else if (wrong) Color(0xFFFF6579) else Border
            Row(
                Modifier.fillMaxWidth().padding(bottom = 9.dp).background(borderColor.copy(.1f), RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .clickable(enabled = selected < 0) { onSelect(index) }.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(('A' + index).toString(), color = borderColor, fontWeight = FontWeight.Bold)
                Text(answer, color = White, modifier = Modifier.weight(1f).padding(horizontal = 14.dp))
                if (correct) Text("✓", color = Green)
            }
        }
        if (selected >= 0) {
            GlowCard(Modifier.fillMaxWidth().padding(top = 9.dp), if (selected == course.correct) Green else Orange) {
                Text(if (selected == course.correct) "Excellent" else "Review this idea", color = if (selected == course.correct) Green else Orange, fontWeight = FontWeight.Bold)
                Text(course.explanation, color = Muted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun LessonRow(number: String, text: String, color: Color) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 9.dp).background(Panel, RoundedCornerShape(11.dp))
            .border(1.dp, Border, RoundedCornerShape(11.dp)).padding(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(30.dp).background(color.copy(.14f), CircleShape), contentAlignment = Alignment.Center) {
            Text(number, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Text(text, color = White, fontSize = 13.sp, modifier = Modifier.padding(start = 12.dp))
    }
}

@Composable
private fun ConceptVisual(color: Color, icon: String) {
    Box(Modifier.fillMaxWidth().height(175.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Brush.radialGradient(listOf(color.copy(.25f), Color.Transparent), center, size.minDimension * .5f), size.minDimension * .45f)
            repeat(3) { index ->
                drawCircle(color.copy(.2f + index * .12f), 35f + index * 38f, center, style = Stroke(2f))
            }
            drawLine(color.copy(.5f), Offset(size.width * .18f, size.height * .75f), Offset(size.width * .82f, size.height * .25f), 3f, StrokeCap.Round)
        }
        Text(icon, color = color, fontSize = 58.sp)
    }
}
