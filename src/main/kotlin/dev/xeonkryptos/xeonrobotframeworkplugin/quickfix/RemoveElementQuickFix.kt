package dev.xeonkryptos.xeonrobotframeworkplugin.quickfix

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle

class RemoveElementQuickFix(element: PsiElement) : LocalQuickFixOnPsiElement(element) {

    override fun getText(): @IntentionName String = RobotBundle.message("intention.family.removal.delete.element.name")

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) = startElement.delete()

    override fun getFamilyName(): @IntentionFamilyName String = RobotBundle.message("intention.family.removal.delete.element.text")
}
