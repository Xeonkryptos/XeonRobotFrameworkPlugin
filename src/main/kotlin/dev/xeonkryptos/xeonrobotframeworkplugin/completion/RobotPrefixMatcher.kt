package dev.xeonkryptos.xeonrobotframeworkplugin.completion

import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher

class RobotPrefixMatcher(prefix: String) : CamelHumpMatcher(prefix, true) {

    override fun prefixMatches(name: String): Boolean {
        if (prefix.isEmpty()) return true
        return super.prefixMatches(normalize(name))
    }

    override fun cloneWithPrefix(prefix: String): PrefixMatcher {
        if (prefix == myPrefix) return this
        return RobotPrefixMatcher(prefix)
    }

    private fun normalize(text: String): String {
        return text.replace(" ", "").replace("_", "")
    }
}
