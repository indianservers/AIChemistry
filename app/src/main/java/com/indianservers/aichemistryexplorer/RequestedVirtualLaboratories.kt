package com.indianservers.aichemistryexplorer

/**
 * Original implementations of the 164 chemistry-relevant laboratory experiences
 * supplied for this project. The number is retained as a stable acceptance key.
 */
internal data class RequestedLabInventoryEntry(
    val number: Int,
    val title: String,
    val category: String
)

internal data class RequestedProtocol(
    val technique: LabTechnique,
    val objectiveAction: String,
    val scientificBasis: String,
    val apparatus: String,
    val reagents: String,
    val safety: String,
    val procedure: List<String>,
    val measuredQuantity: String,
    val referenceValue: Double,
    val unit: String,
    val analysis: String
)

private val requestedTitles = """
General chemistry laboratory orientation
Laboratory safety inspection
Chemistry hazard symbols
Chemical risk assessment
Chemical-waste segregation
Fume-hood operation
Biosafety fundamentals
Scientific-method investigation
Experimental-design laboratory
Measurement uncertainty
Density measurement
Dimensional analysis
Significant figures in experiments
Logarithms and exponential calculations
Micropipette selection
Micropipetting technique
Analytical-balance operation
Volumetric glassware selection
Preparing a laboratory notebook
Calibration-curve construction
Physical versus chemical changes
Homogeneous versus heterogeneous mixtures
Atom, ion and isotope construction
Bohr versus quantum atomic models
Interactive periodic-table organization
Ionic and covalent compound identification
Intermolecular-force investigation
Ideal Gas Law introduction
Gas-law medical application
Constructing a temperature scale
Heating curves and phase changes
Ethanol distillation
Simple biodiesel-waste distillation
Separating mixtures by distillation
Properties of water
Preparing a solution from a solid
Serial dilution
Acid–base titration
Advanced acid–base equilibria
Everyday acidity and alkalinity
Chemical-equilibrium perturbation
Reaction-kinetics fundamentals
Bomb calorimetry
Reaction energy surfaces
Spontaneity investigation
Avogadro-number calculations
Gravimetric stoichiometry
Compound identification by gravimetric analysis
Qualitative elemental analysis
Formula construction and equation balancing
Double-displacement reactions
Single-displacement and seawater corrosion
Calcium-carbonate decomposition
Environmental synthesis reactions
Battery redox chemistry
Electrolysis and electrode products
Electroplating
Nuclear chemistry investigation
Radioactive-decay identification
Nuclear-fission energy
Nuclear-fusion energy
Photoelectric effect and solar panels
Organic-chemistry laboratory introduction
Carbon valence and hybridization
Hydrocarbon nomenclature and representations
Aromatic-compound nomenclature
Functional-group identification
Ceric ammonium nitrate alcohol test
Bromine unsaturation test
Sodium bicarbonate carboxylic-acid test
Litmus test for carboxylic acids
Alkane reactivity
Alkene structure and reactions
Alkyl-halide reactivity
Organometallic-reactivity introduction
Electrophilic addition
Markovnikov product prediction
Electrophilic aromatic substitution
Molecular-resonance investigation
Nucleophilic substitution
Grignard nucleophilic addition
Substitution versus elimination
Elimination synthesis
Stereocentres and E/Z isomerism
Recrystallization and solid purification
Synthetic-polymer preparation
Biodiesel synthesis
Organic-product purity analysis
Infrared-spectrum acquisition
Infrared functional-group interpretation
Proton-NMR acquisition
Proton-NMR structure interpretation
Small-protein NMR
Mass-spectrometer operation
Fragmentation-pattern interpretation
MALDI sample analysis
HPLC method development
Gas-chromatography data analysis
Thin-layer chromatography
Size-exclusion chromatography
Ion-exchange chromatography
UV–visible spectrophotometry
Beer–Lambert calibration
Unknown-element qualitative analysis
Unknown-compound identification
Analytical-method validation
Iodine test for starch
Benedict’s test for reducing sugars
Sudan IV lipid test
Biuret protein test
Food-macromolecule identification
Enzyme-kinetics experiment
Competitive and noncompetitive inhibition
Glycolysis pathway investigation
Krebs-cycle investigation
Cellular-respiration energy measurement
DNA structure and function
Nucleic-acid gel electrophoresis
SDS-PAGE protein separation
Western-blot transfer
Western-blot detection
ELISA protein quantification
Protein-synthesis introduction
Protein-synthesis experiment
Polymerase chain reaction
Molecular cloning
Gene-regulation investigation
Synthetic-biology design
Viral gene-therapy design
Antibody immunoassay
Cancer-sample preparation for mass spectrometry
Diabetes metabolism
Parkinson’s-disease biochemistry
Osmosis and intravenous-solution selection
Fluorescence microscopy
Light microscopy
Confocal microscopy
Concrete aggregate grading
Concrete slump testing
Concrete composition optimization
Bragg’s-law neutron scattering
Neutron diffraction
Neutron transmission imaging
Quasi-elastic neutron scattering
Lithium-ion movement in battery materials
Temperature-dependent battery performance
Crystal-defect analysis
Semiconductor-property investigation
Material stress–property comparison
Wastewater-treatment process
Eutrophication analysis
Dissolved oxygen versus temperature
Coal-power environmental assessment
Fertilizer and phosphorus-cycle investigation
Human impacts on the water cycle
Nitrogen-cycle balancing
Carbon-cycle emissions
Acid-lake neutralization
Marine fish-death chemical investigation
Biodiesel production and waste recycling
Milk homogenization and colloids
Roman-concrete limestone cycle
Seawater-corrosion investigation
Sustainable-process comparison
""".trimIndent().lines()

private fun requestedCategory(number: Int): String = when (number) {
    in 1..20 -> "Laboratory foundations"
    in 21..62 -> "General chemistry laboratories"
    in 63..88 -> "Organic chemistry laboratories"
    in 89..106 -> "Analytical and instrumental laboratories"
    in 107..137 -> "Biochemistry and chemical biology"
    in 138..149 -> "Materials chemistry laboratories"
    else -> "Environmental and industrial chemistry"
}

internal val requestedLabInventory: List<RequestedLabInventoryEntry> =
    requestedTitles.mapIndexed { index, title ->
        RequestedLabInventoryEntry(index + 1, title, requestedCategory(index + 1))
    }

