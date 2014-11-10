package eu.ggnet.dwoss.redtape;

import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.redtape.entity.PositionBuilder;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Dossier;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;

import eu.ggnet.dwoss.event.AddressChange;

import eu.ggnet.dwoss.redtape.RedTapeAgent;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;


import eu.ggnet.dwoss.stock.assist.StockPu;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
public class RedTapeAddressOperationIT {

    private EJBContainer container;

    private long customerId;

    private final String arranger = "JUnitTest";

    @EJB
    private RedTapeAgent redTapeAgent;

    @EJB
    private RedTapeWorker redTapeWorker;

    @Inject
    private CustomerGeneratorOperation cgo;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(RedTapePu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
//         cgo.makeSystemCustomers(); // Probably don't need this.
        customerId = cgo.makeCustomer();
    }

    @After
    public void tearDown() {
        container.close();
    }

    @Test
    public void testAdressChanges() {
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
        Position p1 = new PositionBuilder().setType(PositionType.COMMENT).setName("Comment").setDescription("Comments Description").createPosition();
        Position p2 = new PositionBuilder().setType(PositionType.SERVICE).setName("Service").setPrice(2.).setTax(2.).
                setAfterTaxPrice(2.2).setAmount(1.).setDescription("Service Description").createPosition();
        Position p3 = new PositionBuilder().setType(PositionType.SHIPPING_COST).setName("Shipping cost").setDescription("Shipping cost")
                .setPrice(16.5).setTax(.19).setAfterTaxPrice(16.5 * 1.19).createPosition();

        doc.append(p1);
        doc.append(p2);
        doc.append(p3);
    }
}
