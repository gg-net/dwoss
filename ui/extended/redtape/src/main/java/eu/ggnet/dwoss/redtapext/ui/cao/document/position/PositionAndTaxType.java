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
package eu.ggnet.dwoss.redtapext.ui.cao.document.position;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.common.api.values.TaxType;
import eu.ggnet.dwoss.redtape.ee.entity.Position;

/**
 *
 * @author oliver.guenther
 */
public class PositionAndTaxType {

    public final Position position;

    public final TaxType taxType;

    public PositionAndTaxType(Position position, TaxType taxType) {
        this.position = Objects.requireNonNull(position);
        this.taxType = Objects.requireNonNull(taxType);
    }

    public Position getPosition() {
        return position;
    }

    public TaxType getTaxType() {
        return taxType;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
