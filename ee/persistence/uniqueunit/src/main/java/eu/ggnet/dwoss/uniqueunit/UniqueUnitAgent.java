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
package eu.ggnet.dwoss.uniqueunit;

import javax.ejb.Remote;

import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.assist.CategoryProductDto;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.util.persistence.RemoteAgent;
import eu.ggnet.saft.api.Reply;

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

    /**
     * Creates or Updates a CategoryProduct based on the dto.
     * If the id is 0 a new categoryproduct is assumed. Deletion of prices is done by setting a price to 0. *
     *
     * @param dto      the dto as basis, must not be null.
     * @param username the user who changed that.
     * @return the created or updated CategoryProduct.
     * @throws NullPointerException if dto is null.
     */
    CategoryProduct createOrUpdate(CategoryProductDto dto, String username) throws NullPointerException;

    /**
     * Delete a category product.
     *
     * @param id the id
     * @return a reply
     */
    Reply<Void> deleteCategoryProduct(long id);

    /**
     * Adds a Unit do the unitCollection.
     *
     * @param unit             the unit.
     * @param unitCollectionId the unitCollectionId
     * @return reply of success.
     */
    Reply<Void> addToUnitCollection(PicoUnit unit, long unitCollectionId);

    /**
     * Sets the UnitCollection null on the unit.
     *
     * @param unit the unit
     * @return reply of success.
     */
    Reply<Void> unsetUnitCollection(PicoUnit unit);

}
