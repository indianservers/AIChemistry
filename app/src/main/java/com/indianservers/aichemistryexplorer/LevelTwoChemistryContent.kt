package com.indianservers.aichemistryexplorer

/**
 * Authored Grades 9-10 lessons. The shared content shape lets lesson pages,
 * search, FAQs and knowledge checks reuse one reliable explanation.
 */
private fun levelTwo(
    summary: String,
    explanation: String,
    example: String,
    relationship: String,
    mistake: String,
    application: String,
    vararg connections: String
): LevelOneConceptContent {
    val completeExample = if (example.length >= 80) example else
        "$example Identify the given quantities or particles, apply the relationship, and check units and chemical meaning."
    val completeMistake = if (mistake.length >= 60) mistake else
        "$mistake Recheck the definition, assumptions and evidence before deciding."
    return LevelOneConceptContent(
        summary = summary,
        explanation = explanation,
        workedExample = completeExample,
        keyRelationship = relationship,
        commonMistake = completeMistake,
        application = application,
        connections = connections.toList()
    )
}

internal val levelTwoModuleIcons = mapOf(
    "Atomic Structure" to "ATOM",
    "Chemical Bonding" to "BOND",
    "Mole Concept" to "MOL",
    "States of Matter" to "STATE",
    "Solutions" to "SOL",
    "Acids Bases Salts" to "pH"
)

