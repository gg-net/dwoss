package eu.ggnet.dwoss.redtapext.op.itest;

import java.util.*;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.common.api.values.PositionType;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.customer.ee.assist.gen.Assure;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.receipt.ee.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.gen.RedTapeGeneratorOperation;
import eu.ggnet.dwoss.redtapext.ee.reporting.RedTapeCloser;
import eu.ggnet.dwoss.redtapext.op.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.redtapext.op.itest.support.RedTapeCloserOpertaionItBean;
import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;
import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.common.api.values.DocumentType.BLOCK;
import static eu.ggnet.dwoss.redtape.ee.entity.Document.Condition.CANCELED;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class RedTapeCloserOperationIT extends ArquillianProjectArchive {

    private final static Random R = new Random();

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private RedTapeGeneratorOperation redTapeGenerator;

    @EJB
    private RedTapeCloser redTapeCloser;

    @EJB
    private ReportAgent reportAgent;

    @EJB
    private RedTapeWorker redTapeWorker;

    @EJB
    private RedTapeAgent redTapeAgent;

    @Inject
    private CustomerServiceBean customerService;

    @Inject
    private ProductEao eao;

    @EJB
    private StockAgent stockAgent;

    @Inject
    private RedTapeCloserOpertaionItBean redTapeCloserOpertaionItBean;

    @Inject
    private PostLedger postLedger;

    private void warnIfStockSizeDidNotChange(long stockSize) {
        if ( stockSize == stockAgent.count(StockUnit.class) ) {
            System.err.println("Stock size din't change, this is highly unlikely. To solve:\n"
                    + " - Rerun Test multiple times\n"
                    + " - Increase the amount of generator\n"
                    + " - Manipulate Stock before rollout to ensure the right behavior");
        }
    }

    /**
     * Tests if something gets closed and if the appropriated stock units are gone.
     * <p>
     * @throws UserInfoException
     */
    @Test
    public void testDayClosing() throws UserInfoException {
        assertFalse(customerGenerator.makeCustomers(10).isEmpty());
        assertFalse(receiptGenerator.makeUniqueUnits(200, true, true).isEmpty());
        assertFalse(redTapeGenerator.makeSalesDossiers(30).isEmpty());

        //dossier ids from created blockers
        List<Long> blockerIds = makeBlockerCustomerAndBlockerDossiers();

        assertThat(
                stockAgent.findAllEager(StockTransaction.class)
                        .stream()
                        .map(StockTransaction::getPositions)
                        .flatMap(Collection::stream)
                        .anyMatch(t -> t.getStockUnit() != null)
        )
                .overridingErrorMessage("Their exist a StockTransaction, which is not complete (blocking a stockUnit), impossible!")
                .isFalse();

        long stockUnits = stockAgent.count(StockUnit.class);
        assertThat(stockUnits).isPositive();

        List<LogicTransaction> allLts = stockAgent.findAllEager(LogicTransaction.class);
        assertThat(allLts.size())
                .overridingErrorMessage("No LogicTransactions exist, impossible!")
                .isPositive();

        redTapeCloser.executeManual("Junit");

        List<Dossier> blockerDossiers = new ArrayList<>();
        for (Long blockerId : blockerIds) {
            blockerDossiers.add(redTapeAgent.findByIdEager(Dossier.class, blockerId));
        }

        // There should be 2 closed and 2 open blocker dossiers. For details see below makeBlockerCustomerAndBlockerDossiers
        assertThat(blockerDossiers.stream().filter(d -> d.isClosed()).count()).as("More/Less Blockers than expected passed closing").isEqualTo(2);

        warnIfStockSizeDidNotChange(stockUnits);

        List<Dossier> allDossiers = redTapeAgent.findAllEager(Dossier.class);
        for (Dossier dos : allDossiers) {
            if ( dos.isClosed() ) {
                for (Document doc : dos.getActiveDocuments()) {
                    if ( doc.containsAny(CANCELED) ) continue; // These are just ignored.
                    for (Integer uuId : doc.getPositionsUniqueUnitIds()) {
                        StockUnit su = stockAgent.findStockUnitByUniqueUnitIdEager(uuId);

                        assertNull("There is a StockUnit for a closed Dossier (doc.id= " + doc.getId() + "):\n"
                                + dos.toMultiLine() + "\n\n"
                                + su + "\n\n"
                                + "Original LTS: " + allLts.stream().filter(x -> x.getUnits().contains(su)).findAny().orElse(null), su);
                    }
                }
            } else {
                for (Integer uuId : dos.getRelevantUniqueUnitIds()) {
                    StockUnit su = stockAgent.findStockUnitByUniqueUnitIdEager(uuId);
                    assertNotNull("There is no StockUnit for an open Dossier\n" + dos, su);
                }
            }
        }

        long reportSize = reportAgent.count(ReportLine.class);
        assertFalse(reportSize == 0);

        redTapeCloser.executeManual("Junit");
        assertEquals("Second call should not add anything new", reportSize, reportAgent.count(ReportLine.class));
    }

    /**
     * builds 4 blocker dossiers in the following manner:
     * - 1 comment only blocker -> closable
     * - 1 comment plus service blocker -> not closeable
     * - 1 unit with stockunit blocker -> not closeable
     * - 1 unit without stockunit blocker -> closeable
     * */
    private List<Long> makeBlockerCustomerAndBlockerDossiers() {
        long customerId = customerGenerator.makeCustomer(new Assure.Builder().simple(true).build());
        customerService.updateCustomerFlags(customerId, EnumSet.of(CustomerFlag.SYSTEM_CUSTOMER));

        // In closable state
        Dossier d1 = redTapeWorker.create(customerId, R.nextBoolean(), "JUNIT");
        Document doc = d1.getActiveDocuments(BLOCK).get(0);
        doc.append(Position.builder()
                .amount(1)
                .type(PositionType.COMMENT)
                .name("Comment")
                .description("Comment")
                .build());
        redTapeWorker.update(doc, null, "JUNIT");
        // D1 is in closable state.

        Dossier d2 = redTapeWorker.create(customerId, R.nextBoolean(), "JUNIT");
        doc = d2.getActiveDocuments(BLOCK).get(0);
        doc.append(Position.builder()
                .amount(1)
                .type(PositionType.COMMENT)
                .name("Comment")
                .description("Comment")
                .build());

        doc.append(Position.builder()
                .type(PositionType.SERVICE)
                .price(100)
                .tax(doc.getSingleTax())
                .name("Service")
                .description("Service")
                .amount(1)
                .bookingAccount(postLedger.get(PositionType.SERVICE, doc.getTaxType()).orElse(null))
                .build());
        redTapeWorker.update(doc, null, "JUNIT");
        // D2 is not in closable state. Service Position

        UniqueUnit unit1 = receiptGenerator.makeUniqueUnits(1, true, true).get(0);
        UniqueUnit unit2 = receiptGenerator.makeUniqueUnits(1, true, true).get(0);

        Dossier d3 = redTapeWorker.create(customerId, R.nextBoolean(), "JUNIT");
        doc = d3.getActiveDocuments(BLOCK).get(0);
        doc.append(Position.builder()
                .amount(1)
                .type(PositionType.UNIT)
                .uniqueUnitId(unit1.getId())
                .uniqueUnitProductId(unit1.getProduct().getId())
                .price(unit1.getPrice(PriceType.SALE))
                .tax(d3.getActiveDocuments(BLOCK).get(0).getSingleTax())
                .description(UniqueUnitFormater.toDetailedDiscriptionLine(unit1))
                .name(UniqueUnitFormater.toPositionName(unit1))
                .refurbishedId(unit1.getIdentifier(REFURBISHED_ID))
                .build());
        redTapeWorker.update(doc, null, "JUNIT");
        // D3 is not in closable state. Unit exists

        Dossier d4 = redTapeWorker.create(customerId, R.nextBoolean(), "JUNIT");
        doc = d4.getActiveDocuments(BLOCK).get(0);
        doc.append(Position.builder()
                .amount(1)
                .type(PositionType.UNIT)
                .uniqueUnitId(unit2.getId())
                .uniqueUnitProductId(unit2.getProduct().getId())
                .price(unit2.getPrice(PriceType.SALE))
                .tax(d4.getActiveDocuments(BLOCK).get(0).getSingleTax())
                .description(UniqueUnitFormater.toDetailedDiscriptionLine(unit2))
                .name(UniqueUnitFormater.toPositionName(unit2))
                .refurbishedId(unit2.getIdentifier(REFURBISHED_ID))
                .build());
        redTapeWorker.update(doc, null, "JUNIT");
        redTapeCloserOpertaionItBean.deleteStockUnit(stockAgent.findStockUnitByRefurbishIdEager(unit2.getIdentifier(REFURBISHED_ID)).getId());
        // D4 is in closable state. Unit is deleted.         

        return Arrays.asList(d1.getId(), d2.getId(), d3.getId(), d4.getId());
    }
}
