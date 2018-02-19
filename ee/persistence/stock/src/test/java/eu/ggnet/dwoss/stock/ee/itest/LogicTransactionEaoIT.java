package eu.ggnet.dwoss.stock.ee.itest;

import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.LogicTransactionEao;
import eu.ggnet.dwoss.stock.ee.itest.support.ArquillianProjectArchive;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class LogicTransactionEaoIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    private LogicTransactionEao ltEao;

    @Inject
    @Stocks
    private EntityManager em;

    @Test
    public void testFindByUniqueUnitId() throws Exception {
        utx.begin();
        em.joinTransaction();
        Stock s1 = new Stock(0, "TEEEEEEEST");
        Stock s2 = new Stock(1, "TEEEEEEEST");
        em.persist(s1);
        em.persist(s2);
        StockUnit su1 = new StockUnit("1", 1);
        s1.addUnit(su1);
        em.persist(su1);
        StockUnit su2 = new StockUnit("2", 5);
        s1.addUnit(su2);
        em.persist(su2);
        StockUnit su3 = new StockUnit("3", 5);
        s1.addUnit(su3);
        em.persist(su3);
        StockUnit su4 = new StockUnit("4", 5);
        s1.addUnit(su4);
        em.persist(su4);
        StockUnit su5 = new StockUnit("5", 5);
        s1.addUnit(su5);
        em.persist(su5);
        StockUnit su6 = new StockUnit("6", 6);
        s2.addUnit(su6);
        StockUnit su7 = new StockUnit("7", 7);
        s1.addUnit(su7);
        StockUnit su8 = new StockUnit("8", 8);
        s2.addUnit(su8);
        em.persist(su6);
        em.persist(su7);
        em.persist(su8);
        LogicTransaction lt1 = new LogicTransaction();
        lt1.setDossierId(1);
        lt1.add(su6);
        lt1.add(su7);
        em.persist(lt1);
        long lt1Id = lt1.getId();

        LogicTransaction lt2 = new LogicTransaction();
        lt2.setDossierId(2);
        lt2.add(su8);
        em.persist(lt2);
        long lt2Id = lt2.getId();
        utx.commit();

        lt1 = ltEao.findByUniqueUnitId(1);
        assertNull(lt1);

        lt1 = ltEao.findByUniqueUnitId(-1);
        assertNull(lt1);

        lt1 = ltEao.findByUniqueUnitId(6);
        assertNotNull(lt1);
        assertEquals(lt1Id, lt1.getId());

        lt2 = ltEao.findByUniqueUnitId(8);
        assertNotNull(lt2);
        assertEquals(lt2Id, lt2.getId());

    }
}
