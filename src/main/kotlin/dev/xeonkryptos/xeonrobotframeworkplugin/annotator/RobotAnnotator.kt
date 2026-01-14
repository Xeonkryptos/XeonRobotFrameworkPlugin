package dev.xeonkryptos.xeonrobotframeworkplugin.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.psi.PsiElement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor

abstract class RobotAnnotator : RobotVisitor() {

    val holder: AnnotationHolder
        get() = myHolder!!

    private var myHolder: AnnotationHolder? = null

    @Synchronized
    fun annotateElement(psiElement: PsiElement, holder: AnnotationHolder) {
        this.myHolder = holder
        try {
            psiElement.accept(this)
        } finally {
            this.myHolder = null
            resetState()
        }
    }

    open fun resetState() {}
}
