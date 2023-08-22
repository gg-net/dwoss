package eu.ggnet.dwoss.stock.ee.test;

import org.junit.Test;

import eu.ggnet.dwoss.stock.ee.entity.*;

import static org.junit.Assert.*;

public class BidirectionalTest {

    @Test
    public void testUnitToStockAndTransaction() {
        Stock s0 = new Stock(0);
        Stock s1 = new Stock(1);

        StockUnit su0 = TestStockUnitFactory.makeStockUnitWithId(0);
        StockUnit su1 = TestStockUnitFactory.makeStockUnitWithId(1);

        s0.addUnit(su0);
        assertEquals(1, s0.getUnits().size());
        assertTrue(su0.isInStock());
        assertFalse(su0.isInTransaction());
        assertEquals(s0, su0.getStock());

        su0.setStock(s1);

        assertTrue(s0.getUnits().isEmpty());
        assertEquals(1, s1.getUnits().size());
        assertTrue(su0.isInStock());
        assertFalse(su0.isInTransaction());
        assertEquals(s1, su0.getStock());

        StockTransaction t1 = new StockTransaction();
        t1.addPosition(new StockTransactionPosition(su0));
        t1.addPosition(new StockTransactionPosition(su1));

        // su0 should be invalid du to sock and transaction
        assertNotNull(su0.getValidationViolations());
        assertNull(su1.getValidationViolations());

        s1.removeUnit(su0);

        assertTrue(s0.getUnits().isEmpty());
        assertTrue(s1.getUnits().isEmpty());
        assertEquals(2, t1.getPositions().size());
        assertFalse(su0.isInStock());
        assertTrue(su0.isInTransaction());
    }

}
