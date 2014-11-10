package eu.ggnet.dwoss.redtape;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import java.util.*;

import javax.ejb.*;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.customer.assist.CustomerPu;

import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;

import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.RedTapeAgent;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;


import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;

import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.rules.DocumentType.CAPITAL_ASSET;
import static eu.ggnet.dwoss.uniqueunit.entity.PriceType.CUSTOMER;
import static org.fest.assertions.api.Assertions.*;
import static org.junit.Assert.*;

/**
 *
 * @author bastian.venz
 */
public class RedTapeOperationCapitalAssetIT {

    private long capitalAssestCustomer;

    private EJBContainer container;

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

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(RedTapePu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_TESTING);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
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

    @After
    public void tearDown() {
        container.close();
    }

    @Test
    public void testUpdate() throws UserInfoException {
        UniqueUnit uu = receiptGenerator.makeUniqueUnit();
        uu.setPrice(CUSTOMER, 50., "JunitTestPrice");

        //Generate Dossier
        Dossier dos = redTapeWorker.create(capitalAssestCustomer, false, "Me");
        assertThat(dos.getActiveDocuments()).isNotEmpty();
        assertThat(dos.getActiveDocuments(CAPITAL_ASSET))
                .overridingErrorMessage("Expected a capital Asset Document but has " + dos.getActiveDocuments())
                .isNotEmpty();

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
