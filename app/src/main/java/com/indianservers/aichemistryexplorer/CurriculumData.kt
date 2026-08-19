package com.indianservers.aichemistryexplorer

import androidx.compose.ui.graphics.Color

internal data class CurriculumLesson(
    val title: String,
    val concept: String,
    val formula: String,
    val question: String,
    val answers: List<String>,
    val correct: Int,
    val theory: String = concept,
    val workedExample: String = "Connect the definition to a chemical observation, identify the particles involved, and use the stated relationship to explain the result.",
    val exampleSteps: List<String> = emptyList(),
    val commonMistake: String = "Do not treat a chemical model as a fact without checking its assumptions, units and experimental conditions.",
    val realWorldApplication: String = "Chemists use this concept to connect molecular behavior with measurable properties and practical decisions.",
    val advancedConcepts: List<String> = emptyList()
)

internal data class CurriculumModule(
    val title: String,
    val icon: String,
    val color: Color,
    val description: String,
    val lessons: List<CurriculumLesson>,
    val level: String = "Class 11–12",
    val category: String = "General Chemistry",
    val subcategory: String = "Core concepts"
)

private fun lesson(title: String, concept: String, formula: String, question: String, correct: Int, vararg answers: String) =
    CurriculumLesson(
        title = title,
        concept = concept,
        formula = formula,
        question = question,
        answers = answers.toList(),
        correct = correct,
        theory = expandedTheory(title, concept),
        workedExample = workedExampleFor(title),
        exampleSteps = exampleStepsFor(title),
        commonMistake = commonMistakeFor(title),
        realWorldApplication = applicationFor(title),
        advancedConcepts = advancedConceptsFor(title)
    )

internal val curriculum by lazy { listOf(
    CurriculumModule("Atomic Structure: Senior Secondary", "⚛", Cyan, "Build matter from particles, nuclei and quantum orbitals.", listOf(
        lesson("Subatomic particles", "Protons and neutrons occupy the nucleus; electrons occupy the surrounding probability cloud.", "proton +1 · neutron 0 · electron −1", "Which particle determines an element's atomic number?", 1, "Electron", "Proton", "Neutron", "Photon"),
        lesson("Atomic number and mass number", "Atomic number counts protons. Mass number counts protons plus neutrons.", "A = Z + N", "An atom has 11 protons and 12 neutrons. Its mass number is…", 2, "11", "12", "23", "24"),
        lesson("Isotopes and ions", "Isotopes share a proton count but differ in neutrons; ions form by gaining or losing electrons.", "charge = protons − electrons", "Two isotopes of carbon differ in their number of…", 1, "Protons", "Neutrons", "Valence shells", "Chemical symbols"),
        lesson("Bohr’s atomic model", "Bohr proposed quantized energy levels. Electrons absorb or emit photons while moving between them.", "ΔE = hν", "Light is emitted when an electron moves to…", 0, "A lower energy level", "A higher energy level", "The nucleus", "Another atom"),
        lesson("Quantum numbers and orbitals", "Four quantum numbers describe an electron's energy level, orbital shape, orientation and spin.", "n, ℓ, mℓ, ms", "The maximum number of electrons in one orbital is…", 1, "1", "2", "6", "10")
    )),
    CurriculumModule("Periodic Table and Trends", "▦", Color(0xFF4D8BFF), "Read the table as a map of structure and chemical behavior.", listOf(
        lesson("Periods, groups and blocks", "Rows are periods, columns are groups, and s-, p-, d- and f-blocks reflect the valence subshell being filled.", "group → similar valence behaviour", "Elements in one group usually share similar…", 2, "Mass numbers", "Neutron counts", "Chemical properties", "Nuclear sizes"),
        lesson("Electronic configuration", "Electrons fill lower-energy orbitals first while obeying Pauli exclusion and Hund's rule.", "1s → 2s → 2p → 3s", "The ground-state configuration of lithium ends in…", 0, "2s¹", "2p¹", "3s¹", "1p¹"),
        lesson("Atomic and ionic radius", "Atomic radius generally decreases across a period and increases down a group.", "cation < atom < anion", "Which is typically smaller than its neutral atom?", 1, "Anion", "Cation", "Isotope", "Noble gas"),
        lesson("Ionization energy", "Ionization energy is the energy needed to remove a gaseous electron and generally rises across a period.", "X(g) → X⁺(g) + e⁻", "Ionization energy is highest near which region?", 3, "Lower left", "Center", "Lower right", "Upper right"),
        lesson("Electronegativity and metallic character", "Electronegativity rises toward fluorine; metallic character rises toward francium.", "F = 3.98 on the Pauling scale", "The most electronegative element is…", 2, "Oxygen", "Chlorine", "Fluorine", "Nitrogen")
    )),
    CurriculumModule("Chemical Bonding: Senior Secondary", "⚭", Purple, "Connect electron behavior with bonds, shapes and properties.", listOf(
        lesson("Ionic bonding", "Electron transfer creates oppositely charged ions held in a three-dimensional electrostatic lattice.", "Na → Na⁺ + e⁻", "Ionic compounds usually conduct electricity when…", 2, "Solid only", "Cold", "Molten or dissolved", "Under vacuum"),
        lesson("Covalent bonding", "Covalent bonds form when atoms share electron pairs between their nuclei.", "bond order: single < double < triple", "A double covalent bond shares how many electron pairs?", 1, "One", "Two", "Three", "Four"),
        lesson("Metallic bonding", "Metal cations are attracted to mobile, delocalized valence electrons.", "metal ions + electron sea", "Metal conductivity is explained by…", 3, "Fixed protons", "Neutron flow", "Ionic molecules", "Mobile electrons"),
        lesson("Lewis structures and formal charge", "Lewis structures track valence electrons; preferred structures minimize formal charge.", "FC = valence − dots − bonds", "How many valence electrons does oxygen have?", 2, "2", "4", "6", "8"),
        lesson("VSEPR theory and molecular shapes", "Electron domains repel and arrange to maximize separation around a central atom.", "2 linear · 3 planar · 4 tetrahedral", "The molecular shape of methane is…", 3, "Bent", "Linear", "Trigonal planar", "Tetrahedral")
    )),
    CurriculumModule("States and Phases: Senior Secondary", "◫", Color(0xFF73D0FF), "Relate particles and forces to gases, liquids, solids and phases.", listOf(
        lesson("Properties of solids, liquids and gases", "Particle spacing, motion and attraction explain shape, volume, density and compressibility.", "solid → liquid → gas", "Which state is most compressible?", 2, "Solid", "Liquid", "Gas", "Crystal"),
        lesson("Gas laws", "Pressure, volume, temperature and amount are connected by the ideal gas equation.", "PV = nRT", "At constant temperature, doubling pressure does what to volume?", 1, "Doubles it", "Halves it", "No change", "Quadruples it"),
        lesson("Intermolecular forces", "Dispersion, dipole forces and hydrogen bonding act between molecules and influence boiling points.", "dispersion < dipole < H-bond", "Hydrogen bonding occurs strongly in…", 0, "Water", "Methane", "Carbon dioxide", "Argon"),
        lesson("Phase changes and diagrams", "Phase diagrams show stable states and boundaries as pressure and temperature vary.", "q = mL during phase change", "The direct change from solid to gas is…", 3, "Melting", "Freezing", "Condensation", "Sublimation"),
        lesson("Crystal structures", "Crystalline solids repeat unit cells; packing geometry controls density and material properties.", "SC < BCC < FCC packing", "Which structure has the highest packing efficiency?", 2, "Simple cubic", "Body-centered cubic", "Face-centered cubic", "Linear")
    )),
    CurriculumModule("Organic Chemistry", "⌬", Purple, "Explore carbon skeletons, functional groups and molecular reactions.", listOf(
        lesson("Hydrocarbons and nomenclature", "Hydrocarbons contain only carbon and hydrogen and are named from chain length and bond type.", "alkane CₙH₂ₙ₊₂", "A three-carbon alkane is called…", 1, "Ethane", "Propane", "Butane", "Propene"),
        lesson("Functional groups", "Functional groups are reactive atom patterns that define organic families and characteristic properties.", "alcohol = –OH · acid = –COOH", "Which group defines a carboxylic acid?", 3, "–OH", "–NH₂", "–CHO", "–COOH"),
        lesson("Structural and stereoisomerism", "Isomers share a molecular formula but differ in connectivity or three-dimensional arrangement.", "same formula ≠ same structure", "Cis/trans isomerism is a form of…", 2, "Nuclear change", "Ionization", "Stereoisomerism", "Polymerization"),
        lesson("Organic reaction mechanisms", "Mechanisms use curved arrows to track electron-pair movement through elementary steps.", "nucleophile → electron-poor center", "A nucleophile donates…", 0, "An electron pair", "A proton only", "A neutron", "Heat"),
        lesson("Polymers and biomolecules", "Monomers join into polymers; biological macromolecules include proteins, nucleic acids and carbohydrates.", "n monomers → polymer", "Proteins are polymers of…", 1, "Nucleotides", "Amino acids", "Glucose only", "Fatty acids")
    )),
    CurriculumModule("Reaction Equations and Redox", "⇌", Orange, "Represent, classify and balance transformations of matter.", listOf(
        lesson("Writing and balancing equations", "Coefficients conserve each type of atom without changing the identity of any substance.", "2H₂ + O₂ → 2H₂O", "The balanced coefficient of O₂ in water formation is…", 0, "1", "2", "3", "4"),
        lesson("Combination and decomposition reactions", "Combination joins reactants into one product; decomposition splits one reactant into simpler products.", "A + B → AB · AB → A + B", "CaCO₃ → CaO + CO₂ is…", 2, "Combination", "Displacement", "Decomposition", "Neutralization"),
        lesson("Displacement and precipitation reactions", "Ions exchange partners; an insoluble product leaves solution as a precipitate.", "Ag⁺ + Cl⁻ → AgCl↓", "The downward arrow indicates…", 3, "A gas", "Heat", "A catalyst", "A precipitate"),
        lesson("Combustion reactions", "Complete hydrocarbon combustion in excess oxygen produces carbon dioxide, water and energy.", "CₓHᵧ + O₂ → CO₂ + H₂O", "Complete combustion requires sufficient…", 1, "Nitrogen", "Oxygen", "Hydrogen", "Chlorine"),
        lesson("Redox reactions", "Oxidation loses electrons while reduction gains them; both processes occur together.", "OIL RIG", "A species that gains electrons is…", 2, "Oxidized", "A catalyst", "Reduced", "Neutralized")
    )),
    CurriculumModule("Equilibrium and Solutions", "≋", Green, "Understand dissolved matter, reversible change, acids and buffers.", listOf(
        lesson("Dynamic chemical equilibrium", "Forward and reverse rates become equal while both reactions continue microscopically.", "K = products/reactants", "At equilibrium, forward and reverse rates are…", 1, "Zero", "Equal", "Increasing", "Random"),
        lesson("Le Chatelier’s principle", "An equilibrium shifts to oppose a change in concentration, pressure or temperature.", "stress → opposing shift", "Removing product generally shifts equilibrium…", 2, "Left", "Nowhere", "Right", "To zero"),
        lesson("Solubility and concentration", "Concentration expresses solute relative to solution; solubility is the maximum amount that dissolves.", "M = moles / liters", "Two moles in one liter gives…", 3, "0.5 M", "1 M", "1.5 M", "2 M"),
        lesson("Acids, bases and pH", "Acids donate protons, bases accept them, and pH expresses hydronium concentration logarithmically.", "pH = −log[H₃O⁺]", "A solution of pH 3 is…", 0, "Acidic", "Neutral", "Basic", "Saturated"),
        lesson("Buffers and titration curves", "Buffers resist pH change; titration curves reveal equivalence points and acid-base strength.", "pH = pKₐ + log(base/acid)", "At half-equivalence for a weak acid, pH equals…", 2, "0", "7 always", "pKₐ", "14")
    )),
    CurriculumModule("Kinetics and Electrochemistry", "ϟ", Color(0xFF1DBBFF), "Control reaction speed and channel electron transfer into useful work.", listOf(
        lesson("Reaction rate and rate laws", "Rate laws relate reaction speed to reactant concentrations using experimentally determined orders.", "rate = k[A]ᵐ[B]ⁿ", "Increasing a first-order reactant concentration twofold changes rate by…", 1, "Half", "Twofold", "Fourfold", "No change"),
        lesson("Collision theory and activation energy", "Particles react only when they collide with sufficient energy and suitable orientation.", "k = A e^{−Eₐ/(RT)}", "Higher temperature usually makes reactions…", 3, "Stop", "Reverse only", "Unchanged", "Faster"),
        lesson("Catalysts and reaction mechanisms", "Catalysts offer a lower-energy pathway and are regenerated by the mechanism.", "catalyst lowers Eₐ", "A catalyst changes the equilibrium constant?", 0, "No", "Yes, upward", "Yes, downward", "Only in gases"),
        lesson("Galvanic and electrolytic cells", "Galvanic cells generate electrical work; electrolytic cells consume electricity to force change.", "E°cell = E°cathode − E°anode", "Oxidation occurs at the…", 2, "Salt bridge", "Cathode", "Anode", "Voltmeter"),
        lesson("Batteries, corrosion and electrolysis", "Applied electrochemistry stores energy, explains corrosion and plates or separates materials.", "Q = It", "Rusting of iron is primarily…", 1, "Reduction only", "Oxidation", "Neutralization", "Sublimation")
    )),
    CurriculumModule("Analytical and Applied Chemistry", "⌁", Cyan, "Measure matter and apply chemistry to health, industry and the planet.", listOf(
        lesson("Qualitative chemical analysis", "Selective reactions identify unknown ions through colors, gases, precipitates and flame tests.", "observation → chemical identity", "A flame test is mainly used to identify…", 2, "Anions only", "Solvents", "Metal ions", "Neutrons"),
        lesson("Chromatography and separation methods", "Components separate because they interact differently with mobile and stationary phases.", "Rƒ = solute distance / solvent distance", "An Rƒ value must normally lie between…", 0, "0 and 1", "1 and 10", "−1 and 0", "10 and 100"),
        lesson("Spectroscopy and instrumental analysis", "Spectra connect interactions with electromagnetic radiation to molecular structure and concentration.", "A = εbc", "Beer–Lambert absorbance increases with…", 3, "Lower path length", "Lower concentration", "Time only", "Concentration"),
        lesson("Environmental and green chemistry", "Green chemistry prevents waste, reduces hazards and designs efficient, renewable processes.", "atom economy = useful atoms / all atoms", "The best green strategy is to…", 1, "Treat waste later", "Prevent waste", "Dilute pollutants", "Use more solvent"),
        lesson("Medicinal, industrial and everyday chemistry", "Chemical design connects molecular structure with medicines, materials, food, energy and manufacturing.", "structure → property → function", "A catalyst in industry primarily helps by…", 2, "Changing products", "Breaking conservation", "Lowering activation energy", "Raising equilibrium K")
    ))
).map { it.withCurriculumClassification() } + expandedCurriculum }

