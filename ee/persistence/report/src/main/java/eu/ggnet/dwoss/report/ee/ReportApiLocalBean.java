/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.report.ee;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.report.api.ReportApiLocal;
import eu.ggnet.dwoss.report.api.SimpleReportUnit;
import eu.ggnet.dwoss.report.ee.eao.ReportLineEao;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;

import static eu.ggnet.dwoss.core.common.values.PositionType.PRODUCT_BATCH;
import static eu.ggnet.dwoss.report.ee.entity.ReportLine.SingleReferenceType.WARRANTY;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class ReportApiLocalBean implements ReportApiLocal {

    @Inject
    private ReportLineEao eao;

    @Override
    public String findReportLinesByUniqueUnitIdAsHtml(long uniqueUnitId) {
        String re = "";
        List<ReportLine> reportLines = eao.findByUniqueUnitId(uniqueUnitId);
        if ( reportLines == null || reportLines.isEmpty() ) re += "Keine Reporting-Informationen vorhanden<br />";
        else {
            re += "<table border=\"1\"><tr>";
            re += wrap("<th>", "</th>", "Id", "ReportDate", "Kid", "SopoNr", "Type", "Dossier", "Report");
            re += "</tr>";
            for (ReportLine l : reportLines) {
                re += "<tr>";
                re += wrap("<td>", "</td>",
                        l.getId(),
                        Utils.ISO_DATE.format(l.getReportingDate()),
                        l.getCustomerId(),
                        l.getRefurbishId(),
                        l.getPositionType() == PRODUCT_BATCH && l.getReference(WARRANTY) != null ? "Garantieerweiterung" : l.getPositionType().description,
                        l.getDossierIdentifier() + ", " + l.getDocumentType().description + l.getWorkflowStatus().sign + (l.getDocumentIdentifier() == null ? "" : ", " + l.getDocumentIdentifier()),
                        l.getReports().stream().map(Report::getName).collect(Collectors.joining(","))
                );
            }
            re += "</table><br />";
        }
        return re;
    }

    private static String wrap(String head, String foot, Object... elmes) {
        StringBuilder sb = new StringBuilder();
        for (Object elme : elmes) {
            sb.append(head).append(elme).append(foot);
        }
        return sb.toString();
    }

    @Override
    public SimpleReportUnit findReportUnit(long uniqueUnitId) {
        List<ReportLine> reportLines = eao.findByUniqueUnitId(uniqueUnitId);
        if ( reportLines.isEmpty() ) return null;
        return new SimpleReportUnit.Builder()
                .uniqueUnitId(uniqueUnitId)
                .addAllLines(reportLines.stream().map(ReportLine::toSimpleLine).collect(Collectors.toList()))
                .build();
    }

}
