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
package eu.ggnet.dwoss.customer.ee;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.customer.ee.entity.projection.PicoCustomer;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;
import eu.ggnet.saft.api.Reply;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.EMAIL;

/**
 *
 * @author jens.papenhagen
 */
@Stateless
public class CustomerAgentBean extends AbstractAgentBean implements CustomerAgent {

    private final static Logger L = LoggerFactory.getLogger(CustomerAgentBean.class);

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private CustomerEao customerEao;

    @Inject
    private Mandator mandator;

    @Inject
    private DefaultCustomerSalesdata salesdata;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<PicoCustomer> search(String search, Set<SearchField> customerFields) {
        return customerEao.find(search).stream().map(Customer::toPico).collect(Collectors.toList());
    }

    @Override
    public List<PicoCustomer> search(String search, Set<SearchField> customerFields, int start, int limit) {
        return customerEao.find(search, start, limit).stream().map(Customer::toPico).collect(Collectors.toList());
    }

    @Override
    public int countSearch(String search, Set<SearchField> customerFields) {
        return customerEao.countFind(search);
    }

    private <T> T request(List<T> in, Supplier<T> producer) {
        if ( in.isEmpty() ) {
            T t = producer.get();
            in.add(t);
            return t;
        }
        return in.get(0);
    }

    private void update(List<Communication> communications, Communication.Type type, String identifier) {
        if ( StringUtils.isBlank(identifier) ) {
            Optional<Communication> optEmail = communications.stream().filter(co -> co.getType() == type).findFirst();
            optEmail.ifPresent(email -> {
                communications.remove(email);
                em.remove(email);
            });
        } else {
            Optional<Communication> optEmail = communications.stream().filter(co -> co.getType() == type).findFirst();
            optEmail.ifPresent(email -> {
                email.setIdentifier(identifier);
            });
            if ( !optEmail.isPresent() ) {
                Communication email = new Communication();
                communications.add(email);
                email.setType(type);
                email.setIdentifier(identifier);
            }
        }
    }

    @Override
    public Reply<Customer> store(SimpleCustomer simpleCustomer) {
        boolean exists = (simpleCustomer.getId() > 0);
        boolean bussines = !StringUtils.isBlank(simpleCustomer.getCompanyName());

        Customer customer;
        if ( exists ) {
            customer = em.find(Customer.class, simpleCustomer.getId());
            Objects.requireNonNull(customer, "No Customer with id " + simpleCustomer.getId() + ", error");
        } else {
            customer = new Customer();
        }

        Company comp = null;
        Address a;
        Contact cont;
        AddressLabel al;
        if ( bussines ) {
            comp = request(customer.getCompanies(), () -> new Company());
            comp.setName(simpleCustomer.getCompanyName());
            comp.setTaxId(simpleCustomer.getTaxId());
            a = request(comp.getAddresses(), () -> new Address(AddressType.INVOICE));
            cont = request(comp.getContacts(), () -> new Contact());
            cont.getAddresses().add(a);
        } else {
            cont = request(customer.getContacts(), () -> new Contact());
            a = request(cont.getAddresses(), () -> new Address(AddressType.INVOICE));
        }
        al = new AddressLabel(comp, cont, a, AddressType.INVOICE);
        customer.getAddressLabels().clear();
        customer.getAddressLabels().add(al);

        cont.setFirstName(simpleCustomer.getFirstName());
        cont.setLastName(simpleCustomer.getLastName());
        cont.setSex(simpleCustomer.getSex());
        cont.setTitle(simpleCustomer.getTitle());

        //Contact with only one Address
        a.setCity(simpleCustomer.getCity());
        a.setCountry(simpleCustomer.getCountry());
        a.setStreet(simpleCustomer.getStreet());
        a.setZipCode(simpleCustomer.getZipCode());

        update(cont.getCommunications(), Type.EMAIL, simpleCustomer.getEmail());
        update(cont.getCommunications(), Type.PHONE, simpleCustomer.getLandlinePhone());
        update(cont.getCommunications(), Type.MOBILE, simpleCustomer.getMobilePhone());

        L.info("Trying Customer {} with coms: {} ", simpleCustomer.getLastName(), cont.getCommunications());

        customer.setSource(simpleCustomer.getSource());
        if ( !customer.isValid() ) return Reply.failure(customer.getViolationMessage());

        if ( !exists ) em.persist(customer);
        return Reply.success(customer);
    }

    @Override
    public String findCustomerAsMandatorHtml(long id) {
        return Optional.ofNullable(customerEao.findById(id)).map(c -> c.toHtml(mandator.getMatchCode(), salesdata)).orElse("Kein Kunde mit id " + id + " vorhanden");
    }

    @Override
    public String findCustomerAsHtml(long id) {
        return Optional.ofNullable(customerEao.findById(id)).map(Customer::toHtml).orElse("Kein Kunde mit id " + id + " vorhanden");
    }

}
