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

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.*;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.StockTransactionEao;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.emo.*;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.uniqueunit.api.event.SalesChannelChange;
import eu.ggnet.dwoss.uniqueunit.api.event.UnitHistory;
import eu.ggnet.dwoss.core.system.util.ValidationUtil;

import static eu.ggnet.dwoss.stock.ee.entity.StockTransactionParticipationType.*;
import static eu.ggnet.dwoss.stock.ee.entity.StockTransactionStatusType.*;

/**
 * Implementation of StockTransctionProcessor.
 *
 * @author oliver.guenther
 */
// TODO: Look up methodes here and in the StockTransactionEmo. I assume there some overlays.
@Stateless
public class StockTransactionProcessorOperation implements StockTransactionProcessor {

    private final static Logger L = LoggerFactory.getLogger(StockTransactionProcessorOperation.class);

    private final static Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    private Event<UnitHistory> history;

    @Inject
    private Event<SalesChannelChange> channelChanger;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private Mandator mandator;

    /**
     * Rolls all StockTransaction in, completing them and setting the Stock.
     *
     * @param detachtedTransactions the transactions
     * @param arranger              the arranger
     */
    @Override
    public List<Integer> rollIn(List<StockTransaction> detachtedTransactions, String arranger) {
        SubMonitor m = monitorFactory.newSubMonitor("RollIn", detachtedTransactions.size() * 2);
        StockTransactionEao stockTransactionEao = new StockTransactionEao(stockEm);
        StockTransactionEmo stockTransactionEmo = new StockTransactionEmo(stockEm);
        List<StockTransaction> transactions = new ArrayList<>();
        m.message("loading Transactions");
        for (StockTransaction detachedTransaction : detachtedTransactions) {
            transactions.add(stockTransactionEao.findById(detachedTransaction.getId()));
            m.worked(1);
        }
        m.setWorkRemaining(3);
        m.message("rolling in");
        List<StockUnit> stockUnits = stockTransactionEmo.completeRollIn(arranger, transactions);
        m.worked(2, "adding History");
        for (StockUnit stockUnit : stockUnits) {
            if ( mandator.applyDefaultChannelOnRollIn() ) {
                SalesChannel channel = stockUnit.getStock().getPrimaryChannel();
                channelChanger.fire(new SalesChannelChange.Builder().uniqueUnitId(stockUnit.getUniqueUnitId()).newChannel(channel).build());
                history.fire(UnitHistory.create(stockUnit.getUniqueUnitId(), "Rolled in " + stockUnit.getStock().getName() + " with " + channel.description, arranger));
            } else {
                history.fire(UnitHistory.create(stockUnit.getUniqueUnitId(), "Rolled in " + stockUnit.getStock().getName(), arranger));
            }
        }
        m.finish();
        return stockUnits.stream().map(x -> x.getId()).collect(Collectors.toList());
    }

    /**
     * Prepares the transfer of multiple units.
     * Creates an amount of needed transactions in the form,
     * - that the transactions are correct (all units of a transaction have the same source as the transaciton)
     * - that no transaction has more units than maxUnitSize.
     * <p/>
     * @param stockUnits the stockUnits to transfer
     * @param arranger   the arranger
     * @param comment    a optional comment
     * @return A map with uniqueUnitIds and comments for their history.
     * @throws UserInfoException
     */
    @Override
    public SortedMap<Integer, String> perpareTransfer(List<StockUnit> stockUnits, int destinationStockId, String arranger, String comment) throws UserInfoException {
        if ( stockUnits == null || stockUnits.isEmpty() ) return new TreeMap<>();
        return new StockTransactionEmo(stockEm).prepare(new Transfer.Builder()
                .destinationStockId(destinationStockId)
                .addAllStockUnitIds(stockUnits.stream().map(StockUnit::getId).collect(Collectors.toList()))
                .arranger(arranger)
                .comment(comment)
                .maxTransactionSize(10)
                .build(), monitorFactory.newSubMonitor("Umfuhr vorbereiten"));
    }

