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
package eu.ggnet.dwoss.redtapext.ui.cao.dossierTable;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.RowFilter.Entry;
import javax.swing.*;
import javax.swing.table.TableRowSorter;

import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.Warranty;
import eu.ggnet.dwoss.redtapext.ui.HtmlDialog;
import eu.ggnet.dwoss.core.widget.swing.TableColumnChooserPopup;
import eu.ggnet.dwoss.redtape.ee.api.LegacyRemoteBridge;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.format.DossierFormater;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

import static eu.ggnet.dwoss.redtapext.ui.cao.dossierTable.DossierTableView.FilterType.LEGACY;

/**
 * A JPanel for listings of Dossiers
 * <p>
 * @author pascal.perau
 */
public class DossierTableView extends javax.swing.JPanel {

    private static final Logger L = LoggerFactory.getLogger(DossierTableView.class);

    private static final FilterType INIT_FILTER = FilterType.ACCOUNTANCY_OPEN;

    public static enum FilterType {

        ALL("Alle Vorgänge"),
        SALES_OPEN("Verk. offen"),
        SALES_CLOSED("Verk. geschlossen"),
        ACCOUNTANCY_OPEN("Buchh. offen"),
        ACCOUNTANCY_CLOSED("Buchh. geschlossen"),
        @Deprecated // Nicht mehr in use
        LEGACY("Legacy");

        public final String description;

        private FilterType(String name) {
            this.description = name;
        }
        
    }

    private DossierTableModel model;

    private TableRowSorter<DossierTableModel> sorter;

    private DossierTableController controller;

    private FilterType type;

    private int customerId;

    private final JPopupMenu dossierPopup;

    private JPopupMenu colomnPopup;

    private final List<JRadioButton> filterButtonList;

    private final JPopupMenu filterPopup;

    private final ButtonGroup filterGroup;

    /** Creates new form DossierTableView */
    public DossierTableView() {
        initComponents();
        filterGroup = new ButtonGroup();
        filterPopup = new JPopupMenu();
        colomnPopup = new JPopupMenu();
        filterButtonList = new ArrayList<>();
        table.setRowHeight(25);
        progressBar.setStringPainted(true);
        dossierPopup = buildDossierPopup();
    }

