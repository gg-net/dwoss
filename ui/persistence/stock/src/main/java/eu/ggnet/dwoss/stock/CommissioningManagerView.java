/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.stock;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.*;

import javax.swing.*;

import org.apache.commons.lang3.SystemUtils;

import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockUnit;

public class CommissioningManagerView extends javax.swing.JDialog {

    private class StockTransactionRenderer extends JLabel implements ListCellRenderer {

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
            boolean commissionComplete = false;

            if ( model != null && model.getStockUnits().containsAll(st.getUnits()) ) {
                commissionComplete = true;
            }

            String ccs = "Kommisionsstatus: nicht vollständig";
            if ( commissionComplete ) ccs = "Kommisionsstatus: vollständig";

            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
            setText("<html>Transaktion(" + st.getId() + ") von " + st.getSource().getName() + " nach " + st.getDestination().getName() + "<br>"
                    + st.getStatus().getType() + " - " + df.format(st.getStatus().getOccurence()) + "<br />"
                    + ccs + "</html>");

            if ( isSelected && commissionComplete ) {
                setBackground(Color.GREEN);
                setForeground(Color.WHITE);
            } else if ( isSelected ) {
                setBackground(Color.RED);
                setForeground(Color.WHITE);
            } else if ( commissionComplete ) {
                setBackground(Color.WHITE);
                setForeground(Color.GREEN);
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.RED);
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

