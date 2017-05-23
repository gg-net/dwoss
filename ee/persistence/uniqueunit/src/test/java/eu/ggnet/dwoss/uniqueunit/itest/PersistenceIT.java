package eu.ggnet.dwoss.uniqueunit.itest;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.util.Tuple2;

import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.SERIAL;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class PersistenceIT extends ArquillianProjectArchive {

    @EJB
    private UniqueUnitAgent agent;

    @Inject
    private PersistenceBean persistenceBean;

    @Test
    public void testPersistence() {
        Tuple2<Integer, Integer> t = persistenceBean.fillPersistenceSource();
        assertNotNull(agent.findAll(UniqueUnit.class));

        UniqueUnit unit3_1 = agent.findById(UniqueUnit.class, t._1);
        assertNotNull(unit3_1);
        assertNull(unit3_1.getComment());
        assertNull(unit3_1.getInternalComment());

        UniqueUnit unit4_1 = agent.findById(UniqueUnit.class, t._2);
        assertNotNull(unit4_1);
        assertNotNull(unit4_1.getComment());
        assertNotNull(unit4_1.getInternalComment());
    }

    @Stateless
    public static class PersistenceBean {

        @Inject
        @UniqueUnits
        private EntityManager em;

        public Tuple2<Integer, Integer> fillPersistenceSource() {

            Date now = new Date();

            Product p1 = new Product(ProductGroup.DESKTOP, TradeName.ACER, "LX.11111.222", "Verition Stein");
            p1.setDescription("Ein Tolles Gerät");
            p1.setPrice(PriceType.MANUFACTURER_COST, 200.0, "JUnit - Testcase");
            p1.setPrice(PriceType.CONTRACTOR_REFERENCE, 240.0, "JUnit - Testcase");
            p1.addFlag(Product.Flag.PRICE_FIXED);

            Product p2 = new Product(ProductGroup.COMMENTARY, TradeName.DELL, "DL", "Dienstleistung 1h");
            p2.setDescription("Eine Dienstleistungs Stunde");

            UniqueUnit unit1 = new UniqueUnit(p1, now, "");
            unit1.setIdentifier(SERIAL, "ROFFFLAASSS");
            unit1.setPrice(PriceType.SALE, 125, "JUnit - Testcase");
            unit1.addFlag(UniqueUnit.Flag.PRICE_FIXED);
            unit1.setContractor(TradeName.ONESELF);
            unit1.setComment("Ein Commentar");
            unit1.setCondition(UniqueUnit.Condition.AS_NEW);

            UniqueUnit unit2 = new UniqueUnit(p1, now, "lila");
            unit2.addHistory(new UniqueUnitHistory(UniqueUnitHistory.Type.UNDEFINED, "Aufgenommen als Sopo 332"));
            unit2.addHistory(new UniqueUnitHistory(UniqueUnitHistory.Type.UNIQUE_UNIT, "Zerlegt weil kaput"));
            unit2.setIdentifier(SERIAL, "DBCFDASFDSADEF");
            unit2.setContractor(TradeName.ONESELF);
            unit2.setComment("Auch ein Commentar");
            unit2.setCondition(UniqueUnit.Condition.AS_NEW);

            UniqueUnit unit3 = new UniqueUnit();
            unit3.setProduct(p1);
            unit3.setMfgDate(now);
            unit3.setIdentifier(SERIAL, "ABCDEFJKHKZHJI");
            unit3.setContractor(TradeName.ONESELF);
            unit3.setCondition(UniqueUnit.Condition.AS_NEW);

            UniqueUnit unit4 = new UniqueUnit(p2, now, "");
            unit4.setIdentifier(SERIAL, "ABCDEFFEQGSDFD");
            unit4.setContractor(TradeName.ONESELF);
            unit4.setCondition(UniqueUnit.Condition.USED);
            unit4.setComment("Ein sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr langer Kommentar");
            unit4.setInternalComment("Ein sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                    + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr langer Kommentar");

            em.persist(p1);
            em.persist(p2);

            em.persist(unit1);
            em.persist(unit2);
            em.persist(unit3);
            em.persist(unit4);
            return new Tuple2<>(unit3.getId(), unit4.getId());
        }

    }
}
