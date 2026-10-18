package com.indianservers.aichemistryexplorer

private data class Misconception(
    val claim: String,
    val correction: String
)

private data class AssessmentProfile(
    val id: String,
    val keywords: List<String>,
    val scenarioQuestion: String,
    val scenarioAnswer: String,
    val scenarioDistractors: List<String>,
    val scenarioExplanation: String,
    val evidenceQuestion: String,
    val evidenceAnswer: String,
    val evidenceDistractors: List<String>,
    val evidenceExplanation: String,
    val misconceptions: List<Misconception>
)

private fun authoredCheck(
    question: String,
    answer: String,
    distractors: List<String>,
    correctPosition: Int,
    explanation: String,
    feedback: List<String>,
    skill: String
): KnowledgeCheck {
    val position = correctPosition.coerceIn(0, 3)
    val wrong = distractors.take(3).toMutableList()
    while (wrong.size < 3) wrong += "The conclusion is not supported by the chemical model."
    val options = wrong.toMutableList().apply { add(position, answer) }
    val wrongFeedback = feedback.take(3).toMutableList()
    while (wrongFeedback.size < 3) wrongFeedback += "This option does not track the particles, evidence, or conserved quantity in the situation."
    val optionFeedback = wrongFeedback.toMutableList().apply { add(position, explanation) }
    return KnowledgeCheck(
        question = question,
        options = options,
        correct = position,
        explanation = explanation,
        hint = "Identify the tempting shortcut, then test it against particles, conservation, conditions, or evidence.",
        skill = skill,
        difficulty = "Diagnostic",
        reviewPage = "Think",
        optionFeedback = optionFeedback
    )
}

internal fun authoredMisconceptionChecksFor(
    module: CurriculumModule,
    lesson: CurriculumLesson
): List<KnowledgeCheck> {
    val key = "${module.title} ${module.category} ${module.subcategory} ${lesson.title}".lowercase()
    val profile = assessmentProfiles.mapNotNull { profile ->
        profile.keywords.filter(key::contains).maxOfOrNull(String::length)?.let { matchLength -> profile to matchLength }
    }.maxByOrNull { it.second }?.first ?: return emptyList()
    val scenario = authoredCheck(
        question = profile.scenarioQuestion,
        answer = profile.scenarioAnswer,
        distractors = profile.scenarioDistractors,
        correctPosition = stablePosition("${lesson.title}-${profile.id}-scenario"),
        explanation = profile.scenarioExplanation,
        feedback = profile.scenarioDistractors.map { "This is a plausible shortcut, but it fails here: $it ${profile.scenarioExplanation}" },
        skill = "Misconception diagnosis"
    )
    val evidence = authoredCheck(
        question = profile.evidenceQuestion,
        answer = profile.evidenceAnswer,
        distractors = profile.evidenceDistractors,
        correctPosition = stablePosition("${lesson.title}-${profile.id}-evidence"),
        explanation = profile.evidenceExplanation,
        feedback = profile.evidenceDistractors.map { "This observation would not distinguish the competing explanations: $it" },
        skill = "Evidence discrimination"
    )
    val repairs = profile.misconceptions.take(3).mapIndexed { index, misconception ->
        val otherClaims = profile.misconceptions.filterNot { it == misconception }.map { it.claim }
        authoredCheck(
            question = "A student says, “${misconception.claim}” What is the most precise correction?",
            answer = misconception.correction,
            distractors = listOf(
                "The student is correct; no condition or particle-level explanation is missing.",
                otherClaims.getOrElse(0) { "The rule works because every chemical system behaves ideally." },
                otherClaims.getOrElse(1) { "Only the numerical answer matters, so the reasoning need not be corrected." }
            ),
            correctPosition = stablePosition("${lesson.title}-${profile.id}-repair-$index"),
            explanation = misconception.correction,
            feedback = listOf(
                "Accepting the statement preserves the misconception instead of checking its hidden assumption.",
                "This substitutes a different misconception and still does not repair the student's causal model.",
                "A correct number cannot rescue chemically incorrect reasoning."
            ),
            skill = "Misconception repair"
        )
    }
    return listOf(scenario, evidence) + repairs
}

