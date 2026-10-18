package com.indianservers.aichemistryexplorer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal data class MolecularAtom(
    val element: String,
    val x: Float,
    val y: Float,
    val z: Float = 0f,
    val radius: Float = 1f
)

internal data class MolecularBond(val from: Int, val to: Int, val order: Int = 1)

internal data class LiveMolecularModel(
    val name: String,
    val formula: String,
    val geometry: String,
    val atoms: List<MolecularAtom>,
    val bonds: List<MolecularBond>
)

internal enum class MolecularResponse { ENERGY, DENSITY, REACTION, POLARITY, MODEL }

internal data class MolecularSnapshot(
    val particleCopies: Int,
    val motion: Float,
    val spacing: Float,
    val state: String
)

internal fun molecularResponseFor(controlLabel: String): MolecularResponse {
    val key = controlLabel.lowercase()
    return when {
        listOf("temperature", "energy", "activation", "heat").any(key::contains) -> MolecularResponse.ENERGY
        listOf("concentration", "pressure", "amount", "volume").any(key::contains) -> MolecularResponse.DENSITY
        listOf("reaction", "progress", "equilibrium", "yield").any(key::contains) -> MolecularResponse.REACTION
        listOf("polarity", "electron density", "electronegativity").any(key::contains) -> MolecularResponse.POLARITY
        else -> MolecularResponse.MODEL
    }
}

internal fun molecularSnapshot(response: MolecularResponse, rawControl: Float): MolecularSnapshot {
    val control = rawControl.coerceIn(0f, 1f)
    return when (response) {
        MolecularResponse.ENERGY -> MolecularSnapshot(
            particleCopies = 1,
            motion = .15f + .85f * control,
            spacing = 1f,
            state = if (control < .5f) "Lower kinetic energy · gentle vibration" else "Higher kinetic energy · rapid vibration"
        )
        MolecularResponse.DENSITY -> MolecularSnapshot(
            particleCopies = 2 + (control * 4f).toInt(),
            motion = .25f + .45f * control,
            spacing = 1.2f - .45f * control,
            state = if (control < .5f) "Particles are farther apart" else "More frequent molecular encounters"
        )
        MolecularResponse.REACTION -> MolecularSnapshot(
            particleCopies = 1,
            motion = .25f + .65f * control,
            spacing = 1f,
            state = "Reaction coordinate · ${(control * 100).toInt()}% progressed"
        )
        MolecularResponse.POLARITY -> MolecularSnapshot(
            particleCopies = 1,
            motion = .2f + .25f * control,
            spacing = 1f,
            state = if (control < .5f) "Electron density is more even" else "Electron density is strongly displaced"
        )
        MolecularResponse.MODEL -> MolecularSnapshot(
            particleCopies = 1,
            motion = .2f + .5f * control,
            spacing = 1f,
            state = if (control < .5f) "Low-input molecular configuration" else "High-input molecular response"
        )
    }
}

internal fun liveMolecularModelFor(moduleTitle: String, lessonTitle: String): LiveMolecularModel {
    val key = "$moduleTitle $lessonTitle".lowercase()
    return when {
        "benzene" in key || "aromatic" in key -> moleculeBenzene()
        "ethyne" in key || "alkyne" in key || "triple bond" in key -> moleculeEthyne()
        "ethene" in key || "alkene" in key || "addition reaction" in key -> moleculeEthene()
        "ethanol" in key || "alcohol" in key -> moleculeEthanol()
        "ammonia" in key || "amine" in key -> moleculeAmmonia()
        "methane" in key || "alkane" in key || "hydrocarbon" in key || "combustion" in key -> moleculeMethane()
        "carbon dioxide" in key || "gas law" in key || "gaseous state" in key -> moleculeCarbonDioxide()
        "nitrogen" in key || "nitrile" in key -> moleculeNitrogen()
        "oxygen" in key || "oxidation" in key || "peroxide" in key -> moleculeOxygen()
        "hydrogen chloride" in key || "hydrochloric" in key || "hcl" in key -> moleculeHydrogenChloride()
        else -> moleculeWater()
    }
}

private enum class MolecularRenderStyle(val label: String) {
    BALL_AND_STICK("BALL + STICK"), SPACE_FILL("SPACE FILL")
}

