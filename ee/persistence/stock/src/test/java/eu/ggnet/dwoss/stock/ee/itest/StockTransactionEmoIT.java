package eu.ggnet.dwoss.stock.ee.itest;

import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.stock.ee.eao.StockTransactionEao;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;
import eu.ggnet.dwoss.stock.ee.emo.Transfer;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.stock.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.util.Utils;

import static eu.ggnet.dwoss.stock.ee.entity.StockTransactionStatusType.COMPLETED;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class StockTransactionEmoIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    @Stocks
    private EntityManager em;

    @Inject
    private StockGeneratorOperation gen;

    @After
    public void clearDataBase() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    @Test
    public void testRequestDestroyPrepared() throws Exception {
        List<Stock> stocks = gen.makeStocksAndLocations(2);

        StockTransactionEmo stockTransactionEmo = new StockTransactionEmo(em);

        utx.begin();
        em.joinTransaction();
        StockTransaction st1 = stockTransactionEmo.requestDestroyPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        assertNotNull(st1);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        StockTransaction st2 = stockTransactionEmo.requestDestroyPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        assertNotNull(st2);
        assertEquals(st1.getId(), st2.getId());

        st2.addStatus(new StockTransactionStatus(COMPLETED, new Date()));

        utx.commit();

        utx.begin();
        em.joinTransaction();
        StockTransaction st3 = stockTransactionEmo.requestDestroyPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        assertNotNull(st3);
        assertFalse(st1.getId() == st3.getId());
        utx.commit();
    }

    @Test
    public void testRequestRollInPrepared() throws Exception {
        List<Stock> stocks = gen.makeStocksAndLocations(2);

        StockTransactionEmo stockTransactionEmo = new StockTransactionEmo(em);

        utx.begin();
        em.joinTransaction();
        StockTransaction st1 = stockTransactionEmo.requestRollInPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        assertNotNull(st1);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        StockTransaction st2 = stockTransactionEmo.requestRollInPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        assertNotNull(st2);
        assertEquals(st1.getId(), st2.getId());

        st2.addStatus(new StockTransactionStatus(COMPLETED, new Date()));

        utx.commit();

        utx.begin();
        em.joinTransaction();
        StockTransaction st3 = stockTransactionEmo.requestRollInPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        assertNotNull(st3);
        assertFalse(st1.getId() == st3.getId());
        utx.commit();
    }

    @Test
    public void testCompleteRollInRollOut() throws Exception {
        List<Stock> stocks = gen.makeStocksAndLocations(2);

        StockTransactionEmo stockTransactionEmo = new StockTransactionEmo(em);

        utx.begin();
        em.joinTransaction();
        StockTransaction st1 = stockTransactionEmo.requestRollInPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        st1.addUnit(new StockUnit("1", 1));
        st1.addUnit(new StockUnit("2", 2));
        st1.addUnit(new StockUnit("3", 3));
        st1.addUnit(new StockUnit("4", 4));
        st1.addUnit(new StockUnit("5", 5));
        st1.addUnit(new StockUnit("6", 6));
        st1.addUnit(new StockUnit("7", 7));
        st1.addUnit(new StockUnit("8", 8));
        st1.addUnit(new StockUnit("9", 9));

        List<StockUnit> units = stockTransactionEmo.completeRollIn("Hans", Arrays.asList(st1));
        assertEquals(COMPLETED, st1.getStatus().getType());

        assertNotNull(units);
        assertEquals(st1.getPositions().size(), units.size());

        for (StockUnit stockUnit : units) {
            assertTrue(stockUnit.isInStock());
            assertFalse(stockUnit.isInTransaction());
            assertEquals(stocks.get(0), stockUnit.getStock());
        }
        utx.commit();

        StockUnitEao stockUnitEao = new StockUnitEao(em);

        utx.begin();
        em.joinTransaction();
        st1 = stockTransactionEmo.requestRollOutPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        for (StockUnit stockUnit : stockUnitEao.findAll()) {
            st1.addUnit(stockUnit);
        }

        assertEquals(units.size(), st1.getPositions().size());
        utx.commit();

        utx.begin();
        em.joinTransaction();
        st1 = em.merge(st1);
        List<Integer> uids = stockTransactionEmo.completeRollOut("Horst", Arrays.asList(st1));
        assertNotNull(uids);
        assertEquals(units.size(), uids.size());
        utx.commit();

        utx.begin();
        em.joinTransaction();
        List<StockUnit> stockUnits = stockUnitEao.findAll();
        assertTrue(stockUnits.isEmpty());
        utx.commit();
    }

    @Test
    public void testCompleteDestroy() throws Exception {
        List<Stock> stocks = gen.makeStocksAndLocations(2);

        StockTransactionEmo stockTransactionEmo = new StockTransactionEmo(em);
        StockUnitEao stockUnitEao = new StockUnitEao(em);

        utx.begin();
        em.joinTransaction();
        Stock s1 = em.find(Stock.class, stocks.get(0).getId());
        for (int i = 1; i <= 10; i++) {
            StockUnit su = new StockUnit(Integer.toString(i), i);
            su.setStock(s1);
            em.persist(su);
        }

        List<StockUnit> units = stockUnitEao.findAll();

        assertNotNull(units);

        for (StockUnit stockUnit : units) {
            assertTrue(stockUnit.isInStock());
            assertFalse(stockUnit.isInTransaction());
            assertEquals(s1, stockUnit.getStock());
        }
        utx.commit();

        utx.begin();
        em.joinTransaction();
        StockTransaction st1 = stockTransactionEmo.requestDestroyPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        for (StockUnit stockUnit : stockUnitEao.findAll()) {
            st1.addUnit(stockUnit);
        }

        assertEquals(units.size(), st1.getPositions().size());
        List<Integer> uids = stockTransactionEmo.completeDestroy("Horst", Arrays.asList(st1));
        assertNotNull(uids);
        assertEquals(units.size(), uids.size());
        utx.commit();

        utx.begin();
        em.joinTransaction();
        List<StockUnit> stockUnits = stockUnitEao.findAll();
        assertTrue(stockUnits.isEmpty());
        utx.commit();
    }

    @Test
    public void testPrepare() throws Exception {
        List<Stock> stocks = gen.makeStocksAndLocations(2);

        int transactionSize = 5;

        utx.begin();
        em.joinTransaction();
        Stock s1 = em.find(Stock.class, stocks.get(0).getId());
        for (int i = 1; i <= 15; i++) {
            StockUnit su = new StockUnit(Integer.toString(i), i);
            su.setStock(s1);
            em.persist(su);
        }
        utx.commit();
        utx.begin();
        em.joinTransaction();

        List<Integer> units = new StockUnitEao(em).findAll().stream().map(StockUnit::getId).collect(Collectors.toList());
        assertEquals("Assert 15 persisted Stockunits", 15, units.size());

        //prepare
        new StockTransactionEmo(em).prepare(new Transfer.Builder()
                .destinationStockId(stocks.get(1).getId())
                .addAllStockUnitIds(units)
                .arranger("UnitTest")
                .comment("Test prepare")
                .maxTransactionSize(transactionSize)
                .build(), null);
        utx.commit();
        utx.begin();
        em.joinTransaction();

        StockTransactionEao stEao = new StockTransactionEao(em);
        List<StockTransaction> findByDestination = stEao.findAll();
        assertEquals("Assert three transactions", 3, findByDestination.size());

        for (StockTransaction transaction : findByDestination) {
            List<StockTransactionPosition> positions = transaction.getPositions();
            assertEquals("Assert five posititons", 5, positions.size());
        }
        utx.commit();
    }

}
