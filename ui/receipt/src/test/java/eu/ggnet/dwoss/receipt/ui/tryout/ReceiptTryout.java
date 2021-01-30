/*
 * Copyright (C) 2021 GG-Net GmbH
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.swing.*;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.cdi.WidgetProducers;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.core.widget.dl.RemoteLookup;
import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.receipt.ee.*;
import eu.ggnet.dwoss.receipt.ui.UiUnitSupport;
import eu.ggnet.dwoss.receipt.ui.cap.*;
import eu.ggnet.dwoss.receipt.ui.shipment.ShipmentEditView;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.*;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.spi.ActiveStock;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.impl.Swing;

/**
 *
 * @author oliver.guenther
 */
public class ReceiptTryout {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SeContainerInitializer ci = SeContainerInitializer.newInstance();
        ci.addPackages(ReceiptTryout.class);
        ci.addPackages(WidgetProducers.class);
        ci.addPackages(true, UiUnitSupport.class); // receipt.ui
        ci.disableDiscovery();
        SeContainer container = ci.initialize();
        Instance<Object> instance = container.getBeanManager().createInstance();

        Saft saft = instance.select(Saft.class).get();
        saft.addOnShutdown(() -> container.close());

        UiCore.initGlobal(saft); // Transition.

        RemoteDl remote = instance.select(RemoteDl.class).get();
        ProductProcessorStub pps = new ProductProcessorStub();
        remote.add(ProductProcessor.class, pps);
        remote.add(SpecAgent.class, pps.getSpecAgentStub());
        remote.add(StockAgent.class, new StockAgentStub());
        remote.add(Mandators.class, new MandatorsStub());
        remote.add(UnitProcessor.class, new UnitProcessorStub());
        remote.add(UnitSupporter.class, new UnitSupporterStub());

        Dl.local().add(CachedMandators.class, new MandatorsStub());
        Dl.local().add(RemoteLookup.class, new RemoteLookupStub());
        Dl.local().add(Guardian.class, new GuardianStub());

        StockSpiStub su = new StockSpiStub();
        su.setActiveStock(new PicoStock(0, "Demostock"));
        Dl.local().add(ActiveStock.class, su);

        JFrame mainFrame = UiUtil.startup(() -> {
            JMenuBar menubar = new JMenuBar();

            JMenu receipt = new JMenu("Aufnahme");
            receipt.add(instance.select(OpenCpuListAction.class).get());
            receipt.add(instance.select(OpenGpuListAction.class).get());
            receipt.add(instance.select(OpenSpecListAction.class).get());
            receipt.add(instance.select(OpenShipmentListAction.class).get());
            menubar.add(receipt);

            JButton openShipmentUpdateView = new JButton("Open ShipmentUpdateView");
            openShipmentUpdateView.addActionListener(a -> saft.build().fx().eval(ShipmentEditView.class).cf().thenAccept(System.out::println));

            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(openShipmentUpdateView);

            JPanel main = new JPanel(new BorderLayout());
            main.add(menubar, BorderLayout.NORTH);
            main.add(buttonPanel, BorderLayout.CENTER);

            return main;
        });

        saft.core(Swing.class).initMain(mainFrame);

    }

}
