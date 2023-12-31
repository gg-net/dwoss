/*
 * Copyright (C) 2023 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ee.assist.gen;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static eu.ggnet.dwoss.uniqueunit.ee.entity.QPriceHistory.priceHistory;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.QProduct.product;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.QShopCategory.shopCategory;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.QUniqueUnit.uniqueUnit;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.QUniqueUnitHistory.uniqueUnitHistory;

/**
 * Utility class to clear the customer database.
 *
 * @author oliver.guenther
 */
public class UniqueUnitsDeleteUtils {

    private UniqueUnitsDeleteUtils() {
    }

    public static void deleteAll(EntityManager em) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        queryFactory.delete(uniqueUnitHistory).execute();
        queryFactory.delete(uniqueUnit).execute();
        queryFactory.delete(product).execute();
        queryFactory.delete(priceHistory).execute();
        queryFactory.delete(shopCategory).execute();
        em.flush();
    }

    /**
     * If the database is empty, the result is null, otherwise a message with more details.
     *
     * @param em the custmoer emtitymanager
     * @return null if empty, otherwise a message with details.
     */
    public static String validateEmpty(EntityManager em) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        if ( !queryFactory.from(priceHistory).fetch().isEmpty() ) return "uniqueunit.PriceHistory is not empty";
        if ( !queryFactory.from(product).fetch().isEmpty() ) return "uniqueunit.Product is not empty";
        if ( !queryFactory.from(shopCategory).fetch().isEmpty() ) return "uniqueunit.ShopCategory is not empty";
        if ( !queryFactory.from(uniqueUnit).fetch().isEmpty() ) return "uniqueunit.UniqueUnit is not empty";
        if ( !queryFactory.from(uniqueUnitHistory).fetch().isEmpty() ) return "uniqueunit.UniqueUnitHistory is not empty";
        return null;
    }

}
