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
package eu.ggnet.dwoss.redtapext.ui;

import java.awt.Dialog;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import eu.ggnet.dwoss.redtapext.ee.UnitOverseer;
import eu.ggnet.dwoss.redtapext.ee.UniversalSearcher;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.util.*;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.Guardian;

public class UniversalSearchViewCask extends javax.swing.JFrame {

    private class WorkerSearch extends SwingWorker<Object, Tuple2<Integer, String>> {

        @Override
        protected Object doInBackground() throws Exception {
            if ( searchOperation == null ) {
                resultList.setListData(new String[]{"<h1>No Logic</h1>"});
                return null;
            }

            List<Tuple2<Long, String>> searchResult = new ArrayList<>();
            switch ((Type)searchType.getSelectedItem()) {
                case UNITS:
                    searchResult = searchOperation.searchUnits(searchArguments.getText().toUpperCase().trim());
                    break;
                case CUSTOMER:
                    searchResult = searchOperation.searchCustomers(searchArguments.getText().trim());
                    break;
                case DOSSIER:
                    searchResult = searchOperation.searchDossiers(searchArguments.getText().trim());
                    break;
                case INVOICE:
                    searchResult = searchOperation.searchDocuments(searchArguments.getText().trim(), DocumentType.INVOICE);
                    break;
                case CREDIT_MEMO:
                    searchResult = searchOperation.searchDocuments(searchArguments.getText().trim(), DocumentType.CREDIT_MEMO);
                    break;
                case ANNULATION_INVOICE:
                    searchResult = searchOperation.searchDocuments(searchArguments.getText().trim(), DocumentType.ANNULATION_INVOICE);
                    break;
            }

            DefaultListModel<Tuple2<Long, String>> model = new DefaultListModel<>();

            for (Tuple2<Long, String> tuple2 : searchResult) {
                model.addElement(tuple2);
            }
            if ( searchResult.isEmpty() ) {
                model.addElement(new Tuple2<>(-1l, "Keine Ergebnisse gefunden!"));
            }
            resultList.setModel(model);
            searchButton.setEnabled(true);

            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (CancellationException ex) {
                // Do nothing, normal cancel.
            } catch (ExecutionException | InterruptedException ex) {
                Ui.handle(ex);
            }
        }
    }

    public static enum Type implements INoteModel {

        CUSTOMER("Kunde"), DOSSIER("Vorgang"), INVOICE("Rechnung"),
        CREDIT_MEMO("Gutschrift"), ANNULATION_INVOICE("Stornorechnung"), UNITS("Gerät");

        private final String note;

        private Type(String note) {
            this.note = note;
        }

        @Override
        public String getNote() {
            return note;
        }
    }

    private static UniversalSearchViewCask instance;

    public static void showSingleInstance() {
        if ( instance == null ) {
            instance = new UniversalSearchViewCask();
            instance.setVisible(true);
        } else {
            // UiCore.getMainFrame().toBack();
            instance.toFront();
            if ( instance.getState() == JFrame.ICONIFIED ) instance.setState(JFrame.NORMAL);
        }
    }

    private final UniversalSearcher searchOperation;

    private final UnitOverseer unitOverseer;

    public UniversalSearchViewCask() {
        this(UiCore.getMainFrame(), Dl.remote().lookup(UniversalSearcher.class), Dl.remote().lookup(UnitOverseer.class));
    }

    public UniversalSearchViewCask(java.awt.Window parent, UniversalSearcher searchOperation, UnitOverseer unitOverseer) {
        this.searchOperation = searchOperation;
        this.unitOverseer = unitOverseer;
        initComponents();
        setLocationRelativeTo(parent);
        setIconImage(new ImageIcon(loadIcon()).getImage());
        if ( parent != null ) setLocationRelativeTo(parent);
        searchType.setModel(new DefaultComboBoxModel(Type.values()));
        searchType.setRenderer(new NamedEnumCellRenderer());
        resultList.setCellRenderer(new TupleHtmlRenderer());
    }

