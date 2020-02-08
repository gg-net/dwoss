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

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import eu.ggnet.dwoss.assembly.client.Main;
import eu.ggnet.dwoss.assembly.remote.MainCdi;
import eu.ggnet.dwoss.assembly.remote.cdi.FxmlLoaderInitializer;
import eu.ggnet.dwoss.core.widget.AbstractGuardian;
import eu.ggnet.dwoss.misc.ui.AboutController;
import eu.ggnet.saft.experimental.auth.AuthenticationException;

/**
 *
 * @author oliver.guenther
 */
public class ClientApplication extends Application implements FirstLoginListener {

    private SeContainer container;

    private Stage loginStage;

    private Label info;

    private Parent mainView;

    private Instance<Object> instance;

    @Override
    public void start(Stage primaryStage) throws Exception {

        info = new Label("Info here");
        StackPane mainPane = new StackPane(info);
        mainPane.setPrefSize(800, 600);

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();

        FXMLLoader loader = new FXMLLoader(FirstLoginController.class.getResource("FirstLoginView.fxml"));
        Parent loginView = loader.load();
        FirstLoginController loginController = loader.getController();
        loginController.setLoginListener(this);
        loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.initOwner(primaryStage);
        loginStage.setScene(new Scene(loginView));
        loginStage.show();

        CompletableFuture
                .runAsync(this::postInit)
                .thenRun(() -> { // For now only a Stub, so I don't need the running server
                    loginController.setAndActivateGuardian(new AbstractGuardian() {
                        @Override
                        public void login(String user, char[] pass) throws AuthenticationException {
                            if ( "max".equalsIgnoreCase(user) && "pass".equals(String.valueOf(pass)) ) return; // success
                            System.out.println("User:" + user + "|Pass:" + Arrays.toString(pass));
                            throw new AuthenticationException("User or Pass wrong");
                        }
                    });
                })
                .thenRunAsync(() -> {
                    // Replace Mainview, later kick in Swing temporyry
                    // And remember to relocate
                    mainPane.getChildren().clear(); // remove everything
                    mainPane.getChildren().add(mainView);
                }, Platform::runLater);
    }

    @Override
    public void stop() throws Exception {
        if ( container.isRunning() ) container.close();
    }

    /**
     * init after start
     */
    public void postInit() {
        SeContainerInitializer ci = SeContainerInitializer.newInstance();
        ci.disableDiscovery();
        ci.addPackages(true, MainCdi.class);
        ci.addPackages(true, Main.class);
        ci.addPackages(true, AboutController.class); // misc.ui
        container = ci.initialize();
        // TODO: Remote connection and everything else.
        instance = container.getBeanManager().createInstance();

        // TODO: Here we will have Saft already.
        FXMLLoader mainLoader = instance.select(FxmlLoaderInitializer.class).get().createLoader(ClientMainController.class.getResource("ClientMainView.fxml"));
        try {
            mainLoader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        mainView = mainLoader.getRoot();

        ClientMainController mainController = mainLoader.getController();
    }

    @Override
    public void loginSuccessful() {
        loginStage.close();
    }

    @Override
    public void shutdown() {
        Platform.exit();
    }

}
