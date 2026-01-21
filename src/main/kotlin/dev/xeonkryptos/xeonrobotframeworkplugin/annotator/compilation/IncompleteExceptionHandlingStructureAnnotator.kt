package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.jetbrains.python.ast.findChildByClass
import com.jetbrains.python.ast.findChildByType
import com.jetbrains.python.ast.findChildrenByClass
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExceptStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExceptionHandlingStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFinallyStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTryStructure

class IncompleteExceptionHandlingStructureAnnotator : RobotAnnotator() {

    @Suppress("UnstableApiUsage")
    override fun visitExceptionHandlingStructure(o: RobotExceptionHandlingStructure) {
        val exceptStructures = o.findChildrenByClass(RobotExceptStructure::class.java)
        val finallyStructure = o.findChildByClass(RobotFinallyStructure::class.java)
        if (exceptStructures.isEmpty() && finallyStructure == null) {
            val tryStructure = o.findChildByClass(RobotTryStructure::class.java) ?: return
            val tryTokenElement = tryStructure.findChildByType<PsiElement>(RobotTypes.TRY) ?: return

            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.exception-handling.incomplete")).range(tryTokenElement).create()
        }
    }
}
