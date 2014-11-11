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
package eu.ggnet.dwoss.assembly.local.cos;

import eu.ggnet.saft.core.Server;

import java.util.*;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.*;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.configuration.ConfigurationProvider;
import eu.ggnet.dwoss.configuration.SystemConfig;


import eu.ggnet.dwoss.assembly.local.UnClosableContext;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.report.assist.ReportPu;
import eu.ggnet.dwoss.rights.assist.RightsPu;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;
import eu.ggnet.dwoss.util.dialog.Alert;

@ServiceProvider(service = Server.class)
public class ServerConnCosLocal implements Server {

    private EJBContainer container;

    private boolean isInitialized = false;

    public ServerConnCosLocal() {
    }

    @Override
    public synchronized void initialise() {
        if ( isInitialized ) return;
        isInitialized = true;
        Map<String, Object> c = new HashMap<>();
        ConfigurationProvider config = Lookup.getDefault().lookup(ConfigurationProvider.class);
        if ( config == null ) {
            Alert.builder()
                    .title("No Configuration Provider found")
                    .body("No Configuration Provider found!\nCheck your assembly!\nFalling back to Sample!")
                    .build().showAsError();
            c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
            c.putAll(CustomerPu.CMP_IN_MEMORY);
            c.putAll(StockPu.CMP_IN_MEMORY);
            c.putAll(SpecPu.CMP_IN_MEMORY);
            c.putAll(RedTapePu.CMP_IN_MEMORY);
            c.putAll(ReportPu.CMP_IN_MEMORY);
            c.putAll(RightsPu.CMP_IN_MEMORY);
        } else {
            c.putAll(config.openejbEmbeddedLocalConfiguration());
        }
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_TESTING);
        container = EJBContainer.createEJBContainer(c);
        try {
            container.getContext().bind("inject", this);
        } catch (NamingException ex) {
            throw new RuntimeException("CDI Injection Problem while contaier init", ex);
        }
    }

    @Override
    public synchronized void shutdown() {
        container.close();
    }

    @Override
    public Context getContext() {
        initialise();
        return new UnClosableContext(container.getContext());
    }
}
