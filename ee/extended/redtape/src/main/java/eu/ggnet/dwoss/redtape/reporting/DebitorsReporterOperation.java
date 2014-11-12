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

import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.entity.Document;

import eu.ggnet.dwoss.rules.DocumentType;

import eu.ggnet.dwoss.util.FileJacket;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.*;
import static eu.ggnet.lucidcalc.CFormat.Representation.CURRENCY_EURO;
import static eu.ggnet.lucidcalc.CFormat.Representation.SHORT_DATE;
import static java.awt.Color.*;

/**
 * Creates a Report all Auftrags, which have an Invoice in the supplied date interval.
 *
 * @author oliver.guenther
 */
// Candidate for RedTape Extended.
@Stateless

public class DebitorsReporterOperation implements DebitorsReporter {

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private CustomerService customerService;

    /**
     * Creates the Report
     *
     * @param monitor
     * @return a ByteArray represeting the content of an xls file.
     */
    @Override
    public FileJacket toXls(Date start, Date end) {
        SubMonitor m = monitorFactory.newSubMonitor("DebitorenReport", 25);
        m.message("loading Dossiers");
        DocumentEao documentEao = new DocumentEao(redTapeEm);
        List<Document> documents = new ArrayList<>();
        documents.addAll(documentEao.findDocumentsBetweenDates(start, end, DocumentType.INVOICE));
        m.worked(10, "preparing Data");
        List<Object[]> rows = new ArrayList<>();
        for (Document document : documents) {
            UiCustomer c = customerService.asUiCustomer(document.getDossier().getCustomerId());
            rows.add(new Object[]{
                c.getId(),
                document.getDossier().getIdentifier(),
                c.getCompany(),
                c.toNameLine(),
                document.getDossier().getCrucialDirective().getName(),
                document.getDossier().getComment(),
                document.getActual(),
                document.getIdentifier(),
                document.getPrice(),
                document.getAfterTaxPrice(),
                document.getDossier().getPaymentMethod().getNote()
            });
        }
        m.worked(10, "building Report");
        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, WHITE, BLUE, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("Kid", 8, new CFormat(RIGHT))).add(new STableColumn("AiD", 10, new CFormat(RIGHT))).add(new STableColumn("Firma", 20));
        table.add(new STableColumn("Nachname", 20)).add(new STableColumn("Letzer Status", 20));
        table.add(new STableColumn("Bemerkung", 10)).add(new STableColumn("Datum", 10, new CFormat(RIGHT, SHORT_DATE)));
        table.add(new STableColumn("RE_Nr", 10, new CFormat(RIGHT))).add(new STableColumn("Netto", 15, new CFormat(RIGHT, CURRENCY_EURO))).add(new STableColumn("Brutto", 10, new CFormat(RIGHT, CURRENCY_EURO)));
        table.add(new STableColumn("ZahlungsModalität", 10, new CFormat(RIGHT)));
        table.setModel(new STableModelList(rows));

        CCalcDocument cdoc = new TempCalcDocument("Debitoren_");
        cdoc.add(new CSheet("DebitorenReport", table));

        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc);
        FileJacket result = new FileJacket("Debitoren", ".xls", file);
        m.finish();
        return result;
    }
}
