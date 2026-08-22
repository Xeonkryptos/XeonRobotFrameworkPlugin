package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.ide.highlighter.HighlighterFactory
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.highlighter.EditorHighlighter
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.fields.IntegerField
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBInsets
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotHighlighter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage
import org.jetbrains.annotations.NonNls
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel

class RobotCustomFormattingPanel(settings: CodeStyleSettings) : CodeStyleAbstractPanel(RobotLanguage.INSTANCE, null, settings) {

    private val spacesAfterLocalSettingNameField =
        createIntegerField(RobotBundle.message("formatter.spaces.after.local.setting.name"), RobotCodeStyleSettings.SUPER_SPACE_SIZE, 99, RobotCodeStyleSettings.SUPER_SPACE_SIZE)
    private val spacesAfterVariableStatementField =
        createIntegerField(RobotBundle.message("formatter.spaces.after.variable.statement.assignment"), RobotCodeStyleSettings.SUPER_SPACE_SIZE, 99, RobotCodeStyleSettings.SUPER_SPACE_SIZE)
    private val spacesBetweenKeywordCallAndArgumentsField =
        createIntegerField(RobotBundle.message("formatter.spaces.between.keyword.call.and.arguments"), RobotCodeStyleSettings.SUPER_SPACE_SIZE, 99, RobotCodeStyleSettings.SUPER_SPACE_SIZE)

    private val customSettings = settings.getCustomSettings(RobotCodeStyleSettings::class.java)
    private val configurationPanel = panel {
        group(RobotBundle.message("formatter.settings.custom.spacing.group")) {
            row(RobotBundle.message("formatter.spaces.after.local.setting.name")) {
                cell(spacesAfterLocalSettingNameField).onApply { customSettings.SPACES_AFTER_LOCAL_SETTING_NAME = spacesAfterLocalSettingNameField.value }
                    .onReset { spacesAfterLocalSettingNameField.value = customSettings.SPACES_AFTER_LOCAL_SETTING_NAME }
                    .onIsModified { spacesAfterLocalSettingNameField.value != customSettings.SPACES_AFTER_LOCAL_SETTING_NAME }
            }
            row(RobotBundle.message("formatter.spaces.after.variable.statement.assignment")) {
                cell(spacesAfterVariableStatementField).onApply { customSettings.SPACES_AFTER_VARIABLE_STATEMENT_ASSIGNMENT = spacesAfterVariableStatementField.value }
                    .onReset { spacesAfterVariableStatementField.value = customSettings.SPACES_AFTER_VARIABLE_STATEMENT_ASSIGNMENT }
                    .onIsModified { spacesAfterVariableStatementField.value != customSettings.SPACES_AFTER_VARIABLE_STATEMENT_ASSIGNMENT }
            }
            row(RobotBundle.message("formatter.spaces.between.keyword.call.and.arguments")) {
                cell(spacesBetweenKeywordCallAndArgumentsField).onApply { customSettings.SPACES_BETWEEN_KEYWORD_CALL_AND_ARGUMENTS = spacesBetweenKeywordCallAndArgumentsField.value }
                    .onReset { spacesBetweenKeywordCallAndArgumentsField.value = customSettings.SPACES_BETWEEN_KEYWORD_CALL_AND_ARGUMENTS }
                    .onIsModified { spacesBetweenKeywordCallAndArgumentsField.value != customSettings.SPACES_BETWEEN_KEYWORD_CALL_AND_ARGUMENTS }
            }
        }
    }

    private val mainPanel: JPanel = JPanel().apply {
        layout = GridBagLayout()
        border = IdeBorderFactory.createEmptyBorder(JBInsets(10, 10, 10, 10))
    }

    init {
        mainPanel.add(
            configurationPanel, GridBagConstraints(
                0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                JBInsets(0, 0, 0, 10), 0, 0
            )
        )

        val previewPanel = JPanel().apply { layout = BorderLayout() }
        installPreviewPanel(previewPanel)
        mainPanel.add(
            previewPanel, GridBagConstraints(
                1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                JBInsets(0, 0, 0, 0), 0, 0
            )
        )

        addPanelToWatch( mainPanel)
    }

    @Suppress("SameParameterValue")
    private fun createIntegerField(label: String, minValue: Int, maxValue: Int, defaultValue: Int): IntegerField = IntegerField(label, minValue, maxValue).apply {
        this.defaultValue = defaultValue
        columns = 4
        if (defaultValue < 0) isCanBeEmpty = true
        minimumSize = preferredSize
    }

    override fun getRightMargin(): Int = 80

    override fun createHighlighter(scheme: EditorColorsScheme): EditorHighlighter = HighlighterFactory.createHighlighter(RobotHighlighter(), scheme)

    override fun getFileType(): FileType = RobotFeatureFileType.getInstance()

    override fun getPreviewText(): @NonNls String = FormatterPreviews.WRAPPING_AND_BRACES

    override fun apply(settings: CodeStyleSettings) = configurationPanel.apply()

    override fun isModified(settings: CodeStyleSettings?): Boolean = configurationPanel.isModified()

    override fun getPanel(): JComponent = mainPanel

    override fun resetImpl(settings: CodeStyleSettings) = configurationPanel.reset()

    override fun getTabTitle(): @NlsContexts.TabTitle String = RobotBundle.message("formatter.settings.tab.custom")
}
