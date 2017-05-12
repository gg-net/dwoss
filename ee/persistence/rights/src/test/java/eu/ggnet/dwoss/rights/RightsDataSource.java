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
package eu.ggnet.dwoss.rights;

import java.util.HashMap;

import javax.annotation.ManagedBean;
import javax.annotation.sql.DataSourceDefinition;
import javax.annotation.sql.DataSourceDefinitions;
import javax.enterprise.inject.Produces;

import eu.ggnet.dwoss.mandator.api.value.*;

import static eu.ggnet.dwoss.configuration.SystemConfig.*;
import static eu.ggnet.dwoss.rights.RightsDataSource.DSDRIVER_HSQLDB;
import static eu.ggnet.dwoss.rights.RightsDataSource.DSURL_PROPERTIES;

/**
 *
 * @author oliver.guenther
 */
@DataSourceDefinitions(
        value = {
            @DataSourceDefinition(name = DSNAME_SUFFIX + "rights" + DSNAME_PREFIX_MANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:rights" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=true"}
            )
            ,
            @DataSourceDefinition(name = DSNAME_SUFFIX + "rights" + DSNAME_PREFIX_UNMANAGED,
                                  className = DSDRIVER_HSQLDB,
                                  url = "jdbc:hsqldb:mem:rights" + DSURL_PROPERTIES,
                                  properties = {"JtaManaged=false"}
            )
        }
)
@ManagedBean
public class RightsDataSource {

    public final static String DSDRIVER_HSQLDB = "org.hsqldb.jdbc.JDBCDataSource";

    public final static String DSURL_PROPERTIES = ";hsqldb.sqllog=0";

    @Produces
    public static ReceiptCustomers c = new ReceiptCustomers(new HashMap<>());

    @Produces
    SpecialSystemCustomers sc = new SpecialSystemCustomers(new HashMap<>());

    @Produces
    ShippingTerms st = new ShippingTerms(new HashMap<>());

    @Produces
    PostLedger pl = new PostLedger(new HashMap<>());
}
