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
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.api.Reply;

/**
 * Importer for Contractor and Manufacturer Prices and PartNo.
 * <p>
 * @author oliver.guenther
 */
@Remote
public interface ContractorPricePartNoImporter {

    /**
     * Imports the Costprices of the contractor form an supplied Xls file in a jacket.
     * <p>
     * The file must look like:
     * <p>
     * Manufacturer PartNo | Manufacturer Cost Price | ....
     * <p>
     * @param contractorManufacturer a contractor which is also a manufacturer.
     * @param inFile                 the in file
     * @param arranger               the arranger
     * @return a aggregated import result.
     * @throws UserInfoException reporting all errors after the import.
     */
    Reply<Void> fromManufacturerXls(TradeName contractorManufacturer, FileJacket inFile, String arranger) throws UserInfoException;

    /**
     * Imports the Contractor Reference Prices and Additional PartNos.
     * <p>
     * The file must look like this: Manufacturer PartNo | GTIN | Name | Contractor Reference Price | ContractorPartNo <br />
     * The following rules must apply:
     * <ul>
     * <li>Either Manufacturer PartNo or GTIN must be set. If both are set, valid and no GTIN is in the database, the GTIN is set</li>
     * <li>Name is ignored</li>
     * <li>ContractorPartNo is optional</li>
     * </ul>
     *
     * @param contractor the contractor for the import
     * @param inFile     the inFile
     * @param arranger   the Arranger
     * @return a reply.
     */
    Reply<Void> fromContractorXls(TradeName contractor, FileJacket inFile, String arranger);
}
