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
package eu.ggnet.dwoss.spec.api;

/**
 *
 * @author oliver.guenther
 */
public interface SpecApi {

    /**
     * Returns true if the supplied product id has a spec.
     * It is assumed, that if a spec exists it overseeds the product of uniqueunit.
     *
     * @param productId the productId of uniqueUnit.
     * @return true if there is a spec for the productId.
     */
    boolean hasSpec(long productId);

}
