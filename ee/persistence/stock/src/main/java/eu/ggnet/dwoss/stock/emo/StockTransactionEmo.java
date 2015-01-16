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
package eu.ggnet.dwoss.stock.emo;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.eao.StockTransactionEao;
import eu.ggnet.dwoss.stock.entity.*;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.persistence.eao.DefaultEao;
import eu.ggnet.dwoss.util.validation.ValidationUtil;
import eu.ggnet.saft.api.progress.IMonitor;

@Stateless
public class StockTransactionEmo {

    public static class LastCharSorter implements Comparator<String> {
        // Sorts by last Character

        @Override
        public int compare(String o1, String o2) {
            if ( o1 == null && o2 == null ) return 0;
            if ( o1 == null ) return 1;
            if ( o2 == null ) return -1;
            if ( o2.length() < 3 || o2.length() < 3 ) return o1.compareTo(o2);
            return o1.substring(o1.length() - 2).compareTo(o2.substring(o2.length() - 2));
        }
    }

    private final static Logger L = LoggerFactory.getLogger(StockTransactionEmo.class);

    @Inject
    @Stocks
    private EntityManager em;

    private final StockLocationDiscoverer discoverer;

    public StockTransactionEmo(EntityManager em) {
        this.em = em;
        this.discoverer = new StockLocationDiscoverer(em);
    }

    public StockTransactionEmo() {
        this.discoverer = new StockLocationDiscoverer(em);
    }

    /**
     * Prepares the transfer of multiple units.
     * Creates an amount of needed transactions in the form,
     * - that the transactions are correct (all units of a transaction have the same source as the transaciton)
     * - that no transaction has more units than maxUnitSize.
     * <p/>
     * @param t              a merged parameter view.
     * @param partialMonitor an optional monitor
     * @return a map containing uniqueUnitIds and comments for their history.
     * @throws UserInfoException
     */
    public SortedMap<Integer, String> prepare(Transfer t, IMonitor partialMonitor) throws UserInfoException {
        SubMonitor m = SubMonitor.convert(partialMonitor, "Preparing Transfer Transaciton", (t.getStockUnitIds().size() * 2) + 15);
        m.start();
        ValidationUtil.validate(t);

        Stock destination = em.find(Stock.class, t.getDestinationStockId());
        Stock source = null;

        List<StockUnit> unhandledUnits = new ArrayList<>();

        for (int unitId : t.getStockUnitIds()) {
            m.worked(1, "Loading StockUnit(" + unitId + ")");
            StockUnit stockUnit = em.find(StockUnit.class, unitId);
            if ( stockUnit == null ) throw new UserInfoException("StockUnit " + unitId + " nicht vorhanden.");
            if ( stockUnit.getStock() == null ) throw new UserInfoException(stockUnit + " nicht auf einem Lagerplatz.");
            if ( source == null ) source = stockUnit.getStock();
            if ( !source.equals(stockUnit.getStock()) ) throw new UserInfoException(stockUnit + " nicht auf Quelle " + source.getName() + ", wie alle anderen");
            unhandledUnits.add(stockUnit);
        }
        L.debug("Unhandeled units {}", unhandledUnits.stream().map(StockUnit::toSimple).collect(Collectors.joining(",")));

        SortedMap<Integer, String> result = new TreeMap<>();
        for (int i = 0; i < unhandledUnits.size(); i += t.getMaxTransactionSize()) {
            List<StockUnit> subList = unhandledUnits.subList(i, Math.min(unhandledUnits.size(), i + t.getMaxTransactionSize()));
            L.debug("Eplizit Transfer {}", subList.stream().map(StockUnit::toSimple).collect(Collectors.joining(",")));
            result.putAll(
                    prepareExplicitTransfer(
                            subList,
                            destination,
                            t.getArranger(),
                            t.getComment()
                    ));

            m.worked(t.getMaxTransactionSize());
        }
        m.message("committing");
        m.finish();
        return result;
    }