private fun CurriculumModule.withCurriculumClassification(): CurriculumModule = when (title) {
    "Atomic Structure: Senior Secondary" -> copy(category = "Physical Chemistry", subcategory = "Atomic foundations")
    "Periodic Table and Trends" -> copy(category = "Inorganic Chemistry", subcategory = "Periodic properties")
    "Chemical Bonding: Senior Secondary" -> copy(category = "Physical Chemistry", subcategory = "Molecular structure")
    "States and Phases: Senior Secondary" -> copy(category = "Physical Chemistry", subcategory = "States and phases")
    "Organic Chemistry" -> copy(category = "Organic Chemistry", subcategory = "Core organic chemistry")
    "Reaction Equations and Redox" -> copy(category = "General Chemistry", subcategory = "Reaction chemistry")
    "Equilibrium and Solutions" -> copy(category = "Physical Chemistry", subcategory = "Equilibria and solutions")
    "Kinetics and Electrochemistry" -> copy(category = "Physical Chemistry", subcategory = "Kinetics and electrochemistry")
    "Analytical and Applied Chemistry" -> copy(category = "Analytical Chemistry", subcategory = "Analysis and applications")
    else -> this
}

private data class CurriculumSeed(
    val title: String,
    val level: String,
    val category: String,
    val subcategory: String,
    val concepts: List<String>
)

private fun seed(
    title: String,
    level: String,
    category: String,
    subcategory: String,
    vararg concepts: String
) = CurriculumSeed(title, level, category, subcategory, concepts.toList())

private fun generatedLesson(seed: CurriculumSeed, concept: String): CurriculumLesson {
    val topic = seed.title
    val authoredContent = when (seed.category) {
        "Introduction to Chemistry" -> levelOneConceptContent[concept]
        "High School Chemistry" -> levelTwoConceptContent[concept]
        "Physical Chemistry" -> levelThreeConceptContent["${seed.title}|$concept"]
        "Undergraduate Chemistry" -> levelFourFiveConceptContent["Undergraduate|${seed.title}|$concept"]
        "Postgraduate Chemistry" -> levelFourFiveConceptContent["Postgraduate|${seed.title}|$concept"]
        else -> null
    }
    val rawSummary = authoredContent?.summary ?: conceptSummary(concept, topic)
    val summary = lessonQualitySummary(concept, topic, rawSummary, seed.level)
    val rawTheory = authoredContent?.theory(topic) ?: expandedTheory(concept, summary, topic, seed.category, seed.level)
    val rawExample = authoredContent?.workedExample ?: workedExampleFor(concept, seed.category)
    val rawSteps = authoredContent?.learningSteps ?: exampleStepsFor(concept, seed.category)
    val rawMistake = authoredContent?.commonMistake ?: commonMistakeFor(concept, seed.category)
    val rawApplication = authoredContent?.application ?: applicationFor(concept, seed.category)
    val rawConnections = authoredContent?.connections ?: advancedConceptsFor(concept)
    return CurriculumLesson(
        title = concept,
        concept = summary,
        formula = authoredContent?.keyRelationship ?: keyRelationshipFor(concept),
        question = "Which approach best demonstrates understanding of $concept?",
        answers = listOf(
            "Memorizing its name only",
            "Connecting evidence, mechanism and application",
            "Ignoring experimental limits",
            "Using an unrelated model"
        ),
        correct = 1,
        theory = lessonQualityTheory(rawTheory, seed.category, seed.level),
        workedExample = lessonQualityWorkedExample(concept, rawExample, seed.category),
        exampleSteps = lessonQualitySteps(concept, rawSteps, seed.category),
        commonMistake = lessonQualityMistake(concept, rawMistake),
        realWorldApplication = lessonQualityApplication(concept, rawApplication, seed.category),
        advancedConcepts = lessonQualityConnections(concept, rawConnections, seed.category)
    )
}

private fun lessonQualitySummary(concept: String, topic: String, summary: String, level: String): String {
    val audience = when (level) {
        "Class 6-8" -> "Use particle pictures and everyday evidence."
        "Class 9-10" -> "Connect definitions with observations, formula writing and simple calculations."
        "Class 11-12" -> "Link the model to equations, trends, graphs and laboratory evidence."
        "Undergraduate" -> "Include mechanism, assumptions, limitations and quantitative interpretation."
        else -> "Evaluate evidence quality, uncertainty, model limits and research-level applications."
    }
    return "$summary Learning goal: explain $concept inside $topic, predict one observable result, and justify the answer with evidence. $audience"
}

private fun lessonQualityTheory(theory: String, category: String, level: String): String {
    val evidencePrompt = when {
        category.contains("Analytical", true) -> "Evidence focus: calibration, blank/reference controls, replicate measurements and uncertainty decide whether the conclusion is reliable."
        category.contains("Organic", true) -> "Evidence focus: product identity follows from mechanism, functional groups, stereochemistry and supporting spectra or chemical tests."
        category.contains("Inorganic", true) -> "Evidence focus: oxidation state, electron count, coordination environment, color, magnetism or reactivity should support the claim."
        category.contains("Physical", true) -> "Evidence focus: graphs, equations, limiting cases, units and assumptions must agree with the observed chemical behavior."
        category.contains("Biochemistry", true) -> "Evidence focus: structure, binding, catalytic rate, regulation and biological context must be connected carefully."
        else -> "Evidence focus: connect the particle-level model to a clear observation, measurement or balanced equation."
    }
    val depthPrompt = if (level == "Postgraduate") {
        "At research level, compare alternative models and state the limitation that would change the interpretation."
    } else {
        "Before moving on, state one condition where the idea works well and one mistake that would make the answer unreliable."
    }
    return "$theory\n\n$evidencePrompt\n\n$depthPrompt"
}

private fun lessonQualityWorkedExample(concept: String, example: String, category: String): String {
    val frame = when {
        category.contains("Analytical", true) -> "Worked example focus: define analyte, choose calibration, calculate with units, then report uncertainty."
        category.contains("Organic", true) -> "Worked example focus: mark electron-rich/electron-poor sites, follow arrows, then check the product."
        category.contains("Physical", true) -> "Worked example focus: write the relationship, define variables, substitute units and interpret the trend."
        category.contains("Inorganic", true) -> "Worked example focus: assign oxidation state/electron count, predict structure or reaction, then compare with evidence."
        else -> "Worked example focus: claim, evidence, calculation or model, then chemical meaning."
    }
    return "$frame $example Key check: after solving $concept, explain why at least two wrong approaches fail."
}

