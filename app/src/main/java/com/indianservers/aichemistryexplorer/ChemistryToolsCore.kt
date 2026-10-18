package com.indianservers.aichemistryexplorer

import kotlin.math.abs

internal data class DetailedBalanceResult(
    val balancedEquation: String,
    val coefficients: List<Int>,
    val steps: List<String>,
    val error: String? = null
)

private data class Fraction(val numerator: Long, val denominator: Long = 1) {
    init { require(denominator != 0L) }
    private val sign get() = if (denominator < 0) -1 else 1
    val normalized: Fraction get() {
        val divisor = gcd(abs(numerator), abs(denominator)).coerceAtLeast(1)
        return Fraction(sign * numerator / divisor, abs(denominator) / divisor)
    }
    operator fun plus(other: Fraction) = Fraction(numerator * other.denominator + other.numerator * denominator, denominator * other.denominator).normalized
    operator fun minus(other: Fraction) = Fraction(numerator * other.denominator - other.numerator * denominator, denominator * other.denominator).normalized
    operator fun times(other: Fraction) = Fraction(numerator * other.numerator, denominator * other.denominator).normalized
    operator fun div(other: Fraction) = Fraction(numerator * other.denominator, denominator * other.numerator).normalized
    operator fun unaryMinus() = Fraction(-numerator, denominator)
    fun isZero() = numerator == 0L
}

private tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) abs(a) else gcd(b, a % b)
private fun lcm(a: Long, b: Long): Long = abs(a / gcd(a, b) * b)

internal fun parseChemicalFormula(formula: String): Map<String, Int> {
    val clean = formula.trim().replace(" ", "").replace(Regex("""\^?[0-9]*[+-]$"""), "")
    require(clean.isNotBlank()) { "Enter a chemical formula." }
    val total = linkedMapOf<String, Int>()
    clean.split('·', '.').filter(String::isNotBlank).forEach { section ->
        val prefix = section.takeWhile(Char::isDigit)
        val multiplier = prefix.toIntOrNull() ?: 1
        val body = section.drop(prefix.length)
        val stack = mutableListOf(linkedMapOf<String, Int>())
        var index = 0
        while (index < body.length) {
            when {
                body[index] == '(' || body[index] == '[' -> {
                    stack += linkedMapOf<String, Int>()
                    index++
                }
                body[index] == ')' || body[index] == ']' -> {
                    require(stack.size > 1) { "Unmatched closing bracket in $formula." }
                    index++
                    val numberStart = index
                    while (index < body.length && body[index].isDigit()) index++
                    val count = body.substring(numberStart, index).toIntOrNull() ?: 1
                    val group = stack.removeAt(stack.lastIndex)
                    group.forEach { (element, amount) -> stack.last()[element] = (stack.last()[element] ?: 0) + amount * count }
                }
                body[index].isUpperCase() -> {
                    val symbolStart = index++
                    while (index < body.length && body[index].isLowerCase()) index++
                    val element = body.substring(symbolStart, index)
                    val numberStart = index
                    while (index < body.length && body[index].isDigit()) index++
                    val count = body.substring(numberStart, index).toIntOrNull() ?: 1
                    stack.last()[element] = (stack.last()[element] ?: 0) + count
                }
                else -> error("Unexpected '${body[index]}' in $formula.")
            }
        }
        require(stack.size == 1) { "Unclosed bracket in $formula." }
        stack.single().forEach { (element, amount) -> total[element] = (total[element] ?: 0) + amount * multiplier }
    }
    return total
}

