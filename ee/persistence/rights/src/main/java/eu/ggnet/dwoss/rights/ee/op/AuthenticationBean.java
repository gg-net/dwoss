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

import java.util.Arrays;
import java.util.Set;

import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.PreAuthenticationHook;
import eu.ggnet.dwoss.rights.ee.eao.OperatorEao;
import eu.ggnet.dwoss.rights.ee.entity.Operator;

/**
 *
 * @author Bastian Venz
 *
 * @deprecated Use UserApi.authenticate()
 */
@Deprecated
@Stateless
public class AuthenticationBean implements Authentication {

    private static final Logger L = LoggerFactory.getLogger(AuthenticationBean.class);

    @Inject
    private OperatorEao userEao;

    @Inject
    private Instance<PreAuthenticationHook> service;

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
        L.info("login(user={}, password=xxxxxxx) requested", username);
        //find users by Username
        Operator op = userEao.findByUsername(username);
        if ( op == null ) throw new UserInfoException("User " + username + " ist noch nicht angelegt");
        if ( !service.isAmbiguous() && !service.isUnsatisfied() && service.get().authenticate(username, password) ) {
            L.info("login(user={}, password=xxxxxxx) via AuthenticationService successful.", username);
            return op.toDto();
        } else {
            if ( op.getPassword() != null && op.getSalt() != null
                    && Arrays.equals(op.getPassword(), PasswordUtil.hashPassword(password, op.getSalt())) ) {
                L.info("login(user={}, password=xxxxxxx) via internal database successful.", username);
                return op.toDto();
            }

        }
        L.warn("login(user={}, password=xxxxxxx) failed.", username);
        throw new UserInfoException("Authentifizierung nicht gelungen!");
    }

}
