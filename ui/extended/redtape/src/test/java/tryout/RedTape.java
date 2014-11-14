package tryout;

import eu.ggnet.dwoss.rules.PaymentCondition;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.mandator.api.value.ShippingTerms;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.rules.ShippingCondition;
import eu.ggnet.dwoss.redtape.RedTapeController;
import eu.ggnet.dwoss.redtape.RedTapeView;
import eu.ggnet.dwoss.redtape.RedTapeModel;
import eu.ggnet.dwoss.redtape.UniversalSearcher;
import eu.ggnet.dwoss.redtape.DocumentSupporter;
import eu.ggnet.dwoss.redtape.RedTapeWorker;

import java.util.*;

import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.saft.api.AuthenticationException;

import org.junit.Test;

import net.sf.jasperreports.engine.JasperPrint;

import eu.ggnet.saft.core.Client;

import eu.ggnet.dwoss.common.AbstractGuardian;

import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.mandator.api.service.ShippingCostService;

import eu.ggnet.dwoss.redtape.RedTapeAgent;
import eu.ggnet.dwoss.redtape.api.LegacyBridge;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Dossier;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;

import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;

import tryout.stub.*;

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
        Client.addSampleStub(LegacyBridge.class, null);
        Client.addSampleStub(ShippingCostService.class, null);
        Client.addSampleStub(MandatorSupporter.class, new MandatorSupporter() {

            @Override
            public Mandator loadMandator() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public DefaultCustomerSalesdata loadSalesdata() {
                return new DefaultCustomerSalesdata(ShippingCondition.DEFAULT, PaymentCondition.CUSTOMER, PaymentMethod.DIRECT_DEBIT,
                        Arrays.asList(SalesChannel.CUSTOMER, SalesChannel.RETAILER), null);
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

        RedTapeModel model = new RedTapeModel();
        RedTapeController controller = new RedTapeController();
        RedTapeView view = new RedTapeView();
        controller.setModel(model);
        controller.setView(view);
        view.setModel(model);
        view.setController(controller);
        view.pack();
        view.setVisible(true);
        while (view.isVisible()) {
            Thread.sleep(500);
        }
    }

}