internal fun balanceChemicalEquation(rawEquation: String): DetailedBalanceResult {
    return try {
        val normalized = rawEquation.replace("→", "->").replace("⇌", "->")
        val sides = when {
            "->" in normalized -> normalized.split("->", limit = 2)
            "=" in normalized -> normalized.split("=", limit = 2)
            else -> return DetailedBalanceResult("", emptyList(), emptyList(), "Use an arrow such as H2 + O2 -> H2O.")
        }
        val reactants = sides[0].split('+').map(String::trim).filter(String::isNotBlank)
        val products = sides[1].split('+').map(String::trim).filter(String::isNotBlank)
        require(reactants.isNotEmpty() && products.isNotEmpty()) { "Both sides need at least one substance." }
        val compounds = reactants + products
        val parsed = compounds.map(::parseChemicalFormula)
        val elements = parsed.flatMap { it.keys }.distinct()
        require(elements.all { element -> parsed.take(reactants.size).any { element in it } && parsed.drop(reactants.size).any { element in it } }) {
            "Every element must occur on both sides of the equation."
        }
        val matrix = elements.map { element ->
            compounds.indices.map { column ->
                val direction = if (column < reactants.size) 1 else -1
                Fraction((parsed[column][element] ?: 0).toLong() * direction)
            }.toMutableList()
        }.toMutableList()
        val pivotColumns = mutableListOf<Int>()
        var pivotRow = 0
        for (column in compounds.indices) {
            val candidate = (pivotRow until matrix.size).firstOrNull { !matrix[it][column].isZero() } ?: continue
            val temp = matrix[pivotRow]; matrix[pivotRow] = matrix[candidate]; matrix[candidate] = temp
            val pivot = matrix[pivotRow][column]
            matrix[pivotRow] = matrix[pivotRow].map { it / pivot }.toMutableList()
            matrix.indices.filter { it != pivotRow && !matrix[it][column].isZero() }.forEach { row ->
                val factor = matrix[row][column]
                matrix[row] = matrix[row].mapIndexed { c, value -> value - factor * matrix[pivotRow][c] }.toMutableList()
            }
            pivotColumns += column
            pivotRow++
            if (pivotRow == matrix.size) break
        }
        val freeColumns = compounds.indices.filterNot(pivotColumns::contains)
        require(freeColumns.isNotEmpty()) { "No non-zero balancing solution was found." }
        val solution = MutableList(compounds.size) { Fraction(0) }
        freeColumns.forEach { solution[it] = Fraction(1) }
        for (row in pivotColumns.indices.reversed()) {
            val pivotColumn = pivotColumns[row]
            var sum = Fraction(0)
            for (column in pivotColumn + 1 until compounds.size) sum += matrix[row][column] * solution[column]
            solution[pivotColumn] = -sum
        }
        var commonDenominator = 1L
        solution.forEach { commonDenominator = lcm(commonDenominator, it.normalized.denominator) }
        var integers = solution.map { (it.normalized.numerator * (commonDenominator / it.normalized.denominator)).toInt() }
        if (integers.firstOrNull { it != 0 }?.let { it < 0 } == true) integers = integers.map { -it }
        require(integers.all { it > 0 }) { "This equation needs a more advanced ionic or redox balancing mode." }
        val divisor = integers.map(Int::toLong).reduce(::gcd).toInt().coerceAtLeast(1)
        integers = integers.map { it / divisor }
        fun display(items: List<String>, offset: Int) = items.mapIndexed { index, compound ->
            val coefficient = integers[offset + index]
            if (coefficient == 1) compound else "$coefficient$compound"
        }.joinToString(" + ")
        val balanced = "${display(reactants, 0)} → ${display(products, reactants.size)}"
        val inventory = elements.joinToString("; ") { element ->
            val left = reactants.indices.sumOf { integers[it] * (parsed[it][element] ?: 0) }
            "$element = $left on each side"
        }
        DetailedBalanceResult(
            balanced,
            integers,
            listOf(
                "List the conserved elements: ${elements.joinToString()}.",
                "Assign coefficients ${compounds.indices.joinToString { ('a'.code + it).toChar().toString() }} to ${compounds.joinToString()}.",
                "Create one atom-conservation equation per element and solve the simultaneous ratios.",
                "Scale the ratio to the smallest whole numbers: ${integers.joinToString(" : ")}.",
                "Verify atom counts: $inventory."
            )
        )
    } catch (error: Exception) {
        DetailedBalanceResult("", emptyList(), emptyList(), error.message ?: "The equation could not be parsed.")
    }
}

internal data class IonChoice(val name: String, val symbol: String, val charge: Int, val polyatomic: Boolean = false)

internal val commonCations = listOf(
    IonChoice("Sodium", "Na", 1), IonChoice("Potassium", "K", 1), IonChoice("Ammonium", "NH4", 1, true),
    IonChoice("Magnesium", "Mg", 2), IonChoice("Calcium", "Ca", 2), IonChoice("Zinc", "Zn", 2),
    IonChoice("Aluminium", "Al", 3), IonChoice("Iron(II)", "Fe", 2), IonChoice("Iron(III)", "Fe", 3), IonChoice("Copper(II)", "Cu", 2)
)

