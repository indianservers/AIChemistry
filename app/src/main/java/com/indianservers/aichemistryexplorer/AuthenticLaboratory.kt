package com.indianservers.aichemistryexplorer

internal enum class LaboratoryMode {
    WET_CHEMISTRY,
    INSTRUMENTAL_ANALYSIS,
    BIOCHEMICAL,
    MATERIALS_TESTING,
    COMPUTATIONAL,
    ENVIRONMENTAL_FIELD,
    MODEL_INVESTIGATION
}

internal data class LabApparatus(
    val name: String,
    val specification: String,
    val preparationCheck: String
)

internal data class LabMaterial(
    val name: String,
    val identity: String,
    val workingAmount: String,
    val hazard: String,
    val wasteStream: String
)

internal data class LabProcedureStep(
    val instruction: String,
    val checkpoint: String,
    val expectedObservation: String,
    val failureConsequence: String
)

internal data class LabMeasurement(
    val quantity: String,
    val unit: String,
    val instrument: String,
    val workingRange: String,
    val uncertainty: String,
    val replicateCount: Int
)

internal data class AuthenticLabSpec(
    val id: String,
    val title: String,
    val mode: LaboratoryMode,
    val objective: String,
    val ppe: List<String>,
    val apparatus: List<LabApparatus>,
    val materials: List<LabMaterial>,
    val calibrationSteps: List<String>,
    val procedure: List<LabProcedureStep>,
    val measurements: List<LabMeasurement>,
    val independentVariable: String,
    val dependentVariable: String,
    val controlledVariables: List<String>,
    val positiveControl: String,
    val negativeControl: String,
    val calculationModel: String,
    val acceptanceCriteria: List<String>,
    val failureStates: List<String>,
    val wastePlan: List<String>,
    val reportSections: List<String>
)

private data class LabBlueprint(
    val name: String,
    val mode: LaboratoryMode,
    val apparatus: List<LabApparatus>,
    val materials: List<LabMaterial>,
    val coreSteps: List<LabProcedureStep>,
    val measurements: List<LabMeasurement>,
    val independent: String,
    val dependent: String,
    val controls: List<String>,
    val positiveControl: String,
    val negativeControl: String,
    val calibration: List<String>,
    val waste: List<String>
)

private fun apparatus(name: String, specification: String, check: String) =
    LabApparatus(name, specification, check)

private fun material(name: String, identity: String, amount: String, hazard: String, waste: String) =
    LabMaterial(name, identity, amount, hazard, waste)

private fun step(action: String, checkpoint: String, observation: String, failure: String) =
    LabProcedureStep(action, checkpoint, observation, failure)

private fun measure(quantity: String, unit: String, instrument: String, range: String, uncertainty: String, replicates: Int = 3) =
    LabMeasurement(quantity, unit, instrument, range, uncertainty, replicates)

private val standardPpe = listOf(
    "Splash-rated chemical goggles",
    "Buttoned laboratory coat",
    "Closed footwear and tied-back hair",
    "Chemical-compatible gloves selected from the SDS"
)

private val standardApparatus = listOf(
    apparatus("Analytical balance", "0.0001 g readability", "Level, zero and verify with a check mass"),
    apparatus("Class A volumetric flask", "100.00 mL ±0.08 mL", "Clean, condition and inspect the calibration mark"),
    apparatus("Class A pipette", "10.00 mL ±0.02 mL", "Rinse with working solution and remove air bubbles"),
    apparatus("Temperature probe", "-10–110 °C, 0.1 °C resolution", "Check ice-water and ambient reference points")
)

private val standardMaterials = listOf(
    material("Primary sample", "Lesson-specific analyte", "10–100 mL or 0.1–5 g", "Consult SDS before handling", "Labelled experiment waste"),
    material("Blank/control", "Matrix without analyte", "Same final volume as sample", "Treat as the sample matrix", "Matching experiment waste"),
    material("Deionized water", "H₂O, laboratory grade", "As required", "Low hazard; clean spills promptly", "Aqueous drain only when uncontaminated")
)

