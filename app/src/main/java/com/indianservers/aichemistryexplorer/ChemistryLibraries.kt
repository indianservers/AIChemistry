package com.indianservers.aichemistryexplorer

import androidx.compose.ui.graphics.Color

internal data class MoleculeEntry(
    val chapter: String,
    val name: String,
    val formula: String,
    val molarMass: String,
    val geometry: String,
    val bonding: String,
    val learningPurpose: String,
    val layout: String,
    val theory: String = learningPurpose,
    val exampleChemistry: String = "Use its formula and structure to predict polarity, intermolecular forces and chemical behavior.",
    val uses: String = "Used as a representative example when learning this molecular family.",
    val related: List<String> = emptyList()
)

internal val moleculeLibrary by lazy { listOf(
    MoleculeEntry("Foundations", "Hydrogen", "H₂", "2.016 g/mol", "Linear diatomic", "H–H single σ bond", "Introduces covalent bonding and bond dissociation.", "diatomic"),
    MoleculeEntry("Foundations", "Oxygen", "O₂", "31.998 g/mol", "Linear diatomic", "O=O bond; triplet ground state", "Connects bond order with magnetism and respiration.", "diatomic"),
    MoleculeEntry("Foundations", "Nitrogen", "N₂", "28.014 g/mol", "Linear diatomic", "N≡N triple bond", "Explains the stability of atmospheric nitrogen.", "diatomic"),
    MoleculeEntry("Foundations", "Water", "H₂O", "18.015 g/mol", "Bent · about 104.5°", "Two O–H σ bonds; two lone pairs", "Links molecular shape, polarity and hydrogen bonding.", "bent"),
    MoleculeEntry("Foundations", "Carbon dioxide", "CO₂", "44.009 g/mol", "Linear · 180°", "O=C=O; two C=O double bonds", "Shows how polar bonds can form a non-polar molecule.", "linear3"),

    MoleculeEntry("VSEPR", "Ammonia", "NH₃", "17.031 g/mol", "Trigonal pyramidal · about 107°", "Three N–H bonds; one lone pair", "Demonstrates lone-pair compression and basicity.", "pyramidal"),
    MoleculeEntry("VSEPR", "Methane", "CH₄", "16.043 g/mol", "Tetrahedral · 109.5°", "Four equivalent C–H σ bonds", "Introduces tetrahedral carbon and sp³ bonding.", "tetrahedral"),
    MoleculeEntry("VSEPR", "Boron trifluoride", "BF₃", "67.806 g/mol", "Trigonal planar · 120°", "Three B–F bonds; electron-deficient boron", "Introduces incomplete octets and Lewis acidity.", "trigonal"),
    MoleculeEntry("VSEPR", "Phosphorus pentachloride", "PCl₅", "208.22 g/mol", "Trigonal bipyramidal", "Three equatorial and two axial P–Cl bonds", "Compares axial and equatorial positions.", "bipyramidal"),
    MoleculeEntry("VSEPR", "Sulfur hexafluoride", "SF₆", "146.06 g/mol", "Octahedral · 90°", "Six equivalent S–F bonds", "Demonstrates octahedral symmetry.", "octahedral"),
    MoleculeEntry("VSEPR", "Xenon tetrafluoride", "XeF₄", "207.28 g/mol", "Square planar", "Four Xe–F bonds; two trans lone pairs", "Shows how lone pairs produce square-planar geometry.", "square"),

    MoleculeEntry("Organic", "Ethane", "C₂H₆", "30.070 g/mol", "Tetrahedral at each carbon", "C–C and C–H σ bonds", "Introduces conformational rotation around a single bond.", "ethane"),
    MoleculeEntry("Organic", "Ethene", "C₂H₄", "28.054 g/mol", "Planar at each carbon", "C=C σ+π double bond", "Explains restricted rotation and sp² bonding.", "ethene"),
    MoleculeEntry("Organic", "Ethyne", "C₂H₂", "26.038 g/mol", "Linear at each carbon", "C≡C σ+2π triple bond", "Introduces sp hybridization and linear carbon.", "linear4"),
    MoleculeEntry("Organic", "Methanol", "CH₃OH", "32.042 g/mol", "Tetrahedral carbon; bent oxygen", "C–O and O–H polar bonds", "Connects functional groups with polarity.", "methanol"),
    MoleculeEntry("Organic", "Ethanol", "C₂H₅OH", "46.069 g/mol", "Tetrahedral carbons; bent oxygen", "C–C, C–O and O–H σ bonds", "Introduces alcohol structure and hydrogen bonding.", "ethanol"),
    MoleculeEntry("Organic", "Methanal", "CH₂O", "30.026 g/mol", "Trigonal planar carbon", "Polar C=O carbonyl bond", "Introduces carbonyl geometry and electrophilicity.", "trigonal"),
    MoleculeEntry("Organic", "Ethanoic acid", "CH₃COOH", "60.052 g/mol", "Tetrahedral methyl; planar carboxyl", "Resonance-stabilized carboxyl group", "Links structure with acidity and dimerization.", "ethanol"),
    MoleculeEntry("Organic", "Benzene", "C₆H₆", "78.114 g/mol", "Planar hexagonal ring", "Six delocalized π electrons", "Introduces aromaticity and resonance.", "ring"),

    MoleculeEntry("Biochemistry", "Urea", "CH₄N₂O", "60.056 g/mol", "Approximately planar around carbonyl carbon", "Resonance across O=C(NH₂)₂", "Connects resonance with nitrogen metabolism.", "trigonal"),
    MoleculeEntry("Biochemistry", "Glycine", "C₂H₅NO₂", "75.067 g/mol", "Tetrahedral α-carbon", "Amino and carboxyl functional groups", "Introduces amino acids and zwitterions.", "ethanol"),
    MoleculeEntry("Biochemistry", "Lactic acid", "C₃H₆O₃", "90.078 g/mol", "Tetrahedral stereogenic carbon", "Hydroxyl and carboxyl groups", "Links stereochemistry with metabolism.", "ethanol"),
    MoleculeEntry("Biochemistry", "Glucose", "C₆H₁₂O₆", "180.156 g/mol", "Mostly cyclic pyranose in solution", "Multiple O–H groups and a hemiacetal", "Introduces carbohydrates and ring–chain equilibrium.", "ring")
).map { entry ->
    entry.copy(
        exampleChemistry = originalMoleculeExamples[entry.name]
            ?: "Compare ${entry.name}'s structure with another member of ${entry.chapter} to predict polarity and reactivity.",
        uses = originalMoleculeUses[entry.name]
            ?: "A key teaching and research example in ${entry.chapter.lowercase()} chemistry.",
        related = originalMoleculeRelations[entry.name].orEmpty()
    )
} + expandedMoleculeLibrary }

