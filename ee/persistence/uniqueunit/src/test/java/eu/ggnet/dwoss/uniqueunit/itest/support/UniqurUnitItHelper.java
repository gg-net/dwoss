/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.itest.support;

import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;

import java.util.*;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.event.UnitHistory;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class UniqurUnitItHelper {

    @Inject
    @UniqueUnits
    private EntityManager em;

    @Inject
    private Contractors contractors;

    public int createSampleUnit() {
        Product p = new ProductEao(em).findByPartNo("AA.AAAAA.AAA");
        if ( p == null ) {
            p = new Product(ProductGroup.DESKTOP, TradeName.FUJITSU, "AA.AAAAA.AAA", "Verition Stein");
            p.setDescription("Ein Tolles Gerät");
            p.setPrice(PriceType.MANUFACTURER_COST, 200.0, "JUnit - Testcase");
            p.setAdditionalPartNo(contractors.all().iterator().next(), "833.323");
            p.setPrice(PriceType.CONTRACTOR_REFERENCE, 240.0, "JUnit - Testcase");
            p.addFlag(Product.Flag.PRICE_FIXED);
            em.persist(p);
        }
        UniqueUnit unit = new UniqueUnit(p, new Date(), "");
        unit.setIdentifier(Identifier.SERIAL, "AAAAAAAAAAA123AAADFSADFSA");
        unit.setIdentifier(Identifier.REFURBISHED_ID, "30001");
        unit.setContractor(TradeName.ONESELF);
        unit.setComment("Ein Commentar");
        unit.setCondition(UniqueUnit.Condition.AS_NEW);
        em.persist(unit);
        return unit.getId();
    }

    @Inject
    private Event<UnitHistory> historyEvent;

    public void send(int id, String msg, String arranger) {
        historyEvent.fire(new UnitHistory(id, msg, arranger));
    }


}
