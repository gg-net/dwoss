package eu.ggnet.dwoss.receipt.ui.unit.chain.string;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        assertEquals("ABC", r.execute("SABC").value);
        assertEquals("ABC", r.execute("ABC").value);
    }
}
