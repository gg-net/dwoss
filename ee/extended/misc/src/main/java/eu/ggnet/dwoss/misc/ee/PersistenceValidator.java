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
package eu.ggnet.dwoss.misc.ee;

import jakarta.ejb.Remote;

import eu.ggnet.dwoss.core.common.FileJacket;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface PersistenceValidator {

    /**
     * This Method Validate all Databases.
     * It's validate:
     * - RedTape
     * - UniqueUnit
     * - Stock
     * <p/>
     * @return a Filejacket where a xls from the JExcel api is, that contains all Errors, or null if DB is Valide.
     */
    // HINT: Don't change it to optional, will not work via Wildfly serialisation.
    FileJacket validateDatabase();
}