private fun lessonQualitySteps(concept: String, steps: List<String>, category: String): List<String> {
    val base = steps.ifEmpty {
        listOf(
            "Identify the chemical species, quantities and observations.",
            "Choose the relationship, model or balanced equation that applies.",
            "Work through the reasoning with units and assumptions.",
            "Compare the answer with evidence and chemical plausibility."
        )
    }
    val qualityStep = when {
        category.contains("Laboratory", true) || category.contains("Analytical", true) ->
            "Record uncertainty, controls, possible interferences and whether the evidence is sufficient."
        category.contains("Drug", true) ->
            "Balance potency with solubility, permeability, metabolism, selectivity and safety."
        else ->
            "Finish by naming the assumption, common mistake and real-world use connected with $concept."
    }
    return (base + qualityStep).distinct().take(6)
}

private fun lessonQualityMistake(concept: String, mistake: String): String =
    "$mistake Fix it by writing the claim in your own words, checking units/conditions, and explaining how the evidence supports $concept."

private fun lessonQualityApplication(concept: String, application: String, category: String): String {
    val impact = when {
        category.contains("Environmental", true) -> "Also consider sustainability, exposure and waste."
        category.contains("Drug", true) -> "Also consider patient safety, dosing and off-target effects."
        category.contains("Materials", true) -> "Also consider structure-property tuning and durability."
        category.contains("Nuclear", true) || category.contains("Radiochemistry", true) || concept.contains("nuclear", true) || concept.contains("radiation", true) ->
            "Also consider radiation protection, medical imaging, power generation and waste management."
        else -> "Also consider safety, cost, efficiency and measurement reliability."
    }
    return "$application $impact"
}

private fun lessonQualityConnections(concept: String, connections: List<String>, category: String): List<String> {
    val required = listOf(
        "Evidence and uncertainty",
        "Common misconception boundary",
        "Real-world safety or design decision"
    )
    val nuclear = if (
        concept.contains("nuclear", true) || concept.contains("decay", true) ||
        concept.contains("radiation", true) || category.contains("Radiochemistry", true)
    ) {
        listOf("Nuclide notation", "Half-life modelling", "Radiation shielding")
    } else emptyList()
    return (connections + nuclear + required).distinct().take(8)
}

private fun conceptSummary(concept: String, topic: String): String {
    val name = concept.lowercase()
    return when {
        listOf("mole", "molar", "stoichi", "limiting", "percentage composition").any { it in name } ->
            "$concept connects measurable amounts to numbers of chemical particles, allowing balanced equations to become quantitative predictions."
        listOf("acid", "base", "ph", "buffer", "neutralization").any { it in name } ->
            "$concept explains proton transfer and the resulting composition of aqueous solutions, including indicator changes and quantitative pH behavior."
        listOf("bond", "hybrid", "orbital", "vsepr", "molecular shape").any { it in name } ->
            "$concept links valence-electron arrangement with molecular geometry, stability, polarity and chemical reactivity."
        listOf("rate", "kinetic", "activation", "catal", "mechanism").any { it in name } ->
            "$concept explains how quickly chemical change occurs by relating molecular collisions, energy barriers and reaction pathways."
        listOf("spectr", "nmr", "raman", "mass spect", "chromat", "analysis").any { it in name } ->
            "$concept converts a chemical or physical response into evidence about identity, structure, purity or concentration."
        listOf("nuclear", "radioactivity", "half-life", "half life", "fission", "fusion", "alpha", "beta", "gamma", "mass defect", "binding energy").any { it in name } ->
            "$concept explains how unstable nuclei change while conserving nucleon number, charge, energy and momentum, with radiation evidence interpreted safely."
        listOf("enzyme", "protein", "carbohydrate", "lipid", "metabolism", "biomolecule").any { it in name } ->
            "$concept applies chemical structure, bonding and reaction energetics to molecules and transformations found in living systems."
        listOf("drug", "pharmaco", "adme", "qsar", "docking", "tox").any { it in name } ->
            "$concept connects molecular structure and physicochemical properties with biological activity, exposure, selectivity and safety."
        listOf("metal", "ligand", "coordination", "block", "inorganic", "ore").any { it in name } ->
            "$concept explains the structure, bonding, preparation and characteristic reactions of elements, ions and inorganic compounds."
        listOf("organic", "alkane", "alkene", "aromatic", "carbonyl", "amine", "alcohol", "stereo").any { it in name } ->
            "$concept uses electron movement and functional-group behavior to explain the structure, properties and reactions of carbon compounds."
        else ->
            "$concept is a core part of $topic. It connects particle-level structure and chemical interactions with observable evidence and practical applications."
    }
}

private fun expandedTheory(
    title: String,
    summary: String,
    topic: String = title,
    category: String = "",
    level: String = "Class 11–12"
): String {
    val name = title.lowercase()
    val explanation = when {
        listOf("mole", "molar", "stoichi", "limiting", "percentage composition").any { it in name } ->
            "Chemists count atoms and molecules indirectly because individual particles are too small to handle. One mole contains 6.022 × 10²³ specified entities. Molar mass converts between mass and moles, while a balanced equation supplies the mole ratio between reactants and products. The limiting reactant is consumed first and therefore fixes the maximum theoretical yield."
        listOf("acid", "base", "ph", "buffer", "neutralization").any { it in name } ->
            "In the Brønsted–Lowry model, an acid donates H⁺ and a base accepts H⁺. In water these transfers establish equilibria, so strength and concentration are different ideas. pH is logarithmic: a change of one pH unit corresponds to a tenfold change in hydronium concentration. Buffers use a weak acid/base conjugate pair to consume small additions of acid or base."
        listOf("bond", "hybrid", "vsepr", "molecular shape").any { it in name } ->
            "Chemical bonds form because a new electron arrangement can lower the energy of the combined atoms. Ionic bonding is dominated by electrostatic attraction, covalent bonding by shared electron density, and metallic bonding by delocalized electrons. Electron domains arrange to reduce repulsion, so Lewis structures and VSEPR predictions connect directly to shape, polarity and intermolecular attraction."
        listOf("orbital", "quantum", "electronic configuration", "atomic model").any { it in name } ->
            "Electrons are described by wavefunctions and occupy quantized states rather than fixed classical paths. Orbitals represent probability distributions with characteristic energies, shapes and nodes. The Aufbau principle, Pauli exclusion principle and Hund’s rule organize electron configurations; those configurations explain periodic trends and valence behavior."
        listOf("rate", "kinetic", "activation", "catal", "mechanism").any { it in name } ->
            "A chemical equation gives overall change, while a mechanism describes the elementary steps. Reaction rate depends on the frequency of effective collisions and the fraction of molecules able to cross the activation barrier. Rate laws are established experimentally. A catalyst creates an alternative pathway with a lower barrier but does not change the balanced reaction or equilibrium composition."
        listOf("spectr", "nmr", "raman", "mass spect").any { it in name } ->
            "Spectroscopic methods probe quantized interactions between matter and energy. Peak position reports an energy difference or chemical environment, intensity relates to probability or amount, and splitting or fragmentation reveals connectivity. Reliable interpretation combines multiple signals with molecular formula, symmetry and chemical reasoning."
        listOf("nuclear", "radioactivity", "half-life", "half life", "fission", "fusion", "alpha", "beta", "gamma", "mass defect", "binding energy").any { it in name } ->
            "Nuclear chemistry studies changes in the nucleus rather than ordinary electron-shell reactions. A balanced nuclear equation conserves mass number and atomic number. Radioactive decay is random for individual nuclei but predictable for large samples through activity and half-life. Energy release is explained by mass defect and binding energy. Radiation must be interpreted with shielding, distance, time and dose in mind."
        listOf("chromat", "separation", "purification").any { it in name } ->
            "A mixture separates when its components interact differently with two phases or respond differently to a physical property. In chromatography, repeated partitioning between stationary and mobile phases produces different migration rates. Good separation requires appropriate selectivity, controlled conditions and a way to detect the separated substances."
        listOf("enzyme", "protein", "carbohydrate", "lipid", "metabolism", "biomolecule").any { it in name } ->
            "Biomolecular behavior follows ordinary chemical principles: covalent structure, non-covalent forces, acid–base chemistry and reaction kinetics. Three-dimensional structure creates binding and catalytic sites. Metabolic pathways couple favorable and unfavorable reactions, transfer electrons through cofactors and regulate chemical flux according to cellular demand."
        listOf("drug", "pharmaco", "adme", "qsar", "docking", "tox").any { it in name } ->
            "A useful drug must do more than bind a target. Its functional groups, ionization, lipophilicity, shape and flexibility influence potency, selectivity, solubility, permeability, metabolism and toxicity. Medicinal chemists change structure systematically, measure the resulting properties and use structure–activity relationships to balance efficacy with exposure and safety."
        listOf("coordination", "ligand", "transition metal", "organometal").any { it in name } ->
            "A coordination compound contains a central metal bound to electron-pair-donating ligands. Metal oxidation state, d-electron count, geometry and ligand-field splitting control color, magnetism and reactivity. Substitution, electron transfer and changes in oxidation state form the basis of many inorganic and catalytic mechanisms."
        listOf("organic", "alkane", "alkene", "aromatic", "carbonyl", "amine", "alcohol", "stereo", "functional group").any { it in name } ->
            "Organic reactivity is governed by electron-rich and electron-poor sites. Functional groups define characteristic bond polarities and reaction patterns; curved arrows track electron-pair movement. Structure, resonance, induction, steric effects and stereochemistry together determine which pathway and product are favored."
        listOf("solid", "crystal", "material", "polymer", "nano").any { it in name } ->
            "Bulk material properties arise from composition, bonding and structure across several length scales. Crystal symmetry and defects affect conductivity and strength, while polymer chain length and intermolecular forces affect flexibility and thermal behavior. Characterization connects microscopic organization with measured performance."
        else ->
            "Begin with the particles or chemical species involved, then identify the forces, bonds or electron transfers between them. A chemical model is useful when it explains observations and makes testable predictions. Always connect the symbolic representation—formula, equation, graph or structure—to measurable evidence, units and experimental conditions."
    }
    val disciplineContext = when (category) {
        "Inorganic Chemistry" ->
            "When studying $title, compare electron configuration, oxidation state, coordination environment and periodic position. These variables explain trends in color, magnetism, stability and reaction type."
        "Organic Chemistry" ->
            "For $title, draw the structure explicitly, mark electron-rich and electron-poor sites, and follow bond changes one elementary step at a time. Product prediction should include regioselectivity, stereochemistry and reaction conditions where relevant."
        "Analytical Chemistry" ->
            "For $title, separate the complete measurement process into sampling, calibration, signal generation, interference control and uncertainty. A numerical result is meaningful only when units, significant figures and method limitations are reported."
        "Biochemistry" ->
            "For $title, connect functional groups and non-covalent interactions with three-dimensional structure. Then follow atoms, electrons or energy through the biochemical transformation instead of memorizing a pathway as isolated names."
        "Drug Discovery" ->
            "For $title, evaluate potency together with selectivity, solubility, permeability, metabolic stability and safety. Improving one molecular property can worsen another, so evidence must be interpreted as a multi-parameter optimization problem."
        "Materials Chemistry" ->
            "For $title, connect composition and bonding to structure at atomic, microscopic and bulk scales. Processing changes structure, and structure determines the measured electrical, optical, mechanical or thermal property."
        "Applied Chemistry", "Environmental Chemistry" ->
            "For $title, use balanced chemical transformations and material flows to compare efficiency, hazards, waste and environmental impact. Practical conclusions should follow from chemical evidence rather than labels such as ‘natural’ or ‘green’ alone."
        "Physical Chemistry" ->
            "For $title, translate between a molecular model, a mathematical relationship and an experiment. State the assumptions behind the equation and check whether the predicted trend agrees with limiting cases and measured data."
        else ->
            "A complete explanation of $title should identify the substances or particles involved, describe the chemical interaction, predict an observation and state the evidence that would support the prediction."
    }
    val levelContext = when (level) {
        "Class 6–8" ->
            "At this level, begin with observable properties and safe everyday examples, then use a simple particle model to explain what cannot be seen directly."
        "Class 9–10" ->
            "Use particle diagrams, word equations and balanced formula equations together. Distinguish observation from inference and support every conclusion with a chemical change or measured property."
        "Class 11–12" ->
            "Use mole relationships, electronic structure, balanced equations and graphs quantitatively. Show units at every step and explain exceptions instead of treating trends as absolute rules."
        "Undergraduate" ->
            "Evaluate the model quantitatively, derive or justify the central relationship, connect it with experimental technique and identify the conditions under which the approximation fails."
        "Postgraduate" ->
            "Compare competing models, connect the concept with current experimental or computational methods, and evaluate uncertainty, mechanism and limitations before drawing a conclusion."
        else -> ""
    }
    return "$summary\n\n$explanation\n\n$disciplineContext\n\n$levelContext"
}

