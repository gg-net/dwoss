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
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import jakarta.inject.Inject;

import javax.swing.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.*;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.core.widget.swing.ComboBoxController;
import eu.ggnet.dwoss.core.widget.swing.NamedEnumCellRenderer;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.receipt.ee.UnitSupporter;
import eu.ggnet.dwoss.receipt.ui.*;
import eu.ggnet.dwoss.receipt.ui.product.SimpleView;
import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink;
import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink.Result;
import eu.ggnet.dwoss.receipt.ui.unit.chain.Chains;
import eu.ggnet.dwoss.receipt.ui.unit.model.*;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.format.SpecFormater;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Equipment;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.StaticComment;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.StaticInternalComment;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.core.common.values.ReceiptOperation.IN_SALE;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.SERIAL;
import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;
import static eu.ggnet.saft.core.ui.UiParent.of;
import static javafx.scene.control.ButtonType.NO;
import static javafx.scene.control.ButtonType.YES;

/**
 *
 * @author bastian.venz, oliver.guenther
 */
@Dependent
@Title("Aufnahme")
@StoreLocation
public class UnitView extends javax.swing.JPanel implements Consumer<UnitView.In>, ResultProducer<UnitView.Out> {

    public static class WarrantyShortRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if ( value == null ) {
                label.setText("");
                return label;
            }
            if ( value instanceof Warranty w ) {
                if (w.description().length() > 35) {
                    label.setText(w.description().substring(0, 35) + " ...");
                    label.setToolTipText(w.description());
                } else {
                    label.setText(w.description());
                }
            } else {
                label.setText(value.toString());
            }
            return label;
        }
    }

    /**
     * Input Consumer class.
     */
    public static interface In {

        public static final class Create implements UnitView.In {

            private final Shipment shipment;

            public Create(Shipment shipment) {
                this.shipment = Objects.requireNonNull(shipment, "shipment must not be null");
            }

            public Shipment shipment() {
                return shipment;
            }

        }

        public static final class Edit implements UnitView.In {

            private final UniqueUnit uniqueUnit;

            private final ReceiptOperation receiptOperation;

            // TODO: Verify, if the case still exists, that a uniqueUnit has no product
            private final String partNo;

            public Edit(UniqueUnit uniqueUnit, ReceiptOperation receiptOperation, String partNo) {
                this.uniqueUnit = Objects.requireNonNull(uniqueUnit, "uniqueUnit must not be null");
                this.receiptOperation = Objects.requireNonNull(receiptOperation, "receiptOperation must not be null");
                this.partNo = Objects.requireNonNull(partNo, "partNo must not be null");
            }

            public UniqueUnit uniqueUnit() {
                return uniqueUnit;
            }

            public ReceiptOperation receiptOperation() {
                return receiptOperation;
            }

            // TODO: Verify, if the case still exists, that a uniqueUnit has no product
            public String partNo() {
                return partNo;
            }

        }

    }

    /**
     * Result Object, name may be optimzed
     */
    public static class Out {

        private final UniqueUnit uniqueUnit;

        private final Product product;

        private final ReceiptOperation receiptOperation;

        private final String comment;

        // Wenn shipment und stocktransaction null, dann edit, sonst create.
        public Out(UniqueUnit uniqueUnit, Product product, ReceiptOperation receiptOperation, String comment) {
            this.uniqueUnit = Objects.requireNonNull(uniqueUnit, "uniqueunit must not be null");
            this.product = Objects.requireNonNull(product, "product must not be null");
            this.receiptOperation = Objects.requireNonNull(receiptOperation, "receiptOperation must not be null");
            this.comment = comment; // can be null.
        }

        public UniqueUnit uniqueUnit() {
            return uniqueUnit;
        }

        public Product product() {
            return product;
        }

        public ReceiptOperation receiptOperation() {
            return receiptOperation;
        }

        /**
         * Returns comment, may be null
         *
         * @return comment, may be null
         */
        public String comment() {
            return comment;
        }

    }

    private class OperationAction extends AbstractAction {

        private final ReceiptOperation operation;

        public OperationAction(ReceiptOperation operation) {
            super(operation.description());
            this.operation = operation;
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            saft.exec(() -> {
                Optional<String> result = saft.build(UnitView.this).title("Übergabe in " + operation).dialog()
                        .eval(() -> {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setHeaderText("Unit in den Prozess " + operation + " übergeben ?");
                            dialog.setContentText("Kommentar:");
                            return dialog;
                        }).opt();
                if ( result.isEmpty() ) return;
                model.setOperation(operation);
                model.setOperationComment(result.get());
                cancel = false;
                showingProperty.set(false);
            });
        }
    }

    private class SaleableAction extends AbstractAction {

        public SaleableAction() {
            super("In den Verkauf");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            saft.exec(() -> {
                Optional<ButtonType> result = saft.build(UnitView.this).title("Zum Verkauf").dialog()
                        .eval(() -> {
                            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                            alert.setHeaderText("Unit zum Verkauf freigeben ?");
                            alert.getButtonTypes().setAll(YES, NO);
                            return alert;
                        }).opt();
                if ( result.isEmpty() || result.get().equals(NO) ) return;
                model.setOperation(ReceiptOperation.SALEABLE);
                cancel = false;
                showingProperty.set(false);
            });
        }
    }

    private class InSaleAction extends AbstractAction {

        public InSaleAction() {
            super("Änderungen übernehmen");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setOperation(ReceiptOperation.IN_SALE);
            cancel = false;
            showingProperty.set(false);
        }
    }

    private static final Logger L = LoggerFactory.getLogger(UnitView.class);

    @Bind(SHOWING)
    private final BooleanProperty showingProperty = new SimpleBooleanProperty();

    private final ReentrantLock lock = new ReentrantLock();

    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    @Inject
    private ProductUiBuilder productUiBuilder;

    private CheckBoxTableNoteModel<Equipment> equipmentModel = new CheckBoxTableNoteModel(Arrays.asList(Equipment.class.getEnumConstants()), "Ausstattung");

    private CheckBoxTableNoteModel<StaticComment> commentModel = new CheckBoxTableNoteModel(Arrays.asList(StaticComment.class.getEnumConstants()), "Bemerkungen");

    private CheckBoxTableNoteModel<StaticInternalComment> internalCommentModel = new CheckBoxTableNoteModel(Arrays.asList(StaticInternalComment.class.getEnumConstants()), "Interne Bemerkungen");

    private ComboBoxController<UniqueUnit.Condition> conditionController;

    private ComboBoxController<Warranty> warrantyController;

    private UniqueUnit unit;

    private UnitModel model;

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
            validateMfgDate();
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
            updateActions();
        }
    };

    public UnitView() {
        initComponents();
        this.model = new UnitModel();

        // Setting the change also in the subcomponent. FocusListener does not work completely.
        mfgDateChooser.addPropertyChangeListener(mfgProperty);
        mfgDateChooser.getDateEditor().getUiComponent().addPropertyChangeListener(mfgProperty);

        warrantyTillChooser.addPropertyChangeListener(warrantyProperty);
        warrantyTillChooser.getDateEditor().getUiComponent().addPropertyChangeListener(warrantyProperty);

        refurbishedIdEditButton.setEnabled(false);
        equipmentTable.setModel(equipmentModel);
        equipmentModel.setTable(equipmentTable);
        commentTable.setModel(commentModel);
        commentModel.setTable(commentTable);
        internalCommentTable.setModel(internalCommentModel);
        internalCommentModel.setTable(internalCommentTable);

        conditionController = new ComboBoxController<>(unitStateBox, UniqueUnit.Condition.values());
        warrantyController = new ComboBoxController<>(warrantyTypeChooser, Warranty.valuesSorted());
        warrantyTypeChooser.setRenderer(new WarrantyShortRenderer());
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

        contractorBox.setRenderer(new NamedEnumCellRenderer());
        contractorBox.setModel(new DefaultComboBoxModel(TradeName.getManufacturers().toArray()));
        showingProperty.addListener((ObservableValue<? extends Boolean> ov, Boolean o, Boolean n) -> {
            if ( n ) EventQueue.invokeLater(() -> refurbishedIdField.requestFocusInWindow());
        });
    }

    @Override
    public void accept(In in) {
        /*
        Der Workflow aus 2008 ist blöd, aber ihn komplett zu ersetzen, dauert zu lange.
        getUnit erzeugt im Zweifel eine instance und speichert bereits dinge.
         */

        if ( Objects.requireNonNull(in, "in must not be null") instanceof In.Create ) {
            var create = (In.Create)in;
            shipmentIdField.setText(create.shipment().getShipmentId());
            contractorField.setText(create.shipment().getContractor().toString());
            model.setContractor(create.shipment().getContractor());
            model.setMode(create.shipment().getDefaultManufacturer());
            //      contractorBox.setSelectedItem(create.shipment().getDefaultManufacturer());
        } else if ( in instanceof In.Edit ) {
            var edit = (In.Edit)in;
            model.setContractor(edit.uniqueUnit().getContractor());
            model.setOperation(edit.receiptOperation());
            model.setEditMode(true);
            setUnit(edit.uniqueUnit());
            if ( edit.uniqueUnit().getProduct() == null ) {
                setPartNo(edit.partNo()); // TODO: Verify, if this case still exists.
            } else {
                model.setMode(edit.uniqueUnit().getProduct().getTradeName().getManufacturer());
            }
        } else {
            throw new IllegalArgumentException("in is neither of type create nor edit. Should never happen");
        }

        updateChains();
        updateContratorBoxFromModel();
        validateAll();
        if ( model.getOperation() == IN_SALE ) { // This Unit is not available so only changes to the unit, but no followupaction ist allowed.
            addClosingAction(new InSaleAction());
        } else {
            addClosingAction(new SaleableAction());
            Dl.local().lookup(CachedMandators.class).loadReceiptCustomers().enabledOperations(model.getContractor())
                    .stream().forEach(r -> addClosingAction(new OperationAction(r)));
        }

    }

    @Override
    public Out getResult() {
        if ( cancel ) return null;
        // Modify internal comment on getResult. Cannot be done on setUnit, because setUnit is used internally. Old Workflow
        UniqueUnit uniqueUnit = getUnit();
        if ( !StringUtils.isBlank(model.getOperationComment()) ) {
            uniqueUnit.setInternalComment(uniqueUnit.getInternalComment() + ", " + model.getOperation() + ":" + model.getOperationComment());
        }
        return new UnitView.Out(uniqueUnit, model.getProduct(), model.getOperation(), model.getOperationComment());
    }

    public boolean isCancel() {
        return cancel;
    }

    private UniqueUnit getUnit() {
        if ( unit == null ) {
            unit = new UniqueUnit();
        }
        model.getMetaUnit().loadTo(unit);
        unit.setReceiveAssignAttribute(raaTextField.getText());
        unit.setCondition(conditionController.getSelected());
        unit.setWarranty(warrantyController.getSelected());
        unit.setEquipments(equipmentModel.getMarked());
        unit.setComments(commentModel.getMarked());
        unit.setInternalComments(internalCommentModel.getMarked());
        unit.setContractor(model.getContractor());

        if ( !StringUtils.isBlank(commentArea.getText()) ) {
            unit.setComment(commentArea.getText().replaceAll(System.lineSeparator(), " ").replaceAll("\\t", " "));
        } else {
            unit.setComment(commentArea.getText());
        }
        if ( !StringUtils.isBlank(internalCommentArea.getText()) ) {
            unit.setInternalComment(internalCommentArea.getText().replaceAll(System.lineSeparator(), " ").replaceAll("\\t", " "));
        } else {
            unit.setInternalComment(internalCommentArea.getText());
        }
        if ( warrantyController.getSelected().equals(Warranty.WARRANTY_TILL_DATE) ) {
            unit.setWarrentyValid(warrantyTillChooser.getDate());
        }
        return unit;
    }

    // TODO: set to model or at lest update the unitMetaModel
    private void setUnit(UniqueUnit unit) {
        if ( unit == null ) return;
        this.unit = unit;
        refurbishedIdField.setEditable(false);
        model.getMetaUnit().loadFrom(unit);
        model.setProduct(unit.getProduct());
        updateMetaUnit();
        raaTextField.setText(unit.getReceiveAssignAttribute());
        equipmentModel.setMarked(unit.getEquipments());
        commentModel.setMarked(unit.getComments());
        internalCommentModel.setMarked(unit.getInternalComments());
        conditionController.setSelected(unit.getCondition());
        warrantyController.setSelected(unit.getWarranty());
        commentArea.setText(unit.getComment());
        internalCommentArea.setText(unit.getInternalComment());
        contractorField.setText(unit.getContractor().toString());
        model.setContractor(unit.getContractor());

        if ( StringUtils.isNotBlank(unit.getShipmentLabel()) ) shipmentIdField.setText(unit.getShipmentLabel());
        if ( unit.getWarranty().equals(Warranty.WARRANTY_TILL_DATE) ) warrantyTillChooser.setDate(unit.getWarrentyValid());
    }

    private void setPartNo(String partNo) {
        partNoField.setText(partNo);
        model.getMetaUnit().getPartNo().setValue(partNo);
        validatePartNoAndLoadDetails();
    }

    private void addOperationAction(Action action) {
        operationButtonPanel.add(new JButton(action));
    }

    private void addClosingAction(Action action) {
        model.addAction(action);
        addOperationAction(action);
    }

    /**
     * Reloads the validation statuses of refurbishId, serial, partNo and mfg date from the model.
     */
    private void updateValidationStatus() {
        StringBuilder sb = new StringBuilder("Last Update:\n");
        MetaUnit mu = model.getMetaUnit();
        updateValidationStatus(refurbishedIdField, mu.getRefurbishId().getSurvey(), sb);
        updateValidationStatus(partNoField, mu.getPartNo().getSurvey(), sb);
        updateValidationStatus(mfgDateChooser, mu.getMfgDate().getSurvey(), sb);
        updateValidationStatus(serialField, mu.getSerial().getSurvey(), sb);
        lastMessage = sb.toString();
    }

    private void updateValidationStatus(JComponent component, UnitSurvey vs, StringBuilder sb) {
        EventQueue.invokeLater(() -> {
            component.setToolTipText(vs.getMessage());
            component.setForeground(vs.getStatus().color);
        });
        sb.append("- ").append(component.getName()).append(": ").append(vs.getStatus()).append(" : ").append(vs.getMessage()).append("\n");
    }

    private void updateChains() {
        L.debug("updateChains called with {}", model.getMode());
        MetaUnit metaUnit = model.getMetaUnit();
        Chains chains = Chains.getInstance(model.getMode());
        metaUnit.getRefurbishId().setChain(chains.newRefubishIdChain(model.getContractor(), Dl.remote().lookup(UnitSupporter.class), model.isEditMode()));
        metaUnit.getSerial().setChain(chains.newSerialChain(Dl.remote().lookup(UnitSupporter.class), (model.isEditMode() ? metaUnit.getRefurbishId().getValue() : null)));
        metaUnit.getPartNo().setChain(chains.newPartNoChain(Dl.remote().lookup(SpecAgent.class), Dl.local().lookup(CachedMandators.class).loadContractors().allowedBrands()));
        metaUnit.getMfgDate().setChain(chains.newMfgDateChain());
    }

    private void updateActions() {
        final boolean enabled = model.getMetaUnit().isOkOrWarn();
        final java.util.List<Action> updateActions = changedActions(enabled);
        if ( updateActions == null ) return;
        EventQueue.invokeLater(() -> {
            for (Action action : updateActions) {
                action.setEnabled(enabled);
            }
        });
    }

    // TODO: Wird das überhaupt verwendet ?
    private void editRefurbishedId(String refurbishId) {
        saft.build(this).title("SopoNr bearbeiten").dialog()
                .eval(() -> {
                    TextInputDialog dialog = new TextInputDialog(refurbishId);
                    dialog.setContentText("SopoNr:");
                    return dialog;
                }).cf()
                .thenAccept((String id) -> {
                    // TODO: Push this through the Validation Chain.
                    var tid = id.trim();
                    if ( tid.equals("") || tid.equals(refurbishId) ) throw new CancellationException("refurbishid is empty or unchanege");
                    if ( remote.lookup(UnitSupporter.class).isRefurbishIdAvailable(tid) ) {
                        model.getMetaUnit().getRefurbishId().setValue(tid);
                        updateMetaUnit();
                    } else {
                        saft.build(this).alert().message("SopoNr nicht verfügbar").show(AlertType.ERROR);
                    }
                }).handle(saft.handler(this));
    }

    private void validateRefurbishedId() {
        final MetaValue<String> value = model.getMetaUnit().getRefurbishId();

        new SwingWorker<String, Object>() {
            @Override
            protected String doInBackground() throws Exception {
                L.debug("Validating refurbishId : {}", value.getValue());
                return validateValue(value.getValue(), value.getChain(), value.getSurvey());
            }

            @Override
            protected void done() {
                try {
                    model.getMetaUnit().getRefurbishId().setValue(get());
                } catch (InterruptedException | ExecutionException ex) {
                    Ui.handle(ex);
                } finally {
                    updateMetaUnit();
                }
            }
        }.execute();
    }

    private void validatePartNoAndLoadDetails() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                MetaValue<String> value = model.getMetaUnit().getPartNo();
                L.debug("Validating partNo : {}", value.getValue());
                value.setValue(validateValue(value.getValue(), value.getChain(), value.getSurvey()));

                if ( value.getSurvey().isOkOrWarn() ) {
                    L.debug("Loading Details for PartNo: {}", value.getValue());
                    // Load details for update.
                    model.setProduct(Dl.remote().lookup(UniqueUnitAgent.class).findProductByPartNo(value.getValue()));
                    model.setProductSpecDescription(SpecFormater.toHtml(Dl.remote().lookup(SpecAgent.class).findProductSpecByPartNoEager(value.getValue())));
                } else {
                    L.debug("Removeing PartNo Details.");
                    model.setProduct(null);
                    model.setProductSpecDescription("");
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException ex) {
                    saft.handle(commentArea, ex);
                } finally {
                    updateMetaUnit();
                    updateProduct();
                }
            }
        }.execute();
    }

    private void validateSerial() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                MetaValue<String> value = model.getMetaUnit().getSerial();
                L.debug("Validating serial : {}", value.getValue());
                value.setValue(validateValue(value.getValue(), value.getChain(), value.getSurvey()));
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException ex) {
                    saft.handle(commentArea, ex);
                } finally {
                    updateMetaUnit();
                }
            }
        }.execute();
    }

    private void validateMfgDate() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                MetaValue<Date> value = model.getMetaUnit().getMfgDate();
                L.debug("Validating mfgDate : {}", value.getValue());
                value.setValue(validateValue(value.getValue(), value.getChain(), value.getSurvey()));
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException ex) {
                    saft.handle(commentArea, ex);
                } finally {
                    updateMetaUnit();
                }
            }
        }.execute();
    }

    private void validateAll() {
        validateRefurbishedId();
        validateSerial();
        validatePartNoAndLoadDetails();
        validateMfgDate();
    }

    private <T> T validateValue(T value, java.util.List<ChainLink<T>> chain, UnitSurvey validationStatus) {
        lock.lock();
        try {
            validationStatus.validating("Wert wird geprüft");
            updateValidationStatus();

            Result<T> result = Chains.execute(chain, value);

            L.debug("After Chain (optionals={}, metaunit.partno.isSet={}, metaunit.mfgDate.isSet={}) : {}",
                    result.hasOptionals(), model.getMetaUnit().getPartNo().isSet(), model.getMetaUnit().getMfgDate().isSet(), result);

            if ( result.hasOptionals() && result.optional.partNo != null && !model.getMetaUnit().getPartNo().isSet() ) {
                model.getMetaUnit().getPartNo().setValue(result.optional.partNo);
                validatePartNoAndLoadDetails();
            }

            if ( result.hasOptionals() && result.optional.mfgDate != null && !model.getMetaUnit().getMfgDate().isSet() ) {
                model.getMetaUnit().getMfgDate().setValue(result.optional.mfgDate);
                validateMfgDate();
            }

            validationStatus.setStatus(result.valid, result.message);
            updateValidationStatus();

            updateActions();
            return result.value;
        } finally {
            lock.unlock();
        }
    }

    private java.util.List<Action> changedActions(boolean enabled) {
        java.util.List<Action> updateActions = null;
        for (Action action : model.getActions()) {
            if ( enabled != action.isEnabled() ) {
                if ( updateActions == null ) updateActions = new ArrayList<>();
                updateActions.add(action);
            }
        }
        return updateActions;
    }

    /**
     * Reloads the values of refurbishId, serial, partNo and mfg date from the model.
     */
    private void updateMetaUnit() {
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

    /**
     * Updates the Product and the Description from the Model;
     */
    private void updateProduct() {
        Set<UniqueUnit.Equipment> equipment = UniqueUnit.Equipment.valueSet();
        Set<UniqueUnit.StaticComment> comments = UniqueUnit.StaticComment.valueSet();
        // Only show elements for the group
        if ( model.getProduct() != null ) {
            equipment.retainAll(UniqueUnit.Equipment.valueSet(model.getProduct().getGroup(), model.getProduct().getName()));
            comments.retainAll(UniqueUnit.StaticComment.valueSet(model.getProduct().getGroup(), model.getProduct().getName()));
        }
        // In the case (old products) a unit might contain a equipment, that the group no longer has.
        if ( unit != null ) {
            equipment.addAll(unit.getEquipments());
            comments.addAll(unit.getComments());
        }
        equipmentModel.setFiltered(equipment);
        commentModel.setFiltered(comments);
        detailArea.setText(model.getProductSpecDescription());
    }

    private void updateContratorBoxFromModel() {
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
        unitPanel = new javax.swing.JPanel();
        manufacturerPanel = new javax.swing.JPanel();
        contractorBox = new javax.swing.JComboBox();
        refurbishedIdLabel = new javax.swing.JLabel();
        refurbishedIdField = new javax.swing.JTextField();
        refurbishedIdEditButton = new javax.swing.JButton();
        serialLabel = new javax.swing.JLabel();
        serialField = new javax.swing.JTextField();
        partNoLabel = new javax.swing.JLabel();
        partNoField = new javax.swing.JTextField();
        partNoEditButton = new javax.swing.JButton();
        raaLabel = new javax.swing.JLabel();
        raaTextField = new javax.swing.JTextField();
        mfgLabel = new javax.swing.JLabel();
        mfgDateChooser = new com.toedter.calendar.JDateChooser();
        unitStatusLabel = new javax.swing.JLabel();
        unitStateBox = new javax.swing.JComboBox();
        warrantyTypeLabel = new javax.swing.JLabel();
        warrantyTypeChooser = new javax.swing.JComboBox();
        warrantyTillLabe = new javax.swing.JLabel();
        warrantyTillChooser = new com.toedter.calendar.JDateChooser();
        equipmentScrollPane = new javax.swing.JScrollPane();
        equipmentTable = new javax.swing.JTable();
        commentScrollPane = new javax.swing.JScrollPane();
        commentTable = new javax.swing.JTable();
        internalCommentScrollPane = new javax.swing.JScrollPane();
        internalCommentTable = new javax.swing.JTable();
        commentAreaScrollPane = new javax.swing.JScrollPane();
        commentArea = new javax.swing.JTextArea();
        internalCommentAreaScrollPane = new javax.swing.JScrollPane();
        internalCommentArea = new javax.swing.JTextArea();
        shipmentIdLabel = new javax.swing.JLabel();
        shipmentIdField = new javax.swing.JTextField();
        contractorLabel = new javax.swing.JLabel();
        contractorField = new javax.swing.JTextField();
        detailLabel = new javax.swing.JLabel();
        detailScrollPane = new javax.swing.JScrollPane();
        detailArea = new javax.swing.JEditorPane();
        cancelButton = new javax.swing.JButton();
        messagesButton = new javax.swing.JButton();
        operationButtonPanel = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(400, 200));
        setPreferredSize(new java.awt.Dimension(800, 600));
        setLayout(new java.awt.BorderLayout());

        unitPanel.setMinimumSize(new java.awt.Dimension(400, 200));
        unitPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        unitPanel.setLayout(new java.awt.GridBagLayout());

        manufacturerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hersteller Support")); // NOI18N
        manufacturerPanel.setPreferredSize(new java.awt.Dimension(150, 45));

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
            .addComponent(contractorBox, 0, 296, Short.MAX_VALUE)
        );
        manufacturerPanelLayout.setVerticalGroup(
            manufacturerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contractorBox, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        unitPanel.add(manufacturerPanel, gridBagConstraints);

        refurbishedIdLabel.setText("SopoNr:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        unitPanel.add(refurbishedIdLabel, gridBagConstraints);

        refurbishedIdField.setName("refurbishId"); // NOI18N
        refurbishedIdField.setNextFocusableComponent(serialField);
        refurbishedIdField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                refurbishedIdFieldFocusLost(evt);
            }
        });
        refurbishedIdField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refurbishedIdFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        unitPanel.add(refurbishedIdField, gridBagConstraints);

        refurbishedIdEditButton.setText("Edit");
        refurbishedIdEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refurbishedIdEditButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 2);
        unitPanel.add(refurbishedIdEditButton, gridBagConstraints);

        serialLabel.setText("SerienNr:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        unitPanel.add(serialLabel, gridBagConstraints);

        serialField.setName("serial"); // NOI18N
        serialField.setNextFocusableComponent(partNoField);
        serialField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                serialFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        unitPanel.add(serialField, gridBagConstraints);

        partNoLabel.setText("ArtikelNr:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        unitPanel.add(partNoLabel, gridBagConstraints);

        partNoField.setName("partNo"); // NOI18N
        partNoField.setNextFocusableComponent(raaTextField);
        partNoField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                partNoFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        unitPanel.add(partNoField, gridBagConstraints);

        partNoEditButton.setText("Edit");
        partNoEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                partNoEditButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 2);
        unitPanel.add(partNoEditButton, gridBagConstraints);

        raaLabel.setText("RAA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        unitPanel.add(raaLabel, gridBagConstraints);

        raaTextField.setName("raa"); // NOI18N
        raaTextField.setNextFocusableComponent(unitStateBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        unitPanel.add(raaTextField, gridBagConstraints);

        mfgLabel.setText("MFG Date:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        unitPanel.add(mfgLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        unitPanel.add(mfgDateChooser, gridBagConstraints);

        unitStatusLabel.setText("Zustand:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        unitPanel.add(unitStatusLabel, gridBagConstraints);

        unitStateBox.setNextFocusableComponent(warrantyTillChooser);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        unitPanel.add(unitStateBox, gridBagConstraints);

        warrantyTypeLabel.setText("Garantietyp:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        unitPanel.add(warrantyTypeLabel, gridBagConstraints);

        warrantyTypeChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kurz", "Länger", "Ganz laaaaaaaaaaaaaaaaaaaaaaaaaag" }));
        warrantyTypeChooser.setMaximumSize(new java.awt.Dimension(100, 22));
        warrantyTypeChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warrantyTypeChooserActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        unitPanel.add(warrantyTypeChooser, gridBagConstraints);

        warrantyTillLabe.setText("Garantie bis:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        unitPanel.add(warrantyTillLabe, gridBagConstraints);

        warrantyTillChooser.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        unitPanel.add(warrantyTillChooser, gridBagConstraints);

        equipmentScrollPane.setPreferredSize(new java.awt.Dimension(220, 220));

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
        equipmentScrollPane.setViewportView(equipmentTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 2);
        unitPanel.add(equipmentScrollPane, gridBagConstraints);

        commentScrollPane.setPreferredSize(new java.awt.Dimension(200, 200));

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
        commentScrollPane.setViewportView(commentTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        unitPanel.add(commentScrollPane, gridBagConstraints);

        internalCommentScrollPane.setPreferredSize(new java.awt.Dimension(200, 200));

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
        internalCommentTable.setPreferredSize(new java.awt.Dimension(100, 80));
        internalCommentScrollPane.setViewportView(internalCommentTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 2);
        unitPanel.add(internalCommentScrollPane, gridBagConstraints);

        commentArea.setColumns(20);
        commentArea.setLineWrap(true);
        commentArea.setRows(5);
        commentArea.setWrapStyleWord(true);
        commentAreaScrollPane.setViewportView(commentArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        unitPanel.add(commentAreaScrollPane, gridBagConstraints);

        internalCommentArea.setColumns(20);
        internalCommentArea.setLineWrap(true);
        internalCommentArea.setRows(5);
        internalCommentArea.setWrapStyleWord(true);
        internalCommentAreaScrollPane.setViewportView(internalCommentArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 2);
        unitPanel.add(internalCommentAreaScrollPane, gridBagConstraints);

        shipmentIdLabel.setText("Shipment Id:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        unitPanel.add(shipmentIdLabel, gridBagConstraints);

        shipmentIdField.setEditable(false);
        shipmentIdField.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        unitPanel.add(shipmentIdField, gridBagConstraints);

        contractorLabel.setText("Besitzer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        unitPanel.add(contractorLabel, gridBagConstraints);

        contractorField.setEditable(false);
        contractorField.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        unitPanel.add(contractorField, gridBagConstraints);

        detailLabel.setText("Details:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        unitPanel.add(detailLabel, gridBagConstraints);

        detailScrollPane.setFocusable(false);

        detailArea.setContentType("text/html"); // NOI18N
        detailArea.setFocusable(false);
        detailScrollPane.setViewportView(detailArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        unitPanel.add(detailScrollPane, gridBagConstraints);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        unitPanel.add(cancelButton, gridBagConstraints);

        messagesButton.setText("Meldungen");
        messagesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                messagesButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        unitPanel.add(messagesButton, gridBagConstraints);

        operationButtonPanel.setMinimumSize(new java.awt.Dimension(14, 3));
        operationButtonPanel.setPreferredSize(new java.awt.Dimension(14, 30));
        operationButtonPanel.setLayout(new java.awt.FlowLayout(2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 2);
        unitPanel.add(operationButtonPanel, gridBagConstraints);

        add(unitPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        showingProperty.set(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void messagesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messagesButtonActionPerformed
        saft.build(this).alert(lastMessage);
    }//GEN-LAST:event_messagesButtonActionPerformed

    private void contractorBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contractorBoxActionPerformed
        TradeName contractor = (TradeName)contractorBox.getSelectedItem();
        model.setMode(contractor);
        if ( contractor.isNoMfgDate() ) {
            mfgDateChooser.setDate(Utils.toDate(LocalDate.now().minusYears(2))); // Autoset Date
        }
        updateChains();
        validateAll();
    }//GEN-LAST:event_contractorBoxActionPerformed

    private void warrantyTypeChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warrantyTypeChooserActionPerformed
        if ( warrantyTypeChooser.getSelectedItem() != null && warrantyTypeChooser.getSelectedItem().equals(Warranty.WARRANTY_TILL_DATE) ) {
            warrantyTillChooser.setEnabled(true);
            model.getMetaUnit().setWarrentyTillSetted(true);
            updateActions();
        } else {
            warrantyTillChooser.setEnabled(false);
            model.getMetaUnit().setWarrentyTillSetted(false);
            updateActions();
        }
    }//GEN-LAST:event_warrantyTypeChooserActionPerformed

    private void refurbishedIdEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refurbishedIdEditButtonActionPerformed
        editRefurbishedId(refurbishedIdField.getText());
    }//GEN-LAST:event_refurbishedIdEditButtonActionPerformed

    private void serialFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_serialFieldFocusLost
        final String serial = serialField.getText();
        // Shortcut. Rethink if ok. Better pick from model
        if ( unit != null && serial.equals(unit.getIdentifier(SERIAL)) ) return;
        model.getMetaUnit().getSerial().setValue(serial);
        validateSerial();
    }//GEN-LAST:event_serialFieldFocusLost

    private void refurbishedIdFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refurbishedIdFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_refurbishedIdFieldActionPerformed

    private void refurbishedIdFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_refurbishedIdFieldFocusLost
        String refurbishedId = refurbishedIdField.getText();
        // Shortcut. Rethink if ok. Better pick from model
        if ( unit != null && refurbishedId.equals(unit.getIdentifier(REFURBISHED_ID)) ) return;
        model.getMetaUnit().getRefurbishId().setValue(refurbishedId);
        validateRefurbishedId();
    }//GEN-LAST:event_refurbishedIdFieldFocusLost

    private void partNoFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_partNoFieldFocusLost
        final String partNo = partNoField.getText();
        model.getMetaUnit().getPartNo().setValue(partNo);
        validatePartNoAndLoadDetails();
    }//GEN-LAST:event_partNoFieldFocusLost

    private void partNoEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_partNoEditButtonActionPerformed
        productUiBuilder.createOrEditPart(() -> new SimpleView.CreateOrEdit(model.getMode(), partNoField.getText()), of(this))
                .thenAccept(p -> validatePartNoAndLoadDetails())
                .thenAccept(p -> validateRefurbishedId())
                .handle(UiCore.global().handler(this));
    }//GEN-LAST:event_partNoEditButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextArea commentArea;
    private javax.swing.JScrollPane commentAreaScrollPane;
    private javax.swing.JScrollPane commentScrollPane;
    private javax.swing.JTable commentTable;
    private javax.swing.JComboBox contractorBox;
    private javax.swing.JTextField contractorField;
    private javax.swing.JLabel contractorLabel;
    private javax.swing.JEditorPane detailArea;
    private javax.swing.JLabel detailLabel;
    private javax.swing.JScrollPane detailScrollPane;
    private javax.swing.JScrollPane equipmentScrollPane;
    private javax.swing.JTable equipmentTable;
    private javax.swing.JTextArea internalCommentArea;
    private javax.swing.JScrollPane internalCommentAreaScrollPane;
    private javax.swing.JScrollPane internalCommentScrollPane;
    private javax.swing.JTable internalCommentTable;
    private javax.swing.ButtonGroup manufacturerButtonGroup;
    private javax.swing.JPanel manufacturerPanel;
    private javax.swing.JButton messagesButton;
    private com.toedter.calendar.JDateChooser mfgDateChooser;
    private javax.swing.JLabel mfgLabel;
    private javax.swing.JPanel operationButtonPanel;
    private javax.swing.JButton partNoEditButton;
    private javax.swing.JTextField partNoField;
    private javax.swing.JLabel partNoLabel;
    private javax.swing.JLabel raaLabel;
    private javax.swing.JTextField raaTextField;
    private javax.swing.JButton refurbishedIdEditButton;
    private javax.swing.JTextField refurbishedIdField;
    private javax.swing.JLabel refurbishedIdLabel;
    private javax.swing.JTextField serialField;
    private javax.swing.JLabel serialLabel;
    private javax.swing.JTextField shipmentIdField;
    private javax.swing.JLabel shipmentIdLabel;
    private javax.swing.JPanel unitPanel;
    private javax.swing.JComboBox unitStateBox;
    private javax.swing.JLabel unitStatusLabel;
    com.toedter.calendar.JDateChooser warrantyTillChooser;
    private javax.swing.JLabel warrantyTillLabe;
    javax.swing.JComboBox warrantyTypeChooser;
    private javax.swing.JLabel warrantyTypeLabel;
    // End of variables declaration//GEN-END:variables
}