private fun genericSteps(subject: String) = listOf(
    step("Read the method, SDS information and risk assessment for $subject.", "Hazards, exposure routes and controls identified", "A complete pre-lab record", "The experiment remains locked"),
    step("Inspect, clean and assemble the specified apparatus.", "No cracks, contamination or loose connections", "Equipment is clean, dry where required and stable", "Contamination or leakage biases the result"),
    step("Calibrate or verify every quantitative instrument.", "Reference falls inside its tolerance", "A traceable calibration record", "Measurements are invalid until recalibrated"),
    step("Prepare and label blank, control and experimental samples.", "Identity, concentration, date and operator recorded", "Samples are traceable and distinguishable", "A sample mix-up invalidates the batch"),
    step("Run the blank and controls before unknown samples.", "Controls meet their acceptance criteria", "Background and method performance established", "Stop and troubleshoot failed controls"),
    step("Measure the experimental samples using randomized replicate order.", "At least three valid replicates", "Raw values with units and instrument resolution", "Single measurements cannot establish precision"),
    step("Calculate the result with uncertainty and significant figures.", "Units, formula and assumptions shown", "Mean, spread and final reported value", "Unsupported precision is rejected"),
    step("Compare the result with chemical expectations and investigate outliers.", "Outlier decision documented, never silently deleted", "Evidence-based interpretation", "Cherry-picking produces an invalid conclusion"),
    step("Decontaminate equipment and segregate every waste stream.", "Waste container and label match the SDS", "Clean bench and complete disposal record", "Incompatible waste creates a safety incident"),
    step("Complete the laboratory report and electronic notebook audit trail.", "Raw data, changes and calculations are traceable", "Reproducible final report", "Missing provenance prevents sign-off")
)

private fun titrationBlueprint(subject: String) = LabBlueprint(
    name = "Quantitative titration: $subject",
    mode = LaboratoryMode.WET_CHEMISTRY,
    apparatus = listOf(
        apparatus("Class A burette", "50.00 mL ±0.05 mL", "Leak test, rinse with titrant and remove tip bubble"),
        apparatus("Volumetric pipette", "25.00 mL ±0.03 mL", "Condition with analyte and verify drainage time"),
        apparatus("Conical flask", "250 mL", "Rinse with deionized water; do not condition with analyte"),
        apparatus("pH meter", "0–14 pH, 0.01 resolution", "Two- or three-point calibration bracketing the endpoint"),
        apparatus("Magnetic stirrer", "Variable speed", "Confirm a stable vortex without splashing")
    ),
    materials = listOf(
        material("Standard titrant", "Standardized NaOH or HCl", "0.0500–0.2000 mol L⁻¹", "Corrosive", "Acid/base aqueous waste"),
        material("Unknown analyte", "Acidic or basic sample", "25.00 mL aliquots", "Treat as corrosive", "Acid/base aqueous waste"),
        material("Primary standard", "KHP or anhydrous Na₂CO₃", "0.2–0.8 g", "Irritant", "Acid/base aqueous waste"),
        material("Indicator", "Phenolphthalein or suitable alternative", "2–3 drops", "Flammable solvent may be present", "Organic/indicator waste")
    ),
    coreSteps = genericSteps(subject),
    measurements = listOf(
        measure("Initial and final burette readings", "mL", "Class A burette", "0.00–50.00", "±0.05 mL"),
        measure("Endpoint pH", "pH", "Calibrated pH meter", "2–12", "±0.02 pH"),
        measure("Analyte concentration", "mol L⁻¹", "Stoichiometric calculation", "0.001–2.000", "Expanded uncertainty from glassware and titre")
    ),
    independent = "Delivered titrant volume",
    dependent = "pH or indicator response",
    controls = listOf("Analyte aliquot volume", "Titrant concentration", "Temperature", "Stirring rate"),
    positiveControl = "Known acid/base standard producing the expected endpoint",
    negativeControl = "Deionized-water blank with no analyte titre",
    calibration = listOf("Standardize the titrant against a dried primary standard", "Calibrate the pH meter with traceable buffers", "Verify burette delivery gravimetrically"),
    waste = listOf("Neutralize compatible dilute aqueous fractions only under the approved SOP", "Collect indicator/organic fractions separately", "Rinse burette and pipette into the correct waste container")
)