    public class StockUnitRenderer extends JLabel implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, // the list
                Object value, // value to display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) // does the cell have focus
        {
            if ( !(value instanceof StockUnit) ) {
                setText("Missuse:renderer=" + this.getClass().getSimpleName() + ", value=" + value.getClass().getSimpleName());
                return this;
            }
            StockUnit stockUnit = (StockUnit)value;
            setText(stockUnit.getRefurbishId() + " - " + stockUnit.getName());
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

    private CommissioningManagerModel model;

    private CommissioningManagerController controller;

    /** Creates new form StockTransactionManagerHandelStatusDialog */
    public CommissioningManagerView(Window parent) {
        super(parent);
        setModalityType(ModalityType.APPLICATION_MODAL);
        initComponents();
        if ( parent != null ) setLocationRelativeTo(parent);
        transactionList.setCellRenderer(new StockTransactionRenderer());
        unitsList.setCellRenderer(new StockUnitRenderer());
    }

    public void setModel(CommissioningManagerModel model) {
        this.model = model;
        updateStatus();
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                updateStatus();
                if ( evt.getPropertyName().equals(CommissioningManagerModel.PROP_FULL) ) {
                    boolean temp = (Boolean)evt.getNewValue();
                    done1Button.setEnabled(temp);
                    done2Button.setEnabled(temp);
                }
                if ( evt.getPropertyName().equals(CommissioningManagerModel.PROP_PARTICIPANT_ONE) ) {
                    done1Button.setText("Authentifiziere " + evt.getNewValue().toString());
                }
                if ( evt.getPropertyName().equals(CommissioningManagerModel.PROP_PARTICIPANT_TWO) ) {
                    done2Button.setText("Authentifiziere " + evt.getNewValue().toString());
                }
                if ( evt.getPropertyName().equals(CommissioningManagerModel.PROP_COMPLETEABLE) ) {
                    boolean temp = (Boolean)evt.getNewValue();
                    confirmButton.setEnabled(temp);
                }
            }
        });
        unitsList.setModel(model.getUnitModel());
        transactionList.setModel(model.getTransactionModel());
    }

    public void setController(CommissioningManagerController controller) {
        this.controller = controller;
    }

    private void updateStatus() {
        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<u>StatusMsg:</u> ").append(model.getStatusMessage()).append("<br />");
        if ( !model.isFull() ) {
            sb.append("<div style=\"color:red\">Mind. ein Gerät fehlt noch.</div>");
        }
        if ( !model.isParticipantOneAuthenticated() ) {
            sb.append("<div style=\"color:red\">").append(model.getParticipantOne()).append(" noch nicht authentifiziert.</div>");
        }
        if ( !model.isParticipantTwoAuthenticated() ) {
            sb.append("<div style=\"color:red\">").append(model.getParticipantTwo()).append(" noch nicht authentifiziert.</div>");
        }
        if ( !model.isCompleteAble() ) {
            sb.append("<div style=\"color:red\">Transaktionsvorgang nicht vollständig.</div>");
        } else {
            sb.append("<div style=\"color:#C8C800\">Transaktionsvorgang vollstädigen, bitte bestätigen.</div>");
        }
        sb.append("</html>");
        statusTextPane.setText(sb.toString());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        unitIdLabel = new javax.swing.JLabel();
        unitIdTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        unitsList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        transactionList = new javax.swing.JList();
        cancelButton = new javax.swing.JButton();
        done2Button = new javax.swing.JButton();
        detailButton = new javax.swing.JButton();
        failButton = new javax.swing.JButton();
        done1Button = new javax.swing.JButton();
        confirmButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        statusTextPane = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Kommissionsmanager");

        unitIdLabel.setText("UnitId:");

        unitIdTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unitIdTextFieldActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(unitsList);

        jScrollPane2.setViewportView(transactionList);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        done2Button.setText("Authentifiziere User 2");
        done2Button.setEnabled(false);
        done2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                done2ButtonActionPerformed(evt);
            }
        });

        detailButton.setText("Details");
        detailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailButtonActionPerformed(evt);
            }
        });

        failButton.setText("Fail Transaktion");
        failButton.setEnabled(false);

        done1Button.setText("Authentifiziere User 1");
        done1Button.setEnabled(false);
        done1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                done1ButtonActionPerformed(evt);
            }
        });

        confirmButton.setFont(confirmButton.getFont());
        confirmButton.setText("Statusänderung durchführen");
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmButtonActionPerformed(evt);
            }
        });

        statusTextPane.setContentType("text/html"); // NOI18N
        jScrollPane4.setViewportView(statusTextPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(unitIdLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(unitIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane4)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(failButton, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(detailButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cancelButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(done2Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(confirmButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(done1Button, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(detailButton)
                            .addComponent(failButton))
                        .addGap(31, 31, 31)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(done1Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(done2Button, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(confirmButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(unitIdLabel)
                            .addComponent(unitIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void unitIdTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unitIdTextFieldActionPerformed
        if ( controller != null ) controller.addUnit(unitIdTextField.getText());
    }//GEN-LAST:event_unitIdTextFieldActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void done1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_done1ButtonActionPerformed
        if ( controller != null ) controller.authenticateUserOne();
    }//GEN-LAST:event_done1ButtonActionPerformed

    private void done2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_done2ButtonActionPerformed
        if ( controller != null ) controller.authenticateUserTwo();
    }//GEN-LAST:event_done2ButtonActionPerformed

    private void confirmButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmButtonActionPerformed
        if ( controller == null ) return;
        boolean successful = controller.executeTransmutation();
        if ( successful ) this.setVisible(false);
    }//GEN-LAST:event_confirmButtonActionPerformed

    private void detailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailButtonActionPerformed
        StringBuilder sb = new StringBuilder("Noch nicht erfasst: ").append(SystemUtils.LINE_SEPARATOR).append(SystemUtils.LINE_SEPARATOR);
        SortedSet<String> missing = model.getMissingRefurbishedIds();
        if ( missing.isEmpty() ) {
            sb.append("Alle Gerät sind erfasst.");
        } else {
            for (String line : missing) {
                sb.append(" - ").append(line).append(SystemUtils.LINE_SEPARATOR);
            }
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }//GEN-LAST:event_detailButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton cancelButton;
    javax.swing.JButton confirmButton;
    javax.swing.JButton detailButton;
    javax.swing.JButton done1Button;
    javax.swing.JButton done2Button;
    javax.swing.JButton failButton;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JScrollPane jScrollPane2;
    javax.swing.JScrollPane jScrollPane4;
    javax.swing.JTextPane statusTextPane;
    javax.swing.JList transactionList;
    javax.swing.JLabel unitIdLabel;
    javax.swing.JTextField unitIdTextField;
    javax.swing.JList unitsList;
    // End of variables declaration//GEN-END:variables
}
