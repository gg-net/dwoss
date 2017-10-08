package tryout;

import java.util.Arrays;

import net.sf.jasperreports.engine.JasperPrint;

import org.junit.Test;

import eu.ggnet.dwoss.common.AbstractGuardian;
import eu.ggnet.dwoss.customer.api.CustomerCos;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.mandator.api.service.ShippingCostService;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.redtape.*;
import eu.ggnet.dwoss.redtape.api.LegacyRemoteBridge;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.api.AuthenticationException;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.authorisation.Guardian;

import tryout.stub.*;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author oliver.guenther
 */
public class RedTape {

    @Test
    public void tryout() throws InterruptedException {

        Client.addSampleStub(RedTapeAgent.class, new RedTapeAgentStub());
        Client.addSampleStub(RedTapeWorker.class, new RedTapeWorkerStub());
        Client.addSampleStub(UniversalSearcher.class, new UniversalSearcherStub());
        Client.addSampleStub(CustomerService.class, new CustomerServiceStub());
        Client.addSampleStub(DocumentSupporter.class, new DocumentSupporter() {
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
        Client.addSampleStub(Guardian.class, new AbstractGuardian() {
            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
                setRights(new Operator(user, 1, Arrays.asList(AtomicRight.values())));
            }
        });
        Client.addSampleStub(StockAgent.class, null);
        Client.addSampleStub(UniqueUnitAgent.class, null);
        Client.addSampleStub(LegacyRemoteBridge.class, null);
        Client.addSampleStub(ShippingCostService.class, null);

        MandatorSupporter mandatorSupporterMock = mock(MandatorSupporter.class);
        when(mandatorSupporterMock.loadSalesdata()).thenReturn(new DefaultCustomerSalesdata(ShippingCondition.DEFAULT, PaymentCondition.CUSTOMER, PaymentMethod.DIRECT_DEBIT,
                Arrays.asList(SalesChannel.CUSTOMER, SalesChannel.RETAILER), null));

        Client.addSampleStub(MandatorSupporter.class, mandatorSupporterMock);

        CustomerCos ccos = mock(CustomerCos.class);
        when(ccos.createCustomer()).thenReturn(0L);
        when(ccos.updateCustomer(anyLong())).thenReturn(true);

        Client.addSampleStub(CustomerCos.class, ccos);

        RedTapeModel model = new RedTapeModel();
        RedTapeController controller = new RedTapeController();
        RedTapeView view = new RedTapeView();
        controller.setModel(model);
        controller.setView(view);
        view.setModel(model);
        view.setController(controller);
        view.pack();
        view.setVisible(true);
        UiCore.continueSwing(view);
        while (view.isVisible()) {
            Thread.sleep(500);
        }
    }

}
