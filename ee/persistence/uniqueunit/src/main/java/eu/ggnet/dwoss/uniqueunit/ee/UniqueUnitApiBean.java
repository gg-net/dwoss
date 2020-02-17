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

import eu.ggnet.dwoss.redtape.api.DossierViewer;
import eu.ggnet.dwoss.report.api.ReportApiLocal;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.RightsApiLocal;
import eu.ggnet.dwoss.stock.api.StockApiLocal;
import eu.ggnet.dwoss.uniqueunit.api.SimpleUniqueUnit;
import eu.ggnet.dwoss.uniqueunit.api.SimpleUniqueUnit.Builder;
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

    @Inject
    private UniqueUnitEao eao;

    @Inject
    private Instance<DossierViewer> dossierViewer;

    @Inject
    private Instance<RightsApiLocal> rights;

    @Inject
    private Instance<StockApiLocal> stocks;

    @Inject
    private Instance<ReportApiLocal> reports;

    @Override
    public SimpleUniqueUnit findByRefurbishedId(String refurbishId) {
        Builder suBuilder = new SimpleUniqueUnit.Builder();
        UniqueUnit uu = eao.findByIdentifier(Identifier.REFURBISHED_ID, refurbishId);
        if ( uu == null ) {
            uu = eao.findByRefurbishedIdInHistory(refurbishId);
            suBuilder.lastRefurbishId(refurbishId);
        }
        if ( uu == null ) return null;
        return suBuilder
                .id(uu.getId())
                .refurbishedId(uu.getRefurbishId())
                .shortDescription(UniqueUnitFormater.toPositionName(uu))
                .build();
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

        if ( rights.isResolvable() && rights.get().findByName(username).contains(AtomicRight.VIEW_COST_AND_REFERENCE_PRICES) ) {
            re += "<hr />";
            re += "<b>Geräte Preis-Informationen</b>";
            re += UniqueUnitFormater.toHtmlPriceInformation(uu.getPrices(), uu.getPriceHistory());
            re += "<b>Artikel Preis-Informationen</b>";
            re += UniqueUnitFormater.toHtmlPriceInformation(uu.getProduct().getPrices(), uu.getProduct().getPriceHistory());
        }
        return re;
    }

}
