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
package eu.ggnet.dwoss.redtapext.ee.reporting;

import java.io.File;
import java.util.*;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.format.DocumentFormater;
import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.lucidcalc.*;

import static eu.ggnet.dwoss.core.common.values.PaymentMethod.DIRECT_DEBIT;
import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.RIGHT;
import static eu.ggnet.lucidcalc.CFormat.Representation.CURRENCY_EURO;
import static eu.ggnet.lucidcalc.CFormat.Representation.SHORT_DATE;
import static java.awt.Color.*;

/**
 * Creates a Report containing only BusinessTransactions with PaymentMethod = Lastschrift and are not paid.
 *
 * @author pascal.perau
 */
@Stateless
public class DirectDebitReporterOperation implements DirectDebitReporter {

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private CustomerServiceBean customerService;

    /**
     * Creates the Report
     * <p/>
     * @return a ByteArray represeting the content of an xls file.
     */
    @Override
    public FileJacket toXls() {
        SubMonitor m = monitorFactory.newSubMonitor("Lastschriften", 25);
        m.worked(10);

        DocumentEao docEao = new DocumentEao(redTapeEm);
        List<Document> documents = docEao.findInvoiceUnpaid(DIRECT_DEBIT);

        Set<Long> customers = new HashSet<>();
        for (Document document : documents) {
            customers.add(document.getDossier().getCustomerId());
        }
        for (Long customerId : customers) {
            documents.addAll(docEao.findUnBalancedAnulation(customerId, DIRECT_DEBIT));
        }
        List<Object[]> rows = new ArrayList<>();
        for (Document doc : documents) {
            UiCustomer customer = customerService.asUiCustomer(doc.getDossier().getCustomerId());
            rows.add(new Object[]{
                doc.getDossier().getIdentifier(),
                customer.id(),
                customer.toNameCompanyLine(),
                doc.getDirective().getName(),
                DocumentFormater.toConditions(doc),
                doc.getIdentifier(),
                doc.getActual(),
                doc.getPrice(),
                doc.toAfterTaxPrice(),
                doc.getDossier().getComment(),
                customerService.defaultEmailCommunication(customer.id())
            });
        }
        m.worked(10);
        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, WHITE, BLUE, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("Vorgang", 10)).add(new STableColumn("KID", 8)).add(new STableColumn("Kunde", 40));
        table.add(new STableColumn("Anweisung", 35)).add(new STableColumn("Status", 20)).add(new STableColumn("Dokument", 15)).add(new STableColumn("Datum", 15, new CFormat(SHORT_DATE)));
        table.add(new STableColumn("Netto", 15, new CFormat(RIGHT, CURRENCY_EURO))).add(new STableColumn("Brutto", 15, new CFormat(RIGHT, CURRENCY_EURO)));
        table.add(new STableColumn("Bemerkung", 50)).add(new STableColumn("eMail", 50));
        table.setModel(new STableModelList(rows));

        CCalcDocument cdoc = new TempCalcDocument("Lastschriften");
        cdoc.add(new CSheet("Sheet1", table));

        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc);
        FileJacket result = new FileJacket("Lastschriften", ".xls", file);
        m.finish();
        return result;
    }
}
