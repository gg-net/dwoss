package eu.ggnet.dwoss.report.op;

import java.util.*;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.report.RevenueReportSum;
import eu.ggnet.dwoss.report.eao.ReportLineEao.Step;

import eu.ggnet.dwoss.rules.PositionType;

import eu.ggnet.dwoss.util.FileJacket;

/**
 * Reporter for aggregated Revenues.
 * <p>
 * @author oliver.guenther
 */
@Local
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
     * @return a FileJecket containing an XLS with the step revenue for the range.
     */
    FileJacket toXls(Date start, Date end, Step step);

}
