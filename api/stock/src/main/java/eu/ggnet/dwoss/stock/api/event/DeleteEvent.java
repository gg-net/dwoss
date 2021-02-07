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
package eu.ggnet.dwoss.stock.api.event;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Informs about a successful deletion of one or more units.
 *
 * @author oliver.guenther
 */
public class DeleteEvent implements Serializable {

    private final List<Long> uniqueUnitIds;

    private final String arranger;

    private final String comment;

    public DeleteEvent(List<Long> uniqueUnitIds, String arranger, String comment) {
        this.uniqueUnitIds = Objects.requireNonNull(uniqueUnitIds);
        if ( uniqueUnitIds.isEmpty() ) throw new IllegalArgumentException("ids must not be empty");
        this.arranger = Objects.requireNonNull(arranger, "arranger must not be null");
        this.comment = Objects.requireNonNull(comment, "comment must not be null");
    }

    public List<Long> uniqueUnitIds() {
        return uniqueUnitIds;
    }

    public String arranger() {
        return arranger;
    }

    public String comment() {
        return comment;
    }

    @Override
    public String toString() {
        return "DeleteEvent{" + "uniqueUnitIds=" + uniqueUnitIds + ", arranger=" + arranger + ", comment=" + comment + '}';
    }

}
