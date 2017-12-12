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
package eu.ggnet.dwoss.price.imex;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.FileJacket;

/**
 * The Contractor Exporter.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface ContractorPricePartNoExporter {

    /**
     * Exports all Products which have no Contractor Reference Price and AddiditionalPartNo of the supplied contractor.
     *
     * @param contractor the contractor as filter
     * @return a file jacket wrapping an XLS Document containing PartNo and Name of the incomplete products.
     */
    FileJacket toContractorMissingXls(TradeName contractor);

    /**
     * Exports all Products of the supplied contractor.
     *
     * @param contractor the contractor as filter
     * @return a file jacket wrapping an XLS Document containing PartNo and Name of the incomplete products.
     */
    FileJacket toContractorXls(TradeName contractor);

    /**
     * Exports all Products which have no Costprice of the manufacturer.
     *
     * @param contractor a contractor which is also a manufacturer.
     * @return a file jacket wrapping an XLS Document containing PartNo and Name of the incomplete products.
     */
    FileJacket toManufacturerMissingXls(TradeName contractor);
}
