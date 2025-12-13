package dev.xeonkryptos.xeonrobotframeworkplugin.completion

import com.intellij.codeInsight.completion.PrefixMatcher

class RobotPrefixMatcher(prefix: String) : PrefixMatcher(prefix) {

    private val normalizedPrefix = normalize(prefix)

    override fun prefixMatches(name: String): Boolean {
        if (prefix.isEmpty()) return true
        val normalizedName = normalize(name)
        return normalizedName.startsWith(normalizedPrefix, ignoreCase = true)
    }

    override fun cloneWithPrefix(prefix: String): PrefixMatcher {
        return RobotPrefixMatcher(prefix)
    }

    private fun normalize(text: String): String {
        return text.replace(" ", "").replace("_", "")
    }
}
