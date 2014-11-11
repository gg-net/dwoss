/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.redtape.assist;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.PositionType;

import java.util.Collection;

import eu.ggnet.dwoss.redtape.entity.*;

/**
 *
 * @author oliver.guenther
 */
public class RedTapeSamples {

    private final static String loremIpsum = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor"
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
        Position p1 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(420.17).
                setTax(0.19).setAfterTaxPrice(500.).setName("Packard Bell OneTwo S A4146 GE | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription("AMD E Series E-300 (1.3 Ghz), Memory (in MB): 4096, AMD Radeon HD 6000 Series 6310, Festplatte(n): 500GB HDD, Optische(s) Laufwerk(e): DVD Super Multi, Display: 20.1\" (51,05 cm), Matt, Full HD (1920x1080), 16:9, Farbe: schwarz-silber, Ausstattung: Webcam, Kartenleser, Windows 7 Home Premium 64").createPosition();
        Position p2 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(933.61).
                setTax(0.19).setAfterTaxPrice(1111).setName("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription("Intel Core I7 i7-2600 (3.4 Ghz, 4 Kern(e)), Memory (in MB): 8192, nVidia GeForce 500 Series GTX 570 HD, Festplatte(n): 2000GB HDD, 32GB SSD, Optische(s) Laufwerk(e): DVD Super Multi, Blu-Ray Combo, , Farbe: schwarz-orange, Ausstattung: USB 3, PS2, Kartenleser, Videokonnektor(en) : DVI, HDMI, Windows 7 Home Premium 64, Bemerkung: Kratzer auf dem Gehäuse, Untere Fronklappe fehlt, Geänderte Konfiguration: 1x DVD-ROM, 1x DVD-SuperMulti, kein Blu-ray. W-Lan. W-Lan-Antenne dabei.").createPosition();
        Position p3 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(50.).
                setTax(0.19).setAfterTaxPrice(50.).setName("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription(loremIpsum).createPosition();
        Position p4 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(50.).
                setTax(0.19).setAfterTaxPrice(1.).setName("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription("Ein Menge an Info").createPosition();
        Position p5 = new PositionBuilder().setType(PositionType.PRODUCT_BATCH).setPrice(50.).
                setTax(0.19).setAfterTaxPrice(1.).setName("Product Batch").
                setDescription("Ein Menge an Info").createPosition();
        Position p6 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(100000.).
                setTax(0.19).setAfterTaxPrice(100000.).setName("Acer Aspire Teuer | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription("Ein Menge an teure Info").createPosition();
        Position p7 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(50.25).
                setTax(0.19).setAfterTaxPrice(50.25).setName("Acer Aspire Ungrade | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription("Ein Menge an ungrader Info").createPosition();
        Position p8 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(50.).
                setTax(0.19).setAfterTaxPrice(1.).setName("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription("Ein Menge an Info").createPosition();
        Position p9 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(50.).
                setTax(0.19).setAfterTaxPrice(1.).setName("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription("Ein Menge an Info").createPosition();
        Position p10 = new PositionBuilder().setType(PositionType.COMMENT).setName("Comment").setDescription("Comments Description").createPosition();
        Position p11 = new PositionBuilder().setType(PositionType.SERVICE).setName("Service").setPrice(2.).setTax(2.).
                setAfterTaxPrice(2.2).setAmount(1.).setDescription("Service Description").createPosition();
        Position p12 = new PositionBuilder().setType(PositionType.SHIPPING_COST).setName("Shipping cost").setDescription("Shipping cost").setPrice(16.5).createPosition();

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
        return document.getPositions().values();
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
        Position p1 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(50.).
                setTax(1.).setAfterTaxPrice(1.).setName("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription("Ein Menge an Info").createPosition();
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
        Position p1 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(50.).
                setTax(1.).setAfterTaxPrice(1.).setName("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription("Ein Menge an Info").createPosition();
        Position p2 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(50.).
                setTax(1.).setAfterTaxPrice(1.).setName("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription("Ein Menge an Info").createPosition();
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
        Position p1 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(50.).
                setTax(1.).setAfterTaxPrice(1.).setName("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription(loremIpsum).createPosition();
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
        Position p1 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(50.).
                setTax(1.).setAfterTaxPrice(1.).setName("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription(loremIpsum).createPosition();
        Position p2 = new PositionBuilder().setType(PositionType.UNIT).setUniqueUnitId(1).setPrice(50.).
                setTax(1.).setAfterTaxPrice(1.).setName("Acer Aspire Blub | SN: AAAAAAAAAAAAAAAAAAAAAAAAAAa").
                setDescription(loremIpsum).createPosition();
        document.append(p1);
        document.append(p2);
        return document.getPositions().values();
    }
}
