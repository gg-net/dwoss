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
package eu.ggnet.dwoss.rules;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.*;

/**
 * These are possible special operations that either show a status, like saleable, or have in common, that a unit is block some there for some internal/external
 * process.
 *
 * @author oliver.guenther
 */
@RequiredArgsConstructor
@Getter
public enum ReceiptOperation implements Serializable {

    /**
     * The internal rework process.
     */
    INTERNAL_REWORK("Interne Nacharbeiten", true),
    /**
     * The internal process for missing parts.
     */
    MISSING_PARTS("Fehlende Teile", true),
    /**
     * The external process for a repair of the manufacturer.
     */
    REPAIR("Reparatur", true),
    /**
     * The sale able process, meaning the unit is available and not blocked any there.
     */
    // HINT: OG doesn't like a process indicator which is handled different, but accepts it for now.
    SALEABLE("Verkaufsfähig", false),
    /**
     * The in sale process, meaning the unit is reserved or sold.
     */
    // HINT: OG doesn't like a process indicator which is handled different, but accepts it for now.
    IN_SALE("Im Verkauf oder Reported (akiver Kundenauftrag)", false);

    private final String note;

    private final boolean backedByCustomer;

    public static List<ReceiptOperation> valuesBackedByCustomer() {
        return Arrays.asList(values()).stream().filter(ReceiptOperation::isBackedByCustomer).collect(Collectors.toList());
    }

}
