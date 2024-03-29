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
package eu.ggnet.dwoss.uniqueunit.ee;

import jakarta.ejb.Remote;

import eu.ggnet.dwoss.uniqueunit.ee.entity.*;
import eu.ggnet.dwoss.core.system.persistence.RemoteAgent;

/**
 * The UniqueUnitAgent.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface UniqueUnitAgent extends RemoteAgent {

    /**
     * Finds a Product with the partNo.
     * <p/>
     * @param partNo the partNo as search
     * @return the found product or null.
     */
    Product findProductByPartNo(String partNo);

    /**
     * Finds a UniqueUnit by the Identifier.
     * <p/>
     * @param type       the identifierType
     * @param identifier the identifier
     * @return the uniqueUnit or null.
     */
    UniqueUnit findUnitByIdentifierEager(UniqueUnit.Identifier type, String identifier);

    /**
     * Finds a Product with the partNo, eager loading all resources.
     * <p/>
     * @param partNo the partNo as search
     * @return the found product or null.
     */
    Product findProductByPartNoEager(String partNo);

}
