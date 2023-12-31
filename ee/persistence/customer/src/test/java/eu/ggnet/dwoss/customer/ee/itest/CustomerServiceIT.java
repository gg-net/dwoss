package eu.ggnet.dwoss.customer.ee.itest;

import java.util.Collections;
import java.util.List;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.customer.ee.itest.support.ArquillianProjectArchive;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class CustomerServiceIT extends ArquillianProjectArchive {

    private final static Logger L = LoggerFactory.getLogger(CustomerServiceIT.class);

    @Inject
    private CustomerGeneratorOperation cgo;

    @EJB
    private CustomerService cs;

    private List<Long> customerIds = Collections.emptyList();

    public void setup() {
        customerIds = cgo.makeCustomers(20);
    }

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
