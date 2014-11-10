package eu.ggnet.dwoss.common;

import eu.ggnet.saft.core.authorisation.UserChangeListener;

import java.util.Arrays;

import org.junit.Test;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;

import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_IMAGE_IDS;
import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class AccessCosTest {

    private static class TestListener implements UserChangeListener {

        public String lastUser;

        @Override
        public void loggedIn(String name) {
            this.lastUser = name;
        }

        @Override
        public void loggedOut() {
        }
    };

    @Test
    public void testAuthentication() {
        SampleAccessCos access = new SampleAccessCos();
        Operator operator = new Operator("test1", 1, Arrays.asList(AtomicRight.values()));
        TestListener listener = new TestListener();
        access.addUserChangeListener(listener);

        ClassWithSetEnabled ad1 = new ClassWithSetEnabled();
        ClassWithSetEnabledAndImageIds ad2 = new ClassWithSetEnabledAndImageIds();
        access.add(ad1, IMPORT_IMAGE_IDS);
        access.add(ad2);

        assertFalse("No authentication jet, should be disabled", ad1.isEnabled());
        assertFalse("No authentication jet, should be disabled", ad2.isEnabled());

        access.setRights(operator);
        assertTrue("Successfull authenticated, should be enabled", ad1.isEnabled());
        assertTrue("Successfull authenticated, should be enabled", ad2.isEnabled());

        access.logout();

        assertFalse("No authentication jet, should be disabled", ad1.isEnabled());
        assertFalse("No authentication jet, should be disabled", ad2.isEnabled());

        access.remove(ad2);

        access.quickAuthenticate(operator.getQuickLoginKey()); // This is like successful authentication

        assertTrue("Successfull authenticated, should be enabled", ad1.isEnabled());
        assertFalse("Successfull authenticated, but ad2 is removed, should be disabled", ad2.isEnabled());

        assertEquals("User should be equal", operator.getUsername(), listener.lastUser);
    }

}
