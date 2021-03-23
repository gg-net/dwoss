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

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.redtape.api.DossierViewer;
import eu.ggnet.dwoss.report.api.ReportApiLocal;
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
    private Instance<ReportApiLocal> reports;

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
        }

        if ( uu.getHistory() != null && !uu.getHistory().isEmpty() ) {
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

        if ( reports.isResolvable() ) {
            re += "<hr />";
            re += "<b>Reporting-Informationen</b>";
            re += reports.get().findReportLinesByUniqueUnitIdAsHtml(id);
        }

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

    /*
    Artikelnummer 	Bezeichnung 	SopoNr 	Serial 	Lieferant 	Shipment 	InputDate 	Status (verfügbar, in transver, nicht im Lager) 	ReportName 	ReportDate 	Weitere Reportinformationen

     */
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

            result.add(new Object[]{
                p.getPartNo(),
                p.getTradeName().getDescription() + " " + p.getName(),
                uu.getRefurbishId(),
                uu.getSerial(),
                uu.getContractor().getDescription(),
                uu.getShipmentLabel(),
                uu.getInputDate(),
                toStatus(su)
            // ReportUnit kommt noch.
            // LocalDate als ordner kommt noch.
            });
        }

        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, WHITE, BLUE, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("Artikelnummer", 12));
        table.add(new STableColumn("Bezeichnung", 20));
        table.add(new STableColumn("RefurbishId", 12));
        table.add(new STableColumn("Serial", 18));
        table.add(new STableColumn("Lieferant", 12));
        table.add(new STableColumn("Shipment", 12));
        table.add(new STableColumn("Aufnahmedatum", 12, new CFormat(SHORT_DATE)));
        table.add(new STableColumn("Status", 12));

        CCalcDocument cdoc = new TempCalcDocument("Artikelreport");
        cdoc.add(new CSheet(partNo, table));

        table.setModel(new STableModelList(result));

        FileJacket fj = new FileJacket(name, ".xls", new JExcelLucidCalcWriter().write(cdoc));
        m.finish();
        return fj;

        /*
        (Artikelnummer,Bezeichnung, SopoNr, Serial, Lieferant, Shipment, InputDate)

        StockApiLocal -> StockUnit
        (Status)
        ReportApiLoca -> function existiert, Api Objekt noch nicht.
        (ReportName, ReportDate, Weitere Reportinformationen)

        nach reportingdate sortieren.

        mache XLS draus.
         */
    }

    private static String toStatus(SimpleStockUnit su) {
        if ( su == null ) return "nicht im Lager";
        if ( su.onLogicTransaction() ) return "im Verkaufporzess/in transfer";
        if ( su.stockTransaction().isPresent() ) return "verfügbar, in Umfuhr/Rollin";
        return "verfügbar";
    }
}
