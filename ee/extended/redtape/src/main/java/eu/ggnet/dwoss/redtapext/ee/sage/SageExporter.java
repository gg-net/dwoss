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
package eu.ggnet.dwoss.redtapext.ee.sage;

import java.util.Date;

import jakarta.ejb.Remote;

import eu.ggnet.dwoss.core.common.FileJacket;

/**
 * GsOffice Exporter.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface SageExporter {

    /**
     * Exports the all Documents in the Range as the specified XML lines.
     * <p/>
     * @param start the starting date
     * @param end   the ending date
     * @return an Xml document, ready for import in GS Office.
     */
    FileJacket toXml(Date start, Date end);
}
