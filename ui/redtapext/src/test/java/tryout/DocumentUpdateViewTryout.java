package tryout;

import eu.ggnet.dwoss.core.common.values.PaymentCondition;
import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PaymentMethod;
import eu.ggnet.dwoss.core.common.values.ShippingCondition;

import java.awt.Font;
import java.util.Arrays;

import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.*;

import eu.ggnet.dwoss.core.widget.AbstractGuardian;
import eu.ggnet.dwoss.core.widget.saft.OkCancelWrap;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ee.UnitOverseer;
import eu.ggnet.dwoss.redtapext.ui.cao.document.DocumentUpdateController;
import eu.ggnet.dwoss.redtapext.ui.cao.document.DocumentUpdateView;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.dl.RemoteLookup;
import eu.ggnet.saft.experimental.auth.AuthenticationException;
import eu.ggnet.saft.experimental.auth.Guardian;

import tryout.stub.CustomerServiceStub;
import tryout.stub.RedTapeWorkerStub;

import static eu.ggnet.dwoss.core.common.values.PositionType.*;
import static eu.ggnet.dwoss.core.common.values.TaxType.*;
import static eu.ggnet.dwoss.redtape.ee.entity.Document.Directive.SEND_ORDER;

/**
 *
 * @author oliver.guenther
 */
public class DocumentUpdateViewTryout {

    private final static Ledger L_1000_STD_UNIT = new Ledger(1000, "Standard Geräte");

    private final static Ledger L_1001_HW_SW_STORE = new Ledger(1001, "Hardware/Software/Kleinteile Store");

    private final static Ledger L_1002_VERSANDKOSTEN = new Ledger(1002, "Versandkosten");

    public static void main(String[] args) {
        // Test different settings of booking accounts in ledgers and in positions
        // Test comment
        Dl.local().add(RemoteLookup.class, new RemoteLookup() {
            @Override
            public <T> boolean contains(Class<T> clazz) {
                return false;
            }

            @Override
            public <T> T lookup(Class<T> clazz) {
                return null;
            }
        });
        Dl.remote().add(CustomerService.class, new CustomerServiceStub());
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ( "Nimbus".equals(info.getName()) ) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        UiCore.startSwing(() -> {
            JLabel l = new JLabel("Main Application");
            l.setFont(new Font("DejaVu Sans", 0, 48));
            return l;
        });

        Dossier dos = new Dossier();
        dos.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);

        final Document doc = new Document(DocumentType.ORDER, SEND_ORDER, new DocumentHistory("UnitTest", "Ein Kommentar"));
        dos.add(doc);
        Address address = new Address("Max Mustermann\nMusterstrass 11\n22222 Musterstadt");
        doc.setInvoiceAddress(address);
        doc.setShippingAddress(address);
        doc.setTaxType(GENERAL_SALES_TAX_DE_SINCE_2007);
        doc.append(Position.builder()
                .type(UNIT)
                .amount(1)
                .description("Intel Core I7 i7-4500U (1.8 Ghz), Memory (in MB): 8192, Intel Graphics Series HD on Board, Festplatte(n): 750GB HDD,"
                        + " Display: 15.6\" (39,62 cm), Crystal Bright, HD (1366x768), 16:9, , Farbe: silber, Ausstattung: Touchscreen, USB 3, Bluetooth,"
                        + " Kartenleser, WLAN b + g + n, Webcam, Videokonnektor(en) : HDMI, VGA, Windows 8.1 64")
                .name("Acer Aspire E1-572P-74508G75Dnii (NX.MFSED.003) SopoNr:123456 SN:NXMFSED00312312122EF001S")
                .bookingAccount(L_1000_STD_UNIT)
                .uniqueUnitId(1)
                .uniqueUnitProductId(1)
                .price(100)
                .tax(doc.getSingleTax())
                .build());
        doc.append(Position.builder()
                .type(UNIT)
                .amount(1)
                .description("Intel Core I7 i7-4500U (1.8 Ghz), Memory (in MB): 8192, Intel Graphics Series HD on Board, Festplatte(n): 750GB HDD,"
                        + " Display: 15.6\" (39,62 cm), Crystal Bright, HD (1366x768), 16:9, , Farbe: silber, Ausstattung: Touchscreen, USB 3, Bluetooth,"
                        + " Kartenleser, WLAN b + g + n, Webcam, Videokonnektor(en) : HDMI, VGA, Windows 8.1 64")
                .name("Acer Aspire E1-572P-74508G75Dnii (NX.MFSED.003) SopoNr:12345 SN:NXMFSED00312312122EF001S")
                .bookingAccount(L_1000_STD_UNIT)
                .uniqueUnitId(2)
                .uniqueUnitProductId(2)
                .price(100)
                .tax(doc.getSingleTax())
                .build());
        doc.append(Position.builder()
                .type(UNIT)
                .amount(1)
                .description("Intel Core I7 i7-4500U (1.8 Ghz), Memory (in MB): 8192, Intel Graphics Series HD on Board, Festplatte(n): 750GB HDD,"
                        + " Display: 15.6\" (39,62 cm), Crystal Bright, HD (1366x768), 16:9, , Farbe: silber, Ausstattung: Touchscreen, USB 3, Bluetooth,"
                        + " Kartenleser, WLAN b + g + n, Webcam, Videokonnektor(en) : HDMI, VGA, Windows 8.1 64")
                .name("Acer Aspire E1-572P-74508G75Dnii (NX.MFSED.003) SopoNr:1234 SN:NXMFSED00312312122EF001S")
                .bookingAccount(L_1000_STD_UNIT)
                .price(100)
                .uniqueUnitId(3)
                .uniqueUnitProductId(3)
                .tax(doc.getSingleTax())
                .build());
        doc.append(Position.builder()
                .type(SERVICE)
                .amount(1)
                .description("Intel Core I7 i7-4500U (1.8 Ghz), Memory (in MB): 8192, Intel Graphics Series HD on Board, Festplatte(n): 750GB HDD,"
                        + " Display: 15.6\" (39,62 cm), Crystal Bright, HD (1366x768), 16:9, , Farbe: silber, Ausstattung: Touchscreen, USB 3, Bluetooth,"
                        + " Kartenleser, WLAN b + g + n, Webcam, Videokonnektor(en) : HDMI, VGA, Windows 8.1 64")
                .name("Service Acer Aspire E1-572P-74508G75Dnii")
                .price(100)
                .bookingAccount(L_1001_HW_SW_STORE)
                .tax(doc.getSingleTax())
                .build());
        doc.append(Position.builder()
                .type(SHIPPING_COST)
                .amount(1)
                .description("Versandkosten")
                .name("Versandkosten")
                .price(10)
                .bookingAccount(L_1002_VERSANDKOSTEN)
                .tax(doc.getSingleTax())
                .build()
        );

