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
package eu.ggnet.dwoss.report.eao;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.SalesChannel;

import java.util.*;
import java.util.Map.Entry;

import lombok.*;

/**
 * Container to handle a Revenue Result.
 * <p>
 * @author oliver.guenther
 */
@Value
public class Revenue {

    @Value
    public static class Key {

        private final SalesChannel channel;

        private final DocumentType type;

        /**
         * Creates or reuses an Instance of Key.
         * <p>
         * @param c the channel
         * @param t the documentType
         * @return a instance of Key and caches the instance.
         */
        public static Key valueOf(SalesChannel c, DocumentType t) {
            // TODO: Implent Cache :-)
            return new Key(c, t);
        }

    }

    private final Map<Key, Double> details = new HashMap<>();

    {
        for (SalesChannel channel : SalesChannel.values()) {
            for (DocumentType type : DocumentType.values()) {
                details.put(new Key(channel, type), 0.);
            }
        }
    }

    public void addTo(SalesChannel channel, DocumentType type, double amount) {
        Key k = new Key(channel, type);
        details.put(k, details.get(k) + amount);
    }

    public double getSum() {
        double sum = 0;
        for (Double value : details.values()) sum += value;
        return sum;
    }

    public double getSum(DocumentType type) {
        double sum = 0;
        for (Entry<Key, Double> entry : details.entrySet()) {
            if ( entry.getKey().getType() == type ) sum += entry.getValue();
        }
        return sum;
    }

}