private fun spectroscopyBlueprint(subject: String) = LabBlueprint(
    name = "Instrumental spectrum acquisition: $subject",
    mode = LaboratoryMode.INSTRUMENTAL_ANALYSIS,
    apparatus = listOf(
        apparatus("Spectrometer", "Technique appropriate to $subject", "Warm up, self-test and wavelength/frequency verification"),
        apparatus("Sample holder", "Matched cuvette, ATR crystal, NMR tube or target plate", "Clean, blank and inspect optical surfaces"),
        apparatus("Micropipette", "20–200 or 100–1000 µL", "Gravimetric performance check"),
        apparatus("Data workstation", "Raw-data retention enabled", "Verify method version and audit trail")
    ),
    materials = listOf(
        material("Reference standard", "Certified spectral reference", "Method-specific", "Use reference SDS", "Technique-specific waste"),
        material("Unknown sample", "Lesson-specific analyte or mixture", "Method-specific", "Assume hazardous until identified", "Technique-specific waste"),
        material("Solvent blank", "Spectroscopy-grade solvent", "Same matrix and path length", "May be flammable or toxic", "Organic or aqueous solvent waste")
    ),
    coreSteps = genericSteps(subject),
    measurements = listOf(
        measure("Signal position", "nm, cm⁻¹, ppm, m/z or 2θ", "Calibrated spectrometer", "Technique-specific", "Instrument resolution"),
        measure("Signal intensity", "absorbance or relative abundance", "Calibrated spectrometer", "0–instrument limit", "Blank and repeatability uncertainty"),
        measure("Quantified analyte", "mol L⁻¹ or mass fraction", "Calibration model", "Validated working range", "Regression confidence interval")
    ),
    independent = "Wavelength, frequency, field, mass-to-charge ratio or diffraction angle",
    dependent = "Detector response",
    controls = listOf("Sample concentration", "Path length/sample geometry", "Temperature", "Acquisition settings"),
    positiveControl = "Certified reference producing required diagnostic signals",
    negativeControl = "Matrix blank below the detection threshold",
    calibration = listOf("Run instrument performance verification with a certified reference", "Acquire and accept a matrix blank", "Build or verify a multi-point calibration where quantification is required"),
    waste = listOf("Return reusable reference materials", "Collect solvent and sample waste by compatibility", "Clean the sample holder using the validated procedure")
)

private fun separationBlueprint(subject: String) = LabBlueprint(
    name = "Chromatographic separation: $subject",
    mode = LaboratoryMode.INSTRUMENTAL_ANALYSIS,
    apparatus = listOf(
        apparatus("Chromatography system", "HPLC, GC, TLC or ion-exchange configuration", "Leak/pressure test and detector self-test"),
        apparatus("Analytical column or plate", "Method-specified stationary phase", "Condition and verify plate/column integrity"),
        apparatus("Autosampler or capillary", "Calibrated injection volume", "Rinse and check carryover"),
        apparatus("Data system", "Integration audit trail enabled", "Verify acquisition rate and integration method")
    ),
    materials = listOf(
        material("Calibration standards", "At least five analyte levels", "Bracket expected samples", "Technique-specific", "Chromatography solvent waste"),
        material("Unknown mixture", "Lesson-specific analytes", "Filtered sample aliquot", "Assume hazardous", "Chromatography solvent waste"),
        material("Mobile phase", "Validated solvent/buffer composition", "One batch per sequence", "Often flammable/corrosive", "Dedicated mobile-phase waste")
    ),
    coreSteps = genericSteps(subject),
    measurements = listOf(
        measure("Retention or migration", "min or Rf", "Chromatography system", "Method-specific", "Repeatability SD"),
        measure("Peak area or spot intensity", "area counts", "Detector/data system", "Validated linear range", "Calibration-model uncertainty"),
        measure("Resolution", "dimensionless", "Chromatographic calculation", "0–5+", "Propagation from retention and peak width")
    ),
    independent = "Mobile-phase composition, temperature or electric field",
    dependent = "Retention, migration and detector response",
    controls = listOf("Injection volume", "Flow rate", "Stationary phase", "Sample solvent"),
    positiveControl = "Resolution standard containing known analytes",
    negativeControl = "Mobile-phase blank demonstrating acceptable carryover",
    calibration = listOf("Equilibrate the stationary phase to a stable baseline", "Verify flow or migration distance", "Run blank and multi-level standards before unknowns"),
    waste = listOf("Collect organic mobile phase in a compatible sealed container", "Segregate halogenated and non-halogenated solvents", "Dispose of contaminated plates, filters and vials as solid chemical waste")
)

