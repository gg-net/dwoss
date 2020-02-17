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
package eu.ggnet.dwoss.customer.ee;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.Css;
import eu.ggnet.dwoss.core.common.values.CustomerFlag;
import eu.ggnet.dwoss.customer.api.*;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;

/**
 * CustomerService implementation.
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
        return Optional.ofNullable(customerEao.findById(customerId)).map(c -> Css.toHtml5WithStyle(c.toHtml(mandator.matchCode(), salesData))).orElse("Kein Kunde mit id " + customerId + " gefunden");
    }

    @Override
    public UiCustomer asUiCustomer(long customerId) {
        return customerEao.findById(customerId).toUiCustomer();
    }

    @Override
    public List<UiCustomer> asUiCustomers(String search) {
        return customerEao.find(search, null).stream().map(Customer::toUiCustomer).collect(Collectors.toCollection(() -> new ArrayList<>()));
    }

    @Override
    public List<UiCustomer> asUiCustomers(String company, String firstName, String lastName, String email, boolean appendWildcard) {
        L.debug("asUiCustomers called with company={},firstName={},lastName={},email={},wildcard={}", company, firstName, lastName, email, appendWildcard);
        return customerEao.find(company, firstName, lastName, email, appendWildcard)
                .stream().map(Customer::toUiCustomer).collect(Collectors.toCollection(() -> new ArrayList<>()));
    }

    @Override
    public CustomerMetaData asCustomerMetaData(long customerId) {
        CustomerMetaData data = asCustomerMetaData(customerEao.findById(customerId));
        L.info("asCustomerMetadata(id={}):{}", customerId, data);
        return data;
    }

    @Override
    public String findComment(long id) {
        return customerEao.findById(id).getComment();
    }

    // TODO: Gehört hier eigentlich nicht hin, da es direkt aus ee.redtape benutzt wird. Eher extra bean.
    @Override
    public void updateCustomerFlags(long customerId, Set<CustomerFlag> flags) {
        Customer customer = customerEao.findById(customerId);
        customer.getFlags().clear();
        for (CustomerFlag customerFlag : flags) {
            customer.getFlags().add(customerFlag);
        }
    }

    @Override
    public List<CustomerMetaData> allAsCustomerMetaData() {
        return customerEao.findAll().stream().map((customer) -> asCustomerMetaData(customer)).collect(Collectors.toList());
    }

    private CustomerMetaData asCustomerMetaData(Customer customer) {
        Optional<MandatorMetadata> omm = Optional.ofNullable(customer.getMandatorMetadata(mandator.matchCode()));
        return new CustomerMetaData(
                customer.getId(),
                omm.map(MandatorMetadata::getPaymentCondition).filter(mm -> mm != null).orElse(salesData.paymentCondition()),
                omm.map(MandatorMetadata::getPaymentMethod).filter(mm -> mm != null).orElse(salesData.paymentMethod()),
                omm.map(MandatorMetadata::getShippingCondition).filter(mm -> mm != null).orElse(salesData.shippingCondition()),
                customer.getFlags(),
                omm.map(MandatorMetadata::getAllowedSalesChannels).filter(sc -> !sc.isEmpty()).orElse(salesData.allowedSalesChannels()),
                customer.getViolationMessage()
        );
    }

    @Override
    public List<Long> allSystemCustomerIds() {
        return customerEao.findAllSystemCustomerIds();
    }

    @Override
    public String defaultEmailCommunication(long customerId) {
        return customerEao.findById(customerId).getDefaultEmailCommunication().map(Communication::getIdentifier).orElse(null);
    }

}