    private SortedMap<Integer, String> prepareExplicitTransfer(List<StockUnit> stockUnits, Stock destination, String arranger, String comment) {
        String error = preValidatePrepareTransfer(stockUnits, destination);
        if ( error != null ) throw new IllegalArgumentException(error);
        StockTransaction st = new StockTransaction(StockTransactionType.TRANSFER);
        st.setComment(comment);
        StockTransactionStatus status = new StockTransactionStatus(StockTransactionStatusType.PREPARED, new Date());
        status.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.ARRANGER, arranger));
        st.addStatus(status);
        st.setDestination(destination);
        em.persist(st);
        SortedMap<Integer, String> result = new TreeMap<>();
        for (StockUnit stockUnit : stockUnits) {
            if ( st.getSource() == null ) st.setSource(stockUnit.getStock());
            StockTransactionPosition stp = new StockTransactionPosition(stockUnit);
            st.addPosition(stp);
            em.persist(stp);
            // HINT: Don't use history.fire here untill we got rid of the .
            result.put(stockUnit.getUniqueUnitId(), "Prepared on TransferTransaction(id=" + st.getId() + ",source=" + st.getSource().getName()
                    + ",destination=" + st.getDestination().getName() + ") by " + arranger);
        }
        L.info("Created: {} with {}", st.toSimpleLine(), st.getPositions().stream().map(p -> p.getStockUnit().toSimple()).collect(Collectors.joining(",")));
        return result;
    }

    private String preValidatePrepareTransfer(List<StockUnit> stockUnits, Stock destination) {
        Stock source = null;
        for (StockUnit stockUnit : stockUnits) {
            if ( stockUnit.isInTransaction() )
                return "StockUnit(id=" + stockUnit.getId() + ",unitId=" + stockUnit + ") is allready on Transaction(" + stockUnit.getTransaction().getId() + ")";
            if ( source == null ) {
                source = stockUnit.getStock();
                if ( source.equals(destination) ) return "source=Stock(id=" + source.getId() + ",name=" + source.getName() + ") is equal to destination";
            }
            if ( !stockUnit.getStock().equals(source) )
                return "StockUnit(id=" + stockUnit.getId() + ",unitId=" + stockUnit + ") is on Stock(id=" + stockUnit.getStock().getId() + ",name=" + stockUnit.getStock().getName() + ")"
                        + " but source=Stock(id=" + source.getId() + ",name=" + source.getName() + ")";
        }
        return null;
    }

    public StockTransaction requestDestroyPrepared(int sourceId, String arrangerName, String comment) {
        return request(StockTransactionType.DESTROY, StockTransactionStatusType.PREPARED, sourceId, null, arrangerName, comment);
    }

    public StockTransaction requestRollInPrepared(int destinationId, String arrangerName, String comment) {
        return request(StockTransactionType.ROLL_IN, StockTransactionStatusType.PREPARED, null, destinationId, arrangerName, comment);
    }

    public StockTransaction requestRollOutPrepared(int sourceId, String arrangerName, String comment) {
        return request(StockTransactionType.ROLL_OUT, StockTransactionStatusType.PREPARED, sourceId, null, arrangerName, comment);
    }

    public StockTransaction requestExternalTransferPrepare(int sourceId, int destinationId, String arrangerName, String comment) {
        return request(StockTransactionType.EXTERNAL_TRANSFER, StockTransactionStatusType.PREPARED, sourceId, destinationId, arrangerName, comment);
    }

    /**
     * Tries to bring the Transactions(ROLL_IN) into Status Complete, or deletes it if its empty.
     * <p/>
     * @param arrangerName the arranger
     * @param transactions the transactions
     * @return List of StockUnits, which are now in stock
     */
    public List<StockUnit> completeRollIn(String arrangerName, List<StockTransaction> transactions) {
        validate(StockTransactionType.ROLL_IN, StockTransactionStatusType.PREPARED, transactions);
        List<StockUnit> result = new ArrayList<>();
        for (StockTransaction transaction : transactions) {
            if ( transaction.getPositions().isEmpty() ) {
//                L.info("Removing Empty {}", transaction);
//                transaction.setSource(null);
//                transaction.setDestination(null);
//                em.remove(transaction);
                L.info("Ignoring Empty {}", transaction);
                // TODO: Delete doesn' work, see http://overload.ahrensburg.gg-net.de/jira/browse/DW-1344
            } else {
                // TODO: If the addStatus succedes, but the hole transaciont fails and is rolled back, the addStatus is keept. Meaning the rollback is not successful completely.
                StockTransactionStatus status = new StockTransactionStatus(StockTransactionStatusType.COMPLETED, new Date());
                status.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.ARRANGER, arrangerName));
                transaction.addStatus(status);
                for (StockTransactionPosition position : transaction.getPositions()) {
                    StockUnit stockUnit = position.getStockUnit();
                    L.debug("RollingIn StockUnit={} of Transaction(id={})", stockUnit, transaction.getId());
                    position.setStockUnit(null);
                    discoverer.discoverAndSetLocation(stockUnit, transaction.getDestination());
                    if ( stockUnit != null ) result.add(stockUnit);
                }
            }
        }
        return result;
    }

    /**
     * Completes the External Transfer to the Destination.
     *
     * @param arrangerName the arranger.
     * @param transactions the transactions
     * @return a list of StockUnits which are now on a new Stock.
     */
    // TODO: Test
    public List<StockUnit> completeExternalTransfer(String arrangerName, Collection<StockTransaction> transactions) {
        validate(StockTransactionType.EXTERNAL_TRANSFER, StockTransactionStatusType.PREPARED, transactions);
        List<StockUnit> result = new ArrayList<>();
        for (StockTransaction st : transactions) {
            StockTransactionStatus status = new StockTransactionStatus(StockTransactionStatusType.COMPLETED, new Date());
            status.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.ARRANGER, arrangerName));
            st.addStatus(status);
            for (StockUnit stockUnit : st.getUnits()) {
                result.add(stockUnit);
                stockUnit.setPosition(null);
                discoverer.discoverAndSetLocation(stockUnit, st.getDestination());
            }
        }
        return result;
    }

    /**
     * Tries to bring a StockTransaction(DESTROY) in to status complete.
     * This will also remove all units for the persistence layer.
     *
     * @param arrangerName the arranger
     * @param transactions the transactions.
     * @return the uniqueUnitIds of the destroyed Units
     */
    public List<Integer> completeDestroy(String arrangerName, List<StockTransaction> transactions) {
        validate(StockTransactionType.DESTROY, StockTransactionStatusType.PREPARED, transactions);
        return completeRollOutDestroy(arrangerName, transactions);
    }

    /**
     * Tries to bring a StockTransaction(ROLL_OUT) in to status complete.
     * This will also remove all units for the persistence layer.
     *
     * @param arrangerName the arranger
     * @param transactions the transactions.
     * @return the uniqueUnitIds of the rolled out Units
     */
    public List<Integer> completeRollOut(String arrangerName, List<StockTransaction> transactions) {
        validate(StockTransactionType.ROLL_OUT, StockTransactionStatusType.PREPARED, transactions);
        return completeRollOutDestroy(arrangerName, transactions);
    }

    /**
     * Validates if all supplied transactions have the selected type and status.
     *
     * @param transactionType the type to validate
     * @param statusType      the status to validate
     * @param transactions    the selected transactions
     */
    private void validate(StockTransactionType transactionType, StockTransactionStatusType statusType, Collection<StockTransaction> transactions) {
        if ( transactions == null ) throw new RuntimeException("No Transaction supplied");
        for (StockTransaction stockTransaction : transactions) {
            if ( stockTransaction.getType() != transactionType )
                throw new IllegalArgumentException("Transaction not of type " + transactionType + ", " + stockTransaction);
            if ( stockTransaction.getStatus().getType() != statusType )
                throw new IllegalArgumentException("Transaction not in status " + statusType + ", " + stockTransaction);
        }
    }

    /**
     * Tries to bring the Transactions(ROLL_OUT or DESTROY) into Status complete and removes all attached Units from the stock.
     * Also removes LogicTransactions if they have become empty.
     * <p/>
     * @param arrangerName the arranger
     * @param transactions the transactions
     * @return List of UniqueUnitIds which where assosiated with the removed stockUnits
     */
    private List<Integer> completeRollOutDestroy(String arrangerName, Collection<StockTransaction> transactions) {
        List<Integer> result = new ArrayList<>();
        for (StockTransaction transaction : transactions) {
            for (StockUnit stockUnit : transaction.getUnits()) {
                stockUnit.setPosition(null);
                stockUnit.setStock(null);
                LogicTransaction lt = stockUnit.getLogicTransaction();
                if ( lt != null ) lt.remove(stockUnit);
                result.add(stockUnit.getUniqueUnitId());
                em.remove(stockUnit);
                if ( lt != null && lt.getUnits().isEmpty() ) em.remove(lt);
            }
            StockTransactionStatus status = new StockTransactionStatus(StockTransactionStatusType.COMPLETED, new Date());
            status.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.ARRANGER, arrangerName));
            transaction.addStatus(status);
        }
        return result;
    }

    /**
     * Request a StockTransaction with the selected parameters.
     *
     * @param transactionType the type
     * @param statusType      the status
     * @param sourceId        the source, if null only the destination is used.
     * @param destinationId   the destination, if null only the source is used
     * @param arrangerName    the arranger
     * @param comment         the comment
     * @return always a persisted transactions with the supplied parameters
     */
    // TODO: Still not implemented to findByTypeAndStatus a transaction with source an destination. (For Transfer)
    private StockTransaction request(StockTransactionType transactionType,
                                     StockTransactionStatusType statusType,
                                     Integer sourceId, Integer destinationId,
                                     String arrangerName, String comment) {
        StockTransactionEao stockTransactionEao = new StockTransactionEao(em);
        List<StockTransaction> stockTransactions;
        if ( sourceId != null ) stockTransactions = stockTransactionEao.findBySource(sourceId, transactionType, statusType, arrangerName, comment);
        else stockTransactions = stockTransactionEao.findByDestination(destinationId, transactionType, statusType, arrangerName, comment);

        if ( !stockTransactions.isEmpty() ) return stockTransactions.get(0);

        DefaultEao<Stock> stockEao = new DefaultEao<>(Stock.class, em);
        StockTransaction st = new StockTransaction(transactionType);
        st.setComment(comment);
        StockTransactionStatus status = new StockTransactionStatus(statusType, new Date());
        status.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.ARRANGER, arrangerName));
        st.addStatus(status);
        if ( sourceId != null ) st.setSource(stockEao.findById(sourceId));
        if ( destinationId != null ) st.setDestination(stockEao.findById(destinationId));
        em.persist(st);
        return st;
    }
}
