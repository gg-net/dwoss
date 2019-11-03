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
package eu.ggnet.dwoss.redtapext.ee.sage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.common.ee.log.AutoLogger;
import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.sage.SageExporterConfig;
import eu.ggnet.dwoss.redtape.ee.sage.SageExporterEngine;
import eu.ggnet.dwoss.core.common.FileJacket;

import static eu.ggnet.dwoss.common.api.values.DocumentType.*;

/**
 * Operation for the Export to GsOffice of RedTapeimport eu.ggnet.dwoss.redtape.ee.sage.SageExporterConfig;
 * <p>
 * Informations.
 * <p/>
 * @author pascal.perau
 */
@Stateless
public class SageExporterOperation implements SageExporter {

    private final static Logger L = LoggerFactory.getLogger(SageExporterOperation.class);

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private CustomerServiceBean customerService;

    @Inject
    private SageExporterConfig config;

    @Inject
    private Mandator mandator;

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Exports the all Documents in the Range as the specified XML lines.
     * <p/>
     * @param start the starting date
     * @param end   the ending date
     * @return an Xml document, ready for import in GS Office.
     */
    @Override
    @AutoLogger
    public FileJacket toXml(Date start, Date end) {
        SubMonitor m = monitorFactory.newSubMonitor("GS Buchhalter Export", 100);
        m.start();
        m.message("Loading Invoices");
        DocumentEao documentEao = new DocumentEao(redTapeEm);
        List<Document> documents = new ArrayList<>();
        documents.addAll(documentEao.findDocumentsBetweenDates(start, end, INVOICE, CREDIT_MEMO, ANNULATION_INVOICE));
        L.info("Loaded {} amount of documents", documents.size());
        m.worked(10);
        Map<Document, UiCustomer> customerInvoices = new HashMap<>();
        m.setWorkRemaining(documents.size() * 2);
        for (Document document : documents) {
            m.worked(1, "Handling Invoice " + document.getIdentifier());
            customerInvoices.put(document, customerService.asUiCustomer(document.getDossier().getCustomerId()));
        }
        m.message("Generating Outfile");
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            SageExporterEngine exporter = new SageExporterEngine(out, customerInvoices, config);
            exporter.execute(m);
            m.finish();
            return new FileJacket("Buchungsaetze DW " + mandator.company().name() + " von " + DATE_FORMAT.format(start) + " bis " + DATE_FORMAT.format(end), ".xml", out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
