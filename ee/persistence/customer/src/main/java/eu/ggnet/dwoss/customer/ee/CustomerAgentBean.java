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
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;
import eu.ggnet.saft.api.Reply;


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

    @Override
    public Reply<Customer> store(SimpleCustomer simpleCustomer) {
        L.warn("Simulating Store : " + simpleCustomer.toString());

        //convert the SimpleCustomer to a Customer
        boolean bussines = !StringUtils.isBlank(simpleCustomer.getCompanyName());

        Customer c = new Customer();

        Contact cont = new Contact();
        cont.setFirstName(simpleCustomer.getFirstName());
        cont.setLastName(simpleCustomer.getLastName());
        cont.setSex(simpleCustomer.getSex());
        cont.setTitle(simpleCustomer.getTitle());

        //Contact with only one Address
        Address a = new Address();
        a.setCity(simpleCustomer.getCity());
        a.setIsoCountry(new Locale(simpleCustomer.getIsoCountry().toLowerCase(), simpleCustomer.getIsoCountry().toUpperCase()));
        a.setStreet(simpleCustomer.getStreet());
        a.setZipCode(simpleCustomer.getZipCode());
        cont.getAddresses().add(a);

        //one Communication form eatch type email, phone, mobile allowed
        if ( !StringUtils.isBlank(simpleCustomer.getEmail()) )
            cont.getCommunications().add(new Communication(Type.EMAIL, simpleCustomer.getEmail()));
        if ( !StringUtils.isBlank(simpleCustomer.getLandlinePhone()) )
            cont.getCommunications().add(new Communication(Type.PHONE, simpleCustomer.getLandlinePhone()));
        if ( !StringUtils.isBlank(simpleCustomer.getMobilePhone()) )
            cont.getCommunications().add(new Communication(Type.MOBILE, simpleCustomer.getMobilePhone()));


        AddressLabel al;
        if ( bussines ) {
            Company comp = new Company();
            comp.setName(simpleCustomer.getCompanyName());
            comp.setTaxId(simpleCustomer.getTaxId());

            //The Address of the Company Contact has to match the Company Address
            comp.getAddresses().add(a);
            comp.getContacts().add(cont);
            //build AddressLabel
            al = new AddressLabel(comp, comp.getContacts().get(0), a, AddressType.INVOICE);

            c.getCompanies().add(comp);

        } else {
            c.getContacts().add(cont);
            al = new AddressLabel(null, cont, a, AddressType.INVOICE);
        }
        c.getAddressLabels().add(al);
        c.setSource(simpleCustomer.getSource());

        if ( !c.isValid() ) return Reply.failure(c.getViolationMessage());
        return Reply.success(c);
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
