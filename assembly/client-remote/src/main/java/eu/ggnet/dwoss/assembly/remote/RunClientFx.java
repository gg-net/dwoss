/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.assembly.remote;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.*;
import java.util.Objects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import eu.ggnet.dwoss.assembly.remote.provides.RemoteServer;
import eu.ggnet.dwoss.assembly.remote.select.RemoteMandatorSelectorController;
import eu.ggnet.dwoss.assembly.remote.select.RemoteMode;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.Mandator;

import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.returns.Summary;

import eu.ggnet.dwoss.util.MetawidgetConfig;
import eu.ggnet.dwoss.util.dialog.Alert;

import eu.ggnet.dwoss.common.UnhandledExceptionCatcher;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.runtime.SwingClient;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * JavaFx entry Point.
 * <p/>
 * @author oliver.guenther
 */
public class RunClientFx extends Application {

    private SwingClient swingClient;

    public static void main(String[] args) {
        System.out.println("Starting JavaFx Remote Client");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Starting RemoteClient");
        Platform.setImplicitExit(false);
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new UnhandledExceptionCatcher());
        MetawidgetConfig.enhancedMetawidget(ReportLine.class, Mandator.class, Summary.class);
        Client.enableCache(MandatorSupporter.class);

        // Default Mode: GG-Net Productive
        if ( (getParameters().getRaw().isEmpty()
              || (getParameters().getNamed().containsKey("autologout") && getParameters().getNamed().size() == 1))
                && !Objects.equals(System.getProperty("select"), "true") ) {
            startRemoteApplication(RemoteMode.GG_NET_PRODUCTIVE.getUrl());
            return;
        }
        // If Parameters contain key --url, use parameter and move forward
        if ( getParameters().getNamed().containsKey("url") ) {
            startRemoteApplication(getParameters().getNamed().get("url"));
            return;
        }
        // If Parameters contain keys --mandator and --mode and the approriated Values can be mapped to a Mode, use the url and move forward
        if ( getParameters().getNamed().containsKey("mandator")
                && getParameters().getNamed().containsKey("mode")
                && RemoteMode.find(getParameters().getNamed().get("mandator"), getParameters().getNamed().get("mode")) != null ) {
            startRemoteApplication(RemoteMode.find(getParameters().getNamed().get("mandator"), getParameters().getNamed().get("mode")).getUrl());
            return;
        }
        // Otherwise show all paramters, telling, that these are not useful and to use usage.
        if ( !getParameters().getRaw().contains("--select") && !Objects.equals(System.getProperty("select"), "true") ) {
            Alert.builder()
                    .title("Fehlerhafte Paramter")
                    .body("Es wurden folgende fehlerhaften Parameter gefunden:\n"
                            + getParameters().getRaw() + "\n"
                            + "Für die korrekte Benutzung Usage anschauen")
                    .build()
                    .showAsError();
        }
        // If Parameters are empty, silently move to Selector Dialog.

        FXMLLoader loader = RemoteMandatorSelectorController.newAutoLoader();
        Pane pane = loader.getRoot();
        final RemoteMandatorSelectorController controller = loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setScene(new Scene(pane));
        primaryStage.setTitle("Host Selector");

        primaryStage.showingProperty().not().and(controller.okProperty()).addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                startRemoteApplication(controller.getUrl());
            }
        });
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("JavaFx Init called");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("JavaFx Stop called");
    }

    private void startRemoteApplication(String url) {
        System.out.println("Starting with " + url);
        final URL provider;
        try {
            provider = new URL(url);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
        if ( !isReachable(provider.getHost()) ) {
            Alert.builder()
                    .title("Fehler")
                    .body("Verbindung zum Server " + provider.getHost() + " nicht möglich. Abbruch.\nBitte noch einmal versuchen und die Technik informieren.")
                    .build()
                    .showAsError();
            return;
        }
        // Setting the URL for Remote Connections.
        RemoteServer.URL = url;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingClient = new SwingClient();
                swingClient.init();
                swingClient.show(
                        "(Remote," + provider.getHost() + ":" + provider.getPort() + ") - Mandant:"
                        + lookup(MandatorSupporter.class).loadMandator().getCompany().getName(), getParameters());
            }
        });
    }

    private boolean isReachable(String host) {
        try {
            if ( InetAddress.getByName(host).isReachable(5000) ) return true;
        } catch (IOException ex) {
            // TODO: Log me
        }
        return false;
    }
}
