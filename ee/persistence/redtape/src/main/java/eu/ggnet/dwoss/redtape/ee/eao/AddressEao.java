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
package eu.ggnet.dwoss.redtape.ee.eao;

import javax.persistence.EntityManager;

import eu.ggnet.dwoss.redtape.ee.entity.Address;
import eu.ggnet.dwoss.redtape.ee.entity.QAddress;
import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

import com.querydsl.jpa.impl.JPAQuery;

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
     * @param addressString
     * @return a Address via correct formatted String.
     */
    public Address findByDescription(String addressString) {
        QAddress a = QAddress.address;
        return new JPAQuery<Address>(em).from(a).where(a.description.eq(addressString)).fetchFirst();
    }

}
