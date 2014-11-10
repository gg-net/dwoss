package eu.ggnet.dwoss.stock;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.util.persistence.RemoteAgent;
import eu.ggnet.dwoss.stock.entity.*;


/**
 * The Stock Agent
 * <p/>
 * @author oliver.guenther
 */
@Remote
@Local
public interface StockAgent extends RemoteAgent {

    /**
     * Returns a StockUnit identified by the uniqueUnitId, or null if not existent.
     * <p/>
     * @param uniqueUnitId the uniqueUnitId.
     * @return a StockUnit identified by the uniqueUnitId, or null if not existent.
     */
    StockUnit findStockUnitByUniqueUnitIdEager(Integer uniqueUnitId);

    /**
     * Returns a StockUnit identified by the refurbishId, or null if not existent.
     * <p/>
     * @param refurbishId the refubishId.
     * @return a StockUnit identified by the refurbishId, or null if not existent.
     */
    StockUnit findStockUnitByRefurbishIdEager(String refurbishId);

    /**
     * Returns a List of StockTransactions, which match the supplied parameters.
     * <p/>
     * @param type       the type of the transaction
     * @param statusType the statusType of the transaction.
     * @return a List of StockTransactions, which match the supplied parameters.
     */
    List<StockTransaction> findStockTransactionEager(StockTransactionType type, StockTransactionStatusType statusType);

    /**
     * Returns a List of StockTransactions, which match the supplied parameters.
     * <p/>
     * @param type       the type of the transaction
     * @param statusType the statusType of the transaction.
     * @param start      the start of the database result.
     * @param amount     the amount of the database result.
     * @return a List of StockTransactions, which match the supplied parameters.
     */
    List<StockTransaction> findStockTransactionEager(StockTransactionType type, StockTransactionStatusType statusType, int start, int amount);

    /**
     * Finds a List of StockUnits identified by the refurbishIds, which are able to be transfered.
     * <p/>
     * @param refurbishIds
     * @return the list of stockUnits.
     */
    List<StockUnit> findStockUnitsByRefurbishIdEager(List<String> refurbishIds);

    /**
     * Persist a T Instance and return it then.
     * <p>
     * @param <T>
     * @param t   the instance that will be persisted.
     * @return the persisted instance.
     */
    <T> T persist(T t);

    /**
     * Merge a T Instance and return it then.
     * <p>
     * @param <T>
     * @param t   the instance that will be merged.
     * @return the merged instance.
     */
    <T> T merge(T t);

    /**
     * Delete a T Instance.
     * <p>
     * @param t the instance that will be deleted.
     */
    <T> void delete(T t);

    StockTransaction findOrCreateRollInTransaction(int stockId, String userName, String comment);
}