private val originalMoleculeExamples = mapOf(
    "Hydrogen" to "2H₂ + O₂ → 2H₂O illustrates combustion, redox and bond-energy release.",
    "Oxygen" to "O₂ accepts electrons in respiration and supports combustion to form oxides.",
    "Nitrogen" to "N₂ + 3H₂ ⇌ 2NH₃ shows how a strong triple bond affects industrial reaction conditions.",
    "Water" to "H₂O + H₂O ⇌ H₃O⁺ + OH⁻ connects polarity, proton transfer and pH.",
    "Carbon dioxide" to "CO₂ + H₂O ⇌ H₂CO₃ links molecular geometry with carbonate equilibria.",
    "Ammonia" to "NH₃ + H₂O ⇌ NH₄⁺ + OH⁻ demonstrates Brønsted basicity.",
    "Methane" to "CH₄ + 2O₂ → CO₂ + 2H₂O is the simplest hydrocarbon combustion.",
    "Boron trifluoride" to "BF₃ + NH₃ → F₃B←NH₃ forms a Lewis acid–base adduct.",
    "Phosphorus pentachloride" to "PCl₅ ⇌ PCl₃ + Cl₂ shows temperature-dependent dissociation.",
    "Sulfur hexafluoride" to "Its six equivalent bonds and octahedral symmetry make the molecular dipole zero.",
    "Xenon tetrafluoride" to "XeF₄ hydrolysis and fluorination chemistry demonstrate noble-gas reactivity.",
    "Ethane" to "Rotation about C–C produces staggered and eclipsed conformations.",
    "Ethene" to "C₂H₄ + H₂ → C₂H₆ demonstrates addition across a π bond.",
    "Ethyne" to "HC≡CH + 2H₂ → CH₃CH₃ shows stepwise hydrogenation of a triple bond.",
    "Methanol" to "2CH₃OH + 3O₂ → 2CO₂ + 4H₂O; oxidation can also produce methanal.",
    "Ethanol" to "CH₃CH₂OH + [O] → CH₃CHO → CH₃COOH connects alcohols, aldehydes and acids.",
    "Methanal" to "Nucleophilic addition at its C=O carbon illustrates carbonyl reactivity.",
    "Ethanoic acid" to "CH₃COOH + C₂H₅OH ⇌ CH₃COOC₂H₅ + H₂O is esterification.",
    "Benzene" to "Electrophilic substitution preserves its aromatic π system better than addition.",
    "Urea" to "Urease-catalyzed hydrolysis produces ammonia and carbon dioxide species.",
    "Glycine" to "H₃N⁺CH₂COO⁻ shows zwitterion formation near neutral pH.",
    "Lactic acid" to "Pyruvate + NADH ⇌ lactate + NAD⁺ connects stereochemistry with metabolism.",
    "Glucose" to "C₆H₁₂O₆ + 6O₂ → 6CO₂ + 6H₂O summarizes aerobic oxidation."
)

private val originalMoleculeUses = mapOf(
    "Hydrogen" to "Ammonia production, fuel cells, hydrogenation and energy-storage research.",
    "Oxygen" to "Medical oxygen, steelmaking, combustion, wastewater treatment and respiration.",
    "Nitrogen" to "Inert atmospheres, cryogenic cooling and ammonia/fertilizer production.",
    "Water" to "Biological solvent, heat transfer, cleaning, power generation and chemical processing.",
    "Carbon dioxide" to "Carbonation, fire suppression, refrigeration, plant growth and supercritical extraction.",
    "Ammonia" to "Fertilizers, nitric acid, refrigeration and cleaning chemistry.",
    "Methane" to "Natural-gas fuel, hydrogen production and chemical feedstock.",
    "Boron trifluoride" to "Lewis-acid catalyst in polymerization and organic synthesis.",
    "Phosphorus pentachloride" to "Chlorinating reagent for preparing acyl and phosphoryl chlorides.",
    "Sulfur hexafluoride" to "High-voltage insulation and tracer studies, with strict greenhouse-gas controls.",
    "Xenon tetrafluoride" to "Selective fluorination and specialized inorganic synthesis.",
    "Ethane" to "Primary petrochemical feedstock for ethene production.",
    "Ethene" to "Polyethylene, ethylene oxide, ethanol and fruit-ripening control.",
    "Ethyne" to "Oxy-fuel welding and organic synthesis.",
    "Methanol" to "Solvent, fuel, formaldehyde manufacture and chemical feedstock; toxic.",
    "Ethanol" to "Solvent, disinfectant, beverage chemistry, fuel and synthesis.",
    "Methanal" to "Resins, polymers, preservation chemistry and manufacturing; hazardous vapor.",
    "Ethanoic acid" to "Food acidulation, acetate esters, polymers and chemical synthesis.",
    "Benzene" to "Industrial feedstock for polymers and chemicals; exposure is strictly controlled because it is carcinogenic.",
    "Urea" to "Fertilizers, resins, dermatology products and biological nitrogen excretion.",
    "Glycine" to "Protein biosynthesis, buffers, nutrition and pharmaceutical formulations.",
    "Lactic acid" to "Food fermentation, biodegradable PLA production and biochemical metabolism.",
    "Glucose" to "Cellular fuel, fermentation, food chemistry and medical energy solutions."
)

private val originalMoleculeRelations = mapOf(
    "Hydrogen" to listOf("Oxygen", "Water"), "Oxygen" to listOf("Ozone", "Water"),
    "Nitrogen" to listOf("Ammonia", "Nitric oxide"), "Water" to listOf("Hydrogen", "Carbon dioxide"),
    "Carbon dioxide" to listOf("Carbon monoxide", "Carbonic acid"), "Ammonia" to listOf("Hydrazine", "Phosphine"),
    "Methane" to listOf("Ethane", "Silane"), "Ethanol" to listOf("Methanol", "Ethanoic acid"),
    "Glucose" to listOf("Fructose", "Sucrose"), "Glycine" to listOf("Alanine", "Serine")
)

private fun molecule(
    chapter: String, name: String, formula: String, mass: String, geometry: String,
    bonding: String, theory: String, example: String, uses: String,
    layout: String = "ethanol", related: List<String> = emptyList()
) = MoleculeEntry(chapter, name, formula, mass, geometry, bonding, theory, layout, theory, example, uses, related)

