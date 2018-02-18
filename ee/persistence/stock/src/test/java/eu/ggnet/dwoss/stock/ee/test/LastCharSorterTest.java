package eu.ggnet.dwoss.stock.ee.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo.LastCharSorter;

import static org.junit.Assert.*;

public class LastCharSorterTest {

    @Test
    public void testSorter() {
        List<String> unsorted = Arrays.asList("11118", "11111", "11117", "11113", "11112", "11115", "71116", "11119", "31110", "51114");
        List<String> sorted = Arrays.asList("31110", "11111", "11112", "11113", "51114", "11115", "71116", "11117", "11118", "11119");
        Collections.sort(unsorted, new LastCharSorter());
        assertEquals(sorted, unsorted);
    }
}
