package eu.ggnet.dwoss.redtape;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.StockAgent;
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
import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;

import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;

import eu.ggnet.dwoss.redtape.RedTapeAgent;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;


import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;

import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.rules.PositionType.COMMENT;
import static eu.ggnet.dwoss.uniqueunit.entity.PriceType.CUSTOMER;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;
import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
public class RedTapeOperationDossierIT {

    private EJBContainer container;

    @Inject
    private CustomerGeneratorOperation cgo;

    @EJB
    private RedTapeWorker redTapeWorker;

    @EJB
    private UnitOverseer unitOverseer;

    @EJB
    private RedTapeAgent redTapeAgent;

    @EJB
    private StockAgent stockAgent;

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
    public void testDossierDelete() throws UserInfoException {
        long customerId = cgo.makeCustomer();
        UniqueUnit uu1 = receiptGenerator.makeUniqueUnit();
        Dossier dos = setupDossier(customerId, uu1);

        //delete dossier, cleanup stock and sopo
        redTapeWorker.delete(dos);

        assertTrue("No Dossiers should exist", redTapeAgent.findAll(Dossier.class).isEmpty());
        assertTrue("No Documents should exist", redTapeAgent.findAll(Document.class).isEmpty());
        assertTrue("No Positions should exist", redTapeAgent.findAll(Position.class).isEmpty());
        assertTrue("No LogicTransactions should exist", stockAgent.findAll(LogicTransaction.class).isEmpty());
    }

    @Test
//    @Ignore
    public void testRevertCreate() throws UserInfoException {
        long customerId = cgo.makeCustomer();
        UniqueUnit uu1 = receiptGenerator.makeUniqueUnit();
        Dossier dos = setupDossier(customerId, uu1);
        Document doc = dos.getActiveDocuments(DocumentType.ORDER).get(0);
        doc.setType(DocumentType.INVOICE);
        doc.add(Document.Condition.PAID);
        doc.add(Document.Condition.PICKED_UP);
        doc.add(Document.Settlement.CASH);
        doc.setDirective(Document.Directive.NONE);
        doc = redTapeWorker.update(doc, null, "JUnit");
        assertTrue("Check size of active documents", doc.getDossier().getActiveDocuments().size() == 2);

        String title = "Komentar-" + Math.random();
        String description = "Beschreibung-" + Math.random();

        doc.append(NaivBuilderUtil.comment(title, description));
        doc = redTapeWorker.revertCreate(doc);

        assertThat(extractProperty("name", String.class).from(doc.getPositions(COMMENT).values())).doesNotContain(title);
        assertThat(extractProperty("description", String.class).from(doc.getPositions(COMMENT).values())).doesNotContain(description);
    }

    private Dossier setupDossier(long customerId, UniqueUnit uu1) throws UserInfoException {
        uu1.setPrice(CUSTOMER, 100, "Price Added in Test");
        Dossier dos = redTapeWorker.create(customerId, true, "Me");
        Document doc = dos.getActiveDocuments(DocumentType.ORDER).get(0);

        doc.append(NaivBuilderUtil.unit(uu1));
        doc.append(NaivBuilderUtil.service());

        //add unit to LogicTransaction
        unitOverseer.lockStockUnit(dos.getId(), uu1.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));

        //update to create the order and sopo auftrag
        doc = redTapeWorker.update(doc, null, "JUnit");

        doc.append(NaivBuilderUtil.comment());

        return redTapeWorker.update(doc, null, "JUnit").getDossier();
    }

}
