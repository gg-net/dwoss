package eu.ggnet.dwoss.rights.op;

import java.util.Set;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.util.UserInfoException;

/**
 *
 * @author Bastian Venz
 */
@Remote
public interface Authentication {

    /**
     * This method returns a {@link Set} of {@link AtomicRight}'s when the {@link Operator} is authorized or throw a {@link UserInfoException} when username
     * and/or password is wrong.
     * <p>
     * @param username the username of the {@link Operator}.
     * @param password the password of the {@link Operator}.
     * @return {@link Set} of {@link AtomicRight}'s when the {@link Operator} is authorized.
     * @throws de.dw.util.UserInfoException is thrown when username and/or password is wrong.
     */
    Operator login(String username, char[] password) throws UserInfoException;

}
