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
package tryout.stub;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.LockModeType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.customer.ee.entity.projection.PicoCustomer;
import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.customer.ee.entity.stash.*;
import eu.ggnet.saft.api.Reply;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerAgentStub implements CustomerAgent {

    private final int AMOUNT = 25;

    private final int SLOW = 40;

    private final Logger L = LoggerFactory.getLogger(CustomerAgentStub.class);

    private final CustomerGenerator CGEN = new CustomerGenerator();

    private final List<Customer> CUSTOMERS;

    private Customer customer;

    public CustomerAgentStub(Customer customer) {
        this.customer = customer;
    }

    public CustomerAgentStub() {
    }

    {
        CUSTOMERS = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Customer customer = CGEN.makeCustomer();
            customer.getMandatorMetadata().add(CGEN.makeMandatorMetadata());
            CUSTOMERS.add(customer);
        }
    }

    @Override
    public <T> long count(Class<T> entityClass) {
        if ( entityClass.equals(Customer.class) ) {
            return SLOW;
        }
        throw new UnsupportedOperationException(entityClass + " not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //<editor-fold defaultstate="collapsed" desc="Unused Methods">
    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<T> findAll(Class< T> entityClass, int start, int amount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<T> findAllEager(Class< T> entityClass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<T> findAllEager(Class< T> entityClass, int start, int amount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findById(Class< T> entityClass, Object id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findById(Class< T> entityClass, Object id, LockModeType lockModeType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T findByIdEager(Class< T> entityClass, Object id, LockModeType lockModeType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    //</editor-fold>

    @Override
    public <T> T findByIdEager(Class< T> entityClass, Object id) {
        if ( entityClass.equals(Customer.class) ) {
            if ( customer != null ) return (T)customer;
            else return (T)CGEN.makeCustomer();
        }
        if ( entityClass.equals(Company.class) && customer != null ) {
            Optional<Company> company = customer.getCompanies().stream().filter(c -> Objects.equals((Long)c.getId(), (Long)id)).findFirst();
            if ( company.isPresent() ) return (T)company.get();
            else return null;
        }
        if ( entityClass.equals(Contact.class) && customer != null ) {
            Optional<Contact> contact = customer.getContacts().stream().filter(c -> Objects.equals((Long)c.getId(), (Long)id)).findFirst();
            if ( contact.isPresent() ) return (T)contact.get();
            else return null;
        }
        return null;
    }

    @Override
    public List<PicoCustomer> search(String search, Set<SearchField> customerFields) {
        List<PicoCustomer> list = new ArrayList<>();
        for (int i = 0; i < AMOUNT; i++) {
            list.add(CGEN.makeCustomer().toPico());
        }

        L.info("Returning {}", list);
        return list;
    }

    /**
     * this methoes drops NPE like hell because many fields of the genaratet customer get checkt
     *
     * @param search
     * @param customerFields
     * @param start
     * @param limit
     * @return
     */
    @Override
    public List<PicoCustomer> search(String search, Set<SearchField> customerFields, int start, int limit) {
        L.info("SearchString {},{},start={},limit={}", search, customerFields.size(), start, limit);

        try {
            Thread.sleep(200L, SLOW);
        } catch (InterruptedException ex) {
            L.error("InterruptedException get throw");
        }

        if ( start >= CUSTOMERS.size() ) return Collections.emptyList();
        if ( limit >= CUSTOMERS.size() ) limit = CUSTOMERS.size();
        List<Customer> result = CUSTOMERS.subList(start, limit + start);

        List<Customer> tempList = new ArrayList<>();
        if ( customerFields.contains(SearchField.LASTNAME) ) {
            result.forEach(c -> {
                c.getContacts().stream().filter((contact) -> (contact.getLastName().contains(search))).forEachOrdered((sa) -> {
                    tempList.add(c);
                });
            });
            result.addAll(tempList);
        }

        if ( customerFields.contains(SearchField.FIRSTNAME) ) {
            result.forEach((customer) -> {
                customer.getContacts().stream().filter((cont) -> (cont.getFirstName().contains(search))).forEachOrdered((_item) -> {
                    tempList.add(customer);
                });
            });
        }
        if ( customerFields.contains(SearchField.ID) ) {
            if ( search.matches("\\d*") ) {
                result.stream().filter((customer) -> (customer.getId() == Long.parseLong(search))).forEachOrdered((customer) -> {
                    tempList.add(customer);
                });
                result.addAll(tempList);
            }
        }
        if ( customerFields.contains(SearchField.COMPANY) ) {
            result.forEach((customer) -> {
                customer.getCompanies().stream().filter((com) -> (com.getName().contains(search))).forEachOrdered((_item) -> {
                    tempList.add(customer);
                });
            });
            result.addAll(tempList);
        }
        if ( customerFields.contains(SearchField.ADDRESS) ) {
            for (Customer customer : result) {

                customer.getContacts().forEach((contact) -> {
                    contact.getAddresses().stream().map((addr) -> {
                        if ( addr.getStreet().contains(search) ) {
                            tempList.add(customer);
                        }
                        return addr;
                    }).map((addr) -> {
                        if ( addr.getCity().contains(search) ) {
                            tempList.add(customer);
                        }
                        return addr;
                    }).filter((addr) -> (addr.getZipCode().contains(search))).forEachOrdered((_item) -> {
                        tempList.add(customer);
                    });
                });
                result.addAll(tempList);

            }
        }

        L.info("Returning {}", result);
        return result.stream().map(Customer::toPico).collect(Collectors.toList());
    }

    @Override
    public int countSearch(String search, Set<SearchField> customerFields) {

        return search(search, customerFields, 0, CUSTOMERS.size()).size();
    }

    @Override
    public Reply<Customer> store(SimpleCustomer simpleCustomer) {
        L.info("Input form Stubs: " + simpleCustomer.toString());

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
        a.setCountry(Country.GERMANY);
        a.setStreet(simpleCustomer.getStreet());
        a.setZipCode(simpleCustomer.getZipCode());
        cont.getAddresses().add(a);

        //one Communication form eatch type email, phone, mobile allowed
        if ( simpleCustomer.getEmail() != null ) {
            cont.getCommunications().add(new Communication(Type.EMAIL, simpleCustomer.getEmail()));
        }
        if ( simpleCustomer.getLandlinePhone() != null ) {
            cont.getCommunications().add(new Communication(Type.PHONE, simpleCustomer.getLandlinePhone()));
        }
        if ( simpleCustomer.getMobilePhone() != null ) {
            cont.getCommunications().add(new Communication(Type.MOBILE, simpleCustomer.getMobilePhone()));
        }

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
        return "Showing a Customer with MandatorHtml";
    }

    @Override
    public String findCustomerAsHtml(long id) {
        return "Showing a Customer as Html";
    }

    @Override
    public void create(Root root, Object raw) {
        L.info("create {} form {}", raw, root);
        Object rootElement = null;

        if ( root.getClazz() == Customer.class ) rootElement = customer;
        else if ( root.getClazz() == Company.class ) {
            Optional<Company> company = customer.getCompanies().stream().filter(c -> c.getId() == root.getId()).findFirst();
            if ( company.isPresent() ) rootElement = company.get();
            else throw new IllegalArgumentException("Company is not on the customer " + customer);
        } else if ( root.getClazz() == Contact.class ) {
            Optional<Contact> contact = customer.getContacts().stream().filter(c -> c.getId() == root.getId()).findFirst();
            if ( contact.isPresent() ) rootElement = contact.get();
            else throw new IllegalArgumentException("Contact is not on the customer " + customer);
        } else throw new IllegalArgumentException("Root instance is not supported. Root: " + root);

        if ( raw instanceof Address ) ((AddressStash)rootElement).getAddresses().add((Address)raw);
        else if ( raw instanceof AddressLabel ) ((Customer)rootElement).getAddressLabels().add((AddressLabel)raw);
        else if ( raw instanceof Company ) ((Customer)rootElement).getCompanies().add((Company)raw);
        else if ( raw instanceof Contact ) ((ContactStash)rootElement).getContacts().add((Contact)raw);
        else if ( raw instanceof MandatorMetadata ) ((Customer)rootElement).getMandatorMetadata().add((MandatorMetadata)raw);
        else if ( raw instanceof Communication ) ((CommunicationStash)rootElement).getCommunications().add((Communication)raw);
        else throw new IllegalArgumentException("Raw instance is not supported Raw: " + raw);

    }

    @Override
    public void update(Object t) {
        L.info("update Objekt: " + t);
        //Black Magic (Only one customer object is handled)
    }

    @Override
    public void delete(Root root, Object raw) {
        L.info("delete {} form {}", raw, root);

        Object rootElement = null;
        if ( root.getClazz() == Customer.class ) rootElement = customer;
        else if ( root.getClazz() == Company.class ) {
            Optional<Company> findAny = customer.getCompanies().stream().filter(c -> (Long)c.getId() == root.getId()).findAny();
            if ( findAny.isPresent() ) rootElement = findAny.get();
            else throw new IllegalArgumentException("Could not find company to delete from customer " + customer);
        } else if ( root.getClazz() == Contact.class ) {
            Optional<Contact> findAny = customer.getContacts().stream().filter(c -> (Long)c.getId() == root.getId()).findAny();
            if ( findAny.isPresent() ) rootElement = findAny.get();
            else throw new IllegalArgumentException("Could not find contact to delete from customer " + customer);
        } else throw new IllegalArgumentException("Root instance is not supported. Root: " + root);

        if ( raw instanceof Address ) ((AddressStash)rootElement).getAddresses().remove((Address)raw);
        else if ( raw instanceof Company ) ((Customer)rootElement).getCompanies().remove((Company)raw);
        else if ( raw instanceof Contact ) ((ContactStash)rootElement).getContacts().remove((Contact)raw);
        else if ( raw instanceof Communication ) ((CommunicationStash)rootElement).getCommunications().remove((Communication)raw);
        else throw new IllegalArgumentException("Raw instance is not supported. Raw: " + raw);
    }

}
