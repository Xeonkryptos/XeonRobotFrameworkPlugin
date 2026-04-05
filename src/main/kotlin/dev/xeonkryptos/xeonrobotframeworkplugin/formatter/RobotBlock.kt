package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.lang.tree.util.children
import com.intellij.openapi.util.Key
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.tree.TokenSet
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants

class RobotBlock(node: ASTNode, private val context: RobotBlockContext, wrap: Wrap? = null, alignment: Alignment? = null, private val indent: Indent? = null, private val childIndent: Indent? = null) :
    AbstractBlock(node, wrap, alignment) {
    companion object {
        private val PARENT_BLOCK_KEY = Key.create<RobotBlock>("PARENT_BLOCK")
        private val CURRENT_BLOCK_KEY = Key.create<RobotBlock>("CURRENT_BLOCK")
        private val TEMPLATE_VALUES_ALIGNMENT_KEY = Key.create<Array<Alignment>>("TEMPLATE_VALUE_ALIGNMENT")
        private val USER_KEYWORD_PARAMETER_ALIGNMENT = Key.create<Alignment>("USER_KEYWORD_PARAMETER_ALIGNMENT")
        private val KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT_KEY = Key.create<Alignment>("KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT")
        private val CONTINUATION_TOKEN = TokenType.WHITE_SPACE

        // Differs from whitespace set of RobotTokenSets because it also includes EOL and EOS, which are treated as whitespace for formatting purposes, but not for parsing.
        private val WHITESPACE_TYPES = TokenSet.create(CONTINUATION_TOKEN, RobotTypes.EOL, RobotTypes.EOS)
        private val SECTION_HEADER_TYPES =
            TokenSet.create(RobotTypes.SETTINGS_HEADER, RobotTypes.VARIABLES_HEADER, RobotTypes.TEST_CASES_HEADER, RobotTypes.TASKS_HEADER, RobotTypes.USER_KEYWORDS_HEADER, RobotTypes.COMMENTS_HEADER)
        private val CHILD_INDENTED_SECTION_TYPES = TokenSet.create(RobotTypes.TEST_CASES_SECTION, RobotTypes.TASKS_SECTION, RobotTypes.KEYWORDS_SECTION)
        private val LEAF_TYPES = TokenSet.create(RobotTypes.IMPORT_ARGUMENT,
            RobotTypes.USER_KEYWORD_STATEMENT_ID,
            RobotTypes.TEST_CASE_ID,
            RobotTypes.TASK_ID,
            RobotTypes.LOCAL_ARGUMENTS_SETTING_ID,
            RobotTypes.PARAMETER_ID,
            RobotTypes.TEMPLATE_PARAMETER_ID,
            RobotTypes.LOCAL_SETTING_ID,
            RobotTypes.KEYWORD_CALL_NAME)
        private val BLOCK_OPENING_TYPES = TokenSet.create(RobotTypes.TEST_CASE_STATEMENT,
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
            RobotTypes.GROUP_STRUCTURE)
        private val BLOCK_OPENING_PART_TYPES = TokenSet.create(RobotTypes.FOR_LOOP_HEADER,
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
            RobotTypes.TASK_ID)
        private val FLATTENABLE_TYPES = TokenSet.create(RobotTypes.EXECUTABLE_STATEMENT, RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER, RobotTypes.LITERAL_CONSTANT_VALUE)
    }

    private val parent: RobotBlock?
        get() = myNode.getUserData(PARENT_BLOCK_KEY)

    override fun buildChildren(): List<Block> {
        val blocks = mutableListOf<Block>()
        if (isLeaf) return blocks

        val parentWrap = createWrapIfNecessary()
        if (myNode.elementType === RobotTypes.KEYWORD_VARIABLE_STATEMENT) {
            myNode.putUserData(KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT_KEY, Alignment.createAlignment())
        } else if (myNode.elementType === RobotTypes.LOCAL_ARGUMENTS_SETTING && context.commonCodeStyleSettings.ALIGN_MULTILINE_PARAMETERS) {
            myNode.putUserData(USER_KEYWORD_PARAMETER_ALIGNMENT, Alignment.createAlignment(true))
        }

        fun addNewRobotBlock(alignmentIndex: Int, child: ASTNode) {
            if (shouldCreateBlockForNode(child)) {
                val indent = getIndentation(child)
                val childWrap = if (shouldAssignWrapToNode(child)) parentWrap else null
                val alignment = getAlignment(child, alignmentIndex)
                val childIndent = getChildIndent(child)
                RobotBlock(child, context, indent = indent, wrap = childWrap, alignment = alignment, childIndent = childIndent).apply {
                    blocks.add(this)
                    myNode.putUserData(CURRENT_BLOCK_KEY, this)
                    myNode.putUserData(PARENT_BLOCK_KEY, this@RobotBlock)
                }
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
        RobotTypes.KEYWORD_CALL -> {
            val wrapType = context.commonCodeStyleSettings.CALL_PARAMETERS_WRAP
            if (wrapType != CommonCodeStyleSettings.DO_NOT_WRAP) Wrap.createWrap(wrapType, isChopOrAlways(wrapType)) else null
        }

        RobotTypes.LOCAL_ARGUMENTS_SETTING -> {
            val wrapType = context.commonCodeStyleSettings.METHOD_PARAMETERS_WRAP
            if (wrapType != CommonCodeStyleSettings.DO_NOT_WRAP) Wrap.createWrap(wrapType, isChopOrAlways(wrapType)) else null
        }

        RobotTypes.LOCAL_SETTING -> {
            val wrapType = context.robotCodeStyleSettings.LOCAL_SETTINGS_WRAP
            if (wrapType != CommonCodeStyleSettings.DO_NOT_WRAP) Wrap.createWrap(wrapType, isChopOrAlways(wrapType)) else null
        }

        RobotTypes.FOR_LOOP_HEADER -> {
            val wrapType = context.commonCodeStyleSettings.FOR_STATEMENT_WRAP
            if (wrapType != CommonCodeStyleSettings.DO_NOT_WRAP) Wrap.createWrap(wrapType, isChopOrAlways(wrapType)) else null
        }

        RobotTypes.WHILE_LOOP_HEADER -> {
            val wrapType = context.robotCodeStyleSettings.WHILE_STATEMENT_WRAP
            if (wrapType != CommonCodeStyleSettings.DO_NOT_WRAP) Wrap.createWrap(wrapType, isChopOrAlways(wrapType)) else null
        }

        else -> null
    }

    /**
     * Returns true if the wrap type is one that should wrap ALL elements including the first one.
     * - [CommonCodeStyleSettings.WRAP_ON_EVERY_ITEM] (5): "Chop down if long" — all on own lines when line exceeds margin
     * - [CommonCodeStyleSettings.WRAP_ALWAYS] (2): always put every element on its own line
     */
    private fun isChopOrAlways(wrapType: Int): Boolean = wrapType == CommonCodeStyleSettings.WRAP_ALWAYS || wrapType == CommonCodeStyleSettings.WRAP_ON_EVERY_ITEM

    private fun getChildIndent(child: ASTNode): Indent? = when {
        BLOCK_OPENING_TYPES.contains(child.elementType) || CHILD_INDENTED_SECTION_TYPES.contains(child.elementType) -> Indent.getNormalIndent()

        SECTION_HEADER_TYPES.contains(child.elementType) || child.elementType === CONTINUATION_TOKEN -> Indent.getNoneIndent()

        else -> Indent.getContinuationIndent()
    }

    private fun shouldAssignWrapToNode(node: ASTNode): Boolean = when (node.elementType) {
        RobotTypes.PARAMETER, RobotTypes.POSITIONAL_ARGUMENT -> myNode.elementType === RobotTypes.KEYWORD_CALL || myNode.elementType === RobotTypes.FOR_LOOP_HEADER || myNode.elementType === RobotTypes.WHILE_LOOP_HEADER || myNode.elementType === RobotTypes.LOCAL_SETTING

        RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER_MANDATORY, RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER_OPTIONAL -> true

        else -> false
    }

    private fun findFirstNestedArgument(node: ASTNode, argumentTypes: TokenSet): ASTNode? {
        for (child in node.children()) {
            if (argumentTypes.contains(child.elementType)) return child
            if (FLATTENABLE_TYPES.contains(child.elementType)) {
                val nested = findFirstNestedArgument(child, argumentTypes)
                if (nested != null) return nested
            }
        }
        return null
    }

    private fun getIndentation(node: ASTNode): Indent? = if (BLOCK_OPENING_TYPES.contains(myNode.elementType) && !BLOCK_OPENING_PART_TYPES.contains(node.elementType)) Indent.getNormalIndent()
    else if (node.elementType === CONTINUATION_TOKEN) Indent.getContinuationIndent()
    else Indent.getNoneIndent()

    private fun getAlignment(node: ASTNode, index: Int): Alignment? = when {
        RobotTokenSets.TEMPLATE_VALUES_HOLDER_SET.contains(node.elementType) -> myNode.getUserData(TEMPLATE_VALUES_ALIGNMENT_KEY)?.let { if (index < it.size) it[index] else null }
        node.elementType === RobotTypes.VARIABLE_DEFINITION -> myNode.getUserData(KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT_KEY)
        node.elementType === RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER_MANDATORY || node.elementType === RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER_OPTIONAL -> myNode.getUserData(
            USER_KEYWORD_PARAMETER_ALIGNMENT)

        node.elementType === CONTINUATION_TOKEN -> myNode.treeParent?.getUserData(KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT_KEY)
        else -> null
    }

    override fun getIndent(): Indent? = indent

    override fun getAlignment(): Alignment? = when {
        myNode.elementType === CONTINUATION_TOKEN && !context.robotCodeStyleSettings.ALIGN_CONTINUATION_WITH_VARIABLE_DEFINITION -> null
        else -> super.alignment
    }

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        val myParent = parent
        if (myParent == null && newChildIndex >= subBlocks.size) {
            return ChildAttributes.DELEGATE_TO_PREV_CHILD
        } else if (myParent != null && isChildAttributesForContinuationTokenRequested(newChildIndex)) {
            return ChildAttributes.DELEGATE_TO_PREV_CHILD
        } else if (myNode.elementType === RobotTypes.ROOT) {
            val lastSubBlock = subBlocks.lastOrNull()
            val childAttributes = lastSubBlock?.getChildAttributes(lastSubBlock.subBlocks.size)
            if (childAttributes != null) return childAttributes
        }
        return super.getChildAttributes(newChildIndex)
    }

    private fun isChildAttributesForContinuationTokenRequested(newChildIndex: Int): Boolean =
        newChildIndex > 0 && ((subBlocks[newChildIndex - 1] as ASTBlock).node?.elementType === CONTINUATION_TOKEN || newChildIndex < subBlocks.size && (subBlocks[newChildIndex] as ASTBlock).node?.elementType === CONTINUATION_TOKEN)

    override fun getChildIndent(): Indent? = childIndent

    override fun getSpacing(child1: Block?, child2: Block): Spacing? = context.spacingBuilder.getSpacing(this, child1, child2)

    override fun isLeaf(): Boolean = LEAF_TYPES.contains(myNode.elementType) || myNode.firstChildNode === null
}
