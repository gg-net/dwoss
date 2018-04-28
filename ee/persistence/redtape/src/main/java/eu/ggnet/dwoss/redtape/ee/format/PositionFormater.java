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
package eu.ggnet.dwoss.redtape.ee.format;

import java.text.DecimalFormat;
import java.util.Scanner;

import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.common.api.values.PositionType;

/**
 * Formatter for the Position.
 * <p/>
 * @author oliver.guenther
 */
public class PositionFormater {

    private static DecimalFormat CUR = new DecimalFormat("#,##0.00 €");

    private static DecimalFormat A = new DecimalFormat("0.##");

    /**
     * Return a HTML string of the name.
     * The result looks like: <div>position.name</div>
     * <p/>
     * @param position
     * @param width    the width of the use embedded html table.
     * @return
     */
    public static String toHtmlShort(Position position, String width) {
        if ( position == null ) return "Position is null";
        StringBuilder formatted = new StringBuilder("<table width=\"" + width + "\">");
        if ( position.getType() == PositionType.UNIT ) {
            Scanner sc = new Scanner(position.getName());
            formatted.append("<tr><td>");
            do {
                if ( formatted.toString().endsWith("</td></tr>") ) formatted.append("<tr><td>");
                String token = sc.next();
                if ( token.startsWith("SopoNr:") ) {
                    formatted.append("</td></tr><tr><td>").append(token.substring(0, 7)).append("<b>").append(token.substring(7)).append("</b> ");
                } else formatted.append(token).append(" ");
            } while (sc.hasNext());
            if ( !formatted.toString().endsWith("</td></tr>") ) formatted.append("</td></tr>");
            if ( Math.abs(position.getPrice()) > 0.0001 ) {
                formatted.append("<tr><td>");
                formatted.append("netto: ").append(CUR.format(position.getPrice()));
                formatted.append(" | brutto: ").append(CUR.format(position.toAfterTaxPrice()));
                formatted.append("</td></tr>");
            }
        } else {
            formatted.append("<tr><td><u>").append(position.getName()).append("</u></td></tr>");
            formatted.append("<tr><td>").append(position.getDescription()).append("</td></tr>");
            if ( Math.abs(position.getPrice()) > 0.0001 ) {
                formatted.append("<tr><td>");
                formatted.append("Menge: ").append(A.format(position.getAmount()));
                formatted.append(" | netto: ").append(CUR.format(position.getPrice() * position.getAmount()));
                formatted.append(" | brutto: ").append(CUR.format(position.toAfterTaxPrice() * position.getAmount()));
                formatted.append("</td></tr>");
            }
        }
        formatted.append("</table>");
        return formatted.toString();
    }

    public static String toHtmlShortOld(Position position, int width) {
        if ( position == null ) return "Position is null";
        StringBuilder formatted = new StringBuilder("<table border=1 width=\"" + width + "px\">");
        if ( position.getType() == PositionType.UNIT ) {
            Scanner sc = new Scanner(position.getName());
            formatted.append("<tr><td>");
            do {
                if ( formatted.toString().endsWith("</td></tr>") ) formatted.append("<tr><td>");
                String token = sc.next();
                if ( token.startsWith("SopoNr:") ) {
                    formatted.append("</td></tr><tr><td>").append(token.substring(0, 7)).append("<b>").append(token.substring(7)).append("</b> ");
                } else formatted.append(token).append(" ");
            } while (sc.hasNext());
            if ( !formatted.toString().endsWith("</td></tr>") ) formatted.append("</td></tr>");
            if ( Math.abs(position.getPrice()) > 0.0001 ) {
                formatted.append("<tr><td>");
                formatted.append("netto: ").append(CUR.format(position.getPrice()));
                formatted.append(" | brutto: ").append(CUR.format(position.toAfterTaxPrice()));
                formatted.append("</td></tr>");
            }
        } else {
            formatted.append("<tr><td><u>").append(position.getName()).append("</u></td></tr>");
            formatted.append("<tr><td>").append(position.getDescription()).append("</td></tr>");
            if ( Math.abs(position.getPrice()) > 0.0001 ) {
                formatted.append("<tr><td>");
                formatted.append("Menge: ").append(A.format(position.getAmount()));
                formatted.append(" | netto: ").append(CUR.format(position.getPrice() * position.getAmount()));
                formatted.append(" | brutto: ").append(CUR.format(position.toAfterTaxPrice() * position.getAmount()));
                formatted.append("</td></tr>");
            }
        }
        formatted.append("</table>");
        return formatted.toString();
    }

    public static String toHtmlDetailed(Position pos) {
        String res = "";
        res += "Positionstyp: " + pos.getType().getName() + " | " + pos.getType() + "<br />";
        res += pos.getName() + "<br />";
        res += pos.getDescription() == null ? "Keine beschreibung vorhanden<br />" : pos.getDescription() + "<br />";
        if ( pos.getType() != PositionType.COMMENT ) {
            res += "Netto: " + CUR.format(pos.getPrice() * pos.getAmount()) + " | Brutto: " + CUR.format(pos.toAfterTaxPrice() * pos.getAmount()) + "| Buchungskonto: " + pos.getBookingAccount() + "<br />";
        }
        return res;
    }

    public static void main(String[] args) {
        Position p = new Position();
        p.setType(PositionType.UNIT);
        p.setName("Acer Aspire Stein (AA.BBBBB.CCC) SopoNr:12345 SN:AAAABBBBAVAFDADFDASFD");
        p.setPrice(10);
        System.out.println(PositionFormater.toHtmlShort(p, "250px"));

        p = new Position();
        p.setType(PositionType.PRODUCT_BATCH);
        p.setName("Acer Aspire Stein (AA.BBBBB.CCC)");
        p.setAmount(1.25);
        p.setDescription("Eine Tolle Beschreibung");
        p.setPrice(10);
        System.out.println(PositionFormater.toHtmlShort(p, "250px"));

    }
}
