package eu.ggnet.dwoss.stock.ee.itest;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.ShipmentEao;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.stock.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.core.common.values.ShipmentStatus;
import eu.ggnet.dwoss.core.system.util.Utils;

import eu.ggnet.dwoss.stock.ee.assist.gen.StockDeleteUtils;

import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static org.assertj.core.api.Assertions.assertThat;
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
        StockDeleteUtils.deleteAll(em);
        assertThat(StockDeleteUtils.validateEmpty(em)).isNull();
        utx.commit();
    }

    @Test
    public void testCreateShipment() throws Exception {
        utx.begin();
        em.joinTransaction();
        Shipment ship1 = new Shipment("001", TradeName.ONESELF, TradeName.HP, ShipmentStatus.OPENED);
        em.persist(ship1);
        assertThat(ship1.getId()).isNotEqualTo(0);
        utx.commit();

        utx.begin();
        em.joinTransaction();

        Shipment ship2 = new Shipment("002", TradeName.ALSO, TradeName.DELL, ShipmentStatus.OPENED);
        em.persist(ship2);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        List<Shipment> list = eao.findAll();
        utx.commit();

        assertThat(list).isNotNull().isNotEmpty().hasSize(2);
        assertThat(list.get(0).getId()).isNotEqualTo(0);
        assertThat(list.get(1).getId()).isNotEqualTo(0);
    }

    @Test
    public void testDeleteShipment() throws Exception {
        utx.begin();
        em.joinTransaction();
        Shipment ship1 = new Shipment("001", TradeName.ONESELF, TradeName.APPLE, ShipmentStatus.OPENED);
        em.persist(ship1);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        ship1 = em.find(Shipment.class, ship1.getId());
        Shipment ship2 = new Shipment("002", TradeName.DELL, TradeName.DELL, ShipmentStatus.OPENED);
        em.persist(ship2);
        em.remove(ship1);
        List<Shipment> list = eao.findAll();
        assertEquals(1, list.size());
        utx.commit();
    }

    @Test
    public void testUpdateShipment() throws Exception {
        utx.begin();
        em.joinTransaction();
        Shipment ship1 = new Shipment("001", TradeName.ONESELF, TradeName.LENOVO, ShipmentStatus.OPENED);
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
    
    @Test
    public void testFindSince() throws Exception {
        final String S1 = "001";
        final String S2 = "002";
        final String S3 = "003";
        
        utx.begin();
        em.joinTransaction();
        Shipment s1 = new Shipment(S1, TradeName.ONESELF, TradeName.LENOVO, ShipmentStatus.OPENED);
        s1.setDate(Utils.toDate(LocalDate.of(2020, Month.JANUARY, 1)));
        em.persist(s1);        
        Shipment s2 = new Shipment(S2, TradeName.ONESELF, TradeName.LENOVO, ShipmentStatus.OPENED);
        s2.setDate(Utils.toDate(LocalDate.of(2020, Month.JANUARY, 2)));
        em.persist(s2);
        Shipment s3 = new Shipment(S3, TradeName.ONESELF, TradeName.LENOVO, ShipmentStatus.OPENED);
        s3.setDate(Utils.toDate(LocalDate.of(2021, Month.JANUARY, 1)));
        em.persist(s3);
        utx.commit();
        
        utx.begin();
        em.joinTransaction();

        assertThat(eao.findSince(LocalDate.of(2020,JANUARY,2))).extracting(Shipment::getShipmentId).contains(S2,S3);
        assertThat(eao.findSince(LocalDate.of(2020,JULY,1))).extracting(Shipment::getShipmentId).contains(S3);
        
        utx.commit();                
    }
    

}
