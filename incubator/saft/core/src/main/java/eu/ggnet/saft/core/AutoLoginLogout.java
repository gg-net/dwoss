/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.saft.core;

/**
 *
 * @author oliver.guenther
 */
public interface AutoLoginLogout {

    /**
     * Change the Timeout.
     *
     * @param timeInSeconds the timeout in seconds, 0 or less stops the auto logout.
     */
    void setTimeout(int timeInSeconds);

    /**
     * Shows the Authenticator manually.
     */
    void showAuthenticator();
}
