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

import java.util.*;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.event.UnitHistory;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.uniqueunit.entity.*;

import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.SERIAL;

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

    public List<Integer> fillPersistenceSource() {

        Date now = new Date();

        Product p1 = new Product(ProductGroup.DESKTOP, TradeName.ACER, "LX.11111.222", "Verition Stein");
        p1.setDescription("Ein Tolles Gerät");
        p1.setPrice(PriceType.MANUFACTURER_COST, 200.0, "JUnit - Testcase");
        p1.setPrice(PriceType.CONTRACTOR_REFERENCE, 240.0, "JUnit - Testcase");
        p1.addFlag(Product.Flag.PRICE_FIXED);

        Product p2 = new Product(ProductGroup.COMMENTARY, TradeName.DELL, "DL", "Dienstleistung 1h");
        p2.setDescription("Eine Dienstleistungs Stunde");

        UniqueUnit unit1 = new UniqueUnit(p1, now, "");
        unit1.setIdentifier(SERIAL, "ROFFFLAASSS");
        unit1.setPrice(PriceType.SALE, 125, "JUnit - Testcase");
        unit1.addFlag(UniqueUnit.Flag.PRICE_FIXED);
        unit1.setContractor(TradeName.ONESELF);
        unit1.setComment("Ein Commentar");
        unit1.setCondition(UniqueUnit.Condition.AS_NEW);

        UniqueUnit unit2 = new UniqueUnit(p1, now, "lila");
        unit2.addHistory(new UniqueUnitHistory(UniqueUnitHistory.Type.UNDEFINED, "Aufgenommen als Sopo 332"));
        unit2.addHistory(new UniqueUnitHistory(UniqueUnitHistory.Type.UNIQUE_UNIT, "Zerlegt weil kaput"));
        unit2.setIdentifier(SERIAL, "DBCFDASFDSADEF");
        unit2.setContractor(TradeName.ONESELF);
        unit2.setComment("Auch ein Commentar");
        unit2.setCondition(UniqueUnit.Condition.AS_NEW);

        UniqueUnit unit3 = new UniqueUnit();
        unit3.setProduct(p1);
        unit3.setMfgDate(now);
        unit3.setIdentifier(SERIAL, "ABCDEFJKHKZHJI");
        unit3.setContractor(TradeName.ONESELF);
        unit3.setCondition(UniqueUnit.Condition.AS_NEW);

        UniqueUnit unit4 = new UniqueUnit(p2, now, "");
        unit4.setIdentifier(SERIAL, "ABCDEFFEQGSDFD");
        unit4.setContractor(TradeName.ONESELF);
        unit4.setCondition(UniqueUnit.Condition.USED);
        unit4.setComment("Ein sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr langer Kommentar");
        unit4.setInternalComment("Ein sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr"
                + " sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr langer Kommentar");

        em.persist(p1);
        em.persist(p2);

        em.persist(unit1);
        em.persist(unit2);
        em.persist(unit3);
        em.persist(unit4);
        return Arrays.asList(unit3.getId(), unit4.getId());
    }

}
