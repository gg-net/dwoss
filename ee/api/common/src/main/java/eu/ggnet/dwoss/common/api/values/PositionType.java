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
package eu.ggnet.dwoss.common.api.values;

/**
 * The different Types of position.
 * <p>
 * @author oliver.guenther
 */
public enum PositionType {

    /**
     * A Position representing a full Unit.
     */
    UNIT("Gerät"),
    /**
     * Unit Annex.
     * Is used in a CreditMemo for partial cash backs.
     */
    UNIT_ANNEX("Zusatzinformation zum Gerät"),
    /**
     * A Service Position.
     */
    SERVICE("Dienstleistungen"),
    /**
     * Multiple Units, one PartNo, same Price.
     */
    PRODUCT_BATCH("Mehrere Artikel mit Preis"),
    /**
     * A Comment.
     */
    COMMENT("Komentar"),
    /**
     * The Shipping Costs.
     */
    SHIPPING_COST("Versandkosten");

    /**
     * A short (german) description.
     */
    public final String description;

    private PositionType(String name) {
        this.description = name;
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

}
