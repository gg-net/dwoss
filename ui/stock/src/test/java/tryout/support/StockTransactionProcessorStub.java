/*
 * Copyright (C) 2021 GG-Net GmbH
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
package tryout.support;

import java.util.*;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.stock.ee.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;

/**
 *
 * @author oliver.guenther
 */
public class StockTransactionProcessorStub implements StockTransactionProcessor {

    @Override
    public List<Integer> rollIn(List<StockTransaction> detachtedTransactions, String arranger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SortedMap<Integer, String> perpareTransfer(List<StockUnit> stockUnits, int destinationStockId, String arranger, String comment) throws UserInfoException {
        SortedMap<Integer, String> r = new TreeMap<>();
        if ( stockUnits == null || stockUnits.isEmpty() ) throw new UserInfoException("Keine Geräte für eine Transaction angegeben");
        stockUnits.forEach(su -> r.put(Optional.ofNullable(su.getUniqueUnitId()).orElse(0), "Kein Kommentar"));
        return r;
    }

    @Override
    public void cancel(StockTransaction transaction, String arranger, String comment) throws UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void commission(List<StockTransaction> transactions, String picker, String deliverer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void receive(List<StockTransaction> transactions, String deliverer, String reciever) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeFromPreparedTransaction(String refurbishId, String arranger, String comment) throws UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
