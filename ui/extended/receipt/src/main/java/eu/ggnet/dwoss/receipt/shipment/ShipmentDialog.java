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
package eu.ggnet.dwoss.receipt.shipment;

import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.mandator.upi.CachedMandators;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.stock.entity.Shipment;
import eu.ggnet.dwoss.util.ComboBoxController;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.core.ui.UserPreferences;

/**
 *
 * @author pascal.perau
 */
public class ShipmentDialog extends javax.swing.JDialog {

    private ShipmentModel model;

    private ShipmentController controller;

    private ComboBoxController<Shipment.Status> filterStatus;

    private ComboBoxController<TradeName> filterOwner;

    public ShipmentDialog(ShipmentController controller) {
        this(UiCore.getMainFrame(), controller);
    }

    public ShipmentDialog(java.awt.Window parent, ShipmentController controller) {
        super(parent);
        initComponents();
        setModalityType(ModalityType.APPLICATION_MODAL);
        this.model = controller.getModel();
        this.controller = controller;
        controller.setView(this);
        try {
            shipmentTable.setModel(model);
            model.setTable(shipmentTable);
        } catch (ArrayIndexOutOfBoundsException e) {
            LoggerFactory.getLogger(ShipmentDialog.class).error("Exception happend were we expected it. So bug is found, now we need to fix it, {}, {}", e.getClass().getName(), e.getMessage());
        }
        filterStatus = new ComboBoxController<>(filterStatusbox, Shipment.Status.values());
        filterOwner = new ComboBoxController<>(filterOwnerbox, Dl.local().lookup(CachedMandators.class).loadContractors().all().toArray());
        if ( parent != null ) setLocationRelativeTo(parent);
        Dl.local().lookup(UserPreferences.class).loadLocation(this.getClass(), this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        createButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        inclusionButton = new javax.swing.JButton();
        filterPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        filterShipmentLabel = new javax.swing.JLabel();
        filterShipmentField = new javax.swing.JTextField();
        filterStatusLabel = new javax.swing.JLabel();
        filterStatusbox = new javax.swing.JComboBox();
        filterOwnerLabel = new javax.swing.JLabel();
        filterOwnerbox = new javax.swing.JComboBox();
        filterStatusEnableBox = new javax.swing.JCheckBox();
        filterShipmentIdEnableBox = new javax.swing.JCheckBox();
        filterOwnerEnableBox = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        shipmentTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Shipments");
        setMinimumSize(new java.awt.Dimension(400, 400));
        setName("Shipments"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        createButton.setFont(createButton.getFont());
        createButton.setText("Neu");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        deleteButton.setFont(deleteButton.getFont());
        deleteButton.setText("Löschen");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        editButton.setFont(editButton.getFont());
        editButton.setText("Bearbeiten");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        exitButton.setFont(exitButton.getFont());
        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        inclusionButton.setText("Aufnahme");
        inclusionButton.setToolTipText("Add Unit to selected Shipment");
        inclusionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inclusionButtonActionPerformed(evt);
            }
        });

        filterPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        filterLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        filterLabel.setText("Shipmentfilter:");

        filterShipmentLabel.setText("Shipment ID:");

