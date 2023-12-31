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
package eu.ggnet.dwoss.uniqueunit.itest.support;

import java.util.Collections;
import java.util.List;

import jakarta.ejb.Stateless;

import eu.ggnet.dwoss.report.api.ReportApiLocal;
import eu.ggnet.dwoss.report.api.SimpleReportUnit;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class ReportApiLocalStub implements ReportApiLocal {

    @Override
    public String findReportLinesByUniqueUnitIdAsHtml(long uniqueUnitId) {
        return "ReportLinesByUniqueUnitIdAsHtml of UniqueUnit.id=" + uniqueUnitId;
    }

    @Override
    public SimpleReportUnit findReportUnit(long uniqueUnitId) {
        return null;
    }

    @Override
    public List<SimpleReportUnit> findUnreportedOpenComplaints() {
        return Collections.emptyList();
    }

}