private val expandedMoleculeLibrary = listOf(
    molecule("Atmosphere & environment", "Ozone", "O₃", "47.998 g/mol", "Bent", "Resonance makes both O–O bonds equivalent", "Ozone is a polar allotrope of oxygen with delocalized bonding.", "O₃ + UV → O₂ + O protects the stratosphere but drives oxidation near the ground.", "Stratospheric UV shielding, water treatment and controlled oxidation.", "bent", listOf("Oxygen", "Nitrogen dioxide")),
    molecule("Atmosphere & environment", "Carbon monoxide", "CO", "28.010 g/mol", "Linear diatomic", "Polar triple bond with a carbon-centered lone pair", "CO binds transition metals strongly through σ donation and π back-bonding.", "2CO + O₂ → 2CO₂ in combustion and catalytic converters.", "Metal refining, synthesis gas and monitored industrial chemistry; highly toxic.", "diatomic", listOf("Carbon dioxide", "Oxygen")),
    molecule("Atmosphere & environment", "Nitric oxide", "NO", "30.006 g/mol", "Linear diatomic", "Odd-electron radical", "NO is paramagnetic and acts as a short-lived signaling molecule.", "2NO + O₂ → 2NO₂ occurs readily in air.", "Biological signaling, nitric-acid manufacture and atmospheric chemistry.", "diatomic", listOf("Nitrogen dioxide", "Nitrous oxide")),
    molecule("Atmosphere & environment", "Nitrogen dioxide", "NO₂", "46.005 g/mol", "Bent", "Odd-electron molecule; dimerizes to N₂O₄", "Its unpaired electron explains its color, reactivity and reversible dimerization.", "2NO₂ ⇌ N₂O₄ shifts toward the colorless dimer on cooling.", "Nitric-acid production, rocket oxidizer systems and air-pollution studies.", "bent", listOf("Nitric oxide", "Nitrous oxide")),
    molecule("Atmosphere & environment", "Sulfur dioxide", "SO₂", "64.066 g/mol", "Bent", "Polar S–O bonds with resonance", "SO₂ dissolves in water and is readily oxidized, central to sulfur chemistry.", "2SO₂ + O₂ ⇌ 2SO₃ is the Contact-process oxidation.", "Sulfuric-acid production, food preservation and bleaching; also an air pollutant.", "bent", listOf("Sulfur trioxide", "Hydrogen sulfide")),
    molecule("Atmosphere & environment", "Sulfur trioxide", "SO₃", "80.063 g/mol", "Trigonal planar gas-phase monomer", "Three equivalent S–O bonds by delocalization", "Electron-domain symmetry makes isolated SO₃ non-polar despite polar bonds.", "SO₃ + H₂O → H₂SO₄ is strongly exothermic.", "Sulfuric-acid and sulfonation chemistry.", "trigonal", listOf("Sulfur dioxide", "Sulfuric acid")),
    molecule("Atmosphere & environment", "Nitrous oxide", "N₂O", "44.013 g/mol", "Nearly linear", "Resonance-distributed N–N–O bonding", "N₂O is a greenhouse gas and oxidizer with a linear but unsymmetrical structure.", "On heating, N₂O can supply oxygen to support oxidation.", "Medical anesthetic, food aerosol propellant and controlled oxidation.", "linear3", listOf("Nitric oxide", "Nitrogen dioxide")),
    molecule("Atmosphere & environment", "Hydrogen sulfide", "H₂S", "34.081 g/mol", "Bent", "Two polar H–S bonds and two lone pairs", "H₂S is a weak diprotic acid and stronger reducing agent than water.", "2H₂S + 3O₂ → 2SO₂ + 2H₂O on complete combustion.", "Sulfur recovery and analytical sulfide chemistry; extremely toxic.", "bent", listOf("Water", "Sulfur dioxide")),

    molecule("Acids & bases", "Hydrogen chloride", "HCl", "36.461 g/mol", "Linear diatomic", "Strongly polar H–Cl σ bond", "In water HCl transfers its proton essentially completely to form hydronium.", "HCl + H₂O → H₃O⁺ + Cl⁻.", "Steel pickling, pH control, chloride preparation and laboratory acidification.", "diatomic", listOf("Water", "Sulfuric acid")),
    molecule("Acids & bases", "Sulfuric acid", "H₂SO₄", "98.079 g/mol", "Tetrahedral around sulfur", "Two O–H groups and delocalized S–O bonding", "A diprotic strong acid in its first dissociation and a powerful dehydrating reagent.", "H₂SO₄ + 2NaOH → Na₂SO₄ + 2H₂O.", "Fertilizers, batteries, petroleum processing and chemical manufacture.", "tetrahedral", listOf("Sulfur trioxide", "Hydrogen chloride")),
    molecule("Acids & bases", "Nitric acid", "HNO₃", "63.012 g/mol", "Planar nitrate framework", "Resonance-stabilized conjugate base", "Strong acidity follows from stabilization of nitrate after proton transfer.", "3Cu + 8HNO₃(dilute) → 3Cu(NO₃)₂ + 2NO + 4H₂O.", "Fertilizers, nitration, metal treatment and oxidizing chemistry.", "trigonal", listOf("Nitrogen dioxide", "Sulfuric acid")),
    molecule("Acids & bases", "Phosphoric acid", "H₃PO₄", "97.994 g/mol", "Tetrahedral around phosphorus", "Three ionizable O–H groups", "Stepwise deprotonation gives three buffer regions with different pKa values.", "H₃PO₄ + 3NaOH → Na₃PO₄ + 3H₂O.", "Fertilizers, food acidulation, rust treatment and phosphate buffers.", "tetrahedral", listOf("Sulfuric acid", "ATP")),
    molecule("Acids & bases", "Carbonic acid", "H₂CO₃", "62.024 g/mol", "Planar carbon center", "Carbonyl plus two O–H groups", "It forms reversibly when CO₂ hydrates and controls major natural buffer systems.", "CO₂ + H₂O ⇌ H₂CO₃ ⇌ H⁺ + HCO₃⁻.", "Blood buffering, carbonate geology and carbonated-water chemistry.", "trigonal", listOf("Carbon dioxide", "Water")),
    molecule("Acids & bases", "Sodium hydroxide", "NaOH", "39.997 g/mol", "Ionic solid; linear Na–O–H unit in gas phase", "Na⁺ and OH⁻ ions", "In water it dissociates to supply hydroxide, making it a strong base.", "NaOH + HCl → NaCl + H₂O.", "Soap, paper, drain cleaning, pH control and chemical synthesis.", "linear3", listOf("Hydrogen chloride", "Calcium hydroxide")),
    molecule("Acids & bases", "Calcium hydroxide", "Ca(OH)₂", "74.093 g/mol", "Ionic lattice", "Ca²⁺ with hydroxide ions", "Limited solubility gives alkaline limewater and establishes a dissolution equilibrium.", "Ca(OH)₂ + CO₂ → CaCO₃ + H₂O.", "Mortar, water treatment, soil conditioning and carbon-dioxide tests.", "linear3", listOf("Sodium hydroxide", "Carbon dioxide")),
    molecule("Acids & bases", "Hydrazine", "N₂H₄", "32.045 g/mol", "Pyramidal at each nitrogen", "N–N single bond; two lone pairs", "Lone pairs make hydrazine basic and readily oxidized.", "N₂H₄ + O₂ → N₂ + 2H₂O.", "Propellants, polymer blowing agents and oxygen scavenging.", "ethane", listOf("Ammonia", "Nitrogen")),

    molecule("Hydrocarbons", "Propane", "C₃H₈", "44.097 g/mol", "Tetrahedral carbons", "C–C and C–H σ bonds", "A saturated alkane whose conformations rotate around C–C bonds.", "C₃H₈ + 5O₂ → 3CO₂ + 4H₂O.", "LPG heating, cooking and chemical feedstock.", "ethane", listOf("Methane", "Butane")),
    molecule("Hydrocarbons", "Butane", "C₄H₁₀", "58.124 g/mol", "Tetrahedral carbon chain", "All σ bonds", "Anti and gauche conformers illustrate torsional and steric energy.", "2C₄H₁₀ + 13O₂ → 8CO₂ + 10H₂O.", "LPG, lighter fuel, aerosol propellant and petrochemical feedstock.", "ethane", listOf("Propane", "Pentane")),
    molecule("Hydrocarbons", "Pentane", "C₅H₁₂", "72.151 g/mol", "Flexible tetrahedral chain", "All σ bonds", "Increasing surface area strengthens London dispersion relative to smaller alkanes.", "Complete combustion forms CO₂ and H₂O.", "Laboratory solvent, blowing agent and gasoline component.", "ethane", listOf("Butane", "Hexane")),
    molecule("Hydrocarbons", "Hexane", "C₆H₁₄", "86.178 g/mol", "Flexible tetrahedral chain", "All σ bonds", "A non-polar solvent whose isomers demonstrate carbon skeleton variation.", "C₆H₁₄ + 19/2 O₂ → 6CO₂ + 7H₂O.", "Extraction and chromatography solvent; neurotoxic with chronic exposure.", "ethane", listOf("Pentane", "Cyclohexane")),
    molecule("Hydrocarbons", "Cyclohexane", "C₆H₁₂", "84.162 g/mol", "Chair conformation", "Six C–C σ bonds in a ring", "The chair minimizes angle and torsional strain; axial/equatorial sites control stability.", "Cyclohexane oxidation supplies intermediates for nylon manufacture.", "Nylon feedstock and non-polar solvent.", "ring", listOf("Hexane", "Benzene")),
    molecule("Hydrocarbons", "Propene", "C₃H₆", "42.081 g/mol", "Trigonal planar at C=C", "One σ and one π bond in C=C", "The π bond is electron-rich and undergoes electrophilic addition.", "nCH₂=CHCH₃ → polypropylene.", "Polypropylene manufacture and organic synthesis.", "ethene", listOf("Ethene", "Styrene")),
    molecule("Hydrocarbons", "1,3-Butadiene", "C₄H₆", "54.092 g/mol", "Conjugated, mostly planar", "Alternating π bonds", "Conjugation delocalizes electrons and changes addition and polymerization behavior.", "nCH₂=CH–CH=CH₂ → polybutadiene.", "Synthetic rubber and copolymer manufacture.", "ethene", listOf("Ethene", "Styrene")),
    molecule("Hydrocarbons", "Toluene", "C₇H₈", "92.141 g/mol", "Planar aromatic ring with tetrahedral methyl", "Delocalized aromatic π system", "The methyl group activates the benzene ring toward electrophilic substitution.", "Toluene nitration produces ring-substituted nitrotoluenes.", "Solvent and feedstock for polymers, coatings and synthesis.", "ring", listOf("Benzene", "Styrene")),
    molecule("Hydrocarbons", "Naphthalene", "C₁₀H₈", "128.174 g/mol", "Planar fused rings", "Ten π electrons delocalized across two rings", "Fused aromaticity gives a rigid planar molecule and characteristic substitution chemistry.", "Electrophilic substitution commonly occurs at an alpha position.", "Dye and resin intermediates; formerly common in moth repellents.", "ring", listOf("Benzene", "Toluene")),
    molecule("Hydrocarbons", "Styrene", "C₈H₈", "104.152 g/mol", "Planar conjugated vinyl group", "Aromatic ring conjugated with C=C", "Its vinyl π bond polymerizes while the phenyl group controls material properties.", "nC₆H₅CH=CH₂ → polystyrene.", "Polystyrene, ABS and synthetic rubber manufacture.", "ring", listOf("Benzene", "Propene")),

    molecule("Functional groups", "Propan-1-ol", "C₃H₈O", "60.096 g/mol", "Tetrahedral chain; bent oxygen", "Primary alcohol O–H group", "Hydrogen bonding raises boiling point compared with similar hydrocarbons.", "Oxidation gives propanal and then propanoic acid.", "Solvent, coatings, pharmaceutical and ester manufacture.", "ethanol", listOf("Ethanol", "Propanone")),
    molecule("Functional groups", "Propanone", "C₃H₆O", "58.080 g/mol", "Trigonal planar carbonyl", "Polar C=O ketone group", "The electrophilic carbonyl carbon undergoes nucleophilic addition.", "Reduction of propanone gives propan-2-ol.", "Common solvent, cleaning and chemical synthesis.", "trigonal", listOf("Methanal", "Propan-1-ol")),
    molecule("Functional groups", "Ethanal", "C₂H₄O", "44.053 g/mol", "Trigonal planar carbonyl", "Aldehyde C=O with one hydrogen", "Aldehydes oxidize readily because the carbonyl carbon bears a hydrogen.", "CH₃CHO + [O] → CH₃COOH.", "Intermediate in manufacture and biochemical ethanol metabolism.", "trigonal", listOf("Ethanol", "Ethanoic acid")),
    molecule("Functional groups", "Ethyl ethanoate", "C₄H₈O₂", "88.106 g/mol", "Planar ester group", "Resonance between two C–O bonds", "Ester resonance reduces carbonyl reactivity and gives characteristic hydrolysis chemistry.", "CH₃COOH + C₂H₅OH ⇌ CH₃COOC₂H₅ + H₂O.", "Solvent in coatings, inks, adhesives and extraction.", "ethanol", listOf("Ethanol", "Ethanoic acid")),
    molecule("Functional groups", "Diethyl ether", "C₄H₁₀O", "74.123 g/mol", "Bent oxygen between ethyl groups", "Two C–O σ bonds; no O–H bond", "It accepts hydrogen bonds but cannot donate them, lowering boiling point versus alcohols.", "Ethers can form peroxides during prolonged air exposure.", "Laboratory solvent and historical anesthetic; highly flammable.", "ethanol", listOf("Ethanol", "Ethyl ethanoate")),
    molecule("Functional groups", "Phenol", "C₆H₆O", "94.113 g/mol", "Planar aromatic ring", "O lone pair conjugated with benzene", "Resonance stabilizes phenoxide, making phenol more acidic than aliphatic alcohols.", "Phenol + Br₂(aq) → 2,4,6-tribromophenol.", "Resins, polymers, disinfectant chemistry and synthesis.", "ring", listOf("Benzene", "Aniline")),
    molecule("Functional groups", "Aniline", "C₆H₇N", "93.129 g/mol", "Nearly planar amino group and ring", "Nitrogen lone pair conjugated with benzene", "Delocalization lowers basicity compared with aliphatic amines.", "Diazotization enables azo-dye formation.", "Dyes, polyurethane intermediates, rubber and pharmaceuticals.", "ring", listOf("Phenol", "Benzene")),
    molecule("Functional groups", "Acetamide", "C₂H₅NO", "59.068 g/mol", "Planar amide group", "Strong N–C=O resonance", "Amide resonance restricts rotation and makes nitrogen weakly basic.", "Hydrolysis yields ethanoic acid/ethanoate and ammonia/ammonium.", "Model for peptide bonding and an industrial solvent/plasticizer intermediate.", "trigonal", listOf("Urea", "Ethanoic acid")),
    molecule("Functional groups", "Acetonitrile", "C₂H₃N", "41.053 g/mol", "Linear C≡N group", "Polar nitrile triple bond", "Its high dipole and aprotic character make it a useful polar solvent.", "Hydrolysis ultimately gives ethanoic acid derivatives.", "HPLC/mobile-phase solvent, battery and synthesis chemistry.", "linear4", listOf("Ethanal", "Acetamide")),
    molecule("Functional groups", "Chloroform", "CHCl₃", "119.378 g/mol", "Tetrahedral", "Three polar C–Cl bonds", "Its tetrahedral dipoles do not cancel completely, giving modest polarity.", "Strong light and oxygen can form hazardous phosgene; stabilized storage is essential.", "Specialized solvent and chemical intermediate under controlled conditions.", "tetrahedral", listOf("Methane", "Carbon tetrachloride")),

    molecule("Biomolecules", "Fructose", "C₆H₁₂O₆", "180.156 g/mol", "Mostly cyclic furanose/pyranose forms", "Ketose with multiple hydroxyl groups", "Fructose shares glucose's formula but differs in connectivity and carbonyl type.", "It enters glycolytic metabolism after enzyme-controlled phosphorylation.", "Natural fruit sugar, food sweetener and metabolism studies.", "ring", listOf("Glucose", "Sucrose")),
    molecule("Biomolecules", "Sucrose", "C₁₂H₂₂O₁₁", "342.297 g/mol", "Linked glucose and fructose rings", "α,β glycosidic acetal bond", "Both anomeric centers are tied in the bond, making sucrose non-reducing.", "Sucrose + H₂O → glucose + fructose.", "Table sugar, food processing and fermentation feedstock after hydrolysis.", "ring", listOf("Glucose", "Fructose")),
    molecule("Biomolecules", "Ribose", "C₅H₁₀O₅", "150.130 g/mol", "Mostly five-membered ring in nucleotides", "Aldopentose with hydroxyl groups", "Ribose supplies the sugar scaffold of RNA and energy-carrying nucleotides.", "Phosphate ester formation builds the RNA backbone and ATP.", "RNA, ATP, cofactors and biochemical research.", "ring", listOf("Glucose", "ATP")),
    molecule("Biomolecules", "Alanine", "C₃H₇NO₂", "89.094 g/mol", "Tetrahedral α-carbon", "Amino and carboxyl groups", "At physiological pH it is mainly a zwitterion; its methyl side chain is non-polar.", "Transamination interconverts alanine and pyruvate.", "Protein biosynthesis and nitrogen/carbon metabolism.", "ethanol", listOf("Glycine", "Pyruvic acid")),
    molecule("Biomolecules", "Serine", "C₃H₇NO₃", "105.093 g/mol", "Tetrahedral α-carbon", "Hydroxymethyl side chain", "Its polar O–H group forms hydrogen bonds and is a common phosphorylation site.", "Kinases convert protein serine–OH to phosphate esters.", "Protein structure, signaling and one-carbon metabolism.", "ethanol", listOf("Alanine", "Glycine")),
    molecule("Biomolecules", "Cysteine", "C₃H₇NO₂S", "121.160 g/mol", "Tetrahedral α-carbon", "Reactive thiol side chain", "Two cysteine thiols can oxidize to a stabilizing disulfide bond.", "2RSH ⇌ RSSR + 2H⁺ + 2e⁻.", "Protein folding, enzyme active sites and redox buffering.", "ethanol", listOf("Serine", "Glycine")),
    molecule("Biomolecules", "Caffeine", "C₈H₁₀N₄O₂", "194.190 g/mol", "Planar fused heterocycle", "Conjugated purine-like π system", "Resonance and methyl substitution make caffeine a weak base and receptor-active molecule.", "Metabolism oxidizes and demethylates caffeine in the liver.", "Beverages, alertness products and pharmacology research.", "ring", listOf("Adenosine", "Urea")),
    molecule("Biomolecules", "ATP", "C₁₀H₁₆N₅O₁₃P₃", "507.181 g/mol", "Adenosine linked to triphosphate", "Phosphoanhydride and phosphate-ester bonds", "ATP hydrolysis is favorable because products have improved resonance, hydration and reduced repulsion.", "ATP + H₂O → ADP + Pᵢ + H⁺.", "Universal biochemical energy coupling, phosphorylation and signaling.", "ring", listOf("Ribose", "Phosphoric acid")),
    molecule("Biomolecules", "Cholesterol", "C₂₇H₄₆O", "386.654 g/mol", "Rigid four-ring steroid", "Mostly non-polar framework with one O–H", "Its amphipathic shape tunes membrane fluidity and supplies steroid precursors.", "Enzymatic oxidation and side-chain modification produce bile acids and hormones.", "Cell membranes, steroid biosynthesis and lipoprotein studies.", "ring", listOf("Glucose", "Lactic acid")),
    molecule("Biomolecules", "Pyruvic acid", "C₃H₄O₃", "88.062 g/mol", "Planar keto-acid region", "Ketone and carboxylic acid groups", "Pyruvate sits at a major metabolic branch connecting glycolysis and respiration.", "Pyruvate + NADH ⇌ lactate + NAD⁺.", "Metabolism, fermentation and biochemical analysis.", "trigonal", listOf("Lactic acid", "Alanine")),

    molecule("Medicines & materials", "Aspirin", "C₉H₈O₄", "180.159 g/mol", "Planar aromatic core", "Carboxylic acid and ester groups", "Acetylation changes salicylic-acid reactivity and enables irreversible enzyme acetylation.", "Hydrolysis gives salicylic acid and ethanoic acid.", "Analgesic, antipyretic and antiplatelet medicine.", "ring", listOf("Ethanoic acid", "Paracetamol")),
    molecule("Medicines & materials", "Paracetamol", "C₈H₉NO₂", "151.163 g/mol", "Mostly planar aromatic molecule", "Phenol and amide groups", "Conjugation and hydrogen bonding govern its crystal form, solubility and reactivity.", "Controlled metabolism gives conjugated products; overdose can form a toxic metabolite.", "Analgesic and antipyretic medicine.", "ring", listOf("Phenol", "Aspirin")),
    molecule("Medicines & materials", "Ibuprofen", "C₁₃H₁₈O₂", "206.285 g/mol", "Flexible arylpropionic acid", "Carboxylic acid plus hydrophobic aromatic group", "Its stereogenic center and acid group are central to binding and pharmacology.", "Acid–base salts increase aqueous formulation compatibility.", "Non-steroidal anti-inflammatory and analgesic medicine.", "ring", listOf("Aspirin", "Ethanoic acid")),
    molecule("Medicines & materials", "Penicillin G", "C₁₆H₁₈N₂O₄S", "334.390 g/mol", "Strained fused β-lactam ring", "Reactive cyclic amide", "Ring strain makes the β-lactam carbonyl susceptible to enzyme-targeted nucleophilic attack.", "β-Lactam hydrolysis opens the ring and removes antibacterial activity.", "Antibacterial treatment and β-lactam chemistry education.", "ring", listOf("Acetamide", "Cysteine")),
    molecule("Medicines & materials", "PET repeat unit", "(C₁₀H₈O₄)ₙ", "192.17 g/mol per repeat", "Aromatic polyester chain", "Ester-linked terephthalate units", "Rigid aromatic units give strength while ester groups permit condensation synthesis and recycling chemistry.", "Hydrolysis or glycolysis cleaves polyester bonds.", "Beverage bottles, fibers, films and recyclable packaging.", "ring", listOf("Ethyl ethanoate", "Ethylene glycol")),
    molecule("Medicines & materials", "PTFE repeat unit", "(C₂F₄)ₙ", "100.02 g/mol per repeat", "Helical fluorocarbon chain", "Strong C–F and C–C bonds", "A fluorine sheath lowers surface energy and protects the carbon backbone.", "Tetrafluoroethene undergoes radical addition polymerization.", "Non-stick coatings, chemical seals and electrical insulation.", "ethane", listOf("Ethene", "Sulfur hexafluoride")),
    molecule("Medicines & materials", "Caprolactam", "C₆H₁₁NO", "113.160 g/mol", "Seven-membered cyclic amide", "Lactam amide group", "Ring-opening polymerization converts a cyclic amide into nylon-6.", "n caprolactam → [–NH(CH₂)₅CO–]ₙ.", "Nylon-6 fibers, films and engineering plastics.", "ring", listOf("Acetamide", "Cyclohexane")),
    molecule("Medicines & materials", "Bisphenol A", "C₁₅H₁₆O₂", "228.291 g/mol", "Two phenol rings around tetrahedral carbon", "Two phenolic O–H groups", "Its rigid bifunctional structure forms step-growth polymers but also drives exposure concerns.", "Reacts with carbonate or epoxide partners in polymer manufacture.", "Polycarbonate and epoxy-resin intermediate; increasingly restricted in some uses.", "ring", listOf("Phenol", "PET repeat unit")),
    molecule("Medicines & materials", "TNT", "C₇H₅N₃O₆", "227.131 g/mol", "Planar aromatic core with twisted nitro groups", "Three electron-withdrawing nitro groups", "Nitro groups store oxidizing capacity within an energetic molecule.", "Rapid decomposition forms stable small gases and releases energy.", "Historical/controlled explosive applications and energetic-material studies.", "ring", listOf("Toluene", "Nitric acid")),
    molecule("Medicines & materials", "Glyphosate", "C₃H₈NO₅P", "169.073 g/mol", "Flexible zwitterionic molecule", "Amine, carboxylate and phosphonate groups", "Multiple ionizable groups make its charge and binding strongly pH-dependent.", "It inhibits an enzyme in the shikimate biosynthetic pathway.", "Herbicide chemistry and environmental fate studies.", "ethanol", listOf("Phosphoric acid", "Glycine")),

    molecule("Inorganic molecules", "Silane", "SiH₄", "32.117 g/mol", "Tetrahedral", "Four Si–H σ bonds", "Silane is the silicon analogue of methane but is much more readily oxidized.", "SiH₄ + 2O₂ → SiO₂ + 2H₂O.", "Semiconductor silicon deposition and materials processing.", "tetrahedral", listOf("Methane", "Silicon tetrachloride")),
    molecule("Inorganic molecules", "Phosphine", "PH₃", "33.998 g/mol", "Trigonal pyramidal", "Three P–H bonds and one lone pair", "It is less basic than ammonia because the phosphorus lone pair is less available.", "PH₃ burns to phosphorus oxides under appropriate conditions.", "Semiconductor doping and organophosphorus synthesis; highly toxic.", "pyramidal", listOf("Ammonia", "Phosphorus pentachloride")),
    molecule("Inorganic molecules", "Diborane", "B₂H₆", "27.670 g/mol", "Bridged structure", "Two three-center two-electron B–H–B bonds", "Electron deficiency produces multicenter bonds that cannot be described as ordinary two-center bonds.", "Hydrolysis yields borate species and hydrogen.", "Borane chemistry, reductions and semiconductor processing.", "ethane", listOf("Boron trifluoride", "Hydrogen")),
    molecule("Inorganic molecules", "Silicon tetrachloride", "SiCl₄", "169.898 g/mol", "Tetrahedral", "Four polar Si–Cl bonds", "Symmetry cancels bond dipoles, but electrophilic silicon hydrolyzes vigorously.", "SiCl₄ + 2H₂O → SiO₂ + 4HCl.", "High-purity silica, optical fibers and silicon chemistry.", "tetrahedral", listOf("Silane", "Titanium tetrachloride")),
    molecule("Inorganic molecules", "Titanium tetrachloride", "TiCl₄", "189.679 g/mol", "Tetrahedral monomer", "Four polar Ti–Cl bonds", "A volatile Lewis acid that hydrolyzes to titanium oxide species.", "TiCl₄ + 2H₂O → TiO₂ + 4HCl.", "Titanium metal and TiO₂ pigment manufacture; smoke-screen chemistry.", "tetrahedral", listOf("Silicon tetrachloride", "Boron trifluoride")),
    molecule("Inorganic molecules", "Uranium hexafluoride", "UF₆", "352.019 g/mol", "Octahedral", "Six U–F bonds", "Octahedral symmetry and volatility enable gas-phase isotope-separation processes.", "UF₆ reacts with moisture to form corrosive uranium and fluorine compounds.", "Nuclear-fuel-cycle isotope enrichment under strict containment.", "octahedral", listOf("Sulfur hexafluoride", "Xenon tetrafluoride")),
    molecule("Inorganic molecules", "Xenon difluoride", "XeF₂", "169.289 g/mol", "Linear", "Three-center four-electron F–Xe–F bonding", "Three equatorial lone pairs in a trigonal-bipyramidal electron arrangement leave a linear molecule.", "XeF₂ acts as a selective fluorinating and etching reagent.", "Fluorination, silicon etching and noble-gas chemistry.", "linear3", listOf("Xenon tetrafluoride", "Sulfur hexafluoride")),
    molecule("Inorganic molecules", "Chlorine trifluoride", "ClF₃", "92.448 g/mol", "T-shaped", "Three bonds and two equatorial lone pairs", "Five electron domains give trigonal-bipyramidal electron geometry but a T-shaped molecule.", "It reacts vigorously as an exceptionally strong fluorinating oxidizer.", "Specialized semiconductor cleaning and fluorination under extreme controls.", "trigonal", listOf("Xenon difluoride", "Boron trifluoride"))
)