    /**
     * Remove the stockUnit represented by the refurbishId from a stock transaction, if that transaction exists and is in state prepared.
     * <p/>
     * @param refurbishId the refurbishId
     * @param arranger    the arranger
     * @param comment     a optional comment
     * @throws UserInfoException if no unit exists, the unit is not on a transaction or the transaction has another state then prepared.
     */
    @Override
    public void removeFromPreparedTransaction(final String refurbishId, final String arranger, final String comment) throws UserInfoException {
        StockUnit stockUnit = new StockUnitEao(stockEm).findByRefurbishId(refurbishId);
        if ( stockUnit == null ) throw new UserInfoException("SopoNr: " + refurbishId + " existiert nicht.");
        if ( !stockUnit.isInTransaction() ) throw new UserInfoException("SopoNr: " + refurbishId + " nicht in Transaction.");
        StockTransaction transaction = stockUnit.getTransaction();
        if ( transaction.getStatus().getType() != PREPARED ) {
            throw new UserInfoException("SopoNr: " + refurbishId + " auf Transaction, aber Transaction(" + transaction.getId() + ") not in Status prepared");
        }
        // The one case we remove the position as well, but dont need to set the stock because of beeing in status prepared
        StockTransactionPosition position = stockUnit.getPosition();
        stockEm.remove(position);
        transaction.setComment(transaction.getComment() + ", Unit " + stockUnit.getRefurbishId() + " removed by " + arranger + ", cause=" + comment);
        history.fire(UnitHistory.create(stockUnit.getUniqueUnitId(), "Unit returned to Stock(" + transaction.getSource().getId() + ") " + transaction.getSource().getName()
                + ", removed from Transaction, cause: " + comment, arranger));
        L.info("{} removed from {}", stockUnit, transaction);
    }

    /**
     * Cancels a stock transaction.
     * <p/>
     * @param transaction the transaction to cancel.
     * @param arranger    the arranger
     * @param comment     a comment to describe why
     * @throws UserInfoException if the transaction is not in state prepared.
     */
    @Override
    public void cancel(StockTransaction transaction, final String arranger, final String comment) throws UserInfoException {
        transaction = stockEm.find(StockTransaction.class, transaction.getId());
        if ( transaction.getStatus().getType() != PREPARED ) {
            throw new UserInfoException("Supplied transaction is not in state prepared, but " + transaction.getStatus() + ", cancel not allowed");
        }
        StockTransactionStatus status = new StockTransactionStatus(CANCELLED, new Date(), comment);
        status.addParticipation(new StockTransactionParticipation(ARRANGER, arranger));
        transaction.addStatus(status);
        for (StockUnit stockUnit : transaction.getUnits()) {
            history.fire(UnitHistory.create(stockUnit.getUniqueUnitId(), "Unit returned to Stock(" + transaction.getSource().getId() + ") "
                    + transaction.getSource().getName() + ", cancelled Transaction(" + transaction.getId() + ")", arranger));
            L.info("cancelTransaction(): Returning {} to Stock {} ", stockUnit, transaction.getSource());
            stockUnit.setPosition(null);
            // Nothing to do because of special case prepared transaction, which allows the unit to be on transaction and on stock
        }
    }

    /**
     * Bring a list of transactions from prepared into the state in transfer via commission.
     * <p/>
     * @param transactions the transaction to commission
     * @param picker       the pricker of units
     * @param deliverer    the transferer.
     * @throws RuntimeException if the transaction is in some form invalid.
     */
    // TODO: how about some validations.
    @Override
    public void commission(List<StockTransaction> transactions, String picker, String deliverer) throws RuntimeException {
        for (StockTransaction transaction : transactions) {
            L.info("Commissioning {}", transaction);
            Set<ConstraintViolation<StockTransaction>> violations = VALIDATOR.validate(transaction);
            if ( !violations.isEmpty() )
                throw new RuntimeException("Invalid StockTransaction in PRE-Validate: " + ValidationUtil.formatToSingleLine(violations));
            transaction = stockEm.find(StockTransaction.class, transaction.getId());
            Date before = transaction.addStatus(COMMISSIONED, PICKER, picker, DELIVERER, deliverer);
            transaction.addStatus(DateUtils.addSeconds(before, 1), IN_TRANSFER, DELIVERER, deliverer);
            violations = VALIDATOR.validate(transaction);
            if ( !violations.isEmpty() )
                throw new RuntimeException("Invalid StockTransaction in POST-Validate: " + ValidationUtil.formatToSingleLine(violations));
            for (StockUnit stockUnit : transaction.getUnits()) {
                stockUnit.setStock(null);
            }
        }
    }

    /**
     * Receive a list of transactions in the destination stock.
     * <p/>
     * @param transactions the transactions to receive.
     * @param deliverer    the deliverer
     * @param reciever     the receiver
     */
    @Override
    public void receive(List<StockTransaction> transactions, String deliverer, String reciever) {
        StockLocationDiscoverer discoverer = new StockLocationDiscoverer(stockEm);
        for (StockTransaction transaction : transactions) {
            transaction = stockEm.find(StockTransaction.class, transaction.getId());
            transaction.addStatus(RECEIVED, DELIVERER, deliverer, RECEIVER, reciever);
            L.info("Receiving {} in {}", transaction, transaction.getDestination());
            Stock destination = transaction.getDestination();
            for (StockUnit stockUnit : transaction.getUnits()) {
                stockUnit.setPosition(null);
                discoverer.discoverAndSetLocation(stockUnit, destination);
                history.fire(UnitHistory.create(stockUnit.getUniqueUnitId(), "Unit received in Stock(" + destination.getId() + ") " + destination.getName(), reciever));
            }
        }
    }
}
