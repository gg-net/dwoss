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
package eu.ggnet.dwoss.customer.assist.gen;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.customer.priv.ConverterUtil;
import eu.ggnet.dwoss.customer.priv.OldCustomer;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.gen.GeneratedAddress;
import eu.ggnet.dwoss.util.gen.NameGenerator;

import static eu.ggnet.dwoss.rules.AddressType.INVOICE;
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
    private EntityManager cem;

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
            Customer c = new Customer();
            c.setComment("Generatered Customer Number: " + i);
            cem.persist(c);
            if ( systemCustomerName.containsKey(c.getId()) ) {
                c.add(Company.builder().name(systemCustomerName.get(c.getId())).prefered(true).build());
                c.add(CustomerFlag.SYSTEM_CUSTOMER);
            }
        }
    }

    public ReceiptCustomers makeReceiptCustomers(TradeName... contractors) {
        if ( contractors == null || contractors.length == 0 ) return null;
        ReceiptCustomers.Builder receiptCustomersBuilder = ReceiptCustomers.builder();
        for (TradeName contractor : contractors) {
            for (ReceiptOperation operation : ReceiptOperation.valuesBackedByCustomer()) {
                Customer c = new Customer();
                c.setComment("Generatered Receipt Customer: " + contractor + "," + operation);
                c.add(CustomerFlag.SYSTEM_CUSTOMER);
                c.add(Company.builder().name(operation + " - " + contractor).prefered(true).build());
                c.add(Contact.builder().firstName(operation.getNote()).lastName(contractor.getName()).prefered(true).build());
                cem.persist(c);
                receiptCustomersBuilder.put(contractor, operation, c.getId());
            }
        }
        return receiptCustomersBuilder.build();
    }

    public RepaymentCustomers makeRepaymentCustomers(TradeName... contractors) {
        if ( contractors == null || contractors.length == 0 ) return null;
        CustomersBuilder repaymentCustomersBuilder = new CustomersBuilder();
        for (TradeName contractor : contractors) {
            Customer c = new Customer();
            c.setComment("Generatered Repayment Customer: " + contractor);
            c.add(CustomerFlag.SYSTEM_CUSTOMER);
            c.add(Company.builder().name("Repayment - " + contractor).prefered(true).build());
            c.add(Contact.builder().firstName("Repayment").lastName(contractor.getName()).prefered(true).build());
            cem.persist(c);
            repaymentCustomersBuilder.put(contractor, c.getId());
        }
        return repaymentCustomersBuilder.toRepayment();
    }

    public ScrapCustomers makeScrapCustomers(TradeName... contractors) {
        if ( contractors == null || contractors.length == 0 ) return null;
        CustomersBuilder scrapCustomersBuilder = new CustomersBuilder();

        for (TradeName contractor : contractors) {
            Customer c = new Customer();
            c.setComment("Generatered Scrap Customer: " + contractor);
            c.add(CustomerFlag.SYSTEM_CUSTOMER);
            c.add(Company.builder().name("Scrap - " + contractor).prefered(true).build());
            c.add(Contact.builder().firstName("Scrap").lastName(contractor.getName()).prefered(true).build());
            cem.persist(c);
            scrapCustomersBuilder.put(contractor, c.getId());
        }
        return scrapCustomersBuilder.toScrap();
    }

    public DeleteCustomers makeDeleteCustomers(TradeName... contractors) {
        if ( contractors == null || contractors.length == 0 ) return null;
        CustomersBuilder deleteCustomersBuilder = new CustomersBuilder();
        for (TradeName contractor : contractors) {
            Customer c = new Customer();
            c.setComment("Generatered Delete Customer: " + contractor);
            c.add(CustomerFlag.SYSTEM_CUSTOMER);
            c.add(Company.builder().name("Delete - " + contractor).prefered(true).build());
            c.add(Contact.builder().firstName("Delete").lastName(contractor.getName()).prefered(true).build());
            cem.persist(c);
            deleteCustomersBuilder.put(contractor, c.getId());
        }
        return deleteCustomersBuilder.toDelete();
    }

    public SpecialSystemCustomers makeSpecialCustomers(DocumentType... types) {
        Map<Long, DocumentType> specialCustomers = new HashMap<>();
        if ( types == null || types.length == 0 ) return new SpecialSystemCustomers(specialCustomers);
        for (DocumentType type : types) {
            Customer c = new Customer();
            c.setComment("Generatered " + type.getName() + "");
            c.add(CustomerFlag.SYSTEM_CUSTOMER);
            c.add(Company.builder().name(type.getName()).prefered(true).build());
            c.add(Contact.builder().firstName("Special").lastName(type.getName()).prefered(true).build());
            cem.persist(c);
            specialCustomers.put(c.getId(), type);
        }
        return new SpecialSystemCustomers(specialCustomers);
    }

    public void makeSystemCustomers(TradeName... contractors) {
        makeReceiptCustomers(contractors);
        makeDeleteCustomers(contractors);
        makeScrapCustomers(contractors);
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
            Customer c = CGEN.makeOldCustomer(mandator.getMatchCode(), defaults);
            if ( L.isDebugEnabled() ) L.debug("Persisting {}", c.toMultiLine());
            cem.persist(c);
            ids.add(c.getId());
        }
        cem.flush(); // Solves some batching issues.
        cem.clear();
        m.finish();
        return ids;
    }

    public void scrambleAddress(long customerId, AddressType type) {
        Customer c = cem.find(Customer.class, customerId);
        OldCustomer sc = ConverterUtil.convert(c, mandator.getMatchCode(), defaults);
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
        ConverterUtil.mergeFromOld(sc, c, mandator.getMatchCode(), defaults);
    }

}
