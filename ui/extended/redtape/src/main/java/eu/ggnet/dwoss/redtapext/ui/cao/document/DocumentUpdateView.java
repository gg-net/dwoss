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
package eu.ggnet.dwoss.redtapext.ui.cao.document;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URL;
import java.util.EnumSet;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.text.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.api.service.ShippingCostService;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.redtape.ee.api.WarrantyHook;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtapext.ui.cao.common.PositionListCell;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.util.*;
import eu.ggnet.dwoss.util.validation.ValidationUtil;
import eu.ggnet.saft.*;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.swing.VetoableOnOk;
import eu.ggnet.saft.core.ui.UiAlertBuilder.Type;

import lombok.Getter;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CHANGE_TAX;
import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_PRICE_INVOICES;
import static eu.ggnet.saft.Client.lookup;

import eu.ggnet.dwoss.mandator.Mandators;

/**
 *
 * @author pascal.perau
 */
public class DocumentUpdateView extends javax.swing.JPanel implements IPreClose, Consumer<Void>, VetoableOnOk, ResultProducer<Document> {

    static URL loadPlus() {
        return DocumentUpdateView.class.getResource("plus.png");
    }

    static URL loadMinus() {
        return DocumentUpdateView.class.getResource("minus.png");
    }

    static URL loadUp() {
        return DocumentUpdateView.class.getResource("up.png");
    }

    static URL loadDown() {
        return DocumentUpdateView.class.getResource("down.png");
    }

    static URL loadAddProductBatch() {
        return DocumentUpdateView.class.getResource("addProductBatch.png");
    }

    static URL loadAddCoin() {
        return DocumentUpdateView.class.getResource("add-coin-icon.png");
    }

    private final static Logger L = LoggerFactory.getLogger(DocumentUpdateView.class);

    @Getter
    private long customerId;

    private DocumentUpdateController controller;

    private final Document document;

    private final Guardian accessCos;

    private final ObservableList<Position> positions = FXCollections.observableArrayList();

    private ListView<Position> positionsFxList;

    public DocumentUpdateView(Document document) {
        initComponents();
        addUnitButton.setIcon(new ImageIcon(loadPlus()));
        addProductBatchButton.setIcon(new ImageIcon(loadAddProductBatch()));
        moveUpButton.setIcon(new ImageIcon(loadUp()));
        moveDownButton.setIcon(new ImageIcon(loadDown()));
        removePositionButton.setIcon(new ImageIcon(loadMinus()));
        convertToWarrantyPositionButton.setIcon(new ImageIcon(loadAddCoin()));

        initFxComponents();
        this.document = document;
        positions.addAll(document.getPositions().values());
        this.accessCos = Client.lookup(Guardian.class);
        accessCos.add(taxChangeButton, CHANGE_TAX);
        refreshAddressArea();

        if ( !Client.hasFound(WarrantyHook.class) ) convertToWarrantyPositionButton.setVisible(false);

        if ( document.isClosed() || EnumSet.of(DocumentType.COMPLAINT, DocumentType.CREDIT_MEMO, DocumentType.ANNULATION_INVOICE).contains(document.getType()) ) {
            disableComponents(addProductBatchButton, addUnitButton, unitInputField, addServiceButton, shippingCostButton);
        } else if ( document.getType() == DocumentType.INVOICE ) {
            accessCos.add(addProductBatchButton, UPDATE_PRICE_INVOICES);
            accessCos.add(addUnitButton, UPDATE_PRICE_INVOICES);
            accessCos.add(unitInputField, UPDATE_PRICE_INVOICES);
            accessCos.add(addServiceButton, UPDATE_PRICE_INVOICES);
            accessCos.add(shippingCostButton, UPDATE_PRICE_INVOICES);
            accessCos.add(moveDownButton, UPDATE_PRICE_INVOICES);
            accessCos.add(moveUpButton, UPDATE_PRICE_INVOICES);
        } else if ( document.getType() == DocumentType.RETURNS || document.getType() == DocumentType.CAPITAL_ASSET )
            disableComponents(addProductBatchButton, addServiceButton, shippingCostButton);
    }

