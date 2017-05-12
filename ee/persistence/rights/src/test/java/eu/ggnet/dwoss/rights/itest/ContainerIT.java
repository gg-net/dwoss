package eu.ggnet.dwoss.rights.itest;

import java.io.File;
import java.util.HashMap;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.enterprise.inject.Produces;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.rights.RightsAgent;
import eu.ggnet.dwoss.rights.RightsDataSource;
import eu.ggnet.dwoss.rights.entity.Operator;
import eu.ggnet.dwoss.rights.entity.Persona;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ANNULATION_INVOICE;
import static java.lang.Package.getPackage;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.jboss.shrinkwrap.resolver.api.maven.ScopeType.RUNTIME;

/**
 *
 * @author Bastian Venz
 */
@RunWith(Arquillian.class)
@ManagedBean
public class ContainerIT {

    @EJB
    private RightsAgent agent;

    @Deployment
    public static WebArchive createDeployment() {
        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .importRuntimeDependencies()
                .addDependency(MavenDependencies.createDependency("eu.ggnet.dwoss:dwoss-mandator-sample", RUNTIME, false))
                .resolve().withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "rights-container.war")
                .addPackages(true, Filters.exclude(getPackage("eu.ggnet.dwoss.rights.itest")), "eu.ggnet.dwoss.rights")
                .addClass(RightsDataSource.class)
                .addAsResource(new ClassLoaderAsset("META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsLibraries(libs)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testPrePersist() {
        assertThat(agent).as("RightsAgent").isNotNull();
        Operator op = new Operator("TestUser");
        Persona p = new Persona("Testpersona");
        p.add(CREATE_ANNULATION_INVOICE);
        p = agent.store(p);
        op.add(p);
        op = agent.store(op);
        // Now we have one operator with one persona with one right.
        op.add(CREATE_ANNULATION_INVOICE); // adding the same right to the operator
        op = agent.store(op); // This should clear the duplicated right.
        assertThat(op.getRights()).contains(CREATE_ANNULATION_INVOICE).as("The Operator should not have any right, cause its duplicate of the persona. Rights=" + op + ",personas=" + op.getPersonas());
    }

}
