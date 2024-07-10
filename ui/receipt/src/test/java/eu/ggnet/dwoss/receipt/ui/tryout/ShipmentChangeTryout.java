/*
 * Copyright (C) 2020 GG-Net GmbH
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

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import javafx.application.Application;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.cdi.WidgetProducers;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.receipt.ui.ProductUiBuilder;
import eu.ggnet.dwoss.receipt.ui.cap.ShipmentChangeMenuItem;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.*;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.impl.Fx;

/**
 * This tryout allows testing the functionality of the Rights module seperated from the application.
 *
 * @author oliver.guenther
 */
public class ShipmentChangeTryout {

    public static class ShipmentChangeApplication extends Application {

        @Override
        public void start(Stage stage) throws Exception {
            SeContainerInitializer ci = SeContainerInitializer.newInstance();
            ci.addPackages(ReceiptTryout.class);
            ci.addPackages(WidgetProducers.class);
            ci.addPackages(true, ProductUiBuilder.class); // receipt.ui
            ci.disableDiscovery();
            SeContainer container = ci.initialize();
            Instance<Object> instance = container.getBeanManager().createInstance();

            Saft saft = instance.select(Saft.class).get();
            UiCore.initGlobal(saft);
            saft.addOnShutdown(() -> container.close());

            RemoteDl remote = instance.select(RemoteDl.class).get();
            remote.add(StockApi.class, new StockApiStub());
            remote.add(UniqueUnitApi.class, new UniqueUnitApiStub());

            Dl.local().add(Guardian.class, new GuardianStub());
            
            UiUtil.startup(stage, () -> {
                Menu m = new Menu("Shipment");
                m.getItems().addAll(instance.select(ShipmentChangeMenuItem.class).get());
                return new MenuBar(m);
            });

            saft.core(Fx.class).initMain(stage);
        }
    }

    public static void main(String[] args) {
        Application.launch(ShipmentChangeApplication.class, args);
    }
}
