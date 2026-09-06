package dev.xeonkryptos.xeonrobotframeworkplugin.config

import ai.grazie.utils.toLinkedSet
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.UI
import com.intellij.openapi.application.asContextElement
import com.intellij.openapi.application.readAction
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import com.jetbrains.python.psi.search.PyClassInheritorsSearch
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.config.model.LanguageConfiguration
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.PythonResolver
import dev.xeonkryptos.xeonrobotframeworkplugin.ui.util.table.TableRowTransferHandler
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Nls
import javax.swing.DefaultCellEditor
import javax.swing.DropMode
import javax.swing.JComponent
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellRenderer

@Suppress("UnstableApiUsage")
class RobotConfiguration(project: Project, cs: CoroutineScope) : Configurable.NoScroll, SearchableConfigurable {

    private val optionsProvider = RobotOptionsProvider.getInstance(project)

    private val languageConfigurationsTableModel = LanguageConfigurationsTableModel()
    private val languageConfigurationsTable = createLanguagesTable(languageConfigurationsTableModel).apply {
        dragEnabled = true
        dropMode = DropMode.INSERT_ROWS
        transferHandler = TableRowTransferHandler(this)
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    }
    private val languageConfigurationsTableToolbar = ToolbarDecorator.createDecorator(languageConfigurationsTable).apply {
        setMoveUpAction {
            languageConfigurationsTableModel.reorder(languageConfigurationsTable.selectedRow, languageConfigurationsTable.selectedRow - 1)
            languageConfigurationsTable.selectionModel.setSelectionInterval(languageConfigurationsTable.selectedRow - 1, languageConfigurationsTable.selectedRow - 1)
        }
        setMoveDownAction {
            languageConfigurationsTableModel.reorder(languageConfigurationsTable.selectedRow, languageConfigurationsTable.selectedRow + 1)
            languageConfigurationsTable.selectionModel.setSelectionInterval(languageConfigurationsTable.selectedRow + 1, languageConfigurationsTable.selectedRow + 1)
        }

        setMoveUpActionUpdater { languageConfigurationsTable.selectedRow > 0 }
        setMoveDownActionUpdater { languageConfigurationsTable.selectedRow < languageConfigurationsTableModel.rowCount - 1 }
    }

    private val panel = panel {
        group(RobotBundle.message("options.languages.row.label")) {
            row {
                cell(languageConfigurationsTableToolbar.createPanel()).resizableColumn().align(Align.FILL).onApply {
                    val enabledLanguages = languageConfigurationsTableModel.languages.asSequence().filter { it.active }.map { it.languageClassReference }.toLinkedSet()
                    optionsProvider.enabledLanguages = enabledLanguages
                }
            }.resizableRow()
        }.resizableRow()
    }

    init {
        cs.launch {
            val languageConfigurations = readAction {
                PythonResolver.findClass(RobotNames.LANGUAGE_BASE_CLASS_REFERENCE, project, null)?.let { languageBaseClass ->
                    val inheritors = PyClassInheritorsSearch.search(languageBaseClass, false).findAll()
                    inheritors.map { inheritor ->
                        val qualifiedName = inheritor.qualifiedName!!
                        LanguageConfiguration(
                            displayName = inheritor.docStringValue?.split(Regex("[\\r\\n]+"))?.firstOrNull() ?: inheritor.name!!,
                            languageClassReference = qualifiedName,
                            active = optionsProvider.enabledLanguages.contains(qualifiedName) || qualifiedName == RobotNames.ENGLISH_LANGUAGE_CLASS_REFERENCE
                        )
                    }
                }
            }?.sortedWith { o1, o2 ->
                if (o1.languageClassReference == RobotNames.ENGLISH_LANGUAGE_CLASS_REFERENCE && o2.languageClassReference != RobotNames.ENGLISH_LANGUAGE_CLASS_REFERENCE) -1
                else if (o2.languageClassReference == RobotNames.ENGLISH_LANGUAGE_CLASS_REFERENCE && o1.languageClassReference != RobotNames.ENGLISH_LANGUAGE_CLASS_REFERENCE) 1
                else o1.displayName.compareTo(o2.displayName)
            }
            withContext(Dispatchers.UI + ModalityState.any().asContextElement()) {
                languageConfigurationsTableModel.setLanguages(languageConfigurations ?: listOf())
            }
        }
    }

    private fun createLanguagesTable(languageConfigurationsTableModel: LanguageConfigurationsTableModel): JBTable =
        JBTable(languageConfigurationsTableModel).apply {
            columnModel.getColumn(0).cellRenderer = DefaultTableCellRenderer()
            columnModel.getColumn(0).cellEditor = DefaultCellEditor(JBTextField())

            columnModel.getColumn(1).cellRenderer = TableCellRenderer { table, value, selected, _, row, _ ->
                JBCheckBox().apply {
                    isOpaque = true
                    isSelected = value as? Boolean ?: false
                    background = if (selected) table.selectionBackground else table.background
                    foreground = if (selected) table.selectionForeground else table.foreground
                    isEnabled = languageConfigurationsTableModel.languages[row].languageClassReference != RobotNames.ENGLISH_LANGUAGE_CLASS_REFERENCE
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
