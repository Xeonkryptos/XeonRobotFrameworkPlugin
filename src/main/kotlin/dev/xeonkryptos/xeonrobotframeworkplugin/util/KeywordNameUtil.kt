package dev.xeonkryptos.xeonrobotframeworkplugin.util

object KeywordNameUtil {

    @JvmStatic
    fun normalizeKeywordName(name: String): String = name.lowercase().replace(Regex("[_\\s]+"), " ").trim()
}
