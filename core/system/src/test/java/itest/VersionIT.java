package itest;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.arquillian.junit5.ArquillianExtension;

import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.dwoss.core.system.version.Version;
import eu.ggnet.dwoss.core.system.version.VersionBean;

import jakarta.ejb.EJB;

@ExtendWith(ArquillianExtension.class)
public class VersionIT {

    @EJB
    private Version version;
    
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "version.jar")
                .addClasses(GlobalConfig.class,Version.class,VersionBean.class)
                .addPackages(true, "org.assertj")
                .addPackages(true, "org.slf4j")
                .addAsResource("jboss-deployment-structure.xml");
    }

    @Test
    public void monitor() {
        assertThat(version.api()).as("Api Version").isEqualTo(GlobalConfig.API_VERSION);
    }

}
