package eu.ggnet.dwoss.receipt.unit.chain.string;

import eu.ggnet.dwoss.receipt.unit.chain.string.MustStartWith;

import org.junit.Test;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class MustStartWithTest {

    /**
     * Test of execute method, of class RemoveIfStartsWith.
     */
    @Test
    public void testExecute() {
        MustStartWith r = new MustStartWith("S");
        assertEquals(ValidationStatus.OK, r.execute("SABC").getValid());
        assertEquals(ValidationStatus.ERROR, r.execute("ABC").getValid());
    }
}
