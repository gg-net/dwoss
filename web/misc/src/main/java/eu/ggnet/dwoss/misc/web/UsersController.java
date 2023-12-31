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
package eu.ggnet.dwoss.misc.web;

import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import eu.ggnet.dwoss.rights.api.User;

import jakarta.annotation.ManagedBean;
import eu.ggnet.dwoss.rights.api.UserApiLocal;

/**
 *
 * @author oliver.guenther
 */
@Named
@ManagedBean
@Stateless
public class UsersController {

    @Inject
    private UserApiLocal users;

    public List<User> findAll() {
        return users.findAll();
    }

}
