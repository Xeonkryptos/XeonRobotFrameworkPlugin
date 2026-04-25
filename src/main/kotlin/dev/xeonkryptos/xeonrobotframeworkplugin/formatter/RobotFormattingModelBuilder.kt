package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.formatting.CustomFormattingModelBuilder
import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.SpacingBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.tree.TokenSet
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import kotlin.math.max

private val SINGLE_GLOBAL_SETTING_STATEMENTS_SET = TokenSet.create(RobotTypes.DOCUMENTATION_STATEMENT_GLOBAL_SETTING,
    RobotTypes.SUITE_NAME_STATEMENT_GLOBAL_SETTING,
    RobotTypes.TAGS_STATEMENT_GLOBAL_SETTING,
    RobotTypes.TEMPLATE_STATEMENTS_GLOBAL_SETTING,
    RobotTypes.TIMEOUT_STATEMENTS_GLOBAL_SETTING,
    RobotTypes.UNKNOWN_SETTING_STATEMENTS_GLOBAL_SETTING)

private val SUPER_SPACE_SETS: TokenSet = TokenSet.orSet(RobotTokenSets.GHERKIN_SET,
    RobotTokenSets.LOOP_KEYWORDS_SET,
    RobotTokenSets.CONTROL_KEYWORDS_SET,
    RobotTokenSets.EXCEPTION_KEYWORDS_SET,
    RobotTokenSets.LOCAL_SETTING_NAMES_SET,
    RobotTokenSets.GLOBAL_SETTING_NAMES_SET,
    RobotTokenSets.ARGUMENTS_TYPE_SET,
    TokenSet.create(RobotTypes.VAR,
        RobotTypes.KEYWORD_NAME,
        RobotTypes.TEST_CASES_HEADER_NAME,
        RobotTypes.TASKS_HEADER_NAME,
        RobotTypes.DATA_DRIVEN_COLUMN_NAME,
        RobotTypes.TEST_CASE_NAME_PART,
        RobotTypes.TASK_NAME_PART))

private val VARIABLE_DEFINITION_WITH_OPTIONAL_ASSIGNMENT_SET = TokenSet.create(RobotTypes.VARIABLE_DEFINITION, RobotTypes.ASSIGNMENT)

private val VARIABLE_VALUE_SINGLE_SET = TokenSet.create(RobotTypes.VARIABLE_VALUE)

class RobotFormattingModelBuilder : CustomFormattingModelBuilder {

    override fun isEngagedToFormat(context: PsiElement?): Boolean = context?.containingFile?.language == RobotLanguage.INSTANCE

    override fun createModel(context: FormattingContext): FormattingModel {
        val element = context.psiElement
        val commonSettings = context.codeStyleSettings.getCommonSettings(RobotLanguage.INSTANCE)
        val customSettings = context.codeStyleSettings.getCustomSettings(RobotCodeStyleSettings::class.java)
        val spaceBuilder = createSpaceBuilder(context.codeStyleSettings)
        val blockContext = RobotBlockContext(commonSettings, customSettings, spaceBuilder)
        val robotBlock = RobotBlock(element.node, blockContext)
        return RobotFormattingModel(element.containingFile, robotBlock)
    }