        Dl.remote().add(Mandators.class, new Mandators() {

            @Override
            public PostLedger loadPostLedger() {

                return new PostLedger(
                        PostLedger.add()
                                .positionTypes(UNIT)
                                .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                                .primaryLedger(L_1000_STD_UNIT),
                        PostLedger.add()
                                .positionTypes(UNIT)
                                .taxTypes(UNTAXED)
                                .primaryLedger(2001, "Geräte ohne Ust."),
                        PostLedger.add()
                                .positionTypes(UNIT, SERVICE, PRODUCT_BATCH, SHIPPING_COST, UNIT_ANNEX)
                                .taxTypes(REVERSE_CHARGE)
                                .primaryLedger(3000, "Reverse Charge"),
                        PostLedger.add()
                                .positionTypes(SERVICE)
                                .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                                .primaryLedger(2002, "Dienstleistung Store")
                                .alternativeLedger(L_1000_STD_UNIT)
                                .alternativeLedger(L_1002_VERSANDKOSTEN)
                                .alternativeLedger(L_1001_HW_SW_STORE),
                        PostLedger.add()
                                .positionTypes(PRODUCT_BATCH)
                                .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                                .primaryLedger(L_1001_HW_SW_STORE),
                        PostLedger.add()
                                .positionTypes(SHIPPING_COST)
                                .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                                .primaryLedger(L_1002_VERSANDKOSTEN)
                );
            }

            @Override
            public DefaultCustomerSalesdata loadSalesdata() {
                return new DefaultCustomerSalesdata.Builder()
                        .shippingCondition(ShippingCondition.SIX_MIN_TEN)
                        .paymentCondition(PaymentCondition.CUSTOMER)
                        .paymentMethod(PaymentMethod.DIRECT_DEBIT)
                        .build();
            }

            //<editor-fold defaultstate="collapsed" desc="Unused Methods">
            @Override
            public Mandator loadMandator() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ReceiptCustomers loadReceiptCustomers() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public SpecialSystemCustomers loadSystemCustomers() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Contractors loadContractors() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            //</editor-fold>
        });
        Dl.remote().add(RedTapeWorker.class, new RedTapeWorkerStub());
        Dl.remote().add(RedTapeAgent.class, null);
        Dl.remote().add(UnitOverseer.class, null);
        Dl.local().add(Guardian.class, new AbstractGuardian() {

            {
                setRights(new Operator("All Rights", 1, Arrays.asList(AtomicRight.values())));
            }

            @Override
            public void login(String user, char[] pass) throws AuthenticationException {

            }
        });

        DocumentUpdateView view = new DocumentUpdateView(doc);
        DocumentUpdateController controller = new DocumentUpdateController(view, doc);

        view.setController(controller);
        view.setCustomerValues(1);
        Ui.exec(() -> {
            Ui.build().title("Dokument bearbeiten").swing().eval(() -> OkCancelWrap.vetoResult(view)).opt().ifPresent(System.out::println);
        });

    }

}