    private void initFxComponents() {
        final JFXPanel jfxp = new JFXPanel();
        positionPanelFx.add(jfxp, BorderLayout.CENTER);

        Platform.runLater(() -> {
            BorderPane pane = new BorderPane();
            Scene scene = new Scene(pane, Color.ALICEBLUE);

            positionsFxList = new ListView<>();
            positionsFxList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            positionsFxList.setCellFactory(new PositionListCell.Factory());
            positionsFxList.setItems(positions);
            positionsFxList.setOnMouseClicked((mouseEvent) -> {
                if ( mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2 ) {
                    if ( isChangeAllowed() ) {
                        Ui.exec(() -> {
                            controller.editPosition(positionsFxList.getSelectionModel().getSelectedItem());
                            Platform.runLater(() -> positionsFxList.refresh());
                        });
                    } else {
                        UiAlert.show("Änderung an Positionen ist nicht erlaubt.");
                    }
                }
            });

            pane.setCenter(positionsFxList);
            jfxp.setScene(scene);
        });
    }

    public void setController(DocumentUpdateController controller) {
        this.controller = controller;
    }

    /**
     * Display customer values in UI.
     * <p>
     * @param customerId id of the current customer
     */
    public void setCustomerValues(long customerId) {
        this.customerId = customerId;
        String labelText = lookup(CustomerService.class).asUiCustomer(customerId).getSimpleHtml();
        recentCustomerLabel.setText("<html><div align=\"center\" width=\"120px\"><i>" + labelText + "</i></div></html>");
        recentCustomerLabel.setToolTipText("<html>" + labelText + "</html>");
        paymentMethodLabel.setText(paymentMethodLabel.getText() + " " + lookup(CustomerService.class).asCustomerMetaData(customerId).getPaymentMethod().getNote());
    }

    public Document getDocument() {
        return document;
    }

    /**
     * Refreshes the Areas contant if changes occur.
     */
    public final void refreshAddressArea() {
        addressesArea.setText("");
        StyledDocument doc = addressesArea.getStyledDocument();
        Style boldStyle = addressesArea.addStyle("bold", null);
        StyleConstants.setBold(boldStyle, true);
        try {
            if ( document.getInvoiceAddress().getDescription().equals(document.getShippingAddress().getDescription()) ) {
                doc.insertString(doc.getLength(), "Rechnungs und Lieferadresse:\n", boldStyle);
                doc.insertString(doc.getLength(), document.getInvoiceAddress().getDescription(), null);
            } else {
                doc.insertString(doc.getLength(), "Rechnungsadresse:\n", boldStyle);
                doc.insertString(doc.getLength(), document.getInvoiceAddress().getDescription(), null);
                doc.insertString(doc.getLength(), "\n\nLieferadresse:\n", boldStyle);
                doc.insertString(doc.getLength(), document.getShippingAddress().getDescription(), null);
            }
        } catch (BadLocationException ex) {
            addressesArea.setText("Rechnungsadresse:\n"
                    + document.getInvoiceAddress().getDescription()
                    + "\n\nLieferAdresse:\n"
                    + document.getShippingAddress().getDescription());
        }
    }

    /**
     * Cause the Position does not have any Bindable parameters, we use a workaround.
     * <p>
     * @param p the position to refresh
     */
    public void refresh(Position p) {
        int index = positions.indexOf(p);
        int selectedIndex = positionsFxList.getSelectionModel().getSelectedIndex();
        positions.remove(p);
        positions.add(index, p);
        positionsFxList.getSelectionModel().select(selectedIndex);
    }

