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

    @Override
    public <T> long count(Class<T> entityClass) {
        if ( entityClass.equals(Customer.class) ) {
            return AMOUNT;
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

        return list;
    }

    @Override
    public List<Customer> search(String search, Set<SearchField> customerFields, int start, int limit) {
        List<Customer> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            list.add(CGEN.makeCustomer());
        }
        try {
            Thread.sleep(1000L, SLOW);
        } catch (InterruptedException ex) {

        }

        //debug
        System.out.println("--------------------------------------");
        System.out.println("Debug from CustomerAgentStub");
        if ( customerFields != null ) {
            System.out.println("Set größe. " + customerFields.size());
        }
        System.out.println("Listgröße: " + list.size());
        list.forEach((customer) -> {
            customer.getCompanies().forEach((company) -> {
                System.out.println("Company Name: " + company.getName());
            });
        });
        System.out.println("--------------------------------------");

        return list;
    }

    @Override
    public int countSearch(String search, Set<SearchField> customerFields) {
        return 50;
    }

    @Override
    public Customer store(SimpleCustomer simpleCustomer) {
        System.out.println("Input form Stubs: " + simpleCustomer.toString());

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
        cont.add(a);

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
        if ( comm.getViolationMessages() == null ) {
            cont.add(comm);
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
            comp.add(a);
            comp.add(cont);
            //build AddressLabel
            al = new AddressLabel(comp, comp.getContacts().get(0), a, AddressType.INVOICE);

            c.add(comp);

        } else {
            //Contains only one Contact or one Company.
            c.getCompanies().clear();
            c.add(cont);
            al = new AddressLabel(null, cont, a, AddressType.INVOICE);
        }
        c.getAddressLabels().clear();
        c.getAddressLabels().add(al);

        MandatorMetadata mandatorMetadata = new MandatorMetadata();
        c.add(mandatorMetadata);

        System.out.println("Output form Stubs: " + c.toString());
        System.out.println("ViolationMessage " + c.getViolationMessage());

        return c;
    }

}
