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
package eu.ggnet.dwoss.redtape.emo;

import javax.persistence.EntityManager;

import eu.ggnet.dwoss.redtape.eao.AddressEao;
import eu.ggnet.dwoss.redtape.entity.Address;

/**
 *
 * @author pascal.perau
 */
public class AddressEmo {

    private EntityManager em;

    public AddressEmo(EntityManager em) {
        this.em = em;
    }

    /**
     * Get a Address by description search.
     *
     * @param searchAddress the address description to search for.
     * @return a Address by description search or a new persisted Address.
     */
    public Address request(String searchAddress) {
        AddressEao addressEao = new AddressEao(em);

        Address address = addressEao.findByDescription(searchAddress);

        //persist address if nothing is found
        if ( address == null ) {
            address = new Address(searchAddress);
            em.persist(address);
            return address;
        }
        return address;
    }
}
