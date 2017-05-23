package eu.ggnet.dwoss.uniqueunit.itest;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.event.UnitHistory;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.itest.support.ArquillianProjectArchive;

import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.SERIAL;
import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class UnitHistoryListenerOperationIT extends ArquillianProjectArchive {

    @EJB
    private UniqueUnitAgent uniqueUnitAgent;

    @Inject
    private SenderBean senderBean;

    @Inject
    private UnitBean unitBean;

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
    public static class SenderBean {

        @Inject
        private Event<UnitHistory> historyEvent;

        public void send(int id, String msg, String arranger) {
            historyEvent.fire(new UnitHistory(id, msg, arranger));
        }
    }

    @Stateless
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
