/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ee.entity.projection;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A Pico Customer concept.
 *
 * @author oliver.guenther
 */
public class PicoCustomer implements Serializable {

    /**
     * id of the customer.
     */
    public final long id;

    /**
     * a short description.
     */
    public final String shortDescription;

    public PicoCustomer(long id, String shortDescription) {
        this.id = id;
        this.shortDescription = Objects.requireNonNull(shortDescription, "shortDescription must not be null");
        if ( shortDescription.trim().isEmpty() ) throw new IllegalArgumentException("shortDescription must not be blank");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
