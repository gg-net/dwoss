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

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.entity.Stock;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
//TODO removed all usages of this panel
@Deprecated
public class AfterInvoiceInfoPanel extends javax.swing.JPanel {

    Stock stockLocation;

    /** Creates new form AfterInvoiceInfoPanel */
    public AfterInvoiceInfoPanel() {
        initComponents();

        stockSelectionBox.setModel(new DefaultComboBoxModel(lookup(StockAgent.class).findAll(Stock.class).toArray(new Stock[0])));
        stockSelectionBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if ( value instanceof Stock ) {
                    label.setText(((Stock)value).getName());
                }
                return label;
            }
        });
    }

    public String getBallancedBy() {
        return balancingBox.getSelectedItem().toString();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        stockSelectionBox = new javax.swing.JComboBox();
        balancingBox = new javax.swing.JComboBox();

        jLabel3.setText("Standort:");

        stockSelectionBox.setMinimumSize(new java.awt.Dimension(250, 25));
        stockSelectionBox.setPreferredSize(new java.awt.Dimension(250, 25));
        stockSelectionBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockSelectionBoxActionPerformed(evt);
            }
        });

        balancingBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rechnungsbetrag bar ausgezahlt", "Rechnungsbetrag wird überwiesen", "Rechnungsbetrag wird verrechnet" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stockSelectionBox, 0, 1, Short.MAX_VALUE))
            .addComponent(balancingBox, 0, 299, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(stockSelectionBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(balancingBox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void stockSelectionBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockSelectionBoxActionPerformed
        if ( stockSelectionBox.getSelectedItem() != null ) stockLocation = (Stock)stockSelectionBox.getSelectedItem();
    }//GEN-LAST:event_stockSelectionBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox balancingBox;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox stockSelectionBox;
    // End of variables declaration//GEN-END:variables
}
