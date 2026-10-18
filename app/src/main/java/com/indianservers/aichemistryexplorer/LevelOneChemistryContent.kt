package com.indianservers.aichemistryexplorer

/**
 * Grade 6-8 content is kept in one reusable catalogue so the curriculum,
 * search, lesson pages, FAQs and knowledge checks all show the same explanation.
 */
internal data class LevelOneConceptContent(
    val summary: String,
    val explanation: String,
    val workedExample: String,
    val keyRelationship: String,
    val commonMistake: String,
    val application: String,
    val connections: List<String>
) {
    fun theory(moduleTitle: String): String = "$summary\n\n$explanation"

    val learningSteps: List<String>
        get() = (workedExample.split(Regex("(?<=[.!?])\\s+")) + keyRelationship + commonMistake)
            .filter { it.isNotBlank() }.distinct()

}

private fun levelOne(
    summary: String,
    explanation: String,
    example: String,
    relationship: String,
    mistake: String,
    application: String,
    vararg connections: String
): LevelOneConceptContent {
    return LevelOneConceptContent(
        summary,
        explanation,
        example,
        relationship,
        mistake,
        application,
        connections.toList()
    )
}

internal val levelOneModuleIcons = mapOf(
    "Matter Around Us" to "MAT",
    "Measurement in Chemistry" to "SI",
    "Atoms" to "ATOM",
    "Molecules" to "MOL",
    "Periodic Table" to "PT",
    "Chemical Reactions" to "RXN"
)

