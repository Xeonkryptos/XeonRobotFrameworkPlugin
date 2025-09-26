package dev.xeonkryptos.xeonrobotframeworkplugin.util

import java.util.regex.Pattern

object VariableNameUtil {

    private val VARIABLE_BASENAME_PATTERN: Pattern = "([\\p{Alnum}_\\s]+)".toPattern()

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
}
