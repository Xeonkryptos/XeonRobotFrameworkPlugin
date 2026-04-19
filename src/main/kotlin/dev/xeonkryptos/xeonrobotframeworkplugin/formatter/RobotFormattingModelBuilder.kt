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
        val maximumSpacesAfterTemplateValues = if (customSettings.KEEP_ADDITIONAL_SPACES_BETWEEN_TEMPLATE_VALUES) Integer.MAX_VALUE else RobotCodeStyleSettings.SUPER_SPACE_SIZE
        val maximumSpacesAfterVariableAssignment = if (customSettings.KEEP_ADDITIONAL_SPACES_AFTER_VARIABLE_ASSIGNMENTS) Integer.MAX_VALUE else RobotCodeStyleSettings.SUPER_SPACE_SIZE

        return SpacingBuilder(codeStyleSettings, RobotLanguage.INSTANCE).before(RobotTokenSets.SECTIONS_HEADER_SET)
            .spacing(0, 0, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .after(RobotTokenSets.SECTIONS_HEADER_SET)
            .blankLines(0)
            .before(SINGLE_GLOBAL_SETTING_STATEMENTS_SET)
            .spacing(0, 0, customSettings.BLANK_LINES_BEFORE_GLOBAL_SETTING, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .after(SINGLE_GLOBAL_SETTING_STATEMENTS_SET)
            .blankLines(customSettings.BLANK_LINES_AFTER_GLOBAL_SETTING)
            .before(RobotTypes.IMPORT_SETTINGS)
            .spacing(0, 0, commonSettings.BLANK_LINES_BEFORE_IMPORTS, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .after(RobotTypes.IMPORT_SETTINGS)
            .blankLines(commonSettings.BLANK_LINES_AFTER_IMPORTS)
            .before(RobotTypes.SETUP_TEARDOWN_SETTINGS)
            .spacing(0, 0, customSettings.BLANK_LINES_BEFORE_GLOBAL_SETUP_TEARDOWN, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .after(RobotTypes.SETUP_TEARDOWN_SETTINGS)
            .blankLines(customSettings.BLANK_LINES_AFTER_GLOBAL_SETUP_TEARDOWN)
            .before(RobotTypes.METADATA_SETTINGS)
            .spacing(0, 0, customSettings.BLANK_LINES_BEFORE_GLOBAL_METADATA, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .after(RobotTypes.METADATA_SETTINGS)
            .blankLines(customSettings.BLANK_LINES_AFTER_GLOBAL_METADATA)
            .before(RobotTypes.VARIABLE_STATEMENTS)
            .spacing(0, 0, customSettings.BLANK_LINES_BEFORE_VARIABLE_STATEMENTS, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .after(RobotTypes.VARIABLE_STATEMENTS)
            .blankLines(customSettings.BLANK_LINES_AFTER_VARIABLE_STATEMENTS)
            .after(SUPER_SPACE_SETS)
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
            .after(TokenSet.create(RobotTypes.TEST_CASE_ID, RobotTypes.TASK_ID, RobotTypes.USER_KEYWORD_STATEMENT_ID))
            .blankLines(commonSettings.BLANK_LINES_AFTER_CLASS_HEADER)
            .after(RobotTokenSets.TEMPLATE_VALUES_HOLDER_SET)
            .spacing(RobotCodeStyleSettings.SUPER_SPACE_SIZE, maximumSpacesAfterTemplateValues, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .between(RobotTypes.VARIABLE_DEFINITION, RobotTypes.ASSIGNMENT)
            .spaceIf(commonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
            .betweenInside(TokenSet.create(RobotTypes.ASSIGNMENT), TokenSet.ANY, RobotTypes.SINGLE_VARIABLE_STATEMENT)
            .spacing(RobotCodeStyleSettings.SUPER_SPACE_SIZE, maximumSpacesAfterVariableAssignment, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .betweenInside(TokenSet.create(RobotTypes.ASSIGNMENT), TokenSet.ANY, RobotTypes.INLINE_VARIABLE_STATEMENT)
            .spacing(RobotCodeStyleSettings.SUPER_SPACE_SIZE, maximumSpacesAfterVariableAssignment, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .betweenInside(TokenSet.create(RobotTypes.ASSIGNMENT), TokenSet.ANY, RobotTypes.EMPTY_VARIABLE_STATEMENT)
            .spacing(RobotCodeStyleSettings.SUPER_SPACE_SIZE, maximumSpacesAfterVariableAssignment, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .betweenInside(TokenSet.create(RobotTypes.ASSIGNMENT), TokenSet.ANY, RobotTypes.KEYWORD_VARIABLE_STATEMENT)
            .spacing(RobotCodeStyleSettings.SUPER_SPACE_SIZE, maximumSpacesAfterVariableAssignment, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .betweenInside(TokenSet.create(RobotTypes.ASSIGNMENT), TokenSet.ANY, RobotTypes.IF_VARIABLE_STATEMENT)
            .spacing(RobotCodeStyleSettings.SUPER_SPACE_SIZE, maximumSpacesAfterVariableAssignment, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .around(TokenSet.orSet(TokenSet.create(RobotTypes.WITH_NAME), RobotTokenSets.FOR_LOOP_IN_TYPES))
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
            .afterInside(RobotTypes.KEYWORD_CALL, TokenSet.create(RobotTypes.TEST_CASE_STATEMENT, RobotTypes.TASK_STATEMENT, RobotTypes.USER_KEYWORD_STATEMENT))
            .blankLines(0)
            .betweenInside(TokenSet.create(RobotTypes.ASSIGNMENT), TokenSet.ANY, RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER_OPTIONAL)
            .spaces(0)
            .after(RobotTypes.VARIABLE_LBRACE)
            .spaceIf(commonSettings.SPACE_WITHIN_BRACES)
            .before(RobotTypes.VARIABLE_RBRACE)
            .spaceIf(commonSettings.SPACE_WITHIN_BRACES)
            .after(TokenSet.create(RobotTypes.LOCAL_SETTING_START, RobotTypes.VARIABLE_ACCESS_START))
            .spaceIf(commonSettings.SPACE_WITHIN_BRACKETS)
            .before(TokenSet.create(RobotTypes.LOCAL_SETTING_END, RobotTypes.VARIABLE_ACCESS_END))
            .spaceIf(commonSettings.SPACE_WITHIN_BRACKETS) //.after(TokenType.WHITE_SPACE)
            //.spacing(customSettings.AFTER_CONTINUATION_INDENT_SIZE, customSettings.AFTER_CONTINUATION_INDENT_SIZE, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .between(TokenSet.create(RobotTypes.KEYWORD_CALL_NAME), RobotTokenSets.ARGUMENTS_TYPE_SET)
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
    }
}

private val LINE_FEED_SETS: TokenSet = TokenSet.create(RobotTypes.LOCAL_SETTING,
    RobotTypes.LOCAL_ARGUMENTS_SETTING,
    RobotTypes.EMPTY_VARIABLE_STATEMENT,
    RobotTypes.IF_VARIABLE_STATEMENT,
    RobotTypes.KEYWORD_VARIABLE_STATEMENT,
    RobotTypes.INLINE_VARIABLE_STATEMENT,
    RobotTypes.FOR_LOOP_HEADER,
    RobotTypes.WHILE_LOOP_HEADER,
    RobotTypes.BREAK,
    RobotTypes.CONTINUE,
    RobotTypes.TRY,
    RobotTypes.EXCEPT_HEADER,
    RobotTypes.FINALLY,
    RobotTypes.END,
    RobotTypes.GROUP_HEADER,
    RobotTypes.RETURN_STRUCTURE,
    RobotTypes.TASK_ID)

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
    TokenSet.create(RobotTypes.KEYWORD_NAME))
