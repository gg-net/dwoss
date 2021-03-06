package eu.ggnet.dwoss.stock.ee.itest;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.ShipmentEao;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.stock.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.core.system.util.Utils;

import static org.junit.Assert.*;

/**
 *
 * @author Bastian Venz
 */
@RunWith(Arquillian.class)
public class ShipmentIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    @Stocks
    private EntityManager em;

    private ShipmentEao eao;

    @Before
    public void before() throws Exception {
        eao = new ShipmentEao(em);
    }

    @After
    public void clearDataBase() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    //<editor-fold defaultstate="collapsed" desc=" testCreation ">
    @Test
    public void testCreateShipment() throws Exception {
        utx.begin();
        em.joinTransaction();
        Shipment ship1 = new Shipment("001", TradeName.ONESELF, TradeName.HP, Shipment.Status.OPENED);
        em.persist(ship1);
        assertTrue(ship1.getId() > 0);
        utx.commit();

        utx.begin();
        em.joinTransaction();

        Shipment ship2 = new Shipment("002", TradeName.ALSO, TradeName.DELL, Shipment.Status.OPENED);
        em.persist(ship2);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        List<Shipment> list = eao.findAll();
        utx.commit();

        assertNotNull(list);
        assertEquals(2, list.size());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" testDeletion ">
    @Test
    public void testDeleteShipment() throws Exception {
        utx.begin();
        em.joinTransaction();
        Shipment ship1 = new Shipment("001", TradeName.ONESELF, TradeName.APPLE, Shipment.Status.OPENED);
        em.persist(ship1);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        ship1 = em.find(Shipment.class, ship1.getId());
        Shipment ship2 = new Shipment("002", TradeName.DELL, TradeName.DELL, Shipment.Status.OPENED);
        em.persist(ship2);
        em.remove(ship1);
        List<Shipment> list = eao.findAll();
        assertEquals(1, list.size());
        utx.commit();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" testUpdate ">
    @Test
    public void testUpdateShipment() throws Exception {
        utx.begin();
        em.joinTransaction();
        Shipment ship1 = new Shipment("001", TradeName.ONESELF, TradeName.LENOVO, Shipment.Status.OPENED);
        em.persist(ship1);
        utx.commit();

        ship1.setShipmentId("005");
        ship1.setContractor(TradeName.AMAZON);

        utx.begin();
        em.joinTransaction();
        em.merge(ship1);
        assertEquals(TradeName.AMAZON, eao.findAll().get(0).getContractor());
        utx.commit();

    }
    //</editor-fold>

}
