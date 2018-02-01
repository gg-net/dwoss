/*
 * Copyright (C) 2018 GG-Net GmbH
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

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.redtape.ee.entity.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Default implementation for the sage exporter config.
 *
 * @author oliver.guenther
 */
@AllArgsConstructor
public class DefaultSageExporterConfig implements SageExporterConfig, Serializable {

    @Getter
    private int defaultDebitorLedger = 0;

    @Getter
    private boolean customerLedgersDisabled = false;

    @Override
    public String beleg(Document doc, UiCustomer customer) {
        String dossierIdentifier = doc.getDossier().getIdentifier() == null ? "NoDossierIdentifier" : doc.getDossier().getIdentifier().replace("_", "");
        String documentIdentifier = doc.getIdentifier() == null ? "NoDocumentIdentifier" : doc.getIdentifier().substring(0, 4);
        return "AR/K" + doc.getDossier().getCustomerId() + dossierIdentifier + "/" + documentIdentifier;
    }

    @Override
    public String buchText(Document doc, UiCustomer customer) {
        String buchungsText = customer.getCompany();
        if ( StringUtils.isBlank(buchungsText) ) {
            buchungsText = customer.getLastName();
        }
        if ( StringUtils.isBlank(buchungsText) ) {
            buchungsText = "Kundenummer=" + customer.getId();
        }
        buchungsText = buchungsText.replaceAll("-", "_");
        buchungsText += " - " + doc.getIdentifier();
        return buchungsText;
    }

    @Override
    public String wawiBeleg(Document doc, UiCustomer customer) {
        return "K" + customer.getId() + "/" + doc.getIdentifier();
    }

    @Override
    public String stCode(Document doc) {
        return doc.getTaxType().getTaxCode();
    }

}
