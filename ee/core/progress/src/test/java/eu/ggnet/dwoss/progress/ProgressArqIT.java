package eu.ggnet.dwoss.progress;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.progress.support.MonitorFactorySupportBean;
import eu.ggnet.saft.api.progress.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class ProgressArqIT {

    @Inject
    private MonitorFactorySupportBean monitorer;

    @Inject
    private MonitorFactory progressObserver;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "progress.jar")
                .addClasses(ProgressObserverOperation.class, ProgressObserver.class, HiddenMonitor.class, IMonitor.class, MonitorFactorySupportBean.class, MonitorFactory.class, ProgressProducerForTests.class, SubMonitor.class, NullMonitor.class)
                .addPackages(true, "org.assertj")
                .addPackages(true, "org.slf4j")
                .addPackages(true, "org.apache.log4j")
                .addAsResource("jboss-deployment-structure.xml")
                .addAsResource(new ClassLoaderAsset("log4j.properties"), "log4j.properties")
                .addAsManifestResource("beans.xml");
    }

    @Test
    public void monitor() {
        assertThat(monitorer).isNotNull().as("monitorer");
        assertNotNull(monitorer);
        monitorer.doSomething();
        assertNotNull(progressObserver);
        assertFalse(progressObserver.hasProgress());
    }

}
