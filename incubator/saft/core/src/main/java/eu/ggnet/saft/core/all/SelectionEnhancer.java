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
package eu.ggnet.saft.core.all;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * A way to generate multiple different instances from one selection.
 * 
 * @author oliver.guenther
 */
public interface SelectionEnhancer<T> {
    
    /**
     * Filters the the selected value and may return more than one other embedded selected value, must never return null.
     * 
     * @param selected
     * @return the the selected value and may return more than one other embedded selected value, must never return null.
     */
    @NotNull
    List<?> enhance(T selected);
    
}
