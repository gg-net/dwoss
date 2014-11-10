package eu.ggnet.dwoss.stock.emo;

import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.StockTransactionPosition;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatus;
import eu.ggnet.dwoss.stock.entity.StockUnit;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.stock.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.stock.eao.StockTransactionEao;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;

import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.stock.entity.StockTransactionStatusType.*;
import static org.junit.Assert.*;

public class StockTransactionEmoIT {

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

    private List<Stock> prepareStocks() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        StockGeneratorOperation gen = new StockGeneratorOperation(em);
        List<Stock> stocks = gen.makeStocksAndLocations(2);
        tx.commit();
        return stocks;
    }

    @Test
    public void testRequestDestroyPrepared() {
        EntityTransaction tx = em.getTransaction();
        List<Stock> stocks = prepareStocks();

        StockTransactionEmo stockTransactionEmo = new StockTransactionEmo(em);

        tx.begin();
        StockTransaction st1 = stockTransactionEmo.requestDestroyPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        assertNotNull(st1);
        tx.commit();

        tx.begin();
        StockTransaction st2 = stockTransactionEmo.requestDestroyPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        assertNotNull(st2);
        assertEquals(st1.getId(), st2.getId());

        st2.addStatus(new StockTransactionStatus(COMPLETED, new Date()));

        tx.commit();

        tx.begin();
        StockTransaction st3 = stockTransactionEmo.requestDestroyPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        assertNotNull(st3);
        assertFalse(st1.getId() == st3.getId());
        tx.commit();
    }

    @Test
    public void testRequestRollInPrepared() {
        EntityTransaction tx = em.getTransaction();
        List<Stock> stocks = prepareStocks();

        StockTransactionEmo stockTransactionEmo = new StockTransactionEmo(em);

        tx.begin();
        StockTransaction st1 = stockTransactionEmo.requestRollInPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        assertNotNull(st1);
        tx.commit();

        tx.begin();
        StockTransaction st2 = stockTransactionEmo.requestRollInPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        assertNotNull(st2);
        assertEquals(st1.getId(), st2.getId());

        st2.addStatus(new StockTransactionStatus(COMPLETED, new Date()));

        tx.commit();

        tx.begin();
        StockTransaction st3 = stockTransactionEmo.requestRollInPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        assertNotNull(st3);
        assertFalse(st1.getId() == st3.getId());
        tx.commit();
    }

    @Test
    public void testCompleteRollInRollOut() {
        EntityTransaction tx = em.getTransaction();
        List<Stock> stocks = prepareStocks();

        StockTransactionEmo stockTransactionEmo = new StockTransactionEmo(em);

        tx.begin();
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
        tx.commit();

        StockUnitEao stockUnitEao = new StockUnitEao(em);

        tx.begin();
        st1 = stockTransactionEmo.requestRollOutPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        for (StockUnit stockUnit : stockUnitEao.findAll()) {
            st1.addUnit(stockUnit);
        }

        assertEquals(units.size(), st1.getPositions().size());
        tx.commit();

        tx.begin();
        st1 = em.merge(st1);
        List<Integer> uids = stockTransactionEmo.completeRollOut("Horst", Arrays.asList(st1));
        assertNotNull(uids);
        assertEquals(units.size(), uids.size());
        tx.commit();

        tx.begin();
        List<StockUnit> stockUnits = stockUnitEao.findAll();
        assertTrue(stockUnits.isEmpty());
        tx.commit();
    }

    @Test
    public void testCompleteDestroy() {
        EntityTransaction tx = em.getTransaction();
        List<Stock> stocks = prepareStocks();

        StockTransactionEmo stockTransactionEmo = new StockTransactionEmo(em);
        StockUnitEao stockUnitEao = new StockUnitEao(em);

        tx.begin();
        for (int i = 1; i <= 10; i++) {
            StockUnit su = new StockUnit(Integer.toString(i), i);
            su.setStock(stocks.get(0));
            em.persist(su);
        }

        List<StockUnit> units = stockUnitEao.findAll();

        assertNotNull(units);

        for (StockUnit stockUnit : units) {
            assertTrue(stockUnit.isInStock());
            assertFalse(stockUnit.isInTransaction());
            assertEquals(stocks.get(0), stockUnit.getStock());
        }
        tx.commit();

        tx.begin();
        StockTransaction st1 = stockTransactionEmo.requestDestroyPrepared(stocks.get(0).getId(), "Hugo", "Ein toller Komentar");
        for (StockUnit stockUnit : stockUnitEao.findAll()) {
            st1.addUnit(stockUnit);
        }

        assertEquals(units.size(), st1.getPositions().size());
        List<Integer> uids = stockTransactionEmo.completeDestroy("Horst", Arrays.asList(st1));
        assertNotNull(uids);
        assertEquals(units.size(), uids.size());
        tx.commit();

        tx.begin();
        List<StockUnit> stockUnits = stockUnitEao.findAll();
        assertTrue(stockUnits.isEmpty());
        tx.commit();
    }

    @Test
    public void testPrepare() throws UserInfoException {
        EntityTransaction tx = em.getTransaction();
        List<Stock> stocks = prepareStocks();

        int transactionSize = 5;

        tx.begin();
        for (int i = 1; i <= 15; i++) {
            StockUnit su = new StockUnit(Integer.toString(i), i);
            su.setStock(stocks.get(0));
            em.persist(su);
        }
        tx.commit();
        tx.begin();

        List<Integer> units = new StockUnitEao(em).findAll().stream().map(StockUnit::getId).collect(Collectors.toList());
        assertEquals("Assert 15 persisted Stockunits", 15, units.size());

        //prepare
        new StockTransactionEmo(em).prepare(Transfer.builder()
                .destinationStockId(stocks.get(1).getId())
                .stockUnitIds(units)
                .arranger("UnitTest")
                .comment("Test prepare")
                .maxTransactionSize(transactionSize)
                .build(), null);
        tx.commit();
        tx.begin();

        StockTransactionEao stEao = new StockTransactionEao(em);
        List<StockTransaction> findByDestination = stEao.findAll();
        assertEquals("Assert three transactions", 3, findByDestination.size());

        int multiplier = transactionSize;
        for (StockTransaction transaction : findByDestination) {
            List<StockTransactionPosition> positions = transaction.getPositions();
            assertEquals("Assert five posititons", 5, positions.size());
            assertEquals("Assert last position to be a multiplier of 5", multiplier, positions.get(transactionSize - 1).getId());
            multiplier += transactionSize;
        }
        tx.commit();
    }

}
