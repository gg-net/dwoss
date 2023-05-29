/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.ee.itest;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.stock.ee.assist.ShipmentCount;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.stock.ee.eao.ShipmentEao;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.stock.ee.itest.support.ArquillianProjectArchive;

import static eu.ggnet.dwoss.core.common.values.ShipmentStatus.ANNOUNCED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class ShipmentCountIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    @Stocks
    private EntityManager em;
    
    @Inject
    private StockGeneratorOperation stockGenerator;
    
    @Inject
    private ShipmentEao shipmentEao;

    @After
    public void clearDataBase() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    @Test
    public void count() {
        List<Shipment> allShipments = stockGenerator.makeShipments(30);
        long countedAnnounce = allShipments.stream().filter(s -> s.getStatus() == ANNOUNCED).count();
        long sumAnnounceUnits = allShipments.stream().filter(s -> s.getStatus() == ANNOUNCED).mapToLong(s -> s.getAmountOfUnits()).sum();
        List<ShipmentCount> counted = shipmentEao.countShipmentsByStatus();
        Optional<ShipmentCount> osc = counted.stream().filter(sc -> sc.status() == ANNOUNCED).findFirst();
        assertThat(osc.isPresent()).as("ShipmentCount für Announced").isTrue();
        assertThat(osc.get().amount()).as("count of Shipment of Announced").isEqualTo(countedAnnounce);
        assertThat(osc.get().amountOfUnits()).as("sum of Shipment Units of Announced").isEqualTo(sumAnnounceUnits);
    }
}
