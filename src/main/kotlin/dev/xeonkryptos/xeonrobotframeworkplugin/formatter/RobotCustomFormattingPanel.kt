package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.ide.highlighter.HighlighterFactory
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.highlighter.EditorHighlighter
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.fileTypes.RobotFeatureFileType
import dev.xeonkryptos.xeonrobotframeworkplugin.fileTypes.RobotHighlighter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent
import kotlin.apply

class RobotCustomFormattingPanel(settings: CodeStyleSettings) : CodeStyleAbstractPanel(RobotLanguage.INSTANCE, null, settings) {

    private val customSettings = settings.getCustomSettings(RobotCodeStyleSettings::class.java)
    private val panel = panel {
        row {
            checkBox(RobotBundle.message("formatter.custom.multiline-indentation")).bindSelected(customSettings::MULTILINE_INDENTATION).onChanged { onSomethingChanged() }
        }
        row {
            checkBox(RobotBundle.message("formatter.custom.capitalize-keywords")).bindSelected(customSettings::CAPITALIZE_KEYWORDS).onChanged { onSomethingChanged() }
        }
    }.apply { addPanelToWatch(this) }

    override fun getRightMargin(): Int = 80

    override fun createHighlighter(scheme: EditorColorsScheme): EditorHighlighter = HighlighterFactory.createHighlighter(RobotHighlighter(), scheme)

    override fun getFileType(): FileType = RobotFeatureFileType.INSTANCE

    override fun getPreviewText(): @NonNls String? = null

    override fun apply(settings: CodeStyleSettings) = panel.apply()

    override fun isModified(settings: CodeStyleSettings?): Boolean = panel.isModified()

    override fun getPanel(): JComponent = panel

    override fun resetImpl(settings: CodeStyleSettings) = panel.reset()
}
