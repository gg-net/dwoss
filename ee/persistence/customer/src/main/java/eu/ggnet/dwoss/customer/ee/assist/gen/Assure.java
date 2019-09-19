/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ee.assist.gen;

import java.util.List;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Flags for the generator instead of a million extra methods.
 */
@FreeBuilder
public interface Assure {

    class Builder extends Assure_Builder {
      
        public Builder() {
            simple(false);
        }
        
    };
    
    /**
     * Shortcut for new Builder().build();
     * 
     * @return defaults. 
     */
    static Assure defaults() {
        return new Builder().build();
    }
    
    /**
     * Indicates, that only simple customers must be generated, defaults to false.
     * 
     * @return simple 
     */
    boolean simple();

    /**
     * For all supplied strings, metadata will be generated. Null or empty indicates no metadata generation.
     * 
     * @return mandator metadata matchcodes 
     */
    List<String> mandatorMetadataMatchCodes();
    
}