internal val commonAnions = listOf(
    IonChoice("Chloride", "Cl", -1), IonChoice("Oxide", "O", -2), IonChoice("Sulfide", "S", -2),
    IonChoice("Hydroxide", "OH", -1, true), IonChoice("Nitrate", "NO3", -1, true), IonChoice("Carbonate", "CO3", -2, true),
    IonChoice("Sulfate", "SO4", -2, true), IonChoice("Phosphate", "PO4", -3, true)
)

internal data class FormulaBuildResult(val formula: String, val explanation: String)

internal fun buildIonicFormula(cation: IonChoice, anion: IonChoice): FormulaBuildResult {
    val divisor = gcd(abs(cation.charge).toLong(), abs(anion.charge).toLong()).toInt()
    val cationCount = abs(anion.charge) / divisor
    val anionCount = abs(cation.charge) / divisor
    fun part(ion: IonChoice, count: Int): String {
        val symbol = if (ion.polyatomic && count > 1) "(${ion.symbol})" else ion.symbol
        return symbol + if (count == 1) "" else count
    }
    return FormulaBuildResult(
        part(cation, cationCount) + part(anion, anionCount),
        "${cation.name} is ${chargeText(cation.charge)} and ${anion.name} is ${chargeText(anion.charge)}. " +
            "Use $cationCount cation${if (cationCount == 1) "" else "s"} and $anionCount anion${if (anionCount == 1) "" else "s"}; total charge is zero."
    )
}

private fun chargeText(charge: Int) = "${abs(charge)}${if (charge > 0) "+" else "−"}"

internal data class VseprResult(val stericNumber: Int, val electronGeometry: String, val molecularShape: String, val approximateAngle: String, val notation: String)

internal fun vseprShape(bondedDomains: Int, lonePairs: Int): VseprResult {
    require(bondedDomains in 1..6 && lonePairs in 0..3 && bondedDomains + lonePairs in 2..6)
    val steric = bondedDomains + lonePairs
    val electronGeometry = listOf("", "", "Linear", "Trigonal planar", "Tetrahedral", "Trigonal bipyramidal", "Octahedral")[steric]
    val shape = when (steric to lonePairs) {
        2 to 0 -> "Linear"; 3 to 0 -> "Trigonal planar"; 3 to 1 -> "Bent"
        4 to 0 -> "Tetrahedral"; 4 to 1 -> "Trigonal pyramidal"; 4 to 2 -> "Bent"
        5 to 0 -> "Trigonal bipyramidal"; 5 to 1 -> "Seesaw"; 5 to 2 -> "T-shaped"; 5 to 3 -> "Linear"
        6 to 0 -> "Octahedral"; 6 to 1 -> "Square pyramidal"; 6 to 2 -> "Square planar"
        else -> "Special case"
    }
    val angle = when (shape) {
        "Linear" -> "180°"; "Trigonal planar" -> "120°"; "Bent" -> if (steric == 3) "<120°" else "<109.5°"
        "Tetrahedral" -> "109.5°"; "Trigonal pyramidal" -> "≈107°"; "Trigonal bipyramidal" -> "90°, 120°, 180°"
        "Seesaw", "T-shaped" -> "<90°, <120°, <180°"; "Octahedral", "Square pyramidal", "Square planar" -> "90°, 180°"
        else -> "Depends on ligands"
    }
    return VseprResult(steric, electronGeometry, shape, angle, "AX${bondedDomains}${if (lonePairs == 0) "" else "E$lonePairs"}")
}

internal data class ReactionPrediction(val products: String, val type: String, val explanation: String)

internal fun predictReactionProducts(raw: String): ReactionPrediction? {
    val key = raw.lowercase().replace(" ", "").replace("→", "+")
    return when {
        "hcl" in key && "naoh" in key -> ReactionPrediction("NaCl + H₂O", "Neutralisation", "H⁺ combines with OH⁻ to form water; the remaining ions form sodium chloride.")
        "h2" in key && "o2" in key -> ReactionPrediction("2H₂O", "Combination / combustion", "Hydrogen burns in oxygen and the atoms combine in the smallest 2:1 molecular ratio.")
        "ch4" in key && "o2" in key -> ReactionPrediction("CO₂ + 2H₂O", "Complete combustion", "Excess oxygen oxidises carbon to carbon dioxide and hydrogen to water.")
        "caco3" in key && ("heat" in key || "δ" in key) -> ReactionPrediction("CaO + CO₂", "Thermal decomposition", "Heating a metal carbonate releases carbon dioxide and leaves the metal oxide.")
        "zn" in key && "hcl" in key -> ReactionPrediction("ZnCl₂ + H₂", "Single displacement", "Zinc is above hydrogen in the reactivity series, so it displaces hydrogen from acid.")
        "agno3" in key && "nacl" in key -> ReactionPrediction("AgCl↓ + NaNO₃", "Double displacement / precipitation", "Ag⁺ and Cl⁻ form insoluble silver chloride.")
        "fe" in key && "cuso4" in key -> ReactionPrediction("FeSO₄ + Cu", "Single displacement", "Iron is more reactive than copper and displaces Cu²⁺ from solution.")
        "na" in key && "h2o" in key -> ReactionPrediction("2NaOH + H₂", "Metal–water reaction", "Sodium forms the hydroxide and releases hydrogen gas.")
        else -> null
    }
}

