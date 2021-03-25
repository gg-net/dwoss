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
package eu.ggnet.dwoss.report.api;

import java.io.Serializable;
import java.util.List;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Contains all Reported Information about a unit.
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface SimpleReportUnit extends Serializable {

    class Builder extends SimpleReportUnit_Builder {
    }

    /**
     * Returns all lines for this unit
     *
     * @return all lines for this unit.
     */
    List<SimpleReportLine> lines();

    /**
     * Returns the uniqueUnitId
     *
     * @return the uniqueUnitId
     */
    Long uniqueUnitId();

}
