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
package eu.ggnet.dwoss.stock.ee.eao;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.core.system.persistence.AbstractEao;
import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.stock.ee.assist.*;

import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.stock.ee.entity.QShipment.shipment;
/**
 *
 * @author Bastian Venz
 */
@Stateless
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

    public List<ShipmentCount> countShipmentsByStatus() {
         return new JPAQuery<ShipmentCount>(em)
                 .select(new QShipmentCount(shipment.status, shipment.count(), shipment.amountOfUnits.sum()))
                 .from(shipment)
                 .groupBy(shipment.status)
                 .fetch();
    }
    
    /**
     * Returns all shipments since
     * 
     * @param since date since, must not be null
     * @return all shipments since.
     * @throws NullPointerException if date is null
     */
    public List<Shipment> findSince(LocalDate since) throws NullPointerException{        
         return new JPAQuery<Shipment>(em)
                 .from(shipment)
                 .where(shipment.date.after(Utils.toDate(Objects.requireNonNull(since,"since must not be null").minusDays(1))))
                 .fetch();        
    }
    
}
