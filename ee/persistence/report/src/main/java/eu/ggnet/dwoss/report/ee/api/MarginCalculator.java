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
package eu.ggnet.dwoss.report.ee.api;

import eu.ggnet.dwoss.report.ee.ViewReportResult;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;

/**
 *
 * @author Bastian Venz <bastian.venz at gg-net.de>
 */
public interface MarginCalculator {

    /**
     * Recalculate the Report. All {@link ReportLine}'s must be detached. Any {@link ReportLine} which is not detached will be modified in this process.
     * <p>
     * @param report
     */
    void recalc(ViewReportResult report);

}
