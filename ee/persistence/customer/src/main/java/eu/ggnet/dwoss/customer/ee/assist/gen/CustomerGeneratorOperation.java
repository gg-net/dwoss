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

import eu.ggnet.dwoss.mandator.api.value.CustomersBuilder;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;
import eu.ggnet.dwoss.mandator.api.value.DeleteCustomers;
import eu.ggnet.dwoss.mandator.api.value.ScrapCustomers;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.RepaymentCustomers;
import eu.ggnet.dwoss.common.api.values.ReceiptOperation;
import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.common.api.values.DocumentType;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.common.api.values.AddressType;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.priv.ConverterUtil;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomer;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.util.gen.GeneratedAddress;
import eu.ggnet.dwoss.util.gen.NameGenerator;

import static eu.ggnet.dwoss.common.api.values.AddressType.INVOICE;
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

    private final static EnumSet<CustomerFlag> ALLOWED_FLAG = EnumSet.of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER);

    private final NameGenerator GEN = new NameGenerator();

    private final CustomerGenerator CGEN = new CustomerGenerator();

    private final Random R = new Random();

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private Mandator mandator;

    @Inject
    private DefaultCustomerSalesdata defaults;

    public void makeSystemCustomersBasedOnMandator(DefaultCustomerSalesdata defaults, DeleteCustomers deleteCustomers,
                                                   ReceiptCustomers receiptCustomers, RepaymentCustomers repaymentCustomers,
                                                   ScrapCustomers scrapCustomers, SpecialSystemCustomers specialSystemCustomers) {
        Map<Long, String> systemCustomerName = deleteCustomers.getContractorCustomers().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getValue(), e -> "SystemCustomer Delete " + e.getKey()));
        systemCustomerName.putAll(repaymentCustomers.getContractorCustomers().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getValue(), e -> "SystemCustomer Repayment " + e.getKey())));
        systemCustomerName.putAll(scrapCustomers.getContractorCustomers().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getValue(), e -> "SystemCustomer Scrap " + e.getKey())));
        systemCustomerName.putAll(specialSystemCustomers.getSpecialCustomers().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> "SystemCustomer " + e.getValue())));
        systemCustomerName.putAll(receiptCustomers.getReceiptCustomers().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getValue(), e -> "SystemCustomer " + e.getKey().getContractor() + " - " + e.getKey().getOperation())));
        long max = systemCustomerName.keySet().stream().max(Comparator.comparingLong(v -> v)).orElse(0l);

        for (int i = 0; i < max; i++) {
            Customer c = CGEN.makeCustomer();
            c.setComment("Generatered Customer Number: " + i);
            em.persist(c);
            if ( systemCustomerName.containsKey(c.getId()) ) {
                c.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);
            }
        }
    }

    public ReceiptCustomers makeReceiptCustomers(TradeName... contractors) {
        if ( contractors == null || contractors.length == 0 ) return null;
        ReceiptCustomers.Builder receiptCustomersBuilder = ReceiptCustomers.builder();
        for (TradeName contractor : contractors) {
            L.info("try to create consumer and busines customer for TradeName " + contractor);
            for (ReceiptOperation operation : ReceiptOperation.valuesBackedByCustomer()) {
                L.info("ReceiptOperation " + operation);

                L.info("creating consumer customer for " + operation);
                Customer customer = CGEN.makeCustomer();
                customer.setComment("Generatered Receipt Customer: " + contractor + "," + operation);
                customer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);
                em.persist(customer);
                L.info("ReceiptCustomers (consumer) ID " + customer.getId());
                receiptCustomersBuilder.put(contractor, operation, customer.getId());

                L.info("creating business customer for " + operation);
                Customer businessCustomer = CGEN.makeCustomer();
                businessCustomer.setComment("Generatered Receipt Customer: " + contractor + "," + operation);
                businessCustomer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);

                Company company = CGEN.makeCompany();
                businessCustomer.getCompanies().add(company);
                businessCustomer.getContacts().clear();
                em.persist(businessCustomer);
                L.info("ReceiptCustomers (business) ID" + businessCustomer.getId());
                receiptCustomersBuilder.put(contractor, operation, businessCustomer.getId());
            }

        }
        return receiptCustomersBuilder.build();
    }

    public RepaymentCustomers makeRepaymentCustomers(TradeName... contractors) {
        if ( contractors == null || contractors.length == 0 ) return null;
        CustomersBuilder repaymentCustomersBuilder = new CustomersBuilder();
        for (TradeName contractor : contractors) {
            L.info("TradeName " + contractor);
            Customer customer = CGEN.makeCustomer();
            customer.setComment("Generatered Repayment Customer: " + contractor);
            customer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);
            em.persist(customer);
            repaymentCustomersBuilder.put(contractor, customer.getId());
        }
        L.info("for business Customer");
        for (TradeName contractor : contractors) {
            L.info("TradeName " + contractor);
            Customer businessCustomer = CGEN.makeCustomer();
            businessCustomer.setComment("Generatered Repayment Customer: " + contractor);
            businessCustomer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);

            Company company = CGEN.makeCompany();
            businessCustomer.getCompanies().add(company);
            businessCustomer.getContacts().clear();
            em.persist(businessCustomer);
            repaymentCustomersBuilder.put(contractor, businessCustomer.getId());
        }
        return repaymentCustomersBuilder.toRepayment();
    }

    public ScrapCustomers makeScrapCustomers(TradeName... contractors) {
        if ( contractors == null || contractors.length == 0 ) return null;
        CustomersBuilder scrapCustomersBuilder = new CustomersBuilder();

        for (TradeName contractor : contractors) {
            L.info("TradeName " + contractor);
            Customer customer = CGEN.makeCustomer();
            customer.setComment("Generatered Scrap Customer: " + contractor);
            customer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);
            em.persist(customer);
            scrapCustomersBuilder.put(contractor, customer.getId());
        }
        L.info("for business Customer");
        for (TradeName contractor : contractors) {
            L.info("TradeName " + contractor);
            Customer businessCustomer = CGEN.makeCustomer();
            businessCustomer.setComment("Generatered Scrap Customer: " + contractor);
            businessCustomer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);
            Company company = CGEN.makeCompany();
            businessCustomer.getCompanies().add(company);
            businessCustomer.getContacts().clear();
            em.persist(businessCustomer);
            scrapCustomersBuilder.put(contractor, businessCustomer.getId());
        }
        return scrapCustomersBuilder.toScrap();
    }

    public DeleteCustomers makeDeleteCustomers(TradeName... contractors) {
        if ( contractors == null || contractors.length == 0 ) return null;
        CustomersBuilder deleteCustomersBuilder = new CustomersBuilder();
        for (TradeName contractor : contractors) {
            L.info("TradeName " + contractor);
            Customer customer = CGEN.makeCustomer();
            customer.setComment("Generatered Delete Customer: " + contractor);
            customer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);
            em.persist(customer);
            deleteCustomersBuilder.put(contractor, customer.getId());
        }
        L.info("for business Customer");
        for (TradeName contractor : contractors) {
            L.info("TradeName " + contractor);
            Customer businessCustomer = CGEN.makeCustomer();
            businessCustomer.setComment("Generatered Delete Customer: " + contractor);
            businessCustomer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);

            Company company = CGEN.makeCompany();
            businessCustomer.getCompanies().add(company);
            businessCustomer.getContacts().clear();
            em.persist(businessCustomer);
            deleteCustomersBuilder.put(contractor, businessCustomer.getId());
        }
        return deleteCustomersBuilder.toDelete();
    }

    public SpecialSystemCustomers makeSpecialCustomers(DocumentType... types) {
        Map<Long, DocumentType> specialCustomers = new HashMap<>();
        if ( types == null || types.length == 0 ) return new SpecialSystemCustomers(specialCustomers);
        for (DocumentType type : types) {
            L.info("DocumentType " + type);
            Customer customer = CGEN.makeCustomer();
            customer.setComment("Generatered " + type.getName() + "");
            customer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);
            em.persist(customer);
            specialCustomers.put(customer.getId(), type);
        }
        L.info("for business Customer");
        for (DocumentType type : types) {
            L.info("DocumentType " + type);
            Customer businessCustomer = CGEN.makeCustomer();
            businessCustomer.setComment("Generatered " + type.getName() + "");
            businessCustomer.getFlags().add(CustomerFlag.SYSTEM_CUSTOMER);

            Company company = CGEN.makeCompany();
            businessCustomer.getCompanies().add(company);
            businessCustomer.getContacts().clear();
            em.persist(businessCustomer);
            specialCustomers.put(businessCustomer.getId(), type);
        }
        return new SpecialSystemCustomers(specialCustomers);
    }

    public void makeSystemCustomers(TradeName... contractors) {
        L.info("Start makeReceiptCustomers");
        makeReceiptCustomers(contractors);

        L.info("Start makeDeleteCustomers");
        makeDeleteCustomers(contractors);

        L.info("Start makeScrapCustomers");
        makeScrapCustomers(contractors);

        L.info("Start makeRepaymentCustomers");
        makeRepaymentCustomers(contractors);
    }

    public long makeCustomer() {
        return makeCustomers(1).get(0);
    }

    public List<Long> makeCustomers(int amount) {
        SubMonitor m = monitorFactory.newSubMonitor("Generiere " + amount + " Kunden", amount);
        L.info("Generating {} customers", amount);
        m.start();
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            // Customer c = CGEN.makeOldCustomer(mandator.getMatchCode(), defaults);
            Customer customer = CGEN.makeCustomer();
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

    @Deprecated
    public void scrambleAddress(long customerId, AddressType type) {
        Customer customer = em.find(Customer.class, customerId);
        OldCustomer sc = ConverterUtil.convert(customer, mandator.getMatchCode(), defaults);
        GeneratedAddress newAddress = GEN.makeAddress();
        if ( type == INVOICE ) {
            sc.setREAdresse(newAddress.getStreet());
            sc.setREOrt(newAddress.getTown());
            sc.setREPlz(newAddress.getPostalCode());
        } else {
            sc.setLIAdresse(newAddress.getStreet());
            sc.setLIOrt(newAddress.getTown());
            sc.setLIPlz(newAddress.getPostalCode());
        }
        ConverterUtil.mergeFromOld(sc, customer, mandator.getMatchCode(), defaults);
    }

}
