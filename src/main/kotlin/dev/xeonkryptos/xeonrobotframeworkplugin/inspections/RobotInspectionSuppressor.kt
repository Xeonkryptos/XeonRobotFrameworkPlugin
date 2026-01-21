package dev.xeonkryptos.xeonrobotframeworkplugin.inspections

import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.codeInspection.SuppressionUtil
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement

class RobotInspectionSuppressor : InspectionSuppressor {

    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        // @formatter:off
        return isSuppressedForParent(element, RobotExecutableStatement::class.java, toolId)
                || isSuppressedForParent(element, RobotVariableStatement::class.java, toolId)
                || isSuppressedForParent(element, RobotTestCaseStatement::class.java, toolId)
                || isSuppressedForParent(element, RobotTaskStatement::class.java, toolId)
                || isSuppressedForParent(element, RobotUserKeywordStatement::class.java, toolId)
                || isSuppressedForParent(element, RobotSection::class.java, toolId)
        // @formatter:on
    }

    private fun isSuppressedForParent(element: PsiElement, parentClass: Class<out RobotElement?>, suppressId: String): Boolean {
        val parent: RobotElement = PsiTreeUtil.getParentOfType(element, parentClass, false) ?: return false
        return isSuppressedForElement(parent, suppressId)
    }

    private fun isSuppressedForElement(stmt: RobotElement, suppressId: String): Boolean {
        var prevSibling = stmt.prevSibling
        if (prevSibling == null) {
            val parent = stmt.parent
            if (parent != null) {
                prevSibling = parent.prevSibling
            }
        }
        while (prevSibling is PsiComment || prevSibling is PsiWhiteSpace || prevSibling.elementType == RobotTypes.EOL) {
            if (prevSibling is PsiComment && isSuppressedInComment(prevSibling.text.substring(1).trim { it <= ' ' }, suppressId)) {
                return true
            }
            prevSibling = prevSibling.prevSibling
        }
        return false
    }

    private fun isSuppressedInComment(commentText: String, suppressId: String): Boolean {
        val m = NOINSPECTION_REGEX.matchEntire(commentText)
        return m != null && SuppressionUtil.isInspectionToolIdMentioned(m.groupValues[1], suppressId)
    }

    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<out SuppressQuickFix> {
        return arrayOf(
            RobotSuppressInspectionFix(
                toolId,
                RobotBundle.message("INSP.robot.suppressor.suppress.for.statement"),
                RobotExecutableStatement::class.java,
                RobotVariableStatement::class.java
            ),
            RobotSuppressInspectionFix(toolId, RobotBundle.message("INSP.robot.suppressor.suppress.for.user-keyword"), RobotUserKeywordStatement::class.java),
            RobotSuppressInspectionFix(toolId, RobotBundle.message("INSP.robot.suppressor.suppress.for.testcase"), RobotTestCaseStatement::class.java),
            RobotSuppressInspectionFix(toolId, RobotBundle.message("INSP.robot.suppressor.suppress.for.task"), RobotTaskStatement::class.java),
            RobotSuppressInspectionFix(toolId, RobotBundle.message("INSP.robot.suppressor.suppress.for.section"), RobotSection::class.java)
        )
    }
}

private val NOINSPECTION_REGEX = SuppressionUtil.COMMON_SUPPRESS_REGEXP.toRegex()
