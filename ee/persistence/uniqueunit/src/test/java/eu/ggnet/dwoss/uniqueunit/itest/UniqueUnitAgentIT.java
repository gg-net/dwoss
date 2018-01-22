package eu.ggnet.dwoss.uniqueunit.itest;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.entity.dto.CategoryProductDto;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.itest.support.ArquillianProjectArchive;
import eu.ggnet.saft.api.Reply;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class UniqueUnitAgentIT extends ArquillianProjectArchive {

    @EJB
    private UniqueUnitAgent agent;

    @Inject
    @UniqueUnits
    private EntityManager em;

    @Inject
    UserTransaction utx;

    @Test
    public void testCreateOrUpdateCategoryProduct() throws Exception {
        // Create a some Products.
        utx.begin();
        em.joinTransaction();

        Product p1 = new Product(ProductGroup.DESKTOP, TradeName.ACER, "LX.11111.222", "Verition Stein");
        p1.setDescription("Ein Tolles Gerät");
        p1.setPrice(PriceType.MANUFACTURER_COST, 200.0, "JUnit - Testcase");
        p1.setPrice(PriceType.CONTRACTOR_REFERENCE, 240.0, "JUnit - Testcase");
        p1.addFlag(Product.Flag.PRICE_FIXED);

        Product p2 = new Product(ProductGroup.COMMENTARY, TradeName.DELL, "DL", "Dienstleistung 1h");
        p2.setDescription("Eine Dienstleistungs Stunde");

        em.persist(p1);
        em.persist(p2);

        utx.commit();

        // Store a simple cp.
        CategoryProductDto dto1 = new CategoryProductDto();
        dto1.setName("CP1");
        dto1.setDescription("Some Description");
        CategoryProduct cp = agent.createOrUpdate(dto1, "Test");
        assertThat(cp).as("CategroyProduct").isNotNull();

        dto1.setId(cp.getId());
        dto1.getProducts().add(new PicoProduct(p1.getId(), "irrelevant"));
        dto1.getPrices().put(PriceType.SALE, 200.0);

        long lastid = cp.getId();

        cp = agent.createOrUpdate(dto1, "TEst");

        assertThat(cp).as("CategroyProtuct").isNotNull().as("CategroyProtuct id is equal").returns(lastid, CategoryProduct::getId);
        assertThat(cp.getProducts()).contains(p1);
        assertThat(cp.getPrice(PriceType.SALE)).isEqualTo(200.0);

        Reply<Void> reply = agent.deleteCategoryProduct(cp.getId());
        assertThat(reply).isNotNull().returns(true, Reply::hasSucceded);

        CategoryProduct notFound = agent.findById(CategoryProduct.class, cp.getId());
        assertThat(notFound).isNull();
    }
}
