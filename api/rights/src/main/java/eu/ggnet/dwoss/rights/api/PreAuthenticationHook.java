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
package eu.ggnet.dwoss.rights.api;

import javax.ejb.Local;

import eu.ggnet.dwoss.core.common.UserInfoException;

/**
 * This Service can be implemented to create a alternative authentication of users.
 * <p>
 * @author bastian.venz
 */
@Local
public interface PreAuthenticationHook {

    /**
     * This method should returns true if the user with the given username and password is valid and authenticate.
     * <p>
     * @param username the username of the user.
     * @param password the password
     * @return true if the authentication was successful.
     * @throws eu.ggnet.dwoss.core.common.UserInfoException if the user did something wrong.
     */
    boolean authenticate(String username, char[] password) throws UserInfoException;
}