private fun workedExampleFor(title: String, category: String = ""): String {
    val name = title.lowercase()
    return when {
        listOf("mole", "molar", "stoichi").any { it in name } ->
            "Example: 18.0 g of H₂O ÷ 18.0 g mol⁻¹ = 1.00 mol H₂O. This represents about 6.022 × 10²³ water molecules. In 2H₂ + O₂ → 2H₂O, producing 1.00 mol H₂O requires 1.00 mol H₂ and 0.50 mol O₂."
        listOf("acid", "ph").any { it in name } ->
            "Example: if [H₃O⁺] = 1.0 × 10⁻³ mol L⁻¹, pH = −log(1.0 × 10⁻³) = 3. The solution is acidic, and diluting it tenfold ideally raises the pH by about one unit."
        listOf("bond", "vsepr", "molecular shape").any { it in name } ->
            "Example: CH₄ has four bonding domains around carbon and no lone pairs. The domains point toward the corners of a tetrahedron, giving bond angles close to 109.5°. The symmetric molecule is non-polar overall."
        listOf("rate", "kinetic").any { it in name } ->
            "Example: for rate = k[A], doubling [A] doubles the initial rate. If doubling [A] instead makes the rate four times larger, the reaction is second order in A because 2² = 4."
        listOf("electro", "cell", "battery").any { it in name } ->
            "Example: in a Zn/Cu galvanic cell, Zn is oxidized at the anode and Cu²⁺ is reduced at the cathode. Electrons flow through the wire from Zn to Cu while the salt bridge maintains charge balance."
        listOf("chromat").any { it in name } ->
            "Example: a spot moves 3.0 cm while the solvent front moves 5.0 cm, so Rf = 3.0/5.0 = 0.60. A second compound with Rf = 0.25 interacts more strongly with the stationary phase under the same conditions."
        listOf("spectr", "beer", "absorb").any { it in name } ->
            "Example: Beer–Lambert law gives A = εbc. If ε = 100 L mol⁻¹ cm⁻¹, b = 1.0 cm and c = 0.0020 mol L⁻¹, then A = 0.20."
        listOf("enzyme").any { it in name } ->
            "Example: when substrate concentration is much lower than Km, enzyme rate is sensitive to added substrate. Near saturation, most active sites are occupied and the rate approaches Vmax."
        listOf("drug", "adme", "pharmaco").any { it in name } ->
            "Example: adding a polar group may improve aqueous solubility but reduce membrane permeability. A medicinal-chemistry decision compares both measurements and checks whether potency and metabolic stability are preserved."
        listOf("oxid", "redox").any { it in name } ->
            "Example: Zn + Cu²⁺ → Zn²⁺ + Cu. Zinc loses two electrons and is oxidized; copper(II) gains those electrons and is reduced. The electron count lost and gained is equal."
        listOf("density").any { it in name } ->
            "Example: a sample has mass 54 g and volume 20 cm³. Density = mass/volume = 2.7 g cm⁻³, consistent with aluminium under ordinary conditions."
        category == "Inorganic Chemistry" ->
            "Example: in [Fe(CN)₆]⁴⁻, six CN⁻ ligands contribute −6 overall. Because the complex charge is −4, iron is +2. The metal oxidation state and ligand field are then used to discuss electron count, magnetism and color."
        category == "Organic Chemistry" ->
            "Example: propene reacts with HBr by protonation of the double bond followed by bromide attack. Under ordinary ionic conditions, the pathway through the more stable secondary carbocation gives 2-bromopropane as the major product."
        category == "Analytical Chemistry" ->
            "Example: standards of 1, 2 and 3 mg L⁻¹ give signals of 0.20, 0.40 and 0.60. A sample signal of 0.50 corresponds to 2.5 mg L⁻¹ from the calibration line, before applying any dilution factor."
        category == "Biochemistry" ->
            "Example: an enzyme lowers the activation barrier for substrate conversion but does not change the reaction equilibrium. Increasing substrate raises the initial rate until active sites approach saturation."
        category == "Drug Discovery" ->
            "Example: analogue A is potent but poorly soluble; analogue B adds a solubilizing group and retains target activity. Chemists next compare permeability, metabolic stability and off-target activity before selecting the better lead."
        category == "Materials Chemistry" ->
            "Example: adding a small controlled amount of phosphorus to silicon introduces extra charge carriers. The atomic-scale substitution changes electrical conductivity and enables an n-type semiconductor."
        category == "Applied Chemistry" || category == "Environmental Chemistry" ->
            "Example: compare two synthesis routes using product yield, atom economy, solvent hazard and energy demand. A high-yield route may still create more waste if most reactant atoms leave as by-products."
        else ->
            "Example: identify the chemical species, write the balanced relationship, predict the measurable change, and compare the prediction with evidence such as color, pH, mass, spectrum, reaction rate or product yield."
    }
}

private fun exampleStepsFor(title: String, category: String = ""): List<String> {
    val name = title.lowercase()
    return when {
        listOf("mole", "molar", "stoichi", "limiting").any { it in name } -> listOf(
            "Write and balance the chemical equation before using any numerical data.",
            "Convert the given mass, volume or particle count into moles with units.",
            "Apply the balanced-equation mole ratio and identify the limiting reactant if more than one reactant is given.",
            "Convert the result into the requested unit and check significant figures."
        )
        listOf("acid", "ph", "buffer", "titration").any { it in name } -> listOf(
            "Identify the acid–base species and decide whether they are strong, weak or part of a buffer pair.",
            "Write the proton-transfer or equilibrium equation and calculate the relevant concentration.",
            "Use pH = −log[H₃O⁺] or the appropriate equilibrium relationship.",
            "Check that the calculated pH and chemical interpretation are consistent."
        )
        listOf("bond", "vsepr", "hybrid", "molecular shape").any { it in name } -> listOf(
            "Count total valence electrons and draw a valid Lewis structure.",
            "Count bonding domains and lone-pair domains around the central atom.",
            "Assign electron-domain geometry, molecular shape and approximate bond angles.",
            "Use bond polarity and symmetry to decide whether the whole molecule is polar."
        )
        listOf("rate", "kinetic", "activation", "catal").any { it in name } -> listOf(
            "Identify the measured concentration change and the time interval.",
            "Determine the experimental rate law or apply the stated reaction order.",
            "Compare how concentration, temperature or a catalyst changes effective collisions and the activation pathway.",
            "Report the rate with correct units and distinguish rate from equilibrium yield."
        )
        listOf("electro", "cell", "battery", "corrosion").any { it in name } -> listOf(
            "Write the oxidation and reduction half-reactions separately.",
            "Balance atoms and charge, then match the number of transferred electrons.",
            "Identify anode, cathode and electron-flow direction.",
            "Calculate cell potential or deposited amount using the stated electrochemical relationship."
        )
        listOf("spectr", "nmr", "raman", "mass spect", "absorb").any { it in name } -> listOf(
            "Record peak position, intensity, multiplicity or fragmentation information.",
            "Assign each important signal to a plausible structural feature.",
            "Check the assignment against molecular formula, symmetry and chemical environment.",
            "Combine evidence from more than one signal before proposing the final structure."
        )
        listOf("nuclear", "radioactivity", "half-life", "half life", "fission", "fusion", "alpha", "beta", "gamma", "mass defect", "binding energy").any { it in name } -> listOf(
            "Write the nuclide notation and identify mass number, atomic number and emitted radiation.",
            "Balance nucleon number and charge on both sides of the nuclear equation.",
            "Use half-life, activity or mass-energy relationships only after defining every variable.",
            "Interpret the result with radiation safety: time, distance, shielding and contamination control."
        )
        listOf("chromat", "separation", "purification").any { it in name } -> listOf(
            "Identify the mixture components and the property that can distinguish them.",
            "Select stationary/mobile phases or a physical separation condition.",
            "Calculate retention, Rf, recovery or purity when data are available.",
            "Confirm identity and purity with a second observation or analytical method."
        )
        listOf("enzyme", "protein", "metabolism", "biomolecule").any { it in name } -> listOf(
            "Identify the reacting functional groups, substrate and product.",
            "Locate the catalyst, cofactor or energy-coupling step.",
            "Follow atoms or electrons through the transformation.",
            "Explain how structure, concentration or regulation changes the observed rate."
        )
        listOf("drug", "adme", "pharmaco", "qsar", "docking", "tox").any { it in name } -> listOf(
            "State the target property and the experimental evidence used to measure it.",
            "Compare structural changes between compounds and build a structure–activity relationship.",
            "Check potency together with solubility, permeability, metabolism and selectivity.",
            "Choose the next compound using the full property profile, not potency alone."
        )
        category == "Inorganic Chemistry" -> listOf(
            "Assign oxidation states and determine the metal or main-group electron count.",
            "Identify ligands, coordination number, geometry or periodic relationship.",
            "Use bonding and electronic structure to predict color, magnetism or reactivity.",
            "Check the prediction against a known reaction, spectrum or chemical test."
        )
        category == "Organic Chemistry" -> listOf(
            "Draw the substrate and identify its functional groups.",
            "Mark nucleophilic, electrophilic and leaving-group sites.",
            "Use curved arrows to follow electron-pair movement and intermediates.",
            "Check regiochemistry, stereochemistry, charge and atom conservation in the product."
        )
        category == "Analytical Chemistry" -> listOf(
            "Define the analyte, sample matrix and required concentration range.",
            "Select a calibration method and appropriate standards or blank.",
            "Convert the measured signal into concentration and apply dilution factors.",
            "Report uncertainty, significant figures and possible interferences."
        )
        else -> listOf(
            "Identify the substances, particles and measurable quantities involved.",
            "Choose the chemical model, balanced equation or relationship that applies.",
            "Work through the prediction with units and explicit reasoning.",
            "Compare the result with experimental evidence and state any assumptions."
        )
    }
}

