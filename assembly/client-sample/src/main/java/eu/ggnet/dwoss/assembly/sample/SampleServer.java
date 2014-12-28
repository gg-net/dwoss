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
package eu.ggnet.dwoss.assembly.sample;

import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.swing.SwingWorker;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.configuration.ConfigurationProvider;
import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.redtape.gen.RedTapeGeneratorOperation;
import eu.ggnet.dwoss.report.assist.ReportPu;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.assist.RightsPu;
import eu.ggnet.dwoss.rights.assist.gen.RightsGeneratorOperation;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.stock.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;
import eu.ggnet.saft.core.Server;

@ServiceProvider(service = Server.class)
public class SampleServer implements Server {

    private EJBContainer container;

    private boolean isInitialized = false;

    private final Map<String, Object> containerProperties;

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @Inject
    private StockGeneratorOperation stockGenerator;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private RedTapeGeneratorOperation redTapeGenerator;

    @Inject
    private RightsGeneratorOperation rightsGenerator;

    public SampleServer() {
        Map<String, Object> c = new HashMap<>();
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(RedTapePu.CMP_IN_MEMORY);
        c.putAll(ReportPu.CMP_IN_MEMORY);
        c.putAll(RightsPu.CMP_IN_MEMORY);
        ConfigurationProvider config = Lookup.getDefault().lookup(ConfigurationProvider.class);
        if ( config != null ) c.putAll(config.openejbAddToEmbeddedSampleConfiguration());
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_TESTING_WITHOUT_JPA);
        c.putAll(SystemConfig.OPENEJB_XBEAN_FINDER);
        containerProperties = c;
    }

    @Override
    public synchronized void initialise() {
        if ( isInitialized ) return;
        container = EJBContainer.createEJBContainer(containerProperties);
        isInitialized = true;
        try {
            container.getContext().bind("inject", this);
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                rightsGenerator.make("admin", EnumSet.allOf(AtomicRight.class));
                rightsGenerator.make("user", EnumSet.noneOf(AtomicRight.class));
                stockGenerator.makeStocksAndLocations(2);
                customerGenerator.makeCustomers(100);
                receiptGenerator.makeProductSpecs(30, true);
                receiptGenerator.makeUniqueUnits(200, true, true);
                redTapeGenerator.makeSalesDossiers(50);
                ConfigurationProvider config = Lookup.getDefault().lookup(ConfigurationProvider.class);
                if ( config != null ) config.initializeSample();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }.execute();

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
