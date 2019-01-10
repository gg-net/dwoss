/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.rights.ee.op;

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
import eu.ggnet.dwoss.rights.ee.api.AuthenticationService;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
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
        L.info("Authentication request(user={})", username);
        //find users by Username
        List<Operator> result = rightsEm.createNamedQuery("Operator.byUsername", Operator.class).setParameter(1, username).getResultList();
        if ( result.isEmpty() ) throw new UserInfoException("User " + username + " ist noch nicht angelegt");
        Operator op = result.get(0);
        if ( !service.isAmbiguous() && !service.isUnsatisfied() ) {
            if ( service.get().authenticate(username, password) ) {
                eu.ggnet.dwoss.rights.api.Operator login = op.toDto();
                L.info("Authentication successful: {}", login);
                return login;
            }
        } else {
            if ( op.getPassword() != null && op.getSalt() != null
                    && Arrays.equals(op.getPassword(), PasswordUtil.hashPassword(password, op.getSalt())) ) {
                eu.ggnet.dwoss.rights.api.Operator login = op.toDto();
                L.info("Authentication successful: {}", login);
                return login;
            }

        }
        L.info("Authentication denied");
        throw new UserInfoException("Authentifizierung nicht gelungen!");
    }

}