private fun computationalBlueprint(subject: String) = LabBlueprint(
    name = "Reproducible computational investigation: $subject",
    mode = LaboratoryMode.COMPUTATIONAL,
    apparatus = listOf(
        apparatus("Compute environment", "Version-pinned chemistry software", "Record software, method and hardware versions"),
        apparatus("Structure editor", "Valence and stereochemistry validation", "Check charge, multiplicity and atom labels"),
        apparatus("Workflow runner", "Deterministic seeds and logged inputs", "Validate a reference calculation"),
        apparatus("Analysis notebook", "Executable cells with provenance", "Restart and reproduce all outputs")
    ),
    materials = listOf(
        material("Input structure", "Validated molecular or materials model", "One reference and one test system", "Digital data; verify license and provenance", "Versioned archive"),
        material("Reference dataset", "Experimental or high-level benchmark", "Sufficient validation cases", "Digital data", "Versioned archive"),
        material("Parameter set", "Basis set, force field or model weights", "Fixed for controlled comparison", "Document limitations", "Versioned archive")
    ),
    coreSteps = genericSteps(subject),
    measurements = listOf(
        measure("Calculated response", "energy/property unit", "Validated software workflow", "Method-specific", "Numerical and model uncertainty"),
        measure("Convergence", "dimensionless or residual", "Solver log", "Defined tolerance", "Repeat runs"),
        measure("Prediction error", "property unit", "Benchmark comparison", "Validation domain", "Confidence interval from validation set")
    ),
    independent = "Model chemistry, molecular configuration or simulation condition",
    dependent = "Calculated energy, structure, trajectory or predicted property",
    controls = listOf("Software version", "Numerical thresholds", "Initial structure", "Random seed"),
    positiveControl = "Benchmark system with a published or experimentally known result",
    negativeControl = "Perturbed or null model expected to fail the target criterion",
    calibration = listOf("Reproduce a benchmark calculation", "Verify units, coordinate conventions and convergence thresholds", "Run a sensitivity check against a second parameter level"),
    waste = listOf("Archive raw inputs, logs and outputs", "Remove temporary confidential data according to policy", "Retain checksums and environment manifest")
)

private fun specialistBlueprint(
    subject: String,
    mode: LaboratoryMode,
    specificApparatus: List<LabApparatus>,
    specificMaterials: List<LabMaterial>,
    measurements: List<LabMeasurement>,
    independent: String,
    dependent: String,
    controls: List<String>,
    calibration: List<String>,
    waste: List<String>
) = LabBlueprint(
    name = "$subject laboratory investigation",
    mode = mode,
    apparatus = (specificApparatus + standardApparatus).distinctBy { it.name }.take(6),
    materials = (specificMaterials + standardMaterials).distinctBy { it.name }.take(5),
    coreSteps = genericSteps(subject),
    measurements = measurements,
    independent = independent,
    dependent = dependent,
    controls = controls,
    positiveControl = "Certified or characterized reference expected to produce the target $subject response",
    negativeControl = "Blank matrix processed through every method step",
    calibration = calibration,
    waste = waste
)

