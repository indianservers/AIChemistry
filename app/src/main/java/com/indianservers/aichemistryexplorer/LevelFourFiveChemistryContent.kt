package com.indianservers.aichemistryexplorer

private data class AdvancedFact(
    val focus: String,
    val example: String,
    val relationship: String
)

private fun af(focus: String, example: String, relationship: String) =
    AdvancedFact(focus, example, relationship)

internal val levelFourTopics = linkedMapOf(
    "Advanced Physical Chemistry" to listOf(
        "Quantum chemistry", "Statistical mechanics", "Spectroscopy", "Molecular symmetry", "Group theory",
        "Phase rule", "Non-ideal systems"
    ),
    "Advanced Organic Chemistry" to listOf(
        "Reaction mechanisms", "Pericyclic reactions", "Rearrangements", "Named reactions",
        "Heterocyclic chemistry", "Organometallic chemistry", "Stereochemistry", "Asymmetric synthesis"
    ),
    "Advanced Inorganic Chemistry" to listOf(
        "Crystal field theory", "Ligand field theory", "Bioinorganic chemistry",
        "Organometallic compounds", "Cluster compounds"
    ),
    "Analytical Chemistry" to listOf(
        "Gravimetric analysis", "Volumetric analysis", "Complexometric titrations",
        "Redox titrations", "Instrumental analysis", "Error analysis"
    ),
    "Spectroscopy" to listOf("UV", "IR", "Raman", "NMR", "ESR", "Mass spectrometry", "XRD", "XPS"),
    "Separation Techniques" to listOf("Chromatography", "HPLC", "GC", "TLC", "Ion exchange", "Electrophoresis"),
    "Computational Chemistry" to listOf("Molecular modeling", "Gaussian", "DFT", "Molecular dynamics", "Docking"),
    "Polymer Chemistry" to listOf("Polymerization", "Conducting polymers", "Biopolymers"),
    "Industrial Chemistry" to listOf(
        "Cement", "Fertilizers", "Glass", "Ceramics", "Petrochemicals", "Soap", "Paint", "Dyes", "Explosives"
    ),
    "Environmental Chemistry" to listOf(
        "Air pollution", "Water pollution", "Green chemistry", "Waste management", "Atmospheric chemistry"
    ),
    "Biochemistry" to listOf(
        "Proteins", "DNA", "RNA", "Enzymes", "Metabolism", "ATP", "Photosynthesis", "Respiration"
    ),
    "Medicinal Chemistry" to listOf("Drug discovery", "SAR", "Pharmacokinetics", "Antibiotics", "Antivirals"),
    "Materials Chemistry" to listOf(
        "Nanomaterials", "Ceramics", "Smart materials", "Biomaterials", "Batteries", "Solar cells"
    )
)

internal val levelFiveTopics = linkedMapOf(
    "Advanced Quantum Chemistry" to listOf("Core principles and applications"),
    "Advanced Thermodynamics" to listOf("Core principles and applications"),
    "Statistical Thermodynamics" to listOf("Core principles and applications"),
    "Advanced Chemical Kinetics" to listOf("Core principles and applications"),
    "Photochemistry" to listOf("Core principles and applications"),
    "Electroanalytical Chemistry" to listOf("Core principles and applications"),
    "Supramolecular Chemistry" to listOf("Core principles and applications"),
    "Solid State Chemistry" to listOf("Core principles and applications"),
    "Organometallic Chemistry" to listOf("Core principles and applications"),
    "Bioorganic Chemistry" to listOf("Core principles and applications"),
    "Bioinorganic Chemistry" to listOf("Core principles and applications"),
    "Synthetic Organic Chemistry" to listOf("Core principles and applications"),
    "Retrosynthetic Analysis" to listOf("Core principles and applications"),
    "Green Chemistry" to listOf("Core principles and applications"),
    "Catalysis" to listOf("Homogeneous", "Heterogeneous", "Enzyme catalysis", "Photocatalysis"),
    "Nanochemistry" to listOf("Core principles and applications"),
    "Surface Science" to listOf("Core principles and applications"),
    "Medicinal Chemistry" to listOf("Core principles and applications"),
    "Pharmaceutical Chemistry" to listOf("Core principles and applications"),
    "Food Chemistry" to listOf("Core principles and applications"),
    "Agricultural Chemistry" to listOf("Core principles and applications"),
    "Petrochemical Chemistry" to listOf("Core principles and applications"),
    "Forensic Chemistry" to listOf("Core principles and applications"),
    "Geochemistry" to listOf("Core principles and applications"),
    "Atmospheric Chemistry" to listOf("Core principles and applications"),
    "Marine Chemistry" to listOf("Core principles and applications"),
    "Radiochemistry" to listOf("Core principles and applications"),
    "Astrochemistry" to listOf("Core principles and applications"),
    "Materials Science" to listOf("Core principles and applications"),
    "Computational Chemistry" to listOf("Core principles and applications"),
    "Chemical Biology" to listOf("Core principles and applications"),
    "Chemical Engineering Fundamentals" to listOf("Unit operations", "Mass transfer", "Heat transfer", "Reactor design"),
    "Instrumentation" to listOf(
        "GC-MS", "LC-MS/MS", "ICP-MS", "AAS", "ICP-OES", "XRF", "SEM", "TEM", "AFM", "Confocal microscopy"
    ),
    "Research Methodology" to listOf(
        "Literature review", "Experimental design", "Scientific writing", "Data analysis", "Chemometrics"
    ),
    "Advanced Laboratory Techniques" to listOf(
        "Air-sensitive synthesis", "Glove box", "Schlenk line", "High-pressure reactions", "Cryogenic techniques"
    ),
    "Emerging Areas" to listOf(
        "AI in Chemistry", "Machine Learning", "Quantum Computing for Chemistry", "CRISPR Chemistry",
        "Molecular Electronics", "Soft Matter", "MOFs", "COFs", "Click Chemistry", "Flow Chemistry", "Microfluidics"
    )
)

