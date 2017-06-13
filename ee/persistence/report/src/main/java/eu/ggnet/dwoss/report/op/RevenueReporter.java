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
package eu.ggnet.dwoss.report.op;

import java.util.*;

import javax.ejb.Remote;

import eu.ggnet.dwoss.report.RevenueReportSum;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.Step;
import eu.ggnet.dwoss.util.FileJacket;

/**
 * Reporter for aggregated Revenues.
 * <p>
 * @author oliver.guenther
 */
@Remote
public interface RevenueReporter {

    /**
     * <p>
     * @param pTypes position types to be included
     * @param start  start date
     * @param end    end date
     * @return Daily seperated {@link RevenueReportSum} containing the aggregated information.
     */
    Set<RevenueReportSum> aggregateDailyRevenue(List<PositionType> pTypes, Date start, Date end);

    /**
     * Returns a FileJecket containing an XLS with the step revenue for the range.
     * <p>
     * @param start
     * @param end
     * @param step
     * @param extraReported
     * @return a FileJecket containing an XLS with the step revenue for the range.
     */
    FileJacket toXls(Date start, Date end, Step step, boolean extraReported);

}
