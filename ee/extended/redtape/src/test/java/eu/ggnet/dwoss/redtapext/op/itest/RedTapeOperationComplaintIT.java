package eu.ggnet.dwoss.redtapext.op.itest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.UnitOverseer;
import eu.ggnet.dwoss.redtapext.op.itest.support.*;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.util.UserInfoException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
/**
 *
 * @author bastian.venz, oliver.guenther
 */
@RunWith(Arquillian.class)
public class RedTapeOperationComplaintIT extends ArquillianProjectArchive {

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @EJB
    private RedTapeWorker redTapeOperation;

    @EJB
    private UnitOverseer unitOverseer;

    @Inject
    private SupportBean supportBean;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Test
    public void testCreditMemo() throws UserInfoException {
        long customerId = customerGenerator.makeCustomer();
        List<UniqueUnit> units = receiptGenerator.makeUniqueUnits(2, true, true);
        UniqueUnit uu1 = units.get(0);
        UniqueUnit uu2 = units.get(1);

        Dossier dos = redTapeOperation.create(customerId, true, "Me");
        Document doc = dos.getActiveDocuments(DocumentType.ORDER).get(0);
        assertThat(doc).overridingErrorMessage("Expected active document Order, got null. Dossier: " + dos.toMultiLine()).isNotNull();

        //Create Positions
        Position p1 = NaivBuilderUtil.unit(uu1);
        Position p2 = NaivBuilderUtil.unit(uu2);

        doc.append(p1);
        doc.append(p2);

        //add units to LogicTransaction
        unitOverseer.lockStockUnit(dos.getId(), uu1.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));
        unitOverseer.lockStockUnit(dos.getId(), uu2.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID));

        doc = redTapeOperation.update(doc, null, "JUnit");

        doc.add(Document.Condition.PAID);
        doc.add(Document.Condition.PICKED_UP);
        doc.setType(DocumentType.INVOICE);

        doc = redTapeOperation.update(doc, null, "JUnit");

        LogicTransaction lt = supportBean.findByDossierId(doc.getDossier().getId());
        assertNotNull("A LogicTrasaction must exists", lt);
        assertEquals("The Size of the LogicTransaction", 2, lt.getUnits().size());
        assertEquals("Document should have exactly two possitions", 2, doc.getPositions().size());

        doc.setType(DocumentType.COMPLAINT);
        doc.setDirective(Document.Directive.WAIT_FOR_COMPLAINT_COMPLETION);
        doc.removeAt(p2.getId());
        assertEquals("Document should have exactly one possitions", 1, doc.getPositions().size());

        doc = redTapeOperation.update(doc, null, "JUnit Test");

        lt = supportBean.findByDossierId(doc.getDossier().getId());

        assertNotNull("A LogicTrasaction must still exists", lt);
        assertEquals("The Size of the LogicTransaction", 2, lt.getUnits().size());
        Arrays.asList(uu1.getId(), uu2.getId());

        assertTrue("Should contain the same UniqueUnitIds", lt.getUnits().stream().map(su -> su.getUniqueUnitId())
                .collect(Collectors.toSet()).containsAll(Arrays.asList(uu1.getId(), uu2.getId())));

        doc.add(Document.Condition.REJECTED);
        redTapeOperation.update(doc, null, "JUnit Test");
    }

}
