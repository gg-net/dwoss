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
package eu.ggnet.dwoss.spec.ee;

import javax.ejb.Remote;

import eu.ggnet.dwoss.core.common.FileJacket;

/**
 * Operation to allow the XML Export of multiple ProductSpecs.
 * <p>
 * @author oliver.guenther
 */
@Remote
public interface SpecExporter {

    /**
     * Exports specs to an XML till the supplied amount.
     * <p>
     * @param amount the amount to export
     * @return a FileJacket containing all the found specs.
     */
    FileJacket toXml(int amount);
}
