package com.indianservers.aichemistryexplorer

private fun levelThree(
    module: String,
    concept: String,
    summary: String,
    explanation: String,
    example: String,
    relationship: String,
    mistake: String,
    application: String,
    vararg connections: String
): Pair<String, LevelOneConceptContent> {
    val completeExample = if (example.length >= 80) example else
        "$example State the sign convention, substitute consistent units, and interpret the numerical result chemically."
    val completeMistake = if (mistake.length >= 60) mistake else
        "$mistake Check the definitions, signs, units and stated conditions."
    return "$module|$concept" to LevelOneConceptContent(
        summary = summary,
        explanation = explanation,
        workedExample = completeExample,
        keyRelationship = relationship,
        commonMistake = completeMistake,
        application = application,
        connections = connections.toList()
    )
}

internal val levelThreeModuleIcons = mapOf(
    "Thermodynamics" to "dE",
    "Thermochemistry" to "dH",
    "Solutions: Senior Secondary" to "SOL",
    "Classification and Periodicity: Senior Secondary" to "PER",
    "Chemical Bonding and Molecular Structure" to "BOND",
    "Hydrocarbons: Senior Secondary" to "HC",
    "Organic Purification and Analysis" to "QA",
    "Gaseous State: Formative" to "GAS",
    "s-Block Elements: Formative" to "s",
    "p-Block Elements: Formative" to "p",
    "Chemical Equilibrium" to "Keq",
    "Chemical Kinetics" to "RATE",
    "Electrochemistry" to "CELL",
    "Surface Chemistry" to "SURF",
    "Solid State" to "XTAL",
    "Nuclear Chemistry" to "NUC"
)

