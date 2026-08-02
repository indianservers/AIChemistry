package com.indianservers.aichemistryexplorer

internal data class MicroConcept(
    val id: String,
    val title: String,
    val explanation: String,
    val example: String,
    val masteryPrompt: String
)

internal enum class SimulationMode {
    PARAMETER_SWEEP,
    PARTICLE_MODEL,
    EVIDENCE_CHALLENGE
}

internal data class InteractiveSimulationSpec(
    val id: String,
    val title: String,
    val mode: SimulationMode,
    val objective: String,
    val controlLabel: String,
    val lowState: String,
    val highState: String,
    val observationGuide: String
)

internal enum class Visualization3DMode {
    PARTICLE_ARRANGEMENT,
    BONDING_STRUCTURE,
    ENERGY_LANDSCAPE,
    PROCESS_PATHWAY,
    DATA_INSTRUMENT
}

internal enum class Chemistry3DScene {
    ATOMIC_ORBITAL,
    ELECTRON_DENSITY,
    MOLECULAR_GEOMETRY,
    CRYSTAL_LATTICE,
    PHASE_PARTICLES,
    ENERGY_SURFACE,
    REACTION_COORDINATE,
    EQUILIBRIUM_CHAMBER,
    SURFACE_INTERFACE,
    ELECTROCHEMICAL_CELL,
    SPECTRAL_RESPONSE,
    SEPARATION_COLUMN,
    BIOPOLYMER,
    POLYMER_CHAIN,
    NUCLEAR_TRANSFORMATION,
    ENVIRONMENTAL_CYCLE,
    PROCESS_FLOW,
    DATA_SURFACE
}

internal enum class ChemistryMotionStyle {
    ORBITAL_PULSE,
    BOND_BREATHING,
    COLLISION_FLOW,
    LATTICE_PHONON,
    ENERGY_WAVE,
    REACTION_CASCADE,
    EQUILIBRIUM_EXCHANGE,
    SURFACE_ADSORPTION,
    ELECTRON_TRANSFER,
    SPECTRAL_SCAN,
    SEPARATION_MIGRATION,
    BIOPOLYMER_TWIST,
    POLYMER_FLEX,
    NUCLEAR_EVENT,
    ENVIRONMENTAL_TRANSPORT,
    PROCESS_THROUGHPUT,
    DATA_RIPPLE
}

/**
 * Chemical subject encoded by the procedural model. The scene describes the
 * camera/view (structure, energy, process, evidence); the motif describes what
 * chemistry is actually being drawn. There is deliberately no GENERIC motif.
 */
internal enum class ChemistryVisualMotif {
    MATTER_STATES, METROLOGY, ATOMIC_MODEL, PERIODIC_TRENDS, STOICHIOMETRY,
    BOND_GEOMETRY, REACTION_CLASS, GAS_LAWS, SOLVATION, ACID_BASE,
    THERMODYNAMICS, CALORIMETRY, EQUILIBRIUM, KINETICS, ELECTROCHEMISTRY,
    SURFACE_ASSEMBLY, CRYSTAL_SOLID, NUCLEAR, ORGANIC_MECHANISM, COORDINATION,
    SPECTROSCOPY, SEPARATION, COMPUTATIONAL, POLYMER, INDUSTRIAL_PROCESS,
    ENVIRONMENTAL_CYCLE, BIOCHEMISTRY, MEDICINAL_BINDING, MATERIALS,
    ANALYTICAL_MEASUREMENT, PHOTOCHEMISTRY, SUPRAMOLECULAR, LABORATORY,
    INSTRUMENTATION, CHEMICAL_ENGINEERING, AGRICULTURAL, FORENSIC,
    ASTROCHEMISTRY, FOOD_CHEMISTRY, DESCRIPTIVE_INORGANIC,
    PHASE_TRANSITION, PLASMA_CLOUD, BOSE_EINSTEIN_CONDENSATE,
    UNCERTAINTY_ANALYSIS, DENSITY_MEASUREMENT, PERIODIC_CLASSIFICATION,
    ELECTRON_CONFIGURATION, MOLECULAR_FORMULA, LIMITING_REAGENT,
    REDOX_TRANSFER, COMBUSTION_FLAME, IDEAL_GAS_CHAMBER, LIQUID_INTERFACE,
    BUFFER_SYSTEM, TITRATION_CURVE, CATALYTIC_CYCLE, GALVANIC_BATTERY,
    ELECTROLYSIS_CELL, CORROSION_FRONT, COLLOID_DISPERSION, MICELLE_ASSEMBLY,
    CRYSTAL_PACKING, SEMICONDUCTOR_BANDS, NUCLEIC_ACID, PROTEIN_FOLDING,
    ENZYME_CATALYSIS, METABOLIC_PATHWAY, DRUG_ADME, NANOMATERIAL,
    SOLAR_CELL, CHROMATOGRAPHY, MAGNETIC_RESONANCE, MASS_SPECTROMETRY,
    DIFFRACTION_PATTERN, QUANTUM_CHEMISTRY, MOLECULAR_DYNAMICS,
    FLOW_CHEMISTRY, MICROFLUIDICS, GREEN_CHEMISTRY, BATTERY_MATERIAL
}

internal data class Visualization3DSpec(
    val id: String,
    val title: String,
    val mode: Visualization3DMode,
    val scene: Chemistry3DScene,
    val motif: ChemistryVisualMotif,
    val variantSeed: Int,
    val motionStyle: ChemistryMotionStyle,
    val motionSpeed: Float,
    val motionIntensity: Float,
    val conceptTitle: String,
    val description: String,
    val interactionHint: String,
    val scientificFocus: String,
    val entityLabels: List<String>,
    val stageLabels: List<String>,
    val axisLabels: List<String>,
    val keyRelationship: String
)

internal data class SubtopicExpansion(
    val id: String,
    val level: String,
    val majorTopic: String,
    val subtopic: String,
    val visualMotif: ChemistryVisualMotif,
    val virtualLab: AuthenticLabSpec,
    val microConcepts: List<MicroConcept>,
    val simulations: List<InteractiveSimulationSpec>,
    val visualizations3d: List<Visualization3DSpec>
)

private fun stableSlug(value: String): String = value
    .lowercase()
    .replace(Regex("[^a-z0-9]+"), "-")
    .trim('-')

