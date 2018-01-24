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
package eu.ggnet.dwoss.rules;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents Taxtype for Documents.
 * Used in persistence layer, so order must noch change.
 *
 * @author oliver.guenther
 */
@AllArgsConstructor
@Getter
public enum TaxType {

    GENERAL_SALES_TAX_DE_SINCE_2007(0.19,
            "19% Mwst",
            "Standard Umsatzsteuer (Mehrwertsteuer) in Deutschland seit 2007",
            null),
    UNTAXED(0,
            "Keine Ust (0%)",
            "0% Umsatzsteuer, z.b. bei Verkäufen ins Ausland oder Versicherungsverkäufen an Logistiker",
            null),
    REVERSE_CHARGE(0,
            "Reverse Charge",
            "0% Umsatzsteuer bei Verkauf von Endgeräten mit Funkanbindungen und einem Rechnungswert von mid. 5000,-€",
            "Steuerschuldnerschaft des Leistungsempfängers gemäß § 13b Abs. 2 Nr. 10 UStG (Reverse-Charge-Verfahren)");

    /**
     * Tax value, must never Change.
     */
    private final double tax;

    /**
     * Short name of the tax type, typical usage on buttons.
     */
    private final String name;

    /**
     * A more detailed description about the tax type, may be used as tooltip.
     */
    private final String description; // Usefull as Tooltip

    /**
     * A optional Text, that must be displayed in invoices.
     */
    private final String documentText;

}
