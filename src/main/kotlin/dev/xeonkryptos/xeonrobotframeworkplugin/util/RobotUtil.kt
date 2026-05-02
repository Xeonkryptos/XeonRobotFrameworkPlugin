package dev.xeonkryptos.xeonrobotframeworkplugin.util

object RobotUtil {

    private val identifierNormalizerRegex = Regex("[_\\s]+")

    @JvmStatic
    fun normalizeRobotIdentifier(identifier: String?): String? {
        if (identifier == null) return null
        return identifier.lowercase().replace(identifierNormalizerRegex, "")
    }
}
