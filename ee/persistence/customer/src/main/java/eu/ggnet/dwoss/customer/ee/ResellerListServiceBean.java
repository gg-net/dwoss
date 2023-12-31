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
package eu.ggnet.dwoss.customer.ee;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import eu.ggnet.dwoss.customer.api.ResellerListCustomer;
import eu.ggnet.dwoss.customer.api.ResellerListService;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.Customer;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class ResellerListServiceBean implements ResellerListService {

    @Inject
    private CustomerEao eao;

    @Override
    public List<ResellerListCustomer> allResellerListCustomers() {
        return eao.findAllWithResellerListEmailCommunication().stream()
                .map((Customer c) -> {
                    return new ResellerListCustomer.Builder()
                            .id(c.getId())
                            .name(c.toName())
                            .email(c.getResellerListEmailCommunication().get().getIdentifier()) // Never unset.
                            .build();
                })
                .collect(Collectors.toList());
    }

}
