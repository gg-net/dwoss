package eu.ggnet.dwoss.stock.itest;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.entity.*;
import eu.ggnet.dwoss.stock.itest.support.ArquillianProjectArchive;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class StockUnitEaoIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    @Stocks
    private EntityManager em;

    @Inject
    private StockUnitEao sus;

    @After
    public void clearDataBase() throws Exception {
        utx.begin();
        em.joinTransaction();
        em.createNativeQuery("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK").executeUpdate();
        utx.commit();
    }

    @Test
    public void testFindByIdentifierAndStock() throws Exception {
        utx.begin();
        em.joinTransaction();
        Stock s = new Stock(0, "TEEEEEEEEEEEEEEEST");
        em.persist(s);

        StockLocation sl = new StockLocation("Lagerplatz");
        s.addStockLocation(sl);

        StockUnit s1 = new StockUnit("G1", 1);
        s1.setRefurbishId("23");
        StockUnit s2 = new StockUnit("G2", 2);
        s2.setRefurbishId("42");
        s.addUnit(s1, sl);
        s.addUnit(s2, sl);

        em.persist(new Stock(1, "TEEEEEEEEST"));
        utx.commit();

        int id1 = s1.getId();
        int id2 = s2.getId();

        assertFalse(id1 == id2);

        s1 = sus.findByUniqueUnitId(1);
        s2 = sus.findByUniqueUnitId(2);

        assertThat(s1).isNotNull();
        assertThat(s2).isNotNull();

        assertEquals(id1, s1.getId());
        assertEquals(id2, s2.getId());

        List<StockUnit> units = sus.findByStockId(s.getId());
        assertEquals(2, units.size());

        s1 = sus.findByRefurbishId("23");
        s2 = sus.findByRefurbishId("42");

        assertNotNull(s1);
        assertNotNull(s2);

        assertEquals(id1, s1.getId());
        assertEquals(id2, s2.getId());

        assertNull(sus.findByRefurbishId("123"));
        assertNull(sus.findByRefurbishId(null));
    }

    @Test
    public void testSumByTransaction() throws Exception {
        utx.begin();
        em.joinTransaction();
        Stock s0 = new Stock(0, "1111111111111111111111111111");
        Stock s1 = new Stock(1, "2222222222222222222222222222");
        em.persist(s0);
        em.persist(s1);
        StockLocation s0l0 = new StockLocation("Lagerplatz");
        s0.addStockLocation(s0l0);

        StockUnit su0 = new StockUnit("g1", 1);
        su0.setRefurbishId("23");
        su0.setName("Name");
        StockUnit su1 = new StockUnit("g2", 2);
        su1.setRefurbishId("42");
        su1.setName("Name");
        StockUnit su2 = new StockUnit("g3", 3);
        su2.setRefurbishId("42");
        su2.setName("Name");
        s0.addUnit(su0, s0l0);
        s0.addUnit(su1, s0l0);
        s0.addUnit(su2, s0l0);

        em.persist(su0);
        em.persist(su1);
        em.persist(su2);

        StockTransaction st = new StockTransaction(StockTransactionType.TRANSFER);
        st.setDestination(s1);
        st.setSource(s0);
        st.addStatus(new StockTransactionStatus(StockTransactionStatusType.PREPARED, new Date()));
        em.persist(st);
        st.addPosition(new StockTransactionPosition(su0));
        st.addPosition(new StockTransactionPosition(su1));
        utx.commit();

        assertThat(sus.countByTransaction(s0.getId(), StockTransactionType.TRANSFER, StockTransactionStatusType.PREPARED)).isEqualTo(2);

    }

    @Test
    public void testFindByNoLogicTransaction() throws Exception {
        utx.begin();
        em.joinTransaction();
        Stock s = new Stock(0, "TEEEEEEEEEEEEEEEEEEEEEst");
        em.persist(s);
        StockLocation sl = new StockLocation("Lagerplatz");
        s.addStockLocation(sl);

        StockUnit s1 = new StockUnit("G1", 1);
        StockUnit s2 = new StockUnit("G2", 2);
        StockUnit s3 = new StockUnit("G3", 3);
        StockUnit s4 = new StockUnit("G4", 4);
        s.addUnit(s1, sl);
        s.addUnit(s2, sl);
        s.addUnit(s3, sl);
        s.addUnit(s4, sl);
        em.persist(s);
        em.persist(new Stock(1, "teeeeeeeeeeest"));
        LogicTransaction lt = new LogicTransaction();
        lt.setDossierId(1);
        lt.add(s4);
        em.persist(lt);
        utx.commit();

        List<StockUnit> sts = sus.findByNoLogicTransaction();
        assertEquals(3, sts.size());

        List<Integer> uuids = sus.findByNoLogicTransactionAsUniqueUnitId();
        assertEquals(3, uuids.size());
    }

    @Test
    public void testFindByNoTransaction() throws Exception {
        utx.begin();
        em.joinTransaction();
        Stock s = new Stock(0, "TEEEEEEEEEEEEEEST");
        em.persist(s);
        StockLocation sl = new StockLocation("Lagerplatz");
        s.addStockLocation(sl);

        StockUnit s1 = new StockUnit("G1", 1);
        StockUnit s2 = new StockUnit("G2", 2);
        StockUnit s3 = new StockUnit("G3", 3);
        StockUnit s4 = new StockUnit("G4", 4);
        s.addUnit(s1, sl);
        s.addUnit(s2, sl);
        s.addUnit(s3, sl);
        s.addUnit(s4, sl);
        em.persist(s);
        em.persist(new Stock(1, "TEEEEEEEEEST2"));
        LogicTransaction lt = new LogicTransaction();
        lt.setDossierId(1);
        lt.add(s4);
        em.persist(lt);

        StockTransaction st = new StockTransaction(StockTransactionType.TRANSFER);
        st.setSource(s);
        st.addStatus(new StockTransactionStatus(StockTransactionStatusType.PREPARED, new Date()));
        em.persist(st);
        st.addPosition(new StockTransactionPosition(s1));
        utx.commit();

        List<StockUnit> sts = sus.findByNoTransaction();
        assertEquals(2, sts.size());
    }
}
