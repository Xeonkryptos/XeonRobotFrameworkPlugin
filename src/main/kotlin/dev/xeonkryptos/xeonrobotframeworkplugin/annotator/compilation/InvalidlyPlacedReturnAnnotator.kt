package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.util.parentOfType
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotReturnStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames
import dev.xeonkryptos.xeonrobotframeworkplugin.util.matchesNormalizedName

class InvalidlyPlacedReturnAnnotator : RobotAnnotator() {

    override fun visitLocalSettingId(o: RobotLocalSettingId) {
        if (o.settingName.text == RobotNames.RETURN_LOCAL_SETTING_NAME && o.parentOfType<RobotUserKeywordStatement>() == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.return.setting.not.in.user-keyword")).range(o).create()
        }
    }

    override fun visitReturnStructure(o: RobotReturnStructure) {
        if (o.parentOfType<RobotUserKeywordStatement>() == null) {
            val returnKeywordElement = o.firstChild ?: return
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.return.structure.not.in.user-keyword")).range(returnKeywordElement).create()
        }
    }

    override fun visitKeywordCall(o: RobotKeywordCall) {
        if (o.matchesNormalizedName(RobotNames.RETURN_FROM_KEYWORD_NORMALIZED_KEYWORD_NAME) || o.matchesNormalizedName(RobotNames.RETURN_FROM_KEYWORD_IF_NORMALIZED_KEYWORD_NAME)) {
            val parentUserKeyword = o.parentOfType<RobotUserKeywordStatement>()
            if (parentUserKeyword == null) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.return.from.keyword.not.in.user-keyword")).range(o.keywordCallName).create()
            }
        }
    }
}
