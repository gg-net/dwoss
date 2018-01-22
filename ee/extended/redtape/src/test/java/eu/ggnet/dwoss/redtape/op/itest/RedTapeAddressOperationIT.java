package eu.ggnet.dwoss.redtape.op.itest;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.event.AddressChange;
import eu.ggnet.dwoss.redtape.RedTapeAgent;
import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.redtape.op.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.rules.PositionType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class RedTapeAddressOperationIT extends ArquillianProjectArchive {

    private final String arranger = "JUnitTest";

    @EJB
    private RedTapeAgent redTapeAgent;

    @EJB
    private RedTapeWorker redTapeWorker;

    @Inject
    private CustomerGeneratorOperation cgo;

    @Test
    public void testAdressChanges() {
        long customerId = cgo.makeCustomer();
        Dossier dos = redTapeWorker.create(customerId, false, arranger);
        Document doc = dos.getActiveDocuments().get(0);
        addRandomPositions(doc);
        doc = redTapeWorker.update(doc, null, arranger);

        //start assertion
        assertEquals(doc.getInvoiceAddress(), redTapeWorker.requestAdressesByCustomer(customerId).getInvoice());

        //change adress and assert the changes
        cgo.scrambleAddress(customerId, AddressType.INVOICE);
        assertFalse(doc.getInvoiceAddress().equals(redTapeWorker.requestAdressesByCustomer(customerId).getInvoice()));

        //update adresses to all document adresses and assert changes
        redTapeWorker.updateAllDocumentAdresses(new AddressChange(customerId, "Test", AddressType.INVOICE, "", ""));
        doc = redTapeAgent.findByIdEager(Dossier.class, dos.getId()).getActiveDocuments().get(0);
        assertEquals(doc.getInvoiceAddress(), redTapeWorker.requestAdressesByCustomer(customerId).getInvoice());
    }

    private void addRandomPositions(Document doc) {
        Position p1 = new PositionBuilder().type(PositionType.COMMENT).name("Comment").description("Comments Description").build();
        Position p2 = new PositionBuilder().type(PositionType.SERVICE).name("Service").price(2.).tax(2.)
                .amount(1.).description("Service Description").build();
        Position p3 = new PositionBuilder().type(PositionType.SHIPPING_COST).name("Shipping cost").description("Shipping cost")
                .price(16.5).tax(.19).build();

        doc.append(p1);
        doc.append(p2);
        doc.append(p3);
    }
}
