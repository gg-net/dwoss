package eu.ggnet.dwoss.spec.itest;

import java.util.List;

import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;
import eu.ggnet.dwoss.spec.ee.entity.ProductModel;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;
import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.api.SpecApi;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.ProductModelEao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 *
 * @author bastian.venz
 */
@RunWith(Arquillian.class)
public class ProductModelEaoIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;
    
    private final String NAME = "Model 1";

    @Test
    public void testFind() throws Exception {
        utx.begin();
        em.joinTransaction();
        ProductFamily family = new ProductFamily("Family1");
        ProductSeries series = new ProductSeries(TradeName.SAMSUNG, ProductGroup.MISC, "TestSeries");
        em.persist(series);
        family.setSeries(series);
        em.persist(family);
        ProductModel model = new ProductModel(NAME);
        model.setFamily(family);
        em.persist(model);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        ProductModelEao productModelEao = new ProductModelEao(em);
        ProductModel productModel = productModelEao.find("Model 1");
        assertNotNull(productModel);
        assertEquals(model, productModel);
        assertNull(productModelEao.find("No Model"));

        List<SpecApi.NameId> result = productModelEao.findAsNameId(family.getId());
        assertThat(result).isNotEmpty().hasSize(1);
        assertThat(result.get(0).name()).isEqualTo(NAME);

        utx.commit();
    }
}
