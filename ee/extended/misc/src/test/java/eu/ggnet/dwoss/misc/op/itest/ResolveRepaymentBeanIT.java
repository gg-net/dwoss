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
package eu.ggnet.dwoss.misc.op.itest;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.time.DateUtils;
import org.fest.assertions.core.Condition;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.mandator.api.value.RepaymentCustomers;
import eu.ggnet.dwoss.misc.op.ResolveRepayment;
import eu.ggnet.dwoss.misc.op.ResolveRepaymentBean;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.*;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.redtape.gen.RedTapeGeneratorOperation;
import eu.ggnet.dwoss.redtape.reporting.RedTapeCloser;
import eu.ggnet.dwoss.report.ReportAgent;
import eu.ggnet.dwoss.report.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.eao.ReportLineEao;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.ReportLine.Storeable;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.util.MathUtil;
import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.rules.DocumentType.*;
import static eu.ggnet.dwoss.rules.PositionType.UNIT;
import static eu.ggnet.dwoss.rules.TradeName.ACER;
import static eu.ggnet.dwoss.rules.TradeName.AMAZON;
import static eu.ggnet.dwoss.uniqueunit.entity.PriceType.CUSTOMER;
import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class ResolveRepaymentBeanIT extends ArquillianProjectArchive {

    @Inject
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

    @Inject
    private DatabaseCleaner cleaner;

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
    }

    @Test
    public void testGetRepaymentLines() {
        int amount = 50;
        helper.generateLines(amount);

        List<ReportLine> repaymentLines = bean.getRepaymentLines(AMAZON);
        assertThat(repaymentLines).isNotEmpty().hasSize(amount);
    }

    @Test
    public void testResolve() throws UserInfoException {
        List<Stock> allStocks = stockGenerator.makeStocksAndLocations(2);
        assertThat(allStocks).isNotEmpty().hasSize(2);
        Stock activeStock = allStocks.get(0);
        assertThat(customerGenerator.makeCustomers(10)).isNotEmpty();
        assertThat(receiptGenerator.makeUniqueUnits(200, true, true)).isNotEmpty();
        assertThat(redTapeGenerator.makeSalesDossiers(30)).isNotEmpty();
        TradeName tradeName = ACER;
        assertThat(tradeName).is(new Condition<TradeName>() {
            @Override
            public boolean matches(TradeName t) {
                return t.isManufacturer();
            }
        });
        long customerId = customerGenerator.makeCustomer();
        List<UniqueUnit> uus = receiptGenerator.makeUniqueUnits(1, true, true);
        UniqueUnit uu = uus.get(0);
        helper.changeContractors(uu.getId(), tradeName);
        String refurbishId = uu.getIdentifier(REFURBISHED_ID);

        Dossier dos = redTapeWorker.create(customerId, true, "Me");
        Document doc = dos.getActiveDocuments(DocumentType.ORDER).get(0); // order oder direct invoice

        //Create Positions
        doc.append(Position.builder()
                .type(UNIT)
                .amount(1)
                .uniqueUnitId(uu.getId())
                .uniqueUnitProductId(uu.getProduct().getId())
                .price(uu.getPrice(CUSTOMER))
                .tax(GlobalConfig.TAX)
                .afterTaxPrice(MathUtil.roundedApply(uu.getPrice(CUSTOMER), GlobalConfig.TAX, 0.))
                .name(uu.getProduct().getName() + " | SN:" + uu.getIdentifier(UniqueUnit.Identifier.SERIAL))
                .description(uu.getProduct().getDescription())
                .bookingAccount(-1)
                .refurbishedId(refurbishId)
                .build());

        //add units to LogicTransaction
        unitOverseer.lockStockUnit(dos.getId(), uu.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));

        doc.add(Document.Condition.PAID);
        doc.add(Document.Condition.PICKED_UP);
        doc.setType(DocumentType.INVOICE);

        doc = redTapeWorker.update(doc, null, "JUnit");

        doc = redTapeAgent.findByIdEager(Document.class, doc.getId());
        doc.setType(ANNULATION_INVOICE);
        ArrayList<Position> positions = new ArrayList<>();
        for (Position value : doc.getPositions().values()) {
            if ( value.getType() == UNIT ) positions.add(value);
            value.setPrice(value.getPrice() * -1);
            value.setAfterTaxPrice(value.getAfterTaxPrice() * -1);

        }
        doc = redTapeWorker.update(doc, activeStock.getId(), "JUnit Test");

        redTapeCloser.executeManual("JUnitTest");

        // Ensure, that we have a Mirror Dossier on the repaymentcustomers.
        List<Dossier> findDossiersOpenByCustomerIdEager = redTapeAgent.findDossiersOpenByCustomerIdEager(repaymentCustomers.get(tradeName).get());
        assertThat(findDossiersOpenByCustomerIdEager).isNotEmpty();
        Dossier repaymentDossier = findDossiersOpenByCustomerIdEager.get(0);
        List<Document> activeDocuments = repaymentDossier.getActiveDocuments(BLOCK);
        assertThat(activeDocuments).isNotEmpty();
        assertThat(activeDocuments.get(0).getPositions(UNIT)).isNotEmpty();

        Report report = new Report("Test", tradeName, new Date(), new Date());
        List<ReportLine> reportLines = reportLineEao.findAll();
        List<Storeable> arrayList = new ArrayList<>();
        reportLines.stream().filter((line) -> (line.getDocumentType() == INVOICE)).forEach((line) -> {
            arrayList.add(line.toStorable());
        });
        report = reportAgent.store(report, arrayList);
        assertThat(report).isNotNull();
        assertThat(report.getLines()).isNotEmpty();
        List<ReportLine> notReported = report.getLines().stream().filter((l) -> reportLines.contains(l)).collect(Collectors.toList());

        ReportLine lineToUniqueUnit = notReported.stream().filter((line) -> line.getUniqueUnitId() == uu.getId()).collect(Collectors.toList()).get(0);
        assertThat(lineToUniqueUnit).isNotNull();

        List<ReportLine> repaymentLines = bean.getRepaymentLines(lineToUniqueUnit.getContractor());
        ReportLine repaymentLine = repaymentLines.stream().filter((l) -> l.getRefurbishId().equals(refurbishId)).collect(Collectors.toList()).get(0);

        assertThat(stockAgent.findStockUnitsByRefurbishIdEager(Arrays.asList(refurbishId))).isNotEmpty();
        //Resolving of the Unit.
        bean.resolveUnit(refurbishId, tradeName, "JUnit", "JUnit");

        List<Report> reports = reportAgent.findAll(Report.class);
        assertThat(reports).hasSize(2);
        Report repaymentReport = null;
        // Try to get Report with the Name that is generated in a Static method inside the ResolveRepaymentBean.
        if ( reports.get(0).getName().equals(ResolveRepaymentBean.toReportName(tradeName)) ) repaymentReport = reports.get(0);
        else repaymentReport = reports.get(1);

        repaymentReport = reportAgent.findByIdEager(Report.class, repaymentReport.getId());

        assertThat(repaymentReport.getLines()).hasSize(1);
        assertThat(repaymentLines).contains(repaymentLine);

        // Ensure, that we the mirror Dossier has be cleared of the unit
        findDossiersOpenByCustomerIdEager = redTapeAgent.findDossiersOpenByCustomerIdEager(repaymentCustomers.get(tradeName).get());
        assertThat(findDossiersOpenByCustomerIdEager).isNotEmpty();
        repaymentDossier = findDossiersOpenByCustomerIdEager.get(0);
        activeDocuments = repaymentDossier.getActiveDocuments(BLOCK);
        assertThat(activeDocuments).isNotEmpty();
        assertThat(activeDocuments.get(0).getPositions(UNIT)).isEmpty();
        assertThat(activeDocuments.get(0).getPositions(PositionType.COMMENT)).isNotEmpty(); // We still should have comments there.

        assertThat(stockAgent.findStockUnitsByRefurbishIdEager(Arrays.asList(refurbishId))).isNullOrEmpty();
    }

    @Stateless
    public static class ResolveRepaymentBeanITHelper {

        @Inject
        private ReportLineGenerator generator;

        @Inject
        ReportLineEao eao;

        @EJB
        private UniqueUnitAgent uniqueUnitAgent;

        public void generateLines(int amount) {
            for (int i = 0; i < amount; i++) {
                ReportLine makeReportLine = generator.makeReportLine(Arrays.asList(AMAZON), DateUtils.addDays(new Date(), -30), 25);
                makeReportLine.setPositionType(PositionType.UNIT);
                makeReportLine.setDocumentType(ANNULATION_INVOICE);
                eao.getEntityManager().persist(makeReportLine);
            }
        }

        public void changeContractors(int uniqueUnitID, TradeName name) {
            UniqueUnit uu = uniqueUnitAgent.findByIdEager(UniqueUnit.class, uniqueUnitID);
            uu.setContractor(name);
        }
    }
}
