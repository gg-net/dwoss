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
package eu.ggnet.dwoss.receipt.ui.tryout;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.swing.JFrame;
import javax.swing.UIManager;

import eu.ggnet.dwoss.core.widget.cdi.WidgetProducers;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.receipt.ui.ProductUiBuilder;
import eu.ggnet.dwoss.receipt.ui.product.BasicView;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.*;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.impl.Swing;

/**
 *
 * @author lucas.huelsen
 */
public class BasicViewTryout {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SeContainerInitializer ci = SeContainerInitializer.newInstance();
        ci.addPackages(ReceiptTryout.class);
        ci.addPackages(WidgetProducers.class);
        ci.addPackages(true, ProductUiBuilder.class); // receipt.ui
        ci.disableDiscovery();
        SeContainer container = ci.initialize();
        Instance<Object> instance = container.getBeanManager().createInstance();
        
        RemoteDl remote = instance.select(RemoteDl.class).get();
        remote.add(UniqueUnitApi.class, new UniqueUnitApiStub());
                
        Saft saft = instance.select(Saft.class).get();
        saft.addOnShutdown(() -> container.close());

        UiCore.initGlobal(saft); // Transition.

        JFrame mainFrame = UiUtil.startup(() -> {
            return new BasicView();
        });

        saft.core(Swing.class).initMain(mainFrame);
    }
}