    @Override
    public boolean pre(CloseType type) {
        if ( type == CloseType.CANCEL ) return true;
        if ( controller == null ) return true;
        if ( customerId == 0 ) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Bitte Kunden wählen");
            return false;
        }
        if ( document.getPositions().isEmpty() ) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Ein Dokument muss mindestens eine Position enthalten.");
            return false;
        }

        if ( document.getPositions(PositionType.SHIPPING_COST).size() > 1 ) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Es sind mehr als eine Position des Types Versandkosten enthalten.");
            return false;
        }
        if ( !ValidationUtil.isValidOrShow(SwingUtilities.getWindowAncestor(this), document) ) return false;
        if ( accessCos != null ) {
            for (Component component : this.getComponents()) {
                accessCos.remove(component);
            }
        }
        if ( Client.hasFound(ShippingCostService.class) ) return controller.optionalRecalcShippingCost();
        else return true;
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

        jLabel1 = new javax.swing.JLabel();
        unitInputField = new javax.swing.JTextField();
        addUnitButton = new javax.swing.JButton();
        recentCustomerLabel = new javax.swing.JLabel();
        addProductBatchButton = new javax.swing.JButton();
        addServiceButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        removePositionButton = new javax.swing.JButton();
        addCommentButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        shippingCostButton = new javax.swing.JButton();
        paymentMethodLabel = new javax.swing.JLabel();
        editInvoiceAddressButton = new javax.swing.JButton();
        editShippingAddress = new javax.swing.JButton();
        resetAddressesButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        addressesArea = new javax.swing.JTextPane();
        jSeparator3 = new javax.swing.JSeparator();
        taxChangeButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        positionPanelFx = new javax.swing.JPanel();
        convertToWarrantyPositionButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(800, 600));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Aktueller Kunde:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel1, gridBagConstraints);

        unitInputField.setToolTipText("<html>SopoNr. eingeben<br />-Enter für schnelle eingabe verwenden</html>");
        unitInputField.setMinimumSize(new java.awt.Dimension(100, 25));
        unitInputField.setPreferredSize(new java.awt.Dimension(100, 25));
        unitInputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUnitAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        add(unitInputField, gridBagConstraints);

        addUnitButton.setToolTipText("Sopo Gerät hinzufügen");
        addUnitButton.setMaximumSize(new java.awt.Dimension(2147483647, 46));
        addUnitButton.setMinimumSize(new java.awt.Dimension(40, 40));
        addUnitButton.setPreferredSize(new java.awt.Dimension(40, 40));
        addUnitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUnitAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        add(addUnitButton, gridBagConstraints);

        recentCustomerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        recentCustomerLabel.setText("Derzeit kein Kunde gewählt.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(recentCustomerLabel, gridBagConstraints);

        addProductBatchButton.setText("<html>Neuware<br>hinzufügen</html>");
        addProductBatchButton.setMaximumSize(new java.awt.Dimension(120, 45));
        addProductBatchButton.setMinimumSize(new java.awt.Dimension(120, 45));
        addProductBatchButton.setPreferredSize(new java.awt.Dimension(120, 45));
        addProductBatchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNonUnitPosition(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(addProductBatchButton, gridBagConstraints);

        addServiceButton.setText("<html>Dienstleistung<br />Kleinteil, Gebühr");
        addServiceButton.setRolloverEnabled(false);
        addServiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNonUnitPosition(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(addServiceButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 5, 0);
        add(jSeparator1, gridBagConstraints);

        moveUpButton.setToolTipText("Position hoch schieben");
        moveUpButton.setMaximumSize(new java.awt.Dimension(40, 28));
        moveUpButton.setMinimumSize(new java.awt.Dimension(40, 28));
        moveUpButton.setPreferredSize(new java.awt.Dimension(40, 28));
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePositionOrderAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        add(moveUpButton, gridBagConstraints);

        moveDownButton.setToolTipText("Position runter schieben");
        moveDownButton.setMaximumSize(new java.awt.Dimension(40, 28));
        moveDownButton.setMinimumSize(new java.awt.Dimension(40, 28));
        moveDownButton.setPreferredSize(new java.awt.Dimension(40, 28));
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePositionOrderAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        add(moveDownButton, gridBagConstraints);

        removePositionButton.setToolTipText("Position entfernen");
        removePositionButton.setMaximumSize(new java.awt.Dimension(40, 40));
        removePositionButton.setMinimumSize(new java.awt.Dimension(40, 40));
        removePositionButton.setPreferredSize(new java.awt.Dimension(40, 40));
        removePositionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePositionAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(removePositionButton, gridBagConstraints);

        addCommentButton.setText("Kommentar");
        addCommentButton.setPreferredSize(new java.awt.Dimension(99, 42));
        addCommentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNonUnitPosition(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(addCommentButton, gridBagConstraints);

        jLabel3.setText("SopoNr:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel3, gridBagConstraints);

        shippingCostButton.setText("Versandkosten");
        shippingCostButton.setPreferredSize(new java.awt.Dimension(123, 42));
        shippingCostButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNonUnitPosition(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(shippingCostButton, gridBagConstraints);

        paymentMethodLabel.setText("Zahlungsmodalität:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(paymentMethodLabel, gridBagConstraints);

        editInvoiceAddressButton.setText("<html>Rechnungsadresse<br />bearbeiten</html>");
        editInvoiceAddressButton.setToolTipText("");
        editInvoiceAddressButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editInvoiceAddressButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editInvoiceAddressButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(editInvoiceAddressButton, gridBagConstraints);

        editShippingAddress.setText("<html>Lieferungsadresse<br />bearbeiten</html>");
        editShippingAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editShippingAddressActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(editShippingAddress, gridBagConstraints);

        resetAddressesButton.setText("Adressen zurücksetzen");
        resetAddressesButton.setPreferredSize(new java.awt.Dimension(180, 42));
        resetAddressesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetAddressesButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(resetAddressesButton, gridBagConstraints);

        addressesArea.setEditable(false);
        addressesArea.setPreferredSize(new java.awt.Dimension(12, 80));
        jScrollPane3.setViewportView(addressesArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        add(jScrollPane3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jSeparator3, gridBagConstraints);

        taxChangeButton.setText("<html>Steuern und Fibukonten<br />anpassen</html>");
        taxChangeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        taxChangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                taxChangeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(taxChangeButton, gridBagConstraints);

        jSeparator2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 5, 0);
        add(jSeparator2, gridBagConstraints);

        positionPanelFx.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        add(positionPanelFx, gridBagConstraints);

        convertToWarrantyPositionButton.setToolTipText("Garantie manuell erweitern");
        convertToWarrantyPositionButton.setMaximumSize(new java.awt.Dimension(40, 40));
        convertToWarrantyPositionButton.setMinimumSize(new java.awt.Dimension(40, 40));
        convertToWarrantyPositionButton.setPreferredSize(new java.awt.Dimension(40, 40));
        convertToWarrantyPositionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                convertToWarrantyPositionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(convertToWarrantyPositionButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void changePositionOrderAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePositionOrderAction
        final MultipleSelectionModel<Position> selection = positionsFxList.getSelectionModel();
        final int index = selection.getSelectedIndex();
        Position selectedItem = selection.getSelectedItem();
        if ( index < 0 ) return;
        if ( evt.getSource() == moveUpButton ) {
            if ( index == 0 ) return; // Don't move at the beginning
            document.moveUp(selectedItem);
            Platform.runLater(() -> {
                Position removed = positions.remove(index);
                positions.add(index - 1, removed);
                selection.select(index - 1);
            });
        } else {
            if ( index == positions.size() - 1 ) return; // Don't move at the end
            document.moveDown(selectedItem);
            Platform.runLater(() -> {
                Position removed = positions.remove(index);
                positions.add(index + 1, removed);
                selection.select(index + 1);
            });
        }
    }//GEN-LAST:event_changePositionOrderAction

    private void addUnitAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUnitAction
        if ( unitInputField.getText().isEmpty() ) return;
        if ( document.isClosed() ) {
            UiAlert.show(this, "Nicht erlaubt", "Hinzufügen von Sopo Ware nicht erlaubt, abgeschlossenes Dokument", Type.INFO);
            return;
        }

        for (String sopo : unitInputField.getText().trim().split("(\\s*,\\s*|\\s+)")) {
            if ( StringUtils.isBlank(sopo) ) continue;
            try {
                controller.addPosition(document.getDossier().getId(), PositionType.UNIT, sopo, false);
            } catch (Exception ex) {
                Ui.handle(ex);
            }
        }
        refreshAll();
        unitInputField.setText("");
    }//GEN-LAST:event_addUnitAction

    private void addNonUnitPosition(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNonUnitPosition
        Ui.exec(() -> {
            PositionType type;
            if ( ((JButton)evt.getSource()) == addProductBatchButton ) {
                type = PositionType.PRODUCT_BATCH;
            } else if ( ((JButton)evt.getSource()) == addServiceButton ) {
                type = PositionType.SERVICE;
            } else if ( ((JButton)evt.getSource()) == addCommentButton ) {
                type = PositionType.COMMENT;
            } else {
                type = PositionType.SHIPPING_COST;
            }
            try {
                controller.addPosition(document.getDossier().getId(), type, null, false);
                refreshAll();
            } catch (UserInfoException ex) {
                Ui.handle(ex);
            }
        });

    }//GEN-LAST:event_addNonUnitPosition

    private void removePositionAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePositionAction
        final MultipleSelectionModel<Position> selection = positionsFxList.getSelectionModel();
        final int index = selection.getSelectedIndex();
        Position selectedItem = selection.getSelectedItem();
        if ( index == -1 ) return;
        if ( isChangeAllowed() ) {
            document.removeAt(selectedItem.getId());
            refreshAll();
        } else {
            JOptionPane.showMessageDialog(this, "Änderungen am Dokument sind nicht erlaubt.");
        }
    }//GEN-LAST:event_removePositionAction

    private void editInvoiceAddressButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editInvoiceAddressButtonActionPerformed
        if ( controller == null ) return;
        controller.editDocumentInvoiceAddress();
    }//GEN-LAST:event_editInvoiceAddressButtonActionPerformed

    private void editShippingAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editShippingAddressActionPerformed
        if ( controller == null ) return;
        controller.editDocumentShippingAddress();
    }//GEN-LAST:event_editShippingAddressActionPerformed

    private void resetAddressesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetAddressesButtonActionPerformed
        if ( customerId <= 0 ) return;
        controller.resetAddressesToCustomerData();
    }//GEN-LAST:event_resetAddressesButtonActionPerformed

    private void convertToWarrantyPositionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_convertToWarrantyPositionButtonActionPerformed
        if ( !positionsFxList.getSelectionModel().isEmpty() ) {
            Platform.runLater(() -> {
                try {
                    //constructor made sure the service is present
                    document.appendAll(
                            lookup(WarrantyHook.class)
                                    .addWarrantyForUnitPosition(positionsFxList.getSelectionModel().getSelectedItem(), document.getId())
                                    .request(new SwingInteraction(this)));
                    positions.clear();
                    positions.addAll(document.getPositions().values());
                } catch (UserInfoException ex) {
                    Ui.handle(ex);
                }
            });
        }
    }//GEN-LAST:event_convertToWarrantyPositionButtonActionPerformed

    private void taxChangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_taxChangeButtonActionPerformed
        Ui.exec(() -> {
            Ui.build(this).fx().eval(() -> new TaxChangePane()).ifPresent(taxType -> {
                L.debug("Changeing Tax to {}", taxType);
                document.setTaxType(taxType);
                final PostLedger ledgers = Client.lookup(Mandators.class).loadPostLedger();
                document.getPositions().values().forEach(p -> {
                    p.setTax(taxType.getTax());
                    p.setBookingAccount(ledgers.get(p.getType(), taxType).orElse(null));
                });
                refreshAll();
            });
        });
    }//GEN-LAST:event_taxChangeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCommentButton;
    private javax.swing.JButton addProductBatchButton;
    private javax.swing.JButton addServiceButton;
    private javax.swing.JButton addUnitButton;
    private javax.swing.JTextPane addressesArea;
    private javax.swing.JButton convertToWarrantyPositionButton;
    private javax.swing.JButton editInvoiceAddressButton;
    private javax.swing.JButton editShippingAddress;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JLabel paymentMethodLabel;
    private javax.swing.JPanel positionPanelFx;
    private javax.swing.JLabel recentCustomerLabel;
    private javax.swing.JButton removePositionButton;
    private javax.swing.JButton resetAddressesButton;
    private javax.swing.JButton shippingCostButton;
    private javax.swing.JButton taxChangeButton;
    private javax.swing.JTextField unitInputField;
    // End of variables declaration//GEN-END:variables

    private void disableComponents(Component... components) {
        for (Component component : components) {
            component.setEnabled(false);
        }
    }

    private boolean isChangeAllowed() {
        if ( positionsFxList.getSelectionModel().getSelectedItem().getType() != PositionType.COMMENT ) {
            if ( document.isClosed() ) {
                return false;
            } else if ( document.getType() == DocumentType.INVOICE && !accessCos.hasRight(UPDATE_PRICE_INVOICES) ) {
                return false;
            } else if ( EnumSet.of(DocumentType.COMPLAINT, DocumentType.CREDIT_MEMO, DocumentType.ANNULATION_INVOICE).contains(document.getType()) ) {
                return false;
            }
        }
        return true;
    }

    private void refreshAll() {
        Platform.runLater(() -> { // reload the ui.
            positions.clear();
            positions.addAll(document.getPositions().values());
        });
    }

    @Override
    public void accept(Void t) {
        // Ignoere
    }

    @Override
    public boolean mayClose() {
        return pre(CloseType.OK);
    }

    @Override
    public Document getResult() {
        return document;
    }

}
