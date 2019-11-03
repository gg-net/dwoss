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
package eu.ggnet.dwoss.receipt.ui.shipment;

import java.awt.Window;

import eu.ggnet.dwoss.receipt.ee.UnitProcessor;
import eu.ggnet.dwoss.receipt.ui.UiUnitSupport;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.saft.core.Dl;

/**
 *
 * @author pascal.perau
 */
public class ShipmentInclusionViewCask extends javax.swing.JDialog {

    private final Shipment productShipment;

    private final StockTransaction stockTransaction;

    private final UiUnitSupport controller;

    private boolean inclusionAborted;

    private boolean inclusionClosed;

    public ShipmentInclusionViewCask(java.awt.Window parent, Shipment shipment, StockTransaction stockTransaction) {
        this(parent, shipment, stockTransaction, Dl.remote().lookup(UnitProcessor.class));
    }

    /** Creates new form ShipmentInclusion */
    public ShipmentInclusionViewCask(Window parent, Shipment shipment, StockTransaction stockTransaction, UnitProcessor unitProcessor) {
        super(parent);
        setModalityType(ModalityType.APPLICATION_MODAL);
        this.productShipment = shipment;
        this.stockTransaction = stockTransaction;
        initComponents();
        inclusionShipField.setText(shipment.getShipmentId());
        inclusionOwnerField.setText(shipment.getContractor().toString());
        this.controller = new UiUnitSupport(unitProcessor);

    }

    public boolean inclusionAborted() {
        return inclusionAborted;
    }

    public boolean inclusionClosed() {
        return inclusionClosed;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inclusionShipLabel = new javax.swing.JLabel();
        inclusionOwnerLabel = new javax.swing.JLabel();
        inclusionOkButton = new javax.swing.JButton();
        inclusionCancelButton = new javax.swing.JButton();
        inclusionUnittButton = new javax.swing.JButton();
        inclusionShipField = new javax.swing.JTextField();
        inclusionOwnerField = new javax.swing.JTextField();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        inclusionShipLabel.setText("Shipment ID:");

        inclusionOwnerLabel.setText("Besitzer:");

        inclusionOkButton.setText("Aufnahme abschließen");
        inclusionOkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inclusionOkButtonActionPerformed(evt);
            }
        });

        inclusionCancelButton.setText("Aufnahme unterbrechen");
        inclusionCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inclusionCancelButtonActionPerformed(evt);
            }
        });

        inclusionUnittButton.setText("Gerät hinzufügen");
        inclusionUnittButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inclusionUnittButtonActionPerformed(evt);
            }
        });

        inclusionShipField.setEditable(false);

        inclusionOwnerField.setEditable(false);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inclusionUnittButton, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(inclusionOkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inclusionCancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(inclusionShipLabel)
                            .addComponent(inclusionOwnerLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(inclusionOwnerField, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                            .addComponent(inclusionShipField, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inclusionShipLabel)
                    .addComponent(inclusionShipField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inclusionOwnerLabel)
                    .addComponent(inclusionOwnerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inclusionUnittButton, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inclusionOkButton)
                    .addComponent(inclusionCancelButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inclusionUnittButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inclusionUnittButtonActionPerformed
        if ( controller != null ) controller.createUnit(stockTransaction, productShipment, this);
    }//GEN-LAST:event_inclusionUnittButtonActionPerformed

    private void inclusionOkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inclusionOkButtonActionPerformed
        this.setVisible(false);
        this.inclusionClosed = true;
    }//GEN-LAST:event_inclusionOkButtonActionPerformed

    private void inclusionCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inclusionCancelButtonActionPerformed
        this.setVisible(false);
        this.inclusionAborted = true;
    }//GEN-LAST:event_inclusionCancelButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton inclusionCancelButton;
    private javax.swing.JButton inclusionOkButton;
    private javax.swing.JTextField inclusionOwnerField;
    private javax.swing.JLabel inclusionOwnerLabel;
    private javax.swing.JTextField inclusionShipField;
    private javax.swing.JLabel inclusionShipLabel;
    private javax.swing.JButton inclusionUnittButton;
    // End of variables declaration//GEN-END:variables

}