package eu.ggnet.dwoss.receipt;

import eu.ggnet.dwoss.stock.entity.Shipment;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.Stock;

import java.awt.Component;
import java.awt.Window;
import java.util.Objects;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.Guardian;


import eu.ggnet.dwoss.receipt.product.ComboBoxDialog;
import eu.ggnet.dwoss.receipt.unit.*;

import eu.ggnet.dwoss.rules.ReceiptOperation;

import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import eu.ggnet.dwoss.util.UserInfoException;

import lombok.Value;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * Ui support for the unit Operations.
 * <p/>
 * @author oliver.guenther
 */
public class UiUnitSupport {

    @Value
    private final static class UnitAndModel {

        private final UnitModel unitModel;

        private final UniqueUnit uniqueUnit;
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
                result.getUniqueUnit(),
                result.getUnitModel().getProduct(),
                productShipment,
                stockTransaction,
                result.getUnitModel().getOperation(),
                result.getUnitModel().getOperationComment(),
                lookup(Guardian.class).getUsername()
        );
    }

    /**
     * Starts the Ui to edit an existing Unit.
     * <p/>
     * @param refurbishedIdOrSerial the refurbishId or a serial
     * @throws UserInfoException if the unit may not be edited.
     */
    public void editUnit(String refurbishedIdOrSerial) throws UserInfoException {
        Window parent = lookup(Workspace.class).getMainFrame();
        if ( refurbishedIdOrSerial == null || refurbishedIdOrSerial.trim().equals("") ) return;
        refurbishedIdOrSerial = refurbishedIdOrSerial.trim().toUpperCase();
        UnitProcessor.EditableUnit eu = unitProcessor.findEditableUnit(refurbishedIdOrSerial);
        if ( eu.getOperation() == ReceiptOperation.IN_SALE ) {
            JOptionPane.showMessageDialog(parent, "Achtung, dieses Gerät ist in einem Kundenauftrag, ändern nicht empfohlen.");
        } else if ( eu.getOperation() != ReceiptOperation.SALEABLE ) {
            JOptionPane.showMessageDialog(parent, "Gerät ist in Operation : " + eu.getOperation());
        }

        UniqueUnit uu = eu.getUniqueUnit();
        if ( eu.getStockUnit() != null )
            uu = optionalChangeStock(eu.getUniqueUnit(), eu.getStockUnit(), lookup(Workspace.class).getValue(Stock.class), parent, lookup(Guardian.class).getUsername());

        UnitAndModel result = createEditUnit(parent, uu, eu.getOperation(), eu.getPartNo(), null);
        if ( result == null ) return;

        unitProcessor.update(
                result.getUniqueUnit(),
                result.getUnitModel().getProduct(),
                result.getUnitModel().getOperation(),
                result.getUnitModel().getOperationComment(),
                lookup(Guardian.class).getUsername()
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

    private UniqueUnit optionalChangeStock(UniqueUnit uniqueUnit, StockUnit stockUnit, Stock localStock, Window parent, String account) {
        if ( !stockUnit.isInStock() ) return uniqueUnit;
        if ( localStock.equals(stockUnit.getStock()) ) return uniqueUnit;
        if ( stockUnit.isInTransaction() ) {
            JOptionPane.showMessageDialog(parent,
                    "Achtung, Gerät ist nicht auf " + localStock.getName() + ",\n"
                    + "aber Gerät ist auch auf einer Transaktion.\n"
                    + "Automatische Lageränderung nicht möglich !");
            return uniqueUnit;
        }
        int option = JOptionPane.showConfirmDialog(parent,
                "Gerät steht nicht auf " + localStock.getName() + ", welches als Standort angegeben ist. Gerätestandort ändern ?",
                "Standortabweichung", JOptionPane.YES_NO_OPTION);
        if ( option == JOptionPane.YES_OPTION ) {
            ComboBoxDialog<Stock> dialog = new ComboBoxDialog<>(parent, lookup(StockAgent.class).findAll(Stock.class).toArray(new Stock[0]), new StockCellRenderer());
            dialog.setSelection(localStock);
            dialog.setVisible(true);
            if ( dialog.isOk() ) return unitProcessor.transfer(uniqueUnit, dialog.getSelection().getId(), account);
        }
        return uniqueUnit;
    }
}
