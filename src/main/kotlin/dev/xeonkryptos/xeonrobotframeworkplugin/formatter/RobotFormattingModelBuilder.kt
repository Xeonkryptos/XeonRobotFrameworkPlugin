package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.FormattingModelProvider
import com.intellij.formatting.SpacingBuilder
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes

class RobotFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(context: FormattingContext): FormattingModel {
        val element = context.psiElement
        val settings = context.codeStyleSettings
        val commonSettings = settings.getCommonSettings(RobotLanguage.INSTANCE)
        val customSettings = settings.getCustomSettings(RobotCodeStyleSettings::class.java)
        val spaceBuilder = createSpaceBuilder(commonSettings, customSettings)
        val blockContext = RobotBlockContext(commonSettings, customSettings, spaceBuilder)
        val robotBlock = RobotBlock(element.node, blockContext)

        return FormattingModelProvider.createFormattingModelForPsiFile(element.containingFile, robotBlock, settings)
    }

    private fun createSpaceBuilder(commonSettings: CommonCodeStyleSettings, customSettings: RobotCodeStyleSettings): SpacingBuilder {
        return SpacingBuilder(commonSettings).before(RobotTypes.ASSIGNMENT)
            .spaceIf(commonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
            .after(RobotTypes.ASSIGNMENT)
            .spaces(RobotCodeStyleSettings.SUPER_SPACE_SIZE)
            .around(RobotTypes.VARIABLE_BODY)
            .spaceIf(customSettings.SPACE_AROUND_VARIABLE_BODY)
            .after(TokenType.WHITE_SPACE)
            .spacing(RobotCodeStyleSettings.SUPER_SPACE_SIZE, RobotCodeStyleSettings.SUPER_SPACE_SIZE * 2, 0, false, 0)
            .after(RobotTokenSets.SUPER_SPACE_SETS)
            .spacing(RobotCodeStyleSettings.SUPER_SPACE_SIZE, RobotCodeStyleSettings.SUPER_SPACE_SIZE, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
    }
}
