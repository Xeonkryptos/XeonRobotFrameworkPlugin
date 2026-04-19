package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.StaticTextWhiteSpaceDefinitionStrategy
import com.intellij.psi.impl.source.tree.LeafElement
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants

class RobotWhitespaceFormattingStrategy : StaticTextWhiteSpaceDefinitionStrategy(GlobalConstants.CONTINUATION) {

    override fun adjustWhiteSpaceIfNecessary(whiteSpaceText: CharSequence,
                                             text: CharSequence,
                                             startOffset: Int,
                                             endOffset: Int,
                                             codeStyleSettings: CodeStyleSettings?,
                                             nodeAfter: ASTNode?): CharSequence {
        return super.adjustWhiteSpaceIfNecessary(whiteSpaceText, text, startOffset, endOffset, codeStyleSettings, nodeAfter)
    }

    override fun adjustWhiteSpaceIfNecessary(whiteSpaceText: CharSequence, startElement: PsiElement, startOffset: Int, endOffset: Int, codeStyleSettings: CodeStyleSettings?): CharSequence? {
        return super.adjustWhiteSpaceIfNecessary(whiteSpaceText, startElement, startOffset, endOffset, codeStyleSettings)
    }

    override fun addWhitespace(treePrev: ASTNode, whiteSpaceElement: LeafElement): Boolean {
        return super.addWhitespace(treePrev, whiteSpaceElement)
    }
}
