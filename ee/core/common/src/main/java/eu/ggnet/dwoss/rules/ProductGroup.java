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
package eu.ggnet.dwoss.rules;

import eu.ggnet.dwoss.util.INoteModel;

/**
 * All constant ProductGroups.
 *
 * @author oliver.guenther
 */
public enum ProductGroup implements INoteModel {

    /**
     * Represents a Product, which has no physical representative, like a service or a free text.
     *//**
     * Represents a Product, which has no physical representative, like a service or a free text.
     */
    COMMENTARY("!!"),
    MISC("Sonstige"),
    DESKTOP("Desktop"),
    DESKTOP_BUNDLE("Desktop/Monitor Bundle"),
    ALL_IN_ONE("All in one PC"),
    NOTEBOOK("Notebook"),
    MONITOR("Monitor"),
    PROJECTOR("Projektor"),
    TV("TV"),
    SERVER("Server"),
    TABLET_SMARTPHONE("Tablet/SmartPhone"),
    PHONE("SimplePhone");

    private final String note;

    private ProductGroup(String note) {
        this.note = note;
    }

    @Override
    public String getNote() {
        return note;
    }

    public String getName() {
        return note;
    }
}
