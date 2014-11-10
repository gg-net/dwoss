package eu.ggnet.dwoss.stock.eao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransactionParticipation;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatusType;
import eu.ggnet.dwoss.stock.entity.StockTransactionType;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

public class StockTransactionEao extends AbstractEao<StockTransaction> {

    private EntityManager em;

    public StockTransactionEao(EntityManager em) {
        super(StockTransaction.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns a List of Transactions matching all supplied parameters
     * <p/>
     * @param sourceId        the destination of the stock
     * @param type            the type of the transaction
     * @param statusType      the status the transaction is in
     * @param participantName the arrangerName
     * @param comment         the comment
     * @return a List of Transactions
     */
    public List<StockTransaction> findBySource(int sourceId, StockTransactionType type, StockTransactionStatusType statusType, String participantName, String comment) {
        TypedQuery<StockTransaction> query = em.createNamedQuery("StockTransaction.bySourceTypesComment", StockTransaction.class);
        query.setParameter(1, sourceId);
        query.setParameter(2, type);
        query.setParameter(3, statusType);
        query.setParameter(4, comment);
        List<StockTransaction> queryResult = query.getResultList();
        if ( participantName == null ) return queryResult;
        Set<StockTransaction> result = new HashSet<>();
        for (StockTransaction stockTransaction : queryResult) {
            for (StockTransactionParticipation participation : stockTransaction.getParticipations()) {
                if ( participantName.equals(participation.getPraticipantName()) ) result.add(stockTransaction);
            }
        }
        return new ArrayList<>(result);
    }

    /**
     * Returns a List of Transactions matching all supplied parameters
     * <p/>
     * @param destinationId   the destination of the stock
     * @param type            the type of the transaction
     * @param statusType      the status the transaction is in
     * @param participantName the arrangerName
     * @param comment         the comment
     * @return a List of Transactions
     */
    public List<StockTransaction> findByDestination(int destinationId, StockTransactionType type, StockTransactionStatusType statusType, String participantName, String comment) {
        TypedQuery<StockTransaction> query = em.createNamedQuery("StockTransaction.byDestinationTypesComment", StockTransaction.class);
        query.setParameter(1, destinationId);
        query.setParameter(2, type);
        query.setParameter(3, statusType);
        query.setParameter(4, comment);
        List<StockTransaction> queryResult = query.getResultList();
        if ( participantName == null ) return queryResult;
        Set<StockTransaction> result = new HashSet<>();
        for (StockTransaction stockTransaction : queryResult) {
            for (StockTransactionParticipation participation : stockTransaction.getParticipations()) {
                if ( participantName.equals(participation.getPraticipantName()) ) result.add(stockTransaction);
            }
        }
        return new ArrayList<>(result);
    }

    public List<StockTransaction> findByDestination(int destinationId, StockTransactionType type, StockTransactionStatusType statusType) {
        TypedQuery<StockTransaction> query = em.createNamedQuery("StockTransaction.byDestinationTypes", StockTransaction.class);
        query.setParameter(1, destinationId);
        query.setParameter(2, type);
        query.setParameter(3, statusType);
        return query.getResultList();
    }

    public List<StockTransaction> findByTypeAndStatus(StockTransactionType type, StockTransactionStatusType statusType) {
        return em.createNamedQuery("StockTransaction.byTypeAndStatus", StockTransaction.class).setParameter(1, type).setParameter(2, statusType).getResultList();
    }

    public List<StockTransaction> findByTypeAndStatus(StockTransactionType type, StockTransactionStatusType statusType, int start, int amount) {
        return em.createNamedQuery("StockTransaction.byTypeAndStatus", StockTransaction.class)
                .setParameter(1, type).setParameter(2, statusType).setFirstResult(start).setMaxResults(amount).getResultList();
    }
}
