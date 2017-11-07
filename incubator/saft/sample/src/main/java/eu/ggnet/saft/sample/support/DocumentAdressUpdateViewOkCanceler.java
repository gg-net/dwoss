package eu.ggnet.saft.sample.support;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import javax.swing.JFrame;

import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.all.UiUtil;
import eu.ggnet.saft.core.swing.DialogButtonPanel;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 *
 * @author pascal.perau
 */
public class DocumentAdressUpdateViewOkCanceler extends javax.swing.JPanel implements Consumer<String>, ResultProducer<String> {

    private String originalAddress;

    private DialogButtonPanel buttons;

    public DocumentAdressUpdateViewOkCanceler() {
        initComponents();
        buttons = new DialogButtonPanel("Save", "Abbrechen");
        buttonPanel.add(buttons);
    }

    public String getAddress() {
        return adressArea.getText();
    }

    @Override
    public void accept(String t) {
        this.originalAddress = t;
        adressArea.setText(t);
    }

    @Override
    public String getResult() {
        if ( !buttons.isOk() ) return null;
        if ( UiUtil.isBlank(adressArea.getText()) ) return null;
        return adressArea.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        adressArea = new javax.swing.JTextArea();
        resetToOriginalButton = new javax.swing.JButton();
        resetToCustomerButton = new javax.swing.JButton();
        buttonPanel = new javax.swing.JPanel();

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

        buttonPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(buttonPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void resetToOriginalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetToOriginalButtonActionPerformed
        adressArea.setText(originalAddress);
    }//GEN-LAST:event_resetToOriginalButtonActionPerformed

    private void resetToCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetToCustomerButtonActionPerformed
        System.out.println("Would do a reset");
    }//GEN-LAST:event_resetToCustomerButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea adressArea;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton resetToCustomerButton;
    private javax.swing.JButton resetToOriginalButton;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        String adress = "Hans Mustermann\nMusterstrasse 22\n12345 Musterhausen";

        DocumentAdressUpdateViewOkCanceler view = new DocumentAdressUpdateViewOkCanceler();
        view.accept(adress);

        EventQueue.invokeAndWait(() -> {
            JFrame f = new JFrame("Test");
            f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            f.getContentPane().add(view);
            f.pack();
            f.setVisible(true);
        });

        System.out.println("Test");

    }

}