package eu.ggnet.dwoss.stock.ee.itest;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.stock.api.StockApiLocal;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.stock.ee.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class StockApiLocalIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    @Stocks
    private EntityManager em;

    @Inject
    private StockApiLocal api;

    private final static int STOCK0_ID = 0;

    private final static int STOCK1_ID = 1;

    private final static String SU0_REFURBISHID = "A1";

    private final static String SU0_NAME = "Exone One";

    private final static long SU0_UNQIUE_UNIT_ID = 10;

    private final static String SU1_REFURBISHID = "B2";

    private final static String SU1_NAME = "Acer Asprie Predator";

    private final static long SU1_UNQIUE_UNIT_ID = 11;

    private final static String SU2_REFURBISHID = "C3";

    private final static String SU2_NAME = "Aurous Wifi 2020";

    private final static long SU2_UNQIUE_UNIT_ID = 12;

    private final static String SU3_REFURBISHID = "D4";

    private final static String SU3_NAME = "Stein 2000";

    private final static long SU3_UNQIUE_UNIT_ID = 13;

    @Before
    public void fillDataBase() throws Exception {
        utx.begin();
        em.joinTransaction();
        Stock stock0 = new Stock(STOCK0_ID, "Stock Zero (0)");
        Stock stock1 = new Stock(STOCK1_ID, "Stock One (1)");

        em.persist(stock0);
        em.persist(stock1);

        StockUnit su0 = new StockUnit(SU0_REFURBISHID, SU0_NAME, (int)SU0_UNQIUE_UNIT_ID);
        su0.setStock(stock0);
        StockUnit su1 = new StockUnit(SU1_REFURBISHID, SU1_NAME, (int)SU1_UNQIUE_UNIT_ID);
        su1.setStock(stock0);
        StockUnit su2 = new StockUnit(SU2_REFURBISHID, SU2_NAME, (int)SU2_UNQIUE_UNIT_ID);
        su2.setStock(stock1);
        StockUnit su3 = new StockUnit(SU3_REFURBISHID, SU3_NAME, (int)SU3_UNQIUE_UNIT_ID);
        StockTransaction st = new StockTransactionEmo(em).requestExternalTransferPrepare(STOCK0_ID, STOCK1_ID, "IntegrationTestUser", "Integeration Test");
        st.addUnit(su3);

        em.persist(su0);
        em.persist(su1);
        em.persist(su2);
        em.persist(su3);
        utx.commit();
    }

    @After
    public void clearDataBase() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    @Test
    public void testFindByUniqueUnitId() throws Exception {
        assertThat(api.findByUniqueUnitId(999)).as("Non exisiting Unit").isNull();
        assertThat(api.findByUniqueUnitId(SU0_UNQIUE_UNIT_ID))
                .isNotNull()
                .matches(ssu -> ssu.stock().isPresent())
                .matches(ssu -> !ssu.stockTransaction().isPresent())
                .extracting(ssu -> ssu.stock().get())
                .matches(s -> s.id == STOCK0_ID);

        assertThat(api.findByUniqueUnitId(SU3_UNQIUE_UNIT_ID)).as("StockUnit 3")
                .isNotNull()
                .matches(ssu -> !ssu.stock().isPresent())
                .matches(ssu -> ssu.stockTransaction().isPresent())
                .extracting(ssu -> ssu.stockTransaction().get())
                .matches(st -> st.source().isPresent())
                .matches(st -> st.destination().isPresent())
                .returns(STOCK0_ID, st -> st.source().get().id)
                .returns(STOCK1_ID, st -> st.destination().get().id);
    }
}