    private JPopupMenu buildDossierPopup() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem detailsItem = new JMenuItem(new AbstractAction("Details") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dossier dos = selectedDossier();
                new HtmlDialog(SwingUtilities.getWindowAncestor(DossierTableView.this), Dialog.ModalityType.MODELESS)
                        .setText(Dl.remote().lookup(RedTapeWorker.class).toDetailedHtml(dos.getId())).setVisible(true);
            }
        });
        detailsItem.setText("Details");

        JMenuItem historyItem = new JMenuItem(new AbstractAction("Verlauf") {
            @Override
            public void actionPerformed(ActionEvent e) {
                HtmlDialog dialog = new HtmlDialog(SwingUtilities.getWindowAncestor(DossierTableView.this), ModalityType.MODELESS);
                dialog.setText(DossierFormater.toHtmlHistory(selectedDossier()));
                dialog.setVisible(true);
            }
        });
        historyItem.setText("Verlauf");

        JMenuItem warrantyChangeItem = new JMenuItem(new AbstractAction("Garantie ändern") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ui.build(DossierTableView.this).title("Garantieänderung").modality(Modality.WINDOW_MODAL).dialog().eval(() -> {

                    //prepare warranty combobox
                    ComboBox<Warranty> warrantyBox = new ComboBox<>(FXCollections.observableArrayList(Warranty.values()));
                    warrantyBox.setCellFactory((callback) -> {
                        return new ListCell<Warranty>() {
                            @Override
                            protected void updateItem(Warranty item, boolean empty) {
                                super.updateItem(item, empty);
                                if ( item == null || empty ) {
                                    setGraphic(null);
                                } else {
                                    setText(item.getName());
                                }
                            }
                        };
                    });

                    //prepare vbox to display components
                    VBox box = new VBox(5.,
                            new Label("Garantie für alle Geräte des Auftrages auf folgenden Wert ändern:"),
                            warrantyBox,
                            new Separator(Orientation.HORIZONTAL)
                    );

                    ButtonType addButtonType = new ButtonType("Durchführen", ButtonData.OK_DONE);
                    javafx.scene.control.Dialog<Dossier> dialog = new javafx.scene.control.Dialog<>();
                    dialog.getDialogPane().setContent(box);
                    dialog.getDialogPane().getButtonTypes().add(addButtonType);
                    dialog.setResultConverter(bType -> {
                        if ( bType == addButtonType ) {
                            if ( warrantyBox.getValue() != null ) {
                                return controller.updateDossierWarranty(selectedDossier(), warrantyBox.getValue());
                            }
                        }
                        Ui.build(DossierTableView.this).alert("Bitte Garantietyp wählen");
                        return null;
                    });
                    return dialog;
                }).cf().thenAcceptAsync((dos) -> {
                    L.debug("Finished warranty update, calling model/view updates");
                    model.update(selectedDossier(), dos);
                    controller.selectionChanged(null);
                    controller.selectionChanged(dos);
                }, EventQueue::invokeLater);
            }
        });
        warrantyChangeItem.setText("Garantie ändern");
        Dl.local().lookup(Guardian.class).add(warrantyChangeItem, AtomicRight.UPDATE_UNIQUE_UNIT);

        menu.add(detailsItem);
        menu.add(historyItem);
        menu.add(warrantyChangeItem);
        return menu;
    }

    private RowFilter setDependantRowFilter(final FilterType type) {
        RowFilter<DossierTableModel, Integer> filter = new RowFilter<DossierTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DossierTableModel, ? extends Integer> entry) {
                Dossier dos = entry.getModel().getDossier(entry.getIdentifier());
                switch (type) {
                    case SALES_CLOSED:
                        return  dos.getCrucialDirective() == Document.Directive.NONE;
                    case SALES_OPEN:
                        return  dos.getId() > 0 && dos.getCrucialDirective() != Document.Directive.NONE;
                    case ACCOUNTANCY_CLOSED:
                        return  dos.getId() > 0 && dos.isClosed();
                    case ACCOUNTANCY_OPEN:
                        return  dos.getId() > 0 && !dos.isClosed();
                    case ALL:
                        return true;
                    default:
                        return true;
                }
            }
        };
        return filter;
    }

    public DossierTableController getController() {
        return controller;
    }

    public void setController(DossierTableController controller) {
        this.controller = controller;
    }

    public DossierTableModel getModel() {
        return model;
    }

    public void setModel(DossierTableModel model) {
        this.model = model;
        table.setModel(model);
        sorter = new TableRowSorter<>();
        table.setRowSorter(sorter);
        sorter.setModel(model);
        table.setDefaultRenderer(Dossier.class, new DossierIconPanelRenderer());
        colomnPopup = new TableColumnChooserPopup(table);
        //initColumnCheckBoxes(table.getColumnModel().getColumns());
        initFilterButtons();
    }

    public JTable getTable() {
        return table;
    }

    private void initFilterButtons() {
        for (FilterType t : FilterType.values()) {
            final FilterType filterType = t;
            if ( filterType == LEGACY && !Dl.remote().contains(LegacyRemoteBridge.class) ) continue;
            JRadioButton button = new JRadioButton(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    type = filterType;
                    sorter.setRowFilter(setDependantRowFilter(filterType));
                    if ( type == INIT_FILTER ) {
                        // Do nothing
                    } else if ( type == LEGACY ) {
                        controller.loadLegacyDossiers(customerId);
                    } else {
                        controller.loadClosedDossiers(customerId);
                    }
                    filterPopup.setVisible(false);
                }
            });
            String label = filterType.description;
            button.setName(label);
            button.setText(label);
            filterGroup.add(button);
            filterPopup.add(button);
            filterButtonList.add(button);
        }
    }

    public void resetTableData(int customerId) {
        controller.resetLoader();
        if ( model.getRowCount() > 0 ) model.clear();
        for (JRadioButton filterButton : filterButtonList) {
            if ( filterButton.getName().equals(INIT_FILTER.description) ) {
                filterButton.setSelected(true);
                this.customerId = customerId;
                type = INIT_FILTER;
                sorter.setRowFilter(setDependantRowFilter(type));
                controller.loadOpenDossiers(customerId);
            }
        }
    }

    private Dossier selectedDossier() {
        return model.getDossier(table.convertRowIndexToModel(table.getSelectedRow()));
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
        table = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        tableFilterButton = new javax.swing.JButton();
        columnChooserButton = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        helpButton = new javax.swing.JButton();

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });
        table.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(table);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        tableFilterButton.setText("Filter");
        tableFilterButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tableFilterButton.setFocusable(false);
        tableFilterButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tableFilterButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tableFilterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tableFilterButtonMousePressed(evt);
            }
        });
        jToolBar1.add(tableFilterButton);

        columnChooserButton.setText("Tabelle Konfigurieren");
        columnChooserButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        columnChooserButton.setFocusable(false);
        columnChooserButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        columnChooserButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        columnChooserButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                columnChooserButtonMousePressed(evt);
            }
        });
        jToolBar1.add(columnChooserButton);

        progressBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        progressBar.setPreferredSize(new java.awt.Dimension(200, 20));
        progressBar.setString("---");
        jToolBar1.add(progressBar);

        helpButton.setText("Hilfe");
        helpButton.setFocusable(false);
        helpButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        helpButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(helpButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 731, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
        if ( model == null || controller == null ) return;
        int row = table.rowAtPoint(evt.getPoint());
        table.setRowSelectionInterval(row, row);
        Dossier selectedDos = selectedDossier();
        controller.selectionChanged(selectedDos);
        if ( evt.getClickCount() == 2 && table.getSelectedRow() != -1 && model != null ) {
            Dossier dos = selectedDossier();
            new HtmlDialog(SwingUtilities.getWindowAncestor(this), Dialog.ModalityType.MODELESS).setText(Dl.remote().lookup(RedTapeWorker.class).toDetailedHtml(dos.getId())).setVisible(true);
        }
        if ( SwingUtilities.isRightMouseButton(evt) ) {
            dossierPopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tableMouseClicked

    private void columnChooserButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_columnChooserButtonMousePressed
        colomnPopup.show(evt.getComponent(), evt.getComponent().getBounds().x, evt.getComponent().getBounds().height);
    }//GEN-LAST:event_columnChooserButtonMousePressed

    private void tableFilterButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableFilterButtonMousePressed
        filterPopup.show(evt.getComponent(), evt.getComponent().getBounds().x, evt.getComponent().getBounds().height);
    }//GEN-LAST:event_tableFilterButtonMousePressed

    private void tableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableKeyReleased
        if ( evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN ) {
            Dossier selectedDos = model.getDossier(table.convertRowIndexToModel(table.getSelectedRow()));
            controller.selectionChanged(selectedDos);
        }
    }//GEN-LAST:event_tableKeyReleased

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        if ( controller == null ) return;
        HtmlDialog dialog = new HtmlDialog();
        dialog.setText(controller.generateHelp());
        dialog.setVisible(true);
    }//GEN-LAST:event_helpButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton columnChooserButton;
    private javax.swing.JButton helpButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    public javax.swing.JProgressBar progressBar;
    private javax.swing.JTable table;
    private javax.swing.JButton tableFilterButton;
    // End of variables declaration//GEN-END:variables
}
