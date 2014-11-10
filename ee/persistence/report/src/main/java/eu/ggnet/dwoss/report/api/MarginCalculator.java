package eu.ggnet.dwoss.report.api;

import eu.ggnet.dwoss.report.ReportAgent.ViewReportResult;
import eu.ggnet.dwoss.report.entity.ReportLine;

/**
 *
 * @author Bastian Venz <bastian.venz at gg-net.de>
 */
public interface MarginCalculator {

    /**
     * Recalculate the Report. All {@link ReportLine}'s must be dettached. Any {@link ReportLine} which is not dettached will be modified in this prozess.
     * <p>
     * @param report
     */
    void recalc(ViewReportResult report);

}
