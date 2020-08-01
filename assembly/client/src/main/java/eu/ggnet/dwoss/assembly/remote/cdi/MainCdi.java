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
package eu.ggnet.dwoss.assembly.remote.cdi;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import eu.ggnet.dwoss.assembly.remote.cdi.CdiClient;
import eu.ggnet.dwoss.misc.ui.AboutController;

/**
 * Experiment: https://jira.cybertron.global/browse/DWOSS-335
 *
 * @author oliver.guenther
 */
public class MainCdi {

    public static class CdiApplication extends Application {

        @Override
        public void start(Stage stage) throws Exception {
            SeContainerInitializer ci = SeContainerInitializer.newInstance();
            ci.disableDiscovery();
            ci.addPackages(true, MainCdi.class);
            ci.addPackages(true, AboutController.class);
            try (SeContainer container = ci.initialize()) {
                CdiClient client = container.select(CdiClient.class).get();
                client.main();
                Parent root = client.root();
                stage.setScene(new Scene(root));
                stage.show();
            }
        }

    }

    public static void main(String[] args) {
        Application.launch(CdiApplication.class, args);
    }

}
