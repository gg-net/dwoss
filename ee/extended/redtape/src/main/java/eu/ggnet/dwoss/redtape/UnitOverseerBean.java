package eu.ggnet.dwoss.redtape;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.util.MathUtil;
import eu.ggnet.dwoss.util.DateFormats;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.redtape.api.LegacyBridge;
import eu.ggnet.dwoss.redtape.eao.DossierEao;
import eu.ggnet.dwoss.redtape.api.RedTapeHookService;
import eu.ggnet.dwoss.redtape.eao.PositionEao;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.*;

import eu.ggnet.dwoss.common.log.AutoLogger;
import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.format.DossierFormater;
import eu.ggnet.dwoss.report.assist.Reports;
import eu.ggnet.dwoss.report.eao.ReportLineEao;

import eu.ggnet.dwoss.rights.api.AtomicRight;

import eu.ggnet.dwoss.rights.eao.OperatorEao;
import eu.ggnet.dwoss.rights.entity.Operator;


import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.emo.LogicTransactionEmo;
import eu.ggnet.dwoss.stock.format.StockUnitFormater;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.uniqueunit.format.UniqueUnitFormater;

import eu.ggnet.dwoss.util.interactiveresult.Result;

import static eu.ggnet.dwoss.report.entity.ReportLine.SingleReferenceType.WARRANTY;
import static eu.ggnet.dwoss.rules.PositionType.PRODUCT_BATCH;
import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static java.util.Locale.GERMANY;

