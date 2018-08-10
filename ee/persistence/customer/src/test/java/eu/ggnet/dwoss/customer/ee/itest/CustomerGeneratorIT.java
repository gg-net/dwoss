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
    private CustomerGeneratorOperation genOp;

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

    private TradeName contractors = TradeName.LENOVO;

    private DocumentType types = DocumentType.ORDER;

    @Test
    public void make200Customers() {
        final int AMOUNT = 200;

        List<Long> ids = genOp.makeCustomers(AMOUNT);
        assertThat(ids).as("Generated Ids").isNotNull().isNotEmpty().hasSize(AMOUNT);

        assertThat(eao.count()).as("less Customer get found").isEqualTo(AMOUNT);
    }

    @Test
    public void testMakeReceiptCustomers() {
        //ReceiptCustomers are Customer where ReceiptOperation are backed By Customer
        genOp.makeReceiptCustomers(contractors);

        assertThat(eao.findAll().size()).as("get not amount Customer").isEqualTo(6);
    }

    @Test
    public void testMakeRepaymentCustomers() {
        genOp.makeRepaymentCustomers(contractors);
        assertThat(eao.findAll().size()).as("get not amount Customer").isEqualTo(2);
    }

    @Test
    public void testMakeScrapCustomers() {
        genOp.makeScrapCustomers(contractors);
        assertThat(eao.findAll().size()).as("get not amount Customer").isEqualTo(2);
    }

    @Test
    public void testMakeDeleteCustomers() {
        genOp.makeDeleteCustomers(contractors);
        assertThat(eao.findAll().size()).as("get not amount Customer").isEqualTo(2);
    }

    @Test
    public void testMakeSpecialCustomers() {
        genOp.makeSpecialCustomers(types);
        assertThat(eao.findAll().size()).as("get not amount Customer").isEqualTo(2);
    }

    @Test
    public void testScrambleAddress() {
        genOp.makeCustomers(25);
        Customer customerFormTheDb = agent.findByIdEager(Customer.class, 1l);
        assertThat(customerFormTheDb.isValid()).as("not a valid Customer").isTrue();

        genOp.scrambleAddress(1, AddressType.INVOICE);
        Customer customerFormTheDbAfterScrambleAddress = agent.findByIdEager(Customer.class, 1l);
        assertThat(customerFormTheDbAfterScrambleAddress.isValid()).as("not a valid Customer").isTrue();
    }
}
