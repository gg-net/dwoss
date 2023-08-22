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
package eu.ggnet.dwoss.stock.ee.assist.gen;

import java.util.*;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.common.values.ShipmentStatus;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.core.system.generator.NameGenerator;
import eu.ggnet.dwoss.stock.ee.entity.*;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

/**
 *
 * @author oliver.guenther
 */
@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class StockGeneratorOperation {

    private final static Random R = new Random();

    private final Logger L = LoggerFactory.getLogger(StockGeneratorOperation.class);

    private final NameGenerator GEN = new NameGenerator();    
    
    @Inject
    @Stocks
    private EntityManager em;

    public StockGeneratorOperation() {
    }

    public StockGeneratorOperation(EntityManager em) {
        this.em = em;
    }

    public List<Stock> makeStocksAndLocations(int amount) {
        if ( SalesChannel.values().length < amount )
            throw new IllegalArgumentException("Amount auf Stocks (" + amount + ") to be generated must be lower that amount of Saleschannels");
        List<Stock> result = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Stock s = new Stock(i);
            s.setName("Lager " + GEN.makeAddress().getTown());
            s.setPrimaryChannel(SalesChannel.values()[i]);
            em.persist(s);           
            result.add(s);
        }
        return result;
    }

    public List<Shipment> makeShipments(int amount) {
        List<Shipment> shipments = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            shipments.add(makeShipment());
        }
        return shipments;
    }
    
    
    public Shipment makeShipment() {
        Shipment shipment = new Shipment("TEST-SHIPMENT-" + R.nextInt(1000),randomContractor() , randomManufacturer(), randomStatus());
        shipment.setAmountOfUnits(R.nextInt(500));
        em.persist(shipment);
        return shipment;
    }

    private ShipmentStatus randomStatus() {
      return ShipmentStatus.values()[R.nextInt(ShipmentStatus.values().length)];
    }

    
    private TradeName randomContractor() {
        return TradeName.values()[R.nextInt(TradeName.values().length)];
    }

    private TradeName randomManufacturer() {
        return new ArrayList<>(TradeName.getManufacturers()).get(R.nextInt(TradeName.getManufacturers().size()));
    }

}
