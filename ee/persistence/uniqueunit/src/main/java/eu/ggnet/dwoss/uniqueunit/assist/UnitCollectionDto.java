/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.assist;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.entity.*;

import lombok.*;

/**
 *
 * @author jens.papenhagen
 */
@Data
public class UnitCollectionDto implements Serializable {

    private long id;

    private String nameExtension;

    private String descriptionExtension;

    private String partNoExtension;

    List<PicoUnit> units = new ArrayList<>();

    private Map<PriceType, Double> prices = new HashMap<>();

    private List<PriceHistory> priceHistories = new ArrayList<>();

    private SalesChannel salesChannel;

    public List<PicoUnit> getUnits() {
        if ( units == null ) return Collections.emptyList();
        return units;
    }

    public Map<PriceType, Double> getPrices() {
        if ( prices == null ) return Collections.emptyMap();
        return prices;
    }

    public List<PriceHistory> getPriceHistories() {
        if ( priceHistories == null ) return Collections.emptyList();
        return priceHistories;
    }

}
