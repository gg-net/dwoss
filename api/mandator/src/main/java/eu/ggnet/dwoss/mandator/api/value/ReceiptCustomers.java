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

import eu.ggnet.dwoss.core.common.values.ReceiptOperation;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;

/**
 * Contains SystemCustomers which are used for the receipt operations based on the Contractor.
 * <p>
 * @author oliver.guenther
 */
public class ReceiptCustomers implements Serializable {

    public static class Key implements Serializable {

        public final TradeName contractor;

        public final ReceiptOperation operation;

        public Key(TradeName contractor, ReceiptOperation operation) {
            this.contractor = contractor;
            this.operation = operation;
        }

        public static Key of(TradeName contractor, ReceiptOperation operation) {
            // TODO: if lazy, implement some cache.
            return new Key(contractor, operation);
        }

        // Getter used in the web
        public TradeName getContractor() {
            return contractor;
        }

        public ReceiptOperation getOperation() {
            return operation;
        }

        //<editor-fold defaultstate="collapsed" desc="equals and hashcode of contractor,operation">
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 43 * hash + Objects.hashCode(this.contractor);
            hash = 43 * hash + Objects.hashCode(this.operation);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final Key other = (Key)obj;
            if ( this.contractor != other.contractor ) return false;
            if ( this.operation != other.operation ) return false;
            return true;
        }
        //</editor-fold>

        @Override
        public String toString() {
            return "Key{" + "contractor=" + contractor + ", operation=" + operation + '}';
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

    private final Map<Key, Long> receiptCustomers;

    public ReceiptCustomers(Map<Key, Long> receiptCustomers) {
        this.receiptCustomers = receiptCustomers;
    }

    public Map<Key, Long> getReceiptCustomers() {
        return Collections.unmodifiableMap(receiptCustomers);
    }

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
                .map(e -> e.getKey().operation);
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
                .filter(k -> k.contractor == contractor)
                .map(k -> k.operation)
                .collect(Collectors.toSet());
    }
}
