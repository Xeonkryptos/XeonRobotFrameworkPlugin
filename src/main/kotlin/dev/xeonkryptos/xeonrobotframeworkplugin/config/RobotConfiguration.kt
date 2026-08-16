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
import dev.xeonkryptos.xeonrobotframeworkplugin.ui.util.table.TableRowTransferHandler
import org.jetbrains.annotations.Nls
import javax.swing.DefaultCellEditor
import javax.swing.DropMode
import javax.swing.JComponent
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellRenderer

class RobotConfiguration(project: Project) : Configurable.NoScroll, SearchableConfigurable {

    private val optionsProvider = RobotOptionsProvider.getInstance(project)

    private val languageConfigurationsTableModel = LanguageConfigurationsTableModel("options.languages.language.column.name")
    private val languageConfigurationsTable = createLanguagesTable(languageConfigurationsTableModel).apply {
        dragEnabled = true
        dropMode = DropMode.INSERT_ROWS
        transferHandler = TableRowTransferHandler(this)
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    }
    private val languageConfigurationsTableToolbar = ToolbarDecorator.createDecorator(languageConfigurationsTable).apply {
        setAddAction { languageConfigurationsTableModel.addNewLanguage() }
        setRemoveAction {
            languageConfigurationsTable.selectedRows.map { selectedRow -> languageConfigurationsTableModel.languages[selectedRow] }
                .forEach { selectedLanguage -> languageConfigurationsTableModel.removeLanguage(selectedLanguage) }
        }
        setMoveUpAction { languageConfigurationsTableModel.reorder(languageConfigurationsTable.selectedRow, languageConfigurationsTable.selectedRow - 1) }
        setMoveDownAction { languageConfigurationsTableModel.reorder(languageConfigurationsTable.selectedRow, languageConfigurationsTable.selectedRow + 1) }

        setAddActionUpdater { isNoDefaultLanguageSelected() }
        setRemoveActionUpdater { isNoDefaultLanguageSelected() }
        setMoveUpActionUpdater { languageConfigurationsTable.selectedRow > 0 }
        setMoveDownActionUpdater { languageConfigurationsTable.selectedRow < languageConfigurationsTableModel.rowCount - 1 }
    }

    private fun isNoDefaultLanguageSelected(): Boolean =
        languageConfigurationsTable.selectedRows.map { index -> languageConfigurationsTableModel.languages[index] }.none { language -> language.defaultLanguage }

    private val panel = panel {
        group(RobotBundle.message("options.languages.row.label")) {
            row {
                cell(languageConfigurationsTableToolbar.createPanel()).resizableColumn().align(Align.FILL).onApply {
                    val enabledDefaultLanguages = languageConfigurationsTableModel.languages.asSequence().filter { it.active }.mapNotNull { it.languageClassReference }.toList()
                    optionsProvider.enabledDefaultLanguageCodes = enabledDefaultLanguages
                }
            }.resizableRow()
        }
    }

    private fun createLanguagesTable(languageConfigurationsTableModel: LanguageConfigurationsTableModel): JBTable =
        JBTable(languageConfigurationsTableModel).apply {
            columnModel.getColumn(0).cellRenderer = DefaultTableCellRenderer()
            columnModel.getColumn(0).cellEditor = DefaultCellEditor(JBTextField())

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
