/*
 * Copyright (C) 2015 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.api;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents some Information of a Unit with possible reference.
 */
public class UnitShard implements Serializable {

    private final String refurbishedId;

    private final int uniqueUnitId;

    private final String htmlDescription;
    
    /**
     * Status of available.
     * True == available.
     * False == not available.
     * Null == not existent.
     */
    private final Optional<Boolean> available;

    private final Optional<Integer> stockId;

    public UnitShard(String refurbishedId, int uniqueUnitId, String htmlDescription, Boolean available, Integer stockId) {
        this.refurbishedId = Objects.requireNonNull(refurbishedId);
        this.uniqueUnitId = Objects.requireNonNull(uniqueUnitId);
        this.htmlDescription = Objects.requireNonNull(htmlDescription);
        this.available = Optional.ofNullable(available);
        this.stockId = Optional.ofNullable(stockId);
    }

    public String getRefurbishedId() {
        return refurbishedId;
    }

    public int getUniqueUnitId() {
        return uniqueUnitId;
    }

    public String getHtmlDescription() {
        return htmlDescription;
    }

    public Optional<Boolean> getAvailable() {
        return available;
    }

    public Optional<Integer> getStockId() {
        return stockId;
    }

    public boolean isAvailable() {
        return available.orElse(Boolean.FALSE);
    }

    @Override
    public String toString() {
        return "UnitShard{" + "refurbishedId=" + refurbishedId + ", uniqueUnitId=" + uniqueUnitId + ", htmlDescription=" + htmlDescription + ", available=" + available + ", stockId=" + stockId + '}';
    }

}
