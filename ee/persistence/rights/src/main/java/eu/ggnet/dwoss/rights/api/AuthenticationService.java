package eu.ggnet.dwoss.rights.api;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.util.UserInfoException;

/**
 * This Service can be implemented to create a alternative authentication of users.
 * <p>
 * @author bastian.venz
 */
@Remote
@Local
public interface AuthenticationService {

    /**
     * This method should returns true if the user with the given username and password is valid and authenticate.
     * <p>
     * @param username the username of the user.
     * @param password the password
     * @return true if the authentication was successful.
     */
    boolean authenticate(String username, char[] password) throws UserInfoException;
}
