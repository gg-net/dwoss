/* 
 * Copyright (C) 2014 pascal.perau
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

import lombok.Value;

/**
 *
 * @author Bastian Venz <bastian.venz at gg-net.de>
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

}
