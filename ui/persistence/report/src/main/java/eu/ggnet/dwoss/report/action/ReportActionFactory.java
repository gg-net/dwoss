package eu.ggnet.dwoss.report.action;

import java.util.Arrays;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.ActionFactory;

@ServiceProvider(service = ActionFactory.class)
public class ReportActionFactory implements ActionFactory {

    @Override
    public List<MetaAction> createMetaActions() {
        return Arrays.asList(
                new MetaAction("Geschäftsführung", "Abschluss Reporte", new ShowRawReportLinesAction()),
                new MetaAction("Geschäftsführung", "Abschluss Reporte", new CreateReportAction()),
                new MetaAction("Geschäftsführung", "Abschluss Reporte", new ShowExistingReportAction()),
                new MetaAction("Geschäftsführung", "Abschluss Reporte", new CreateReturnsReportAction()),
                // Disabled for now.
                //                new MetaAction("Geschäftsführung", "Abschluss Reporte", new RevenueReportAction()),
                new MetaAction("Geschäftsführung", "Abschluss Reporte", new ExportRevenueReportAction()));
    }
}
