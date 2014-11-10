package eu.ggnet.dwoss.rights.op;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.AuthenticationService;
import eu.ggnet.dwoss.rights.assist.Rights;
import eu.ggnet.dwoss.rights.entity.Operator;
import eu.ggnet.dwoss.util.UserInfoException;

/**
 *
 * @author Bastian Venz
 */
@Stateless
public class AuthenticationBean implements Authentication {

    private static final Logger L = LoggerFactory.getLogger(AuthenticationBean.class);

    @Inject
    @Rights
    EntityManager rightsEm;

    @Inject
    Instance<AuthenticationService> service;

    /**
     * This method returns a {@link Set} of {@link AtomicRight}'s when the {@link Operator} is authorized or throw a {@link UserInfoException} when username
     * and/or password is wrong.
     * <p>
     * @param username the username of the {@link Operator}.
     * @param password the password of the {@link Operator}.
     * @return {@link Operator} with {@link AtomicRight}'s when the {@link Operator} is authorized.
     * @throws UserInfoException is thrown when username and/or password is wrong.
     */
    @Override
    public eu.ggnet.dwoss.rights.api.Operator login(String username, char[] password) throws UserInfoException {
        //find users by Usernmae
        List<Operator> result = rightsEm.createNamedQuery("Operator.byUsername", Operator.class).setParameter(1, username).getResultList();
        if ( result.isEmpty() ) throw new UserInfoException("User " + username + " ist noch nicht angelegt");
        Operator op = result.get(0);
        if ( !service.isAmbiguous() && !service.isUnsatisfied() ) {
            if ( service.get().authenticate(username, password) ) return op.toDto();
        } else {
            if ( op.getPassword() != null && op.getSalt() != null
                    && Arrays.equals(op.getPassword(), hashPassword(password, op.getSalt())) )
                return op.toDto();
        }
        throw new UserInfoException("Authentifizierung nicht gelungen!");
    }

    /**
     * This hash the given password with the given salt.
     * <p>
     * @param password is the readable Password.
     * @param salt     is the Salt that will used to salt the password.
     * @return return the hashed and salted password.
     */
    private static byte[] hashPassword(char[] password, byte[] salt) {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] pwBytes = new String(password).getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(pwBytes);
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString().getBytes("UTF-8");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new RuntimeException();
        }
    }
}
