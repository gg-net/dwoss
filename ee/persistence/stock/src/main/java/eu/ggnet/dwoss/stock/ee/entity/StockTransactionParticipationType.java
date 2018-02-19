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
package eu.ggnet.dwoss.stock.ee.entity;

/**
 * Selects the type of the participant in a transaction. See {@link StockTransactionType} which participant is needed
 * and optionally allowed for each status.
 */
public enum StockTransactionParticipationType {

    /**
     * If a {@link StockTransactionStatusType} allowes only one person (e.g. {@link StockTransactionStatusType#PREPARED}
     * than this participant can allways be used.
     */
    ARRANGER,
    /**
     * The person that commisions the units for delivery
     */
    PICKER,
    /**
     * The one that is actually delivering the goods
     */
    DELIVERER,
    /**
     * The other person that takes the units in the destination stock
     */
    RECEIVER
}