@Composable
internal fun LiveMolecularView(
    module: CurriculumModule,
    lesson: CurriculumLesson,
    simulation: InteractiveSimulationSpec,
    control: Float,
    running: Boolean,
    modifier: Modifier = Modifier
) {
    val model = remember(module.title, lesson.title) { liveMolecularModelFor(module.title, lesson.title) }
    val response = remember(simulation.controlLabel) { molecularResponseFor(simulation.controlLabel) }
    val snapshot = molecularSnapshot(response, control)
    var style by remember(module.title, lesson.title) { mutableStateOf(MolecularRenderStyle.BALL_AND_STICK) }
    val reduceMotion = LocalReducedMotion.current
    val transition = rememberInfiniteTransition(label = "live-molecule")
    val animatedPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing), RepeatMode.Restart),
        label = "molecule-phase"
    )
    val phase = if (running && !reduceMotion) animatedPhase else .55f

    GlowCard(modifier.fillMaxWidth().padding(top = 8.dp), module.color) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("LIVE MOLECULAR VIEW", color = module.color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("${model.name}  ·  ${model.formula}", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(model.geometry, color = Muted, fontSize = 9.sp)
            }
            Text(
                if (running) "● LIVE" else "Ⅱ PAUSED",
                color = if (running) Green else Orange,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row(Modifier.fillMaxWidth().padding(top = 9.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            MolecularRenderStyle.entries.forEach { option ->
                Text(
                    option.label,
                    color = if (style == option) Night else Muted,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                        .background(if (style == option) module.color else Panel, RoundedCornerShape(7.dp))
                        .border(1.dp, if (style == option) module.color else Border, RoundedCornerShape(7.dp))
                        .clickable { style = option }
                        .padding(vertical = 7.dp)
                )
            }
        }
        Box(
            Modifier.fillMaxWidth().height(230.dp).padding(top = 8.dp)
                .background(
                    Brush.radialGradient(listOf(module.color.copy(.14f), Night.copy(.92f))),
                    RoundedCornerShape(12.dp)
                )
                .border(1.dp, Border, RoundedCornerShape(12.dp))
                .semantics {
                    contentDescription = "Animated ${model.name} molecular model, ${model.formula}, ${snapshot.state}"
                }
        ) {
            MolecularCanvas(model, snapshot, style, phase, control, module.color)
            Text(
                "Drag the experiment control to change molecular behaviour",
                color = Muted,
                fontSize = 8.sp,
                modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
            )
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(snapshot.state, color = White, fontSize = 9.sp, modifier = Modifier.weight(1f))
            Text("${model.atoms.size} ATOMS · ${model.bonds.sumOf { it.order }} BONDS", color = Cyan, fontSize = 8.sp)
        }
        ElementLegend(model)
    }
}

@Composable
private fun MolecularCanvas(
    model: LiveMolecularModel,
    snapshot: MolecularSnapshot,
    style: MolecularRenderStyle,
    phase: Float,
    control: Float,
    accent: Color
) {
    Canvas(Modifier.fillMaxSize()) {
        val anchors = when (snapshot.particleCopies) {
            1 -> listOf(Offset(.5f, .47f))
            2 -> listOf(Offset(.32f, .43f), Offset(.68f, .52f))
            3 -> listOf(Offset(.25f, .35f), Offset(.7f, .35f), Offset(.5f, .67f))
            4 -> listOf(Offset(.27f, .31f), Offset(.72f, .33f), Offset(.3f, .68f), Offset(.7f, .66f))
            5 -> listOf(Offset(.2f, .3f), Offset(.5f, .27f), Offset(.8f, .32f), Offset(.32f, .68f), Offset(.68f, .67f))
            else -> listOf(Offset(.18f, .3f), Offset(.5f, .27f), Offset(.82f, .31f), Offset(.2f, .67f), Offset(.5f, .7f), Offset(.8f, .66f))
        }
        val baseScale = if (anchors.size == 1) size.minDimension * .28f else size.minDimension * .105f
        anchors.forEachIndexed { copyIndex, anchorFraction ->
            val localPhase = phase + copyIndex * .83f
            val drift = if (anchors.size == 1) 1f else snapshot.spacing
            val anchor = Offset(
                size.width * (.5f + (anchorFraction.x - .5f) * drift) + sin(localPhase * 1.7f) * 5f * snapshot.motion,
                size.height * (.5f + (anchorFraction.y - .5f) * drift) + cos(localPhase * 1.3f) * 5f * snapshot.motion
            )
            drawMolecule(model, anchor, baseScale, localPhase, snapshot.motion, style, control, accent)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMolecule(
    model: LiveMolecularModel,
    anchor: Offset,
    scale: Float,
    phase: Float,
    motion: Float,
    style: MolecularRenderStyle,
    control: Float,
    accent: Color
) {
    val angle = phase * (.12f + .18f * motion)
    val ca = cos(angle)
    val sa = sin(angle)
    data class Projected(val atom: MolecularAtom, val point: Offset, val depth: Float)
    val projected = model.atoms.mapIndexed { index, atom ->
        val rotatedX = atom.x * ca + atom.z * sa
        val depth = -atom.x * sa + atom.z * ca
        val vibration = sin(phase * (1.5f + motion) + index * 1.37f) * scale * .035f * motion
        Projected(
            atom,
            Offset(anchor.x + rotatedX * scale + vibration, anchor.y + atom.y * scale + depth * scale * .22f),
            depth
        )
    }
    model.bonds.forEach { bond ->
        val start = projected[bond.from].point
        val end = projected[bond.to].point
        val dx = end.x - start.x
        val dy = end.y - start.y
        val length = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
        val normal = Offset(-dy / length, dx / length)
        val gap = 4f
        repeat(bond.order) { line ->
            val offset = normal * ((line - (bond.order - 1) / 2f) * gap)
            drawLine(
                color = Color(0xFFAFC2D4).copy(if (style == MolecularRenderStyle.SPACE_FILL) .32f else .9f),
                start = start + offset,
                end = end + offset,
                strokeWidth = if (style == MolecularRenderStyle.SPACE_FILL) 7f else 4f
            )
        }
    }
    projected.sortedBy { it.depth }.forEach { item ->
        val elementColor = elementColor(item.atom.element)
        val depthScale = (.92f + item.depth * .08f).coerceIn(.78f, 1.16f)
        val styleScale = if (style == MolecularRenderStyle.SPACE_FILL) 1.62f else 1f
        val radius = scale * .18f * item.atom.radius * depthScale * styleScale
        if (control > .55f) {
            drawCircle(accent.copy((control - .5f) * .25f), radius * 1.65f, item.point, style = Stroke(2f))
        }
        drawCircle(
            Brush.radialGradient(
                listOf(Color.White.copy(.95f), elementColor, elementColor.copy(.58f)),
                center = item.point - Offset(radius * .34f, radius * .34f),
                radius = radius * 1.25f
            ),
            radius,
            item.point
        )
        drawCircle(Color.White.copy(.3f), radius, item.point, style = Stroke(1.2f))
    }
}

@Composable
private fun ElementLegend(model: LiveMolecularModel) {
    Row(Modifier.fillMaxWidth().padding(top = 7.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        model.atoms.map { it.element }.distinct().forEach { element ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.padding(end = 4.dp).background(elementColor(element), RoundedCornerShape(50)).size(8.dp))
                Text("$element · ${elementName(element)}", color = Muted, fontSize = 8.sp)
            }
        }
    }
}

private fun elementColor(element: String): Color = when (element) {
    "H" -> Color(0xFFF4F7FB)
    "C" -> Color(0xFF566779)
    "N" -> Color(0xFF3977FF)
    "O" -> Color(0xFFFF4B55)
    "Cl" -> Color(0xFF4FD56A)
    "S" -> Color(0xFFFFD644)
    else -> Color(0xFFC46CFF)
}

private fun elementName(element: String): String = when (element) {
    "H" -> "Hydrogen"
    "C" -> "Carbon"
    "N" -> "Nitrogen"
    "O" -> "Oxygen"
    "Cl" -> "Chlorine"
    "S" -> "Sulfur"
    else -> element
}

private fun moleculeWater() = LiveMolecularModel(
    "Water", "H₂O", "Bent · 104.5°", listOf(
        MolecularAtom("O", 0f, 0f, radius = 1.18f), MolecularAtom("H", -.78f, .62f, radius = .72f), MolecularAtom("H", .78f, .62f, radius = .72f)
    ), listOf(MolecularBond(0, 1), MolecularBond(0, 2))
)

private fun moleculeCarbonDioxide() = LiveMolecularModel(
    "Carbon dioxide", "CO₂", "Linear · 180°", listOf(
        MolecularAtom("O", -1f, 0f, radius = 1.1f), MolecularAtom("C", 0f, 0f), MolecularAtom("O", 1f, 0f, radius = 1.1f)
    ), listOf(MolecularBond(0, 1, 2), MolecularBond(1, 2, 2))
)

private fun moleculeMethane() = LiveMolecularModel(
    "Methane", "CH₄", "Tetrahedral · 109.5°", listOf(
        MolecularAtom("C", 0f, 0f, 0f, 1.08f), MolecularAtom("H", -.8f, -.65f, .55f, .7f),
        MolecularAtom("H", .8f, -.65f, .55f, .7f), MolecularAtom("H", 0f, .82f, -.72f, .7f), MolecularAtom("H", 0f, .35f, .95f, .7f)
    ), (1..4).map { MolecularBond(0, it) }
)

private fun moleculeAmmonia() = LiveMolecularModel(
    "Ammonia", "NH₃", "Trigonal pyramidal · 107°", listOf(
        MolecularAtom("N", 0f, -.18f, radius = 1.1f), MolecularAtom("H", -.82f, .58f, .25f, .7f),
        MolecularAtom("H", .82f, .58f, .25f, .7f), MolecularAtom("H", 0f, .62f, -.72f, .7f)
    ), (1..3).map { MolecularBond(0, it) }
)

private fun moleculeOxygen() = LiveMolecularModel(
    "Oxygen", "O₂", "Diatomic · double bond", listOf(MolecularAtom("O", -.55f, 0f, radius = 1.1f), MolecularAtom("O", .55f, 0f, radius = 1.1f)),
    listOf(MolecularBond(0, 1, 2))
)

private fun moleculeNitrogen() = LiveMolecularModel(
    "Nitrogen", "N₂", "Diatomic · triple bond", listOf(MolecularAtom("N", -.55f, 0f, radius = 1.1f), MolecularAtom("N", .55f, 0f, radius = 1.1f)),
    listOf(MolecularBond(0, 1, 3))
)

private fun moleculeHydrogenChloride() = LiveMolecularModel(
    "Hydrogen chloride", "HCl", "Linear · polar covalent", listOf(MolecularAtom("H", -.58f, 0f, radius = .72f), MolecularAtom("Cl", .48f, 0f, radius = 1.35f)),
    listOf(MolecularBond(0, 1))
)

private fun moleculeEthene() = LiveMolecularModel(
    "Ethene", "C₂H₄", "Planar · C=C double bond", listOf(
        MolecularAtom("C", -.48f, 0f), MolecularAtom("C", .48f, 0f),
        MolecularAtom("H", -.95f, -.72f, radius = .65f), MolecularAtom("H", -.95f, .72f, radius = .65f),
        MolecularAtom("H", .95f, -.72f, radius = .65f), MolecularAtom("H", .95f, .72f, radius = .65f)
    ), listOf(MolecularBond(0, 1, 2), MolecularBond(0, 2), MolecularBond(0, 3), MolecularBond(1, 4), MolecularBond(1, 5))
)

private fun moleculeEthyne() = LiveMolecularModel(
    "Ethyne", "C₂H₂", "Linear · C≡C triple bond", listOf(
        MolecularAtom("H", -1.25f, 0f, radius = .65f), MolecularAtom("C", -.43f, 0f), MolecularAtom("C", .43f, 0f), MolecularAtom("H", 1.25f, 0f, radius = .65f)
    ), listOf(MolecularBond(0, 1), MolecularBond(1, 2, 3), MolecularBond(2, 3))
)

private fun moleculeEthanol() = LiveMolecularModel(
    "Ethanol", "C₂H₅OH", "Tetrahedral carbons · polar O–H", listOf(
        MolecularAtom("C", -.72f, .12f), MolecularAtom("C", .12f, -.12f), MolecularAtom("O", .87f, .18f, radius = 1.08f),
        MolecularAtom("H", 1.38f, -.35f, radius = .62f), MolecularAtom("H", -.98f, -.7f, .25f, .62f),
        MolecularAtom("H", -1.2f, .62f, -.2f, .62f), MolecularAtom("H", -.55f, .55f, .72f, .62f),
        MolecularAtom("H", .15f, -.75f, -.45f, .62f), MolecularAtom("H", .25f, .48f, -.5f, .62f)
    ), listOf(MolecularBond(0, 1), MolecularBond(1, 2), MolecularBond(2, 3), MolecularBond(0, 4), MolecularBond(0, 5), MolecularBond(0, 6), MolecularBond(1, 7), MolecularBond(1, 8))
)

private fun moleculeBenzene(): LiveMolecularModel {
    val carbons = (0 until 6).map { index ->
        val angle = index * (PI / 3.0)
        MolecularAtom("C", cos(angle).toFloat(), sin(angle).toFloat())
    }
    val hydrogens = (0 until 6).map { index ->
        val angle = index * (PI / 3.0)
        MolecularAtom("H", (cos(angle) * 1.62).toFloat(), (sin(angle) * 1.62).toFloat(), radius = .6f)
    }
    val ringBonds = (0 until 6).map { MolecularBond(it, (it + 1) % 6, if (it % 2 == 0) 2 else 1) }
    val hydrogenBonds = (0 until 6).map { MolecularBond(it, it + 6) }
    return LiveMolecularModel("Benzene", "C₆H₆", "Planar aromatic ring · delocalised π system", carbons + hydrogens, ringBonds + hydrogenBonds)
}
