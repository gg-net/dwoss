package eu.ggnet.dwoss.redtape;

import java.util.Arrays;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.redtape.action.*;
import eu.ggnet.dwoss.redtape.reporting.*;
import eu.ggnet.saft.core.ActionFactory;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = ActionFactory.class)
public class RedTapeActionFactory implements ActionFactory {

    @Override
    public List<MetaAction> createMetaActions() {
        return Arrays.asList(
                new MetaAction("Kunden und Aufträge", new OpenSearchAction()),
                new MetaAction("Kunden und Aufträge", new RedTapeSimpleAction(), true),
                new MetaAction("Kunden und Aufträge", new DossierFilterAction()),
                new MetaAction("Artikelstamm", new SalesProductAction()),
                new MetaAction("Geschäftsführung", "Allgemeine Reporte", new ExportDossierToXlsAction()),
                new MetaAction("Geschäftsführung", "Allgemeine Reporte", new CreditMemoReportAction()),
                new MetaAction("Geschäftsführung", "Allgemeine Reporte", new OptimizedCreditMemoReportAction()),
                new MetaAction("Geschäftsführung", "Allgemeine Reporte", new DebitorsReportAction()),
                new MetaAction("Geschäftsführung", "Allgemeine Reporte", new DirectDebitReportAction()),
                new MetaAction("Geschäftsführung", "Abschluss Reporte", new LastWeekCloseAction()),
                new MetaAction("Geschäftsführung", new GsOfficeExportAction())
        );
    }
}
