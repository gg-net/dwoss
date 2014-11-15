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
import java.util.stream.Collectors;

import eu.ggnet.dwoss.rules.ReceiptOperation;
import eu.ggnet.dwoss.rules.TradeName;

import lombok.*;

/**
 * Contains SystemCustomers which are used for the receipt operations based on the Contractor.
 * <p/>
 * @author oliver.guenther
 */
@RequiredArgsConstructor
public class ReceiptCustomers implements Serializable {

    @Value
    public static class Key implements Serializable {

        private final TradeName contractor;

        private final ReceiptOperation operation;

        public static Key of(TradeName contractor, ReceiptOperation operation) {
            // TODO: if lazy, implement some cache.
            return new Key(contractor, operation);
        }
    }

    public static class Builder {

        private Builder() {
        }

        private final Map<Key, Long> result = new HashMap<>();

        public Builder put(TradeName contractor, ReceiptOperation operation, long customerId) {
            if ( !operation.isBackedByCustomer() ) throw new IllegalArgumentException(operation + " is not backed by a customer, not allowed");
            result.put(Key.of(contractor, operation), customerId);
            return this;
        }

        public ReceiptCustomers build() {
            return new ReceiptCustomers(result);
        }

    }

    public static ReceiptCustomers.Builder builder() {
        return new Builder();
    }

    @Getter
    private final Map<Key, Long> receiptCustomers;

    /**
     * Returns the customer id for the contractor and the operation
     * <p>
     * @param contractor the contractor
     * @param operation  the operation
     * @return the customerId
     */
    public long getCustomerId(TradeName contractor, ReceiptOperation operation) {
        return receiptCustomers.get(Key.of(contractor, operation));
    }

    /**
     * Returns the receiptOperation which is represented by the customer.
     * <p>
     * <p>
     * @param customerId the customer
     * @return the operation, or null if none.
     */
    public Optional<ReceiptOperation> getOperation(long customerId) {
        return receiptCustomers
                .entrySet()
                .stream()
                .filter(e -> e.getValue() == customerId)
                .findFirst()
                .map(e -> e.getKey().getOperation());
    }

    /**
     * Returns the list of operations which are enabled for the contractor.
     * <p>
     * @param contractor the contractor
     * @return the operations.
     */
    public Set<ReceiptOperation> enabledOperations(TradeName contractor) {
        return receiptCustomers
                .keySet()
                .stream()
                .filter(k -> k.getContractor() == contractor)
                .map(k -> k.getOperation())
                .collect(Collectors.toSet());
    }
}
