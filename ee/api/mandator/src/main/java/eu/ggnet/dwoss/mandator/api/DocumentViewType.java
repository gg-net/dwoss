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
package eu.ggnet.dwoss.mandator.api;

/**
 * Document View Types.
 * These types can be used, to supply different templates or texts in the resulting documents.
 * Hint: Only on this component to have type safty in the Mandator.
 *
 * @author oliver.guenther
 */
public enum DocumentViewType {

    /**
     * The default viewtype. (Alternative to null)
     */
    DEFAULT("Default", "Default", "Document_Template.jrxml"),
    /**
     * Represents a reservation.
     */
    RESERVATION("Reservierung", "Angebot/Reservierung für 48 Stunden", "Document_Template.jrxml"),
    /**
     * Represents the shipping document.
     */
    SHIPPING("Lieferschein", "Lieferschein", "Shipping_Template.jrxml");

    public final String description;

    public final String documentTitle;
    
    public final String fileName;

    private DocumentViewType(String name, String documentTitle, String fileName) {
        this.description = name;
        this.documentTitle = documentTitle;
        this.fileName = fileName;
    }

}
