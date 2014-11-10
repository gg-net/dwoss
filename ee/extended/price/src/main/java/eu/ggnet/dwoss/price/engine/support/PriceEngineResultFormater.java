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
        sb.append("<tr><td>EVK (inc.Tax) :</td><td>").append(per.getCustomerPrice() * (1 + GlobalConfig.TAX)).append("</td></tr>");
        sb.append("</table>");
        if ( per.isError() || per.isWarning() ) {
            sb.append("<p>ErrorLog : ").append(per.getRulesLog()).append("</p>");
        }
        return sb.toString();
    }
}
