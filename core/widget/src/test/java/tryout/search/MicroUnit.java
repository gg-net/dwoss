/*
 * Copyright (C) 2014 GG-Net GmbH
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
package tryout.search;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import eu.ggnet.saft.api.IdSupplier;

/**
 *
 * @author oliver.guenther
 */
public class MicroUnit implements SearchResult,IdSupplier {
    
    public final int uniqueUnitId;
    
    public final String shortDescription;

    public MicroUnit(int uniqueUnitId, String shortDescription) {
        this.uniqueUnitId = uniqueUnitId;
        this.shortDescription = shortDescription;
    }

    @Override
    public String shortDescription() {
        return shortDescription;
    }

    @Override
    public String id() {
        return "" + uniqueUnitId;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
    
}
