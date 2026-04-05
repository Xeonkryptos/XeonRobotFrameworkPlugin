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
            .before(RobotTypes.ASSIGNMENT)
            .spaceIf(commonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
            .after(RobotTypes.ASSIGNMENT)
            .spacing(RobotCodeStyleSettings.SUPER_SPACE_SIZE, maximumSpacesAfterVariableAssignment, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            .withinPair(RobotTypes.VARIABLE_LBRACE, RobotTypes.VARIABLE_RBRACE)
            .spaceIf(commonSettings.SPACE_WITHIN_BRACES)
            .withinPair(RobotTypes.VARIABLE_ACCESS_START, RobotTypes.VARIABLE_ACCESS_END)
            .spaceIf(commonSettings.SPACE_WITHIN_BRACKETS)
            .afterInside(TokenType.WHITE_SPACE, RobotTypes.LOCAL_SETTING)
            .spacing(customSettings.AFTER_CONTINUATION_INDENT_SIZE,
                customSettings.AFTER_CONTINUATION_INDENT_SIZE,
                0,
                commonSettings.KEEP_LINE_BREAKS,
                customSettings.KEEP_BLANK_LINES_IN_LOCAL_SETTINGS)
            .after(TokenType.WHITE_SPACE)
            .spacing(customSettings.AFTER_CONTINUATION_INDENT_SIZE, customSettings.AFTER_CONTINUATION_INDENT_SIZE, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
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
