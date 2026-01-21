package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.openapi.util.TextRange
import java.util.regex.Pattern

object VariableNameUtil {

    private val VARIABLE_BASENAME_PATTERN: Pattern = "([\\p{L}\\p{N}_\\s]+)".toPattern()

    fun matchesVariableName(variableName: String?, variableNameToMatchVariants: Set<String>): Boolean {
        if (variableName == null) {
            return false
        }
        val variableNameVariants = computeVariableNameVariants(variableName)
        return variableNameVariants.intersect(variableNameToMatchVariants).isNotEmpty()
    }

    fun computeVariableNameVariants(variableName: String?): Set<String> {
        if (variableName == null) {
            return emptySet()
        }
        val variableNameVariants = mutableSetOf(variableName.lowercase().trim())
        val matcher = VARIABLE_BASENAME_PATTERN.matcher(variableName)
        if (matcher.find()) {
            val baseVariableName = matcher.group()
            variableNameVariants.add(baseVariableName.lowercase().trim())
            val reducedVariableName = baseVariableName.lowercase().replace(Regex("[_\\s]"), "")
            variableNameVariants.add(reducedVariableName)
        }
        return variableNameVariants
    }

    fun computeBaseVariableName(variableName: String): String {
        val matcher = VARIABLE_BASENAME_PATTERN.matcher(variableName)
        return if (matcher.find()) {
            matcher.group().trim()
        } else {
            variableName.trim()
        }
    }

    fun computeBaseVariableNameTextRange(variableName: String): TextRange {
        val matcher = VARIABLE_BASENAME_PATTERN.matcher(variableName)
        return if (matcher.find()) {
            val baseNameLength = matcher.group().trim().length
            TextRange(0, baseNameLength)
        } else {
            TextRange.create(0, variableName.trim().length)
        }
    }
}
