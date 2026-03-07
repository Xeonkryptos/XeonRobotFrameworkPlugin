package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.psi.codeStyle.CodeStyleDefaults
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.ui.components.fields.IntegerField
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import javax.swing.JLabel

class RobotIndentOptionsEditor : SmartIndentOptionsEditor() {
    private lateinit var afterContinuationIndentLabel: JLabel
    private lateinit var afterContinuationIndentField: IntegerField

    @Suppress("removal")
    override fun addComponents() {
        super.addComponents()

        afterContinuationIndentLabel = JLabel(RobotBundle.message("formatter.indent.after.continuation"))
        afterContinuationIndentField =
            createIndentTextField(RobotBundle.message("formatter.indent.after.continuation"), RobotCodeStyleSettings.SUPER_SPACE_SIZE, 99, CodeStyleDefaults.DEFAULT_INDENT_SIZE)
        afterContinuationIndentLabel.labelFor = afterContinuationIndentField
        add(afterContinuationIndentLabel, afterContinuationIndentField)
    }

    override fun isModified(settings: CodeStyleSettings, options: CommonCodeStyleSettings.IndentOptions): Boolean {
        if (super.isModified(settings, options)) return true

        val customSettings = settings.getCustomSettings(RobotCodeStyleSettings::class.java)
        return isFieldModified(afterContinuationIndentField, customSettings.AFTER_CONTINUATION_INDENT_SIZE)
    }

    override fun apply(settings: CodeStyleSettings, options: CommonCodeStyleSettings.IndentOptions) {
        super.apply(settings, options)

        settings.getCustomSettings(RobotCodeStyleSettings::class.java).apply { AFTER_CONTINUATION_INDENT_SIZE = afterContinuationIndentField.value }
    }

    override fun reset(settings: CodeStyleSettings, options: CommonCodeStyleSettings.IndentOptions) {
        super.reset(settings, options)

        settings.getCustomSettings(RobotCodeStyleSettings::class.java).apply { afterContinuationIndentField.value = AFTER_CONTINUATION_INDENT_SIZE }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

        afterContinuationIndentField.isEnabled = enabled
        afterContinuationIndentLabel.isEnabled = enabled
    }
}


