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
package eu.ggnet.dwoss.redtape.reporting;

import eu.ggnet.lucidcalc.CBorder;
import eu.ggnet.lucidcalc.CFormat;
import eu.ggnet.lucidcalc.CSheet;
import eu.ggnet.lucidcalc.CCalcDocument;
import eu.ggnet.lucidcalc.TempCalcDocument;
import eu.ggnet.lucidcalc.STableModelList;
import eu.ggnet.lucidcalc.STableColumn;
import eu.ggnet.lucidcalc.STable;
import eu.ggnet.lucidcalc.LucidCalc;

import java.io.File;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.op.CustomerServiceBean;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.format.DocumentFormater;

import eu.ggnet.dwoss.rules.PaymentMethod;

import eu.ggnet.dwoss.util.FileJacket;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.*;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.RIGHT;
import static eu.ggnet.lucidcalc.CFormat.Representation.*;
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
        List<Document> documents = docEao.findOpenInvoiceUnpaidByTypePaymentMethod(PaymentMethod.DIRECT_DEBIT);

        Set<Long> customers = new HashSet<>();
        for (Document document : documents) {
            customers.add(document.getDossier().getCustomerId());
        }
        for (Long customerId : customers) {
            documents.addAll(docEao.findOpenAnulationByCustomerPaymentMethod(customerId));
        }
        List<Object[]> rows = new ArrayList<>();
        for (Document doc : documents) {
            UiCustomer customer = customerService.asUiCustomer(doc.getDossier().getCustomerId());
            rows.add(new Object[]{
                doc.getDossier().getIdentifier(),
                customer.getId(),
                customer.toNameCompanyLine(),
                doc.getDirective().getName(),
                DocumentFormater.toConditions(doc),
                doc.getIdentifier(),
                doc.getActual(),
                doc.getPrice(),
                doc.getAfterTaxPrice(),
                doc.getDossier().getComment(),
                customerService.asCustomerMetaData(customer.getId()).getEmail()
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