private fun buildMicroConcepts(
    id: String,
    module: CurriculumModule,
    lesson: CurriculumLesson,
    includeExtension: Boolean
): List<MicroConcept> {
    val core = listOf(
        MicroConcept(
            id = "$id-definition",
            title = "Definition and scientific scope",
            explanation = "${lesson.concept} Interpret this definition using the stated chemical entities, conditions and level-appropriate model.",
            example = lesson.workedExample,
            masteryPrompt = "Define ${lesson.title}, identify the chemical entities involved and distinguish it from a closely related idea."
        ),
        MicroConcept(
            id = "$id-model",
            title = "Governing model and relationship",
            explanation = "Use ${lesson.formula} as a compact model. Define each symbol, state the conditions and connect the relationship to ${lesson.title}.",
            example = lesson.exampleSteps.joinToString(" ") { it }.take(700),
            masteryPrompt = "Apply the relationship with consistent units, then explain why the result is chemically plausible."
        ),
        MicroConcept(
            id = "$id-evidence",
            title = "Evidence, measurement and interpretation",
            explanation = "A strong conclusion about ${lesson.title} combines observation, calibration or model output with uncertainty and an independent chemical check.",
            example = "Evidence check: ${lesson.commonMistake} ${lesson.realWorldApplication}",
            masteryPrompt = "Choose evidence that could distinguish the proposed explanation from a plausible alternative."
        )
    )
    if (!includeExtension) return core
    return core + MicroConcept(
        id = "$id-boundaries",
        title = "Boundary conditions and transfer",
        explanation = "Evaluate where the model for ${lesson.title} succeeds, where non-ideal behaviour or experimental limitations matter, and how it connects with ${module.title}.",
        example = "Extension: ${lesson.advancedConcepts.joinToString("; ")}.",
        masteryPrompt = "Predict how changing one assumption would alter the result and identify a suitable validation method."
    )
}

private fun buildSimulations(
    id: String,
    module: CurriculumModule,
    lesson: CurriculumLesson
): List<InteractiveSimulationSpec> = listOf(
    InteractiveSimulationSpec(
        id = "$id-parameter-sweep",
        title = "Relationship explorer",
        mode = SimulationMode.PARAMETER_SWEEP,
        objective = "Vary the main quantity in ${lesson.formula} and predict the response before revealing it.",
        controlLabel = "Normalized driving variable",
        lowState = "Low-input or limiting-condition regime",
        highState = "High-input or strong-driving regime",
        observationGuide = "Track direction, proportionality, units and the point at which the stated model may stop being reliable."
    ),
    InteractiveSimulationSpec(
        id = "$id-particle-model",
        title = "Molecular mechanism lab",
        mode = SimulationMode.PARTICLE_MODEL,
        objective = "Connect the macroscopic behaviour of ${lesson.title} with particles, bonds, forces or electron movement.",
        controlLabel = "Molecular energy or interaction strength",
        lowState = "Few effective events; structure or attraction dominates",
        highState = "More energetic events; rearrangement or transport dominates",
        observationGuide = "Explain which microscopic change causes the displayed macroscopic response."
    ),
    InteractiveSimulationSpec(
        id = "$id-evidence-challenge",
        title = "Evidence and model challenge",
        mode = SimulationMode.EVIDENCE_CHALLENGE,
        objective = "Compare a prediction for ${lesson.title} with simulated evidence and diagnose disagreement.",
        controlLabel = "Evidence quality and model agreement",
        lowState = "Noisy, biased or model-inconsistent evidence",
        highState = "Calibrated, reproducible and model-consistent evidence",
        observationGuide = "Decide whether to accept the model, repeat the measurement or revise an assumption."
    )
)

private fun buildVisualizations(
    id: String,
    module: CurriculumModule,
    lesson: CurriculumLesson,
    motif: ChemistryVisualMotif
): List<Visualization3DSpec> {
    val profile = visualizationProfile(module, lesson)
    val conceptSeed = stableVisualSeed("${module.level}|${module.title}|${lesson.title}")
    val modes = Visualization3DMode.entries
    val viewNames = listOf(
        "Structure and entities",
        "Spatial interaction",
        "Energy and stability",
        "Mechanism or process",
        "Evidence and measurement"
    )
    val hints = listOf(
        "Rotate ${lesson.title} and inspect the spatial relationship between ${profile.entities.take(3).joinToString(", ")}.",
        "Change orientation in ${lesson.title} to reveal how ${profile.entities.takeLast(2).joinToString(" and ")} produce the stated behaviour.",
        "Sweep the ${profile.axes.first()} coordinate and interpret the landscape using ${lesson.formula}.",
        "Move through ${profile.stages.take(3).joinToString(" → ")} and check conservation at every stage.",
        "Rotate the ${profile.axes.take(2).joinToString(" versus ")} response and connect it to evidence for ${lesson.title}."
    )
    return profile.scenes.mapIndexed { index, scene ->
        Visualization3DSpec(
            id = "$id-${scene.name.lowercase().replace('_', '-')}",
            title = "${lesson.title} · ${viewNames[index]}",
            mode = modes[index],
            scene = scene,
            motif = motif,
            variantSeed = conceptSeed + index * 97,
            motionStyle = motionStyleFor(scene),
            motionSpeed = motionSpeedFor(motif, conceptSeed + index * 97),
            motionIntensity = .58f + ((conceptSeed + index * 43) % 37) / 100f,
            conceptTitle = lesson.title,
            description = profile.descriptions[index],
            interactionHint = hints[index],
            scientificFocus = profile.focus,
            entityLabels = profile.entities,
            stageLabels = profile.stages,
            axisLabels = profile.axes,
            keyRelationship = lesson.formula
        )
    }
}

private fun motionStyleFor(scene: Chemistry3DScene): ChemistryMotionStyle = when (scene) {
    Chemistry3DScene.ATOMIC_ORBITAL, Chemistry3DScene.ELECTRON_DENSITY ->
        ChemistryMotionStyle.ORBITAL_PULSE
    Chemistry3DScene.MOLECULAR_GEOMETRY -> ChemistryMotionStyle.BOND_BREATHING
    Chemistry3DScene.CRYSTAL_LATTICE -> ChemistryMotionStyle.LATTICE_PHONON
    Chemistry3DScene.PHASE_PARTICLES -> ChemistryMotionStyle.COLLISION_FLOW
    Chemistry3DScene.ENERGY_SURFACE -> ChemistryMotionStyle.ENERGY_WAVE
    Chemistry3DScene.REACTION_COORDINATE -> ChemistryMotionStyle.REACTION_CASCADE
    Chemistry3DScene.EQUILIBRIUM_CHAMBER -> ChemistryMotionStyle.EQUILIBRIUM_EXCHANGE
    Chemistry3DScene.SURFACE_INTERFACE -> ChemistryMotionStyle.SURFACE_ADSORPTION
    Chemistry3DScene.ELECTROCHEMICAL_CELL -> ChemistryMotionStyle.ELECTRON_TRANSFER
    Chemistry3DScene.SPECTRAL_RESPONSE -> ChemistryMotionStyle.SPECTRAL_SCAN
    Chemistry3DScene.SEPARATION_COLUMN -> ChemistryMotionStyle.SEPARATION_MIGRATION
    Chemistry3DScene.BIOPOLYMER -> ChemistryMotionStyle.BIOPOLYMER_TWIST
    Chemistry3DScene.POLYMER_CHAIN -> ChemistryMotionStyle.POLYMER_FLEX
    Chemistry3DScene.NUCLEAR_TRANSFORMATION -> ChemistryMotionStyle.NUCLEAR_EVENT
    Chemistry3DScene.ENVIRONMENTAL_CYCLE -> ChemistryMotionStyle.ENVIRONMENTAL_TRANSPORT
    Chemistry3DScene.PROCESS_FLOW -> ChemistryMotionStyle.PROCESS_THROUGHPUT
    Chemistry3DScene.DATA_SURFACE -> ChemistryMotionStyle.DATA_RIPPLE
}

