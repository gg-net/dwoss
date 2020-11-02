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

import java.io.Serializable;
import java.util.*;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Discribes an (authenticatable) User.
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface User extends Serializable {

    class Builder extends User_Builder {
    };

    Optional<Long> getId();
    
    String getUsername();

    Optional<Integer> getOptLock();

    List<AtomicRight> getRights();

    List<Group> getGroups();

    default List<AtomicRight> getAllRights() {
        EnumSet<AtomicRight> rights = EnumSet.noneOf(AtomicRight.class);
        rights.addAll(getRights());
        for (Group group : getGroups()) {
            rights.addAll(group.getRights());
        }
        return new ArrayList<>(rights);
    }
}
