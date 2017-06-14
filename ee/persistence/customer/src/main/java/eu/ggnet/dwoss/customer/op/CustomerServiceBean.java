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
package eu.ggnet.dwoss.customer.op;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.api.*;
import eu.ggnet.dwoss.customer.eao.CustomerEao;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.priv.ConverterUtil;
import eu.ggnet.dwoss.customer.priv.OldCustomer;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.rules.CustomerFlag;

/**
 * CustomerService implementation for {@link OldCustomer}.
 */
@Stateless
@LocalBean
public class CustomerServiceBean implements CustomerService {

    private final static Logger L = LoggerFactory.getLogger(CustomerServiceBean.class);

    @Inject
    private CustomerEao customerEao;

    @Inject
    private Mandator mandator;

    @Inject
    private DefaultCustomerSalesdata salesData;

    @Override
    public String asHtmlHighDetailed(long customerId) {
        return convert(customerEao.findById(customerId)).toHtmlHighDetailed();
    }

    @Override
    public UiCustomer asUiCustomer(long customerId) {
        return asUiCustomer(convert(customerEao.findById(customerId)));
    }

    @Override
    public List<UiCustomer> asUiCustomers(String search) {
        return customerEao.find(search).stream().map((customer) -> {
            return asUiCustomer(convert(customer));
        }).collect(Collectors.toList());
    }

    @Override
    public List<UiCustomer> asUiCustomers(String company, String firstName, String lastName, String email, boolean appendWildcard) {
        L.debug("asUiCustomers called with company={},firstName={},lastName={},email={},wildcard={}", company, firstName, lastName, email, appendWildcard);
        List<UiCustomer> customers = new ArrayList<>();
        for (Customer customer : customerEao.find(company, firstName, lastName, email, appendWildcard)) {
            customers.add(asUiCustomer(convert(customer)));
        }
        return customers;
    }

    @Override
    public CustomerMetaData asCustomerMetaData(long customerId) {
        return asCustomerMetaData(convert(customerEao.findById(customerId)));
    }

    @Override
    public String findComment(long id) {
        return customerEao.findById(id).getComment();
    }

    @Override
    public void updateCustomerFlags(long customerId, Set<CustomerFlag> flags) {
        Customer customer = customerEao.findById(customerId);
        customer.clearFlags();
        for (CustomerFlag customerFlag : flags) {
            customer.add(customerFlag);
        }
    }

    @Override
    public List<CustomerMetaData> allAsCustomerMetaData() {
        List<CustomerMetaData> customers = new ArrayList<>();
        for (Customer customer : customerEao.findAll()) {
            customers.add(asCustomerMetaData(convert(customer)));
        }

        return customers;
    }

    private OldCustomer convert(Customer c) {
        return ConverterUtil.convert(c, mandator.getMatchCode(), salesData);
    }

    private UiCustomer asUiCustomer(OldCustomer old) {
        return new UiCustomer(
                old.getId(),
                old.getTitel() != null ? old.getTitel() + " " : "",
                old.getVorname(),
                old.getNachname(),
                old.getFirma(),
                old.toHtmlSimple(),
                old.getEmail(),
                old.getLedger());
    }

    private CustomerMetaData asCustomerMetaData(OldCustomer old) {
        return new CustomerMetaData(
                old.getId(),
                old.getEmail(),
                old.getPaymentCondition(),
                old.getPaymentMethod(),
                old.getShippingCondition(),
                old.getFlags(),
                old.getAllowedSalesChannels());
    }

    @Override
    public List<Long> allSystemCustomerIds() {
        return customerEao.findAllSystemCustomerIds();
    }
}
