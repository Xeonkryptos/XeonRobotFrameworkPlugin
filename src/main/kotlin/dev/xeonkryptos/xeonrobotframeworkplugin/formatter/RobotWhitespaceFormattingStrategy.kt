package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.StaticTextWhiteSpaceDefinitionStrategy
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants
import org.toml.lang.psi.ext.elementType

class RobotWhitespaceFormattingStrategy : StaticTextWhiteSpaceDefinitionStrategy(GlobalConstants.CONTINUATION) {

    override fun adjustWhiteSpaceIfNecessary(whiteSpaceText: CharSequence, startElement: PsiElement, startOffset: Int, endOffset: Int, codeStyleSettings: CodeStyleSettings): CharSequence {
        if (whiteSpaceText.contains('\n') && startElement.nextSibling != null && startElement.nextSibling.elementType !== RobotTypes.EOL) {
            val continuationIndentSize = codeStyleSettings.getContinuationIndentSize(startElement.containingFile.fileType)
            val customSettings = codeStyleSettings.getCustomSettings(RobotCodeStyleSettings::class.java)
            val afterContinuationIndentSize = customSettings.AFTER_CONTINUATION_INDENT_SIZE
            return StringBuilder(whiteSpaceText).append(" ".repeat(continuationIndentSize)).append(GlobalConstants.CONTINUATION).append(" ".repeat(afterContinuationIndentSize))
        }
        return super.adjustWhiteSpaceIfNecessary(whiteSpaceText, startElement, startOffset, endOffset, codeStyleSettings)
    }
}
