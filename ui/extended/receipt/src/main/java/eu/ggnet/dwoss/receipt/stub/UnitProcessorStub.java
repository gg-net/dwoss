package eu.ggnet.dwoss.receipt.stub;

import javax.enterprise.inject.Alternative;

import eu.ggnet.dwoss.receipt.UnitProcessor;

import eu.ggnet.dwoss.rules.ReceiptOperation;

import eu.ggnet.dwoss.stock.entity.Shipment;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import eu.ggnet.dwoss.util.UserInfoException;

/**
 *
 * @author oliver.guenther
 */
@Alternative
public class UnitProcessorStub implements UnitProcessor {

    @Override
    public void receipt(UniqueUnit recieptUnit, Product product, Shipment shipment, StockTransaction transaction, ReceiptOperation operation, String operationComment, String arranger) throws IllegalArgumentException {
        System.out.println("Receipt of " + recieptUnit);
    }

    @Override
    public void update(UniqueUnit uniqueUnit, Product product, ReceiptOperation updateOperation, String operationComment, String arranger) throws IllegalArgumentException {
        System.out.println("Update of" + uniqueUnit);
    }

    @Override
    public EditableUnit findEditableUnit(String refurbishedIdOrSerial) throws UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UniqueUnit transfer(UniqueUnit uniqueUnit, int stockId, String arranger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