private fun stablePosition(seed: String): Int = (seed.fold(0) { hash, character -> hash * 31 + character.code } and Int.MAX_VALUE) % 4

private val assessmentProfiles = listOf(
    AssessmentProfile(
        id = "equations",
        keywords = listOf("balancing equation", "chemical equation", "reaction equation", "stoichiometric equation"),
        scenarioQuestion = "The equation H₂ + O₂ → H₂O is not balanced. Which change preserves the identities of all substances?",
        scenarioAnswer = "Place coefficients to obtain 2H₂ + O₂ → 2H₂O.",
        scenarioDistractors = listOf("Change H₂O to H₂O₂.", "Change O₂ to O₃.", "Delete oxygen from the reactant side."),
        scenarioExplanation = "Coefficients change molecule counts; subscripts define substances and must not be altered while balancing.",
        evidenceQuestion = "Which check proves that 4Fe + 3O₂ → 2Fe₂O₃ is balanced?",
        evidenceAnswer = "Both sides contain four Fe atoms and six O atoms.",
        evidenceDistractors = listOf("The coefficients add to the same total on both sides.", "Every formula contains a subscript.", "Iron appears before oxygen on both sides."),
        evidenceExplanation = "Balancing is verified element by element, not by adding coefficients or matching visual patterns.",
        misconceptions = listOf(
            Misconception("Balancing means changing the subscripts until both sides look alike.", "Never change a correct formula's subscripts; change only whole-formula coefficients and then recount each element."),
            Misconception("A balanced equation must have the same number of molecules on each side.", "A balanced equation conserves atoms of every element; the total number of molecules may change."),
            Misconception("If oxygen is balanced, the whole equation must be balanced.", "Every element must be counted independently; balancing one element does not prove conservation of the others.")
        )
    ),
    AssessmentProfile(
        id = "mole",
        keywords = listOf("mole concept", "molar mass", "stoichiometry", "limiting reagent", "empirical formula", "molecular formula"),
        scenarioQuestion = "One mole of O₂ and one mole of CO₂ are compared. Which statement is correct?",
        scenarioAnswer = "They contain the same number of molecules but have different masses and atom counts.",
        scenarioDistractors = listOf("They have equal masses because both amounts are one mole.", "O₂ contains more molecules because it is lighter.", "CO₂ contains fewer atoms because its molar mass is larger."),
        scenarioExplanation = "A mole fixes the number of entities, not their mass; molecular composition determines atoms per molecule.",
        evidenceQuestion = "Which measurement is needed to convert 9.0 g of water into amount in moles?",
        evidenceAnswer = "The molar mass of H₂O, 18.0 g mol⁻¹.",
        evidenceDistractors = listOf("The density of oxygen gas.", "The coefficient of water in every possible equation.", "Avogadro's constant alone, without molar mass."),
        evidenceExplanation = "For a pure substance, n = m/M links sample mass to amount through molar mass.",
        misconceptions = listOf(
            Misconception("A mole is a fixed mass of every substance.", "A mole is a fixed number of specified entities; its mass depends on the substance's molar mass."),
            Misconception("Equation coefficients give gram ratios directly.", "Coefficients give particle or mole ratios; convert masses to moles before applying that ratio."),
            Misconception("The limiting reagent is the reactant with the smaller starting mass.", "The limiting reagent is identified by comparing available moles with stoichiometric requirements, not masses alone.")
        )
    ),
    AssessmentProfile(
        id = "states",
        keywords = listOf("state of matter", "states and phases", "phase change", "gaseous state", "gas law", "evaporation", "boiling"),
        scenarioQuestion = "A sealed syringe of gas is compressed at constant temperature. What particle-level change best explains the pressure increase?",
        scenarioAnswer = "The same particles occupy less volume and collide with the walls more frequently.",
        scenarioDistractors = listOf("Each gas particle becomes larger.", "New gas particles are created by compression.", "The particles stop moving and become heavier."),
        scenarioExplanation = "Compression changes spacing and wall-collision frequency, not particle size, number, or mass.",
        evidenceQuestion = "During boiling at constant pressure, energy is supplied while temperature stays constant. Where does the energy go?",
        evidenceAnswer = "It increases potential energy by overcoming intermolecular attractions.",
        evidenceDistractors = listOf("It disappears because the thermometer does not rise.", "It makes individual molecules expand.", "It converts molecules into different elements."),
        evidenceExplanation = "Latent heat changes particle arrangement and potential energy during the phase transition.",
        misconceptions = listOf(
            Misconception("Gas particles expand to fill a container.", "Gas particles retain essentially the same size; the distance between them changes as the gas fills the container."),
            Misconception("Evaporation can occur only at the boiling point.", "Evaporation occurs at the surface over a range of temperatures; boiling occurs throughout the liquid at its boiling condition."),
            Misconception("No temperature rise means no energy was absorbed.", "During a phase change, absorbed energy can overcome attractions while average kinetic energy and temperature remain constant.")
        )
    ),
    AssessmentProfile(
        id = "separation",
        keywords = listOf("separating mixture", "separation", "chromatography", "distillation", "crystallisation", "sublimation", "centrifugation", "purification"),
        scenarioQuestion = "A mixture contains sand, dissolved salt and water. Which sequence can recover dry salt?",
        scenarioAnswer = "Filter off sand, then evaporate or crystallise the filtrate.",
        scenarioDistractors = listOf("Filter once and collect salt on the filter paper.", "Use a magnet to remove dissolved salt.", "Chromatograph the whole mixture and call every spot pure salt."),
        scenarioExplanation = "Filtration removes insoluble sand; dissolved salt passes through and requires solvent removal or crystallisation.",
        evidenceQuestion = "In paper chromatography, what evidence indicates that an ink contains more than one soluble dye?",
        evidenceAnswer = "The sample separates into more than one spot under the same conditions.",
        evidenceDistractors = listOf("The solvent front reaches the top quickly.", "The original ink spot is dark.", "The paper becomes wet."),
        evidenceExplanation = "Multiple separated spots are evidence of multiple mobile components; darkness or wetness alone is not.",
        misconceptions = listOf(
            Misconception("Filtration can remove dissolved salt from water.", "Dissolved ions pass through ordinary filter paper; use evaporation, crystallisation, or distillation depending on the desired product."),
            Misconception("One separation method works for every mixture.", "Choose a method from differences in solubility, particle size, boiling point, density, magnetism, or affinity."),
            Misconception("A single chromatography spot always proves absolute purity.", "One spot under one set of conditions is supporting evidence, not absolute proof; another solvent or method may reveal hidden components.")
        )
    ),
    AssessmentProfile(
        id = "atomic",
        keywords = listOf("atomic structure", "atomic model", "electron configuration", "isotope", "quantum number", "orbital"),
        scenarioQuestion = "Two neutral atoms have the same proton number but different neutron numbers. How should they be classified?",
        scenarioAnswer = "They are isotopes of the same element with different mass numbers.",
        scenarioDistractors = listOf("They are different elements because their masses differ.", "They are ions because neutron number changed.", "They must have different chemical symbols and proton numbers."),
        scenarioExplanation = "Element identity is fixed by proton number; isotopes differ in neutron number and therefore mass number.",
        evidenceQuestion = "Which observation was strongest evidence that atoms contain a small, dense, positive nucleus?",
        evidenceAnswer = "A small fraction of alpha particles were deflected through very large angles in the gold-foil experiment.",
        evidenceDistractors = listOf("Most alpha particles passed straight through.", "Atoms combine in whole-number ratios.", "Cathode rays travel in straight lines."),
        evidenceExplanation = "Rare large deflections require concentrated positive charge and mass rather than a diffuse positive sphere.",
        misconceptions = listOf(
            Misconception("Electrons orbit the nucleus exactly like planets on fixed paths.", "Orbitals describe probability distributions; the planetary picture is a limited model, not a literal trajectory."),
            Misconception("Mass number and atomic number mean the same thing.", "Atomic number counts protons; mass number counts protons plus neutrons."),
            Misconception("An electron farther from the nucleus always moves more slowly on a fixed circular path.", "Quantum states are described by energies and orbitals, not classical circular speeds and paths.")
        )
    ),
    AssessmentProfile(
        id = "periodic",
        keywords = listOf("periodic", "classification of elements", "mendeleev", "newlands", "dobereiner", "s-block", "p-block"),
        scenarioQuestion = "Across Period 3 from Na toward Cl, which trend is generally expected and why?",
        scenarioAnswer = "Atomic radius decreases because effective nuclear attraction increases within the same principal shell.",
        scenarioDistractors = listOf("Radius increases because every atom gains another occupied shell.", "Radius is constant because all atoms are in one period.", "Radius decreases because the nucleus loses protons."),
        scenarioExplanation = "Across a period, proton number increases while added electrons enter the same main shell, increasing effective attraction.",
        evidenceQuestion = "Which fact supports placing elements in the same modern periodic-table group?",
        evidenceAnswer = "They show related valence-electron patterns and recurring chemical properties.",
        evidenceDistractors = listOf("They have identical atomic masses.", "They contain the same number of occupied shells.", "They were discovered in the same year."),
        evidenceExplanation = "Groups capture recurring valence structure and chemistry, whereas periods track occupied shells.",
        misconceptions = listOf(
            Misconception("The modern periodic table is arranged mainly by atomic mass.", "Modern periodic order is based on atomic number; mass-based anomalies were resolved by proton number."),
            Misconception("All periodic trends are exception-free rules.", "Trends are general patterns; subshell energies, shielding, pairing, and structure explain important exceptions."),
            Misconception("Elements in one group have identical properties.", "Group members have related valence patterns and chemistry, but size, reactivity, and physical properties change down the group.")
        )
    ),
    AssessmentProfile(
        id = "bonding",
        keywords = listOf("chemical bonding", "molecular structure", "ionic compound", "covalent compound", "electron-dot", "lewis", "vsepr", "hybridisation"),
        scenarioQuestion = "Solid sodium chloride does not conduct, but molten sodium chloride does. What changes?",
        scenarioAnswer = "The ions become mobile in the liquid while remaining charged.",
        scenarioDistractors = listOf("Melting creates electrons inside neutral NaCl molecules.", "The ions lose their charges when the lattice breaks.", "Covalent bonds form and carry current."),
        scenarioExplanation = "Conduction requires mobile charge carriers; ions are fixed in the solid lattice and mobile when molten.",
        evidenceQuestion = "Which evidence best distinguishes a molecular covalent substance from an ionic solid?",
        evidenceAnswer = "The covalent substance has discrete molecules and usually lacks mobile ions in pure liquid form.",
        evidenceDistractors = listOf("It contains no electrons.", "It always has a higher melting point than an ionic solid.", "Its atoms have no electronegativity difference."),
        evidenceExplanation = "Bonding and charge-carrier structure, not a single appearance or absolute melting rule, distinguish the models.",
        misconceptions = listOf(
            Misconception("Ionic compounds consist of separate molecules such as individual NaCl units.", "An ionic solid is an extended charge-balanced lattice; a formula unit gives the simplest ion ratio, not a discrete molecule."),
            Misconception("A double bond counts as two electron domains in VSEPR.", "Any multiple bond occupies one bonding domain around the central atom, although it can repel somewhat more strongly."),
            Misconception("Every stable atom must obey the octet rule without exception.", "The octet rule is useful but has electron-deficient, odd-electron, and expanded-valence exceptions.")
        )
    ),
    AssessmentProfile(
        id = "solutions",
        keywords = listOf("solution", "solubility", "colligative", "molarity", "molality", "concentration", "abnormal molar mass"),
        scenarioQuestion = "Water is added to a sodium chloride solution without losing any solute. What changes?",
        scenarioAnswer = "Concentration decreases, but the number of moles of NaCl remains constant.",
        scenarioDistractors = listOf("Some NaCl is destroyed by dilution.", "The moles of NaCl increase because volume increases.", "The solution becomes saturated automatically."),
        scenarioExplanation = "Dilution changes solute amount per solution volume, not the conserved amount of solute.",
        evidenceQuestion = "A solute associates into dimers in solution. How does this affect a colligative molar-mass result?",
        evidenceAnswer = "There are fewer solute particles than expected, so the apparent molar mass is too high.",
        evidenceDistractors = listOf("There are more particles, so apparent molar mass is too high.", "Association cannot affect a colligative property.", "Only the solvent molar mass changes."),
        evidenceExplanation = "Colligative effects depend on particle count; association lowers the van 't Hoff factor and the observed effect.",
        misconceptions = listOf(
            Misconception("Dilution removes some solute from the solution.", "Dilution adds solvent; solute moles stay constant unless material is removed or reacts."),
            Misconception("Molarity and molality are interchangeable because both measure concentration.", "Molarity uses solution volume and changes with temperature; molality uses solvent mass and is temperature independent."),
            Misconception("Solubility tells how fast a solute dissolves.", "Solubility is the equilibrium amount that can dissolve under stated conditions; dissolution rate is a kinetic quantity.")
        )
    ),
    AssessmentProfile(
        id = "acid-base",
        keywords = listOf("acid", "base", "ph", "buffer", "titration", "neutralisation", "salt hydrolysis"),
        scenarioQuestion = "Equal-concentration solutions of HCl and CH₃COOH are compared. Why can their pH values differ?",
        scenarioAnswer = "HCl ionises essentially completely, while ethanoic acid ionises only partly.",
        scenarioDistractors = listOf("Ethanoic acid contains fewer hydrogen atoms in its formula.", "HCl has a larger molar mass.", "Strong acids are always more concentrated than weak acids."),
        scenarioExplanation = "Acid strength concerns extent of ionisation; concentration is a separate quantity.",
        evidenceQuestion = "Which observation most reliably identifies the equivalence region in an acid-base titration?",
        evidenceAnswer = "A calibrated pH curve or suitable indicator changes sharply near the stoichiometric equivalence point.",
        evidenceDistractors = listOf("The solution volume becomes exactly 100 mL.", "The solution must be colourless at equivalence.", "The pH must always be exactly 7."),
        evidenceExplanation = "Equivalence is stoichiometric; its pH depends on acid/base strength and temperature.",
        misconceptions = listOf(
            Misconception("A strong acid is the same thing as a concentrated acid.", "Strength describes extent of ionisation; concentration describes amount per volume, so a strong acid can be dilute."),
            Misconception("Every neutralisation equivalence point has pH 7.", "Strong acid–strong base equivalence is near pH 7 at 25 °C; weak systems produce acidic or basic equivalence solutions."),
            Misconception("An indicator causes the neutralisation reaction.", "The acid and base react independently; a suitable indicator only signals a pH range near the endpoint.")
        )
    ),
    AssessmentProfile(
        id = "kinetics-equilibrium",
        keywords = listOf("kinetic", "rate equation", "collision theory", "half-life", "equilibrium", "le chatelier", "activation energy", "catalyst"),
        scenarioQuestion = "A catalyst is added to a reversible reaction already at equilibrium. What happens?",
        scenarioAnswer = "Forward and reverse reactions speed up equally, so equilibrium is reached faster without changing its position.",
        scenarioDistractors = listOf("Only the forward reaction speeds up, producing more product at equilibrium.", "The equilibrium constant increases because activation energy falls.", "The reverse reaction stops."),
        scenarioExplanation = "A catalyst provides lower-energy pathways in both directions and does not change ΔG° or K.",
        evidenceQuestion = "Which result supports a first-order rate law for reactant A?",
        evidenceAnswer = "The half-life remains constant when the initial concentration of A changes.",
        evidenceDistractors = listOf("The rate never changes during the reaction.", "The concentration falls by a constant number of moles each minute.", "The balanced-equation coefficient of A is one."),
        evidenceExplanation = "Constant half-life is characteristic of ideal first-order decay; order must be measured, not read from coefficients.",
        misconceptions = listOf(
            Misconception("Increasing temperature lowers the activation energy.", "Temperature changes the energy distribution and the fraction exceeding Ea; a catalyst changes the activation pathway."),
            Misconception("At equilibrium, forward and reverse reactions have stopped.", "Dynamic equilibrium has equal forward and reverse rates, so macroscopic composition is constant while molecular events continue."),
            Misconception("Reaction orders always equal balanced-equation coefficients.", "Orders are determined experimentally except for known elementary steps; overall stoichiometry alone does not fix the rate law.")
        )
    ),
    AssessmentProfile(
        id = "electrochemistry",
        keywords = listOf("electrochem", "conductivity", "molar conductivity", "kohlrausch", "electrolysis", "galvanic", "battery", "corrosion"),
        scenarioQuestion = "In a working galvanic cell, which path is taken by electrons released at the anode?",
        scenarioAnswer = "They travel through the external circuit to the cathode.",
        scenarioDistractors = listOf("They travel through the salt bridge to the cathode.", "They remain permanently on the anode metal.", "They are carried through solution by anions as free electrons."),
        scenarioExplanation = "Electrons use the metallic external circuit; ions move through electrolyte and salt bridge to maintain charge balance.",
        evidenceQuestion = "Why does molar conductivity of a weak electrolyte rise strongly on dilution?",
        evidenceAnswer = "Dilution increases its degree of ionisation as well as reducing interionic effects.",
        evidenceDistractors = listOf("Dilution creates extra solute moles.", "The electrodes become larger.", "Conductivity and molar conductivity must always change in the same direction."),
        evidenceExplanation = "Specific conductivity usually falls on dilution, while conductivity per mole can rise because ions are more independent and weak electrolytes ionise further.",
        misconceptions = listOf(
            Misconception("The anode is always the negative electrode.", "Oxidation always occurs at the anode; its sign is negative in a galvanic cell and positive in an electrolytic cell."),
            Misconception("Electrons move through the salt bridge.", "The salt bridge carries ions to maintain electroneutrality; electrons flow through the external conductor."),
            Misconception("If specific conductivity decreases on dilution, molar conductivity must also decrease.", "Specific conductivity and molar conductivity are different quantities; molar conductivity generally increases on dilution.")
        )
    ),
    AssessmentProfile(
        id = "thermodynamics",
        keywords = listOf("thermodynamic", "thermochemistry", "enthalpy", "entropy", "gibbs", "calorimetry", "endothermic", "exothermic"),
        scenarioQuestion = "Why can breaking bonds never be the energy-releasing step in an enthalpy calculation?",
        scenarioAnswer = "Energy is required to separate bonded atoms; energy is released when new bonds form.",
        scenarioDistractors = listOf("Every bond contains stored heat that escapes when cut.", "Bond breaking is exothermic only in gases.", "The sign depends only on whether oxygen is present."),
        scenarioExplanation = "Overall reaction enthalpy compares energy absorbed in breaking bonds with energy released in forming bonds.",
        evidenceQuestion = "An exothermic reaction warms an uninsulated cup less than expected. What is the best explanation?",
        evidenceAnswer = "Some released energy warms the cup and surroundings instead of the measured solution.",
        evidenceDistractors = listOf("Conservation of energy fails in an open cup.", "Exothermic reactions cannot have activation energy.", "The thermometer creates a lower enthalpy change."),
        evidenceExplanation = "Calorimetry requires an energy boundary; unmeasured heat transfer biases the calculated value.",
        misconceptions = listOf(
            Misconception("Breaking a chemical bond releases energy.", "Bond breaking requires energy; bond formation releases energy. Net enthalpy depends on both totals."),
            Misconception("Every exothermic reaction is spontaneous at every temperature.", "Spontaneity depends on ΔG = ΔH − TΔS, not on enthalpy alone."),
            Misconception("Heat and temperature are the same quantity.", "Heat is energy transferred because of temperature difference; temperature reflects the system's thermal state.")
        )
    ),
    AssessmentProfile(
        id = "organic",
        keywords = listOf("organic", "hydrocarbon", "iupac", "nomenclature", "functional group", "alkane", "alkene", "alkyne", "alcohol", "aldehyde", "ketone", "carboxylic"),
        scenarioQuestion = "For CH₃–CH(CH₃)–CH₂–OH, which naming decision must be made first?",
        scenarioAnswer = "Choose the longest chain containing the carbon bonded to –OH and number from the –OH end.",
        scenarioDistractors = listOf("Choose the visually straightest chain and number from either end.", "Ignore –OH and name only the carbon skeleton.", "Give the methyl group the lowest number even if –OH gets a higher number."),
        scenarioExplanation = "The principal functional group must be included in the parent chain and controls numbering priority.",
        evidenceQuestion = "Which observation distinguishes an alkene from an alkane under suitable test conditions?",
        evidenceAnswer = "The alkene rapidly decolourises bromine solution by addition, whereas the alkane does not without radical conditions.",
        evidenceDistractors = listOf("Only the alkane burns in oxygen.", "The alkene always dissolves in water.", "The alkane contains no carbon–carbon bonds."),
        evidenceExplanation = "The π bond enables electrophilic addition; both classes burn and are usually poorly soluble in water.",
        misconceptions = listOf(
            Misconception("The longest chain is always the straight horizontal line in a drawing.", "Trace every continuous carbon path; orientation on the page does not determine the parent chain."),
            Misconception("Double-bond position can be omitted whenever there is only one double bond.", "Include the locant whenever more than one constitutional position is possible under IUPAC rules."),
            Misconception("Oxidation of every alcohol gives a carboxylic acid.", "Primary alcohols can oxidise to aldehydes then acids; secondary alcohols give ketones, while tertiary alcohols resist simple oxidation.")
        )
    ),
    AssessmentProfile(
        id = "metals",
        keywords = listOf("metal", "reactivity series", "extraction", "ore", "alloy", "transition element", "coordination", "carbonyl"),
        scenarioQuestion = "Why is aluminium extracted by electrolysis rather than by heating aluminium oxide with carbon?",
        scenarioAnswer = "Aluminium is more reactive than carbon, so carbon cannot economically reduce its stable oxide.",
        scenarioDistractors = listOf("Aluminium oxide contains no oxygen to remove.", "Electrolysis is used because aluminium is below copper in the reactivity series.", "Carbon reacts only with metal sulfides."),
        scenarioExplanation = "Extraction method follows oxide stability and metal reactivity; very reactive metals require electrolytic reduction.",
        evidenceQuestion = "Which observation shows that iron rusting is a chemical change rather than removable dirt?",
        evidenceAnswer = "A new hydrated iron oxide forms and the original iron is consumed.",
        evidenceDistractors = listOf("The surface looks less shiny.", "The object can be wiped.", "Iron is a solid before and after exposure."),
        evidenceExplanation = "Formation of a new substance with changed composition distinguishes corrosion from surface contamination.",
        misconceptions = listOf(
            Misconception("Every metal oxide can be reduced by carbon.", "Oxides of metals above carbon in the reactivity series generally require electrolysis or another stronger reduction route."),
            Misconception("An alloy is a pure compound with one fixed formula.", "An alloy is a metallic mixture or solid solution whose composition can vary over a useful range."),
            Misconception("In a metal carbonyl, only the metal donates electrons to CO.", "Bonding is synergic: CO donates through carbon and the metal back-donates into CO π* orbitals.")
        )
    ),
    AssessmentProfile(
        id = "biomolecules",
        keywords = listOf("biomolecule", "vitamin", "hormone", "enzyme", "protein", "carbohydrate", "nucleic acid", "amino acid"),
        scenarioQuestion = "An enzyme speeds a biochemical reaction. Which statement describes its role?",
        scenarioAnswer = "It stabilises a lower-activation-energy pathway and is regenerated after the catalytic cycle.",
        scenarioDistractors = listOf("It supplies the reaction's equilibrium energy and is consumed.", "It changes the equilibrium constant to favour products.", "It turns every substrate into product regardless of structure."),
        scenarioExplanation = "Enzymes are selective catalysts: they change rate through pathway stabilisation, not equilibrium thermodynamics.",
        evidenceQuestion = "Which evidence distinguishes a vitamin from a major energy nutrient?",
        evidenceAnswer = "It is required in small amounts for normal function but is not chiefly oxidised to provide dietary energy.",
        evidenceDistractors = listOf("Every vitamin is a protein enzyme.", "Vitamins are needed only when a person is ill.", "All vitamins are stored indefinitely in body fat."),
        evidenceExplanation = "Vitamins have diverse regulatory or cofactor roles; water- and fat-soluble vitamins also differ in storage and toxicity.",
        misconceptions = listOf(
            Misconception("Enzymes are used up in the reactions they catalyse.", "Enzymes are regenerated overall, although they form temporary enzyme–substrate intermediates."),
            Misconception("Vitamins provide calories in the same way as carbohydrates and fats.", "Vitamins support biochemical functions in small amounts but are not major energy-yielding nutrients."),
            Misconception("Hormones and enzymes are interchangeable names for biological catalysts.", "Hormones are signalling molecules; enzymes are catalysts. Some are proteins, but their functions are distinct.")
        )
    ),
    AssessmentProfile(
        id = "laboratory-data",
        keywords = listOf("laboratory", "experimental design", "measurement", "uncertainty", "significant figure", "data representation", "safety"),
        scenarioQuestion = "Three titration titres are 24.80, 24.85 and 26.10 mL. What is the defensible treatment?",
        scenarioAnswer = "Investigate the discordant 26.10 mL result and repeat rather than averaging all three automatically.",
        scenarioDistractors = listOf("Average all values because every reading must be used.", "Keep only 26.10 mL because it is the largest.", "Add more decimal places so the readings agree."),
        scenarioExplanation = "Replicates should be checked for concordance and procedural causes before exclusion or averaging.",
        evidenceQuestion = "Which design best tests the effect of temperature on reaction rate?",
        evidenceAnswer = "Change temperature while controlling concentrations, volumes, apparatus and endpoint method, then repeat trials.",
        evidenceDistractors = listOf("Change temperature and concentration together.", "Run one trial at each temperature with different apparatus.", "Record only the fastest result at each temperature."),
        evidenceExplanation = "A fair test isolates the independent variable and uses replication to estimate variability.",
        misconceptions = listOf(
            Misconception("More decimal places automatically make a measurement more accurate.", "Digits should reflect instrument resolution and uncertainty; extra displayed digits do not remove systematic error."),
            Misconception("Precise repeated values must be accurate.", "Precision is agreement among readings; accuracy is closeness to a valid reference and can be harmed by systematic bias."),
            Misconception("One successful trial is enough evidence for a reliable conclusion.", "Replicates reveal random variation, anomalies, and whether the result is reproducible.")
        )
    )
)
