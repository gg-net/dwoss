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
package eu.ggnet.dwoss.customer.spi;

import java.util.function.Consumer;

import eu.ggnet.saft.core.ui.UiParent;

/**
 * This interface shall provide the possibility to create or modify Customers.
 * <p>
 * @author pascal.perau
 */
public interface CustomerUiModifier {

    /**
     * Method for Customer creation.
     * <p>
     * @param parent optional parent
     * @param id
     */
    void createCustomer(UiParent parent, Consumer<Long> id);

    /**
     * Method for Customer modification.
     * <p>
     * @param parent     optional parent
     * @param customerId the SopoCustomer to be modified
     * @param change     change consumer, will be called with true, if customer was changed.
     */
    void updateCustomer(UiParent parent, long customerId, Runnable change);
}
