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
package eu.ggnet.dwoss.stock.eao;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.entity.Shipment;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

/**
 *
 * @author Bastian Venz
 */
public class ShipmentEao extends AbstractEao<Shipment> {

    @Inject
    @Stocks
    private EntityManager em;

    /**
     * Default Constructor.
     *
     * @param em the Shipment Manager.
     */
    public ShipmentEao(EntityManager em) {
        super(Shipment.class);
        this.em = em;
    }

    public ShipmentEao() {
        super(Shipment.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}
