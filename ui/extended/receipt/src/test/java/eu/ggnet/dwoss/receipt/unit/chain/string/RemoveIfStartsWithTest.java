package eu.ggnet.dwoss.receipt.unit.chain.string;

import eu.ggnet.dwoss.receipt.unit.chain.string.RemoveIfStartsWith;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class RemoveIfStartsWithTest {

    /**
     * Test of execute method, of class RemoveIfStartsWith.
     */
    @Test
    public void testExecute() {
        RemoveIfStartsWith r = new RemoveIfStartsWith("S");
        assertEquals("ABC", r.execute("SABC").getValue());
        assertEquals("ABC", r.execute("ABC").getValue());
    }
}
