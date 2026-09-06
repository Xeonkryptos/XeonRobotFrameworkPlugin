package dev.xeonkryptos.xeonrobotframeworkplugin.config

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.config.model.LanguageConfiguration
import dev.xeonkryptos.xeonrobotframeworkplugin.ui.util.table.Reorderable
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames
import javax.swing.table.AbstractTableModel

class LanguageConfigurationsTableModel : AbstractTableModel(), Reorderable {

    private val myLanguages = mutableListOf<LanguageConfiguration>()
    val languages: List<LanguageConfiguration>
        get() = myLanguages

    override fun getRowCount(): Int = myLanguages.size

    override fun getColumnCount(): Int = 2

    override fun getColumnName(column: Int): String? = when (column) {
        0 -> RobotBundle.message("options.languages.language.column.name")
        1 -> RobotBundle.message("options.languages.enabled.column.name")
        else -> null
    }

    override fun getColumnClass(columnIndex: Int): Class<*>? = when (columnIndex) {
        0 -> String::class.java
        1 -> Boolean::class.java
        else -> null
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = myLanguages[rowIndex].languageClassReference != RobotNames.ENGLISH_LANGUAGE_CLASS_REFERENCE && columnIndex > 0

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? = when (columnIndex) {
        0 -> myLanguages[rowIndex].displayName
        1 -> myLanguages[rowIndex].active
        else -> null
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        when (columnIndex) {
            1 -> myLanguages[rowIndex].active = aValue as Boolean
        }
        if (columnIndex != 0) {
            fireTableCellUpdated(rowIndex, columnIndex)
        }
    }

    override fun reorder(fromIndex: Int, toIndex: Int) {
        val languageToMove = myLanguages.removeAt(fromIndex)
        myLanguages.add(toIndex, languageToMove)

        fireTableRowsUpdated(minOf(fromIndex, toIndex), maxOf(fromIndex, toIndex))
    }

    fun setLanguages(languages: List<LanguageConfiguration>) {
        myLanguages.clear()
        myLanguages.addAll(languages)
        fireTableDataChanged()
    }
}