private fun motionSpeedFor(motif: ChemistryVisualMotif, seed: Int): Float {
    val base = when (motif) {
        ChemistryVisualMotif.BOSE_EINSTEIN_CONDENSATE, ChemistryVisualMotif.CRYSTAL_SOLID,
        ChemistryVisualMotif.CRYSTAL_PACKING -> .32f
        ChemistryVisualMotif.PLASMA_CLOUD, ChemistryVisualMotif.COMBUSTION_FLAME,
        ChemistryVisualMotif.NUCLEAR, ChemistryVisualMotif.CATALYTIC_CYCLE -> 1.18f
        ChemistryVisualMotif.PROTEIN_FOLDING, ChemistryVisualMotif.NUCLEIC_ACID,
        ChemistryVisualMotif.SUPRAMOLECULAR -> .62f
        else -> .78f
    }
    return (base + (seed % 23) / 100f).coerceIn(.28f, 1.4f)
}

private fun stableVisualSeed(value: String): Int =
    value.fold(17) { hash, character -> (hash * 31 + character.code) and 0x7fffffff }

private fun visualMotif(module: CurriculumModule, lesson: CurriculumLesson): ChemistryVisualMotif {
    val topic = module.title.lowercase()
    val title = lesson.title.lowercase()
    val category = module.category.lowercase()
    val text = "$topic $title"
    fun has(vararg terms: String) = terms.any { it in text }

    return when {
        title == "plasma" -> ChemistryVisualMotif.PLASMA_CLOUD
        has("bose-einstein", "bose einstein") -> ChemistryVisualMotif.BOSE_EINSTEIN_CONDENSATE
        has("phase transition", "physical changes", "physical change", "phase diagram") ->
            ChemistryVisualMotif.PHASE_TRANSITION
        has("density") && has("measurement", "matter around") -> ChemistryVisualMotif.DENSITY_MEASUREMENT
        has("significant figures", "precision", "accuracy", "errors", "error analysis", "uncertainty") ->
            ChemistryVisualMotif.UNCERTAINTY_ANALYSIS
        has("electron configuration", "electronic configuration", "aufbau", "hund rule", "pauli principle") ->
            ChemistryVisualMotif.ELECTRON_CONFIGURATION
        has("mendeleev", "modern periodic", "periodic classification", "groups", "periods") ->
            ChemistryVisualMotif.PERIODIC_CLASSIFICATION
        has("molecular formula", "empirical formula", "chemical formula", "formula mass") ->
            ChemistryVisualMotif.MOLECULAR_FORMULA
        has("limiting reagent", "percentage yield") -> ChemistryVisualMotif.LIMITING_REAGENT
        has("combustion", "flame") -> ChemistryVisualMotif.COMBUSTION_FLAME
        has("redox", "oxidation", "reduction", "displacement") -> ChemistryVisualMotif.REDOX_TRANSFER
        has("ideal gas", "gas laws", "kinetic theory") -> ChemistryVisualMotif.IDEAL_GAS_CHAMBER
        has("surface tension", "viscosity", "vapour pressure", "liquids") ->
            ChemistryVisualMotif.LIQUID_INTERFACE
        has("buffer") -> ChemistryVisualMotif.BUFFER_SYSTEM
        has("titration", "volumetric analysis") -> ChemistryVisualMotif.TITRATION_CURVE
        has("catalytic cycle", "homogeneous catalysis", "enzyme catalysis", "photocatalysis") ->
            ChemistryVisualMotif.CATALYTIC_CYCLE
        has("galvanic", "batter", "fuel cell") -> ChemistryVisualMotif.GALVANIC_BATTERY
        has("electrolysis", "electroplating") -> ChemistryVisualMotif.ELECTROLYSIS_CELL
        has("corrosion") -> ChemistryVisualMotif.CORROSION_FRONT
        has("micelle") -> ChemistryVisualMotif.MICELLE_ASSEMBLY
        has("colloid", "emulsion") -> ChemistryVisualMotif.COLLOID_DISPERSION
        has("crystal packing", "packing", "unit cell") -> ChemistryVisualMotif.CRYSTAL_PACKING
        has("semiconductor", "band structure", "band theory") -> ChemistryVisualMotif.SEMICONDUCTOR_BANDS
        title in setOf("dna", "rna") || has("nucleic acid", "crispr chemistry") ->
            ChemistryVisualMotif.NUCLEIC_ACID
        has("protein", "protein folding") -> ChemistryVisualMotif.PROTEIN_FOLDING
        has("enzyme", "enzymology") -> ChemistryVisualMotif.ENZYME_CATALYSIS
        has("metabolism", "metabolic", "atp", "respiration", "photosynthesis") ->
            ChemistryVisualMotif.METABOLIC_PATHWAY
        has("pharmacokinetic", "drug metabolism", "adme", "clearance", "absorption and distribution") ->
            ChemistryVisualMotif.DRUG_ADME
        has("nanomaterial", "nanochemistry") -> ChemistryVisualMotif.NANOMATERIAL
        has("solar cell", "photovoltaic") -> ChemistryVisualMotif.SOLAR_CELL
        has("battery material") -> ChemistryVisualMotif.BATTERY_MATERIAL
        has("chromat", "hplc", "tlc", "ion exchange", "electrophores") || title == "gc" ->
            ChemistryVisualMotif.CHROMATOGRAPHY
        has("mass spect", "gc-ms", "lc-ms") -> ChemistryVisualMotif.MASS_SPECTROMETRY
        has("magnetic resonance", "spin resonance") || title in setOf("nmr", "esr", "epr") ->
            ChemistryVisualMotif.MAGNETIC_RESONANCE
        has("diffraction", "x-ray crystallography") || title in setOf("xrd", "xps") ->
            ChemistryVisualMotif.DIFFRACTION_PATTERN
        has("dft", "gaussian", "quantum chemistry", "quantum computing") ->
            ChemistryVisualMotif.QUANTUM_CHEMISTRY
        has("molecular dynamics") -> ChemistryVisualMotif.MOLECULAR_DYNAMICS
        has("flow chemistry", "flow and automated synthesis") -> ChemistryVisualMotif.FLOW_CHEMISTRY
        has("microfluidic") -> ChemistryVisualMotif.MICROFLUIDICS
        has("green chemistry", "atom economy", "safer solvents") -> ChemistryVisualMotif.GREEN_CHEMISTRY
        has("radio", "nuclear", "alpha decay", "beta decay", "gamma ray", "half-life", "fission", "fusion") ->
            ChemistryVisualMotif.NUCLEAR
        has("astrochem") -> ChemistryVisualMotif.ASTROCHEMISTRY
        has("forensic") -> ChemistryVisualMotif.FORENSIC
        has("food chemistry") -> ChemistryVisualMotif.FOOD_CHEMISTRY
        has("agricultural", "fertilizer", "pesticide", "soil and nutrient") -> ChemistryVisualMotif.AGRICULTURAL
        has("photochem", "jablonski", "photosensit", "quantum yield", "excited state") ->
            ChemistryVisualMotif.PHOTOCHEMISTRY
        has("spectroscop", "infrared", "raman", "mass spect", "magnetic resonance", "spin resonance", "diffraction") ||
            title in setOf("uv", "ir", "nmr", "esr", "epr", "xps", "xrd") ->
            ChemistryVisualMotif.SPECTROSCOPY
        has("chromat", "hplc", "tlc", "ion exchange", "electrophores", "separation") || title == "gc" ->
            ChemistryVisualMotif.SEPARATION
        has("instrumentation", "gc-ms", "lc-ms", "icp", "aas", "xrf", "sem", "tem", "afm", "microscopy") ->
            ChemistryVisualMotif.INSTRUMENTATION
        has("laboratory", "glove box", "schlenk", "cryogenic", "high-pressure", "practical chemistry") ->
            ChemistryVisualMotif.LABORATORY
        has("metrology", "measurement", "significant figures", "precision", "accuracy", "errors", "si units") ->
            ChemistryVisualMotif.METROLOGY
        has("analytical", "gravimetric", "volumetric", "titration", "calibration", "quality assurance", "method validation") ->
            ChemistryVisualMotif.ANALYTICAL_MEASUREMENT
        has("comput", "dft", "gaussian", "molecular dynamics", "machine learning", "chemometric", "data analysis", "quantum computing") ->
            ChemistryVisualMotif.COMPUTATIONAL
        has("drug", "medicinal", "pharmac", "antibiotic", "antiviral", "sar", "docking", "qsar") ->
            ChemistryVisualMotif.MEDICINAL_BINDING
        has("protein", "dna", "rna", "enzyme", "metabolism", "atp", "respiration", "photosynthesis", "biochem", "chemical biology") ->
            ChemistryVisualMotif.BIOCHEMISTRY
        has("supramolecular", "host–guest", "host-guest", "self-assembly") -> ChemistryVisualMotif.SUPRAMOLECULAR
        has("coordination", "ligand", "crystal field", "organometallic", "cluster", "transition-metal") ->
            ChemistryVisualMotif.COORDINATION
        has("polymer", "plastic", "fibre", "biopolymer") -> ChemistryVisualMotif.POLYMER
        has("nanomaterial", "materials", "ceramic", "smart material", "biomaterial", "solar cell", "mof", "cof") ->
            ChemistryVisualMotif.MATERIALS
        has("surface", "adsorption", "colloid", "emulsion", "micelle", "heterogeneous catalysis") ->
            ChemistryVisualMotif.SURFACE_ASSEMBLY
        has("crystal", "solid state", "unit cell", "packing", "defect", "semiconductor", "lattice") ->
            ChemistryVisualMotif.CRYSTAL_SOLID
        has("electrochem", "galvanic", "electrolysis", "battery", "fuel cell", "corrosion", "nernst", "emf") ->
            ChemistryVisualMotif.ELECTROCHEMISTRY
        has("acid", "base", "buffer", "hydrolysis", "salt chemistry", "solvent chemistry") || title == "ph" ->
            ChemistryVisualMotif.ACID_BASE
        has("solution", "solubility", "molarity", "molality", "normality", "mole fraction", "ppm", "dilution") ->
            ChemistryVisualMotif.SOLVATION
        has("equilibrium", "le chatelier") -> ChemistryVisualMotif.EQUILIBRIUM
        has("kinetic", "rate law", "arrhenius", "activation energy", "molecularity", "catalysis") ->
            ChemistryVisualMotif.KINETICS
        has("thermochemistry", "calorimetry", "heat capacity", "specific heat", "bond enthalpy", "born-haber") ->
            ChemistryVisualMotif.CALORIMETRY
        has("thermodynamic", "enthalpy", "entropy", "gibbs", "hess", "spontaneity", "phase rule") ->
            ChemistryVisualMotif.THERMODYNAMICS
        has("gas law", "ideal gas", "kinetic theory", "vapour pressure", "viscosity", "surface tension") ->
            ChemistryVisualMotif.GAS_LAWS
        has("matter around", "states of matter", "solid", "liquid", "gas", "plasma", "bose-einstein", "physical change") ->
            ChemistryVisualMotif.MATTER_STATES
        has("mole concept", "stoichiometr", "avogadro", "empirical formula", "molecular formula", "limiting reagent", "yield") ->
            ChemistryVisualMotif.STOICHIOMETRY
        has("symmetry", "group theory", "stereochem") -> ChemistryVisualMotif.BOND_GEOMETRY
        has("atomic", "atom", "quantum", "orbital", "aufbau", "hund", "pauli", "isotope", "isobar", "isotone") ->
            ChemistryVisualMotif.ATOMIC_MODEL
        has("periodic", "mendeleev", "group", "metals", "nonmetals", "metalloids", "s-block", "p-block", "d- and f-block") ->
            ChemistryVisualMotif.PERIODIC_TRENDS
        has("bond", "vsepr", "hybrid", "lewis", "molecular geometry", "valency") || topic == "molecules" ->
            ChemistryVisualMotif.BOND_GEOMETRY
        has("organic", "reaction mechanism", "pericyclic", "rearrangement", "named reaction", "heterocyclic", "retrosynthetic", "hydrocarbon") ->
            ChemistryVisualMotif.ORGANIC_MECHANISM
        has("reaction", "combination", "decomposition", "displacement", "neutralization", "combustion", "oxidation", "reduction", "redox") ->
            ChemistryVisualMotif.REACTION_CLASS
        has("engineering", "unit operation", "mass transfer", "heat transfer", "reactor", "microfluidic", "flow chemistry") ->
            ChemistryVisualMotif.CHEMICAL_ENGINEERING
        has("industrial", "cement", "glass", "petrochemical", "soap", "paint", "dye", "explosive", "fuel") ->
            ChemistryVisualMotif.INDUSTRIAL_PROCESS
        has("environment", "pollution", "atmospher", "marine", "geochem", "green chemistry", "waste") ->
            ChemistryVisualMotif.ENVIRONMENTAL_CYCLE
        category.contains("inorganic") -> ChemistryVisualMotif.DESCRIPTIVE_INORGANIC
        category.contains("organic") -> ChemistryVisualMotif.ORGANIC_MECHANISM
        category.contains("biochem") -> ChemistryVisualMotif.BIOCHEMISTRY
        category.contains("analytical") -> ChemistryVisualMotif.ANALYTICAL_MEASUREMENT
        category.contains("physical") -> ChemistryVisualMotif.THERMODYNAMICS
        else -> ChemistryVisualMotif.REACTION_CLASS
    }
}

