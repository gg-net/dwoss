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
package tryout;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import javafx.application.Application;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import eu.ggnet.dwoss.core.widget.cdi.WidgetProducers;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.rights.api.GroupApi;
import eu.ggnet.dwoss.rights.api.UserApi;
import eu.ggnet.dwoss.rights.ui.RightsManagementController;
import eu.ggnet.dwoss.rights.ui.cap.RightsManagementMenuItem;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.impl.Fx;

import tryout.stub.GroupApiStub;
import tryout.stub.UserApiStub;

/**
 * This tryout allows testing the functionality of the Rights module seperated from the application.
 *
 * @author mirko.schulze
 */
public class RightsManagementControllerTryout {

    public static class RightsManagementControllerApplication extends Application {

        @Override
        public void start(Stage stage) throws Exception {
            SeContainerInitializer ci = SeContainerInitializer.newInstance();
            ci.addPackages(RightsManagementControllerTryout.class);
            ci.addPackages(WidgetProducers.class);
            ci.addPackages(true, RightsManagementController.class);
            ci.disableDiscovery();
            SeContainer container = ci.initialize();
            Instance<Object> instance = container.getBeanManager().createInstance();

            Saft saft = instance.select(Saft.class).get();
            UiCore.initGlobal(saft);
            saft.addOnShutdown(() -> container.close());

            RemoteDl remote = instance.select(RemoteDl.class).get();
            remote.add(UserApi.class, new UserApiStub());
            remote.add(GroupApi.class, new GroupApiStub());

            UiUtil.startup(stage, () -> {
                Menu m = new Menu("Rechte");
                m.getItems().addAll(instance.select(RightsManagementMenuItem.class).get());
                return new MenuBar(m);
            });

            saft.core(Fx.class).initMain(stage);
        }
    }
    
    public static void main(String[] args) {
            Application.launch(RightsManagementControllerApplication.class, args);
        }
}
