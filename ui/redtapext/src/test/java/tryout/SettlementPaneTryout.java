/*
 * Copyright (C) 2023 GG-Net GmbH
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

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import eu.ggnet.dwoss.core.widget.cdi.WidgetProducers;
import eu.ggnet.dwoss.redtapext.ui.ReactivePicoUnitDetailViewCask;
import eu.ggnet.dwoss.redtapext.ui.cao.stateaction.SettlementPane;
import eu.ggnet.saft.core.*;

/**
 *
 * @author oliver.guenther
 */
public class SettlementPaneTryout {

    public static class SettlementPaneTryoutApplication extends Application {

        private Saft saft;

        @Override
        public void init() throws Exception {
            SeContainerInitializer ci = SeContainerInitializer.newInstance();
            ci.addPackages(SettlementPaneTryout.class);
            ci.addPackages(WidgetProducers.class);
            ci.addPackages(true, ReactivePicoUnitDetailViewCask.class);
            ci.disableDiscovery();
            SeContainer container = ci.initialize();
            Instance<Object> instance = container.getBeanManager().createInstance();
            
            saft = instance.select(Saft.class).get();
            saft.addOnShutdown(() -> container.close());
            UiCore.initGlobal(saft);
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            Stage s = UiUtil.startup(primaryStage, () -> {
                Button b = new Button("Settlement");
                b.setOnAction(e -> saft.build().fx().eval(SettlementPane.class).cf().thenAccept(System.out::println));
                return b;
            });
            s.show();
        }

    }

    public static void main(String[] args) {

        Application.launch(SettlementPaneTryoutApplication.class);
    }

}
