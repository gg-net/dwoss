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
package eu.ggnet.dwoss.redtape.ee.assist.gen;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static eu.ggnet.dwoss.redtape.ee.entity.QAddress.address;
import static eu.ggnet.dwoss.redtape.ee.entity.QDocument.document;
import static eu.ggnet.dwoss.redtape.ee.entity.QDossier.dossier;
import static eu.ggnet.dwoss.redtape.ee.entity.QPosition.position;
import static eu.ggnet.dwoss.redtape.ee.entity.QRedTapeCounter.redTapeCounter;
import static eu.ggnet.dwoss.redtape.ee.entity.QSalesProduct.salesProduct;

/**
 * Utility class to clear the customer database.
 *
 * @author oliver.guenther
 */
public class RedTapeDeleteUtils {

    private RedTapeDeleteUtils() {
    }

    public static void deleteAll(EntityManager em) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        queryFactory.delete(position).execute();
        queryFactory.delete(document).execute();
        queryFactory.delete(dossier).execute();
        queryFactory.delete(address).execute();
        queryFactory.delete(redTapeCounter).execute();
        queryFactory.delete(salesProduct).execute();
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

        if ( !queryFactory.from(address).fetch().isEmpty() ) return "spec.AllInOne is not empty";
        if ( !queryFactory.from(document).fetch().isEmpty() ) return "spec.BasicSpec is not empty";
        if ( !queryFactory.from(dossier).fetch().isEmpty() ) return "spec.Desktop is not empty";
        if ( !queryFactory.from(position).fetch().isEmpty() ) return "spec.DesktopBundle is not empty";
        if ( !queryFactory.from(redTapeCounter).fetch().isEmpty() ) return "spec.Monitor is not empty";
        if ( !queryFactory.from(salesProduct).fetch().isEmpty() ) return "spec.Notebook is not empty";
        return null;
    }

}
