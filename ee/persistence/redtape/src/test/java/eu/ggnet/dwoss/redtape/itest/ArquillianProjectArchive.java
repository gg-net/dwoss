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
package eu.ggnet.dwoss.redtape.itest;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.Coordinate;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;

import eu.ggnet.dwoss.mandator.sample.datasource.SampleDataSourceDefinition;
import eu.ggnet.dwoss.redtape.itest.eao.RedTapeHelper;
import eu.ggnet.dwoss.redtape.test.PositionTest;

import static org.jboss.shrinkwrap.resolver.api.maven.ScopeType.RUNTIME;

/**
 * Extend for Arquillian as deployer.
 *
 * @author oliver.guenther
 */
public class ArquillianProjectArchive {

    @Deployment
    public static WebArchive createDeployment() {
        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .importRuntimeDependencies()
                .addDependency(MavenDependencies.createDependency("eu.ggnet.dwoss:dwoss-mandator-sample", RUNTIME, false)) // The Sample Mandator is needed on many places.
                .addDependency(MavenDependencies.createDependency("org.assertj:assertj-core", RUNTIME, false)) // Fest assertion
                .resolve().withTransitivity().asFile();
        WebArchive war = ShrinkWrap.create(WebArchive.class, "redtape-persistence-test.war")
                .addPackages(true,
                        Filters.exclude(PersistenceIT.class.getPackage(), // Compile safe package "eu.ggnet.dwoss.redtape.itest"
                                PositionTest.class.getPackage()), // Compile safe package "eu.ggnet.dwoss.redtape.test"
                        "eu.ggnet.dwoss.redtape")
                .addClass(MandatorSupportProducer.class) // The Datasource Configuration and the Static Producers
                .addClass(Coordinate.class) // Need this cause of the maven resolver is part of the deployment
                .addClass(SampleDataSourceDefinition.class) // Alle Datasources. More than we need.
                .addClass(ArquillianProjectArchive.class) // The local deployer configuration
                .addClass(RedTapeHelper.class)
                .addAsResource(new ClassLoaderAsset("META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsWebInfResource("jboss-deployment-structure.xml") // Needed for jboss/wildfly h2 enablement
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(libs);
        return war;
    }
}
