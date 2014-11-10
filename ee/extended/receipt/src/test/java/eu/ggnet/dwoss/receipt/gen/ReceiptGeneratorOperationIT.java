package eu.ggnet.dwoss.receipt.gen;

import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;

import java.util.*;

import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;

import eu.ggnet.dwoss.util.UserInfoException;

import eu.ggnet.dwoss.customer.assist.CustomerPu;

import static org.junit.Assert.*;

/**
 * Test for the RedTapeGeneratorOperation.
 * <p/>
 * @author oliver.guenther
 */
public class ReceiptGeneratorOperationIT {

    private EJBContainer container;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(RedTapePu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }

    @Test
    public void testGenerate() throws UserInfoException {
        // The asserts are mearly nice to have. More importend is that the generators work without an exception.
        assertFalse(receiptGenerator.makeProductSpecs(20, true).isEmpty());
        assertFalse(receiptGenerator.makeUniqueUnits(20, true, true).isEmpty());
    }
}
