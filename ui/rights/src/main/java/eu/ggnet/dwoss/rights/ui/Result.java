/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.rights.ui;

import java.util.Optional;

import eu.ggnet.dwoss.rights.api.User;

/**
 * Result object to create and modify a {@link User} via UI.
 *
 * @author mirko.schulze
 */
public class Result {

    private final User user;

    private final Optional<String> password;

    public Result(User user, Optional<String> password) {
        this.user = user;
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public Optional<String> getPassword() {
        return password;
    }

}