private data class ConceptVisualizationProfile(
    val scenes: List<Chemistry3DScene>,
    val entities: List<String>,
    val stages: List<String>,
    val axes: List<String>,
    val focus: String,
    val descriptions: List<String>
)

private fun lessonStages(lesson: CurriculumLesson, fallback: List<String>): List<String> =
    (lesson.exampleSteps.map { step ->
        step.substringBefore(".").substringBefore(",").take(54)
    } + fallback).filter { it.isNotBlank() }.distinct().take(5).let { stages ->
        if (stages.size >= 3) stages else stages + fallback.take(3 - stages.size)
    }

private fun conceptProfile(
    lesson: CurriculumLesson,
    scenes: List<Chemistry3DScene>,
    entities: List<String>,
    fallbackStages: List<String>,
    axes: List<String>,
    focus: String
): ConceptVisualizationProfile {
    val stages = lessonStages(lesson, fallbackStages)
    val descriptions = listOf(
        "A concept-specific 3D structure for ${lesson.title}, placing ${entities.take(3).joinToString(", ")} in their chemically meaningful spatial arrangement.",
        "A spatial interaction model for ${lesson.title} that exposes how ${entities.takeLast(2).joinToString(" and ")} change the observable behaviour.",
        "An energy-resolved view of ${lesson.title} using ${lesson.formula} to distinguish stable states, constraints and transitions.",
        "A staged mechanism for ${lesson.title}: ${stages.take(3).joinToString(" → ")}.",
        "A measurement model for ${lesson.title} relating ${axes.take(3).joinToString(", ")} with evidence, calibration and uncertainty."
    )
    return ConceptVisualizationProfile(
        scenes = scenes,
        entities = (listOf(lesson.title) + entities).distinct().take(6),
        stages = stages,
        axes = axes,
        focus = focus,
        descriptions = descriptions
    )
}

