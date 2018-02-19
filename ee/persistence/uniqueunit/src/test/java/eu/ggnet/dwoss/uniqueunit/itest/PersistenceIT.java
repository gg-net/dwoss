package eu.ggnet.dwoss.uniqueunit.itest;

import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.CategoryProduct;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UnitCollection;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.itest.support.ArquillianProjectArchive;

import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.SERIAL;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class PersistenceIT extends ArquillianProjectArchive {

    @EJB
    private UniqueUnitAgent agent;

    @Inject
    @UniqueUnits
    private EntityManager em;

    @Inject
    UserTransaction utx;

    @Test
    public void testPersistence() throws Exception {
        utx.begin();
        em.joinTransaction();
        Date now = new Date();

        CategoryProduct cp1 = new CategoryProduct();
        cp1.setName("Mixup");
        cp1.setPrice(PriceType.SALE, 100.0, "The Salepreice");
        cp1.setSalesChannel(SalesChannel.RETAILER);

        Product p1 = new Product(ProductGroup.DESKTOP, TradeName.ACER, "LX.11111.222", "Verition Stein");
        p1.setDescription("Ein Tolles Gerät");
        p1.setPrice(PriceType.MANUFACTURER_COST, 200.0, "JUnit - Testcase");
        p1.setPrice(PriceType.CONTRACTOR_REFERENCE, 240.0, "JUnit - Testcase");
        p1.addFlag(Product.Flag.PRICE_FIXED);
        p1.setCategoryProduct(cp1);

        Product p2 = new Product(ProductGroup.COMMENTARY, TradeName.DELL, "DL", "Dienstleistung 1h");
        p2.setDescription("Eine Dienstleistungs Stunde");
        p2.setCategoryProduct(cp1);

        UnitCollection uc1 = new UnitCollection();
        uc1.setProduct(p1);
        uc1.setPartNoExtension("demo1");
        uc1.setNameExtension("Demo1");

        UnitCollection uc2 = new UnitCollection();
        uc2.setProduct(p1);
        uc2.setPartNoExtension("demo2");
        uc2.setNameExtension("Demo2");

        UniqueUnit unit1 = new UniqueUnit(p1, now, "");
        unit1.setIdentifier(SERIAL, "ROFFFLAASSS");
        unit1.setPrice(PriceType.SALE, 125, "JUnit - Testcase");
        unit1.addFlag(UniqueUnit.Flag.PRICE_FIXED);
        unit1.setContractor(TradeName.ONESELF);
        unit1.setComment("Ein Commentar");
        unit1.setCondition(UniqueUnit.Condition.AS_NEW);
        unit1.setUnitCollection(uc1);

        UniqueUnit unit2 = new UniqueUnit(p1, now, "lila");
        unit2.addHistory("Aufgenommen als Sopo 332");
        unit2.addHistory("Zerlegt weil kaput");
        unit2.setIdentifier(SERIAL, "DBCFDASFDSADEF");
        unit2.setContractor(TradeName.ONESELF);
        unit2.setComment("Auch ein Commentar");
        unit2.setCondition(UniqueUnit.Condition.AS_NEW);
        unit2.setUnitCollection(uc2);

        UniqueUnit unit3 = new UniqueUnit();
        unit3.setProduct(p1);
        unit3.setMfgDate(now);
        unit3.setIdentifier(SERIAL, "ABCDEFJKHKZHJI");
        unit3.setContractor(TradeName.ONESELF);
        unit3.setCondition(UniqueUnit.Condition.AS_NEW);
        unit3.setUnitCollection(uc2);

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

        em.persist(cp1);

        em.persist(p1);
        em.persist(p2);

        em.persist(uc1);
        em.persist(uc2);

        em.persist(unit1);
        em.persist(unit2);
        em.persist(unit3);
        em.persist(unit4);

        utx.commit();
        List<CategoryProduct> allCp = agent.findAll(CategoryProduct.class);
        assertThat(allCp).as("findAllCategoryProducts").isNotEmpty().hasSize(1).contains(cp1);
        CategoryProduct getCp1 = allCp.get(0);
        assertThat(getCp1.getSalesChannel()).as("saleschanel of categoryproduct").isEqualTo(SalesChannel.RETAILER);
        assertThat(getCp1.hasPrice(PriceType.SALE)).as("price sale is set on categoryproduct").isTrue();

        assertThat(agent.findAll(UniqueUnit.class)).as("findAllUniqueUnits").isNotNull().isNotEmpty().hasSize(4);

        UniqueUnit unit3_1 = agent.findById(UniqueUnit.class, unit3.getId());
        assertThat(unit3_1).as("Expected unit3").isNotNull().satisfies(u -> {
            assertThat(u.getComment()).isNull();
            assertThat(u.getInternalComment()).isNull();
            assertThat(u.getUnitCollection()).isEqualTo(uc2);
        });

        UniqueUnit unit4_1 = agent.findById(UniqueUnit.class, unit4.getId());
        assertThat(unit4_1).as("Expected unit4").isNotNull();
        assertThat(unit4_1.getComment()).as("Comment of unit4").isNotBlank();
        assertThat(unit4_1.getInternalComment()).as("InternalComment of unit4").isNotBlank();
    }
}
