package eu.ggnet.dwoss.uniqueunit.itest;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.common.values.ProductGroup;

import java.util.Date;
import java.util.List;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.apache.commons.lang3.time.DateUtils;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.entity.*;
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

    @EJB
    private UniqueUnitApi api;    
    
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
        
        ShopCategory sh1 = new ShopCategory();
        sh1.setName("Category 1");
        sh1.setShopId(1);

                ShopCategory sh2 = new ShopCategory();
        sh2.setName("Category 1");
        sh2.setShopId(1);

        Product p1 = new Product(ProductGroup.DESKTOP, TradeName.ACER, "LX.11111.222", "Verition Stein");
        p1.setDescription("Ein Tolles Gerät");
        p1.setPrice(PriceType.MANUFACTURER_COST, 200.0, "JUnit - Testcase");
        p1.setPrice(PriceType.CONTRACTOR_REFERENCE, 240.0, "JUnit - Testcase");
        p1.addFlag(Product.Flag.PRICE_FIXED);
        p1.setShopCategory(sh1);
 
        Product p2 = new Product(ProductGroup.COMMENTARY, TradeName.DELL, "DL", "Dienstleistung 1h");
        p2.setDescription("Eine Dienstleistungs Stunde");

        UniqueUnit unit1 = new UniqueUnit(p1, DateUtils.addDays(now, -5), "");
        unit1.setIdentifier(SERIAL, "ROFFFLAASSS");
        unit1.setPrice(PriceType.SALE, 125, "JUnit - Testcase");
        unit1.addFlag(UniqueUnit.Flag.PRICE_FIXED);
        unit1.setContractor(TradeName.ONESELF);
        unit1.setComment("Ein Commentar");
        unit1.setCondition(UniqueUnit.Condition.AS_NEW);
       
        UniqueUnit unit2 = new UniqueUnit(p1, now, "lila");
        unit2.addHistory("Aufgenommen als Sopo 332");
        unit2.addHistory("Zerlegt weil kaput");
        unit2.setIdentifier(SERIAL, "DBCFDASFDSADEF");
        unit2.setContractor(TradeName.ONESELF);
        unit2.setComment("Auch ein Commentar");
        unit2.setCondition(UniqueUnit.Condition.AS_NEW);
    
        UniqueUnit unit3 = new UniqueUnit();
        unit3.setProduct(p1);
        unit3.setMfgDate(DateUtils.addDays(now, -5));
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

  
        em.persist(sh1);
        em.persist(sh2);
        
        em.persist(p1);
        em.persist(p2);

   
        em.persist(unit1);
        em.persist(unit2);
        em.persist(unit3);
        em.persist(unit4);

        utx.commit();
      

        assertThat(agent.findAll(UniqueUnit.class)).as("findAllUniqueUnits").isNotNull().isNotEmpty().hasSize(4);

        UniqueUnit unit3_1 = agent.findById(UniqueUnit.class, unit3.getId());
        assertThat(unit3_1).as("Expected unit3").isNotNull().satisfies(u -> {
            assertThat(u.getComment()).isNull();
            assertThat(u.getInternalComment()).isNull();
        });

        UniqueUnit unit4_1 = agent.findById(UniqueUnit.class, unit4.getId());
        assertThat(unit4_1).as("Expected unit4").isNotNull();
        assertThat(unit4_1.getComment()).as("Comment of unit4").isNotBlank();
        assertThat(unit4_1.getInternalComment()).as("InternalComment of unit4").isNotBlank();
        
        List<eu.ggnet.dwoss.uniqueunit.api.ShopCategory> categories = api.findAllShopCategories();
        assertThat(categories).hasSize(2);
    }
}
