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
package eu.ggnet.dwoss.receipt.ui.unit;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.common.api.values.Warranty;
import eu.ggnet.dwoss.common.ui.*;
import eu.ggnet.dwoss.common.ui.table.CheckBoxTableNoteModel;
import eu.ggnet.dwoss.receipt.ui.unit.UnitModel.Survey;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Equipment;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.StaticComment;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.StaticInternalComment;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.UserPreferences;

import lombok.Getter;
import lombok.Setter;

import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.SERIAL;

/**
 *
 * @author bastian.venz, oliver.guenther
 */
public class UnitView extends javax.swing.JDialog {

    CheckBoxTableNoteModel<Equipment> equipmentModel = new CheckBoxTableNoteModel(Arrays.asList(Equipment.class.getEnumConstants()), "Ausstattung");

    CheckBoxTableNoteModel<StaticComment> commentModel = new CheckBoxTableNoteModel(Arrays.asList(StaticComment.class.getEnumConstants()), "Bemerkungen");

    CheckBoxTableNoteModel<StaticInternalComment> internalCommentModel = new CheckBoxTableNoteModel(Arrays.asList(StaticInternalComment.class.getEnumConstants()), "Interne Bemerkungen");

    private ComboBoxController<UniqueUnit.Condition> conditionController;

    private ComboBoxController<Warranty> warrantyController;

    private UniqueUnit unit;

    @Getter
    @Setter
    private UnitModel model;

    @Getter
    @Setter
    private UnitController controller;

    private boolean cancel = true;

    private String lastMessage = "";

