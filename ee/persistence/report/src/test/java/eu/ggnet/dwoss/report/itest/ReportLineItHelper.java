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
package eu.ggnet.dwoss.report.itest;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.report.assist.Reports;
import eu.ggnet.dwoss.report.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.rules.*;

import static java.time.LocalDate.of;
import static java.time.ZoneId.systemDefault;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class ReportLineItHelper {

    @Inject
    @Reports
    private EntityManager em;

    private final ReportLineGenerator gen = new ReportLineGenerator();

    public ReportLine makeReportLine(Date reportDate, TradeName contractor, DocumentType doc, PositionType pos, String refurbishId) {
        ReportLine r = gen.makeReportLine();
        r.setReportingDate(reportDate);
        r.setContractor(contractor);
        r.setDocumentType(doc);
        r.setPositionType(pos);
        r.setRefurbishId(refurbishId);
        em.persist(r);
        return r;
    }

    public final static Date date(int year, int month, int day) {
        return Date.from(of(year, month, day).atStartOfDay(systemDefault()).toInstant());
    }

}
