/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.web.stub;

import java.util.*;

import jakarta.ejb.Stateless;

import eu.ggnet.dwoss.rights.api.*;

@Stateless
public class UserApiStub implements UserApiLocal {

    private final List<User> users;

    private final Group group;

    public UserApiStub() {
        group = new Group.Builder().setName("TestGruppe").addRights(AtomicRight.VIEW_COST_AND_REFERENCE_PRICES).build();
        users = new ArrayList<>();
        users.add(new User.Builder().setUsername("test").addGroups(group).build());
        users.add(new User.Builder().setUsername("admin").addGroups(group).addRights(AtomicRight.values()).build());
    }

    @Override
    public User findByName(String username) throws IllegalArgumentException, NullPointerException {
        for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
            User user = iterator.next();
            if ( user.getUsername().equals(username) ) return user;
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return users;
    }

}
