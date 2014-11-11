/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.redtape;

import java.util.List;

import javax.ejb.Remote;

import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.SalesProduct;

import eu.ggnet.dwoss.util.persistence.RemoteAgent;

/**
 * The ReadTape Agent.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface RedTapeAgent extends RemoteAgent {

    /**
     * Removes the instance from the Database.
     * <p/>
     * @param salesProduct the instance to be removed.
     */
    void remove(SalesProduct salesProduct);

    /**
     * Update (Merge) a supplied instance.
     * <p/>
     * @param salesProduct the instance to merge.
     * @return the merged instance.
     */
    SalesProduct merge(SalesProduct salesProduct);

    /**
     * Get the dossiers where the customerId matches without a specific directive
     * This method will Fetch/Eager every entity.
     * <p/>
     * @param customerId the customer id
     * @return the dossiers where the customerId matches and the directive does not match
     */
    List<Dossier> findDossiersOpenByCustomerIdEager(long customerId);

    /**
     * Get the dossiers where the customerId and the directive matches.
     * This method will Fetch/Eager every entity.
     * <p/>
     * @param customerId the cusotmer id
     * @param start      limites the returned list, this is the start
     * @param amount     limites the returned list, this is the amount of elements to show
     * @return the dossiers where the customerId and the directive matches
     */
    List<Dossier> findDossiersClosedByCustomerIdEager(long customerId, int start, int amount);

    List<Dossier> findAllEagerDescending(int start, int end);

    public SalesProduct persist(SalesProduct salesProduct);
}
