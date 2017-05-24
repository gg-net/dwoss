package eu.ggnet.dwoss.receipt.itest;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.receipt.UnitDestroyer;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.receipt.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.util.UserInfoException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class ScrapUnitOperationIT extends ArquillianProjectArchive {

    @Inject
    private StockUnitEao stockUnitEao;

    @EJB
    private UnitDestroyer unitDestroyer;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Test
    public void testScrap() throws UserInfoException {
        UniqueUnit unit = receiptGenerator.makeUniqueUnit();
        unit = unitDestroyer.verifyScarpOrDeleteAble(unit.getRefurbishId());
        unitDestroyer.scrap(unit, "Someone", "cause i can");
        assertThat(stockUnitEao.findByRefurbishId(unit.getRefurbishId())).isNull();
    }

}
