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
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;

import eu.ggnet.dwoss.event.UnitHistory;
import eu.ggnet.dwoss.mandator.api.value.RepaymentCustomers;
import eu.ggnet.dwoss.redtape.RedTapeAgent;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.report.ReportAgent;
import eu.ggnet.dwoss.report.eao.ReportLineEao;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.emo.StockTransactionEmo;
import eu.ggnet.dwoss.stock.entity.*;
import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.rules.DocumentType.ANNULATION_INVOICE;
import static eu.ggnet.dwoss.rules.DocumentType.CREDIT_MEMO;

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

    @EJB
    private RedTapeAgent redTapeAgent;

    @Inject
    private RepaymentCustomers repaymentCustomers;

    @Override
    public List<ReportLine> getRepaymentLines(TradeName contractor) {
        List<ReportLine> findUnreportedUnits = reportLineEao.findUnreportedUnits(contractor, startThisYear, endhisYear);
        return findUnreportedUnits.stream()
                .filter((l) -> {
                    return l.getDocumentType() == ANNULATION_INVOICE || l.getDocumentType() == CREDIT_MEMO;
                }).collect(Collectors.toList());
    }

    @Override
    public void resolveUnit(String identifier, TradeName contractor, String arranger) throws UserInfoException {
        //search with refurbishid and serial number.
        List<SimpleReportLine> reportLines = reportLineEao.findReportLinesByIdentifiers(identifier.trim());

        List<ReportLine> repaymentLines = getRepaymentLines(contractor);
        ReportLine line = null;

        List<Long> repaymentIds = repaymentLines.stream().map((l) -> l.getId()).collect(Collectors.toList());

        for (SimpleReportLine reportLine : reportLines) {
            if ( repaymentIds.contains(reportLine.getId()) ) {
                line = reportLineEao.findById(reportLine.getId());
            }
        }

        if ( line == null ) throw new UserInfoException("Es konnte keine ReportLine mit diesem Identifier gefunden werden");
        if ( !line.getReports().isEmpty() ) throw new UserInfoException("ReportLine ist schon in einem Report.\nReports:" + line.getReports());
        // Rolling out
        StockUnit stockUnit = stockAgent.findStockUnitByRefurbishIdEager(line.getRefurbishId());
        if ( stockUnit == null ) throw new UserInfoException("Es exestiert keine Stock Unit zu dem Gerät");
        if ( stockUnit.isInTransaction() ) throw new UserInfoException("Unit is in einer StockTransaction. ID:" + stockUnit.getTransaction().getId());

        long dossierId = stockUnit.getLogicTransaction().getDossierId();
        Dossier dossier = redTapeAgent.findById(Dossier.class, dossierId);

        if ( repaymentCustomers.get(contractor) == null || !repaymentCustomers.get(contractor).isPresent()
                || !repaymentCustomers.get(contractor).get().equals(dossier.getCustomerId()) ) {
            throw new UserInfoException("Unit is nicht auf einem Auftrag eines Repayment Customers. DossierId:" + dossier.getId());
        }

        List<StockTransaction> stockTransactions = new ArrayList<>();
        StockTransaction st = stEmo.requestRollOutPrepared(stockUnit.getId(), arranger, "Resolved Repayment");
        st.addUnit(stockUnit);
        stockTransactions.add(st);
        history.fire(new UnitHistory(stockUnit.getUniqueUnitId(), "Resolved Repayment", arranger));
        stEmo.completeRollOut(arranger, stockTransactions);

        Report report = reportAgent.findOrCreateReport(getReportName(contractor),
                contractor, startThisYear, endhisYear);
        report.add(line);

    }

    /**
     * This Returns the Name of a Report,based on contractor and year.
     * <p>
     * @param contractor
     * @return
     */
    public static String getReportName(TradeName contractor) {
        return contractor.getName() + " Gutschriften " + new SimpleDateFormat("yyyy").format(startThisYear);
    }

}