private fun visualizationProfile(
    module: CurriculumModule,
    lesson: CurriculumLesson
): ConceptVisualizationProfile {
    val text = "${module.title} ${module.category} ${lesson.title} ${lesson.concept}".lowercase()
    val formulaAxis = lesson.formula.take(48)
    return when {
        listOf("radioact", "nuclear", "alpha decay", "beta decay", "gamma ray", "half-life", "fission", "fusion").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.NUCLEAR_TRANSFORMATION, Chemistry3DScene.ATOMIC_ORBITAL,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE,
                    Chemistry3DScene.SPECTRAL_RESPONSE
                ),
                listOf("parent nucleus", "protons", "neutrons", "emitted radiation", "daughter nucleus"),
                listOf("identify parent nuclide", "apply conservation of A and Z", "emit particle or photon", "identify daughter", "measure activity"),
                listOf("nucleon number", "nuclear energy", "decay probability"),
                "Nuclear composition, decay pathway, mass-energy balance and radiation evidence"
            )
        module.title == "Environmental Chemistry" ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.ENVIRONMENTAL_CYCLE, Chemistry3DScene.PHASE_PARTICLES,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.PROCESS_FLOW,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("emission or source", "air/water/soil compartment", "reactive chemical species", "transport pathway", "sink or treatment"),
                listOf("identify source", "release or mobilize", "transport and transform", "measure exposure", "remove or manage"),
                listOf("space or time", "concentration or flux", formulaAxis),
                "Chemical source, environmental transport, transformation, exposure and removal"
            )
        module.title == "Industrial Chemistry" ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.PROCESS_FLOW, Chemistry3DScene.PHASE_PARTICLES,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("feed stream", "reactor or processing unit", "energy or separating agent", "product stream", "waste/recycle stream"),
                listOf("prepare feed", "contact or react", "transfer heat or mass", "separate product", "recycle and control waste"),
                listOf("process position", "conversion or property", formulaAxis),
                "Material flow, transformation, heat and mass transfer, product quality and process efficiency"
            )
        module.title == "Computational Chemistry" ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.DATA_SURFACE, Chemistry3DScene.MOLECULAR_GEOMETRY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.PROCESS_FLOW,
                    Chemistry3DScene.SPECTRAL_RESPONSE
                ),
                listOf("chemical representation", "model parameters", "training or simulation data", "predicted observable", "validation set or experiment"),
                listOf("define chemical question", "prepare representation and data", "fit or propagate model", "predict property", "validate uncertainty and transferability"),
                listOf("chemical feature", "predicted response", formulaAxis),
                "Chemical representation, model assumptions, energy or data landscape, prediction and validation"
            )
        module.title == "Biochemistry" ||
            listOf("protein", "dna", "rna", "enzyme", "metabolism", "atp", "photosynthesis", "respiration")
                .any { it == lesson.title.lowercase() } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.BIOPOLYMER, Chemistry3DScene.MOLECULAR_GEOMETRY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("biomolecular chain", "active or binding site", "substrate or ligand", "cofactor", "product or signal"),
                listOf("recognize molecular partner", "bind or fold", "stabilize transition state", "transfer matter or energy", "release product or response"),
                listOf("molecular coordinate", "free energy", formulaAxis),
                "Biomolecular structure, recognition, catalysis, energy coupling and measured function"
            )
        listOf(
            "orbital", "quantum", "electron configuration", "aufbau", "hund", "pauli", "atomic number",
            "isotope", "subatomic", "atomic model", "bohr", "periodic table", "periodic trend", "ionization energy",
            "electronegativity", "atomic radius", "electronic configuration"
        ).any { it in text } &&
            listOf("crystal field", "ligand field", "coordination compound", "organometallic", "cluster compound")
                .none { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.ATOMIC_ORBITAL, Chemistry3DScene.ELECTRON_DENSITY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE,
                    Chemistry3DScene.SPECTRAL_RESPONSE
                ),
                listOf("nucleus", "electron probability", "orbital nodes", "energy levels", "photon"),
                listOf("choose quantum state", "populate allowed orbitals", "apply electron rules", "change energy", "observe spectrum"),
                listOf("radial distance", "probability density", formulaAxis),
                "Quantized energy, orbital probability, electron occupancy and spectroscopic evidence"
            )
        module.title == "Molecules" || listOf(
            "bond", "vsepr", "hybrid", "molecular geometry", "lewis", "valency",
            "stereochem", "symmetry", "group theory", "crystal field", "ligand field",
            "coordination compound", "organometallic", "cluster compound"
        ).any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.MOLECULAR_GEOMETRY, Chemistry3DScene.ELECTRON_DENSITY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE,
                    Chemistry3DScene.SPECTRAL_RESPONSE
                ),
                listOf("central atom", "bonding domains", "lone pairs", "bond dipoles", "molecular symmetry"),
                listOf("count valence electrons", "construct bonding pattern", "arrange electron domains", "optimize geometry", "predict polarity or spectrum"),
                listOf("bond angle", "electron density", formulaAxis),
                "Electron-domain arrangement, three-dimensional geometry, polarity and symmetry"
            )
        module.title == "Surface Chemistry" ||
            listOf("adsorption", "colloid", "emulsion", "micelle", "surface science", "surfactant").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.SURFACE_INTERFACE, Chemistry3DScene.PHASE_PARTICLES,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.PROCESS_FLOW,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("bulk phase", "surface or interface", "adsorbate/surfactant", "active site or droplet", "dispersion medium"),
                listOf("create interface", "transport species to surface", "adsorb or self-assemble", "reach surface equilibrium", "measure coverage or stability"),
                listOf("surface coordinate", "coverage or interfacial energy", formulaAxis),
                "Surface sites, adsorption, interfacial forces, self-assembly and measurable coverage"
            )
        listOf("crystal", "lattice", "unit cell", "packing", "solid state", "defect", "semiconductor", "xrd").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.CRYSTAL_LATTICE, Chemistry3DScene.MOLECULAR_GEOMETRY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.PROCESS_FLOW,
                    Chemistry3DScene.SPECTRAL_RESPONSE
                ),
                listOf("unit cell", "lattice point", "coordination shell", "defect site", "diffraction plane"),
                listOf("select lattice", "place basis particles", "repeat unit cell", "introduce defect or dopant", "calculate diffraction response"),
                listOf("lattice coordinate", "occupancy", formulaAxis),
                "Periodic structure, coordination, defects, packing and diffraction evidence"
            )
        listOf("polymer", "plastic", "biopolymer", "conducting polymer").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.POLYMER_CHAIN, Chemistry3DScene.MOLECULAR_GEOMETRY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.PROCESS_FLOW,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("monomer", "repeat unit", "polymer chain", "cross-link or dopant", "bulk morphology"),
                listOf("activate monomer", "propagate or condense", "grow chain", "assemble morphology", "measure material property"),
                listOf("chain length", "interaction strength", formulaAxis),
                "Repeat-unit chemistry, chain conformation, morphology and material response"
            )
        module.category == "Biochemistry" || listOf("protein", "dna", "rna", "enzyme", "metabolism", "atp", "photosynthesis", "respiration", "biomolecule", "chemical biology").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.BIOPOLYMER, Chemistry3DScene.MOLECULAR_GEOMETRY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("biomolecular chain", "active or binding site", "substrate or ligand", "cofactor", "product or signal"),
                listOf("recognize molecular partner", "bind or fold", "stabilize transition state", "transfer matter or energy", "release product or response"),
                listOf("molecular coordinate", "free energy", formulaAxis),
                "Biomolecular structure, recognition, catalysis, energy coupling and measured function"
            )
        module.title != "Analytical Chemistry" &&
            listOf("electro", "galvanic", "battery", "fuel cell", "corrosion", "redox", "emf", "nernst").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.ELECTROCHEMICAL_CELL, Chemistry3DScene.MOLECULAR_GEOMETRY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("anode", "cathode", "electron path", "mobile ions", "electrolyte or salt bridge"),
                listOf("write half-reactions", "balance electrons", "separate electrodes", "move charge", "measure potential or deposited amount"),
                listOf("electrode coordinate", "potential", formulaAxis),
                "Oxidation-reduction sites, electron flow, ionic charge balance and cell potential"
            )
        listOf("chromat", "hplc", "gc", "tlc", "ion exchange", "electrophoresis", "separation").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.SEPARATION_COLUMN, Chemistry3DScene.PHASE_PARTICLES,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.PROCESS_FLOW,
                    Chemistry3DScene.SPECTRAL_RESPONSE
                ),
                listOf("sample mixture", "stationary phase", "mobile phase", "separated bands", "detector"),
                listOf("inject sample", "partition between phases", "migrate at different rates", "resolve components", "detect and quantify"),
                listOf("migration coordinate", "detector response", formulaAxis),
                "Differential partitioning, transport, resolution and detector response"
            )
        module.title == "Analytical Chemistry" ||
            listOf("gravimetric", "volumetric", "complexometric", "redox titration", "error analysis", "calibration", "laboratory measurement").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.PROCESS_FLOW, Chemistry3DScene.MOLECULAR_GEOMETRY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("sample/analyte", "reagent or standard", "selective chemical response", "endpoint or detector", "calibrated result"),
                listOf("collect representative sample", "prepare standards and blank", "generate selective response", "locate endpoint or signal", "calculate result and uncertainty"),
                listOf("amount or volume", "analytical signal", formulaAxis),
                "Sampling, stoichiometric or instrumental response, calibration, endpoint and uncertainty"
            )
        listOf("spectr", " nmr", " esr", " raman", " uv", " ir", "mass spect", "xps", "xrf", "aas", "icp", "microscopy", "sem", "tem", "afm").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.SPECTRAL_RESPONSE, Chemistry3DScene.MOLECULAR_GEOMETRY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.PROCESS_FLOW,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("chemical sample", "excitation source", "quantized state or interaction volume", "detector", "calibrated signal"),
                listOf("prepare sample", "apply radiation or probe", "generate chemical response", "detect signal", "calibrate and assign"),
                listOf("energy or position", "signal intensity", formulaAxis),
                "Excitation, chemical response, detector signal, calibration and structural assignment"
            )
        module.title !in setOf(
            "Thermodynamics", "Thermochemistry", "Advanced Thermodynamics", "Statistical Thermodynamics",
            "Chemical Kinetics", "Advanced Chemical Kinetics"
        ) &&
            listOf("equilibrium", "buffer", "acid", "base", "ph", "hydrolysis", "solubility", "solution", "molarity", "molality", "normality", "mole fraction", "dilution").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.EQUILIBRIUM_CHAMBER, Chemistry3DScene.PHASE_PARTICLES,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("reactant or solute", "product or dissolved species", "solvent", "conjugate or counter-ion", "equilibrium boundary"),
                listOf("define species", "establish reversible process", "apply mass and charge balance", "perturb conditions", "measure equilibrium composition"),
                listOf("composition", "chemical potential or pH", formulaAxis),
                "Species distribution, reversible molecular exchange, mass balance and equilibrium response"
            )
        listOf("gas", "liquid", "vapour", "viscosity", "surface tension", "state of matter", "phase", "condensate", "plasma").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.PHASE_PARTICLES, Chemistry3DScene.MOLECULAR_GEOMETRY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.EQUILIBRIUM_CHAMBER,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("representative particles", "intermolecular attraction", "container or interface", "thermal motion", "phase boundary"),
                listOf("set temperature and pressure", "assign particle energy", "balance attraction and motion", "change phase or flow", "measure bulk property"),
                listOf("particle spacing", "thermal energy", formulaAxis),
                "Particle spacing, intermolecular force, thermal motion, phase stability and bulk properties"
            )
        listOf("thermo", "enthalpy", "entropy", "gibbs", "heat", "calor", "hess", "lattice energy", "bond enthalpy", "spontan").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.PHASE_PARTICLES,
                    Chemistry3DScene.REACTION_COORDINATE, Chemistry3DScene.PROCESS_FLOW,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("system", "surroundings", "initial state", "final state", "energy transfer"),
                listOf("define boundary", "identify heat and work", "connect state functions", "follow process path", "test spontaneity or energy balance"),
                listOf("process coordinate", "energy or entropy", formulaAxis),
                "System boundary, heat and work transfer, state functions, entropy and free-energy change"
            )
        listOf("rate", "kinetic", "arrhenius", "activation", "catal", "molecularity", "order").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.REACTION_COORDINATE, Chemistry3DScene.PHASE_PARTICLES,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.PROCESS_FLOW,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("reactant particles", "effective collision", "transition-state region", "catalysed pathway", "product particles"),
                listOf("prepare reactants", "generate collisions", "cross activation barrier", "form products", "measure concentration against time"),
                listOf("reaction coordinate or time", "energy or rate", formulaAxis),
                "Collision dynamics, activation barrier, mechanism, catalyst pathway and measured rate"
            )
        listOf("organic", "reaction mechanism", "pericyclic", "rearrangement", "named reaction", "heterocycl", "asymmetric", "synthesis", "retrosynth", "click chemistry").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.MOLECULAR_GEOMETRY, Chemistry3DScene.ELECTRON_DENSITY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE,
                    Chemistry3DScene.SPECTRAL_RESPONSE
                ),
                listOf("substrate", "nucleophile or reagent", "electrophilic site", "intermediate or transition structure", "product stereochemistry"),
                listOf("identify functional groups", "mark electron-rich and electron-poor sites", "move electron pairs", "form or break bonds", "verify product and selectivity"),
                listOf("bond-forming coordinate", "relative energy", formulaAxis),
                "Three-dimensional substrate structure, electron movement, intermediates and stereochemical outcome"
            )
        listOf("medicinal", "drug discovery", "pharmaco", "antibiotic", "antiviral", "docking", "sar", "pharmaceutical").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.MOLECULAR_GEOMETRY, Chemistry3DScene.BIOPOLYMER,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("drug or ligand", "biological target", "binding-site interactions", "metabolic or transport pathway", "measured efficacy/safety response"),
                listOf("define target profile", "model ligand-target recognition", "optimize structure and properties", "measure exposure and response", "test selectivity and safety"),
                listOf("molecular descriptor", "binding/free-energy or exposure response", formulaAxis),
                "Ligand conformation, target recognition, free energy, exposure and therapeutic response"
            )
        listOf("environment", "pollution", "atmospher", "marine", "geochem", "waste", "green chemistry").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.ENVIRONMENTAL_CYCLE, Chemistry3DScene.PHASE_PARTICLES,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.PROCESS_FLOW,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("emission or source", "air/water/soil compartment", "reactive chemical species", "transport pathway", "sink or treatment"),
                listOf("identify source", "release or mobilize", "transport and transform", "measure exposure", "remove or manage"),
                listOf("space or time", "concentration or flux", formulaAxis),
                "Chemical source, environmental transport, transformation, exposure and removal"
            )
        listOf("industrial", "cement", "fertilizer", "glass", "ceramic", "petrochemical", "soap", "paint", "dye", "explosive", "engineering", "unit operation", "mass transfer", "heat transfer", "reactor").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.PROCESS_FLOW, Chemistry3DScene.PHASE_PARTICLES,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE,
                    Chemistry3DScene.DATA_SURFACE
                ),
                listOf("feed stream", "reactor or processing unit", "energy or separating agent", "product stream", "waste/recycle stream"),
                listOf("prepare feed", "contact or react", "transfer heat or mass", "separate product", "recycle and control waste"),
                listOf("process position", "conversion or property", formulaAxis),
                "Material flow, transformation, heat and mass transfer, product quality and process efficiency"
            )
        listOf("comput", "modeling", "gaussian", "dft", "molecular dynamics", "docking", "data analysis", "chemometric", "machine learning", "ai in chemistry", "quantum computing", "research methodology").any { it in text } ->
            conceptProfile(
                lesson,
                listOf(
                    Chemistry3DScene.DATA_SURFACE, Chemistry3DScene.MOLECULAR_GEOMETRY,
                    Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.PROCESS_FLOW,
                    Chemistry3DScene.SPECTRAL_RESPONSE
                ),
                listOf("chemical representation", "model parameters", "training or simulation data", "predicted observable", "validation set or experiment"),
                listOf("define chemical question", "prepare representation and data", "fit or propagate model", "predict property", "validate uncertainty and transferability"),
                listOf("chemical feature", "predicted response", formulaAxis),
                "Chemical representation, model assumptions, energy or data landscape, prediction and validation"
            )
        else -> motifSpecificProfile(module, lesson, visualMotif(module, lesson))
    }
}

