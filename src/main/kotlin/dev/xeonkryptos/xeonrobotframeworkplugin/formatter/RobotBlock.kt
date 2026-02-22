package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.Indent
import com.intellij.formatting.Spacing
import com.intellij.formatting.SpacingBuilder
import com.intellij.formatting.Wrap
import com.intellij.lang.ASTNode
import com.intellij.lang.tree.util.children
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.tree.IElementType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants

class RobotBlock(
    node: ASTNode, private val spacingBuilder: SpacingBuilder, wrap: Wrap? = null, alignment: Alignment? = null, private val indent: Indent? = null, private val sharedAlignmentMap: Map<Int, Alignment>? = null
) : AbstractBlock(node, wrap, alignment) {

    companion object {
        private val WHITESPACE_TYPES = setOf(TokenType.WHITE_SPACE, RobotTypes.EOL, RobotTypes.EOS)
        private val LEAF_TYPES = setOf<IElementType>(
            RobotTypes.IMPORT_ARGUMENT,
            RobotTypes.USER_KEYWORD_STATEMENT_ID,
            RobotTypes.TEST_CASE_ID,
            RobotTypes.TASK_ID,
            RobotTypes.LOCAL_ARGUMENTS_SETTING_ID,
            RobotTypes.PARAMETER_ID,
            RobotTypes.TEMPLATE_PARAMETER_ID,
            RobotTypes.LOCAL_SETTING_ID,
            RobotTypes.VARIABLE_CONTENT,
            RobotTypes.POSITIONAL_ARGUMENT,
            RobotTypes.LITERAL_CONSTANT_VALUE,
            RobotTypes.KEYWORD_CALL_NAME
        )
        private val BLOCK_OPENING_TYPES = setOf(
            RobotTypes.TEST_CASE_STATEMENT,
            RobotTypes.USER_KEYWORD_STATEMENT,
            RobotTypes.TASK_STATEMENT,
            RobotTypes.FOR_LOOP_STRUCTURE,
            RobotTypes.WHILE_LOOP_STRUCTURE,
            RobotTypes.IF_STRUCTURE,
            RobotTypes.ELSE_IF_STRUCTURE,
            RobotTypes.ELSE_STRUCTURE,
            RobotTypes.TRY_STRUCTURE,
            RobotTypes.EXCEPT_STRUCTURE,
            RobotTypes.FINALLY_STRUCTURE,
            RobotTypes.GROUP_STRUCTURE
        )
        private val BLOCK_OPENING_PART_TYPES = setOf(
            RobotTypes.FOR_LOOP_HEADER,
            RobotTypes.WHILE_LOOP_HEADER,
            RobotTypes.IF,
            RobotTypes.ELSE_IF,
            RobotTypes.ELSE,
            RobotTypes.TRY,
            RobotTypes.EXCEPT,
            RobotTypes.FINALLY,
            RobotTypes.END,
            RobotTypes.USER_KEYWORD_STATEMENT_ID,
            RobotTypes.TEST_CASE_NAME,
            RobotTypes.TASK_NAME
        )
    }

    override fun buildChildren(): List<Block> {
        val blocks = ArrayList<Block>()
        if (isLeaf) return blocks

        // Prepare alignment map for children if this is a section
        val childrenAlignmentMap = if (isAlignableSection(myNode)) {
            // Create alignments for columns. We assume up to 20 columns max.
            (0..20).associateWith { Alignment.createAlignment(true) }
        } else {
            null
        }

        for (child in myNode.children()) {
            if (!WHITESPACE_TYPES.contains(child.elementType) && child.textRange.length > 0) {
                val indent = if (BLOCK_OPENING_TYPES.contains(myNode.elementType) && !BLOCK_OPENING_PART_TYPES.contains(child.elementType)) Indent.getNormalIndent() else Indent.getNoneIndent()
                // Determine alignment for the child
                // If we are inside a Statement (which has a sharedAlignmentMap from parent Section),
                // we apply the alignment based on child index.
                var childAlignment: Alignment? = null

                if (sharedAlignmentMap != null) {
                    val columnIndex = getColumnIndex(child)
                    if (columnIndex >= 0) {
                        childAlignment = sharedAlignmentMap[columnIndex]
                    }
                }

                val robotBlock = RobotBlock(child, null, childAlignment, spacingBuilder, indent = indent, sharedAlignmentMap = childrenAlignmentMap)
                blocks.add(robotBlock)
            } else if (child.text == GlobalConstants.CONTINUATION) {
                // Handle continuation lines. They should align with the first column of the previous line.
                val continuationAlignment = sharedAlignmentMap?.get(0) ?: Alignment.createAlignment(true)
                val robotBlock = RobotBlock(child, null, continuationAlignment, spacingBuilder, indent = Indent.getContinuationIndent(), sharedAlignmentMap = childrenAlignmentMap)
                blocks.add(robotBlock)
            }
        }
        return blocks
    }

    private fun getColumnIndex(node: ASTNode): Int {
        // This is a simplified heuristic. To do this properly, we need to know the semantic index.
        // Count preceding siblings that are not whitespace/comments.
        var index = 0
        var prev = node.treePrev
        while (prev != null) {
            if (!WHITESPACE_TYPES.contains(prev.elementType) && prev.elementType !== RobotTypes.COMMENT) {
                index++
            }
            prev = prev.treePrev
        }
        return index
    }

    private fun isAlignableSection(node: ASTNode): Boolean = node.elementType == RobotTypes.VARIABLES_SECTION || node.elementType == RobotTypes.SETTINGS_SECTION || node.elementType == RobotTypes.TEST_CASES_SECTION || node.elementType == RobotTypes.KEYWORDS_SECTION

    override fun getIndent(): Indent? = indent

    override fun getSpacing(child1: Block?, child2: Block): Spacing? = spacingBuilder.getSpacing(this, child1, child2)

    override fun isLeaf(): Boolean = LEAF_TYPES.contains(myNode.elementType) || myNode.firstChildNode === null
}
