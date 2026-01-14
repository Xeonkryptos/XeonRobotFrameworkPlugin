package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDictVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotEnvironmentVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpression
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotScalarVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableIndexAccessContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableNestedAccessContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableSliceAccessContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class RobotInZipVariableSourceTypeAnnotator : RobotAnnotator() {

    private var lookingAtForLoopArguments = false

    override fun visitForLoopStructure(o: RobotForLoopStructure) {
        if (o.forInElement?.text == RobotNames.FOR_IN_ZIP) {
            lookingAtForLoopArguments = true
            o.positionalArgumentList.forEach { it.acceptChildren(this) }
        }
    }

    override fun visitEnvironmentVariable(o: RobotEnvironmentVariable) {
        if (lookingAtForLoopArguments) holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.for-loop.in-zip.arguments.invalid.variable.type"))
            .range(o)
            .withFix(ConvertVariableToScalarQuickFix(o.text))
            .create()
    }

    override fun visitDictVariable(o: RobotDictVariable) {
        if (lookingAtForLoopArguments) holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.for-loop.in-zip.arguments.invalid.variable.type"))
            .range(o)
            .withFix(ConvertVariableToScalarQuickFix(o.text))
            .create()
    }

    override fun resetState() {
        lookingAtForLoopArguments = false
    }

    private class ConvertVariableToScalarQuickFix(variable: String) : PsiElementBaseIntentionAction() {

        init {
            text = RobotBundle.message("intention.family.variable.convert.to.scalar.text", variable)
        }

        override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
            val variableElement = element.parentOfType<RobotVariable>(true) ?: return
            val variableBody = PsiTreeUtil.findChildOfAnyType(variableElement, true, RobotVariableContent::class.java, RobotPythonExpression::class.java)
            val extendedAccesses =
                PsiTreeUtil.findChildrenOfAnyType(
                    variableElement,
                    true,
                    RobotVariableNestedAccessContent::class.java,
                    RobotVariableSliceAccessContent::class.java,
                    RobotVariableIndexAccessContent::class.java
                )
            val extendedAccessesAsString = extendedAccesses.joinToString("") { it.text }
            val newScalarVariable = RobotElementGenerator.getInstance(project).createNewScalarVariable(variableBody?.text ?: "", extendedAccessesAsString)
            variableElement.replace(newScalarVariable)
        }

        override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
            val variableElement = element as? RobotVariable ?: element.parentOfType<RobotVariable>()
            return variableElement != null && variableElement !is RobotScalarVariable
        }

        @IntentionFamilyName
        override fun getFamilyName(): String = RobotBundle.message("intention.family.variable.convert.to.scalar.name")
    }
}
