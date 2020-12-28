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
package eu.ggnet.dwoss.redtape.ee.assist;

import java.util.Collection;

import eu.ggnet.dwoss.core.common.values.*;
import eu.ggnet.dwoss.redtape.ee.entity.*;

/**
 * Used in Tryouts and in iReport for Testdata only.
 *
 * @author oliver.guenther
 */
public class RedTapeSamples {

    private final static double TAX = TaxType.GENERAL_SALES_TAX_DE_19_PERCENT.tax;

    private final static String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor"
            + " invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et"
            + " ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
            + "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
            + " At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor"
            + " sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor"
            + " invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et"
            + " ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
            + "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
            + " At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor"
            + " sit amet.";

    public static Collection<Position> getPositions() {
        return getDocument().getPositions().values();
    }

    public static Document getDocument() {
        Address a = new Address("This is\nA adress\nfor you!");
        Address a2 = new Address("This is\nA adress\nfor you too!");
        Document document = new Document();
        document.setInvoiceAddress(a);
        document.setShippingAddress(a2);
        document.setIdentifier("TestIdDoc");
        document.setType(DocumentType.ORDER);
        document.setTaxType(TaxType.GENERAL_SALES_TAX_DE_19_PERCENT);
        Dossier dossier = new Dossier();
        document.setDossier(dossier);
        dossier.setComment("Epic Fail Comment");
        dossier.setCustomerId(1337);
        dossier.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        dossier.setDispatch(true);
        dossier.setIdentifier("DosId");

        //Create Positions
        Position p1 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(420.17).tax(TAX).name("Packard Bell OneTwo S A4146 GE | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description("AMD E Series E-300 (1.3 Ghz), Memory (in MB): 4096, AMD Radeon HD 6000 Series 6310, Festplatte(n): 500GB HDD, Optische(s) Laufwerk(e): DVD Super Multi, Display: 20.1\" (51,05 cm), Matt, Full HD (1920x1080), 16:9, Farbe: schwarz-silber, Ausstattung: Webcam, Kartenleser, Windows 7 Home Premium 64").build();
        Position p2 = Position.builder().amount(214).type(PositionType.SERVICE).name("Service").price(7.5).tax(TAX).description("Service Description").build();
        Position p3 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(50.).tax(TAX).name("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description(LOREM_IPSUM).build();
        Position p4 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(50.).tax(TAX).name("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description("Ein Menge an Info").build();
        Position p5 = Position.builder().amount(1).type(PositionType.PRODUCT_BATCH).price(50.).tax(TAX).name("Product Batch").description("Ein Menge an Info").build();
        Position p6 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(100000.).tax(TAX).name("Acer Aspire Teuer | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description("Ein Menge an teure Info").build();
        Position p7 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(50.25).tax(TAX).name("Acer Aspire Ungrade | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description("Ein Menge an ungrader Info").build();
        Position p8 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(50.).tax(TAX).name("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description("Ein Menge an Info").build();
        Position p9 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(50.).tax(TAX).name("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description(LOREM_IPSUM).build();
        Position p10 = Position.builder().amount(1).type(PositionType.COMMENT).name("Comment").tax(TAX).description(LOREM_IPSUM + " " + LOREM_IPSUM).build();
        Position p11 = Position.builder().amount(1).type(PositionType.SERVICE).name("Service").price(2.).tax(TAX).description("Service Description").build();
        Position p12 = Position.builder().amount(1).type(PositionType.SHIPPING_COST).name("Shipping cost").description("Shipping cost").tax(TAX).price(16.5).build();
        Position p13 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(933.61).tax(TAX).name("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description("Intel Core I7 i7-2600 (3.4 Ghz, 4 Kern(e)), Memory (in MB): 8192, nVidia GeForce 500 Series GTX 570 HD, Festplatte(n): 2000GB HDD, 32GB SSD, Optische(s) Laufwerk(e): DVD Super Multi, Blu-Ray Combo, , Farbe: schwarz-orange, Ausstattung: USB 3, PS2, Kartenleser, Videokonnektor(en) : DVI, HDMI, Windows 7 Home Premium 64, Bemerkung: Kratzer auf dem Gehäuse, Untere Fronklappe fehlt, Geänderte Konfiguration: 1x DVD-ROM, 1x DVD-SuperMulti, kein Blu-ray. W-Lan. W-Lan-Antenne dabei.").build();

        document.append(p1);
        document.append(p2);
        document.append(p3);
        document.append(p4);
        document.append(p5);
        document.append(p6);
        document.append(p7);
        document.append(p8);
        document.append(p9);
        document.append(p10);
        document.append(p11);
        document.append(p12);
        document.append(p13);
        return document;
    }

    public static Collection<Position> getOnePosition() {
        Address a = new Address("This is\nA adress\nfor you!");
        Address a2 = new Address("This is\nA adress\nfor you too!");
        Document document = new Document();
        document.setInvoiceAddress(a);
        document.setShippingAddress(a2);
        document.setIdentifier("TestIdDoc");
        Dossier dossier = new Dossier();
        document.setDossier(dossier);
        dossier.setComment("Epic Fail Comment");
        dossier.setCustomerId(1337);
        dossier.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        dossier.setDispatch(true);
        dossier.setIdentifier("DosId");
        document.setType(DocumentType.ORDER);
        //Create Positions
        Position p1 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(50.).tax(1.).name("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description("Ein Menge an Info").build();
        document.append(p1);
        return document.getPositions().values();
    }

    public static Collection<Position> getTwoPosition() {
        Address a = new Address("This is\nA adress\nfor you!");
        Address a2 = new Address("This is\nA adress\nfor you too!");
        Document document = new Document();
        document.setInvoiceAddress(a);
        document.setShippingAddress(a2);
        document.setIdentifier("TestIdDoc");
        Dossier dossier = new Dossier();
        document.setDossier(dossier);
        dossier.setComment("Epic Fail Comment");
        dossier.setCustomerId(1337);
        dossier.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        dossier.setDispatch(true);
        dossier.setIdentifier("DosId");
        document.setType(DocumentType.ORDER);
        //Create Positions
        Position p1 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(50.).tax(1.).name("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description("Ein Menge an Info").build();
        Position p2 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(50.).tax(1.).name("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description("Ein Menge an Info").build();
        document.append(p1);
        document.append(p2);
        return document.getPositions().values();
    }

    public static Collection<Position> getOneLogPosition() {
        Address a = new Address("This is\nA adress\nfor you!");
        Address a2 = new Address("This is\nA adress\nfor you too!");
        Document document = new Document();
        document.setInvoiceAddress(a);
        document.setShippingAddress(a2);
        document.setIdentifier("TestIdDoc");
        Dossier dossier = new Dossier();
        document.setDossier(dossier);
        dossier.setComment("Epic Fail Comment");
        dossier.setCustomerId(1337);
        dossier.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        dossier.setDispatch(true);
        dossier.setIdentifier("DosId");
        document.setType(DocumentType.ORDER);
        //Create Positions
        Position p1 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(50.).tax(1.).name("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description(LOREM_IPSUM).build();
        document.append(p1);
        return document.getPositions().values();
    }

    public static Collection<Position> getTwoLogPosition() {
        Address a = new Address("This is\nA adress\nfor you!");
        Address a2 = new Address("This is\nA adress\nfor you too!");
        Document document = new Document();
        document.setInvoiceAddress(a);
        document.setShippingAddress(a2);
        document.setIdentifier("TestIdDoc");
        Dossier dossier = new Dossier();
        document.setDossier(dossier);
        dossier.setComment("Epic Fail Comment");
        dossier.setCustomerId(1337);
        dossier.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        dossier.setDispatch(true);
        dossier.setIdentifier("DosId");
        document.setType(DocumentType.ORDER);
        //Create Positions
        Position p1 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(50.).tax(1.).name("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description(LOREM_IPSUM).build();
        Position p2 = Position.builder().amount(1).type(PositionType.UNIT).uniqueUnitId(1).price(50.).tax(1.).name("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").description(LOREM_IPSUM).build();
        document.append(p1);
        document.append(p2);
        return document.getPositions().values();
    }

}
