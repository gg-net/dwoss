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
import java.util.stream.Stream;

import javax.persistence.LockModeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.ui.AlertType;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerAgentStub implements CustomerAgent {

    private final int AMOUNT = 200;

    private final int SLOW = 40;

    private final Logger L = LoggerFactory.getLogger(CustomerAgentStub.class);

    private final CustomerGenerator CGEN = new CustomerGenerator();

    private final List<Customer> CUSTOMERS;

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
    public <T> T findByIdEager(Class< T> entityClass, Object id) {
        return (T)CGEN.makeCustomer();

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
    public List<Customer> search(String search, Set<SearchField> customerFields) {
        List<Customer> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            list.add(CGEN.makeCustomer());
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
    public List<Customer> search(String search, Set<SearchField> customerFields, int start, int limit) {
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
            result.clear();
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
            result.stream().filter((customer) -> (customer.getId() == Integer.parseInt(search))).forEachOrdered((customer) -> {
                tempList.add(customer);
            });
            result.clear();
            result.addAll(tempList);
        }
        if ( customerFields.contains(SearchField.COMPANY) ) {
            result.forEach((customer) -> {
                customer.getCompanies().stream().filter((com) -> (com.getName().contains(search))).forEachOrdered((_item) -> {
                    tempList.add(customer);
                });
            });
            result.clear();
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
                result.clear();
                result.addAll(tempList);

            }
        }

        L.info("Returning {}", result);
        return result;
    }

    @Override
    public int countSearch(String search, Set<SearchField> customerFields) {
        return CUSTOMERS.size();
    }

    @Override
    public Customer store(SimpleCustomer simpleCustomer) {
        L.info("Input form Stubs: " + simpleCustomer.toString());

        //convert the SimpleCustomer to a Customer
        boolean bussines = false;
        if ( !simpleCustomer.getCompanyName().trim().isEmpty() ) {
            bussines = true;
        }

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
        Communication comm = new Communication();
        if ( simpleCustomer.getEmail() != null ) {
            comm.setType(Type.EMAIL);
            comm.setIdentifier(simpleCustomer.getEmail());
        }
        if ( simpleCustomer.getLandlinePhone() != null ) {
            comm.setType(Type.PHONE);
            comm.setIdentifier(simpleCustomer.getLandlinePhone());
        }
        if ( simpleCustomer.getMobilePhone() != null ) {
            comm.setType(Type.MOBILE);
            comm.setIdentifier(simpleCustomer.getMobilePhone());
        }

        //check if the Communication is valid with the right pattern
        if ( comm.getViolationMessage() == null ) {
            cont.getCommunications().add(comm);
        } else {
            Ui.build().alert().message("CustomerAgentStub - Eingabefehler in einem der Kommunikationswege. Bitte überprüfen Sie Diese.").show(AlertType.WARNING);
        }

        AddressLabel al = null;
        if ( bussines ) {
            //Either a Contact or a Company are set.
            //Contains only one Contact or one Company.
            c.getContacts().clear();

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
            //Contains only one Contact or one Company.
            c.getCompanies().clear();
            c.getContacts().add(cont);
            al = new AddressLabel(null, cont, a, AddressType.INVOICE);
        }
        c.getAddressLabels().clear();
        c.getAddressLabels().add(al);

        MandatorMetadata mandatorMetadata = new MandatorMetadata();
        c.getMandatorMetadata().add(mandatorMetadata);

        L.info("Output form Stubs: " + c.toString());
        L.info("ViolationMessage " + c.getViolationMessage());

        return c;
    }

}
