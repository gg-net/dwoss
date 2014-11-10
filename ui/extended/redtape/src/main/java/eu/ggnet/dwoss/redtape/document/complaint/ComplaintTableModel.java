package eu.ggnet.dwoss.redtape.document.complaint;

import java.util.List;

import eu.ggnet.dwoss.redtape.document.AfterInvoicePosition;
import eu.ggnet.dwoss.util.table.Column;
import eu.ggnet.dwoss.util.table.IColumnGetAction;
import eu.ggnet.dwoss.util.table.IColumnGetSetAction;
import eu.ggnet.dwoss.util.table.SimpleTableModel;

/**
 *
 * @author pascal.perau
 */
public class ComplaintTableModel extends SimpleTableModel<AfterInvoicePosition>{

    public ComplaintTableModel(final List<AfterInvoicePosition> lines) {

            super(lines);
            addColumn(new Column<AfterInvoicePosition>("Reklamieren", true, 35, Boolean.class, new IColumnGetSetAction() {
                @Override
                public void setValue(int row, Object value) {
                    Boolean bValue = (boolean)value;
                    lines.get(row).setParticipate(bValue);
                    fireTableRowsUpdated(row, row);
                }

                @Override
                public Object getValue(int row) {
                    return lines.get(row).isParticipant();
                }
            }));
            addColumn(new Column<AfterInvoicePosition>("Position", false, 180, String.class, new IColumnGetAction() {
                @Override
                public Object getValue(int row) {
                    return lines.get(row).getPosition().getName();
                }
            }));
            addColumn(new Column<AfterInvoicePosition>("Netto", false, 50, Double.class, new IColumnGetSetAction() {
                @Override
                public void setValue(int row, Object value) {
                    double dValue = (double)value;
                        lines.get(row).getPosition().setPrice(dValue);
                        lines.get(row).getPosition().setAfterTaxPrice(dValue * (lines.get(row).getPosition().getTax() + 1));
                    fireTableRowsUpdated(row, row);
                }

                @Override
                public Object getValue(int row) {
                    return lines.get(row).getPosition().getPrice();
                }
            }));
            addColumn(new Column<AfterInvoicePosition>("Brutto", false, 50, Double.class, new IColumnGetSetAction() {
                @Override
                public Object getValue(int row) {
                    return lines.get(row).getPosition().getAfterTaxPrice();
                }

                @Override
                public void setValue(int row, Object value) {
                    double dValue = (double)value;
                        lines.get(row).getPosition().setAfterTaxPrice(dValue);
                        lines.get(row).getPosition().setPrice(dValue / (lines.get(row).getPosition().getTax() + 1));
                    fireTableRowsUpdated(row, row);
                }
            }));
    }
}
