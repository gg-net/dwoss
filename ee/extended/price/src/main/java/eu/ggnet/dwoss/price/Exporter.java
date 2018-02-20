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
package eu.ggnet.dwoss.price;

import javax.ejb.Remote;

import eu.ggnet.dwoss.price.engine.PriceEngineResult;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;

/**
 * Remote Interface for the {@link ExporterOperation}.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface Exporter {

    /**
     * Export PriceManagement as Xls.
     * <p/>
     * @return PriceManagement as Xls.
     */
    FileJacket toXls();

    /**
     * Creates a price compare sheet, expects an xls file with the first column filed with partNos.
     *
     * @param inFile the infile
     * @return the price compare xls outfile.
     */
    FileJacket toXlsByXls(FileJacket inFile);

    /**
     * Loads exactly one Unit as PriceEngineResult.
     *
     * @param refurbishId the unitid
     * @return The PriceEngineResult or Null if Id not found
     * @throws UserInfoException if the unitId is not a Number
     */
    PriceEngineResult load(String refurbishId) throws UserInfoException;

    /**
     * Calculates a Price for on Unit.
     *
     * @param refurbishId the refurbishId
     * @return The PriceEngineResult or Null if Id not found
     */
    PriceEngineResult onePrice(String refurbishId);

}
