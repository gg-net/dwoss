/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ee.eao;

import java.util.*;

import eu.ggnet.dwoss.common.api.values.TradeName;

/**
 * Aggregate Result of a Count be Brand an Contractor.
 * <p>
 * @author oliver.guenther
 */
public class BrandContractorCount {

    public static class Key {

        private final TradeName brand;

        private final TradeName contractor;

        public Key(TradeName brand, TradeName contractor) {
            this.brand = brand;
            this.contractor = contractor;
        }

        public TradeName getBrand() {
            return brand;
        }

        public TradeName getContractor() {
            return contractor;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + Objects.hashCode(this.brand);
            hash = 89 * hash + Objects.hashCode(this.contractor);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final Key other = (Key)obj;
            if ( this.brand != other.brand ) return false;
            if ( this.contractor != other.contractor ) return false;
            return true;
        }

        @Override
        public String toString() {
            return "Key{" + "brand=" + brand + ", contractor=" + contractor + '}';
        }

        /**
         * Creates or reuses an Instance of Key.
         * <p>
         * @param brand
         * @param contractor the contractor
         * @return a instance of Key and caches the instance.
         */
        public static Key valueOf(TradeName brand, TradeName contractor) {
            // TODO: Implent Cache :-)
            return new Key(brand, contractor);
        }

    }

    private final Map<Key, Double> details = new HashMap<>();

    public void addTo(TradeName brand, TradeName contractor, double amount) {
        Key k = Key.valueOf(brand, contractor);
        if ( !details.containsKey(k) ) details.put(k, amount);
        else details.put(k, details.get(k) + amount);
    }

    public double count() {
        return details.values().stream().mapToDouble(v -> v).sum();
    }

    public double countByContractor(TradeName contractor) {
        return details.entrySet().stream()
                .filter(e -> e.getKey().getContractor() == contractor)
                .mapToDouble(e -> e.getValue())
                .sum();
    }

    public double countByManufacturer(TradeName manufacturer) {
        return details.entrySet().stream()
                .filter(e -> manufacturer.getBrands().contains(e.getKey().getBrand()))
                .mapToDouble(e -> e.getValue())
                .sum();
    }

    public double countByContractorManufacturer(TradeName contractor, TradeName manufacturer) {
        return details.entrySet().stream()
                .filter(e -> e.getKey().getContractor() == contractor && manufacturer.getBrands().contains(e.getKey().getBrand()))
                .mapToDouble(e -> e.getValue())
                .sum();
    }

}
