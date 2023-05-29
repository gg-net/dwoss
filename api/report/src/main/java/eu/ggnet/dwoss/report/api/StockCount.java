/*
 * Copyright (C) 2023 GG-Net GmbH
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
package eu.ggnet.dwoss.report.api;

import java.io.Serializable;
import java.time.LocalDate;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Count of Stock and Shipments.
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface StockCount extends Serializable {

    /**
     * Returs date of creation.
     *
     * @return date of creation.
     */
    LocalDate created();

    int shipmentsAnnounced();

    int shipmentsAnnouncedUnits();

    int shipmentsDelivered();

    int shipmentsDeliveredUnits();

    int shipmentsOpened();

    int shipmentsOpenedUnits();

    int shipmentsOpenedRemainderUnits();

    int stockUnitsAvailable();

    int stockUnitsAvailablePriceZero();

    int stockUnitsAvailablePriceBelowOneHundred();

    int stockUnitsAvailablePriceBelowThreeHundred();

    int stockUnitsAvailablePriceAboveThreeHundred();

    int stockUnitsInTransfer();

    int stockUnitsInTransferPriceZero();

    int stockUnitsInTransferPriceBelowOneHundred();

    int stockUnitsInTransferPriceBelowThreeHundred();

    int stockUnitsInTransferPriceAboveThreeHundred();

    class Builder extends StockCount_Builder {

        @SuppressWarnings("OverridableMethodCallInConstructor")
        public Builder() {
            shipmentsAnnounced(0);
            shipmentsAnnouncedUnits(0);
            shipmentsDelivered(0);
            shipmentsDeliveredUnits(0);
            shipmentsOpened(0);
            shipmentsOpenedUnits(0);
            shipmentsOpenedRemainderUnits(0);
            stockUnitsAvailable(0);
            stockUnitsAvailablePriceZero(0);
            stockUnitsAvailablePriceBelowOneHundred(0);
            stockUnitsAvailablePriceBelowThreeHundred(0);
            stockUnitsAvailablePriceAboveThreeHundred(0);
            stockUnitsInTransfer(0);
            stockUnitsInTransferPriceZero(0);
            stockUnitsInTransferPriceBelowOneHundred(0);
            stockUnitsInTransferPriceBelowThreeHundred(0);
            stockUnitsInTransferPriceAboveThreeHundred(0);
        }

    }
}
