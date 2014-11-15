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
package eu.ggnet.dwoss.redtape.eao;

import javax.persistence.*;

import eu.ggnet.dwoss.redtape.entity.Address;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

/**
 *
 * @author pascal.perau
 */
public class AddressEao extends AbstractEao<Address> {

    private EntityManager em;

    public AddressEao(EntityManager em) {
        super(Address.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Find a Address via correct formatted String.
     * The convention is: street + \n + zipCode + \n + city
     * <p>
     * @param address
     * @return a Address via correct formatted String.
     */
    public Address findByDescription(String address) {
        Query query = em.createNamedQuery("byFormatedString");
        query.setParameter(1, address);
        try {
            return (Address)query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

}
