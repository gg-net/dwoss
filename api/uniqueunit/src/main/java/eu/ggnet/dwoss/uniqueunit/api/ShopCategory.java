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
package eu.ggnet.dwoss.uniqueunit.api;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface ShopCategory extends Serializable {

    long id();

    String name();

    int shopId();

    class Builder extends ShopCategory_Builder {

        public Builder() {
            // Default 0 to database id. 0 Means not persisted yet.
            id(0);
        }

        @Override
        public Builder name(String name) {
            if (name == null) throw new NullPointerException("name must not be null");
            if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
            return super.name(name); 
        }       
        
    };

}
