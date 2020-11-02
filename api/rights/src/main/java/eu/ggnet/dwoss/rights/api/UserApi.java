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

    boolean authenticate(String username, byte[] password) throws IllegalArgumentException, NullPointerException;

    int getQuickLoginKey(long userId) throws IllegalArgumentException;

    User create(String username) throws IllegalArgumentException, NullPointerException;

    User updateUsername(long userId, String username) throws IllegalArgumentException, NullPointerException;
    
    User addRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException;

    User removeRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException;

    User addGroup(long userId, long groupId) throws IllegalArgumentException;

    User removeGroup(long userId, long groupId) throws IllegalArgumentException;

    void delete(long userId) throws IllegalArgumentException;

    User findById(long userId) throws IllegalArgumentException;

    User findByName(String username) throws IllegalArgumentException, NullPointerException;

    List<User> findAll();
}
