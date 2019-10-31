/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.api;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Representation of the customer allowed to receive a reseller list and his email address.
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface ResellerListCustomer {

    long id();

    String name();

    String email();

    class Builder extends ResellerListCustomer_Builder {
    }

}
