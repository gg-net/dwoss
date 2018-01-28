/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.redtapext;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 *
 * @author oliver.guenther
 */
public class SystemErrListener extends RunListener {

    @Override
    public void testFailure(Failure failure) throws Exception {
        System.err.println("-------------------------------------------------------");
        System.err.println("Failure in " + failure.getDescription());
        System.err.println("-------------------------------------------------------");
        System.err.println(failure.getTrace());
    }
}
