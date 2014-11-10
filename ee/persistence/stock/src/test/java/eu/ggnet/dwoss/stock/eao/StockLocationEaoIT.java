package eu.ggnet.dwoss.stock.eao;

import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.StockLocation;
import eu.ggnet.dwoss.stock.assist.StockPu;

import java.util.List;

import javax.persistence.*;

import org.junit.Test;

import static eu.ggnet.dwoss.stock.assist.gen.StockGeneratorOperation.STOCK_LOCATION_NAMES;
import static org.junit.Assert.*;

public class StockLocationEaoIT {

    @Test
    public void testFind() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(StockPu.NAME, StockPu.JPA_IN_MEMORY);
        EntityManager em = emf.createEntityManager();

        StockLocationEao sls = new StockLocationEao(em);

        EntityTransaction tx = em.getTransaction();
        tx.begin();
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
        tx.commit();

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

    }
}
