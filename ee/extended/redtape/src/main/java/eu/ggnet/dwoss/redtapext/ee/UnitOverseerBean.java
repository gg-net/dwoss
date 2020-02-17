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
package eu.ggnet.dwoss.redtapext.ee;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.core.common.Css;
import eu.ggnet.dwoss.core.system.autolog.AutoLogger;
import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.redtape.ee.api.LegacyLocalBridge;
import eu.ggnet.dwoss.redtape.ee.api.UnitPositionHook;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.*;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtape.ee.format.DossierFormater;
import eu.ggnet.dwoss.report.ee.assist.Reports;
import eu.ggnet.dwoss.report.ee.eao.ReportLineEao;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.eao.OperatorEao;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.emo.LogicTransactionEmo;
import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.stock.ee.format.StockUnitFormater;
import eu.ggnet.dwoss.uniqueunit.api.UnitShard;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnitHistory;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.redtape.ee.interactiveresult.Result;

import static eu.ggnet.dwoss.core.common.values.PositionType.PRODUCT_BATCH;
import static eu.ggnet.dwoss.report.ee.entity.ReportLine.SingleReferenceType.WARRANTY;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.REFURBISHED_ID;

/**
 * A EJB to supply Information about Units bimport eu.ggnet.dwoss.redtape.api.LegacyRemoteBridge;
 * <p>
 * acked up by multiple data sources.
 * <p/>
 * @author oliver.guenther
 */
@Stateless
@AutoLogger
public class UnitOverseerBean implements UnitOverseer {

    private final Logger L = LoggerFactory.getLogger(UnitOverseerBean.class);

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    @Reports
    private EntityManager reportEm;

    @Inject
    private CustomerServiceBean customerService;

    @Inject
    private Instance<LegacyLocalBridge> bridgeInstance;

    @Inject
    private Instance<UnitPositionHook> redTapeHook;

    @Inject
    private PostLedger postLedger;

    @Inject
    private OperatorEao operatorEao;

    private boolean hasRight(String username, AtomicRight right) {
        if ( username == null ) return false;
        Operator operator = operatorEao.findByUsername(username);
        if ( operator == null ) return false;
        return operator.getAllActiveRights().contains(right);
    }

    /**
     * Find a Unit and its representative and return a html formated String representing it.
     * Ensure to add the html start/end tags manually
     * <p/>
     * @param refurbishId the refurbishedId or serial
     * @param username
     * @return a html formated String representing a Unit.
     */
    @Override
    public String toDetailedHtml(String refurbishId, String username) {
        UniqueUnitEao uuEao = new UniqueUnitEao(uuEm);
        UniqueUnit uniqueUnit = uuEao.findByIdentifier(Identifier.REFURBISHED_ID, refurbishId);
        // Try Serail if Sopo does not match.
        if ( uniqueUnit == null ) uniqueUnit = uuEao.findByIdentifier(Identifier.SERIAL, refurbishId);
        if ( uniqueUnit != null ) return toDetailedHtmlUnit(uniqueUnit, hasRight(username, AtomicRight.VIEW_COST_AND_REFERENCE_PRICES));
        // Unique Unit is null, optional fallback to legacy system.
        if ( !bridgeInstance.isUnsatisfied() && !bridgeInstance.get().isUnitIdentifierAvailable(refurbishId) )
            return "<i><u>Informationen aus Legacy System Sopo:</u></i>" + bridgeInstance.get().toDetailedHtmlUnit(refurbishId);
        return "<h1>Keine Informationen zu SopoNr/Seriennummer " + refurbishId + "</h1>";
    }