private fun commonMistakeFor(title: String, category: String = ""): String {
    val name = title.lowercase()
    return when {
        listOf("mole", "molar", "stoichi").any { it in name } ->
            "A common mistake is using masses directly in an equation ratio. Balanced coefficients represent mole ratios, so convert every quantity to moles first."
        listOf("acid", "ph", "buffer").any { it in name } ->
            "Do not confuse acid strength with concentration. Strength describes extent of ionization; concentration describes how much acid is present per volume."
        listOf("bond", "vsepr", "molecular shape").any { it in name } ->
            "Do not name the electron-domain geometry when the question asks for molecular shape. Lone pairs affect the shape even though they are not shown as atoms."
        listOf("equilibrium", "le chatelier").any { it in name } ->
            "Equilibrium does not mean equal amounts or stopped reactions. It means the forward and reverse rates are equal under unchanged conditions."
        listOf("rate", "kinetic", "catal").any { it in name } ->
            "A catalyst increases both forward and reverse rates. It does not change the equilibrium constant or make an impossible product thermodynamically favored."
        listOf("electro", "cell", "battery").any { it in name } ->
            "Do not identify anode and cathode only by sign. Oxidation is always at the anode and reduction is always at the cathode."
        listOf("spectr", "nmr", "mass spect").any { it in name } ->
            "Do not assign a structure from one peak alone. A valid structure must account for every important signal, the molecular formula and chemical plausibility."
        listOf("nuclear", "radioactivity", "half-life", "half life", "fission", "fusion", "alpha", "beta", "gamma", "mass defect", "binding energy").any { it in name } ->
            "Do not treat nuclear equations like ordinary chemical equations. Balance mass number and atomic number, and distinguish irradiation from contamination and exposure from dose."
        listOf("drug", "adme", "pharmaco", "docking").any { it in name } ->
            "A high docking score or strong biochemical potency does not by itself make a good drug. Exposure, selectivity, metabolism and toxicity must also be measured."
        category == "Organic Chemistry" ->
            "Do not memorise the product without tracking electrons. Conditions, intermediate stability and stereochemistry determine which pathway operates."
        category == "Analytical Chemistry" ->
            "Precision is not the same as accuracy. Closely repeated values can still be biased if calibration, sampling or interference is wrong."
        else ->
            "Avoid memorizing the final statement without connecting it to particles, bonds, a balanced equation and observable evidence."
    }
}

private fun applicationFor(title: String, category: String = ""): String {
    val name = title.lowercase()
    return when {
        listOf("acid", "ph", "buffer").any { it in name } ->
            "pH and buffer chemistry control medicines, blood chemistry, food preservation, soil treatment, fermentation and industrial water systems."
        listOf("electro", "battery", "corrosion").any { it in name } ->
            "Electrochemical principles guide battery design, metal refining, electroplating, corrosion prevention, sensors and green-hydrogen technologies."
        listOf("spectr", "nmr", "mass spect", "chromat").any { it in name } ->
            "These methods identify medicines, pollutants, forensic samples and reaction products while measuring purity and concentration."
        listOf("polymer", "plastic").any { it in name } ->
            "Polymer chemistry controls packaging, fibres, coatings, medical devices, electronics and the design of recyclable or degradable materials."
        listOf("enzyme", "protein", "metabolism").any { it in name } ->
            "This chemistry supports clinical diagnostics, biotechnology, fermentation, enzyme engineering and understanding metabolic disease."
        listOf("drug", "adme", "pharmaco", "qsar", "docking", "tox").any { it in name } ->
            "Drug-discovery teams use this concept to select safer molecules, predict human exposure and decide which compounds should advance to further testing."
        listOf("fertilizer", "soil", "agricultural").any { it in name } ->
            "Agricultural chemists use this knowledge to measure nutrients, improve fertilizer efficiency and reduce contamination of soil and water."
        listOf("nuclear", "radioactivity", "half-life", "half life", "fission", "fusion", "alpha", "beta", "gamma", "mass defect", "binding energy").any { it in name } ->
            "Nuclear chemistry supports medical imaging and therapy, radiocarbon dating, smoke detectors, power generation, sterilization, tracers and industrial inspection."
        category == "Inorganic Chemistry" ->
            "Inorganic chemistry supports catalysts, pigments, ceramics, electronic materials, metal extraction and metal-based diagnostics or medicines."
        category == "Organic Chemistry" ->
            "Organic reaction design is used to prepare pharmaceuticals, agrochemicals, dyes, polymers, fragrances and functional materials."
        category == "Analytical Chemistry" ->
            "Analytical laboratories use this concept for quality control, environmental monitoring, clinical testing and confirmation of product identity."
        category == "Materials Chemistry" ->
            "Materials chemists use this relationship to tune batteries, solar cells, semiconductors, coatings, structural composites and sensors."
        else ->
            "Chemists apply this concept when selecting materials, controlling reactions, interpreting measurements and designing safer or more efficient processes."
    }
}

private fun keyRelationshipFor(title: String): String {
    val name = title.lowercase()
    return when {
        "half-life" in name || "half life" in name || "decay kinetics" in name -> "N = N0(1/2)^(t/t1/2)"
        "alpha" in name -> "A_ZX -> A-4_Z-2Y + 4_2He"
        "beta" in name -> "A_ZX -> A_Z+1Y + beta- + antineutrino"
        "gamma" in name -> "X* -> X + gamma"
        "mass defect" in name -> "Delta m = Zmp + Nmn - mnucleus"
        "binding energy" in name -> "E = Delta mc^2"
        "fission" in name -> "heavy nucleus + n -> fragments + neutrons + energy"
        "fusion" in name -> "light nuclei -> heavier nucleus + energy"
        "nuclear" in name || "radioactivity" in name || "isotope" in name -> "A = Z + N"
        "density" in name -> "density = mass / volume"
        listOf("mole", "molar").any { it in name } -> "moles = mass / molar mass"
        "ph" in name -> "pH = −log[H₃O⁺]"
        "rate" in name -> "rate = change in concentration / time"
        "beer" in name || "absorb" in name -> "A = εbc"
        "cell potential" in name -> "E°cell = E°cathode − E°anode"
        else -> "structure → bonding → properties → reaction"
    }
}

private fun advancedConceptsFor(title: String): List<String> {
    val name = title.lowercase()
    return when {
        listOf("nuclear", "radioactivity", "half-life", "half life", "fission", "fusion", "alpha", "beta", "gamma", "mass defect", "binding energy").any { it in name } ->
            listOf("Nuclide notation and conservation", "Decay kinetics and activity", "Mass-energy equivalence", "Shielding and dose")
        listOf("atomic", "orbital", "quantum", "electronic").any { it in name } ->
            listOf("Wavefunctions, nodes and orbital probability", "Shielding and effective nuclear charge", "Photoelectron spectroscopy")
        listOf("bond", "vsepr", "hybrid").any { it in name } ->
            listOf("Molecular orbital theory", "Bond order and magnetic behavior", "Walsh diagrams and hypervalency")
        listOf("acid", "base", "ph", "buffer").any { it in name } ->
            listOf("Polyprotic equilibria", "Activity and non-ideal solutions", "Buffer capacity and titration modelling")
        listOf("organic", "alkane", "alkene", "aromatic", "carbonyl", "amine", "alcohol", "stereo").any { it in name } ->
            listOf("Reaction-energy diagrams", "Stereoelectronic effects", "Retrosynthesis and chemoselectivity")
        listOf("coordination", "ligand", "transition", "organometal").any { it in name } ->
            listOf("Ligand-field stabilization energy", "Electronic spectra and selection rules", "Catalytic cycles and electron counting")
        listOf("spectr", "nmr", "mass spect", "chromat", "analysis").any { it in name } ->
            listOf("Instrument calibration and detection limits", "Two-dimensional and hyphenated techniques", "Chemometrics and uncertainty")
        listOf("enzyme", "protein", "metabolism", "biomolecule").any { it in name } ->
            listOf("Transition-state stabilization", "Allosteric regulation", "Isotope tracing and metabolic flux")
        listOf("drug", "adme", "pharmaco", "qsar", "docking", "tox").any { it in name } ->
            listOf("Free-energy perturbation", "Bioisosteres and scaffold hopping", "PK/PD and exposure–response modelling")
        listOf("solid", "crystal", "material", "polymer", "nano").any { it in name } ->
            listOf("Reciprocal space and diffraction", "Band structure and charge transport", "Structure–property optimization")
        else ->
            listOf("Molecular-level mechanism", "Experimental evidence and limitations", "Advanced chemical applications")
    }
}