private val advancedFacts = mapOf(
    "Quantum chemistry" to af(
        "uses wavefunctions and operators to predict quantized molecular energies, electron density and observables.",
        "Solving the particle-in-a-box model gives En proportional to n squared and predicts increasingly spaced electronic states.",
        "H psi = E psi"
    ),
    "Statistical mechanics" to af(
        "connects molecular energy levels and populations with bulk thermodynamic properties.",
        "For two states separated by deltaE, their equilibrium population ratio is exp(-deltaE/kT) when degeneracies are equal.",
        "Pi/Pj = (gi/gj) exp[-(Ei-Ej)/kT]"
    ),
    "Spectroscopy" to af(
        "infers structure and dynamics from the interaction of electromagnetic radiation with quantized molecular states.",
        "An IR absorption at about 1700 cm-1 is evidence for many carbonyl groups, but the full spectrum must confirm identity.",
        "deltaE = h nu"
    ),
    "Molecular symmetry" to af(
        "classifies molecules through symmetry operations that leave nuclear positions indistinguishable.",
        "Water has E, a C2 axis and two vertical mirror planes and is assigned to the C2v point group.",
        "symmetry operations -> molecular point group"
    ),
    "Group theory" to af(
        "uses mathematical groups and character tables to predict orbital combinations, degeneracy and spectroscopic selection rules.",
        "Reducing a vibrational representation for H2O identifies three normal modes and their IR/Raman activity.",
        "Gamma reducible = sum ai Gamma irreducible"
    ),
    "Phase rule" to af(
        "relates components, phases and independent intensive variables at equilibrium.",
        "At the water triple point, C = 1 and P = 3, so F = 0 and temperature and pressure are fixed.",
        "F = C - P + 2"
    ),
    "Non-ideal systems" to af(
        "corrects ideal-gas and ideal-solution models for intermolecular interactions and finite molecular volume.",
        "A van der Waals calculation uses a/V squared to correct attraction and nb to correct excluded volume.",
        "(P + an^2/V^2)(V - nb) = nRT"
    ),

    "Reaction mechanisms" to af(
        "describe elementary bond-making and bond-breaking steps consistent with kinetics, stereochemistry and isotope evidence.",
        "An SN2 substitution gives second-order kinetics and inversion at a chiral centre through backside attack.",
        "rateSN2 = k[nucleophile][substrate]"
    ),
    "Pericyclic reactions" to af(
        "are concerted cyclic electron reorganizations governed by orbital-symmetry and thermal or photochemical conditions.",
        "A thermal Diels-Alder reaction combines a conjugated diene and dienophile through a six-electron cyclic transition state.",
        "4n+2 thermal cycloaddition pathways are symmetry allowed"
    ),
    "Rearrangements" to af(
        "move atoms or groups within a reactive intermediate to form a more stable skeleton or electron arrangement.",
        "A Wagner-Meerwein 1,2-shift can convert a less stable carbocation into a more substituted cation before capture.",
        "migrating group moves with its bonding electron pair"
    ),
    "Named reactions" to af(
        "organize reproducible bond constructions by substrate pattern, reagents, mechanism and synthetic scope.",
        "The Aldol reaction joins enolate and carbonyl partners to make a beta-hydroxy carbonyl and often a new C-C bond.",
        "enolate + carbonyl -> beta-hydroxy carbonyl"
    ),
    "Heterocyclic chemistry" to af(
        "examines rings containing heteroatoms and connects aromaticity, lone pairs and substitution patterns to reactivity.",
        "Pyridine's lone pair lies outside its aromatic sextet, so it can accept a proton without destroying aromaticity.",
        "aromatic ring satisfies 4n+2 pi electrons"
    ),
    "Organometallic chemistry" to af(
        "uses compounds with metal-carbon bonding as reagents and catalysts for selective bond formation.",
        "A Grignard reagent adds its carbon nucleophile to an aldehyde; acidic workup produces a secondary alcohol.",
        "RMgX + R'CHO -> R'CH(OH)R after workup"
    ),
    "Stereochemistry" to af(
        "describes three-dimensional molecular arrangement, configuration, conformation and stereochemical reaction outcomes.",
        "Assigning priorities by CIP rules lets a chiral centre be labelled R or S and distinguishes enantiomers.",
        "configuration determined by CIP priority sequence"
    ),
    "Asymmetric synthesis" to af(
        "creates one enantiomer preferentially using chiral catalysts, auxiliaries or substrates.",
        "A catalyst giving 90% R and 10% S product has 80% enantiomeric excess in favour of R.",
        "ee = |%R - %S|"
    ),

    "Crystal field theory" to af(
        "models electrostatic splitting of metal d orbitals by surrounding ligands.",
        "In an octahedral field, d orbitals split into lower t2g and higher eg sets separated by deltao.",
        "CFSEoct = (-0.4 nt2g + 0.6 neg) deltao"
    ),
    "Ligand field theory" to af(
        "extends crystal-field ideas with covalent metal-ligand orbital interactions.",
        "Pi-acceptor CO lowers metal t2g orbitals and commonly produces a large splitting and low-spin complexes.",
        "metal and ligand orbitals combine by matching symmetry and energy"
    ),
    "Bioinorganic chemistry" to af(
        "explains how metal ions support biological structure, transport, electron transfer and catalysis.",
        "Haemoglobin reversibly binds O2 at Fe(II), with the protein environment controlling affinity and cooperativity.",
        "metal coordination environment -> biological function"
    ),
    "Organometallic compounds" to af(
        "contain metal-carbon bonds and are interpreted through oxidation state, electron count and elementary organometallic steps.",
        "Ferrocene contains two eta5 cyclopentadienyl ligands and satisfies an 18-electron count at iron.",
        "valence electron count often approaches 18"
    ),
    "Cluster compounds" to af(
        "contain bonded groups of metal or main-group atoms whose structures require delocalized multicentre bonding.",
        "Wade rules classify closo B6H6(2-) as a six-vertex cluster with seven skeletal electron pairs.",
        "closo n vertices -> n+1 skeletal electron pairs"
    ),

    "Gravimetric analysis" to af(
        "determines analyte amount from the mass of a pure, stable compound of known composition.",
        "Chloride precipitated and weighed as AgCl is converted to chloride mass using the AgCl-to-Cl stoichiometric factor.",
        "mass analyte = mass precipitate x gravimetric factor"
    ),
    "Volumetric analysis" to af(
        "determines analyte amount from the accurately measured volume of a standardized reagent.",
        "At equivalence, 25.00 mL of 0.1000 M HCl requires 25.00 mL of 0.1000 M NaOH.",
        "moles titrant / coefficient = moles analyte / coefficient"
    ),
    "Complexometric titrations" to af(
        "use a chelating ligand, commonly EDTA, to determine metal-ion concentration under controlled pH.",
        "EDTA reacts 1:1 with Ca2+, so titrant moles at endpoint equal calcium moles in the aliquot.",
        "M(n+) + Y(4-) <-> MY^(n-4)"
    ),
    "Redox titrations" to af(
        "measure analytes through quantitative electron-transfer reactions and a suitable endpoint.",
        "Acidified permanganate oxidizes Fe2+ with a 1:5 MnO4-:Fe2+ mole ratio and acts as its own indicator.",
        "electrons accepted = electrons donated"
    ),
    "Instrumental analysis" to af(
        "converts a calibrated physical signal into chemical identity or concentration with controlled selectivity and uncertainty.",
        "A linear absorbance calibration converts an unknown signal to concentration, followed by the recorded dilution factor.",
        "signal = sensitivity x concentration + intercept"
    ),
    "Error analysis" to af(
        "quantifies random variation, systematic bias, uncertainty and fitness of analytical results.",
        "Replicate values give a mean and standard deviation, while a certified reference reveals possible bias.",
        "s = sqrt[sum(xi-xbar)^2/(n-1)]"
    ),

    "UV" to af(
        "UV-visible spectroscopy measures electronic absorption, especially pi-to-pi-star and n-to-pi-star transitions.",
        "A conjugated dye gives a visible absorption; increasing conjugation commonly shifts lambda max to longer wavelength.",
        "A = epsilon b c"
    ),
    "IR" to af(
        "infrared spectroscopy measures vibrational transitions that change molecular dipole moment.",
        "A broad 3200-3600 cm-1 band supports an O-H group, while a strong band near 1700 cm-1 supports C=O.",
        "wavenumber is proportional to sqrt(force constant/reduced mass)"
    ),
    "Raman" to af(
        "Raman spectroscopy measures inelastic light scattering from vibrations that change polarizability.",
        "The symmetric stretch of CO2 is Raman active even though it is inactive in IR, showing complementary selection rules.",
        "Raman shift = incident wavenumber - scattered wavenumber"
    ),
    "NMR" to af(
        "NMR spectroscopy probes nuclear spin environments, connectivity and molecular dynamics in a magnetic field.",
        "An ethyl group often gives a three-proton triplet and two-proton quartet through neighbouring proton coupling.",
        "chemical shift delta = frequency offset / spectrometer frequency x 10^6"
    ),
    "ESR" to af(
        "electron spin resonance detects species with unpaired electrons and reports their electronic environment.",
        "A radical interacting with one proton can show a two-line hyperfine pattern when equivalent-state assumptions apply.",
        "deltaE = g beta B"
    ),
    "Mass spectrometry" to af(
        "separates gas-phase ions by mass-to-charge ratio and uses isotope and fragmentation patterns for identification.",
        "A chlorine-containing molecular ion shows M and M+2 peaks in an approximately 3:1 intensity ratio.",
        "m/z = ion mass / charge number"
    ),
    "XRD" to af(
        "X-ray diffraction determines long-range crystal structure from constructive interference by lattice planes.",
        "A peak at known wavelength and angle gives interplanar spacing through Bragg's law.",
        "n lambda = 2 d sin theta"
    ),
    "XPS" to af(
        "X-ray photoelectron spectroscopy measures surface elemental composition and chemical-state-dependent binding energies.",
        "An oxidized metal peak appears at a different binding energy from the zero-valent metal, allowing surface-state assignment.",
        "binding energy = photon energy - kinetic energy - work function"
    ),

    "Chromatography" to af(
        "separates mixture components through different distributions between stationary and mobile phases.",
        "A compound retained more strongly by the stationary phase elutes later under otherwise identical conditions.",
        "retention factor depends on stationary/mobile phase distribution"
    ),
    "HPLC" to af(
        "uses high-pressure liquid mobile phase and packed columns for efficient separation of nonvolatile analytes.",
        "A reversed-phase gradient increases organic solvent to elute increasingly hydrophobic compounds.",
        "resolution Rs = 2(tR2-tR1)/(w1+w2)"
    ),
    "GC" to af(
        "separates volatile, thermally stable compounds in a gas mobile phase using boiling point and stationary-phase interactions.",
        "A temperature program resolves low-boiling solvent first and higher-boiling components later.",
        "retention time reflects volatility and partitioning"
    ),
    "TLC" to af(
        "uses a thin stationary layer for rapid qualitative separation and reaction monitoring.",
        "A spot travelling 3.0 cm while solvent travels 5.0 cm has Rf = 0.60 under those conditions.",
        "Rf = solute distance / solvent-front distance"
    ),
    "Ion exchange" to af(
        "separates ions through reversible electrostatic binding to charged resin sites.",
        "A cation-exchange resin retains Ca2+ more strongly than Na+ under suitable conditions because charge and selectivity differ.",
        "resin-site occupancy follows ion-exchange equilibrium"
    ),
    "Electrophoresis" to af(
        "separates charged species by their field-driven migration through a medium.",
        "Smaller DNA fragments usually migrate farther through agarose gel during a fixed run because the matrix sieves them less.",
        "electrophoretic velocity = mobility x electric field"
    ),

    "Molecular modeling" to af(
        "represents molecular structures and energies using quantum or classical computational models.",
        "A conformational search compares optimized energies of anti and gauche butane while checking that each geometry is a minimum.",
        "predicted structure minimizes the chosen energy function"
    ),
    "Gaussian" to af(
        "is a quantum-chemistry software package used for electronic-structure, geometry and spectroscopic calculations.",
        "A geometry optimization followed by a frequency calculation verifies no imaginary frequencies for a proposed minimum.",
        "method/basis set + molecular specification -> computed observables"
    ),
    "DFT" to af(
        "density functional theory approximates molecular ground-state properties through electron density and an exchange-correlation functional.",
        "Comparing a calculated reaction barrier with experiment tests whether the chosen functional and basis are adequate.",
        "energy is a functional of electron density E[rho]"
    ),
    "Molecular dynamics" to af(
        "propagates atomic motion over time using forces from a potential-energy model.",
        "A protein-water simulation integrates positions and velocities in femtosecond steps before structural averages are analysed.",
        "Fi = mi ai = -gradient_i U"
    ),
    "Docking" to af(
        "samples ligand poses in a binding site and ranks them with an approximate scoring function.",
        "A plausible pose reproduces a known hydrogen-bond network, but experimental affinity is still needed to validate ranking.",
        "score approximates binding interactions and conformational penalties"
    ),

    "Polymerization" to af(
        "joins monomers into macromolecules by chain-growth or step-growth pathways.",
        "Free-radical ethene polymerization uses initiation, propagation and termination to form polyethylene.",
        "degree of polymerization = polymer molar mass / repeat-unit molar mass"
    ),
    "Conducting polymers" to af(
        "combine conjugated backbones with doping to create mobile charge carriers.",
        "Oxidative doping of polyaniline increases conductivity by generating delocalized charge along the chain.",
        "conductivity depends on carrier density and mobility"
    ),
    "Biopolymers" to af(
        "are natural or bio-derived macromolecules whose sequence and interactions control biological or material function.",
        "Cellulose contains beta-linked glucose chains whose hydrogen-bonded assembly gives strong fibres.",
        "monomer sequence + intermolecular assembly -> function"
    ),

    "Cement" to af(
        "is a hydraulic binder whose clinker phases hydrate into strength-giving calcium-silicate-hydrate.",
        "Mixing Portland cement with water forms C-S-H gel and calcium hydroxide as curing proceeds.",
        "clinker + H2O -> hydrated binding phases"
    ),
    "Fertilizers" to af(
        "supply plant nutrients in chemically available forms while requiring control of losses and environmental impact.",
        "The Haber-Bosch process supplies ammonia used to manufacture urea and ammonium fertilizers.",
        "2NH3 + CO2 -> CO(NH2)2 + H2O"
    ),
    "Glass" to af(
        "is an amorphous inorganic network commonly based on silica modified to tune melting and properties.",
        "Adding Na2O lowers silica processing temperature while CaO improves chemical durability in soda-lime glass.",
        "composition + cooling history -> glass properties"
    ),
    "Ceramics" to af(
        "are inorganic nonmetallic materials processed by shaping and high-temperature densification.",
        "Sintering an alumina compact reduces porosity and raises strength without fully melting the body.",
        "powder compact + heat -> densified ceramic"
    ),
    "Petrochemicals" to af(
        "are industrial chemicals derived mainly from petroleum or natural-gas feedstocks.",
        "Steam cracking converts larger hydrocarbons into ethene and propene used for polymers and intermediates.",
        "hydrocarbon feed + heat -> smaller olefins"
    ),
    "Soap" to af(
        "is an alkali-metal salt of a long-chain fatty acid that cleans through surfactant and micelle formation.",
        "Saponifying a triglyceride with NaOH gives glycerol and sodium fatty-acid salts.",
        "triglyceride + 3NaOH -> glycerol + 3 soap"
    ),
    "Paint" to af(
        "combines binder, pigment, solvent or water and additives to form a protective or decorative film.",
        "An acrylic latex coalesces as water evaporates, leaving a polymer film that binds pigment to the surface.",
        "wet coating -> solvent loss/cure -> continuous film"
    ),
    "Dyes" to af(
        "are coloured molecules whose chromophores absorb visible light and whose functional groups control fibre affinity.",
        "An azo dye contains -N=N- conjugation; substituents shift absorption and enable binding to a chosen textile.",
        "extended conjugation -> visible absorption"
    ),
    "Explosives" to af(
        "release hot gases extremely rapidly through strongly exothermic decomposition or redox chemistry.",
        "An oxygen-balance calculation assesses whether an energetic formulation can oxidize its carbon and hydrogen internally.",
        "rapid gas generation + heat -> pressure wave"
    ),

    "Air pollution" to af(
        "studies harmful atmospheric gases and particles, their sources, transformation, exposure and control.",
        "NOx and volatile organics react in sunlight to form ground-level ozone in photochemical smog.",
        "emissions + atmospheric chemistry -> concentration and exposure"
    ),
    "Water pollution" to af(
        "covers chemical, biological and physical contamination that degrades water quality and ecosystems.",
        "Excess phosphate drives algal growth; decomposition then consumes dissolved oxygen and can cause fish death.",
        "pollutant load - removal = environmental accumulation"
    ),
    "Green chemistry" to af(
        "designs products and processes that prevent waste and reduce hazard across the life cycle.",
        "A catalytic high-atom-economy route can replace a stoichiometric process that creates large salt by-products.",
        "atom economy = desired-product formula mass / reactant formula masses x 100"
    ),
    "Waste management" to af(
        "prioritizes prevention, reuse, recovery, treatment and safe disposal using material and hazard data.",
        "Solvent recovery by distillation can return reusable solvent and reduce both purchase and disposal burdens.",
        "mass input = products + recovered material + emissions + waste"
    ),
    "Atmospheric chemistry" to af(
        "examines gas- and particle-phase reactions controlling air composition, climate and pollutant lifetime.",
        "OH radicals oxidize methane through a reaction chain that influences ozone and greenhouse forcing.",
        "source - sink = atmospheric rate of change"
    ),

    "Proteins" to af(
        "are amino-acid polymers whose sequence drives folding, dynamics and biological function.",
        "Replacing a buried hydrophobic residue with a charged residue can destabilize a protein core and reduce activity.",
        "sequence -> structure -> function"
    ),
    "DNA" to af(
        "stores genetic information in antiparallel nucleotide strands paired by hydrogen bonding and base stacking.",
        "During PCR, complementary primers define the sequence amplified by repeated denaturation, annealing and extension.",
        "A pairs with T; G pairs with C"
    ),
    "RNA" to af(
        "is a ribonucleotide polymer that carries information and can fold into catalytic or regulatory structures.",
        "mRNA codons are read by tRNA during translation, while rRNA helps catalyse peptide-bond formation.",
        "RNA sequence + folding -> molecular function"
    ),
    "Enzymes" to af(
        "are biological catalysts that stabilize transition states and show substrate-dependent kinetics.",
        "At [S] = Km, a Michaelis-Menten enzyme operates at half Vmax under the model assumptions.",
        "v = Vmax[S]/(Km+[S])"
    ),
    "Metabolism" to af(
        "is the regulated network of reactions that transforms matter and couples energy release to cellular work.",
        "Glycolysis converts glucose to pyruvate while producing net ATP and reducing NAD+ to NADH.",
        "catabolism supplies energy and precursors; anabolism consumes them"
    ),
    "ATP" to af(
        "couples favourable phosphate-transfer chemistry to transport, motion and biosynthesis.",
        "ATP hydrolysis can drive an otherwise unfavourable phosphorylation when the reactions share an intermediate.",
        "deltaGcoupled = sum of component deltaG values"
    ),
    "Photosynthesis" to af(
        "converts light energy into chemical potential and reduces carbon dioxide into organic matter.",
        "Photosystems generate ATP and NADPH, which the Calvin cycle uses to fix CO2.",
        "6CO2 + 6H2O + light -> C6H12O6 + 6O2"
    ),
    "Respiration" to af(
        "extracts energy from fuels through redox pathways and chemiosmotic ATP synthesis.",
        "Electrons from NADH pass through the respiratory chain, pumping protons whose return drives ATP synthase.",
        "fuel + O2 -> CO2 + H2O + usable energy"
    ),

    "Drug discovery" to af(
        "progresses from target evidence through screening, optimization and preclinical candidate selection.",
        "A hit series is improved for potency, selectivity, solubility and metabolic stability before nomination as a lead.",
        "target -> hit -> lead -> candidate"
    ),
    "SAR" to af(
        "structure-activity relationships connect controlled molecular changes with measured biological effects.",
        "Replacing a methyl group with chlorine may improve potency but must also be checked for lipophilicity and clearance changes.",
        "structural change -> property change -> activity change"
    ),
    "Pharmacokinetics" to af(
        "quantifies absorption, distribution, metabolism and excretion over time.",
        "For first-order elimination, concentration falls exponentially and half-life relates to clearance and distribution volume.",
        "t1/2 = 0.693 Vd / clearance"
    ),
    "Antibiotics" to af(
        "selectively inhibit bacterial processes while requiring resistance-aware dosing and stewardship.",
        "Beta-lactams inhibit cell-wall transpeptidases, but beta-lactamase production can destroy the drug.",
        "exposure relative to MIC helps predict antibacterial effect"
    ),
    "Antivirals" to af(
        "target virus-specific entry, replication, processing or release while limiting host toxicity.",
        "A nucleoside analogue can be incorporated by viral polymerase and terminate genome elongation.",
        "selectivity = antiviral effect / host-cell toxicity"
    ),

    "Nanomaterials" to af(
        "have nanoscale dimensions that create size-dependent surface, optical, electronic or mechanical properties.",
        "Gold nanoparticles display size-dependent plasmon colour unlike bulk gold because electrons respond collectively to light.",
        "surface-to-volume ratio increases as particle size decreases"
    ),
    "Smart materials" to af(
        "respond predictably to stimuli such as temperature, light, pH, stress or electric field.",
        "A shape-memory alloy returns to a programmed shape when heating triggers a reversible phase transformation.",
        "stimulus -> reversible structural response -> property change"
    ),
    "Biomaterials" to af(
        "are engineered to interact with biological systems with controlled compatibility, mechanics and degradation.",
        "A biodegradable polymer scaffold supports cells, then loses mass as tissue replaces it.",
        "structure + surface + degradation -> biological response"
    ),
    "Batteries" to af(
        "store chemical free energy in coupled electrode reactions and deliver it as electrical work.",
        "Lithium-ion capacity is estimated from reversible electron transfer per mass of active material.",
        "specific capacity = charge / active-material mass"
    ),
    "Solar cells" to af(
        "convert absorbed photons into separated charge carriers and electrical power.",
        "A semiconductor junction creates a built-in field that separates photogenerated electron-hole pairs.",
        "efficiency = electrical power out / solar power in"
    )
)

