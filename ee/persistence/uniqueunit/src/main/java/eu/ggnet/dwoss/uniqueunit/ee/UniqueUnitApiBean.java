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
import java.util.TreeSet;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.redtape.api.DossierViewer;
import eu.ggnet.dwoss.report.api.ReportApiLocal;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.UserApiLocal;
import eu.ggnet.dwoss.stock.api.StockApiLocal;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnitHistory;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class UniqueUnitApiBean implements UniqueUnitApi {

    private final static Logger L = LoggerFactory.getLogger(UniqueUnitApiBean.class);

    @Inject
    private UniqueUnitEao eao;

    @Inject
    private Instance<DossierViewer> dossierViewer;

    @Inject
    private Instance<UserApiLocal> rights;

    @Inject
    private Instance<StockApiLocal> stocks;

    @Inject
    private Instance<ReportApiLocal> reports;

    @Override
    public String findBySerialAsHtml(String serial, String username) {
        UniqueUnit uu = eao.findByIdentifier(Identifier.SERIAL, serial);
        if ( uu == null ) return "Kein Gerät mit Seriennummer: " + serial;
        return findAsHtml(uu.getId(), username);
    }

    @Override
    public String findAsHtml(long id, String username) {
        UniqueUnit uu = eao.findById((int)id);
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

        if ( stocks.isResolvable() ) {
            re += "<hr />";
            re += "<p><b>Lagerinformationen</b><br />";
            re += stocks.get().findByUniqueUnitIdAsHtml(id);
            re += "</p>";
        }

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
        UniqueUnit uu = eao.findById(uniqueUnitId);
        if ( uu == null ) throw new UserInfoException("Keine Unit mit der Id " + uniqueUnitId + " gefunden");
        uu.addHistory(history + " - " + arranger);
    }

    @Override
    public void addHistoryByRefurbishId(String refurbishId, String history, String arranger) throws UserInfoException {
        if ( history == null || history.isBlank() ) throw new UserInfoException("history darf nicht null oder leer sein");
        if ( arranger == null || arranger.isBlank() ) throw new UserInfoException("arranger dar nicht null oder leer sein");
        if ( refurbishId == null || refurbishId.isBlank() ) throw new UserInfoException("refurbishId darf nicht null oder leer sein");
        UniqueUnit uu = eao.findByIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, refurbishId);
        if ( uu == null ) throw new UserInfoException("Keine Unit mit der Refurbishid " + refurbishId + " gefunden");
        uu.addHistory(history + " - " + arranger);
    }

}
