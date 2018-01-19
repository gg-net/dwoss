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

import eu.ggnet.dwoss.customer.CustomerAgent;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.entity.dto.SimpleCustomer;

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
    public void store(SimpleCustomer simpleCustomer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
