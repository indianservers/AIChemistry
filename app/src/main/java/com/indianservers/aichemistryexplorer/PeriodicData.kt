package com.indianservers.aichemistryexplorer

import androidx.compose.ui.graphics.Color

private val periodicSymbols = """
H He Li Be B C N O F Ne Na Mg Al Si P S Cl Ar
K Ca Sc Ti V Cr Mn Fe Co Ni Cu Zn Ga Ge As Se Br Kr
Rb Sr Y Zr Nb Mo Tc Ru Rh Pd Ag Cd In Sn Sb Te I Xe
Cs Ba La Ce Pr Nd Pm Sm Eu Gd Tb Dy Ho Er Tm Yb Lu
Hf Ta W Re Os Ir Pt Au Hg Tl Pb Bi Po At Rn
Fr Ra Ac Th Pa U Np Pu Am Cm Bk Cf Es Fm Md No Lr
Rf Db Sg Bh Hs Mt Ds Rg Cn Nh Fl Mc Lv Ts Og
""".trimIndent().split(Regex("\\s+"))

private val names1to36 = listOf(
    "Hydrogen","Helium","Lithium","Beryllium","Boron","Carbon","Nitrogen","Oxygen","Fluorine","Neon",
    "Sodium","Magnesium","Aluminium","Silicon","Phosphorus","Sulfur","Chlorine","Argon",
    "Potassium","Calcium","Scandium","Titanium","Vanadium","Chromium","Manganese","Iron","Cobalt","Nickel",
    "Copper","Zinc","Gallium","Germanium","Arsenic","Selenium","Bromine","Krypton"
)

private val names37to71 = listOf(
    "Rubidium","Strontium","Yttrium","Zirconium","Niobium","Molybdenum","Technetium","Ruthenium","Rhodium",
    "Palladium","Silver","Cadmium","Indium","Tin","Antimony","Tellurium","Iodine","Xenon",
    "Caesium","Barium","Lanthanum","Cerium","Praseodymium","Neodymium","Promethium","Samarium","Europium",
    "Gadolinium","Terbium","Dysprosium","Holmium","Erbium","Thulium","Ytterbium","Lutetium"
)

private val names72to103 = listOf(
    "Hafnium","Tantalum","Tungsten","Rhenium","Osmium","Iridium","Platinum","Gold","Mercury","Thallium",
    "Lead","Bismuth","Polonium","Astatine","Radon","Francium","Radium","Actinium","Thorium","Protactinium",
    "Uranium","Neptunium","Plutonium","Americium","Curium","Berkelium","Californium","Einsteinium","Fermium",
    "Mendelevium","Nobelium","Lawrencium"
)

private val names104to118 = listOf(
    "Rutherfordium","Dubnium","Seaborgium","Bohrium","Hassium","Meitnerium","Darmstadtium","Roentgenium",
    "Copernicium","Nihonium","Flerovium","Moscovium","Livermorium","Tennessine","Oganesson"
)

private val periodicNames = names1to36 + names37to71 + names72to103 + names104to118

private val noble = setOf("He","Ne","Ar","Kr","Xe","Rn","Og")
private val halogens = setOf("F","Cl","Br","I","At","Ts")
private val nonmetals = setOf("H","C","N","O","P","S","Se")
private val metalloids = setOf("B","Si","Ge","As","Sb","Te","Po")
private val alkali = setOf("Li","Na","K","Rb","Cs","Fr")
private val alkaline = setOf("Be","Mg","Ca","Sr","Ba","Ra")
private val postTransition = setOf("Al","Ga","In","Sn","Tl","Pb","Bi","Nh","Fl","Mc","Lv")

internal fun periodicCategory(number: Int, symbol: String): String = when {
    number in 57..71 -> "Lanthanide"
    number in 89..103 -> "Actinide"
    symbol in noble -> "Noble gas"
    symbol in halogens -> "Halogen"
    symbol in nonmetals -> "Non-metal"
    symbol in metalloids -> "Metalloid"
    symbol in alkali -> "Alkali metal"
    symbol in alkaline -> "Alkaline earth"
    symbol in postTransition -> "Post-transition"
    else -> "Transition metal"
}

internal fun categoryColor(category: String): Color = when (category) {
    "Noble gas" -> Purple
    "Halogen", "Metalloid" -> Green
    "Non-metal" -> Cyan
    "Alkali metal" -> Color(0xFF4D8BFF)
    "Alkaline earth" -> Orange
    "Post-transition" -> Color(0xFFE34C9A)
    "Lanthanide" -> Color(0xFF8A6CFF)
    "Actinide" -> Color(0xFFB64DDB)
    else -> Color(0xFF1CC6A1)
}

internal fun completePeriodicTable(rich: List<Element>): List<Element> = periodicSymbols.mapIndexed { index, symbol ->
    val number = index + 1
    val facts = elementFacts(number)
    val category = periodicCategory(number, symbol)
    val preferredColor = rich.firstOrNull { it.number == number }?.color ?: categoryColor(category)
    Element(
        number = number,
        symbol = symbol,
        name = periodicNames[index],
        mass = facts.atomicMass,
        category = category,
        color = preferredColor,
        radius = factOrDash(facts.atomicRadiusPm, "pm (van der Waals)"),
        boiling = factOrDash(facts.boilingPointK, "K")
    )
}

internal val periodicRows: List<List<Int?>> = listOf(
    listOf(1) + List(16) { null } + listOf(2),
    listOf(3,4) + List(10) { null } + (5..10).toList(),
    listOf(11,12) + List(10) { null } + (13..18).toList(),
    (19..36).toList(),
    (37..54).toList(),
    listOf(55,56,57) + (72..86).toList(),
    listOf(87,88,89) + (104..118).toList()
).map { row -> row.map { it } }

internal val lanthanideRow = (58..71).toList()
internal val actinideRow = (90..103).toList()
