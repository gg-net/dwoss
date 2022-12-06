/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ee;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.redtape.api.DossierViewer;
import eu.ggnet.dwoss.report.api.*;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.UserApiLocal;
import eu.ggnet.dwoss.stock.api.SimpleStockUnit;
import eu.ggnet.dwoss.stock.api.StockApiLocal;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.uniqueunit.ee.entity.*;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;
import eu.ggnet.lucidcalc.*;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcWriter;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static eu.ggnet.lucidcalc.CFormat.Representation.SHORT_DATE;
import static java.awt.Color.*;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class UniqueUnitApiBean implements UniqueUnitApi {

    private final static Logger L = LoggerFactory.getLogger(UniqueUnitApiBean.class);

    @Inject
    private UniqueUnitEao uuEao;

    @Inject
    private ProductEao pEao;

    @Inject
    private Instance<DossierViewer> dossierViewer;

    @Inject
    private Instance<UserApiLocal> rights;

    @Inject
    private StockApiLocal stockApi;

    @Inject
    private ReportApiLocal reportApi;

    @Inject
    private MonitorFactory monitorFactory;

    @Override
    public String findBySerialAsHtml(String serial, String username) {
        UniqueUnit uu = uuEao.findByIdentifier(Identifier.SERIAL, serial);
        if ( uu == null ) return "Kein Gerät mit Seriennummer: " + serial;
        return findAsHtml(uu.getId(), username);
    }

    @Override
    public String findAsHtml(long id, String username) {
        UniqueUnit uu = uuEao.findById((int)id);
        if ( uu == null ) return "<h1>Keine Informationen zu UniqueUnitId " + id + "</h1>";

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        String re = UniqueUnitFormater.toHtmlDetailed(uu);

        if ( dossierViewer.isResolvable() ) {
            re += "<hr />";
            re += "<b>Vorgänge:</b><ul>";
            re += dossierViewer.get().findByUniqueUnitIdAsHtml(uu.getId());
            re += "</ul>";
        }

        if ( uu.getHistory() != null && !uu.getHistory().isEmpty() ) {
            re += "<hr />";
            re += "<b>Unit History:</b><ul>";
            for (UniqueUnitHistory history : new TreeSet<>(uu.getHistory())) {
                re += "<li>" + df.format(history.getOccurence()) + " - " + history.getComment() + "</li>";
            }
            re += "</ul>";
        }

        re += "<hr />";
        re += "<p><b>Lagerinformationen</b><br />";
        re += stockApi.findByUniqueUnitIdAsHtml(id);
        re += "</p>";

        re += "<hr />";
        re += "<b>Reporting-Informationen</b>";
        re += reportApi.findReportLinesByUniqueUnitIdAsHtml(id);

        try {
            if ( rights.isResolvable() && rights.get().findByName(username).getAllRights().contains(AtomicRight.VIEW_COST_AND_REFERENCE_PRICES) ) {
                re += "<hr />";
                re += "<b>Geräte Preis-Informationen</b>";
                re += UniqueUnitFormater.toHtmlPriceInformation(uu.getPrices(), uu.getPriceHistory());
                re += "<b>Artikel Preis-Informationen</b>";
                re += UniqueUnitFormater.toHtmlPriceInformation(uu.getProduct().getPrices(), uu.getProduct().getPriceHistory());
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            // Both are thrown in findByName, if user is missing.
        }
        return re;
    }

    @Override
    public void addHistory(long uniqueUnitId, String history, String arranger) throws UserInfoException {
        if ( history == null || history.isBlank() ) throw new UserInfoException("history darf nicht null oder leer sein");
        if ( arranger == null || arranger.isBlank() ) throw new UserInfoException("arranger dar nicht null oder leer sein");
        UniqueUnit uu = uuEao.findById(uniqueUnitId);
        if ( uu == null ) throw new UserInfoException("Keine Unit mit der Id " + uniqueUnitId + " gefunden");
        uu.addHistory(history + " - " + arranger);
    }

    @Override
    public void addHistoryByRefurbishId(String refurbishId, String history, String arranger) throws UserInfoException {
        if ( history == null || history.isBlank() ) throw new UserInfoException("history darf nicht null oder leer sein");
        if ( arranger == null || arranger.isBlank() ) throw new UserInfoException("arranger dar nicht null oder leer sein");
        if ( refurbishId == null || refurbishId.isBlank() ) throw new UserInfoException("refurbishId darf nicht null oder leer sein");
        UniqueUnit uu = uuEao.findByIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, refurbishId);
        if ( uu == null ) throw new UserInfoException("Keine Unit mit der Refurbishid " + refurbishId + " gefunden");
        uu.addHistory(history + " - " + arranger);
    }

    @Override
    public FileJacket toUnitsOfPartNoAsXls(String partNo) throws UserInfoException {
        if ( partNo == null || partNo.isBlank() ) throw new UserInfoException("Keine Artikelnummer angegeben");
        String name = "Artikelnummerreport " + partNo;
        SubMonitor m = monitorFactory.newSubMonitor(name, 100);
        m.start();
        m.message("loading Artikelnummer " + partNo);
        Product p = pEao.findByPartNo(partNo);
        m.worked(10);
        if ( p == null ) {
            m.finish();
            throw new UserInfoException("Keine Artikel mit Artikelnummer " + partNo + " gefunden");
        }

        List<Object[]> result = new ArrayList<>();

        m.setWorkRemaining(p.getUniqueUnits().size() + 10);
        for (UniqueUnit uu : p.getUniqueUnits()) {
            m.worked(1, "verarbeite " + uu.getRefurbishId());
            SimpleStockUnit su = stockApi.findByUniqueUnitId(uu.getId());
            SimpleReportUnit sru = reportApi.findReportUnit(uu.getId());

            result.add(new Object[]{
                p.getPartNo(),
                p.getTradeName().getDescription() + " " + p.getName(),
                uu.getRefurbishId(),
                uu.getSerial(),
                uu.getContractor().getDescription(),
                uu.getShipmentLabel(),
                uu.getInputDate(),
                toStatus(su),
                toReports(sru),
                toReportPositionTypes(sru),
                toReportInformation(sru, uu)
            });
        }

        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, WHITE, BLUE, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("Artikelnummer", 16));
        table.add(new STableColumn("Bezeichnung", 30));
        table.add(new STableColumn("SopoNr", 12));
        table.add(new STableColumn("Serial", 25));
        table.add(new STableColumn("Lieferant", 15));
        table.add(new STableColumn("Shipment", 15));
        table.add(new STableColumn("Aufnahme", 12, new CFormat(SHORT_DATE)));
        table.add(new STableColumn("Status", 30));
        table.add(new STableColumn("Reports", 40));
        table.add(new STableColumn("ReportType", 20));
        table.add(new STableColumn("ReportInformationen", 120));
        table.setModel(new STableModelList(result));

        CCalcDocument cdoc = new TempCalcDocument("Artikelreport");
        cdoc.add(new CSheet(partNo, table));

        FileJacket fj = new FileJacket(name, ".xls", new JExcelLucidCalcWriter().write(cdoc));
        m.finish();
        return fj;

    }

    private static String toStatus(SimpleStockUnit su) {
        if ( su == null ) return "nicht im Lager";
        if ( su.onLogicTransaction() ) return "im Verkaufporzess/in transfer";
        if ( su.stockTransaction().isPresent() ) return "verfügbar, in Umfuhr/Rollin";
        return "verfügbar";
    }

    private static String toReports(SimpleReportUnit sru) {
        if ( sru == null || sru.lines().isEmpty() ) return "";
        return sru.lines().stream()
                .map(SimpleReportLine::reportName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .collect(Collectors.joining(","));
    }

    private static String toReportPositionTypes(SimpleReportUnit sru) {
        if ( sru == null || sru.lines().isEmpty() ) return "";
        return sru.lines().stream()
                .map(srl -> srl.documentType().description + "(" + (srl.isWarranty() ? "Garantieerweiterung" : srl.positionType().description()) + ")")
                .collect(Collectors.joining(","));
    }

    private static String toReportInformation(eu.ggnet.dwoss.report.api.SimpleReportUnit sru, UniqueUnit uu) {
        if ( sru != null ) return sru.toString();
        var lastHistory = new TreeSet<>(uu.getHistory()).last();        
        return "Letzter Kommentar: " + Utils.ISO_DATE.format(lastHistory.getOccurence()) + " - " + lastHistory.getComment();
    }
}