    private String toDetailedHtmlUnit(UniqueUnit uniqueUnit, boolean showPrices) {

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyy");

        StockUnit stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(uniqueUnit.getId());
        List<ReportLine> reportLines = new ReportLineEao(reportEm).findByUniqueUnitId(uniqueUnit.getId());

        String re = UniqueUnitFormater.toHtmlDetailed(uniqueUnit);

        TreeSet<Dossier> dossiers = new TreeSet<>(Dossier.ORDER_INVERSE_ACTIVE_ACTUAL);
        for (Position pos : new PositionEao(redTapeEm).findByUniqueUnitId(uniqueUnit.getId())) {
            if ( !pos.getDocument().isActive() ) continue; // For now we ignore all Dossiers which just had the unit in the history
            dossiers.add(pos.getDocument().getDossier());
        }
        re += "<hr />";
        re += "<b>Vorgänge:</b><ul>";
        if ( dossiers.isEmpty() ) re += "<li>Keine Vorgänge vorhanden</li>";
        for (Dossier dossier : dossiers) {
            re += "<li>";
            re += customerService.asUiCustomer(dossier.getCustomerId()).toNameCompanyLine();
            re += DossierFormater.toHtmlSimpleWithDocument(dossier) + "<br /></li>";
        }
        re += "</ul>";
        re += "<hr />";

        if ( uniqueUnit.getHistory() != null && !uniqueUnit.getHistory().isEmpty() ) {
            re += "<b>Unit History:</b><ul>";
            for (UniqueUnitHistory history : new TreeSet<>(uniqueUnit.getHistory())) {
                re += "<li>" + df.format(history.getOccurence()) + " - " + history.getComment() + "</li>";
            }
            re += "</ul>";
        }
        re += "<hr />";

        re += "<p><b>Lagerinformationen</b><br />";
        if ( stockUnit == null ) re += "Kein Lagergerät vorhanden<br />";
        else re += StockUnitFormater.toHtml(stockUnit);
        re += "</p>";
        re += "<hr />";
        re += "<b>Reporting-Informationen</b>";
        if ( reportLines == null || reportLines.isEmpty() ) re += "Keine Reporting-Informationen vorhanden<br />";
        else {
            re += "<table border=\"1\"><tr>";
            re += wrap("<th>", "</th>", "Id", "ReportDate", "Kid", "SopoNr", "Type", "Dossier", "Report");
            re += "</tr>";
            for (ReportLine l : reportLines) {
                re += "<tr>";
                re += wrap("<td>", "</td>",
                        l.getId(),
                        Utils.ISO_DATE.format(l.getReportingDate()),
                        l.getCustomerId(),
                        l.getRefurbishId(),
                        l.getPositionType() == PRODUCT_BATCH && l.getReference(WARRANTY) != null ? "Garantieerweiterung" : l.getPositionType().getName(),
                        l.getDossierIdentifier() + ", " + l.getDocumentType().getName() + l.getWorkflowStatus().sign + (l.getDocumentIdentifier() == null ? "" : ", " + l.getDocumentIdentifier()),
                        l.getReports().stream().map(Report::getName).collect(Collectors.joining(","))
                );
            }
            re += "</table><br />";
        }
        if ( !showPrices ) return re;
        re += "<hr />";
        re += "<b>Geräte Preis-Informationen</b>";
        re += UniqueUnitFormater.toHtmlPriceInformation(uniqueUnit.getPrices(), uniqueUnit.getPriceHistory());
        re += "<b>Artikel Preis-Informationen</b>";
        re += UniqueUnitFormater.toHtmlPriceInformation(uniqueUnit.getProduct().getPrices(), uniqueUnit.getProduct().getPriceHistory());
        return Css.toHtml5WithStyle(re);
    }

    private static String wrap(String head, String foot, Object... elmes) {
        StringBuilder sb = new StringBuilder();
        for (Object elme : elmes) {
            sb.append(head).append(elme).append(foot);
        }
        return sb.toString();
    }

    /**
     * Find an available StockUnit and locks it by add to a LogicTransaction via DossierId.
     * <p/>
     * If no unit is found a LayerEightException is thrown.
     * <p/>
     * @param dossierId     The Dossiers ID
     * @param refurbishedId The refurbished id for the Unique Unit search
     * @throws IllegalStateException if the refurbishId is not available
     */
    @Override
    public void lockStockUnit(long dossierId, String refurbishedId) throws IllegalStateException {
        if ( !internalFind(refurbishedId).isAvailable() )
            throw new IllegalStateException("Trying to lock refusbishId " + refurbishedId + ", but it is not available!");
        UniqueUnit uu = new UniqueUnitEao(uuEm).findByIdentifier(Identifier.REFURBISHED_ID, refurbishedId);
        StockUnit stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(uu.getId());
        LogicTransaction lt = new LogicTransactionEmo(stockEm).request(dossierId);
        lt.add(stockUnit);
    }

