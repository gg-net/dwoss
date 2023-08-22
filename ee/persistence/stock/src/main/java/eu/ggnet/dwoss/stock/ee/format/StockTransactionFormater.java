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
package eu.ggnet.dwoss.stock.ee.format;

import java.text.DateFormat;
import java.util.*;

import eu.ggnet.dwoss.stock.ee.entity.*;

public class StockTransactionFormater {

    public static String toHtml(StockTransaction tx) {
        SortedMap<String, SortedSet<String>> detailMap = new TreeMap<>();
        String shadow = "Schatten";
        String miss = "Lagerplatz unbekannt";

        for (StockTransactionPosition position : tx.getPositions()) {
            String group;
            if ( position.getStockUnit() == null ) {
                group = shadow;
            } else if ( position.getStockUnit().getStock() != null ) {
                group = position.getStockUnit().getStock().getName();
            } else {
                group = miss;
            }
            SortedSet<String> groupSet = detailMap.get(group);
            if ( groupSet == null ) {
                groupSet = new TreeSet<>();
                detailMap.put(group, groupSet);
            }
            groupSet.add(position.getStockUnit() == null ? position.getDescription() : position.getStockUnit().getRefurbishId() + " - " + position.getStockUnit().getName());
        }

        String details = "<table><tr><td>"
                + "<h3><u><i>Transaktion Nummer:</i></u> " + tx.getId()
                + "<br /><u><i>Type:</i></u> " + tx.getType()
                + "<br /><u><i>Status:</i></u> " + tx.getStatus().getType()
                + "</h3>"
                + "<h3><u><i>Quelle:</i></u>" + (tx.getSource() == null ? "Keine" : tx.getSource().getName())
                + "<br /><u><i>Ziel:</i></u> " + (tx.getDestination() == null ? "Keine" : tx.getDestination().getName())
                + "</h3>"
                + "</td>"
                + "<b><u>Verlauf:</u></b><br />";
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        for (StockTransactionStatus status : tx.getStatusHistory()) {
            details = details + df.format(status.getOccurence()) + " - " + status.getType() + (status.getComment() == null ? "" : " (" + status.getComment() + ")") + "<br />";
            if ( !status.getParticipations().isEmpty() ) {
                details += "<ul>";
                for (StockTransactionParticipation participation : status.getParticipations()) {
                    details += "<li>" + participation.getType() + " - " + participation.getPraticipantName() + "</li>";
                }
                details += "</ul>";
            }
        }

        details += "<td>"
                + "</td><tr>"
                + "</table>"
                + "<hr />";
        for (String group : detailMap.keySet()) {
            details += "<p>" + group + ":<br />";
            for (String line : detailMap.get(group)) {
                details += line + "<br />";
            }
            details += "</p>";
        }

        details += "<hr />";
        return details;
    }

    public static String toHtml(List<StockTransaction> transactions) {

        String details = "";

        for (StockTransaction tx : transactions) {
            details += "<table><tr><td>"
                    + "<h3><u><i>Transaktion Nummer:</i></u> " + tx.getId()
                    + "<br /><u><i>Type:</i></u> " + tx.getType()
                    + "<br /><u><i>Status:</i></u> " + tx.getStatus().getType()
                    + "</h3>"
                    + "<h3><u><i>Quelle:</i></u>" + (tx.getSource() == null ? "Keine" : tx.getSource().getName())
                    + "<br /><u><i>Ziel:</i></u> " + (tx.getDestination() == null ? "Keine" : tx.getDestination().getName())
                    + "</h3>"
                    + "</td>"
                    + "<b><u>Verlauf:</u></b><br />";
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
            for (StockTransactionStatus status : tx.getStatusHistory()) {
                details = details + df.format(status.getOccurence()) + " - " + status.getType() + (status.getComment() == null ? "" : " (" + status.getComment() + ")") + "<br />";
                if ( !status.getParticipations().isEmpty() ) {
                    details += "<ul>";
                    for (StockTransactionParticipation participation : status.getParticipations()) {
                        details += "<li>" + participation.getType() + " - " + participation.getPraticipantName() + "</li>";
                    }
                    details += "</ul>";
                }
            }

            SortedMap<String, SortedSet<String>> detailMap = new TreeMap<>();
            String shadow = "Schatten";
            String miss = "Lagerplatz unbekannt";

            for (StockTransactionPosition position : tx.getPositions()) {
                String group;
                if ( position.getStockUnit() == null ) {
                    group = shadow;
                } else if ( position.getStockUnit().getStock() != null ) {
                    group = position.getStockUnit().getStock().getName();
                } else {
                    group = miss;
                }
                SortedSet<String> d = detailMap.get(group);
                if ( d == null ) {
                    d = new TreeSet<>();
                    detailMap.put(group, d);
                }
                d.add(position.getStockUnit() == null ? position.getDescription() : position.getStockUnit().getRefurbishId() + " - " + position.getStockUnit().getName());
            }

            details += "<td>"
                    + "</td><tr>"
                    + "</table>"
                    + "<hr />";
            for (String group : detailMap.keySet()) {
                details += "<p>" + group + ":<br />";
                for (String line : detailMap.get(group)) {
                    details += line + "<br />";
                }
                details += "</p>";
            }
            details += "<hr />";
        }
        return details;
    }
}
