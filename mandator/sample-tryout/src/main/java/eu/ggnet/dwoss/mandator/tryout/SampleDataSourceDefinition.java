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

/**
 * In memory data sources for the sample tryout.
 *
 * @author oliver.guenther
 */
@DataSourceDefinitions(
        value = {
            // <editor-fold defaultstate="collapsed" desc="report">
            @DataSourceDefinition(name = "java:comp/env/reportDataSource",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:report",
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = "java:comp/env/reportDataSourceUnmanaged",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:report",
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="redtape">

            @DataSourceDefinition(name = "java:comp/env/redtapeDataSource",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:redtape",
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = "java:comp/env/redtapeDataSourceUnmanaged",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:redtape",
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="uniqueunit">
            @DataSourceDefinition(name = "java:comp/env/uniqueunitDataSource",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:uniqueunit",
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = "java:comp/env/uniqueunitDataSourceUnmanaged",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:uniqueunit",
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="spec">
            @DataSourceDefinition(name = "java:comp/env/specDataSource",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:spec",
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = "java:comp/env/specDataSourceUnmanaged",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:spec",
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="stock">
            @DataSourceDefinition(name = "java:comp/env/stockDataSource",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:stock",
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = "java:comp/env/stockDataSourceUnmanaged",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:stock",
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="rights">
            @DataSourceDefinition(name = "java:comp/env/rightsDataSource",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:rights",
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = "java:comp/env/rightsDataSourceUnmanaged",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:rights",
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="customer">
            @DataSourceDefinition(name = "java:comp/env/customerDataSource",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:customer",
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = "java:comp/env/customerDataSourceUnmanaged",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:customer",
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // Special datasources
            // <editor-fold defaultstate="collapsed" desc="repair">
            @DataSourceDefinition(name = "java:comp/env/repairDataSource",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:repair",
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = "java:comp/env/repairDataSourceUnmanaged",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:repair",
                                  properties = {"JtaManaged=false"}
            )
            ,
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="sopo">
            @DataSourceDefinition(name = "java:comp/env/sopoDataSource",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:sopo",
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = "java:comp/env/sopoDataSourceUnmanaged",
                                  className = "org.hsqldb.jdbcDriver",
                                  user = "sa",
                                  password = "",
                                  url = "jdbc:hsqldb:mem:sopo",
                                  properties = {"JtaManaged=false"}
            )
        // </editor-fold>

        }
)
public class SampleDataSourceDefinition {

}
