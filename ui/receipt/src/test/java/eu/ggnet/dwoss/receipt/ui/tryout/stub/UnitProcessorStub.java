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

import java.util.List;
import java.util.Random;

import jakarta.enterprise.inject.Alternative;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.ReceiptOperation;
import eu.ggnet.dwoss.receipt.ee.UnitProcessor;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

/**
 *
 * @author oliver.guenther
 */
@Alternative
public class UnitProcessorStub implements UnitProcessor {

    private final List<UniqueUnit> uniqueUnits;

    private final List<StockUnit> stockUnits;

    private final static Random R = new Random();

    public UnitProcessorStub(List<UniqueUnit> uniqueUnits, List<StockUnit> stockUnits) {
        this.uniqueUnits = uniqueUnits;
        this.stockUnits = stockUnits;
    }

    @Override
    public void receipt(UniqueUnit recieptUnit, Product product, Shipment shipment, int destinationId, ReceiptOperation operation, String operationComment, String arranger) throws IllegalArgumentException {
        System.out.println("UnitProcessorStub.receipt(" + recieptUnit + ")");
    }

    @Override
    public void update(UniqueUnit uniqueUnit, Product product, ReceiptOperation updateOperation, String operationComment, String arranger) throws IllegalArgumentException {
        System.out.println("UnitProcessorStub.update(" + uniqueUnit + ")");
    }

    @Override
    public EditableUnit findEditableUnit(String refurbishedIdOrSerial) throws UserInfoException {
        UniqueUnit uniqueUnit = uniqueUnits.stream().filter(uu -> refurbishedIdOrSerial.equals(uu.getRefurbishId()) || refurbishedIdOrSerial.equals(uu.getSerial()))
                .findAny().orElseThrow(() -> new UserInfoException("Keine UniqeuUnit mit SopoNr/Seriennummer: " + refurbishedIdOrSerial));
        StockUnit stockUnit = stockUnits.stream().filter(su -> uniqueUnit.getRefurbishId().equals(su.getRefurbishId()))
                .findAny().orElseThrow(() -> new UserInfoException("Keine StockUnit mit SopoNr/Seriennummer: " + refurbishedIdOrSerial));
        return new EditableUnit(uniqueUnit, stockUnit, ReceiptOperation.values()[R.nextInt(ReceiptOperation.values().length)], uniqueUnit.getProduct().getPartNo());
    }

    @Override
    public UniqueUnit transfer(UniqueUnit uniqueUnit, int stockId, String arranger) {
        System.out.println("UnitProcessorStub.transfer(" + uniqueUnit + ")");
        return uniqueUnit;
    }
}
