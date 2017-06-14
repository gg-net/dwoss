package tryout;

import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.rules.PaymentCondition;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.ShippingTerms;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.rules.ShippingCondition;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.redtape.document.DocumentUpdateController;
import eu.ggnet.dwoss.redtape.document.DocumentUpdateView;
import eu.ggnet.dwoss.redtape.UnitOverseer;
import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Address;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.junit.Test;

import eu.ggnet.saft.core.Client;

import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.MandatorSupporter;

import eu.ggnet.dwoss.redtape.RedTapeAgent;

import eu.ggnet.dwoss.util.OkCancelDialog;

import tryout.stub.*;

import static eu.ggnet.dwoss.configuration.GlobalConfig.TAX;
import static eu.ggnet.dwoss.rules.PositionType.*;

/**
 *
 * @author oliver.guenther
 */
public class DocumentUpdate {

    @Test
    public void tryout() throws InterruptedException {
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
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ShippingTerms loadShippingTerms() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        Client.addSampleStub(RedTapeWorker.class, new RedTapeWorkerStub());
        Client.addSampleStub(RedTapeAgent.class, null);
        Client.addSampleStub(UnitOverseer.class, null);

        DocumentUpdateView cd = new DocumentUpdateView(doc);
        DocumentUpdateController controller = new DocumentUpdateController(cd, doc);

        cd.setController(controller);
        cd.setCustomerValues(1);
        OkCancelDialog<DocumentUpdateView> cdDialog = new OkCancelDialog<>("Auftrag anlegen", cd);
        cdDialog.setVisible(true);
    }

}
