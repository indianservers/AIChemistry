package com.indianservers.aichemistryexplorer

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em

private val subscriptStyle = SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = .76.em)
private val superscriptStyle = SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = .76.em)

/**
 * Renders common chemistry and lightweight LaTeX-style notation without a WebView.
 * Supports H2SO4, Fe^3+, SO4^2-, x_{2}, 10^{-5}, orbital 3d5, arrows and \frac{a}{b}.
 */
internal fun chemistryNotation(source: String): AnnotatedString {
    val input = source
        .replace("\\rightleftharpoons", "⇌")
        .replace("\\leftrightarrow", "↔")
        .replace("\\rightarrow", "→")
        .replace("\\to", "→")
        .replace("\\Delta", "Δ")
        .replace("\\cdot", "·")
        .replace("\\times", "×")
        .replace("<=>", "⇌")
        .replace("<->", "↔")
        .replace("->", "→")
    val out = AnnotatedString.Builder()
    var index = 0

    fun appendStyled(value: String, style: SpanStyle) {
        if (value.isEmpty()) return
        val start = out.length
        out.append(value)
        out.addStyle(style, start, out.length)
    }

    fun bracedValue(start: Int): Pair<String, Int>? {
        if (start >= input.length) return null
        if (input[start] != '{') return input[start].toString() to (start + 1)
        val end = input.indexOf('}', start + 1)
        return if (end > start) input.substring(start + 1, end) to (end + 1) else null
    }

    while (index < input.length) {
        if (input.startsWith("\\frac", index)) {
            val numerator = bracedValue(index + 5)
            val denominator = numerator?.let { bracedValue(it.second) }
            if (numerator != null && denominator != null) {
                appendStyled(numerator.first, superscriptStyle)
                out.append("⁄")
                appendStyled(denominator.first, subscriptStyle)
                index = denominator.second
                continue
            }
        }

        val current = input[index]
        if ((current == '_' || current == '^') && index + 1 < input.length) {
            val parsed = bracedValue(index + 1)
            if (parsed != null) {
                var value = parsed.first
                var next = parsed.second
                if (current == '^' && next < input.length && input[next] in "+-−") {
                    value += input[next]
                    next++
                }
                appendStyled(value, if (current == '_') subscriptStyle else superscriptStyle)
                index = next
                continue
            }
        }

        if (current in '0'..'9' && index > 0) {
            val previous = input[index - 1]
            var end = index + 1
            while (end < input.length && input[end] in '0'..'9') end++
            val digits = input.substring(index, end)
            val followedByCharge = end < input.length && input[end] in "+-−"
            val orbitalOccupation = previous in "spdf" && index >= 2 && input[index - 2].isDigit()
            val hybridization = previous in "sp" && index >= 2 && input[index - 2].lowercaseChar() in 'a'..'z'
            val squaredOrCubedUnit = previous == 'm' && digits in setOf("2", "3")
            val formulaSubscript = previous.isLetter() || previous == ')'
            val protectedScientificName = previous == 'P' && digits == "450"
            when {
                protectedScientificName -> Unit
                followedByCharge -> {
                    appendStyled(digits + input[end], superscriptStyle)
                    index = end + 1
                    continue
                }
                orbitalOccupation || hybridization || squaredOrCubedUnit -> {
                    appendStyled(digits, superscriptStyle)
                    index = end
                    continue
                }
                formulaSubscript -> {
                    appendStyled(digits, subscriptStyle)
                    index = end
                    continue
                }
            }
        }

        if (current in "+-−" && index > 0 && !input[index - 1].isWhitespace() &&
            (index + 1 == input.length || input[index + 1].isWhitespace() || input[index + 1] in ",.;)")
        ) {
            appendStyled(current.toString(), superscriptStyle)
            index++
            continue
        }

        if (current == '/' && index > 0 && index + 1 < input.length &&
            input[index - 1] in '0'..'9' && input[index + 1] in '0'..'9'
        ) {
            out.append("⁄")
            index++
            continue
        }

        out.append(current)
        index++
    }
    return out.toAnnotatedString()
}

/**
 * App-wide text entry point. Keeping the Material Text signature means every
 * existing screen automatically receives chemistry-aware notation.
 */
@Composable
internal fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) {
    MaterialText(
        text = chemistryNotation(text),
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout ?: {},
        style = style
    )
}
