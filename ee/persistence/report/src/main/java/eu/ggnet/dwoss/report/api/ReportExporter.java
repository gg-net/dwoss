package eu.ggnet.dwoss.report.api;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.report.ReportAgent.ViewReportResult;

import eu.ggnet.dwoss.util.FileJacket;

/**
 * Optional Service to Export a Report to a special XLS.
 * <p>
 * @author oliver.guenther
 */
@Remote
@Local
public interface ReportExporter {

    FileJacket toFullXls(ViewReportResult report);

}
