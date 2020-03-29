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

import eu.ggnet.dwoss.core.widget.swing.ComboBoxController;
import eu.ggnet.dwoss.core.widget.swing.CloseType;
import eu.ggnet.dwoss.core.widget.swing.IPreClose;

import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.mandator.api.service.ShipmentLabelValidator;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.core.system.util.ValidationUtil;
import eu.ggnet.dwoss.core.widget.Dl;

/**
 *
 * @author pascal.perau
 */
public class ShipmentEditPanel extends javax.swing.JPanel implements IPreClose {

    private final Shipment shipment;

    private final ComboBoxController<Shipment.Status> statusController;

    private final ComboBoxController<TradeName> ownerController;

    /** Creates new form ShipmentEditPanel */
    public ShipmentEditPanel(Shipment shipment) {
        this.shipment = shipment;
        initComponents();
        idField.setText(Long.toString(shipment.getId()));
        shipmentField.setText(shipment.getShipmentId());
        ownerController = new ComboBoxController<>(ownerBox, Dl.local().lookup(CachedMandators.class).loadContractors().all().toArray());
        statusController = new ComboBoxController<>(statusBox, Shipment.Status.values());
        ownerController.setSelected(shipment.getContractor());
        statusController.setSelected(shipment.getStatus());
    }

    public Shipment getShipment() {
        shipment.setDate(new Date());
        shipment.setShipmentId(shipmentField.getText());
        shipment.setContractor(ownerController.getSelected());
        shipment.setStatus(statusController.getSelected());
        return shipment;
    }

    @Override
    public boolean pre(CloseType type) {
        if ( type == CloseType.CANCEL ) return true;
        Shipment resultShipment = getShipment();
        if ( !ValidationUtil.isValidOrShow(SwingUtilities.getWindowAncestor(this), resultShipment) ) return false;
        if ( !Dl.remote().contains(ShipmentLabelValidator.class) ) return true;
        String warn = Dl.remote().lookup(ShipmentLabelValidator.class).validate(shipment.getShipmentId(), shipment.getContractor());
        if ( warn == null ) return true;
        int result = JOptionPane.showConfirmDialog(this, warn, "Achtung", JOptionPane.OK_CANCEL_OPTION);
        if ( result == JOptionPane.CANCEL_OPTION ) return false;
        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editShipLabel = new javax.swing.JLabel();
        shipmentField = new javax.swing.JTextField();
        editOwnerLabel = new javax.swing.JLabel();
        ownerBox = new javax.swing.JComboBox();
        idLabel = new javax.swing.JLabel();
        idField = new javax.swing.JTextField();
        statusLabel = new javax.swing.JLabel();
        statusBox = new javax.swing.JComboBox();

        editShipLabel.setText("Shipment ID:");

        editOwnerLabel.setText("Besitzer:");

        idLabel.setText("ID:");

        idField.setEditable(false);

        statusLabel.setText("Status:");

        statusBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(statusLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(editOwnerLabel, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(editShipLabel))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(idLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(shipmentField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(idField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ownerBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(statusBox, 0, 147, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idLabel)
                    .addComponent(idField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editShipLabel)
                    .addComponent(shipmentField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ownerBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editOwnerLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statusLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel editOwnerLabel;
    private javax.swing.JLabel editShipLabel;
    private javax.swing.JTextField idField;
    private javax.swing.JLabel idLabel;
    private javax.swing.JComboBox ownerBox;
    private javax.swing.JTextField shipmentField;
    private javax.swing.JComboBox statusBox;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables
}
