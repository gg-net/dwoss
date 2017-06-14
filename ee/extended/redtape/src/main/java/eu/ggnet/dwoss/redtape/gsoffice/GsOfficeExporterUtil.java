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
package eu.ggnet.dwoss.redtape.gsoffice;

import eu.ggnet.dwoss.redtape.api.RowData;

import java.io.OutputStream;
import java.util.*;

import javax.xml.bind.*;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.mandator.api.value.FinancialAccounting;
import eu.ggnet.saft.api.progress.IMonitor;

import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.redtape.api.*;

import eu.ggnet.dwoss.redtape.entity.Document;

import lombok.*;

/**
 * The GsOfficeExporterUtil.
 * <p/>
 * Defined:
 * - Beleg: AR/K"Customerid""DossierIdentifier"/"DocumenteIdentifier first letters numbers" (e.g. AR/K123DW32412/RS12)
 * - WawiBeleg: K"CustomerId"/"DocumentIdentifier" (e.g. K1234/RS12_00001)
 * <p/>
 * <
 * p/>
 * @author pascal.perau
 */
@Getter
@Setter
@AllArgsConstructor
public class GsOfficeExporterUtil {

    private GsOfficeSupport supportImplementation;

    private OutputStream output;

    private Map<Document, UiCustomer> customerInvoices;

    private FinancialAccounting accounting;

    public void execute(IMonitor monitor) {
        SubMonitor m = SubMonitor.convert(monitor, "Create GS-Office XML Data", customerInvoices.size() + 10);
        RowData rowData = supportImplementation.generateGSRowData(customerInvoices, accounting, monitor);
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

}
