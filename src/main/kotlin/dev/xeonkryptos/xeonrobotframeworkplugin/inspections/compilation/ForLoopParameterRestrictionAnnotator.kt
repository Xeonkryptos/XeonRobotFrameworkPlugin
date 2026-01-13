package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation

import com.intellij.lang.annotation.HighlightSeverity
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotAnnotator
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
        for (parameter in o.forLoopStructureParameterList) {
            parameter.accept(this)
        }

        startParameter?.let { startParam ->
            val forInElementText = o.forInElement?.text
            if (forInElementText != null && forInElementText != RobotNames.FOR_IN_ENUMERATE) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.for-loop.parameter.unsupported.for.loop.type", "start", forInElementText, RobotNames.FOR_IN_ENUMERATE))
                    .range(startParam)
                    .create()
            }
        }
        modeParameter?.let { modeParam ->
            val forInElementText = o.forInElement?.text
            if (forInElementText != null && forInElementText != RobotNames.FOR_IN_ZIP) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.for-loop.parameter.unsupported.for.loop.type", "mode", forInElementText, RobotNames.FOR_IN_ZIP))
                    .range(modeParam)
                    .create()
            }
        }
        fillParameter?.let { modeParam ->
            val forInElementText = o.forInElement?.text
            if (forInElementText != null && forInElementText != RobotNames.FOR_IN_ZIP) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.for-loop.parameter.unsupported.for.loop.type", "fill", forInElementText, RobotNames.FOR_IN_ZIP))
                    .range(modeParam)
                    .create()
            }
        }

        modeParameter = null
        fillParameter = null
        startParameter = null
        visitForLoopStructureChildren = false
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
}