private fun RequestedLabInventoryEntry.protocol(): RequestedProtocol {
    val text = title.lowercase()
    return when {
        number <= 20 -> foundationsProtocol(title)
        number in 21..62 -> generalChemistryProtocol(title)
        listOf("infrared", "nmr", "mass spect", "maldi", "uv–visible", "beer–lambert").any { it in text } ->
            spectroscopyProtocol(title)
        listOf("hplc", "chromatography", "recrystallization", "distillation", "purification").any { it in text } ->
            separationProtocol(title)
        listOf("gel electrophoresis", "sds-page", "western-blot", "elisa", "immunoassay", "microscopy").any { it in text } ->
            bioanalysisProtocol(title)
        listOf("enzyme", "glycolysis", "krebs", "respiration", "metabolism", "inhibition").any { it in text } ->
            kineticsProtocol(title, biochemical = true)
        listOf("dna", "protein-synthesis", "polymerase chain", "molecular cloning", "gene-", "synthetic-biology", "gene-therapy").any { it in text } ->
            molecularBiologyProtocol(title)
        listOf("concrete", "neutron", "battery performance", "battery materials", "crystal-defect", "semiconductor", "material stress").any { it in text } ->
            materialsProtocol(title)
        number >= 150 -> environmentalProtocol(title)
        listOf("titration", "acidity", "alkalinity", "acid–base").any { it in text } -> titrationProtocol(title)
        listOf("calorimetry", "energy surface", "spontaneity", "heating curve", "phase change").any { it in text } ->
            calorimetryProtocol(title)
        listOf("gas law", "ideal gas").any { it in text } -> gasProtocol(title)
        listOf("equilibrium", "intermolecular", "properties of water").any { it in text } -> equilibriumProtocol(title)
        listOf("kinetic", "reaction rate").any { it in text } -> kineticsProtocol(title)
        listOf("battery redox", "electrolysis", "electroplating", "corrosion", "single-displacement").any { it in text } ->
            electrochemistryProtocol(title)
        listOf("gravimetric", "density").any { it in text } -> gravimetryProtocol(title)
        listOf("test", "identification", "qualitative", "unknown", "functional-group").any { it in text } ->
            qualitativeProtocol(title)
        listOf("synthesis", "polymer", "biodiesel", "grignard", "addition", "substitution", "elimination", "reactivity").any { it in text } ->
            synthesisProtocol(title)
        else -> modelProtocol(title)
    }
}

private fun foundation(
    technique: LabTechnique = LabTechnique.PHYSICAL_MEASUREMENT,
    action: String,
    basis: String,
    apparatus: String,
    materials: String,
    safety: String,
    measure: String,
    target: Double,
    unit: String,
    analysis: String,
    steps: List<String>
) = RequestedProtocol(
    technique = technique,
    objectiveAction = action,
    scientificBasis = basis,
    apparatus = apparatus,
    reagents = materials,
    safety = safety,
    procedure = steps.toList(),
    measuredQuantity = measure,
    referenceValue = target,
    unit = unit,
    analysis = analysis
)

/**
 * Deliberately exhaustive: adding or renaming a foundation lab must provide a
 * dedicated protocol instead of silently receiving generic laboratory text.
 */
