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
package eu.ggnet.dwoss.report.ee.assist.gen;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.gen.*;

public class ReportLineGenerator {

    private final static Random R = new Random();

    private final static List<TradeName> brands = TradeName.getManufacturers()
            .stream().map(TradeName::getBrands).flatMap(Collection::stream).collect(Collectors.toList());

    /** This Method return a Random Positiv Long Value;
     * <p/>
     * @return a positiv long Value.
     */
    private static long getRandomLong() {
        return Math.abs(R.nextInt(100000));
    }

    /** This Method return a Random Positiv int Value;
     * <p/>
     * @return a positiv int Value.
     */
    private static int getRandomInt() {
        return Math.abs(R.nextInt(1000000));
    }

    public ReportLine makeReportLine(List<TradeName> contractors, Date starting, int maxDeltaDays) {
        return makeReportLine(contractors, starting, maxDeltaDays, Arrays.asList(PositionType.values()), Arrays.asList(DocumentType.values()));
    }

    /**
     * Makes one special Reportline
     * <p/>
     * @param contractors  the contractor
     * @param posTypes     the allowed types of positions
     * @param starting     a starting date, this is the earlies date a line will have the reporting date set
     * @param maxDeltaDays this is a max delta for days of the reporting date. The date will be between starting and starting+maxDeltaDays.
     * @param docTypes     documetTypes
     * @return the ReportingLine
     */
    public ReportLine makeReportLine(List<TradeName> contractors, Date starting, int maxDeltaDays, List<PositionType> posTypes, List<DocumentType> docTypes) {
        ReportLine line = new ReportLine();
        line.setName("ReportLine-" + getRandomInt());
        line.setDescription("desription-" + getRandomInt());
        line.setDossierId(getRandomLong());
        line.setDocumentIdentifier("dossierIdentifier-" + getRandomInt());
        line.setDocumentId(getRandomLong());
        line.setDocumentIdentifier("documentIdentifier-" + getRandomInt());
        line.setPositionType(posTypes.get(R.nextInt(posTypes.size())));
        line.setDocumentType(docTypes.get(R.nextInt(docTypes.size())));
        line.setCustomerId(getRandomLong());
        line.setContractorPartNo("123.456");

        line.setAmount(getRandomLong());
        double tax = GlobalConfig.DEFAULT_TAX.getTax();
        double price = Math.abs(R.nextDouble() * R.nextInt(1500));
        line.setManufacturerCostPrice(price * 1.10);
        line.setContractorReferencePrice(price * 1.10);
        line.setPrice(price);
        line.setTax(tax);

        line.setBookingAccount(getRandomInt());
        GeneratedAddress makeAddress = new NameGenerator().makeAddress();
        Name makeName = new NameGenerator().makeName();
        String name = makeName.getFirst() + " " + makeName.getLast();
        line.setCustomerName(name);
        String invoiceAdress = name + ", "
                + makeAddress.getStreet() + " " + makeAddress.getNumber() + ", " + makeAddress.getPostalCode() + " " + makeAddress.getTown();
        line.setCustomerCompany(R.nextInt(10) < 7 ? "" : "TestFirma");
        line.setInvoiceAddress(invoiceAdress);
        line.setRefurbishId("" + R.nextInt(100000));
        line.setUniqueUnitId(getRandomLong());
        line.setSerial("serial" + getRandomInt());
        line.setProductId(getRandomLong());
        line.setPartNo("partNo" + getRandomInt());
        line.setContractor(contractors.size() == 1 ? contractors.get(0) : contractors.get(R.nextInt(contractors.size())));
        line.setProductBrand(brands.get(R.nextInt(brands.size())));

        Date pastFiveYears = DateUtils.setYears(new Date(), 2009);
        line.setMfgDate(DateUtils.addDays(pastFiveYears, R.nextInt(2000)));

        line.setReportingDate(maxDeltaDays > 0 ? DateUtils.addDays(starting, R.nextInt(maxDeltaDays)) : starting);

        return line;
    }

    public ReportLine makeReportLine() {
        ReportLine reportLine = new ReportLine();
        Date pastFiveYears = DateUtils.setYears(new Date(), 2009);
        reportLine.setActual(new Date());
        reportLine.setName("ReportLine-" + getRandomInt());
        reportLine.setDescription("desription-" + getRandomInt());
        reportLine.setDossierId(getRandomLong());
        reportLine.setDocumentIdentifier("dossierIdentifier-" + getRandomInt());
        reportLine.setDocumentId(getRandomLong());
        reportLine.setDocumentIdentifier("documentIdentifier-" + getRandomInt());
        reportLine.setPositionType(PositionType.values()[R.nextInt(PositionType.values().length)]);
        reportLine.setDocumentType(DocumentType.values()[R.nextInt(DocumentType.values().length)]);
        reportLine.setCustomerId(getRandomLong());

        reportLine.setAmount(getRandomLong());
        double tax = GlobalConfig.DEFAULT_TAX.getTax();
        double price = Math.abs(R.nextDouble() * R.nextInt(1500));
        reportLine.setManufacturerCostPrice(price + 15);
        reportLine.setPrice(price);
        reportLine.setTax(tax);

        reportLine.setBookingAccount(getRandomInt());
        GeneratedAddress makeAddress = new NameGenerator().makeAddress();
        Name makeName = new NameGenerator().makeName();
        String invoiceAdress = makeName.getFirst() + " " + makeName.getLast() + ", "
                + makeAddress.getStreet() + " " + makeAddress.getNumber() + ", " + makeAddress.getPostalCode() + " " + makeAddress.getTown();
        reportLine.setInvoiceAddress(invoiceAdress);
        reportLine.setRefurbishId("" + R.nextInt(100000));
        reportLine.setUniqueUnitId(getRandomLong());
        reportLine.setSerial("serial" + getRandomInt());
        reportLine.setProductId(getRandomLong());
        reportLine.setPartNo("partNo" + getRandomInt());
        List<TradeName> names = new ArrayList<>();
        names.addAll(Arrays.asList(TradeName.ACER, TradeName.APPLE, TradeName.DELL, TradeName.HP));
        reportLine.setContractor(names.get(R.nextInt(names.size())));
        reportLine.setProductBrand(names.get(R.nextInt(names.size())));

        reportLine.setMfgDate(DateUtils.addDays(pastFiveYears, R.nextInt(2000)));
        reportLine.setReportingDate(DateUtils.addDays(reportLine.getMfgDate(), R.nextInt(400)));

        return reportLine;
    }
}
