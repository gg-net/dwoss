package eu.ggnet.dwoss.stock.ee.test;

import org.junit.Test;

import eu.ggnet.dwoss.stock.ee.entity.StockTransactionType;

import static org.junit.Assert.assertEquals;

public class StockTransactionTypeTest {

    @Test
    public void ensureOrder() {
        // Saftynet for the developer
        assertEquals(0, StockTransactionType.TRANSFER.ordinal());
        assertEquals(1, StockTransactionType.ROLL_IN.ordinal());
        assertEquals(2, StockTransactionType.ROLL_OUT.ordinal());
        assertEquals(3, StockTransactionType.DESTROY.ordinal());
    }

}
