package eu.ggnet.dwoss.redtape;

import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.stock.entity.Stock;

import java.util.Arrays;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.eao.LogicTransactionEao;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.emo.StockLocationDiscoverer;
import eu.ggnet.dwoss.stock.emo.StockTransactionEmo;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class SupportBean {

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    public LogicTransaction findByDossierId(long id) {
        LogicTransaction lt = new LogicTransactionEao(stockEm).findByDossierId(id);
        lt.getUnits().size();
        return lt;
    }

    public StockUnit changeStock(int uniqueUnitId, int target) {
        StockUnit stockUnit1 = new StockUnitEao(stockEm).findByUniqueUnitId(uniqueUnitId);
        StockLocationDiscoverer discoverer = new StockLocationDiscoverer(stockEm);
        Stock notDestination = stockEm.find(Stock.class, target);
        discoverer.discoverAndSetLocation(stockUnit1, notDestination);
        return stockUnit1;
    }

    public StockUnit rollOut(int uniqueUnitId) {
        StockTransactionEmo transactionEmo = new StockTransactionEmo(stockEm);
        StockUnit stockUnit2 = new StockUnitEao(stockEm).findByUniqueUnitId(uniqueUnitId);
        StockTransaction rollOut = transactionEmo.requestRollOutPrepared(stockUnit2.getStock().getId(), "JUnit", "JUnit");
        rollOut.addUnit(stockUnit2);
        transactionEmo.completeRollOut("JUnit", Arrays.asList(rollOut));
        return stockUnit2;
    }

    public Document changeActual(Document doc, Date date) {
        doc = new DocumentEao(redTapeEm).findById(doc.getId());
        doc.setActual(date);
        return doc;
    }
}
