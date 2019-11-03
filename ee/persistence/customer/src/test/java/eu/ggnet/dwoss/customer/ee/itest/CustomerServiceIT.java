package eu.ggnet.dwoss.customer.ee.itest;

import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.customer.ee.itest.support.Utils;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class CustomerServiceIT extends ArquillianProjectArchive {

    private final static Logger L = LoggerFactory.getLogger(CustomerServiceIT.class);

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Inject
    private CustomerGeneratorOperation cgo;

    @EJB
    private CustomerService cs;

    private List<Long> customerIds = Collections.emptyList();

    public void setup() {
        customerIds = cgo.makeCustomers(20);
    }

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
    public void touchEveryMethod() {
        String h = "touchEveryMethod():";
        for (Long cid : customerIds) {
            L.info("{}CustomerId:{}", h, cid);
            L.info(h + cs.asCustomerMetaData(cid));
            L.info(h + cs.asUiCustomer(cid));
            L.info(h + "Comment:" + cs.findComment(cid));
            L.info(h + cs.asHtmlHighDetailed(cid).substring(0, 20) + "...");
        }
    }
}