internal data class SolubilityPoint(val temperature: Int, val gramsPer100gWater: Float)
internal data class SolubilitySeries(val name: String, val points: List<SolubilityPoint>)

internal val solubilitySeries = listOf(
    SolubilitySeries("KNO₃", listOf(0 to 13.3f, 20 to 31.6f, 40 to 63.9f, 60 to 110f, 80 to 169f, 100 to 246f).map { SolubilityPoint(it.first, it.second) }),
    SolubilitySeries("NaCl", listOf(0 to 35.7f, 20 to 36f, 40 to 36.6f, 60 to 37.3f, 80 to 38.4f, 100 to 39.8f).map { SolubilityPoint(it.first, it.second) }),
    SolubilitySeries("KCl", listOf(0 to 27.6f, 20 to 34.2f, 40 to 40f, 60 to 45.8f, 80 to 51.3f, 100 to 56.7f).map { SolubilityPoint(it.first, it.second) }),
    SolubilitySeries("NH₄Cl", listOf(0 to 29.4f, 20 to 37.2f, 40 to 45.8f, 60 to 55.3f, 80 to 65.6f, 100 to 77.3f).map { SolubilityPoint(it.first, it.second) })
)

internal fun interpolateSolubility(series: SolubilitySeries, temperature: Float): Float {
    val t = temperature.coerceIn(series.points.first().temperature.toFloat(), series.points.last().temperature.toFloat())
    val upper = series.points.firstOrNull { it.temperature >= t } ?: series.points.last()
    val lower = series.points.lastOrNull { it.temperature <= t } ?: series.points.first()
    if (upper.temperature == lower.temperature) return upper.gramsPer100gWater
    val fraction = (t - lower.temperature) / (upper.temperature - lower.temperature)
    return lower.gramsPer100gWater + fraction * (upper.gramsPer100gWater - lower.gramsPer100gWater)
}

internal data class TrendElement(val symbol: String, val name: String, val period: Int, val group: Int, val atomicRadius: Int, val electronegativity: Float?, val ionizationEnergy: Int)
internal val trendElements = listOf(
    TrendElement("Li", "Lithium", 2, 1, 167, .98f, 520), TrendElement("Be", "Beryllium", 2, 2, 112, 1.57f, 900),
    TrendElement("B", "Boron", 2, 13, 87, 2.04f, 801), TrendElement("C", "Carbon", 2, 14, 67, 2.55f, 1086),
    TrendElement("N", "Nitrogen", 2, 15, 56, 3.04f, 1402), TrendElement("O", "Oxygen", 2, 16, 48, 3.44f, 1314),
    TrendElement("F", "Fluorine", 2, 17, 42, 3.98f, 1681), TrendElement("Na", "Sodium", 3, 1, 190, .93f, 496),
    TrendElement("Mg", "Magnesium", 3, 2, 145, 1.31f, 738), TrendElement("Al", "Aluminium", 3, 13, 118, 1.61f, 578),
    TrendElement("Si", "Silicon", 3, 14, 111, 1.90f, 787), TrendElement("P", "Phosphorus", 3, 15, 98, 2.19f, 1012),
    TrendElement("S", "Sulfur", 3, 16, 88, 2.58f, 1000), TrendElement("Cl", "Chlorine", 3, 17, 79, 3.16f, 1251)
)

