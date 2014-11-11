/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.uniqueunit.entity;

/**
 * The different types of prices for {@link UniqueUnit}s and {@link Product}s.
 * <p>
 * @author oliver.guenther
 */
public enum PriceType {

    /**
     * The Price something is sold.
     */
    SALE,
    /**
     * A Price, something is bought.
     */
    PURCHASE,
    /**
     * The Costprice of the Manufacturer.
     */
    MANUFACTURER_COST,
    /**
     * A Reference Price for the Contractor.
     */
    CONTRACTOR_REFERENCE,
    /**
     * The price used in the retailer chanel.
     */
    RETAILER,
    /**
     * The price used in the customer chanel.
     */
    CUSTOMER

}
