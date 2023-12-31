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
package eu.ggnet.dwoss.redtapext.ui.cao;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import jakarta.inject.Inject;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jakarta.annotation.PostConstruct;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.HtmlPane;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.customer.api.*;
import eu.ggnet.dwoss.customer.spi.CustomerUiModifier;
import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtapext.ee.state.*;
import eu.ggnet.dwoss.redtapext.ui.cao.common.DocumentStringRenderer;
import eu.ggnet.dwoss.redtapext.ui.cao.common.UiCustomerRenderer;
import eu.ggnet.dwoss.redtapext.ui.cao.dossierTable.DossierTableView;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.saft.core.ui.Bind.Type.ICONS;

/**
 * The main UI for using RedTape components.
 * <p>
 * @author pascal.perau
 */
@eu.ggnet.saft.core.ui.Frame
@Title("Kunden und Aufträge")
@StoreLocation
@Dependent
public class RedTapeView extends JPanel {

    public final static String ONCE_KEY = "RedTape";

    private final Logger L = LoggerFactory.getLogger(RedTapeView.class);

    private final PropertyChangeListener redTapeViewListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            final RedTapeModel m = (RedTapeModel)evt.getSource();
            switch (evt.getPropertyName()) {
                case RedTapeModel.PROP_CUSTOMER:
                    customerDetailArea.setText(remote.lookup(CustomerService.class).asHtmlHighDetailed(m.getPurchaseCustomer().id()));
                    controller.fillToolBar();
                    break;
                case RedTapeModel.PROP_DOCUMENTS:
                    documentList.clearSelection();
                    Collections.sort(m.getDocuments());
                    documentList.setListData(m.getDocuments().toArray());
                    break;
                case RedTapeModel.PROP_POSITIONS:
                    Platform.runLater(() -> {
                        positions.clear(); // This could also be done with javaFX style (use the list of the model)
                        positions.addAll(m.getPositions());
                    });
                    break;
                case RedTapeModel.PROP_SEARCH_RESULT:
                    searchResultList.setListData(m.getSearchResult().toArray());
                    break;
            }
        }
    };

    @Inject
    private RemoteDl remote;

    @Inject
    private RedTapeModel model;

    @Inject
    private RedTapeController controller;

    @Inject
    private DossierTableView dossierTableView;

    private final JPopupMenu documentPopup;

    private final ObservableList<Position> positions = FXCollections.observableArrayList();

    @Bind(ICONS)
    private final ObservableList<javafx.scene.image.Image> icons = FXCollections.observableArrayList();

    /** Creates new form RedTapeBasic */
    public RedTapeView() {
        initComponents();

        initFxComponents();

        documentList.setModel(new DefaultListModel());
        documentList.setCellRenderer(new DocumentStringRenderer());
        searchResultList.setModel(new DefaultListModel());
        searchResultList.setCellRenderer(new UiCustomerRenderer());

        customerDetailArea.setComponentPopupMenu(buildCustomerPopup());
        dossierButtonPanel.setComponentPopupMenu(builtStateInfoPopup());
        documentPopup = new JPopupMenu();

        newCustomerButton.setEnabled(Dl.local().lookup(CustomerUiModifier.class) != null);
    }

    @PostConstruct
    private void initCdi() {
        Mandators mandators = Dl.local().lookup(CachedMandators.class);
        FileJacket caoIcon = mandators.loadCaoIcon();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(caoIcon.getContent())) {
            icons.add(new javafx.scene.image.Image(bais));
        } catch (IOException e) {
            L.warn("Loading of Cao Icon from Mandator not sucessful");
        }

        dossierTableViewPanel.add(dossierTableView, BorderLayout.CENTER);

        // Init Controller
        dossierTableView.setController(controller.getDossierTableController());
        controller.getDossierTableController().setView(dossierTableView);

        // Init Model
        dossierTableView.setModel(model.getDossierTableModel());
        model.addPropertyChangeListener(redTapeViewListener);

        controller.setModel(model);
        controller.setView(this);
    }

    private void initFxComponents() {
        final JFXPanel jfxp = new JFXPanel();
        positionFxPanel.add(jfxp, BorderLayout.CENTER);
        Platform.runLater(() -> RedTapeFxUtil.positionFxList(jfxp, positions));
    }

    /**
     * Get the model from the view.
     * <p/>
     * @return The recent model of the view
     */
    public RedTapeModel getModel() {
        return model;
    }

    public void setStateActions(List<Action> actions) {
        dossierButtonPanel.removeAll();
        for (Action action : actions) {
            JButton b = new JButton(action);
            dossierButtonPanel.add(b);
        }
        dossierButtonPanel.revalidate();
        dossierButtonPanel.repaint();
    }

    public void setDocumentPopupActions(Action... action) {
        documentPopup.removeAll();
        JMenuItem item;
        for (Action action1 : action) {
            item = new JMenuItem(action1);
            item.setName(action1.getValue(Action.NAME).toString());
            documentPopup.add(item);
        }
    }

    private JPopupMenu builtStateInfoPopup() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem item = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerMetaData customer = model.getPurchaseCustomer();
                if ( model.getSelectedDocument() != null && customer != null ) {
                    CustomerDocument cdoc = new CustomerDocument(
                            customer.flags(),
                            model.getSelectedDocument(),
                            customer.shippingCondition(),
                            customer.paymentMethod());
                    RedTapeStateCharacteristic sc = (RedTapeStateCharacteristic)new RedTapeStateCharacteristicFactory().characterize(cdoc);
                    Ui.exec(() -> {
                        Ui.build().parent(jLabel1).title("StageInfo").fx()
                                .show(() -> {
                                    return "<html>" + (sc.isDispatch() ? "DISPATCH - " : "PICKUP - ") + "<b>" + sc.getType() + "</b><br />"
                                            + "PaymentMethod - " + sc.getPaymentMethod() + "<br />Directive - " + sc.getDirective() + (sc.getConditions().isEmpty() ? "" : "<br />Conditions:<br />" + sc.getConditions())
                                            + (sc.getCustomerFlags().isEmpty() ? "" : "<br />Flags:<br />" + sc.getCustomerFlags()) + "<br /></html>";
                                }, () -> new HtmlPane());
                    });
                }
            }
        });
        item.setText("State Info");
        menu.add(item);
        return menu;
    }

    private JPopupMenu buildCustomerPopup() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem newCustomerItem = new JMenuItem("Neu");
        newCustomerItem.addActionListener(e -> {
            if ( controller != null ) controller.openCreateCustomer();
        });

        JMenuItem editEditItem = new JMenuItem("Bearbeiten");
        editEditItem.addActionListener(e -> {
            if ( model.getPurchaseCustomer() != null ) {
                controller.openUpdateCustomer(model.getPurchaseCustomer().id());
                customerDetailArea.setText(remote.lookup(CustomerService.class).asHtmlHighDetailed(model.getPurchaseCustomer().id()));
            }
        });

        menu.add(newCustomerItem);
        menu.add(editEditItem);
        return menu;
    }

    static URL loadImage() {
        return RedTapeView.class.getResource("RedTapeIcon.png");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        jLabel1 = new JLabel();
        jScrollPane1 = new JScrollPane();
        searchResultList = new JList();
        searchCommandField = new JTextField();
        positionPanel = new JPanel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        priceSumLabel = new JLabel();
        afterTaxSumLabel = new JLabel();
        jLabel4 = new JLabel();
        positionAmountLabel = new JLabel();
        positionFxPanel = new JPanel();
        documentCreationPanel = new JPanel();
        jScrollPane5 = new JScrollPane();
        documentList = new JList();
        dossierTableViewPanel = new JPanel();
        jScrollPane8 = new JScrollPane();
        customerDetailArea = new JEditorPane();
        newCustomerButton = new JButton();
        dossierButtonPanel = new JPanel();
        jPanel2 = new JPanel();
        jScrollPane4 = new JScrollPane();
        dossierCommentArea = new JTextArea();
        editCommentButton = new JButton();
        actionBar = new JToolBar();

        setLayout(new GridBagLayout());

        jLabel1.setFont(new Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel1.setText("Kundensuche");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        add(jLabel1, gridBagConstraints);

        jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setToolTipText("");
        jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        searchResultList.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, new Color(204, 204, 255), Color.black));
        searchResultList.setModel(new AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        searchResultList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                searchResultSelectionChanged(evt);
            }
        });
        jScrollPane1.setViewportView(searchResultList);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 10.0;
        gridBagConstraints.insets = new Insets(45, 0, 0, 0);
        add(jScrollPane1, gridBagConstraints);

        searchCommandField.setName(""); // NOI18N
        searchCommandField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchCommandFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        add(searchCommandField, gridBagConstraints);

        positionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, new Color(204, 204, 255), Color.black), "Positionen"));
        positionPanel.setMaximumSize(new Dimension(400, 32767));
        positionPanel.setMinimumSize(new Dimension(400, 33));
        positionPanel.setPreferredSize(new Dimension(400, 33));

        jLabel2.setText("Nettosumme:");

        jLabel3.setText("Bruttosumme:");

        priceSumLabel.setPreferredSize(new Dimension(0, 15));

        afterTaxSumLabel.setPreferredSize(new Dimension(0, 15));

        jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel4.setText("Positionen");

        positionAmountLabel.setHorizontalAlignment(SwingConstants.CENTER);

        positionFxPanel.setLayout(new BorderLayout());

        GroupLayout positionPanelLayout = new GroupLayout(positionPanel);
        positionPanel.setLayout(positionPanelLayout);
        positionPanelLayout.setHorizontalGroup(positionPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, positionPanelLayout.createSequentialGroup()
                .addGroup(positionPanelLayout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(positionPanelLayout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(priceSumLabel, GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                    .addComponent(afterTaxSumLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(positionPanelLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(positionAmountLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(positionFxPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        positionPanelLayout.setVerticalGroup(positionPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, positionPanelLayout.createSequentialGroup()
                .addComponent(positionFxPanel, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(positionPanelLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(priceSumLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(positionPanelLayout.createParallelGroup(Alignment.LEADING)
                    .addGroup(positionPanelLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(afterTaxSumLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(positionAmountLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 5.0;
        add(positionPanel, gridBagConstraints);

        documentCreationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, new Color(204, 204, 255), Color.black), "Dokumente"));
        documentCreationPanel.setMaximumSize(new Dimension(400, 2147483647));
        documentCreationPanel.setMinimumSize(new Dimension(400, 14));
        documentCreationPanel.setName(""); // NOI18N
        documentCreationPanel.setPreferredSize(new Dimension(400, 100));
        documentCreationPanel.setLayout(new GridBagLayout());

        jScrollPane5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        documentList.setModel(new AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        documentList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                editDocumentMouseActionPerformed(evt);
            }
        });
        documentList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                documentListSelectionChanged(evt);
            }
        });
        jScrollPane5.setViewportView(documentList);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        documentCreationPanel.add(jScrollPane5, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 5.0;
        add(documentCreationPanel, gridBagConstraints);

        dossierTableViewPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, new Color(204, 204, 255), Color.black));
        dossierTableViewPanel.setLayout(new BorderLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 13;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 10.0;
        gridBagConstraints.weighty = 8.0;
        gridBagConstraints.insets = new Insets(7, 0, 2, 0);
        add(dossierTableViewPanel, gridBagConstraints);

        customerDetailArea.setEditable(false);
        customerDetailArea.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, new Color(204, 204, 255), Color.black));
        customerDetailArea.setContentType("text/html"); // NOI18N
        jScrollPane8.setViewportView(customerDetailArea);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 10.0;
        gridBagConstraints.weighty = 10.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        add(jScrollPane8, gridBagConstraints);

        newCustomerButton.setText("Neuer Kunde");
        newCustomerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                newCustomerButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        add(newCustomerButton, gridBagConstraints);

        dossierButtonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, new Color(204, 204, 255), Color.black), "Auftrag fortführen"));
        dossierButtonPanel.setMaximumSize(new Dimension(400, 32767));
        dossierButtonPanel.setMinimumSize(new Dimension(400, 33));
        dossierButtonPanel.setPreferredSize(new Dimension(400, 33));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 3.0;
        add(dossierButtonPanel, gridBagConstraints);

        jPanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, new Color(204, 204, 255), Color.black)));
        jPanel2.setMaximumSize(new Dimension(300, 32767));

        jScrollPane4.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        dossierCommentArea.setEditable(false);
        dossierCommentArea.setColumns(20);
        dossierCommentArea.setLineWrap(true);
        dossierCommentArea.setRows(5);
        dossierCommentArea.setWrapStyleWord(true);
        dossierCommentArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(""), "Bemerkungen"));
        jScrollPane4.setViewportView(dossierCommentArea);

        editCommentButton.setText("bearbeiten");
        editCommentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editCommentButtonActionPerformed(evt);
            }
        });

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jScrollPane4, GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
            .addGroup(Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 238, Short.MAX_VALUE)
                .addComponent(editCommentButton, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane4, GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(editCommentButton))
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 13;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(7, 0, 2, 0);
        add(jPanel2, gridBagConstraints);

        actionBar.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        actionBar.setFloatable(false);
        actionBar.setRollover(true);
        actionBar.setMinimumSize(new Dimension(4, 30));
        actionBar.setPreferredSize(new Dimension(4, 30));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        add(actionBar, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void documentListSelectionChanged(ListSelectionEvent evt) {//GEN-FIRST:event_documentListSelectionChanged
        if ( model == null ) return;
        if ( ((JList)evt.getSource()).getSelectedValue() == null ) return;
        model.setSelectedDocument((Document)((JList)evt.getSource()).getSelectedValue());
    }//GEN-LAST:event_documentListSelectionChanged

    private void searchResultSelectionChanged(ListSelectionEvent evt) {//GEN-FIRST:event_searchResultSelectionChanged
        if ( model == null ) return;
        if ( evt.getValueIsAdjusting() || searchResultList.getSelectedIndex() == -1 ) return;
        model.setSelectedSearchResult(((UiCustomer)searchResultList.getSelectedValue()).id());
    }//GEN-LAST:event_searchResultSelectionChanged

    private void newCustomerButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_newCustomerButtonActionPerformed
        controller.openCreateCustomer();
    }//GEN-LAST:event_newCustomerButtonActionPerformed

    private void searchCommandFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchCommandFieldActionPerformed
        if ( model == null ) return;
        model.setSearch(searchCommandField.getText());
    }//GEN-LAST:event_searchCommandFieldActionPerformed

    private void editDocumentMouseActionPerformed(MouseEvent evt) {//GEN-FIRST:event_editDocumentMouseActionPerformed
        if ( controller == null ) return;
        if ( evt.getClickCount() == 2 && documentList.getModel().getSize() != 0 && SwingUtilities.isLeftMouseButton(evt) ) {
            controller.openDocumentViewer(model.getSelectedDocument());
        }
        if ( documentList.getModel().getSize() != 0 && SwingUtilities.isRightMouseButton(evt) ) {
            int index = documentList.locationToIndex(evt.getPoint());
            documentList.setSelectedIndex(index);
            documentPopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_editDocumentMouseActionPerformed

    private void editCommentButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_editCommentButtonActionPerformed
        if ( controller == null ) return;
        controller.openEditComment(model.getSelectedDossier());
    }//GEN-LAST:event_editCommentButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    JToolBar actionBar;
    JLabel afterTaxSumLabel;
    JEditorPane customerDetailArea;
    JPanel documentCreationPanel;
    JList documentList;
    JPanel dossierButtonPanel;
    JTextArea dossierCommentArea;
    JPanel dossierTableViewPanel;
    JButton editCommentButton;
    JLabel jLabel1;
    JLabel jLabel2;
    JLabel jLabel3;
    JLabel jLabel4;
    JPanel jPanel2;
    JScrollPane jScrollPane1;
    JScrollPane jScrollPane4;
    JScrollPane jScrollPane5;
    JScrollPane jScrollPane8;
    JButton newCustomerButton;
    JLabel positionAmountLabel;
    JPanel positionFxPanel;
    JPanel positionPanel;
    JLabel priceSumLabel;
    JTextField searchCommandField;
    JList searchResultList;
    // End of variables declaration//GEN-END:variables

}
