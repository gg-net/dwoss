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
import eu.ggnet.dwoss.spec.ee.eao.ProductSeriesEao;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class ProductSeriesEaoIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;
    
    private final String NAME = "GG-Net uber Multicore";

    @Test
    public void testFindBrandGroupName() throws Exception {
        utx.begin();
        em.joinTransaction();
        ProductSeries series = new ProductSeries(TradeName.SAMSUNG, ProductGroup.MISC, NAME);
        em.persist(series);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        ProductSeriesEao seriesEao = new ProductSeriesEao(em);
        ProductSeries productSeries = seriesEao.find(series.getBrand(), series.getGroup(), series.getName());
        assertThat(seriesEao.find(TradeName.SAMSUNG, ProductGroup.MISC, "Gibbet nich")).isNull();
        assertThat(productSeries).isNotNull();
        assertThat(series).isEqualTo(productSeries);
        
        List<SpecApi.NameId> result = seriesEao.findAsNameId(TradeName.SAMSUNG, ProductGroup.MISC);
        assertThat(result).isNotEmpty().hasSize(1);
        assertThat(result.get(0).name()).isEqualTo(NAME);
        
        result = seriesEao.findAsNameId(TradeName.SAMSUNG, ProductGroup.COMMENTARY);
        assertThat(result).isEmpty();

        utx.commit();
    }
}