private val postgraduateCoreFacts = mapOf(
    "Advanced Quantum Chemistry" to af(
        "develops ab initio and correlated electronic-structure methods beyond introductory orbital models.",
        "A coupled-cluster calculation can improve a reaction energy after basis-set and correlation convergence are checked.",
        "Etotal = Ereference + Ecorrelation"
    ),
    "Advanced Thermodynamics" to af(
        "uses chemical potentials, fugacity and activity to treat multicomponent non-ideal equilibrium.",
        "Equality of chemical potential across phases determines liquid-vapour equilibrium at fixed temperature and pressure.",
        "dG = VdP - SdT + sum(mu_i dn_i)"
    ),
    "Statistical Thermodynamics" to af(
        "derives thermodynamic properties from molecular partition functions and ensembles.",
        "A molecular partition function combines translational, rotational, vibrational and electronic contributions.",
        "q = qtrans qrot qvib qelec"
    ),
    "Advanced Chemical Kinetics" to af(
        "treats complex mechanisms, fast reactions, transport coupling and modern rate theories.",
        "Applying the steady-state approximation to a reactive intermediate produces an observable composite rate law.",
        "d[intermediate]/dt approximately 0"
    ),
    "Photochemistry" to af(
        "studies excited-state formation, relaxation and chemical pathways initiated by light.",
        "A Jablonski diagram distinguishes absorption, fluorescence, intersystem crossing and phosphorescence.",
        "quantum yield = events / photons absorbed"
    ),
    "Electroanalytical Chemistry" to af(
        "uses potential, current, charge and impedance to identify and quantify chemical species.",
        "A cyclic voltammogram's peak separation and scan-rate response diagnose reversibility and diffusion control.",
        "ip proportional to n^(3/2) A D^(1/2) C v^(1/2)"
    ),
    "Supramolecular Chemistry" to af(
        "designs host-guest systems and self-assembled structures through reversible noncovalent interactions.",
        "A crown ether binds K+ selectively when cavity size and donor arrangement complement the ion.",
        "Ka = [host-guest]/([host][guest])"
    ),
    "Solid State Chemistry" to af(
        "connects crystal structure, defects and electronic organization with bulk material properties.",
        "Rietveld refinement compares a calculated diffraction pattern with observed powder XRD data.",
        "structure + defects + microstructure -> properties"
    ),
    "Organometallic Chemistry" to af(
        "uses metal-carbon compounds and elementary steps to understand catalytic cycles.",
        "Oxidative addition raises metal oxidation state and coordination number before reductive elimination forms a new bond.",
        "electron counting + elementary steps -> catalytic cycle"
    ),
    "Bioorganic Chemistry" to af(
        "applies organic mechanism and synthesis to biomolecules and biological transformations.",
        "A mechanism-based inhibitor forms a reactive intermediate only after enzyme processing, producing selective inhibition.",
        "molecular structure -> biochemical reactivity"
    ),
    "Bioinorganic Chemistry" to af(
        "examines metal-ion homeostasis, metalloenzymes, electron transfer and metal-based therapeutics.",
        "Zinc in carbonic anhydrase activates bound water for rapid CO2 hydration.",
        "metal coordination tunes acidity and redox potential"
    ),
    "Synthetic Organic Chemistry" to af(
        "plans and executes selective multistep construction using mechanism, chemoselectivity and protecting-group strategy.",
        "A convergent route couples two advanced fragments and can outperform a long linear sequence.",
        "overall yield = product of fractional step yields"
    ),
    "Retrosynthetic Analysis" to af(
        "works backward from a target through strategic disconnections to available synthons and starting materials.",
        "Disconnecting an alcohol beside the C-O bond suggests a carbonyl precursor and nucleophilic organometallic partner.",
        "target -> disconnection -> synthons -> reagents"
    ),
    "Green Chemistry" to af(
        "optimizes hazard, waste, energy and resource use at the molecular and process-design stages.",
        "Replacing a chlorinated solvent with a recyclable benign medium is evaluated with yield, E-factor and life-cycle evidence.",
        "E-factor = mass waste / mass product"
    ),
    "Nanochemistry" to af(
        "controls nucleation, growth, surface functionalization and nanoscale property emergence.",
        "A seed-mediated synthesis separates nucleation from growth to narrow a nanoparticle size distribution.",
        "nucleation rate versus growth rate controls particle size"
    ),
    "Surface Science" to af(
        "characterizes atomic composition, structure, adsorption and reactions at interfaces.",
        "XPS before and after adsorption reveals surface coverage and chemical-state shifts.",
        "surface excess and adsorption energy determine coverage"
    ),
    "Medicinal Chemistry" to af(
        "integrates target engagement, physicochemical properties, ADME, safety and synthetic feasibility during lead optimization.",
        "A bioisostere preserves binding geometry while reducing metabolic liability in an analogue series.",
        "potency + exposure + safety -> therapeutic profile"
    ),
    "Pharmaceutical Chemistry" to af(
        "connects drug substance chemistry with formulation, stability, manufacturing and quality.",
        "Accelerated stability data distinguish hydrolysis from oxidation and guide formulation and packaging.",
        "degradation often follows ln(C/C0) = -kt"
    ),
    "Food Chemistry" to af(
        "studies molecular composition, processing reactions, texture, flavour, nutrition and safety.",
        "Maillard browning between reducing sugars and amino groups creates colour and aroma during heating.",
        "composition + processing -> structure, flavour and stability"
    ),
    "Agricultural Chemistry" to af(
        "integrates soil, nutrient, pesticide and plant chemistry for productive and sustainable agriculture.",
        "A soil test and nutrient budget guide fertilizer dose while reducing nitrate leaching.",
        "nutrient balance = inputs - crop removal - losses"
    ),
    "Petrochemical Chemistry" to af(
        "converts hydrocarbon feedstocks through separation, cracking, reforming and catalytic functionalization.",
        "Catalytic reforming raises gasoline octane while producing hydrogen for refinery operations.",
        "feed composition + catalyst + conditions -> product slate"
    ),
    "Forensic Chemistry" to af(
        "applies validated analytical methods and defensible interpretation to legal evidence.",
        "GC-MS comparison of an unknown and reference supports identity only with controls, uncertainty and chain of custody.",
        "evidential conclusion = analytical result + uncertainty + context"
    ),
    "Geochemistry" to af(
        "studies elemental distribution and reactions among rocks, water, atmosphere and life.",
        "Stable-isotope ratios can distinguish sources and trace water-rock interaction.",
        "partition coefficient D = concentrationmineral / concentrationmelt"
    ),
    "Atmospheric Chemistry" to af(
        "models photochemical cycles, aerosols, transport and climate-active trace gases.",
        "Ozone production is diagnosed from coupled NOx, radicals and volatile-organic chemistry rather than a single reaction.",
        "concentration tendency = emissions + chemistry + transport - removal"
    ),
    "Marine Chemistry" to af(
        "examines seawater equilibria, trace elements, gases and ocean biogeochemical cycles.",
        "The carbonate system buffers seawater pH while added CO2 shifts speciation toward bicarbonate.",
        "dissolved inorganic carbon = [CO2*]+[HCO3-]+[CO3(2-)]"
    ),
    "Radiochemistry" to af(
        "uses radioactive transformations, tracer-level separations and nuclear measurements.",
        "An isotope-dilution analysis adds a known tracer and uses activity ratios to recover analyte amount.",
        "A = lambda N"
    ),
    "Astrochemistry" to af(
        "studies molecule formation and spectroscopy in interstellar, circumstellar and planetary environments.",
        "Rotational lines identify molecules in a cold cloud by matching multiple transitions at one velocity.",
        "observed spectrum -> molecular constants -> abundance"
    ),
    "Materials Science" to af(
        "links processing, structure and properties across atomic to macroscopic length scales.",
        "Changing heat treatment alters precipitate size and therefore alloy strength and toughness.",
        "processing -> structure -> properties -> performance"
    ),
    "Computational Chemistry" to af(
        "selects and validates quantum, molecular-simulation and data-driven methods for chemical prediction.",
        "A computed free-energy difference is reported only after sampling, force-field and convergence checks.",
        "prediction quality = model accuracy + sampling + validation"
    ),
    "Chemical Biology" to af(
        "uses designed molecules to measure, perturb and control biological systems.",
        "A selective covalent probe labels an active enzyme and is quantified by proteomic analysis.",
        "chemical probe + biological target -> measurable response"
    )
)

