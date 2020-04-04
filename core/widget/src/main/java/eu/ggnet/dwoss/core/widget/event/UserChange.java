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
package eu.ggnet.dwoss.core.widget.event;

import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.rights.api.Authorisation;

/**
 * User Change Event.
 *
 * @author oliver.guenther
 */
public class UserChange {

    private final String username;

    private final Set<Authorisation> allowedRights;

    public UserChange(String username, Set<Authorisation> allowedRights) {
        this.username = Objects.requireNonNull(username);
        this.allowedRights = Objects.requireNonNull(allowedRights);
    }

    /**
     * Returns the username
     *
     * @return the username
     */
    public String username() {
        return username;
    }

    /**
     * Returns the allowed rights of the new user.
     *
     * @return the allowed rights of the new user.
     */
    public Set<Authorisation> allowedRights() {
        return allowedRights;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
