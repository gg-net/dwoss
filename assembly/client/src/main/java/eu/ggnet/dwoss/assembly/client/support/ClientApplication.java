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
package eu.ggnet.dwoss.assembly.client.support;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import eu.ggnet.dwoss.assembly.client.Main;
import eu.ggnet.dwoss.assembly.remote.MainCdi;
import eu.ggnet.dwoss.assembly.remote.cdi.CdiClient;
import eu.ggnet.dwoss.assembly.remote.cdi.FxmlLoaderInitializer;
import eu.ggnet.dwoss.misc.ui.AboutController;

/**
 *
 * @author oliver.guenther
 */
public class ClientApplication extends Application {

    private SeContainer container;

    private ClientMainController mainController;

    private Parent mainView;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(mainView));
        stage.show();
        mainController.add(new Menu("Testmenu"));
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                container = initContainer();
            }

        }).thenRunAsync(new Runnable() {
            @Override
            public void run() {
                MenuItem m = new MenuItem("Hallo Welt");
                Menu mu = new Menu("Hallo");
                mu.getItems().add(m);
                mainController.add(mu);
            }

        }, Platform::runLater);
        // show minimal ui
        // Open blocking login (will wait for connection completion, if you hit return, that authenticate)
        // fill menu, Toolbar, Main
        // Connect to server, Preload, Discovery e.t.c
    }

    @Override
    public void init() throws Exception {
        // Load the minimal Ui, so something gets visible.
        FXMLLoader loader = new FXMLLoader(ClientMainController.class.getResource("ClientMainView.fxml"));
        mainView = loader.load();
        mainController = loader.getController();
    }

    @Override
    public void stop() throws Exception {
        if ( container.isRunning() ) container.close();
    }

    public static SeContainer initContainer() {
        SeContainerInitializer ci = SeContainerInitializer.newInstance();
        ci.disableDiscovery();
        ci.addPackages(true, MainCdi.class);
        ci.addPackages(true, Main.class);
        ci.addPackages(true, AboutController.class);
        return ci.initialize();
    }

}
