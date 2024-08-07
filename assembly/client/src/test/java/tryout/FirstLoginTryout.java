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

import java.util.Arrays;
import java.util.concurrent.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import eu.ggnet.dwoss.assembly.client.support.login.LoginScreenConfiguration;
import eu.ggnet.dwoss.assembly.client.support.login.LoginScreenController;
import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.dwoss.core.system.version.Version;
import eu.ggnet.dwoss.core.widget.AbstractGuardian;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.AuthenticationException;

/**
 *
 * @author oliver.guenther
 */
public class FirstLoginTryout {

    public static class FirstLoginApplication extends Application {

        private Stage loginStage;

        private Label info;

        private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

        @Override
        public void start(Stage primaryStage) throws Exception {

            Dl.remote().add(Version.class, (Version)() -> GlobalConfig.API_VERSION+1);

            info = new Label("Info here");
            StackPane mainPane = new StackPane(info);
            mainPane.setPrefSize(800, 600);

            primaryStage.setScene(new Scene(mainPane));
            primaryStage.show();

            FXMLLoader loader = new FXMLLoader(LoginScreenController.class.getResource("LoginScreenView.fxml"));
            Parent root = loader.load();
            LoginScreenController controller = loader.getController();
            controller.accept(
                    new LoginScreenConfiguration.Builder()
                            .onSuccess(p -> {
                                System.out.println("success");
                                System.exit(0);
                            })
                            .onCancel(() -> {
                                System.out.println("cancel");
                                System.exit(0);
                            })
                            .build()
            );

            loginStage = new Stage();
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.initOwner(primaryStage);
            loginStage.setScene(new Scene(root));
            loginStage.setOnCloseRequest(e -> controller.closed());
            loginStage.show();

            // Simulate slowness
            ses.schedule(() -> {
                controller.setAndActivateGuardianAndRemote(new AbstractGuardian() {
                    @Override
                    public void login(String user, char[] pass) throws AuthenticationException {
                        if ( "max".equalsIgnoreCase(user) && "pass".equals(String.valueOf(pass)) ) return; // success
                        System.out.println("User:" + user + "|Pass:" + Arrays.toString(pass));
                        throw new AuthenticationException("User or Pass wrong");
                    }
                }, Dl.remote());
            }, 4, TimeUnit.SECONDS);

        }

        @Override
        public void stop() throws Exception {
            ses.shutdown();
        }

    }

    public static void main(String[] args) {
        Application.launch(FirstLoginApplication.class);
    }

}
