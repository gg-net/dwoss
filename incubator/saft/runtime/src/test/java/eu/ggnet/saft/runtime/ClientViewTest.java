package eu.ggnet.saft.runtime;

import org.junit.Test;

import static org.junit.Assert.*;

public class ClientViewTest {

    @Test
    public void testResorces() {
        assertNotNull(ClientView.loadIcon());
        try {
            ClientView.loadBundle();
        } catch (Exception e) {
            fail("Bundle not found, " + e.getMessage());
        }
    }

}
