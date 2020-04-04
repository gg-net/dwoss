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
package eu.ggnet.dwoss.price.ee;

import javax.ejb.Remote;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.price.ee.engine.PriceEngineResult;

@Remote
public interface Importer {

    /**
     * Imports the Price Management from an XLS file with a defined form.
     * The Form is as follows
     * <ul>
     * <li>Column 1 (A) = Refurbished Id, Type:Integer</li>
     * <li>Column 2 (C) = Manufacturer PartNo, Type:String</li>
     * <li>Column 4 (E) = Retailer Price, Type:Double</li>
     * <li>Column 7 (H) = Customer Price without Tax, Type:Double</li>
     * <li>Column 9 (J) = Set/Unset PartNoFixed Price, Type:Integer</li>
     * <li>Column 10 (K) = Warranty Id, Type:Integer</li>
     * </ul>
     *
     * @param jacket   the file in a jacket
     * @param arranger
     * @throws UserInfoException
     */
    void fromXls(FileJacket jacket, String arranger) throws UserInfoException;

    /**
     * Uses the Engine in the Background, and imports all Prices direct.
     * <p/>
     * @param arranger the arranger.
     */
    void direct(String arranger);

    /**
     * Store one price result in the Price Engine.
     * <p/>
     * @param pers     the price result
     * @param comment  a optional comment
     * @param arranger the arranger
     */
    void store(PriceEngineResult pers, String comment, String arranger);
}
