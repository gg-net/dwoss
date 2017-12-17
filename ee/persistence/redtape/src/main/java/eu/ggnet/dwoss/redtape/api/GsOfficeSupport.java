/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.redtape.api;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Local;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.mandator.api.value.FinancialAccounting;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.saft.api.progress.IMonitor;

/**
 * Support for the GsOffice XML export to be implemented by the mandator.
 *
 * @author oliver.guenther
 */
@Local
public interface GsOfficeSupport {

    // TODO: Please move this to each mandator. A default solution is not very helpful.
    public default RowData generateGSRowData(Map<Document, UiCustomer> documents, FinancialAccounting accounting, IMonitor m) {
        RowData rowData = new RowData();
        for (Document doc : documents.keySet()) {
            Row r = new Row();
            m.worked(1, "processing Invioce " + doc.getIdentifier());
            UiCustomer customer = documents.get(doc);
            r.setBeleg("AR/K" + doc.getDossier().getCustomerId() + doc.getDossier().getIdentifier().replace("_", "") + "/" + doc.getIdentifier().substring(0, 4));
            r.setBuerfDatum(doc.getActual());
            r.setDatum(doc.getActual());
            r.setFaDatum(doc.getActual());
            r.setReDatum(doc.getActual());
            String buchungsText = customer.getCompany();
            if ( buchungsText == null || buchungsText.trim().equals("") ) {
                buchungsText = customer.getLastName();
            }
            if ( buchungsText == null || buchungsText.trim().equals("") ) {
                buchungsText = "Kundenummer=" + customer.getId();
            }
            buchungsText = buchungsText.replaceAll("-", "_");
            buchungsText += " - " + doc.getIdentifier();
            r.setBuchtext(buchungsText);
            r.setWawiBeleg("K" + customer.getId() + "/" + doc.getIdentifier());
            r.setStCode("01");

            r.setKonto(accounting.getDefaultLedger());
            if ( !accounting.isDisableCustomerLedgers() && customer.getLedger() > 0 ) r.setKonto(customer.getLedger());

            Map<Integer, Row> bookingRates = new HashMap<>();
            for (Position position : doc.getPositions().values()) {
                if ( position.getBookingAccount() <= 0 ) continue;
                Row row;
                if ( !bookingRates.containsKey(position.getBookingAccount()) ) {
                    bookingRates.put(position.getBookingAccount(), new Row(r));
                    row = bookingRates.get(position.getBookingAccount());
                    row.setNettoSumme(row.getNettoSumme() + (position.getAmount() * position.getPrice()));
                    row.setBruttoSumme(row.getBruttoSumme() + (position.getAmount() * position.toAfterTaxPrice()));
                    row.setBetrag((position.getAmount() * position.getPrice()), (position.getAmount() * position.toAfterTaxPrice()));
                    row.setStProz(position.getTax() * 100);
                    row.setStNumeric(position.getTax() * 100);
                    if ( doc.getType() == DocumentType.CREDIT_MEMO ) {
                        row.setKonto(position.getBookingAccount());
                        row.setGKonto(accounting.getDefaultLedger());
                    } else {
                        row.setGKonto(position.getBookingAccount());
                    }
                    rowData.add(row);
                } else {
                    row = bookingRates.get(position.getBookingAccount());
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
