package eu.ggnet.dwoss.stock.entity;


import java.util.*;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.stock.assist.StockPu;

import static org.junit.Assert.assertTrue;

public class PersistenceIT {

    private EntityManagerFactory emf;

    @Before
    public void setUp() throws NamingException {
        emf = Persistence.createEntityManagerFactory(StockPu.NAME, StockPu.JPA_IN_MEMORY);
    }

    @After
    public void after() {
        emf.close();
    }

    @Test
    public void testUniqueUnitReferenceBug() {
        EntityManager em = emf.createEntityManager();

        Date now = new Date();

        em.getTransaction().begin();
        Stock s1 = new Stock(1);
        s1.setName("1111111111111111");
        Stock s2 = new Stock(2);
        s2.setName("2222222222222222");
        em.persist(s1);
        em.persist(s2);
        em.getTransaction().commit();

        StockLocation s1l1 = new StockLocation("Regal A");
        StockLocation s1l2 = new StockLocation("Regal B");
        s1.addStockLocation(s1l1);
        s1.addStockLocation(s1l2);
        StockUnit su1 = new StockUnit("Gerät", 1);
        su1.setStockLocation(s1l2);

        em.getTransaction().begin();
        StockTransaction t1 = new StockTransaction(StockTransactionType.TRANSFER);
        t1.addStatus(new StockTransactionStatus(StockTransactionStatusType.PREPARED, now));
        t1.setSource(s1);
        t1.setDestination(s2);
        t1.addPosition(new StockTransactionPosition(su1));
        su1.setStock(null);
        em.persist(t1);
        em.getTransaction().commit();

        em.getTransaction().begin();
        for (StockTransactionPosition position : t1.getPositions()) {
            t1.getDestination().addUnit(position.getStockUnit());
            position.setStockUnit(null);
        }
        em.getTransaction().commit();

        em.getTransaction().begin();
        t1 = new StockTransaction(StockTransactionType.TRANSFER);
        t1.addStatus(new StockTransactionStatus(StockTransactionStatusType.PREPARED, now));
        t1.setSource(s2);
        t1.setDestination(s1);
        t1.addPosition(new StockTransactionPosition(su1));
        su1.setStock(null);
        em.persist(t1);
        em.getTransaction().commit();

        em.getTransaction().begin();
        for (StockTransactionPosition position : t1.getPositions()) {
            t1.getDestination().addUnit(position.getStockUnit());
            position.setStockUnit(null);
        }
        em.getTransaction().commit();

    }

    @Test
    public void testPersistence() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Stock s1 = new Stock(1);
        s1.setName("1111111111111111");
        Stock s2 = new Stock(2);
        s2.setName("2222222222222222");
        em.persist(s1);
        tx.commit();

        StockUnit su1 = new StockUnit("g1", 1);
        StockUnit su2 = new StockUnit("g2", 2);
        StockUnit su3 = new StockUnit("g3", 3);
        StockUnit su4 = new StockUnit("g4", 4);

        s1.addUnit(su1);
        s1.addUnit(su2);
        s1.addUnit(su3);
        s2.addUnit(su4);

        tx = em.getTransaction();
        tx.begin();
        em.persist(su1);
        em.persist(su2);
        em.persist(su3);
        em.persist(su4);
        tx.commit();

        tx = em.getTransaction();
        tx.begin();

        TypedQuery<Stock> q = em.createQuery("Select s from " + Stock.class.getSimpleName() + " s", Stock.class);
        List<Stock> sus = q.getResultList();
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

        tx.commit();
        tx = em.getTransaction();
        tx.begin();

        StockTransactionStatus transfer = new StockTransactionStatus(StockTransactionStatusType.IN_TRANSFER, new Date());
        transfer.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.DELIVERER, "User3", false));
        t1.addStatus(transfer);

        StockTransactionPosition removePos = t1.getPositions().get(0);
        removePos.getStockUnit().setStock(st2);

        em.remove(removePos);

        tx.commit();
        tx = em.getTransaction();
        tx.begin();

        StockTransactionStatus receive = new StockTransactionStatus(StockTransactionStatusType.RECEIVED, new Date());
        receive.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.RECEIVER, "User4", true));
        receive.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.DELIVERER, "User5", true));
        t1.addStatus(receive);

        tx.commit();
        tx = em.getTransaction();
        tx.begin();

        StockTransaction t2 = em.createQuery("Select t from " + StockTransaction.class.getSimpleName() + " t", StockTransaction.class).getSingleResult();
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

        tx.commit();
        tx = em.getTransaction();
        tx.begin();

        for (StockTransactionPosition position : t1.getPositions()) {
            t1.getDestination().addUnit(position.getStockUnit());
            position.setStockUnit(null);
        }

        tx.commit();

        tx.begin();

        List<StockUnit> units = em.createNamedQuery("all", StockUnit.class).getResultList();
        LogicTransaction lt = new LogicTransaction();
        lt.setDossierId(2);
        for (StockUnit stockUnit : units) {
            lt.add(stockUnit);
        }
        em.persist(lt);

        tx.commit();

    }
}