private fun foundationsProtocol(title: String): RequestedProtocol = when (title) {
    "General chemistry laboratory orientation" -> foundation(
        action = "navigate a general chemistry laboratory and complete a safe start-to-close workflow",
        basis = "Laboratory zones separate chemical storage, measurement, reaction, emergency response and waste operations so incompatible activities cannot create uncontrolled exposure.",
        apparatus = "Room map · eyewash and safety shower · extinguisher · spill kit · gas/electric isolation · first-aid station",
        materials = "Orientation checklist · emergency contact card · mock spill marker · equipment-isolation labels",
        safety = "Keep exits and emergency equipment unobstructed; never begin experimental work until eyewash, shower, exits and utility shutoffs have been physically located.",
        measure = "orientation competency", target = 100.0, unit = "%",
        analysis = "Score critical items separately from general items: every emergency-location and shutdown item must be correct; an average score cannot compensate for a critical miss.",
        steps = listOf(
        "Trace the entry route and identify coat, bag and personal-item storage outside chemical work zones.",
        "Locate the eyewash, shower, fire alarm, extinguisher, spill kit, exits and emergency telephone.",
        "Demonstrate how to isolate gas, electricity and water without operating an emergency device unnecessarily.",
        "Sort example activities into weighing, wet chemistry, fume-hood, instrument and waste zones.",
        "Respond verbally to spill, splash, fire and evacuation scenarios using the local emergency sequence.",
        "Complete the opening and close-out checklists and pass every critical orientation checkpoint."
    ))
    "Laboratory safety inspection" -> foundation(
        technique = LabTechnique.QUALITATIVE,
        action = "conduct and document a risk-based laboratory safety inspection",
        basis = "A systematic inspection converts observable conditions into corrective actions ranked by severity, likelihood and exposure frequency; evidence and ownership make findings auditable.",
        apparatus = "Inspection map · lux/clearance guides · secondary-containment check · emergency-equipment tags · camera/notebook",
        materials = "Seeded inspection scenarios · inspection checklist · corrective-action register · status labels",
        safety = "Do not touch, open or relocate suspect containers during inspection; isolate the area and escalate leaking, pressurized, unlabeled or peroxide-forming materials.",
        measure = "critical-hazard detection", target = 100.0, unit = "%",
        analysis = "Calculate detected seeded hazards and false positives, then rank findings. Passing requires every critical hazard plus a named owner and deadline for every corrective action.",
        steps = listOf(
        "Review the inspection scope, previous open actions and laboratory-specific hazards.",
        "Inspect access, housekeeping, storage compatibility, labels, ventilation indicators and emergency clearances.",
        "Check eyewash/shower inspection tags, extinguisher access, spill supplies and utility isolation.",
        "Record objective evidence and classify each finding as critical, major, minor or observation.",
        "Assign containment, corrective owner, due date and verification evidence for every finding.",
        "Reinspect the seeded hazards and close only actions whose controls have been physically verified."
    ))
    "Chemistry hazard symbols" -> foundation(
        technique = LabTechnique.QUALITATIVE,
        action = "interpret GHS pictograms, signal words and hazard/precautionary statements in realistic label decisions",
        basis = "GHS communication combines pictogram, signal word, hazard statement and precautionary statement; a pictogram alone does not specify concentration, route, severity or required control.",
        apparatus = "GHS label workstation · SDS viewer · storage cabinet map · PPE compatibility chart",
        materials = "Representative acid, oxidizer, solvent, compressed-gas and chronic-toxicity labels · decoy labels",
        safety = "Never infer handling or disposal from color or pictogram alone; verify the current SDS, concentration, physical form and local risk assessment.",
        measure = "label interpretation accuracy", target = 100.0, unit = "%",
        analysis = "Score pictogram meaning, signal word, exposure route, incompatibility and precaution independently; critical incompatibility errors are automatic failures.",
        steps = listOf(
        "Match each GHS pictogram to its hazard class without using container color as a cue.",
        "Read Section 2 of the SDS and reconcile pictograms with signal and hazard statements.",
        "Use Sections 7 and 10 to identify handling, storage and incompatibility requirements.",
        "Select engineering control, PPE and emergency response for each labeled scenario.",
        "Detect incomplete or contradictory workplace labels and quarantine the container.",
        "Create a compliant secondary-container label and justify every included element."
    ))
    "Chemical risk assessment" -> foundation(
        action = "construct a task-specific chemical risk assessment and verify the hierarchy of controls",
        basis = "Risk is determined by hazard together with exposure likelihood and consequence; substitution and engineering controls are more reliable than dependence on behavior or PPE alone.",
        apparatus = "SDS database · process flow sheet · likelihood–severity matrix · control-verification checklist",
        materials = "Procedure scenarios · quantity/concentration cards · exposure-route cards · substitution options",
        safety = "Risk assessment must occur before material release; stop when hazards, quantities, reaction energy, pressure, exposure route or emergency controls are unknown.",
        measure = "residual-risk control score", target = 100.0, unit = "%",
        analysis = "Calculate initial and residual risk for each task step and verify controls in hierarchy order. High residual risk requires redesign or supervisor authorization, not additional unsupported PPE.",
        steps = listOf(
        "Break the proposed experiment into receiving, dispensing, reaction, work-up, cleanup and waste steps.",
        "Identify intrinsic hazards, quantities, concentrations, energy sources and credible exposure routes.",
        "Assign initial likelihood and severity using evidence from the SDS and process conditions.",
        "Apply elimination, substitution, engineering, administrative and PPE controls in that order.",
        "Test whether each control is present, functional and suitable for the failure scenario.",
        "Record residual risk, stop-work triggers, emergency response and authorization status."
    ))
    "Chemical-waste segregation" -> foundation(
        technique = LabTechnique.QUALITATIVE,
        action = "classify, label and segregate laboratory waste without creating incompatible mixtures",
        basis = "Waste compatibility depends on chemical identity and reactivity, not only physical state; halogenated solvents, oxidizers, reactive metals, acids/bases, heavy metals, sharps and biological waste require controlled routes.",
        apparatus = "Compatible waste containers · secondary containment · closed funnel · label printer · compatibility matrix",
        materials = "Mock aqueous, solvent, metal, oxidizer, reactive-solid, sharps and contaminated-consumable waste cards",
        safety = "Never mix unknowns or perform unapproved neutralization; close containers except during addition, preserve headspace and isolate bulging, hot, fuming or leaking waste.",
        measure = "waste-routing accuracy", target = 100.0, unit = "%",
        analysis = "Build a compatibility matrix and require correct stream, container, label, closure and storage location for every item; one incompatible combination fails the exercise.",
        steps = listOf(
        "Identify each waste from the experiment record and SDS rather than appearance.",
        "Separate halogenated/non-halogenated solvents, metals, oxidizers, acids/bases, reactives, solids and sharps.",
        "Select a compatible container with adequate headspace and secure closure.",
        "Complete the label with full constituents, approximate percentages, hazards and accumulation date.",
        "Place containers in compatible secondary containment at the designated accumulation point.",
        "Audit the final arrangement for incompatibilities, unknowns, open funnels, damage and collection readiness."
    ))
    "Fume-hood operation" -> foundation(
        action = "set up, verify and operate a chemical fume hood within its containment envelope",
        basis = "A fume hood controls inhalation exposure through directional airflow; sash position, face velocity, cross-drafts, clutter and equipment placement determine containment.",
        apparatus = "Chemical fume hood · airflow monitor · sash stop · smoke visualization simulator · equipment blocks",
        materials = "Volatile-process scenario · inspection tag · airflow acceptance range · spill tray",
        safety = "A hood is not a storage cabinet or explosion shield. Stop work for an airflow alarm, failed inspection, damaged sash or uncontrolled cross-draft and keep your head outside the hood plane.",
        measure = "containment-control compliance", target = 100.0, unit = "%",
        analysis = "Pass only when airflow status, sash height, six-inch setback, airfoil clearance, cross-draft control and shutdown behavior all meet the local hood SOP.",
        steps = listOf(
        "Check the current inspection label, airflow monitor and physical condition before loading equipment.",
        "Remove unnecessary chemicals and place required apparatus at least 15 cm behind the sash plane.",
        "Raise large equipment to preserve rear-baffle flow and keep the front airfoil unobstructed.",
        "Set the sash at or below the marked operating height and minimize rapid movement and cross-drafts.",
        "Respond to a simulated airflow alarm by capping sources, lowering the sash and stopping the procedure.",
        "Close materials, decontaminate the surface, remove apparatus and lower the sash after work."
    ))
    "Biosafety fundamentals" -> foundation(
        technique = LabTechnique.QUALITATIVE,
        action = "apply containment, aseptic workflow and decontamination to a basic biosafety task",
        basis = "Biosafety combines risk-group assessment, containment level, aerosol control, aseptic technique, exposure response and validated decontamination to interrupt transmission.",
        apparatus = "Class II biosafety-cabinet simulator · sealed rotor · biohazard bin · sharps container · disinfectant station",
        materials = "Nonpathogenic teaching sample · sterile consumables · biological spill kit · contact-time timer",
        safety = "Treat all biological material according to assigned containment; minimize aerosols, never recap contaminated sharps and report splashes, needlesticks or containment failures immediately.",
        measure = "biosafety workflow integrity", target = 100.0, unit = "%",
        analysis = "Map each operation to its containment and decontamination control. Critical failures include broken clean-to-dirty flow, aerosol release, incorrect sharps handling or shortened disinfectant contact time.",
        steps = listOf(
        "Review agent, procedure and host factors to select containment and required PPE.",
        "Establish clean-to-dirty cabinet flow without blocking front or rear grilles.",
        "Perform a mock transfer using aseptic technique and aerosol-resistant handling.",
        "Contain a simulated spill and apply disinfectant from perimeter inward for full contact time.",
        "Place cultures, contaminated solids, liquids and sharps into their validated treatment streams.",
        "Remove PPE in contamination-minimizing order, wash hands and document exposure-free close-out."
    ))
    "Scientific-method investigation" -> foundation(
        action = "complete a falsifiable scientific-method investigation using blinded evidence",
        basis = "A scientific claim must generate a testable prediction; controlled observations can support, refine or reject it but cannot prove a universal hypothesis from one experiment.",
        apparatus = "Investigation station · randomized sample set · calibrated sensor · analysis notebook",
        materials = "Positive and negative controls · blinded unknowns · reference material · replicate sample set",
        safety = "Complete the method-specific risk assessment before testing and preserve blinding without bypassing labels, containment or sample-tracking rules.",
        measure = "prediction accuracy on blinded samples", target = 90.0, unit = "%",
        analysis = "Pre-register the hypothesis, predicted direction and decision threshold; compare blinded outcomes with predictions and distinguish observations, calculations and interpretations.",
        steps = listOf(
        "Turn the research question into a falsifiable hypothesis and directional prediction.",
        "Define independent, dependent and controlled variables plus a decision threshold.",
        "Design positive/negative controls and a randomized blinded sample sequence.",
        "Calibrate the measurement system and collect raw replicate observations.",
        "Analyze all trials using the pre-declared rule before revealing sample identities.",
        "Compare prediction with evidence, revise the explanation and propose a discriminating next test."
    ))
    "Experimental-design laboratory" -> foundation(
        action = "design an efficient, randomized and replicated chemistry experiment",
        basis = "Valid design separates factor effects from noise through controls, randomization, replication, blocking and adequate measurement range while avoiding confounding and pseudoreplication.",
        apparatus = "Design-of-experiments board · randomization tool · power/precision estimator · run-order worksheet",
        materials = "Factor cards · nuisance-variable cards · control scenarios · simulated pilot dataset",
        safety = "Design optimization never overrides safe operating limits; exclude factor combinations outside the approved chemical, temperature and pressure envelope.",
        measure = "estimable design terms", target = 100.0, unit = "%",
        analysis = "Construct the design matrix, verify independence and estimability, distinguish technical from biological/experimental replicates and justify sample size from target precision.",
        steps = listOf(
        "Define the response, factors, levels, experimental unit and safe operating envelope.",
        "Identify nuisance variables and decide which to control, randomize or block.",
        "Add negative, positive and process controls that answer distinct validity questions.",
        "Generate a balanced randomized run order with independent replication.",
        "Use pilot variance to estimate replication needed for the target precision or effect.",
        "Audit the matrix for confounding, pseudoreplication and missing interaction information."
    ))
    "Measurement uncertainty" -> foundation(
        action = "construct and report a complete measurement-uncertainty budget",
        basis = "A result is incomplete without uncertainty; Type A repeatability and Type B calibration, resolution and method components combine by sensitivity coefficients and root-sum-of-squares.",
        apparatus = "Calibrated balance · Class A pipette · thermometer · spreadsheet uncertainty budget",
        materials = "Certified check mass · deionized water · calibration certificates · replicate dataset",
        safety = "Handle glassware and reference materials using their SOPs; do not invent precision when calibration status or environmental conditions are uncontrolled.",
        measure = "expanded uncertainty coverage", target = 95.0, unit = "%",
        analysis = "Convert each source to standard uncertainty, multiply by its sensitivity coefficient, combine as uc = √Σ(ciui)² and report U = kuc with coverage factor and units.",
        steps = listOf(
        "Write the measurand equation and identify every input quantity and unit.",
        "Collect independent replicate measurements for the Type A component.",
        "Extract calibration, tolerance, resolution and environmental Type B components.",
        "Convert rectangular, triangular or normal distributions to standard uncertainties.",
        "Combine components with sensitivity coefficients and calculate expanded uncertainty.",
        "Report result ± U, coverage factor, confidence interpretation and dominant contributors."
    ))
    "Density measurement" -> foundation(
        action = "determine solid and liquid density with temperature and buoyancy-aware controls",
        basis = "Density is mass per calibrated volume at a stated temperature; bubbles, meniscus reading, evaporation, trapped liquid and porous samples produce systematic bias.",
        apparatus = "Analytical balance · pycnometer · Class A cylinder · displacement vessel · calibrated thermometer",
        materials = "Deionized-water volume reference · unknown liquid · nonporous solid · lint-free tissue",
        safety = "Treat unknown liquids as volatile and flammable until identified; cap promptly, avoid inhalation and collect them as unknown organic or aqueous waste after classification.",
        measure = "density accuracy", target = 99.0, unit = "%",
        analysis = "Calculate ρ = (mfilled − mempty)/V(T), correct the calibrated volume for temperature where required and compare liquid and displacement methods with propagated uncertainty.",
        steps = listOf(
        "Verify the balance and determine the vessel volume using water at recorded temperature.",
        "Clean, dry and tare the pycnometer; fill without bubbles and equilibrate thermally.",
        "Weigh three independent liquid fillings with caps in place to limit evaporation.",
        "Measure the solid by direct mass and displacement while removing surface bubbles.",
        "Calculate replicate densities and inspect method-dependent bias.",
        "Report density at the measured temperature with uncertainty and a justified identity range."
    ))
    "Dimensional analysis" -> foundation(
        action = "execute multi-stage chemical calculations using unit-factor dimensional analysis",
        basis = "Units behave algebraically; conversion factors equal one and must cancel explicitly, making dimensional inconsistency visible before a numerical answer is accepted.",
        apparatus = "Unit-factor workboard · SI reference table · balance/glassware data cards · calculator",
        materials = "Mass, volume, concentration, pressure, energy and amount-of-substance challenge datasets",
        safety = "A unit error can create dangerous scale, dose, pressure or concentration errors; calculations controlling real dispensing require independent verification.",
        measure = "dimensionally valid calculations", target = 100.0, unit = "%",
        analysis = "Write every quantity with its unit, orient conversion factors to cancel, preserve exact versus measured numbers and reject any result whose final dimension does not match the measurand.",
        steps = listOf(
        "State the requested measurand, target unit and given quantities.",
        "Map the conversion path before entering any numbers.",
        "Write conversion factors with numerator and denominator units visible.",
        "Cancel units algebraically and flag exact conversion factors.",
        "Estimate order of magnitude, compute and compare with the estimate.",
        "Perform an independent reverse conversion and approve only dimensionally correct results."
    ))
    "Significant figures in experiments" -> foundation(
        action = "record and calculate experimental results without false precision",
        basis = "Instrument resolution limits recorded digits; multiplication/division follows relative precision while addition/subtraction follows decimal-place precision, with rounding deferred to the final result.",
        apparatus = "Analog and digital measuring devices · balance displays · burette scale · calculation worksheet",
        materials = "Raw readings with mixed resolution · exact count values · calibration uncertainty cards",
        safety = "Never round intermediate values used for safety-critical preparation; retain guard digits and independently verify the final dispensed quantity.",
        measure = "precision-reporting accuracy", target = 100.0, unit = "%",
        analysis = "Classify exact and measured quantities, retain guard digits, apply operation-specific rounding once at the end and ensure uncertainty and value share the same decimal place.",
        steps = listOf(
        "Read analog scales one estimated digit beyond the smallest graduation.",
        "Record digital displays exactly without adding unsupported zeros.",
        "Separate exact counts and defined conversions from measured quantities.",
        "Complete addition/subtraction and multiplication/division examples with guard digits.",
        "Round only the final result and align its decimal place with uncertainty.",
        "Audit a mock report for premature rounding, ambiguous zeros and false precision."
    ))
    "Logarithms and exponential calculations" -> foundation(
        action = "solve and validate logarithmic and exponential chemistry relationships",
        basis = "Logarithms invert exponentials: pH = −log10[H+], integrated rate laws linearize exponential decay, and Arrhenius behavior uses ln k versus reciprocal absolute temperature.",
        apparatus = "Scientific calculator · pH dataset · kinetic time series · Arrhenius plotting worksheet",
        materials = "Hydrogen-ion standards · decay dataset · rate constants at known temperatures",
        safety = "Calculator results do not validate chemical plausibility; reject impossible concentration signs, non-kelvin Arrhenius temperatures and unlogged unitful arguments.",
        measure = "logarithmic-model accuracy", target = 99.0, unit = "%",
        analysis = "Verify log base, use dimensionless ratios, back-transform every result and inspect whether pH, concentration, half-life and activation energy have plausible signs and magnitudes.",
        steps = listOf(
        "Distinguish log10, natural log and their inverse functions on the calculator.",
        "Convert hydrogen-ion concentration to pH and back-transform to verify it.",
        "Fit ln(concentration) versus time and recover a first-order rate constant.",
        "Plot ln k versus 1/T using kelvin and calculate activation energy from slope.",
        "Track significant figures through logarithms using decimal-place rules.",
        "Reject seeded base, sign and temperature-unit errors through independent back-calculation."
    ))
    "Micropipette selection" -> foundation(
        action = "select the smallest suitable micropipette and tip for each transfer",
        basis = "Air-displacement pipettes are most reliable within their specified volume range; accuracy and precision deteriorate near limits and volatile, viscous or dense liquids may require different technique or positive displacement.",
        apparatus = "P2/P10/P20/P200/P1000 pipettes · compatible filtered/unfiltered tips · selection chart · balance",
        materials = "Aqueous dye · glycerol solution · volatile-solvent scenario cards · transfer-volume series",
        safety = "Never pipette corrosive or volatile liquid without confirming pipette compatibility and containment; keep pipettes upright when liquid is in the tip.",
        measure = "correct pipette-and-tip selection", target = 100.0, unit = "%",
        analysis = "Choose the lowest-capacity pipette whose range contains the target, verify tip compatibility and calculate whether required tolerance is achievable from the manufacturer specification.",
        steps = listOf(
        "Inspect calibration status and identify the minimum/maximum range of each pipette.",
        "Match target volumes to the smallest pipette that contains them without exceeding limits.",
        "Choose filtered, low-retention or positive-displacement options from liquid properties and contamination risk.",
        "Set sample volumes and detect impossible or ambiguous display settings.",
        "Verify representative selections gravimetrically with water.",
        "Document selection, tip, liquid class, expected tolerance and any required technique adjustment."
    ))
    "Micropipetting technique" -> foundation(
        action = "perform accurate forward and reverse micropipetting with contamination control",
        basis = "Pre-wetting, immersion depth, vertical aspiration, smooth plunger speed, dwell time and correct blow-out determine delivered volume; gravimetric testing converts water mass to volume.",
        apparatus = "Calibrated micropipette · compatible tips · analytical balance · evaporation trap · temperature probe",
        materials = "Deionized water · colored practice sample · low-retention tips · weigh boats",
        safety = "Use aerosol-resistant tips for contamination-sensitive or biological samples; never lay a loaded pipette down and eject tips directly into the assigned waste.",
        measure = "gravimetric delivery accuracy", target = 99.0, unit = "%",
        analysis = "Convert water mass to volume using temperature-dependent density; calculate systematic error and CV over ten deliveries and compare with pipette acceptance limits.",
        steps = listOf(
        "Confirm pipette range and calibration, attach the correct tip and pre-wet it three times.",
        "Aspirate vertically at controlled immersion depth using the first stop and a short dwell.",
        "Withdraw smoothly, touch off exterior droplets and dispense against the receiving wall.",
        "Use the second stop only for blow-out, then withdraw before releasing the plunger.",
        "Repeat ten gravimetric deliveries and perform reverse pipetting for a viscous sample.",
        "Calculate mean volume, systematic error and coefficient of variation; troubleshoot failed performance."
    ))
    "Analytical-balance operation" -> foundation(
        action = "operate an analytical balance using verified, contamination-free weighing practice",
        basis = "Analytical balances respond to drafts, vibration, temperature gradients, static and buoyancy; leveling, warm-up, check masses and closed doors are essential for traceable mass.",
        apparatus = "0.1 mg analytical balance · certified check masses · antistatic device · weighing vessel · forceps",
        materials = "Stable granular sample · hygroscopic sample scenario · weighing paper/boat · cleaning brush",
        safety = "Never weigh hot, wet, volatile or corrosive material directly on the pan; use containment and clean spills only by the balance SOP.",
        measure = "check-mass recovery", target = 100.0, unit = "%",
        analysis = "Apply tare and weighing-by-difference correctly; evaluate repeatability and check-mass error against tolerance while retaining every raw displayed digit.",
        steps = listOf(
        "Inspect cleanliness, calibration status and level; allow the balance and sample to equilibrate.",
        "Verify zero and measure a certified check mass with doors closed.",
        "Tare a clean vessel and transfer sample without contacting the pan.",
        "Record a stable direct mass, then repeat using weighing by difference.",
        "Assess drift/static by repeat readings and apply the approved mitigation.",
        "Remove the sample, clean the chamber and document check result, mass and uncertainty."
    ))
    "Volumetric glassware selection" -> foundation(
        action = "select and use volumetric glassware according to delivery/containment purpose and tolerance",
        basis = "Volumetric flasks are calibrated to contain, pipettes and burettes to deliver, while graduated cylinders provide lower accuracy and beakers provide approximate volume only.",
        apparatus = "Class A flask · volumetric and graduated pipettes · burette · cylinder · beaker · tolerance table",
        materials = "Deionized water · colored solution · gravimetric receiving vessels",
        safety = "Inspect glass for chips and cracks, use a pipette aid and keep funnels out of burettes during readings; broken or contaminated glass follows its designated waste route.",
        measure = "volume-delivery accuracy", target = 99.5, unit = "%",
        analysis = "Match TC/TD function and tolerance to the task, convert delivered water mass to volume and compare bias/precision across glassware types.",
        steps = listOf(
        "Classify each item as to-contain, to-deliver or approximate and read its stated tolerance.",
        "Select glassware for solution preparation, exact aliquot, variable delivery and approximate mixing tasks.",
        "Clean and condition the selected glassware with the appropriate solution.",
        "Set/read the meniscus at eye level and observe the specified drainage time.",
        "Verify delivered volume gravimetrically for pipette, cylinder and beaker.",
        "Compare errors and justify the least costly glassware that still meets each required tolerance."
    ))
    "Preparing a laboratory notebook" -> foundation(
        action = "create a contemporaneous, traceable and reproducible laboratory notebook record",
        basis = "A defensible notebook preserves who did what, when and why through permanent raw data, identifiers, units, deviations and transparent corrections rather than reconstructed narrative.",
        apparatus = "Bound or validated electronic notebook · permanent pen/audit trail · sample labels · instrument-file registry",
        materials = "Method version · sample batch · raw-data printout · deviation and correction scenarios",
        safety = "Record incidents and deviations immediately; never erase, obscure or silently rewrite safety-relevant data, sample identities or failed controls.",
        measure = "notebook audit completeness", target = 100.0, unit = "%",
        analysis = "Audit date/time, objective, method version, risks, sample IDs, raw data, units, calculations, deviations, file links, waste and sign-off; critical provenance fields cannot be averaged away.",
        steps = listOf(
        "Create a dated title, objective, hypothesis and reference to the exact method version.",
        "Record risk controls, reagent identities/lots, apparatus IDs and calibration status before work.",
        "Design raw-data tables with units and sample/control identifiers before collecting values.",
        "Enter observations contemporaneously and correct errors with a single-line, reason, date and initials.",
        "Link immutable instrument files and show one complete calculation with formula and units.",
        "Write conclusion, deviations, waste disposition and signature, then complete a peer audit."
    ))
    "Calibration-curve construction" -> foundation(
        technique = LabTechnique.SPECTROSCOPY,
        action = "construct, validate and use a matrix-matched calibration curve",
        basis = "A calibration model relates traceable standards to instrument response only within a demonstrated working range; blanks, residuals, independent checks and dilution integrity govern validity.",
        apparatus = "UV–visible spectrophotometer · matched cuvettes · Class A flasks · calibrated micropipettes · regression workstation",
        materials = "Certified stock standard · matrix blank · at least five calibration levels · independent QC standard · unknown",
        safety = "Review analyte and solvent SDSs, use matched containment and never return standards to stock bottles; collect all calibration and unknown solutions by compatibility.",
        measure = "calibration model fit", target = 0.995, unit = "R²",
        analysis = "Blank-correct response, fit the justified model, inspect residuals and back-calculated standards, verify independent QC and interpolate only unknowns inside the accepted range.",
        steps = listOf(
        "Prepare a blank and at least five independently traceable standards spanning the expected unknown.",
        "Verify wavelength accuracy, blank the instrument and measure standards in randomized order.",
        "Plot response against concentration and inspect leverage, curvature and residual pattern.",
        "Fit the simplest justified model without forcing the intercept unless the method requires it.",
        "Run an independent mid-range QC and replicate unknown; dilute any sample outside range.",
        "Calculate concentration with dilution and prediction uncertainty, then archive raw data and model diagnostics."
    ))
    else -> error("Missing dedicated foundation protocol: $title")
}

