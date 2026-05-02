package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.lang.ASTNode
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.StaticSymbolWhiteSpaceDefinitionStrategy
import com.intellij.psi.util.parentOfType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotMultiLineContainerElement
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants
import org.toml.lang.psi.ext.elementType

class RobotWhitespaceFormattingStrategy : StaticSymbolWhiteSpaceDefinitionStrategy(' ', '\t', '\n', '.') {

    override fun replaceDefaultStrategy(): Boolean = true

    override fun adjustWhiteSpaceIfNecessary(whiteSpaceText: CharSequence,
                                             text: CharSequence,
                                             startOffset: Int,
                                             endOffset: Int,
                                             codeStyleSettings: CodeStyleSettings,
                                             nodeAfter: ASTNode?): CharSequence {
        if (whiteSpaceText.isNotEmpty() && whiteSpaceText[0] == '\n' && nodeAfter != null && nodeAfter.psi != null) {
            return computeAdjustedWhitespaceWithContinuation(whiteSpaceText, nodeAfter.psi, codeStyleSettings)
        }
        return whiteSpaceText
    }

    override fun adjustWhiteSpaceIfNecessary(whiteSpaceText: CharSequence, startElement: PsiElement, startOffset: Int, endOffset: Int, codeStyleSettings: CodeStyleSettings): CharSequence {
        if (whiteSpaceText.isNotEmpty() && whiteSpaceText[0] == '\n' && (startElement.nextSibling != null && startElement.nextSibling.elementType !== RobotTypes.EOL || startElement.elementType === RobotTypes.EOS)) {
            return computeAdjustedWhitespaceWithContinuation(whiteSpaceText, startElement, codeStyleSettings)
        }
        return whiteSpaceText
    }

    private fun computeAdjustedWhitespaceWithContinuation(whiteSpaceText: CharSequence, startElement: PsiElement, codeStyleSettings: CodeStyleSettings): CharSequence {
        startElement.parentOfType<RobotMultiLineContainerElement>(false)?.let {
            var multilineParent = it
            if (startElement.textRange.startOffset == it.textRange.startOffset) {
                multilineParent = it.parentOfType<RobotMultiLineContainerElement>(false) ?: it
            }
            if (startElement.textRange.startOffset != multilineParent.textRange.startOffset) {
                return computeAdjustedWhitespaceWithContinuation(whiteSpaceText, codeStyleSettings, startElement.containingFile.fileType)
            }
        }
        return whiteSpaceText
    }

    private fun computeAdjustedWhitespaceWithContinuation(whiteSpaceText: CharSequence, codeStyleSettings: CodeStyleSettings, fileType: FileType): CharSequence {
        val continuationIndentSize = codeStyleSettings.getContinuationIndentSize(fileType)
        val customSettings = codeStyleSettings.getCustomSettings(RobotCodeStyleSettings::class.java)
        val afterContinuationIndentSize = customSettings.AFTER_CONTINUATION_INDENT_SIZE
        return StringBuilder(whiteSpaceText).append(" ".repeat(continuationIndentSize)).append(GlobalConstants.CONTINUATION).append(" ".repeat(afterContinuationIndentSize))
    }

    override fun containsWhitespacesOnly(node: ASTNode): Boolean = RobotTokenSets.EXTENDED_WHITESPACE_SET.contains(node.elementType)
}
