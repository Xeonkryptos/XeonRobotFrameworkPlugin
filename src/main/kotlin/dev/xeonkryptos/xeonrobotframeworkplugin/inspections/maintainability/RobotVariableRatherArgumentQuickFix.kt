package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLiteralConstantValue
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator

class RobotVariableRatherArgumentQuickFix(element: RobotLiteralConstantValue) : LocalQuickFixOnPsiElement(element) {

    override fun getText(): @IntentionName String = RobotBundle.message("intention.family.variable-rather-argument.name")

    override fun invoke(
        project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement
    ) {
        RobotElementGenerator.getInstance(project).createNewScalarVariable(startElement.text)?.let { robotVariable -> startElement.replace(robotVariable) }
    }

    override fun isAvailable(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement): Boolean =
        startElement is RobotLiteralConstantValue && startElement.parent is RobotPositionalArgument && ReservedVariable.entries.any {
            it.variable.contentEquals(startElement.text, true)
        }

    override fun getFamilyName(): @IntentionFamilyName String = RobotBundle.message("intention.family.variable-rather-argument.text")
}
