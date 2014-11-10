package eu.ggnet.dwoss.stock.assist.gen;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.StockLocation;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class StockGeneratorOperation {

    public static final String[] STOCK_LOCATION_NAMES = {
        "Regal Endnummer - 0",
        "Regal Endnummer - 1",
        "Regal Endnummer - 2",
        "Regal Endnummer - 3",
        "Regal Endnummer - 4",
        "Regal Endnummer - 5",
        "Regal Endnummer - 6",
        "Regal Endnummer - 7",
        "Regal Endnummer - 8",
        "Regal Endnummer - 9"
    };

    @Inject
    @Stocks
    private EntityManager em;

    public StockGeneratorOperation() {
    }

    public StockGeneratorOperation(EntityManager em) {
        this.em = em;
    }

    public List<Stock> makeStocksAndLocations(int amount) {
        List<Stock> result = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Stock s = new Stock(i);
            s.setName("Lager#" + i);
            em.persist(s);
            for (String name : STOCK_LOCATION_NAMES) {
                StockLocation sl = new StockLocation(name);
                s.addStockLocation(sl);
                em.persist(sl);
            }
            result.add(s);
        }
        return result;
    }

}
