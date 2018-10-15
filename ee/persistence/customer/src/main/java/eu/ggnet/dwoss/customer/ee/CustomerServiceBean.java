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
import eu.ggnet.dwoss.customer.ee.priv.OldCustomer;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.common.ee.Css;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.customer.ee.entity.Communication;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.EMAIL;

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
        return Optional.ofNullable(customerEao.findById(customerId)).map(c -> Css.toHtml5WithStyle(c.toHtml(mandator.getMatchCode(), salesData))).orElse("Kein Kunde mit id " + customerId + " gefunden");
//        return convert(customerEao.findById(customerId)).toHtmlHighDetailed();
    }

    @Override
    public UiCustomer asUiCustomer(long customerId) {
        return asUiCustomer(customerEao.findById(customerId));
    }

    @Override
    public List<UiCustomer> asUiCustomers(String search) {
        return customerEao.find(search, null ).stream().map((customer) -> {
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

        if ( customer.isBusiness() ) {

            String email = "";
            Optional<Communication> findEMAIL = customer.getCompanies().get(0).getCommunications().stream().filter(c -> c.getType() == EMAIL).findFirst();
            if ( findEMAIL.isPresent() ) {
                email = findEMAIL.get().getIdentifier();
            }
            return new UiCustomer(
                    customer.getId(),
                    customer.getCompanies().get(0).getContacts().get(0).getTitle() != null ? customer.getCompanies().get(0).getContacts().get(0).getTitle() + " " : "",
                    customer.getCompanies().get(0).getContacts().get(0).getFirstName(),
                    customer.getCompanies().get(0).getContacts().get(0).getLastName(),
                    customer.getCompanies().get(0).getName(),
                    customer.toString(),
                    email,
                    customer.getCompanies().get(0).getLedger());

        } else {
            String email = "";
            Optional<Communication> findEMAIL = customer.getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == EMAIL).findFirst();
            if ( findEMAIL.isPresent() ) {
                email = findEMAIL.get().getIdentifier();
            }
            return new UiCustomer(
                    customer.getId(),
                    customer.getContacts().get(0).getTitle() != null ? customer.getContacts().get(0).getTitle() + " " : "",
                    customer.getContacts().get(0).getFirstName(),
                    customer.getContacts().get(0).getLastName(),
                    null,
                    customer.toString(),
                    email,
                    0);
        }

        //TODO Olli fragen ob okay        
//        return new UiCustomer(
//                old.getId(),
//                old.getTitel() != null ? old.getTitel() + " " : "",
//                old.getVorname(),
//                old.getNachname(),
//                old.getFirma(),
//                old.toHtmlSimple(),
//                old.getEmail(),
//                old.getLedger());
    }

    private CustomerMetaData asCustomerMetaData(Customer customer) {
        String email = "";
        if ( customer.isBusiness() ) {
            email = customer.getCompanies().get(0)
                    .getCommunications().stream()
                    .filter(c -> c.getType() == EMAIL)
                    .map(Communication::getIdentifier)
                    .findFirst()
                    .orElse("no email");
        } else {
            email = customer.getContacts().get(0)
                    .getCommunications().stream()
                    .filter(c -> c.getType() == EMAIL)
                    .map(Communication::getIdentifier)
                    .findFirst().orElse("no email");
        }

        return new CustomerMetaData(
                customer.getId(),
                email,
                customer.getMandatorMetadata().size() > 0 ? customer.getMandatorMetadata().get(0).getPaymentCondition() : null,
                customer.getMandatorMetadata().size() > 0 ? customer.getMandatorMetadata().get(0).getPaymentMethod() : null,
                customer.getMandatorMetadata().size() > 0 ? customer.getMandatorMetadata().get(0).getShippingCondition() : null,
                customer.getFlags(),
                customer.getMandatorMetadata().size() > 0 ? customer.getMandatorMetadata().get(0).getAllowedSalesChannels() : null
        );
        //TODO Olli fragen ob okay
//        return new CustomerMetaData(
//                old.getId(),
//                old.getEmail(),
//                old.getPaymentCondition(),
//                old.getPaymentMethod(),
//                old.getShippingCondition(),
//                old.getFlags(),
//                old.getAllowedSalesChannels());
    }

    @Override
    public List<Long> allSystemCustomerIds() {
        return customerEao.findAllSystemCustomerIds();
    }

    @Deprecated // Useless merged it allready
    @Override
    public String asNewHtmlHighDetailed(long id) {
        return customerEao.findById(id).toHtml();
    }
}
