/*
 * Copyright (C) 2017 GG-Net GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.redtape.op.itest.support;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.Coordinate;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;

import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.op.itest.InjectIT;

import static org.jboss.shrinkwrap.api.Filters.exclude;
import static org.jboss.shrinkwrap.resolver.api.maven.ScopeType.RUNTIME;

/**
 * Extend for Arquillian as deployer.
 *
 * @author oliver.guenther
 */
public class ArquillianProjectArchive {

    @Deployment
    public static WebArchive createDeployment() {
        // Compile Safe Packages.
        Package projectPackage = RedTapeWorker.class.getPackage();
        Package itestPackage = InjectIT.class.getPackage();

        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .importRuntimeDependencies()
                .addDependency(MavenDependencies.createDependency("eu.ggnet.dwoss:dwoss-mandator-sample", RUNTIME, false)) // The Sample Mandator
                .addDependency(MavenDependencies.createDependency("eu.ggnet.dwoss:dwoss-mandator-sample-service", RUNTIME, false)) // The Sample Mandator Services
                .addDependency(MavenDependencies.createDependency("eu.ggnet.dwoss:dwoss-ee-extended-receipt", RUNTIME, false)) // Using Receipt for unit generation
                .addDependency(MavenDependencies.createDependency("org.slf4j:slf4j-log4j12", RUNTIME, false)) // Log4J API
                .addDependency(MavenDependencies.createDependency("org.easytesting:fest-assert-core", RUNTIME, false)) // Fest assertion
                .resolve().withTransitivity().asFile();
        WebArchive war = ShrinkWrap.create(WebArchive.class, "receipt-persistence-test.war")
                .addPackages(true, exclude(itestPackage), projectPackage)
                .addClass(RedtapeDataSourceAndProducer.class) // The Datasource Configuration and the Static Producers
                .addClass(Coordinate.class) // Need this cause of the maven resolver is part of the deployment
                .addClass(ArquillianProjectArchive.class) // The local deployer configuration
                .addClass(SupportBean.class)
                .addClass(NaivBuilderUtil.class)
                .addClass(DatabaseCleaner.class)
                .addClass(WarrantyServiceStup.class)
                .addAsResource(new ClassLoaderAsset("META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsResource(new ClassLoaderAsset("log4j.properties"), "log4j.properties")
                .addAsResource("eu/ggnet/dwoss/redtape/Document_Template.jrxml")
                .addAsResource("eu/ggnet/dwoss/redtape/Shipping_Template.jrxml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(libs);
        return war;
    }
}
