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

import javax.swing.JOptionPane;

import eu.ggnet.dwoss.customer.opi.AddressService;
import eu.ggnet.dwoss.redtape.ee.entity.Address;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.util.*;
import eu.ggnet.saft.Dl;

/**
 *
 * @author pascal.perau
 */
public class DocumentAdressUpdateView extends javax.swing.JPanel implements IPreClose {

    private String originalAddress;

    private long customerId;

    private final boolean invoice;

    /** Creates new form DocumentAdressUpdateView */
    public DocumentAdressUpdateView(long customerId, String address, boolean invoice) {
        initComponents();
        this.customerId = customerId;
        this.originalAddress = address;
        adressArea.setText(address);
        this.invoice = invoice;
    }

    public String getAddress() {
        return adressArea.getText();
    }

    @Override
    public boolean pre(CloseType type) {
        if ( type == CloseType.OK && adressArea.getText().trim().equals("") ) {
            JOptionPane.showMessageDialog(this, "Addressfeld ist leer...");
        }
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
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        adressArea = new javax.swing.JTextArea();
        resetToOriginalButton = new javax.swing.JButton();
        resetToCustomerButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(300, 300));
        setLayout(new java.awt.GridBagLayout());

        adressArea.setColumns(20);
        adressArea.setRows(5);
        jScrollPane1.setViewportView(adressArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        resetToOriginalButton.setText("<html>Auf Originaladresse<br />zurücksetzen</html>");
        resetToOriginalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetToOriginalButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        add(resetToOriginalButton, gridBagConstraints);

        resetToCustomerButton.setText("<html>Auf Kundenadresse<br />zurücksetzen</html>");
        resetToCustomerButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetToCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetToCustomerButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(resetToCustomerButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void resetToOriginalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetToOriginalButtonActionPerformed
        adressArea.setText(originalAddress);
    }//GEN-LAST:event_resetToOriginalButtonActionPerformed

    private void resetToCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetToCustomerButtonActionPerformed
        if ( invoice ) adressArea.setText(Dl.remote().lookup(AddressService.class).defaultAddressLabel(customerId, AddressType.INVOICE));
        else adressArea.setText(Dl.remote().lookup(AddressService.class).defaultAddressLabel(customerId, AddressType.SHIPPING));
    }//GEN-LAST:event_resetToCustomerButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea adressArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton resetToCustomerButton;
    private javax.swing.JButton resetToOriginalButton;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        Address invoice = new Address("Blubba, invoice");

        // TODO : fill Dl.remote().add(AddressService.class) // With a sample
        DocumentAdressUpdateView view = new DocumentAdressUpdateView(1, invoice.getDescription(), true);
        OkCancelDialog<DocumentAdressUpdateView> dialog = new OkCancelDialog<>("TOLLER TITEL", view);
        dialog.setVisible(true);
        System.exit(0);
    }
}
