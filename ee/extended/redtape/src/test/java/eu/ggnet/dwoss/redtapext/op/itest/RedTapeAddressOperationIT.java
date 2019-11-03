package eu.ggnet.dwoss.redtapext.op.itest;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.values.AddressType;
import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.customer.api.AddressChange;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.op.itest.support.ArquillianProjectArchive;

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
        assertEquals(doc.getInvoiceAddress(), redTapeWorker.requestAdressesByCustomer(customerId).invoice);

        //change adress and assert the changes
        //TODO CONVERTERUTIL AAAAAHHHHHH
        cgo.scrambleAddress(customerId, AddressType.INVOICE);
        assertFalse(doc.getInvoiceAddress().equals(redTapeWorker.requestAdressesByCustomer(customerId).invoice));

        //update adresses to all document adresses and assert changes
        redTapeWorker.updateAllDocumentAdresses(new AddressChange(customerId, "Test", AddressType.INVOICE, "", ""));
        doc = redTapeAgent.findByIdEager(Dossier.class, dos.getId()).getActiveDocuments().get(0);
        assertEquals(doc.getInvoiceAddress(), redTapeWorker.requestAdressesByCustomer(customerId).invoice);
    }

    private void addRandomPositions(Document doc) {
        Position p1 = Position.builder().amount(1).type(PositionType.COMMENT).name("Comment").description("Comments Description").build();
        Position p2 = Position.builder().amount(1).type(PositionType.SERVICE).name("Service").price(2.).tax(2.)
                .amount(1.).description("Service Description").build();
        Position p3 = Position.builder().amount(1).type(PositionType.SHIPPING_COST).name("Shipping cost").description("Shipping cost")
                .price(16.5).tax(.19).build();

        doc.append(p1);
        doc.append(p2);
        doc.append(p3);
    }
}
