package dev.xeonkryptos.xeonrobotframeworkplugin.inspections

import com.intellij.codeInsight.daemon.impl.actions.AbstractBatchSuppressByNoInspectionCommentFix
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiParserFacade
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElement

open class RobotSuppressInspectionFix(id: String, text: @IntentionName String, private vararg val containerClass: Class<out RobotElement?>) :
    AbstractBatchSuppressByNoInspectionCommentFix(id, false) {

    init {
        this.text = text
    }

    override fun getContainer(context: PsiElement?): PsiElement? = PsiTreeUtil.getParentOfType(context, *containerClass)

    override fun createSuppression(project: Project, element: PsiElement, container: PsiElement) {
        var prevSibling = container.prevSibling
        if (prevSibling == null) {
            val parent = container.parent
            if (parent != null) {
                prevSibling = parent.prevSibling
            }
        }
        val indentation = if (prevSibling is PsiWhiteSpace) prevSibling.text.replace("\n", "") else ""

        super.createSuppression(project, element, container)

        val whitespace = PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\n${indentation}")
        container.parent.addBefore(whitespace, container)
    }
}
