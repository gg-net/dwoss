package eu.ggnet.dwoss.stock.ee.itest;

import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;

import java.util.Arrays;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.LogicTransactionEao;
import eu.ggnet.dwoss.stock.ee.emo.LogicTransactionEmo;
import eu.ggnet.dwoss.stock.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.core.system.util.Utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class LogicTransactionEmoIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    @Stocks
    private EntityManager em;

    private final static Logger L = LoggerFactory.getLogger(LogicTransactionEmoIT.class);

    @After
    public void clearDataBase() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    @Test
    public void testRequest() throws Exception {
        L.info("starting test request");
        LogicTransactionEmo ltEmo = new LogicTransactionEmo(em);
        utx.begin();
        em.joinTransaction();

        LogicTransaction lt1 = new LogicTransaction();
        lt1.setDossierId(1);

        LogicTransaction lt2 = ltEmo.request(2);

        em.persist(lt1);

        assertNotNull(lt1);
        assertNotNull(lt2);

        utx.commit();
    }

    @Test
    public void testEquilibrate() throws Exception {
        L.info("starting test equilibrate");
        utx.begin();
        em.joinTransaction();
        Stock s = new Stock(0);
        s.setName("Test-0");
        L.debug("Persisted {}", s);
        em.persist(s);
//        StockEntityHelper.createOrUpdateMasterData(em);

        utx.commit();

        utx.begin();
        em.joinTransaction();
        s = em.merge(s);
        L.debug("Merged {}", s);
        LogicTransaction logicTransaction = new LogicTransaction();
        LogicTransaction logicTransaction1 = new LogicTransaction();
        LogicTransaction logicTransaction2 = new LogicTransaction();

        StockUnit stockUnit1 = new StockUnit("Unit1", "Unit1", 1);
        StockUnit stockUnit2 = new StockUnit("Unit2", "Unit2", 2);
        StockUnit stockUnit3 = new StockUnit("Unit3", "Unit3", 3);
        StockUnit stockUnit4 = new StockUnit("Unit4", "Unit4", 4);
        StockUnit stockUnit5 = new StockUnit("Unit5", "Unit5", 5);
        StockUnit stockUnit6 = new StockUnit("Unit6", "Unit6", 6);
        StockUnit stockUnit7 = new StockUnit("Unit7", "Unit7", 7);
        StockUnit stockUnit8 = new StockUnit("Unit8", "Unit8", 8);

        stockUnit1.setStock(s);
        stockUnit2.setStock(s);
        stockUnit3.setStock(s);
        stockUnit4.setStock(s);
        stockUnit5.setStock(s);
        stockUnit6.setStock(s);
        stockUnit7.setStock(s);
        stockUnit8.setStock(s);

        logicTransaction.setDossierId(1l);
        logicTransaction1.setDossierId(1l);
        logicTransaction2.setDossierId(1l);

        em.persist(logicTransaction);
        em.persist(logicTransaction1);
        em.persist(logicTransaction2);

        em.persist(stockUnit1);
        em.persist(stockUnit2);
        em.persist(stockUnit3);
        em.persist(stockUnit4);
        em.persist(stockUnit5);
        em.persist(stockUnit6);
        em.persist(stockUnit7);
        em.persist(stockUnit8);

        logicTransaction.add(stockUnit1);
        logicTransaction.add(stockUnit2);
        logicTransaction1.add(stockUnit3);
        logicTransaction1.add(stockUnit4);
        logicTransaction2.add(stockUnit5);
        logicTransaction2.add(stockUnit6);
        L.debug("pre commit");
        utx.commit();

        L.debug("Post Commit");
        utx.begin();
        em.joinTransaction();
        LogicTransaction find = em.find(LogicTransaction.class, 1l);

        assertTrue("Size was not 2. find=" + find, find.getUnits().size() == 2);
        assertTrue("It wasnt All Units correctly in the Transaction. find=" + find,
                find.getUnits().containsAll(Arrays.asList(new StockUnit[]{stockUnit1, stockUnit2})));
        utx.commit();

        LogicTransactionEmo emo = new LogicTransactionEmo(em);

        utx.begin();
        em.joinTransaction();
        emo.equilibrate(1l, Arrays.asList(new Integer[]{1, 2, 7, 8}));
        utx.commit();

        utx.begin();
        em.joinTransaction();
        find = em.find(LogicTransaction.class, 1l);
        assertTrue("Size was not 4. find=" + find, find.getUnits().size() == 4);
        assertTrue("It wasnt All Units correctly in the Transaction. find=" + find,
                find.getUnits().containsAll(Arrays.asList(new StockUnit[]{stockUnit1, stockUnit2, stockUnit7, stockUnit8})));
        utx.commit();

        utx.begin();
        em.joinTransaction();
        emo.equilibrate(1l, Arrays.asList(new Integer[]{1, 2, 8}));
        utx.commit();

        utx.begin();
        em.joinTransaction();
        find = em.find(LogicTransaction.class, 1l);
        assertTrue("Size was not 3. find=" + find, find.getUnits().size() == 3);
        assertTrue("It wasnt All Units correctly in the Transaction. find="
                + find, find.getUnits().containsAll(Arrays.asList(new StockUnit[]{stockUnit1, stockUnit2, stockUnit8})));
        utx.commit();

        utx.begin();
        em.joinTransaction();
        emo.equilibrate(1l, Arrays.asList(new Integer[]{1, 7, 8}));
        utx.commit();

        utx.begin();
        em.joinTransaction();
        find = em.find(LogicTransaction.class, 1l);
        assertTrue("Size was not 3. find=" + find, find.getUnits().size() == 3);
        assertTrue("It wasnt All Units correctly in the Transaction. find="
                + find, find.getUnits().containsAll(Arrays.asList(new StockUnit[]{stockUnit1, stockUnit7, stockUnit8})));
        utx.commit();

        utx.begin();
        em.joinTransaction();
        emo.equilibrate(1l, Arrays.asList(new Integer[]{}));
        utx.commit();

        utx.begin();
        em.joinTransaction();
        find = em.find(LogicTransaction.class, 1l);
        assertTrue("LT was not null. find=" + find, find == null);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        emo.equilibrate(5l, Arrays.asList(new Integer[]{1, 2, 7, 8}));
        utx.commit();

        utx.begin();
        em.joinTransaction();
        find = new LogicTransactionEao(em).findByDossierId(5l);
        assertTrue("LT was Null.", find != null);
        assertTrue("Size was not 4. find=" + find, find.getUnits().size() == 4);
        assertTrue("It wasnt All Units correctly in the Transaction. find="
                + find, find.getUnits().containsAll(Arrays.asList(new StockUnit[]{stockUnit1, stockUnit2, stockUnit7, stockUnit8})));
        utx.commit();
        L.info("stopping test equilibrate");
    }
}
