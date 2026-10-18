package com.indianservers.aichemistryexplorer

/** Board-focused additions for CBSE Class XII and AP Intermediate chemistry. */
private fun boardLesson(
    module: String,
    title: String,
    summary: String,
    explanation: String,
    example: String,
    relationship: String,
    mistake: String,
    application: String,
    vararg connections: String
): Pair<String, LevelOneConceptContent> = "$module|$title" to LevelOneConceptContent(
    summary = summary,
    explanation = explanation,
    workedExample = example,
    keyRelationship = relationship,
    commonMistake = mistake,
    application = application,
    connections = connections.toList()
)

internal val seniorSecondaryBoardContent: Map<String, LevelOneConceptContent> = mapOf(
    boardLesson(
        "Solutions: Senior Secondary", "Ideal and non-ideal solutions",
        "Ideal solutions obey Raoult's law throughout the composition range; non-ideal solutions show positive or negative deviations because unlike-molecule attractions differ from like-molecule attractions.",
        "For an ideal binary solution, each partial vapour pressure is pi = xi pi° and the total is their sum. DeltaHmix and DeltaVmix are zero. Weaker A-B attractions produce positive deviation, higher vapour pressure and often a minimum-boiling azeotrope; stronger A-B attractions produce negative deviation and may form a maximum-boiling azeotrope.",
        "Example: at a fixed temperature, xA = 0.40, pA° = 100 kPa, xB = 0.60 and pB° = 50 kPa. ptotal = 0.40(100) + 0.60(50) = 70 kPa. A measured value above 70 kPa indicates positive deviation.",
        "pi = xi pi°; ptotal = sum(pi); ideal: DeltaHmix = 0 and DeltaVmix = 0",
        "Do not label a solution ideal merely because both components are liquids; compare the measured vapour pressure and mixing effects with Raoult's-law predictions.",
        "Vapour-pressure diagrams and azeotropes determine whether industrial liquid mixtures can be separated by ordinary fractional distillation.",
        "Raoult's law", "Vapour-pressure diagrams", "Azeotropes"
    ),
    boardLesson(
        "Solutions: Senior Secondary", "Abnormal molar mass and van't Hoff factor",
        "Solute association or dissociation changes the number of dissolved particles, producing an apparent molar mass different from the true value.",
        "The van't Hoff factor i is the observed colligative property divided by the value expected for a non-electrolyte. Dissociation normally gives i greater than one; association gives i less than one. For dissociation into nu particles, i = 1 + alpha(nu - 1); for n-mer association, i = 1 - alpha(1 - 1/n).",
        "Example: 0.10 mol CaCl2 in 1.0 kg water gives depression in freezing point 0.465 K when Kf = 1.86 K kg mol-1. i = 0.465/(1.86 x 0.10) = 2.50; alpha = (2.50 - 1)/(3 - 1) = 0.75.",
        "i = observed colligative property / calculated value; DeltaTf = i Kf m",
        "Do not automatically use the integer number of ions as i; incomplete dissociation and ion pairing make experimental i non-integral.",
        "Van't Hoff corrections improve molar-mass determination and electrolyte formulation in pharmaceuticals, antifreeze and physiological solutions.",
        "Colligative properties", "Degree of dissociation", "Association"
    ),
    boardLesson(
        "Solutions: Senior Secondary", "Colligative-property numericals",
        "Relative lowering of vapour pressure, boiling-point elevation, freezing-point depression and osmotic pressure depend on the number of solute particles rather than their chemical identity.",
        "Select the equation that matches the measurement, convert mass to moles, use kilograms of solvent for molality and kelvin for temperature differences. Include i for association or dissociation. Osmotic pressure is preferred for macromolecules because it can be measured at room temperature using very dilute solutions.",
        "Example: 1.80 g glucose is dissolved in 100 g water. Moles = 1.80/180 = 0.0100 and m = 0.0100/0.100 = 0.100 mol kg-1. With Kf(water) = 1.86 K kg mol-1, DeltaTf = 0.186 K and the freezing point is -0.186 °C.",
        "DeltaTb = i Kb m; DeltaTf = i Kf m; pi = i C R T; (p°-p)/p° = xsolute",
        "Do not use grams of solvent where kilograms are required, and do not substitute solution volume into a molality calculation.",
        "Colligative calculations are used in molar-mass measurement, IV-fluid design, food preservation and engine coolants.",
        "Molality", "Osmotic pressure", "Molar mass"
    ),

    boardLesson(
        "Electrochemistry", "Electrolytic conductivity",
        "Conductivity measures the ability of an electrolyte solution to carry current through moving ions; it depends on ion concentration, charge and mobility.",
        "Resistance R is measured between electrodes. Conductance G = 1/R, while conductivity kappa = G(l/A), where l/A is the cell constant. Conductivity usually decreases on dilution because fewer ions occupy each unit volume, even though individual ions may move more freely.",
        "Example: a solution has R = 250 ohm in a cell with cell constant 1.25 cm-1. G = 0.00400 S and kappa = 0.00400 x 1.25 = 5.00 x 10^-3 S cm-1.",
        "G = 1/R; kappa = G x cell constant; cell constant = l/A",
        "Do not use conductance and conductivity as synonyms: conductance depends on cell geometry, whereas conductivity is a property of the solution at a stated temperature.",
        "Conductivity measurements monitor water purity, salinity, ionic reactions, batteries and industrial process streams.",
        "Resistance", "Cell constant", "Ion mobility"
    ),
    boardLesson(
        "Electrochemistry", "Molar conductivity and Kohlrausch law",
        "Molar conductivity is the conductance contribution of the ions produced by one mole of electrolyte and approaches a limiting value at infinite dilution.",
        "Lambda_m = kappa x 1000/C when kappa is in S cm-1 and C is in mol L-1. Strong electrolytes rise gradually on dilution; weak electrolytes rise sharply as ionisation increases. Kohlrausch's law states that at infinite dilution each ion contributes independently, allowing limiting values for weak electrolytes to be assembled from strong-electrolyte data.",
        "Example: Lambda°m(CH3COOH) = Lambda°m(HCl) + Lambda°m(CH3COONa) - Lambda°m(NaCl). Using 426.0, 91.0 and 126.4 S cm2 mol-1 gives 390.6 S cm2 mol-1.",
        "Lambda_m = kappa x 1000/C; Kohlrausch: Lambda°m = sum(nu_i lambda°_i)",
        "Do not extrapolate weak-electrolyte data as a straight line or forget to use compatible concentration and conductivity units.",
        "Kohlrausch's law supports weak-electrolyte dissociation constants, sparingly soluble salt solubilities and electrolyte quality control.",
        "Infinite dilution", "Ionic mobility", "Degree of dissociation"
    ),

    boardLesson(
        "Chemical Kinetics", "Integrated rate equations",
        "Integrated rate laws connect reactant concentration with time and allow reaction order and rate constants to be extracted from experimental data.",
        "For a zero-order reaction, [A]t = [A]0 - kt and [A] versus t is linear. For first order, ln[A]t = ln[A]0 - kt and ln[A] versus t is linear. The slope is -k, with units mol L-1 s-1 for zero order and s-1 for first order.",
        "Example: a first-order reactant falls from 0.800 M to 0.200 M in 40.0 min. k = (2.303/40.0) log(0.800/0.200) = 0.0347 min-1.",
        "zero: [A]t=[A]0-kt; first: k=(2.303/t)log([A]0/[A]t)",
        "Do not identify order from the balanced equation; order is determined experimentally from the rate law or the correct linear concentration-time plot.",
        "Integrated laws predict shelf life, drug degradation, pollutant decay and the time required for industrial conversion.",
        "Reaction order", "Linear plots", "Rate constant"
    ),
    boardLesson(
        "Chemical Kinetics", "Half-life of reactions",
        "Half-life is the time required for a reactant concentration to fall to half its current value, and its concentration dependence reveals reaction order.",
        "For zero order, t1/2 = [A]0/(2k), so half-life decreases as the initial concentration decreases. For first order, t1/2 = ln2/k and is independent of initial concentration. Successive first-order halvings therefore take equal times.",
        "Example: if a first-order process has k = 2.31 x 10^-3 s-1, t1/2 = 0.693/k = 300 s. After three half-lives, the fraction remaining is (1/2)^3 = 1/8.",
        "zero: t1/2=[A]0/(2k); first: t1/2=0.693/k",
        "Do not apply the radioactive or first-order half-life formula to a zero-order reaction without first establishing the reaction order.",
        "Half-life calculations guide pharmaceutical expiry, radiotracer use, environmental persistence and reaction scheduling.",
        "Integrated rate laws", "Exponential decay", "Shelf life"
    ),
    boardLesson(
        "Chemical Kinetics", "Collision theory",
        "Collision theory explains reaction rate through collision frequency, sufficient energy and suitable molecular orientation.",
        "Only collisions with energy at least equal to the activation energy and an effective orientation produce products. The Arrhenius factor exp(-Ea/RT) estimates the energetic fraction, while the pre-exponential factor includes collision frequency and orientation. Raising temperature increases the high-energy fraction strongly.",
        "Example: two molecules may collide often but react slowly when Ea is high or their reactive sites do not meet. A catalyst supplies another pathway with lower Ea, increasing effective collisions without changing DeltaG° or K.",
        "k = A exp(-Ea/RT); effective collision = sufficient energy + correct orientation",
        "Do not say every collision causes reaction or that a catalyst increases the energy of colliding molecules.",
        "Collision theory helps engineers choose temperature, catalysts and mixing conditions for safe and efficient reactors.",
        "Activation energy", "Arrhenius equation", "Catalysis"
    ),

    boardLesson(
        "d- and f-Block Chemistry", "Important compounds of transition elements",
        "Potassium dichromate and potassium permanganate are strongly coloured transition-metal oxidants whose products depend on reaction conditions.",
        "Acidified dichromate, Cr2O7^2-, is reduced from Cr(VI) to green Cr3+; acidified permanganate, MnO4-, is reduced from Mn(VII) to nearly colourless Mn2+. In neutral or weakly alkaline medium permanganate commonly forms brown MnO2. Preparation, colour changes and balanced ionic equations are central board requirements.",
        "Example: MnO4- + 5Fe2+ + 8H+ -> Mn2+ + 5Fe3+ + 4H2O. One mole permanganate oxidises five moles Fe2+ in acid solution and acts as its own indicator in titration.",
        "Cr2O7^2- + 14H+ + 6e- -> 2Cr3+ + 7H2O; MnO4- + 8H+ + 5e- -> Mn2+ + 4H2O",
        "Do not write the same permanganate reduction product for acidic, neutral and strongly alkaline media; the medium controls the manganese product.",
        "Dichromate and permanganate chemistry is used in redox analysis, synthesis, water testing and oxidation-process control.",
        "Oxidation states", "Redox titration", "Colour chemistry"
    ),
    boardLesson(
        "Coordination Compounds", "Bonding in metal carbonyls",
        "Metal carbonyl bonding is synergic: carbon monoxide donates a lone pair to the metal while the metal back-donates d-electron density into CO antibonding orbitals.",
        "Sigma donation forms M<-CO bonding and pi back-bonding strengthens M-C while weakening C-O. Greater back-donation lowers the C-O stretching frequency. Electron counting explains stable examples such as Ni(CO)4 and Fe(CO)5, while bridging and terminal carbonyls differ structurally and spectroscopically.",
        "Example: Ni in Ni(CO)4 has oxidation state zero. Nickel contributes 10 valence electrons and four neutral CO ligands donate 8, giving an 18-electron tetrahedral complex.",
        "M <- :CO sigma donation; M(dpi) -> CO(pi*) back-donation; 18-electron count",
        "Do not describe the M-C bond as donation in only one direction, and do not assign CO a negative charge in ordinary neutral carbonyl electron counting.",
        "Metal carbonyl bonding underlies homogeneous catalysis, metal purification and interpretation of infrared spectra.",
        "Synergic bonding", "18-electron rule", "Infrared spectroscopy"
    ),

    boardLesson(
        "Hydrocarbons and Halo Compounds", "Preparation and reactions of alkanes",
        "Alkanes are prepared by hydrogenation, Wurtz coupling, decarboxylation and Kolbe electrolysis and react mainly by combustion and free-radical substitution.",
        "Hydrogenation adds H2 to an alkene over Ni/Pt/Pd. Wurtz reaction couples alkyl halides with sodium in dry ether and is best for symmetrical alkanes. Soda-lime decarboxylation gives an alkane with one fewer carbon. Chlorination proceeds through initiation, propagation and termination radical steps.",
        "Example: CH3COONa + NaOH --CaO,heat--> CH4 + Na2CO3. Under UV light, CH4 + Cl2 -> CH3Cl + HCl through a radical chain mechanism.",
        "RCOONa + NaOH -> RH + Na2CO3; 2RX + 2Na -> R-R + 2NaX",
        "Do not use Wurtz reaction to claim a pure unsymmetrical alkane from two different halides; a mixture of coupling products results.",
        "Controlled alkane reactions produce fuels, halogenated intermediates and carbon feedstocks.",
        "Free radicals", "Decarboxylation", "Wurtz reaction"
    ),
    boardLesson(
        "Hydrocarbons and Halo Compounds", "Preparation and reactions of alkenes and alkynes",
        "Alkenes and alkynes are prepared by elimination reactions and undergo electrophilic addition, oxidation and reduction across multiple bonds.",
        "Alcohol dehydration and haloalkane dehydrohalogenation form alkenes, usually following Zaitsev orientation. Vicinal or geminal dihalides can give alkynes by double dehydrohalogenation. HX addition generally follows Markovnikov's rule; HBr with peroxide is the important anti-Markovnikov exception. Ozonolysis identifies double-bond positions.",
        "Example: CH3CH2OH --conc H2SO4,443 K--> CH2=CH2 + H2O. Propene + HBr gives mainly 2-bromopropane, but with peroxide gives 1-bromopropane.",
        "alcohol -> alkene + H2O; C=C + H2 -> alkane; C=C + O3 -> carbonyl products",
        "Do not apply the peroxide effect to HCl or HI; the school-level anti-Markovnikov radical exception is specific to HBr.",
        "Alkene and alkyne reactions manufacture polymers, solvents, alcohols and carbonyl compounds.",
        "Electrophilic addition", "Markovnikov rule", "Ozonolysis"
    ),
    boardLesson(
        "Hydrocarbons and Halo Compounds", "Preparation and reactions of benzene",
        "Benzene is prepared by cyclic polymerisation, decarboxylation or reduction routes and characteristically undergoes electrophilic aromatic substitution while retaining aromaticity.",
        "Nitration uses concentrated HNO3/H2SO4, sulphonation uses fuming H2SO4, halogenation uses FeX3 and Friedel-Crafts reactions use anhydrous AlCl3. Substituents direct later substitution according to electron donation or withdrawal. Addition occurs only under forcing conditions because aromatic stabilisation would be lost.",
        "Example: C6H6 + HNO3 --conc H2SO4--> C6H5NO2 + H2O. The electrophile is NO2+, generated by the acid mixture.",
        "benzene + E+ -> substituted benzene + H+; 3HCCH -> C6H6",
        "Do not draw benzene as an ordinary isolated triene or predict easy bromine-water decolourisation without a Lewis-acid catalyst.",
        "Aromatic substitution produces dyes, pharmaceuticals, polymers and high-volume chemical intermediates.",
        "Aromaticity", "Electrophilic substitution", "Directing effects"
    ),
    boardLesson(
        "Hydrocarbons and Halo Compounds", "Preparation and reactions of haloalkanes",
        "Haloalkanes are prepared from alcohols or hydrocarbons and undergo nucleophilic substitution, elimination and reactions with metals.",
        "Alcohols react with HX, PCl3/PCl5 or SOCl2; SOCl2 is convenient because gaseous by-products escape. SN1 is favoured by stable carbocations, polar protic solvent and tertiary substrates; SN2 is a one-step backside attack favoured by methyl or primary substrates. Alcoholic KOH promotes beta-elimination, while aqueous KOH promotes substitution.",
        "Example: CH3CH2Br + KOH(aq) -> CH3CH2OH + KBr, whereas CH3CH2Br + KOH(alc), heat -> CH2=CH2 + KBr + H2O.",
        "RX + OH- -> ROH + X-; RX + KOH(alc) -> alkene + KX + H2O",
        "Do not ignore solvent and temperature when choosing substitution versus elimination, and do not assign SN2 to a tertiary carbon without severe steric consequences.",
        "Haloalkanes are intermediates for alcohols, nitriles, amines, organometallic reagents and polymers.",
        "SN1 and SN2", "Elimination", "Grignard reagent"
    ),
    boardLesson(
        "Hydrocarbons and Halo Compounds", "Preparation and reactions of haloarenes",
        "Haloarenes are made by aromatic halogenation or diazonium chemistry and are less reactive toward nucleophilic substitution than haloalkanes because the aryl C-X bond has partial double-bond character.",
        "Benzene reacts with Cl2/FeCl3 or Br2/FeBr3. Sandmeyer reactions replace an aryl diazonium group by Cl or Br using Cu(I) salts. Resonance, sp2 carbon and bond shortening resist ordinary SN1/SN2 pathways, though strong conditions or electron-withdrawing groups enable substitution.",
        "Example: C6H5N2+Cl- --CuCl--> C6H5Cl + N2. Chlorobenzene with NaOH at high temperature and pressure gives phenol after acidification.",
        "ArN2+ + CuX -> ArX + N2; ArX + NaOH --high T,P--> ArONa -> ArOH",
        "Do not assume chlorobenzene reacts with aqueous hydroxide as readily as chloroethane; aryl and alkyl C-X bonds have different structures.",
        "Haloarenes serve as intermediates in pharmaceuticals, agrochemicals, dyes and specialty polymers.",
        "Sandmeyer reaction", "Aryl bond resonance", "Nucleophilic aromatic substitution"
    ),

    boardLesson(
        "Oxygen and Nitrogen Compounds", "Preparation and reactions of alcohols",
        "Alcohols are prepared by hydration or reduction and react through the O-H bond and C-O bond in oxidation, dehydration, esterification and substitution.",
        "Alkenes hydrate to alcohols; aldehydes and ketones reduce to primary and secondary alcohols. Primary alcohol oxidation gives aldehydes then acids, secondary gives ketones, while tertiary alcohols resist mild oxidation. Concentrated acid causes dehydration, and carboxylic acids give esters reversibly.",
        "Example: CH3CH2OH + [O] -> CH3CHO + H2O -> CH3COOH. With ethanoic acid and acid catalyst it forms ethyl ethanoate and water.",
        "RCH2OH + [O] -> RCHO -> RCOOH; ROH + R'COOH <=> R'COOR + H2O",
        "Do not state that all alcohols oxidise to carboxylic acids; the product depends on whether the alcohol is primary, secondary or tertiary.",
        "Alcohol reactions support solvents, fuels, antiseptics, fragrances, polymers and pharmaceutical synthesis.",
        "Oxidation", "Dehydration", "Esterification"
    ),
    boardLesson(
        "Oxygen and Nitrogen Compounds", "Preparation and reactions of phenols",
        "Phenol is prepared from haloarenes, diazonium salts or cumene and undergoes acidic and strongly activated electrophilic aromatic reactions.",
        "Phenoxide ion is resonance-stabilised, making phenol more acidic than alcohols. The OH group activates ortho and para positions: bromine water gives 2,4,6-tribromophenol, dilute nitric acid gives o- and p-nitrophenols. Kolbe-Schmitt carboxylation gives salicylic acid and Reimer-Tiemann reaction gives salicylaldehyde.",
        "Example: phenol + 3Br2(aq) -> 2,4,6-tribromophenol (white precipitate) + 3HBr; phenol dissolves in NaOH but not in NaHCO3.",
        "C6H5OH + NaOH -> C6H5ONa + H2O; phenoxide + CO2 -> salicylate",
        "Do not treat phenol as an ordinary alcohol; resonance changes both its acidity and the reactivity of its aromatic ring.",
        "Phenol chemistry produces antiseptics, resins, dyes, aspirin precursors and polymer materials.",
        "Kolbe reaction", "Reimer-Tiemann reaction", "Electrophilic substitution"
    ),
    boardLesson(
        "Oxygen and Nitrogen Compounds", "Preparation and reactions of ethers",
        "Ethers are prepared by dehydration of alcohols or Williamson synthesis and generally react only under strong cleavage conditions.",
        "Williamson synthesis is an SN2 reaction between an alkoxide and a primary alkyl halide; tertiary halides tend to eliminate. Symmetrical ethers can form by intermolecular dehydration of primary alcohols near 413 K. Hot concentrated HI or HBr cleaves C-O bonds, with aryl-alkyl ethers cleaving on the alkyl side.",
        "Example: C2H5ONa + CH3I -> C2H5OCH3 + NaI. Anisole + HI gives phenol and CH3I rather than iodobenzene.",
        "RONa + R'X -> ROR' + NaX; ROR' + HI -> RI + R'OH",
        "Do not choose a tertiary haloalkane for Williamson synthesis or predict cleavage of the aryl C-O bond in anisole under ordinary HI treatment.",
        "Ethers are solvents, anaesthetic-related compounds, fuel additives and intermediates in organic synthesis.",
        "Williamson synthesis", "SN2", "Ether cleavage"
    ),
    boardLesson(
        "Oxygen and Nitrogen Compounds", "Preparation and reactions of aldehydes and ketones",
        "Carbonyl compounds are prepared by controlled oxidation or other functional-group conversions and undergo nucleophilic addition, reduction, oxidation and carbon-carbon bond-forming condensations.",
        "Primary alcohols give aldehydes and secondary alcohols give ketones. Aldehydes oxidise readily with Tollens' or Fehling's reagent; ketones usually resist. Both add HCN and bisulphite, reduce to alcohols, and compounds with alpha-hydrogen undergo aldol condensation. Carbonyls lacking alpha-hydrogen may undergo Cannizzaro disproportionation.",
        "Example: 2CH3CHO --dilute NaOH--> CH3CH(OH)CH2CHO, which on heating loses water to form crotonaldehyde. Ethanal also gives a silver mirror with Tollens' reagent.",
        "RCHO + [O] -> RCOOH; R2CO + HCN -> R2C(OH)CN",
        "Do not use Tollens' oxidation as a universal ketone test, and check for alpha-hydrogen before choosing aldol versus Cannizzaro reaction.",
        "Carbonyl chemistry is central to flavours, fragrances, pharmaceuticals, resins and carbon-carbon bond formation.",
        "Nucleophilic addition", "Aldol condensation", "Cannizzaro reaction"
    ),
    boardLesson(
        "Oxygen and Nitrogen Compounds", "Preparation and reactions of carboxylic acids",
        "Carboxylic acids form by oxidation or hydrolysis and react through acidity and nucleophilic acyl substitution to make salts, esters, acyl halides and amides.",
        "Oxidation of primary alcohols or aldehydes and hydrolysis of nitriles produce acids. Resonance stabilises carboxylate ions. Acids react with bicarbonate releasing CO2, esterify alcohols reversibly, and decarboxylate as sodium salts with soda lime. Electron-withdrawing substituents increase acidity.",
        "Example: CH3COOH + NaHCO3 -> CH3COONa + CO2 + H2O. CH3COOH + C2H5OH <=> CH3COOC2H5 + H2O with H+ and heat.",
        "RCOOH + ROH <=> RCOOR + H2O; RCOONa + NaOH -> RH + Na2CO3",
        "Do not confuse alcohol acidity with carboxylic-acid acidity or forget that Fischer esterification is an equilibrium requiring acid catalyst.",
        "Carboxylic-acid derivatives occur in medicines, polymers, food preservatives, fats, soaps and fragrances.",
        "Acidity", "Esterification", "Decarboxylation"
    ),
    boardLesson(
        "Oxygen and Nitrogen Compounds", "Preparation and reactions of amines",
        "Amines are prepared by reduction, ammonolysis, Gabriel synthesis or Hofmann bromamide degradation and react as bases and nucleophiles.",
        "Nitro compounds, nitriles and amides can be reduced to amines. Gabriel synthesis gives pure primary alkyl amines; Hofmann degradation gives a primary amine with one fewer carbon. Basicity depends on electron donation, resonance, solvation and steric effects. Primary amines react with nitrous acid differently from aromatic primary amines.",
        "Example: CH3CONH2 + Br2 + 4NaOH -> CH3NH2 + 2NaBr + Na2CO3 + 2H2O. Aniline is less basic than methylamine because its lone pair is delocalised into the ring.",
        "RCONH2 + Br2 + 4NaOH -> RNH2 + 2NaBr + Na2CO3 + 2H2O",
        "Do not rank amine basicity from inductive effect alone; resonance and aqueous solvation can reverse a simple gas-phase prediction.",
        "Amines are building blocks for medicines, dyes, surfactants, polymers and biological molecules.",
        "Basicity", "Hofmann degradation", "Gabriel synthesis"
    ),
    boardLesson(
        "Oxygen and Nitrogen Compounds", "Preparation and reactions of diazonium salts",
        "Arenediazonium salts are prepared from primary aromatic amines at low temperature and replace the diazonium group by many substituents or form azo dyes.",
        "Aniline is diazotised using NaNO2/HCl at 273-278 K because diazonium salts decompose when warm. Sandmeyer and Gattermann reactions introduce Cl, Br or CN; KI introduces I, warm water gives phenol, hypophosphorous acid gives H, and coupling with activated rings gives coloured azo compounds.",
        "Example: C6H5NH2 + NaNO2 + 2HCl at 273-278 K -> C6H5N2+Cl- + NaCl + 2H2O; coupling with phenol in alkaline solution gives an orange azo dye.",
        "ArNH2 + HNO2 + H+ -> ArN2+ + 2H2O; ArN2+ + CuX -> ArX + N2",
        "Do not prepare or store arenediazonium salts at room temperature; maintain 273-278 K and distinguish diazotisation from azo coupling.",
        "Diazonium chemistry connects aromatic amines with dyes, indicators, pharmaceuticals and diverse substituted aromatic compounds.",
        "Diazotisation", "Sandmeyer reaction", "Azo coupling"
    ),

    boardLesson(
        "Biomolecules and Polymers", "Vitamins",
        "Vitamins are organic micronutrients required in small amounts; water-soluble B and C vitamins differ in storage and deficiency patterns from fat-soluble A, D, E and K.",
        "Water-soluble vitamins are generally not stored extensively and excess is excreted, while fat-soluble vitamins can accumulate. Key board associations include A with vision, B1 with carbohydrate metabolism, C with collagen, D with calcium balance, E with antioxidant protection and K with blood clotting. Deficiency and source must be paired carefully.",
        "Example: prolonged vitamin C deficiency impairs collagen formation and causes scurvy; vitamin D deficiency reduces mineralisation and causes rickets in children.",
        "water soluble: B, C; fat soluble: A, D, E, K",
        "Do not classify every vitamin as an energy source; vitamins mainly support biochemical processes and excessive fat-soluble vitamin intake can be harmful.",
        "Vitamin chemistry guides nutrition, food fortification, clinical diagnosis and stability of pharmaceutical formulations.",
        "Coenzymes", "Deficiency diseases", "Nutrition"
    ),
    boardLesson(
        "Biomolecules and Polymers", "Hormones",
        "Hormones are chemical messengers released in small amounts that coordinate metabolism, growth, reproduction and internal balance through specific receptors.",
        "Peptide hormones such as insulin bind cell-surface receptors, while steroid hormones derived from cholesterol cross membranes and regulate gene expression through intracellular receptors. Thyroxine is an iodinated amino-acid derivative. Hormone action depends on concentration, receptor selectivity, feedback and controlled secretion.",
        "Example: after a meal, increased blood glucose stimulates insulin release. Insulin promotes glucose uptake and storage, lowering blood glucose; falling glucose then reduces the stimulus through negative feedback.",
        "signal + receptor -> cellular response; endocrine control commonly uses negative feedback",
        "Do not treat hormones as enzymes or assume all hormones share one chemical structure and one receptor location.",
        "Hormone chemistry supports diabetes care, thyroid diagnosis, reproductive medicine and understanding of endocrine-disrupting chemicals.",
        "Receptors", "Feedback regulation", "Steroid and peptide hormones"
    )
)