internal val levelTwoConceptContent: Map<String, LevelOneConceptContent> = mapOf(
    "Quantum numbers" to levelTwo(
        "Quantum numbers are four labels that describe the state of an electron in an atom.",
        "The principal number n identifies shell and energy, l identifies subshell shape, ml identifies orbital orientation, and ms identifies spin. Only allowed combinations describe possible electron states.",
        "Example: an electron in a 2p orbital has n = 2 and l = 1; ml can be -1, 0 or +1 and ms can be +1/2 or -1/2.",
        "n = 1,2,...; l = 0 to n-1; ml = -l to +l; ms = +/-1/2",
        "Do not assign l = 2 when n = 2; allowed l values must be smaller than n.",
        "Quantum numbers organize electron configurations and help interpret spectra and periodic behavior.",
        "Orbitals", "Electron configuration", "Pauli principle"
    ),
    "Orbitals" to levelTwo(
        "An orbital is a three-dimensional probability region that can hold at most two electrons.",
        "Orbitals are not fixed paths. The s subshell has one orbital, p has three, d has five and f has seven; their shapes and orientations affect bonding.",
        "Example: the three 2p orbitals have equal energy in an isolated atom and together can hold six electrons.",
        "maximum electrons in a subshell = 2(2l + 1)",
        "Do not confuse an orbital with a shell; a shell contains one or more subshells and many orbitals.",
        "Orbital shapes explain bond direction, molecular geometry, spectra and periodic trends.",
        "Probability clouds", "Subshells", "Hybridization"
    ),
    "Electron configuration" to levelTwo(
        "Electron configuration records how an atom's electrons occupy shells, subshells and orbitals.",
        "Electrons are placed in available low-energy states while obeying Aufbau filling, Hund's rule and the Pauli principle. Valence electrons largely control chemical behavior.",
        "Example: oxygen has configuration 1s2 2s2 2p4; its six valence electrons explain its common valency of two.",
        "total superscripts in a neutral atom = atomic number",
        "Do not exceed subshell capacities: s holds 2, p holds 6, d holds 10 and f holds 14 electrons.",
        "Configurations predict ion formation, bonding, magnetism and an element's position in the periodic table.",
        "Valence electrons", "Aufbau", "Periodic table"
    ),
    "Aufbau" to levelTwo(
        "The Aufbau principle states that electrons occupy lower-energy orbitals before higher-energy orbitals.",
        "The usual filling order follows increasing n + l; when two subshells have equal n + l, the one with lower n fills first. Some transition elements have exceptions.",
        "Example: potassium fills 4s before 3d, giving [Ar]4s1 because 4s is lower in energy for the neutral atom at that stage.",
        "lower orbital energy fills first; use the n + l rule",
        "Do not treat the diagonal filling order as exception-free, especially for chromium and copper.",
        "Aufbau filling helps construct ground-state configurations and understand periodic organization.",
        "Orbital energy", "Electron configuration", "Configuration exceptions"
    ),
    "Hund rule" to levelTwo(
        "Hund's rule states that electrons occupy equal-energy orbitals singly with parallel spins before pairing.",
        "Spreading electrons across degenerate orbitals reduces repulsion and gives a lower-energy arrangement.",
        "Example: nitrogen's three 2p electrons occupy the three p orbitals separately with parallel spins rather than pairing in one orbital.",
        "degenerate orbitals: single occupation before pairing",
        "Do not pair electrons in one p orbital while another orbital of the same subshell is empty.",
        "Hund's rule predicts unpaired electrons and therefore helps explain magnetic behavior.",
        "Degenerate orbitals", "Paramagnetism", "Pauli principle"
    ),
    "Pauli principle" to levelTwo(
        "The Pauli exclusion principle states that no two electrons in one atom have the same four quantum numbers.",
        "Therefore one orbital holds at most two electrons, and those two must have opposite spins.",
        "Example: a filled 1s orbital is written with one spin-up and one spin-down electron; a third electron must enter 2s.",
        "one orbital -> maximum 2 electrons with opposite spins",
        "Pauli does not say electrons must remain unpaired; it restricts how paired electrons occupy one orbital.",
        "The principle determines orbital capacity, configurations and the structure of the periodic table.",
        "Quantum numbers", "Electron spin", "Orbital capacity"
    ),

    "Ionic bond" to levelTwo(
        "An ionic bond is the electrostatic attraction between oppositely charged ions.",
        "Electron transfer commonly creates cations and anions, which assemble into a three-dimensional lattice rather than separate pairs. Strong attractions give high melting points.",
        "Example: sodium loses one electron to form Na+, chlorine gains it to form Cl-, and the ions form an NaCl lattice.",
        "electrostatic attraction increases with ionic charge and decreases with distance",
        "Do not describe an ionic bond as a shared electron pair or NaCl as a single isolated molecule.",
        "Ionic materials are used in salts, ceramics, electrolytes, batteries and fertilizers.",
        "Ion formation", "Crystal lattice", "Electrolytes"
    ),
    "Covalent bond" to levelTwo(
        "A covalent bond forms when atoms share one or more pairs of electrons.",
        "Shared electron density attracts both nuclei. Single, double and triple bonds differ in shared pairs, length and strength.",
        "Example: in H2, each hydrogen contributes one electron to a shared pair, allowing both atoms to achieve a stable duet.",
        "bond order: single < double < triple",
        "Sharing is not always perfectly equal; electronegativity differences can make a covalent bond polar.",
        "Covalent bonding creates molecules, polymers, biomolecules and network solids.",
        "Electronegativity", "Lewis structures", "Bond polarity"
    ),
    "Coordinate bond" to levelTwo(
        "A coordinate covalent bond is a shared pair for which both electrons are donated by one atom.",
        "A lone-pair donor acts as a Lewis base and an electron-pair acceptor acts as a Lewis acid. After formation, the bond behaves like another covalent bond.",
        "Example: NH3 donates its nitrogen lone pair to H+ to form NH4+, represented initially by an arrow N -> H.",
        "lone-pair donor -> electron-pair acceptor",
        "Do not assume the electrons remain the property of the donor after the bond has formed.",
        "Coordinate bonds occur in metal complexes, ammonium salts and biological molecules.",
        "Lewis acids and bases", "Lone pairs", "Coordination compounds"
    ),
    "Metallic bond" to levelTwo(
        "Metallic bonding is attraction between positive metal cores and delocalized valence electrons.",
        "The mobile electron cloud extends through the solid, explaining electrical and thermal conductivity, lustre and the ability of layers to slide without shattering.",
        "Example: copper conducts because delocalized electrons move through its lattice when a potential difference is applied.",
        "metal cation lattice + delocalized electrons",
        "Metallic bonding is not a collection of separate molecules or fixed electron pairs between two atoms.",
        "Metallic bonding underpins wires, alloys, structural metals and electronic contacts.",
        "Delocalization", "Conductivity", "Alloys"
    ),
    "Hydrogen bond" to levelTwo(
        "A hydrogen bond is a strong intermolecular attraction involving H bonded to N, O or F and a lone pair on N, O or F.",
        "The highly polar X-H bond exposes a partially positive hydrogen that attracts an electronegative atom on the same or another molecule.",
        "Example: hydrogen bonds between water molecules raise water's boiling point compared with similarly sized molecules.",
        "X-H...Y, where X and Y are usually N, O or F",
        "A hydrogen bond is not the covalent O-H or N-H bond inside the molecule.",
        "Hydrogen bonding controls water properties, DNA pairing, protein shape and material solubility.",
        "Polarity", "Intermolecular forces", "Boiling point"
    ),
    "Van der Waals forces" to levelTwo(
        "Van der Waals forces are weak attractions between atoms or molecules caused by permanent or temporary dipoles.",
        "They include London dispersion forces in all particles and dipole-dipole attractions in polar molecules. Their combined effect grows with polarizability and contact area.",
        "Example: iodine is solid at room temperature because its large electron clouds produce stronger dispersion forces than those in chlorine.",
        "greater polarizability and surface contact -> stronger dispersion attraction",
        "Weak individually does not mean unimportant; many attractions together can strongly affect properties.",
        "These forces influence boiling points, liquefaction, molecular recognition and adhesion.",
        "London dispersion", "Dipole-dipole forces", "Polarizability"
    ),
    "Lewis structures" to levelTwo(
        "Lewis structures show valence electrons as bonding pairs and lone pairs.",
        "A reliable structure counts all valence electrons, connects atoms, completes outer shells where possible and checks formal charges.",
        "Example: CO2 is drawn O=C=O with two lone pairs on each oxygen; all atoms reach octets and formal charges are zero.",
        "formal charge = valence electrons - lone electrons - number of bonds",
        "Do not add or remove electrons without accounting for the overall charge of the species.",
        "Lewis structures help predict bonding, resonance, geometry, polarity and reactivity.",
        "Octet rule", "Formal charge", "Resonance"
    ),
    "VSEPR" to levelTwo(
        "VSEPR theory predicts shape by arranging electron domains to minimize repulsion around a central atom.",
        "Bonding pairs and lone pairs count as domains; lone pairs repel more strongly and can compress bond angles.",
        "Example: NH3 has four electron domains but one lone pair, giving tetrahedral electron geometry and trigonal-pyramidal molecular shape.",
        "electron domains maximize separation",
        "Do not count a double bond as two domains or include lone pairs as atoms in the molecular-shape name.",
        "VSEPR predicts shapes that control polarity, biological binding and material behavior.",
        "Electron domains", "Lone-pair repulsion", "Molecular geometry"
    ),
    "Hybridization" to levelTwo(
        "Hybridization is a model that mixes atomic orbitals on one atom into directed bonding orbitals.",
        "Common sets are sp for two domains, sp2 for three and sp3 for four. The model connects Lewis structures with sigma-bond geometry.",
        "Example: carbon in methane is described as sp3 hybridized, giving four equivalent orbitals directed tetrahedrally.",
        "steric number 2 -> sp; 3 -> sp2; 4 -> sp3",
        "Hybridization is a bonding model, not a physical stirring of orbitals, and pi bonds use unhybridized orbitals.",
        "Hybridization helps explain geometry, bond angles and multiple bonding in organic molecules.",
        "Sigma and pi bonds", "Steric number", "Molecular geometry"
    ),
    "Molecular geometry" to levelTwo(
        "Molecular geometry is the three-dimensional arrangement of atoms in a molecule.",
        "It depends on electron-domain arrangement, lone pairs and multiple bonds. Shape and bond polarity together determine molecular polarity.",
        "Example: CO2 is linear and nonpolar because equal C=O bond dipoles cancel, while bent H2O is polar.",
        "molecular polarity = bond polarity + three-dimensional symmetry",
        "Do not decide whole-molecule polarity from one polar bond without considering shape.",
        "Geometry affects boiling point, smell, drug action, reaction pathway and material properties.",
        "VSEPR", "Bond angles", "Molecular polarity"
    ),

    "Avogadro number" to levelTwo(
        "Avogadro's number is the number of specified particles in one mole: exactly 6.02214076 x 10^23 mol-1.",
        "It links the microscopic count of atoms, molecules or ions to laboratory-scale amounts.",
        "Example: 0.50 mol of oxygen molecules contains 0.50 x 6.022 x 10^23 = 3.011 x 10^23 O2 molecules.",
        "number of particles = moles x Avogadro constant",
        "Always state the entity being counted; one mole of O atoms differs from one mole of O2 molecules.",
        "The constant connects measured masses with particle counts in reactions and materials.",
        "Moles", "Particles", "Molar mass"
    ),
    "Moles" to levelTwo(
        "The mole is the SI unit for amount of substance.",
        "One mole contains Avogadro's number of specified entities. Molar mass converts between amount in moles and measured mass.",
        "Example: 18.0 g of water divided by its molar mass, 18.0 g/mol, equals 1.00 mol H2O.",
        "moles = mass / molar mass",
        "Do not use grams directly as equation coefficients; balanced equations compare amounts in moles.",
        "Moles allow chemists to scale reactions, formulations and material compositions.",
        "Avogadro number", "Molar mass", "Stoichiometry"
    ),
    "Stoichiometry" to levelTwo(
        "Stoichiometry uses a balanced equation to calculate quantitative relationships between reactants and products.",
        "Coefficients give mole ratios. Convert the known quantity to moles, apply the ratio, then convert to the requested unit.",
        "Example: 2H2 + O2 -> 2H2O shows that 3.0 mol O2 can react with 6.0 mol H2 to form 6.0 mol H2O.",
        "moles wanted = moles known x coefficient wanted / coefficient known",
        "Never calculate from an unbalanced equation or treat coefficients as mass ratios.",
        "Stoichiometry controls industrial batches, medicine preparation, fuels and laboratory synthesis.",
        "Balanced equations", "Mole ratios", "Limiting reagent"
    ),
    "Empirical formula" to levelTwo(
        "An empirical formula gives the simplest whole-number ratio of elements in a compound.",
        "Convert each element's mass or percentage to moles, divide by the smallest amount and scale to whole numbers.",
        "Example: 40.0% C, 6.7% H and 53.3% O gives mole ratio about 1:2:1, so the empirical formula is CH2O.",
        "empirical subscripts = simplest mole ratio",
        "Do not use mass ratios directly as subscripts; convert each mass to moles first.",
        "Empirical formulas help identify unknown substances from composition data.",
        "Percentage composition", "Mole ratios", "Molecular formula"
    ),
    "Molecular formula" to levelTwo(
        "A molecular formula gives the actual number of each type of atom in one molecule.",
        "It is a whole-number multiple of the empirical formula, found by comparing molar mass with empirical-formula mass.",
        "Example: CH2O has empirical mass 30 g/mol; if the compound's molar mass is 180 g/mol, n = 6 and the formula is C6H12O6.",
        "molecular formula = (empirical formula)n; n = molar mass / empirical mass",
        "The multiplier n must be a positive whole number within experimental uncertainty.",
        "Molecular formulas support substance identification, structure determination and reaction calculations.",
        "Empirical formula", "Molar mass", "Composition"
    ),
    "Limiting reagent" to levelTwo(
        "The limiting reagent is the reactant consumed first and therefore sets the maximum product amount.",
        "Compare each reactant in moles relative to its balanced coefficient. The other reactant is present in excess.",
        "Example: for 2H2 + O2 -> 2H2O, 3 mol H2 needs 1.5 mol O2; if only 1 mol O2 is available, O2 is limiting.",
        "smallest value of moles/coefficient identifies the limiting reagent",
        "Do not choose the reactant with the smaller mass without converting to moles and using coefficients.",
        "Limiting-reagent control reduces waste and predicts output in manufacturing and synthesis.",
        "Stoichiometry", "Excess reagent", "Theoretical yield"
    ),
    "Percentage yield" to levelTwo(
        "Percentage yield compares the product actually obtained with the theoretical maximum.",
        "Actual yield is measured; theoretical yield is calculated from the limiting reagent. Losses and side reactions usually make the actual yield lower.",
        "Example: if theoretical yield is 10.0 g and 8.5 g is collected, percentage yield = 8.5/10.0 x 100 = 85%.",
        "percentage yield = actual yield / theoretical yield x 100",
        "Use the limiting reagent to calculate theoretical yield, and do not confuse yield with purity.",
        "Yield measures process efficiency in laboratories, pharmaceuticals and chemical plants.",
        "Theoretical yield", "Limiting reagent", "Process efficiency"
    ),

    "Gas laws" to levelTwo(
        "Gas laws describe relationships among pressure, volume, temperature and amount.",
        "Boyle's law applies at constant temperature, Charles's law at constant pressure and Avogadro's law at fixed temperature and pressure. Temperature must be absolute.",
        "Example: at constant temperature, compressing a gas from 2.0 L to 1.0 L doubles its pressure because P1V1 = P2V2.",
        "P1V1/T1 = P2V2/T2 for fixed amount",
        "Never substitute Celsius temperature directly into gas-law ratios; convert it to kelvin.",
        "Gas laws guide tyres, syringes, weather balloons, breathing and gas storage.",
        "Boyle law", "Charles law", "Pressure"
    ),
    "Ideal gas equation" to levelTwo(
        "The ideal gas equation combines pressure, volume, amount and absolute temperature.",
        "PV = nRT models particles with negligible volume and no intermolecular attractions. Real gases approach ideal behavior at low pressure and high temperature.",
        "Example: one mole of ideal gas at 273.15 K and 1 atm occupies about 22.4 L using R = 0.08206 L atm mol-1 K-1.",
        "PV = nRT",
        "Choose an R value consistent with the pressure and volume units, and always use kelvin.",
        "The equation estimates gas amounts, molar masses, densities and process conditions.",
        "Gas laws", "Molar volume", "Real gases"
    ),
    "Kinetic theory" to levelTwo(
        "Kinetic molecular theory explains gas properties using constant random particle motion.",
        "Ideal particles collide elastically, have negligible individual volume and exert pressure through wall collisions. Average kinetic energy depends on absolute temperature.",
        "Example: heating a sealed rigid container makes particles move faster and strike the walls more forcefully, raising pressure.",
        "average translational kinetic energy is proportional to kelvin temperature",
        "Equal temperature means equal average kinetic energy, not equal molecular speed for different gases.",
        "Kinetic theory explains diffusion, pressure, temperature and gas-law behavior.",
        "Particle motion", "Temperature", "Diffusion"
    ),
    "Liquids" to levelTwo(
        "A liquid has fixed volume but no fixed shape because its particles remain close while moving past one another.",
        "Intermolecular attractions give liquids measurable viscosity, surface tension and vapour pressure.",
        "Example: water flows to match a container but remains nearly incompressible because its molecules are close together.",
        "liquid properties depend on intermolecular forces and temperature",
        "Liquids are not particles with no attraction; their particles are close and interact strongly enough to maintain volume.",
        "Liquid properties matter in fuels, lubricants, medicines, coatings and fluid transport.",
        "Intermolecular forces", "Viscosity", "Vapour pressure"
    ),
    "Surface tension" to levelTwo(
        "Surface tension is the energy required to increase a liquid's surface area.",
        "Surface molecules experience a net inward attraction, so the surface tends to minimize area. Stronger cohesive forces generally produce higher surface tension.",
        "Example: small water drops become nearly spherical because a sphere has minimum surface area for its volume.",
        "surface tension = force/length = energy/area",
        "Surface tension is not a solid skin; it results from unbalanced molecular attractions at an interface.",
        "Surface tension affects detergents, capillary tubes, inkjet printing and biological droplets.",
        "Cohesion", "Capillary action", "Surfactants"
    ),
    "Viscosity" to levelTwo(
        "Viscosity is a fluid's resistance to flow.",
        "Stronger intermolecular attractions and molecular entanglement usually increase viscosity, while heating most liquids lowers it.",
        "Example: honey flows more slowly than water because it has greater viscosity; warming honey makes it flow faster.",
        "greater internal friction -> greater viscosity",
        "Do not confuse viscosity with density; a dense liquid is not necessarily highly viscous.",
        "Viscosity controls lubricants, paints, food texture, blood flow and pipeline design.",
        "Intermolecular forces", "Temperature", "Fluid flow"
    ),
    "Vapour pressure" to levelTwo(
        "Vapour pressure is the pressure of vapour in dynamic equilibrium with its liquid at a given temperature.",
        "Faster molecules escape the surface while vapour molecules condense. Higher temperature raises vapour pressure; stronger attractions lower it.",
        "Example: a liquid boils when its vapour pressure equals external pressure, so water boils below 100 degrees C at high altitude.",
        "boiling occurs when vapour pressure = external pressure",
        "Vapour pressure is not the same as atmospheric pressure and applies even below the boiling point.",
        "Vapour pressure informs distillation, fuel storage, drying and solvent safety.",
        "Evaporation", "Dynamic equilibrium", "Boiling point"
    ),
    "Solids" to levelTwo(
        "Solids have fixed shape and volume because their particles occupy stable positions.",
        "Crystalline solids possess long-range repeating order, whereas amorphous solids lack a regular extended lattice. Bonding determines hardness, conductivity and melting behavior.",
        "Example: sodium chloride forms an ordered ionic crystal, while ordinary glass is an amorphous solid.",
        "solid properties = particle type + bonding + arrangement",
        "Not every solid is crystalline, and particles in solids still vibrate.",
        "Understanding solids supports design of metals, ceramics, polymers, electronics and medicines.",
        "Crystalline solids", "Amorphous solids", "Material properties"
    ),
    "Crystal lattice" to levelTwo(
        "A crystal lattice is a regular three-dimensional array of repeating points representing particle positions.",
        "A unit cell is the smallest repeating portion. Lattice arrangement and bonding control density, cleavage, strength and other bulk properties.",
        "Example: in rock salt, Na+ and Cl- occupy alternating positions in a cubic repeating structure.",
        "crystal = unit cell repeated in three dimensions",
        "A lattice point represents an equivalent environment, not necessarily one single atom.",
        "Crystal lattices are central to minerals, semiconductors, salts and pharmaceutical solids.",
        "Unit cells", "Ionic solids", "Crystal structure"
    ),

    "Types" to levelTwo(
        "Solutions can be classified by physical state, solvent-solute combination and degree of saturation.",
        "They may be gaseous, liquid or solid and may be unsaturated, saturated or supersaturated. A true solution is homogeneous at the particle scale.",
        "Example: air is a gas solution, salt water is a solid-in-liquid solution and brass is a solid solution.",
        "solution = solvent + one or more solutes",
        "A solution does not have to be a liquid, and a suspension is not a true homogeneous solution.",
        "Classifying solutions helps select preparation, storage and separation methods.",
        "Solvent and solute", "Saturation", "Homogeneous mixtures"
    ),
    "Solubility" to levelTwo(
        "Solubility is the maximum amount of solute that dissolves in a stated amount of solvent at specified conditions.",
        "It depends on solute-solvent interactions, temperature and, for gases, pressure. A saturated solution is in dynamic equilibrium with undissolved solute.",
        "Example: if at most 36 g NaCl dissolves in 100 g water at a temperature, adding 40 g leaves about 4 g undissolved.",
        "solubility = maximum dissolved solute at stated temperature and pressure",
        "Do not confuse dissolving rate with solubility; stirring speeds dissolving but may not change the final maximum.",
        "Solubility guides crystallization, medicines, water treatment and formulation.",
        "Saturation", "Temperature effects", "Intermolecular forces"
    ),
    "Concentration" to levelTwo(
        "Concentration expresses how much solute is present relative to a defined amount of solution or solvent.",
        "Different units suit different purposes, including mass percent, molarity, molality, mole fraction and ppm.",
        "Example: dissolving 5 g salt to make 100 g solution gives a mass percentage of 5%.",
        "mass percent = mass solute / mass solution x 100",
        "Always identify whether the denominator is solution, solvent, volume or mass.",
        "Concentration controls medicine doses, reaction rates, product quality and pollution limits.",
        "Molarity", "Molality", "Parts per million"
    ),
    "Molarity" to levelTwo(
        "Molarity is the number of moles of solute per litre of solution.",
        "Because solution volume changes with temperature, molarity is temperature dependent. Volumetric glassware is used to prepare accurate molar solutions.",
        "Example: 0.50 mol NaCl made up to 2.00 L of solution has molarity 0.250 mol/L.",
        "M = moles of solute / litres of solution",
        "Use final solution volume, not the initial solvent volume, and convert millilitres to litres.",
        "Molarity is used in reaction calculations, titrations and laboratory preparation.",
        "Moles", "Volumetric flask", "Dilution"
    ),
    "Molality" to levelTwo(
        "Molality is the number of moles of solute per kilogram of solvent.",
        "It depends on mass rather than volume and therefore does not change with temperature. It is useful for colligative properties.",
        "Example: 0.20 mol glucose dissolved in 0.50 kg water gives molality 0.40 mol/kg.",
        "m = moles of solute / kilograms of solvent",
        "The denominator is mass of solvent, not mass of the whole solution.",
        "Molality is used for boiling-point elevation, freezing-point depression and precise thermal work.",
        "Colligative properties", "Moles", "Concentration units"
    ),
    "Normality" to levelTwo(
        "Normality is the number of reactive equivalents of solute per litre of solution.",
        "Its value depends on the reaction being considered because an equivalent may represent transferable H+, OH- or electrons.",
        "Example: 1.0 M H2SO4 is 2.0 N for complete acid-base neutralization because each mole supplies two moles of H+.",
        "N = equivalents of solute / litres of solution",
        "Normality is reaction dependent; do not quote it without specifying the chemical process.",
        "Normality is still encountered in titration, industrial analysis and water testing.",
        "Equivalent factor", "Titration", "Molarity"
    ),
    "Mole fraction" to levelTwo(
        "Mole fraction is the fraction of total moles contributed by one component.",
        "It is dimensionless, lies between zero and one, and all component mole fractions sum to one.",
        "Example: a mixture of 2 mol ethanol and 3 mol water has ethanol mole fraction 2/(2+3) = 0.40.",
        "xi = ni / total moles; sum xi = 1",
        "Use moles rather than masses directly, and include every component in the total.",
        "Mole fraction is used in gas mixtures, vapour-liquid equilibrium and solution thermodynamics.",
        "Mixtures", "Partial pressure", "Composition"
    ),
    "ppm" to levelTwo(
        "Parts per million expresses one part of a component per one million parts of mixture.",
        "For dilute water solutions, 1 mg/L is approximately 1 ppm when density is close to 1 kg/L.",
        "Example: 3 mg fluoride in 1 kg of water corresponds approximately to 3 ppm by mass.",
        "ppm = amount solute / amount solution x 10^6",
        "Do not use the mg/L approximation when density differs greatly from water or high accuracy is required.",
        "ppm reports trace pollutants, minerals, additives and exposure limits.",
        "Trace concentration", "Mass fraction", "Environmental analysis"
    ),
    "Dilution" to levelTwo(
        "Dilution lowers concentration by adding solvent without changing the amount of solute.",
        "For the same solute before and after dilution, initial moles equal final moles.",
        "Example: 25.0 mL of 2.0 M stock diluted to 100.0 mL gives M2 = 2.0 x 25.0/100.0 = 0.50 M.",
        "M1V1 = M2V2",
        "Adding solvent changes concentration and volume but not moles of the original solute.",
        "Dilution prepares standards, medicines, cleaners and safe laboratory reagents.",
        "Molarity", "Volumetric glassware", "Stock solutions"
    ),

    "Arrhenius" to levelTwo(
        "An Arrhenius acid increases H3O+ concentration in water, while an Arrhenius base increases OH- concentration.",
        "The definition clearly explains many aqueous neutralizations but is limited to water-based systems.",
        "Example: HCl forms H3O+ in water, while NaOH dissociates to provide OH-; together they form water and NaCl.",
        "acid in water -> H3O+; base in water -> OH-",
        "A substance need not contain OH in its formula to act as a base in broader acid-base theories.",
        "The Arrhenius model explains common laboratory acids, alkalis and neutralization.",
        "Aqueous ions", "Neutralization", "Bronsted theory"
    ),
    "Bronsted" to levelTwo(
        "A Bronsted-Lowry acid donates a proton and a Bronsted-Lowry base accepts a proton.",
        "Each acid forms a conjugate base after donating H+, and each base forms a conjugate acid after accepting H+.",
        "Example: NH3 + H2O <-> NH4+ + OH-; NH3 accepts H+ and is the base, while water donates H+ and is the acid.",
        "acid + base <-> conjugate base + conjugate acid",
        "Identify proton transfer, not simply the presence of H or OH in a formula.",
        "This theory explains buffers, biological proton transfer and non-hydroxide bases.",
        "Conjugate pairs", "Proton transfer", "Amphiprotic species"
    ),
    "Lewis" to levelTwo(
        "A Lewis acid accepts an electron pair and a Lewis base donates an electron pair.",
        "This broad definition includes reactions without proton transfer, such as bond formation with electron-deficient molecules or metal ions.",
        "Example: BF3 accepts a lone pair from NH3, so BF3 is a Lewis acid and NH3 is a Lewis base.",
        "Lewis base electron pair -> Lewis acid",
        "Do not reverse donor and acceptor: the base donates the electron pair.",
        "Lewis theory explains coordination compounds, catalysts and many organic mechanisms.",
        "Coordinate bonds", "Electrophiles", "Lone pairs"
    ),
    "pH" to levelTwo(
        "pH is a logarithmic measure related to hydronium-ion concentration.",
        "At school level, pH below 7 is acidic, 7 is neutral and above 7 is basic for water near room temperature. Each pH unit represents a tenfold concentration change.",
        "Example: if [H3O+] = 1.0 x 10^-3 mol/L, pH = -log(1.0 x 10^-3) = 3.00.",
        "pH = -log10[H3O+]",
        "pH 3 is ten times more acidic by hydronium concentration than pH 4, not merely one unit more.",
        "pH control is vital in blood, soil, food, medicines, pools and wastewater.",
        "Hydronium concentration", "Logarithms", "Acid strength"
    ),
    "Buffers" to levelTwo(
        "A buffer resists large pH changes when small amounts of acid or base are added.",
        "It commonly contains a weak acid and its conjugate base, which consume added OH- and H3O+ respectively. Buffer capacity is limited.",
        "Example: an ethanoic acid/ethanoate buffer uses CH3COOH to neutralize added OH- and CH3COO- to consume added H3O+.",
        "pH approximately pKa + log([base]/[acid])",
        "A buffer does not keep pH perfectly fixed and fails when too much acid or base is added.",
        "Buffers stabilize blood, medicines, foods, fermentation and analytical solutions.",
        "Conjugate pairs", "Weak acids", "Buffer capacity"
    ),
    "Indicators" to levelTwo(
        "Acid-base indicators are weak acids or bases whose conjugate forms have different colours.",
        "Changing pH shifts the balance between the two coloured forms. Each indicator changes over a characteristic pH range.",
        "Example: phenolphthalein is colourless in acidic solution and pink in sufficiently basic solution.",
        "HIn <-> H+ + In-; the two forms have different colours",
        "An indicator does not cause neutralization and its endpoint range must suit the reaction.",
        "Indicators locate titration endpoints and provide quick pH tests in laboratories and products.",
        "Equilibrium", "Titration", "pH range"
    ),
    "Hydrolysis" to levelTwo(
        "Salt hydrolysis is the reaction of a salt ion with water to produce H3O+ or OH-.",
        "Ions that are conjugates of weak acids or bases can shift solution pH, while ions from strong acids and strong bases usually have little effect.",
        "Example: acetate reacts as CH3COO- + H2O <-> CH3COOH + OH-, making sodium acetate solution basic.",
        "conjugate base + H2O <-> weak acid + OH-",
        "Do not assume every salt solution is neutral; the parent acid and base strengths matter.",
        "Hydrolysis helps predict soil, medicine and industrial-solution pH.",
        "Conjugate acids and bases", "Salt pH", "Equilibrium"
    ),
    "Salt chemistry" to levelTwo(
        "Salts are ionic compounds produced when acid hydrogen is replaced by a metal or ammonium ion.",
        "They may be prepared by neutralization, precipitation or reactions of acids with metals, oxides or carbonates. Solubility and hydrolysis determine their behavior.",
        "Example: H2SO4 + 2NaOH -> Na2SO4 + 2H2O prepares sodium sulfate by neutralization.",
        "acid + base -> salt + water",
        "Not every salt is neutral in solution, and 'salt' in chemistry includes far more than sodium chloride.",
        "Salts are used in fertilizers, medicines, food, water treatment, construction and manufacturing.",
        "Salt preparation", "Solubility rules", "Hydrolysis"
    )
)
