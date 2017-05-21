package eu.ggnet.dwoss.stock.itest;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.eao.StockTransactionEao;
import eu.ggnet.dwoss.stock.entity.*;
import eu.ggnet.dwoss.stock.itest.support.ArquillianProjectArchive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class StockTransactionEaoIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    @Stocks
    private EntityManager em;

    @Test
    public void testfind() throws Exception {
        utx.begin();
        em.joinTransaction();
        Stock s1 = new Stock(0, "TEEEEEEEST");
        Stock s2 = new Stock(1, "TEEEEEEEST");
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

        utx.commit();

        utx.begin();
        em.joinTransaction();
        StockTransactionEao stockTransactionEao = new StockTransactionEao(em);
        List<StockTransaction> sts = stockTransactionEao.findByDestination(s1.getId(), StockTransactionType.ROLL_IN, StockTransactionStatusType.PREPARED);

        assertNotNull(sts);
        assertEquals(1, sts.size());
        assertEquals(st2.getId(), sts.get(0).getId());

        sts = stockTransactionEao.findByDestination(s2.getId(), StockTransactionType.ROLL_IN, StockTransactionStatusType.PREPARED, "Hugo", "Muh");
        assertNotNull(sts);
        assertEquals(1, sts.size());
        assertEquals(st4.getId(), sts.get(0).getId());
        utx.commit();
    }
}