internal val levelThreeConceptContent: Map<String, LevelOneConceptContent> = mapOf(
    levelThree(
        "Thermodynamics", "System",
        "A thermodynamic system is the part of the universe selected for study.",
        "An open system exchanges matter and energy, a closed system exchanges energy but not matter, and an isolated system exchanges neither. The chosen boundary determines which transfers are counted.",
        "Example: hot water in an open beaker is an open system; a sealed bottle is approximately closed, while an ideal insulated sealed flask approaches an isolated system.",
        "universe = system + surroundings",
        "Do not classify a system from its container alone; identify whether both matter and energy can cross the boundary.",
        "Defining the system correctly is essential in engines, reactors, calorimeters and biological energy balances.",
        "Boundaries", "State variables", "Surroundings"
    ),
    levelThree(
        "Thermodynamics", "Surroundings",
        "The surroundings are everything outside the system that can interact with it.",
        "Energy or matter crossing the boundary appears with opposite accounting in system and surroundings. Together they form the thermodynamic universe.",
        "Example: for a reaction mixture in a flask, the flask, water bath and room are surroundings; heat lost by the system is gained by them.",
        "qsystem = -qsurroundings for an insulated combined universe",
        "Do not call only the immediate container the surroundings; the definition includes everything outside the selected boundary.",
        "Surroundings accounting supports calorimetry, thermal management and environmental energy analysis.",
        "System boundary", "Heat transfer", "Conservation of energy"
    ),
    levelThree(
        "Thermodynamics", "Work",
        "Thermodynamic work is organized energy transfer caused by a force acting through a distance.",
        "For pressure-volume work with the chemistry sign convention, expansion against external pressure gives negative work on the system; compression gives positive work.",
        "Example: a gas expanding by 2.0 L against 100 kPa does w = -Pext deltaV = -200 J because 1 kPa L equals 1 J.",
        "w = -Pexternal deltaV",
        "Do not confuse work done by the system with work done on the system; state the sign convention before calculating.",
        "Pressure-volume work describes pistons, engines, compressors and expanding reaction gases.",
        "First law", "Expansion", "State and path functions"
    ),
    levelThree(
        "Thermodynamics", "Heat",
        "Heat is energy transferred solely because of a temperature difference.",
        "Heat flows spontaneously from hotter to colder regions. In chemistry, q is positive when the system absorbs heat and negative when it releases heat.",
        "Example: if a reaction transfers 5.0 kJ to a water bath, qsystem = -5.0 kJ and qsurroundings = +5.0 kJ.",
        "qsystem = -qsurroundings in an insulated calorimeter",
        "Heat is energy in transit, not a substance stored inside a body; internal energy is the state property.",
        "Heat-transfer calculations guide reactors, refrigeration, climate control and material processing.",
        "Temperature", "Calorimetry", "Internal energy"
    ),
    levelThree(
        "Thermodynamics", "Internal energy",
        "Internal energy is the total microscopic kinetic and potential energy of a system.",
        "Its absolute value is usually unknown, but changes can be measured. The first law says energy changes through heat and work and is conserved overall.",
        "Example: if a system absorbs 300 J of heat and 80 J of work is done on it, deltaU = 300 + 80 = 380 J.",
        "deltaU = q + w",
        "Do not omit the sign of work or assume internal energy depends only on temperature for every system.",
        "Internal-energy balances are used in reactors, engines, batteries and phase-change equipment.",
        "First law", "Heat", "Work"
    ),
    levelThree(
        "Thermodynamics", "Enthalpy",
        "Enthalpy is the state function H = U + PV, useful for constant-pressure processes.",
        "At constant pressure with only pressure-volume work, the enthalpy change equals heat absorbed by the system. Negative deltaH is exothermic and positive deltaH is endothermic.",
        "Example: combustion releasing 890 kJ per mole has deltaH = -890 kJ mol-1; the negative sign records heat leaving the system.",
        "H = U + PV; at constant pressure deltaH = qp",
        "Do not drop the sign of deltaH or describe exothermic products as having more enthalpy than reactants.",
        "Enthalpy data support fuel comparison, process heating, food energy and reaction design.",
        "State functions", "Thermochemistry", "Hess Law"
    ),
    levelThree(
        "Thermodynamics", "Entropy",
        "Entropy measures energy dispersal and the number of microscopic arrangements consistent with a state.",
        "Entropy usually increases when matter spreads, a solid melts, a liquid vaporizes or the number of gas particles rises. For a reversible transfer, deltaS = qrev/T.",
        "Example: melting one mole of ice at 273 K with deltaHfus = 6.01 kJ mol-1 gives deltaS = 6010/273 about 22.0 J mol-1 K-1.",
        "deltaS = qreversible / T",
        "Entropy is not simply 'disorder'; use energy dispersal, accessible microstates and the stated system boundary.",
        "Entropy predicts phase stability, mixing, reaction direction and maximum energy efficiency.",
        "Second law", "Microstates", "Gibbs free energy"
    ),
    levelThree(
        "Thermodynamics", "Gibbs free energy",
        "Gibbs free energy combines enthalpy and entropy to predict change at constant temperature and pressure.",
        "A process is thermodynamically spontaneous when deltaG is negative, at equilibrium when it is zero, and nonspontaneous in the written direction when positive.",
        "Example: if deltaH = -40 kJ mol-1 and deltaS = -0.050 kJ mol-1 K-1 at 298 K, deltaG = -40 - 298(-0.050) = -25.1 kJ mol-1.",
        "deltaG = deltaH - T deltaS",
        "A negative deltaG does not say the process is fast; kinetics determines the rate.",
        "Free energy guides equilibrium, electrochemical cells, phase stability and process feasibility.",
        "Spontaneity", "Equilibrium constant", "Electrochemical potential"
    ),
    levelThree(
        "Thermodynamics", "Hess Law",
        "Hess Law states that reaction enthalpy depends only on initial and final states, not on the route.",
        "Because enthalpy is a state function, equations may be reversed, multiplied and added; their deltaH values must undergo the same operations.",
        "Example: adding C + O2 -> CO2 and the reverse of CO + 1/2O2 -> CO2 yields C + 1/2O2 -> CO and its enthalpy.",
        "deltaHoverall = sum of deltaHsteps",
        "When reversing or scaling a chemical equation, also reverse the sign or scale the value of deltaH.",
        "Hess cycles calculate inaccessible reaction enthalpies, formation energies and lattice energies.",
        "State functions", "Thermochemical equations", "Born-Haber cycle"
    ),
    levelThree(
        "Thermodynamics", "Spontaneity",
        "A spontaneous process can proceed without continuous external driving under the stated conditions.",
        "At constant temperature and pressure, deltaG < 0 predicts spontaneity. Spontaneous does not mean instantaneous, irreversible or able to occur without an activation pathway.",
        "Example: diamond converting to graphite is thermodynamically favorable under ordinary conditions but extremely slow because of a large activation barrier.",
        "constant T,P: spontaneous if deltaG < 0",
        "Do not equate spontaneity with speed; thermodynamics predicts direction while kinetics predicts rate.",
        "Spontaneity criteria help assess reactions, mixing, corrosion and energy-storage processes.",
        "Gibbs free energy", "Entropy of universe", "Activation energy"
    ),

    levelThree(
        "Thermochemistry", "Heat capacity",
        "Heat capacity is the heat required to raise the temperature of an entire object by one kelvin.",
        "It is an extensive property and depends on amount and material. At constant pressure and volume, Cp and Cv describe different constraints.",
        "Example: an object absorbing 600 J while warming by 15 K has heat capacity C = 600/15 = 40 J K-1.",
        "C = q / deltaT",
        "Do not confuse an object's heat capacity with a substance's specific heat capacity.",
        "Heat capacity determines thermal buffering in buildings, reactors, electronics and storage systems.",
        "Specific heat", "Calorimetry", "Thermal mass"
    ),
    levelThree(
        "Thermochemistry", "Specific heat",
        "Specific heat capacity is the heat required to raise one unit mass of a substance by one kelvin.",
        "It is an intensive material property over a stated temperature range. Water's high value makes it an effective thermal buffer.",
        "Example: heating 100 g water by 5.0 K requires q = 100 x 4.18 x 5.0 = 2.09 kJ.",
        "q = mc deltaT",
        "Use the mass of the material being heated and keep heat-capacity units consistent with mass units.",
        "Specific heat guides cooling systems, cooking, climate models and material selection.",
        "Heat capacity", "Temperature change", "Calorimetry"
    ),
    levelThree(
        "Thermochemistry", "Calorimetry",
        "Calorimetry determines heat transfer from measured temperature changes.",
        "An insulated calorimeter uses conservation of energy: heat lost by the reaction equals heat gained by the calorimeter and contents, after accounting for heat capacity.",
        "Example: if 200 g solution with c = 4.18 J g-1 K-1 warms 3.0 K, qsolution = 2.51 kJ and qreaction is approximately -2.51 kJ.",
        "qreaction + qsurroundings = 0",
        "Do not assign the solution's positive heat directly to an exothermic reaction; the reaction has the opposite sign.",
        "Calorimetry measures food energy, fuel values, reaction enthalpies and material heat capacities.",
        "Energy conservation", "Specific heat", "Enthalpy"
    ),
    levelThree(
        "Thermochemistry", "Bond enthalpy",
        "Bond enthalpy is the energy required to break one mole of a specified gas-phase bond.",
        "Average bond enthalpies estimate reaction enthalpy by comparing bonds broken with bonds formed. Breaking requires energy; forming releases energy.",
        "Example: for H2 + Cl2 -> 2HCl, estimate deltaH = D(H-H) + D(Cl-Cl) - 2D(H-Cl).",
        "deltaH approximately sum bonds broken - sum bonds formed",
        "Do not add bond-forming energies as positive; formation lowers energy and is subtracted in the estimate.",
        "Bond enthalpies estimate fuel energies, reaction feasibility and bond strengths.",
        "Reaction enthalpy", "Covalent bonds", "Hess Law"
    ),
    levelThree(
        "Thermochemistry", "Lattice energy",
        "Lattice energy measures the energetic strength of an ionic crystal.",
        "Its magnitude increases with larger ionic charges and smaller ion separation. Sign conventions differ, so specify whether formation or separation is meant.",
        "Example: MgO has a much larger lattice-energy magnitude than NaCl because Mg2+ and O2- carry greater charges.",
        "magnitude roughly proportional to |z+z-| / ionic distance",
        "Never quote a lattice-energy sign without stating whether the lattice is being formed or separated.",
        "Lattice energy helps explain melting point, solubility, stability and hardness of ionic solids.",
        "Coulomb attraction", "Ionic radius", "Born-Haber cycle"
    ),
    levelThree(
        "Thermochemistry", "Born-Haber cycle",
        "A Born-Haber cycle applies Hess Law to ionic-solid formation.",
        "It combines atomization, ionization, electron affinity, bond dissociation and lattice steps so an unmeasurable lattice enthalpy can be inferred.",
        "Example: a NaCl cycle links sodium sublimation, chlorine bond dissociation, sodium ionization, chlorine electron gain and NaCl lattice formation.",
        "deltaHformation = sum of atom and ion steps + lattice enthalpy",
        "Track stoichiometric factors and signs carefully, especially half a diatomic bond and the electron-affinity convention.",
        "Born-Haber cycles compare ionic stability, lattice energy and plausibility of oxidation states.",
        "Hess Law", "Ionization energy", "Electron affinity"
    ),

    levelThree(
        "Chemical Equilibrium", "Dynamic equilibrium",
        "Dynamic equilibrium occurs when forward and reverse reaction rates are equal in a closed system.",
        "Macroscopic concentrations remain constant even though both reactions continue microscopically. Equilibrium can be reached from either direction.",
        "Example: in N2O4(g) <-> 2NO2(g), molecules continue interconverting while the colour and concentrations remain constant at equilibrium.",
        "rateforward = ratereverse",
        "Equilibrium does not mean reactions stop or that reactant and product concentrations are equal.",
        "Dynamic equilibrium controls industrial synthesis, gas binding, solubility and acid-base systems.",
        "Reversible reactions", "Equilibrium constant", "Reaction rates"
    ),
    levelThree(
        "Chemical Equilibrium", "Equilibrium constant",
        "The equilibrium constant relates equilibrium activities or concentrations of products and reactants.",
        "Each term is raised to its stoichiometric coefficient; pure solids and liquids are omitted. K changes with temperature but not with initial composition.",
        "Example: for N2 + 3H2 <-> 2NH3, Kc = [NH3]^2/([N2][H2]^3).",
        "Kc = product concentrations^coefficients / reactant concentrations^coefficients",
        "Use equilibrium values, omit pure solids and liquids, and never include catalysts in the expression.",
        "Equilibrium constants predict yield, dissociation, solubility and acid-base composition.",
        "Reaction quotient", "Gibbs free energy", "Le Chatelier principle"
    ),
    levelThree(
        "Chemical Equilibrium", "Le Chatelier principle",
        "Le Chatelier's principle predicts how an equilibrium responds to a disturbance.",
        "The system shifts in the direction that partly opposes a concentration, pressure or temperature change. A catalyst speeds approach but does not shift equilibrium.",
        "Example: increasing pressure in N2 + 3H2 <-> 2NH3 shifts equilibrium toward ammonia because that side has fewer gas moles.",
        "disturbance -> shift that reduces its effect",
        "Do not say a catalyst changes K or equilibrium yield; it accelerates both directions.",
        "The principle guides ammonia manufacture, gas reactions, buffers and product recovery.",
        "Reaction quotient", "Equilibrium constant", "Industrial optimization"
    ),
    levelThree(
        "Chemical Equilibrium", "Ionic equilibrium",
        "Ionic equilibrium describes reversible ionization and ion reactions in solution.",
        "Weak acids, weak bases, water and sparingly soluble salts establish equilibria described by Ka, Kb, Kw and Ksp.",
        "Example: for HA <-> H+ + A-, Ka = [H+][A-]/[HA]; a smaller Ka indicates less ionization under comparable conditions.",
        "Kw = [H3O+][OH-]; at 25 C, Kw = 1.0 x 10^-14",
        "Do not treat weak electrolytes as fully dissociated or ignore charge and mass balance.",
        "Ionic equilibria determine pH, precipitation, water chemistry and analytical separations.",
        "Acid dissociation", "Solubility product", "Common-ion effect"
    ),
    levelThree(
        "Chemical Equilibrium", "Buffer systems",
        "A buffer system resists pH change using a weak acid-base conjugate pair.",
        "The acid consumes added base and the conjugate base consumes added acid. Effective buffering requires appreciable amounts of both components and has limited capacity.",
        "Example: equal concentrations of CH3COOH and CH3COO- give pH approximately pKa; adding a little acid converts acetate into ethanoic acid.",
        "pH = pKa + log([conjugate base]/[weak acid])",
        "The Henderson-Hasselbalch equation uses equilibrium concentrations or justified approximations, not arbitrary initial values.",
        "Buffers stabilize blood, pharmaceuticals, fermentation, foods and analytical measurements.",
        "Conjugate pairs", "Buffer capacity", "Acid-base equilibrium"
    ),

    levelThree(
        "Chemical Kinetics", "Rate laws",
        "A rate law relates reaction rate to reactant concentrations through experimentally determined powers.",
        "For rate = k[A]^m[B]^n, m and n are orders and k depends mainly on temperature and catalyst. The balanced equation alone usually cannot provide the exponents.",
        "Example: if rate = k[A]^2, doubling [A] increases rate by 2^2 = four while other conditions remain fixed.",
        "rate = k[A]^m[B]^n",
        "Do not infer rate-law exponents from an overall balanced equation unless the step is explicitly elementary.",
        "Rate laws determine reactor size, shelf life, dosing intervals and safe process conditions.",
        "Reaction order", "Initial rates", "Rate constant"
    ),
    levelThree(
        "Chemical Kinetics", "Order",
        "Reaction order is the exponent of a concentration term in a rate law; overall order is the exponent sum.",
        "Order is measured experimentally and may be zero, fractional or negative. It determines rate sensitivity and integrated-law units.",
        "Example: rate = k[A][B]^2 is first order in A, second order in B and third order overall.",
        "overall order = sum of concentration exponents",
        "Order is not the same as a balanced coefficient or molecularity.",
        "Reaction order helps predict concentration-time behavior and select kinetic models.",
        "Rate laws", "Half-life", "Integrated rate equations"
    ),
    levelThree(
        "Chemical Kinetics", "Molecularity",
        "Molecularity is the number of reacting particles in one elementary step.",
        "An elementary step may be unimolecular, bimolecular or rarely termolecular. Molecularity is always a positive integer and is defined for a step, not a complex overall reaction.",
        "Example: A + B -> products as one elementary collision is bimolecular and has step rate proportional to [A][B].",
        "molecularity = particles participating in an elementary step",
        "Do not assign molecularity to an overall multistep equation or confuse it with experimentally observed order.",
        "Molecularity connects proposed mechanisms with elementary-step rate laws.",
        "Elementary reactions", "Mechanisms", "Reaction order"
    ),
    levelThree(
        "Chemical Kinetics", "Arrhenius equation",
        "The Arrhenius equation describes how the rate constant changes with temperature and activation energy.",
        "A is the pre-exponential factor and exp(-Ea/RT) represents the fraction of suitably energetic events. A plot of ln k against 1/T has slope -Ea/R.",
        "Example: rate constants at two temperatures can give Ea from ln(k2/k1) = -Ea/R(1/T2 - 1/T1).",
        "k = A exp(-Ea/RT)",
        "Use kelvin temperature and consistent energy units for Ea and R.",
        "Arrhenius analysis predicts shelf life, process rates and temperature sensitivity.",
        "Activation energy", "Temperature", "Rate constant"
    ),
    levelThree(
        "Chemical Kinetics", "Activation energy",
        "Activation energy is the minimum energy barrier between reactants and the transition-state region.",
        "A larger barrier gives a smaller rate constant at the same temperature. Catalysts speed reactions by providing a pathway with lower activation energy.",
        "Example: heating increases the fraction of molecules above Ea, so more collisions can cross the barrier and the reaction accelerates.",
        "higher Ea at fixed T -> smaller k",
        "Activation energy is not the reaction enthalpy; it controls rate rather than overall energy difference.",
        "Barrier control is crucial in combustion, enzymes, food preservation and industrial safety.",
        "Transition state", "Arrhenius equation", "Catalysis"
    ),
    levelThree(
        "Chemical Kinetics", "Catalysis",
        "Kinetic catalysis increases reaction rate through an alternative lower-energy mechanism.",
        "A catalyst participates in elementary steps but is regenerated overall. It accelerates forward and reverse processes without changing deltaG, K or equilibrium composition.",
        "Example: MnO2 accelerates H2O2 decomposition by lowering the pathway barrier and remains chemically regenerated overall.",
        "catalyst -> alternative pathway with lower activation energy",
        "A catalyst does not increase equilibrium yield or make a thermodynamically impossible process favorable.",
        "Catalysis enables lower-temperature manufacturing, emission control and enzyme-driven life chemistry.",
        "Mechanisms", "Activation energy", "Reaction rate"
    ),

    levelThree(
        "Electrochemistry", "Redox",
        "A redox reaction transfers electrons through simultaneous oxidation and reduction.",
        "Oxidation is electron loss and reduction is electron gain. Half-reactions balance atoms, charge and transferred electrons.",
        "Example: Zn + Cu2+ -> Zn2+ + Cu; zinc loses two electrons and copper(II) gains the same two electrons.",
        "electrons lost = electrons gained",
        "Do not change subscripts to balance redox equations; use coefficients and balance charge explicitly.",
        "Redox chemistry powers cells, corrosion, electrolysis, metallurgy and biological respiration.",
        "Oxidation states", "Half-reactions", "Electron transfer"
    ),
    levelThree(
        "Electrochemistry", "Galvanic cells",
        "A galvanic cell converts a spontaneous redox reaction into electrical work.",
        "Oxidation occurs at the anode, reduction at the cathode, electrons travel through the wire and ions through the electrolyte or salt bridge maintain charge balance.",
        "Example: in a Daniell cell, Zn oxidizes at the anode and Cu2+ reduces at the cathode; electrons flow from Zn to Cu.",
        "Ecell = Ecathode - Eanode",
        "Oxidation is always at the anode and reduction at the cathode; do not rely only on electrode signs.",
        "Galvanic-cell principles underpin batteries, sensors and corrosion measurements.",
        "Cell notation", "EMF", "Salt bridge"
    ),
    levelThree(
        "Electrochemistry", "Electrolysis",
        "Electrolysis uses electrical energy to drive a nonspontaneous redox reaction.",
        "The power supply pushes electrons, while ions migrate to electrodes. Deposited or released amount is proportional to charge passed.",
        "Example: passing 193000 C through Cu2+ solution supplies 2 mol electrons and ideally deposits 1 mol copper.",
        "Q = It; moles electrons = Q/F",
        "Electrode signs differ from a galvanic cell, but oxidation remains at the anode and reduction at the cathode.",
        "Electrolysis is used for plating, metal refining, chemical manufacture and hydrogen production.",
        "Faraday laws", "Electrodes", "Nonspontaneous reactions"
    ),
    levelThree(
        "Electrochemistry", "Nernst equation",
        "The Nernst equation gives electrode or cell potential under nonstandard conditions.",
        "Potential depends logarithmically on reaction quotient and approaches zero at equilibrium for the full cell. Temperature and electron count must be specified.",
        "Example: at 298 K, E = Estandard - (0.05916/n)log Q; increasing product activity lowers E for the written cell reaction.",
        "E = Estandard - (RT/nF) ln Q",
        "Construct Q from the balanced cell reaction and omit pure solids and liquids.",
        "The Nernst equation supports concentration sensors, batteries, pH electrodes and equilibrium calculations.",
        "Reaction quotient", "Cell potential", "Equilibrium constant"
    ),
    levelThree(
        "Electrochemistry", "EMF",
        "Electromotive force is the maximum potential difference of a cell when essentially no current flows.",
        "Positive Ecell for the written reaction indicates a spontaneous galvanic process under those conditions and connects directly with Gibbs free energy.",
        "Example: if Ecathode = +0.34 V and Eanode reduction potential = -0.76 V, Ecell = 0.34 - (-0.76) = 1.10 V.",
        "deltaG = -nFEcell",
        "EMF is a voltage, not a mechanical force, and standard potentials are not multiplied by stoichiometric coefficients.",
        "EMF determines battery voltage, sensor response and available electrical work.",
        "Electrode potentials", "Gibbs energy", "Galvanic cells"
    ),
    levelThree(
        "Electrochemistry", "Batteries",
        "A battery contains one or more electrochemical cells that convert stored chemical energy into electricity.",
        "Primary batteries are used once; secondary batteries use reversible reactions for recharging. Capacity, voltage, power, lifetime and safety depend on materials and design.",
        "Example: during lithium-ion discharge, Li+ moves through electrolyte while electrons travel through the external circuit to do work.",
        "electrical energy approximately = charge x cell voltage",
        "Recharging does not restore a battery perfectly; side reactions and material changes cause capacity loss.",
        "Batteries power electronics, vehicles, medical devices and renewable-energy storage.",
        "Cell capacity", "Rechargeability", "Electrode materials"
    ),
    levelThree(
        "Electrochemistry", "Fuel cells",
        "A fuel cell generates electricity continuously while fuel and oxidant are supplied.",
        "Unlike a battery, reactants are stored externally. Hydrogen fuel cells combine hydrogen and oxygen electrochemically to produce water, electricity and heat.",
        "Example: the overall hydrogen fuel-cell reaction is 2H2 + O2 -> 2H2O while electrons pass through the load.",
        "chemical fuel + oxidant -> electrical work + products",
        "Fuel cells are not pollution-free by definition; total impact depends on fuel production and system materials.",
        "Fuel cells provide power for vehicles, backup systems and distributed generation.",
        "Galvanic cells", "Hydrogen", "Energy efficiency"
    ),
    levelThree(
        "Electrochemistry", "Corrosion",
        "Corrosion is unwanted electrochemical oxidation of a material by its environment.",
        "For iron rusting, anodic iron dissolution and cathodic oxygen reduction occur at different surface regions; water and ions complete the circuit.",
        "Example: zinc coating protects steel both as a barrier and, if scratched, by oxidizing sacrificially before iron.",
        "anode: Fe -> Fe2+ + 2e-",
        "Rusting requires an electrochemical pathway; oxygen alone without suitable moisture is insufficient for ordinary rust formation.",
        "Corrosion science protects bridges, pipelines, vehicles, ships and electronic equipment.",
        "Sacrificial protection", "Galvanization", "Electrochemical cells"
    ),

    levelThree(
        "Surface Chemistry", "Adsorption",
        "Adsorption is accumulation of particles at a surface or interface.",
        "Physisorption uses weak intermolecular forces and is often reversible; chemisorption forms stronger surface bonds. Surface area, pressure and temperature affect coverage.",
        "Example: activated charcoal removes vapours because its porous structure provides a very large surface for adsorption.",
        "amount adsorbed increases with available surface sites",
        "Do not confuse adsorption at a surface with absorption throughout a bulk material.",
        "Adsorption is used in purification, gas masks, chromatography, sensors and heterogeneous catalysis.",
        "Surface area", "Adsorption isotherms", "Physisorption"
    ),
    levelThree(
        "Surface Chemistry", "Catalysis",
        "Surface catalysis occurs when reactants adsorb and react at active sites on a catalyst.",
        "Adsorption brings molecules together and weakens bonds; products then desorb, freeing sites. Excessively strong adsorption can poison or block the surface.",
        "Example: finely divided iron provides surface sites for nitrogen and hydrogen during ammonia synthesis.",
        "adsorption -> surface reaction -> desorption",
        "A catalyst surface is not uniformly active; defects, facets and poisons strongly affect performance.",
        "Heterogeneous catalysis enables fertilizers, fuels, pollution control and many bulk chemicals.",
        "Active sites", "Adsorption", "Catalyst poisoning"
    ),
    levelThree(
        "Surface Chemistry", "Colloids",
        "A colloid contains dispersed particles intermediate in size between a true solution and a suspension.",
        "Colloidal particles remain distributed through Brownian motion and surface charge and scatter light in the Tyndall effect.",
        "Example: milk is a colloidal dispersion whose droplets scatter a beam of light even though they do not rapidly settle.",
        "dispersed phase + dispersion medium -> colloid",
        "A colloid may look homogeneous to the eye but is heterogeneous at the microscopic scale.",
        "Colloids occur in foods, medicines, paints, smoke control and nanomaterial processing.",
        "Tyndall effect", "Brownian motion", "Coagulation"
    ),
    levelThree(
        "Surface Chemistry", "Emulsions",
        "An emulsion is a colloidal dispersion of one liquid in another immiscible liquid.",
        "Emulsifiers adsorb at droplet surfaces, reduce interfacial tension and slow coalescence. Emulsions may be oil-in-water or water-in-oil.",
        "Example: mayonnaise is an oil-in-water emulsion stabilized largely by lecithin from egg yolk.",
        "immiscible liquids + emulsifier -> stabilized droplets",
        "Shaking may disperse liquids temporarily, but a stable emulsion usually requires an emulsifier.",
        "Emulsions are central to foods, cosmetics, medicines, paints and agrochemical formulations.",
        "Surfactants", "Interfacial tension", "Colloids"
    ),
    levelThree(
        "Surface Chemistry", "Micelles",
        "Micelles are organized aggregates of surfactant molecules formed above a critical concentration.",
        "In water, hydrophobic tails cluster inward and hydrophilic heads face outward, allowing oily material to be dispersed. Formation also depends on temperature.",
        "Example: soap micelles surround grease with hydrocarbon tails inward while ionic heads keep the aggregate suspended in wash water.",
        "surfactant concentration > CMC -> micelles form",
        "Individual surfactant molecules exist below the critical micelle concentration; micelles are not permanent covalent structures.",
        "Micelles enable cleaning, drug delivery, extraction and controlled formulation.",
        "Surfactants", "Critical micelle concentration", "Hydrophobic effect"
    ),

    levelThree(
        "Solid State", "Crystal systems",
        "The seven crystal systems classify unit cells by their edge lengths and interaxial angles.",
        "They are cubic, tetragonal, orthorhombic, hexagonal, trigonal, monoclinic and triclinic. Symmetry distinguishes systems beyond outward crystal shape.",
        "Example: a cubic unit cell has a = b = c and alpha = beta = gamma = 90 degrees.",
        "crystal system determined by a,b,c and alpha,beta,gamma constraints",
        "Do not identify a crystal system only from a rough external shape; internal symmetry and lattice parameters define it.",
        "Crystal-system identification supports diffraction, mineralogy and material-property prediction.",
        "Symmetry", "Lattice parameters", "Unit cells"
    ),
    levelThree(
        "Solid State", "Unit cell",
        "A unit cell is the smallest repeating volume that reproduces a crystal lattice by translation.",
        "Atoms at corners, faces, edges and inside contribute fractional or whole counts to one cell. Geometry relates cell dimensions to density.",
        "Example: a simple cubic cell has eight corner atoms, each shared by eight cells, giving 8 x 1/8 = 1 atom per unit cell.",
        "density = ZM / (NA a^3) for a cubic cell",
        "Do not count every drawn corner atom as fully belonging to one unit cell.",
        "Unit-cell analysis reveals crystal density, composition, packing and diffraction patterns.",
        "Lattice points", "Coordination number", "Crystal density"
    ),
    levelThree(
        "Solid State", "Packing",
        "Crystal packing describes how particles occupy space in a solid.",
        "Close-packed layers produce hexagonal close packing or cubic close packing, both with coordination number 12 and about 74% packing efficiency.",
        "Example: face-centred cubic packing contains four atoms per unit cell and occupies about 74% of the cell volume.",
        "packing efficiency = occupied particle volume / unit-cell volume x 100",
        "Do not confuse body-centred cubic with close packing; BCC has lower packing efficiency and coordination number 8.",
        "Packing influences density, slip, strength, voids and alloy formation.",
        "Close packing", "Coordination number", "Interstitial sites"
    ),
    levelThree(
        "Solid State", "Defects",
        "Crystal defects are departures from perfect periodic arrangement.",
        "Point defects include vacancies, interstitials, Schottky and Frenkel defects; impurities and dislocations also alter electrical, optical and mechanical properties.",
        "Example: a Schottky defect removes equal numbers of cations and anions from an ionic crystal, preserving neutrality while lowering density.",
        "defect concentration generally rises with temperature",
        "Defects do not merely damage materials; controlled defects often create useful properties.",
        "Defect engineering tunes semiconductors, ionic conductors, alloys and coloured crystals.",
        "Vacancies", "Nonstoichiometry", "Dislocations"
    ),
    levelThree(
        "Solid State", "Semiconductors",
        "Semiconductors have electrical conductivity between conductors and insulators and a modest band gap.",
        "Heating or doping changes carrier concentration. Donor dopants create n-type material; acceptor dopants create p-type material.",
        "Example: phosphorus-doped silicon has extra electrons as majority carriers and behaves as an n-type semiconductor.",
        "conductivity = charge x carrier concentration x mobility",
        "n-type material is electrically neutral overall; the label describes majority mobile carriers, not net charge.",
        "Semiconductors enable diodes, transistors, solar cells, sensors and modern computing.",
        "Band gap", "Doping", "Charge carriers"
    ),

    levelThree(
        "Nuclear Chemistry", "Radioactivity",
        "Radioactivity is spontaneous transformation of unstable nuclei with emission of particles or radiation.",
        "Decay is random for one nucleus but predictable for a large sample and is largely unaffected by ordinary temperature, pressure or chemical state.",
        "Example: a Geiger counter records individual decay events whose average rate falls exponentially with time.",
        "A = lambda N",
        "Radioactivity is a nuclear change, not an electron-shell chemical reaction.",
        "Radioisotopes are used in imaging, therapy, dating, tracers and industrial inspection.",
        "Nuclear stability", "Decay constant", "Half-life"
    ),
    levelThree(
        "Nuclear Chemistry", "Alpha decay",
        "Alpha decay emits a helium-4 nucleus containing two protons and two neutrons.",
        "The parent mass number decreases by four and atomic number by two. Alpha particles ionize strongly but penetrate weakly.",
        "Example: uranium-238 undergoes alpha decay to thorium-234 plus helium-4.",
        "A -> A-4; Z -> Z-2",
        "An alpha particle is a bare helium nucleus, not a neutral helium atom with electrons.",
        "Alpha emitters are used in some smoke detectors and targeted therapies but require contamination control.",
        "Nuclear equations", "Ionizing power", "Radiation safety"
    ),
    levelThree(
        "Nuclear Chemistry", "Beta decay",
        "Beta decay changes a neutron-proton balance while emitting a beta particle and a neutrino or antineutrino.",
        "In beta-minus decay, a neutron becomes a proton, electron and antineutrino; mass number stays constant and atomic number rises by one.",
        "Example: carbon-14 beta-minus decays to nitrogen-14, an electron and an antineutrino.",
        "beta-minus: A unchanged; Z -> Z+1",
        "The emitted beta electron is created in nuclear decay; it is not an orbital electron expelled from the atom.",
        "Beta emitters support thickness gauges, tracers, dating and medical treatment.",
        "Weak interaction", "Neutrinos", "Nuclear equations"
    ),
    levelThree(
        "Nuclear Chemistry", "Gamma rays",
        "Gamma rays are high-energy photons emitted when an excited nucleus loses energy.",
        "Gamma emission changes neither mass number nor atomic number. Gamma radiation penetrates strongly and requires dense shielding.",
        "Example: an excited technetium-99m nucleus emits a gamma photon to reach a lower nuclear energy state without changing element.",
        "excited nucleus -> lower-energy nucleus + gamma photon",
        "Gamma rays have no mass or charge, but they are ionizing electromagnetic radiation and can be hazardous.",
        "Gamma rays are used in imaging, sterilization, radiotherapy and non-destructive testing.",
        "Electromagnetic radiation", "Nuclear energy levels", "Shielding"
    ),
    levelThree(
        "Nuclear Chemistry", "Half-life",
        "Half-life is the time required for half the radioactive nuclei in a sample to decay.",
        "It is constant for a given radionuclide and independent of initial amount. Radioactive decay follows first-order exponential behavior.",
        "Example: after three half-lives, the remaining fraction is (1/2)^3 = 1/8 or 12.5%.",
        "N = N0(1/2)^(t/t1/2); t1/2 = ln2/lambda",
        "Half-life does not mean every nucleus decays at that exact time; individual decay is random.",
        "Half-life calculations guide dating, medicine dosing, waste storage and radiation safety.",
        "Exponential decay", "Decay constant", "Activity"
    ),
    levelThree(
        "Nuclear Chemistry", "Nuclear fission",
        "Nuclear fission splits a heavy nucleus into smaller nuclei, neutrons and energy.",
        "A released neutron can trigger further events, producing a chain reaction. Controlled reactions sustain power generation; uncontrolled multiplication releases energy rapidly.",
        "Example: neutron absorption by uranium-235 can produce two medium-mass fragments, additional neutrons and about 200 MeV of energy.",
        "heavy nucleus + neutron -> fragments + neutrons + energy",
        "Fission products are not always the same pair of nuclei, and mass-energy rather than ordinary mass alone is conserved.",
        "Fission supplies low-carbon electricity and radioisotopes while requiring waste and safety management.",
        "Chain reactions", "Criticality", "Mass-energy equivalence"
    ),
    levelThree(
        "Nuclear Chemistry", "Nuclear fusion",
        "Nuclear fusion combines light nuclei into a heavier nucleus and releases energy.",
        "Very high temperature and confinement are needed to overcome electrostatic repulsion. The energy comes from a decrease in total rest mass.",
        "Example: deuterium plus tritium can form helium-4, a neutron and 17.6 MeV of energy.",
        "light nuclei -> heavier nucleus + energy",
        "Fusion is not ordinary burning; it is a nuclear process requiring plasma conditions.",
        "Fusion powers stars and is being developed as a potential energy source.",
        "Plasma", "Binding energy", "Stellar nucleosynthesis"
    )
)
