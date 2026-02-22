package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.openapi.util.NlsContexts
import com.intellij.psi.codeStyle.CodeStyleConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage
import org.jetbrains.annotations.NonNls

class RobotCodeStyleSettingsProvider : CodeStyleSettingsProvider() {

    override fun getConfigurableDisplayName(): @NlsContexts.ConfigurableName String = RobotBundle.message("options.entrypoint")

    override fun createCustomSettings(settings: CodeStyleSettings): CustomCodeStyleSettings = RobotCodeStyleSettings(settings)

    override fun createConfigurable(settings: CodeStyleSettings, originalSettings: CodeStyleSettings): CodeStyleConfigurable =
        object : CodeStyleAbstractConfigurable(settings, originalSettings, RobotBundle.message("options.entrypoint")) {
            override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel = RobotCodeStyleMainPanel(currentSettings, settings)

            override fun getHelpTopic(): @NonNls String = "reference.settingsdialog.codestyle.robot"
        }

    private class RobotCodeStyleMainPanel(currentSettings: CodeStyleSettings, settings: CodeStyleSettings) : TabbedLanguageCodeStylePanel(RobotLanguage.INSTANCE, currentSettings, settings)
}
