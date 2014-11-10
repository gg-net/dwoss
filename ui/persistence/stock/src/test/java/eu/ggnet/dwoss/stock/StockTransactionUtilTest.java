package eu.ggnet.dwoss.stock;

import eu.ggnet.dwoss.stock.StockTransactionUtil;

import java.util.Date;

import org.junit.Test;

import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatus;
import eu.ggnet.dwoss.stock.entity.StockTransactionType;

import static eu.ggnet.dwoss.stock.entity.StockTransactionStatusType.*;
import static org.junit.Assert.*;

/**
 *
 */
public class StockTransactionUtilTest {

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
        assertNull(StockTransactionUtil.equalStateMessage(t1, t2));

        t1.setSource(s2);
        assertNotNull(StockTransactionUtil.equalStateMessage(t1, t2));
        t1.setSource(s1);
        assertNull(StockTransactionUtil.equalStateMessage(t1, t2));

        t1.setDestination(s1);
        assertNotNull(StockTransactionUtil.equalStateMessage(t1, t2));
        t1.setDestination(s2);
        assertNull(StockTransactionUtil.equalStateMessage(t1, t2));

        t1.setType(StockTransactionType.ROLL_IN);
        assertNotNull(StockTransactionUtil.equalStateMessage(t1, t2));
        t1.setType(StockTransactionType.TRANSFER);
        assertNull(StockTransactionUtil.equalStateMessage(t1, t2));

        t2.addStatus(new StockTransactionStatus(FAILED, new Date()));
        assertNotNull(StockTransactionUtil.equalStateMessage(t1, t2));

    }
}
