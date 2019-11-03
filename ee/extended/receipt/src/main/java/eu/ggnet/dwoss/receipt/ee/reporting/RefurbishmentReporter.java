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
package eu.ggnet.dwoss.receipt.ee.reporting;

import java.util.Date;

import javax.ejb.Remote;

import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.core.common.FileJacket;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface RefurbishmentReporter {

    /**
     * Generates the report between two dates for the contractor.
     *
     * @param contractor the contractor to report about.
     * @param start      the starting date
     * @param end        the end date
     * @return an XLS document as FileJacket
     */
    FileJacket toXls(TradeName contractor, Date start, Date end);
}
