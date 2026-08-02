package com.indianservers.aichemistryexplorer

private fun generalChemistry(
    technique: LabTechnique,
    action: String,
    basis: String,
    apparatus: String,
    materials: String,
    safety: String,
    measure: String,
    target: Double,
    unit: String,
    analysis: String,
    operations: List<String>
) = RequestedProtocol(
    technique = technique,
    objectiveAction = action,
    scientificBasis = basis,
    apparatus = apparatus,
    reagents = materials,
    safety = safety,
    procedure = listOf(
        "Verify apparatus condition and calibration, review the method-specific hazards, and document blank, reference and acceptance criteria."
    ) + operations + listOf(
        "Run independent replicates, retain raw observations with units, evaluate controls and uncertainty, then complete method-specific waste close-out."
    ),
    measuredQuantity = measure,
    referenceValue = target,
    unit = unit,
    analysis = analysis
)

/**
 * Exhaustive, title-specific protocols for requested inventory items 21–62.
 * There is intentionally no generic fallback.
 */
internal fun generalChemistryProtocol(title: String): RequestedProtocol = when (title) {
    "Physical versus chemical changes" -> generalChemistry(
        LabTechnique.QUALITATIVE,
        "distinguish physical changes from chemical reactions using reversible-property and new-substance evidence",
        "A physical change preserves chemical identity, whereas a chemical change changes bonding and composition. Color or phase change alone is insufficient; gas, precipitate, energy and reversibility evidence must be interpreted with controls.",
        "Microwell plate · capped microtubes · balance · conductivity probe · temperature probe",
        "Ice/water · sodium chloride · paraffin · sodium bicarbonate/citric acid · copper sulfate/alkali microscale set",
        "Use microscale quantities and splash protection; vent gas-producing tubes, avoid sealing reactions and segregate copper-containing residues.",
        "evidence-classification accuracy", 100.0, "%",
        "Classify each transformation only after comparing mass, conductivity, reversibility and product evidence. Treat dissolving and phase changes as physical unless chemical speciation evidence supports otherwise.",
        listOf(
            "Record initial mass, state, color, conductivity and temperature for each controlled sample.",
            "Melt/refreeze water or paraffin and dissolve/recover sodium chloride to test reversible identity.",
            "Run gas-forming and precipitation reactions beside reagent blanks and record product evidence.",
            "Construct an evidence table separating observation from the conclusion and balance applicable equations."
        )
    )
    "Homogeneous versus heterogeneous mixtures" -> generalChemistry(
        LabTechnique.SEPARATION,
        "classify mixtures by phase uniformity and verify classification through sampling and separation",
        "A homogeneous mixture has uniform composition at the sampling scale; a heterogeneous mixture contains distinguishable phases or spatially varying composition. Colloids may appear uniform yet scatter light.",
        "Sample vials · microscope · conductivity meter · Tyndall-light source · filter · centrifuge",
        "Salt solution · sand/water suspension · milk colloid · oil/water emulsion · certified uniform dye solution",
        "Do not stare into the light beam; cap samples before mixing and dispose of oil/emulsion separately from drain-compatible salt solution.",
        "mixture classification accuracy", 100.0, "%",
        "Compare replicate aliquots from top, middle and bottom using appearance, conductivity or absorbance; uniformity within measurement uncertainty supports homogeneous classification.",
        listOf(
            "Prepare equal-volume examples without assuming that transparency means homogeneous.",
            "Sample each mixture at three depths before and after controlled mixing.",
            "Apply filtration, settling/centrifugation and Tyndall scattering as diagnostic tests.",
            "Relate separability and spatial variance to solution, colloid, suspension or emulsion classification."
        )
    )
    "Atom, ion and isotope construction" -> generalChemistry(
        LabTechnique.PHYSICAL_MEASUREMENT,
        "construct atoms, ions and isotopes while conserving nucleon and electron counts",
        "Atomic number Z equals protons; mass number A equals protons plus neutrons; ionic charge q/e equals protons minus electrons. Isotopes share Z but differ in neutron number.",
        "Particle-count model · nuclide-card reader · periodic-table reference · charge/mass validator",
        "Proton, neutron and electron tokens · stable/unstable nuclide cards · ion challenge set",
        "This is a model investigation: particle placement is symbolic and not to scale; distinguish mass number from relative atomic mass.",
        "particle-accounting accuracy", 100.0, "%",
        "For every species verify Z = p, A = p + n and charge = p − e. Reject impossible element labels and report nuclide notation as ᴬZX with charge.",
        listOf(
            "Build a neutral reference atom from a supplied atomic number and mass number.",
            "Generate cations and anions by changing electrons only, never protons.",
            "Generate isotopes by changing neutrons while preserving atomic number.",
            "Decode blinded nuclide constructions and flag unstable or physically impossible combinations."
        )
    )
    "Bohr versus quantum atomic models" -> generalChemistry(
        LabTechnique.SPECTROSCOPY,
        "compare Bohr energy-level predictions with the quantum-mechanical orbital model",
        "For hydrogen, En = −13.6 eV/n² and ΔE = hν = hc/λ. Bohr orbits reproduce hydrogenic energies but quantum mechanics replaces fixed paths with orbitals described by probability density and quantum numbers.",
        "Hydrogen discharge spectrum · spectroscope · energy-level simulator · orbital probability viewer",
        "Hydrogen Balmer reference lines · calibration lamp · orbital/quantum-number challenge set",
        "Use the discharge lamp only with intact shielding and power isolated before adjustment; model orbitals are probability distributions, not electron tracks.",
        "Balmer wavelength agreement", 99.0, "%",
        "Calculate Balmer wavelengths with the Rydberg relation, compare residuals with measured lines and state precisely why the Bohr model fails for multielectron spectra and bonding.",
        listOf(
            "Calibrate wavelength using a reference lamp and acquire visible hydrogen emission lines.",
            "Assign transitions terminating at n = 2 and calculate wavelengths from energy differences.",
            "Compare fixed-orbit imagery with s and p probability-density/isodensity representations.",
            "Test allowed quantum-number sets and document the predictive scope of each model."
        )
    )
    "Interactive periodic-table organization" -> generalChemistry(
        LabTechnique.PHYSICAL_MEASUREMENT,
        "organize elements from electron configuration and validate periodic property trends",
        "The modern periodic table is ordered by atomic number; period reflects the highest occupied principal shell, while valence configuration explains blocks and much group similarity. Trends have chemically meaningful exceptions.",
        "Interactive periodic grid · electron-configuration engine · property dataset · trend plotting tool",
        "Element cards Z = 1–36 · ionization-energy and radius reference data · blinded element set",
        "This is a data/model activity; never treat trend arrows as exact predictions or ignore oxidation state, shielding and subshell effects.",
        "element placement accuracy", 100.0, "%",
        "Place elements by Z and configuration, plot rather than assume trends, and explain anomalies such as B/Be and O/N ionization energies using subshell energy and electron pairing.",
        listOf(
            "Arrange shuffled elements by atomic number and mark periods, groups and s/p/d/f blocks.",
            "Generate ground-state configurations and infer period, block and common valence behavior.",
            "Plot atomic radius and first-ionization energy across selected periods and groups.",
            "Use evidence to identify blinded elements and explain deviations from simple monotonic trends."
        )
    )
    "Ionic and covalent compound identification" -> generalChemistry(
        LabTechnique.QUALITATIVE,
        "identify ionic, molecular-covalent and network-covalent substances from converging property evidence",
        "Ionic solids contain extended ion lattices and conduct when mobile ions exist in melt or solution; molecular substances consist of discrete molecules, while network covalent solids have extended bonds and distinct conductivity/hardness behavior.",
        "Conductivity probe · melting-point simulator · solubility rack · crystal model · low-voltage circuit",
        "Sodium chloride · sucrose · paraffin · graphite · silicon dioxide · deionized-water blank",
        "Use low voltage only, avoid tasting substances, contain powders and do not infer bond type from one property or from the presence of a metal alone.",
        "bonding-classification confidence", 0.95, "fraction",
        "Score solid/solution conductivity, solubility and thermal behavior against references. Report graphite and network solids explicitly as exceptions to a simplistic ionic-versus-covalent dichotomy.",
        listOf(
            "Observe physical state and test solid conductivity using the validated low-voltage circuit.",
            "Test controlled solubility in water and a nonpolar medium using equal sample masses.",
            "Measure solution conductivity against water and known ionic/molecular controls.",
            "Combine all observations with structural models to classify each blinded substance."
        )
    )
    "Intermolecular-force investigation" -> generalChemistry(
        LabTechnique.PHYSICAL_MEASUREMENT,
        "relate intermolecular attractions to evaporation, viscosity, surface tension and boiling behavior",
        "London dispersion acts between all particles; permanent dipoles add dipole–dipole attraction and suitable X–H groups enable hydrogen bonding. Molecular size and shape must be controlled before attributing a property difference to one force.",
        "Temperature probes · capillary viscometer · drop counter · evaporation pads · thermostated bath",
        "Pentane/hexane simulation · acetone · ethanol · 1-propanol · water · glycerol",
        "Use the volatile-liquid set only in ventilation with ignition sources excluded; collect organic liquids by compatibility.",
        "structure–property model agreement", 95.0, "%",
        "Compare matched molecular pairs, plot evaporation cooling/viscosity versus structural descriptors and avoid ranking a molecule by a single named force without considering polarizability and geometry.",
        listOf(
            "Record equal-volume evaporation cooling under identical airflow and surface area.",
            "Measure relative viscosity at controlled temperature and drop-count surface behavior.",
            "Compare homologous and functional-group-matched pairs to isolate size and hydrogen bonding.",
            "Build and test a multivariable explanation for an unknown liquid’s property pattern."
        )
    )
    "Ideal Gas Law introduction" -> generalChemistry(
        LabTechnique.GAS,
        "determine the gas constant from corrected pressure, volume, amount and temperature measurements",
        "PV = nRT describes an ideal gas; pressure must be absolute, temperature in kelvin and wet-gas pressure corrected by subtracting water-vapor pressure.",
        "Gas syringe · absolute-pressure sensor · thermostated bath · temperature probe · leak-test valve",
        "Dry air or nitrogen · water bath · pressure reference · known-volume calibration",
        "Use pressure-rated apparatus, protect the sensor from liquid ingress and never heat a closed rigid volume beyond its rating.",
        "experimental gas constant", 8.314, "J mol⁻¹ K⁻¹",
        "Calculate R = PV/nT with SI units, propagate P/V/T/n uncertainty and inspect compressibility-related deviations at extreme conditions.",
        listOf(
            "Leak-test the apparatus and calibrate volume, pressure and temperature channels.",
            "Trap a known amount of gas and establish multiple equilibrium temperatures/volumes.",
            "Record absolute pressure only after thermal and mechanical stabilization.",
            "Calculate R for each state and evaluate constancy with residual and uncertainty plots."
        )
    )
    "Gas-law medical application" -> generalChemistry(
        LabTechnique.GAS,
        "model safe gas delivery by applying Boyle, Charles and Dalton relationships to respiratory scenarios",
        "Lung and delivery-gas behavior requires absolute pressure and temperature; Boyle’s relation describes compression, Charles’s relation thermal expansion and Dalton’s law gives partial pressure Pi = yiPtotal.",
        "Lung syringe model · pressure/volume sensors · humidifier model · oxygen-fraction analyzer",
        "Medical-air simulation · oxygen/nitrogen mixture data · water-vapor table · altitude scenarios",
        "This simulation is educational and must not be used to set clinical oxygen, ventilator or diving parameters; real decisions require qualified clinicians and validated equipment.",
        "corrected oxygen partial pressure", 19.9, "kPa",
        "Correct for water vapor before calculating inspired partial pressure and distinguish gauge from absolute pressure. State model limits for nonideal lungs and regulated flow.",
        listOf(
            "Verify sensors and record ambient absolute pressure, temperature and humidity.",
            "Generate pressure–volume data with the lung syringe at isothermal conditions.",
            "Calculate dry and humidified oxygen partial pressures at altitude and sea level.",
            "Identify unsafe reasoning errors involving gauge pressure, Celsius or ignored water vapor."
        )
    )
    "Constructing a temperature scale" -> generalChemistry(
        LabTechnique.PHYSICAL_MEASUREMENT,
        "construct and validate a linear temperature scale from reproducible fixed points",
        "A linear scale maps a thermometric property between defined fixed points. Celsius uses water-related reference conventions; kelvin has the same interval with T/K = t/°C + 273.15 and cannot be treated as a degree offset in ratios.",
        "Temperature sensor · ice-point bath · boiling-point apparatus · barometer · fixed-point regression tool",
        "Pure ice/water · boiling water · ambient validation bath · pressure correction data",
        "Use heat-resistant gloves and splash shielding; boiling point depends on pressure, so record barometric pressure rather than assuming 100 °C.",
        "temperature-scale validation error", 0.2, "°C",
        "Fit property = a + bt from fixed points, correct boiling reference for pressure and validate at an intermediate temperature with uncertainty.",
        listOf(
            "Measure sensor response in a well-prepared stirred ice–water equilibrium bath.",
            "Measure boiling-point response while recording atmospheric pressure.",
            "Define equal intervals and derive the response-to-temperature calibration equation.",
            "Validate the constructed scale in an independent bath and convert results to kelvin."
        )
    )
    "Heating curves and phase changes" -> generalChemistry(
        LabTechnique.CALORIMETRY,
        "measure a heating curve and separate sensible heat from latent-heat regions",
        "Within one phase q = mcΔT; during a first-order phase change added energy changes phase at nearly constant temperature, q = nΔHtrans, subject to heat loss and pressure.",
        "Controlled heater · insulated vessel · calibrated temperature probe · balance · power logger",
        "Pure ice/water or approved phase-change material · empty-vessel blank",
        "Use electrical and thermal guards, do not seal the vessel and keep the probe from contacting the heater.",
        "water fusion plateau", 0.0, "°C",
        "Plot temperature versus cumulative energy, fit slopes within phases and integrate plateau energy after blank heat-loss correction.",
        listOf(
            "Weigh the sample and record a sub-melting baseline under constant mixing.",
            "Apply measured electrical power and log temperature at fixed time intervals.",
            "Continue through melting and liquid warming without changing heater geometry.",
            "Identify phase regions objectively and calculate specific/latent heat with losses."
        )
    )
    "Ethanol distillation" -> generalChemistry(
        LabTechnique.SEPARATION,
        "separate and characterize an ethanol–water mixture by fractional distillation",
        "Vapor is enriched in the more volatile component according to vapor–liquid equilibrium; ethanol–water forms a minimum-boiling azeotrope, so ordinary distillation cannot produce absolute ethanol.",
        "Fractional-distillation train · packed column · thermometer at takeoff · condenser · refractometer",
        "Ethanol–water feed · boiling chips · refractive-index standards · cooling water",
        "Ethanol vapor is highly flammable: use electric heating in a fume hood, inspect joints, keep the system open to atmosphere and cool before disassembly.",
        "ethanol-rich fraction boiling range", 78.3, "°C",
        "Plot head temperature and ethanol composition versus collected volume, close mass balance and explicitly identify the azeotropic separation limit.",
        listOf(
            "Assemble and leak-inspect the open fractional column with thermometer bulb at the sidearm.",
            "Charge measured feed and anti-bumping aid, then establish steady reflux.",
            "Collect labeled fractions at controlled takeoff rate while logging head temperature.",
            "Measure fraction composition and recovery using a calibrated refractive-index model."
        )
    )
    "Simple biodiesel-waste distillation" -> generalChemistry(
        LabTechnique.SEPARATION,
        "recover methanol-rich volatile material from biodiesel process waste under controlled conditions",
        "Distillation separates volatile methanol from less volatile glycerol, water, catalyst and salts; composition and nonideal boiling behavior require fraction analysis rather than temperature alone.",
        "Microscale simple-distillation train · electric mantle · condenser · receiver ice bath · refractometer",
        "Simulated glycerol/methanol/water waste · boiling chips · composition standards",
        "Methanol is toxic and highly flammable; perform only in effective ventilation with closed receivers and no ignition sources, and never distill to dryness.",
        "methanol recovery", 85.0, "%",
        "Use feed and fraction mass/composition to calculate component recovery and mass-balance closure; reject reuse claims without purity verification.",
        listOf(
            "Characterize feed mass and composition, and inspect the distillation train for open venting.",
            "Heat electrically at a controlled rate while cooling the receiver and condenser.",
            "Collect early, transition and residue fractions without approaching dryness.",
            "Measure methanol enrichment and calculate recovery, purity and unreconciled loss."
        )
    )
    "Separating mixtures by distillation" -> generalChemistry(
        LabTechnique.SEPARATION,
        "select simple or fractional distillation from boiling-point and efficiency evidence",
        "Separation depends on vapor–liquid equilibrium and repeated theoretical stages; simple distillation suits a volatile/nonvolatile pair or widely separated boiling points, while close-boiling liquids require fractionation.",
        "Simple and fractional columns · controlled heater · head thermometer · condenser · composition detector",
        "Wide-boiling and close-boiling binary-mixture simulations · boiling chips · calibration standards",
        "Never seal heated apparatus, use electric heating for flammables, maintain condenser flow and stop before the still pot is dry.",
        "fractional-column resolution gain", 50.0, "%",
        "Compare composition profiles, recovery and energy/time for both configurations; quantify separation factor instead of judging from temperature alone.",
        listOf(
            "Predict the appropriate apparatus from component volatility and azeotrope information.",
            "Run equal feed amounts through simple distillation at a controlled takeoff rate.",
            "Repeat with a packed fractionating column and equivalent heat input.",
            "Analyze fractions and compare purity, recovery, resolution and limitations."
        )
    )
    "Properties of water" -> generalChemistry(
        LabTechnique.PHYSICAL_MEASUREMENT,
        "quantify water’s polarity-linked thermal, cohesive and solvent properties",
        "Bent geometry and hydrogen bonding contribute to water’s high heat capacity, surface tension and dielectric behavior; density and phase behavior depend strongly on temperature and structure.",
        "Conductivity probe · calorimeter · capillary tube · temperature-controlled density vessel",
        "Deionized water · saline standard · ethanol comparison · nonpolar-liquid simulation",
        "Keep electrical probes at low voltage, handle comparison solvents in ventilation and do not infer purity from visual clarity.",
        measure = "specific heat capacity of water", target = 4.184, unit = "J g⁻¹ K⁻¹",
        analysis = "Calculate specific heat from calibrated energy input and compare surface/dissolution responses with matched controls; distinguish intrinsic water properties from dissolved-ion effects.",
        operations = listOf(
            "Verify water purity by conductivity and measure density at controlled temperature.",
            "Apply known heat and calculate specific heat after vessel blank correction.",
            "Measure capillary/surface behavior against an ethanol comparison under identical geometry.",
            "Test ionic and nonpolar solubility models and reconcile exceptions with molecular interactions."
        )
    )
    "Preparing a solution from a solid" -> generalChemistry(
        LabTechnique.PHYSICAL_MEASUREMENT,
        "prepare a traceable molar solution from a solid standard",
        "For a pure solid, required mass m = cVM. Quantitative transfer and final dilution in a calibrated volumetric flask establish concentration; beakers are not calibrated preparation vessels.",
        "Analytical balance · weighing bottle · powder funnel · Class A volumetric flask · transfer pipette",
        "Dried sodium chloride or potassium hydrogen phthalate · deionized water · certified check mass",
        "Review solid hazards and hygroscopicity, avoid dust, never add final solvent before complete dissolution and label the finished solution immediately.",
        "prepared concentration recovery", 100.0, "%",
        "Correct for purity if required, calculate mass with significant figures and propagate balance, purity and flask-volume uncertainty.",
        listOf(
            "Calculate target mass from desired concentration, final volume, molar mass and purity.",
            "Verify the balance and weigh by difference into a clean vessel.",
            "Dissolve below final volume and quantitatively transfer all rinsings to the flask.",
            "Equilibrate to calibration temperature, dilute to the mark, mix repeatedly and label."
        )
    )
    "Serial dilution" -> generalChemistry(
        LabTechnique.PHYSICAL_MEASUREMENT,
        "prepare and verify a serial dilution while controlling cumulative transfer error",
        "Each dilution obeys C1V1 = C2V2, and the total dilution factor is the product of step factors. Error and contamination can propagate through every downstream tube.",
        "Calibrated micropipettes · Class A flasks/tubes · mixer · absorbance or conductivity detector",
        "Certified colored/conductive stock · matrix blank · independent end-point QC",
        "Use fresh tips to prevent carryover, mix every stage before transfer and never pipette outside the calibrated volume range.",
        "terminal dilution recovery", 100.0, "%",
        "Calculate each stage and cumulative factor, propagate pipette/flask uncertainty, and compare the terminal tube with an independently prepared QC.",
        listOf(
            "Design step volumes so every transfer falls comfortably inside pipette range.",
            "Prepare labeled diluent vessels and mix the stock before the first transfer.",
            "Transfer, dispense, mix completely and change tips at every sequential stage.",
            "Measure all stages, plot response versus expected concentration and detect propagation bias."
        )
    )
    "Acid–base titration" -> generalChemistry(
        LabTechnique.TITRATION,
        "standardize a titrant and determine an unknown acid concentration from concordant titres",
        "At equivalence, n(acid)/coefficient = n(base)/coefficient. The endpoint is an observed indicator or instrumental change and must be selected close to the equivalence region.",
        "Class A burette · 25.00 mL pipette · conical flask · pH meter · magnetic stirrer",
        "Primary-standard KHP · approximately 0.100 M NaOH · HCl unknown · phenolphthalein/buffers",
        "HCl and NaOH are corrosive; remove the burette funnel before readings, clear tip bubbles and collect indicator-containing waste correctly.",
        "unknown acid concentration", 0.100, "mol L⁻¹",
        "Standardize NaOH first, use only concordant titres (typically within 0.10 mL), apply balanced stoichiometry and propagate glassware/standard uncertainty.",
        listOf(
            "Dry/cool KHP as specified, weigh by difference and standardize the NaOH.",
            "Condition burette/pipette and transfer an exact unknown aliquot.",
            "Titrate while recording the curve, approaching the endpoint dropwise.",
            "Obtain three concordant titres and calculate unknown concentration separately for each."
        )
    )
    "Advanced acid–base equilibria" -> generalChemistry(
        LabTechnique.EQUILIBRIUM,
        "resolve polyprotic, weak-acid/base and activity effects from complete titration data",
        "Acid–base speciation follows mass balance, charge balance and Ka relationships; Henderson–Hasselbalch is a buffer-region approximation, while activities replace concentrations at non-negligible ionic strength.",
        "Autotitrator · calibrated pH electrode · temperature probe · ionic-strength model · volumetric glassware",
        "Diprotic acid unknown · standardized strong base · pH 4/7/10 buffers · ionic-strength adjuster",
        "Use splash protection for titrant and analyte; calibrate with temperature compensation and keep electrode hydrated without contaminating samples.",
        "recovered pKa1", 4.20, "",
        "Fit the entire titration curve with mass/charge balance, compare derivative endpoints and report where Henderson–Hasselbalch or concentration-only assumptions fail.",
        listOf(
            "Calibrate the electrode with three traceable buffers bracketing the measurement range.",
            "Collect dense, equilibrated pH data across both equivalence regions.",
            "Locate endpoints by stoichiometry and derivative evidence without deleting noisy points.",
            "Fit pKa values and species fractions, then test ionic-strength sensitivity."
        )
    )
    "Everyday acidity and alkalinity" -> generalChemistry(
        LabTechnique.TITRATION,
        "compare pH and titratable acidity/alkalinity in consumer and environmental samples",
        "pH measures hydrogen-ion activity at one state, whereas titratable acidity or alkalinity measures the amount of standard base or acid required to reach a defined endpoint; they are not interchangeable.",
        "Calibrated pH meter · burette · pipette · magnetic stirrer · sample degassing setup",
        "Vinegar/juice/water/antacid simulations · standardized acid/base · buffers · blanks",
        "Do not taste samples; consumer products remain laboratory chemicals after transfer, and carbonated samples must be vented without sealing.",
        "matrix result agreement", 95.0, "%",
        "Report pH and titratable capacity separately, apply dilution and blank corrections, and explain matrix buffering rather than ranking samples by pH alone.",
        listOf(
            "Homogenize or degas samples and document any quantitative dilution.",
            "Measure stable pH after a bracketing buffer calibration.",
            "Titrate measured aliquots to the method-defined pH endpoint.",
            "Calculate acid/base equivalents per original sample amount and compare with direct pH."
        )
    )
    "Chemical-equilibrium perturbation" -> generalChemistry(
        LabTechnique.EQUILIBRIUM,
        "test concentration and temperature perturbations without changing equilibrium-constant interpretation",
        "After a disturbance, reaction quotient Q predicts direction: Q < K favors products and Q > K favors reactants. At constant temperature K is unchanged by concentration or catalyst; only temperature changes K.",
        "Spectrophotometer · thermostated cuvette holder · calibrated pipettes · reversible-equilibrium cells",
        "Iron(III)/thiocyanate equilibrium microscale set · standards · matrix blank · temperature baths",
        "Iron and thiocyanate solutions require gloves and controlled waste; use capped cuvettes and avoid confusing darker color from dilution/path changes.",
        "equilibrium mass-balance closure", 99.0, "%",
        "Calculate Q immediately after each perturbation and K after re-equilibration; verify that same-temperature K values agree within uncertainty.",
        listOf(
            "Create and calibrate a response curve for the colored equilibrium species.",
            "Measure an equilibrated control and calculate its K value.",
            "Apply separate concentration, dilution and temperature perturbations with matched controls.",
            "Track the new plateau, calculate Q/K behavior and distinguish rate from equilibrium position."
        )
    )
    "Reaction-kinetics fundamentals" -> generalChemistry(
        LabTechnique.KINETICS,
        "derive a rate law from initial-rate experiments with controlled mixing and temperature",
        "For rate = k[A]^m[B]^n, concentration ratios across initial-rate trials reveal orders; molecularity applies only to an elementary step and must not be inferred from the overall equation.",
        "Stopped-mix or rapid-mix vessel · spectrophotometer · thermostat · calibrated pipettes · timer",
        "Crystal-violet/hydroxide or clock-reaction set · reagent blank · concentration matrix",
        "Control corrosive/oxidizing reagents and start timing only after safe reproducible mixing; contain all dye and reaction waste.",
        "rate-law model fit", 0.98, "R²",
        "Use early-time slopes, ratio or regression methods to estimate orders and k, inspect residuals and report temperature with the rate constant.",
        listOf(
            "Prepare a design that changes one reactant concentration independently.",
            "Equilibrate solutions and detector at a fixed temperature.",
            "Initiate reactions with identical mixing and record dense early-time signals.",
            "Fit initial rates, determine orders and test predicted rates on a held-out condition."
        )
    )
    "Bomb calorimetry" -> generalChemistry(
        LabTechnique.CALORIMETRY,
        "determine constant-volume combustion energy using calibrated bomb calorimetry",
        "At constant bomb volume qv = ΔU. The calorimeter constant converts corrected temperature rise to heat; fuse-wire/acid corrections and ΔH = ΔU + ΔngRT may be required.",
        "Pressure-rated oxygen bomb · calorimeter bucket · ignition circuit · 0.001 °C probe · analytical balance",
        "Certified benzoic acid · compacted fuel sample · oxygen · ignition wire · wash water",
        "Inspect the pressure vessel and seals, use only approved oxygen pressure, shield ignition, verify electrical isolation and vent slowly only after cooling.",
        "benzoic-acid calibration recovery", 100.0, "%",
        "Determine Ccal from certified benzoic acid, apply ignition/acid corrections, calculate sample ΔU per mole or mass and state conversion assumptions for ΔH.",
        listOf(
            "Weigh benzoic acid and ignition wire, charge oxygen to the approved pressure and leak-check.",
            "Ignite under shielding while logging pre-period, rise and post-period temperatures.",
            "Vent after cooling, inspect complete combustion and quantify wire/acid corrections.",
            "Repeat with the sample and calculate corrected constant-volume energy."
        )
    )
    "Reaction energy surfaces" -> generalChemistry(
        LabTechnique.KINETICS,
        "map reactant, transition-state, intermediate and product energies on reaction coordinates",
        "A potential-energy surface assigns energy to nuclear configurations; stationary minima represent stable/intermediate structures and first-order saddle points represent transition states. Activation energy differs from reaction energy.",
        "Molecular-model engine · coordinate scanner · transition-state validator · energy-unit converter",
        "Reference reaction pathway · alternative mechanism structures · benchmark energies",
        "Computational energies are model-dependent; validate structures and frequencies and never present an unverified animation as a reaction mechanism.",
        "stationary-point assignment accuracy", 100.0, "%",
        "Confirm minima have no imaginary frequencies and transition states one appropriate imaginary mode; report forward/reverse barriers and ΔE using a consistent zero.",
        listOf(
            "Optimize reactant and product structures with a documented model chemistry.",
            "Scan a chemically meaningful coordinate to generate an initial energy profile.",
            "Locate candidate transition states/intermediates and validate Hessian signatures.",
            "Compare competing pathways and identify kinetic versus thermodynamic conclusions."
        )
    )
    "Spontaneity investigation" -> generalChemistry(
        LabTechnique.EQUILIBRIUM,
        "determine spontaneity from enthalpy, entropy and temperature rather than reaction speed",
        "At constant temperature and pressure ΔG = ΔH − TΔS; ΔG < 0 indicates thermodynamic favorability in the stated direction, ΔG = 0 equilibrium, and does not imply a fast rate.",
        "Calorimetric dataset · equilibrium-cell simulator · temperature control · thermodynamic calculator",
        "Dissolution/reaction systems with known ΔH and ΔS · kinetic counterexample dataset",
        "Thermodynamic favorability does not remove activation, pressure or incompatibility hazards; assess actual reaction controls independently.",
        "ΔG sign prediction accuracy", 100.0, "%",
        "Use kelvin and consistent molar units, calculate crossover temperature only where meaningful and compare ΔG° with equilibrium through ΔG° = −RT ln K.",
        listOf(
            "Classify supplied processes by signs of ΔH and ΔS without assuming spontaneity.",
            "Calculate ΔG across temperature for selected cases and locate sign changes.",
            "Relate measured equilibrium constants to standard Gibbs energy.",
            "Compare a favorable slow process with a rapid unfavorable reverse to separate kinetics from thermodynamics."
        )
    )
    "Avogadro-number calculations" -> generalChemistry(
        LabTechnique.PHYSICAL_MEASUREMENT,
        "connect microscopic entity counts to measurable amount of substance",
        "One mole contains exactly NA = 6.02214076 × 10²³ specified entities. Entity type must be stated, and molar mass converts between mass and amount without changing stoichiometric identity.",
        "Analytical balance · molar-mass database · particle-count model · electrolysis dataset",
        "Element/compound samples · formula-unit and molecule challenge cards · Faraday-charge reference",
        "Avoid handling reactive elemental samples; the counting model uses inert reference masses and supplied datasets.",
        "entity-count calculation accuracy", 100.0, "%",
        "Use N = nNA and n = m/M, name atoms/molecules/formula units/ions explicitly and preserve exact NA while limiting results by measured mass and molar-mass uncertainty.",
        listOf(
            "Weigh an inert sample and calculate its amount using verified chemical formula and molar mass.",
            "Convert amount to the correct entity count and distinguish formula units from molecules.",
            "Calculate constituent atom/ion counts using formula subscripts.",
            "Cross-check an electrochemical entity-count dataset using charge per mole of electrons."
        )
    )
    "Gravimetric stoichiometry" -> generalChemistry(
        LabTechnique.GRAVIMETRY,
        "determine analyte quantity through a pure, known-composition gravimetric product",
        "A quantitative reaction converts analyte to a weighable form; analyte mass equals product mass multiplied by the stoichiometric molar-mass factor.",
        "Analytical balance · sintered crucible · hotplate · oven · desiccator",
        "Sulfate unknown · barium chloride precipitant · dilute acid · wash solution · blank",
        "Soluble barium is toxic; use containment, collect all barium waste and handle hot crucibles with tongs.",
        "gravimetric analyte recovery", 99.5, "%",
        "Use m(analyte) = m(product) × νanalyteManalyte/(νproductMproduct), blank-correct and accept only pure product at constant mass.",
        listOf(
            "Acidify and heat a measured aliquot, then add precipitant slowly with stirring.",
            "Digest the precipitate and test supernatant for complete precipitation.",
            "Filter and wash until adsorbed impurities are below the test threshold.",
            "Dry/ignite, cool in a desiccator and repeat weighing to constant mass."
        )
    )
    "Compound identification by gravimetric analysis" -> generalChemistry(
        LabTechnique.GRAVIMETRY,
        "identify a compound by comparing experimental gravimetric composition with candidate formulas",
        "Mass fraction of a conserved component is fixed by formula; quantitative conversion to a known residue or precipitate allows candidates to be discriminated within uncertainty.",
        "Analytical balance · crucibles · controlled furnace · desiccator · candidate-composition database",
        "Hydrated-salt unknown · certified reference hydrate · inert blank",
        "Heat gradually to prevent spattering, use tongs and a desiccator, and treat the final residue according to metal-ion hazards.",
        "candidate-formula confidence", 0.95, "fraction",
        "Calculate water/residue mole ratios with uncertainty, test integer consistency rather than rounding blindly and reject candidates whose expected composition lies outside the confidence interval.",
        listOf(
            "Condition the crucible and record sample mass by difference.",
            "Heat gently then strongly under the method’s defined conversion conditions.",
            "Repeat heat–cool–weigh cycles until mass is constant.",
            "Calculate component mole ratio and compare every candidate formula quantitatively."
        )
    )
    "Qualitative elemental analysis" -> generalChemistry(
        LabTechnique.QUALITATIVE,
        "identify selected elements using blank-controlled and independently confirmed tests",
        "Elemental tests rely on characteristic emission or selective conversion of ions into precipitates/gases; matrix interference means no single color observation proves identity.",
        "Flame-emission source/spectroscope · microwell plate · gas-test setup · centrifuge",
        "Known ion standards · reagent blank · unknown · selective precipitation/confirmation reagents",
        "Use flame work behind shielding with nonflammable holders; contain heavy-metal reagents and never sniff evolved gases directly.",
        "element identification confidence", 0.95, "fraction",
        "Require agreement between known reference, blank, primary response and orthogonal confirmation; explicitly record likely interferences and detection limits.",
        listOf(
            "Prepare blank, positive references and unknown with clean contamination-controlled tools.",
            "Acquire wavelength-resolved emission where appropriate rather than relying on unaided color.",
            "Perform selective wet tests on separate aliquots in the validated reagent order.",
            "Confirm each assignment independently and report unresolved or mixed evidence."
        )
    )
    "Formula construction and equation balancing" -> generalChemistry(
        LabTechnique.PHYSICAL_MEASUREMENT,
        "construct charge-neutral formulas and balance reactions by conservation laws",
        "Chemical formulas encode composition and charge neutrality; balanced equations conserve every element and net charge but do not imply mechanism or equilibrium completion.",
        "Ion/formula model · atom-inventory ledger · equation-balance solver · reaction evidence cards",
        "Common ion cards · molecular species cards · unbalanced reaction set · spectator-ion challenges",
        "Do not use a balanced paper equation as a safety assessment; thermochemistry, rate, pressure and incompatibility must still be evaluated.",
        "mass-and-charge conservation accuracy", 100.0, "%",
        "Use smallest whole-number coefficients, never alter subscripts to balance, verify element and charge totals and derive net ionic equations by cancelling true spectators only.",
        listOf(
            "Construct ionic formulas from explicit ion charges and reduce formula-unit ratios.",
            "Translate names/formulas while distinguishing molecular prefixes from ionic charges.",
            "Balance molecular equations using an element inventory.",
            "Write complete and net ionic forms, then verify mass and charge conservation."
        )
    )
    "Double-displacement reactions" -> generalChemistry(
        LabTechnique.QUALITATIVE,
        "predict and verify precipitation, neutralization and gas-forming metathesis reactions",
        "Aqueous ion exchange has a net driving process only when species form a precipitate, weak electrolyte or gas; otherwise the correct net ionic result is no reaction.",
        "Microwell plate · conductivity probe · centrifuge · pH indicator · gas-test cover",
        "Soluble ionic reactants · solubility-rule references · acid/base pair · carbonate/acid pair",
        "Use microscale quantities, vent gas formation and collect precipitates containing regulated metals in hazardous waste.",
        "net-ionic prediction accuracy", 100.0, "%",
        "Write complete ionic equations using actual strong electrolytes, cancel spectators and compare predicted precipitate/pH/gas evidence with blanks.",
        listOf(
            "Predict products and physical states before combining any pair.",
            "Combine controlled microscale aliquots with single-reagent blanks.",
            "Record precipitate, pH, conductivity and gas evidence objectively.",
            "Write molecular, complete ionic and net ionic equations, including no-reaction cases."
        )
    )
    "Single-displacement and seawater corrosion" -> generalChemistry(
        LabTechnique.ELECTROCHEMISTRY,
        "connect metal displacement potentials with chloride-accelerated galvanic corrosion",
        "A more easily oxidized metal can reduce ions of a less active metal; in seawater, ionic conductivity, dissolved oxygen, chloride film breakdown and galvanic coupling control corrosion.",
        "Metal-coupon rack · artificial-seawater cells · high-impedance voltmeter · balance · dissolved-oxygen probe",
        "Mg/Zn/Fe/Cu coupons · copper-ion solution · artificial seawater · deoxygenated and chloride-free controls",
        "Metal salts may be hazardous; deburr coupons, avoid skin contact and collect all metal-containing solutions and corrosion products.",
        "corrosion mass-loss repeatability", 95.0, "%",
        "Balance oxidation/reduction half-reactions, predict polarity from potentials and calculate corrosion rate from cleaned coupon mass loss, area and exposure time.",
        listOf(
            "Polish, degrease, dry and weigh coded metal coupons.",
            "Run displacement pairs and measure open-circuit polarity before visual interpretation.",
            "Expose isolated/coupled coupons to seawater, chloride-free and oxygen controls.",
            "Clean by the defined method, reweigh and relate rate to electrochemical conditions."
        )
    )
    "Calcium-carbonate decomposition" -> generalChemistry(
        LabTechnique.GRAVIMETRY,
        "quantify thermal decomposition of calcium carbonate by mass and gas balance",
        "CaCO₃(s) → CaO(s) + CO₂(g). Complete conversion predicts a residue fraction M(CaO)/M(CaCO₃), while incomplete heating and CaO hydration/carbonation bias results.",
        "Preweighed crucible · muffle-furnace simulator · desiccator · analytical balance · CO₂ detector",
        "Dry calcium carbonate reference · unknown carbonate sample · CO₂ calibration gas",
        "High-temperature work requires rated tongs and shielding; cool in a desiccator because CaO is caustic and readily absorbs water/CO₂.",
        "CaO residue mass fraction", 56.03, "%",
        "Use constant-mass residue and evolved-CO₂ evidence, calculate conversion and check m(initial) ≈ m(residue) + m(CO₂) within uncertainty.",
        listOf(
            "Condition and weigh the crucible and dry sample.",
            "Heat under the defined temperature program while monitoring evolved CO₂.",
            "Cool in a sealed desiccator and repeat heat–cool–weigh to constant mass.",
            "Calculate conversion, residue identity evidence and complete mass balance."
        )
    )
    "Environmental synthesis reactions" -> generalChemistry(
        LabTechnique.SYNTHESIS,
        "compare conventional and greener synthesis routes using yield, hazard and waste metrics",
        "Reaction success includes selectivity and resource efficiency: atom economy is theoretical, while reaction mass efficiency, E-factor and process mass intensity incorporate actual quantities and waste.",
        "Microscale reactors · controlled heater · balance · separation setup · energy logger",
        "Matched conventional/alternative reaction sets · catalysts · solvents · quench and purification materials",
        "Complete route-specific risk assessments; a bio-based or catalytic reagent is not automatically benign, and all products/wastes retain their SDS-based controls.",
        "process mass intensity improvement", 25.0, "%",
        "Calculate yield, selectivity, atom economy, E-factor, PMI and energy per product mass; state tradeoffs rather than declaring a route green from one metric.",
        listOf(
            "Scale both routes to the same theoretical product amount and inventory all inputs.",
            "Run matched reactions with logged time, temperature and energy.",
            "Isolate products using documented solvent/wash quantities and determine purity.",
            "Compare complete mass/energy/hazard metrics and propose a justified improvement."
        )
    )
    "Battery redox chemistry" -> generalChemistry(
        LabTechnique.ELECTROCHEMISTRY,
        "relate half-reactions, ion transport and state of charge to battery voltage",
        "A galvanic cell separates spontaneous oxidation and reduction; Ecell = Ecathode − Eanode and ΔG = −nFE. Terminal voltage under load also includes kinetic and ohmic losses.",
        "Reconfigurable half-cells · salt bridge · high-impedance voltmeter · controlled load · data logger",
        "Zn/Zn²⁺ and Cu/Cu²⁺ systems · concentration standards · separator/electrolyte variants",
        "Prevent short circuits and electrolyte contact; use current limiting, de-energize before reconfiguration and collect metal-ion solutions separately.",
        "standard cell emf", 1.10, "V",
        "Predict polarity from reduction potentials, apply the Nernst equation to nonstandard concentrations and separate open-circuit EMF from loaded terminal voltage.",
        listOf(
            "Prepare verified half-cells and measure each reference electrode response.",
            "Assemble the cell with correct ion-conducting bridge and record open-circuit voltage.",
            "Change concentration systematically and compare measured voltage with Nernst predictions.",
            "Apply controlled loads and plot voltage/current to identify polarization and internal resistance."
        )
    )
    "Electrolysis and electrode products" -> generalChemistry(
        LabTechnique.ELECTROCHEMISTRY,
        "predict and verify electrode products in aqueous electrolysis",
        "An external potential drives nonspontaneous redox. Actual aqueous products depend on electrode material, concentrations and overpotential; anode is positive and cathode negative in an electrolytic cell.",
        "Current-limited DC supply · inert electrodes · divided cell · gas collection · pH indicator · coulometer",
        "Aqueous sodium sulfate/copper sulfate/chloride scenarios · gas-test references · blanks",
        "Use low-voltage current-limited equipment, ventilate possible H₂/Cl₂/O₂, keep ignition sources away and never mix collected gases.",
        "Faradaic product recovery", 95.0, "%",
        "Write competing half-reactions, use Q = It and n = Q/zF, compare predicted/observed products and report current efficiency plus overpotential assumptions.",
        listOf(
            "List thermodynamically possible half-reactions for each electrode/electrolyte.",
            "Assemble the de-energized cell, then apply controlled current while logging charge.",
            "Collect/identify gases or metal deposits and map local pH changes.",
            "Calculate theoretical product, current efficiency and causes of competing reactions."
        )
    )
    "Electroplating" -> generalChemistry(
        LabTechnique.ELECTROCHEMISTRY,
        "control electroplated mass, thickness, adhesion and current efficiency",
        "At the cathode Mn+ + ne− → M; Faraday’s law predicts mass m = ItM/nF. Current density, mass transport, surface preparation and competing hydrogen evolution affect coating quality.",
        "Current-controlled plating cell · soluble/inert anode · coupon jig · analytical balance · micrometer",
        "Copper sulfate plating bath · copper anode · steel/brass coupons · cleaning solutions",
        "Metal electrolytes are hazardous; de-energize before touching the bath, use splash protection and collect rinses and spent bath as metal waste.",
        "plating current efficiency", 95.0, "%",
        "Compare measured mass gain with Faraday prediction, calculate mean thickness from density/area and evaluate uniformity and adhesion separately.",
        listOf(
            "Polish, degrease, rinse, dry and weigh a defined-area cathode coupon.",
            "Set electrode spacing and current density before energizing the cell.",
            "Plate for a measured time while logging current, voltage and temperature.",
            "Rinse/dry/reweigh, calculate efficiency and thickness, and inspect coating defects."
        )
    )
    "Nuclear chemistry investigation" -> generalChemistry(
        LabTechnique.PHYSICAL_MEASUREMENT,
        "analyze nuclear stability, radiation interaction and counting statistics",
        "Nuclear transformations conserve charge, nucleon number and total energy; detected counts follow Poisson statistics and require background, efficiency, geometry and dead-time consideration.",
        "Shielded radiation-counter simulator · source-holder geometry · absorber set · timer · spectrum viewer",
        "Sealed-source datasets · background dataset · alpha/beta/gamma references · absorber cards",
        "Use simulation or institutionally controlled sealed sources only; apply time, distance and shielding principles and never handle or relocate a source outside authorization.",
        "background-corrected count precision", 95.0, "%",
        "Subtract scaled background, use √N counting uncertainty, preserve fixed geometry and identify radiation type from penetration/deflection evidence rather than count magnitude alone.",
        listOf(
            "Measure background for a duration sufficient to characterize its uncertainty.",
            "Acquire reference counts at fixed distance and time with instrument dead-time known.",
            "Insert paper, aluminum and dense shielding in a controlled sequence.",
            "Correct counts and classify radiation while documenting geometry and statistical confidence."
        )
    )
    "Radioactive-decay identification" -> generalChemistry(
        LabTechnique.KINETICS,
        "identify a radionuclide from background-corrected decay kinetics",
        "Radioactive decay follows N = N0e^(−λt), activity A = λN and t1/2 = ln2/λ. Random decay produces counting scatter even under unchanged conditions.",
        "Time-stamped counter dataset · background monitor · semilog plotting tool · isotope reference table",
        "Unknown decay dataset · long-lived control · background series · detector-efficiency record",
        "Use data or authorized sealed-source measurements only; never infer safety from half-life alone because activity, radiation type, intake and shielding matter.",
        "recovered half-life accuracy", 98.0, "%",
        "Subtract time-matched background, fit ln(activity) only where single-exponential assumptions hold, inspect residuals and compare half-life confidence interval with candidates.",
        listOf(
            "Acquire or import equal-duration count intervals with stable geometry.",
            "Measure background and convert both series to count rate with Poisson uncertainty.",
            "Fit exponential decay and semilog representations without logging nonpositive corrected values.",
            "Estimate half-life and identify the nuclide only if radiation and uncertainty evidence agree."
        )
    )
    "Nuclear-fission energy" -> generalChemistry(
        LabTechnique.PHYSICAL_MEASUREMENT,
        "calculate fission energy and model neutron multiplication under controlled assumptions",
        "Heavy-nucleus fission converts mass defect to energy E = Δmc² and releases neutrons. The multiplication factor k_eff distinguishes subcritical, critical and supercritical systems.",
        "Binding-energy database · fission-product mass calculator · neutron-generation simulator · energy converter",
        "U-235 fission channel data · delayed-neutron dataset · control-absorber scenarios",
        "This is a non-operational educational model; reactor control and criticality safety require licensed facilities, validated codes and qualified personnel.",
        "fission energy per event", 200.0, "MeV",
        "Use atomic/nuclear masses consistently, include emitted neutrons, convert u to MeV correctly and distinguish energy per event from reactor power over time.",
        listOf(
            "Balance a representative fission channel for nucleon number and charge.",
            "Calculate mass defect and energy per fission with stated mass conventions.",
            "Simulate generations for k_eff below, equal to and above one.",
            "Evaluate how absorbers and delayed neutrons change kinetics without extrapolating to reactor operation."
        )
    )
    "Nuclear-fusion energy" -> generalChemistry(
        LabTechnique.PHYSICAL_MEASUREMENT,
        "calculate fusion Q-value and compare plasma-energy constraints",
        "Light nuclei release energy when products have greater binding energy per nucleon; for D + T → He-4 + n, mass defect yields about 17.6 MeV. Positive Q does not by itself guarantee net power.",
        "Isotope-mass database · binding-energy plot · plasma-temperature/confinement simulator · energy-balance tool",
        "Deuterium–tritium reaction dataset · bremsstrahlung and confinement-loss scenarios",
        "This is a computational investigation; tritium, neutron sources and plasma devices require specialized licensed facilities and are not classroom materials.",
        "D–T fusion Q-value", 17.6, "MeV",
        "Calculate Q from consistent masses, partition energy by momentum conservation and compare fusion heating with modeled losses and confinement criteria.",
        listOf(
            "Balance candidate fusion reactions for charge and nucleon number.",
            "Calculate D–T mass defect and convert it to energy per reaction and per mole.",
            "Model temperature-dependent reaction probability and confinement losses.",
            "Distinguish positive reaction Q, plasma gain and whole-system net energy."
        )
    )
    "Photoelectric effect and solar panels" -> generalChemistry(
        LabTechnique.SPECTROSCOPY,
        "measure photoelectric threshold behavior and photovoltaic current–voltage performance",
        "Photon energy is E = hν = hc/λ. In the photoelectric effect Kmax = hν − φ above threshold; photovoltaic cells generate electron–hole pairs and have power P = IV with efficiency set at the maximum-power point.",
        "Monochromatic light source · phototube simulator · calibrated irradiance sensor · solar cell · variable load",
        "Wavelength filters · dark control · reference photodiode · solar-cell samples",
        "Use enclosed low-intensity light sources, avoid direct viewing of UV/laser radiation and prevent electrical short circuits or overheating of cells.",
        "Planck-constant recovery", 95.0, "%",
        "Fit stopping potential versus frequency to recover h/e and threshold; separately obtain solar-cell Voc, Isc, fill factor and efficiency from an irradiance-corrected I–V curve.",
        listOf(
            "Measure dark response and verify wavelength and irradiance references.",
            "Record stopping potential across frequencies above and below threshold.",
            "Acquire solar-cell I–V curves at controlled irradiance and temperature.",
            "Fit the photoelectric relation and calculate photovoltaic maximum power and efficiency."
        )
    )
    else -> error("Missing dedicated general chemistry protocol: $title")
}
