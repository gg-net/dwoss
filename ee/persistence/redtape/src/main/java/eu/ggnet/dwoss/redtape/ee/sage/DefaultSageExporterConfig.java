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
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.redtape.ee.entity.Document;

/**
 * Default implementation for the sage exporter config.
 *
 * @author oliver.guenther
 */
public class DefaultSageExporterConfig implements SageExporterConfig, Serializable {

    private final int defaultDebitorLedger;

    private final boolean customerLedgersDisabled;

    private final String kakenn;

    private final String kbkenn;

    public DefaultSageExporterConfig(int defaultDebitorLedger, boolean customerLedgersDisabled, String kakenn, String kbkenn) {
        this.defaultDebitorLedger = defaultDebitorLedger;
        this.customerLedgersDisabled = customerLedgersDisabled;
        this.kakenn = Objects.requireNonNull(kakenn, "kakenn must not be null");
        this.kbkenn = Objects.requireNonNull(kbkenn, "kbkenn must not be null");
    }

    @Override
    public int getDefaultDebitorLedger() {
        return defaultDebitorLedger;
    }

    @Override
    public boolean isCustomerLedgersDisabled() {
        return customerLedgersDisabled;
    }

    @Override
    public String beleg(Document doc, UiCustomer customer) {
        String dossierIdentifier = doc.getDossier().getIdentifier() == null ? "NoDos" : doc.getDossier().getIdentifier().replace("_", "");
  //      String documentIdentifier = doc.getIdentifier() == null ? "NoDoc" : doc.getIdentifier().substring(0, 4);

        String documentIdentifier = doc.getIdentifier() == null ? "NoDoc" : doc.getIdentifier().substring(0, 2);        
        return dossierIdentifier + ".K" + doc.getDossier().getCustomerId() + "." + documentIdentifier;
//        return "AR/K" + doc.getDossier().getCustomerId() + dossierIdentifier + "/" + documentIdentifier;
    }

    @Override
    public String buchText(Document doc, UiCustomer customer) {
        String buchungsText = customer.company;
        if ( StringUtils.isBlank(buchungsText) ) {
            buchungsText = customer.lastName;
        }
        if ( StringUtils.isBlank(buchungsText) ) {
            buchungsText = "Kundenummer=" + customer.id;
        }
        buchungsText = buchungsText.replaceAll("-", "_");
        buchungsText += " - " + doc.getIdentifier();
        return buchungsText;
    }

    @Override
    public String wawiBeleg(Document doc, UiCustomer customer) {
        return "K" + customer.id + "/" + doc.getIdentifier();
    }

    @Override
    public String stCode(Document doc) {
        return doc.getTaxType().taxCode();
    }

    @Override
    public String kakenn() {
        return kakenn;
    }

    @Override
    public String kbkenn() {
        return kbkenn;
    }

}
