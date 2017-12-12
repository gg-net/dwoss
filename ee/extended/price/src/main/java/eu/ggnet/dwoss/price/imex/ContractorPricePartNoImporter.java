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

import java.io.Serializable;
import java.util.Collection;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;

import lombok.Value;

/**
 * Importer for Contractor and Manufacturer Prices and PartNo.
 * <p>
 * @author oliver.guenther
 */
@Remote
public interface ContractorPricePartNoImporter {

    @Value
    public static class ImportResult implements Serializable {

        private final String overview;

        private final String errors;

        public ImportResult(int readAbleLines, int validLines, int importAbleSize, int importedSize, Collection<?> detailedErrors) {
            this.overview = "Lesbare Zeilen: " + readAbleLines + "\n"
                    + "Valide Zeilen: " + validLines + "\n"
                    + "Valide Zeilen für existierende UniqueUnit Daten: " + importAbleSize + "\n"
                    + "Imortierte neue Daten: " + importedSize + "\n";
            StringBuilder sb = new StringBuilder();
            for (Object error : detailedErrors) {
                sb.append(error.toString()).append("\n");
            }
            this.errors = sb.toString();
        }

        public ImportResult(String overview, String errors) {
            this.overview = overview;
            this.errors = errors;
        }
    }

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
    ImportResult fromManufacturerXls(TradeName contractorManufacturer, FileJacket inFile, String arranger) throws UserInfoException;

    /**
     * Imports the Contractor Reference Prices and Additional PartNos.
     * <p>
     * The file must look like:
     * <p>
     * Contractor PartNo (optional) | Manufacturer PartNo (oder EAN) | *ignored* | *ignored* | Contractor Reference Price | EAN (oder Manufacturer)
     * <p>
     * @param contractor the contractor for the import
     * @param inFile     the inFile
     * @param arranger   the Arranger
     * @return a aggregated import result.
     */
    ImportResult fromContractorXls(TradeName contractor, FileJacket inFile, String arranger);
}
