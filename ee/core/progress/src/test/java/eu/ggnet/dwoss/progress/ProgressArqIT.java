package eu.ggnet.dwoss.progress;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.saft.api.progress.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class ProgressArqIT {

    @Inject
    private Monitorer monitorer;

    @Inject
    private MonitorFactory progressObserver;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "progress.jar")
                .addClasses(ProgressObserverOperation.class, ProgressObserver.class, HiddenMonitor.class, IMonitor.class, Monitorer.class, MonitorFactory.class, ProgressProducerForTests.class, SubMonitor.class, NullMonitor.class)
                .addAsManifestResource("ejb-jar.xml")
                .addAsManifestResource("beans.xml");
    }

    @Test
    public void monitor() {
        assertNotNull(monitorer);
        monitorer.doSomething();
        assertNotNull(progressObserver);
        assertFalse(progressObserver.hasProgress());
    }

    @Stateless
    public static class Monitorer {

        @Inject
        private MonitorFactory monitorFactory;

        public void doSomething() {
            SubMonitor m = monitorFactory.newSubMonitor("The Test Progress", 100);
            for (int i = 0; i < 100; i++) {
                m.worked(1, "Done: " + i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
            }
            m.finish();
        }
    }

}