private fun motifSpecificProfile(
    module: CurriculumModule,
    lesson: CurriculumLesson,
    motif: ChemistryVisualMotif
): ConceptVisualizationProfile {
    val scenes = when (motif) {
        ChemistryVisualMotif.METROLOGY, ChemistryVisualMotif.ANALYTICAL_MEASUREMENT,
        ChemistryVisualMotif.INSTRUMENTATION, ChemistryVisualMotif.FORENSIC,
        ChemistryVisualMotif.UNCERTAINTY_ANALYSIS, ChemistryVisualMotif.DENSITY_MEASUREMENT,
        ChemistryVisualMotif.TITRATION_CURVE ->
            listOf(Chemistry3DScene.PROCESS_FLOW, Chemistry3DScene.MOLECULAR_GEOMETRY, Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.SPECTRAL_RESPONSE, Chemistry3DScene.DATA_SURFACE)
        ChemistryVisualMotif.STOICHIOMETRY, ChemistryVisualMotif.REACTION_CLASS,
        ChemistryVisualMotif.ORGANIC_MECHANISM, ChemistryVisualMotif.MOLECULAR_FORMULA,
        ChemistryVisualMotif.LIMITING_REAGENT, ChemistryVisualMotif.REDOX_TRANSFER,
        ChemistryVisualMotif.COMBUSTION_FLAME, ChemistryVisualMotif.CATALYTIC_CYCLE ->
            listOf(Chemistry3DScene.REACTION_COORDINATE, Chemistry3DScene.MOLECULAR_GEOMETRY, Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.PROCESS_FLOW, Chemistry3DScene.DATA_SURFACE)
        ChemistryVisualMotif.INDUSTRIAL_PROCESS, ChemistryVisualMotif.CHEMICAL_ENGINEERING,
        ChemistryVisualMotif.LABORATORY, ChemistryVisualMotif.AGRICULTURAL,
        ChemistryVisualMotif.FOOD_CHEMISTRY, ChemistryVisualMotif.FLOW_CHEMISTRY,
        ChemistryVisualMotif.MICROFLUIDICS ->
            listOf(Chemistry3DScene.PROCESS_FLOW, Chemistry3DScene.PHASE_PARTICLES, Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE, Chemistry3DScene.DATA_SURFACE)
        ChemistryVisualMotif.ENVIRONMENTAL_CYCLE, ChemistryVisualMotif.ASTROCHEMISTRY,
        ChemistryVisualMotif.GREEN_CHEMISTRY ->
            listOf(Chemistry3DScene.ENVIRONMENTAL_CYCLE, Chemistry3DScene.PHASE_PARTICLES, Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE, Chemistry3DScene.SPECTRAL_RESPONSE)
        ChemistryVisualMotif.MATERIALS, ChemistryVisualMotif.DESCRIPTIVE_INORGANIC,
        ChemistryVisualMotif.NANOMATERIAL, ChemistryVisualMotif.SEMICONDUCTOR_BANDS,
        ChemistryVisualMotif.SOLAR_CELL, ChemistryVisualMotif.BATTERY_MATERIAL,
        ChemistryVisualMotif.CRYSTAL_PACKING ->
            listOf(Chemistry3DScene.CRYSTAL_LATTICE, Chemistry3DScene.MOLECULAR_GEOMETRY, Chemistry3DScene.ELECTRON_DENSITY, Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.SPECTRAL_RESPONSE)
        ChemistryVisualMotif.SUPRAMOLECULAR, ChemistryVisualMotif.MICELLE_ASSEMBLY,
        ChemistryVisualMotif.COLLOID_DISPERSION ->
            listOf(Chemistry3DScene.MOLECULAR_GEOMETRY, Chemistry3DScene.SURFACE_INTERFACE, Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE, Chemistry3DScene.SPECTRAL_RESPONSE)
        ChemistryVisualMotif.PHOTOCHEMISTRY, ChemistryVisualMotif.MAGNETIC_RESONANCE,
        ChemistryVisualMotif.MASS_SPECTROMETRY, ChemistryVisualMotif.DIFFRACTION_PATTERN,
        ChemistryVisualMotif.QUANTUM_CHEMISTRY ->
            listOf(Chemistry3DScene.ELECTRON_DENSITY, Chemistry3DScene.ATOMIC_ORBITAL, Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE, Chemistry3DScene.SPECTRAL_RESPONSE)
        else ->
            listOf(Chemistry3DScene.MOLECULAR_GEOMETRY, Chemistry3DScene.PHASE_PARTICLES, Chemistry3DScene.ENERGY_SURFACE, Chemistry3DScene.REACTION_COORDINATE, Chemistry3DScene.DATA_SURFACE)
    }
    val lessonEntities = (
        listOf(lesson.title, module.title) +
            lesson.advancedConcepts.take(2) +
            lesson.exampleSteps.take(2).map { it.substringBefore(".").take(46) }
        ).filter { it.isNotBlank() }.distinct()
    val entities = (lessonEntities + listOf("species described by ${lesson.formula}", "${lesson.title} observable")).take(6)
    return conceptProfile(
        lesson = lesson,
        scenes = scenes,
        entities = entities,
        fallbackStages = lesson.exampleSteps.take(5),
        axes = listOf("${lesson.title} coordinate", "${lesson.title} response", lesson.formula),
        focus = "${lesson.title} within ${module.title}: ${lesson.concept} The procedural geometry is parameterized by the lesson entities, relationship and experimental stages."
    )
}

