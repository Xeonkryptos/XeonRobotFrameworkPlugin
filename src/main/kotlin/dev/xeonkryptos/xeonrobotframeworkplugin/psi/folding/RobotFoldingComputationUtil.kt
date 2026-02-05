package dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotBlockOpeningStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants

object RobotFoldingComputationUtil {

    const val SINGLE_SPACE: String = " "
    const val CONTAINER_FOLDING_PLACEHOLDER = "..."
    const val CONTAINER_FOLDING_PLACEHOLDER_WITH_SINGLE_SPACE_SEPARATOR = "${SINGLE_SPACE}${CONTAINER_FOLDING_PLACEHOLDER}"
    const val CONTAINER_FOLDING_PLACEHOLDER_WITH_SUPER_SPACE_SEPARATOR = "${GlobalConstants.SUPER_SPACE}${CONTAINER_FOLDING_PLACEHOLDER}"

    const val MAX_LIST_FOLDING_LENGTH = 100
    const val MAX_VARIABLE_FOLDING_LENGTH: Int = 50

    @JvmStatic
    fun isFoldingUseful(psiElement: PsiElement, document: Document): Boolean {
        val textRange = psiElement.textRange
        if (textRange.isEmpty) return false

        var lastRelevantChild = psiElement.lastChild
        while (lastRelevantChild != null && (lastRelevantChild.node.elementType === RobotTypes.EOL || lastRelevantChild.node.elementType === TokenType.WHITE_SPACE || lastRelevantChild.node.elementType === RobotTypes.COMMENT)) {
            lastRelevantChild = lastRelevantChild.prevSibling
        }
        if (lastRelevantChild === null || lastRelevantChild === psiElement.firstChild) {
            return false
        }

        val startOffset = textRange.startOffset
        val endOffset = lastRelevantChild.textRange.endOffset

        val startLineNumber = document.getLineNumber(startOffset)
        val endLineNumber = document.getLineNumber(endOffset)

        // Only consider folding regions that span more than one line as useful
        return startLineNumber != endLineNumber
    }

    @JvmStatic
    @JvmOverloads
    fun computeFoldingDescriptorsForBlockStructure(
        element: PsiElement,
        lastHeaderElement: PsiElement,
        items: Collection<PsiElement>,
        document: Document,
        endOffset: Int? = null
    ): List<FoldingDescriptor> {
        if (items.isEmpty()) return emptyList()

        var child = element.lastChild
        while (child !== null && child !== items.last() && child.node.elementType !== RobotTypes.END) {
            child = child.prevSibling
        }
        val computedEndOffset = if (endOffset === null) {
            child = child ?: items.last()
            computeFoldableTextRange(child, document).endOffset
        } else endOffset

        return computeFoldingDescriptorsForBlockStructure(element.node, lastHeaderElement, computedEndOffset, items, document)
    }

    @JvmStatic
    fun computeFoldingDescriptorsForBlockStructure(node: ASTNode, headerElement: PsiElement, endOffset: Int, items: Collection<PsiElement>, document: Document): List<FoldingDescriptor> {
        if (items.isEmpty()) return emptyList()

        val foldingDescriptors = mutableListOf<FoldingDescriptor>()
        if (items.size == 1 && items.first() !is RobotBlockOpeningStructure && !isFoldingUseful(items.first(), document) && endOffset > items.first().textRange.endOffset) {
            val bodyElement = items.first()
            val headerMarkerFoldableTextRange = TextRange.create(headerElement.textRange.endOffset, bodyElement.textRange.startOffset)
            val endMarkerFoldableTextRange = TextRange.create(bodyElement.textRange.endOffset, endOffset)
            val foldingGroup = FoldingGroup.newGroup("SingleItemBlockStructureFolding")

            FoldingDescriptor(node, headerMarkerFoldableTextRange, foldingGroup, GlobalConstants.SUPER_SPACE).let { foldingDescriptors.add(it) }
            FoldingDescriptor(node, endMarkerFoldableTextRange, foldingGroup, GlobalConstants.SUPER_SPACE).let { foldingDescriptors.add(it) }
        } else {
            val foldableTextRange = TextRange.create(headerElement.textRange.endOffset, endOffset)
            val placeholderText = computeMethodLikeFoldingPlaceholder(headerElement) + GlobalConstants.SUPER_SPACE
            FoldingDescriptor(node, foldableTextRange, null, placeholderText).let { foldingDescriptors.add(it) }
        }
        return foldingDescriptors
    }

