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
package eu.ggnet.dwoss.common.api.values;

import lombok.*;

/**
 * These are the ShippingCondition in a Enum.
 * <p/>
 * @author bastian.venz
 */
@RequiredArgsConstructor
@Getter
public enum ShippingCondition {

    SIX_MIN_TEN("6€/Gerät min. 10€", 10, 6), FIVE("5€/Gerät", 5, 5), SIX("6€/Gerät", 6, 6), FIVE_EIGHTY("5,80€/Gerät", 5.8, 5.8);

    private final String name;

    /**
     * Price of a single unit shipping
     */
    private final double priceOfOne;

    /**
     * Multiplicator for shippings of multiple unit
     */
    private final double multiplicator;

}
