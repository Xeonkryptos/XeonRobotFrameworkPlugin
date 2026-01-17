package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation

import com.intellij.lang.annotation.HighlightSeverity
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructureFillParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructureModeParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructureParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructureStartParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class ForLoopParameterRestrictionAnnotator : RobotAnnotator() {

    private var visitForLoopStructureChildren: Boolean = false

    private var modeParameter: RobotForLoopStructureModeParameter? = null
    private var fillParameter: RobotForLoopStructureFillParameter? = null
    private var startParameter: RobotForLoopStructureStartParameter? = null

    override fun visitForLoopStructure(o: RobotForLoopStructure) {
        visitForLoopStructureChildren = true
        val forLoopHeader = o.forLoopHeader
        for (parameter in forLoopHeader.forLoopStructureParameterList) {
            parameter.accept(this)
        }

        startParameter?.let { startParam ->
            val forInElementText = forLoopHeader.forInElement?.text
            if (forInElementText != null && forInElementText != RobotNames.FOR_IN_ENUMERATE_RESERVED_NAME) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.for-loop.parameter.unsupported.for.loop.type", "start", forInElementText, RobotNames.FOR_IN_ENUMERATE_RESERVED_NAME))
                    .range(startParam)
                    .create()
            }
        }
        modeParameter?.let { modeParam ->
            val forInElementText = forLoopHeader.forInElement?.text
            if (forInElementText != null && forInElementText != RobotNames.FOR_IN_ZIP_RESERVED_NAME) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.for-loop.parameter.unsupported.for.loop.type", "mode", forInElementText, RobotNames.FOR_IN_ZIP_RESERVED_NAME))
                    .range(modeParam)
                    .create()
            }
        }
        fillParameter?.let { modeParam ->
            val forInElementText = forLoopHeader.forInElement?.text
            if (forInElementText != null && forInElementText != RobotNames.FOR_IN_ZIP_RESERVED_NAME) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.for-loop.parameter.unsupported.for.loop.type", "fill", forInElementText, RobotNames.FOR_IN_ZIP_RESERVED_NAME))
                    .range(modeParam)
                    .create()
            }
        }
    }

    override fun visitForLoopStructureParameter(o: RobotForLoopStructureParameter) {
        if (visitForLoopStructureChildren) o.acceptChildren(this)
    }

    override fun visitForLoopStructureFillParameter(o: RobotForLoopStructureFillParameter) {
        if (visitForLoopStructureChildren) fillParameter = o
    }

    override fun visitForLoopStructureModeParameter(o: RobotForLoopStructureModeParameter) {
        if (visitForLoopStructureChildren) modeParameter = o
    }

    override fun visitForLoopStructureStartParameter(o: RobotForLoopStructureStartParameter) {
        if (visitForLoopStructureChildren) startParameter = o
    }

    override fun resetState() {
        modeParameter = null
        fillParameter = null
        startParameter = null
        visitForLoopStructureChildren = false
    }
}
