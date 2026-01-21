package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentOfTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLoopControlStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotWhileLoopStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class InvalidlyPlacedContinueBreakAnnotator : RobotAnnotator() {

    override fun visitLoopControlStructure(o: RobotLoopControlStructure) {
        val loopParent = o.parentOfTypes(RobotForLoopStructure::class, RobotWhileLoopStructure::class)
        if (loopParent == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.continue-break.not.in.loop")).range(o.firstChild).create()
        }
    }

    override fun visitKeywordCall(o: RobotKeywordCall) {
        val normalizedKeywordName = KeywordUtil.normalizeKeywordName(o.keywordCallName.keywordName.text)
        when (normalizedKeywordName) {
            RobotNames.CONTINUE_FOR_LOOP_NORMALIZED_KEYWORD_NAME, RobotNames.CONTINUE_FOR_LOOP_IF_NORMALIZED_KEYWORD_NAME, RobotNames.EXIT_FOR_LOOP_NORMALIZED_KEYWORD_NAME, RobotNames.EXIT_FOR_LOOP_IF_NORMALIZED_KEYWORD_NAME -> {
                val forLoopParent = o.parentOfType<RobotForLoopStructure>()
                if (forLoopParent == null) {
                    holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.for-loop.continue-exit.keywords.not.in-for-loop", o.keywordCallName.text))
                        .range(o.firstChild)
                        .create()
                }
            }
        }
    }
}
