package eu.ggnet.dwoss.redtapext.op.itest;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.UnitOverseer;
import eu.ggnet.dwoss.redtapext.op.itest.support.*;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.rules.PositionType.COMMENT;
import static eu.ggnet.dwoss.uniqueunit.entity.PriceType.CUSTOMER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class RedTapeOperationDossierIT extends ArquillianProjectArchive {

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

    @Inject
    private DatabaseCleaner cleaner;

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
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
    public void testRevertCreate() throws UserInfoException {
        long customerId = cgo.makeCustomer();
        UniqueUnit uu1 = receiptGenerator.makeUniqueUnit();
        Dossier dos = setupDossier(customerId, uu1);
        Document doc = dos.getActiveDocuments(DocumentType.ORDER).get(0);
        assertThat(doc).overridingErrorMessage("Expected active document Order, got null. Dossier: " + dos.toMultiLine()).isNotNull();

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
        assertThat(doc).overridingErrorMessage("Expected active document Order, got null. Dossier: " + dos.toMultiLine()).isNotNull();

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
