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
package eu.ggnet.dwoss.stock.ee;

import java.util.List;
import java.util.SortedMap;

import javax.ejb.Remote;

import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.util.UserInfoException;

/**
 * StockTransctionProcessor.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface StockTransactionProcessor {

    /**
     * Rolls all StockTransaction in, completing them and setting the Stock.
     *
     * @param detachtedTransactions the transactions
     * @param arranger              the arranger
     * @return returns all stockunitIds which were rolled in.
     */
    List<Integer> rollIn(List<StockTransaction> detachtedTransactions, String arranger);

    /**
     * Prepares the transfer of multiple units.
     * Creates an amount of needed transactions in the form,
     * - that the transactions are correct (all units of a transaction have the same source as the transaciton)
     * - that no transaction has more units than maxUnitSize.
     * <p/>
     * @param stockUnits         the stockUnits to transfer
     * @param destinationStockId the destination stockId
     * @param arranger           the arranger
     * @param comment            a optional comment
     * @return A map with uniqueUnitIds and comments for their history.
     * @throws UserInfoException
     */
    SortedMap<Integer, String> perpareTransfer(List<StockUnit> stockUnits, int destinationStockId, String arranger, String comment) throws UserInfoException;

    /**
     * Cancels a stock transaction.
     * <p/>
     * @param transaction the transaction to cancel.
     * @param arranger    the arranger
     * @param comment     a comment to describe why
     * @throws UserInfoException if the transaction is not in state prepared.
     */
    void cancel(StockTransaction transaction, final String arranger, final String comment) throws UserInfoException;

    /**
     * Bring a list of transactions from prepared into the state in transfer via commission.
     * <p/>
     * @param transactions the transaction to commission
     * @param picker       the pricker of units
     * @param deliverer    the transferer.
     */
    void commission(List<StockTransaction> transactions, String picker, String deliverer);

    /**
     * Receive a list of transactions in the destination stock.
     * <p/>
     * @param transactions the transactions to receive.
     * @param deliverer    the deliverer
     * @param reciever     the receiver
     */
    void receive(List<StockTransaction> transactions, String deliverer, String reciever);

    /**
     * Remove the stockUnit represented by the refurbishId from a stock transaction, if that transaction exists and is in state prepared.
     * <p/>
     * @param refurbishId the refurbishId
     * @param arranger    the arranger
     * @param comment     a optional comment
     * @throws UserInfoException if no unit exists, the unit is not on a transaction or the transaction has another state then prepared.
     */
    void removeFromPreparedTransaction(final String refurbishId, final String arranger, final String comment) throws UserInfoException;
}
