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
package eu.ggnet.dwoss.misc.ee;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.common.api.values.*;
import eu.ggnet.dwoss.mandator.api.value.RepaymentCustomers;
import eu.ggnet.dwoss.redtape.ee.eao.DossierEao;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.report.ee.eao.ReportLineEao;
import eu.ggnet.dwoss.report.ee.emo.ReportEmo;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.entity.ReportLine.SingleReferenceType;
import eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.uniqueunit.api.event.UnitHistory;
import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.common.api.values.DocumentType.ANNULATION_INVOICE;
import static eu.ggnet.dwoss.common.api.values.DocumentType.CREDIT_MEMO;
import static eu.ggnet.dwoss.common.api.values.PositionType.UNIT;
import static java.time.ZoneId.systemDefault;

/**
 *
 * @author bastian.venz
 */
@Stateless
public class ResolveRepaymentBean implements ResolveRepayment {

    private static final Logger L = LoggerFactory.getLogger(ResolveRepaymentBean.class);

    @Inject
    private ReportLineEao reportLineEao;

    @Inject
    private StockUnitEao stockUnitEao;

    @Inject
    private StockTransactionEmo stEmo;

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    private Event<UnitHistory> history;

    @Inject
    private ReportEmo reportEmo;

    @Inject
    private DossierEao dossierEao;

    @Inject
    private RepaymentCustomers repaymentCustomers;

    @Override
    public List<ReportLine> getRepaymentLines(TradeName contractor) {
        List<ReportLine> findUnreportedUnits = reportLineEao.findUnreportedUnits(contractor, null, new Date()); // All
        return findUnreportedUnits.stream()
                .filter((l) -> {
                    return l.getDocumentType() == ANNULATION_INVOICE || l.getDocumentType() == CREDIT_MEMO;
                }).collect(Collectors.toList());
    }

    @Override
    public ResolveResult resolveUnit(String identifier, TradeName contractor, String arranger, String comment) throws UserInfoException {
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

        ReportLine reference = line.getReference(SingleReferenceType.WARRANTY);

        // Rolling out the unit if still in Stock.
        StockUnit stockUnit = line.getPositionType() == UNIT // Saftynet, e.g. unit annex shoud not clear units.
                ? stockUnitEao.findByRefurbishId(line.getRefurbishId())
                : null;
        if ( stockUnit != null && stockUnit.isInTransaction() )
            throw new UserInfoException("Unit is in einer StockTransaction. ID:" + stockUnit.getTransaction().getId());

        ResolveResult msgs = new ResolveResult();
        if ( stockUnit == null ) {
            msgs.stockMessage = "Es existiert keine Stock Unit (mehr) zu dem Gerät";
            msgs.redTapeMessage = "Keine StockUnit, Kein Vorgang";
        } else {
            LogicTransaction lt = stockUnit.getLogicTransaction();
            long dossierId = lt.getDossierId();
            Dossier dossier = dossierEao.findById(dossierId);

            if ( !repaymentCustomers.get(contractor).isPresent()
                    || !repaymentCustomers.get(contractor).get().equals(dossier.getCustomerId()) ) {
                throw new UserInfoException("Unit is nicht auf einem Auftrag eines Repayment Customers. DossierId:" + dossier.getId());
            }
            List<Document> activeDocuments = dossier.getActiveDocuments(DocumentType.BLOCK);
            if ( activeDocuments.size() != 1 ) {
                throw new UserInfoException("Der Gutschriftsvorgang " + dossier.toSimpleLine() + " ist fehlerhaft, entweder kein oder zu viele akive Blocker");
            }
            Position pos = activeDocuments.get(0).getPositionByUniqueUnitId(stockUnit.getUniqueUnitId());
            if ( pos == null ) {
                throw new UserInfoException("Auf Gutschriftsvorgang " + dossier.toSimpleLine() + " ist das Gerät " + stockUnit.toSimple() + " nicht auffindbar");
            }
            msgs.redTapeMessage = "Kid: " + dossier.getCustomerId() + ", Vorgang:" + dossier.getIdentifier() + " wurde Gerät entfernt";
            convertToComment(pos, arranger, comment);
            lt.remove(stockUnit);

            StockTransaction st = stEmo.requestRollOutPrepared(stockUnit.getStock().getId(), arranger, "Resolved Repayment");
            st.addUnit(stockUnit);
            msgs.stockMessage = stockUnit.toSimple() + " aus Lager ausgerollt auf StockTransaction(id=" + st.getId() + ")";
            history.fire(UnitHistory.create(stockUnit.getUniqueUnitId(), "Resolved Repayment", arranger));
            stEmo.completeRollOut(arranger, Arrays.asList(st));
            stockEm.flush();
            if ( lt.getUnits().isEmpty() ) {
                msgs.stockMessage += ", LogicTransaction " + lt.getId() + " ist jetzt leer, wird gelöscht";
                stockEm.remove(lt);
            }

        }

        Date startOfYear = Date.from(LocalDate.of(LocalDate.now().getYear(), 1, 1).atStartOfDay(systemDefault()).toInstant());
        Date endOfYear = Date.from(LocalDate.of(LocalDate.now().getYear(), 12, 31).atStartOfDay(systemDefault()).toInstant());

        Report report = reportEmo.request(toReportName(contractor), contractor, startOfYear, endOfYear);
        line.setComment(comment);
        report.add(line);
        msgs.reportMessage = "Repayment Unit " + line.getRefurbishId() + " line " + line.getId() + " resolved in " + report.getName();
        if ( reference != null ) {
            L.info("Warrenty Reference exist. Putted also into the report. ReportLine ID of Warrenty:{}", reference.getId());
            reference.setComment(comment);
            report.add(reference);
            msgs.reportMessage += ", including warranty " + reference.getId();
        }
        return msgs;
    }

    /**
     * This Returns the Name of a Report,based on contractor and year.
     * <p>
     * @param contractor
     * @return
     */
    public static String toReportName(TradeName contractor) {
        return contractor.getName() + " Gutschriften " + LocalDate.now().getYear();
    }

    private void convertToComment(Position position, String arranger, String comment) {
        position.setType(PositionType.COMMENT);
        position.setUniqueUnitId(0);
        position.setUniqueUnitProductId(0);
        position.setPrice(0);
        position.setDescription("Entfernt durch Gutschrifstausgleich von " + arranger + ", war: " + position.getName() + ", Kommentar:" + comment);
        position.setName("Entfernt durch Gutschrifstausgleich von " + arranger);
    }

}