private fun blueprintFor(
    module: CurriculumModule,
    lesson: CurriculumLesson,
    motif: ChemistryVisualMotif
): LabBlueprint {
    val text = "${module.title} ${lesson.title}".lowercase()
    return when {
        listOf("titration", "acid", "base", "buffer", "ph").any { it in text } -> titrationBlueprint(lesson.title)
        listOf("calorim", "heat capacity", "specific heat", "thermochem").any { it in text } ->
            specialistBlueprint(
                lesson.title,
                LaboratoryMode.WET_CHEMISTRY,
                listOf(
                    apparatus("Bomb or coffee-cup calorimeter", "Known heat capacity and insulated vessel", "Leak test and determine calorimeter constant"),
                    apparatus("Ignition unit", "Controlled electrical ignition", "Verify continuity and ignition wire mass"),
                    apparatus("Precision temperature probe", "0.001–0.1 °C resolution", "Calibrate against traceable temperature points")
                ),
                listOf(
                    material("Calibration substance", "Benzoic acid or electrical standard", "Certified quantity", "Combustible/irritant", "Solid chemical waste"),
                    material("Reaction sample", lesson.formula, "Known mass or volume", "Review reaction hazards", "Reaction-specific waste")
                ),
                listOf(
                    measure("Temperature change", "°C", "Calibrated temperature probe", "-5–50", "±0.02 °C"),
                    measure("Heat or enthalpy", "J or kJ mol⁻¹", "Calorimetric calculation", "Method-specific", "Combined calibration and repeatability uncertainty")
                ),
                "Reactant amount or identity",
                "Temperature change and calculated enthalpy",
                listOf("Total solution mass", "Initial temperature", "Calorimeter constant", "Pressure"),
                listOf("Determine the calorimeter constant with a certified standard", "Verify balance and temperature probe", "Run an empty/solvent blank"),
                listOf("Vent only after cooling and depressurization", "Collect combustion residues as solid chemical waste", "Segregate reaction solutions by compatibility")
            )
        listOf("ideal gas", "gas law", "kinetic theory").any { it in text } ->
            specialistBlueprint(
                lesson.title,
                LaboratoryMode.WET_CHEMISTRY,
                listOf(
                    apparatus("Gas syringe", "100 mL with low-friction plunger", "Leak test at two pressures"),
                    apparatus("Pressure sensor", "80–130 kPa, 0.1 kPa resolution", "Zero and verify against atmospheric reference"),
                    apparatus("Thermostated bath", "5–80 °C", "Verify uniformity with a second thermometer")
                ),
                listOf(
                    material("Dry test gas", "Air, N₂ or approved gas", "Closed 20–100 mL charge", "Compressed gas", "Vent through approved outlet"),
                    material("Thermal bath fluid", "Water or validated heat-transfer fluid", "Sufficient to immerse vessel", "Burn risk when hot", "Reuse or approved aqueous disposal")
                ),
                listOf(
                    measure("Pressure", "kPa", "Calibrated pressure sensor", "80–130", "±0.2 kPa"),
                    measure("Gas volume", "mL", "Gas syringe", "20–100", "±0.5 mL"),
                    measure("Temperature", "K", "Temperature probe", "278–353", "±0.2 K")
                ),
                "Temperature, volume or pressure",
                "Complementary gas-state variable",
                listOf("Amount of gas", "Leak rate", "Equilibration time", "Gas identity"),
                listOf("Leak-test the closed system", "Zero pressure at ambient reference", "Verify temperature at two traceable points"),
                listOf("Vent approved gases safely", "Cool bath before draining", "Do not vent toxic or flammable gases into the room")
            )
        listOf("gravimetric", "precipitation").any { it in text } ->
            specialistBlueprint(
                lesson.title,
                LaboratoryMode.WET_CHEMISTRY,
                listOf(
                    apparatus("Sintered-glass crucible", "Selected porosity and constant mass", "Dry, cool in desiccator and weigh to constant mass"),
                    apparatus("Vacuum filtration assembly", "Chemical-resistant flask and trap", "Leak test and secure glassware"),
                    apparatus("Drying oven and desiccator", "Controlled drying temperature", "Verify temperature and fresh desiccant")
                ),
                listOf(
                    material("Unknown analyte solution", lesson.formula, "Measured aliquot", "Assume hazardous", "Inorganic aqueous waste"),
                    material("Selective precipitating reagent", "Stoichiometric excess", "Calculated addition", "May be corrosive/toxic", "Heavy-metal/inorganic waste")
                ),
                listOf(
                    measure("Crucible plus precipitate mass", "g", "Analytical balance", "0.1–100", "±0.0001 g"),
                    measure("Analyte mass fraction", "%", "Stoichiometric calculation", "0–100", "Expanded gravimetric uncertainty")
                ),
                "Sample aliquot or precipitant amount",
                "Dry precipitate mass",
                listOf("Digestion time", "Wash volume", "Drying temperature", "Precipitant excess"),
                listOf("Verify analytical balance with check masses", "Establish constant crucible mass", "Run reagent blank through filtration"),
                listOf("Collect precipitate and filtrate according to metal identity", "Depressurize filtration assembly before opening", "Do not discharge heavy-metal solutions")
            )
        listOf("reaction kinetic", "rate law", "arrhenius", "activation energy").any { it in text } ->
            specialistBlueprint(
                lesson.title,
                LaboratoryMode.WET_CHEMISTRY,
                listOf(
                    apparatus("Stopped-flow or timed reaction vessel", "Defined dead time", "Determine mixing delay"),
                    apparatus("Spectrophotometer or conductivity probe", "Time-resolved acquisition", "Blank and response verification"),
                    apparatus("Thermostated holder", "±0.2 °C stability", "Verify temperature before each run")
                ),
                listOf(
                    material("Reactant A", "Known standardized solution", "Series of concentrations", "Reaction-specific hazard", "Reaction waste"),
                    material("Reactant B", "Known standardized solution", "Controlled excess or series", "Reaction-specific hazard", "Reaction waste")
                ),
                listOf(
                    measure("Time", "s", "Synchronized acquisition clock", "0–3600", "Instrument time base"),
                    measure("Concentration proxy", "absorbance or conductivity", "Validated detector", "Linear method range", "Calibration and repeatability uncertainty"),
                    measure("Rate constant", "order-dependent unit", "Regression model", "Validated temperature range", "Fit confidence interval")
                ),
                "Reactant concentration or temperature",
                "Initial rate or rate constant",
                listOf("Total volume", "Ionic strength", "Mixing protocol", "Acquisition interval"),
                listOf("Verify detector response with standards", "Measure mixing dead time", "Run a no-reactant blank"),
                listOf("Quench remaining reactive mixtures", "Collect reaction products by compatibility", "Rinse timed flow paths into reaction waste")
            )
        listOf("electrolysis", "galvanic", "battery", "corrosion", "electrochem").any { it in text } ->
            specialistBlueprint(
                lesson.title,
                LaboratoryMode.WET_CHEMISTRY,
                listOf(
                    apparatus("Potentiostat/galvanostat", "Defined current and potential ranges", "Run electronic self-test and dummy-cell check"),
                    apparatus("Electrochemical cell", "Two- or three-electrode configuration", "Inspect seals and electrode spacing"),
                    apparatus("Reference electrode", "Known potential and filled junction", "Verify against a secondary reference")
                ),
                listOf(
                    material("Electrolyte", "Known salt/acid/base composition", "Cell working volume", "Corrosive or irritant", "Electrochemical aqueous waste"),
                    material("Working electrode", "Specified metal or carbon", "Known geometric area", "Metal-specific hazard", "Solid metal waste")
                ),
                listOf(
                    measure("Cell potential", "V", "Potentiostat or high-impedance voltmeter", "-5–5", "±1 mV"),
                    measure("Current", "A", "Potentiostat/galvanostat", "µA–A", "Range-dependent uncertainty"),
                    measure("Charge or corrosion rate", "C or mm y⁻¹", "Integrated electrochemical response", "Method-specific", "Area and time propagated uncertainty")
                ),
                "Applied potential/current or electrode material",
                "Current, potential, deposited mass or corrosion rate",
                listOf("Electrode area", "Electrolyte concentration", "Temperature", "Electrode spacing"),
                listOf("Verify reference-electrode potential", "Run dummy-cell electronics check", "Measure solution resistance"),
                listOf("Disconnect power before opening the cell", "Collect metal-containing electrolyte separately", "Rinse electrodes into the correct metal waste")
            )
        listOf("distillation", "recrystallization", "purification").any { it in text } ->
            specialistBlueprint(
                lesson.title,
                LaboratoryMode.WET_CHEMISTRY,
                listOf(
                    apparatus("Distillation or reflux assembly", "Ground-glass joints and condenser", "Inspect, clamp and verify unobstructed venting"),
                    apparatus("Heating mantle/hotplate", "Controlled non-flame heating", "Check controller and over-temperature protection"),
                    apparatus("Refractometer or melting-point apparatus", "Purity assessment", "Calibrate with certified references")
                ),
                listOf(
                    material("Crude mixture", "Lesson-specific feed", "Documented mass/volume", "Often flammable", "Organic process waste"),
                    material("Purification solvent", "Selected using boiling point or solubility", "Minimum effective volume", "Flammable/toxic as specified", "Organic solvent waste")
                ),
                listOf(
                    measure("Head temperature or melting range", "°C", "Calibrated temperature system", "Ambient–250", "±0.5 °C"),
                    measure("Recovered product", "g or mL", "Balance or volumetric receiver", "Method-specific", "Balance/glassware uncertainty"),
                    measure("Purity", "%", "Refractive index, melting range or spectrum", "0–100", "Method repeatability")
                ),
                "Cut temperature, solvent volume or cooling rate",
                "Recovery and purity",
                listOf("Heating rate", "Condenser flow", "Feed composition", "Collection interval"),
                listOf("Verify temperature probe placement", "Check the system is vented and leak-free", "Run solvent/reference purity check"),
                listOf("Never distil to dryness", "Cool apparatus before disassembly", "Separate halogenated and non-halogenated solvent waste")
            )
        listOf("enzyme", "protein", "dna", "rna", "electrophores").any { it in text } ->
            specialistBlueprint(
                lesson.title,
                LaboratoryMode.BIOCHEMICAL,
                listOf(
                    apparatus("Micropipette set", "P10, P200 and P1000 ranges", "Gravimetric verification and fresh sterile tips"),
                    apparatus("Microplate reader or gel system", "Assay-specific detection", "Self-test, blank and control verification"),
                    apparatus("Temperature-controlled incubator", "Assay setpoint ±0.5 °C", "Verify with independent probe")
                ),
                listOf(
                    material("Biological sample", "Protein, nucleic acid or enzyme system", "µL–mL scale", "Treat as biological material", "Biohazard or biochemical waste"),
                    material("Assay reagent", "Buffer, substrate, stain or gel", "Protocol-specific", "Consult SDS", "Assay-specific waste")
                ),
                listOf(
                    measure("Signal", "absorbance, fluorescence or migration", "Plate reader or imaging system", "Validated assay range", "Blank and replicate uncertainty"),
                    measure("Biomolecule response", "concentration, rate or base pairs", "Calibration/ladder model", "Assay-specific", "Calibration confidence interval")
                ),
                "Substrate/sample concentration, time or field",
                "Signal, activity or migration",
                listOf("Buffer pH", "Incubation temperature", "Reaction time", "Pipetting order"),
                listOf("Verify pipette delivery", "Run molecular ladder or assay standard", "Accept blank, positive and negative controls"),
                listOf("Deactivate biological material when required", "Collect stains and gels separately", "Dispose of sharps and contaminated tips in designated containers")
            )
        listOf("material", "crystal", "semiconductor", "concrete", "nanomaterial").any { it in text } ->
            specialistBlueprint(
                lesson.title,
                LaboratoryMode.MATERIALS_TESTING,
                listOf(
                    apparatus("Materials test frame or diffractometer", "Calibrated load/detector range", "Reference standard and zero check"),
                    apparatus("Sample preparation tools", "Geometry-specific molds, cutter or press", "Verify dimensions and cleanliness"),
                    apparatus("Dimensional metrology", "Caliper/micrometer with traceable check", "Zero and check gauge block")
                ),
                listOf(
                    material("Test specimen", "Defined composition and processing history", "At least three specimens", "Dust/sharp-edge controls", "Solid materials waste"),
                    material("Reference material", "Certified phase or mechanical standard", "One verification specimen", "Material-specific", "Return/archive when reusable")
                ),
                listOf(
                    measure("Primary material response", "MPa, 2θ, conductivity or particle size", "Calibrated test instrument", "Method-specific", "Instrument and specimen uncertainty"),
                    measure("Specimen dimension", "mm", "Micrometer/caliper", "Method geometry", "±0.01 mm")
                ),
                "Composition, processing condition or applied load",
                "Structure or material property",
                listOf("Specimen geometry", "Loading/acquisition rate", "Temperature", "Conditioning history"),
                listOf("Verify with a certified reference material", "Check load/detector zero and range", "Measure specimen dimensions in replicate"),
                listOf("Control dust during cutting/grinding", "Collect fractured solids by material class", "Store nanomaterial waste in sealed labelled containers")
            )
        listOf("environment", "pollution", "water", "atmospher", "marine", "soil", "waste").any { it in text } ->
            specialistBlueprint(
                lesson.title,
                LaboratoryMode.ENVIRONMENTAL_FIELD,
                listOf(
                    apparatus("Field multiparameter meter", "pH, conductivity, temperature and oxygen", "Field-calibrate immediately before sampling"),
                    apparatus("Preserved sample bottles", "Analyte-specific material and preservative", "Verify lot, blank and chain-of-custody labels"),
                    apparatus("GPS and field log", "Timestamped sampling location", "Verify location and clock")
                ),
                listOf(
                    material("Environmental sample", "Air, water, soil or sediment", "Triplicate representative samples", "Unknown biological/chemical hazard", "Return to laboratory waste stream"),
                    material("Field blank", "Analyte-free matrix", "One per sampling batch", "Preservative hazard may apply", "Matching analytical waste")
                ),
                listOf(
                    measure("Target pollutant or condition", "mg L⁻¹, µg m⁻³ or soil unit", "Validated field/laboratory method", "Regulatory method range", "Sampling plus analytical uncertainty"),
                    measure("Field parameters", "pH, °C, µS cm⁻¹ or mg L⁻¹", "Calibrated multiparameter meter", "Environmental range", "Probe specification")
                ),
                "Sampling location, time or treatment",
                "Pollutant concentration or environmental response",
                listOf("Sampling depth", "Container/preservative", "Holding time", "Weather or flow condition"),
                listOf("Calibrate field probes with traceable standards", "Collect field blank and duplicate", "Maintain chain of custody and holding time"),
                listOf("Treat returned samples as potentially contaminated", "Segregate preservative-containing samples", "Document transport and final disposal")
            )
        listOf("spectroscop", "nmr", "esr", "raman", "infrared", "mass spect", "xrd", "xps", "aas", "xrf", "icp").any { it in text } ->
            spectroscopyBlueprint(lesson.title)
        listOf("chromat", "hplc", "gc", "tlc", "ion exchange", "electrophoresis", "separation").any { it in text } ->
            separationBlueprint(lesson.title)
        motif in setOf(
            ChemistryVisualMotif.COMPUTATIONAL,
            ChemistryVisualMotif.QUANTUM_CHEMISTRY,
            ChemistryVisualMotif.MOLECULAR_DYNAMICS
        ) -> computationalBlueprint(lesson.title)
        else -> LabBlueprint(
            name = "${lesson.title} evidence laboratory",
            mode = when (motif) {
                ChemistryVisualMotif.BIOCHEMISTRY, ChemistryVisualMotif.NUCLEIC_ACID,
                ChemistryVisualMotif.PROTEIN_FOLDING, ChemistryVisualMotif.ENZYME_CATALYSIS,
                ChemistryVisualMotif.METABOLIC_PATHWAY -> LaboratoryMode.BIOCHEMICAL
                ChemistryVisualMotif.MATERIALS, ChemistryVisualMotif.NANOMATERIAL,
                ChemistryVisualMotif.CRYSTAL_SOLID, ChemistryVisualMotif.CRYSTAL_PACKING,
                ChemistryVisualMotif.SEMICONDUCTOR_BANDS, ChemistryVisualMotif.BATTERY_MATERIAL ->
                    LaboratoryMode.MATERIALS_TESTING
                ChemistryVisualMotif.ENVIRONMENTAL_CYCLE, ChemistryVisualMotif.GREEN_CHEMISTRY,
                ChemistryVisualMotif.AGRICULTURAL, ChemistryVisualMotif.ASTROCHEMISTRY ->
                    LaboratoryMode.ENVIRONMENTAL_FIELD
                ChemistryVisualMotif.ATOMIC_MODEL, ChemistryVisualMotif.PERIODIC_TRENDS,
                ChemistryVisualMotif.BOND_GEOMETRY, ChemistryVisualMotif.ELECTRON_CONFIGURATION ->
                    LaboratoryMode.MODEL_INVESTIGATION
                else -> LaboratoryMode.WET_CHEMISTRY
            },
            apparatus = standardApparatus,
            materials = standardMaterials.mapIndexed { index, item ->
                if (index == 0) item.copy(name = "${lesson.title} sample", identity = lesson.formula) else item
            },
            coreSteps = genericSteps(lesson.title),
            measurements = listOf(
                measure(lesson.title, "lesson-appropriate SI unit", "Calibrated primary instrument", "Validated method range", "Instrument resolution and replicate SD"),
                measure("Control response", "same response unit", "Same measurement system", "Acceptance range", "Control-chart limits")
            ),
            independent = "${lesson.title} experimental condition",
            dependent = "${lesson.title} measured response",
            controls = listOf("Sample amount", "Temperature", "Measurement time", "Instrument settings"),
            positiveControl = "Reference known to exhibit the expected ${lesson.title} response",
            negativeControl = "Blank or matrix known not to exhibit the response",
            calibration = listOf("Verify the primary instrument with a traceable reference", "Run blank and positive-control checks", "Document calibration status and expiry"),
            waste = listOf("Segregate samples according to their SDS compatibility", "Decontaminate reusable apparatus", "Record waste identity, quantity and destination")
        )
    }
}

