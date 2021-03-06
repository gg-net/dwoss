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
package eu.ggnet.dwoss.stock.ui;

import java.awt.Component;
import java.text.DateFormat;

import javax.swing.*;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.stock.ui.cap.RemoveUnitFromTransactionAction;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.ClosedListener;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_TRANSACTION_TO_CANCLE;
import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_TRANSACTION_TO_REMOVE_UNIT;

public class StockTransactionManagerView extends javax.swing.JPanel implements ClosedListener {

    @Override
    public void closed() {
        controller.cancelLoader();
    }

    public class StockTransactionRenderer extends JLabel implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, // the list
                Object value, // value to display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) // does the cell have focus
        {
            if ( !(value instanceof StockTransaction) ) {
                setText("Use of StockTransactionManagerListCellRenderer for a non standard value");
                return this;
            }
            StockTransaction st = (StockTransaction)value;
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
            String source = (st.getSource() == null ? "Keine Quelle" : st.getSource().getName());
            String destination = (st.getDestination() == null ? "Kein Ziel" : st.getDestination().getName());
            setText("<html>Transaktion(" + st.getId() + ") Type: " + st.getType() + "<br />Quelle: " + source + ", Ziel: " + destination + "<br />"
                    + "Letzter Status: " + st.getStatus().getType() + " vom " + df.format(st.getStatus().getOccurence()) + "<hr /></html>");

            if ( isSelected ) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

    private StockTransactionManagerController controller;

    private StockTransactionManagerModel model;

    /** Creates new form StockTransactionManagerView */
    public StockTransactionManagerView() {
        initComponents();

        typeComboBox.setModel(new DefaultComboBoxModel(StockTransactionType.values()));
        statusComboBox.setModel(new DefaultComboBoxModel(StockTransactionStatusType.values()));
        transactionList.setCellRenderer(new StockTransactionRenderer());

        Guardian accessCos = Dl.local().lookup(Guardian.class);
        if ( accessCos != null ) {
            accessCos.add(cancelTransactionButton, UPDATE_TRANSACTION_TO_CANCLE);
            accessCos.add(removeUnitFromTransactionButton, UPDATE_TRANSACTION_TO_REMOVE_UNIT);
        }

    }

    public void setController(final StockTransactionManagerController controller) {
        this.controller = controller;
    }

    public void setModel(StockTransactionManagerModel model) {
        this.model = model;
        transactionList.setModel(model);
        typeComboBox.setSelectedItem(model.getTransactionType());
        statusComboBox.setSelectedItem(model.getStatusType());
    }

    private void filterChanged() {
        if ( controller == null ) return;
        controller.reload();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        transactionList = new javax.swing.JList();
        detailButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        cancelTransactionButton = new javax.swing.JButton();
        typeComboBox = new javax.swing.JComboBox();
        typeLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        statusComboBox = new javax.swing.JComboBox();
        removeUnitFromTransactionButton = new javax.swing.JButton();

        transactionList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(transactionList);

        detailButton.setText("Details");
        detailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Schließen");
        closeButton.setMinimumSize(new java.awt.Dimension(100, 29));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        cancelTransactionButton.setText("Transaktion abbrechen");
        cancelTransactionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelTransactionButtonActionPerformed(evt);
            }
        });

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "test, Tess 2" }));
        typeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeComboBoxActionPerformed(evt);
            }
        });

        typeLabel.setText("Type:");

        statusLabel.setText("Status:");

        statusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        statusComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusComboBoxActionPerformed(evt);
            }
        });

        removeUnitFromTransactionButton.setText("Einzelnes Gerät entfernen");
        removeUnitFromTransactionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeUnitFromTransactionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(typeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(statusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(statusComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 134, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(detailButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelTransactionButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(closeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeUnitFromTransactionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statusLabel)
                    .addComponent(statusComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(detailButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelTransactionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeUnitFromTransactionButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE))
                .addContainerGap())
        );

   
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        Ui.closeWindowOf(this);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void typeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeComboBoxActionPerformed
        model.setTransactionType((StockTransactionType)typeComboBox.getSelectedItem());
        filterChanged();
    }//GEN-LAST:event_typeComboBoxActionPerformed

    private void statusComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusComboBoxActionPerformed
        model.setStatusType((StockTransactionStatusType)statusComboBox.getSelectedItem());
        filterChanged();
    }//GEN-LAST:event_statusComboBoxActionPerformed

    private void detailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailButtonActionPerformed
        if ( transactionList.isSelectionEmpty() ) return;
        if ( controller != null ) controller.showDetails((StockTransaction)transactionList.getSelectedValue());
    }//GEN-LAST:event_detailButtonActionPerformed

    private void cancelTransactionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelTransactionButtonActionPerformed
        if ( transactionList.isSelectionEmpty() ) return;
        if ( controller != null ) controller.cancel((StockTransaction)transactionList.getSelectedValue());
    }//GEN-LAST:event_cancelTransactionButtonActionPerformed

    private void removeUnitFromTransactionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeUnitFromTransactionButtonActionPerformed
        new RemoveUnitFromTransactionAction().actionPerformed(evt);
    }//GEN-LAST:event_removeUnitFromTransactionButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelTransactionButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton detailButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeUnitFromTransactionButton;
    private javax.swing.JComboBox statusComboBox;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JList transactionList;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables

}
