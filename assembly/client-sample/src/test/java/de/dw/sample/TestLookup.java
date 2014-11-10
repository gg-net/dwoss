package de.dw.sample;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Lookup;

import eu.ggnet.saft.core.Server;
import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.assembly.sample.NullAccessCos;
import eu.ggnet.dwoss.assembly.sample.ServerConnCosSample;

import static org.junit.Assert.*;

public class TestLookup {

    public TestLookup() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testLookUp() {
        assertEquals(1, Lookup.getDefault().lookupAll(Server.class).size());
        assertTrue(Lookup.getDefault().lookup(Server.class) instanceof ServerConnCosSample);

        assertEquals(1, Lookup.getDefault().lookupAll(Guardian.class).size());
        assertTrue(Lookup.getDefault().lookup(Guardian.class) instanceof NullAccessCos);
    }

}