private fun spectroscopyProtocol(title: String) = RequestedProtocol(
    LabTechnique.SPECTROSCOPY,
    "acquire and interpret defensible data for $title",
    when {
        "infrared" in title.lowercase() -> "Molecular vibrations absorb infrared radiation at frequencies governed by bond strength and reduced mass; diagnostic bands support functional-group assignments."
        "nmr" in title.lowercase() -> "Chemical shift, integration, multiplicity and coupling report the electronic and through-bond environments of magnetic nuclei."
        "mass" in title.lowercase() || "maldi" in title.lowercase() -> "Ionization followed by mass-to-charge separation produces molecular-ion, isotope and fragment evidence for composition and structure."
        else -> "Absorbance follows A = εbc within the validated linear range; blanks and standards separate analyte response from the sample matrix."
    },
    "Technique-specific spectrometer · certified reference · sample holder · micropipettes · acquisition workstation",
    "Matrix blank · reference standard · calibration series · lesson-specific unknown",
    "Confirm instrument interlocks and sample compatibility; use ventilation for volatile solvents and collect all solvent/sample waste by compatibility.",
    listOf(
        "Start the instrument, complete self-tests and verify mass, frequency or wavelength accuracy.",
        "Prepare a matrix blank, performance reference and traceable sample dilution.",
        "Clean and load the correct holder without bubbles, fingerprints or cross-contamination.",
        "Acquire the blank and reference; reject the run if performance criteria fail.",
        "Acquire randomized replicate sample data and retain the unmodified raw file.",
        "Assign signals, quantify where validated, report uncertainty and clean the sample interface."
    ),
    "validated spectral agreement", 98.0, "%",
    "Apply blank correction, verify diagnostic peaks and calculate agreement with the reference; report resolution, calibration fit and any ambiguous assignment."
)

