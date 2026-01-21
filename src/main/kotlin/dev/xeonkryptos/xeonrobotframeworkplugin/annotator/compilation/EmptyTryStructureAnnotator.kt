package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation

import com.intellij.lang.annotation.HighlightSeverity
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTryStructure

class EmptyTryStructureAnnotator : RobotAnnotator() {

    override fun visitTryStructure(o: RobotTryStructure) {
        if (o.executableStatementList.isEmpty()) {
            val firstChild = o.firstChild ?: return
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.exception-handling.empty.try.body")).range(firstChild).create()
        }
    }
}
