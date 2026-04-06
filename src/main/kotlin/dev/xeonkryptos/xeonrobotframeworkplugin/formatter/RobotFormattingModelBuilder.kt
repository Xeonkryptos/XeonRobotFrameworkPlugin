package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.formatting.*
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.tree.TokenSet
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes

class RobotFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(context: FormattingContext): FormattingModel {
        val element = context.psiElement
        val commonSettings = context.codeStyleSettings.getCommonSettings(RobotLanguage.INSTANCE)
        val customSettings = context.codeStyleSettings.getCustomSettings(RobotCodeStyleSettings::class.java)
        val spaceBuilder = createSpaceBuilder(context.codeStyleSettings)
        val blockContext = RobotBlockContext(commonSettings, customSettings, spaceBuilder)
        val robotBlock = RobotBlock(element.node, blockContext)
        return FormattingModelProvider.createFormattingModelForPsiFile(element.containingFile, robotBlock, context.codeStyleSettings)
    }

    private fun createSpaceBuilder(codeStyleSettings: CodeStyleSettings): SpacingBuilder {
        val commonSettings = codeStyleSettings.getCommonSettings(RobotLanguage.INSTANCE)
        val customSettings = codeStyleSettings.getCustomSettings(RobotCodeStyleSettings::class.java)
        val maximumSpacesAfterTemplateValues = if (customSettings.KEEP_ADDITIONAL_SPACES_BETWEEN_TEMPLATE_VALUES) Integer.MAX_VALUE else RobotCodeStyleSettings.SUPER_SPACE_SIZE
        val maximumSpacesAfterVariableAssignment = if (customSettings.KEEP_ADDITIONAL_SPACES_AFTER_VARIABLE_ASSIGNMENTS) Integer.MAX_VALUE else RobotCodeStyleSettings.SUPER_SPACE_SIZE

        return SpacingBuilder(codeStyleSettings,
            RobotLanguage.INSTANCE) // We need to make absolutely sure, any newlines after a template value holder is preserved. Otherwise, we might break test cases by putting everything into the same line
            .after(RobotTokenSets.TEMPLATE_VALUES_HOLDER_SET)
            .spacing(RobotCodeStyleSettings.SUPER_SPACE_SIZE, maximumSpacesAfterTemplateValues, 0, true, commonSettings.KEEP_BLANK_LINES_IN_CODE)
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
            .betweenInside(TokenSet.create(RobotTypes.ASSIGNMENT), TokenSet.ANY, RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER_OPTIONAL)
            .spaces(0)
            .withinPair(RobotTypes.VARIABLE_LBRACE, RobotTypes.VARIABLE_RBRACE)
            .spaceIf(commonSettings.SPACE_WITHIN_BRACES)
            .withinPair(RobotTypes.VARIABLE_ACCESS_START, RobotTypes.VARIABLE_ACCESS_END)
            .spaceIf(commonSettings.SPACE_WITHIN_BRACKETS)
            .withinPair(RobotTypes.LOCAL_SETTING_START, RobotTypes.LOCAL_SETTING_END)
            .spaceIf(commonSettings.SPACE_WITHIN_BRACKETS)
            .between(TokenSet.create(TokenType.WHITE_SPACE), TokenSet.ANY)
            .spacing(customSettings.AFTER_CONTINUATION_INDENT_SIZE, customSettings.AFTER_CONTINUATION_INDENT_SIZE, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .between(TokenSet.create(RobotTypes.KEYWORD_CALL_NAME), TokenSet.create(RobotTypes.PARAMETER, RobotTypes.POSITIONAL_ARGUMENT))
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
            .after(SUPER_SPACE_SETS)
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
    }
}

private val SUPER_SPACE_SETS: TokenSet = TokenSet.orSet(RobotTokenSets.GHERKIN_SET,
    RobotTokenSets.LOOP_KEYWORDS_SET,
    RobotTokenSets.CONTROL_KEYWORDS_SET,
    RobotTokenSets.EXCEPTION_KEYWORDS_SET,
    RobotTokenSets.LOCAL_SETTING_NAMES_SET,
    RobotTokenSets.GLOBAL_SETTING_NAMES_SET,
    TokenSet.create(RobotTypes.KEYWORD_NAME, RobotTypes.PARAMETER, RobotTypes.POSITIONAL_ARGUMENT))
