package com.indianservers.aichemistryexplorer

import androidx.compose.ui.graphics.Color

internal data class CurriculumLesson(
    val title: String,
    val concept: String,
    val formula: String,
    val question: String,
    val answers: List<String>,
    val correct: Int
)

internal data class CurriculumModule(
    val title: String,
    val icon: String,
    val color: Color,
    val description: String,
    val lessons: List<CurriculumLesson>
)

private fun lesson(title: String, concept: String, formula: String, question: String, correct: Int, vararg answers: String) =
    CurriculumLesson(title, concept, formula, question, answers.toList(), correct)

internal val curriculum = listOf(
    CurriculumModule("Atomic Structure", "⚛", Cyan, "Build matter from particles, nuclei and quantum orbitals.", listOf(
        lesson("Subatomic particles", "Protons and neutrons occupy the nucleus; electrons occupy the surrounding probability cloud.", "proton +1 · neutron 0 · electron −1", "Which particle determines an element's atomic number?", 1, "Electron", "Proton", "Neutron", "Photon"),
        lesson("Atomic number and mass number", "Atomic number counts protons. Mass number counts protons plus neutrons.", "A = Z + N", "An atom has 11 protons and 12 neutrons. Its mass number is…", 2, "11", "12", "23", "24"),
        lesson("Isotopes and ions", "Isotopes share a proton count but differ in neutrons; ions form by gaining or losing electrons.", "charge = protons − electrons", "Two isotopes of carbon differ in their number of…", 1, "Protons", "Neutrons", "Valence shells", "Chemical symbols"),
        lesson("Bohr’s atomic model", "Bohr proposed quantized energy levels. Electrons absorb or emit photons while moving between them.", "ΔE = hν", "Light is emitted when an electron moves to…", 0, "A lower energy level", "A higher energy level", "The nucleus", "Another atom"),
        lesson("Quantum numbers and orbitals", "Four quantum numbers describe an electron's energy level, orbital shape, orientation and spin.", "n, ℓ, mℓ, ms", "The maximum number of electrons in one orbital is…", 1, "1", "2", "6", "10")
    )),
    CurriculumModule("Periodic Table", "▦", Color(0xFF4D8BFF), "Read the table as a map of structure and chemical behavior.", listOf(
        lesson("Periods, groups and blocks", "Rows are periods, columns are groups, and s, p, d and f blocks reflect the valence subshell being filled.", "group → similar valence behavior", "Elements in one group usually share similar…", 2, "Mass numbers", "Neutron counts", "Chemical properties", "Nuclear sizes"),
        lesson("Electronic configuration", "Electrons fill lower-energy orbitals first while obeying Pauli exclusion and Hund's rule.", "1s → 2s → 2p → 3s", "The ground-state configuration of lithium ends in…", 0, "2s¹", "2p¹", "3s¹", "1p¹"),
        lesson("Atomic and ionic radius", "Atomic radius generally decreases across a period and increases down a group.", "cation < atom < anion", "Which is typically smaller than its neutral atom?", 1, "Anion", "Cation", "Isotope", "Noble gas"),
        lesson("Ionization energy", "Ionization energy is the energy needed to remove a gaseous electron and generally rises across a period.", "X(g) → X⁺(g) + e⁻", "Ionization energy is highest near which region?", 3, "Lower left", "Center", "Lower right", "Upper right"),
        lesson("Electronegativity and metallic character", "Electronegativity rises toward fluorine; metallic character rises toward francium.", "F = 3.98 on the Pauling scale", "The most electronegative element is…", 2, "Oxygen", "Chlorine", "Fluorine", "Nitrogen")
    )),
    CurriculumModule("Chemical Bonding", "⚭", Purple, "Connect electron behavior with bonds, shapes and properties.", listOf(
        lesson("Ionic bonding", "Electron transfer creates oppositely charged ions held in a three-dimensional electrostatic lattice.", "Na → Na⁺ + e⁻", "Ionic compounds usually conduct electricity when…", 2, "Solid only", "Cold", "Molten or dissolved", "Under vacuum"),
        lesson("Covalent bonding", "Covalent bonds form when atoms share electron pairs between their nuclei.", "bond order: single < double < triple", "A double covalent bond shares how many electron pairs?", 1, "One", "Two", "Three", "Four"),
        lesson("Metallic bonding", "Metal cations are attracted to mobile, delocalized valence electrons.", "metal ions + electron sea", "Metal conductivity is explained by…", 3, "Fixed protons", "Neutron flow", "Ionic molecules", "Mobile electrons"),
        lesson("Lewis structures and formal charge", "Lewis structures track valence electrons; preferred structures minimize formal charge.", "FC = valence − dots − bonds", "How many valence electrons does oxygen have?", 2, "2", "4", "6", "8"),
        lesson("VSEPR theory and molecular shapes", "Electron domains repel and arrange to maximize separation around a central atom.", "2 linear · 3 planar · 4 tetrahedral", "The molecular shape of methane is…", 3, "Bent", "Linear", "Trigonal planar", "Tetrahedral")
    )),
    CurriculumModule("States of Matter", "◫", Color(0xFF73D0FF), "Relate particles and forces to gases, liquids, solids and phases.", listOf(
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
    CurriculumModule("Chemical Reactions", "⇌", Orange, "Represent, classify and balance transformations of matter.", listOf(
        lesson("Writing and balancing equations", "Coefficients conserve each type of atom without changing the identity of any substance.", "2H₂ + O₂ → 2H₂O", "The balanced coefficient of O₂ in water formation is…", 0, "1", "2", "3", "4"),
        lesson("Combination and decomposition reactions", "Combination joins reactants into one product; decomposition splits one reactant into simpler products.", "A + B → AB · AB → A + B", "CaCO₃ → CaO + CO₂ is…", 2, "Combination", "Displacement", "Decomposition", "Neutralization"),
        lesson("Displacement and precipitation reactions", "Ions exchange partners; an insoluble product leaves solution as a precipitate.", "Ag⁺ + Cl⁻ → AgCl↓", "The downward arrow indicates…", 3, "A gas", "Heat", "A catalyst", "A precipitate"),
        lesson("Combustion reactions", "Complete hydrocarbon combustion in excess oxygen produces carbon dioxide, water and energy.", "CₓHᵧ + O₂ → CO₂ + H₂O", "Complete combustion requires sufficient…", 1, "Nitrogen", "Oxygen", "Hydrogen", "Chlorine"),
        lesson("Redox reactions", "Oxidation loses electrons while reduction gains them; both processes occur together.", "OIL RIG", "A species that gains electrons is…", 2, "Oxidized", "A catalyst", "Reduced", "Neutralized")
    )),
    CurriculumModule("Thermodynamics", "↯", Color(0xFFC456FF), "Follow heat, work, disorder and usable energy.", listOf(
        lesson("System, surroundings and state functions", "A system is the chosen matter under study; state functions depend only on initial and final states.", "universe = system + surroundings", "Which is a state function?", 0, "Enthalpy", "Path length", "Friction", "Process time"),
        lesson("Heat, work and internal energy", "The first law conserves energy: internal energy changes through heat and work.", "ΔU = q + w", "If q = +50 J and w = −20 J, ΔU is…", 1, "20 J", "30 J", "50 J", "70 J"),
        lesson("Enthalpy and calorimetry", "At constant pressure, enthalpy change equals heat transferred; calorimetry measures that heat.", "q = mcΔT", "An exothermic reaction has ΔH that is…", 2, "Zero", "Positive", "Negative", "Undefined"),
        lesson("Entropy and spontaneity", "Entropy measures energy dispersal and the number of accessible microscopic arrangements.", "ΔSᵤₙᵢᵥ > 0 for spontaneity", "Which state usually has greatest entropy?", 3, "Crystal", "Solid", "Liquid", "Gas"),
        lesson("Gibbs free energy", "Gibbs energy combines enthalpy and entropy to predict spontaneity at constant temperature and pressure.", "ΔG = ΔH − TΔS", "A spontaneous process has ΔG…", 0, "< 0", "> 0", "= 1", "Always = 0")
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
        lesson("Collision theory and activation energy", "Particles react only when they collide with sufficient energy and suitable orientation.", "k = Ae^(−Ea/RT)", "Higher temperature usually makes reactions…", 3, "Stop", "Reverse only", "Unchanged", "Faster"),
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
)
