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

import java.util.List;

import javax.ejb.Remote;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface UserApi {

    int getQuickLoginKey(long id) throws IllegalArgumentException;
    
    boolean authenticate(String username, byte[] password) throws IllegalArgumentException, NullPointerException;

    User findById(long id) throws IllegalArgumentException;

    User findByName(String username) throws IllegalArgumentException, NullPointerException;

    List<User> findAll();
}