private val postgraduateTopicFacts = mapOf(
    "Homogeneous" to af(
        "catalysis uses catalyst and reactants in one phase, enabling molecularly defined active sites and tunable ligands.",
        "A soluble rhodium complex catalyses alkene hydrogenation through oxidative addition, coordination and reductive elimination.",
        "rate and selectivity depend on catalyst speciation"
    ),
    "Heterogeneous" to af(
        "catalysis occurs at interfaces between phases and depends on adsorption, active sites and transport.",
        "Ammonia synthesis on iron requires N2 and H2 adsorption, surface reaction and NH3 desorption.",
        "observed rate combines intrinsic kinetics and mass transfer"
    ),
    "Enzyme catalysis" to af(
        "uses structured active sites to achieve transition-state stabilization and selective biochemical conversion.",
        "Competitive inhibition raises apparent Km without changing Vmax in the simple Michaelis-Menten model.",
        "v = Vmax[S]/(Km(1+[I]/Ki)+[S])"
    ),
    "Photocatalysis" to af(
        "uses absorbed light to generate reactive excited states or electron-hole pairs that drive chemistry.",
        "Illuminated TiO2 creates carriers that form radicals able to oxidize an adsorbed contaminant.",
        "photon energy must meet or exceed the relevant excitation gap"
    ),
    "Unit operations" to af(
        "are physical processing steps such as mixing, filtration, distillation and drying used across chemical plants.",
        "A distillation column separates a volatile mixture through repeated vapour-liquid equilibrium stages.",
        "overall balance: input = output + accumulation"
    ),
    "Mass transfer" to af(
        "describes species transport by diffusion and convection across phases and equipment.",
        "A gas absorber transfers solute from gas to liquid under a concentration driving force.",
        "flux = mass-transfer coefficient x driving force"
    ),
    "Heat transfer" to af(
        "treats energy transport by conduction, convection and radiation.",
        "A counter-current heat exchanger uses a log-mean temperature difference to determine required area.",
        "Qdot = U A deltaTlm"
    ),
    "Reactor design" to af(
        "connects kinetics, flow, mixing and heat effects to conversion and selectivity.",
        "For a first-order reaction, a CSTR and plug-flow reactor require different volumes for the same conversion.",
        "design equation: V = flow x concentration change / rate"
    ),
    "GC-MS" to af(
        "couples gas chromatographic separation with mass-spectral identification of volatile analytes.",
        "Retention time plus a deconvoluted EI fragmentation spectrum identifies a solvent in a complex mixture.",
        "separation in time + diagnostic m/z pattern"
    ),
    "LC-MS/MS" to af(
        "couples liquid chromatography with tandem mass selection and fragmentation for sensitive targeted analysis.",
        "A precursor-to-product ion transition quantifies a drug against an isotopically labelled internal standard.",
        "response ratio -> calibration -> analyte concentration"
    ),
    "ICP-MS" to af(
        "ionizes an aerosol in an argon plasma and measures elemental isotopes by mass spectrometry.",
        "Internal standards and collision-cell conditions correct drift and polyatomic interference in trace-metal analysis.",
        "counts per second = sensitivity x elemental concentration"
    ),
    "AAS" to af(
        "measures element-specific absorption by free ground-state atoms.",
        "A hollow-cathode lamp and flame determine copper concentration from a matrix-matched calibration.",
        "A = epsilon b c within the working range"
    ),
    "ICP-OES" to af(
        "measures element-specific optical emission from plasma-excited atoms and ions.",
        "Multiple wavelengths quantify several metals while spectral overlaps and background are corrected.",
        "emission intensity is calibrated against concentration"
    ),
    "XRF" to af(
        "identifies and quantifies elements through characteristic secondary X-ray emission.",
        "A pressed geological pellet is analysed with matrix corrections and certified reference materials.",
        "X-ray line energy identifies element; intensity estimates amount"
    ),
    "SEM" to af(
        "scans a focused electron beam to image surface morphology and compositional contrast.",
        "Secondary electrons reveal fracture topography while backscattered electrons highlight atomic-number contrast.",
        "beam-sample signals -> spatially resolved image"
    ),
    "TEM" to af(
        "transmits electrons through an ultrathin specimen to resolve internal structure and diffraction.",
        "Lattice fringes and selected-area diffraction distinguish nanoparticle phases and orientations.",
        "electron wavelength enables sub-nanometre resolution"
    ),
    "AFM" to af(
        "maps surface forces with a nanoscale probe to obtain topography and mechanical information.",
        "Tapping mode images a polymer surface while reducing lateral damage compared with contact mode.",
        "cantilever deflection -> tip-sample force/topography"
    ),
    "Confocal microscopy" to af(
        "uses point illumination and a pinhole to reject out-of-focus light and build optical sections.",
        "A fluorescent sample is scanned through z to reconstruct a three-dimensional concentration distribution.",
        "optical sections -> 3D image stack"
    ),
    "Literature review" to af(
        "systematically finds, evaluates and synthesizes prior evidence to define a research gap.",
        "A reproducible database search records query, dates, inclusion criteria and reasons for excluding studies.",
        "research question -> search -> appraisal -> synthesis"
    ),
    "Experimental design" to af(
        "plans factors, controls, replication and randomization to estimate effects efficiently and without avoidable bias.",
        "A factorial design varies temperature and catalyst loading together and detects their interaction.",
        "response = main effects + interactions + error"
    ),
    "Scientific writing" to af(
        "communicates methods, evidence, uncertainty and interpretation in a reproducible scholarly form.",
        "A results section reports calibrated values and uncertainty, while discussion separates observation from mechanistic inference.",
        "claim + evidence + reasoning + limitation"
    ),
    "Data analysis" to af(
        "cleans, visualizes and models measurements while preserving provenance and uncertainty.",
        "Residual plots reveal curvature missed by a linear calibration and motivate a justified model change.",
        "data = model + residual"
    ),
    "Chemometrics" to af(
        "uses multivariate statistics to extract chemical information from complex measurements.",
        "PCA scores reveal sample groupings while loadings identify spectral variables responsible for separation.",
        "X approximately scores x loadings transpose + residual"
    ),
    "Air-sensitive synthesis" to af(
        "excludes oxygen and moisture when reagents or products react rapidly with air.",
        "A pyrophoric reagent is transferred by cannula under inert gas after leak-testing dry apparatus.",
        "risk control = exclusion + containment + quench plan"
    ),
    "Glove box" to af(
        "provides a sealed, purified inert atmosphere for manipulating sensitive materials.",
        "Solids enter through a purged antechamber while oxygen and water monitors confirm acceptable atmosphere.",
        "continuous purification maintains low O2 and H2O"
    ),
    "Schlenk line" to af(
        "combines vacuum and inert-gas manifolds for controlled evacuation, backfilling and transfer.",
        "A flask is cycled vacuum/inert gas before reagent addition and maintained under positive inert pressure.",
        "evacuate/backfill cycles reduce residual air"
    ),
    "High-pressure reactions" to af(
        "use rated reactors to access gas solubility, reaction rates and equilibria unavailable at ambient pressure.",
        "A hydrogenation is charged below vessel limits with pressure relief, remote monitoring and a defined cool-down.",
        "gas amount and pressure follow real-gas-corrected PV relations"
    ),
    "Cryogenic techniques" to af(
        "use very low temperatures to control reactivity, trap intermediates or study phase and quantum behaviour.",
        "A reaction at -78 C slows side pathways while controlled warming completes conversion.",
        "cooling changes rate constants and phase equilibria"
    ),
    "AI in Chemistry" to af(
        "applies learned models to molecular property prediction, synthesis planning and experimental decision support.",
        "A model ranks candidate reactions, but prospective experiments and uncertainty calibration test whether it generalizes.",
        "prediction = model(features); validation requires unseen data"
    ),
    "Machine Learning" to af(
        "learns quantitative patterns from chemical data using representations, objectives and validation.",
        "Nested cross-validation tunes a QSAR model without leaking test information into feature selection.",
        "generalization error measured on held-out data"
    ),
    "Quantum Computing for Chemistry" to af(
        "explores quantum algorithms for electronic-structure and molecular simulation problems.",
        "A variational quantum eigensolver estimates a small molecule ground-state energy with a classical optimizer.",
        "min_theta <psi(theta)|H|psi(theta)> approximates E0"
    ),
    "CRISPR Chemistry" to af(
        "examines molecular recognition, cleavage chemistry and chemical control of CRISPR systems.",
        "A guide RNA directs Cas nuclease to complementary DNA adjacent to a required motif for cleavage.",
        "sequence recognition + catalytic activation -> targeted editing"
    ),
    "Molecular Electronics" to af(
        "uses individual molecules or molecular assemblies as charge-transport components.",
        "A conjugated molecule between electrodes shows conductance that changes with length and contact chemistry.",
        "conductance depends exponentially on tunnelling distance"
    ),
    "Soft Matter" to af(
        "studies polymers, colloids, liquid crystals and membranes governed by energies comparable to thermal motion.",
        "Changing salt concentration alters electrostatic screening and assembly of charged colloids.",
        "structure emerges from interaction energy relative to kT"
    ),
    "MOFs" to af(
        "metal-organic frameworks are crystalline porous networks assembled from metal nodes and organic linkers.",
        "A MOF with open metal sites selectively adsorbs CO2, measured by an adsorption isotherm.",
        "node connectivity + linker geometry -> pore architecture"
    ),
    "COFs" to af(
        "covalent organic frameworks are ordered porous networks built from strong directional covalent bonds.",
        "Reversible bond formation enables error correction during synthesis and improves long-range order.",
        "monomer symmetry + linkage topology -> framework"
    ),
    "Click Chemistry" to af(
        "uses modular, selective, high-yielding reactions that operate reliably with simple purification.",
        "Cu(I)-catalysed azide-alkyne cycloaddition forms a 1,2,3-triazole linkage under mild conditions.",
        "azide + terminal alkyne -> triazole"
    ),
    "Flow Chemistry" to af(
        "performs reactions continuously in channels with controlled residence time, mixing and heat transfer.",
        "A hazardous intermediate is generated and consumed in-line, minimizing its inventory.",
        "residence time = reactor volume / volumetric flow"
    ),
    "Microfluidics" to af(
        "controls microlitre-to-picolitre fluids in channels where laminar flow and diffusion dominate.",
        "Droplet microfluidics creates uniform nanolitre reactors for high-throughput screening.",
        "Reynolds number is typically low; mixing is diffusion-controlled"
    )
)

