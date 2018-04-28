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
package eu.ggnet.dwoss.uniqueunit.ee.format;

import java.text.*;
import java.util.Map.Entry;
import java.util.*;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.rules.Warranty;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Equipment;
import eu.ggnet.dwoss.uniqueunit.ee.entity.*;
import eu.ggnet.dwoss.util.DateFormats;
import eu.ggnet.dwoss.common.api.INoteModel;

import static eu.ggnet.dwoss.common.api.values.SalesChannel.UNKNOWN;
import static java.util.Locale.GERMANY;

/**
 * Formatter for UniqueUnits.
 *
 * @author pascal.perau
 */
public abstract class UniqueUnitFormater {

    private static final SimpleDateFormat DF = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * Returns a String like: Acer Aspire 5473 (LX.AAAAA.012) SopoNr:12345 SN:LXAAAAA0123123AABDBSA232.
     *
     * @param unit the unit to format
     * @return the formated String.
     */
    public static String toPositionName(UniqueUnit unit) {
        return ProductFormater.toNameWithPartNo(unit.getProduct())
                + " SopoNr:" + unit.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID)
                + " SN:" + unit.getIdentifier(UniqueUnit.Identifier.SERIAL);
    }

    public static String toSimpleHtml(UniqueUnit unit) {
        if ( unit != null ) {
            String re = "<b>" + unit.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID) + "</b> - ";
            re += unit.getIdentifier(UniqueUnit.Identifier.SERIAL) + "<br />";
            re += ProductFormater.toName(unit.getProduct());

            return re;
        }
        return "Unit null";
    }

    /**
     * Displays all supplied prices and if supplied, the corresponding histories.
     *
     * @param prices
     * @param optionalHistories
     * @return
     */
    public static String toHtmlPriceInformation(Map<PriceType, Double> prices, List<PriceHistory> optionalHistories) {
        if ( prices == null ) return "<b>Fehler, Map of Price is null</b>";
        List<PriceHistory> histories = Optional.ofNullable(optionalHistories).orElse(new ArrayList<>());
        final NumberFormat CUR = NumberFormat.getCurrencyInstance(GERMANY);
        StringBuilder sb = new StringBuilder("<table><tr><th>Preistyp</th><th>Preis(netto)</th>");
        if ( !histories.isEmpty() ) sb.append("<th>History</th>");
        sb.append("</tr>");
        for (Entry<PriceType, Double> entry : prices.entrySet()) {
            PriceType type = entry.getKey();
            Double price = entry.getValue();
            sb.append("<tr>");
            sb.append("<td>").append(type).append("</td>");
            sb.append("<td>").append(CUR.format(entry.getValue())).append("</td>");
            if ( !histories.isEmpty() ) {
                sb.append("<td><ul>");
                histories.stream()
                        .filter(ph -> ph.getType() == type)
                        .sorted(Comparator.comparing(PriceHistory::getDate))
                        .forEach(ph -> sb.append("<li>").append(DateFormats.ISO.format(ph.getDate())).append(" - ").append(CUR.format(ph.getPrice())).append(" - ").append(ph.getComment()).append("</li>"));
                sb.append("</ul></td>");
            }
        }
        sb.append("</tr></table>");
        return sb.toString();
    }

    /**
     * Returns a html formated String with a detailed description of a Unit.
     * Ensure to add the html start/end tags manually
     * The SopoProduct may be null if unnecessary
     * <p/>
     * @param unit the UniqueUnit
     * @return a html formated String with a detailed description of a Unit.
     */
    public static String toHtmlDetailed(UniqueUnit unit) {
        if ( unit == null ) return "<h1>Keine UniqueUnit Spezifikation vorhanden.</h1>";
        Product p = unit.getProduct();
        String re = "<b>SopoNr/RefurbishedId: " + unit.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID)
                + " - " + p.getGroup().getNote() + " - " + p.getTradeName().getName() + " " + p.getName() + "</b><br />";
        re += "<table style=\"width:100%\">"
                + "<tr>"
                + "<th>Seriennummer</th><th>Artikelnummer</th><th>GTIN/EAN</th><th>Lieferantenartikelnummer</th><th>Lieferant</th>"
                + "</tr><tr>"
                + "<td>" + unit.getSerial() + "</td><td>" + p.getPartNo() + "</td><td>" + p.getGtin() + "</td>"
                + "<td>" + Optional.ofNullable(p.getAdditionalPartNo(unit.getContractor())).orElse("Keine Daten") + "</td><td>" + unit.getContractor().getName() + "</td>"
                + "</tr></table>";

        re += "<p>" + p.getDescription() + "</p><p>";

        re += "Zubehör: " + UniqueUnitFormater.toSingleLineAccessories(unit) + "<br />";
        re += "Bemerkung: " + UniqueUnitFormater.toSingleLineComment(unit) + "<br />";
        re += "Interne Bemerkung: " + UniqueUnitFormater.toSingleLineInternalComment(unit) + "<br />";
        re += "Shipment: " + unit.getShipmentLabel() + " (" + unit.getShipmentId() + ")<br />";
        re += "</p>";

        re += "<table style=\"width:100%\">"
                + "<tr>"
                + "<th>juristischer Zustand</th><th>optischer Zustand</th><th>Garantie</th><th>Shipment</th><th>Aufgenommen am</th><th>Mfg Date</th>"
                + "</tr><tr>"
                + "<td>gebraucht</td><td>" + unit.getCondition().getNote() + "</td><td>" + toWarranty(unit) + "</td>"
                + "<td>" + unit.getShipmentLabel() + " (id=" + unit.getShipmentId() + ")</td><td>" + DF.format(unit.getInputDate()) + "</td>"
                + "<td>" + DF.format(unit.getMfgDate()) + "</td>"
                + "</tr></table>";

        re += "<p><b>Preisinformationen</b>";
        re += toHtmlPrices(unit) + "</p>";
        return re;
    }

    private static String toHtmlPrices(UniqueUnit unit) {
        String re = "";
        DecimalFormat decFormat = new DecimalFormat("#0.00");
        String salesChannel = "";
        switch (unit.getSalesChannel()) {
            case CUSTOMER:
                salesChannel = "Endkundenkanal";
                break;
            case RETAILER:
                salesChannel = "Händlerkanal";
                break;
            case UNKNOWN:
                salesChannel = "kein Verkaufskanal";
                break;
        }

        double customerPrice = unit.getPrice(PriceType.CUSTOMER);
        double retailerPrice = unit.getPrice(PriceType.RETAILER);

        re += "<table style=\"width:100%\">"
                + "<tr>"
                + "<th></th>"
                + "<th align=\"right\">netto</th>"
                + "<th align=\"right\">brutto</th>"
                + "</tr><tr>"
                + "<td><b>Endkundenpreis:</b></td>"
                + "<td align=\"right\">" + decFormat.format(customerPrice) + " €</td>"
                + "<td align=\"right\">" + decFormat.format((customerPrice * (1 + GlobalConfig.DEFAULT_TAX.getTax()))) + " €</td>"
                + "</tr><tr>"
                + "<td><b>Händlerpreis:</b></td>"
                + "<td align=\"right\">" + decFormat.format(retailerPrice) + " €</td>"
                + "<td align=\"right\">" + decFormat.format((retailerPrice * (1 + GlobalConfig.DEFAULT_TAX.getTax()))) + " €</td>"
                + "</tr><tr>"
                + "<td><b>Verkaufskanal: </b></td>"
                + "<td align=\"left\">" + salesChannel + "</td>"
                + "</tr>"
                + "</table>";
        return re;
    }

    public static String toHtmlList(List<UniqueUnit> units) {
        if ( units != null && !units.isEmpty() ) {
            String re = "<h2>Geräte:</h2><ol type=\"disc\">";
            for (UniqueUnit uniqueUnit : units) {
                re += "<li>" + toSimpleHtml(uniqueUnit) + "</li>";
            }
            return re + "</ol>";
        }
        return "No Units";
    }

    public static String toHtmlUniqueUnitHistory(UniqueUnit unit) {
        String res = "<p>Geräthistorie:<ol type=\"disc\">";
        TreeSet<UniqueUnitHistory> history = new TreeSet<>((UniqueUnitHistory o1, UniqueUnitHistory o2) -> o1.getOccurence().compareTo(o2.getOccurence()));
        history.addAll(unit.getHistory());
        res = history.stream().map((uniqueUnitHistory)
                -> "<li>Datum: " + uniqueUnitHistory.getOccurence()
                + "<br />Anmerkung:" + uniqueUnitHistory.getComment() + "</li>")
                .reduce(res, String::concat);
        res += "</ol></p>";
        return res;
    }

    public static String toSingleLineComment(UniqueUnit unit) {
        List elems = new ArrayList();
        elems.addAll(unit.getComments());
        elems.add(unit.getComment());
        if ( unit.getWarranty().equals(Warranty.WARRANTY_TILL_DATE) )
            elems.add("Garantie bis: " + DateFormats.ISO.format(unit.getWarrentyValid()));
        return collectionToString(null, elems);
    }

    public static String toSingleLineAccessories(UniqueUnit unit) {
        String result = collectionToString(null, unit.getEquipments());
        return (result == null ? "" : result);
    }

    public static String toSingleLineEquipmentAndComment(UniqueUnit unit) {
        List<String> r = new ArrayList<>();
        r.add(collectionToString("Zubehör: ", unit.getEquipments()));
        List elems = new ArrayList();
        elems.addAll(unit.getComments());
        elems.add(unit.getComment());
        r.add(collectionToString("Bemerkungen: ", elems));
        return collectionToString(null, r);
    }

    public static String toSingleLineInternalComment(UniqueUnit unit) {
        List elems = new ArrayList();
        elems.addAll(unit.getInternalComments());
        elems.add(unit.getInternalComment());
        return collectionToString(null, elems);
    }

    public static String toDetailedDiscriptionLine(UniqueUnit unit) {
        String description = unit.getProduct().getDescription();
        description += ", Zubehör:" + UniqueUnitFormater.toSingleLineAccessories(unit);
        description += ", optischer Zustand: " + unit.getCondition().getNote();
        description += ", juristischer Zustand: gebraucht";
        description += ", " + UniqueUnitFormater.toSingleLineComment(unit);
        description += ", " + toWarranty(unit);
        return description;
    }

    private static String collectionToString(String head, Collection elems) {
        if ( elems == null || elems.isEmpty() ) return null;
        String s = (head == null ? "" : head);
        for (Iterator it = elems.iterator(); it.hasNext();) {
            Object elem = it.next();
            if ( elem == null ) continue;
            if ( elem instanceof INoteModel ) s += ((INoteModel)elem).getNote();
            else s += elem.toString();
            if ( it.hasNext() ) s += ", ";
        }
        return s;
    }

    public static String toSource(UniqueUnit unit) {
        String var = randomVar();
        String re = unit.getClass().getSimpleName() + " " + var + " = new " + unit.getClass().getName() + "();\n";
        for (UniqueUnit.Identifier id : unit.getIdentifiers().keySet()) {
            re += var + ".setIdentifier(UniqueUnit.Identifier." + id + ",\"" + unit.getIdentifier(id) + "\");\n";
        }
        for (PriceType id : unit.getPrices().keySet()) {
            re += var + ".setPrice(PriceType." + id + "," + unit.getPrice(id) + ",\"formToSource\");\n";
        }
        if ( !unit.getEquipments().isEmpty() ) {
            re += var + ".setEquipments(EnumSet.of(";
            for (Iterator<Equipment> it = unit.getEquipments().iterator(); it.hasNext();) {
                UniqueUnit.Equipment equipment = it.next();
                re += "UniqueUnit.Equipment." + equipment;
                if ( it.hasNext() ) re += ",";
            }
            re += "));\n";
        }
        if ( !unit.getComments().isEmpty() ) {
            re += var + ".setComments(EnumSet.of(";
            for (Iterator<UniqueUnit.StaticComment> it = unit.getComments().iterator(); it.hasNext();) {
                UniqueUnit.StaticComment comment = it.next();
                re += "UniqueUnit.StaticComment." + comment;
                if ( it.hasNext() ) re += ",";
            }
            re += "));\n";
        }
        if ( !unit.getInternalComments().isEmpty() ) {
            re += var + ".setInternalComments(EnumSet.of(";
            for (Iterator<UniqueUnit.StaticInternalComment> it = unit.getInternalComments().iterator(); it.hasNext();) {
                UniqueUnit.StaticInternalComment comment = it.next();
                re += "UniqueUnit.StaticInternalComment." + comment;
                if ( it.hasNext() ) re += ",";
            }
            re += "));\n";
        }
        if ( !unit.getFlags().isEmpty() ) {
            re += var + ".setFlags(EnumSet.of(";
            for (Iterator<UniqueUnit.Flag> it = unit.getFlags().iterator(); it.hasNext();) {
                UniqueUnit.Flag flag = it.next();
                re += "UniqueUnit.Flag." + flag;
                if ( it.hasNext() ) re += ",";
            }
            re += "));\n";
        }
        if ( unit.getComment() != null ) re += var + ".setComment(\"" + unit.getComment() + "\");\n";
        if ( unit.getInternalComment() != null ) re += var + ".setInternalComment(\"" + unit.getInternalComment() + "\");\n";
        re += var + ".setContractor(TradeName." + unit.getContractor() + ");\n";
        re += var + ".setCondition(UniqueUnit.Condition." + unit.getCondition() + ");\n";
        re += var + ".setMfgDate(new Date(" + unit.getMfgDate().getTime() + "));\n";
        if ( unit.getWarranty().equals(Warranty.WARRANTY_TILL_DATE) ) re += var + ".setWarrentyTill( new Date(" + unit.getWarrentyValid().getTime() + "));";

        return re;
    }

    private static String randomVar() {
        return "" + (char)(97 + (Math.random() * ((122 - 97) + 1)))
                + (char)(97 + (Math.random() * ((122 - 97) + 1)))
                + (char)(97 + (Math.random() * ((122 - 97) + 1)));
    }

    private static String toWarranty(UniqueUnit uu) {
        if ( uu.getWarranty() == Warranty.WARRANTY_TILL_DATE && uu.getWarrentyValid() != null )
            return "Restgarantie bis zum " + DF.format(uu.getWarrentyValid());
        return "Garantie: " + uu.getWarranty().getName();
    }
}
