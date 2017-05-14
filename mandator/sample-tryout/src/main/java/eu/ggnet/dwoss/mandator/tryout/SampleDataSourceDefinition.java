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
package eu.ggnet.dwoss.mandator.tryout;

import javax.annotation.sql.DataSourceDefinition;
import javax.annotation.sql.DataSourceDefinitions;

import static eu.ggnet.dwoss.configuration.SystemConfig.*;
import static eu.ggnet.dwoss.mandator.tryout.SampleDataSourceDefinition.DSDRIVER_HSQLDB;
import static eu.ggnet.dwoss.mandator.tryout.SampleDataSourceDefinition.DSURL_PROPERTIES;

/**
 * In memory data sources for the sample tryout.
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
            )
            ,
            // </editor-fold>
            // Special datasources
            // <editor-fold defaultstate="collapsed" desc="repair">
            @DataSourceDefinition(name = DSNAME_PREFIX + "repair" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:repair" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = DSNAME_PREFIX + "repair" + DSNAME_SUFFIX_UNMANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:repair" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="sopo">
            @DataSourceDefinition(name = DSNAME_PREFIX + "sopo" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:sopo" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = DSNAME_PREFIX + "sopo" + DSNAME_SUFFIX_UNMANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:sopo" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=false"}
            )
        // </editor-fold>

        }
)
public class SampleDataSourceDefinition {

    public final static String DSDRIVER_HSQLDB = "org.hsqldb.jdbc.JDBCDataSource";

    public final static String DSURL_PROPERTIES = ";hsqldb.sqllog=0";

}
