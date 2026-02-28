package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.Indent
import com.intellij.formatting.Spacing
import com.intellij.formatting.Wrap
import com.intellij.lang.ASTNode
import com.intellij.lang.tree.util.children
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.tree.TokenSet
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants

class RobotBlock(
    node: ASTNode, private val context: RobotBlockContext, wrap: Wrap? = null, alignment: Alignment? = null, private val indent: Indent? = null
) : AbstractBlock(node, wrap, alignment) {
    companion object {
        // Differs from whitespace set of RobotTokenSets because it also includes EOL and EOS, which are treated as whitespace for formatting purposes, but not for parsing.
        private val WHITESPACE_TYPES = TokenSet.create(TokenType.WHITE_SPACE, RobotTypes.EOL, RobotTypes.EOS)
        private val LEAF_TYPES = TokenSet.create(
            RobotTypes.IMPORT_ARGUMENT,
            RobotTypes.USER_KEYWORD_STATEMENT_ID,
            RobotTypes.TEST_CASE_ID,
            RobotTypes.TASK_ID,
            RobotTypes.LOCAL_ARGUMENTS_SETTING_ID,
            RobotTypes.PARAMETER_ID,
            RobotTypes.TEMPLATE_PARAMETER_ID,
            RobotTypes.LOCAL_SETTING_ID,
            RobotTypes.VARIABLE_CONTENT,
            RobotTypes.LITERAL_CONSTANT_VALUE,
            RobotTypes.KEYWORD_CALL_NAME
        )
        private val BLOCK_OPENING_TYPES = TokenSet.create(
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
        private val BLOCK_OPENING_PART_TYPES = TokenSet.create(
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
            RobotTypes.TEST_CASE_ID,
            RobotTypes.TASK_NAME,
            RobotTypes.TASK_ID
        )
    }

    override fun buildChildren(): List<Block> {
        val blocks = mutableListOf<Block>()
        if (isLeaf) return blocks

        for (child in myNode.children()) {
            val block = if (!WHITESPACE_TYPES.contains(child.elementType) && child.textLength > 0) {
                val wrap: Wrap? = createWrapIfNecessary(child)
                val indent = if (BLOCK_OPENING_TYPES.contains(myNode.elementType) && !BLOCK_OPENING_PART_TYPES.contains(child.elementType)) Indent.getNormalIndent() else Indent.getNoneIndent()
                RobotBlock(child, context, indent = indent, wrap = wrap)
            } else if (child.text == GlobalConstants.CONTINUATION) RobotBlock(child, context, indent = Indent.getContinuationIndent()) else null
            block?.let { blocks.add(it) }
        }
        return blocks
    }

    private fun createWrapIfNecessary(node: ASTNode): Wrap? = when (node.elementType) {
        RobotTypes.KEYWORD_CALL -> Wrap.createWrap(context.commonCodeStyleSettings.CALL_PARAMETERS_WRAP, false)
        RobotTypes.LOCAL_ARGUMENTS_SETTING, RobotTypes.LOCAL_SETTING -> Wrap.createWrap(context.commonCodeStyleSettings.METHOD_PARAMETERS_WRAP, false)
        RobotTypes.PARAMETER, RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER -> wrap
        RobotTypes.POSITIONAL_ARGUMENT -> if (node.treeParent?.elementType !== RobotTypes.PARAMETER) wrap else null
        else -> null
    }

    override fun getIndent(): Indent? = indent

    override fun getSpacing(child1: Block?, child2: Block): Spacing? = context.spacingBuilder.getSpacing(this, child1, child2)

    override fun isLeaf(): Boolean = LEAF_TYPES.contains(myNode.elementType) || myNode.firstChildNode === null
}
