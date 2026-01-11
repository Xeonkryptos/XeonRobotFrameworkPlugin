package dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding

import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes

object RobotFoldingComputationUtil {

    const val SINGLE_SPACE: String = " "
    const val CONTAINER_FOLDING_PLACEHOLDER = "{ ... }"
    const val CONTAINER_FOLDING_PLACEHOLDER_WITH_SINGLE_SPACE_SEPARATOR = "${SINGLE_SPACE}${CONTAINER_FOLDING_PLACEHOLDER}"

    @JvmStatic
    fun computeFoldingDescriptorForIdBasedContainerRepresentation(element: PsiElement, idElement: PsiElement, document: Document): FoldingDescriptor? {
        if (!isFoldingUseful(element.textRange, document)) return null

        val identifiedFoldableTextRange = computeFoldableTextRange(element, document)
        val foldableTextRange = TextRange.create(identifiedFoldableTextRange.startOffset + idElement.textLength, identifiedFoldableTextRange.endOffset)
        if (foldableTextRange.isEmpty) return null

        val placeholderText = computeMethodLikeFoldingPlaceholder(idElement)
        return FoldingDescriptor(element.node, foldableTextRange, null, placeholderText)
    }

    @JvmStatic
    fun isFoldingUseful(textRange: TextRange, document: Document): Boolean {
        if (textRange.isEmpty) return false

        val startOffset = textRange.startOffset
        val endOffset = textRange.endOffset

        val startLineNumber = document.getLineNumber(startOffset)
        val endLineNumber = document.getLineNumber(endOffset)

        // Only consider folding regions that span more than one line as useful
        return startLineNumber != endLineNumber
    }

    @JvmStatic
    fun computeMethodLikeFoldingPlaceholder(element: PsiElement): String {
        return if (!element.text.endsWith(SINGLE_SPACE)) CONTAINER_FOLDING_PLACEHOLDER_WITH_SINGLE_SPACE_SEPARATOR
        else CONTAINER_FOLDING_PLACEHOLDER
    }

    /**
     * Computes an optimized folding region for simple folding scenarios. Simple folding regions are encompassing the entire element, replacing it in the editor.
     *
     * @param element The PSI element for which the folding region is to be computed.
     * @param document The document containing the text of the PSI element.
     *
     * @return A folding descriptor for a simple folding region
     */
    @JvmStatic
    fun computeSimpleFoldingRegionFor(element: PsiElement, document: Document): FoldingDescriptor {
        val foldingRegion = computeFoldableTextRange(element, document)
        return FoldingDescriptor(element, foldingRegion)
    }

    /**
     * Computes the foldable text range for a given PSI element, excluding trailing new lines and comments in different lines. The produced text range encompasses
     * the complete element, but excludes any trailing new lines and comments that are not in the same line as the last relevant element.
     */
    @JvmStatic
    fun computeFoldableTextRange(element: PsiElement, document: Document): TextRange {
        var ignorableNewLines = 0
        val charsSequence = document.charsSequence
        val textRange = element.textRange
        for (i in textRange.endOffset - 1 downTo textRange.startOffset) {
            val c = charsSequence[i]
            if (c != '\n' && c != '\r') {
                val offset = textRange.endOffset - ignorableNewLines
                val elementAt = element.findElementAt(offset)
                if (elementAt != null && elementAt.node.elementType === RobotTypes.EOL) {
                    return computeTextRangeWithoutCommentsInDifferentLines(elementAt, textRange, document)
                }
                break
            }
            // Count any new lines at the end of this element to ignore them in the folding region later on. That way, newlines which are a part of the element
            // tree structure as children are ignored, and the folding region keeps the basic spaces/structure of the code without too much interruption.
            ignorableNewLines++
        }
        return textRange.grown(-ignorableNewLines)
    }

    private fun computeTextRangeWithoutCommentsInDifferentLines(elementAt: PsiElement, textRange: TextRange, document: Document): TextRange {
        var prevSibling = elementAt.prevSibling
        if (prevSibling != null) {
            var prevSiblingNode = prevSibling.node
            while (prevSiblingNode.elementType === RobotTypes.EOL || prevSiblingNode.elementType === TokenType.WHITE_SPACE) {
                prevSibling = prevSibling.prevSibling
                prevSiblingNode = prevSibling.node
            }
            var newEndOffset = prevSibling.textRange.endOffset
            // Look for the end offset of the current line identified as the relevant line to stop the folding region at. That way, any comment in the same line
            // as the last relevant element is also included in the folding region. Any other comments in different lines are excluded.
            val lineNumber = document.getLineNumber(newEndOffset)
            newEndOffset = document.getLineEndOffset(lineNumber)
            return TextRange(textRange.startOffset, newEndOffset)
        }
        return textRange
    }
}
