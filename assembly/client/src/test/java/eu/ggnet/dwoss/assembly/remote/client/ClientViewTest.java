package eu.ggnet.dwoss.assembly.remote.client;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
