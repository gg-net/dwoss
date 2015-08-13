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
package eu.ggnet.dwoss.redtape.dossiertable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import eu.ggnet.dwoss.redtape.entity.Dossier;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;

/**
 *
 * @author pascal.perau
 */
public class DossierTableModel extends AbstractTableModel {

    private List<Dossier> data;

    private Object[][] columns = new Object[][]{
        {"Vorgang", String.class},
        {"Zahlungsmodalität", PaymentMethod.class},
        {"Versand", Boolean.class},
        {"Aktuelles Dokument", DocumentType.class},
        {"Rechnungsnummer", String.class},
        {"Leistungsdatum", Date.class},
        {"Status", Dossier.class},
        {"Anweisung", String.class}};

    public DossierTableModel(List<Dossier> data) {
        this.data = data;
    }

    public DossierTableModel() {
        this.data = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return (String)columns[column][0];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return (Class)columns[columnIndex][1];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return data.get(rowIndex).getIdentifier();
            case 1:
                return data.get(rowIndex).getPaymentMethod().getNote();
            case 2:
                return data.get(rowIndex).isDispatch();
            case 3:
                return data.get(rowIndex).getCrucialDocument().getType().getName();
            case 4:
                return !data.get(rowIndex).getActiveDocuments(DocumentType.INVOICE).isEmpty()
                        ? data.get(rowIndex).getActiveDocuments(DocumentType.INVOICE).get(0).getIdentifier()
                        : "---";
            case 5:
                return !data.get(rowIndex).getActiveDocuments(DocumentType.INVOICE).isEmpty()
                        ? data.get(rowIndex).getActiveDocuments(DocumentType.INVOICE).get(0).getActual()
                        : data.get(rowIndex).getActiveDocuments().get(data.get(rowIndex).getActiveDocuments().size() - 1).getActual();
            case 6:
                return data.get(rowIndex);
            case 7:
                return data.get(rowIndex).getCrucialDirective().getName();
            default:
                return null;
        }
    }

    public void add(Dossier dos) {
        int index = data.size();
        data.add(dos);
        fireTableChanged(new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    public void update(Dossier oldDos, Dossier newDos) {
        int index = data.indexOf(oldDos);
        if(index < 0 ) return; //if oldDos is null or not in the data list. Return if so.
        data.set(index, newDos);
        fireTableChanged(new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    public void delete(Dossier dos) {
        int index = data.indexOf(dos);
        data.remove(dos);
        fireTableRowsDeleted(index, index);
    }

    public void clear() {
        data.clear();
        fireTableDataChanged();
    }

    public Dossier getDossier(int row) {
        return data.get(row);
    }
}