    static URL loadIcon() {
        return UniversalSearchViewCask.class.getResource("unisearch_icon.png");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        searchType = new javax.swing.JComboBox();
        searchArguments = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultList = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        searchButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Universelle Suche");
        setMinimumSize(new java.awt.Dimension(400, 350));
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setName("search"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jLabel1.setText("Suchen nach:");

        jLabel2.setText("Suchbegriffe:");

        searchType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        searchArguments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchArgumentsActionPerformed(evt);
            }
        });
        searchArguments.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchArgumentsKeyReleased(evt);
            }
        });

        resultList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                detailActionPerformed(evt);
            }
        });
        jScrollPane1.setViewportView(resultList);

        jLabel3.setText("Suchresultate");

        searchButton.setText("Suchen");
        searchButton.setEnabled(false);
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });

        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(searchType, 0, 147, Short.MAX_VALUE)
                            .addComponent(searchArguments, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(helpButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(6, 6, 6)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(searchType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(helpButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(searchArguments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        search();
    }//GEN-LAST:event_searchActionPerformed

    private void searchArgumentsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchArgumentsKeyReleased
        searchButton.setEnabled(!searchArguments.getText().isEmpty());
    }//GEN-LAST:event_searchArgumentsKeyReleased

    private void searchArgumentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchArgumentsActionPerformed
        search();
    }//GEN-LAST:event_searchArgumentsActionPerformed

    private void detailActionPerformed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_detailActionPerformed
        if ( evt.getClickCount() == 2 ) {
            String re = "";
            Tuple2<Long, String> value = (Tuple2<Long, String>)resultList.getSelectedValue();
            if ( value == null || value._1 == null || value._2 == null || value._1 == -1l ) return;

            switch ((Type)searchType.getSelectedItem()) {
                case UNITS:
                    re = unitOverseer.toDetailedHtml(value._1.toString(), Dl.local().lookup(Guardian.class).getUsername());
                    break;
                case CUSTOMER:
                    re = searchOperation.findCustomer(value._1.intValue());
                    break;
                case DOSSIER:
                    re = searchOperation.findDossier(value._1);
                    break;
                case INVOICE:
                case CREDIT_MEMO:
                case ANNULATION_INVOICE:
                    re = searchOperation.findDocument(value._1);
                    break;
            }
            HtmlDialog view = new HtmlDialog(this, Dialog.ModalityType.MODELESS);
            view.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
            view.setSize(600, 500);
            view.setText(re);
            view.setVisible(true);
        }
    }//GEN-LAST:event_detailActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        String helpGuide = "";
        helpGuide += "<h1>Einleitung zur effektiven Suche.</h1>";
        helpGuide += "<ol><li><b>Suchen von Kunden</b><ol style=\"disc\">"
                + "<li>Suchkriterien: Vor-,Nachname und Firma </li>"
                + "<li>\"Sternchensuche\" ist bei allen Kriterien fortlaufend aktiv</li>"
                + "</ol></li>"
                + "<li><b>Suchen von Aufträgen</b><ol style=disc>"
                + "<li>Suchkriterien: Auftrags ID und Rechnungsnummer</li>"
                + "<li>\"Sternchensuche\" nur für Rechnungsnummer aktiv</li>"
                + "<li>Zum suchen eines Auftrages bitte die gesamte ID eingeben</li>"
                + "</ol></li>"
                + "<li><b>Suchen von Geräten</b><ol style=\"disc\">"
                + "<li>Suchkriterien: SopoNr. und Seriennummer</li>"
                + "<li>\"Sternchensuche\" nur für Seriennummern aktiv</li>"
                + "<li>Für Suchen über die SopoNr. ist eine vollständige eingabe notwendig</li>"
                + "</ol></li>"
                + "<li><b>Suchen von Vorgängen</b><ol style=\"disc\">"
                + "<li>Suchkriterien: Vorgangsnummer</li>"
                + "<li>\"Sternchensuche\" aktiv</li>"
                + "</ol></li>"
                + "<li><b>Suchen von Rechnungen, Stornorechnungen u. Gutschriften</b><ol style=\"disc\">"
                + "<li>Suchkriterien: Rechnungs/Gutschriftsnummer <b>nur für neue/konvertierte Vorgänge</b></li>"
                + "<li>\"Sternchensuche\" aktiv</li>"
                + "</ol></li></ol>"
                + "Sollte es dennoch Fragen oder Anregungen geben dann gebt diese nach eigenem "
                + "ermessen telefonisch, per e-Mail oder als Ticket weiter.<br />Danke.";
        HtmlDialog helpView = new HtmlDialog(this, Dialog.ModalityType.MODELESS);
        helpView.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        helpView.setSize(600, 500);
        helpView.setText(helpGuide);
        helpView.setVisible(true);
    }//GEN-LAST:event_helpButtonActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        instance = null;
    }//GEN-LAST:event_formWindowClosed

    private void search() {
        DefaultListModel<Tuple2<Long, String>> emptyModel = new DefaultListModel<>();
        emptyModel.addElement(new Tuple2<>(-1l, "Es wird gesucht..."));
        resultList.setModel(emptyModel);
        new WorkerSearch().execute();
        searchButton.setEnabled(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton helpButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList resultList;
    private javax.swing.JTextField searchArguments;
    private javax.swing.JButton searchButton;
    private javax.swing.JComboBox searchType;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        UniversalSearchViewCask s = new UniversalSearchViewCask(null, null, null);
        s.setVisible(true);
    }
}
