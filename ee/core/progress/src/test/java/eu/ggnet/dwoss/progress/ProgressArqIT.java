package eu.ggnet.dwoss.progress;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.saft.api.progress.ProgressObserver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class ProgressArqIT {

    @Inject
    private Monitorer monitorer;

    @EJB
    private ProgressObserver progressObserver;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "progress.jar")
                .addClasses(Monitorer.class, MonitorFactory.class, ProgressProducerForTests.class, SubMonitor.class, NullMonitor.class)
                .addAsManifestResource("beans.xml");
    }

    @Test
    public void monitor() {
        System.out.println("HUH");
        assertNotNull(monitorer);
        System.out.println("HOHO");
        System.out.println("M:" + (monitorer.getClass()));

        monitorer.doSomething();
        assertNotNull(progressObserver);
        assertFalse(progressObserver.hasProgress());
    }

}
