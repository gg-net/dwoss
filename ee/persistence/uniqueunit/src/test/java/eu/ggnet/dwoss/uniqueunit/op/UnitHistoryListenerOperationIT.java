package eu.ggnet.dwoss.uniqueunit.op;

import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnitHistory;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.entity.Product;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.event.UnitHistory;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;

import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.*;
import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class UnitHistoryListenerOperationIT {

    private EJBContainer container;

    @EJB
    private UniqueUnitAgent uniqueUnitAgent;

    @Inject
    private SenderBean senderBean;

    @Inject
    private UnitBean unitBean;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void after() {
        container.close();
    }

    @Test
    public void testEvent() throws InterruptedException {
        String MSG = "Eine Nachricht";
        int id1 = unitBean.createSampleUnit();
        senderBean.send(id1, MSG, "Junit");
        Thread.sleep(1000);
        UniqueUnit u1 = uniqueUnitAgent.findByIdEager(UniqueUnit.class, id1);
        assertNotNull(u1);
        assertEquals("Should have three history Elements, contains " + u1.getHistory(), 3, u1.getHistory().size());
        assertTrue("Should contain '" + MSG + "', but has '" + u1 + "'", hasMessage(u1, MSG));
    }

    private boolean hasMessage(UniqueUnit uu, String msg) {
        for (UniqueUnitHistory uuh : uu.getHistory()) {
            if ( uuh.getComment().contains(msg) ) return true;
        }
        return false;
    }

    @Stateless
//
    public static class SenderBean {

        @Inject
        private Event<UnitHistory> historyEvent;

        public void send(int id, String msg, String arranger) {
            historyEvent.fire(new UnitHistory(id, msg, arranger));
        }
    }

    @Stateless
//
//
    public static class UnitBean {

        @Inject
        @UniqueUnits
        private EntityManager em;

        @Inject
        private Contractors contractors;

        public int createSampleUnit() {
            Product p = new ProductEao(em).findByPartNo("AA.AAAAA.AAA");
            if ( p == null ) {
                p = new Product(ProductGroup.DESKTOP, TradeName.FUJITSU, "AA.AAAAA.AAA", "Verition Stein");
                p.setDescription("Ein Tolles Gerät");
                p.setPrice(PriceType.MANUFACTURER_COST, 200.0, "JUnit - Testcase");
                p.setAdditionalPartNo(contractors.all().iterator().next(), "833.323");
                p.setPrice(PriceType.CONTRACTOR_REFERENCE, 240.0, "JUnit - Testcase");
                p.addFlag(Product.Flag.PRICE_FIXED);
                em.persist(p);
            }

            UniqueUnit unit = new UniqueUnit(p, new Date(), "");
            unit.setIdentifier(SERIAL, "AAAAAAAAAAA123AAADFSADFSA");
            unit.setIdentifier(REFURBISHED_ID, "30001");
            unit.setContractor(TradeName.ONESELF);
            unit.setComment("Ein Commentar");
            unit.setCondition(UniqueUnit.Condition.AS_NEW);

            em.persist(unit);
            return unit.getId();
        }
    }
}
