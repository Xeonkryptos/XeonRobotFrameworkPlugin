package dev.xeonkryptos.xeonrobotframeworkplugin.config

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import org.jetbrains.annotations.Nls
import javax.swing.DefaultCellEditor
import javax.swing.JComponent
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellRenderer

class RobotConfiguration(project: Project) : Configurable.NoScroll, SearchableConfigurable {

    private val optionsProvider = RobotOptionsProvider.getInstance(project)

    private val defaultLanguageConfigurationsTableModel = LanguageConfigurationsTableModel("options.languages.default.language.column.name")
    private val defaultLanguageConfigurationsTable = createLanguagesTable(defaultLanguageConfigurationsTableModel)

    private val customLanguageConfigurationsTableModel = LanguageConfigurationsTableModel("options.languages.language.class.fqdn.column.name")
    private val customLanguageConfigurationsTable = createLanguagesTable(customLanguageConfigurationsTableModel)
    private val customLanguageConfigurationsTableToolbar = ToolbarDecorator.createDecorator(customLanguageConfigurationsTable).apply {
        setAddAction {
            customLanguageConfigurationsTableModel.addNewLanguage()
        }
        setRemoveAction {
            customLanguageConfigurationsTable.selectedRows.asSequence()
                .map { selectedRow -> customLanguageConfigurationsTableModel.languages[selectedRow] }
                .forEach { selectedLanguage -> customLanguageConfigurationsTableModel.removeLanguage(selectedLanguage) }
        }
    }

    private val panel = panel {
        group(RobotBundle.message("options.languages.row.label")) {
            row {
                scrollCell(defaultLanguageConfigurationsTable).resizableColumn().align(Align.FILL)
            }.resizableRow()
            row {
                cell(customLanguageConfigurationsTableToolbar.createPanel()).resizableColumn().align(Align.FILL)
            }.resizableRow()
        }
    }

    private fun createLanguagesTable(languageConfigurationsTableModel: LanguageConfigurationsTableModel): JBTable =
        JBTable(languageConfigurationsTableModel).apply { // Spalte 0: Standard-Renderer (Text) + JBTextField-Editor
            columnModel.getColumn(0).cellRenderer = DefaultTableCellRenderer()
            columnModel.getColumn(0).cellEditor = DefaultCellEditor(JBTextField())

            // Spalte 1: JBCheckBox-Renderer + JBCheckBox-Editor für Boolean-Werte
            columnModel.getColumn(1).cellRenderer = TableCellRenderer { table, value, selected, _, _, _ ->
                JBCheckBox().apply {
                    isOpaque = true
                    isSelected = value as? Boolean ?: false
                    background = if (selected) table.selectionBackground else table.background
                    foreground = if (selected) table.selectionForeground else table.foreground
                }
            }
            columnModel.getColumn(1).cellEditor = DefaultCellEditor(JBCheckBox())
        }

    override fun getId(): String = helpTopic

    @Nls
    override fun getDisplayName(): String = RobotBundle.message("options.entrypoint")

    override fun getHelpTopic(): String = "reference.idesettings.robot"

    override fun createComponent(): JComponent = panel

    override fun isModified(): Boolean = panel.isModified()

    override fun apply() = panel.apply()

    override fun reset() = panel.reset()
}
