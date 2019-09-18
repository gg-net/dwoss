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

import javax.xml.bind.annotation.*;

/**
 * Represents some Information of a Unit with possible reference.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
/*
TODO:
In the lombok removal phase, i didn't have time to verify, how best to convert this class.
So as long as there are no cool tests, i use a simple pojo.
Consider conversion to Freebuilder.
*/
public class UnitShard implements Serializable {

    private String refurbishedId;

    private int uniqueUnitId;

    private String htmlDescription;

    /**
     * Status of available.
     * True == available.
     * False == not available.
     * Null == not existent.
     */
    private Boolean available;

    private Integer stockId;

    public UnitShard() {
    }

    public UnitShard(String refurbishedId, int uniqueUnitId, String htmlDescription, Boolean available, Integer stockId) {
        this.refurbishedId = refurbishedId;
        this.uniqueUnitId = uniqueUnitId;
        this.htmlDescription = htmlDescription;
        this.available = available;
        this.stockId = stockId;
    }

    public String getRefurbishedId() {
        return refurbishedId;
    }

    public void setRefurbishedId(String refurbishedId) {
        this.refurbishedId = refurbishedId;
    }

    public int getUniqueUnitId() {
        return uniqueUnitId;
    }

    public void setUniqueUnitId(int uniqueUnitId) {
        this.uniqueUnitId = uniqueUnitId;
    }

    public String getHtmlDescription() {
        return htmlDescription;
    }

    public void setHtmlDescription(String htmlDescription) {
        this.htmlDescription = htmlDescription;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Integer getStockId() {
        return stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }
    
    public boolean isAvailable() {
        return Boolean.TRUE.equals(available);
    }

    @Override
    public String toString() {
        return "UnitShard{" + "refurbishedId=" + refurbishedId + ", uniqueUnitId=" + uniqueUnitId + ", htmlDescription=" + htmlDescription + ", available=" + available + ", stockId=" + stockId + '}';
    }

}
