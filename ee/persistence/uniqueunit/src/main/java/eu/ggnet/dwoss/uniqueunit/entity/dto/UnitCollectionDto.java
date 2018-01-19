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
package eu.ggnet.dwoss.uniqueunit.entity.dto;

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

    private Map<PriceType, Double> prices = new HashMap<>();

    private SalesChannel salesChannel;

}
