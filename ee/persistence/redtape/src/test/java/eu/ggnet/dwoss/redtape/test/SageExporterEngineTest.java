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
package eu.ggnet.dwoss.redtape.test;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.*;

import org.junit.Test;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.mandator.api.value.Ledger;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtape.ee.sage.DefaultSageExporterConfig;
import eu.ggnet.dwoss.redtape.ee.sage.SageExporterEngine;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.TaxType;

import static eu.ggnet.dwoss.rules.DocumentType.INVOICE;
import static eu.ggnet.dwoss.rules.PositionType.SHIPPING_COST;
import static eu.ggnet.dwoss.rules.PositionType.UNIT;
import static eu.ggnet.dwoss.rules.TaxType.REVERSE_CHARGE;
import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 * import static eu.ggnet.dwoss.rules.TaxType.REVERSE_CHARGE;
 */
public class SageExporterEngineTest {

    private final static String VALID = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>\n"
            + "<DATAPACKET Version=\"2.0\">\n"
            + "    <METADATA>\n"
            + "        <FIELDS>\n"
            + "            <FIELD attrname=\"B_EBBUCHUNG\" fieldtype=\"i4\"/>\n"
            + "            <FIELD attrname=\"B_EGKONTO\" fieldtype=\"i4\"/>\n"
            + "            <FIELD attrname=\"BELEG\" fieldtype=\"string\" width=\"20\"/>\n"
            + "            <FIELD attrname=\"BEREICH\" fieldtype=\"string\" width=\"1\"/>\n"
            + "            <FIELD attrname=\"BETRAG\" fieldtype=\"r8\"/>\n"
            + "            <FIELD attrname=\"BETRAGN\" fieldtype=\"r8\"/>\n"
            + "            <FIELD attrname=\"BETRAGS\" fieldtype=\"r8\"/>\n"
            + "            <FIELD attrname=\"BUCHTEXT\" fieldtype=\"string\" width=\"80\"/>\n"
            + "            <FIELD attrname=\"BUERFDATUM\" fieldtype=\"datetime\"/>\n"
            + "            <FIELD attrname=\"DATUM\" fieldtype=\"datetime\"/>\n"
            + "            <FIELD attrname=\"EXTERNEOPNUMMER\" fieldtype=\"string\" width=\"20\"/>\n"
            + "            <FIELD attrname=\"FADATUM\" fieldtype=\"datetime\"/>\n"
            + "            <FIELD attrname=\"GKONTO\" fieldtype=\"string\" width=\"12\"/>\n"
            + "            <FIELD attrname=\"INTERNEOPID\" fieldtype=\"i4\"/>\n"
            + "            <FIELD attrname=\"KAKENN\" fieldtype=\"string\" width=\"32\"/>\n"
            + "            <FIELD attrname=\"KBKENN\" fieldtype=\"string\" width=\"32\"/>\n"
            + "            <FIELD attrname=\"KONTO\" fieldtype=\"string\" width=\"12\"/>\n"
            + "            <FIELD attrname=\"PROGRAMM\" fieldtype=\"string\" width=\"10\"/>\n"
            + "            <FIELD attrname=\"REDATUM\" fieldtype=\"datetime\"/>\n"
            + "            <FIELD attrname=\"SKBETRAG\" fieldtype=\"r8\"/>\n"
            + "            <FIELD attrname=\"SKCODE\" fieldtype=\"string\" width=\"2\"/>\n"
            + "            <FIELD attrname=\"SKPROZ\" fieldtype=\"r8\"/>\n"
            + "            <FIELD attrname=\"START\" fieldtype=\"string\" width=\"1\"/>\n"
            + "            <FIELD attrname=\"STCODE\" fieldtype=\"string\" width=\"2\"/>\n"
            + "            <FIELD attrname=\"STPROZ\" fieldtype=\"r8\"/>\n"
            + "            <FIELD attrname=\"USTIDNR\" fieldtype=\"string\" width=\"20\"/>\n"
            + "            <FIELD attrname=\"WAWIBELEG\" fieldtype=\"string\" width=\"20\"/>\n"
            + "        </FIELDS>\n"
            + "    </METADATA>\n"
            + "    <ROWDATA>\n"
            + "        <ROW WAWIBELEG=\"K1/IN1234\" STCODE=\"01\" REDATUM=\"30.01.2018\" KONTO=\"666\" GKONTO=\"1000\" FADATUM=\"30.01.2018\" DATUM=\"30.01.2018\" BUERFDATUM=\"30.01.2018\" BUCHTEXT=\"Müstermann - IN1234\" BELEG=\"AR/K0DW0001/IN12\" STPROZ=\"19\" BETRAGS=\"38,00\" BETRAGN=\"200,00\" B_EGKONTO=\"0\" B_EBBUCHUNG=\"0\" BETRAG=\"238,00\"/>\n"
            + "        <ROW WAWIBELEG=\"K1/IN1234\" STCODE=\"01\" REDATUM=\"30.01.2018\" KONTO=\"666\" GKONTO=\"2000\" FADATUM=\"30.01.2018\" DATUM=\"30.01.2018\" BUERFDATUM=\"30.01.2018\" BUCHTEXT=\"Müstermann - IN1234\" BELEG=\"AR/K0DW0001/IN12\" STPROZ=\"19\" BETRAGS=\"19,00\" BETRAGN=\"100,00\" B_EGKONTO=\"0\" B_EBBUCHUNG=\"0\" BETRAG=\"119,00\"/>\n"
            + "        <ROW WAWIBELEG=\"K1/IN4321\" STCODE=\"36\" REDATUM=\"30.01.2018\" KONTO=\"666\" GKONTO=\"1234\" FADATUM=\"30.01.2018\" DATUM=\"30.01.2018\" BUERFDATUM=\"30.01.2018\" BUCHTEXT=\"Müstermann - IN4321\" BELEG=\"AR/K0DW0002/IN43\" STPROZ=\"0\" BETRAGS=\"0,00\" BETRAGN=\"200,00\" B_EGKONTO=\"0\" B_EBBUCHUNG=\"0\" BETRAG=\"200,00\"/>\n"
            + "    </ROWDATA>\n"
            + "</DATAPACKET>\n"
            + "";


