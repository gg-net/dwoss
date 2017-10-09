/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.op;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.customer.eao.CustomerEao;
import eu.ggnet.dwoss.search.api.*;

import static eu.ggnet.dwoss.search.api.GlobalKey.Component.CUSTOMER;

/**
 * Search Provider for Customers.
 *
 * @author oliver.guenther
 */
@Stateless
public class CustomerSearchProvider implements SearchProvider {

    @Inject
    private CustomerEao customerEao;

    @Override
    public int estimateMaxResults(SearchRequest request) {
        // TODO: Test Speed, never used that.
        return customerEao.countFind(request.getSearch());
    }

    @Override
    public List<ShortSearchResult> search(SearchRequest request, int start, int limit) {
        return customerEao.find(request.getSearch(), start, limit).stream().map((customer) -> {
            return new ShortSearchResult(new GlobalKey(CUSTOMER, customer.getId()), customer.toName());
        }).collect(Collectors.toList());
    }

    @Override
    public String details(GlobalKey key) {
        // TODO: How about some safty mesures.
        return customerEao.findById(key.getId()).toHtml();
    }

    @Override
    public GlobalKey.Component getSource() {
        return GlobalKey.Component.CUSTOMER;
    }

}