    PropertyChangeListener mfgProperty = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // HINT: If a date was set and is switched back to null, it is ignored.
            if ( !evt.getPropertyName().equals("date") ) return;
            // ShortCut
            if ( Objects.equals(model.getMetaUnit().getMfgDate().getValue(), mfgDateChooser.getDate()) ) return;
            model.getMetaUnit().getMfgDate().setValue(mfgDateChooser.getDate());
            controller.validateMfgDate();
        }
    };

    PropertyChangeListener warrantyProperty = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // HINT: If a date was set and is switched back to null, it is ignored.
            if ( !evt.getPropertyName().equals("date") ) return;
            // ShortCut
            if ( Objects.equals(model.getMetaUnit().getWarrentyTill(), warrantyTillChooser.getDate()) ) return;
            model.getMetaUnit().setWarrentyTillSetted(((Warranty)warrantyTypeChooser.getSelectedItem()) == Warranty.WARRANTY_TILL_DATE);
            model.getMetaUnit().setWarrentyTill(warrantyTillChooser.getDate());
            controller.updateActions();
        }
    };

    public UnitView(Window window) {
        super(window);
        initComponents();
        setModalityType(ModalityType.APPLICATION_MODAL);
        setLocationRelativeTo(window);
        Dl.local().lookup(UserPreferences.class).loadLocation(this.getClass(), this);
        // Setting the change also in the subcomponent. FocusListener does not work completely.
        mfgDateChooser.addPropertyChangeListener(mfgProperty);
        mfgDateChooser.getDateEditor().getUiComponent().addPropertyChangeListener(mfgProperty);

        warrantyTillChooser.addPropertyChangeListener(warrantyProperty);
        warrantyTillChooser.getDateEditor().getUiComponent().addPropertyChangeListener(warrantyProperty);

        editRefurbishedIdButton.setEnabled(false);
        equipmentTable.setModel(equipmentModel);
        equipmentModel.setTable(equipmentTable);
        commentTable.setModel(commentModel);
        commentModel.setTable(commentTable);
        internalCommentTable.setModel(internalCommentModel);
        internalCommentModel.setTable(internalCommentTable);

        conditionController = new ComboBoxController<>(unitStateBox, UniqueUnit.Condition.values());
        warrantyController = new ComboBoxController<>(warrantyTypeChooser, Warranty.values());
        warrantyTypeChooser.setRenderer(new NamedEnumCellRenderer());
        unitStateBox.setRenderer(new NamedEnumCellRenderer());
        unitStateBox.setModel(new DefaultComboBoxModel(UniqueUnit.Condition.values()));

        refurbishedIdField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                new HashSet<>(Arrays.asList(KeyStroke.getKeyStroke("pressed ENTER"), KeyStroke.getKeyStroke("pressed TAB"))));
        serialField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                new HashSet<>(Arrays.asList(KeyStroke.getKeyStroke("pressed ENTER"), KeyStroke.getKeyStroke("pressed TAB"))));

        SwingTraversalUtil.forwardTab(partNoField, unitStateBox, internalCommentArea, commentArea);
        SwingTraversalUtil.backwardTab(refurbishedIdField, serialField, partNoField, unitStateBox);

        SwingTraversalUtil.spaceSelection(equipmentTable);
        SwingTraversalUtil.spaceSelection(internalCommentTable);
        SwingTraversalUtil.spaceSelection(commentTable);

        refurbishedIdField.requestFocus();
        contractorBox.setRenderer(new NamedEnumCellRenderer());
        contractorBox.setModel(new DefaultComboBoxModel(TradeName.getManufacturers().toArray()));
    }

    public void setShipment(Shipment shipment) {
        unitShipField.setText(shipment.getShipmentId());
        unitOwnerField.setText(shipment.getContractor().toString());
        model.setContractor(shipment.getContractor());
        model.setMode(shipment.getDefaultManufacturer());
        contractorBox.setSelectedItem(shipment.getDefaultManufacturer());
        controller.updateChains();
    }

    // TODO: set to model or at lest update the unitMetaModel
    public void setUnit(UniqueUnit unit) {
        if ( unit == null ) return;
        this.unit = unit;
        refurbishedIdField.setEditable(false);
        model.getMetaUnit().loadFrom(unit);
        model.setProduct(unit.getProduct());
        updateMetaUnit();
        equipmentModel.setMarked(unit.getEquipments());
        commentModel.setMarked(unit.getComments());
        internalCommentModel.setMarked(unit.getInternalComments());
        conditionController.setSelected(unit.getCondition());
        warrantyController.setSelected(unit.getWarranty());
        commentArea.setText(unit.getComment());
        internalCommentArea.setText(unit.getInternalComment());
        unitOwnerField.setText(unit.getContractor().toString());
        model.setContractor(unit.getContractor());

        if ( StringUtils.isNotBlank(unit.getShipmentLabel()) ) unitShipField.setText(unit.getShipmentLabel());
        if ( unit.getWarranty().equals(Warranty.WARRANTY_TILL_DATE) ) warrantyTillChooser.setDate(unit.getWarrentyValid());
    }

    public UniqueUnit getUnit() {
        if ( unit == null ) {
            unit = new UniqueUnit();
        }
        model.getMetaUnit().loadTo(unit);
        unit.setCondition(conditionController.getSelected());
        unit.setWarranty(warrantyController.getSelected());
        unit.setEquipments(equipmentModel.getMarked());
        unit.setComments(commentModel.getMarked());
        unit.setInternalComments(internalCommentModel.getMarked());
        unit.setContractor(model.getContractor());

        if ( !StringUtils.isBlank(commentArea.getText()) ) {
            unit.setComment(commentArea.getText().replaceAll(SystemUtils.LINE_SEPARATOR, " ").replaceAll("\\t", " "));
        } else {
            unit.setComment(commentArea.getText());
        }
        if ( !StringUtils.isBlank(internalCommentArea.getText()) ) {
            unit.setInternalComment(internalCommentArea.getText().replaceAll(SystemUtils.LINE_SEPARATOR, " ").replaceAll("\\t", " "));
        } else {
            unit.setInternalComment(internalCommentArea.getText());
        }
        if ( warrantyController.getSelected().equals(Warranty.WARRANTY_TILL_DATE) ) {
            unit.setWarrentyValid(warrantyTillChooser.getDate());
        }
        return unit;
    }

    public void setPartNo(String partNo) {
        partNoField.setText(partNo);
        model.getMetaUnit().getPartNo().setValue(partNo);
        controller.validatePartNoAndLoadDetails();
    }

    void addOperationAction(Action action) {
        operationButtonPanel.add(new JButton(action));
    }

    public boolean isCancel() {
        return cancel;
    }

    /**
     * Reloads the validation statuses of refurbishId, serial, partNo and mfg date from the model.
     */
    void updateValidationStatus() {
        StringBuilder sb = new StringBuilder("Last Update:\n");
        UnitModel.MetaUnit mu = model.getMetaUnit();
        updateValidationStatus(refurbishedIdField, mu.getRefurbishId().getSurvey(), sb);
        updateValidationStatus(partNoField, mu.getPartNo().getSurvey(), sb);
        updateValidationStatus(mfgDateChooser, mu.getMfgDate().getSurvey(), sb);
        updateValidationStatus(serialField, mu.getSerial().getSurvey(), sb);
        lastMessage = sb.toString();
    }

    private void updateValidationStatus(JComponent component, Survey vs, StringBuilder sb) {
        EventQueue.invokeLater(() -> {
            component.setToolTipText(vs.getMessage());
            component.setForeground(vs.getStatus().getColor());
        });
        sb.append("- ").append(component.getName()).append(": ").append(vs.getStatus()).append(" : ").append(vs.getMessage()).append("\n");
    }

    /**
     * Reloads the values of refurbishId, serial, partNo and mfg date from the model.
     */
    void updateMetaUnit() {
        if ( !Objects.equals(refurbishedIdField.getText(), model.getMetaUnit().getRefurbishId().getValue()) )
            refurbishedIdField.setText(model.getMetaUnit().getRefurbishId().getValue());

        if ( !Objects.equals(serialField.getText(), model.getMetaUnit().getSerial().getValue()) )
            serialField.setText(model.getMetaUnit().getSerial().getValue());

        if ( !Objects.equals(partNoField.getText(), model.getMetaUnit().getPartNo().getValue()) )
            partNoField.setText(model.getMetaUnit().getPartNo().getValue());

        if ( !Objects.equals(mfgDateChooser.getDate(), model.getMetaUnit().getMfgDate().getValue()) )
            mfgDateChooser.setDate(model.getMetaUnit().getMfgDate().getValue());

        if ( !Objects.equals(warrantyTillChooser.getDate(), model.getMetaUnit().getWarrentyTill()) )
            warrantyTillChooser.setDate(model.getMetaUnit().getWarrentyTill());
    }

    void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Updates the Product and the Description from the Model;
     */
    void updateProduct() {
        Set<UniqueUnit.Equipment> equipment = UniqueUnit.Equipment.getEquipments();
        if ( model.getProduct() != null ) equipment.retainAll(UniqueUnit.Equipment.getEquipments(model.getProduct().getGroup()));
        if ( unit != null ) equipment.addAll(unit.getEquipments());
        equipmentModel.setFiltered(equipment);
        detailArea.setText(model.getProductSpecDescription());
    }

    void updateMode() {
        contractorBox.setSelectedItem(model.getMode());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        manufacturerButtonGroup = new javax.swing.ButtonGroup();
        unitWritePanel = new javax.swing.JPanel();
        internalCommentAreaScrollPane = new javax.swing.JScrollPane();
        internalCommentArea = new javax.swing.JTextArea();
        commentAreaScrollPane = new javax.swing.JScrollPane();
        commentArea = new javax.swing.JTextArea();
        unitItemLabel = new javax.swing.JLabel();
        unitStatusLabel = new javax.swing.JLabel();
        unitStateBox = new javax.swing.JComboBox();
        editProductButton = new javax.swing.JButton();
        partNoField = new javax.swing.JTextField();
        refurbishedIdField = new javax.swing.JTextField();
        unitSnLabel = new javax.swing.JLabel();
        unitNumberLabel = new javax.swing.JLabel();
        serialField = new javax.swing.JTextField();
        editRefurbishedIdButton = new javax.swing.JButton();
        mfgLabel = new javax.swing.JLabel();
        mfgDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        warrantyTillChooser = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        warrantyTypeChooser = new javax.swing.JComboBox();
        manufacturerPanel = new javax.swing.JPanel();
        contractorBox = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        equipmentTable = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        commentTable = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        internalCommentTable = new javax.swing.JTable();
        unitShipLabel = new javax.swing.JLabel();
        unitShipField = new javax.swing.JTextField();
        unitOwnerLabel = new javax.swing.JLabel();
        unitOwnerField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        detailArea = new javax.swing.JEditorPane();
        messagesButton = new javax.swing.JButton();
        operationButtonPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gerät bearbeiten/aufnehmen");
        setMinimumSize(new java.awt.Dimension(1080, 700));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        unitWritePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(204, 204, 255), new java.awt.Color(51, 51, 51))));
        unitWritePanel.setMinimumSize(new java.awt.Dimension(500, 400));
        unitWritePanel.setPreferredSize(new java.awt.Dimension(597, 519));

        internalCommentArea.setColumns(20);
        internalCommentArea.setLineWrap(true);
        internalCommentArea.setRows(5);
        internalCommentArea.setWrapStyleWord(true);
        internalCommentAreaScrollPane.setViewportView(internalCommentArea);

        commentArea.setColumns(20);
        commentArea.setLineWrap(true);
        commentArea.setRows(5);
        commentArea.setWrapStyleWord(true);
        commentAreaScrollPane.setViewportView(commentArea);

        unitItemLabel.setText("ArtikelNr:");

        unitStatusLabel.setText("Zustand:");

        unitStateBox.setNextFocusableComponent(warrantyTillChooser);

        editProductButton.setText("Edit");
        editProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editProductButtonActionPerformed(evt);
            }
        });

        partNoField.setName("partNo"); // NOI18N
        partNoField.setNextFocusableComponent(unitStateBox);
        partNoField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                partNoFieldFocusLost(evt);
            }
        });

        refurbishedIdField.setName("refurbishId"); // NOI18N
        refurbishedIdField.setNextFocusableComponent(serialField);
        refurbishedIdField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                refurbishedIdFieldFocusLost(evt);
            }
        });

        unitSnLabel.setText("SerienNr:");

        unitNumberLabel.setText("SopoNr:");

        serialField.setName("serial"); // NOI18N
        serialField.setNextFocusableComponent(partNoField);
        serialField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                serialFieldFocusLost(evt);
            }
        });

        editRefurbishedIdButton.setText("Edit");
        editRefurbishedIdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editRefurbishedIdButtonActionPerformed(evt);
            }
        });

        mfgLabel.setText("MFG Date:");

        jLabel3.setText("Garantie bis:");

        warrantyTillChooser.setEnabled(false);

        jLabel4.setText("Garantietyp:");

        warrantyTypeChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warrantyTypeChooserActionPerformed(evt);
            }
        });

        manufacturerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hersteller Support"));

        contractorBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        contractorBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contractorBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout manufacturerPanelLayout = new javax.swing.GroupLayout(manufacturerPanel);
        manufacturerPanel.setLayout(manufacturerPanelLayout);
        manufacturerPanelLayout.setHorizontalGroup(
            manufacturerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contractorBox, 0, 322, Short.MAX_VALUE)
        );
        manufacturerPanelLayout.setVerticalGroup(
            manufacturerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contractorBox, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        equipmentTable.setModel(new javax.swing.table.DefaultTableModel(
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
        equipmentTable.setMaximumSize(new java.awt.Dimension(300, 64));
        jScrollPane3.setViewportView(equipmentTable);

        commentTable.setModel(new javax.swing.table.DefaultTableModel(
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
        commentTable.setMaximumSize(new java.awt.Dimension(300, 64));
        jScrollPane4.setViewportView(commentTable);

        internalCommentTable.setModel(new javax.swing.table.DefaultTableModel(
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
        internalCommentTable.setMaximumSize(new java.awt.Dimension(300, 64));
        jScrollPane5.setViewportView(internalCommentTable);

        unitShipLabel.setText("Shipment ID:");

        unitShipField.setEditable(false);
        unitShipField.setFocusable(false);

        unitOwnerLabel.setText("Besitzer:");

        unitOwnerField.setEditable(false);
        unitOwnerField.setFocusable(false);

        jLabel1.setText("Details:");

        jScrollPane2.setFocusable(false);

        detailArea.setContentType("text/html"); // NOI18N
        detailArea.setFocusable(false);
        jScrollPane2.setViewportView(detailArea);

        messagesButton.setText("Meldungen");
        messagesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                messagesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout unitWritePanelLayout = new javax.swing.GroupLayout(unitWritePanel);
        unitWritePanel.setLayout(unitWritePanelLayout);
        unitWritePanelLayout.setHorizontalGroup(
            unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(unitWritePanelLayout.createSequentialGroup()
                .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(unitWritePanelLayout.createSequentialGroup()
                            .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(unitStatusLabel)
                                .addComponent(jLabel4)
                                .addComponent(jLabel3))
                            .addGap(9, 9, 9)
                            .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(warrantyTillChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(unitStateBox, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(warrantyTypeChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(unitWritePanelLayout.createSequentialGroup()
                            .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(unitSnLabel)
                                .addComponent(unitNumberLabel))
                            .addGap(24, 24, 24)
                            .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(unitWritePanelLayout.createSequentialGroup()
                                    .addComponent(refurbishedIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(editRefurbishedIdButton, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(serialField, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(unitWritePanelLayout.createSequentialGroup()
                            .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(mfgLabel)
                                .addComponent(unitItemLabel))
                            .addGap(18, 18, 18)
                            .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(mfgDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(unitWritePanelLayout.createSequentialGroup()
                                    .addComponent(partNoField, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(editProductButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addComponent(manufacturerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(commentAreaScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(internalCommentAreaScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(unitWritePanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, unitWritePanelLayout.createSequentialGroup()
                                .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(unitShipLabel)
                                    .addComponent(unitOwnerLabel)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(unitOwnerField)
                                    .addComponent(unitShipField))
                                .addContainerGap())))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, unitWritePanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(messagesButton)
                        .addContainerGap())))
        );
        unitWritePanelLayout.setVerticalGroup(
            unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(unitWritePanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(unitShipField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unitShipLabel))
                .addGap(14, 14, 14)
                .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(unitOwnerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unitOwnerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(messagesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, unitWritePanelLayout.createSequentialGroup()
                .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(unitWritePanelLayout.createSequentialGroup()
                        .addComponent(manufacturerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(refurbishedIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(editRefurbishedIdButton)
                            .addComponent(unitNumberLabel))
                        .addGap(2, 2, 2)
                        .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(serialField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(unitSnLabel))
                        .addGap(4, 4, 4)
                        .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(partNoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(editProductButton)
                            .addComponent(unitItemLabel))
                        .addGap(4, 4, 4)
                        .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mfgDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfgLabel))
                        .addGap(4, 4, 4)
                        .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(unitStateBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(unitStatusLabel))
                        .addGap(4, 4, 4)
                        .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(warrantyTypeChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(4, 4, 4)
                        .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(warrantyTillChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
                    .addGroup(unitWritePanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(unitWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(commentAreaScrollPane)
                    .addComponent(internalCommentAreaScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        operationButtonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        operationButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
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
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(operationButtonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1212, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(unitWritePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1293, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(unitWritePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(operationButtonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Dl.local().lookup(UserPreferences.class).storeLocation(this.getClass(), this);
    }//GEN-LAST:event_formWindowClosing

    private void messagesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messagesButtonActionPerformed
        JOptionPane.showMessageDialog(this, lastMessage);
    }//GEN-LAST:event_messagesButtonActionPerformed

    private void contractorBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contractorBoxActionPerformed
        model.setMode((TradeName)contractorBox.getSelectedItem());
        controller.updateChains();
        controller.validateAll();
    }//GEN-LAST:event_contractorBoxActionPerformed

    private void warrantyTypeChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warrantyTypeChooserActionPerformed
        if ( warrantyTypeChooser.getSelectedItem() != null && warrantyTypeChooser.getSelectedItem().equals(Warranty.WARRANTY_TILL_DATE) ) {
            warrantyTillChooser.setEnabled(true);
            model.getMetaUnit().setWarrentyTillSetted(true);
            controller.updateActions();
        } else {
            warrantyTillChooser.setEnabled(false);
            model.getMetaUnit().setWarrentyTillSetted(false);
            controller.updateActions();
        }
    }//GEN-LAST:event_warrantyTypeChooserActionPerformed

    private void editRefurbishedIdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editRefurbishedIdButtonActionPerformed
        controller.editRefurbishedId(refurbishedIdField.getText());
    }//GEN-LAST:event_editRefurbishedIdButtonActionPerformed

    private void serialFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_serialFieldFocusLost
        final String serial = serialField.getText();
        // Shortcut. Rethink if ok. Better pick from model
        if ( unit != null && serial.equals(unit.getIdentifier(SERIAL)) ) return;
        model.getMetaUnit().getSerial().setValue(serial);
        controller.validateSerial();
    }//GEN-LAST:event_serialFieldFocusLost

    private void refurbishedIdFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_refurbishedIdFieldFocusLost
        String refurbishedId = refurbishedIdField.getText();
        // Shortcut. Rethink if ok. Better pick from model
        if ( unit != null && refurbishedId.equals(unit.getIdentifier(REFURBISHED_ID)) ) return;
        model.getMetaUnit().getRefurbishId().setValue(refurbishedId);
        controller.validateRefurbishedId();
    }//GEN-LAST:event_refurbishedIdFieldFocusLost

    private void partNoFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_partNoFieldFocusLost
        final String partNo = partNoField.getText();
        model.getMetaUnit().getPartNo().setValue(partNo);
        controller.validatePartNoAndLoadDetails();
    }//GEN-LAST:event_partNoFieldFocusLost

    private void editProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editProductButtonActionPerformed
        try {
            controller.createOrEditPart(partNoField.getText());
        } catch (UserInfoException ex) {
            Ui.handle(ex);
        }
        controller.validateRefurbishedId();
    }//GEN-LAST:event_editProductButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextArea commentArea;
    private javax.swing.JScrollPane commentAreaScrollPane;
    private javax.swing.JTable commentTable;
    private javax.swing.JComboBox contractorBox;
    private javax.swing.JEditorPane detailArea;
    private javax.swing.JButton editProductButton;
    private javax.swing.JButton editRefurbishedIdButton;
    private javax.swing.JTable equipmentTable;
    private javax.swing.JTextArea internalCommentArea;
    private javax.swing.JScrollPane internalCommentAreaScrollPane;
    private javax.swing.JTable internalCommentTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.ButtonGroup manufacturerButtonGroup;
    private javax.swing.JPanel manufacturerPanel;
    private javax.swing.JButton messagesButton;
    private com.toedter.calendar.JDateChooser mfgDateChooser;
    private javax.swing.JLabel mfgLabel;
    private javax.swing.JPanel operationButtonPanel;
    private javax.swing.JTextField partNoField;
    private javax.swing.JTextField refurbishedIdField;
    private javax.swing.JTextField serialField;
    private javax.swing.JLabel unitItemLabel;
    private javax.swing.JLabel unitNumberLabel;
    private javax.swing.JTextField unitOwnerField;
    private javax.swing.JLabel unitOwnerLabel;
    private javax.swing.JTextField unitShipField;
    private javax.swing.JLabel unitShipLabel;
    private javax.swing.JLabel unitSnLabel;
    private javax.swing.JComboBox unitStateBox;
    private javax.swing.JLabel unitStatusLabel;
    private javax.swing.JPanel unitWritePanel;
    private com.toedter.calendar.JDateChooser warrantyTillChooser;
    private javax.swing.JComboBox warrantyTypeChooser;
    // End of variables declaration//GEN-END:variables
}
