package tryout;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.ShippingCondition;
import eu.ggnet.dwoss.common.api.values.PaymentCondition;
import eu.ggnet.dwoss.common.api.values.SalesChannel;

import java.util.Arrays;
import java.util.function.Consumer;

import javax.swing.JLabel;

import net.sf.jasperreports.engine.JasperPrint;

import eu.ggnet.dwoss.common.ui.AbstractGuardian;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.ee.Mandators;
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
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.experimental.auth.AuthenticationException;
import eu.ggnet.saft.experimental.auth.Guardian;
import eu.ggnet.saft.core.dl.RemoteLookup;

import tryout.stub.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.ggnet.dwoss.customer.upi.CustomerUpi;
import eu.ggnet.saft.core.ui.UiParent;

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
        when(mandatorSupporterMock.loadSalesdata()).thenReturn(new DefaultCustomerSalesdata(ShippingCondition.SIX_MIN_TEN, PaymentCondition.CUSTOMER, PaymentMethod.DIRECT_DEBIT,
                Arrays.asList(SalesChannel.CUSTOMER, SalesChannel.RETAILER), null));

        Dl.remote().add(Mandators.class, mandatorSupporterMock);

        Dl.local().add(CustomerUpi.class, new CustomerUpi() {
            @Override
            public void createCustomer(UiParent parent, Consumer<Long> id) {
            }

            @Override
            public void updateCustomer(UiParent parent, long customerId, Runnable change) {
            }
        });

        UiCore.startSwing(() -> new JLabel("Main Applikation"));
        Ui.exec(() -> {
            Ui.build().swing().show(() -> RedTapeController.build().getView());
        });
    }

}
