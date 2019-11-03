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
package eu.ggnet.dwoss.receipt.ui;

import java.awt.Component;
import java.awt.Window;
import java.util.Objects;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.core.common.values.ReceiptOperation;
import eu.ggnet.dwoss.receipt.ee.UnitProcessor;
import eu.ggnet.dwoss.receipt.ui.unit.*;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.stock.upi.StockUpi;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.experimental.auth.Guardian;

/**
 * Ui support for the unit Operations.
 * <p/>
 * @author oliver.guenther
 */
public class UiUnitSupport {

    private final static class UnitAndModel {

        private final UnitModel unitModel;

        private final UniqueUnit uniqueUnit;

        public UnitAndModel(UnitModel unitModel, UniqueUnit uniqueUnit) {
            this.unitModel = unitModel;
            this.uniqueUnit = uniqueUnit;
        }

        @Override
        public String toString() {
            return "UnitAndModel{" + "unitModel=" + unitModel + ", uniqueUnit=" + uniqueUnit + '}';
        }
        
    }

    public class StockCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if ( value == null ) return label;
            if ( value instanceof Stock ) label.setText(((Stock)value).getName());
            return label;
        }
    }

    private final UnitProcessor unitProcessor;

    public UiUnitSupport(UnitProcessor unitProcessor) {
        this.unitProcessor = Objects.requireNonNull(unitProcessor, UnitProcessor.class.getSimpleName() + " must not be null");
    }

    /**
     * Starts the Ui to create a new Unit in the stock.
     * <p/>
     * @param stockTransaction the stockTransaction, that will be used to rollin the unit.
     * @param productShipment  the productShipment the unit is comming from.
     * @param parent           the parent window to center the dialog
     */
    public void createUnit(StockTransaction stockTransaction, Shipment productShipment, Window parent) {

        UnitAndModel result = createEditUnit(parent, null, null, null, productShipment);
        if ( result == null ) return;

        unitProcessor.receipt(
                result.uniqueUnit,
                result.unitModel.getProduct(),
                productShipment,
                stockTransaction,
                result.unitModel.getOperation(),
                result.unitModel.getOperationComment(),
                Dl.local().lookup(Guardian.class).getUsername()
        );
    }

    /**
     * Starts the Ui to edit an existing Unit.
     * <p/>
     * @param refurbishedIdOrSerial the refurbishId or a serial
     * @throws UserInfoException if the unit may not be edited.
     */
    public void editUnit(String refurbishedIdOrSerial) throws UserInfoException {
        Window parent = UiCore.getMainFrame();
        if ( refurbishedIdOrSerial == null || refurbishedIdOrSerial.trim().equals("") ) return;
        refurbishedIdOrSerial = refurbishedIdOrSerial.trim().toUpperCase();
        UnitProcessor.EditableUnit eu = unitProcessor.findEditableUnit(refurbishedIdOrSerial);
        if ( eu.operation == ReceiptOperation.IN_SALE ) {
            JOptionPane.showMessageDialog(parent, "Achtung, dieses Gerät ist in einem Kundenauftrag, ändern nicht empfohlen.");
        } else if ( eu.operation != ReceiptOperation.SALEABLE ) {
            JOptionPane.showMessageDialog(parent, "Gerät ist in Operation : " + eu.operation);
        }

        UniqueUnit uu = eu.uniqueUnit;
        if ( eu.stockUnit != null )
            uu = optionalChangeStock(eu.uniqueUnit, eu.stockUnit, Dl.local().lookup(StockUpi.class).getActiveStock(), parent, Dl.local().lookup(Guardian.class).getUsername());

        UnitAndModel result = createEditUnit(parent, uu, eu.operation, eu.partNo, null);
        if ( result == null ) return;

        unitProcessor.update(
                result.uniqueUnit,
                result.unitModel.getProduct(),
                result.unitModel.getOperation(),
                result.unitModel.getOperationComment(),
                Dl.local().lookup(Guardian.class).getUsername()
        );
    }

    /**
     * Part of create and Edit, which is equal.
     * <p/>
     * @param parent           the parent for layout
     * @param inUnit           the unit to manipulate. (Only needed in Edit)
     * @param receiptOperation the last receiptOperation (Only needed in Edit)
     * @param partNo           the partNo of the unit.product (Only needed in Edit and if the UniqueUnit.product == null)
     * @param shipment         the shipment (Only needed in Create)
     * @return a tuple of the modified unit and the ui model for supplementary information or null if the manipulation has been canceled.
     */
    private UnitAndModel createEditUnit(final Window parent,
                                        final UniqueUnit inUnit,
                                        final ReceiptOperation receiptOperation,
                                        final String partNo,
                                        final Shipment shipment) {
        UnitModel model = new UnitModel();
        if ( inUnit != null ) model.setContractor(inUnit.getContractor()); // Only on Edit
        if ( receiptOperation != null ) model.setOperation(receiptOperation); // Only on Edit

        UnitController controller = new UnitController();
        controller.setModel(model);

        UnitView view = new UnitView(parent);
        view.setModel(model);
        view.setController(controller);
        controller.setView(view);

        if ( shipment != null ) view.setShipment(shipment); // Only on Create

        if ( inUnit != null ) { // Only on Edit
            view.setUnit(inUnit);
            if ( inUnit.getProduct() == null ) view.setPartNo(partNo); // Extra on Edit
        }

        controller.init();
        view.setVisible(true);
        if ( view.isCancel() ) return null; // HINT JDK8 OptionalPattern
        // This would normaly be in the controller, but the design of the UnitView Controller is a little bit stupid
        UniqueUnit uniqueUnit = view.getUnit();
        if ( !StringUtils.isBlank(model.getOperationComment()) ) {
            uniqueUnit.setInternalComment(uniqueUnit.getInternalComment() + ", " + model.getOperation() + ":" + model.getOperationComment());
        }
        return new UnitAndModel(model, uniqueUnit);
    }

    private UniqueUnit optionalChangeStock(UniqueUnit uniqueUnit, StockUnit stockUnit, PicoStock localStock, Window parent, String account) {
        if ( !stockUnit.isInStock() ) return uniqueUnit;
        if ( localStock.id == stockUnit.getStock().getId() ) return uniqueUnit;
        if ( stockUnit.isInTransaction() ) {
            JOptionPane.showMessageDialog(parent,
                    "Achtung, Gerät ist nicht auf " + localStock.shortDescription + ",\n"
                    + "aber Gerät ist auch auf einer Transaktion.\n"
                    + "Automatische Lageränderung nicht möglich !");
            return uniqueUnit;
        }
        int option = JOptionPane.showConfirmDialog(parent,
                "Gerät steht nicht auf " + localStock.shortDescription + ", welches als Standort angegeben ist. Gerätestandort ändern ?",
                "Standortabweichung", JOptionPane.YES_NO_OPTION);
        if ( option == JOptionPane.YES_OPTION ) {
            StockDialog dialog = new StockDialog(parent, Dl.remote().lookup(StockAgent.class).findAll(Stock.class).toArray(new Stock[0]), new StockCellRenderer());
            dialog.setSelection(localStock);
            dialog.setVisible(true);
            if ( dialog.isOk() ) return unitProcessor.transfer(uniqueUnit, dialog.getSelection().getId(), account);
        }
        return uniqueUnit;
    }
}
