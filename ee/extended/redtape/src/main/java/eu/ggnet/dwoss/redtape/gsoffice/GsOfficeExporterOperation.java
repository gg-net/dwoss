package eu.ggnet.dwoss.redtape.gsoffice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.mandator.api.value.FinancialAccounting;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.entity.Document;

import eu.ggnet.dwoss.util.FileJacket;

import static eu.ggnet.dwoss.rules.DocumentType.*;

/**
 * Operation for the Export to GsOffice of RedTape Informations.
 * <p/>
 * @author pascal.perau
 */
@Stateless
public class GsOfficeExporterOperation implements GsOfficeExporter {

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private CustomerService customerService;

    @Inject
    private FinancialAccounting accounting;

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Exports the all Documents in the Range as the specified XML lines.
     * <p/>
     * @param start the starting date
     * @param end   the ending date
     * @return an Xml document, ready for import in GS Office.
     */
    @Override
    public FileJacket toXml(Date start, Date end) {
        SubMonitor m = monitorFactory.newSubMonitor("GS Buchhalter Export", 100);
        m.start();
        m.message("Loading Invoices");
        DocumentEao documentEao = new DocumentEao(redTapeEm);
        List<Document> documents = new ArrayList<>();
        documents.addAll(documentEao.findDocumentsBetweenDates(start, end, INVOICE, CREDIT_MEMO, ANNULATION_INVOICE));
        m.worked(10);
        Map<Document, UiCustomer> customerInvoices = new HashMap<>();
        m.setWorkRemaining(documents.size() * 2);
        for (Document document : documents) {
            m.worked(1, "Handling Invoice " + document.getIdentifier());
            customerInvoices.put(document, customerService.asUiCustomer(document.getDossier().getCustomerId()));
        }
        m.message("Generating Outfile");
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            GsOfficeExporterUtil exporter = new GsOfficeExporterUtil(out, customerInvoices, accounting);
            exporter.execute(m);
            m.finish();
            return new FileJacket("Buchungsaetze Sopo von " + DATE_FORMAT.format(start) + " bis " + DATE_FORMAT.format(end), ".xml", out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
