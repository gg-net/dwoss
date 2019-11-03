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
package eu.ggnet.dwoss.login;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.experimental.auth.UserChangeListener;
import eu.ggnet.dwoss.common.ui.ToolbarComponent;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = ToolbarComponent.class)
public class LoginStatusViewCask extends javax.swing.JPanel implements ToolbarComponent, UserChangeListener {

    public LoginStatusViewCask() {
        initComponents();
    }

    @Override
    public void loggedIn(String name) {
        userTextField.setText(name);
    }

    @Override
    public void loggedOut() {
        userTextField.setText("None");
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        userTextField = new javax.swing.JTextField();
        showRightButton = new javax.swing.JButton();

        userTextField.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        userTextField.setForeground(new java.awt.Color(153, 0, 0));
        userTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        userTextField.setText("None");

        showRightButton.setText("Rechte");
        showRightButton.setToolTipText("Aktive Berechtigung des Nutzers anzeigen");
        showRightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRightButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showRightButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showRightButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showRightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRightButtonActionPerformed
        new ShowRightsAction().actionPerformed(evt);
    }//GEN-LAST:event_showRightButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton showRightButton;
    private javax.swing.JTextField userTextField;
    // End of variables declaration//GEN-END:variables
}
