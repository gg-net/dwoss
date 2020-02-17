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
package eu.ggnet.dwoss.report.api;

import javax.ejb.Local;

/**
 * Local Report Api.
 *
 * @author oliver.guenther
 */
@Local
public interface ReportApiLocal {

    /**
     * Returns a html represenation of all report lines referenced by the unique unit id.
     *
     * @param uniqueUnitId the unique unit id.
     * @return a html representation.
     */
    String findReportLinesByUniqueUnitIdAsHtml(long uniqueUnitId);

}
