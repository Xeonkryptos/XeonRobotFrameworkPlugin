package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation

import com.intellij.lang.annotation.HighlightSeverity
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructureFillParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructureModeParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructureParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructureStartParameter

class DuplicatedDefinedForLoopParameterSettingsAnnotator : RobotAnnotator() {

    private var definedFillParameters: MutableList<RobotForLoopStructureFillParameter>? = null
    private var definedModeParameters: MutableList<RobotForLoopStructureModeParameter>? = null
    private var definedStartParameters: MutableList<RobotForLoopStructureStartParameter>? = null

    override fun visitForLoopStructure(o: RobotForLoopStructure) {
        val localDefinedFillParameters = mutableListOf<RobotForLoopStructureFillParameter>()
        val localDefinedModeParameters = mutableListOf<RobotForLoopStructureModeParameter>()
        val localDefinedStartParameters = mutableListOf<RobotForLoopStructureStartParameter>()
        definedFillParameters = localDefinedFillParameters
        definedModeParameters = localDefinedModeParameters
        definedStartParameters = localDefinedStartParameters
        for (parameter in o.forLoopStructureParameterList) {
            parameter.accept(this)
        }

        if (localDefinedFillParameters.size > 1) {
            localDefinedFillParameters.forEach { holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.for-loop.parameter.duplicated", "fill")).range(it).create() }
        }
        if (localDefinedModeParameters.size > 1) {
            localDefinedModeParameters.forEach { holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.for-loop.parameter.duplicated", "mode")).range(it).create() }
        }
        if (localDefinedStartParameters.size > 1) {
            localDefinedStartParameters.forEach { holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.for-loop.parameter.duplicated", "start")).range(it).create() }
        }

        definedFillParameters = null
        definedModeParameters = null
    }

    override fun visitForLoopStructureParameter(o: RobotForLoopStructureParameter) {
        definedFillParameters?.let { o.acceptChildren(this) }
    }

    override fun visitForLoopStructureFillParameter(o: RobotForLoopStructureFillParameter) {
        definedFillParameters?.add(o)
    }

    override fun visitForLoopStructureModeParameter(o: RobotForLoopStructureModeParameter) {
        definedModeParameters?.add(o)
    }

    override fun visitForLoopStructureStartParameter(o: RobotForLoopStructureStartParameter) {
        definedStartParameters?.add(o)
    }
}
