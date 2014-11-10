package eu.ggnet.dwoss.stock.entity;


import org.junit.Test;

import static org.junit.Assert.*;

public class StockTransactionStatusTypeTest {

    @Test
    public void ensureOrder() {
        // Saftynet for the developer
        assertEquals(0, StockTransactionStatusType.PREPARED.ordinal());
        assertEquals(1, StockTransactionStatusType.COMMISSIONED.ordinal());
        assertEquals(2, StockTransactionStatusType.IN_TRANSFER.ordinal());
        assertEquals(3, StockTransactionStatusType.RECEIVED.ordinal());
        assertEquals(4, StockTransactionStatusType.FAILED.ordinal());
        assertEquals(5, StockTransactionStatusType.CANCELLED.ordinal());
        assertEquals(6, StockTransactionStatusType.COMPLETED.ordinal());
    }

}
