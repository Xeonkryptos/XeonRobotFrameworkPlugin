package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.parentOfType
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class ReplaceRunKeywordAndExpectErrorQuickFix(keywordCall: RobotKeywordCall) : LocalQuickFixOnPsiElement(keywordCall) {

    companion object {
        private val replaceableKeywordNames =
            setOf(RobotNames.RUN_KEYWORD_AND_EXPECT_ERROR_NORMALIZED_KEYWORD_NAME, "${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RUN_KEYWORD_AND_EXPECT_ERROR_NORMALIZED_KEYWORD_NAME}")
    }

    override fun getText(): @IntentionName String = RobotBundle.message("intention.family.deprecated.keyword.native.replacement.name")

    override fun isAvailable(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement): Boolean = startElement.parentOfType<RobotKeywordCall>(withSelf = true)?.let {
        val normalizeKeywordName = KeywordUtil.normalizeKeywordName(it.name)
        // The quick fix is not available for keyword calls with parameters. A keyword call like "Run Keyword And Expect Error" needs positional arguments.
        return@let if (replaceableKeywordNames.contains(normalizeKeywordName) && it.parameterList.isEmpty()) {
            it.positionalArgumentList.size == 2
        } else false
    } ?: false

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val runKeywordCall = startElement.parentOfType<RobotKeywordCall>(withSelf = true) ?: return

        val quickFixArgumentsCollector = QuickFixArgumentsCollector()
        runKeywordCall.positionalArgumentList.forEach { it.accept(quickFixArgumentsCollector) }

        val expectedError = quickFixArgumentsCollector.expectedError?.text ?: return
        val keywordCall = quickFixArgumentsCollector.keywordCall?.text ?: return

        val newConditionalStructure =
            RobotElementGenerator.getInstance(project).createNewExceptionHandlingStructure(keywordCall, arrayOf(Pair(expectedError, "Fail  Expected error not raised.")), null)
        startElement.replace(newConditionalStructure)
    }

    override fun getFamilyName(): @IntentionFamilyName String = RobotBundle.message("intention.family.deprecated.keyword.native.replacement.text")

    private class QuickFixArgumentsCollector : RobotVisitor() {

        var expectedError: RobotPositionalArgument? = null
        var keywordCall: RobotKeywordCall? = null

        override fun visitPositionalArgument(o: RobotPositionalArgument) {
            if (expectedError == null) expectedError = o
            else o.acceptChildren(this)
        }

        override fun visitKeywordCall(o: RobotKeywordCall) {
            keywordCall = o
        }
    }
}
