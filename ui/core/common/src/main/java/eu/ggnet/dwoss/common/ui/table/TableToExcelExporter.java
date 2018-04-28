/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.common.ui.table;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import eu.ggnet.lucidcalc.CCalcDocument;
import eu.ggnet.lucidcalc.CSheet;
import eu.ggnet.lucidcalc.LucidCalc;
import eu.ggnet.lucidcalc.STable;
import eu.ggnet.lucidcalc.STableColumn;
import eu.ggnet.lucidcalc.STableModelList;
import eu.ggnet.lucidcalc.TempCalcDocument;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.PojoUtil;
import eu.ggnet.dwoss.util.UserInfoException;

/**
 *
 * @author pascal.perau
 */
public class TableToExcelExporter {

    /**
     * Export a JTable view to a temporary .xls file.
     * Supported cell renderer:<ol>
     * </ol>
     * <p/>
     * @param table
     * @param fileName
     * @return
     * @throws de.dw.util.UserInfoException
     */
    public static FileJacket export(JTable table, String fileName) throws UserInfoException {

        int visibleRowCount = table.getRowCount();
        if ( visibleRowCount < 1 ) throw new UserInfoException("Die Tabelle enthält keine Daten.");
        int columnCount = table.getColumnCount();
        TableModel model = table.getModel();
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < visibleRowCount; i++) {
            Object[] row = new Object[columnCount + 1];
            for (int j = 0; j < columnCount; j++) {

                //convert value of table to specified value
                Object value = model.getValueAt(table.convertRowIndexToModel(i), table.convertColumnIndexToModel(j));

                if ( value instanceof Date ) {
                    row[j] = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.GERMANY).format(value);
                    continue;
                }

                //check for enums with getNote() or getName() via PojoUtil reflection
                if ( value instanceof Enum ) {
                    try {
                        row[j] = PojoUtil.getValue("note", value);
                        continue;
                    } catch (RuntimeException e) {
                        try {
                            row[j] = PojoUtil.getValue("name", value);
                            continue;
                        } catch (RuntimeException ex) {
                        }
                    }
                }
                row[j] = value;
            }
            rows.add(row);
        }

        STable sTable = new STable();
        for (int i = 0; i < columnCount; i++) {
            int width = table.getColumnModel().getColumn(i).getWidth();
            sTable.add(new STableColumn(table.getColumnName(i), width / 5));
        }
        sTable.setModel(new STableModelList(rows));

        CCalcDocument cdoc = new TempCalcDocument(fileName);
        cdoc.add(new CSheet("Sheet1", sTable));

        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc);
        return new FileJacket(fileName, ".xls", file);
    }
}
