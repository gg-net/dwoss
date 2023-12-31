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
package eu.ggnet.dwoss.report.ee.assist.gen;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static eu.ggnet.dwoss.report.ee.entity.QReport.report;
import static eu.ggnet.dwoss.report.ee.entity.QReportLine.reportLine;
import static eu.ggnet.dwoss.report.ee.entity.QStockCount.stockCount;

/**
 * Utility class to clear the customer database.
 *
 * @author oliver.guenther
 */
public class ReportDeleteUtils {

    private ReportDeleteUtils() {
    }

    public static void deleteAll(EntityManager em) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        queryFactory.delete(report).execute();
        queryFactory.delete(reportLine).execute();
        queryFactory.delete(stockCount).execute();
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
        if ( !queryFactory.from(report).fetch().isEmpty() ) return "report.Report is not empty";
        if ( !queryFactory.from(reportLine).fetch().isEmpty() ) return "report.ReportLine is not empty";
        if ( !queryFactory.from(stockCount).fetch().isEmpty() ) return "report.StockCount is not empty";
        return null;
    }

}
