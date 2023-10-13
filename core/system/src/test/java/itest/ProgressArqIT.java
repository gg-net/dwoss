package itest;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.ggnet.dwoss.core.system.progress.*;

import static org.assertj.core.api.Assertions.assertThat;
import org.jboss.arquillian.junit5.ArquillianExtension;

@ExtendWith(ArquillianExtension.class)
public class ProgressArqIT {

    @Inject
    private MonitorFactorySupportBean monitorer;

    @Inject
    private MonitorFactory progressObserver;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "progress.jar")
                .addClasses(ProgressObserverOperation.class, ProgressObserver.class, HiddenMonitor.class, IMonitor.class, MonitorFactorySupportBean.class,
                        MonitorFactory.class, ProgressProducerForTests.class, SubMonitor.class, NullMonitor.class)
                .addPackages(true, "org.assertj")
                .addPackages(true, "org.slf4j")
                .addAsResource("jboss-deployment-structure.xml")
                .addAsManifestResource("META-INF/test-beans.xml", "beans.xml");
    }

    @Test
    public void monitor() {
        assertThat(monitorer).as("monitorer").isNotNull();
        assertThat(progressObserver).as("progressObserver").isNotNull();
        monitorer.doSomething(); // Do some activity in backgeoung
        assertThat(progressObserver.hasProgress()).as("active progress").isFalse();
    }

}