private fun moduleApplication(module: String): String = when (module) {
    "Advanced Physical Chemistry" -> "These models predict molecular energies, equilibrium, phases and non-ideal behaviour in research and process design."
    "Advanced Organic Chemistry" -> "This reasoning guides selective synthesis of medicines, agrochemicals, probes and functional molecules."
    "Advanced Inorganic Chemistry" -> "These concepts support catalysts, magnetic materials, metalloenzymes and metal-based technologies."
    "Analytical Chemistry", "Spectroscopy", "Separation Techniques" ->
        "Laboratories use this method for defensible identification, quantification, purity testing and structure determination."
    "Computational Chemistry" -> "Researchers use computation to prioritize experiments, interpret measurements and predict molecular behaviour."
    "Polymer Chemistry", "Materials Chemistry", "Materials Science" ->
        "This concept connects processing and molecular structure with useful mechanical, electrical, optical or biological performance."
    "Industrial Chemistry" -> "Industrial chemists balance conversion, selectivity, safety, energy use, quality and environmental impact."
    "Environmental Chemistry", "Atmospheric Chemistry", "Marine Chemistry", "Geochemistry" ->
        "This chemistry supports monitoring, source attribution, risk assessment and evidence-based environmental control."
    "Biochemistry", "Bioorganic Chemistry", "Bioinorganic Chemistry", "Chemical Biology" ->
        "This concept links molecular structure and chemical reactivity with measurable biological function."
    "Medicinal Chemistry", "Pharmaceutical Chemistry" ->
        "Drug teams apply this evidence to optimize efficacy, exposure, formulation, stability and safety."
    "Instrumentation", "Research Methodology", "Advanced Laboratory Techniques" ->
        "Researchers use this practice to obtain reproducible, traceable and uncertainty-aware experimental evidence."
    "Emerging Areas" -> "This emerging capability is evaluated through chemical relevance, rigorous validation, scalability and responsible use."
    else -> "Advanced practitioners use this concept to connect quantitative chemical models with reproducible evidence and practical decisions."
}

