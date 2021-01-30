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
package tryout;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.Progressor;
import eu.ggnet.dwoss.core.widget.Progressor.Displayer;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.cdi.WidgetProducers;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ui.ProductTask;
import eu.ggnet.dwoss.uniqueunit.ui.cap.AddHistoryToUnitMenuItem;
import eu.ggnet.dwoss.uniqueunit.ui.cap.ProductListMenuItem;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.impl.Fx;

import tryout.stub.*;

/**
 *
 * @author oliver.guenther
 */
public class UniqueUnitTryout {

    private final static Package EU_GGNET_DWOSS_UNIQUEUNIT_UI = ProductTask.class.getPackage();

    public static class UniqueUnitApplication extends Application {

        @Override
        public void start(Stage stage) throws Exception {

            SeContainerInitializer ci = SeContainerInitializer.newInstance();
            ci.addPackages(WidgetProducers.class);
            ci.addPackages(UniqueUnitTryout.class);
            ci.addPackages(true, EU_GGNET_DWOSS_UNIQUEUNIT_UI);

            ci.disableDiscovery();
            SeContainer container = ci.initialize();
            Instance<Object> instance = container.getBeanManager().createInstance();

            Saft saft = instance.select(Saft.class).get();
            saft.addOnShutdown(() -> container.close());
            UiCore.initGlobal(saft);

            instance.select(Progressor.class).get().setDisplayer(new Displayer() {
                @Override
                public void submit(Task<?> task) {
                    try {
                        Future<?> future = saft.executorService().submit(task);
                        saft.executorService().submit(() -> {  // Exceptionrouter for task and future.
                            try {
                                future.get();
                                task.get();
                            } catch (InterruptedException | ExecutionException ex) {
                                saft.handle(ex);
                            }
                        });
                    } catch (Exception e) {
                        saft.handle(e);
                    }
                }
            });

            RemoteDl remote = instance.select(RemoteDl.class).get();
            remote.add(UniqueUnitAgent.class, new UniqueUnitAgentStub());
            remote.add(UniqueUnitApi.class, new UniqueUnitApiStub());

            Dl.local().add(Guardian.class, new GuardianStub());

            UiUtil.startup(stage, () -> {

                Menu m = new Menu("UniqueUnit MenuItems");
                m.getItems().addAll(
                        instance.select(ProductListMenuItem.class).get(),
                        instance.select(AddHistoryToUnitMenuItem.class).get()
                );

                return new MenuBar(m);
            });

            saft.core(Fx.class).initMain(stage);

        }

    }

    public static void main(String[] args) {
        Application.launch(UniqueUnitApplication.class, args);
    }

}
