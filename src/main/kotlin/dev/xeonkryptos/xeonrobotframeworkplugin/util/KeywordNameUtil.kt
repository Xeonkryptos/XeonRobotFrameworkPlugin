package dev.xeonkryptos.xeonrobotframeworkplugin.util

object KeywordNameUtil {

    private val keywordNormalizerRegex = Regex("[_\\s]+")

    @JvmStatic
    fun normalizeKeywordName(name: String): String = name.lowercase().replace(keywordNormalizerRegex, "")
}
