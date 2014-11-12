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
package eu.ggnet.dwoss.rules;

/**
 *
 * @author pascal.perau
 */
public enum CustomerFlag {

    /**
     * System Customer - Customer is special and has only Documents of Type Block.
     */
    SYSTEM_CUSTOMER("Systemkunde"),
    /**
     * Customer has confirmed the terms and conditions for cash on delivery.
     */
    CONFIRMED_CASH_ON_DELIVERY("Nachnahme bestätigt"), /**
     * Customer wants to confirm the pickup or shipping of a Dossier.
     */
    CONFIRMS_DOSSIER("Kunde bestätigt Aufträge");

    private String name;

    private CustomerFlag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
