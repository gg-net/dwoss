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
package eu.ggnet.dwoss.util;

import java.awt.Color;
import java.awt.print.PrinterException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.text.*;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author oliver.guenther
 * @deprecated use HtmlPanel and the SAFT
 */
@Deprecated
public class HtmlDialog extends javax.swing.JDialog {

    public HtmlDialog() {
        this(null, null);
    }

    public HtmlDialog(java.awt.Window parent) {
        this(parent, null);
    }

    /** Creates new form StockTransactionManagerDetailDialog */
    public HtmlDialog(java.awt.Window parent, ModalityType modalityType) {
        super(parent);
        initComponents();
        if ( modalityType == null ) setModalityType(ModalityType.APPLICATION_MODAL);
        else setModalityType(modalityType);
        setLocationRelativeTo(parent);
    }

    public HtmlDialog setText(String text) {
        documentTextPane.setText(text.replaceFirst("\\<\\?.*\\?\\>", "")); // Solution for XHTML
        documentTextPane.setCaretPosition(0);
        return this;
    }

    private void search() {
        try {
            Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);

            documentTextPane.getHighlighter().removeAllHighlights();
            StyledDocument styledDocument = documentTextPane.getStyledDocument();
            String text = styledDocument.getText(0, styledDocument.getLength());

            if ( StringUtils.isBlank(text) || StringUtils.isBlank(searchField.getText()) ) return;
            int indexOf = text.indexOf(searchField.getText());

            if ( indexOf == -1 ) JOptionPane.showMessageDialog(this, "Nichts gefunden.");
            while (indexOf != -1) {
                try {
                    documentTextPane.getHighlighter().addHighlight(indexOf, indexOf + searchField.getText().length(), painter);
                    indexOf = text.indexOf(searchField.getText(), indexOf + 1);
                } catch (BadLocationException ex) {
                    Logger.getLogger(HtmlDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(HtmlDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        documentTextPane = new javax.swing.JTextPane();
        closeButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        documentTextPane.setEditable(false);
        documentTextPane.setContentType("text/html"); // NOI18N
        jScrollPane1.setViewportView(documentTextPane);

        closeButton.setText("Schließen");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        printButton.setText("Drucken");
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });

        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        searchButton.setText("Suchen");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton)
                        .addGap(18, 18, 18)
                        .addComponent(printButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 815, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton)
                    .addComponent(printButton)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        try {
            documentTextPane.print();
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, ex, "PrinterException", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_printButtonActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        search();
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        search();
    }//GEN-LAST:event_searchButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        HtmlDialog dialog = new HtmlDialog();
        dialog.setText(getTestText());
        dialog.setVisible(true);
        System.out.println("Now the dialog is closed");
        System.exit(0);
    }

    private static String getTestText() {
        String s = "<html>"
                + "<head>"
                + "<style type=\"text/css\">"
                + "body  {"
                + "font: 14px Verdana, Arial, Helvetica, sans-serif;"
                + "}"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<h2><u><i>Transaktion Nummer:</i></u> 1 - <u><i>Type:</i></u> BLALBLAA - <u><i>Status:</i></u> PREPARED</h2>"
                + "<h2><u><i>Quelle:</i></u> Strusbek - <u><i>Ziel:</i></u> Manhagener Allee</h2>"
                + "<hr />"
                + "<ul>"
                + "<li>Endnummer 0:</li>"
                + "<ul>"
                + "<li>12310 (PC) - Packard Bell iMedia 2312</li>"
                + "<li>32310 (Notebook) - Aspire 7550-321</li>"
                + "<li>42620 (Aspire PC) - Aspire Predator G7200</li>"
                + "</ul>"
                + "<li>Endnummer 1:</li>"
                + "<ul>"
                + "<li>12311 (PC) - Packard Bell iMedia 2312</li>"
                + "<li>32311 (Notebook) - Aspire 7550-321</li>"
                + "<li>42621 (Aspire PC) - Aspire Predator G7200</li>"
                + "</ul><"
                + "</ul>"
                + "<hr />"
                + "</body>"
                + "</html>";
        return s;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JTextPane documentTextPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton printButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    // End of variables declaration//GEN-END:variables
}
