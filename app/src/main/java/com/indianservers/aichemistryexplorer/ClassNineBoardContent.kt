package com.indianservers.aichemistryexplorer

private fun classNine(
    module: String,
    title: String,
    summary: String,
    explanation: String,
    relationship: String,
    example: String,
    mistake: String,
    application: String,
    vararg connections: String
): Pair<String, LevelOneConceptContent> = "$module|$title" to LevelOneConceptContent(
    summary = summary,
    explanation = explanation,
    workedExample = "Example: $example",
    keyRelationship = relationship,
    commonMistake = mistake,
    application = application,
    connections = connections.toList()
)

internal val classNineBoardContent: Map<String, LevelOneConceptContent> = mapOf(
    classNine(
        "Class 9 Matter and Mixtures", "Crystallisation of dissolved solids",
        "Crystallisation purifies a dissolved solid by forming ordered crystals from a hot concentrated solution as it cools.",
        "Choose a solvent in which the solute is much more soluble when hot than cold. Dissolve the impure solid in minimum hot solvent, hot-filter insoluble impurities, concentrate if needed, cool without disturbance, filter the crystals, wash with a little cold solvent and dry. Soluble impurities largely remain in the mother liquor.",
        "hot saturated solution -> cooling -> crystals + mother liquor",
        "Copper sulfate containing insoluble dirt is dissolved in hot water and filtered. Blue crystals form on slow cooling while much of the soluble impurity stays in solution.",
        "Do not evaporate all the solvent to dryness: rapid complete evaporation can trap impurities and gives poorer crystals.",
        "Crystallisation purifies salt, sugar, pharmaceuticals and laboratory reagents while offering better purity than evaporation alone.",
        "Solubility", "Saturation", "Filtration"
    ),
    classNine(
        "Class 9 Matter and Mixtures", "Simple distillation of liquid mixtures",
        "Simple distillation separates a volatile liquid from a nonvolatile solute or from another liquid with a sufficiently different boiling point.",
        "Heat the mixture safely, allow the more volatile component to vaporise, cool the vapour in a condenser and collect the distillate. A thermometer measures vapour temperature near the condenser entrance. Anti-bumping measures and an open receiving path prevent sudden boiling and pressure buildup.",
        "liquid mixture --heating--> vapour --condensation--> distillate",
        "Distilling salt water produces liquid water as distillate while dissolved salt remains in the flask; filtration could not remove that dissolved salt.",
        "Do not seal a distillation apparatus or heat a flask to complete dryness, and do not confuse distillation with simple evaporation because the solvent is recovered.",
        "Distillation supports water purification, solvent recovery and preparation of distilled laboratory water.",
        "Boiling point", "Condensation", "Separation methods"
    ),
    classNine(
        "Class 9 Matter and Mixtures", "Paper chromatography",
        "Paper chromatography separates dissolved substances because they travel differently with a solvent over paper.",
        "Place a small sample spot on a pencil baseline above the solvent. The solvent rises by capillary action; components repeatedly partition between the mobile solvent and stationary water/paper environment. Mark the solvent front immediately and compare spots using Rf under identical conditions.",
        "Rf = distance travelled by solute / distance travelled by solvent front; 0 <= Rf <= 1",
        "If an ink dye travels 3.0 cm while the solvent front travels 5.0 cm, Rf = 3.0/5.0 = 0.60.",
        "Do not draw the baseline in ink or immerse the sample spot in solvent; either action contaminates or washes away the starting sample.",
        "Chromatography separates inks, food colours, plant pigments and components used in forensic or quality-control comparisons.",
        "Capillary action", "Solubility", "Rf values"
    ),
    classNine(
        "Class 9 Matter and Mixtures", "Sublimation as a separation method",
        "Sublimation separates a solid that changes directly to vapour from non-subliming solids.",
        "Heat the mixture gently under an inverted funnel or cold surface. The sublimable component forms vapour and deposits as solid on the cooler surface, while the nonvolatile component remains. Suitable examples include ammonium chloride, camphor or naphthalene mixed with sand or salt.",
        "solid --heat--> vapour --cooling--> solid deposit",
        "On gently heating ammonium chloride mixed with sodium chloride, ammonium chloride deposits on the cool funnel while sodium chloride remains in the dish.",
        "Do not call ordinary evaporation sublimation; sublimation begins with a solid and bypasses the liquid state under the stated conditions.",
        "Sublimation is used to purify volatile solids and illustrates particle motion during direct solid-gas changes.",
        "Changes of state", "Deposition", "Volatility"
    ),
    classNine(
        "Class 9 Matter and Mixtures", "Centrifugation of mixtures",
        "Centrifugation speeds separation by spinning a mixture so denser suspended particles move outward and settle faster.",
        "Opposing tubes must have equal masses and be placed symmetrically. After spinning at the specified speed and time, the dense sediment is the pellet and the liquid above it is the supernatant. Centrifugation helps when particles are too small or slow to settle by gravity alone.",
        "greater density difference and rotation -> faster sedimentation",
        "A milk separator uses spinning to divide components of different density; a laboratory centrifuge can separate fine chalk from water into pellet and supernatant.",
        "Never run a centrifuge with unbalanced tubes, an unsecured lid or cracked containers; imbalance can damage the rotor and injure users.",
        "Centrifugation is used in dairies, clinical blood processing, wastewater treatment and laboratory sample preparation.",
        "Density", "Sedimentation", "Laboratory safety"
    ),
    classNine(
        "Class 9 Matter and Mixtures", "Coagulation of colloids",
        "Coagulation makes tiny colloidal particles combine into larger aggregates that can settle or be filtered.",
        "Colloid particles often carry like charges that keep them dispersed. Adding a suitable electrolyte, heating, mixing oppositely charged sols or controlled stirring can reduce this stabilisation. In water treatment, coagulant produces flocs that capture suspended material before settling and filtration.",
        "destabilised colloid particles -> flocs -> settling or filtration",
        "Alum added during water treatment forms flocs that trap fine suspended dirt; the larger flocs settle more readily than individual colloidal particles.",
        "Do not describe coagulation as dissolving dirt or as filtration itself; it changes particle aggregation so later settling and filtration work better.",
        "Coagulation clarifies drinking water, treats wastewater and supports food and materials processing.",
        "Colloids", "Water treatment", "Particle charge"
    ),
    classNine(
        "Class 9 Matter and Mixtures", "Concentration calculations",
        "Concentration states how much solute is present in a specified amount of solution.",
        "Class 9 calculations use mass-by-mass percentage, mass-by-volume percentage and volume-by-volume percentage. Identify whether the denominator is mass or volume of the complete solution, use compatible units and include the percent sign. The mass of solution equals solute plus solvent mass.",
        "mass % = mass solute/mass solution x100; mass-volume % = mass solute/volume solution x100; volume % = volume solute/volume solution x100",
        "A solution made from 10 g salt and 90 g water has total mass 100 g, so mass percentage = 10/100 x100 = 10%.",
        "Do not divide by solvent amount when the definition asks for solution amount, and do not mix grams with kilograms or millilitres with litres without conversion.",
        "Concentration calculations guide medicines, beverages, cleaning solutions, agriculture and laboratory preparation.",
        "Solutions", "Percent composition", "Unit conversion"
    ),
    classNine(
        "Class 9 Matter and Mixtures", "Solubility curves and graphs",
        "A solubility curve shows the maximum mass of solute that dissolves in a fixed mass of solvent at different temperatures.",
        "Read temperature on the horizontal axis and solubility—often grams solute per 100 g water—on the vertical axis. A point below the curve is unsaturated, on the curve saturated and above the curve represents excess solute or a supersaturated condition if temporarily maintained. The difference between hot and cold solubility predicts crystallisation yield.",
        "crystals expected = solute dissolved hot - solute remaining dissolved cold",
        "If 80 g solute dissolves in 100 g water at 80 °C but only 30 g at 20 °C, ideal cooling can produce 50 g crystals.",
        "Do not assume every solid becomes more soluble at the same rate with temperature; read the specific curve and its axis units.",
        "Solubility graphs help design crystallisation, compare substances and predict whether a solution is saturated.",
        "Graphs", "Saturation", "Crystallisation yield"
    ),

    classNine(
        "Class 9 Atoms and Formulae", "Formation of ionic and covalent compounds",
        "Ionic compounds form through electron transfer and electrostatic attraction, while covalent compounds form through sharing electron pairs.",
        "Metals commonly lose valence electrons to form cations and non-metals gain them to form anions. Between non-metals, shared pairs allow atoms to approach stable outer-shell configurations. Charge balance determines ionic formulae; shared-pair count determines covalent structure.",
        "ionic: Na -> Na+ + e- and Cl + e- -> Cl-; covalent: H:H",
        "Magnesium loses two electrons and two chlorine atoms each gain one, forming Mg2+ and 2Cl-, hence MgCl2. Two hydrogen atoms share one pair to form H2.",
        "Do not describe an ionic compound as a molecule made of one ion pair; an ionic solid contains a repeating lattice of oppositely charged ions.",
        "Bond type helps predict structure, conductivity, melting point, solubility and material use.",
        "Valency", "Electron transfer", "Electron sharing"
    ),
    classNine(
        "Class 9 Atoms and Formulae", "Properties of ionic versus covalent compounds",
        "Ionic and covalent compounds show different bulk properties because their particles and bonding structures differ.",
        "Ionic solids usually have high melting points, are brittle and conduct only when molten or dissolved because ions must move. Simple covalent substances often have lower melting/boiling points and usually do not conduct because they lack mobile charged particles. Giant covalent structures such as diamond are important exceptions.",
        "mobile ions -> electrical conduction; fixed lattice ions -> no solid-state conduction",
        "Solid NaCl does not light a conductivity tester, but molten or aqueous NaCl can. Sugar solution generally does not conduct strongly because sugar remains as neutral molecules.",
        "Do not claim every covalent substance has a low melting point or is insoluble in water; structure, polarity and intermolecular forces create exceptions.",
        "Comparing compound properties guides material selection, electrolyte use and safe interpretation of laboratory tests.",
        "Particle mobility", "Melting point", "Electrical conductivity"
    ),
    classNine(
        "Class 9 Atoms and Formulae", "Electron-dot structures",
        "Electron-dot structures show valence electrons and the pairs transferred or shared during compound formation.",
        "Write the correct number of valence dots around each element symbol. For covalent molecules, place shared pairs between bonded atoms and lone pairs on the appropriate atoms. For ionic compounds, show bracketed ions with charges after electron transfer.",
        "one shared pair = one covalent bond; bracket plus charge = ion",
        "In H2O, oxygen shares one pair with each hydrogen and retains two lone pairs. For NaCl, show [Na]+ and [Cl]- with chlorine's complete octet.",
        "Do not draw core electrons or omit ionic charges; electron-dot structures focus on valence electrons and must conserve the total electron count.",
        "Dot structures provide a bridge from valency and formulas to bonding, shape and chemical reactivity.",
        "Valence electrons", "Lewis symbols", "Octet rule"
    ),

    classNine(
        "School Practical Chemistry", "Designing a fair chemistry investigation",
        "A fair investigation changes one independent variable, measures a dependent variable and controls other factors that could affect the result.",
        "Begin with a focused question, use scientific knowledge to state a testable hypothesis, identify variables, define an operational measurement, plan repeats and a comparison/control, assess hazards, record results consistently and decide whether evidence supports the hypothesis. Sample size and repeatability make the conclusion more reliable.",
        "question -> hypothesis -> variables -> method -> evidence -> conclusion",
        "To test how temperature affects dissolving time, vary water temperature, measure time for an equal sugar mass to dissolve, and control water volume, particle size, stirring method and container.",
        "Do not change several variables at once or write a conclusion before inspecting the measurements and their variation.",
        "Experimental design supports trustworthy school practicals, product testing, environmental investigations and scientific decision-making.",
        "Variables", "Hypotheses", "Repeats and controls"
    ),
    classNine(
        "School Practical Chemistry", "Representing chemistry data in tables and graphs",
        "Well-designed tables and graphs reveal patterns while preserving units, uncertainty and the distinction between measured and calculated values.",
        "Place the independent variable in the first table column and on the horizontal graph axis; place the dependent variable on the vertical axis. Use headings with units, sensible scales, plotted points and a best-fit line or curve rather than joining automatically. Report anomalous points honestly and calculate averages only when appropriate.",
        "graph: x = independent variable; y = dependent variable; gradient = change in y/change in x",
        "For solubility versus temperature, tabulate temperature in °C and solubility in g per 100 g water, plot points and draw a smooth curve to estimate intermediate values.",
        "Do not omit units, force a graph through the origin without scientific reason or delete an anomalous result merely to improve the trend.",
        "Accurate data representation supports solubility analysis, reaction comparisons, calibration and clear scientific communication.",
        "Tables", "Graphs", "Uncertainty and anomalies"
    ),
    classNine(
        "School Practical Chemistry", "Laboratory safety and chemical handling",
        "Laboratory safety combines hazard recognition, risk reduction, correct technique, emergency readiness and responsible waste handling.",
        "Read labels and instructions before starting; wear eye protection and suitable clothing; tie back hair; use small quantities; label containers; never taste chemicals or pipette by mouth; waft only when instructed; add acid to water; keep flames away from flammables; report spills, breakage and exposure immediately. Dispose of each material through the instructed waste route.",
        "risk = likelihood x severity; control hierarchy: remove/substitute -> engineering control -> procedure -> PPE",
        "When diluting concentrated acid under teacher supervision, add acid slowly to water with stirring and cooling because dilution releases heat; adding water to concentrated acid can cause splashing.",
        "Do not treat goggles as permission to ignore safer quantities, ventilation, labels or teacher instructions; PPE is only one layer of control.",
        "Safe handling prevents injuries, contamination and environmental harm while making experimental evidence more reliable.",
        "Hazard labels", "Risk assessment", "Waste and emergency response"
    )
)