    private fun createSpaceBuilder(codeStyleSettings: CodeStyleSettings): SpacingBuilder {
        val commonSettings = codeStyleSettings.getCommonSettings(RobotLanguage.INSTANCE)
        val customSettings = codeStyleSettings.getCustomSettings(RobotCodeStyleSettings::class.java)
        val afterContinuationSpaceSize = max(customSettings.AFTER_CONTINUATION_INDENT_SIZE, RobotCodeStyleSettings.SUPER_SPACE_SIZE)

        // @formatter:off
        return SpacingBuilder(codeStyleSettings, RobotLanguage.INSTANCE)
            .before(RobotTokenSets.SECTIONS_HEADER_SET)
            .none()
            // @formatter:on
            .after(RobotTokenSets.SECTIONS_HEADER_SET)
            .lineBreakInCode()
            .before(RobotTypes.IMPORT_SETTINGS)
            .blankLines(commonSettings.BLANK_LINES_BEFORE_IMPORTS)
            .after(RobotTypes.IMPORT_SETTINGS)
            .blankLines(commonSettings.BLANK_LINES_AFTER_IMPORTS)
            .before(RobotTypes.SETUP_TEARDOWN_SETTINGS)
            .blankLines(customSettings.BLANK_LINES_BEFORE_GLOBAL_SETUP_TEARDOWN)
            .after(RobotTypes.SETUP_TEARDOWN_SETTINGS)
            .blankLines(customSettings.BLANK_LINES_AFTER_GLOBAL_SETUP_TEARDOWN)
            .before(SINGLE_GLOBAL_SETTING_STATEMENTS_SET)
            .blankLines(customSettings.BLANK_LINES_BEFORE_GLOBAL_SETTING)
            .after(SINGLE_GLOBAL_SETTING_STATEMENTS_SET)
            .blankLines(customSettings.BLANK_LINES_AFTER_GLOBAL_SETTING)
            .before(RobotTypes.METADATA_SETTINGS)
            .blankLines(customSettings.BLANK_LINES_BEFORE_GLOBAL_METADATA)
            .after(RobotTypes.METADATA_SETTINGS)
            .blankLines(customSettings.BLANK_LINES_AFTER_GLOBAL_METADATA)
            .before(RobotTypes.VARIABLE_STATEMENTS)
            .blankLines(customSettings.BLANK_LINES_BEFORE_VARIABLE_STATEMENTS)
            .after(RobotTypes.VARIABLE_STATEMENTS)
            .blankLines(customSettings.BLANK_LINES_AFTER_VARIABLE_STATEMENTS)
            .before(RobotTypes.CONTINUATION)
            .lineBreakInCode()
            .after(RobotTypes.CONTINUATION)
            .spaces(afterContinuationSpaceSize)
            .after(TokenSet.create(RobotTypes.TEST_CASE_ID, RobotTypes.TASK_ID, RobotTypes.USER_KEYWORD_STATEMENT_ID))
            .blankLines(commonSettings.BLANK_LINES_AFTER_CLASS_HEADER)
            .after(TokenSet.create(RobotTypes.TEMPLATE_ARGUMENTS, RobotTypes.LOCAL_SETTING))
            .lineBreakInCode()
            .after(RobotTokenSets.TEMPLATE_VALUES_HOLDER_SET)
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
            .between(RobotTypes.VARIABLE_DEFINITION, RobotTypes.ASSIGNMENT)
            .spaceIf(commonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
            .betweenInside(VARIABLE_DEFINITION_WITH_OPTIONAL_ASSIGNMENT_SET, VARIABLE_VALUE_SINGLE_SET, RobotTypes.SINGLE_VARIABLE_STATEMENT)
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
            .betweenInside(VARIABLE_DEFINITION_WITH_OPTIONAL_ASSIGNMENT_SET, VARIABLE_VALUE_SINGLE_SET, RobotTypes.INLINE_VARIABLE_STATEMENT)
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
            .betweenInside(VARIABLE_DEFINITION_WITH_OPTIONAL_ASSIGNMENT_SET, TokenSet.ANY, RobotTypes.EMPTY_VARIABLE_STATEMENT)
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
            .betweenInside(VARIABLE_DEFINITION_WITH_OPTIONAL_ASSIGNMENT_SET, TokenSet.create(RobotTypes.KEYWORD_CALL), RobotTypes.KEYWORD_VARIABLE_STATEMENT)
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
            .betweenInside(VARIABLE_DEFINITION_WITH_OPTIONAL_ASSIGNMENT_SET, TokenSet.create(RobotTypes.INLINE_IF_STRUCTURE), RobotTypes.IF_VARIABLE_STATEMENT)
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
            .afterInside(RobotTypes.KEYWORD_CALL, TokenSet.create(RobotTypes.TEST_CASE_STATEMENT, RobotTypes.TASK_STATEMENT, RobotTypes.USER_KEYWORD_STATEMENT))
            .lineBreakInCode()
            .betweenInside(TokenSet.create(RobotTypes.ASSIGNMENT), TokenSet.ANY, RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER_OPTIONAL)
            .none()
            .around(TokenSet.orSet(TokenSet.create(RobotTypes.WITH_NAME), RobotTokenSets.FOR_LOOP_IN_TYPES))
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
            .after(RobotTypes.VARIABLE_LBRACE)
            .spaceIf(commonSettings.SPACE_WITHIN_BRACES)
            .before(RobotTypes.VARIABLE_RBRACE)
            .spaceIf(commonSettings.SPACE_WITHIN_BRACES)
            .after(TokenSet.create(RobotTypes.LOCAL_SETTING_START, RobotTypes.VARIABLE_ACCESS_START))
            .spaceIf(commonSettings.SPACE_WITHIN_BRACKETS)
            .before(TokenSet.create(RobotTypes.LOCAL_SETTING_END, RobotTypes.VARIABLE_ACCESS_END))
            .spaceIf(commonSettings.SPACE_WITHIN_BRACKETS)
            .between(TokenSet.create(RobotTypes.KEYWORD_CALL_NAME), RobotTokenSets.ARGUMENTS_TYPE_SET)
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
            .after(SUPER_SPACE_SETS)
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
    }
}