private fun separationProtocol(title: String) = RequestedProtocol(
    LabTechnique.SEPARATION,
    "separate, recover and evaluate components during $title",
    "Separation arises from controlled differences in volatility, solubility, adsorption, charge, molecular size or partition coefficient; both purity and recovery determine success.",
    "Method-specific column, plate or distillation train · calibrated injector · fraction collector · detector · balance",
    "Resolution standard · matrix blank · unknown mixture · validated mobile phase or heating medium",
    "Inspect pressure and glass joints before operation; never seal heated apparatus. Control ignition sources and segregate halogenated, non-halogenated and aqueous waste.",
    listOf(
        "Inspect and condition the stationary phase or assemble and leak-test the distillation train.",
        "Prepare blank, resolution standard and unknown at controlled concentration.",
        "Set flow, temperature or solvent program and document the stabilized baseline.",
        "Load the sample quantitatively and collect time-, volume- or migration-resolved fractions.",
        "Measure component response, purity and carryover in each relevant fraction.",
        "Calculate resolution and recovery; flush, cool and dispose of fractions by compatibility."
    ),
    "target-component recovery", 90.0, "%",
    "Calculate recovery = recovered target / loaded target × 100 and report purity plus resolution; a high yield cannot compensate for co-elution or contamination."
)

