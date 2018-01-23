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
package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.TaxType;

import lombok.Value;

/**
 * PostLedger (Fibu Buchungskonto) engine.
 *
 * @author Bastian Venz
 * @author Oliver Guenther
 */
@Value
public class PostLedger implements Serializable {

    @Value
    public static class LedgerValue implements Serializable {

        public LedgerValue(Integer primaryCustomerId) {
            this.primaryLedgerId = primaryCustomerId;
            this.possibleLedgersIds = null;
        }

        public LedgerValue(Integer primaryCustomerId, List<Integer> possiblePostLedgersIds) {
            this.primaryLedgerId = primaryCustomerId;
            this.possibleLedgersIds = possiblePostLedgersIds;
        }

        private Integer primaryLedgerId;

        private List<Integer> possibleLedgersIds;
    }

    private final Map<PositionType, LedgerValue> ledgerCustomers;

    public Optional<Integer> get(PositionType type) {
        return Optional.ofNullable(ledgerCustomers.get(type)).map(lv -> lv.getPrimaryLedgerId());
    }

    public Optional<List<Integer>> getPossible(PositionType type) {
        return Optional.ofNullable(ledgerCustomers.get(type)).map(lv -> lv.getPossibleLedgersIds());
    }

    /*
    Scenarien:
    1. Default -> bassiert nur auf position type
    2. Reverse Charge -> passiert durch externen eingriff. (Oder aufgrund der Geräte vieleicht irgendwan) -> taxType
    3. östereich, dhl -> kid, country of kid, extra eingriff -> taxType, customerId)

     */
    public Optional<Ledger> get(PositionType type, TaxType taxType, long customerId) {
        return null;
    }


}
