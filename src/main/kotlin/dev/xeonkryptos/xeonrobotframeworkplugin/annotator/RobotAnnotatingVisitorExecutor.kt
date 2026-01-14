package dev.xeonkryptos.xeonrobotframeworkplugin.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.psi.PsiElement

object RobotAnnotatingVisitorExecutor {

    fun runAnnotators(psiElement: PsiElement, holder: AnnotationHolder, annotators: Array<RobotAnnotator>) {
        for (annotator in annotators) {
            annotator.annotateElement(psiElement, holder)
        }
    }
}