/**
 * Builds an original lab around the lesson's scientific objective. Labster
 * stories and assets are not copied; the contract models real laboratory
 * controls, traceability and safety.
 */
internal fun authenticLabFor(
    id: String,
    module: CurriculumModule,
    lesson: CurriculumLesson,
    motif: ChemistryVisualMotif
): AuthenticLabSpec {
    val blueprint = blueprintFor(module, lesson, motif)
    return AuthenticLabSpec(
        id = "$id-authentic-lab",
        title = blueprint.name,
        mode = blueprint.mode,
        objective = "Generate defensible experimental evidence for ${lesson.title}, apply ${lesson.formula}, and report a result that is traceable, uncertainty-aware and chemically justified.",
        ppe = standardPpe,
        apparatus = blueprint.apparatus,
        materials = blueprint.materials,
        calibrationSteps = blueprint.calibration,
        procedure = blueprint.coreSteps,
        measurements = blueprint.measurements,
        independentVariable = blueprint.independent,
        dependentVariable = blueprint.dependent,
        controlledVariables = blueprint.controls,
        positiveControl = blueprint.positiveControl,
        negativeControl = blueprint.negativeControl,
        calculationModel = lesson.formula,
        acceptanceCriteria = listOf(
            "Blank and controls meet their predefined limits",
            "At least three valid replicates are recorded",
            "Relative spread is consistent with the method and instrument",
            "Final units, significant figures and uncertainty are reported",
            "Mass, charge and chemical plausibility checks pass"
        ),
        failureStates = listOf(
            "Calibration or control failure: stop, diagnose and repeat the affected batch",
            "Contamination or sample mix-up: quarantine results and prepare fresh samples",
            "Out-of-range signal: dilute, change method range or select a suitable instrument",
            "Unsafe action or incompatible waste: stop the procedure and apply the emergency/SDS protocol"
        ),
        wastePlan = blueprint.waste,
        reportSections = listOf(
            "Aim and chemical principle",
            "Risk assessment and PPE",
            "Apparatus, reagents and traceability",
            "Raw observations and unedited data",
            "Calculations, uncertainty and quality controls",
            "Results and acceptance decision",
            "Discussion, limitations and improvements",
            "Waste, cleanup and signed conclusion"
        )
    )
}
