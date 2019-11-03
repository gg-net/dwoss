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
package eu.ggnet.dwoss.redtapext.ui.cao.document;

import java.text.NumberFormat;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import eu.ggnet.dwoss.core.widget.swing.SimpleTableModel;

/**
 * The AfterInvoicePositionPriceSumPanel summarizes prices and after tax prices from a {@link SimpleTableModel} with {@link AfterInvoicePosition}
 * <p/>
 * @author pascal.perau
 */
public class AfterInvoicePositionPriceSumPanel extends javax.swing.JPanel implements TableModelListener {

    SimpleTableModel<AfterInvoicePosition> model;

    /** Creates new form AfterInvoicePositionPriceSumPanel */
    public AfterInvoicePositionPriceSumPanel() {
        initComponents();
    }

    public void setModel(SimpleTableModel<AfterInvoicePosition> model) {
        this.model = model;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        sum = new javax.swing.JLabel();
        afterTaxSum = new javax.swing.JLabel();

        jLabel1.setText("Nettosumme:");

        jLabel2.setText("Bruttosumme:");

        sum.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        afterTaxSum.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(sum, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(afterTaxSum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sum, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(afterTaxSum, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel afterTaxSum;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel sum;
    // End of variables declaration//GEN-END:variables

    @Override
    public void tableChanged(TableModelEvent e) {
        if ( model != null ) {
            double nettoSum = 0;
            double bruttoSum = 0;

            for (AfterInvoicePosition cmp : model.getDataModel()) {
                if ( cmp.isParticipant() ) {
                    nettoSum += cmp.getPosition().getPrice();
                    bruttoSum += (cmp.getPosition().getPrice() * (cmp.getPosition().getTax() + 1));
                }
            }
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            sum.setText(nf.format(nettoSum));
            afterTaxSum.setText(nf.format(bruttoSum));
        }
    }
}