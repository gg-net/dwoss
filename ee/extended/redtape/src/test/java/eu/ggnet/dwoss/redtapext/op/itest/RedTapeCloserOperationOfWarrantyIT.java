package eu.ggnet.dwoss.redtapext.op.itest;

import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.core.common.values.TaxType;
import eu.ggnet.dwoss.mandator.api.value.Ledger;
import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;

import java.util.*;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.api.CustomerMetaData;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.receipt.ee.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.api.UnitPositionHook;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.op.itest.support.*;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.redtape.ee.interactiveresult.Result;

import static eu.ggnet.dwoss.core.common.values.PositionType.PRODUCT_BATCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import eu.ggnet.dwoss.redtapext.ee.reporting.RedTapeCloserManual;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class RedTapeCloserOperationOfWarrantyIT extends ArquillianProjectArchive {

    class RedTapeHookStup implements UnitPositionHook {

        @Override
        public Result<List<Position>> elaborateUnitPosition(Position p, long documentId) throws UserInfoException {
            return new Result<>(Arrays.asList(p,
                    Position.builder()
                            .amount(p.getAmount())
                            .price(10.)
                            .tax(p.getTax())
                            .serialNumber(p.getSerial())
                            .refurbishedId(p.getRefurbishedId())
                            .bookingAccount(new Ledger(1000, "DemoLedger"))
                            .type(PRODUCT_BATCH)
                            .tax(TaxType.GENERAL_SALES_TAX_DE_SINCE_2007.getTax())
                            .uniqueUnitId(p.getUniqueUnitId())
                            .uniqueUnitProductId(eao.findByPartNo(WARRANTY_PART_NO).getId())
                            .name("Warranty Position")
                            .description("Warranty Position")
                            .build()
            ));
        }

    }

    private final static String WARRANTY_PART_NO = WarrantyServiceStup.WARRANTY_PART_NO;


    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;
    
    @EJB
    private RedTapeCloserManual redTapeCloser;

    @EJB
    private RedTapeWorker redTapeWorker;

    @EJB
    private RedTapeAgent redTapeAgent;

    @Inject
    private CustomerServiceBean customerService;

    @Inject
    private ProductEao eao;

    @Inject
    private RedTapeCloserOpertaionItBean redTapeCloserOpertaionItBean;

    @Test
    public void testDayClosingWarrenty() throws UserInfoException {
        long customerId = customerGenerator.makeCustomer();
        UniqueUnit uu = receiptGenerator.makeUniqueUnits(1, true, true).get(0);
        Product p = redTapeCloserOpertaionItBean.makeWarrantyProduct(WARRANTY_PART_NO);
        CustomerMetaData metaCustomer = customerService.asCustomerMetaData(customerId);

        assertFalse("no customer in database", customerId == 0);
        assertFalse("bo unique unit in database", uu == null);
        assertFalse("no customer meta data found", metaCustomer == null);
        assertFalse("no warranty product in database", p == null);

        // Create a dossier on a random customer.
        Dossier dos = redTapeWorker.create(customerId, false, "Generated by RedTapeGeneratorOperation.makeSalesDossiers()");
        Document doc = dos.getActiveDocuments(DocumentType.ORDER).get(0);
        assertThat(doc).overridingErrorMessage("Expected active document Order, got null. Dossier: " + dos.toMultiLine()).isNotNull();

        double price = uu.getPrice(PriceType.CUSTOMER);
        if ( price < 0.001 ) price = uu.getPrice(PriceType.RETAILER);
        if ( price < 0.001 ) price = 1111.11;
        Position pos = Position.builder()
                .amount(1)
                .type(PositionType.UNIT)
                .uniqueUnitId(uu.getId())
                .uniqueUnitProductId(uu.getProduct().getId())
                .price(price)
                .tax(doc.getSingleTax())
                .description(UniqueUnitFormater.toDetailedDiscriptionLine(uu))
                .name(UniqueUnitFormater.toPositionName(uu))
                .build();
        pos.setRefurbishedId(uu.getRefurbishId());
        doc.appendAll(new RedTapeHookStup().elaborateUnitPosition(pos, doc.getId()).getPayload());

        doc = redTapeWorker.update(doc, null, "JUnit");
        doc.add(Document.Condition.PAID);
        doc.add(Document.Condition.PICKED_UP);
        doc.setType(DocumentType.INVOICE);

        doc = redTapeWorker.update(doc, null, "JUnit");
        redTapeCloser.executeManual("Junit");

        doc = redTapeAgent.findByIdEager(Document.class, doc.getId());
        doc.setType(DocumentType.COMPLAINT);
        doc.setDirective(Document.Directive.WAIT_FOR_COMPLAINT_COMPLETION);
        doc = redTapeWorker.update(doc, null, "JUnit");

        redTapeCloser.executeManual("Junit");

        redTapeCloserOpertaionItBean.checkReferences(dos.getId());
    }

}
