package eu.ggnet.dwoss.assembly.remote.select;

import eu.ggnet.dwoss.assembly.remote.select.RemoteMandatorSelectorController;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author oliver.guenther
 */
public class RemoteMandatorSelectorControllerTest {

    @Test
    public void testFxmlExistence() {
        Assert.assertNotNull(RemoteMandatorSelectorController.getFxmlUrl());
    }

}
