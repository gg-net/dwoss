package eu.ggnet.dwoss.stock.itest;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.eao.StockLocationEao;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.StockLocation;
import eu.ggnet.dwoss.stock.itest.support.ArquillianProjectArchive;

import static eu.ggnet.dwoss.stock.assist.gen.StockGeneratorOperation.STOCK_LOCATION_NAMES;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class StockLocationEaoIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    @Stocks
    private EntityManager em;

    @Test
    public void testFind() throws Exception {

        StockLocationEao sls = new StockLocationEao(em);

        utx.begin();
        em.joinTransaction();
        Stock laden = new Stock(0);
        laden.setName("Laden");
        for (String name : STOCK_LOCATION_NAMES) {
            laden.addStockLocation(new StockLocation(name));
        }
        em.persist(laden);

        Stock lager = new Stock(1);
        lager.setName("Lager");
        for (String name : STOCK_LOCATION_NAMES) {
            lager.addStockLocation(new StockLocation(name));
        }
        em.persist(lager);
        utx.commit();

        utx.begin();
        em.joinTransaction();

        List<StockLocation> stockLocations = sls.findAll();

        assertEquals(STOCK_LOCATION_NAMES.length * 2, stockLocations.size());

        stockLocations = sls.find("%0");
        assertEquals(2, stockLocations.size());
        assertEquals(STOCK_LOCATION_NAMES[0], stockLocations.get(0).getName());

        stockLocations = sls.find("*0");
        assertEquals(2, stockLocations.size());
        assertEquals(STOCK_LOCATION_NAMES[0], stockLocations.get(0).getName());

        stockLocations = sls.find("*5");
        assertEquals(2, stockLocations.size());
        assertEquals(STOCK_LOCATION_NAMES[5], stockLocations.get(0).getName());

        stockLocations = sls.find(laden, "*0");
        assertEquals(1, stockLocations.size());
        assertEquals(STOCK_LOCATION_NAMES[0], stockLocations.get(0).getName());
        assertEquals(laden, stockLocations.get(0).getStock());
        utx.commit();
    }
}
