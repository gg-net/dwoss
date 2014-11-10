package eu.ggnet.dwoss.progress;

import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.saft.api.progress.ProgressObserver;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProgressIT {

    private EJBContainer container;

    @Inject
    private Monitorer monitorer;

    @EJB
    private ProgressObserver progressObserver;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> o = new HashMap<>();
        o.put("log4j.category.OpenEJB.options", "warn");
        o.put("log4j.category.OpenEJB.server", "warn");
        o.put("log4j.category.OpenEJB.cdi", "warn");
        o.put("log4j.category.OpenEJB.startup", "warn");
        o.put("log4j.category.OpenEJB.startup.service", "warn");
        o.put("log4j.category.OpenEJB.startup.config", "warn");
        o.put("openejb.deployments.classpath.require.descriptor", "true");
        container = EJBContainer.createEJBContainer(o);
        container.getContext().bind("inject", this);
    }

    @After
    public void after() {
        container.close();
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