    /**
     * Find a Unit by its refurbished id and returns it.
     * <p/>
     * This method will throw a UserInfoException describing, why the unit is not available.
     * <p/>
     * @param refurbishId The refurbished id of the UniqueUnit
     * @param documentId  the document as reference for tax and more.
     * @return a Unit by its refurbished id or null if nothing is found of the unit is not available.
     * @throws UserInfoException if the refurbishId is not available
     */
    @Override
    public Result<List<Position>> createUnitPosition(String refurbishId, long documentId) throws UserInfoException {
        UnitShard us = internalFind(refurbishId);
        if ( !us.isAvailable() ) throwNotAvailable(refurbishId, us);

        Document doc = new DocumentEao(redTapeEm).findById(documentId);
        UniqueUnit uu = new UniqueUnitEao(uuEm).findByIdentifier(Identifier.REFURBISHED_ID, refurbishId);

        Position p = Position.builder()
                .amount(1)
                .price(0.)
                .serialNumber(uu.getSerial())
                .refurbishedId(uu.getRefurbishId())
                .bookingAccount(postLedger.get(PositionType.UNIT, doc.getTaxType()).orElse(null))
                .type(PositionType.UNIT)
                .tax(doc.getTaxType().getTax())
                .uniqueUnitId(uu.getId())
                .uniqueUnitProductId(uu.getProduct().getId())
                .name(UniqueUnitFormater.toPositionName(uu))
                .description(UniqueUnitFormater.toDetailedDiscriptionLine(uu))
                .build();

        if ( redTapeHook.isUnsatisfied() ) return new Result(Arrays.asList(p)); //return Result
        return redTapeHook.get().elaborateUnitPosition(p, documentId);
    }

    /**
     * Build and throw an exception for a not available unit.
     * <p>
     * @param refurbishId the refurbished id of the unit
     * @param us          the unit shard
     * @throws UserInfoException
     */
    private void throwNotAvailable(String refurbishId, UnitShard us) throws UserInfoException {
        if ( us.getAvailable() == null ) throw new UserInfoException("SopoNr " + refurbishId + " existiert nicht"); // <- auch in di auslagerung...
        StockUnit stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(us.getUniqueUnitId());
        if ( stockUnit != null && stockUnit.getLogicTransaction() != null ) {
            Dossier dos = new DossierEao(redTapeEm).findById(stockUnit.getLogicTransaction().getDossierId());
            if ( dos == null )
                throw new UserInfoException("SopoNr " + refurbishId + " is on a LogicTransaction, but there is no Dossier, inform Team Software");
            UiCustomer customer = customerService.asUiCustomer(dos.getCustomerId());
            if ( customer == null )
                throw new UserInfoException("SopoNr " + refurbishId + " is on Dossier " + dos.getIdentifier() + ", but Customer " + dos.getCustomerId() + " does not exist.");
            throw new UserInfoException("SopoNr " + refurbishId + " ist schon vergeben"
                    + "\nKID = " + customer.getId()
                    + "\nKunde = " + customer.toTitleNameLine()
                    + "\n\nVorgang = " + dos.getIdentifier());
        }
    }

