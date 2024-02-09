package eu.ggnet.dwoss.redtapext.op.itest.support;

import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;

import java.util.*;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.LogicTransactionEao;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;

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

    @Inject
    private StockUnitEao stockUnitEao;

    @Inject
    private StockTransactionEmo stockTransactionEmo;

    @Inject
    private LogicTransactionEao logicTransactionEao;

    public LogicTransaction findByDossierId(long id) {
        LogicTransaction lt = logicTransactionEao.findByDossierId(id);
        lt.getUnits().size();
        return lt;
    }

    public StockUnit changeStock(int uniqueUnitId, int target) {
        StockUnit stockUnit1 = stockUnitEao.findByUniqueUnitId(uniqueUnitId);
        Stock notDestination = stockEm.find(Stock.class, target);
        stockUnit1.setStock(notDestination);
        return stockUnit1;
    }

    public StockUnit rollOut(int uniqueUnitId) {
        StockUnit stockUnit2 = stockUnitEao.findByUniqueUnitId(uniqueUnitId);
        StockTransaction rollOut = stockTransactionEmo.requestRollOutPrepared(stockUnit2.getStock().getId(), "JUnit", "JUnit");
        rollOut.addUnit(stockUnit2);
        stockTransactionEmo.completeRollOut("JUnit", Arrays.asList(rollOut));
        return stockUnit2;
    }

    public Document changeActual(Document doc, Date date) {
        doc = new DocumentEao(redTapeEm).findById(doc.getId());
        doc.fetchEager();
        doc.setActual(date);
        return doc;
    }

    public List<Document> findDocumentsBetweenDates(Date start, Date end, DocumentType... types) {
        return new DocumentEao(redTapeEm).findDocumentsBetweenDates(start, end, types);
    }

}
