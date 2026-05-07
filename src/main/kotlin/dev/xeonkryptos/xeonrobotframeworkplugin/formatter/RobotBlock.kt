package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.formatting.ASTBlock
import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.ChildAttributes
import com.intellij.formatting.Indent
import com.intellij.formatting.Spacing
import com.intellij.formatting.Wrap
import com.intellij.formatting.WrapType
import com.intellij.lang.ASTNode
import com.intellij.lang.tree.util.children
import com.intellij.lang.tree.util.parents
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.tree.TokenSet
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class RobotBlock(node: ASTNode, private val context: RobotBlockContext, wrap: Wrap? = null, alignment: Alignment? = null, private val indent: Indent? = null, private val childIndent: Indent? = null) :
    AbstractBlock(node, wrap, alignment) {
    companion object {
        private val PARENT_BLOCK_KEY = Key.create<RobotBlock>("PARENT_BLOCK")

        private val TEMPLATE_VALUES_ALIGNMENT_WITH_DATA_DRIVEN_HEADER_KEY = Key.create<MutableList<Alignment>>("TEMPLATE_VALUES_ALIGNMENT_WITH_DATA_DRIVEN_HEADER")
        private val TEMPLATE_VALUES_ALIGNMENT_KEY = Key.create<MutableList<Alignment>>("TEMPLATE_VALUE_ALIGNMENT")
        private val IGNORE_DATA_COLUMN_ALIGNMENT_KEY = Key.create<Boolean>("IGNORE_DATA_COLUMN_ALIGNMENT")
        private val TEMPLATE_ALIGNMENT_INDEX_KEY = Key.create<Int>("TEMPLATE_ALIGNMENT_INDEX")
        private val KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT_KEY = Key.create<Alignment>("KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT")
        private val SINGLE_VARIABLE_STATEMENT_FIRST_ARGUMENT_ALIGNMENT_KEY = Key.create<Alignment>("SINGLE_VARIABLE_STATEMENT_FIRST_ARGUMENT_ALIGNMENT")

        private val PARENT_WRAP_KEY = Key.create<Wrap>("PARENT_WRAP")

        // Differs from whitespace set of RobotTokenSets because it also includes EOL and EOS, which are treated as whitespace for formatting purposes, but not for parsing.
        private val WHITESPACE_TYPES = TokenSet.create(TokenType.WHITE_SPACE, RobotTypes.EOL, RobotTypes.EOS)
        private val SECTION_TYPES = TokenSet.orSet(RobotTokenSets.SECTIONS_HEADER_SET,
            TokenSet.create(RobotTypes.TEST_CASES_SECTION,
                RobotTypes.TASKS_SECTION,
                RobotTypes.KEYWORDS_SECTION,
                RobotTypes.VARIABLES_SECTION,
                RobotTypes.COMMENTS_SECTION,
                RobotTypes.TEST_CASES_HEADER_NAME,
                RobotTypes.TASKS_HEADER_NAME,
                RobotTypes.DATA_DRIVEN_COLUMN_NAME))
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
            RobotTypes.EXCEPT_HEADER,
            RobotTypes.IF,
            RobotTypes.ELSE_IF,
            RobotTypes.ELSE,
            RobotTypes.TRY,
            RobotTypes.EXCEPT,
            RobotTypes.FINALLY,
            RobotTypes.END,
            RobotTypes.USER_KEYWORD_STATEMENT_ID,
            RobotTypes.TEST_CASE_ID,
            RobotTypes.TASK_ID)
        private val PARENT_NODES_FOR_PARAMETER_AND_POSITIONAL_ARGUMENT_TO_RETRIEVE_WRAPS = TokenSet.create(RobotTypes.KEYWORD_CALL,
            RobotTypes.FOR_LOOP_HEADER,
            RobotTypes.WHILE_LOOP_HEADER,
            RobotTypes.LOCAL_SETTING,
            RobotTypes.LIBRARY_IMPORT_GLOBAL_SETTING,
            RobotTypes.RESOURCE_IMPORT_GLOBAL_SETTING,
            RobotTypes.VARIABLES_IMPORT_GLOBAL_SETTING,
            RobotTypes.TAGS_STATEMENT_GLOBAL_SETTING,
            RobotTypes.SUITE_NAME_STATEMENT_GLOBAL_SETTING,
            RobotTypes.TIMEOUT_STATEMENTS_GLOBAL_SETTING,
            RobotTypes.METADATA_STATEMENT_GLOBAL_SETTING,
            RobotTypes.DOCUMENTATION_STATEMENT_GLOBAL_SETTING,
            RobotTypes.TEMPLATE_STATEMENTS_GLOBAL_SETTING,
            RobotTypes.UNKNOWN_SETTING_STATEMENTS_GLOBAL_SETTING)

        private val TEMPLATE_ALIGNMENT_INDEX_INCREASER_SET = TokenSet.orSet(RobotTokenSets.TEMPLATE_VALUES_HOLDER_SET, TokenSet.create(RobotTypes.DATA_DRIVEN_COLUMN_NAME))

        private val DELEGATE_TO_PREV_CHILD_SET = TokenSet.create(RobotTypes.ROOT, RobotTypes.TEST_CASES_SECTION, RobotTypes.TASKS_SECTION, RobotTypes.KEYWORDS_SECTION)
    }

    private val parent: RobotBlock?
        get() = myNode.getUserData(PARENT_BLOCK_KEY)

    override fun buildChildren(): List<Block> {
        val blocks = mutableListOf<Block>()
        if (isLeaf) return blocks

        val parentWrap = createWrapIfNecessary()
        initializeUserData()

        var templateAlignmentIndex = 0
        myNode.children().filter { !WHITESPACE_TYPES.contains(it.elementType) }.forEach { child ->
            val indent = getIndentation(child)
            val childWrap = if (shouldAssignWrapToNode(child)) parentWrap else null
            val alignment = getAlignment(child)

            if (TEMPLATE_ALIGNMENT_INDEX_INCREASER_SET.contains(child.elementType)) {
                child.putUserData(TEMPLATE_ALIGNMENT_INDEX_KEY, templateAlignmentIndex)
                templateAlignmentIndex++
            }

            val childIndent = getChildIndent(child)
            RobotBlock(child, context, indent = indent, wrap = childWrap, alignment = alignment, childIndent = childIndent).apply {
                blocks.add(this)
                myNode.putUserData(PARENT_BLOCK_KEY, this@RobotBlock)
            }

            if (myNode.elementType === RobotTypes.SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING) {
                child.putUserData(PARENT_WRAP_KEY, parentWrap)
            }
            if (child.elementType === RobotTypes.LOCAL_SETTING && (child.psi as RobotLocalSetting).settingName == RobotNames.TEMPLATE_LOCAL_SETTING_NAME) {
                myNode.putUserData(IGNORE_DATA_COLUMN_ALIGNMENT_KEY, true)
            }
        }
        return blocks
    }

    private fun initializeUserData() {
        when {
            myNode.elementType === RobotTypes.KEYWORD_VARIABLE_STATEMENT -> {
                myNode.putUserData(KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT_KEY, Alignment.createAlignment())
            }

            myNode.elementType === RobotTypes.TEST_CASES_HEADER && context.robotCodeStyleSettings.ALIGN_TEMPLATE_ARGUMENTS_WITH_DATA_DRIVEN_NAMES -> {
                val dataDrivenElements = myNode.getChildren(TokenSet.create(RobotTypes.DATA_DRIVEN_COLUMN_NAME)).map { Alignment.createAlignment(true) }.toMutableList()
                parent?.node?.putUserData(TEMPLATE_VALUES_ALIGNMENT_WITH_DATA_DRIVEN_HEADER_KEY, dataDrivenElements)
            }

            myNode.elementType === RobotTypes.TEMPLATE_ARGUMENTS && context.robotCodeStyleSettings.ALIGN_TEMPLATE_ARGUMENTS_WITH_EACH_OTHER -> {
                myNode.parents(false).firstOrNull { it.elementType === RobotTypes.TEST_CASE_STATEMENT || it.elementType === RobotTypes.TASK_STATEMENT }?.let {
                    val alignments = myNode.getChildren(RobotTokenSets.TEMPLATE_VALUES_HOLDER_SET).map { Alignment.createAlignment(true) }.toMutableList()
                    it.putUserData(TEMPLATE_VALUES_ALIGNMENT_KEY, alignments)
                }
            }

            myNode.elementType === RobotTypes.VARIABLE_STATEMENTS -> {
                myNode.putUserData(SINGLE_VARIABLE_STATEMENT_FIRST_ARGUMENT_ALIGNMENT_KEY, Alignment.createAlignment(true))
            }
        }
    }

    private fun createWrapIfNecessary(): Wrap? = when (myNode.elementType) {
        RobotTypes.KEYWORD_CALL -> {
            if (!context.robotCodeStyleSettings.KEEP_SIMPLE_KEYWORD_CALLS_IN_ONE_LINE || myNode.getChildren(RobotTokenSets.ARGUMENTS_TYPE_SET).size > 1) {
                val wrapType = context.commonCodeStyleSettings.CALL_PARAMETERS_WRAP
                val parentWrap = myNode.getUserData(PARENT_WRAP_KEY)
                if (parentWrap != null) Wrap.createChildWrap(parentWrap, WrapType.byLegacyRepresentation(wrapType), context.robotCodeStyleSettings.CALL_PARAMETERS_FIRST_ARGUMENT_ON_NEW_LINE)
                else Wrap.createWrap(wrapType, context.robotCodeStyleSettings.CALL_PARAMETERS_FIRST_ARGUMENT_ON_NEW_LINE)
            } else null
        }

        RobotTypes.LOCAL_ARGUMENTS_SETTING -> {
            val wrapType = context.commonCodeStyleSettings.METHOD_PARAMETERS_WRAP
            Wrap.createWrap(wrapType, context.robotCodeStyleSettings.METHOD_PARAMETERS_FIRST_ARGUMENT_ON_NEW_LINE)
        }

        RobotTypes.LOCAL_SETTING -> {
            val wrapType = context.robotCodeStyleSettings.LOCAL_SETTINGS_WRAP
            Wrap.createWrap(wrapType, context.robotCodeStyleSettings.LOCAL_SETTINGS_FIRST_ARGUMENT_ON_NEW_LINE)
        }

        RobotTypes.FOR_LOOP_HEADER -> {
            val wrapType = context.commonCodeStyleSettings.FOR_STATEMENT_WRAP
            Wrap.createWrap(wrapType, context.robotCodeStyleSettings.FOR_FIRST_ARGUMENT_ON_NEW_LINE)
        }

        RobotTypes.WHILE_LOOP_HEADER -> {
            val wrapType = context.robotCodeStyleSettings.WHILE_STATEMENT_WRAP
            Wrap.createWrap(wrapType, context.robotCodeStyleSettings.WHILE_FIRST_ARGUMENT_ON_NEW_LINE)
        }

        RobotTypes.SINGLE_VARIABLE_STATEMENT -> {
            val wrapType = context.robotCodeStyleSettings.VARIABLE_DEFINITIONS_WRAP
            Wrap.createWrap(wrapType, context.robotCodeStyleSettings.VARIABLE_DEFINITIONS_FIRST_ARGUMENT_ON_NEW_LINE)
        }

        // @formatter:off
        RobotTypes.LIBRARY_IMPORT_GLOBAL_SETTING,
        RobotTypes.RESOURCE_IMPORT_GLOBAL_SETTING,
        RobotTypes.VARIABLES_IMPORT_GLOBAL_SETTING,
        RobotTypes.TAGS_STATEMENT_GLOBAL_SETTING,
        RobotTypes.SUITE_NAME_STATEMENT_GLOBAL_SETTING,
        RobotTypes.TIMEOUT_STATEMENTS_GLOBAL_SETTING,
        RobotTypes.METADATA_STATEMENT_GLOBAL_SETTING,
        RobotTypes.DOCUMENTATION_STATEMENT_GLOBAL_SETTING,
        RobotTypes.TEMPLATE_STATEMENTS_GLOBAL_SETTING,
        RobotTypes.UNKNOWN_SETTING_STATEMENTS_GLOBAL_SETTING,
        RobotTypes.SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING -> {
        // @formatter:on
            val tokenSet = TokenSet.create(RobotTypes.IMPORT_ARGUMENT, RobotTypes.KEYWORD_CALL, RobotTypes.POSITIONAL_ARGUMENT, RobotTypes.PARAMETER, RobotTypes.LITERAL_CONSTANT, RobotTypes.LITERAL_CONSTANT_VALUE, RobotTypes.VARIABLE)
            if (!context.robotCodeStyleSettings.KEEP_SIMPLE_GLOBAL_SETTINGS_IN_ONE_LINE || myNode.getChildren(tokenSet).size > 1) {
                val wrapType = context.robotCodeStyleSettings.GLOBAL_SETTINGS_WRAP
                Wrap.createWrap(wrapType, context.robotCodeStyleSettings.GLOBAL_SETTINGS_FIRST_ARGUMENT_ON_NEW_LINE)
            } else null
        }

        else -> null
    }

    private fun getChildIndent(child: ASTNode): Indent? = when {
        BLOCK_OPENING_TYPES.contains(child.elementType) -> Indent.getNormalIndent()

        SECTION_TYPES.contains(child.elementType) -> Indent.getNoneIndent()

        else -> Indent.getContinuationIndent()
    }

    private fun shouldAssignWrapToNode(node: ASTNode): Boolean = when (node.elementType) {
        RobotTypes.PARAMETER, RobotTypes.POSITIONAL_ARGUMENT -> PARENT_NODES_FOR_PARAMETER_AND_POSITIONAL_ARGUMENT_TO_RETRIEVE_WRAPS.contains(myNode.elementType)

        RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER -> true

        RobotTypes.LITERAL_CONSTANT, RobotTypes.IMPORT_ARGUMENT, RobotTypes.KEYWORD_CALL -> RobotTokenSets.GLOBAL_SETTING_SET.contains(myNode.elementType)

        RobotTypes.VARIABLE_VALUE -> myNode.elementType === RobotTypes.SINGLE_VARIABLE_STATEMENT

        else -> false
    }

    private fun getIndentation(node: ASTNode): Indent? = when {
        BLOCK_OPENING_TYPES.contains(myNode.elementType) && !BLOCK_OPENING_PART_TYPES.contains(node.elementType) -> Indent.getNormalIndent()
        node.elementType === RobotTypes.TEMPLATE_ARGUMENTS -> Indent.getNormalIndent()
        else -> Indent.getNoneIndent()
    }

    override fun getAlignment(): Alignment? {
        if (TEMPLATE_ALIGNMENT_INDEX_INCREASER_SET.contains(myNode.elementType)) {
            val alignmentIndex = myNode.getUserData(TEMPLATE_ALIGNMENT_INDEX_KEY) ?: return null
            return extractTemplateArgumentAlignment(alignmentIndex)
        }
        return super.getAlignment()
    }

    private fun getAlignment(node: ASTNode): Alignment? = when {
        node.elementType === RobotTypes.VARIABLE_DEFINITION -> myNode.getUserData(KEYWORD_VARIABLE_STATEMENT_VARIABLE_ALIGNMENT_KEY)

        // @formatter:off
            node.elementType === RobotTypes.VARIABLE_VALUE
                && myNode.elementType === RobotTypes.SINGLE_VARIABLE_STATEMENT
                && isAlignmentForVariableValueInSingleVariableStatementAvailable(node) -> parent?.node?.getUserData(SINGLE_VARIABLE_STATEMENT_FIRST_ARGUMENT_ALIGNMENT_KEY)
            // @formatter:on
        else -> null
    }

    private fun extractTemplateArgumentAlignment(alignmentIndex: Int): Alignment? {
        if (context.robotCodeStyleSettings.ALIGN_TEMPLATE_ARGUMENTS_WITH_DATA_DRIVEN_NAMES) {
            val parents = myNode.parents(false)
                .filter { it.elementType === RobotTypes.TEST_CASE_STATEMENT || it.elementType === RobotTypes.TEST_CASES_SECTION || it.elementType === RobotTypes.TASKS_SECTION }
                .toList()

            if (parents.none { it.getUserData(IGNORE_DATA_COLUMN_ALIGNMENT_KEY) == true }) {
                parents.firstNotNullOfOrNull { it.getUserData(TEMPLATE_VALUES_ALIGNMENT_WITH_DATA_DRIVEN_HEADER_KEY) }
                    ?.let { return if (alignmentIndex < it.size) it[alignmentIndex] else Alignment.createAlignment(true).apply { it.add(this) } }
            }
        }
        return if (context.robotCodeStyleSettings.ALIGN_TEMPLATE_ARGUMENTS_WITH_EACH_OTHER) myNode.parents(true)
            .firstNotNullOfOrNull { it.getUserData(TEMPLATE_VALUES_ALIGNMENT_KEY) }
            ?.let { if (alignmentIndex < it.size) it[alignmentIndex] else Alignment.createAlignment(true).apply { it.add(this) } }
        else null
    }

    private fun isAlignmentForVariableValueInSingleVariableStatementAvailable(node: ASTNode) =
        context.robotCodeStyleSettings.VARIABLE_DEFINITIONS_WRAP == CommonCodeStyleSettings.DO_NOT_WRAP && context.robotCodeStyleSettings.VARIABLE_DEFINITIONS_ALIGN_FIRST_ARGUMENT && myNode.children()
            .filter { !WHITESPACE_TYPES.contains(it.elementType) }
            .firstOrNull { it.elementType === RobotTypes.VARIABLE_VALUE } == node

    override fun getIndent(): Indent? = indent

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        val myParent = parent
        when {
            myParent == null && newChildIndex >= subBlocks.size -> return ChildAttributes.DELEGATE_TO_PREV_CHILD

            myNode.psi is PsiFile -> return ChildAttributes.DELEGATE_TO_PREV_CHILD

            DELEGATE_TO_PREV_CHILD_SET.contains(myNode.elementType) -> return ChildAttributes.DELEGATE_TO_PREV_CHILD
        }
        return super.getChildAttributes(newChildIndex)
    }

    override fun getChildIndent(): Indent? = childIndent

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        val child1ElementType = ASTBlock.getElementType(child1)
        if (child1ElementType === RobotTypes.TEST_CASE_ID || child1ElementType === RobotTypes.TASK_ID) {
            val child2ElementType = ASTBlock.getElementType(child2)
            if (child2ElementType === RobotTypes.TEMPLATE_ARGUMENTS) {
                return Spacing.createSpacing(RobotCodeStyleSettings.SUPER_SPACE_SIZE, 0, 0, context.commonCodeStyleSettings.KEEP_LINE_BREAKS, context.commonCodeStyleSettings.KEEP_BLANK_LINES_IN_CODE)
            }
        }
        return context.spacingBuilder.getSpacing(this, child1, child2)
    }

    override fun isLeaf(): Boolean = myNode.firstChildNode === null
}
