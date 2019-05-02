package eu.ggnet.dwoss.customer.ee.itest;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.common.api.values.*;
import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.customer.ee.itest.support.Utils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class CustomerGeneratorIT extends ArquillianProjectArchive {

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    UserTransaction utx;

    @Inject
    private CustomerGeneratorOperation cgo;

    @Inject
    private CustomerEao eao;

    @EJB
    private CustomerAgent agent;

    @After
    public void teardown() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    private final TradeName CONTRACTOR = TradeName.LENOVO;

    private final DocumentType DOCUMENT_TYPE = DocumentType.ORDER;

    @Test
    public void make200Customers() {
        final int AMOUNT = 200;

        List<Long> ids = cgo.makeCustomers(AMOUNT);
        assertThat(ids).as("Generated Ids").isNotNull().isNotEmpty().hasSize(AMOUNT);

        assertThat(eao.count()).as("less Customer get found").isEqualTo(AMOUNT);
    }

    @Test
    public void makeCustomerWithAssure() {
        for (int i = 0; i < 20; i++) {
            long cid = cgo.makeCustomer();
            assertThat(agent.findByIdEager(Customer.class,cid).getMandatorMetadata()).as("Customer Metadata must be empty").isEmpty();
        }
    }

    @Test
    public void testMakeReceiptCustomers() {
        //ReceiptCustomers are Customer where ReceiptOperation are backed By Customer
        cgo.makeReceiptCustomers(CONTRACTOR);        
        assertThat(eao.findAll().size()).as("ReceiptCustomers for " + CONTRACTOR).isEqualTo(ReceiptOperation.valuesBackedByCustomer().size());
    }

    @Test
    public void testMakeRepaymentCustomers() {
        cgo.makeRepaymentCustomers(CONTRACTOR);
        assertThat(eao.findAll().size()).as("RepaymentCustomers").isEqualTo(1);
    }

    @Test
    public void testMakeScrapCustomers() {
        cgo.makeScrapCustomers(CONTRACTOR);
        assertThat(eao.findAll().size()).as("ScrapCustomers").isEqualTo(1);
    }

    @Test
    public void testMakeDeleteCustomers() {
        cgo.makeDeleteCustomers(CONTRACTOR);
        assertThat(eao.findAll().size()).as("DeleteCustomers").isEqualTo(1);
    }

    @Test
    public void testMakeSpecialCustomers() {
        cgo.makeSpecialCustomers(DOCUMENT_TYPE);
        assertThat(eao.findAll().size()).as("SpecialCustomer for " + DOCUMENT_TYPE).isEqualTo(1);
    }

    @Test
    public void testScrambleAddress() {
        cgo.makeCustomers(25);
        Customer customerFormTheDb = agent.findByIdEager(Customer.class, 1l);
        assertThat(customerFormTheDb.isValid()).as("not a valid Customer").isTrue();

        cgo.scrambleAddress(1, AddressType.INVOICE);
        Customer customerFormTheDbAfterScrambleAddress = agent.findByIdEager(Customer.class, 1l);
        assertThat(customerFormTheDbAfterScrambleAddress.isValid()).as("not a valid Customer").isTrue();
    }
}
