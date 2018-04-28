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

import eu.ggnet.dwoss.common.api.INoteModel;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Flags set on the Customer, but used on multiple ocations.
 *
 * @author pascal.perau
 */
@Getter
@AllArgsConstructor
public enum CustomerFlag implements INoteModel {
    /**
     * Has a special workflow assigned.
     */
    SYSTEM_CUSTOMER("Systemdatensatz"),
    CONFIRMED_CASH_ON_DELIVERY("Nachnahme bestätigt"),
    CONFIRMS_DOSSIER("Kunde bestätigt Aufträge"),
    CS_UPDATE_CANDIDATE("Kandidat für CS Update"),
    CS_UPDATE_CANDIDATE_NEXT("Kandidat für nächstes CS Update"),
    ITC_CUSTOMER("Systemhauskunde"),
    PRIO_A_CUSTOMER("Prio A Kunde");

    private final String name;

    @Override
    public String getNote() {
        return name;
    }

}
