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

import lombok.*;

/**
 * Represents some Information of a Unit with possible reference.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public boolean isAvailable() {
        return Boolean.TRUE.equals(available);
    }

}
