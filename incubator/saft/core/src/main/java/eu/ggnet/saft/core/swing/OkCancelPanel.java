/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.saft.core.swing;

import java.util.function.Consumer;

import javax.swing.JPanel;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.all.VetoableOnOk;

public class OkCancelPanel<V, U, T extends JPanel & VetoableOnOk & ResultProducer<V> & Consumer<U>> extends javax.swing.JPanel implements VetoableOnOk, ResultProducer<Reply<V>>, Consumer<U> {

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

        okButton.setText("  Ok  ");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton);

        cancelButton.setText(" Abbrechen ");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        add(buttonPanel, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if ( !panel.mayClose() ) return;
        ok = true;
        Ui.closeWindowOf(this);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        Ui.closeWindowOf(this);
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    private final T panel;

    private boolean ok = false;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public OkCancelPanel(T panel) {
        initComponents();
        add(panel);
        this.panel = panel;
    }

    @Override
    public Reply<V> getResult() {
        return ok ? Reply.success(panel.getResult()) : Reply.failure("Cancel pressed");
    }

    @Override
    public void accept(U t) {
        panel.accept(t);
    }

    @Override
    public boolean mayClose() {
        // Only here for simplcity
        return false;
    }
}
