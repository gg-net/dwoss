/*
 * Copyright (C) 2020 GG-Net GmbH
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

/**
 * Local Rights Api.
 *
 * @author oliver.guenther
 */
@Local
public interface UserApiLocal {

    /**
     * Documentation copied from {@link UserApi#findByName(java.lang.String) }.
     *
     * Searches for the {@link Operator} with the submitted username and returns a {@link User} representation of that Operator.
     *
     * @param username name of the Operator, must not be null or blank.
     * @return User - representation of the found Operator or null.
     * @throws IllegalArgumentException if the submitted name is blank or the user is not found.
     * @throws NullPointerException     if the submitted name is null.
     */
    User findByName(String username) throws IllegalArgumentException,NullPointerException;

}
