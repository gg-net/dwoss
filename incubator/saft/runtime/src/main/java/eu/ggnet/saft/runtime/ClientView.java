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
package eu.ggnet.saft.runtime;

import java.awt.BorderLayout;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.SwingCore;

/**
 * Client View, Main Frame.
 * <p>
 * @author oliver.guenther
 */
public class ClientView extends javax.swing.JFrame {

    final static String BUNDLE = "eu/ggnet/saft/runtime/Bundle";

    /**
     * Creates new form ClientView
     * <p>
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public ClientView() {
        initComponents();
        initFxComponents();
        setTitle(loadBundle().getString("ClientView.title")); // NOI18N
        setIconImage(new ImageIcon(loadIcon()).getImage());
    }

    private void initFxComponents() {
        try {
            progressIndicator = new ProgressIndicator();
            progressIndicator.setProgress(0);
            BorderPane pane = new BorderPane(progressIndicator);
            JFXPanel wrap = SwingCore.wrap(pane);
            extraProgressPanel.add(wrap, BorderLayout.CENTER);
        } catch (InterruptedException ex) {
            Ui.handle(ex);
        }
    }

    static URL loadIcon() {
        return ClientView.class.getResource("app-icon1.png"); // NOI18N
    }

    static ResourceBundle loadBundle() {
        return ResourceBundle.getBundle("eu/ggnet/saft/runtime/Bundle"); // NOI18N
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        progressBar.setVisible(false);
        extraProgressPanel.setVisible(false);
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

        messageLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        toolBar = new javax.swing.JToolBar();
        mainPanel = new javax.swing.JPanel();
        extraProgressPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        messageLabel.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        getContentPane().add(messageLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        getContentPane().add(progressBar, gridBagConstraints);

        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(toolBar, gridBagConstraints);

        mainPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(mainPanel, gridBagConstraints);

        extraProgressPanel.setPreferredSize(new java.awt.Dimension(25, 25));
        extraProgressPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        getContentPane().add(extraProgressPanel, gridBagConstraints);
        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JPanel extraProgressPanel;
    javax.swing.JPanel mainPanel;
    javax.swing.JMenuBar menuBar;
    javax.swing.JLabel messageLabel;
    javax.swing.JProgressBar progressBar;
    javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

    ProgressIndicator progressIndicator;

}
