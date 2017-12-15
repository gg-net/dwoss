package tryout;

import java.util.Arrays;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import eu.ggnet.dwoss.common.AbstractGuardian;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.PostLedger.LedgerValue;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.redtape.*;
import eu.ggnet.dwoss.redtape.document.DocumentUpdateController;
import eu.ggnet.dwoss.redtape.document.DocumentUpdateView;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.MapBuilder;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.api.AuthenticationException;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.authorisation.Guardian;
import eu.ggnet.saft.core.swing.OkCancel;

import tryout.stub.CustomerServiceStub;
import tryout.stub.RedTapeWorkerStub;

import static eu.ggnet.dwoss.configuration.GlobalConfig.TAX;
import static eu.ggnet.dwoss.rules.PositionType.*;

/**
 *
 * @author oliver.guenther
 */
public class DocumentUpdateViewTryout {

    public static void main(String[] args) {
        Client.addSampleStub(CustomerService.class, new CustomerServiceStub());
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
        UiCore.startSwing(() -> new MainPanel());

        Dossier dos = new Dossier();
        dos.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);

        final Document doc = new Document();
        dos.add(doc);
        Address address = new Address("Max Mustermann\nMusterstrass 11\n22222 Musterstadt");
        doc.setInvoiceAddress(address);
        doc.setShippingAddress(address);
        doc.append(Position.builder()
                .type(UNIT)
                .amount(1)
                .description("Intel Core I7 i7-4500U (1.8 Ghz), Memory (in MB): 8192, Intel Graphics Series HD on Board, Festplatte(n): 750GB HDD,"
                        + " Display: 15.6\" (39,62 cm), Crystal Bright, HD (1366x768), 16:9, , Farbe: silber, Ausstattung: Touchscreen, USB 3, Bluetooth,"
                        + " Kartenleser, WLAN b + g + n, Webcam, Videokonnektor(en) : HDMI, VGA, Windows 8.1 64")
                .name("Acer Aspire E1-572P-74508G75Dnii (NX.MFSED.003) SopoNr:123456 SN:NXMFSED00312312122EF001S")
                .price(100)
                .tax(TAX)
                .afterTaxPrice(119)
                .bookingAccount(-1).build());
        doc.append(Position.builder()
                .type(UNIT)
                .amount(1)
                .description("Intel Core I7 i7-4500U (1.8 Ghz), Memory (in MB): 8192, Intel Graphics Series HD on Board, Festplatte(n): 750GB HDD,"
                        + " Display: 15.6\" (39,62 cm), Crystal Bright, HD (1366x768), 16:9, , Farbe: silber, Ausstattung: Touchscreen, USB 3, Bluetooth,"
                        + " Kartenleser, WLAN b + g + n, Webcam, Videokonnektor(en) : HDMI, VGA, Windows 8.1 64")
                .name("Acer Aspire E1-572P-74508G75Dnii (NX.MFSED.003) SopoNr:12345 SN:NXMFSED00312312122EF001S")
                .price(100)
                .tax(TAX)
                .afterTaxPrice(119)
                .bookingAccount(-1).build());
        doc.append(Position.builder()
                .type(UNIT)
                .amount(1)
                .description("Intel Core I7 i7-4500U (1.8 Ghz), Memory (in MB): 8192, Intel Graphics Series HD on Board, Festplatte(n): 750GB HDD,"
                        + " Display: 15.6\" (39,62 cm), Crystal Bright, HD (1366x768), 16:9, , Farbe: silber, Ausstattung: Touchscreen, USB 3, Bluetooth,"
                        + " Kartenleser, WLAN b + g + n, Webcam, Videokonnektor(en) : HDMI, VGA, Windows 8.1 64")
                .name("Acer Aspire E1-572P-74508G75Dnii (NX.MFSED.003) SopoNr:1234 SN:NXMFSED00312312122EF001S")
                .price(100)
                .tax(TAX)
                .afterTaxPrice(119)
                .bookingAccount(-1).build());
        doc.append(Position.builder()
                .type(SERVICE)
                .amount(1)
                .description("Intel Core I7 i7-4500U (1.8 Ghz), Memory (in MB): 8192, Intel Graphics Series HD on Board, Festplatte(n): 750GB HDD,"
                        + " Display: 15.6\" (39,62 cm), Crystal Bright, HD (1366x768), 16:9, , Farbe: silber, Ausstattung: Touchscreen, USB 3, Bluetooth,"
                        + " Kartenleser, WLAN b + g + n, Webcam, Videokonnektor(en) : HDMI, VGA, Windows 8.1 64")
                .name("Service Acer Aspire E1-572P-74508G75Dnii")
                .price(100)
                .tax(TAX)
                .afterTaxPrice(119)
                .bookingAccount(-1).build());
        doc.append(Position.builder()
                .type(SHIPPING_COST)
                .amount(1)
                .description("Versandkosten")
                .name("Versandkosten")
                .price(10)
                .tax(TAX)
                .afterTaxPrice(11.90)
                .bookingAccount(-1)
                .build()
        );

        Client.addSampleStub(MandatorSupporter.class, new MandatorSupporter() {

            @Override
            public Mandator loadMandator() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public DefaultCustomerSalesdata loadSalesdata() {
                return new DefaultCustomerSalesdata(ShippingCondition.DEFAULT, PaymentCondition.CUSTOMER, PaymentMethod.DIRECT_DEBIT, null, null);
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

            @Override
            public PostLedger loadPostLedger() {
                return new PostLedger(new MapBuilder<>()
                        .put(UNIT, new LedgerValue(1))
                        .put(SERVICE, new LedgerValue(2, Arrays.asList(
                                21,
                                22
                        )))
                        .put(PRODUCT_BATCH, new LedgerValue(3))
                        .put(SHIPPING_COST, new LedgerValue(4))
                        .toHashMap());
            }

            @Override
            public ShippingTerms loadShippingTerms() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String loadMandatorAsHtml() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        Client.addSampleStub(RedTapeWorker.class, new RedTapeWorkerStub());
        Client.addSampleStub(RedTapeAgent.class, null);
        Client.addSampleStub(UnitOverseer.class, null);
        Client.addSampleStub(Guardian.class, new AbstractGuardian() {

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

        Ui.swing().title("Dokument bearbeiten").eval(() -> OkCancel.wrap(view)).ifPresent(System.out::println);

//        OkCancelDialog<DocumentUpdateView> cdDialog = new OkCancelDialog<>("Auftrag anlegen", view);
//        cdDialog.setVisible(true);
    }

}