private fun academicIcon(title: String): String {
    val initials = title.split(" ", "-", "/")
        .filter { it.isNotBlank() && it.lowercase() !in setOf("and", "in", "of", "for") }
        .joinToString("") { it.first().uppercase() }
    return initials.take(4).ifBlank { "CHEM" }
}

internal val levelFourFiveModuleIcons: Map<String, String> =
    (levelFourTopics.keys.map { "Undergraduate|$it" } + levelFiveTopics.keys.map { "Postgraduate|$it" })
        .associateWith { key -> academicIcon(key.substringAfter("|")) }

private fun buildAcademicContent(
    level: String,
    module: String,
    concept: String,
    fact: AdvancedFact
): LevelOneConceptContent {
    val summary = if (concept == "Core principles and applications") {
        "$module ${fact.focus}"
    } else {
        "$concept ${fact.focus}"
    }
    val explanation =
        "At $level level, use the stated model quantitatively, identify its assumptions and connect every prediction with experimental or computational evidence. " +
            "For $module, compare plausible alternatives, preserve units and chemical constraints, and report uncertainty or limitations before accepting a conclusion."
    val example = "Example: ${fact.example}"
    val misconception =
        "Do not treat $concept as a label or a result from one instrument alone. Check calibration or model assumptions, chemical plausibility, uncertainty and independent evidence."
    return LevelOneConceptContent(
        summary = summary,
        explanation = explanation,
        workedExample = if (example.length >= 80) example else
            "$example Interpret the result using the stated model and verify it against an independent observation.",
        keyRelationship = fact.relationship,
        commonMistake = misconception,
        application = moduleApplication(module),
        connections = listOf(module, "Quantitative modelling and validation", "Experimental evidence and uncertainty")
    )
}

private fun factFor(module: String, concept: String): AdvancedFact =
    advancedFacts["$module|$concept"]
        ?: advancedFacts[concept]
        ?: postgraduateTopicFacts[concept]
        ?: postgraduateCoreFacts[module]
        ?: af(
            "integrates the central theories, methods and applications of $module.",
            "A representative problem compares a quantitative prediction with calibrated evidence and explains any mismatch.",
            "model + measurement + uncertainty -> defensible conclusion"
        )

internal val levelFourFiveConceptContent: Map<String, LevelOneConceptContent> =
    buildMap {
        levelFourTopics.forEach { (module, concepts) ->
            concepts.forEach { concept ->
                put(
                    "Undergraduate|$module|$concept",
                    buildAcademicContent("undergraduate", module, concept, factFor(module, concept))
                )
            }
        }
        levelFiveTopics.forEach { (module, concepts) ->
            concepts.forEach { concept ->
                put(
                    "Postgraduate|$module|$concept",
                    buildAcademicContent("postgraduate", module, concept, factFor(module, concept))
                )
            }
        }
    }
