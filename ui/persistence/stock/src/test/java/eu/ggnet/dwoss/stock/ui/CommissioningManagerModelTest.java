package eu.ggnet.dwoss.stock.ui;

import java.util.Date;

import org.junit.Test;

import eu.ggnet.dwoss.stock.ee.entity.*;

import static eu.ggnet.dwoss.stock.ee.entity.StockTransactionStatusType.FAILED;
import static eu.ggnet.dwoss.stock.ee.entity.StockTransactionStatusType.PREPARED;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class CommissioningManagerModelTest {

    @Test
    public void testEqualMessage() {
        StockTransaction t1 = new StockTransaction();
        StockTransaction t2 = new StockTransaction();
        Stock s1 = new Stock(1);
        Stock s2 = new Stock(2);
        t1.setSource(s1);
        t2.setSource(s1);
        t1.setDestination(s2);
        t2.setDestination(s2);
        t1.setType(StockTransactionType.TRANSFER);
        t2.setType(StockTransactionType.TRANSFER);
        t1.addStatus(new StockTransactionStatus(PREPARED, new Date()));
        t2.addStatus(new StockTransactionStatus(PREPARED, new Date()));
        assertNull(CommissioningManagerModel.equalStateMessage(t1, t2));

        t1.setSource(s2);
        assertNotNull(CommissioningManagerModel.equalStateMessage(t1, t2));
        t1.setSource(s1);
        assertNull(CommissioningManagerModel.equalStateMessage(t1, t2));

        t1.setDestination(s1);
        assertNotNull(CommissioningManagerModel.equalStateMessage(t1, t2));
        t1.setDestination(s2);
        assertNull(CommissioningManagerModel.equalStateMessage(t1, t2));

        t1.setType(StockTransactionType.ROLL_IN);
        assertNotNull(CommissioningManagerModel.equalStateMessage(t1, t2));
        t1.setType(StockTransactionType.TRANSFER);
        assertNull(CommissioningManagerModel.equalStateMessage(t1, t2));

        t2.addStatus(new StockTransactionStatus(FAILED, new Date()));
        assertNotNull(CommissioningManagerModel.equalStateMessage(t1, t2));

    }
}
