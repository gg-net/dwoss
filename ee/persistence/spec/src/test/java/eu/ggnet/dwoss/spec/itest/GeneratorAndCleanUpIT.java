package eu.ggnet.dwoss.spec.itest;

import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import java.util.List;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.assist.gen.SpecDeleteUtils;
import eu.ggnet.dwoss.spec.ee.assist.gen.SpecGenerator;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import static eu.ggnet.dwoss.spec.ee.entity.QProductSpec.productSpec;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for correct injection of EntityManagers
 */
@RunWith(Arquillian.class)
public class GeneratorAndCleanUpIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    @Specs
    private EntityManager em;

    @EJB
    private SpecAgent specAgent;

    @Test
    public void testGeneratorAndCleanup() throws Exception {
        SpecGenerator g = new SpecGenerator();

        final int SIZE = 100;

        utx.begin();
        em.joinTransaction();
        for (int i = 0; i < SIZE; i++) {
            g.makeRandom(em);
        }
        utx.commit();

        utx.begin();
        em.joinTransaction();
        List<ProductSpec> specs = new JPAQuery<ProductSpec>(em).from(productSpec).fetch();
        assertThat(specs).as("There should be " + SIZE + " ProductSpecs").isNotNull().isNotEmpty().hasSize(SIZE);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        SpecDeleteUtils.deleteAll(em);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        assertThat(SpecDeleteUtils.validateEmpty(em)).isNull();
        utx.commit();

    }

}
