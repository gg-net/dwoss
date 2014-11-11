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
package eu.ggnet.dwoss.stock;

import eu.ggnet.dwoss.stock.entity.StockTransaction;

/**
 *
 */
public class StockTransactionUtil {

    /**
     * Returns null if both Transactions in an equal state, meaning the same workflow can be applied.
     * <p>
     * The following details must be equal:
     * </p>
     * <ul>
     * <li>StockTransactionType</li>
     * <li>StockTransactionStatusType</li>
     * <li>Source</li>
     * <li>Destination</li>
     * </ul>
     *
     * @param t1 the first transaction
     * @param t2 the second transaction
     * @return null if both Transactions in an equal state else a message which discribes, whats wrong.
     */
    public static String equalStateMessage(StockTransaction t1, StockTransaction t2) {
        if ( t1 == t2 ) return null;
        if ( t1 == null ) return "Transaction 1 = null";
        if ( t2 == null ) return "Transaction 2 = null";
        if ( t1.getStatus() == null ) return "Transaction 1 invalid. t1.status=null";
        if ( t2.getStatus() == null ) return "Transaction 2 invalid. t2.status=null";
        if ( t1.getType() != t2.getType() ) return "TransactionType not equal, t1.type=" + t1.getType() + ",t2.type=" + t2.getType();
        if ( t1.getStatus().getType() != t2.getStatus().getType() )
            return "TransactionStatusType not equal, t1.statusType=" + t1.getStatus().getType() + ",t2.statusType=" + t2.getStatus().getType();
        if ( !nullEqual(t1.getSource(), t2.getSource()) ) return "Transaction Source not equal, t1.source=" + t1.getSource() + ",t2.source=" + t2.getSource();
        if ( !nullEqual(t1.getDestination(), t2.getDestination()) )
            return "Transaction Destination not equal, t1.destination=" + t1.getDestination() + ",t2.destination=" + t2.getDestination();
        return null;
    }

     /**
     * Returns true if both are null or both are equal;
     *
     * @param s1 candidate 1
     * @param s2 candidate 2
     * @return true if both are null or both are equal;
     */
    public static boolean nullEqual(Object s1, Object s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null) return false;
        if (s2 == null) return false;
        return s1.equals(s2);
    }
}
