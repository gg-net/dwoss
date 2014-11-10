package eu.ggnet.dwoss.stock.emo;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.stock.eao.StockLocationEao;
import eu.ggnet.dwoss.stock.entity.*;

/**
 * This class captures the automatism for the stock location discovery
 */
public class StockLocationDiscoverer {

    private final static Logger L = LoggerFactory.getLogger(StockLocationDiscoverer.class);

    private final StockLocationEao stockLocationEao;

    public StockLocationDiscoverer(EntityManager em) {
        stockLocationEao = new StockLocationEao(em);
    }

    /**
     * Returns a StockLocation for the unitId or null if none found
     *
     * @param unitId the unit id
     * @param stock  the stock in which the location is to be searched
     * @return a StockLocation for the unitId or null if none found
     */
    public StockLocation discover(Stock stock, String unitId) {
        if ( stock == null ) {
            L.warn("Stock is null");
            return null;
        }
        if ( unitId == null ) {
            L.info("UnitId is null");
            return null;
        }
        List<StockLocation> locations = stockLocationEao.find(stock, "*" + unitId.charAt(unitId.length() - 1));
        if ( locations.size() == 1 ) {
            L.debug("Found by autodiscovery {}", locations.get(0));
            return locations.get(0);
        }
        return null;
    }

    /**
     * Discovers and sets the location
     *
     * @param stockUnit the stockUnit which should be set to a location
     * @param stock     the stock which narrows down the location
     * @return ture if a location was discovert and set otherwise false, which means only the stock was set.
     */
    public boolean discoverAndSetLocation(StockUnit stockUnit, Stock stock) {
        if ( stockUnit == null || stock == null ) {
            L.warn("StockUnit={}, Stock={} : one is null, not allowed, dont setting anything", stockUnit, stock);
            return false;
        }
        StockLocation location = discover(stock, stockUnit.getRefurbishId());
        if ( location != null ) {
            stockUnit.setStockLocation(location);
            return true;
        }
        L.warn("autodiscovery for {} not succsessful, falling back to stock", stockUnit.getRefurbishId());
        stockUnit.setStock(stock);
        return false;
    }
}
