/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.stock.format;

import eu.ggnet.dwoss.stock.entity.StockUnit;

/**
 *
 * @author pascal.perau
 */
public class StockUnitFormater {

    /**
     * Generates a Html formated string representing a StockUnit.
     * This method does not add html start or end tags to remain stackable for other html formaters.
     * <p/>
     * @param su the StockUnit
     * @return a Html formated string representing a StockUnit.
     */
    public static String detailedTransactionToHtml(StockUnit su) {
        String res = "";
        if ( su != null ) {
            res += "<p>";
            res += "Standort: " + (su.getStock() != null ? su.getStock().getName() + "<br />" : "nicht in einem Lager<br />");
            if ( su.getTransaction() != null ) {
                res += "Transaktionsnummer: " + su.getTransaction().getId() + "<br />";
                res += "Transaktionstyp: " + su.getTransaction().getType() + "<br />";
                res += "Transaktionsstatus: " + su.getTransaction().getStatus().getType() + "<br />";
                String from = (su.getTransaction().getSource() == null ? "" : "von " + su.getTransaction().getSource().getName() + " ");
                String to = (su.getTransaction().getDestination() == null ? "" : "nach " + su.getTransaction().getDestination().getName());
                res += "Transaktion " + from + to;
            }
            res += "</p>";
        }
        return res;
    }

    public static String toHtml(StockUnit su) {
        if ( su == null ) return "StockUnit == null";
        StringBuilder sb = new StringBuilder("StockUnit");
        sb.append("(id=").append(su.getId()).append(", uniqueUnitId=").append(su.getUniqueUnitId()).append(", refurbisId=").append(su.getRefurbishId()).append(")");
        sb.append("<ul>");
        if ( su.isInStock() ) sb.append("<li>Lagerort: ").append(su.getStock().getName()).append("</li>");
        if ( su.isInTransaction() ) {
            sb.append("<li>Aktive Transaktion(id=").append(su.getTransaction().getId()).append(", type=").append(su.getTransaction().getType()).append(")");
            if ( su.getTransaction().getSource() != null ) sb.append(" von ").append(su.getTransaction().getSource().getName());
            if ( su.getTransaction().getDestination() != null ) sb.append(" nach ").append(su.getTransaction().getDestination().getName());
            sb.append(" mit Status ").append(su.getTransaction().getStatus().getType()).append("</li>");
        }
        if ( su.getLogicTransaction() != null ) {
            sb.append("<li>").append("Logische Transaktion(id=").append(su.getLogicTransaction().getId());
            sb.append(", dossierId=").append(su.getLogicTransaction().getDossierId()).append(")</li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }
}
