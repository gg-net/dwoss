package eu.ggnet.dwoss.stock.entity;


import java.util.List;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.stock.eao.ShipmentEao;

import static org.junit.Assert.*;

/**
 *
 * @author Bastian Venz
 */
public class ShipmentIT {

    private EntityManagerFactory emf;

    private EntityManager em;

    private ShipmentEao eao;

    @Before
    public void before() {
        emf = Persistence.createEntityManagerFactory(StockPu.NAME, StockPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
        eao = new ShipmentEao(em);
    }

    @After
    public void after() {
        em.close();
        emf.close();
    }

    //<editor-fold defaultstate="collapsed" desc=" testCreation ">
    @Test
    public void testCreateShipment() {
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Shipment ship1 = new Shipment("001", TradeName.ONESELF, TradeName.HP, Shipment.Status.OPENED);
        em.persist(ship1);
        tx.commit();

        assertTrue(ship1.getId() > 0);
        tx.begin();

        Shipment ship2 = new Shipment("002", TradeName.ALSO, TradeName.DELL, Shipment.Status.OPENED);
        em.persist(ship2);
        tx.commit();

        tx.begin();
        List<Shipment> list = eao.findAll();
        tx.commit();

        assertNotNull(list);
        assertEquals(2, list.size());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" testDeletion ">
    @Test
    public void testDeleteShipment() {
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Shipment ship1 = new Shipment("001", TradeName.ONESELF, TradeName.APPLE, Shipment.Status.OPENED);
        em.persist(ship1);
        tx.commit();

        assertNotNull(ship1);
        Shipment ship2 = new Shipment("002", TradeName.DELL, TradeName.DELL, Shipment.Status.OPENED);

        tx.begin();
        em.persist(ship2);
        em.remove(ship1);
        List<Shipment> list = eao.findAll();
        tx.commit();

        assertEquals(1, list.size());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" testUpdate ">
    @Test
    public void testUpdateShipment() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Shipment ship1 = new Shipment("001", TradeName.ONESELF, TradeName.LENOVO, Shipment.Status.OPENED);
        em.persist(ship1);
        tx.commit();

        ship1.setShipmentId("005");
        ship1.setContractor(TradeName.AMAZON);
        tx.begin();
        em.merge(ship1);
        tx.commit();

        assertEquals(TradeName.AMAZON, eao.findAll().get(0).getContractor());
    }
    //</editor-fold>

}
