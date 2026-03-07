package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.Indent
import com.intellij.formatting.Spacing
import com.intellij.formatting.Wrap
import com.intellij.lang.ASTNode
import com.intellij.lang.tree.util.children
import com.intellij.openapi.util.Key
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.tree.TokenSet
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants

class RobotBlock(
    node: ASTNode, private val context: RobotBlockContext, wrap: Wrap? = null, alignment: Alignment? = null, private val indent: Indent? = null
) : AbstractBlock(node, wrap, alignment) {
    companion object {
        private val TEMPLATE_VALUES_ALIGNMENT_KEY = Key.create<Array<Alignment>>("TEMPLATE_VALUE_ALIGNMENT")
        private val KEYWORD_ALIGNMENT_KEY = Key.create<Alignment>("KEYWORD_ALIGNMENT")
        private val ARGUMENT_ALIGNMENT_KEY = Key.create<Alignment>("ARGUMENT_ALIGNMENT")
        private val KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT_KEY = Key.create<Alignment>("KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT")
        private val CONTINUATION_TOKEN = TokenType.WHITE_SPACE

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
        private val CHILD_INDENTATION_TYPES =
            TokenSet.create(
                RobotTypes.FOR_LOOP_STRUCTURE,
                RobotTypes.WHILE_LOOP_STRUCTURE,
                RobotTypes.IF_STRUCTURE,
                RobotTypes.ELSE_STRUCTURE,
                RobotTypes.ELSE_STRUCTURE,
                RobotTypes.TRY_STRUCTURE,
                RobotTypes.EXCEPT_STRUCTURE,
                RobotTypes.FINALLY_STRUCTURE,
                RobotTypes.GROUP_STRUCTURE
            )
        private val FLATTENABLE_TYPES = TokenSet.create(RobotTypes.EXECUTABLE_STATEMENT, RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER, RobotTypes.LITERAL_CONSTANT_VALUE)
    }

    override fun buildChildren(): List<Block> {
        val blocks = mutableListOf<Block>()
        if (isLeaf) return blocks

        if (myNode.elementType === RobotTypes.KEYWORD_CALL) {
            myNode.putUserData(ARGUMENT_ALIGNMENT_KEY, Alignment.createAlignment(true))
        } else if (myNode.elementType === RobotTypes.KEYWORD_VARIABLE_STATEMENT) {
            myNode.putUserData(KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT_KEY, Alignment.createAlignment())
        }
        val parentWrap = createWrapIfNecessary()
        fun addNewRobotBlock(alignmentIndex: Int, child: ASTNode) {
            if (shouldCreateBlockForNode(child)) {
                val indent = getIndentation(child)
                val alignment = getAlignment(child, alignmentIndex)
                val childWrap = if (shouldAssignWrapToNode(child)) parentWrap else null
                RobotBlock(child, context, indent = indent, wrap = childWrap, alignment = alignment).apply { blocks.add(this) }
            }
        }

        for (child in myNode.children()) {
            if (child.elementType === RobotTypes.TEMPLATE_ARGUMENTS) {
                if (myNode.getUserData(TEMPLATE_VALUES_ALIGNMENT_KEY) === null) {
                    val templateValueHolderElements = child.getChildren(RobotTokenSets.TEMPLATE_VALUES_HOLDER_SET)
                    val alignments = Array<Alignment>(templateValueHolderElements.size) { Alignment.createAlignment(true) }
                    myNode.putUserData(TEMPLATE_VALUES_ALIGNMENT_KEY, alignments)
                }
                var alignmentIndex = 0
                for (subNode in child.children()) {
                    if (RobotTokenSets.TEMPLATE_VALUES_HOLDER_SET.contains(subNode.elementType)) {
                        addNewRobotBlock(alignmentIndex, subNode)
                        alignmentIndex++
                    } else {
                        addNewRobotBlock(-1, subNode)
                    }
                }
            } else if (FLATTENABLE_TYPES.contains(child.elementType)) addFlattenedNodes(child) { subNode -> addNewRobotBlock(-1, subNode) }
            else addNewRobotBlock(-1, child)
        }
        return blocks
    }

    private fun shouldCreateBlockForNode(node: ASTNode): Boolean =
        !WHITESPACE_TYPES.contains(node.elementType) || (node.elementType === CONTINUATION_TOKEN && node.text == GlobalConstants.CONTINUATION)

    private fun addFlattenedNodes(node: ASTNode, addNewBlock: (subNode: ASTNode) -> Unit) {
        for (child in node.children()) {
            if (FLATTENABLE_TYPES.contains(child.elementType)) {
                addFlattenedNodes(child, addNewBlock)
            } else {
                addNewBlock(child)
            }
        }
    }

    private fun createWrapIfNecessary(): Wrap? = when (myNode.elementType) {
        RobotTypes.KEYWORD_CALL -> Wrap.createWrap(context.commonCodeStyleSettings.CALL_PARAMETERS_WRAP, false)
        RobotTypes.LOCAL_ARGUMENTS_SETTING -> Wrap.createWrap(context.commonCodeStyleSettings.METHOD_PARAMETERS_WRAP, false)
        else -> null
    }

    private fun shouldAssignWrapToNode(node: ASTNode): Boolean = when (node.elementType) {
        RobotTypes.PARAMETER, RobotTypes.POSITIONAL_ARGUMENT -> myNode.elementType === RobotTypes.KEYWORD_CALL && !isFirstElementOfContinuationNode(node)
        RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER_MANDATORY, RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER_OPTIONAL -> !isFirstElementOfContinuationNode(node)
        else -> false
    }

    private fun isFirstElementOfContinuationNode(node: ASTNode): Boolean = node.treePrev?.text == GlobalConstants.CONTINUATION || node.treePrev?.treePrev?.text == GlobalConstants.CONTINUATION

    private fun getIndentation(node: ASTNode): Indent? = if (BLOCK_OPENING_TYPES.contains(myNode.elementType) && !BLOCK_OPENING_PART_TYPES.contains(node.elementType)) Indent.getNormalIndent()
    else if (node.elementType === CONTINUATION_TOKEN) Indent.getContinuationIndent()
    else Indent.getNoneIndent()

    private fun getAlignment(node: ASTNode, index: Int): Alignment? = when {
        RobotTokenSets.TEMPLATE_VALUES_HOLDER_SET.contains(node.elementType) -> myNode.getUserData(TEMPLATE_VALUES_ALIGNMENT_KEY)?.let { if (index < it.size) it[index] else null }
        node.elementType === RobotTypes.KEYWORD_CALL -> Alignment.createAlignment().apply { node.putUserData(KEYWORD_ALIGNMENT_KEY, this) }
        node.elementType === RobotTypes.PARAMETER || node.elementType === RobotTypes.POSITIONAL_ARGUMENT -> myNode.getUserData(ARGUMENT_ALIGNMENT_KEY)
        node.elementType === RobotTypes.VARIABLE_DEFINITION -> myNode.getUserData(KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT_KEY)
        node.elementType === CONTINUATION_TOKEN -> myNode.treeParent?.getUserData(KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT_KEY)
        else -> null
    }

    override fun getIndent(): Indent? = indent

    override fun getAlignment(): Alignment? = when {
        myNode.elementType === CONTINUATION_TOKEN && !context.robotCodeStyleSettings.ALIGN_CONTINUATION_WITH_VARIABLE_DEFINITION -> null
        else -> super.alignment
    }

    override fun getChildIndent(): Indent? = when {
        CHILD_INDENTATION_TYPES.contains(myNode.elementType) -> Indent.getNormalIndent()
        else -> Indent.getNoneIndent()
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? = context.spacingBuilder.getSpacing(this, child1, child2)

    override fun isLeaf(): Boolean = LEAF_TYPES.contains(myNode.elementType) || myNode.firstChildNode === null
}