/**
 * A EJB to supply Information about Units backed up by multiple data sources.
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
    private CustomerService customerService;

    @Inject
    private Instance<LegacyBridge> bridgeInstance;

    @Inject
    private Instance<RedTapeHookService> redTapeHook;

    @Inject
    private PostLedger postLedger;

    @Inject
    private OperatorEao operatorEao;

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
        boolean viewPrices = false;
        if ( username != null ) {
            Operator operator = operatorEao.findByUsername(username);
            viewPrices = operator.getAllActiveRights().contains(AtomicRight.VIEW_COST_AND_REFERENCE_PRICES);
        }
        if ( uniqueUnit != null ) return toDetailedHtmlUnit(uniqueUnit, viewPrices);
        // Unique Unit is null, optional fallback to legacy system.
        if ( !bridgeInstance.isUnsatisfied() && !bridgeInstance.get().isUnitIdentifierAvailable(refurbishId) )
            return "<i><u>Informationen aus Legacy System Sopo:</u></i>" + bridgeInstance.get().toDetailedHtmlUnit(refurbishId);
        return "<h1>Keine Informationen zu SopoNr/Seriennummer " + refurbishId + "</h1>";
    }

    private String toDetailedHtmlUnit(UniqueUnit uniqueUnit, boolean showPrices) {
        StockUnit stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(uniqueUnit.getId());
        List<ReportLine> reportLines = new ReportLineEao(reportEm).findByUniqueUnitId(uniqueUnit.getId());

        String re = UniqueUnitFormater.toHtmlDetailed(uniqueUnit);

        TreeSet<Dossier> dossiers = new TreeSet<>(Dossier.ORDER_INVERSE_ACTIVE_ACTUAL);
        for (Position pos : new PositionEao(redTapeEm).findByUniqueUnitId(uniqueUnit.getId())) {
            if ( !pos.getDocument().isActive() ) continue; // For now we ignore all Dossiers which just had the unit in the history
            dossiers.add(pos.getDocument().getDossier());
        }
        re += "<hr />";
        re += "<h2>Vorgänge:</h2><ol>";
        if ( dossiers.isEmpty() ) re += "<li>Keine Vorgänge vorhanden</li>";
        for (Dossier dossier : dossiers) {
            re += "<li>";
            re += customerService.asUiCustomer(dossier.getCustomerId()).toNameCompanyLine();
            re += DossierFormater.toHtmlSimpleWithDocument(dossier) + "<br /></li>";
        }
        re += "</ol>";
        re += "<hr />";
        re += "<h2>Lagerinformationen</h2>";
        if ( stockUnit == null ) re += "Kein Lagergerät vorhanden<br />";
        else re += StockUnitFormater.toHtml(stockUnit);
        re += "<hr />";
        re += "<h2>Reporting-Informationen</h2>";
        if ( reportLines == null || reportLines.isEmpty() ) re += "Keine Reporting-Informationen vorhanden<br />";
        else {
            re += "<table border=\"1\"><tr>";
            re += wrap("<th>", "</th>", "Id", "ReportDate", "Kid", "SopoNr", "Type", "Dossier", "Report");
            re += "</tr>";
            for (ReportLine l : reportLines) {
                re += "<tr>";
                re += wrap("<td>", "</td>",
                        l.getId(),
                        DateFormats.ISO.format(l.getReportingDate()),
                        l.getCustomerId(),
                        l.getRefurbishId(),
                        l.getPositionType() == PRODUCT_BATCH && l.getReference(WARRANTY) != null ? "Garantieerweiterung" : l.getPositionType().getName(),
                        l.getDossierIdentifier() + ", " + l.getDocumentType().getName() + l.getWorkflowStatus().getSign() + (l.getDocumentIdentifier() == null ? "" : ", " + l.getDocumentIdentifier()),
                        l.getReports().stream().map(Report::getName).collect(Collectors.joining(","))
                );
            }
            re += "</table><br />";
        }
        if ( !showPrices ) return re;
        re += "<hr />";
        re += "<h2>Preis-Informationen</h2>";
        NumberFormat nf = NumberFormat.getCurrencyInstance(GERMANY);
        re += "<ul><li>Unit Preise:";
        re += uniqueUnit.getPrices().entrySet().stream()
                .map(e -> e.getKey() + " : " + nf.format(e.getValue()))
                .collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"));
        re += "</li><li>Produkt Preise:";
        re += uniqueUnit.getProduct().getPrices().entrySet().stream()
                .map(e -> e.getKey() + " : " + nf.format(e.getValue()))
                .collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"));
        re += "</li>";
        return re;
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
     * @param documentId
     * @return a Unit by its refurbished id or null if nothing is found of the unit is not available.
     * @throws UserInfoException if the refurbishId is not available
     */
    @Override
    public Result<List<Position>> createUnitPosition(String refurbishId, long documentId) throws UserInfoException {
        UnitShard us = internalFind(refurbishId);
        if ( !us.isAvailable() ) throwNotAvailable(refurbishId, us);

        UniqueUnit uu = new UniqueUnitEao(uuEm).findByIdentifier(Identifier.REFURBISHED_ID, refurbishId);

        Position p = Position
                .builder()
                .amount(1)
                .price(0.)
                .tax(GlobalConfig.TAX)
                .afterTaxPrice(MathUtil.roundedApply(0., GlobalConfig.TAX, 0.))
                .serialNumber(uu.getSerial())
                .refurbishedId(uu.getRefurbishId())
                .bookingAccount(postLedger.get(PositionType.UNIT).orElse(-1))
                .type(PositionType.UNIT)
                .tax(GlobalConfig.TAX)
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
     * @param unit        the unit
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

    /**
     * Returns true if the unit identified by the refurbishId is available for sale, else false.
     * <p/>
     * @param refurbishId the id to check
     * @return true if the unit identified by the refurbishId is available for sale, else false.
     */
    @Override
    public boolean isAvailable(String refurbishId) {
        return internalFind(refurbishId).isAvailable();
    }

    /**
     * Returns a UnitShard, a small representation of the refurbishId and its status.
     * <p/>
     * @param refurbishId the refurbishId to check.
     * @return a UnitShard, a small representation of the refurbishId and its status.
     */
    @Override
    public UnitShard find(String refurbishId) {
        return internalFind(refurbishId);
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
                    return new UnitShard(refurbishId, 0, toHtmlDescription(refurbishId, null, "Nicht Verfügbar", "", "(Auskunft aus Sopo)"), false);
                } else {
                    return new UnitShard(refurbishId, 0, "<html>SopoNr.:<b>" + refurbishId + "<u> existiert nicht.</u><br /><br /></b></html>", null);
                }
            } else {
                oldRefurbishedOd = "(Frühere SopoNr: " + refurbishId + ")";
                refurbishId = uu.getIdentifier(REFURBISHED_ID);
            }
        }

        StockUnit stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(uu.getId());
        L.debug("find({}) stockUnit={}", refurbishId, stockUnit);
        if ( stockUnit == null ) {
            return new UnitShard(refurbishId, uu.getId(), toHtmlDescription(refurbishId, oldRefurbishedOd, "Nicht Verfügbar", "", null), false);
        }
        if ( stockUnit.getLogicTransaction() != null ) {
            return new UnitShard(refurbishId, uu.getId(), toHtmlDescription(refurbishId, oldRefurbishedOd, "Nicht Verfügbar", stockUnit, null), false, stockUnit);
        }
        // If the Database is clean, the Unit is available, but we make some safty checks here.
        if ( new DossierEao(redTapeEm).isUnitBlocked(uu.getId()) ) {
            L.warn("find({}) Database Error RedTape sanity check", refurbishId);
            return new UnitShard(refurbishId, uu.getId(), toHtmlDescription(refurbishId, oldRefurbishedOd, "Nicht Verfügbar", stockUnit, "(Datenbankfehler, RedTape sanity check!)"), false, stockUnit);
        }

        // Now we are shure.
        return new UnitShard(refurbishId, uu.getId(), toHtmlDescription(refurbishId, oldRefurbishedOd, "Verfügbar", stockUnit, null), true, stockUnit);
    }

    private String toHtmlDescription(String refurbishId, String oldRefurbishedId, String status, StockUnit stockUnit, String error) {
        String stockInfo = "";
        if ( stockUnit.isInStock() ) stockInfo = stockUnit.getStock().getName();
        else if ( stockUnit.isInTransaction() ) stockInfo = "Transaction(" + stockUnit.getTransaction().getId() + ")"
                    + (stockUnit.getTransaction().getSource() == null ? "" : " von " + stockUnit.getTransaction().getSource().getName())
                    + (stockUnit.getTransaction().getDestination() == null ? "" : " nach " + stockUnit.getTransaction().getDestination().getName());
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
