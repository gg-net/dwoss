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

import java.util.Set;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.core.common.UserInfoException;

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
     * @throws UserInfoException is thrown when username and/or password is wrong.
     */
    Operator login(String username, char[] password) throws UserInfoException;

}
