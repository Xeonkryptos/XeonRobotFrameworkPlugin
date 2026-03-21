package dev.xeonkryptos.xeonrobotframeworkplugin.completion

import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotUtil

class RobotPrefixMatcher(prefix: String) : CamelHumpMatcher(prefix, true) {
    private val modifiedPrefixCamelHumpMatcher: CamelHumpMatcher by lazy { CamelHumpMatcher(RobotUtil.normalizeRobotIdentifier(myPrefix), isCaseSensitive) }

    override fun prefixMatches(name: String): Boolean {
        if (super.prefixMatches(name)) return true
        return modifiedPrefixCamelHumpMatcher.prefixMatches(name)
    }

    override fun cloneWithPrefix(prefix: String): PrefixMatcher {
        if (prefix == myPrefix) return this
        return RobotPrefixMatcher(prefix)
    }

    override fun matchingDegree(string: String?): Int {
        return if (string == null) 0
        else if (super.prefixMatches(string)) super.matchingDegree(string)
        else modifiedPrefixCamelHumpMatcher.matchingDegree(string)
    }
}
