/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.core.common.values;

/**
 * Represents Taxtype for Documents.
 * Used in persistence layer, so order must noch change.
 *
 * @author oliver.guenther
 */
public enum TaxType {

    GENERAL_SALES_TAX_DE_SINCE_2007(0.19,
            "19% Mwst",
            "Standard Umsatzsteuer (Mehrwertsteuer) in Deutschland seit 2007",
            null,
            "01"),
    UNTAXED(0,
            "Keine Ust (0%)",
            "0% Umsatzsteuer, z.b. bei Verkäufen ins Ausland oder Versicherungsverkäufen an Logistiker",
            null,
            "00"),
    REVERSE_CHARGE(0,
            "Reverse Charge",
            "0% Umsatzsteuer bei Verkauf von Endgeräten mit Funkanbindungen und einem Rechnungswert von mid. 5000,-€",
            "Steuerschuldnerschaft des Leistungsempfängers gemäß § 13b Abs. 2 Nr. 10 UStG (Reverse-Charge-Verfahren)",
            "36");

    /**
     * Tax value, must never Change.
     */
    public final double tax;

    /**
     * Short name of the tax type, typical usage on buttons.
     */
    public final String description;

    /**
     * A more detailed detailedDescription about the tax type, may be used as tooltip.
     */
    public final String detailedDescription; // Usefull as Tooltip

    /**
     * A optional Text, that must be displayed in invoices.
     */
    public final String documentText;

    /**
     * Code used in the exporter to finacial software (sage in our case).
     */
    public final String taxCode;

    private TaxType(double tax, String name, String description, String documentText, String taxCode) {
        this.tax = tax;
        this.description = name;
        this.detailedDescription = description;
        this.documentText = documentText;
        this.taxCode = taxCode;
    }

    /**
     * Returs the tax
     *
     * @return the tax
     * @deprecated use field tax
     */
    @Deprecated
    public double getTax() {
        return tax;
    }

    /**
     * A short (german) description.
     *
     * @return a short (german) description.
     * @deprecated use field description.
     */
    @Deprecated
    public String getName() {
        return description;
    }

    /**
     * Returns a detailed description
     *
     * @return a detailed desctiption
     * @deprecated use field detailedDescription
     */
    @Deprecated
    public String getDetailedDescription() {
        return detailedDescription;
    }

    /**
     * Returns a text in the document.
     *
     * @return text for the document.
     * @deprecated use field documentText
     */
    @Deprecated
    public String getDocumentText() {
        return documentText;
    }

    /**
     * Returns a tax code.
     *
     * @return a tax code
     * @deprecated use field taxCode
     */
    @Deprecated
    public String getTaxCode() {
        return taxCode;
    }

}
