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
package eu.ggnet.dwoss.stock.emo;

import java.util.*;

import javax.validation.constraints.*;

import lombok.Value;
import lombok.experimental.Builder;

/**
 * Parameter Class for {@link StockTransactionEmo#transfer}.
 * <p>
 * @author oliver.guenther
 */
@Value
@Builder
public class Transfer {

    /**
     * The stockIds to transfer.
     */
    @NotNull
    @Size(min = 1)
    private final List<Integer> stockUnitIds;

    /**
     * The id of the destination stock.
     */
    private final int destinationStockId;

    /**
     * A optional arranger.
     */
    private final String arranger;

    /**
     * A optional comment.
     */
    private final String comment;

    /**
     * The maximum amount of units a trasaction should hold.
     */
    @Min(1)
    @Max(9999)
    private final int maxTransactionSize;

}
