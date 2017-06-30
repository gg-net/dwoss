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
package eu.ggnet.dwoss.uniqueunit.assist.gen;

import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.util.MapBuilder;

import static eu.ggnet.dwoss.rules.ProductGroup.DESKTOP;
import static eu.ggnet.dwoss.rules.Warranty.WARRANTY_TILL_DATE;

/**
 * Generator for UniqueUnits.
 * <p>
 * @author oliver.guenther
 */
public class UniqueUnitGenerator {

    private final Random R = new Random();

    private static final Map<TradeName, Integer> CONTRACTOR_REFURBISHIDS = new MapBuilder<>()
            .put(TradeName.ONESELF, 1)
            .put(TradeName.ACER, 10000)
            .put(TradeName.HP, 20000)
            .put(TradeName.LENOVO, 30000)
            .put(TradeName.SAMSUNG, 40000)
            .toHashMap();

    public UniqueUnit makeUniqueUnit(TradeName contractor, Product product) {
        return makeUniqueUnit(contractor, null, product);
    }

    public UniqueUnit makeUniqueUnit(TradeName contractor, ProductGroup productGroup) {
        return makeUniqueUnit(contractor, productGroup, null);
    }

    /**
     * Generates a random Unit, based on the Product and from the contractor.
     * <p/>
     * @param product    the product as basis, if null ignored.
     * @param contractor the contractor, if null randomly selected.
     * @return the generated unit.
     */
    private UniqueUnit makeUniqueUnit(TradeName contractor, ProductGroup group, Product product) {
        if ( contractor == null ) throw new RuntimeException("As the Contractor is Mandator specific, it must not be null, even for generated Units.");
        String serial = (product == null
                         ? RandomStringUtils.randomAlphanumeric(22).toUpperCase()
                         : product.getPartNo().replaceAll("\\.", "") + RandomStringUtils.randomAlphanumeric(12).toUpperCase());
        if ( group == null && product != null ) group = product.getGroup();
        UniqueUnit uu = new UniqueUnit();
        uu.addHistory("Generated by makeUniqueUnit(contractor=" + contractor + ", group=" + group + ", product=" + product + ")");
        // Not perfect, but works.
        Integer refurbisId = CONTRACTOR_REFURBISHIDS.get(contractor);
        if ( refurbisId == null ) refurbisId = 123456;
        else refurbisId++;
        CONTRACTOR_REFURBISHIDS.put(contractor, refurbisId);
        uu.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, refurbisId.toString());
        uu.setIdentifier(UniqueUnit.Identifier.SERIAL, serial);
        uu.setContractor(contractor);
        uu.setMfgDate(DateUtils.addDays(new Date(), -1 * R.nextInt(1000))); // Random Date, may 3 years in the past.
        uu.setCondition(UniqueUnit.Condition.values()[R.nextInt(UniqueUnit.Condition.values().length)]);
        uu.setEquipments(randomSet(group == null ? UniqueUnit.Equipment.values() : UniqueUnit.Equipment.getEquipments(group).toArray(new UniqueUnit.Equipment[0])));
        uu.setComments(randomSet(UniqueUnit.StaticComment.values()));
        uu.setInternalComments(randomSet(UniqueUnit.StaticInternalComment.values()));
        if ( (Math.random() * 100) > 75 ) {
            uu.setWarranty(WARRANTY_TILL_DATE);
            uu.setWarrentyValid(new Date((long)(System.currentTimeMillis() + Math.random() * 21600000000L)));
        }
        return uu;
    }

    private <T> Set<T> randomSet(T[] values) {
        int limit = R.nextInt(values.length);
        Set<T> equipments = new HashSet<>();
        for (int i = 0; i < limit; i++) {
            equipments.add(values[R.nextInt(values.length)]);
        }
        return equipments;
    }

    public static void main(String[] args) {
        UniqueUnitGenerator generator = new UniqueUnitGenerator();
        for (int i = 0; i < 100; i++) {
            System.out.println(generator.makeUniqueUnit(TradeName.DELL, DESKTOP));
        }
    }
}
