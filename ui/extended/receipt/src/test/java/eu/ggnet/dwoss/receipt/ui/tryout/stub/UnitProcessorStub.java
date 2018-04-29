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
package eu.ggnet.dwoss.receipt.ui.tryout.stub;

import javax.enterprise.inject.Alternative;

import eu.ggnet.dwoss.receipt.ee.UnitProcessor;
import eu.ggnet.dwoss.common.api.values.ReceiptOperation;

import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

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