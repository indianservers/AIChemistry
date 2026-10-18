package com.indianservers.aichemistryexplorer

private fun classTen(
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

internal val classTenBoardContent: Map<String, LevelOneConceptContent> = mapOf(
    classTen(
        "Class 10 Chemical Substances and Carbon", "Balancing chemical equations",
        "A balanced chemical equation contains equal numbers of every type of atom on the reactant and product sides.",
        "Write correct formulae first, count atoms, adjust whole-number coefficients and reduce them to the smallest ratio. Coefficients multiply complete formula units; subscripts belong to substance identities and must not be altered during balancing.",
        "unbalanced: Fe + H2O -> Fe3O4 + H2; balanced: 3Fe + 4H2O -> Fe3O4 + 4H2",
        "For Al + O2 -> Al2O3, balance aluminium and oxygen to obtain 4Al + 3O2 -> 2Al2O3, then verify four Al and six O atoms on each side.",
        "Never change a subscript such as H2O into H2O2 to balance atoms; that changes water into a different substance.",
        "Balanced equations support quantitative reaction calculations, manufacturing yields and pollution accounting.",
        "Conservation of mass", "Coefficients", "Stoichiometry"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "State symbols and reaction observations",
        "State symbols and observation notes connect a symbolic equation with what is actually seen during a reaction.",
        "Use (s), (l), (g) and (aq) for solid, liquid, gas and aqueous solution. Record evidence such as colour change, gas evolution, precipitate formation, temperature change, light or odour without confusing observation with particle-level inference.",
        "CaCO3(s) --heat--> CaO(s) + CO2(g); AgNO3(aq) + NaCl(aq) -> AgCl(s)↓ + NaNO3(aq)",
        "Mixing colourless silver-nitrate and sodium-chloride solutions produces a white precipitate. 'White solid appears' is the observation; 'AgCl formed' is the chemical inference.",
        "Do not write (aq) for every liquid: aqueous means dissolved in water, while pure liquid water is marked (l).",
        "Clear observation records help identify substances, verify reactions and communicate laboratory results safely.",
        "Physical states", "Laboratory evidence", "Precipitates and gases"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "Endothermic and exothermic reactions",
        "Exothermic reactions transfer heat to the surroundings, while endothermic reactions absorb heat from the surroundings.",
        "In an exothermic process the surroundings warm and products have lower enthalpy than reactants; in an endothermic process the surroundings cool and products have higher enthalpy. Activation energy is still required to begin either type.",
        "exothermic: reactants -> products + heat; endothermic: reactants + heat -> products",
        "Combustion of methane, CH4 + 2O2 -> CO2 + 2H2O + heat, is exothermic. Thermal decomposition of CaCO3 requires heat and is endothermic.",
        "A reaction that needs ignition is not necessarily endothermic; combustion needs activation energy but releases more energy overall.",
        "Heat classification explains fuels, respiration, cold packs, thermal decomposition and energy-efficient industrial reactions.",
        "Energy profiles", "Combustion", "Thermal decomposition"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "Precipitation reactions",
        "A precipitation reaction forms an insoluble solid when ions from aqueous solutions combine.",
        "Double-displacement reactions exchange ions, but a reaction is driven only when a precipitate, gas or weakly ionised product forms. The net ionic equation removes spectator ions and shows the species responsible for the visible solid.",
        "Ag+(aq) + Cl-(aq) -> AgCl(s)↓",
        "Pb(NO3)2(aq) + 2KI(aq) -> PbI2(s)↓ + 2KNO3(aq) gives a yellow lead-iodide precipitate.",
        "Do not call every double-displacement mixture a precipitation reaction; first check whether an insoluble product actually forms.",
        "Precipitation is used in ion identification, water treatment, pigment preparation and removal of contaminants.",
        "Double displacement", "Solubility", "Net ionic equations"
    ),

    classTen(
        "Class 10 Chemical Substances and Carbon", "Preparation equations and uses of sodium hydroxide",
        "Sodium hydroxide is manufactured by electrolysis of concentrated aqueous sodium chloride in the chlor-alkali process.",
        "At the anode chloride ions form chlorine; at the cathode water forms hydrogen and hydroxide ions. Sodium ions remain with hydroxide in solution. Chlorine and hydrogen must be kept separated because their mixture is hazardous.",
        "2NaCl(aq) + 2H2O(l) -> 2NaOH(aq) + Cl2(g) + H2(g)",
        "Electrolysis produces NaOH solution together with chlorine for disinfectants/PVC and hydrogen for fuels or chemical synthesis.",
        "Do not say sodium metal is deposited from brine; water is reduced preferentially at the cathode under these aqueous conditions.",
        "Sodium hydroxide is used in soaps, paper, detergents, rayon, degreasing and neutralisation, with strict corrosive-chemical controls.",
        "Chlor-alkali process", "Electrolysis", "Industrial chemicals"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "Preparation equations and uses of bleaching powder",
        "Bleaching powder is produced by passing chlorine over dry slaked lime and is commonly represented as calcium oxychloride, CaOCl2.",
        "The product releases oxidising chlorine species in moist conditions, accounting for bleaching and disinfection. Dry reactants, controlled chlorine exposure and safe storage are important because acids can liberate toxic chlorine gas.",
        "Ca(OH)2 + Cl2 -> CaOCl2 + H2O",
        "Chlorine from the chlor-alkali process reacts with slaked lime to make bleaching powder used to disinfect drinking water and bleach cotton or paper pulp.",
        "Do not mix bleaching powder with acids or ammonia-containing cleaners; dangerous chlorine or chloramine gases may form.",
        "Bleaching powder supports water sanitation, textile processing and oxidising treatments.",
        "Available chlorine", "Oxidation", "Water treatment"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "Preparation equations and uses of baking soda",
        "Baking soda is sodium hydrogen carbonate, NaHCO3, prepared in the Solvay process and decomposed by heat to release carbon dioxide.",
        "Concentrated brine is saturated with ammonia and carbon dioxide so sparingly soluble NaHCO3 precipitates. Gentle heating releases CO2, which expands dough; the mild base also neutralises acids.",
        "NaCl + H2O + CO2 + NH3 -> NH4Cl + NaHCO3; 2NaHCO3 --heat--> Na2CO3 + CO2 + H2O",
        "In baking powder, NaHCO3 reacts with an edible acid to release CO2. Tartaric acid helps prevent the bitter taste that excess sodium carbonate could cause.",
        "Do not identify baking soda as sodium carbonate; it is sodium hydrogen carbonate and has different formula and behaviour.",
        "Baking soda is used in baking powder, antacids and certain fire extinguishers.",
        "Solvay process", "Thermal decomposition", "Carbon dioxide"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "Preparation equations and uses of washing soda",
        "Washing soda is crystalline sodium carbonate decahydrate, Na2CO3·10H2O, obtained by crystallising sodium carbonate with water.",
        "Heating sodium hydrogen carbonate forms sodium carbonate, which crystallises from water as the decahydrate. Carbonate ions precipitate calcium and magnesium ions, allowing washing soda to soften hard water.",
        "2NaHCO3 --heat--> Na2CO3 + CO2 + H2O; Na2CO3 + 10H2O -> Na2CO3·10H2O",
        "In hard water, Ca2+ + CO3^2- -> CaCO3(s), removing calcium ions that otherwise interfere with soap.",
        "Do not confuse anhydrous sodium carbonate with washing-soda crystals; the named school compound contains ten waters of crystallisation.",
        "Washing soda is used for cleaning, water softening and manufacture of glass, soap and paper.",
        "Water of crystallisation", "Hard water", "Sodium carbonate"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "Preparation equations and uses of plaster of Paris",
        "Plaster of Paris is calcium sulfate hemihydrate, CaSO4·1/2H2O, made by carefully heating gypsum.",
        "Partial dehydration of gypsum near 373 K gives a powder that rehydrates and sets into hard gypsum when mixed with water. Excessive heating produces anhydrous calcium sulfate that sets poorly.",
        "CaSO4·2H2O --373 K--> CaSO4·1/2H2O + 3/2H2O; CaSO4·1/2H2O + 3/2H2O -> CaSO4·2H2O",
        "A wet plaster bandage can be shaped around an injured limb; hydration and crystal interlocking make it harden into a supportive cast.",
        "Do not call Plaster of Paris calcium carbonate or ignore the half-water notation; it is a sulfate hemihydrate.",
        "Plaster of Paris is used in casts, moulds, statues, false ceilings and decorative surfaces.",
        "Gypsum", "Hydration", "Setting reaction"
    ),

    classTen(
        "Class 10 Chemical Substances and Carbon", "Metal extraction by reactivity series",
        "A metal's position in the reactivity series determines whether it is extracted by electrolysis, chemical reduction or heating of its ore.",
        "Highly reactive K-Ca-Na-Mg-Al metals require electrolysis of molten compounds. Middle metals such as Zn, Fe, Pb and Cu are commonly obtained after converting ores to oxides and reducing with carbon/CO or aluminium. Low-reactivity metals may occur native or be recovered by heating.",
        "high activity -> electrolysis; middle -> roasting/calcination then reduction; low -> heating or native occurrence",
        "ZnS is roasted: 2ZnS + 3O2 -> 2ZnO + 2SO2; then ZnO + C -> Zn + CO. Aluminium oxide instead requires molten electrolysis.",
        "Do not propose carbon reduction for sodium or aluminium oxides; these metals are more reactive than carbon.",
        "Selecting extraction routes balances chemical feasibility, energy demand, ore composition, pollution control and recycling.",
        "Reactivity series", "Metallurgy", "Electrolysis and reduction"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "Functional groups in carbon compounds",
        "A functional group is an atom or group that gives an organic compound its characteristic reactions and naming suffix or prefix.",
        "Class 10 groups include halo (-X), alcohol (-OH), aldehyde (-CHO), ketone (>C=O) and carboxylic acid (-COOH). The carbon skeleton determines the parent name, while the principal group determines suffix and numbering.",
        "-OH alcohol; -CHO aldehyde; >C=O ketone; -COOH carboxylic acid; -Cl/-Br halo",
        "CH3CH2OH is an alcohol, CH3CHO an aldehyde, CH3COCH3 a ketone and CH3COOH a carboxylic acid.",
        "Do not identify every oxygen-containing compound as an alcohol; locate the exact bonding pattern of the functional group.",
        "Functional groups organise fuels, solvents, flavours, medicines, polymers and biological molecules into predictable reaction families.",
        "Homologous series", "Structure-property relationships", "Organic reactions"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "IUPAC naming of carbon compounds",
        "IUPAC names encode the longest parent chain, carbon-carbon bond type, substituents, functional group and their positions.",
        "Choose the longest chain containing the principal functional group and multiple bond, number for the lowest relevant locants, name substituents alphabetically and apply the correct suffix.",
        "prefix + parent carbon root + primary suffix + functional-group suffix",
        "CH3CH(CH3)CH2OH is 2-methylpropan-1-ol: the OH-containing three-carbon chain is numbered from the OH end.",
        "Do not always choose the visually straight chain or number from the nearest alkyl branch when a principal functional group is present.",
        "Systematic names allow scientists, teachers, regulators and manufacturers to identify structures without ambiguity.",
        "Parent chains", "Functional groups", "Homologous series"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "Addition reactions of carbon compounds",
        "Addition reactions consume a carbon-carbon double or triple bond as atoms add across the multiple bond.",
        "Unsaturated hydrocarbons add hydrogen over Ni/Pd/Pt and add halogens, causing bromine solution to lose colour. Hydrogenation converts unsaturated vegetable oils toward more saturated fats.",
        "CH2=CH2 + H2 --Ni--> CH3-CH3",
        "Ethene decolourises bromine solution as Br atoms add across C=C to form 1,2-dibromoethane.",
        "Do not classify hydrogenation as substitution; no atom is replaced and the pi bond is consumed as new sigma bonds form.",
        "Addition reactions are used to test unsaturation and manufacture fuels, polymers and modified edible fats.",
        "Unsaturation", "Hydrogenation", "Bromine test"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "Substitution reactions of carbon compounds",
        "In substitution, one atom or group in a molecule is replaced by another without changing the carbon skeleton's saturation.",
        "Saturated hydrocarbons react with chlorine or bromine in sunlight through a chain process. Further substitution can continue, so controlling reagent ratio and light exposure affects the product mixture.",
        "CH4 + Cl2 --sunlight--> CH3Cl + HCl",
        "One hydrogen in methane is replaced by chlorine to give chloromethane; excess chlorine may produce more highly chlorinated products.",
        "Do not write ultraviolet light as a reactant or expect one perfectly pure product when several equivalent or nonequivalent hydrogens can be replaced.",
        "Substitution produces useful halo compounds that serve as solvents and chemical intermediates, with environmental and toxicity controls.",
        "Alkanes", "Halogenation", "Reaction conditions"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "Oxidation reactions of carbon compounds",
        "Oxidation of carbon compounds commonly adds oxygen, removes hydrogen or raises the oxidation state of carbon.",
        "Alkaline KMnO4 or acidified K2Cr2O7 oxidises ethanol first to ethanal and then ethanoic acid. Combustion is more extensive oxidation to CO2 and water when oxygen is sufficient.",
        "CH3CH2OH + 2[O] -> CH3COOH + H2O",
        "Warming ethanol with an oxidising agent changes it toward ethanoic acid; the product has acidic properties unlike the starting alcohol.",
        "Do not assume adding visible oxygen gas is the only oxidation pathway; oxidising agents transfer oxygen or remove hydrogen chemically.",
        "Controlled oxidation produces acids, aldehydes and industrial intermediates, while uncontrolled oxidation affects fuels and food spoilage.",
        "Alcohols", "Oxidising agents", "Combustion"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "Soap micelles and cleansing action",
        "Soap ions have a water-attracting ionic head and an oil-attracting hydrocarbon tail, allowing them to surround grease in micelles.",
        "Above a suitable concentration in water, tails cluster inward around oily dirt while charged heads face outward. Agitation breaks grease into suspended droplets that can be rinsed away.",
        "hydrophobic tails -> grease core; hydrophilic COO- heads -> water",
        "When oily cloth is washed with soap, micelles encapsulate oil and keep droplets dispersed instead of redepositing them on the fabric.",
        "Do not say soap chemically destroys all grease; its principal cleansing action emulsifies and suspends oily material.",
        "Micelles enable washing, emulsification and formulation of cleaners and personal-care products.",
        "Amphiphiles", "Emulsification", "Surface chemistry"
    ),
    classTen(
        "Class 10 Chemical Substances and Carbon", "Detergents and hard water",
        "Synthetic detergents clean effectively in hard water because their calcium and magnesium salts remain much more soluble than soap scum.",
        "Soap carboxylate ions react with Ca2+ or Mg2+ to form insoluble scum, wasting soap and reducing lather. Sulfonate or sulfate detergent heads generally avoid this precipitation, though detergent biodegradability and phosphate pollution must be considered.",
        "2RCOO- + Ca2+ -> (RCOO)2Ca(s) scum",
        "Soap lathers poorly in hard water until Ca2+/Mg2+ are removed, while a suitable detergent continues forming micelles and cleaning.",
        "Do not claim every detergent is environmentally harmless merely because it works in hard water; composition and biodegradability matter.",
        "Detergents support domestic and industrial cleaning, while formulation choices affect wastewater and aquatic ecosystems.",
        "Hardness ions", "Soap scum", "Biodegradability"
    ),

    classTen(
        "Periodic Classification and Electrolysis", "Döbereiner's triads",
        "Döbereiner grouped selected elements into triads with similar properties, where the middle atomic mass was approximately the average of the other two.",
        "The idea connected numerical mass patterns with chemical similarity but worked for only a few known groups, so it could not classify all elements.",
        "middle mass approximately = (first mass + third mass)/2",
        "For Li, Na and K, (6.9 + 39.1)/2 = 23.0, close to sodium's atomic mass, and all three show related alkali-metal chemistry.",
        "Do not force any three consecutive elements into a triad; similarity of properties is an essential condition.",
        "Triads illustrate how early evidence led scientists toward the idea of periodic recurrence.",
        "History of periodicity", "Atomic mass", "Chemical families"
    ),
    classTen(
        "Periodic Classification and Electrolysis", "Newlands' law of octaves",
        "Newlands arranged known elements by increasing atomic mass and proposed that every eighth element repeated similar properties.",
        "The octave pattern worked mainly up to calcium, assumed too few undiscovered elements and sometimes placed unlike elements together or two elements in one slot.",
        "in the early mass sequence, property pattern approximately repeats at the eighth element",
        "Lithium and sodium appeared in corresponding octave positions and showed similar reactivity, but the pattern broke down for heavier elements.",
        "Do not present the law of octaves as universally valid; its limitations are part of the required explanation.",
        "Newlands' work was an important attempt to recognise periodic repetition before the modern atomic-number basis was known.",
        "Early classifications", "Periodicity", "Limitations of models"
    ),
    classTen(
        "Periodic Classification and Electrolysis", "Mendeleev's periodic classification",
        "Mendeleev arranged elements mainly by increasing atomic mass while keeping elements with similar chemical properties in the same groups.",
        "He compared formulas of oxides and hydrides, left gaps for undiscovered elements and predicted their properties. Limitations included hydrogen's position, isotopes and anomalous mass order such as cobalt/nickel or tellurium/iodine.",
        "properties were treated as periodic functions of atomic mass",
        "Mendeleev's eka-aluminium predictions closely matched gallium's later measured mass, density and compound formulas, supporting his table.",
        "Do not say Mendeleev blindly followed mass order; he sometimes prioritised chemical similarity, revealing both insight and the need for atomic number.",
        "Mendeleev's table enabled prediction and organised chemical evidence, forming the bridge to the modern periodic table.",
        "Periodic law", "Predicted elements", "Modern atomic number"
    )
)
