package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil
import java.util.*

class EmbeddedUserKeywordNamePatternVisitor @JvmOverloads constructor(private val normalize: Boolean = false) : RobotVisitor() {

    companion object {
        @JvmStatic
        fun isEmbeddedUserKeyword(userKeyword: RobotUserKeywordStatement): Boolean = userKeyword.userKeywordStatementId.variableDefinitionList.isNotEmpty()
    }

    private val embeddedKeywordPatternStringBuilder: StringBuilder = StringBuilder()

    private var visitingUserKeywordStatementId = false

    override fun visitUserKeywordStatementId(o: RobotUserKeywordStatementId) {
        visitingUserKeywordStatementId = true
        o.acceptChildren(this)
        visitingUserKeywordStatementId = false
    }

    override fun visitElement(element: PsiElement) {
        if (element.node.elementType === RobotTypes.USER_KEYWORD_NAME_PART) {
            val elementText = element.text
            val normalizedElementText = if (normalize) KeywordUtil.normalizeKeywordName(elementText) else elementText.lowercase(Locale.getDefault())
            embeddedKeywordPatternStringBuilder.append("\\Q").append(normalizedElementText).append("\\E")
        }
    }

    override fun visitWhiteSpace(space: PsiWhiteSpace) {
        if (visitingUserKeywordStatementId && !normalize) {
            val whitespaceText = space.text
            embeddedKeywordPatternStringBuilder.append(whitespaceText)
        }
    }

    override fun visitVariableDefinition(o: RobotVariableDefinition) {
        if (visitingUserKeywordStatementId) {
            // TODO: It is possible that users define their own custom regular expressions that should be used instead of the generic approach. Especially necessary to reduce possible conflicts.
            //  Those regular expressions needs to be extracted from the variable itself. For now, we simply do it by looking for a ':' in the variable name expression.
            //  A better approach is to take it from the variable itself. For that, the parsing logic needs to be extended as it is needed to support type checks and all the other features of the
            //  latest Robot Framework versions which added additional metadata after the ':' in variable names
            var regexp = ".*?"
            val variableContent = if (o.getVariableContent() != null) o.getVariableContent()!!.text else null
            if (variableContent != null && variableContent.contains(":")) {
                val startOfRegExp = variableContent.indexOf(":")
                regexp = variableContent.substring(startOfRegExp + 1)
            }
            embeddedKeywordPatternStringBuilder.append(regexp)
        }
    }

    fun getEmbeddedKeywordNamePattern(): String? = if (embeddedKeywordPatternStringBuilder.isNotEmpty()) embeddedKeywordPatternStringBuilder.toString() else null
}
