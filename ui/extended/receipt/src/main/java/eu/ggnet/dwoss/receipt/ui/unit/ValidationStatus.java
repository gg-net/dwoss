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
package eu.ggnet.dwoss.receipt.ui.unit;

import java.awt.Color;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Status of a Validation, contains a color to be used in the ui.
 * <p/>
 * @author oliver.guenther
 */
@RequiredArgsConstructor
public enum ValidationStatus {

    /**
     * OK(Color.BLACK).
     */
    OK(Color.BLACK),
    /**
     * VALIDATING(Color.CYAN).
     */
    VALIDATING(Color.CYAN),
    /**
     * WARNING(Color.ORANGE).
     */
    WARNING(Color.ORANGE),
    /**
     * ERROR(Color.RED).
     */
    ERROR(Color.RED);

    /**
     * A color representing the status.
     */
    @Getter
    private final Color color;

}
