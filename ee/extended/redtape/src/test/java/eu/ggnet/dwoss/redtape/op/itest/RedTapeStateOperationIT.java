package eu.ggnet.dwoss.redtape.op.itest;

import java.util.HashSet;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.redtape.op.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.redtape.state.CustomerDocument;
import eu.ggnet.dwoss.redtape.state.RedTapeStateTransitions;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;

import static eu.ggnet.dwoss.rules.PaymentMethod.ADVANCE_PAYMENT;
import static eu.ggnet.dwoss.rules.ShippingCondition.DEFAULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class RedTapeStateOperationIT extends ArquillianProjectArchive {

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @EJB
    private RedTapeWorker redTapeWorker;

    /**
     * Test of create method, of class RedTapeWorkerOperation.
     * <p>
     * @throws java.lang.InterruptedException
     */
    @Ignore
    @Test
    public void testStateError() throws InterruptedException {

        long customerId = customerGenerator.makeCustomer();

        //Generate Dossier
        Dossier dos1 = redTapeWorker.create(customerId, false, "Test");
        Document doc1 = dos1.getActiveDocuments(DocumentType.ORDER).get(0);

        //Create Positions
        Position p1 = new PositionBuilder().type(PositionType.COMMENT).name("OHO").description("Muh").build();
        Position p2 = new PositionBuilder().type(PositionType.PRODUCT_BATCH).name("Muuuuh").description("Muhhhhh").uniqueUnitProductId(1)
                .price(100).tax(0.19).amount(1).build();

        doc1.append(p1);
        doc1.append(p2);

        //update document
        doc1 = redTapeWorker.update(doc1, null, "Me");
        doc1 = redTapeWorker.stateChange(new CustomerDocument(new HashSet<>(), doc1, DEFAULT, ADVANCE_PAYMENT), RedTapeStateTransitions.I_PAY_AND_INVOICE, "Test");
        assertTrue("Set " + doc1.getConditions() + " should contain " + Document.Condition.PAID, doc1.getConditions().contains(Document.Condition.PAID));
        assertEquals("Type should be Invoice", DocumentType.INVOICE, doc1.getType());

        doc1 = redTapeWorker.stateChange(new CustomerDocument(new HashSet<>(), doc1, DEFAULT, ADVANCE_PAYMENT), RedTapeStateTransitions.I_PICK_UP, "Test");
        assertTrue("Set " + doc1.getConditions() + " should contain " + Document.Condition.PICKED_UP, doc1.getConditions().contains(Document.Condition.PICKED_UP));

        //Generate Dossier -- Now Sent
        dos1 = redTapeWorker.create(customerId, true, "Test");
        doc1 = dos1.getActiveDocuments(DocumentType.ORDER).get(0);

        doc1.append(p1.partialClone());
        doc1.append(p2.partialClone());

        //update document
        doc1 = redTapeWorker.update(doc1, null, "Me");

        doc1 = redTapeWorker.stateChange(new CustomerDocument(new HashSet<>(), doc1, DEFAULT, ADVANCE_PAYMENT), RedTapeStateTransitions.II_PAY, "Test");
        assertTrue("Set " + doc1.getConditions() + " should contain " + Document.Condition.PAID, doc1.getConditions().contains(Document.Condition.PAID));

        doc1 = redTapeWorker.stateChange(new CustomerDocument(new HashSet<>(), doc1, DEFAULT, ADVANCE_PAYMENT), RedTapeStateTransitions.II_SEND_AND_INVOICE, "Test");
        assertTrue("Set " + doc1.getConditions() + " should contain " + Document.Condition.SENT, doc1.getConditions().contains(Document.Condition.SENT));
        assertEquals("Type should be Invoice", DocumentType.INVOICE, doc1.getType());
    }
}