    private UnitShard internalFind(String refurbishId) {
        L.debug("find({})", refurbishId);
        UniqueUnitEao uuEao = new UniqueUnitEao(uuEm);
        UniqueUnit uu = uuEao.findByIdentifier(Identifier.REFURBISHED_ID, refurbishId);
        String oldRefurbishedOd = null;
        L.debug("find({}) uniqueUnit={}", refurbishId, uu);
        if ( uu == null ) {
            uu = uuEao.findByRefurbishedIdInHistory(refurbishId);
            if ( uu == null ) {
                if ( !bridgeInstance.isUnsatisfied() && !bridgeInstance.get().isUnitIdentifierAvailable(refurbishId) ) {
                    return new UnitShard(refurbishId, 0, toHtmlDescription(refurbishId, null, "Nicht Verfügbar", "", "(Auskunft aus Sopo)"), false, null);
                } else {
                    return new UnitShard(refurbishId, 0, "<html>SopoNr.:<b>" + refurbishId + "<u> existiert nicht.</u><br /><br /></b></html>", null, null);
                }
            } else {
                oldRefurbishedOd = "(Frühere SopoNr: " + refurbishId + ")";
                refurbishId = uu.getIdentifier(REFURBISHED_ID);
            }
        }

        StockUnit stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(uu.getId());
        L.debug("find({}) stockUnit={}", refurbishId, stockUnit);
        Integer stockId = null;
        if ( stockUnit != null && stockUnit.isInStock() ) stockId = stockUnit.getStock().getId();
        if ( stockUnit == null ) {
            return new UnitShard(refurbishId, uu.getId(), toHtmlDescription(refurbishId, oldRefurbishedOd, "Nicht Verfügbar", "", null), false, null);
        }
        if ( stockUnit.getLogicTransaction() != null ) {
            return new UnitShard(refurbishId, uu.getId(), toHtmlDescription(refurbishId, oldRefurbishedOd, "Nicht Verfügbar", stockUnit, null), false, stockId);
        }
        // If the Database is clean, the Unit is available, but we make some safty checks here.
        if ( new DossierEao(redTapeEm).isUnitBlocked(uu.getId()) ) {
            L.warn("find({}) Database Error RedTape sanity check", refurbishId);
            return new UnitShard(refurbishId, uu.getId(), toHtmlDescription(refurbishId, oldRefurbishedOd, "Nicht Verfügbar", stockUnit, "(Datenbankfehler, RedTape sanity check!)"), false, stockId);
        }

        // Now we are shure.
        return new UnitShard(refurbishId, uu.getId(), toHtmlDescription(refurbishId, oldRefurbishedOd, "Verfügbar", stockUnit, null), true, stockId);
    }

    private String toHtmlDescription(String refurbishId, String oldRefurbishedId, String status, StockUnit stockUnit, String error) {
        String stockInfo = "";
        if ( stockUnit.isInTransaction() ) stockInfo = "Transaction(" + stockUnit.getTransaction().getId() + "," + stockUnit.getTransaction().getType() + ")"
                    + (stockUnit.getTransaction().getSource() == null ? "" : " von " + stockUnit.getTransaction().getSource().getName())
                    + (stockUnit.getTransaction().getDestination() == null ? "" : " nach " + stockUnit.getTransaction().getDestination().getName());
        else if ( stockUnit.isInStock() ) stockInfo = stockUnit.getStock().getName();
        return toHtmlDescription(refurbishId, oldRefurbishedId, status, stockInfo, error);
    }

    private String toHtmlDescription(String refurbishId, String oldRefurbishedId, String status, String stockInfo, String error) {
        String result = "<html>"
                + "<table border =\"0\" width=\"100%\">"
                + "<tr>"
                + "<td width=\"90px\">SopoNr.: <b>" + refurbishId + "</b></td>"
                + "<td align=\"right\" width=\"105px\"><b>" + status + "</b></td>"
                + "</tr>";
        if ( error != null ) {
            result += "<tr>"
                    + "<td width=\"180px\" colspan=\"2\"><i>" + error + "</i></td>"
                    + "</tr>";
        } else if ( oldRefurbishedId != null ) {
            result += "<tr>"
                    + "<td width=\"180px\" colspan=\"2\"><i>" + oldRefurbishedId + "</i></td>"
                    + "</tr>";
        }
        result += "<tr>"
                + "<td width=\"180px\" colspan=\"2\">" + stockInfo + "</td>"
                + "</tr>"
                + "</table>"
                + "</html>";
        return result;
    }

}
