package eu.ggnet.dwoss.stock;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.eao.StockTransactionEao;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.emo.StockTransactionEmo;

import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;
import eu.ggnet.dwoss.util.persistence.entity.Identifiable;

import eu.ggnet.dwoss.stock.entity.*;


/**
 * The StockAgent Implementation.
 * <p/>
 * @author oliver.guenther
 */
@Stateless
public class StockAgentBean extends AbstractAgentBean implements StockAgent {

    @Inject
    @Stocks
    private EntityManager em;

    @Inject
    private StockTransactionEmo stockTransactionEmo;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns a StockUnit identified by the uniqueUnitId, or null if not existent.
     * <p/>
     * @param uniqueUnitId the uniqueUnitId.
     * @return a StockUnit identified by the uniqueUnitId, or null if not existent.
     */
    @Override
    public StockUnit findStockUnitByUniqueUnitIdEager(Integer uniqueUnitId) {
        return optionalFetchEager(new StockUnitEao(em).findByUniqueUnitId(uniqueUnitId));
    }

    /**
     * Returns a StockUnit identified by the refurbishId, or null if not existent.
     * <p/>
     * @param refurbishId the refubishId.
     * @return a StockUnit identified by the refurbishId, or null if not existent.
     */
    @Override
    public StockUnit findStockUnitByRefurbishIdEager(String refurbishId) {
        return optionalFetchEager(new StockUnitEao(em).findByRefurbishId(refurbishId));
    }

    /**
     * Returns a List of StockTransactions, which match the supplied parameters.
     * <p/>
     * @param type       the type of the transaction
     * @param statusType the statusType of the transaction.
     * @return a List of StockTransactions, which match the supplied parameters.
     */
    @Override
    public List<StockTransaction> findStockTransactionEager(StockTransactionType type, StockTransactionStatusType statusType) {
        return optionalFetchEager(new StockTransactionEao(em).findByTypeAndStatus(type, statusType));
    }

    /**
     * Returns a List of StockTransactions, which match the supplied parameters.
     * <p/>
     * @param type       the type of the transaction
     * @param statusType the statusType of the transaction.
     * @param start      the start of the database result.
     * @param amount     the amount of the database result.
     * @return a List of StockTransactions, which match the supplied parameters.
     */
    @Override
    public List<StockTransaction> findStockTransactionEager(StockTransactionType type, StockTransactionStatusType statusType, int start, int amount) {
        return optionalFetchEager(new StockTransactionEao(em).findByTypeAndStatus(type, statusType, start, amount));
    }

    /**
     * Finds a List of StockUnits identified by the refurbishIds, which are able to be transfered.
     * <p/>
     * @param refurbishIds
     * @return the list of stockUnits.
     */
    @Override
    public List<StockUnit> findStockUnitsByRefurbishIdEager(List<String> refurbishIds) {
        return optionalFetchEager(new StockUnitEao(em).findByRefurbishIds(refurbishIds));
    }

    @Override
    public <T> T persist(T t) {
        if ( t == null ) throw new RuntimeException("T was Null in delete.");
        em.persist(t);
        return t;
    }

    @Override
    public <T> T merge(T t) {
        if ( t == null ) throw new RuntimeException("T was Null in delete.");
        em.merge(t);
        return t;
    }

    @Override
    public <T> void delete(T t) {
        if ( t == null ) throw new RuntimeException("T was Null in delete.");
        if ( t instanceof Identifiable ) {
            Identifiable id = (Identifiable)t;
            Identifiable find = em.find(id.getClass(), id.getId());
            em.remove(find);
        } else {
            em.remove(t);
        }
    }

    @Override
    public StockTransaction findOrCreateRollInTransaction(int stockId, String userName, String comment) {
        return stockTransactionEmo.requestRollInPrepared(stockId, userName, comment);
    }

}
