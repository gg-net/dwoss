package eu.ggnet.dwoss.uniqueunit.itest;


import jakarta.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.dwoss.uniqueunit.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class UniqueUnitApiIT extends ArquillianProjectArchive {

    @EJB
    UniqueUnitApi api;

    @Test
    public void createShopCategory() throws UserInfoException {
        var sc1 = new eu.ggnet.dwoss.uniqueunit.api.ShopCategory.Builder().name("Category 1").shopId(1).build();
        var sc2 = new eu.ggnet.dwoss.uniqueunit.api.ShopCategory.Builder().name("Category 2").shopId(2).build();

        var dbsc1 = api.create(sc1);
        var dbsc2 = api.create(sc2);

        assertThat(dbsc1.id()).as("Database id").isGreaterThan(0);
        assertThat(dbsc1.name()).isEqualTo(sc1.name());
        assertThat(dbsc1.shopId()).isEqualTo(sc1.shopId());

        assertThat(dbsc2.id()).as("Database id").isGreaterThan(0);
        assertThat(dbsc2.name()).isEqualTo(sc2.name());
        assertThat(dbsc2.shopId()).isEqualTo(sc2.shopId());

    }
}
