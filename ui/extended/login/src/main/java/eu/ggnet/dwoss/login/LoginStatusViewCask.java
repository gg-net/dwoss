package eu.ggnet.dwoss.login;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.authorisation.UserChangeListener;

import eu.ggnet.saft.core.ToolbarComponent;

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
