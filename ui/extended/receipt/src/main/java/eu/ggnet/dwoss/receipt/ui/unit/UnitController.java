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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.mandator.upi.CachedMandators;
import eu.ggnet.dwoss.receipt.ui.UiProductSupport;
import eu.ggnet.dwoss.receipt.ee.UnitSupporter;
import eu.ggnet.dwoss.receipt.ui.unit.UnitModel.MetaValue;
import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink;
import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink.Result;
import eu.ggnet.dwoss.receipt.ui.unit.chain.Chains;
import eu.ggnet.dwoss.common.api.values.ReceiptOperation;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.format.SpecFormater;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.validation.ValidationUtil;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;

import lombok.Getter;
import lombok.Setter;

import static eu.ggnet.dwoss.common.api.values.ReceiptOperation.IN_SALE;
import static javax.swing.JOptionPane.*;

public class UnitController {

    private class OperationAction extends AbstractAction {

        private final ReceiptOperation operation;

        public OperationAction(ReceiptOperation operation) {
            super(operation.getNote());
            this.operation = operation;
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String comment = showInputDialog(view, "Unit in den Process " + operation + " übergeben ?", "Frage", OK_CANCEL_OPTION);
            if ( comment == null ) return;
            model.setOperation(operation);
            model.setOperationComment(comment);
            view.setCancel(false);
            view.setVisible(false);
        }
    }

    private class SaleableAction extends AbstractAction {

        public SaleableAction() {
            super("In den Verkauf");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ( showConfirmDialog(view, "Unit zum Verkauf freigeben ?", "Frage", YES_NO_OPTION) != YES_OPTION ) return;
            model.setOperation(ReceiptOperation.SALEABLE);
            view.setCancel(false);
            view.setVisible(false);
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
            view.setCancel(false);
            view.setVisible(false);
        }
    }

    private static final Logger L = LoggerFactory.getLogger(UnitController.class);

    @Getter
    @Setter
    private UnitView view;

    // TODO: Add in the usage actions. for now its ok.
    @Getter
    @Setter
    private UnitModel model;

    private ReentrantLock lock = new ReentrantLock();

    /**
     * Init the controller, call after setting view an model.
     */
    public void init() {
        Objects.requireNonNull(model, "Model is null");
        Objects.requireNonNull(view, "View is null");
        Objects.requireNonNull(view.getController(), "View has no controller");
        Objects.requireNonNull(view.getModel(), "View has no model");
        ValidationUtil.validate(model);

        UniqueUnit uu = view.getUnit();
        if ( uu.getId() > 0 ) model.setEditMode(true);
        if ( uu.getProduct() != null ) model.setMode(uu.getProduct().getTradeName().getManufacturer());
        view.updateMode();
        updateChains();
        validateAll();

        if ( model.getOperation() == IN_SALE ) {
            // This Unit is not available so only simple changes are allowed and no RedTape Modifikation.
            addClosingAction(new InSaleAction());
            return;
        }
        addClosingAction(new SaleableAction());
        Dl.local().lookup(CachedMandators.class).loadReceiptCustomers().enabledOperations(model.getContractor())
                .stream().forEach(r -> addClosingAction(new OperationAction(r)));
    }

    public void createOrEditPart(String partNo) throws UserInfoException {
        UiProductSupport.createOrEditPart(model.getMode(), partNo, view);
        validatePartNoAndLoadDetails();
    }

    public void validateRefurbishedId() {
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
                    view.updateMetaUnit();
                }
            }
        }.execute();
    }

    public void validatePartNoAndLoadDetails() {
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
                    view.updateMetaUnit();
                    view.updateProduct();
                }
            }
        }.execute();
    }

    public void validateSerial() {
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
                    view.updateMetaUnit();
                }
            }
        }.execute();
    }

    public void validateMfgDate() {
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
                    view.updateMetaUnit();
                }
            }
        }.execute();
    }

    public void editRefurbishedId(String refurbishId) {
        String newRefurbishId = JOptionPane.showInputDialog(view, "SopoNr:", refurbishId);
        if ( newRefurbishId == null ) return;
        // TODO: Push this through the Validation Chain.
        newRefurbishId = newRefurbishId.trim();
        if ( newRefurbishId.equals("") ) return;
        if ( newRefurbishId.equals(refurbishId) ) return;
        if ( Dl.remote().lookup(UnitSupporter.class).isRefurbishIdAvailable(refurbishId) ) {
            model.getMetaUnit().getRefurbishId().setValue(refurbishId);
            view.updateMetaUnit();
        } else {
            JOptionPane.showMessageDialog(view, "SopoNr nicht verfügbar", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    void validateAll() {
        validateRefurbishedId();
        validateSerial();
        validatePartNoAndLoadDetails();
        validateMfgDate();
    }

    private <T> T validateValue(T value, List<ChainLink<T>> chain, UnitModel.Survey validationStatus) {
        lock.lock();
        try {
            validationStatus.validating("Wert wird geprüft");
            view.updateValidationStatus();

            Result<T> result = Chains.execute(chain, value);

            L.debug("After Chain (optionals={}, metaunit.partno.isSet={}, metaunit.mfgDate.isSet={}) : {}",
                    result.hasOptionals(), model.getMetaUnit().getPartNo().isSet(), model.getMetaUnit().getMfgDate().isSet(), result);

            if ( result.hasOptionals() && result.getOptional().getPartNo() != null && !model.getMetaUnit().getPartNo().isSet() ) {
                model.getMetaUnit().getPartNo().setValue(result.getOptional().getPartNo());
                validatePartNoAndLoadDetails();
            }

            if ( result.hasOptionals() && result.getOptional().getMfgDate() != null && !model.getMetaUnit().getMfgDate().isSet() ) {
                model.getMetaUnit().getMfgDate().setValue(result.getOptional().getMfgDate());
                validateMfgDate();
            }

            validationStatus.setStatus(result.getValid(), result.getMessage());
            view.updateValidationStatus();

            updateActions();
            return result.getValue();
        } finally {
            lock.unlock();
        }
    }

    void updateChains() {
        L.debug("updateChains called with {}", model.getMode());
        UnitModel.MetaUnit metaUnit = model.getMetaUnit();
        Chains chains = Chains.getInstance(model.getMode());
        metaUnit.getRefurbishId().setChain(chains.newRefubishIdChain(model.getContractor(), Dl.remote().lookup(UnitSupporter.class), model.isEditMode()));
        metaUnit.getSerial().setChain(chains.newSerialChain(Dl.remote().lookup(UnitSupporter.class), (model.isEditMode() ? metaUnit.getRefurbishId().getValue() : null)));
        metaUnit.getPartNo().setChain(chains.newPartNoChain(Dl.remote().lookup(SpecAgent.class), Dl.local().lookup(CachedMandators.class).loadContractors().allowedBrands()));
        metaUnit.getMfgDate().setChain(chains.newMfgDateChain());
    }

    void updateActions() {
        final boolean enabled = model.getMetaUnit().isOkOrWarn();
        final List<Action> updateActions = changedActions(enabled);
        if ( updateActions == null ) return;
        EventQueue.invokeLater(() -> {
            for (Action action : updateActions) {
                action.setEnabled(enabled);
            }
        });
    }

    private List<Action> changedActions(boolean enabled) {
        List<Action> updateActions = null;
        for (Action action : model.getActions()) {
            if ( enabled != action.isEnabled() ) {
                if ( updateActions == null ) updateActions = new ArrayList<>();
                updateActions.add(action);
            }
        }
        return updateActions;
    }

    private void addClosingAction(Action action) {
        model.addAction(action);
        view.addOperationAction(action);
    }
}