internal data class ReactionEntry(
    val chapter: String,
    val title: String,
    val equation: String,
    val type: String,
    val conditions: String,
    val purpose: String,
    val explanation: String,
    val color: Color
)

internal val reactionLibrary = listOf(
    ReactionEntry("Synthesis", "Formation of water", "2H₂ + O₂ → 2H₂O", "Combination / redox", "Ignition", "Demonstrates atom conservation and exothermic synthesis.", "Hydrogen is oxidized and oxygen is reduced; the reaction releases substantial energy.", Orange),
    ReactionEntry("Synthesis", "Haber–Bosch ammonia synthesis", "N₂ + 3H₂ ⇌ 2NH₃", "Reversible combination", "Fe catalyst · high pressure · about 400–500 °C", "Foundation of nitrogen-fertilizer manufacture.", "Pressure favors ammonia because four gas molecules become two; temperature balances rate and yield.", Purple),
    ReactionEntry("Synthesis", "Sulfur trioxide formation", "2SO₂ + O₂ ⇌ 2SO₃", "Oxidation / equilibrium", "V₂O₅ catalyst · about 450 °C", "Key Contact-process step for sulfuric acid.", "The catalyst accelerates equilibrium attainment but does not change the equilibrium constant.", Cyan),
    ReactionEntry("Synthesis", "Magnesium oxide formation", "2Mg + O₂ → 2MgO", "Combination / redox", "Ignition", "Classic demonstration of oxidation and mass gain.", "Magnesium loses electrons to form Mg²⁺ while oxygen forms O²⁻.", Green),

    ReactionEntry("Decomposition", "Hydrogen peroxide decomposition", "2H₂O₂ → 2H₂O + O₂", "Decomposition / disproportionation", "MnO₂ or catalase catalyst", "Explains catalysis and oxygen generation.", "Oxygen in peroxide is both oxidized and reduced.", Green),
    ReactionEntry("Decomposition", "Calcium carbonate thermolysis", "CaCO₃ → CaO + CO₂", "Thermal decomposition", "Strong heating", "Central to lime and cement manufacture.", "Heat drives carbonate decomposition to calcium oxide and carbon dioxide.", Orange),
    ReactionEntry("Decomposition", "Potassium chlorate decomposition", "2KClO₃ → 2KCl + 3O₂", "Thermal decomposition / redox", "Heat · MnO₂ catalyst", "Laboratory example of oxygen preparation.", "The catalyst lowers activation energy; careful heating and contamination control are essential.", Purple),
    ReactionEntry("Decomposition", "Water electrolysis", "2H₂O(l) → 2H₂(g) + O₂(g)", "Electrolytic decomposition", "Direct current · electrolyte", "Connects stoichiometry with electrochemistry.", "Electrical energy drives a non-spontaneous redox process with a 2:1 gas ratio.", Cyan),

    ReactionEntry("Acid–base", "Strong acid–base neutralization", "HCl + NaOH → NaCl + H₂O", "Neutralization", "Aqueous solution", "Introduces titration stoichiometry.", "The net ionic change is H₃O⁺ + OH⁻ → 2H₂O.", Cyan),
    ReactionEntry("Acid–base", "Sulfuric acid with hydroxide", "H₂SO₄ + 2NaOH → Na₂SO₄ + 2H₂O", "Neutralization", "Aqueous solution", "Demonstrates diprotic-acid stoichiometry.", "Two moles of hydroxide are required per mole of sulfuric acid for complete neutralization.", Purple),
    ReactionEntry("Acid–base", "Carbonate with acid", "Na₂CO₃ + 2HCl → 2NaCl + H₂O + CO₂", "Acid–carbonate", "Aqueous solution", "Explains effervescence and carbonate tests.", "Carbonic acid forms transiently and decomposes to water and carbon dioxide.", Green),
    ReactionEntry("Acid–base", "Ammonia protonation", "NH₃ + H₂O ⇌ NH₄⁺ + OH⁻", "Brønsted acid–base equilibrium", "Aqueous solution", "Shows why ammonia is a weak base.", "Ammonia accepts a proton; incomplete reaction establishes an equilibrium.", Cyan),

    ReactionEntry("Precipitation", "Silver chloride precipitation", "AgNO₃ + NaCl → AgCl↓ + NaNO₃", "Double displacement", "Aqueous solution", "Tests for chloride ions.", "Ag⁺ and Cl⁻ form sparingly soluble AgCl; Na⁺ and NO₃⁻ are spectator ions.", Green),
    ReactionEntry("Precipitation", "Barium sulfate precipitation", "BaCl₂ + Na₂SO₄ → BaSO₄↓ + 2NaCl", "Double displacement", "Aqueous solution", "Tests for sulfate ions.", "BaSO₄ has very low solubility and forms a white precipitate.", Cyan),
    ReactionEntry("Precipitation", "Lead iodide precipitation", "Pb(NO₃)₂ + 2KI → PbI₂↓ + 2KNO₃", "Double displacement", "Aqueous solution", "Demonstrates solubility and crystallization.", "Yellow PbI₂ precipitates; lead compounds require strict safety controls.", Orange),
    ReactionEntry("Precipitation", "Copper hydroxide precipitation", "CuSO₄ + 2NaOH → Cu(OH)₂↓ + Na₂SO₄", "Double displacement", "Aqueous solution", "Identifies Cu²⁺ through a blue precipitate.", "Hydroxide removes Cu²⁺ from solution as sparingly soluble Cu(OH)₂.", Purple),

    ReactionEntry("Redox", "Zinc displaces copper", "Zn + CuSO₄ → ZnSO₄ + Cu", "Single displacement / redox", "Aqueous solution", "Connects the activity series with electron transfer.", "Zinc is oxidized to Zn²⁺ and Cu²⁺ is reduced to copper metal.", Orange),
    ReactionEntry("Redox", "Iron displaces copper", "Fe + CuSO₄ → FeSO₄ + Cu", "Single displacement / redox", "Aqueous solution", "Visualizes metal reactivity.", "Iron transfers electrons to Cu²⁺ because iron is more easily oxidized.", Green),
    ReactionEntry("Redox", "Permanganate with iron(II)", "MnO₄⁻ + 5Fe²⁺ + 8H⁺ → Mn²⁺ + 5Fe³⁺ + 4H₂O", "Redox titration", "Acidic solution", "Used in quantitative redox analysis.", "Permanganate is reduced from Mn(VII) to Mn(II), while Fe²⁺ becomes Fe³⁺.", Purple),
    ReactionEntry("Redox", "Daniell cell reaction", "Zn + Cu²⁺ → Zn²⁺ + Cu", "Galvanic-cell redox", "Separated half-cells and salt bridge", "Converts chemical energy into electrical work.", "Electrons flow externally from the zinc anode to the copper cathode.", Cyan),

    ReactionEntry("Organic", "Ethene hydrogenation", "C₂H₄ + H₂ → C₂H₆", "Addition", "Ni, Pd or Pt catalyst", "Introduces alkene addition and catalytic reduction.", "Hydrogen adds across the C=C bond, converting π and σ bonding to a single σ bond.", Green),
    ReactionEntry("Organic", "Ethanol oxidation", "CH₃CH₂OH + 2[O] → CH₃COOH + H₂O", "Oxidation", "Acidified dichromate or permanganate · reflux", "Connects alcohols with carboxylic acids.", "Primary alcohol oxidation proceeds through ethanal to ethanoic acid under strong conditions.", Orange),
    ReactionEntry("Organic", "Esterification", "CH₃COOH + C₂H₅OH ⇌ CH₃COOC₂H₅ + H₂O", "Condensation / equilibrium", "Concentrated H₂SO₄ · heat", "Introduces esters, equilibrium and acid catalysis.", "Ethanoic acid and ethanol form ethyl ethanoate reversibly.", Purple),
    ReactionEntry("Organic", "Methane chlorination", "CH₄ + Cl₂ → CH₃Cl + HCl", "Free-radical substitution", "UV light", "Introduces radical chain mechanisms.", "UV light initiates Cl–Cl homolysis; substitution can continue to further chlorinated products.", Cyan),

    ReactionEntry("Combustion", "Methane combustion", "CH₄ + 2O₂ → CO₂ + 2H₂O", "Complete combustion", "Ignition · excess oxygen", "Links fuels, energy and greenhouse-gas formation.", "Carbon reaches +4 in CO₂ and hydrogen forms water.", Orange),
    ReactionEntry("Combustion", "Ethanol combustion", "C₂H₅OH + 3O₂ → 2CO₂ + 3H₂O", "Complete combustion", "Ignition · excess oxygen", "Demonstrates balancing oxygenated-fuel combustion.", "Complete oxidation releases heat; limited oxygen can form CO and soot.", Green),
    ReactionEntry("Combustion", "Carbon monoxide combustion", "2CO + O₂ → 2CO₂", "Combustion / oxidation", "Ignition or catalyst", "Explains catalytic-converter oxidation.", "Toxic CO is oxidized to CO₂; vehicle catalysts accelerate this conversion.", Cyan),
    ReactionEntry("Combustion", "Propane combustion", "C₃H₈ + 5O₂ → 3CO₂ + 4H₂O", "Complete combustion", "Ignition · excess oxygen", "Applies hydrocarbon balancing to LPG fuel.", "Balance carbon, then hydrogen, then oxygen to obtain the coefficients.", Purple),

    ReactionEntry("Biochemical", "Photosynthesis", "6CO₂ + 6H₂O → C₆H₁₂O₆ + 6O₂", "Photochemical redox", "Light · chlorophyll-containing systems", "Stores solar energy in chemical bonds.", "This overall equation summarizes many enzyme-catalyzed light and carbon-fixation steps.", Green),
    ReactionEntry("Biochemical", "Aerobic respiration", "C₆H₁₂O₆ + 6O₂ → 6CO₂ + 6H₂O", "Biochemical oxidation", "Enzyme pathways", "Explains cellular ATP production.", "The overall equation summarizes glycolysis, the citric-acid cycle and oxidative phosphorylation.", Orange),
    ReactionEntry("Biochemical", "ATP hydrolysis", "ATP + H₂O → ADP + Pᵢ + H⁺", "Phosphoanhydride hydrolysis", "Enzyme-catalyzed in cells", "Couples favorable hydrolysis to cellular work.", "Actual protonation states and free energy depend on pH, Mg²⁺ and cellular conditions.", Purple),
    ReactionEntry("Biochemical", "Peptide-bond hydrolysis", "RCONHR′ + H₂O → RCOOH + H₂NR′", "Hydrolysis", "Protease or strong acid/base", "Connects proteins with amide chemistry.", "Water cleaves the amide bond; enzymes make the process selective under mild conditions.", Cyan)
)