internal fun significantFigures(raw: String): Int? {
    val cleaned = raw.trim().lowercase().substringBefore('e').removePrefix("+").removePrefix("-")
    if (cleaned.toDoubleOrNull() == null) return null
    val digits = cleaned.filter(Char::isDigit)
    if (digits.all { it == '0' }) return if ('.' in cleaned) digits.length - digits.indexOfFirst { it == '0' } else 1
    val firstNonZero = digits.indexOfFirst { it != '0' }
    var significant = digits.length - firstNonZero
    if ('.' !in cleaned) significant -= digits.drop(firstNonZero).takeLastWhile { it == '0' }.length
    return significant.coerceAtLeast(1)
}

internal data class UnitConversion(val value: Double, val unit: String)

internal fun convertUnit(value: Double, from: String, to: String): UnitConversion? {
    val factors = mapOf(
        "g" to ("mass" to 1.0), "kg" to ("mass" to 1000.0), "mg" to ("mass" to .001),
        "mol" to ("amount" to 1.0), "mmol" to ("amount" to .001),
        "l" to ("volume" to 1.0), "ml" to ("volume" to .001), "cm3" to ("volume" to .001),
        "pa" to ("pressure" to 1.0), "kpa" to ("pressure" to 1000.0), "atm" to ("pressure" to 101325.0)
    )
    val source = factors[from.lowercase()] ?: return null
    val target = factors[to.lowercase()] ?: return null
    if (source.first != target.first) return null
    return UnitConversion(value * source.second / target.second, to)
}

internal fun approximatelyEqual(student: Double, expected: Double, tolerance: Double = 0.005): Boolean {
    if (expected == 0.0) return abs(student) < tolerance
    return abs(student - expected) / abs(expected) <= tolerance
}

internal data class NomenclatureQuestion(val structure: String, val answer: String, val options: List<String>, val explanation: String)
internal val nomenclatureQuestions = listOf(
    NomenclatureQuestion("CH₃–CH₂–CH₃", "Propane", listOf("Propane", "Propene", "Ethane", "Propanol"), "Three carbons and only single bonds give prop- + -ane."),
    NomenclatureQuestion("CH₃–CH₂–OH", "Ethanol", listOf("Methanol", "Ethanal", "Ethanol", "Ethanoic acid"), "Two carbons give eth- and the hydroxyl group gives -anol."),
    NomenclatureQuestion("CH₃–CH=CH₂", "Prop-1-ene", listOf("Propane", "Prop-1-ene", "Prop-1-yne", "Propanone"), "Number from the end nearest the double bond, placing it at carbon 1."),
    NomenclatureQuestion("CH₃–CO–CH₃", "Propanone", listOf("Propanal", "Propanone", "Propanol", "Propanoic acid"), "A three-carbon chain with an internal carbonyl is a ketone."),
    NomenclatureQuestion("CH₃–CH(CH₃)–CH₃", "2-Methylpropane", listOf("Butane", "2-Methylpropane", "2-Methylbutane", "Propane"), "The longest chain has three carbons with a methyl substituent on carbon 2."),
    NomenclatureQuestion("CH₃–CH₂–COOH", "Propanoic acid", listOf("Propanal", "Propanone", "Propanoic acid", "Ethanoic acid"), "The carboxyl carbon belongs to the three-carbon parent chain; use -oic acid.")
)

internal data class LewisSummary(val totalValenceElectrons: Int, val bondingElectrons: Int, val remainingElectrons: Int, val formalGuidance: String)

internal fun lewisSummary(central: String, ligand: String, ligandCount: Int, bondOrder: Int, charge: Int): LewisSummary {
    val valence = mapOf("H" to 1, "C" to 4, "N" to 5, "O" to 6, "F" to 7, "P" to 5, "S" to 6, "Cl" to 7)
    val total = (valence[central] ?: 0) + ligandCount * (valence[ligand] ?: 0) - charge
    val bonding = ligandCount * bondOrder * 2
    val remaining = (total - bonding).coerceAtLeast(0)
    return LewisSummary(total, bonding, remaining, "Place remaining electrons as lone pairs, complete terminal octets first, then minimise formal charges.")
}

internal data class StoichiometryResult(val targetMoles: Double, val explanation: String)

internal fun stoichiometricMoles(knownMoles: Double, knownCoefficient: Int, targetCoefficient: Int): StoichiometryResult {
    require(knownMoles >= 0 && knownCoefficient > 0 && targetCoefficient > 0)
    val result = knownMoles * targetCoefficient / knownCoefficient
    return StoichiometryResult(result, "$knownMoles mol × ($targetCoefficient ÷ $knownCoefficient) = ${"%.4g".format(result)} mol")
}