    @Test
    public void testExport() throws UnsupportedEncodingException {
        UiCustomer cus = new UiCustomer(1, "Herr", "Max", "Müstermann", null, "none", "max@example.com", 0);

        Date date = Date.from(LocalDate.of(2018, 01, 30).atStartOfDay(systemDefault()).toInstant());

        // Prepare some data
        Dossier dos = new Dossier(PaymentMethod.DIRECT_DEBIT, true, 0);
        dos.setIdentifier("DW0001");
        Document doc = new Document(INVOICE, Directive.NONE, new DocumentHistory("Junit", "NoComment"));
        doc.setIdentifier("IN1234");
        doc.setActual(date);
        dos.add(doc);
        doc.append(unit(doc.getTaxType(), new Ledger(1000, "Demo1")));
        doc.append(Position.builder().type(SHIPPING_COST).amount(1).bookingAccount(new Ledger(2000, "Versand")).price(100).tax(doc.getTaxType().getTax()).name("Versandkosten").description("Versandkosten").build());

        Dossier dos2 = new Dossier(PaymentMethod.DIRECT_DEBIT, true, 0);
        dos2.setIdentifier("DW0002");
        Document doc2 = new Document(INVOICE, Directive.NONE, new DocumentHistory("Junit", "NoComment"));
        doc2.setIdentifier("IN4321");
        doc2.setActual(date);
        doc2.setTaxType(REVERSE_CHARGE);
        dos2.add(doc2);
        doc2.append(unit(doc2.getTaxType(), new Ledger(1234, "Demo2")));

        Map<Document, UiCustomer> content = new HashMap<>();
        content.put(doc, cus);
        content.put(doc2, cus);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        SageExporterEngine engine = new SageExporterEngine(out, content, new DefaultSageExporterConfig(666, false));
        engine.execute(null);

        String result = out.toString("ISO-8859-1");

//      Enable for problemhandling
//        System.out.println(result);
//        System.out.println("---------------");
//        System.out.println(VALID);
//        System.out.println("Diff: " + StringUtils.difference(result, VALID));
//        System.out.println("DiffIndex: " + StringUtils.indexOfDifference(result, VALID));

        assertThat(result).isEqualTo(VALID);
    }

    private Position unit(TaxType taxType, Ledger ledger) {
        return Position.builder()
                .type(UNIT)
                .amount(1)
                .bookingAccount(ledger)
                .uniqueUnitId(1)
                .uniqueUnitProductId(1)
                .price(200)
                .tax(taxType.getTax())
                .name("Gerät id 1")
                .description("Ein Gerät")
                .build();
    }

}
