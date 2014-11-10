package eu.ggnet.dwoss.stock.emo;

import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.entity.StockUnit;

import java.util.Arrays;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.stock.eao.LogicTransactionEao;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
public class LogicTransactionEmoIT {

    private EntityManager em;

    private EntityManagerFactory emf;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(StockPu.NAME, StockPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
    }

    @After
    public void tearDown() {
        if ( em != null ) em.close();
        if ( emf != null ) emf.close();
    }

    @Test
    public void testRequest() {
        LogicTransactionEmo ltEmo = new LogicTransactionEmo(em);
        em.getTransaction().begin();

        LogicTransaction lt1 = new LogicTransaction();
        lt1.setDossierId(1);

        LogicTransaction lt2 = ltEmo.request(2);

        em.persist(lt1);

        assertNotNull(lt1);
        assertNotNull(lt2);

        em.getTransaction().commit();
    }

    @Test
    public void testEquilibrate() throws InterruptedException {
        em.getTransaction().begin();
        Stock s = new Stock(0);
        s.setName("Test-0");
        em.persist(s);
//        StockEntityHelper.createOrUpdateMasterData(em);

        em.getTransaction().commit();

        em.getTransaction().begin();
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

        em.getTransaction().commit();

        em.getTransaction().begin();
        LogicTransaction find = em.find(LogicTransaction.class, 1l);

        assertTrue("Size was not 2. find=" + find, find.getUnits().size() == 2);
        assertTrue("It wasnt All Units correctly in the Transaction. find=" + find,
                find.getUnits().containsAll(Arrays.asList(new StockUnit[]{stockUnit1, stockUnit2})));
        em.getTransaction().commit();

        LogicTransactionEmo emo = new LogicTransactionEmo(em);

        em.getTransaction().begin();
        emo.equilibrate(1l, Arrays.asList(new Integer[]{1, 2, 7, 8}));
        em.getTransaction().commit();

        em.getTransaction().begin();
        find = em.find(LogicTransaction.class, 1l);
        assertTrue("Size was not 4. find=" + find, find.getUnits().size() == 4);
        assertTrue("It wasnt All Units correctly in the Transaction. find=" + find,
                find.getUnits().containsAll(Arrays.asList(new StockUnit[]{stockUnit1, stockUnit2, stockUnit7, stockUnit8})));
        em.getTransaction().commit();

        em.getTransaction().begin();
        emo.equilibrate(1l, Arrays.asList(new Integer[]{1, 2, 8}));
        em.getTransaction().commit();

        em.getTransaction().begin();
        find = em.find(LogicTransaction.class, 1l);
        assertTrue("Size was not 3. find=" + find, find.getUnits().size() == 3);
        assertTrue("It wasnt All Units correctly in the Transaction. find="
                + find, find.getUnits().containsAll(Arrays.asList(new StockUnit[]{stockUnit1, stockUnit2, stockUnit8})));
        em.getTransaction().commit();

        em.getTransaction().begin();
        emo.equilibrate(1l, Arrays.asList(new Integer[]{1, 7, 8}));
        em.getTransaction().commit();

        em.getTransaction().begin();
        find = em.find(LogicTransaction.class, 1l);
        assertTrue("Size was not 3. find=" + find, find.getUnits().size() == 3);
        assertTrue("It wasnt All Units correctly in the Transaction. find="
                + find, find.getUnits().containsAll(Arrays.asList(new StockUnit[]{stockUnit1, stockUnit7, stockUnit8})));
        em.getTransaction().commit();

        em.getTransaction().begin();
        emo.equilibrate(1l, Arrays.asList(new Integer[]{}));
        em.getTransaction().commit();

        em.getTransaction().begin();
        find = em.find(LogicTransaction.class, 1l);
        assertTrue("LT was not null. find=" + find, find == null);
        em.getTransaction().commit();

        em.getTransaction().begin();
        emo.equilibrate(5l, Arrays.asList(new Integer[]{1, 2, 7, 8}));
        em.getTransaction().commit();

        em.getTransaction().begin();
        find = new LogicTransactionEao(em).findByDossierId(5l);
        assertTrue("LT was Null.", find != null);
        assertTrue("Size was not 4. find=" + find, find.getUnits().size() == 4);
        assertTrue("It wasnt All Units correctly in the Transaction. find="
                + find, find.getUnits().containsAll(Arrays.asList(new StockUnit[]{stockUnit1, stockUnit2, stockUnit7, stockUnit8})));
        em.getTransaction().commit();
    }
}
