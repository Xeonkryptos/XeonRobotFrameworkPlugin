package dev.xeonkryptos.xeonrobotframeworkplugin.config

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.config.model.LanguageConfiguration
import dev.xeonkryptos.xeonrobotframeworkplugin.ui.util.table.Reorderable
import javax.swing.table.AbstractTableModel

class LanguageConfigurationsTableModel(private val languageColumnNameKey: String) : AbstractTableModel(), Reorderable {

    private val myLanguages = mutableListOf<LanguageConfiguration>()
    val languages: List<LanguageConfiguration>
        get() = myLanguages

    override fun getRowCount(): Int = myLanguages.size

    override fun getColumnCount(): Int = 2

    override fun getColumnName(column: Int): String? = when (column) {
        0 -> RobotBundle.message(languageColumnNameKey)
        1 -> RobotBundle.message("options.languages.enabled.column.name")
        else -> null
    }

    override fun getColumnClass(columnIndex: Int): Class<*>? = when (columnIndex) {
        0 -> String::class.java
        1 -> Boolean::class.java
        else -> null
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? = when (columnIndex) {
        0 -> myLanguages[rowIndex].languageClassReference
        1 -> myLanguages[rowIndex].active
        else -> null
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        when (columnIndex) {
            0 -> myLanguages[rowIndex].languageClassReference = aValue as String
            1 -> myLanguages[rowIndex].active = aValue as Boolean
        }
        fireTableCellUpdated(rowIndex, columnIndex)
    }

    override fun reorder(fromIndex: Int, toIndex: Int) {
        val languageToMove = myLanguages.removeAt(fromIndex)
        fireTableRowsDeleted(fromIndex, fromIndex)

        myLanguages.add(toIndex, languageToMove)
        fireTableRowsInserted(toIndex, toIndex)
    }

    fun addNewLanguage() {
        myLanguages.add(LanguageConfiguration(defaultLanguage = false))
        fireTableRowsInserted(myLanguages.size - 1, myLanguages.size - 1)
    }

    fun removeLanguage(language: LanguageConfiguration) {
        if (!language.defaultLanguage) {
            myLanguages.remove(language)
            fireTableRowsDeleted(myLanguages.size, myLanguages.size)
        }
    }
}