private val expandedCurriculum = listOf(
    seed("Matter Around Us", "Class 6–8", "Introduction to Chemistry", "Matter Around Us",
        "What is matter?", "States of matter", "Properties of solids", "Liquids", "Gases", "Plasma",
        "Bose-Einstein Condensate", "Physical changes", "Chemical changes", "Classification of matter",
        "Mixtures", "Pure substances"),
    seed("Measurement in Chemistry", "Class 6–8", "Introduction to Chemistry", "Measurement in Chemistry",
        "SI Units", "Derived Units", "Scientific notation", "Significant figures", "Precision", "Accuracy",
        "Errors", "Density", "Temperature scales", "Laboratory measurements"),
    seed("Atoms", "Class 6–8", "Introduction to Chemistry", "Atoms",
        "Discovery of atom", "Dalton theory", "Thomson model", "Rutherford model", "Bohr model",
        "Modern atomic model", "Subatomic particles", "Atomic number", "Mass number", "Isotopes",
        "Isobars", "Isotones", "Isoelectronic species"),
    seed("Molecules", "Class 6–8", "Introduction to Chemistry", "Molecules",
        "Elements", "Compounds", "Molecules", "Atomicity", "Molecular mass", "Formula mass", "Valency",
        "Chemical formula"),
    seed("Periodic Table", "Class 6–8", "Introduction to Chemistry", "Periodic Table",
        "History", "Mendeleev table", "Modern periodic table", "Groups", "Periods", "Metals",
        "Nonmetals", "Metalloids", "Periodic trends", "Electronic configuration basics"),
    seed("Chemical Reactions", "Class 6–8", "Introduction to Chemistry", "Chemical Reactions",
        "Combination", "Decomposition", "Displacement", "Double displacement", "Neutralization",
        "Combustion", "Oxidation", "Reduction", "Redox basics"),
    seed("Air, Water and Environment", "Class 6–8", "Environmental Chemistry", "Earth systems",
        "Composition of air", "Water cycle and purification", "Combustion and air pollution", "Greenhouse effect", "Resources and sustainability"),
    seed("Acids, Bases and Salts: Foundations", "Class 6–8", "Foundations", "Acids and bases",
        "Natural indicators", "Acidic and basic substances", "Neutralization", "Common salts", "Everyday applications"),
    seed("Metals, Non-metals and Carbon", "Class 6–8", "Inorganic Chemistry", "Descriptive chemistry",
        "Physical properties of metals", "Reactivity of metals", "Non-metals and their uses", "Corrosion and prevention", "Carbon forms and fuels"),

    seed("Atomic Structure", "Class 9–10", "High School Chemistry", "Atomic Structure",
        "Quantum numbers", "Orbitals", "Electron configuration", "Aufbau", "Hund rule", "Pauli principle"),
    seed("Chemical Bonding", "Class 9–10", "High School Chemistry", "Chemical Bonding",
        "Ionic bond", "Covalent bond", "Coordinate bond", "Metallic bond", "Hydrogen bond",
        "Van der Waals forces", "Lewis structures", "VSEPR", "Hybridization", "Molecular geometry"),
    seed("Mole Concept", "Class 9–10", "High School Chemistry", "Mole Concept",
        "Avogadro number", "Moles", "Stoichiometry", "Empirical formula", "Molecular formula",
        "Limiting reagent", "Percentage yield"),
    seed("States of Matter", "Class 9–10", "High School Chemistry", "States of Matter",
        "Gas laws", "Ideal gas equation", "Kinetic theory", "Liquids", "Surface tension", "Viscosity",
        "Vapour pressure", "Solids", "Crystal lattice"),
    seed("Solutions", "Class 9–10", "High School Chemistry", "Solutions",
        "Types", "Solubility", "Concentration", "Molarity", "Molality", "Normality", "Mole fraction",
        "ppm", "Dilution"),
    seed("Acids Bases Salts", "Class 9–10", "High School Chemistry", "Acids Bases Salts",
        "Arrhenius", "Bronsted", "Lewis", "pH", "Buffers", "Indicators", "Hydrolysis", "Salt chemistry"),

    seed("Particle Theory and Atomic Models", "Class 9–10", "Physical Chemistry", "Atomic foundations",
        "Laws of chemical combination", "Dalton atomic theory", "Atoms and molecules", "Mole concept introduction", "Atomic models and valency"),
    seed("Chemical Formulae and Stoichiometry", "Class 9–10", "General Chemistry", "Quantitative chemistry",
        "Writing chemical formulae", "Relative atomic and molecular mass", "Moles and molar mass", "Percentage composition", "Limiting reactant introduction"),
    seed("Reactivity and Chemical Change", "Class 9–10", "Inorganic Chemistry", "Reaction chemistry",
        "Activity series", "Displacement reactions", "Oxidation and reduction", "Corrosion and rancidity", "Energy in reactions"),
    seed("Metals and Metallurgy", "Class 9–10", "Inorganic Chemistry", "Extraction and materials",
        "Occurrence of metals", "Ore concentration", "Reduction and refining", "Alloys", "Recycling metals"),
    seed("Carbon Compounds", "Class 9–10", "Organic Chemistry", "Introductory organic",
        "Covalent bonding in carbon", "Homologous series", "Basic nomenclature", "Ethanol and ethanoic acid", "Soaps and detergents"),

    seed("Thermodynamics", "Class 11–12", "Physical Chemistry", "Thermodynamics",
        "System", "Surroundings", "Work", "Heat", "Internal energy", "Enthalpy", "Entropy",
        "Gibbs free energy", "Hess Law", "Spontaneity"),
    seed("Thermochemistry", "Class 11–12", "Physical Chemistry", "Thermochemistry",
        "Heat capacity", "Specific heat", "Calorimetry", "Bond enthalpy", "Lattice energy", "Born-Haber cycle"),
    seed("Chemical Equilibrium", "Class 11–12", "Physical Chemistry", "Chemical Equilibrium",
        "Dynamic equilibrium", "Equilibrium constant", "Le Chatelier principle", "Ionic equilibrium", "Buffer systems"),
    seed("Chemical Kinetics", "Class 11–12", "Physical Chemistry", "Chemical Kinetics",
        "Rate laws", "Order", "Molecularity", "Arrhenius equation", "Activation energy", "Catalysis"),
    seed("Electrochemistry", "Class 11–12", "Physical Chemistry", "Electrochemistry",
        "Redox", "Galvanic cells", "Electrolysis", "Nernst equation", "EMF", "Batteries", "Fuel cells", "Corrosion"),
    seed("Surface Chemistry", "Class 11–12", "Physical Chemistry", "Surface Chemistry",
        "Adsorption", "Catalysis", "Colloids", "Emulsions", "Micelles"),
    seed("Solid State", "Class 11–12", "Physical Chemistry", "Solid State",
        "Crystal systems", "Unit cell", "Packing", "Defects", "Semiconductors"),
    seed("Nuclear Chemistry", "Class 11–12", "Physical Chemistry", "Nuclear Chemistry",
        "Atomic nucleus basics",
        "Protons neutrons and isotopes",
        "Mass number and atomic number",
        "Nuclear stability",
        "Radioactivity",
        "Alpha decay",
        "Beta decay",
        "Gamma rays",
        "Half-life",
        "Nuclear equations",
        "Mass defect",
        "Binding energy",
        "Nuclear fission",
        "Nuclear fusion",
        "Radiation safety and applications"),

    seed("Advanced Mole Concept", "Class 11–12", "Physical Chemistry", "Stoichiometry",
        "Empirical and molecular formulae", "Limiting reagent", "Concentration units", "Equivalent mass", "Redox titration calculations"),
    seed("Advanced Atomic Structure", "Class 11–12", "Physical Chemistry", "Quantum foundations",
        "Photoelectric effect", "de Broglie relation", "Heisenberg uncertainty", "Schrödinger model", "Orbital nodes and shapes"),
    seed("Advanced Chemical Bonding", "Class 11–12", "Physical Chemistry", "Molecular structure",
        "Hybridization", "Molecular orbital theory", "Bond order and magnetism", "Hydrogen bonding", "Fajans rules"),
    seed("s-Block Chemistry", "Class 11–12", "Inorganic Chemistry", "Main-group chemistry",
        "Hydrogen and hydrides", "Alkali metals", "Alkaline earth metals", "Diagonal relationships", "Important compounds"),
    seed("p-Block Chemistry", "Class 11–12", "Inorganic Chemistry", "Main-group chemistry",
        "Groups 13 and 14", "Groups 15 and 16", "Halogens", "Noble gases", "Anomalous behavior and trends"),
    seed("d- and f-Block Chemistry", "Class 11–12", "Inorganic Chemistry", "Transition elements",
        "Electronic configurations", "Oxidation states and color", "Magnetic properties", "Lanthanoid contraction", "Actinoids"),
    seed("Coordination Compounds", "Class 11–12", "Inorganic Chemistry", "Coordination chemistry",
        "Werner theory", "Ligands and nomenclature", "Isomerism", "Crystal field splitting", "Stability and applications"),
    seed("Principles of Organic Chemistry", "Class 11–12", "Organic Chemistry", "Structure and mechanism",
        "Electronic effects", "Reaction intermediates", "Acidity and basicity", "Purification methods", "Qualitative organic analysis"),
    seed("Hydrocarbons and Halo Compounds", "Class 11–12", "Organic Chemistry", "Organic families",
        "Alkanes and cycloalkanes", "Alkenes and alkynes", "Aromaticity", "Haloalkanes", "Haloarenes"),
    seed("Oxygen and Nitrogen Compounds", "Class 11–12", "Organic Chemistry", "Functional groups",
        "Alcohols and phenols", "Ethers", "Aldehydes and ketones", "Carboxylic acids", "Amines and Diazonium Salts"),
    seed("Biomolecules and Polymers", "Class 11–12", "Biochemistry", "Biological molecules",
        "Carbohydrates", "Amino acids and proteins", "Enzymes", "Nucleic acids", "Natural and synthetic polymers"),
    seed("Chemistry in Society", "Class 11–12", "Applied Chemistry", "Health and industry",
        "Medicines and drug classes", "Food chemistry", "Cleansing agents", "Nanomaterials", "Green chemistry principles"),

    seed("Advanced Physical Chemistry", "Undergraduate", "Undergraduate Chemistry", "Advanced Physical Chemistry",
        "Quantum chemistry", "Statistical mechanics", "Spectroscopy", "Molecular symmetry", "Group theory", "Phase rule", "Non-ideal systems"),
    seed("Advanced Organic Chemistry", "Undergraduate", "Undergraduate Chemistry", "Advanced Organic Chemistry",
        "Reaction mechanisms", "Pericyclic reactions", "Rearrangements", "Named reactions", "Heterocyclic chemistry",
        "Organometallic chemistry", "Stereochemistry", "Asymmetric synthesis"),
    seed("Advanced Inorganic Chemistry", "Undergraduate", "Undergraduate Chemistry", "Advanced Inorganic Chemistry",
        "Crystal field theory", "Ligand field theory", "Bioinorganic chemistry", "Organometallic compounds", "Cluster compounds"),
    seed("Analytical Chemistry", "Undergraduate", "Undergraduate Chemistry", "Analytical Chemistry",
        "Gravimetric analysis", "Volumetric analysis", "Complexometric titrations", "Redox titrations",
        "Instrumental analysis", "Error analysis"),
    seed("Spectroscopy", "Undergraduate", "Undergraduate Chemistry", "Spectroscopy",
        "UV", "IR", "Raman", "NMR", "ESR", "Mass spectrometry", "XRD", "XPS"),
    seed("Separation Techniques", "Undergraduate", "Undergraduate Chemistry", "Separation Techniques",
        "Chromatography", "HPLC", "GC", "TLC", "Ion exchange", "Electrophoresis"),
    seed("Computational Chemistry", "Undergraduate", "Undergraduate Chemistry", "Computational Chemistry",
        "Molecular modeling", "Gaussian", "DFT", "Molecular dynamics", "Docking"),
    seed("Polymer Chemistry", "Undergraduate", "Undergraduate Chemistry", "Polymer Chemistry",
        "Polymerization", "Conducting polymers", "Biopolymers"),
    seed("Industrial Chemistry", "Undergraduate", "Undergraduate Chemistry", "Industrial Chemistry",
        "Cement", "Fertilizers", "Glass", "Ceramics", "Petrochemicals", "Soap", "Paint", "Dyes", "Explosives"),
    seed("Environmental Chemistry", "Undergraduate", "Undergraduate Chemistry", "Environmental Chemistry",
        "Air pollution", "Water pollution", "Green chemistry", "Waste management", "Atmospheric chemistry"),
    seed("Biochemistry", "Undergraduate", "Undergraduate Chemistry", "Biochemistry",
        "Proteins", "DNA", "RNA", "Enzymes", "Metabolism", "ATP", "Photosynthesis", "Respiration"),
    seed("Medicinal Chemistry", "Undergraduate", "Undergraduate Chemistry", "Medicinal Chemistry",
        "Drug discovery", "SAR", "Pharmacokinetics", "Antibiotics", "Antivirals"),
    seed("Materials Chemistry", "Undergraduate", "Undergraduate Chemistry", "Materials Chemistry",
        "Nanomaterials", "Ceramics", "Smart materials", "Biomaterials", "Batteries", "Solar cells"),

    seed("Chemical Kinetics and Dynamics", "Undergraduate", "Physical Chemistry", "Reaction dynamics",
        "Integrated rate laws", "Complex reactions", "Steady-state approximation", "Transition-state theory", "Photochemical kinetics"),
    seed("Quantum Chemistry", "Undergraduate", "Physical Chemistry", "Quantum mechanics",
        "Operators and wavefunctions", "Particle in a box", "Rigid rotor", "Harmonic oscillator", "Hydrogen atom"),
    seed("Molecular Spectroscopy", "Undergraduate", "Analytical Chemistry", "Spectroscopy",
        "Rotational spectroscopy", "Infrared and Raman spectroscopy", "UV-visible spectroscopy", "NMR spectroscopy", "Mass spectrometry"),
    seed("Advanced Electrochemistry", "Undergraduate", "Physical Chemistry", "Electrochemistry",
        "Ion activities", "Debye–Hückel theory", "Electrode kinetics", "Polarography and voltammetry", "Fuel cells and batteries"),
    seed("Main-Group Inorganic Chemistry", "Undergraduate", "Inorganic Chemistry", "Main-group chemistry",
        "Electron-deficient compounds", "Boranes and carboranes", "Silicates and silicones", "Phosphazenes", "Noble-gas compounds"),
    seed("Transition-Metal Chemistry", "Undergraduate", "Inorganic Chemistry", "Coordination chemistry",
        "Ligand field theory", "Electronic spectra", "Magnetochemistry", "Substitution mechanisms", "Electron-transfer reactions"),
    seed("Organometallic Chemistry", "Undergraduate", "Inorganic Chemistry", "Organometallics",
        "18-electron rule", "Metal carbonyls", "Metal–alkyl bonding", "Oxidative addition and reductive elimination", "Homogeneous catalysis"),
    seed("Bioinorganic Chemistry", "Undergraduate", "Inorganic Chemistry", "Metals in biology",
        "Metalloproteins", "Oxygen transport", "Electron-transfer proteins", "Metalloenzymes", "Metal toxicity and therapy"),
    seed("Advanced Organic Mechanisms", "Undergraduate", "Organic Chemistry", "Reaction mechanisms",
        "Substitution and elimination", "Electrophilic and nucleophilic addition", "Aromatic substitution", "Rearrangements", "Pericyclic reactions"),
    seed("Stereochemistry and Conformation", "Undergraduate", "Organic Chemistry", "Stereochemistry",
        "Chirality and symmetry", "R/S and E/Z notation", "Conformational analysis", "Stereoselective reactions", "Chiral resolution"),
    seed("Organic Synthesis", "Undergraduate", "Organic Chemistry", "Synthesis",
        "Retrosynthetic analysis", "Protecting groups", "Carbon–carbon bond formation", "Oxidation and reduction reagents", "Multistep synthesis"),
    seed("Heterocyclic and Natural Products Chemistry", "Undergraduate", "Organic Chemistry", "Specialized organic",
        "Five-membered heterocycles", "Six-membered heterocycles", "Alkaloids", "Terpenes and steroids", "Biosynthetic origins"),
    seed("Biophysical and Cellular Biochemistry", "Undergraduate", "Biochemistry", "Biomolecular structure",
        "Water and buffers in cells", "Protein folding", "Membrane structure", "Molecular recognition", "Cellular energetics"),
    seed("Metabolism", "Undergraduate", "Biochemistry", "Metabolic pathways",
        "Glycolysis and gluconeogenesis", "Citric acid cycle", "Oxidative phosphorylation", "Lipid metabolism", "Amino-acid metabolism"),
    seed("Enzymology", "Undergraduate", "Biochemistry", "Enzyme science",
        "Michaelis–Menten kinetics", "Enzyme inhibition", "Allostery", "Catalytic mechanisms", "Enzyme assays"),
    seed("Instrumental Analysis", "Undergraduate", "Analytical Chemistry", "Instrumentation",
        "Sampling and calibration", "Chromatography", "Electroanalytical methods", "Thermal analysis", "Hyphenated techniques"),
    seed("Environmental and Industrial Chemistry", "Undergraduate", "Applied Chemistry", "Processes and sustainability",
        "Atmospheric chemistry", "Water and wastewater treatment", "Industrial unit operations", "Petrochemicals", "Life-cycle assessment"),

    seed("Advanced Quantum Chemistry", "Postgraduate", "Postgraduate Chemistry", "Advanced Quantum Chemistry", "Core principles and applications"),
    seed("Advanced Thermodynamics", "Postgraduate", "Postgraduate Chemistry", "Advanced Thermodynamics", "Core principles and applications"),
    seed("Statistical Thermodynamics", "Postgraduate", "Postgraduate Chemistry", "Statistical Thermodynamics", "Core principles and applications"),
    seed("Advanced Chemical Kinetics", "Postgraduate", "Postgraduate Chemistry", "Advanced Chemical Kinetics", "Core principles and applications"),
    seed("Photochemistry", "Postgraduate", "Postgraduate Chemistry", "Photochemistry", "Core principles and applications"),
    seed("Electroanalytical Chemistry", "Postgraduate", "Postgraduate Chemistry", "Electroanalytical Chemistry", "Core principles and applications"),
    seed("Supramolecular Chemistry", "Postgraduate", "Postgraduate Chemistry", "Supramolecular Chemistry", "Core principles and applications"),
    seed("Solid State Chemistry", "Postgraduate", "Postgraduate Chemistry", "Solid State Chemistry", "Core principles and applications"),
    seed("Organometallic Chemistry", "Postgraduate", "Postgraduate Chemistry", "Organometallic Chemistry", "Core principles and applications"),
    seed("Bioorganic Chemistry", "Postgraduate", "Postgraduate Chemistry", "Bioorganic Chemistry", "Core principles and applications"),
    seed("Bioinorganic Chemistry", "Postgraduate", "Postgraduate Chemistry", "Bioinorganic Chemistry", "Core principles and applications"),
    seed("Synthetic Organic Chemistry", "Postgraduate", "Postgraduate Chemistry", "Synthetic Organic Chemistry", "Core principles and applications"),
    seed("Retrosynthetic Analysis", "Postgraduate", "Postgraduate Chemistry", "Retrosynthetic Analysis", "Core principles and applications"),
    seed("Green Chemistry", "Postgraduate", "Postgraduate Chemistry", "Green Chemistry", "Core principles and applications"),
    seed("Catalysis", "Postgraduate", "Postgraduate Chemistry", "Catalysis",
        "Homogeneous", "Heterogeneous", "Enzyme catalysis", "Photocatalysis"),
    seed("Nanochemistry", "Postgraduate", "Postgraduate Chemistry", "Nanochemistry", "Core principles and applications"),
    seed("Surface Science", "Postgraduate", "Postgraduate Chemistry", "Surface Science", "Core principles and applications"),
    seed("Medicinal Chemistry", "Postgraduate", "Postgraduate Chemistry", "Medicinal Chemistry", "Core principles and applications"),
    seed("Pharmaceutical Chemistry", "Postgraduate", "Postgraduate Chemistry", "Pharmaceutical Chemistry", "Core principles and applications"),
    seed("Food Chemistry", "Postgraduate", "Postgraduate Chemistry", "Food Chemistry", "Core principles and applications"),
    seed("Agricultural Chemistry", "Postgraduate", "Postgraduate Chemistry", "Agricultural Chemistry", "Core principles and applications"),
    seed("Petrochemical Chemistry", "Postgraduate", "Postgraduate Chemistry", "Petrochemical Chemistry", "Core principles and applications"),
    seed("Forensic Chemistry", "Postgraduate", "Postgraduate Chemistry", "Forensic Chemistry", "Core principles and applications"),
    seed("Geochemistry", "Postgraduate", "Postgraduate Chemistry", "Geochemistry", "Core principles and applications"),
    seed("Atmospheric Chemistry", "Postgraduate", "Postgraduate Chemistry", "Atmospheric Chemistry", "Core principles and applications"),
    seed("Marine Chemistry", "Postgraduate", "Postgraduate Chemistry", "Marine Chemistry", "Core principles and applications"),
    seed("Radiochemistry", "Postgraduate", "Postgraduate Chemistry", "Radiochemistry", "Core principles and applications"),
    seed("Astrochemistry", "Postgraduate", "Postgraduate Chemistry", "Astrochemistry", "Core principles and applications"),
    seed("Materials Science", "Postgraduate", "Postgraduate Chemistry", "Materials Science", "Core principles and applications"),
    seed("Computational Chemistry", "Postgraduate", "Postgraduate Chemistry", "Computational Chemistry", "Core principles and applications"),
    seed("Chemical Biology", "Postgraduate", "Postgraduate Chemistry", "Chemical Biology", "Core principles and applications"),
    seed("Chemical Engineering Fundamentals", "Postgraduate", "Postgraduate Chemistry", "Chemical Engineering Fundamentals",
        "Unit operations", "Mass transfer", "Heat transfer", "Reactor design"),
    seed("Instrumentation", "Postgraduate", "Postgraduate Chemistry", "Instrumentation",
        "GC-MS", "LC-MS/MS", "ICP-MS", "AAS", "ICP-OES", "XRF", "SEM", "TEM", "AFM", "Confocal microscopy"),
    seed("Research Methodology", "Postgraduate", "Postgraduate Chemistry", "Research Methodology",
        "Literature review", "Experimental design", "Scientific writing", "Data analysis", "Chemometrics"),
    seed("Advanced Laboratory Techniques", "Postgraduate", "Postgraduate Chemistry", "Advanced Laboratory Techniques",
        "Air-sensitive synthesis", "Glove box", "Schlenk line", "High-pressure reactions", "Cryogenic techniques"),
    seed("Emerging Areas", "Postgraduate", "Postgraduate Chemistry", "Emerging Areas",
        "AI in Chemistry", "Machine Learning", "Quantum Computing for Chemistry", "CRISPR Chemistry",
        "Molecular Electronics", "Soft Matter", "MOFs", "COFs", "Click Chemistry", "Flow Chemistry", "Microfluidics"),

    seed("Advanced Spectroscopic Structure Elucidation", "Postgraduate", "Analytical Chemistry", "Structure determination",
        "Multidimensional NMR", "Advanced mass fragmentation", "Time-resolved spectroscopy", "EPR spectroscopy", "X-ray crystallography"),
    seed("Supramolecular and Materials Chemistry", "Postgraduate", "Materials Chemistry", "Functional materials",
        "Host–guest chemistry", "Self-assembly", "Polymers and composites", "Nanochemistry", "Electronic and energy materials"),
    seed("Advanced Inorganic Reaction Mechanisms", "Postgraduate", "Inorganic Chemistry", "Mechanisms and catalysis",
        "Ligand substitution kinetics", "Inner- and outer-sphere transfer", "Activation of small molecules", "Metal clusters", "Catalytic cycles"),
    seed("Advanced Organic Synthesis", "Postgraduate", "Organic Chemistry", "Modern synthesis",
        "Asymmetric synthesis", "Cross-coupling reactions", "C–H activation", "Photoredox chemistry", "Flow and automated synthesis"),
    seed("Drug Discovery Pipeline", "Postgraduate", "Drug Discovery", "Discovery workflow",
        "Target identification and validation", "Assay development", "Hit discovery and screening", "Hit-to-lead optimization", "Candidate selection"),
    seed("Pharmacokinetics and Drug Metabolism", "Postgraduate", "Drug Discovery", "ADME and safety",
        "Absorption and distribution", "Cytochrome P450 metabolism", "Clearance and half-life", "Toxicology and safety pharmacology", "PK/PD modelling"),
    seed("Computer-Aided Drug Design", "Postgraduate", "Drug Discovery", "Computational design",
        "Molecular descriptors", "QSAR", "Pharmacophore modelling", "Molecular docking", "AI and generative molecular design"),
    seed("Pharmaceutical Chemistry and Formulation", "Postgraduate", "Drug Discovery", "Development",
        "Preformulation", "Dosage forms", "Drug delivery systems", "Stability and degradation", "Quality by design"),

    seed("Class 6 Materials, Separation and Changes", "Class 6–8", "Introduction to Chemistry", "CBSE and AP foundations",
        "Materials by appearance and hardness", "Soluble and insoluble substances", "Floating and sinking materials",
        "Handpicking, sieving and winnowing", "Sedimentation, decantation, filtration and evaporation",
        "Reversible and irreversible changes"),
    seed("Class 7 Acids, Metals and Everyday Reactions", "Class 6–8", "Introduction to Chemistry", "CBSE and AP foundations",
        "Natural indicators and litmus", "Neutralisation in daily life", "Rusting of iron", "Preventing corrosion",
        "Metals and non-metals around us", "Reactions of metals with oxygen, water and acids"),
    seed("Class 8 Electric Current and Chemical Effects", "Class 6–8", "Introduction to Chemistry", "CBSE and AP foundations",
        "Conductors and insulators in liquids", "Electrolytes and simple testers", "Simple electroplating for class 8"),
    seed("Fibres, Plastics, Fuels and Combustion", "Class 6–8", "Applied Chemistry", "Materials and fuels",
        "Natural and synthetic fibres", "Thermoplastics and thermosets", "Coal and petroleum fractions", "Combustion and flame", "Fuel efficiency and pollution"),
    seed("Class 9 Matter and Mixtures", "Class 9–10", "General Chemistry", "CBSE and AP SSC foundations",
        "Evaporation and diffusion", "Sublimation and latent heat", "Solutions, suspensions and colloids",
        "Tyndall effect and concentration", "Separating mixtures in class 9"),
    seed("Class 9 Atoms and Formulae", "Class 9–10", "General Chemistry", "CBSE and AP SSC foundations",
        "Laws of chemical combination", "Dalton atomic theory at school level", "Atomic mass and molecular mass",
        "Valency and writing formulae", "Mole concept introduction"),
    seed("Class 10 Chemical Substances and Carbon", "Class 9–10", "General Chemistry", "CBSE and AP SSC foundations",
        "Chemical equations and conservation", "Important salts in daily life", "Metals, metallurgy and corrosion",
        "Carbon bonding and homologous series", "Ethanol, ethanoic acid, soaps and detergents"),
    seed("Periodic Classification and Electrolysis", "Class 9–10", "General Chemistry", "Classification and electrochemistry",
        "Modern periodic law", "Periodic trends at secondary level", "Chemical effects of electric current", "Electrolysis and electrode products", "Electroplating"),
    seed("School Practical Chemistry", "Class 9–10", "Analytical Chemistry", "Practical chemistry",
        "Preparing and diluting solutions", "Testing gases", "Identifying acids and bases", "Precipitation observations", "Recording results and uncertainty"),
    seed("Intermediate First Year Foundations", "Class 11–12", "General Chemistry", "CBSE and AP Intermediate",
        "Some basic concepts of chemistry", "Stoichiometry and limiting reagent", "Atomic structure for class 11",
        "Hydrogen and its compounds", "Environmental chemistry for school"),
    seed("Intermediate First Year Physical Chemistry", "Class 11–12", "Physical Chemistry", "CBSE and AP Intermediate",
        "States of matter and gas laws", "Thermodynamics at school level", "Chemical and ionic equilibrium",
        "Redox reactions and oxidation number", "Organic chemistry basic principles"),
    seed("Intermediate Second Year Inorganic and Physical", "Class 11–12", "Inorganic Chemistry", "CBSE and AP Intermediate",
        "Solutions and Raoult's Law", "Colligative properties", "d- and f-block elements",
        "Coordination compounds exam basics", "Electrochemistry cells and Nernst equation"),
    seed("Intermediate Second Year Organic and Biomolecules", "Class 11–12", "Organic Chemistry", "CBSE and AP Intermediate",
        "Haloalkanes and haloarenes", "Alcohols, phenols and ethers", "Aldehydes, ketones and carboxylic acids",
        "Amines and Diazonium Salts", "Biomolecules, vitamins and polymers"),
    seed("Senior Secondary Practical Chemistry", "Class 11–12", "Analytical Chemistry", "Practical chemistry",
        "Volumetric analysis", "Systematic salt analysis", "Organic functional-group tests", "Inorganic and organic preparations", "Chemical kinetics and electrochemistry experiments"),
    seed("Nuclear and Radiochemistry", "Undergraduate", "Inorganic Chemistry", "Nuclear chemistry",
        "Nuclear stability and decay", "Decay kinetics and half-life", "Nuclear reactions", "Radiochemical methods", "Applications and radiation safety"),
    seed("Photochemistry", "Undergraduate", "Physical Chemistry", "Excited-state chemistry",
        "Light absorption and excited states", "Jablonski diagrams", "Quantum yield", "Photochemical reactions", "Photosensitization and applications"),
    seed("Symmetry and Group Theory in Chemistry", "Postgraduate", "Inorganic Chemistry", "Molecular symmetry",
        "Symmetry elements and operations", "Point groups", "Character tables", "Vibrational-mode analysis", "Orbital symmetry applications"),
    seed("Advanced Acid–Base and Solvent Chemistry", "Postgraduate", "Inorganic Chemistry", "Solution chemistry",
        "Lewis and HSAB concepts", "Non-aqueous solvents", "Superacids and superbases", "Solvent leveling and differentiating effects", "Acid–base catalysis"),
    seed("Separation Science and Chemometrics", "Postgraduate", "Analytical Chemistry", "Advanced analysis",
        "Advanced liquid chromatography", "Gas chromatography", "Electrophoretic separations", "Method validation", "Multivariate calibration"),
    seed("Chemical Metrology and Quality Assurance", "Postgraduate", "Analytical Chemistry", "Measurement science",
        "Traceability and reference materials", "Measurement uncertainty", "Detection and quantification limits", "Validation and robustness", "Laboratory quality systems"),
    seed("Energy, Petrochemical and Agricultural Chemistry", "Undergraduate", "Applied Chemistry", "Industrial applications",
        "Petroleum refining chemistry", "Alternative fuels", "Fertilizer chemistry", "Pesticide chemistry", "Soil and nutrient analysis")
).mapIndexed { moduleIndex, item ->
    val palette = listOf(Cyan, Purple, Green, Orange, Color(0xFF4D8BFF), Color(0xFFC456FF))
    CurriculumModule(
        title = item.title,
        icon = levelOneModuleIcons[item.title] ?: levelTwoModuleIcons[item.title] ?: levelThreeModuleIcons[item.title]
            ?: levelFourFiveModuleIcons["${item.level}|${item.title}"] ?: when (item.category) {
            "Biochemistry" -> "BIO"
            "Drug Discovery" -> "Rx"
            "Inorganic Chemistry" -> "M"
            "Organic Chemistry" -> "C"
            else -> "•"
        },
        color = palette[moduleIndex % palette.size],
        description = "${item.subcategory} for ${item.level}, with concepts, visual practice and knowledge checks.",
        lessons = item.concepts.map { concept -> generatedLesson(item, concept) },
        level = item.level,
        category = item.category,
        subcategory = item.subcategory
    )
}
