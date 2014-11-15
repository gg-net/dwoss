/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.uniqueunit.assist;

import java.util.Map;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;

import eu.ggnet.dwoss.util.persistence.JpaPersistenceConfiguration;
import eu.ggnet.dwoss.util.persistence.OpenEjbEmbeddedPersistenceConfiguration;

public class UniqueUnitPu {

    public final static String NAME = "uniqueunit-pu";

    public final static String DATASOURCE = "uniqueunitDataSource";

    public final static String DATASOURCE_UNMANAGED = "uniqueunitDataSourceUnmanaged";

    public final static OpenEjbEmbeddedPersistenceConfiguration OPENEJB_CONFIG = OpenEjbEmbeddedPersistenceConfiguration.builder()
            .persistenceUnit(NAME).dataSourceManaged(DATASOURCE).dataSourceUnmanaged(DATASOURCE_UNMANAGED).build();

    public static Map<String, String> JPA_IN_MEMORY = JpaPersistenceConfiguration.asHsqldbInMemory(NAME);

    public static Map<String, String> CMP_IN_MEMORY = OPENEJB_CONFIG.asHsqlInMemory();

    @PersistenceUnit(unitName = NAME)
    @Produces
    @UniqueUnits
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext(unitName = NAME)
    @Produces
    @UniqueUnits
    private EntityManager entityManager;

    @Resource(name = DATASOURCE)
    @Produces
    @UniqueUnits
    private DataSource dataSource;
}
