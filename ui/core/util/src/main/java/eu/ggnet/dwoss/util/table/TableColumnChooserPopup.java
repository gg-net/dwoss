package eu.ggnet.dwoss.util.table;

import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A popupmenu designed to enable easy hide/show funktions for table columns. 
 * @author pascal.perau
 */
public class TableColumnChooserPopup extends JPopupMenu {
        
    @Data
    @AllArgsConstructor
    private class TableColumnContainer {
        private TableColumn column;

        private int index;
    }
    
    private class ColumCheckBox extends JCheckBox {

        public ColumCheckBox(final TableColumnContainer cc) {
            super(new AbstractAction(cc.getColumn().getIdentifier().toString()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if ( ((ColumCheckBox)e.getSource()).isSelected() ) {
                        insertColum(cc);
                    } else {
                        removeColum(cc);
                    }
                }
            });
            setSelected(true);
        }
    }

    private JTable table;

    public TableColumnChooserPopup(JTable table) {
        this.table = table;
        Enumeration<TableColumn> tablecols = table.getColumnModel().getColumns();
        while (tablecols.hasMoreElements()) {
            TableColumn tableColumn = tablecols.nextElement();
            int index = table.getColumnModel().getColumnIndex(tableColumn.getIdentifier());
            
            this.add(new ColumCheckBox(new TableColumnContainer(tableColumn, index)));
        }
    }

    private void insertColum(TableColumnContainer container) {
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.addColumn(container.getColumn());
        try {
            columnModel.moveColumn(columnModel.getColumnIndex(container.getColumn().getIdentifier()), container.getIndex());
        } catch (IllegalArgumentException e) {
            //in case that the index after the actual last one is stored just append the column.
        }
    }

    private void removeColum(TableColumnContainer container) {
        int columnIndex = table.getColumnModel().getColumnIndex(container.getColumn().getIdentifier());
        container.setIndex(columnIndex);
        table.getColumnModel().removeColumn(container.getColumn());
    }
}
