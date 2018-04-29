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

import java.util.Date;

import javax.swing.*;

import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.common.ui.CloseType;
import eu.ggnet.dwoss.common.ui.IPreClose;
import eu.ggnet.dwoss.mandator.api.service.ShipmentLabelValidator;
import eu.ggnet.dwoss.mandator.upi.CachedMandators;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.util.validation.ValidationUtil;
import eu.ggnet.saft.core.Dl;

/**
 *
 * @author pascal.perau
 */
public class ShipmentCreatePanel extends javax.swing.JPanel implements IPreClose {

    /** Creates new form ShipmentCreatePanel */
    public ShipmentCreatePanel() {
        initComponents();
        ownerBox.setModel(new DefaultComboBoxModel(Dl.local().lookup(CachedMandators.class).loadContractors().all().toArray()));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        idLabel = new javax.swing.JLabel();
        idField = new javax.swing.JTextField();
        ownerLabel = new javax.swing.JLabel();
        ownerBox = new javax.swing.JComboBox();

        setMinimumSize(new java.awt.Dimension(0, 0));
        setPreferredSize(new java.awt.Dimension(250, 80));

        idLabel.setText("Shipment ID:");

        ownerLabel.setText("Besitzer:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(idLabel)
                    .addComponent(ownerLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ownerBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(idField, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idLabel)
                    .addComponent(idField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ownerLabel)
                    .addComponent(ownerBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField idField;
    private javax.swing.JLabel idLabel;
    private javax.swing.JComboBox ownerBox;
    private javax.swing.JLabel ownerLabel;
    // End of variables declaration//GEN-END:variables

    public Shipment getShipment() {
        Shipment shipment = new Shipment();
        shipment.setDate(new Date());
        shipment.setShipmentId(idField.getText());
        shipment.setContractor((TradeName)ownerBox.getSelectedItem());
        return shipment;
    }

    @Override
    public boolean pre(CloseType type) {
        if ( type == CloseType.CANCEL ) return true;
        Shipment shipment = getShipment();
        if ( !ValidationUtil.isValidOrShow(SwingUtilities.getWindowAncestor(this), shipment) ) return false;
        if ( !Dl.remote().contains(ShipmentLabelValidator.class) ) return true;
        String warn = Dl.remote().lookup(ShipmentLabelValidator.class).validate(shipment.getShipmentId(), shipment.getContractor());
        if ( warn == null ) return true;
        int result = JOptionPane.showConfirmDialog(this, warn, "Achtung", JOptionPane.OK_CANCEL_OPTION);
        if ( result == JOptionPane.CANCEL_OPTION ) return false;
        return true;
    }
}