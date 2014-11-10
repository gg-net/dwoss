package eu.ggnet.dwoss.stock.eao;


import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransactionParticipation;
import eu.ggnet.dwoss.stock.entity.StockTransactionParticipationType;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatus;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatusType;
import eu.ggnet.dwoss.stock.entity.StockTransactionType;

import static org.junit.Assert.*;

public class StockTransactionEaoIT {

    private EntityManagerFactory emf;

    private EntityManager em;

    @Before
    public void before() {
        emf = Persistence.createEntityManagerFactory(StockPu.NAME, StockPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
    }

    @After
    public void after() {
        em.close();
        emf.close();
    }

    @Test
    public void testfind() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Stock s1 = new Stock(0,"TEEEEEEEST");
        Stock s2 = new Stock(1,"TEEEEEEEST");
        em.persist(s1);
        em.persist(s2);
        StockTransaction st1 = new StockTransaction(StockTransactionType.ROLL_IN);
        st1.setDestination(s1);
        st1.addStatus(new StockTransactionStatus(StockTransactionStatusType.PREPARED, new Date()));
        st1.addStatus(new StockTransactionStatus(StockTransactionStatusType.COMPLETED, new Date()));

        StockTransaction st2 = new StockTransaction(StockTransactionType.ROLL_IN);
        st2.setDestination(s1);
        st2.addStatus(new StockTransactionStatus(StockTransactionStatusType.PREPARED, new Date()));

        StockTransaction st3 = new StockTransaction(StockTransactionType.DESTROY);
        st3.setSource(s1);
        st3.addStatus(new StockTransactionStatus(StockTransactionStatusType.PREPARED, new Date()));

        StockTransaction st4 = new StockTransaction(StockTransactionType.ROLL_IN);
        st4.setDestination(s2);
        StockTransactionStatus status = new StockTransactionStatus(StockTransactionStatusType.PREPARED, new Date());
        status.addParticipation(new StockTransactionParticipation(StockTransactionParticipationType.PICKER, "Hugo"));
        st4.addStatus(status);
        st4.setComment("Muh");

        em.persist(st1);
        em.persist(st2);
        em.persist(st3);
        em.persist(st4);

        tx.commit();

        tx.begin();
        StockTransactionEao stockTransactionEao = new StockTransactionEao(em);
        List<StockTransaction> sts = stockTransactionEao.findByDestination(s1.getId(), StockTransactionType.ROLL_IN, StockTransactionStatusType.PREPARED);

        assertNotNull(sts);
        assertEquals(1, sts.size());
        assertEquals(st2.getId(), sts.get(0).getId());

        sts = stockTransactionEao.findByDestination(s2.getId(), StockTransactionType.ROLL_IN, StockTransactionStatusType.PREPARED, "Hugo", "Muh");
        assertNotNull(sts);
        assertEquals(1, sts.size());
        assertEquals(st4.getId(), sts.get(0).getId());

    }
}