    @JvmStatic
    fun computeFoldingDescriptorForContainer(element: PsiElement, startElement: PsiElement, document: Document): FoldingDescriptor? {
        val identifiedFoldableTextRange = computeFoldableTextRange(element, document)
        val foldableTextRange = TextRange.create(identifiedFoldableTextRange.startOffset + startElement.textLength, identifiedFoldableTextRange.endOffset)
        if (foldableTextRange.isEmpty) return null

        val placeholderText = computeMethodLikeFoldingPlaceholder(startElement)
        return FoldingDescriptor(element.node, foldableTextRange, null, placeholderText, !placeholderText.endsWith(CONTAINER_FOLDING_PLACEHOLDER), emptySet())
    }

    @JvmStatic
    fun computeFoldingDescriptorsForListing(node: ASTNode, foldingGroupName: String, initialElement: PsiElement, listItems: Collection<PsiElement>, document: Document): List<FoldingDescriptor> {
        var currentTextRange: TextRange = initialElement.textRange
        val foldingGroup = FoldingGroup.newGroup(foldingGroupName)

        val foldingDescriptors = mutableListOf<FoldingDescriptor>()
        for (item in listItems) {
            val previousTextRange = currentTextRange
            currentTextRange = item.textRange

            val foldableTextRange = TextRange.create(previousTextRange.endOffset, currentTextRange.startOffset)
            if (foldableTextRange.isEmpty) continue
            val foldingDescriptor = FoldingDescriptor(node, foldableTextRange, foldingGroup, GlobalConstants.SUPER_SPACE)
            foldingDescriptors.add(foldingDescriptor)
        }
        if (foldingDescriptors.size > 1) {
            var completeFoldingRange = TextRange.create(foldingDescriptors.first().range.startOffset, listItems.last().textRange.endOffset)
            if (completeFoldingRange.length > MAX_LIST_FOLDING_LENGTH) {
                foldingDescriptors.clear()

                val lineNumber = document.getLineNumber(completeFoldingRange.endOffset)
                val lineEndOffset = document.getLineEndOffset(lineNumber)

                completeFoldingRange = TextRange.create(completeFoldingRange.startOffset, lineEndOffset)
                val placeholderText = computeMethodLikeFoldingPlaceholder(initialElement)
                foldingDescriptors.add(FoldingDescriptor(node, completeFoldingRange, foldingGroup, placeholderText))
            }
        }
        return foldingDescriptors
    }

    private fun computeMethodLikeFoldingPlaceholder(element: PsiElement): String {
        val text = element.text
        return if (text.endsWith(SINGLE_SPACE)) CONTAINER_FOLDING_PLACEHOLDER_WITH_SINGLE_SPACE_SEPARATOR
        else if (!text.endsWith(SINGLE_SPACE)) CONTAINER_FOLDING_PLACEHOLDER_WITH_SUPER_SPACE_SEPARATOR
        else CONTAINER_FOLDING_PLACEHOLDER
    }

    /**
     * Computes the foldable text range for a given PSI element, excluding trailing new lines and comments in different lines. The produced text range encompasses
     * the complete element, but excludes any trailing new lines and comments that are not in the same line as the last relevant element.
     */
    private fun computeFoldableTextRange(element: PsiElement, document: Document): TextRange {
        var ignorableNewLines = 0
        val charsSequence = document.charsSequence
        val textRange = element.textRange
        for (i in textRange.endOffset - 1 downTo textRange.startOffset) {
            val c = charsSequence[i]
            if (c != '\n' && c != '\r') {
                val offset = textRange.endOffset - ignorableNewLines
                val elementAt = element.findElementAt(offset)
                if (elementAt?.node?.elementType === RobotTypes.EOL) {
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
        if (prevSibling !== null) {
            var prevSiblingNode = prevSibling.node
            while (prevSiblingNode.elementType === RobotTypes.EOL || prevSiblingNode.elementType === TokenType.WHITE_SPACE || prevSiblingNode.elementType === RobotTypes.COMMENT) {
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