private fun titrationProtocol(title: String) = RequestedProtocol(
    LabTechnique.TITRATION,
    "determine composition and endpoint behavior in $title",
    "At equivalence, titrant and analyte have reacted in the balanced stoichiometric ratio; the titration curve or indicator response locates that condition.",
    "Class A burette · volumetric pipette · conical flask · calibrated pH meter · magnetic stirrer",
    "Standardized titrant · lesson-specific analyte · primary standard · indicator · buffer standards",
    "Acids and bases are corrosive. Wear splash protection, remove burette funnels before readings and collect indicator-containing waste separately.",
    listOf(
        "Standardize the titrant and verify the pH meter with buffers bracketing the endpoint.",
        "Condition volumetric glassware and transfer an exact analyte aliquot.",
        "Record the initial meniscus at eye level and begin controlled stirring.",
        "Deliver titrant rapidly at first, then dropwise near the response change.",
        "Record the final meniscus and repeat until three titres are concordant.",
        "Calculate concentration with stoichiometry, uncertainty and appropriate significant figures."
    ),
    "concordant endpoint recovery", 100.0, "%",
    "Use the balanced mole ratio and delivered volume to calculate analyte amount; accept three titres within 0.10 mL and propagate glassware uncertainty."
)

private fun calorimetryProtocol(title: String) = RequestedProtocol(
    LabTechnique.CALORIMETRY,
    "quantify energy transfer and thermodynamic behavior for $title",
    "Conservation of energy relates system heat to calibrated calorimeter and surroundings heat; q = CcalΔT or q = mcΔT, with sign set by the defined system.",
    "Calorimeter · calibrated temperature probe · analytical balance · ignition or mixing system · data logger",
    "Certified calorimeter standard · lesson-specific sample · oxygen or reaction medium · blank",
    "Inspect pressure-rated components and shields; exclude ignition sources except the controlled igniter and vent only after cooling to a safe pressure.",
    listOf(
        "Calibrate the temperature probe and determine the calorimeter constant with a reference.",
        "Measure sample and medium masses and allow them to reach thermal equilibrium.",
        "Start logging, initiate the change and keep mixing or ignition conditions controlled.",
        "Record the full baseline, temperature extremum and post-event drift.",
        "Repeat with a blank and sufficient independent replicates.",
        "Correct for calorimeter response and heat loss; report molar energy with uncertainty."
    ),
    "energy-accounting closure", 98.0, "%",
    "Calculate system heat from the calibrated thermal response and compare input with recovered energy; document heat-loss and baseline corrections."
)

private fun kineticsProtocol(title: String, biochemical: Boolean = false) = RequestedProtocol(
    LabTechnique.KINETICS,
    "measure rate behavior and controlled-variable effects in $title",
    if (biochemical)
        "Initial-rate data connect substrate concentration to enzyme activity; controls distinguish catalytic conversion from background and inhibition models."
    else
        "Time-resolved concentration or signal data reveal rate law, order and activation behavior when one factor changes and all other conditions remain controlled.",
    "Thermostated reaction vessel · timer/data logger · spectrophotometer or gas sensor · calibrated pipettes",
    if (biochemical) "Enzyme · substrate series · inhibitor series · buffer · denatured-enzyme blank"
    else "Reaction standards · concentration series · catalyst where required · reagent blank",
    "Review reagent and biological hazards; control temperature and pressure, contain aerosols, and deactivate biological material using the approved waste route.",
    listOf(
        "Calibrate the detector and equilibrate all reagents at the target temperature.",
        "Prepare a randomized concentration series plus blank and positive control.",
        "Initiate each trial with identical mixing and start-time technique.",
        "Capture dense early-time data inside the detector’s linear response range.",
        "Repeat each condition and inspect residuals rather than deleting outliers silently.",
        "Fit the appropriate rate model and report parameter uncertainty and control performance."
    ),
    "model-fit quality", 0.98, "R²",
    "Estimate initial rates and fit the stated kinetic model; compare nested models using residuals, controls and confidence intervals rather than R² alone."
)

