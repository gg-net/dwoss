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
package eu.ggnet.dwoss.redtape.document.creditmemo;

import java.util.List;

import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.redtape.document.AfterInvoicePosition;
import eu.ggnet.dwoss.util.table.Column;
import eu.ggnet.dwoss.util.table.IColumnGetAction;
import eu.ggnet.dwoss.util.table.IColumnGetSetAction;
import eu.ggnet.dwoss.util.table.SimpleTableModel;

/**
 *
 * @author pascal.perau
 */
public class CreditMemoTableModel extends SimpleTableModel<AfterInvoicePosition> {

    public CreditMemoTableModel(final List<AfterInvoicePosition> lines) {

        super(lines);
        addColumn(new Column<AfterInvoicePosition>("Vollgutschrift", true, 35, Boolean.class, new IColumnGetSetAction() {
            @Override
            public void setValue(int row, Object value) {
                Boolean bValue = (boolean)value;
                if ( bValue ) {
                    lines.get(row).setFullCredit(bValue);
                    lines.get(row).setParticipate(bValue);
                    lines.get(row).setPartialCredit(!bValue);
                    lines.get(row).getPosition().setPrice(lines.get(row).getOriginalPrice());
                    lines.get(row).getPosition().setAfterTaxPrice(lines.get(row).getOriginalPrice() * (lines.get(row).getPosition().getTax() + 1));
                } else {
                    lines.get(row).setParticipate(bValue);
                    lines.get(row).setFullCredit(bValue);
                }
                if ( lines.get(row).getPosition().getDocument().getDossier().isDispatch()
                        && getShippingCost() != null && lines.get(row) != getShippingCost() ) {
                    manipulateShippingCost();
                }
                fireTableDataChanged();
            }

            @Override
            public Object getValue(int row) {
                return lines.get(row).isFullCredit();
            }
        }));
        addColumn(new Column<AfterInvoicePosition>("Teilgutschrift", true, 35, Boolean.class, new IColumnGetSetAction() {
            @Override
            public void setValue(int row, Object value) {
                Boolean bValue = (boolean)value;
                if ( bValue ) {
                    lines.get(row).setPartialCredit(bValue);
                    lines.get(row).setParticipate(bValue);
                    lines.get(row).setFullCredit(!bValue);
                } else {
                    lines.get(row).setParticipate(bValue);
                    lines.get(row).setPartialCredit(bValue);
                    lines.get(row).getPosition().setPrice(lines.get(row).getOriginalPrice());
                    lines.get(row).getPosition().setAfterTaxPrice(lines.get(row).getOriginalPrice() * (lines.get(row).getPosition().getTax() + 1));
                }
                fireTableRowsUpdated(row, row);
            }

            @Override
            public Object getValue(int row) {
                return lines.get(row).isPartialCredit();
            }
        }));
        addColumn(new Column<AfterInvoicePosition>("Position", false, 180, String.class, new IColumnGetAction() {
            @Override
            public Object getValue(int row) {
                return lines.get(row).getPosition().getName();
            }
        }));
        addColumn(new Column<AfterInvoicePosition>("Netto", true, 50, Double.class, new IColumnGetSetAction() {
            @Override
            public void setValue(int row, Object value) {
                if ( value != null ) {
                    double dValue = (double)value;
                    if ( lines.get(row).isPartialCredit() ) {
                        lines.get(row).getPosition().setPrice(dValue);
                        lines.get(row).getPosition().setAfterTaxPrice(dValue * (lines.get(row).getPosition().getTax() + 1));
                    }
                } else {
                    lines.get(row).getPosition().setPrice(lines.get(row).getOriginalPrice());
                    lines.get(row).getPosition().setAfterTaxPrice(lines.get(row).getOriginalPrice() * (lines.get(row).getPosition().getTax() + 1));
                }
                fireTableRowsUpdated(row, row);
            }

            @Override
            public Object getValue(int row) {
                return lines.get(row).getPosition().getPrice();
            }
        }));
        addColumn(new Column<AfterInvoicePosition>("Brutto", true, 50, Double.class, new IColumnGetSetAction() {
            @Override
            public Object getValue(int row) {
                return lines.get(row).getPosition().toAfterTaxPrice();
            }

            @Override
            public void setValue(int row, Object value) {
                if ( value != null ) {
                    double dValue = (double)value;
                    if ( lines.get(row).isPartialCredit() ) {
                        lines.get(row).getPosition().setAfterTaxPrice(dValue);
                        lines.get(row).getPosition().setPrice(dValue / (lines.get(row).getPosition().getTax() + 1));
                    }
                } else {
                    lines.get(row).getPosition().setPrice(lines.get(row).getOriginalPrice());
                    lines.get(row).getPosition().setAfterTaxPrice(lines.get(row).getOriginalPrice() * (lines.get(row).getPosition().getTax() + 1));
                }
                fireTableRowsUpdated(row, row);
            }
        }));
    }

    private AfterInvoicePosition getShippingCost() {
        List<AfterInvoicePosition> lines = this.getDataModel();
        for (AfterInvoicePosition afterInvoicePosition : lines) {
            if ( afterInvoicePosition.getPosition().getType() == PositionType.SHIPPING_COST ) {
                return afterInvoicePosition;
            }
        }
        return null;
    }

    private boolean isEveryoneParticipant() {
        for (AfterInvoicePosition afterInvoicePosition : this.getDataModel()) {
            if ( !afterInvoicePosition.isParticipant() && afterInvoicePosition != getShippingCost() ) return false;
        }
        return true;
    }

    private void manipulateShippingCost() {
        AfterInvoicePosition shippingPos = getShippingCost();
        shippingPos.setParticipate(true);
        if ( isEveryoneParticipant() ) {
            shippingPos.setPartialCredit(false);
            shippingPos.setFullCredit(true);
            shippingPos.getPosition().setPrice(shippingPos.getOriginalPrice());
            shippingPos.getPosition().setAfterTaxPrice(shippingPos.getOriginalPrice() * (shippingPos.getPosition().getTax() + 1));
        } else {
            shippingPos.setFullCredit(false);
            shippingPos.setPartialCredit(true);
            shippingPos.getPosition().setPrice(.0);
            shippingPos.getPosition().setAfterTaxPrice(.0);
        }
    }
}
