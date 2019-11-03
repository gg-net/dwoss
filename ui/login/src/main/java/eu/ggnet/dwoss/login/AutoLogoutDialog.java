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

import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.logging.Level;

import eu.ggnet.saft.experimental.auth.Guardian;
import eu.ggnet.saft.experimental.auth.AuthenticationException;

import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

/**
 *
 * @author bastian.venz
 */
public class AutoLogoutDialog extends javax.swing.JDialog {

    private static final Logger L = LoggerFactory.getLogger(AutoLogoutDialog.class.getName());

    private String typed = "";

    /** Creates new form AutoLogoutDialog */
    public AutoLogoutDialog(java.awt.Window parent, Set<String> onceloggedInUsers) {
        super(parent);

        initComponents();
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setLocationRelativeTo(parent);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if ( e.getID() == KeyEvent.KEY_PRESSED )
                    checkInput(e.getKeyChar());
                return false;
            }
        });
        //Fill loggedUserArea
        String res = "Bisher eingeloggte Nutzer:\n";
        for (String username : onceloggedInUsers) {
            res += username + "\n";
        }
        loggedUserArea.setText(res);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    loginButton.requestFocusInWindow();
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(AutoLogoutDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();

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

        closeButton = new javax.swing.JButton();
        loginButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        loggedUserArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Bitte Einloggen!");
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        closeButton.setText("DW beenden");
        closeButton.setToolTipText("Fährt die Deutsche Warenwirtschaft herunter");
        closeButton.setMaximumSize(new java.awt.Dimension(125, 29));
        closeButton.setMinimumSize(new java.awt.Dimension(125, 29));
        closeButton.setPreferredSize(new java.awt.Dimension(125, 29));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        getContentPane().add(closeButton, gridBagConstraints);

        loginButton.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        loginButton.setText("Einloggen");
        loginButton.setMaximumSize(new java.awt.Dimension(250, 40));
        loginButton.setMinimumSize(new java.awt.Dimension(250, 40));
        loginButton.setPreferredSize(new java.awt.Dimension(250, 40));
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(loginButton, gridBagConstraints);

        loggedUserArea.setEditable(false);
        loggedUserArea.setColumns(20);
        loggedUserArea.setRows(5);
        loggedUserArea.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.black, new java.awt.Color(204, 204, 255), java.awt.Color.black, new java.awt.Color(204, 204, 255)), "Logged Users"));
        jScrollPane1.setViewportView(loggedUserArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        getContentPane().add(jScrollPane1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        UiCore.getMainFrame().setVisible(false); // Shuts down everything.
    }//GEN-LAST:event_closeButtonActionPerformed

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        LoginView view = new LoginView(this, "Login", ModalityType.APPLICATION_MODAL);
        view.setLocationRelativeTo(this);
        view.setVisible(true);
        if ( view.isLoginOk() ) {
            try {
                Lookup.getDefault().lookup(Guardian.class).login(view.getUsername(), view.getPassword());
                this.dispose();
            } catch (AuthenticationException ex) {
                Ui.build(this).alert(ex.getMessage());
            } catch (Exception ex) {
                Ui.handle(ex);
            }
        }
    }//GEN-LAST:event_loginButtonActionPerformed

    private void checkInput(char c) {
        typed += c;
        if ( !typed.startsWith("+") || typed.length() > 4 ) {
            typed = "";
        } else if ( typed.length() == 4 ) {
            try {
                int id = Integer.parseInt(typed.substring(1));
                Lookup.getDefault().lookup(Guardian.class).quickAuthenticate(id);
                // TODO: Stupid use boolean return.
                if ( Lookup.getDefault().lookup(Guardian.class).getUsername() != null
                        && !Lookup.getDefault().lookup(Guardian.class).getUsername().trim().equals("") ) {
                    this.dispose();
                } else {
                    typed = "";
                }
            } catch (NumberFormatException numberFormatException) {
                System.out.println("Entered invalid code: " + typed);
                typed = "";
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea loggedUserArea;
    private javax.swing.JButton loginButton;
    // End of variables declaration//GEN-END:variables
}