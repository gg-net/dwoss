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
package eu.ggnet.dwoss.misc.op;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.*;

import org.apache.commons.lang3.time.DateUtils;

import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.event.UnitHistory;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.redtape.entity.Document.Condition;
import eu.ggnet.dwoss.report.ReportAgent;
import eu.ggnet.dwoss.report.eao.ReportLineEao;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.eao.StockEao;
import eu.ggnet.dwoss.stock.emo.StockTransactionEmo;
import eu.ggnet.dwoss.stock.entity.*;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.rules.DocumentType.ANNULATION_INVOICE;
import static eu.ggnet.dwoss.rules.DocumentType.CREDIT_MEMO;
import static eu.ggnet.dwoss.uniqueunit.entity.PriceType.CONTRACTOR_REFERENCE;
import static eu.ggnet.dwoss.uniqueunit.entity.PriceType.MANUFACTURER_COST;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

/**
 *
 * @author bastian.venz
 */
@Stateless
public class ResolveRepaymentBean implements ResolveRepayment {

    private static final Date startThisYear;

    private static final Date endhisYear;

    static {
        startThisYear = DateUtils.round(DateUtils.setMonths(new Date(), 1), Calendar.YEAR);
        endhisYear = DateUtils.addYears(DateUtils.addMilliseconds(startThisYear, -1), 1);
    }

    @Inject
    private ReportLineEao reportLineEao;

    @Inject
    private StockAgent stockAgent;

    @Inject
    private StockTransactionEmo stEmo;

    @Inject
    private Event<UnitHistory> history;

    @Inject
    private ReportAgent reportAgent;

    @Override
    public List<SimpleReportLine> getRepaymentLines(TradeName contractor) {
        List<ReportLine> findUnreportedUnits = reportLineEao.findUnreportedUnits(contractor, startThisYear, endhisYear);
        return findUnreportedUnits.stream()
                .filter((l) -> {
                    return l.getDocumentType() == ANNULATION_INVOICE || l.getDocumentType() == CREDIT_MEMO;
                }).map((l) -> {
                    return new SimpleReportLine(l.getReportingDate(), l.getRefurbishId(), l.getUniqueUnitId(), l.getContractor(), l.getPartNo(),
                            l.getProductName(), l.getAmount(), l.getPrice(), l.getPurchasePrice(), l.getContractorReferencePrice(),
                            l.getDocumentType(), l.getPositionType(), l.getSerial());
                }).collect(Collectors.toList());
    }

    @Override
    public void resolveSopo(String identifier, TradeName contractor, String arranger) throws UserInfoException {
        //search with refurbishid and serial number.
        List<SimpleReportLine> reportLines = reportLineEao.findReportLinesByIdentifiers(identifier.trim());

        List<SimpleReportLine> repaymentLines = getRepaymentLines(contractor);
        System.out.println(repaymentLines.size());
        ReportLine line = null;
        for (SimpleReportLine reportLine : reportLines) {
            System.out.println("reportLine:" + reportLine);
            if ( repaymentLines.contains(reportLine) ) {
                System.out.println("line:" + line);
                line = reportLineEao.findById(reportLine.getId());
            }
        }

        System.out.println("foud: " + line);
        if ( line == null ) throw new UserInfoException("Es konnte keine ReportLine mit diesem Identifier gefunden werden");
        if ( !line.getReports().isEmpty() ) throw new UserInfoException("ReportLine ist schon in einem Report.");

        // Rolling out
        StockUnit stockUnit = stockAgent.findStockUnitByRefurbishIdEager(line.getRefurbishId());
        if ( stockUnit == null ) throw new UserInfoException("Es exestiert keine Stock Unit zu dem Gerät");
        List<StockTransaction> stockTransactions = new ArrayList<>();
        StockTransaction st = stEmo.requestRollOutPrepared(stockUnit.getId(), arranger, "Resolve Repayment");
        st.addUnit(stockUnit);
        history.fire(new UnitHistory(stockUnit.getUniqueUnitId(), "Resolve Repayment", arranger));
        if ( !stockTransactions.isEmpty() ) stEmo.completeRollOut(arranger, stockTransactions);

        Report report = reportAgent.findOrCreateReport(contractor.getName() + " Gutschriften " + new SimpleDateFormat("yyyy").format(startThisYear),
                contractor, startThisYear, endhisYear);
        report.add(line);
        reportLineEao.getEntityManager().merge(report);

    }

}
