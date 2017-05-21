package eu.ggnet.dwoss.stock.itest;

import java.util.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.entity.*;
import eu.ggnet.dwoss.stock.itest.support.ArquillianProjectArchive;

import com.mysema.query.SearchResults;
import com.mysema.query.jpa.impl.JPAQueryFactory;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class PersistenceIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    @Stocks
    private EntityManager em;

    @After
    public void clearDataBase() throws Exception {
        utx.begin();
        em.joinTransaction();
        em.createNativeQuery("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK").executeUpdate();
        utx.commit();
    }

    @Test
    public void testUniqueUnitReferenceBug() throws Exception {

        Date now = new Date();

        utx.begin();
        em.joinTransaction();
        Stock s1 = new Stock(1);
        s1.setName("1111111111111111");
        Stock s2 = new Stock(2);
        s2.setName("2222222222222222");
        em.persist(s1);
        em.persist(s2);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        s1 = em.find(Stock.class, s1.getId());

        StockLocation s1l1 = new StockLocation("Regal A");
        StockLocation s1l2 = new StockLocation("Regal B");
        s1.addStockLocation(s1l1);
        s1.addStockLocation(s1l2);
        StockUnit su1 = new StockUnit("Gerät", 1);
        su1.setStockLocation(s1l2);

        utx.commit();
        utx.begin();
        em.joinTransaction();
        s1 = em.find(Stock.class, s1.getId());
        s2 = em.find(Stock.class, s1.getId());

        StockTransaction t1 = new StockTransaction(StockTransactionType.TRANSFER);
        t1.addStatus(new StockTransactionStatus(StockTransactionStatusType.PREPARED, now));
        t1.setSource(s1);
        t1.setDestination(s2);
        t1.addPosition(new StockTransactionPosition(su1));
        su1.setStock(null);
        em.persist(t1);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        t1 = em.find(StockTransaction.class, t1.getId());
        for (StockTransactionPosition position : t1.getPositions()) {
            t1.getDestination().addUnit(position.getStockUnit());
            position.setStockUnit(null);
        }
        utx.commit();

        utx.begin();
        em.joinTransaction();
        s1 = em.find(Stock.class, s1.getId());
        s2 = em.find(Stock.class, s1.getId());
        t1 = new StockTransaction(StockTransactionType.TRANSFER);
        t1.addStatus(new StockTransactionStatus(StockTransactionStatusType.PREPARED, now));
        t1.setSource(s2);
        t1.setDestination(s1);
        t1.addPosition(new StockTransactionPosition(su1));
        su1.setStock(null);
        em.persist(t1);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        t1 = em.find(StockTransaction.class, t1.getId());
        for (StockTransactionPosition position : t1.getPositions()) {
            t1.getDestination().addUnit(position.getStockUnit());
            position.setStockUnit(null);
        }
        utx.commit();

    }

    @Test
    public void testPersistence() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(() -> em);

        utx.begin();
        em.joinTransaction();
        Stock s1 = new Stock(1);
        s1.setName("1111111111111111");
        Stock s2 = new Stock(2);
        s2.setName("2222222222222222");
        em.persist(s1);
        utx.commit();

        utx.begin();
        em.joinTransaction();

        s1 = em.merge(s1);
        s2 = em.merge(s2);

        StockUnit su1 = new StockUnit("g1", 1);
        StockUnit su2 = new StockUnit("g2", 2);
        StockUnit su3 = new StockUnit("g3", 3);
        StockUnit su4 = new StockUnit("g4", 4);

        s1.addUnit(su1);
        s1.addUnit(su2);
        s1.addUnit(su3);
        s2.addUnit(su4);

        em.persist(su1);
        em.persist(su2);
        em.persist(su3);
        em.persist(su4);
        utx.commit();

        utx.begin();
        em.joinTransaction();

        List<Stock> sus = queryFactory.query().from(QStock.stock).list(QStock.stock);

        assertTrue(sus.size() == 2);
        Stock st1 = sus.get(0);
        if ( st1.getId() == s2.getId() ) {
            assertTrue(st1.getUnits().size() == 1);
        } else {
            assertTrue(st1.getUnits().size() == 3);
        }
        Stock st2 = sus.get(1);

        StockTransaction t1 = new StockTransaction(StockTransactionType.TRANSFER);
        // TODO: Long comment fails test.
        StringBuilder sb = new StringBuilder("Adding a");
        for (int i = 0; i < 255; i++) {
            sb.append(" very");
        }
        sb.append("long comment");
        t1.setComment(sb.toString());

        t1.setSource(st1);
        t1.setDestination(st2);
        List<StockUnit> sustemp = new ArrayList<>(st1.getUnits());
        for (Iterator<StockUnit> isu = sustemp.iterator(); isu.hasNext();) {
            StockUnit su = isu.next();
            t1.addPosition(new StockTransactionPosition(su));
            su.setStock(null);
        }

        StockTransactionStatus init = new StockTransactionStatus(StockTransactionStatusType.PREPARED, new Date());
        init.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.PICKER, "Hans"));
        t1.addStatus(init);

        StockTransactionStatus commision = new StockTransactionStatus(StockTransactionStatusType.COMMISSIONED, new Date());
        commision.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.PICKER, "User1", true));
        commision.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.DELIVERER, "User2", true));
        t1.addStatus(commision);
        em.persist(t1);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        t1 = em.find(StockTransaction.class, t1.getId());
        st2 = em.find(Stock.class, st2.getId());

        StockTransactionStatus transfer = new StockTransactionStatus(StockTransactionStatusType.IN_TRANSFER, new Date());
        transfer.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.DELIVERER, "User3", false));
        t1.addStatus(transfer);

        StockTransactionPosition removePos = t1.getPositions().get(0);
        removePos.getStockUnit().setStock(st2);

        em.remove(removePos);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        t1 = em.find(StockTransaction.class, t1.getId());

        StockTransactionStatus receive = new StockTransactionStatus(StockTransactionStatusType.RECEIVED, new Date());
        receive.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.RECEIVER, "User4", true));
        receive.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.DELIVERER, "User5", true));
        t1.addStatus(receive);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        SearchResults<StockTransaction> tResult = queryFactory.from(QStockTransaction.stockTransaction).listResults(QStockTransaction.stockTransaction);
        assertThat(tResult.getTotal()).isEqualTo(1);
        StockTransaction t2 = tResult.getResults().get(0);

        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;

        for (StockTransactionParticipation stp : t2.getParticipations()) {
            switch (stp.getType()) {
                case DELIVERER:
                    b1 = true;
                    break;
                case PICKER:
                    b2 = true;
                    break;
                case RECEIVER:
                    b3 = true;
            }
        }

        assertTrue(b1 && b2 && b3);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        t1 = em.find(StockTransaction.class, t1.getId());

        for (StockTransactionPosition position : t1.getPositions()) {
            t1.getDestination().addUnit(position.getStockUnit());
            position.setStockUnit(null);
        }

        utx.commit();

        utx.begin();
        em.joinTransaction();

        List<StockUnit> units = em.createNamedQuery("all", StockUnit.class).getResultList();
        LogicTransaction lt = new LogicTransaction();
        lt.setDossierId(2);
        for (StockUnit stockUnit : units) {
            lt.add(stockUnit);
        }
        em.persist(lt);

        utx.commit();

    }
}
