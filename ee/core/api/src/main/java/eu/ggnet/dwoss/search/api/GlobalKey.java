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
package eu.ggnet.dwoss.search.api;

import java.io.Serializable;

import lombok.Value;

/**
 * Global Key to identify an entiy uniquely.
 *
 * @author oliver.guenther
 */
@Value
public class GlobalKey implements Serializable {

    /**
     * Identifies a application component. For now, we know which components exist in every final depolyment.
     * If this changes in the future, we must convert the componets to some string representation.
     */
    public static enum Component {
        CUSTOMER, UNIQUE_UNIT
    }

    private final Component component;

    /**
     * A unique database identifier.
     */
    private final long id;
}
