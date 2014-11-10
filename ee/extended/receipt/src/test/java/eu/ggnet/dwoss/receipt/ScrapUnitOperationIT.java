package eu.ggnet.dwoss.receipt;

import eu.ggnet.dwoss.receipt.UnitDestroyer;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import java.util.*;

import javax.ejb.*;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;

import eu.ggnet.dwoss.util.UserInfoException;

import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class ScrapUnitOperationIT {

    private EJBContainer container;

    @Inject
    private StockUnitEao stockUnitEao;

    @EJB
    private UnitDestroyer unitDestroyer;

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
    public void testScrap() throws UserInfoException {
        UniqueUnit unit = receiptGenerator.makeUniqueUnit();
        unit = unitDestroyer.verifyScarpOrDeleteAble(unit.getRefurbishId());
        unitDestroyer.scrap(unit, "Someone", "cause i can");
        assertThat(stockUnitEao.findByRefurbishId(unit.getRefurbishId())).isNull();
    }

}
