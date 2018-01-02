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
package eu.ggnet.dwoss.customer.ee;

import java.awt.Color;
import java.util.*;
import java.util.function.Function;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.customer.eao.CustomerEao;
import eu.ggnet.dwoss.customer.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.customer.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.lucidcalc.CFormat.FontStyle;
import eu.ggnet.lucidcalc.*;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcWriter;

import static eu.ggnet.dwoss.customer.entity.Communication.Type.*;
import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class CustomerExporterOperation implements CustomerExporter {

    @Inject
    private CustomerEao eao;

    /**
     * This implementation assumes many things as the old customer was constructed, redesign after the new customer ui is active.
     *
     * @return
     */
    // TODO: Redesign after the new customer editor ui is active.
    @Override
    public FileJacket allToXls() {
        return toXls(eao.findAll());
    }

    /**
     * Converts customers to a xls file.
     * Method is here for tryout.
     *
     * @param customers
     * @return
     */
    public static FileJacket toXls(List<Customer> customers) {

        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, Color.BLACK, Color.YELLOW, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("DW Kid"));
        table.add(new STableColumn("Lexware Id", 12));
        table.add(new STableColumn("Sage Id"));
        table.add(new STableColumn("Fibu Konto", 12));
        table.add(new STableColumn("Firma", 35));
        table.add(new STableColumn("UST Id", 15));
        table.add(new STableColumn("Vorname", 15));
        table.add(new STableColumn("Nachname", 15));
        table.add(new STableColumn("Geschl.", 9));
        table.add(new STableColumn("Straße", 30));
        table.add(new STableColumn("Plz", 9));
        table.add(new STableColumn("Ort", 20));
        table.add(new STableColumn("Tel", 20));
        table.add(new STableColumn("Mobil", 20));
        table.add(new STableColumn("eMail", 25));
        table.add(new STableColumn("DatenQuelle", 15));
        table.add(new STableColumn("Branche", 20));
        table.add(new STableColumn("CS Upd. Kd.", 12, new CFormat(FontStyle.BOLD)));
        table.add(new STableColumn("Systemh.", 11, new CFormat(FontStyle.BOLD)));
        table.add(new STableColumn("Prio A", 10, new CFormat(FontStyle.BOLD)));
        table.add(new STableColumn("Anmerkungen", 40));

        table.setModel(new STableModelList(buildModel(customers)));

        CCalcDocument cdoc = new TempCalcDocument("Kundenexport");
        cdoc.add(new CSheet("Kunden", table));

        FileJacket result = new FileJacket("Kundenexport", ".xls", new JExcelLucidCalcWriter().write(cdoc));

        return result;
    }

    private static List<Object[]> buildModel(List<Customer> customers) {
        List<Object[]> rows = new ArrayList<>();
        for (Customer c : customers) {
            rows.add(new Object[]{
                c.getId(),
                c.getAdditionalCustomerIds().get(ExternalSystem.LEXWARE),
                c.getAdditionalCustomerIds().get(ExternalSystem.SAGE),
                applyCompanies(c, cc -> Integer.toString(cc.getLedger())),
                applyCompanies(c, Company::getName),
                applyCompanies(c, Company::getTaxId),
                applyContacts(c, Contact::getFirstName),
                applyContacts(c, Contact::getLastName),
                applyContacts(c, cc -> Optional.ofNullable(cc.getSex()).map(Sex::getSign).orElse("")),
                applyAddress(c, Address::getStreet),
                applyAddress(c, Address::getZipCode),
                applyAddress(c, Address::getCity),
                getComm(c, PHONE),
                getComm(c, MOBILE),
                getComm(c, EMAIL),
                c.getSource(),
                "",
                c.getFlags().contains(CustomerFlag.CS_UPDATE_CANDIDATE),
                c.getFlags().contains(CustomerFlag.ITC_CUSTOMER),
                c.getFlags().contains(CustomerFlag.PRIO_A_CUSTOMER),
                c.getComment()

            });
        }
        return rows;
    }

    private static String applyCompanies(Customer c, Function<Company, String> func) {
        if ( c.getCompanies().isEmpty() ) return "";
        return func.apply(c.getCompanies().get(0));
    }

    private static String applyContacts(Customer c, Function<Contact, String> func) {
        if ( c.getContacts().isEmpty() ) return "";
        return func.apply(c.getContacts().get(0));
    }

    private static String applyAddress(Customer c, Function<Address, String> func) {
        Optional<Address> optAddress = c.toPreferedInvoiceAddress().getOptContact().map(oc -> oc.prefered(AddressType.INVOICE));
        if ( !optAddress.isPresent() ) return "";
        return func.apply(optAddress.get());
    }

    private static String getComm(Customer c, Communication.Type type) {
        if ( c.getContacts().isEmpty() ) return "";
        if ( c.getContacts().get(0).getCommunications().isEmpty() ) return "";
        return c.getContacts().get(0).getCommunications()
                .stream()
                .filter(co -> co.getType() == type)
                .map(Communication::getIdentifier)
                .findFirst()
                .orElse("");
    }

}
