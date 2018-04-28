package tryout;

import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.ShippingCondition;
import eu.ggnet.dwoss.common.api.values.PaymentCondition;
import eu.ggnet.dwoss.common.api.values.SalesChannel;

import java.util.Arrays;

import javax.swing.JLabel;

import net.sf.jasperreports.engine.JasperPrint;

import eu.ggnet.dwoss.common.AbstractGuardian;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.Mandators;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.mandator.api.service.ShippingCostService;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtapext.ee.*;
import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeController;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AuthenticationException;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.cap.RemoteLookup;

import tryout.stub.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.ggnet.dwoss.customer.upi.CustomerUpi;

/**
 *
 * @author oliver.guenther
 */
public class RedTapeTryout {

    public static void main(String[] args) {
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
        Dl.remote().add(RedTapeAgent.class, new RedTapeAgentStub());
        Dl.remote().add(RedTapeWorker.class, new RedTapeWorkerStub());
        Dl.remote().add(UniversalSearcher.class, new UniversalSearcherStub());
        Dl.remote().add(CustomerService.class, new CustomerServiceStub());
        Dl.remote().add(DocumentSupporter.class, new DocumentSupporter() {
            @Override
            public void mail(Document document, DocumentViewType jtype) throws UserInfoException, RuntimeException {
                System.out.println("Mailing " + document);
            }

            @Override
            public JasperPrint render(Document document, DocumentViewType viewType) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Dossier briefed(Document detached, String arranger) {
                detached.add(Document.Flag.CUSTOMER_BRIEFED);
                detached.add(Document.Flag.CUSTOMER_EXACTLY_BRIEFED);
                return detached.getDossier();
            }

            @Override
            public FileJacket toXls(String identifier) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });
        Dl.local().add(Guardian.class, new AbstractGuardian() {
            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
                setRights(new Operator(user, 1, Arrays.asList(AtomicRight.values())));
            }
        });
        Dl.remote().add(StockAgent.class, null);
        Dl.remote().add(UniqueUnitAgent.class, null);
        //     Dl.remote().add(LegacyRemoteBridge.class, null);
        Dl.remote().add(ShippingCostService.class, null);

        Mandators mandatorSupporterMock = mock(Mandators.class);
        when(mandatorSupporterMock.loadSalesdata()).thenReturn(new DefaultCustomerSalesdata(ShippingCondition.DEFAULT, PaymentCondition.CUSTOMER, PaymentMethod.DIRECT_DEBIT,
                Arrays.asList(SalesChannel.CUSTOMER, SalesChannel.RETAILER), null));

        Dl.remote().add(Mandators.class, mandatorSupporterMock);

        CustomerUpi ccos = mock(CustomerUpi.class);
        when(ccos.createCustomer(any())).thenReturn(0L);
        when(ccos.updateCustomer(any(), anyLong())).thenReturn(true);
        Dl.local().add(CustomerUpi.class, ccos);

        UiCore.startSwing(() -> new JLabel("Main Applikation"));
        Ui.exec(() -> {
            Ui.build().swing().show(() -> RedTapeController.build().getView());
        });
    }

}
