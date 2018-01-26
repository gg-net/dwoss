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
package eu.ggnet.dwoss.price.engine.support;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.price.engine.PriceEngineResult;

public class PriceEngineResultFormater {

    public static String toSimpleHtml(PriceEngineResult per) {
        if ( per == null ) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr><td>SopoNr :</td><td>").append(per.getRefurbishedId()).append("</td></tr>");
        sb.append("<tr><td>ArtikelNr :</td><td>").append(per.getManufacturerPartNo()).append("</td></tr>");
        sb.append("<tr><td>Name :</td><td>").append(per.getProductName()).append("</td></tr>");
        sb.append("<tr><td>Cost Preis :</td><td>").append(per.getCostPrice()).append("</td></tr>");
        sb.append("<tr><td>Contractor Referenc Preis :</td><td>").append(per.getContractorReferencePrice()).append("</td></tr>");
        sb.append("<tr><td>calc HEK :</td><td>").append(per.getRetailerPrice()).append("</td></tr>");
        sb.append("<tr><td>calc EVK :</td><td>").append(per.getCustomerPrice()).append("</td></tr>");
        sb.append("<tr><td>EVK (inc.Tax) :</td><td>").append(per.getCustomerPrice() * (1 + GlobalConfig.DEFAULT_TAX.getTax())).append("</td></tr>");
        sb.append("</table>");
        if ( per.isError() || per.isWarning() ) {
            sb.append("<p>ErrorLog : ").append(per.getRulesLog()).append("</p>");
        }
        return sb.toString();
    }
}
