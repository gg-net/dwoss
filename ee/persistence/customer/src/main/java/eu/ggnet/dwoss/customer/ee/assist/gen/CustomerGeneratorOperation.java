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
package eu.ggnet.dwoss.customer.ee.assist.gen;

import java.util.*;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.common.api.values.*;
import eu.ggnet.dwoss.common.ee.log.AutoLogger;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

/**
 * Customer Generator.
 * <p>
 * @author oliver.guenther
 */
@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class CustomerGeneratorOperation {

    private final Logger L = LoggerFactory.getLogger(CustomerGeneratorOperation.class);

    private final CustomerGenerator CGEN = new CustomerGenerator();

    @Inject
    @Customers 
    private EntityManager em;

    @Inject
    private MonitorFactory monitorFactory;

    /**
     * Generates Customers which to enable the receipt.
     *
     * @param contractors tradenames as contractors which are these customers for.
     * @return the generateded contaioner of contractors with customer ids.
     */
    public ReceiptCustomers makeReceiptCustomers(TradeName... contractors) {
        if ( contractors == null || contractors.length == 0 ) return null;
        ReceiptCustomers.Builder receiptCustomersBuilder = ReceiptCustomers.builder();
        for (TradeName contractor : contractors) {
            L.info("try to create consumer and busines customer for TradeName " + contractor);
            for (ReceiptOperation operation : ReceiptOperation.valuesBackedByCustomer()) {
                L.info("creating customer for " + operation);
                Customer customer = CGEN.makeSimpleBussinesCustomer();
                customer.getCompanies().get(0).setName("Receipt Customer " + contractor);
                customer.setComment("Generatered Receipt Customer: " + contractor + "," + operation);
                customer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);
                em.persist(customer);
                L.info("ReceiptCustomers (consumer) ID " + customer.getId());
                receiptCustomersBuilder.put(contractor, operation, customer.getId());
            }

        }
        return receiptCustomersBuilder.build();
    }

    /**
     * Generates Customers which to enable the repayment operation.
     *
     * @param contractors tradenames as contractors which are these customers for.
     * @return the generateded contaioner of contractors with customer ids.
     */
    public RepaymentCustomers makeRepaymentCustomers(TradeName... contractors) {
        if ( contractors == null || contractors.length == 0 ) return null;
        CustomersBuilder repaymentCustomersBuilder = new CustomersBuilder();
        for (TradeName contractor : contractors) {
            L.info("TradeName " + contractor);
            Customer customer = CGEN.makeSimpleBussinesCustomer();
            customer.getCompanies().get(0).setName("Repayment Customer " + contractor);
            customer.setComment("Generatered Repayment Customer: " + contractor);
            customer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);
            em.persist(customer);
            repaymentCustomersBuilder.put(contractor, customer.getId());
        }
        return repaymentCustomersBuilder.toRepayment();
    }

    /**
     * Generates Customers which to enable the receipt scrap operation.
     *
     * @param contractors tradenames as contractors which are these customers for.
     * @return the generateded contaioner of contractors with customer ids.
     */
    public ScrapCustomers makeScrapCustomers(TradeName... contractors) {
        if ( contractors == null || contractors.length == 0 ) return null;
        CustomersBuilder scrapCustomersBuilder = new CustomersBuilder();

        for (TradeName contractor : contractors) {
            L.info("TradeName " + contractor);
            Customer customer = CGEN.makeSimpleBussinesCustomer();
            customer.getCompanies().get(0).setName("Scrap Customer " + contractor);
            customer.setComment("Generatered Scrap Customer: " + contractor);
            customer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);
            em.persist(customer);
            scrapCustomersBuilder.put(contractor, customer.getId());
        }
        return scrapCustomersBuilder.toScrap();
    }

    /**
     * Generates Customers which to enable the receipt delete operation.
     *
     * @param contractors tradenames as contractors which are these customers for.
     * @return the generateded contaioner of contractors with customer ids.
     */
    public DeleteCustomers makeDeleteCustomers(TradeName... contractors) {
        if ( contractors == null || contractors.length == 0 ) return null;
        CustomersBuilder deleteCustomersBuilder = new CustomersBuilder();
        for (TradeName contractor : contractors) {
            L.info("TradeName " + contractor);
            Customer customer = CGEN.makeSimpleBussinesCustomer();
            customer.getCompanies().get(0).setName("Delete Customer " + contractor);
            customer.setComment("Generatered Delete Customer: " + contractor);
            customer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);
            em.persist(customer);
            deleteCustomersBuilder.put(contractor, customer.getId());
        }
        return deleteCustomersBuilder.toDelete();
    }

    /**
     * Generates Customers which of type system customer.
     *
     * @param types the special document types.
     * @return the generateded contaioner of contractors with customer ids.
     */
    public SpecialSystemCustomers makeSpecialCustomers(DocumentType... types) {
        Map<Long, DocumentType> specialCustomers = new HashMap<>();
        if ( types == null || types.length == 0 ) return new SpecialSystemCustomers(specialCustomers);
        for (DocumentType type : types) {
            L.info("DocumentType " + type);
            Customer customer = CGEN.makeSimpleBussinesCustomer();
            customer.getCompanies().get(0).setName(type.getName() + " Special Customer");
            customer.setComment("Generatered " + type.getName() + "");
            customer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);
            em.persist(customer);
            specialCustomers.put(customer.getId(), type);
        }
        return new SpecialSystemCustomers(specialCustomers);
    }

    /**
     * Generates one randome customer.
     *
     * @return the id of the generated customer.
     */
    public long makeCustomer() {
        return makeCustomer(CustomerGenerator.Assure.builder().build());
    }

    /**
     * Generates and persitst a new customer assureing the supplied rules.
     *
     * @param assure the rules
     * @return the id of the customer.
     */
    @AutoLogger
    public long makeCustomer(CustomerGenerator.Assure assure) {
        Customer customer = CGEN.makeCustomer(assure);
        em.persist(customer);
        em.flush();
        return customer.getId();
    }

    /**
     * Generates and persists a predefined Amount of random Customers.
     *
     * @param amount the amount
     * @return the generated ids.
     */
    public List<Long> makeCustomers(int amount) {
        return makeCustomers(amount, CustomerGenerator.Assure.builder().build());
    }

    /**
     * Generates and persists a predefined Amount of Customers, assuring the supplied conditions.
     *
     * @param amount the amount
     * @param assure the conditions
     * @return the generated ids.
     */
    public List<Long> makeCustomers(int amount, CustomerGenerator.Assure assure) {
        SubMonitor m = monitorFactory.newSubMonitor("Generiere " + amount + " Kunden", amount);
        L.info("Generating {} customers", amount);
        m.start();
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Customer customer = CGEN.makeCustomer(assure);
            if ( customer.getFlags().contains(CustomerFlag.SYSTEM_CUSTOMER) )
                L.error("Generated Customer with flag SystemCustomer, which defentifly should not be. {}", customer);
            em.persist(customer);
            ids.add(customer.getId());
        }
        em.flush(); // Solves some batching issues.
        em.clear();
        m.finish();
        return ids;
    }

    /**
     * Generates a new address on the customer and modifies the Addresslabel of the supplied type.
     *
     * @param customerId the customerid to scramble
     * @param type       the type to change or add
     */
    public void scrambleAddress(long customerId, AddressType type) {
        Customer customer = Objects.requireNonNull(em.find(Customer.class, customerId), "No Customer found of id " + customerId);
        Address newAddress = CGEN.makeAddress();
        Contact newContact = CGEN.makeContact();

        if ( customer.isBusiness() ) {
            customer.getCompanies().get(0).getAddresses().add(newAddress);
            customer.getCompanies().get(0).getContacts().add(newContact);
        } else {
            customer.getContacts().get(0).getAddresses().add(newAddress);
            customer.getContacts().add(newContact);
        }

        AddressLabel al = customer.getAddressLabels().stream()
                .filter(a -> a.getType() == type)
                .findFirst()
                .map((AddressLabel t) -> {
                    t.setContact(newContact);
                    t.setAddress(newAddress);
                    return t;
                })
                .orElseGet(() -> {
                    AddressLabel t = new AddressLabel(newContact, newAddress, type);
                    customer.getAddressLabels().add(t);
                    return t;
                });
        L.info("scrambleAddress(customerId={},addressType={}) generated {}", customerId, type, al);
        em.persist(newAddress);
        em.persist(newContact);
    }

}