internal val levelOneConceptContent: Map<String, LevelOneConceptContent> = mapOf(
    "What is matter?" to levelOne(
        "Matter is anything that has mass and occupies space.",
        "Matter is made of tiny particles. Air, water, a book and your body are matter; light and sound are forms of energy and are not matter.",
        "Example: an inflated football has more mass than the same football when empty because the added air is matter and occupies space.",
        "matter -> has mass + occupies space",
        "Do not say that only things we can see are matter. Air and dissolved substances are matter too.",
        "Recognizing matter helps us choose ways to store, measure, separate and safely use materials.",
        "Particle model of matter", "Mass and volume", "States of matter"
    ),
    "States of matter" to levelOne(
        "A state of matter is a physical form determined by particle arrangement, motion and attraction.",
        "The familiar states are solid, liquid and gas. Plasma occurs at very high energy, while a Bose-Einstein condensate forms near absolute zero.",
        "Example: ice, liquid water and water vapour are the same substance in three states; heating changes particle motion without changing H2O into a new substance.",
        "heating or cooling <-> change in particle energy <-> change of state",
        "A state change does not automatically create a new substance.",
        "Controlling states of matter is essential in refrigeration, cooking, metal casting and gas storage.",
        "Kinetic particle theory", "Phase changes", "Temperature"
    ),
    "Properties of solids" to levelOne(
        "Solids have a fixed shape and volume and are usually difficult to compress.",
        "Their particles are packed closely in fixed positions. The particles still vibrate, but strong attractions prevent them from flowing past one another.",
        "Example: a wooden block keeps its shape when moved from a desk to a box; pressing it does not noticeably reduce its volume.",
        "closely packed particles -> fixed shape and fixed volume",
        "Particles in a solid are not motionless; they vibrate about fixed positions.",
        "Engineers select solids for buildings, tools and devices by comparing hardness, strength and flexibility.",
        "Crystalline and amorphous solids", "Melting", "Material properties"
    ),
    "Liquids" to levelOne(
        "Liquids have a fixed volume but take the shape of their container.",
        "Liquid particles remain close together but can slide past one another, so liquids flow and are only slightly compressible.",
        "Example: 200 mL of water has the same volume in a cup or measuring cylinder, but its surface shape changes with the container.",
        "close mobile particles -> flow + fixed volume",
        "Do not describe a liquid as having no volume; it has no fixed shape.",
        "Liquid behavior matters in pumps, medicines, fuels, cooking and laboratory measurements.",
        "Viscosity", "Surface tension", "Evaporation"
    ),
    "Gases" to levelOne(
        "Gases have neither a fixed shape nor a fixed volume.",
        "Gas particles are far apart, move rapidly in random directions and have weak attractions, so a gas fills its container and is easily compressed.",
        "Example: perfume molecules spread through a room by diffusion, showing that gas particles move and mix.",
        "far-apart fast particles -> diffusion + high compressibility",
        "A gas does not have zero mass; its low density simply makes its mass less obvious.",
        "Gas behavior is used in breathing equipment, tyres, refrigeration and weather forecasting.",
        "Diffusion", "Gas pressure", "Expansion"
    ),
    "Plasma" to levelOne(
        "Plasma is an ionized state of matter containing free electrons and charged particles.",
        "When a gas receives enough energy, electrons separate from atoms. Because it contains charges, plasma conducts electricity and responds to magnetic fields.",
        "Example: in a neon sign, electrical energy ionizes the gas; excited particles emit colored light as they lose energy.",
        "high-energy gas -> ions + free electrons",
        "Plasma is not ordinary hot gas; a significant part of it must be ionized.",
        "Plasma is used in fluorescent lamps, plasma cutting and semiconductor manufacturing, and it makes up stars.",
        "Ionization", "Electrical conductivity", "Stars"
    ),
    "Bose-Einstein Condensate" to levelOne(
        "A Bose-Einstein condensate is a state formed when certain atoms are cooled extremely close to absolute zero.",
        "At such low temperatures the atoms lose almost all thermal motion and many occupy the same lowest-energy quantum state, behaving like one collective matter wave.",
        "Example: dilute rubidium atoms cooled with lasers and magnetic traps can overlap into one condensate instead of behaving as separate warm atoms.",
        "temperature near 0 K -> many bosons share one quantum state",
        "A Bose-Einstein condensate is not simply a frozen solid; it is a quantum state produced under special laboratory conditions.",
        "Scientists use condensates to study quantum behavior, precision sensing and atom interferometry.",
        "Absolute zero", "Quantum states", "Matter waves"
    ),
    "Physical changes" to levelOne(
        "A physical change alters form, size or state without producing a new substance.",
        "The particles keep their chemical identity, although their spacing, arrangement or motion may change. Many physical changes are reversible.",
        "Example: melting ice changes solid H2O to liquid H2O; cooling can reverse the change.",
        "same substance before and after",
        "Reversibility alone is not a perfect test; focus on whether a new substance forms.",
        "Physical changes are used in cutting, grinding, melting, freezing and separating mixtures.",
        "Changes of state", "Dissolving", "Particle arrangement"
    ),
    "Chemical changes" to levelOne(
        "A chemical change rearranges atoms and forms one or more new substances.",
        "Chemical bonds break or form. Evidence can include a lasting color change, gas, precipitate, temperature change or light, but evidence must be interpreted carefully.",
        "Example: iron reacts with oxygen and water to form rust, whose properties differ from metallic iron.",
        "reactants -> new products",
        "Bubbles or heating do not always prove a reaction; boiling also makes bubbles but forms no new substance.",
        "Chemical changes produce medicines, fuels, foods, metals and useful materials.",
        "Chemical reactions", "Conservation of atoms", "Reaction evidence"
    ),
    "Classification of matter" to levelOne(
        "Matter is classified as a pure substance or a mixture according to composition.",
        "Pure substances have a fixed composition and include elements and compounds. Mixtures have variable composition and may be homogeneous or heterogeneous.",
        "Example: pure copper is an element, pure water is a compound, salt water is a homogeneous mixture and soil is heterogeneous.",
        "matter -> pure substances + mixtures",
        "Do not classify a material only by appearance; clear salt water is still a mixture.",
        "Classification guides material identification, quality control and choice of separation method.",
        "Elements", "Compounds", "Homogeneous and heterogeneous mixtures"
    ),
    "Mixtures" to levelOne(
        "A mixture contains two or more substances physically combined in variable proportions.",
        "Each component keeps its identity and can usually be separated using differences in physical properties such as particle size, boiling point or solubility.",
        "Example: salt water can be separated by evaporation or distillation because water is more volatile than salt.",
        "components retain identity; composition can vary",
        "A homogeneous mixture is not a pure substance even when its components cannot be seen.",
        "Separation of mixtures is used in water treatment, food processing, petroleum refining and recycling.",
        "Solutions", "Filtration", "Distillation"
    ),
    "Pure substances" to levelOne(
        "A pure substance contains one kind of element or compound and has a fixed composition.",
        "It has characteristic properties such as a definite melting point at a stated pressure. Physical methods cannot separate an element or compound into mixture components.",
        "Example: pure sodium chloride always contains sodium and chlorine in a fixed 1:1 ion ratio.",
        "fixed composition -> characteristic properties",
        "In everyday speech 'pure' can mean clean, but in chemistry it means one substance with fixed composition.",
        "Purity standards protect medicines, food, drinking water and laboratory results.",
        "Elements", "Compounds", "Purity tests"
    ),

    "SI Units" to levelOne(
        "SI units are internationally agreed units used to report measurements consistently.",
        "Important base units include metre (m), kilogram (kg), second (s), kelvin (K) and mole (mol). Prefixes such as milli-, centi- and kilo- show powers of ten.",
        "Example: 2500 mg = 2.5 g because 1000 mg equals 1 g.",
        "measured value = number x unit",
        "Never write a measured number without its unit.",
        "Shared units let scientists compare recipes, experiments and safety limits anywhere in the world.",
        "Base quantities", "Metric prefixes", "Unit conversion"
    ),
    "Derived Units" to levelOne(
        "Derived units are combinations of base units used for quantities such as area, volume, speed and density.",
        "Multiplication or division of measured quantities also combines their units. For example, volume uses cubic length and density uses mass per volume.",
        "Example: a box 2 m long, 3 m wide and 1 m high has volume 2 x 3 x 1 = 6 m3.",
        "density unit = kg/m3 or g/cm3",
        "Do not treat m2 and m3 as ordinary metres; they measure different quantities.",
        "Derived units describe laboratory glassware, flow, pressure, concentration and material properties.",
        "Area and volume", "Density", "Dimensional consistency"
    ),
    "Scientific notation" to levelOne(
        "Scientific notation writes a number as a coefficient from 1 to less than 10 multiplied by a power of ten.",
        "It makes extremely large and small chemical quantities easier to read, compare and calculate.",
        "Example: 0.00045 g = 4.5 x 10^-4 g, while 602000000000000000000000 = 6.02 x 10^23.",
        "N = a x 10^n, where 1 <= |a| < 10",
        "Moving the decimal left gives a positive exponent; moving it right gives a negative exponent.",
        "Chemists use scientific notation for atom sizes, particle counts and very small concentrations.",
        "Powers of ten", "Metric prefixes", "Calculator notation"
    ),
    "Significant figures" to levelOne(
        "Significant figures are the meaningful digits in a measured value.",
        "They communicate measurement resolution. Non-zero digits and zeros between them are significant; leading zeros only locate the decimal point.",
        "Example: 0.00450 has three significant figures: 4, 5 and the final measured zero.",
        "reported digits = certain digits + one estimated digit",
        "Do not count leading zeros as significant or invent extra digits after a calculation.",
        "Correct significant figures prevent laboratory results from appearing more exact than the instrument allows.",
        "Rounding", "Measurement resolution", "Uncertainty"
    ),
    "Precision" to levelOne(
        "Precision describes how closely repeated measurements agree with one another.",
        "A precise set has little spread, but it may still be far from the accepted value if a systematic problem affects every reading.",
        "Example: 9.81, 9.82 and 9.81 g are more precise than 9.3, 10.1 and 9.7 g.",
        "smaller spread -> greater precision",
        "Precision does not guarantee accuracy.",
        "Precision is important when checking whether a manufacturing or laboratory method gives consistent results.",
        "Repeat measurements", "Random error", "Accuracy"
    ),
    "Accuracy" to levelOne(
        "Accuracy describes how close a measurement is to the accepted or true value.",
        "Accuracy depends on correct calibration, procedure and freedom from systematic bias.",
        "Example: if a reference mass is 10.00 g, a reading of 10.01 g is more accurate than 9.70 g.",
        "smaller difference from accepted value -> greater accuracy",
        "One measurement can be accurate by chance; repeated measurements are needed to judge a method.",
        "Accurate measurements protect medicine doses, product quality and environmental limits.",
        "Calibration", "Systematic error", "Precision"
    ),
    "Errors" to levelOne(
        "Measurement error is the difference between a measured value and an accepted value.",
        "Random errors make readings scatter, systematic errors shift them in one direction, and gross errors come from mistakes such as misreading a scale.",
        "Example: a balance that reads 0.20 g when empty adds about 0.20 g to every uncorrected mass, creating systematic error.",
        "absolute error = |measured value - accepted value|",
        "Error does not always mean carelessness; every measurement has uncertainty.",
        "Understanding errors improves instrument design, calibration and confidence in scientific conclusions.",
        "Random and systematic error", "Uncertainty", "Percent error"
    ),
    "Density" to levelOne(
        "Density is the mass contained in a unit volume of a substance.",
        "Density is a characteristic property at a stated temperature and can help identify a material or predict whether it will float.",
        "Example: a sample of mass 54 g and volume 20 cm3 has density 54/20 = 2.7 g/cm3.",
        "density = mass / volume",
        "Do not confuse density with mass; a large object can have more mass but lower density.",
        "Density is used to identify materials, test purity and design ships, aircraft and storage systems.",
        "Mass", "Volume", "Floating and sinking"
    ),
    "Temperature scales" to levelOne(
        "Temperature measures how hot or cold a system is and is reported using scales such as Celsius and kelvin.",
        "Celsius sets water's freezing point near 0 degrees C at standard pressure. Kelvin begins at absolute zero and has no negative values.",
        "Example: 25 degrees C = 25 + 273.15 = 298.15 K.",
        "K = degrees C + 273.15",
        "A temperature of 0 degrees C is not zero particle motion; 0 K is absolute zero.",
        "Temperature control is essential in food storage, reactions, medicine and weather observations.",
        "Absolute zero", "Thermal energy", "Unit conversion"
    ),
    "Laboratory measurements" to levelOne(
        "Laboratory measurements use suitable instruments and careful reading techniques to obtain reliable data.",
        "Choose an instrument with the needed range and resolution, read liquid levels at eye height, record units immediately and repeat measurements when possible.",
        "Example: for a clear liquid in a measuring cylinder, read the bottom of the meniscus at eye level; a burette is more precise than a beaker.",
        "result = measured value + unit + appropriate uncertainty",
        "Do not estimate more decimal places than the instrument scale supports.",
        "Reliable measurements underpin formulation, quality control, medical tests and safe experiments.",
        "Meniscus", "Instrument resolution", "Repeat readings"
    ),

    "Discovery of atom" to levelOne(
        "The atomic idea developed from philosophical proposals into an evidence-based scientific model.",
        "Democritus suggested indivisible particles, but later experiments by Dalton, Thomson, Rutherford, Bohr and others revealed atoms, electrons, nuclei and quantized behavior.",
        "Example: cathode-ray experiments showed that all atoms contain the same negatively charged particle, the electron.",
        "new evidence -> revised atomic model",
        "No single scientist discovered the complete modern atom at once.",
        "Atomic models explain materials, electricity, spectra, medicines and nuclear energy.",
        "Scientific models", "Cathode rays", "Scattering experiments"
    ),
    "Dalton theory" to levelOne(
        "Dalton proposed that matter consists of atoms and that chemical reactions rearrange them in whole-number ratios.",
        "His theory explained conservation of mass and fixed composition. We now know atoms are divisible and isotopes of one element can have different masses.",
        "Example: water forms with hydrogen and oxygen atoms in a fixed ratio, consistent with atoms combining in simple whole numbers.",
        "compounds contain atoms in fixed whole-number ratios",
        "Dalton's claim that atoms are indivisible is historically important but not part of the modern model.",
        "Dalton's ideas provide the foundation for formulas, equations and stoichiometry.",
        "Conservation of mass", "Law of definite proportions", "Limits of models"
    ),
    "Thomson model" to levelOne(
        "Thomson's model pictured negative electrons embedded in a diffuse positively charged atom.",
        "It was proposed after the electron was discovered and explained overall electrical neutrality, but it had no central nucleus.",
        "Example: the model is often compared to raisins distributed through a pudding, with electrons spread inside positive matter.",
        "total positive charge + total negative charge = neutral atom",
        "Do not place a nucleus in Thomson's model; the nucleus was introduced after Rutherford's evidence.",
        "The model shows how scientific explanations change when new particles are discovered.",
        "Electron discovery", "Cathode rays", "Rutherford model"
    ),
    "Rutherford model" to levelOne(
        "Rutherford's model places nearly all mass and positive charge in a tiny central nucleus.",
        "Most alpha particles passed through gold foil, showing that atoms are mostly empty space; a few large deflections revealed a small, dense, positive center.",
        "Example: if positive charge were spread out as Thomson proposed, large-angle alpha-particle deflections would have been extremely unlikely.",
        "atom = tiny positive nucleus + mostly empty surrounding space",
        "The nucleus is tiny compared with the whole atom; drawings are not to scale.",
        "Scattering methods still reveal structures too small to see directly.",
        "Gold-foil experiment", "Nucleus", "Experimental inference"
    ),
    "Bohr model" to levelOne(
        "The Bohr model places electrons in fixed, quantized energy levels around the nucleus.",
        "An electron absorbs energy to move higher and emits a photon when it returns to a lower level. The model explains hydrogen's line spectrum well.",
        "Example: an excited hydrogen electron dropping to a lower level releases light with energy equal to the difference between the levels.",
        "photon energy = higher level energy - lower level energy",
        "Bohr paths are a useful school model, not literal planetary orbits for all atoms.",
        "Energy-level ideas help explain flame colors, spectra and electronic transitions.",
        "Line spectra", "Energy levels", "Photon emission"
    ),
    "Modern atomic model" to levelOne(
        "The modern atomic model describes electrons with probability regions called orbitals.",
        "Quantum mechanics does not assign an electron a fixed circular path. Orbitals show where an electron is likely to be found and have quantized energies and shapes.",
        "Example: the lowest-energy electron in hydrogen occupies a spherical 1s orbital, represented as a probability cloud.",
        "orbital = region of high probability of finding an electron",
        "An orbital is not a solid shell or a track followed by an electron.",
        "The modern model predicts bonding, spectra, semiconductor behavior and chemical reactivity.",
        "Orbitals", "Probability", "Quantum mechanics"
    ),
    "Subatomic particles" to levelOne(
        "Atoms contain protons, neutrons and electrons.",
        "Protons have charge +1 and neutrons have charge 0 in the nucleus; electrons have charge -1 and occupy the surrounding electron cloud. Protons and neutrons are much more massive than electrons.",
        "Example: a neutral carbon-12 atom has 6 protons, 6 neutrons and 6 electrons.",
        "neutral atom: protons = electrons",
        "Do not say neutrons have no mass; they have no electrical charge.",
        "Subatomic particles explain ions, isotopes, electricity and nuclear reactions.",
        "Nucleus", "Charge", "Relative mass"
    ),
    "Atomic number" to levelOne(
        "Atomic number is the number of protons in an atom's nucleus.",
        "It is written Z and uniquely identifies an element. Changing the number of electrons makes an ion, but changing the proton count makes a different element.",
        "Example: every oxygen atom has Z = 8, so it contains 8 protons.",
        "Z = number of protons",
        "Atomic number is not the number of neutrons or the total number of particles.",
        "Atomic number organizes the modern periodic table and identifies elements in analysis.",
        "Elements", "Protons", "Periodic table"
    ),
    "Mass number" to levelOne(
        "Mass number is the total number of protons and neutrons in one atom's nucleus.",
        "It is a whole number written A. Electrons contribute so little mass that they are not counted in the mass number.",
        "Example: sodium-23 has 11 protons and 12 neutrons, so A = 11 + 12 = 23.",
        "A = Z + N",
        "Mass number for one isotope is not the same as the decimal relative atomic mass on a periodic table.",
        "Mass number helps identify isotopes and calculate neutron numbers.",
        "Protons and neutrons", "Isotopes", "Nuclear notation"
    ),
    "Isotopes" to levelOne(
        "Isotopes are atoms of the same element with the same proton number but different neutron numbers.",
        "They have similar chemical behavior because they have the same electron arrangement, but their masses and nuclear stability can differ.",
        "Example: carbon-12 and carbon-14 both have 6 protons; they have 6 and 8 neutrons respectively.",
        "same Z, different A",
        "Isotopes are not different elements because their atomic numbers are the same.",
        "Isotopes are used in medical imaging, cancer treatment, dating and chemical tracing.",
        "Mass number", "Radioactivity", "Average atomic mass"
    ),
    "Isobars" to levelOne(
        "Isobars are atoms of different elements that have the same mass number.",
        "They have different proton numbers and therefore different chemical identities, even though the total number of nucleons is equal.",
        "Example: argon-40 and calcium-40 are isobars: both have A = 40, but their atomic numbers are 18 and 20.",
        "same A, different Z",
        "Do not confuse isobars with isotopes; isotopes belong to the same element.",
        "Comparing isobars helps organize nuclear transformations and nuclide notation.",
        "Nuclides", "Atomic number", "Mass number"
    ),
    "Isotones" to levelOne(
        "Isotones are atoms of different elements with the same number of neutrons.",
        "Their atomic and mass numbers differ, but N = A - Z gives the same result.",
        "Example: carbon-14 has 8 neutrons and nitrogen-15 also has 8, so they are isotones.",
        "same N, where N = A - Z",
        "Equal mass number does not make isotones; equal neutron number does.",
        "Isotones help scientists compare nuclear stability across different elements.",
        "Neutrons", "Nuclear stability", "Nuclide notation"
    ),
    "Isoelectronic species" to levelOne(
        "Isoelectronic species have the same total number of electrons.",
        "They may be atoms or ions of different elements. Their sizes can differ because nuclei with more protons attract the shared number of electrons more strongly.",
        "Example: Ne, Na+ and F- each have 10 electrons and are isoelectronic.",
        "electrons = atomic number - positive charge + negative charge",
        "Isoelectronic does not mean chemically identical; nuclear charge and element identity still differ.",
        "Electron counting helps predict ion sizes, bonding and stable ion formation.",
        "Ions", "Electron configuration", "Ionic radius"
    ),

    "Elements" to levelOne(
        "An element is a pure substance made of atoms with the same atomic number.",
        "An element cannot be broken into simpler substances by ordinary chemical reactions. It may exist as single atoms, molecules or networks.",
        "Example: oxygen gas is O2, but it is still an element because it contains only oxygen atoms.",
        "one element -> one proton number",
        "An element does not always consist of single, separate atoms.",
        "Elements are the basic chemical building materials used to make every substance.",
        "Atomic number", "Symbols", "Periodic table"
    ),
    "Compounds" to levelOne(
        "A compound is a pure substance containing two or more elements chemically bonded in a fixed ratio.",
        "A compound has properties different from its constituent elements and can be separated only by chemical change.",
        "Example: sodium chloride contains reactive sodium and toxic chlorine chemically combined into table salt.",
        "elements chemically combine in a fixed ratio -> compound",
        "A compound is not a mixture; its composition is fixed and its elements are bonded.",
        "Compounds include water, minerals, fuels, medicines and biological molecules.",
        "Chemical bonds", "Formulas", "Mixtures"
    ),
    "Molecules" to levelOne(
        "A molecule is an electrically neutral group of two or more atoms held together by covalent bonds.",
        "Molecules may contain one element, such as O2, or several elements, such as H2O. Ionic compounds form formula units rather than separate molecules.",
        "Example: one water molecule contains two hydrogen atoms covalently bonded to one oxygen atom.",
        "molecule = definite group of covalently bonded atoms",
        "Do not call every compound a molecule; sodium chloride is an ionic lattice.",
        "Molecular structure determines smell, color, solubility, medicine action and material behavior.",
        "Covalent bonding", "Compounds", "Formula units"
    ),
    "Atomicity" to levelOne(
        "Atomicity is the number of atoms present in one molecule of an element.",
        "Elements may be monatomic, diatomic or polyatomic in their common molecular form.",
        "Example: helium is monatomic (He), oxygen is diatomic (O2), and ozone is triatomic (O3).",
        "atomicity = atoms in one molecule of an element",
        "Atomicity is not the same as atomic number.",
        "Atomicity helps write correct formulas and balance equations involving elemental substances.",
        "Molecular formulas", "Diatomic elements", "Allotropes"
    ),
    "Molecular mass" to levelOne(
        "Molecular mass is the sum of the relative atomic masses of all atoms in a molecule.",
        "The molecular formula tells how many of each atom to include. Relative molecular mass is dimensionless; molecular mass is often expressed in unified atomic mass units.",
        "Example: H2O has molecular mass 2(1.008) + 16.00 = 18.016 u.",
        "molecular mass = sum of atomic masses in the molecular formula",
        "Remember to multiply each atomic mass by its subscript.",
        "Molecular mass is used to compare molecules and convert between mass and amount of substance.",
        "Atomic mass", "Molecular formula", "Molar mass"
    ),
    "Formula mass" to levelOne(
        "Formula mass is the sum of atomic masses in one formula unit of a substance.",
        "The term is especially useful for ionic compounds, which do not exist as separate molecules.",
        "Example: NaCl has formula mass 22.99 + 35.45 = 58.44 u per formula unit.",
        "formula mass = sum of atomic masses in one formula unit",
        "Do not describe an ionic lattice such as NaCl as having a molecular mass.",
        "Formula mass supports calculations for salts, minerals and ionic materials.",
        "Formula units", "Ionic compounds", "Molar mass"
    ),
    "Valency" to levelOne(
        "Valency is an atom's combining capacity, often related to electrons lost, gained or shared.",
        "For simple main-group compounds, valency helps predict how many bonds an atom forms or the charge of its common ion.",
        "Example: magnesium has valency 2 and chlorine has valency 1, giving MgCl2 so the combining capacities balance.",
        "total positive combining capacity = total negative combining capacity",
        "Valency is not always identical to oxidation state, especially for elements with variable behavior.",
        "Valency helps learners write formulas and understand common bonding patterns.",
        "Valence electrons", "Ion charge", "Chemical formulas"
    ),
    "Chemical formula" to levelOne(
        "A chemical formula uses element symbols and subscripts to show a substance's composition.",
        "Subscripts tell the relative number of atoms or ions; coefficients count whole particles and must not be confused with subscripts.",
        "Example: Al3+ and O2- combine in the smallest neutral ratio to give Al2O3.",
        "sum of ionic charges in a neutral formula = 0",
        "Do not change subscripts to balance a reaction; that would change the substances.",
        "Formulas provide a compact language for labels, equations, recipes and safety information.",
        "Element symbols", "Valency", "Formula units"
    ),

    "History" to levelOne(
        "The periodic table developed as scientists searched for repeating patterns among the elements.",
        "Early groupings by Dobereiner and Newlands led to Mendeleev's table; atomic-number evidence later produced the modern periodic law.",
        "Example: Newlands noticed that properties repeated roughly every eighth element when elements were ordered by mass, an early sign of periodicity.",
        "ordered elements -> repeating properties",
        "The periodic table was not invented in its final form by one person.",
        "The history shows how pattern, prediction and new evidence improve scientific classification.",
        "Triads", "Law of octaves", "Modern periodic law"
    ),
    "Mendeleev table" to levelOne(
        "Mendeleev arranged elements mainly by increasing atomic mass and recurring chemical properties.",
        "He left gaps for undiscovered elements and predicted their properties, giving his table strong scientific value despite some mass-order exceptions.",
        "Example: Mendeleev predicted an element below aluminium; later-discovered gallium closely matched his predicted properties.",
        "atomic mass order + similar properties in columns",
        "Mendeleev did not know atomic numbers or modern electron configurations.",
        "His successful predictions demonstrate how a classification model can guide discovery.",
        "Predicted elements", "Periodic patterns", "Limitations"
    ),
    "Modern periodic table" to levelOne(
        "The modern periodic table arranges elements by increasing atomic number.",
        "Repeating valence-electron patterns place chemically similar elements in columns and remove many anomalies from mass-based ordering.",
        "Example: argon has greater mass than potassium but comes first because their atomic numbers are 18 and 19.",
        "properties repeat periodically with atomic number",
        "Modern order follows proton number, not relative atomic mass.",
        "The table predicts bonding, ion formation, reactivity and material properties.",
        "Atomic number", "Electronic configuration", "Periodic law"
    ),
    "Groups" to levelOne(
        "Groups are the vertical columns of the periodic table.",
        "Main-group elements in one group have similar valence-electron patterns and therefore often show similar chemical properties.",
        "Example: lithium, sodium and potassium are Group 1 metals and commonly form +1 ions.",
        "same main group -> similar valence electrons and behavior",
        "Elements in a group are similar, not identical; reactivity can change down the group.",
        "Groups help predict ion charges, formulas and reaction patterns.",
        "Valence electrons", "Families", "Group trends"
    ),
    "Periods" to levelOne(
        "Periods are the horizontal rows of the periodic table.",
        "Elements in the same period have the same number of occupied main electron shells, while properties change gradually from left to right.",
        "Example: sodium through argon lie in Period 3 and have electrons occupying three main shells.",
        "period number -> number of occupied main shells",
        "Elements in the same period do not usually have similar chemical properties.",
        "Period position helps predict electron arrangement and trends across the table.",
        "Electron shells", "Across-period trends", "Atomic number"
    ),
    "Metals" to levelOne(
        "Metals are elements that typically conduct heat and electricity and are malleable, ductile and lustrous.",
        "Their valence electrons are relatively mobile, and metals commonly lose electrons to form positive ions.",
        "Example: copper conducts electrical current and can be drawn into wire, making it useful in cables.",
        "metal atom -> positive ion + electron(s)",
        "Not every metal is hard or solid at room temperature; mercury is liquid.",
        "Metals are used in structures, wiring, transport, tools, batteries and catalysts.",
        "Metallic bonding", "Cations", "Reactivity"
    ),
    "Nonmetals" to levelOne(
        "Nonmetals generally conduct poorly and, when solid, are often brittle rather than malleable.",
        "They may gain or share electrons and can exist as gases, solids or one liquid at room temperature.",
        "Example: oxygen is a gaseous nonmetal essential for respiration and combustion.",
        "nonmetal atoms often gain or share electrons",
        "Nonmetal does not mean unimportant or always gaseous.",
        "Nonmetals form water, air, fuels, fertilizers, medicines and living matter.",
        "Covalent bonding", "Anions", "Periodic position"
    ),
    "Metalloids" to levelOne(
        "Metalloids have a mixture of metallic and nonmetallic properties.",
        "They lie near the zigzag boundary of the periodic table and often behave as semiconductors whose conductivity can be controlled.",
        "Example: silicon is brittle like a nonmetal but conducts under controlled conditions, so it is used in computer chips.",
        "intermediate properties -> semiconductor behavior",
        "Metalloid membership can vary slightly between references; focus on the intermediate properties.",
        "Metalloids are important in electronics, solar cells, glass and flame-resistant materials.",
        "Semiconductors", "Periodic boundary", "Material properties"
    ),
    "Periodic trends" to levelOne(
        "Periodic trends are predictable changes in properties across periods and down groups.",
        "Atomic radius generally decreases across a period and increases down a group; metallic character shows the opposite broad pattern.",
        "Example: sodium is larger and more metallic than chlorine because both are in Period 3 but sodium lies farther left.",
        "across a period: radius generally decreases; down a group: radius generally increases",
        "Trends have exceptions and should not be treated as unbreakable rules.",
        "Trends help predict bonding, reactivity, ion formation and properties of unfamiliar elements.",
        "Atomic radius", "Ionization energy", "Metallic character"
    ),
    "Electronic configuration basics" to levelOne(
        "Electronic configuration describes how electrons are arranged in shells and subshells.",
        "For an introductory shell model, electrons fill lower-energy shells first; valence electrons in the outer shell strongly influence chemical behavior.",
        "Example: sodium has shell arrangement 2,8,1, so it tends to lose one valence electron and form Na+.",
        "neutral atom: total electrons = atomic number",
        "Shell capacity is not simply eight for every shell; the 2,8,8 pattern is an introductory model for early elements.",
        "Electron arrangements explain group similarities, ion charges and bonding.",
        "Valence electrons", "Shells and subshells", "Ion formation"
    ),

    "Combination" to levelOne(
        "A combination reaction forms one main product from two or more reactants.",
        "Atoms are rearranged into a more complex product while total atoms and mass remain conserved.",
        "Example: 2Mg + O2 -> 2MgO combines magnesium and oxygen to form magnesium oxide.",
        "A + B -> AB",
        "A reaction with several products is not a simple combination reaction.",
        "Combination reactions occur in material manufacture, corrosion and synthesis.",
        "Synthesis reactions", "Balancing equations", "Conservation of mass"
    ),
    "Decomposition" to levelOne(
        "A decomposition reaction breaks one compound into two or more simpler substances.",
        "Energy from heat, light or electricity is often needed to break bonds in the reactant.",
        "Example: CaCO3 -> CaO + CO2 when limestone is strongly heated.",
        "AB -> A + B",
        "Melting is not decomposition because it forms no new substance.",
        "Decomposition is used to make lime, extract substances and analyze compounds.",
        "Thermal decomposition", "Electrolysis", "Photodecomposition"
    ),
    "Displacement" to levelOne(
        "In a displacement reaction, a more reactive element replaces a less reactive element in a compound.",
        "The reactivity series predicts whether the replacement will occur and electron transfer is often involved.",
        "Example: Zn + CuSO4 -> ZnSO4 + Cu because zinc is more reactive than copper.",
        "A + BC -> AC + B",
        "A less reactive metal cannot normally displace a more reactive metal from its salt solution.",
        "Displacement reactions are used in metal extraction, batteries and reactivity tests.",
        "Reactivity series", "Redox", "Single replacement"
    ),
    "Double displacement" to levelOne(
        "A double-displacement reaction exchanges ions between two compounds.",
        "In solution, the reaction proceeds when an insoluble precipitate, gas or weakly ionized substance such as water forms.",
        "Example: AgNO3 + NaCl -> AgCl(s) + NaNO3 produces a white silver chloride precipitate.",
        "AB + CD -> AD + CB",
        "Swapping symbols on paper is not enough; there must be a chemical driving force.",
        "Double displacement is used in ion tests, water treatment and preparation of salts.",
        "Precipitation", "Solubility", "Ionic equations"
    ),
    "Neutralization" to levelOne(
        "Neutralization is the reaction of an acid with a base to form salt and water.",
        "Hydrogen ions from the acid combine with hydroxide ions from the base. The final pH is not always exactly 7 for every acid-base pair.",
        "Example: HCl + NaOH -> NaCl + H2O.",
        "H+ + OH- -> H2O",
        "Neutralization does not always mean the final solution is pH 7; strengths and amounts matter.",
        "Neutralization treats acidic soil, indigestion, spills and industrial wastewater.",
        "Acids and bases", "Salts", "Indicators"
    ),
    "Combustion" to levelOne(
        "Combustion is a rapid reaction with oxygen that releases energy, often as heat and light.",
        "Complete combustion of a hydrocarbon forms carbon dioxide and water; limited oxygen can produce carbon monoxide or soot.",
        "Example: CH4 + 2O2 -> CO2 + 2H2O releases energy in a clean-burning gas flame.",
        "fuel + oxygen -> oxides + energy",
        "Combustion is not simply 'burning with a flame'; some oxidation releases energy without a visible flame.",
        "Combustion powers cooking, engines and industry, while emission control reduces pollution.",
        "Fuels", "Complete and incomplete combustion", "Energy change"
    ),
    "Oxidation" to levelOne(
        "Oxidation is loss of electrons; at an introductory level it can also be recognized as gain of oxygen.",
        "Oxidation and reduction happen together because electrons lost by one species must be gained by another.",
        "Example: Mg -> Mg2+ + 2e- shows magnesium being oxidized during formation of magnesium oxide.",
        "oxidation = loss of electrons",
        "Oxidation does not always involve oxygen.",
        "Oxidation occurs in combustion, rusting, respiration, bleaching and batteries.",
        "Electron transfer", "Oxidation number", "Reduction"
    ),
    "Reduction" to levelOne(
        "Reduction is gain of electrons; it may also appear as removal of oxygen from a substance.",
        "A reducing process lowers the oxidation state of a species and must be paired with oxidation elsewhere.",
        "Example: Cu2+ + 2e- -> Cu shows copper(II) ions being reduced to copper metal.",
        "reduction = gain of electrons",
        "Reduction does not mean making a substance physically smaller.",
        "Reduction is central to metal extraction, electroplating, batteries and biological energy transfer.",
        "Electron transfer", "Oxidation number", "Oxidation"
    ),
    "Redox basics" to levelOne(
        "A redox reaction contains simultaneous oxidation and reduction.",
        "Track electron loss and gain or changes in oxidation number. The oxidizing agent gains electrons, while the reducing agent loses them.",
        "Example: Zn + Cu2+ -> Zn2+ + Cu; zinc is oxidized and copper(II) is reduced, with two electrons transferred.",
        "electrons lost = electrons gained",
        "Do not label only one half of a redox reaction; oxidation and reduction always occur together.",
        "Redox chemistry explains batteries, corrosion, respiration, photosynthesis and metal extraction.",
        "Half-equations", "Oxidizing and reducing agents", "Electron balance"
    )
)
