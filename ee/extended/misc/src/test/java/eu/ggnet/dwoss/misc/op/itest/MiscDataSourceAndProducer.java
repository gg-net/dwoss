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
package eu.ggnet.dwoss.misc.op.itest;

import javax.annotation.ManagedBean;
import javax.annotation.sql.DataSourceDefinition;
import javax.annotation.sql.DataSourceDefinitions;
import javax.enterprise.inject.Produces;

import eu.ggnet.dwoss.util.ImageFinder;

import static eu.ggnet.dwoss.configuration.SystemConfig.*;

/**
 * Default datasource definition and empty mandator support informations for tests
 *
 * @author oliver.guenther
 */
@DataSourceDefinitions(
        value = {
            // <editor-fold defaultstate="collapsed" desc="report">
            @DataSourceDefinition(name = DSNAME_PREFIX + "report" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:report" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = DSNAME_PREFIX + "report" + DSNAME_SUFFIX_UNMANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:report" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="redtape">

            @DataSourceDefinition(name = DSNAME_PREFIX + "redtape" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:redtape" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = DSNAME_PREFIX + "redtape" + DSNAME_SUFFIX_UNMANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:redtape" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="uniqueunit">
            @DataSourceDefinition(name = DSNAME_PREFIX + "uniqueunit" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:uniqueunit" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = DSNAME_PREFIX + "uniqueunit" + DSNAME_SUFFIX_UNMANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:uniqueunit" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="spec">
            @DataSourceDefinition(name = DSNAME_PREFIX + "spec" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:spec" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = DSNAME_PREFIX + "spec" + DSNAME_SUFFIX_UNMANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:spec" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="stock">
            @DataSourceDefinition(name = DSNAME_PREFIX + "stock" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:stock" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = DSNAME_PREFIX + "stock" + DSNAME_SUFFIX_UNMANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:stock" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="rights">
            @DataSourceDefinition(name = DSNAME_PREFIX + "rights" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:rights" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = DSNAME_PREFIX + "rights" + DSNAME_SUFFIX_UNMANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:rights" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="customer">
            @DataSourceDefinition(name = DSNAME_PREFIX + "customer" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:customer" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = DSNAME_PREFIX + "customer" + DSNAME_SUFFIX_UNMANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:customer" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=false"}
            ), // </editor-fold>
        }
)
@ManagedBean
public class MiscDataSourceAndProducer {

    @Produces
    public final static ImageFinder NULL_IMAGE_FINDER = new ImageFinder(null);

}
