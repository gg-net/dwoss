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

import eu.ggnet.dwoss.customer.api.CustomerMetaData;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.api.UiCustomer;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.common.ee.Css;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.customer.ee.entity.*;

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
        return Optional.ofNullable(customerEao.findById(customerId)).map(c -> Css.toHtml5WithStyle(c.toHtml(mandator.getMatchCode(), salesData))).orElse("Kein Kunde mit id " + customerId + " gefunden");
    }

    @Override
    public UiCustomer asUiCustomer(long customerId) {
        return asUiCustomer(customerEao.findById(customerId));
    }

    @Override
    public List<UiCustomer> asUiCustomers(String search) {
        return customerEao.find(search, null).stream().map((customer) -> {
            return asUiCustomer(customer);
        }).collect(Collectors.toList());
    }

    @Override
    public List<UiCustomer> asUiCustomers(String company, String firstName, String lastName, String email, boolean appendWildcard) {
        L.debug("asUiCustomers called with company={},firstName={},lastName={},email={},wildcard={}", company, firstName, lastName, email, appendWildcard);
        List<UiCustomer> customers = new ArrayList<>();
        for (Customer customer : customerEao.find(company, firstName, lastName, email, appendWildcard)) {
            customers.add(asUiCustomer(customer));
        }
        return customers;
    }

    @Override
    public CustomerMetaData asCustomerMetaData(long customerId) {
        return asCustomerMetaData(customerEao.findById(customerId));
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
        List<CustomerMetaData> customers = new ArrayList<>();
        for (Customer customer : customerEao.findAll()) {
            customers.add(asCustomerMetaData(customer));
        }

        return customers;
    }

    private UiCustomer asUiCustomer(Customer customer) {
        Objects.requireNonNull(customer, "Supplied customer ist null, not allowed");

        Contact contact = null;
        Company company = null;
        if ( customer.isBusiness() ) {
            //should never be null
            company = customer.getCompanies().stream().findFirst().get();
            contact = company.getContacts().stream().findFirst().orElse(null);
        } else {
            //should never be null
            contact = customer.getContacts().stream().findFirst().get();
        }

        return new UiCustomer(
                customer.getId(),
                contact != null ? contact.getTitle() : "",
                contact != null ? contact.getFirstName() : "KEIN KONTAKT",
                contact != null ? contact.getLastName() : "KEIN KONTAKT",
                company != null ? company.getName() : null,
                customer.toName(),
                Optional.ofNullable(customer.getDefaultEmailCommunication()).map(Communication::getIdentifier).orElse(null),
                company != null ? company.getLedger() : 0);
    }

    private CustomerMetaData asCustomerMetaData(Customer customer) {
        Optional<MandatorMetadata> omm = Optional.ofNullable(customer.getMandatorMetadata(mandator.getMatchCode()));
        return new CustomerMetaData(
                customer.getId(),
                Optional.ofNullable(customer.getDefaultEmailCommunication()).map(Communication::getIdentifier).orElse(null),
                omm.map(MandatorMetadata::getPaymentCondition).filter(mm -> mm != null).orElse(salesData.getPaymentCondition()),
                omm.map(MandatorMetadata::getPaymentMethod).filter(mm -> mm != null).orElse(salesData.getPaymentMethod()),
                omm.map(MandatorMetadata::getShippingCondition).filter(mm -> mm != null).orElse(salesData.getShippingCondition()),
                customer.getFlags(),
                omm.map(MandatorMetadata::getAllowedSalesChannels).filter(sc -> !sc.isEmpty()).orElse(salesData.getAllowedSalesChannels()),
                customer.getViolationMessage()
        );
    }

    @Override
    public List<Long> allSystemCustomerIds() {
        return customerEao.findAllSystemCustomerIds();
    }

}
