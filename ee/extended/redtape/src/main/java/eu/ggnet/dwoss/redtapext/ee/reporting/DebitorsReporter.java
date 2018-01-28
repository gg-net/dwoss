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
package eu.ggnet.dwoss.redtapext.ee.reporting;

import java.util.Date;

import javax.ejb.Remote;

import eu.ggnet.dwoss.util.FileJacket;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface DebitorsReporter {

    /**
     * Creates the Report
     *
     * @param start start intervall for the report
     * @param end end of the report intervall
     * @return a ByteArray represeting the content of an xls file.
     */
    FileJacket toXls(Date start, Date end);
}
