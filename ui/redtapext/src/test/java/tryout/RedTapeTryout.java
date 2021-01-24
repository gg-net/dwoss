package tryout;

import java.util.Arrays;
import java.util.function.Consumer;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.swing.JButton;
import javax.swing.JFrame;

import net.sf.jasperreports.engine.JasperPrint;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.widget.AbstractGuardian;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.AuthenticationException;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.cdi.WidgetProducers;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.core.widget.dl.RemoteLookup;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.spi.CustomerUiModifier;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.mandator.api.service.ShippingCostService;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtapext.ee.DocumentSupporter;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.dwoss.redtapext.ui.ReactivePicoUnitDetailViewCask;
import eu.ggnet.dwoss.redtapext.ui.cao.RedTapeView;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.impl.Swing;
import eu.ggnet.saft.core.ui.UiParent;

import tryout.stub.*;

/**
 *
 * @author oliver.guenther
 */
public class RedTapeTryout {

    public static void main(String[] args) {

        SeContainerInitializer ci = SeContainerInitializer.newInstance();
        ci.addPackages(RedTapeTryout.class);
        ci.addPackages(WidgetProducers.class);
        ci.addPackages(true, ReactivePicoUnitDetailViewCask.class);
        ci.disableDiscovery();
        SeContainer container = ci.initialize();
        Instance<Object> instance = container.getBeanManager().createInstance();

        Saft saft = instance.select(Saft.class).get();
        saft.addOnShutdown(() -> container.close());

        UiCore.initGlobal(saft); // Transition.

        RemoteDl remote = instance.select(RemoteDl.class).get();

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
        remote.add(RedTapeAgent.class, new RedTapeAgentStub());
        remote.add(RedTapeWorker.class, new RedTapeWorkerStub());
        remote.add(CustomerService.class, new CustomerServiceStub());
        remote.add(DocumentSupporter.class, new DocumentSupporter() {
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
        remote.add(StockAgent.class, null);
        remote.add(UniqueUnitAgent.class, null);
        //     Dl.remote().add(LegacyRemoteBridge.class, null);
        remote.add(ShippingCostService.class, null);

        Dl.local().add(CustomerUiModifier.class, new CustomerUiModifier() {
            @Override
            public void createCustomer(UiParent parent, Consumer<Long> id) {
            }

            @Override
            public void updateCustomer(UiParent parent, long customerId, Runnable change) {
            }
        });

        Dl.local().add(CachedMandators.class, new CachedMandatorsStub());

        JFrame mainFrame = UiUtil.startup(() -> {
            JButton b = new JButton("Kunden und Auftragsmanagement");
            b.addActionListener(a -> Ui.build().swing().show(RedTapeView.class));
            return b;
        });

        saft.core(Swing.class).initMain(mainFrame);

    }

}
