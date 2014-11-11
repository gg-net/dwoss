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
package eu.ggnet.dwoss.receipt.unit.chain.string;

import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

import lombok.RequiredArgsConstructor;

/**
 * A Link that removes the "start" from "value" if the "value" has it, else does nothing.
 * <p/>
 * @author oliver.guenther
 */
@RequiredArgsConstructor
public class RemoveIfStartsWith implements ChainLink<String> {

    private final String start;

    @Override
    public Result<String> execute(String value) {
        if ( value.startsWith(start) ) return new Result<>(value.substring(start.length(), value.length()));
        return new Result<>(value);
    }
}
