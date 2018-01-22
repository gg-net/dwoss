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
package eu.ggnet.dwoss.redtapext.ui.cao.document;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author oliver.guenther
 */
@AllArgsConstructor
@Getter
public enum Tax {

    DEFAULT_TAX("19% Mwst", null, 0.19),
    REVERSE_CHARGE("Reverse Charge", "Steuerschuldnerschaft des Leistungsempfängers gemäß § 13b Abs. 2 Nr. 10 UStG (Reverse-Charge-Verfahren)", 0);

    private final String buttonText;

    private final String documentText;

    private final double tax;

}
