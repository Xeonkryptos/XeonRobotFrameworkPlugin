package dev.xeonkryptos.xeonrobotframeworkplugin.completion

import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotUtil

class RobotPrefixMatcher(prefix: String) : CamelHumpMatcher(prefix, true) {

    private val normalizedPrefix: String by lazy { RobotUtil.normalizeRobotIdentifier(myPrefix) ?: "" }

    override fun prefixMatches(name: String): Boolean = super.prefixMatches(name) || name.startsWith(normalizedPrefix)

    override fun cloneWithPrefix(prefix: String): PrefixMatcher = if (prefix == myPrefix) this else RobotPrefixMatcher(prefix)

    override fun matchingDegree(string: String?): Int = if (string == null) 0
    else super.matchingDegree(string)
}
