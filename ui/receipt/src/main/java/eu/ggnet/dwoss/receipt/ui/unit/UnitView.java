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
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.ReceiptOperation;
import eu.ggnet.dwoss.core.common.values.Warranty;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.swing.ComboBoxController;
import eu.ggnet.dwoss.core.widget.swing.NamedEnumCellRenderer;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.receipt.ee.UnitSupporter;
import eu.ggnet.dwoss.receipt.ui.*;
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
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.ui.ResultProducer;

import static eu.ggnet.dwoss.core.common.values.ReceiptOperation.IN_SALE;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.SERIAL;
import static javax.swing.JOptionPane.*;

/**
 *
 * @author bastian.venz, oliver.guenther
 */
public class UnitView extends javax.swing.JDialog implements Consumer<UnitView.In>, ResultProducer<UnitView.Out> {

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
            this.comment = Objects.requireNonNull(comment, "comment must not be null");
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
            String comment = showInputDialog(UnitView.this, "Unit in den Process " + operation + " übergeben ?", "Frage", OK_CANCEL_OPTION);
            if ( comment == null ) return;
            model.setOperation(operation);
            model.setOperationComment(comment);
            setCancel(false);
            setVisible(false);
        }
    }

    private class SaleableAction extends AbstractAction {

        public SaleableAction() {
            super("In den Verkauf");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ( showConfirmDialog(UnitView.this, "Unit zum Verkauf freigeben ?", "Frage", YES_NO_OPTION) != YES_OPTION ) return;
            model.setOperation(ReceiptOperation.SALEABLE);
            setCancel(false);
            setVisible(false);
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
            setCancel(false);
            setVisible(false);
        }
    }

    private static final Logger L = LoggerFactory.getLogger(UnitView.class);

    private final ReentrantLock lock = new ReentrantLock();

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

    public UnitView(Window window) {
        super(window);
        initComponents();
        setModalityType(ModalityType.APPLICATION_MODAL);
        setLocationRelativeTo(window);
        this.model = new UnitModel();

        UiCore.global().locationStorage().loadLocation(this.getClass(), this);
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

    @Override
    public void accept(In in) {
        /*
        Der Workflow aus 2008 ist blöd, aber ihn komplett zu ersetzen, dauert zu lange.
        getUnit erzeugt im Zweifel eine instance und speichert bereits dinge.
         */

        if ( Objects.requireNonNull(in, "in must not be null") instanceof In.Create ) {
            var create = (In.Create)in;
            unitShipField.setText(create.shipment().getShipmentId());
            unitOwnerField.setText(create.shipment().getContractor().toString());
            model.setContractor(create.shipment().getContractor());
            model.setMode(create.shipment().getDefaultManufacturer());
            contractorBox.setSelectedItem(create.shipment().getDefaultManufacturer());
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

        updateMode();
        updateChains();
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

    private void createOrEditPart(String partNo) throws UserInfoException {
        UiProductSupport.createOrEditPart(model.getMode(), partNo, this);
        validatePartNoAndLoadDetails();
    }

    private void editRefurbishedId(String refurbishId) {
        String newRefurbishId = JOptionPane.showInputDialog(this, "SopoNr:", refurbishId);
        if ( newRefurbishId == null ) return;
        // TODO: Push this through the Validation Chain.
        newRefurbishId = newRefurbishId.trim();
        if ( newRefurbishId.equals("") ) return;
        if ( newRefurbishId.equals(refurbishId) ) return;
        if ( Dl.remote().lookup(UnitSupporter.class).isRefurbishIdAvailable(refurbishId) ) {
            model.getMetaUnit().getRefurbishId().setValue(refurbishId);
            updateMetaUnit();
        } else {
            JOptionPane.showMessageDialog(this, "SopoNr nicht verfügbar", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
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
                    Ui.handle(ex);
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
                    Ui.handle(ex);
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
                    Ui.handle(ex);
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

    private void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Updates the Product and the Description from the Model;
     */
    private void updateProduct() {
        Set<UniqueUnit.Equipment> equipment = UniqueUnit.Equipment.getEquipments();
        if ( model.getProduct() != null ) equipment.retainAll(UniqueUnit.Equipment.getEquipments(model.getProduct().getGroup()));
        if ( unit != null ) equipment.addAll(unit.getEquipments());
        equipmentModel.setFiltered(equipment);
        detailArea.setText(model.getProductSpecDescription());
    }

    private void updateMode() {
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
        UiCore.global().locationStorage().storeLocation(this.getClass(), this);
    }//GEN-LAST:event_formWindowClosing

    private void messagesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messagesButtonActionPerformed
        JOptionPane.showMessageDialog(this, lastMessage);
    }//GEN-LAST:event_messagesButtonActionPerformed

    private void contractorBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contractorBoxActionPerformed
        model.setMode((TradeName)contractorBox.getSelectedItem());
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

    private void editRefurbishedIdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editRefurbishedIdButtonActionPerformed
        editRefurbishedId(refurbishedIdField.getText());
    }//GEN-LAST:event_editRefurbishedIdButtonActionPerformed

    private void serialFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_serialFieldFocusLost
        final String serial = serialField.getText();
        // Shortcut. Rethink if ok. Better pick from model
        if ( unit != null && serial.equals(unit.getIdentifier(SERIAL)) ) return;
        model.getMetaUnit().getSerial().setValue(serial);
        validateSerial();
    }//GEN-LAST:event_serialFieldFocusLost

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

    private void editProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editProductButtonActionPerformed
        try {
            createOrEditPart(partNoField.getText());
        } catch (UserInfoException ex) {
            Ui.handle(ex);
        }
        validateRefurbishedId();
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
