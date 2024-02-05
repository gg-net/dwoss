package eu.ggnet.dwoss.spec.itest;

import java.util.List;

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
import eu.ggnet.dwoss.spec.ee.eao.ProductFamilyEao;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 *
 * @author bastian.venz
 */
@RunWith(Arquillian.class)
public class ProductFamilyEaoIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private final String PF1 = "PF1";
    
    /**
     * Test of find method, of class ProductFamilyEao.
     */
    @Test
    public void testFind() throws Exception {
        utx.begin();
        em.joinTransaction();
        ProductFamily productFamily = new ProductFamily(PF1);
        ProductSeries testSeries1 = new ProductSeries(TradeName.HP, ProductGroup.MISC, "TestSeries1");
        em.persist(testSeries1);
        productFamily.setSeries(testSeries1);
        em.persist(productFamily);

        ProductFamily productFamily1 = new ProductFamily("PF2");
        ProductSeries testSeries2 = new ProductSeries(TradeName.HP, ProductGroup.COMMENTARY, "TestSeries2");
        em.persist(testSeries2);
        productFamily1.setSeries(testSeries2);
        em.persist(productFamily1);

        utx.commit();

        utx.begin();
        em.joinTransaction();
        ProductFamilyEao familyEao = new ProductFamilyEao(em);
        ProductFamily testFamily = familyEao.find("PF1");
        assertNotNull(testFamily);
        assertNull(familyEao.find("NoFamily"));
        assertEquals(productFamily.getId(), testFamily.getId());
        
        List<SpecApi.NameId> result = familyEao.findAsNameId(testSeries1.getId());
        assertThat(result).isNotEmpty().hasSize(1);
        assertThat(result.get(0).name()).isEqualTo(PF1);
        
        utx.commit();
    }
}
