package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.codeInsight.intention.FileModifier
import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.parentOfType
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

abstract class AbstractReplaceKeywordQuickFix(keywordCall: RobotKeywordCall, normalizedKeywordName: String) : LocalQuickFixOnPsiElement(keywordCall) {

    @FileModifier.SafeFieldForPreview
    private val replaceableKeywordNames = setOf(normalizedKeywordName, "${RobotNames.BUILTIN_NAMESPACE}.${normalizedKeywordName}")

    override fun getText(): @IntentionName String = RobotBundle.message("intention.family.deprecated.keyword.native.replacement.name")

    final override fun isAvailable(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement): Boolean = startElement.parentOfType<RobotKeywordCall>(withSelf = true)?.let {
        val normalizeKeywordName = KeywordUtil.normalizeKeywordName(it.name)
        // The quick fix is not available for keyword calls with parameters. A keyword call needs only positional arguments.
        return@let if (replaceableKeywordNames.contains(normalizeKeywordName) && it.parameterList.isEmpty()) isAvailable(it) else false
    } ?: false

    abstract fun isAvailable(keywordCall: RobotKeywordCall): Boolean

    final override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val keywordCall = startElement.parentOfType<RobotKeywordCall>(withSelf = true) ?: return
        invoke(project, file, keywordCall)
    }

    abstract fun invoke(project: Project, file: PsiFile, keywordCall: RobotKeywordCall)

    override fun getFamilyName(): @IntentionFamilyName String = RobotBundle.message("intention.family.deprecated.keyword.native.replacement.text")
}
