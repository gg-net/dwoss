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
package eu.ggnet.dwoss.customer.ee.itest;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerDeleteUtils;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;

import static eu.ggnet.dwoss.core.common.values.PaymentCondition.CUSTOMER;
import static eu.ggnet.dwoss.core.common.values.PaymentCondition.EMPLOYEE;
import static eu.ggnet.dwoss.core.common.values.PaymentMethod.ADVANCE_PAYMENT;
import static eu.ggnet.dwoss.core.common.values.PaymentMethod.DIRECT_DEBIT;
import static eu.ggnet.dwoss.core.common.values.ShippingCondition.FIVE;
import static eu.ggnet.dwoss.core.common.values.ShippingCondition.FIVE_EIGHTY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the differnet ways of handling the {@link MandatorMetadata}.
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class CustomerAgentNormalizedStoreMadatorMetadataIT extends ArquillianProjectArchive {

    @EJB
    private CustomerAgent agent;

    @EJB
    private CustomerService apiService;

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    UserTransaction utx;

    @Inject
    private DefaultCustomerSalesdata dcs;

    @Inject
    private Mandator mandator;

    private Customer customer;

    @Before
    public void clearDatabaseAndCreateOneSimpleCustomer() throws Exception {
        utx.begin();
        em.joinTransaction();
        customer = CustomerGenerator.makeSimpleConsumerCustomer();
        em.persist(customer);
        utx.commit();
    }
        
    @After
    public void teardown() throws Exception {
        utx.begin();
        em.joinTransaction();
        CustomerDeleteUtils.deleteAll(em);
        assertThat(CustomerDeleteUtils.validateEmpty(em)).isNull();
        utx.commit();
    }

    /**
     * Generates a MandtorMetadata instance, which is total different from the defaults.
     *
     * @return a mandator metadata instance.
     */
    private MandatorMetadata makeTotalDifferentMetadata() {
        MandatorMetadata mm = new MandatorMetadata(mandator.matchCode());
        mm.getAllowedSalesChannels().add(SalesChannel.CUSTOMER);
        if ( mm.getAllowedSalesChannels().equals(dcs.allowedSalesChannels()) ) mm.getAllowedSalesChannels().add(SalesChannel.RETAILER);

        mm.setShippingCondition(dcs.shippingCondition() != FIVE ? FIVE : FIVE_EIGHTY);
        mm.setPaymentCondition(dcs.paymentCondition() != CUSTOMER ? CUSTOMER : EMPLOYEE);
        mm.setPaymentMethod(dcs.paymentMethod() != DIRECT_DEBIT ? DIRECT_DEBIT : ADVANCE_PAYMENT);
        return mm;
    }

    @Test
    public void addPartialNewMetadata() {
        MandatorMetadata mm = makeTotalDifferentMetadata();
        // These two elements should be nullified on normalizedstore
        mm.setPaymentCondition(dcs.paymentCondition());
        mm.setPaymentMethod(dcs.paymentMethod());

        agent.normalizedStoreMandatorMetadata(customer.getId(), mm);

        Customer c0 = agent.findByIdEager(Customer.class, customer.getId());
        MandatorMetadata c0mm = c0.getMandatorMetadata(mandator.matchCode());
        assertThat(c0mm).as("Customer should have the partial mandator metadata, payment method and condition should be null")
                .isNotNull()
                .returns(mm.getAllowedSalesChannels(), MandatorMetadata::getAllowedSalesChannels)
                .returns(null, MandatorMetadata::getPaymentCondition)
                .returns(null, MandatorMetadata::getPaymentMethod)
                .returns(mm.getShippingCondition(), MandatorMetadata::getShippingCondition);

        // Doublecheck. All api service implementation should work. No Exceptions should be thrown.
        apiService.asCustomerMetaData(customer.getId());
        apiService.asUiCustomer(customer.getId());
    }

    @Test
    public void addTotalNewThenModifyToPartialThenAutoremoveMetadata() {
        MandatorMetadata mm = makeTotalDifferentMetadata();
        agent.normalizedStoreMandatorMetadata(customer.getId(), mm);

        Customer c0 = agent.findByIdEager(Customer.class, customer.getId());
        MandatorMetadata c0mm = c0.getMandatorMetadata(mandator.matchCode());
        assertThat(c0mm).as("Customer should have the full mandator metadata, as it differs completly from the defaults")
                .isNotNull()
                .returns(mm.getAllowedSalesChannels(), MandatorMetadata::getAllowedSalesChannels)
                .returns(mm.getPaymentCondition(), MandatorMetadata::getPaymentCondition)
                .returns(mm.getPaymentMethod(), MandatorMetadata::getPaymentMethod)
                .returns(mm.getShippingCondition(), MandatorMetadata::getShippingCondition);

        // Modify payment codition and method to defaults
        c0mm.setPaymentCondition(dcs.paymentCondition());
        c0mm.setPaymentMethod(dcs.paymentMethod());

        agent.normalizedStoreMandatorMetadata(customer.getId(), c0mm);

        c0 = agent.findByIdEager(Customer.class, customer.getId());
        c0mm = c0.getMandatorMetadata(mandator.matchCode());
        assertThat(c0mm).as("Customer should have the partial mandator metadata, payment method and condition should be null")
                .isNotNull()
                .returns(mm.getAllowedSalesChannels(), MandatorMetadata::getAllowedSalesChannels)
                .returns(null, MandatorMetadata::getPaymentCondition)
                .returns(null, MandatorMetadata::getPaymentMethod)
                .returns(mm.getShippingCondition(), MandatorMetadata::getShippingCondition);

        // Doublecheck. All api service implementation should work. No Exceptions should be thrown.
        apiService.asCustomerMetaData(customer.getId());
        apiService.asUiCustomer(customer.getId());

        // Now set everything to defaults, which results in auto remove
        c0mm.getAllowedSalesChannels().clear();
        c0mm.getAllowedSalesChannels().addAll(dcs.allowedSalesChannels());
        c0mm.setShippingCondition(dcs.shippingCondition());

        agent.normalizedStoreMandatorMetadata(customer.getId(), c0mm);

        c0 = agent.findByIdEager(Customer.class, customer.getId());
        assertThat(c0.getMandatorMetadata(mandator.matchCode())).as("Defaults were stored, should be removed").isNull();

        // Doublecheck. All api service implementation should work. No Exceptions should be thrown.
        apiService.asCustomerMetaData(customer.getId());
        apiService.asUiCustomer(customer.getId());

    }

    @Test
    public void addTotalNewOnlyDefaults() {
        MandatorMetadata mm = new MandatorMetadata(mandator.matchCode());
        mm.getAllowedSalesChannels().addAll(dcs.allowedSalesChannels());
        mm.setShippingCondition(dcs.shippingCondition());
        mm.setPaymentCondition(dcs.paymentCondition());
        mm.setPaymentMethod(dcs.paymentMethod());

        agent.normalizedStoreMandatorMetadata(customer.getId(), mm);

        Customer c0 = agent.findByIdEager(Customer.class, customer.getId());
        assertThat(c0.getMandatorMetadata(mandator.matchCode())).as("Defaults were stored, should not be persisted").isNull();

        // Doublecheck. All api service implementation should work. No Exceptions should be thrown.
        apiService.asCustomerMetaData(customer.getId());
        apiService.asUiCustomer(customer.getId());

    }
}
