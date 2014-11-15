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
package eu.ggnet.dwoss.uniqueunit.format;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.rules.Warranty;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Equipment;

import eu.ggnet.dwoss.util.DateFormats;
import eu.ggnet.dwoss.util.INoteModel;

import eu.ggnet.dwoss.uniqueunit.entity.*;

import static eu.ggnet.dwoss.rules.SalesChannel.UNKNOWN;
import static eu.ggnet.dwoss.uniqueunit.entity.PriceType.CUSTOMER;
import static eu.ggnet.dwoss.uniqueunit.entity.PriceType.RETAILER;

/**
 * Formatter for UniqueUnits.
 *
 * @author pascal.perau
 */
public abstract class UniqueUnitFormater {

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
     * Returns a String containing prices and the {@link PriceHistory} of a {@link UniqueUnit} formated in html.
     *
     * @param uu the unit to format
     * @return the formated String.
     */
    public static String toHtmlPriceInformation(UniqueUnit uu) {
        String res = "";
        if ( uu != null ) {
            res += "<p>Preise:<br />";
            res += "Endkundenpreis (Netto): " + (NumberFormat.getCurrencyInstance().format(uu.getPrice(PriceType.CUSTOMER))) + "<br />";
            res += "Händlerpreis (Netto): " + (NumberFormat.getCurrencyInstance().format(uu.getPrice(PriceType.RETAILER))) + "<br />";
            if ( !uu.getPriceHistory().isEmpty() ) {
                res += "<p>Preishistorie:<ul>";
                for (PriceHistory priceHistory : uu.getPriceHistory()) {
                    res += "<li>Datum: " + priceHistory.getDate() + " | Preis: " + priceHistory.getType()
                            + " - " + NumberFormat.getCurrencyInstance().format(priceHistory.getPrice()) + "<br />" + "Bemerkung: " + priceHistory.getComment() + "</li>";
                }
                res += "</ul></p>";
            }
        }
        return res;
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
        String re = "<h1>SopoNr: " + unit.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID) + "</h1>";
        re += "<u>Seriennummer:</u> " + unit.getIdentifier(UniqueUnit.Identifier.SERIAL) + "<br />";
        re += "<u>Lieferant:</u> " + unit.getContractor().getName() + "<br />";
        re += "<u>UniqueUnit.id:</u> " + unit.getId();
        if ( p == null ) {
            re += "<br /><h2>Keine ProductBeschreibung (UniqueUnit.product == null)</h2>";
        } else {
            re += " - <u>Product.id:</u> " + p.getId() + "<br />";
            re += "<h2>" + p.getGroup().getNote() + " - " + p.getTradeName().getName() + " " + p.getName() + " (" + p.getPartNo() + ")" + "</h2>";
        }
        if ( p != null ) re += (p.getEol() == null ? "" : "<u>Artikel End of Life:</u> " + DateFormats.ISO.format(p.getEol()) + "</br>");
        if ( p != null ) re += (p.getGtin() == null ? "" : "<u>GTIN (EAN):</u> " + p.getGtin() + "</br>");
        if ( p != null ) re += "<p>" + p.getDescription() + "</p>";
        re += "<u>Zubehör:</u> " + UniqueUnitFormater.toSingleLineAccessories(unit) + "<br />";
        re += "<u>Bemerkung:</u> " + UniqueUnitFormater.toSingleLineComment(unit) + "<br />";
        re += "<u>Interne Bemerkung:</u> " + UniqueUnitFormater.toSingleLineInternalComment(unit) + "<br />";
        re += "<u>Shipment:</u> " + unit.getShipmentLabel() + " (" + unit.getShipmentId() + ")<br />";

        re += "<p><table border=\"1\">"
                + "<tr>"
                + "<th>juristischer Zustand</th><th>optischer Zustand</th><th>Garantie</th><th>Shipment</th><th>Aufgenommen am</th><th>Mfg Date</th>"
                + "</tr><tr>"
                + "<td>gebraucht</td><td>" + unit.getCondition().getNote() + "</td><td>" + toWarranty(unit) + "</td>"
                + "<td>" + unit.getShipmentLabel() + " (id=" + unit.getShipmentId() + ")</td><td>" + unit.getInputDate() + "</td>"
                + "<td>" + unit.getMfgDate() + "</td>"
                + "</tr></table>";

        re += "<h3>Preisinformationen</h3>";
        re += toHtmlPrices(unit) + "</p>";
        re += "<hr />";
        if ( unit.getHistory() != null && !unit.getHistory().isEmpty() ) {
            re += "<h2>Unit History:</h2><ul>";
            for (UniqueUnitHistory history : new TreeSet<>(unit.getHistory())) {
                re += "<li>" + DateFormats.ISO.format(history.getOccurence()) + " - " + history.getComment() + "</li>";
            }
            re += "</ul>";
        }
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
        double salePrice = unit.getPrice(PriceType.SALE);

        re += "<table border=\"0\">"
                + "<tr>"
                + "<th></th>"
                + "<th align=\"right\">netto</th>"
                + "<th align=\"right\">brutto</th>"
                + "</tr><tr>"
                + "<td><b>Endkundenpreis:</b></td>"
                + "<td align=\"right\">" + decFormat.format(customerPrice) + " €</td>"
                + "<td align=\"right\">" + decFormat.format((customerPrice * (1 + GlobalConfig.TAX))) + " €</td>"
                + "</tr><tr>"
                + "<td><b>Händlerpreis:</b></td>"
                + "<td align=\"right\">" + decFormat.format(retailerPrice) + " €</td>"
                + "<td align=\"right\">" + decFormat.format((retailerPrice * (1 + GlobalConfig.TAX))) + " €</td>"
                + "</tr><tr>"
                + "<td><b>Verkaufskanal: </b></td>"
                + "<td align=\"left\">" + salesChannel + "</td>"
                + "</tr><tr>"
                + "<td><b>Verkaufspreis: </b></td>"
                + "<td align=\"right\">" + decFormat.format(salePrice) + " €</td>"
                + "<td align=\"right\">" + decFormat.format((salePrice * (1 + GlobalConfig.TAX))) + " €</td>"
                + "</tr></table>";
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
                -> "<li>Datum: " + uniqueUnitHistory.getOccurence() + " | Typ: " + uniqueUnitHistory.getType().name()
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
            return "Restgarantie bis zum " + DateFormats.ISO.format(uu.getWarrentyValid());
        return "Garantie: " + uu.getWarranty().getName();
    }
}