/**
 * The existing 638 curriculum lessons are the subtopic layer. Three
 * micro-concepts per lesson produce 1,914 entries; the first 286 receive a
 * fourth extension, giving exactly 2,200. Every subtopic reuses three
 * interactive simulation engines and five procedural 3D renderers.
 */
internal val scalableCurriculum by lazy {
    val flattened = curriculum.flatMap { module -> module.lessons.map { lesson -> module to lesson } }
    flattened.mapIndexed { index, (module, lesson) ->
        val id = listOf(module.level, module.title, lesson.title).joinToString("-") { stableSlug(it) }
        val motif = visualMotif(module, lesson)
        SubtopicExpansion(
            id = id,
            level = module.level,
            majorTopic = module.title,
            subtopic = lesson.title,
            visualMotif = motif,
            virtualLab = authenticLabFor(id, module, lesson, motif),
            microConcepts = buildMicroConcepts(id, module, lesson, includeExtension = index < 286),
            simulations = buildSimulations(id, module, lesson),
            visualizations3d = buildVisualizations(id, module, lesson, motif)
        )
    }
}

internal fun learningExpansionFor(
    module: CurriculumModule,
    lesson: CurriculumLesson
): SubtopicExpansion {
    val id = listOf(module.level, module.title, lesson.title).joinToString("-") { stableSlug(it) }
    return scalableCurriculum.first { it.id == id }
}
