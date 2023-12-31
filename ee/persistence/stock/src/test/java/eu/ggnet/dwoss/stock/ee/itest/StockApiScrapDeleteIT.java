/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.ee.itest;

import java.util.Arrays;
import java.util.List;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.stock.api.SimpleStockUnit;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.stock.api.StockApi.Scraped;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.assist.gen.StockDeleteUtils;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.stock.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.stock.ee.itest.support.StockEventObserver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class StockApiScrapDeleteIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    @Stocks
    private EntityManager em;

    @Inject
    private StockEventObserver observer;

    @EJB
    private StockAgent agent;

    @EJB
    private StockApi api;

    private final static int STOCK0_ID = 0;

    private final static int STOCK1_ID = 1;

    private final static String COMMENT = "Ein Komentar", USER = "Ein Nutzer";

    private final static String SU0_REFURBISHID = "A1", SU0_NAME = "Exone One";

    private final static long SU0_UNQIUE_UNIT_ID = 10;

    private final static String SU1_REFURBISHID = "B2", SU1_NAME = "Acer Asprie Predator";

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
        StockDeleteUtils.deleteAll(em);
        assertThat(StockDeleteUtils.validateEmpty(em)).isNull();
        utx.commit();
    }

    @Test
    public void scrap() throws Exception {
        SimpleStockUnit ssu1 = api.findByRefurbishId(SU0_REFURBISHID);
        SimpleStockUnit ssu2 = api.findByRefurbishId(SU1_REFURBISHID);
        long wrongid = -1;

        List<Scraped> scraped = api.scrap(Arrays.asList(ssu1.id(), ssu2.id(), wrongid), COMMENT, USER);
        assertThat(scraped).as("Result of StockApi.scrap()").hasSize(3).extracting(s -> s.successful()).containsExactlyInAnyOrder(true, true, false);

        // Verify, that a correct Stock Transaction was created on delete
        List<StockTransaction> sts = agent.findStockTransactionEager(StockTransactionType.DESTROY, StockTransactionStatusType.COMPLETED);
        assertThat(sts).as("All StockTransaction of type Destroy").hasSize(1);
        // Formatter StockTransaction.setPosition is verified.
        assertThat(sts.get(0).getPositions()).as("Positions of the StockTransaction").hasSize(2).extracting(p -> p.getDescription())
                .containsExactlyInAnyOrder(SU0_REFURBISHID + " - " + SU0_NAME, SU1_REFURBISHID + " - " + SU1_NAME);

        // Verify that the event was fired and observed.
        assertThat(observer.scrapEvents()).as("Observed ScrapeEvent").hasSize(1).extracting(t -> t.arranger()).containsExactly(USER);
    }

    @Test
    public void delete() throws Exception {
        SimpleStockUnit ssu0 = api.findByRefurbishId(SU0_REFURBISHID);
        SimpleStockUnit ssu1 = api.findByRefurbishId(SU1_REFURBISHID);
        long wrongid = -1;

        // Produces 2 succesful scraps and one unsuccessful.
        List<Scraped> scraped = api.delete(Arrays.asList(ssu0.id(), ssu1.id(), wrongid), COMMENT, USER);
        assertThat(scraped).as("Result of StockApi.delete()").hasSize(3).extracting(s -> s.successful()).containsExactlyInAnyOrder(true, true, false);

        // Verify, that a correct Stock Transaction was created on delete
        List<StockTransaction> sts = agent.findStockTransactionEager(StockTransactionType.DESTROY, StockTransactionStatusType.COMPLETED);
        assertThat(sts).as("All StockTransaction of type Destroy").hasSize(1);
        // Formatter StockTransaction.setPosition is verified.
        assertThat(sts.get(0).getPositions()).as("Positions of the StockTransaction").hasSize(2).extracting(p -> p.getDescription())
                .containsExactlyInAnyOrder(SU0_REFURBISHID + " - " + SU0_NAME, SU1_REFURBISHID + " - " + SU1_NAME);

        // Verify that the event was fired and observed.
        assertThat(observer.deleteEvents()).as("Observed ScrapeEvent").hasSize(1).extracting(t -> t.arranger()).containsExactly(USER);

    }
}
