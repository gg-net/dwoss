package eu.ggnet.dwoss.redtapext.op.itest;

import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtape.ee.entity.Document;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;
import eu.ggnet.dwoss.receipt.ee.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.UnitOverseer;
import eu.ggnet.dwoss.redtapext.op.itest.support.*;
import eu.ggnet.dwoss.common.api.values.DocumentType;
import eu.ggnet.dwoss.common.api.values.PositionType;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.common.api.values.DocumentType.CAPITAL_ASSET;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType.CUSTOMER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 *
 * @author bastian.venz
 */
@RunWith(Arquillian.class)
public class RedTapeOperationCapitalAssetCancelIT extends ArquillianProjectArchive {

    private long capitalAssestCustomer;

    @EJB
    private UnitOverseer unitOverseer;

    @EJB
    private RedTapeWorker redTapeWorker;

    @EJB
    private StockAgent stockAgent;

    @EJB
    private RedTapeAgent redTapeAgent;

    @Inject
    private SupportBean supportBean;

    @Inject
    private SpecialSystemCustomers specialSystemCustomers;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private DatabaseCleaner cleaner;

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
    }

    @Before
    public void setUp() throws NamingException {
        assertThat(specialSystemCustomers.getSpecialCustomers().values())
                .overridingErrorMessage("SpecialCustomers not initialized, see the SampleMandator Serivce." + CAPITAL_ASSET + " is missing")
                .contains(DocumentType.CAPITAL_ASSET);
        capitalAssestCustomer = specialSystemCustomers.getSpecialCustomers()
                .entrySet()
                .stream()
                .filter(e -> e.getValue() == CAPITAL_ASSET)
                .map(e -> e.getKey())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No SpecialCustomer for Capital Asset"));
    }

    @Test
    public void testCancel() throws UserInfoException {
        UniqueUnit uu = receiptGenerator.makeUniqueUnit();
        uu.setPrice(CUSTOMER, 50., "JunitTestPrice");

        //Generate Dossier
        Dossier dos = redTapeWorker.create(capitalAssestCustomer, false, "Me");
        Document doc = dos.getActiveDocuments(DocumentType.CAPITAL_ASSET).get(0);
        assertTrue(doc.equalsContent(dos.getActiveDocuments(DocumentType.CAPITAL_ASSET).get(0)));

        //Commit explicit date to document for assertion
        doc = supportBean.changeActual(doc, new GregorianCalendar(2012, 3, 15).getTime());

        //Create Positions
        doc.append(NaivBuilderUtil.unit(uu));
        doc.append(NaivBuilderUtil.comment("Comment", "A nice comment"));

        //add units to LogicTransaction
        unitOverseer.lockStockUnit(dos.getId(), uu.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));

        //update document
        Date date = doc.getActual();
        doc = redTapeWorker.update(doc, null, "Me");

        assertEquals("Same actual dates expected", date, doc.getActual());

        assertEquals("Only one Active Document expected", 1, doc.getDossier().getActiveDocuments().size());
        assertTrue(stockAgent.findAllEager(LogicTransaction.class).get(0).getUnits().size() == 1);

        assertEquals("Ammount of Documents", 2, redTapeAgent.findAll(Document.class).size());

        // Now Cancel it
        doc.add(Document.Condition.CANCELED);

        doc = redTapeWorker.update(doc, null, "tester");

        assertTrue("Document doesent contain the Condition Canceled " + doc, doc.getConditions().contains(Document.Condition.CANCELED));

        for (Position position : doc.getPositions(PositionType.UNIT).values()) {
            StockUnit su = stockAgent.findStockUnitByUniqueUnitIdEager(position.getUniqueUnitId());
            assertNull(su + " should not have LogicTransaction", su.getLogicTransaction());
        }

    }

}