        filterShipmentField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterShipmentIdKeyReleased(evt);
            }
        });

        filterStatusLabel.setText("Status:");

        filterStatusbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterStatusAction(evt);
            }
        });

        filterOwnerLabel.setText("Besitzer:");

        filterOwnerbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterOwnerboxAction(evt);
            }
        });

        filterStatusEnableBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterStatusEnableBoxActionPerformed(evt);
            }
        });

        filterShipmentIdEnableBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterShipmentIdEnableBoxActionPerformed(evt);
            }
        });

        filterOwnerEnableBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterOwnerEnableBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout filterPanelLayout = new javax.swing.GroupLayout(filterPanel);
        filterPanel.setLayout(filterPanelLayout);
        filterPanelLayout.setHorizontalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(filterShipmentLabel)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, filterPanelLayout.createSequentialGroup()
                            .addComponent(filterShipmentField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(filterShipmentIdEnableBox)
                            .addGap(12, 12, 12))
                        .addComponent(filterStatusLabel)
                        .addGroup(filterPanelLayout.createSequentialGroup()
                            .addComponent(filterStatusbox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(filterStatusEnableBox))
                        .addComponent(filterOwnerLabel)
                        .addGroup(filterPanelLayout.createSequentialGroup()
                            .addComponent(filterOwnerbox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(filterOwnerEnableBox)))
                    .addComponent(filterLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        filterPanelLayout.setVerticalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filterLabel)
                .addGap(12, 12, 12)
                .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(filterPanelLayout.createSequentialGroup()
                        .addComponent(filterShipmentLabel)
                        .addGap(2, 2, 2)
                        .addComponent(filterShipmentField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(filterShipmentIdEnableBox))
                .addGap(18, 18, 18)
                .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(filterPanelLayout.createSequentialGroup()
                        .addComponent(filterStatusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterStatusbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(filterStatusEnableBox))
                .addGap(18, 18, 18)
                .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(filterPanelLayout.createSequentialGroup()
                        .addComponent(filterOwnerLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterOwnerbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(filterOwnerEnableBox))
                .addContainerGap())
        );

        shipmentTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        shipmentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                shipmentTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(shipmentTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                            .addComponent(editButton, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(exitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(createButton, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)))
                    .addComponent(filterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inclusionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(filterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 139, Short.MAX_VALUE)
                        .addComponent(inclusionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(createButton)
                            .addComponent(editButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(deleteButton)
                            .addComponent(exitButton))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        controller.createShipment();
    }//GEN-LAST:event_createButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        controller.editShipment();
    }//GEN-LAST:event_editButtonActionPerformed

    private void inclusionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inclusionButtonActionPerformed
        controller.inclusion();
    }//GEN-LAST:event_inclusionButtonActionPerformed

    private void filterShipmentIdKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterShipmentIdKeyReleased
        model.filterShipmentId(filterShipmentField.getText(), filterShipmentIdEnableBox.isSelected());
    }//GEN-LAST:event_filterShipmentIdKeyReleased

    private void filterStatusAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterStatusAction
        model.filterStatus(filterStatus.getSelected(), filterStatusEnableBox.isSelected());
    }//GEN-LAST:event_filterStatusAction

    private void filterStatusEnableBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterStatusEnableBoxActionPerformed
        model.filterStatus(filterStatus.getSelected(), filterStatusEnableBox.isSelected());
    }//GEN-LAST:event_filterStatusEnableBoxActionPerformed

    private void filterShipmentIdEnableBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterShipmentIdEnableBoxActionPerformed
        model.filterShipmentId(filterShipmentField.getText(), filterShipmentIdEnableBox.isSelected());
    }//GEN-LAST:event_filterShipmentIdEnableBoxActionPerformed

    private void filterOwnerEnableBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterOwnerEnableBoxActionPerformed
        model.filterOwner(filterOwner.getSelected(), filterOwnerEnableBox.isSelected());
    }//GEN-LAST:event_filterOwnerEnableBoxActionPerformed

    private void filterOwnerboxAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterOwnerboxAction
        model.filterOwner(filterOwner.getSelected(), filterOwnerEnableBox.isSelected());
    }//GEN-LAST:event_filterOwnerboxAction

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        controller.deleteShipment();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void shipmentTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_shipmentTableMouseClicked
        if ( evt.getClickCount() == 2 ) controller.inclusion();
    }//GEN-LAST:event_shipmentTableMouseClicked

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Dl.local().lookup(UserPreferences.class).storeLocation(this.getClass(), this);
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JCheckBox filterOwnerEnableBox;
    private javax.swing.JLabel filterOwnerLabel;
    private javax.swing.JComboBox filterOwnerbox;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JTextField filterShipmentField;
    private javax.swing.JCheckBox filterShipmentIdEnableBox;
    private javax.swing.JLabel filterShipmentLabel;
    private javax.swing.JCheckBox filterStatusEnableBox;
    private javax.swing.JLabel filterStatusLabel;
    private javax.swing.JComboBox filterStatusbox;
    private javax.swing.JButton inclusionButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable shipmentTable;
    // End of variables declaration//GEN-END:variables

    public static void main(String args[]) throws Exception {

    }
}
