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
package eu.ggnet.dwoss.redtape.ee.sage;

import java.io.OutputStream;
import java.util.*;

import javax.xml.bind.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.system.progress.IMonitor;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtape.ee.sage.xml.Row;
import eu.ggnet.dwoss.redtape.ee.sage.xml.RowData;

/**
 * The GsOfficeExporterUtil.
 * Defined:
 * - Beleg: AR/K"Customerid""DossierIdentifier"/"DocumenteIdentifier first letters numbers" (e.g. AR/K123DW32412/RS12)
 * - WawiBeleg: K"CustomerId"/"DocumentIdentifier" (e.g. K1234/RS12_00001)
 * @author pascal.perau
 */
public class SageExporterEngine {

    private final static Logger L = LoggerFactory.getLogger(SageExporterEngine.class);

    private OutputStream output;

    private Map<Document, UiCustomer> customerInvoices;

    private SageExporterConfig config;

    public SageExporterEngine(OutputStream output, Map<Document, UiCustomer> customerInvoices, SageExporterConfig config) {
        setOutput(output);
        setCustomerInvoices(customerInvoices);
        setConfig(config);
    }

    public SageExporterEngine() {
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter (NonNull)">
    public OutputStream getOutput() {
        return output;
    }

    public final void setOutput(OutputStream output) {
        this.output = Objects.requireNonNull(output);
    }

    public Map<Document, UiCustomer> getCustomerInvoices() {
        return customerInvoices;
    }

    public final void setCustomerInvoices(Map<Document, UiCustomer> customerInvoices) {
        this.customerInvoices = Objects.requireNonNull(customerInvoices);
    }

    public SageExporterConfig getConfig() {
        return config;
    }

    public final void setConfig(SageExporterConfig config) {
        this.config = Objects.requireNonNull(config);
    }
    //</editor-fold>

    public void execute(IMonitor monitor) {
        SubMonitor m = SubMonitor.convert(monitor, "Create GS-Office XML Data", customerInvoices.size() + 10);
        RowData rowData = generateGSRowData(monitor);
        m.message("writting Output");
        try {
            JAXBContext context = JAXBContext.newInstance(RowData.class);
            Marshaller ms = context.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ms.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
            ms.marshal(rowData, output);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        m.finish();
    }

    public RowData generateGSRowData(IMonitor monitor) {
        SubMonitor m = SubMonitor.convert(monitor);
        RowData rowData = new RowData();
        for (Document doc : customerInvoices.keySet()) {
            Row r = new Row();
            m.worked(1, "processing Invioce " + doc.getIdentifier());
            UiCustomer customer = customerInvoices.get(doc);
            r.setBeleg(config.beleg(doc, customer));
            r.setBuerfDatum(doc.getActual());
            r.setDatum(doc.getActual());
            r.setFaDatum(doc.getActual());
            r.setReDatum(doc.getActual());
            r.setBuchtext(config.buchText(doc, customer));
            r.setWawiBeleg(config.wawiBeleg(doc, customer));
            r.setStCode(config.stCode(doc));
            r.setKaKenn(config.kakenn());
            r.setKbKenn(config.kbkenn());

            r.setKonto(config.getDefaultDebitorLedger());
            if ( !config.isCustomerLedgersDisabled() && customer.ledger > 0 ) r.setKonto(customer.ledger);

            Map<Integer, Row> bookingRates = new HashMap<>();
            for (Position position : doc.getPositions().values()) {
                if ( !position.getBookingAccount().isPresent() ) {
                    L.warn("Export contains Position without BookingAccount. Kid={},Dossier={},Pos={}", customer.id, doc.getDossier().getIdentifier(), position);
                    continue;
                }
                Row row;
                if ( !bookingRates.containsKey(position.getBookingAccount().get().value) ) {
                    bookingRates.put(position.getBookingAccount().get().value, new Row(r));
                    row = bookingRates.get(position.getBookingAccount().get().value);
                    row.setNettoSumme(row.getNettoSumme() + (position.getAmount() * position.getPrice()));
                    row.setBruttoSumme(row.getBruttoSumme() + (position.getAmount() * position.toAfterTaxPrice()));
                    row.setBetrag((position.getAmount() * position.getPrice()), (position.getAmount() * position.toAfterTaxPrice()));
                    row.setStProz(position.getTax() * 100);
                    row.setStNumeric(position.getTax() * 100);
                    if ( doc.getType() == DocumentType.CREDIT_MEMO ) {
                        row.setKonto(position.getBookingAccount().get().value);
                        row.setGKonto(config.getDefaultDebitorLedger());
                    } else {
                        row.setGKonto(position.getBookingAccount().get().value);
                    }
                    rowData.add(row);
                } else {
                    row = bookingRates.get(position.getBookingAccount().get().value);
                    if ( row.getStNumeric() != (position.getTax() * 100) )
                        throw new RuntimeException("Document enthält Positionen mit unterschiedlicher UmSt. Rechnung: " + doc.getIdentifier() + ", aktuelle UmSt.: " + row.getStProz() + ", abweichung in Position: " + position.getName() + " mit UmSt. von " + (position.getTax() * 100));
                    row.setNettoSumme(row.getNettoSumme() + (position.getAmount() * position.getPrice()));
                    row.setBruttoSumme(row.getBruttoSumme() + (position.getAmount() * position.toAfterTaxPrice()));
                    row.setBetrag(row.getNettoSumme(), row.getBruttoSumme());
                }
            }
        }
        return rowData;
    }

}