private fun gasProtocol(title: String) = RequestedProtocol(
    LabTechnique.GAS,
    "test pressure–volume–temperature relationships in $title",
    "For a fixed amount of ideal gas, PV = nRT; experimental readings require absolute temperature, consistent units, leak control and vapor-pressure correction.",
    "Gas syringe · pressure sensor · thermostated bath · temperature probe · sealed reaction vessel",
    "Approved test gas · pressure reference · water for thermal bath · leak-test solution",
    "Use only pressure-rated apparatus behind a shield; never heat a sealed rigid vessel beyond its rating and vent through the approved route.",
    listOf(
        "Inspect pressure ratings and leak-test the complete gas path.",
        "Calibrate pressure and temperature sensors against traceable references.",
        "Introduce a known gas amount and allow thermal equilibrium.",
        "Change one of pressure, volume or temperature while controlling the others.",
        "Record replicate stabilized readings and atmospheric/vapor-pressure corrections.",
        "Transform the data, fit the predicted relationship and inspect deviations from ideality."
    ),
    "gas-law fit quality", 0.99, "R²",
    "Convert temperature to kelvin, apply pressure corrections and fit the predicted linearized gas-law relation with uncertainty."
)

private fun equilibriumProtocol(title: String) = RequestedProtocol(
    LabTechnique.EQUILIBRIUM,
    "establish and perturb a reproducible equilibrium during $title",
    "Dynamic equilibrium occurs when opposing rates are equal; composition responds predictably to concentration, temperature and pressure changes while K depends principally on temperature.",
    "Thermostated vessels · spectrophotometer or pH meter · volumetric glassware · data logger",
    "Equilibrium mixture · concentration standards · matrix blank · perturbing reagent",
    "Review all species and perturbing reagents in the SDS; keep temperature/pressure within apparatus limits and segregate metal-containing waste.",
    listOf(
        "Calibrate the analytical response with matrix-matched standards and a blank.",
        "Prepare replicate equilibrium mixtures from documented initial amounts.",
        "Hold temperature constant and wait for a stable signal.",
        "Apply one controlled perturbation while retaining an unperturbed control.",
        "Record the response until a new plateau is reached and repeat independently.",
        "Calculate equilibrium quantities and determine whether mass balance and controls close."
    ),
    "equilibrium mass-balance closure", 99.0, "%",
    "Use calibrated equilibrium concentrations in the correct K expression and check conserved-component mass balance before interpreting Le Châtelier behavior."
)

private fun electrochemistryProtocol(title: String) = RequestedProtocol(
    LabTechnique.ELECTROCHEMISTRY,
    "resolve electron transfer, products and performance in $title",
    "Oxidation occurs at the anode and reduction at the cathode; charge, potential and product amount are linked by half-reactions, the Nernst equation and Faraday’s law.",
    "DC supply or high-impedance voltmeter · electrodes · salt bridge/cell · current logger · analytical balance",
    "Electrolyte solutions · electrode materials · redox reference · blank/control cell",
    "Inspect electrical leads before wet work; de-energize before adjustments, ventilate evolved gases and collect metal-ion electrolytes as hazardous waste.",
    listOf(
        "Polish, rinse and weigh electrodes; prepare labeled electrolyte compartments.",
        "Verify the voltmeter or current source and assemble the cell with correct polarity.",
        "Record open-circuit behavior or apply the controlled current/potential.",
        "Track charge, voltage, temperature and observable electrode products.",
        "Run a reference and repeat the experimental condition independently.",
        "De-energize, calculate Faradaic or cell performance and segregate electrolytes."
    ),
    "charge-to-product agreement", 98.0, "%",
    "Balance both half-reactions, integrate charge and compare predicted with observed product or mass change; report current efficiency and uncertainty."
)

private fun gravimetryProtocol(title: String) = RequestedProtocol(
    LabTechnique.GRAVIMETRY,
    "obtain traceable mass-based evidence in $title",
    "Quantitative mass measurement relates sample composition to density, stoichiometric precipitate mass or controlled mass change, provided transfer is complete and the final form is known.",
    "Analytical balance · dried weighing vessel · filtration or volumetric apparatus · oven/desiccator · temperature probe",
    "Certified check mass · lesson-specific unknown · blank · precipitation or calibration reagent",
    "Use tongs for hot vessels, contain toxic precipitating ions and never place contaminated objects directly on the balance pan.",
    listOf(
        "Level and verify the analytical balance with a traceable check mass.",
        "Condition, cool in a desiccator and weigh the empty receiving vessel.",
        "Transfer a documented sample amount and perform the quantitative conversion.",
        "Separate, wash and dry the measured phase without material loss.",
        "Repeat heat–cool–weigh cycles until the constant-mass criterion is met.",
        "Apply the stoichiometric or density model and propagate mass/volume uncertainty."
    ),
    "mass-balance recovery", 99.5, "%",
    "Apply tare correction and the appropriate mass factor; accept constant mass only when successive readings meet the specified tolerance."
)

private fun qualitativeProtocol(title: String) = RequestedProtocol(
    LabTechnique.QUALITATIVE,
    "identify chemical behavior using an evidence matrix for $title",
    "Selective chemical responses convert structure or composition into color, precipitate, gas or solubility evidence; blanks, known references and orthogonal confirmation control false assignments.",
    "Microwell plate · calibrated droppers · test tubes · controlled water bath · pH paper or detector",
    "Unknown · positive reference · reagent blank · primary test reagent · confirmation reagent",
    "Use microscale quantities in ventilation where required; treat unknowns as hazardous and segregate oxidizer, halogenated and metal-containing residues.",
    listOf(
        "Label separate wells for blank, known positive, known negative and unknown.",
        "Inspect reagent condition and record initial sample appearance and pH.",
        "Add the primary reagent in the validated order and amount.",
        "Record timed color, gas, precipitate or solubility observations without inference.",
        "Run an independent confirmation test on a fresh aliquot.",
        "Match the complete evidence matrix, state uncertainty and dispose of each microscale residue."
    ),
    "identification confidence", 0.95, "fraction",
    "Score agreement with positive and negative controls; report an identity only when the primary and confirmation evidence are mutually consistent."
)

private fun synthesisProtocol(title: String) = RequestedProtocol(
    LabTechnique.SYNTHESIS,
    "prepare, isolate and characterize the target in $title",
    "Balanced stoichiometry determines theoretical yield, while reaction mechanism, controlled conditions, work-up and purification determine conversion, selectivity and isolated purity.",
    "Reaction flask · controlled heater/stirrer · condenser or inert adapter · separation apparatus · balance",
    "Limiting reagent · reaction partner · catalyst/base · quench · purification solvents",
    "Complete a reaction-specific risk assessment; control additions and exotherms, exclude ignition sources and segregate reactive, aqueous and organic waste.",
    listOf(
        "Verify reagent identity and calculate a scaled stoichiometric table before dispensing.",
        "Inspect and assemble the dry reaction apparatus with required ventilation or inert control.",
        "Charge reagents in the safe order and monitor time, temperature and observable changes.",
        "Confirm completion with an appropriate analytical checkpoint before quenching.",
        "Isolate and purify the product, then dry it to constant mass.",
        "Measure yield and identity/purity against a reference; reconcile all waste streams."
    ),
    "isolated product recovery", 75.0, "%",
    "Calculate theoretical yield from the limiting reagent, then report isolated yield separately from purity and include evidence for identity."
)

