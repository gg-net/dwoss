package eu.ggnet.dwoss.redtape.reporting;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.redtape.UnitOverseer;
import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;

import java.io.IOException;
import java.util.*;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcReader;

import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;


import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.redtape.*;

import lombok.AllArgsConstructor;
import lombok.ToString;

import static eu.ggnet.dwoss.redtape.NaivBuilderUtil.*;
import static eu.ggnet.dwoss.rules.PositionType.PRODUCT_BATCH;
import static eu.ggnet.dwoss.rules.PositionType.SHIPPING_COST;
import static java.util.stream.Collectors.toSet;
import static org.fest.assertions.api.Assertions.*;
import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
public class CreditMemoReportIT {

    @AllArgsConstructor
    @ToString
    public static class LoadContainer {

        private final String sopoNr;

        private final String serial;

        private final String name;

        private final String contractor;

        private final String type;

        private final Double netto;

        private final Double brutto;

    }

    private EJBContainer container;

    @EJB
    private CreditMemoReporter reportOperation;

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @EJB
    private RedTapeWorker redTapeWorker;

    @EJB
    private UnitOverseer unitOverseer;

    @EJB
    private StockAgent stockAgent;

    @Inject
    private SupportBean support;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(CustomerPu.CMP_IN_MEMORY);
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
//    @Ignore
    // The chang of the report, that needs a predecessor results here in a nullpointer exception.
    public void testCreditMemoReportOperation() throws IOException, InterruptedException {
        long customerId = customerGenerator.makeCustomer();
        List<UniqueUnit> uus = receiptGenerator.makeUniqueUnits(4, true, true);
        UniqueUnit uu1 = uus.get(0);
        UniqueUnit uu2 = uus.get(1);
        UniqueUnit uu3 = uus.get(2);
        UniqueUnit uu4 = uus.get(3);

        Product uuProduct1 = uu1.getProduct();

        int stockIdOfUU1 = stockAgent.findStockUnitByUniqueUnitIdEager(uu1.getId()).getStock().getId();

        Dossier dos = redTapeWorker.create(customerId, true, "Me");
        Document doc = dos.getActiveDocuments(DocumentType.ORDER).get(0);
        assertTrue(doc.equalsContent(dos.getActiveDocuments(DocumentType.ORDER).get(0)));

        doc.append(unit(uu1));
        doc.append(unit(uu2));
        doc.append(unit(uu3));
        doc.append(comment());
        doc.append(service());
        doc.append(batch(uuProduct1));
        doc.append(shippingcost());

        //add units to LogicTransaction
        unitOverseer.lockStockUnit(dos.getId(), uu1.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu2.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu3.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu4.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));

        doc = redTapeWorker.update(doc, null, "JUnit");

        doc.add(Document.Condition.PAID);
        doc.add(Document.Condition.PICKED_UP);
        doc.setType(DocumentType.INVOICE);

        doc = redTapeWorker.update(doc, null, "JUnit");

        LogicTransaction lt = support.findByDossierId(doc.getDossier().getId());
        assertNotNull("A LogicTrasaction must exists", lt);
        assertEquals("The Size of the LogicTransaction", 3, lt.getUnits().size());

        //A CreditMemo for Unit1, negate prices on Annulation Invoice.
        for (Position pos : new ArrayList<>(doc.getPositions().values())) {
            if ( pos.getUniqueUnitId() == uu1.getId() || pos.getType() == PRODUCT_BATCH || pos.getType() == SHIPPING_COST ) {
                pos.setPrice(pos.getPrice() * -1);
                pos.setPrice(pos.getAfterTaxPrice() * -1);
            } else {
                doc.remove(pos);
            }
        }

        assertEquals("Document should have exactly one possition", 3, doc.getPositions().size());
        doc.setType(DocumentType.ANNULATION_INVOICE);

        doc = redTapeWorker.update(doc, stockIdOfUU1, "JUnit Test");

        Collection<Position> positions = doc.getPositions().values();

// Report somethere in the past till now.
        FileJacket jacket = reportOperation.toXls(new Date(1352115909), new Date());
        assertNotNull(jacket);
        assertEquals(".xls", jacket.getSuffix());
        assertTrue(jacket.getContent().length > 0);
        List<LoadContainer> read = new JExcelLucidCalcReader()
                .addColumn(0, String.class)
                .addColumn(1, String.class)
                .addColumn(2, String.class)
                .addColumn(3, String.class)
                .addColumn(4, String.class)
                .addColumn(5, Double.class)
                .addColumn(6, Double.class)
                .read(jacket.toTemporaryFile(), LoadContainer.class);

        // HINT: Not a complete test, but some fileds at least.
        assertThat(positions.stream().map(Position::getPrice).collect(toSet()))
                .containsOnly(read.stream().map(l -> l.netto).toArray((v) -> new Double[v]));
        assertThat(positions.stream().map(Position::getAfterTaxPrice).collect(toSet()))
                .containsOnly(read.stream().map(l -> l.brutto).toArray((v) -> new Double[v]));
        assertThat(positions.stream().map(Position::getName).collect(toSet()))
                .containsOnly(read.stream().map(l -> l.name).toArray((v) -> new String[v]));

    }
}
