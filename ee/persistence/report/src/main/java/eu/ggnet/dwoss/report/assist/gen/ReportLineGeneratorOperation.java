package eu.ggnet.dwoss.report.assist.gen;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.report.assist.Reports;

@Stateless

public class ReportLineGeneratorOperation {

    @Inject
    @Reports
    private EntityManager reportEm;

    @Inject
    private MonitorFactory monitorFactory;

    private final ReportLineGenerator generator = new ReportLineGenerator();

    public void makeReportLines(int amount) {
        SubMonitor m = monitorFactory.newSubMonitor("Erzeuge " + amount + " ReportLines", amount);
        m.start();
        for (int i = 0; i < amount; i++) {
            reportEm.persist(generator.makeReportLine());
            m.worked(1);
        }

    }

}
