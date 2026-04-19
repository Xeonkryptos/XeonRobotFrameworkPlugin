package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.formatter.FormatterUtil
import com.intellij.psi.formatter.FormattingDocumentModelImpl
import com.intellij.psi.formatter.PsiBasedFormattingModel
import com.intellij.psi.tree.TokenSet
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes

class RobotFormattingModel(file: PsiFile, rootBlock: RobotBlock) : PsiBasedFormattingModel(file, rootBlock, FormattingDocumentModelImpl.createOn(file)) {

    private companion object {
        private val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE, RobotTypes.EOL, RobotTypes.EOS)
    }

    private val project = file.project

    override fun replaceWithPsiInLeaf(textRange: TextRange, whiteSpace: String, leafElement: ASTNode): String? {
        if (!myCanModifyAllWhiteSpaces && (WHITE_SPACES.contains(leafElement.elementType))) return null

        var whiteSpaceToUse = whiteSpace
        var deletableEolNode: ASTNode? = null
        val whiteSpaceTokenType = if (isPreviousWhiteSpaceEndOfStatement(leafElement)) RobotTypes.EOS else TokenType.WHITE_SPACE
        if (whiteSpaceTokenType === TokenType.WHITE_SPACE) {
            val eolNode = findPotentiallyAffectedEolNode(textRange)
            if (eolNode != null && whiteSpace.startsWith('\n')) whiteSpaceToUse = whiteSpace.substring(eolNode.textLength)
            else deletableEolNode = eolNode
        }

        CodeStyleManager.getInstance(project).performActionWithFormatterDisabled { FormatterUtil.replaceWhiteSpace(whiteSpaceToUse, leafElement, whiteSpaceTokenType, textRange) }
        deletableEolNode?.let { CodeStyleManager.getInstance(project).performActionWithFormatterDisabled { it.treeParent?.removeChild(it) } }

        return whiteSpace
    }

    private fun findPotentiallyAffectedEolNode(textRange: TextRange): ASTNode? {
        var currentNode = findElementAt(textRange.startOffset)
        while (currentNode != null) {
            if (currentNode.elementType === RobotTypes.EOL) return currentNode
            currentNode = currentNode.lastChildNode
        }
        return null
    }

    private fun isPreviousWhiteSpaceEndOfStatement(leafElement: ASTNode): Boolean {
        val offset = leafElement.textRange.startOffset - 1
        if (offset < 0) return false

        val psiElement = leafElement.psi ?: return false
        val found = psiElement.containingFile.findElementAt(offset) ?: return false

        val treeElement = found.node
        return treeElement != null && treeElement.elementType === RobotTypes.EOS
    }
}
