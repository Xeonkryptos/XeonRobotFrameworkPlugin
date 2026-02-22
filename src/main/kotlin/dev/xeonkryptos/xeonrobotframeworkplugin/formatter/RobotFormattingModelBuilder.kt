package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.FormattingModelProvider
import com.intellij.formatting.SpacingBuilder
import com.intellij.psi.codeStyle.CodeStyleSettings
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage

class RobotFormattingModelBuilder : FormattingModelBuilder {

    override fun createModel(context: FormattingContext): FormattingModel {
        val element = context.psiElement
        val settings = context.codeStyleSettings

        return FormattingModelProvider.createFormattingModelForPsiFile(element.containingFile, RobotBlock(element.node, createSpaceBuilder(settings)), settings)
    }

    private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder {
        val commonSettings = settings.getCommonSettings(RobotLanguage.INSTANCE)
        val customSettings = settings.getCustomSettings(RobotCodeStyleSettings::class.java)
        return SpacingBuilder(commonSettings)
    }
}
