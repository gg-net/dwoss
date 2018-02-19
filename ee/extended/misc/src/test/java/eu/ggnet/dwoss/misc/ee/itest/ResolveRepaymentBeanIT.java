/*
 * Copyright (C) 2014 bastian.venz
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
package eu.ggnet.dwoss.misc.ee.itest;

import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtapext.ee.UnitOverseer;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.mandator.api.value.RepaymentCustomers;
import eu.ggnet.dwoss.misc.ee.ResolveRepayment;
import eu.ggnet.dwoss.misc.ee.ResolveRepaymentBean;
import eu.ggnet.dwoss.receipt.ee.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtapext.ee.gen.RedTapeGeneratorOperation;
import eu.ggnet.dwoss.redtapext.ee.reporting.RedTapeCloser;
import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ee.eao.ReportLineEao;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.entity.ReportLine.Storeable;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.rules.DocumentType.*;
import static eu.ggnet.dwoss.rules.PositionType.UNIT;
import static eu.ggnet.dwoss.rules.TradeName.ACER;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType.CUSTOMER;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class ResolveRepaymentBeanIT extends ArquillianProjectArchive {

    @EJB
    private ResolveRepayment bean;

    @Inject
    private ResolveRepaymentBeanITHelper helper;

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private RedTapeGeneratorOperation redTapeGenerator;

    @Inject
    private StockGeneratorOperation stockGenerator;

    @Inject
    private ReportLineEao reportLineEao;

    @EJB
    private ReportAgent reportAgent;

    @EJB
    private RedTapeAgent redTapeAgent;

    @EJB
    private RedTapeWorker redTapeWorker;

    @EJB
    private RedTapeCloser redTapeCloser;

    @Inject
    private RepaymentCustomers repaymentCustomers;

    @EJB
    private UnitOverseer unitOverseer;

    @EJB
    private StockAgent stockAgent;

    @Test
    public void testResolve() throws UserInfoException {
        List<Stock> allStocks = stockGenerator.makeStocksAndLocations(2);
        assertThat(allStocks).isNotEmpty().hasSize(2);
        Stock activeStock = allStocks.get(0);
        assertThat(customerGenerator.makeCustomers(10)).isNotEmpty();
        assertThat(receiptGenerator.makeUniqueUnits(200, true, true)).isNotEmpty();
        assertThat(redTapeGenerator.makeSalesDossiers(30)).isNotEmpty();
        final TradeName TRADE_NAME = ACER;
        assertThat(TRADE_NAME.isManufacturer()).isTrue();

        long customerId = customerGenerator.makeCustomer();
        List<UniqueUnit> uus = receiptGenerator.makeUniqueUnits(1, true, true);
        UniqueUnit uu = uus.get(0);
        uu = helper.changeContractors(uu.getId(), TRADE_NAME);
        String refurbishId = uu.getRefurbishId();

        Dossier dos = redTapeWorker.create(customerId, true, "Me");
        Document doc = dos.getActiveDocuments(DocumentType.ORDER).get(0); // order oder direct invoice

        //Create Positions
        doc.append(Position.builder()
                .type(UNIT)
                .amount(1)
                .uniqueUnitId(uu.getId())
                .uniqueUnitProductId(uu.getProduct().getId())
                .price(uu.getPrice(CUSTOMER))
                .tax(doc.getSingleTax())
                .name(uu.getProduct().getName() + " | SN:" + uu.getSerial())
                .description(uu.getProduct().getDescription())
                .refurbishedId(refurbishId)
                .build());

        //add units to LogicTransaction
        unitOverseer.lockStockUnit(dos.getId(), uu.getRefurbishId());

        doc.add(Document.Condition.PAID);
        doc.add(Document.Condition.PICKED_UP);
        doc.setType(DocumentType.INVOICE);

        doc = redTapeWorker.update(doc, null, "JUnit");

        // Now create an annulation Invoice
        doc.setType(ANNULATION_INVOICE);
//        ArrayList<Position> positions = new ArrayList<>();
        for (Position value : doc.getPositions().values()) {
            //          if ( value.getType() == UNIT ) positions.add(value);
            value.setPrice(value.getPrice() * -1);

        }
        redTapeWorker.update(doc, activeStock.getId(), "JUnit Test");

        // Closing the Day. Creating report lines.
        redTapeCloser.executeManual("JUnitTest");

        // Ensure, that we have a Mirror Dossier on the repaymentcustomers.
        List<Dossier> repaymentDossiers = redTapeAgent.findDossiersOpenByCustomerIdEager(repaymentCustomers.get(TRADE_NAME).get());
        assertThat(repaymentDossiers).as("RepaymentDossiers").isNotEmpty();
        Dossier repaymentDossier = repaymentDossiers.get(0);
        List<Document> activeDocuments = repaymentDossier.getActiveDocuments(BLOCK);
        assertThat(activeDocuments).isNotEmpty();
        assertThat(activeDocuments.get(0).getPositions(UNIT)).isNotEmpty();

        Report report = new Report("Test", TRADE_NAME, new Date(), new Date());
        List<ReportLine> reportLines = reportLineEao.findAll();
        List<Storeable> arrayList = new ArrayList<>();
        reportLines.stream().filter((line) -> (line.getDocumentType() == INVOICE)).forEach((line) -> {
            arrayList.add(line.toStorable());
        });
        report = reportAgent.store(report, arrayList);
        assertThat(report).isNotNull();
        assertThat(report.getLines()).isNotEmpty();
        List<ReportLine> notReported = report.getLines().stream().filter((l) -> reportLines.contains(l)).collect(Collectors.toList());

        final int uuId = uu.getId();
        ReportLine lineToUniqueUnit = notReported.stream().filter((line) -> line.getUniqueUnitId() == uuId).collect(Collectors.toList()).get(0);
        assertThat(lineToUniqueUnit).isNotNull();

        List<ReportLine> repaymentLines = bean.getRepaymentLines(lineToUniqueUnit.getContractor());
        ReportLine repaymentLine = repaymentLines.stream().filter((l) -> l.getRefurbishId().equals(refurbishId)).collect(Collectors.toList()).get(0);

        assertThat(stockAgent.findStockUnitsByRefurbishIdEager(Arrays.asList(refurbishId))).isNotEmpty();
        //Resolving of the Unit.
        bean.resolveUnit(refurbishId, TRADE_NAME, "JUnit", "JUnit");

        List<Report> reports = reportAgent.findAll(Report.class);
        assertThat(reports).hasSize(2);
        Report repaymentReport = null;
        // Try to get Report with the Name that is generated in a Static method inside the ResolveRepaymentBean.
        if ( reports.get(0).getName().equals(ResolveRepaymentBean.toReportName(TRADE_NAME)) ) repaymentReport = reports.get(0);
        else repaymentReport = reports.get(1);

        repaymentReport = reportAgent.findByIdEager(Report.class, repaymentReport.getId());

        assertThat(repaymentReport.getLines()).hasSize(1);
        assertThat(repaymentLines).contains(repaymentLine);

        // Ensure, that we the mirror Dossier has be cleared of the unit
        repaymentDossiers = redTapeAgent.findDossiersOpenByCustomerIdEager(repaymentCustomers.get(TRADE_NAME).get());
        assertThat(repaymentDossiers).isNotEmpty();
        repaymentDossier = repaymentDossiers.get(0);
        activeDocuments = repaymentDossier.getActiveDocuments(BLOCK);
        assertThat(activeDocuments).isNotEmpty();
        assertThat(activeDocuments.get(0).getPositions(UNIT)).isEmpty();
        assertThat(activeDocuments.get(0).getPositions(PositionType.COMMENT)).isNotEmpty(); // We still should have comments there.

        assertThat(stockAgent.findStockUnitsByRefurbishIdEager(Arrays.asList(refurbishId))).isNullOrEmpty();
    }

}
