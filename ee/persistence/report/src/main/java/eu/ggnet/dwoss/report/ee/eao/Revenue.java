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
package eu.ggnet.dwoss.report.ee.eao;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.common.values.SalesChannel;

import java.util.Map.Entry;
import java.util.*;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Container to handle a Revenue Result.
 * <p>
 * @author oliver.guenther
 */
public class Revenue {

    public static class RevenueMargin {

        public double revenue;

        public double reportedRevenue;

        public double reportedPurchacePrice;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

    }

    public static class Key {

        public final SalesChannel channel;

        public final DocumentType type;

        public final TradeName contractor;

        private Key(SalesChannel channel, DocumentType type, TradeName contractor) {
            this.channel = Objects.requireNonNull(channel,"channel must not be null");
            this.type = Objects.requireNonNull(type,"type must not be null");
            this.contractor = Objects.requireNonNull(contractor,"contractor must not be null");
        }

        //<editor-fold defaultstate="collapsed" desc="equals and hashCode of all">
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + Objects.hashCode(this.channel);
            hash = 79 * hash + Objects.hashCode(this.type);
            hash = 79 * hash + Objects.hashCode(this.contractor);
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final Key other = (Key)obj;
            if ( this.channel != other.channel ) return false;
            if ( this.type != other.type ) return false;
            if ( this.contractor != other.contractor ) return false;
            return true;
        }
        //</editor-fold>
        
        /**
         * Creates or reuses an Instance of Key.
         * <p>
         * @param c          the channel
         * @param t          the documentType
         * @param contractor the contractor
         * @return a instance of Key and caches the instance.
         */
        //INFO: Beispiel, wo Olli overengeniert hat.
        public static Key valueOf(SalesChannel c, DocumentType t, TradeName contractor) {
            return new Key(c, t, contractor);
        }     
        
    }

    private final Map<Key, RevenueMargin> details = new HashMap<>();

    public Revenue() {
        for (SalesChannel channel : SalesChannel.values()) {
            for (DocumentType type : DocumentType.values()) {
                for (TradeName contractor : TradeName.values()) {
                    details.put(Key.valueOf(channel, type, contractor), new RevenueMargin());
                }
            }
        }
    }

    public Map<Key, RevenueMargin> getDetails() {
        return details;
    }
    
    public void addTo(SalesChannel channel, DocumentType type, TradeName contractor, double revenue, double reportedPrice, double purchacePrice) {
        Key k = Key.valueOf(channel, type, contractor);
        RevenueMargin rm = details.get(k);
        rm.revenue += revenue;
        rm.reportedRevenue += reportedPrice;
        rm.reportedPurchacePrice += purchacePrice;
    }

    public double sum() {
        double sum = 0;
        for (RevenueMargin value : details.values()) sum += value.revenue;
        return sum;
    }

    public double sumBy(DocumentType type) {
        double sum = 0;
        for (Entry<Key, RevenueMargin> entry : details.entrySet()) {
            if ( entry.getKey().type == type ) sum += entry.getValue().revenue;
        }
        return sum;
    }

    public double sumBy(DocumentType type, TradeName contractor) {
        double sum = 0;
        for (Entry<Key, RevenueMargin> entry : details.entrySet()) {
            if ( entry.getKey().type == type && entry.getKey().contractor == contractor ) sum += entry.getValue().revenue;
        }
        return sum;
    }

    public double sumBy(SalesChannel channel, DocumentType type) {
        return details.entrySet().stream()
                .filter(e -> e.getKey().channel == channel && e.getKey().type == type)
                .mapToDouble(e -> e.getValue().revenue)
                .sum();
    }

    public double sumBy(TradeName contractor) {
        return details.entrySet().stream()
                .filter(e -> e.getKey().contractor == contractor)
                .mapToDouble(e -> e.getValue().revenue)
                .sum();
    }

    public double sumReportedRevenue() {
        return details.entrySet().stream().mapToDouble(e -> e.getValue().reportedRevenue).sum();
    }

    public double sumReportedRevenueBy(TradeName contractor) {
        return details.entrySet().stream()
                .filter(e -> e.getKey().contractor == contractor)
                .mapToDouble(e -> e.getValue().reportedRevenue)
                .sum();
    }

    public double sumReportedPurchasePrice() {
        return details.entrySet().stream().mapToDouble(e -> e.getValue().reportedPurchacePrice).sum();
    }

    public double sumReportedPurchasePriceBy(TradeName contractor) {
        return details.entrySet().stream()
                .filter(e -> e.getKey().contractor == contractor)
                .mapToDouble(e -> e.getValue().reportedPurchacePrice)
                .sum();
    }

}
