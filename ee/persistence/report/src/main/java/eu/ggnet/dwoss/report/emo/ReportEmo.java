/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.report.emo;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.report.assist.Reports;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.rules.TradeName;

import com.mysema.query.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.report.entity.QReport.report;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class ReportEmo {

    @Inject
    @Reports
    private EntityManager reportEm;

    /**
     * This method search a Report where all parameters are equal to one in the Database, if no is existing in the database a new one will be created and
     * returned.
     * <p>
     * @param name       is the name of the Report
     * @param contractor is the contractor of the Report as {@link TradeName}.
     * @param starting   is the Date where the report is starting
     * @param end        is the Date where the report is ending.
     * @return the founded or the new created Report.
     */
    public Report request(String name, TradeName contractor, Date starting, Date end) {
        Report singleResult = new JPAQuery(reportEm).from(report).where(report.name.equalsIgnoreCase(name).and(
                report.startingDate.eq(starting).and(report.endingDate.eq(end).and(report.type.eq(contractor))))).singleResult(report);
        if ( singleResult != null ) return singleResult;
        Report report = new Report(name, contractor, starting, end);
        reportEm.persist(report);
        return report;
    }

}
