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
package eu.ggnet.dwoss.mandator.sample.datasource;


import jakarta.annotation.ManagedBean;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.annotation.sql.DataSourceDefinitions;

import static eu.ggnet.dwoss.mandator.sample.datasource.SampleDataSourceDefinition.*;

/**
 * In memory data sources for the sample tryout.
 *
 * @author oliver.guenther
 */
@DataSourceDefinitions(
        value = {
            // <editor-fold defaultstate="collapsed" desc="report">
            @DataSourceDefinition(name = DSNAME_PREFIX + "report" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER, password = "sa", user = "sa",
                                  url = "jdbc:h2:mem:report" + DSURL_PROPERTIES
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="redtape">

            @DataSourceDefinition(name = DSNAME_PREFIX + "redtape" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER, password = "sa", user = "sa",
                                  url = "jdbc:h2:mem:redtape" + DSURL_PROPERTIES
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="uniqueunit">
            @DataSourceDefinition(name = DSNAME_PREFIX + "uniqueunit" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER, password = "sa", user = "sa",
                                  url = "jdbc:h2:mem:uniqueunit" + DSURL_PROPERTIES
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="spec">
            @DataSourceDefinition(name = DSNAME_PREFIX + "spec" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER, password = "sa", user = "sa",
                                  url = "jdbc:h2:mem:spec" + DSURL_PROPERTIES
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="stock">
            @DataSourceDefinition(name = DSNAME_PREFIX + "stock" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER, password = "sa", user = "sa",
                                  url = "jdbc:h2:mem:stock" + DSURL_PROPERTIES
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="rights">
            @DataSourceDefinition(name = DSNAME_PREFIX + "rights" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER, password = "sa", user = "sa",
                                  url = "jdbc:h2:mem:rights" + DSURL_PROPERTIES
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="customer">
            @DataSourceDefinition(name = DSNAME_PREFIX + "customer" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER, password = "sa", user = "sa",
                                  url = "jdbc:h2:mem:customer" + DSURL_PROPERTIES
            )
            ,
            // </editor-fold>
            // Special datasources
            // <editor-fold defaultstate="collapsed" desc="repair">
            @DataSourceDefinition(name = DSNAME_PREFIX + "repair" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER, password = "sa", user = "sa",
                                  url = "jdbc:h2:mem:repair" + DSURL_PROPERTIES
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="sopo">
            @DataSourceDefinition(name = DSNAME_PREFIX + "sopo" + DSNAME_SUFFIX_MANAGED,
                                  className = DSDRIVER, password = "sa", user = "sa",
                                  url = "jdbc:h2:mem:sopo" + DSURL_PROPERTIES
            ), // </editor-fold>
        }
)
@ManagedBean
public class SampleDataSourceDefinition {

    public final static String DSDRIVER = "org.h2.jdbcx.JdbcDataSource";

    public final static String DSURL_PROPERTIES = ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

    /**
     * Default suffix for the managed datasource.
     * After the switch to tomee 7 oder even wildfly, someone might have a look into this. The tomee 1.7 needs allways both datasources defined.
     */
    public final static String DSNAME_SUFFIX_MANAGED = "DataSource";

    /**
     * Default prefix for all datasources.
     * A global prefix change might be needed on a container change. Saw that on first experiments with wildfly.
     */
    public final static String DSNAME_PREFIX = "java:comp/env/";

}
