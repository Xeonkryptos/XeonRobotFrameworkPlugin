package dev.xeonkryptos.xeonrobotframeworkplugin.ui.util.table

import java.awt.Cursor
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.DragSource
import javax.activation.DataHandler
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.TransferHandler

class TableRowTransferHandler(private val table: JTable) : TransferHandler() {

    private val localObjectFlavor = DataFlavor(Int::class.java, "Integer Row Index")

    override fun createTransferable(c: JComponent): Transferable {
        assert(c === table)
        return DataHandler(table.selectedRow, localObjectFlavor.getMimeType())
    }

    override fun canImport(info: TransferSupport): Boolean {
        val b = info.component === table && info.isDrop && info.isDataFlavorSupported(localObjectFlavor)
        table.setCursor(if (b) DragSource.DefaultMoveDrop else DragSource.DefaultMoveNoDrop)
        return b
    }

    override fun getSourceActions(c: JComponent?): Int = COPY_OR_MOVE

    override fun importData(info: TransferSupport): Boolean {
        val target = info.component as JTable
        val dl = info.getDropLocation() as JTable.DropLocation
        var index = dl.row
        val max = table.model.rowCount

        if (index !in 0..max) index = max
        target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))

        val rowFrom = info.getTransferable().getTransferData(localObjectFlavor) as Int
        return if (rowFrom != -1 && rowFrom != index) {
            val rows = table.selectedRows
            for ((iter, row) in rows.withIndex()) {
                if (index > row) {
                    index--
                    (table.model as Reorderable).reorder(row - iter, index)
                } else {
                    (table.model as Reorderable).reorder(row, index)
                }
                index++
            }
            target.getSelectionModel().addSelectionInterval(index, index)
            true
        } else false
    }

    override fun exportDone(c: JComponent?, t: Transferable?, act: Int) {
        if (act == MOVE) {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))
        }
    }
}