private fun molecularBiologyProtocol(title: String) = RequestedProtocol(
    LabTechnique.SYNTHESIS,
    "execute a controlled molecular workflow for $title",
    "Sequence-specific base pairing, polymerase activity and regulated information transfer connect molecular structure to amplified, cloned or expressed products.",
    "Micropipettes · sterile tubes · thermocycler/incubator · electrophoresis or fluorescence detector · analysis workstation",
    "Template or construct · primers/enzymes · nucleotide mix · buffer · positive control · no-template control",
    "Use designated clean and post-amplification zones, aerosol-resistant tips and appropriate biosafety containment; deactivate biological and nucleic-acid-stain waste.",
    listOf(
        "Design the construct or assay and verify sequence, control and containment requirements.",
        "Prepare a master mix on ice using calibrated pipettes and contamination controls.",
        "Add template, positive control and no-template control in separate labeled tubes.",
        "Run the validated temperature/incubation program with an audit trail.",
        "Resolve or detect products and verify the expected size or signal against standards.",
        "Interpret controls before samples, archive raw data and decontaminate the workspace."
    ),
    "expected-product specificity", 98.0, "%",
    "Accept the batch only when the positive control succeeds, the no-template control remains negative and the product matches the expected size or sequence."
)

private fun bioanalysisProtocol(title: String) = RequestedProtocol(
    LabTechnique.SEPARATION,
    "prepare, separate and quantitatively detect biomolecular evidence in $title",
    "Biomolecules are resolved or imaged through size, charge, affinity or optical contrast; standards and exposure controls convert bands, wells or pixels into defensible evidence.",
    "Micropipettes · gel/blot/imaging chamber · calibrated power supply or microscope · image-analysis workstation",
    "Molecular ladder or calibration standard · positive control · blank · sample · stains/antibodies/buffers",
    "Treat samples according to biosafety status; prevent electrical access to wet chambers, protect from UV/laser exposure and segregate stains and biological waste.",
    listOf(
        "Prepare samples and standards with calibrated pipettes and a documented loading plan.",
        "Inspect, blank and calibrate the separation or imaging instrument.",
        "Load standards, controls and randomized samples without cross-contamination.",
        "Run the validated voltage, transfer, binding, wash or acquisition program.",
        "Capture unsaturated raw data and verify ladder/control acceptance.",
        "Quantify normalized response, document uncertainty and decontaminate the system."
    ),
    "control-normalized detection", 100.0, "%",
    "Subtract background, normalize to the appropriate control and quantify only inside the calibrated, unsaturated response range."
)

private fun materialsProtocol(title: String) = RequestedProtocol(
    LabTechnique.PHYSICAL_MEASUREMENT,
    "characterize structure–property behavior in $title",
    "Composition, processing history and defects determine structure across length scales; calibrated mechanical, diffraction, scattering, transport or electrochemical measurements reveal property relationships.",
    "Specimen fixture · calibrated load/transport instrument · diffraction or imaging detector · environmental chamber · data system",
    "Reference material · conditioned test specimens · blank/background standard · calibration artifact",
    "Verify guards, radiation interlocks and load limits; control dust and heavy samples, and isolate batteries before opening or disposal.",
    listOf(
        "Condition specimens and record dimensions, composition, batch and processing history.",
        "Verify instrument zero, geometry and response with a certified reference.",
        "Acquire background and reference data using the stated environmental conditions.",
        "Test replicate specimens across the controlled load, angle, temperature or operating range.",
        "Inspect failure, diffraction, transport or imaging features without altering raw data.",
        "Calculate the target property, compare batches and report uncertainty and failure mode."
    ),
    "reference-normalized property", 100.0, "%",
    "Apply geometry/background corrections and compare replicate property values with the reference; retain raw curves and document specimen failure or artifacts."
)

private fun environmentalProtocol(title: String) = RequestedProtocol(
    if ("biodiesel" in title.lowercase() || "concrete" in title.lowercase()) LabTechnique.SYNTHESIS else LabTechnique.PHYSICAL_MEASUREMENT,
    "build a defensible chemical mass balance for $title",
    "Environmental conclusions require representative sampling, field blanks, calibrated sensors, preserved samples and conserved mass/energy across the defined system boundary.",
    "Field sampler · multiparameter probe · preserved sample bottles · spectrophotometer/titration kit · data logger",
    "Site sample · field blank · certified standard · matrix spike · treatment or comparison material",
    "Use site-specific PPE and a field risk plan; preserve samples correctly and return contaminated, nutrient, metal and solvent residues to designated waste streams.",
    listOf(
        "Define the system boundary, hypothesis, sampling locations and chain-of-custody plan.",
        "Calibrate field instruments and prepare trip, field and equipment blanks.",
        "Collect representative samples while recording time, weather, temperature and location.",
        "Analyze standards, blanks, spikes and samples in a contamination-controlled sequence.",
        "Compare untreated/control and intervention conditions with replicate measurements.",
        "Close mass/energy balances, evaluate quality controls and document sustainable disposal."
    ),
    "quality-controlled mass-balance closure", 95.0, "%",
    "Blank-correct results, calculate recovery and mass balance, and accept environmental conclusions only when calibration, duplicates and matrix spikes meet criteria."
)

private fun modelProtocol(title: String) = RequestedProtocol(
    LabTechnique.PHYSICAL_MEASUREMENT,
    "test a manipulable chemical model for $title against defined evidence",
    "A scientific model links controlled inputs to measurable predictions; useful models preserve chemical constraints, state assumptions and are rejected or revised when observations disagree.",
    "Interactive molecular/model workspace · parameter controls · reference dataset · analysis notebook",
    "Validated structures or data cards · positive model case · counterexample · unknown challenge",
    "Model activities do not remove laboratory obligations: verify chemical plausibility, label simulated data and never infer that a visualization is physical evidence.",
    listOf(
        "State a falsifiable prediction and identify independent, dependent and controlled variables.",
        "Validate the model with a known positive case and a counterexample.",
        "Change one parameter while holding all other inputs constant.",
        "Record predicted structures, trends or classifications across the designed range.",
        "Compare predictions with reference evidence and investigate residuals.",
        "Report assumptions, applicability domain, uncertainty and the next physical experiment."
    ),
    "model prediction agreement", 95.0, "%",
    "Score predictions against held-out reference evidence and report both correct classifications and chemically meaningful failure cases."
)

internal val requestedVirtualExperiments: List<VirtualExperiment> =
    requestedLabInventory.map { entry ->
        val protocol = entry.protocol()
        VirtualExperiment(
            id = "requested-${entry.number.toString().padStart(3, '0')}",
            title = entry.title,
            category = entry.category,
            level = when (entry.number) {
                in 1..37 -> "Foundation"
                in 38..88 -> "Intermediate"
                else -> "Undergraduate"
            },
            technique = protocol.technique,
            objective = "Safely ${protocol.objectiveAction}, using blanks, controls, calibrated measurements and an auditable laboratory notebook.",
            equation = protocol.scientificBasis,
            apparatus = protocol.apparatus,
            reagents = protocol.reagents,
            safety = protocol.safety,
            procedure = protocol.procedure,
            measuredQuantity = protocol.measuredQuantity,
            referenceValue = protocol.referenceValue,
            unit = protocol.unit,
            analysis = protocol.analysis,
            theory = "${protocol.scientificBasis} The investigation separates observation from interpretation, requires replicate measurements, and treats control performance and uncertainty as part of the result.",
            expectedResult = "${protocol.measuredQuantity} should approach ${protocol.referenceValue} ${protocol.unit} when controls pass. The learner must retain raw data, explain deviations and reject a run when safety, blank or calibration criteria fail."
        )
    }
