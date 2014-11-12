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
package eu.ggnet.dwoss.assembly.sample;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.*;
import java.net.*;

import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import org.openide.util.Lookup;

import eu.ggnet.dwoss.common.ExceptionUtil;
import eu.ggnet.dwoss.common.UnhandledExceptionCatcher;
import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.util.MetawidgetConfig;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Server;
import eu.ggnet.saft.runtime.SwingClient;

import javassist.NotFoundException;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * JavaFx entry Point.
 * <p>
 * import static javafx.geometry.HPos.CENTER;
 * <p>
 * import static javafx.geometry.Pos.CENTER;
 * <p/>
 * @author oliver.guenther
 */
public class RunClientFx extends Application {

    private SwingClient swingClient;

    public static void main(String[] args) throws NotFoundException, InterruptedException {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        Platform.setImplicitExit(false);
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new UnhandledExceptionCatcher());
        MetawidgetConfig.enhancedMetawidget(ReportLine.class, Mandator.class);
        Client.enableCache(MandatorSupporter.class);

        swingClient = new SwingClient() {
            @Override
            protected void close() {
                Lookup.getDefault().lookup(Server.class).shutdown();
            }
        };
        EventQueue.invokeLater(() -> {
            swingClient.init();
        });
        Lookup.getDefault().lookup(Server.class).initialise();
    }

    @Override
    public void stop() throws Exception {
        // can't use that now cause of the different lifecycles.
    }

    @Override
    public void start(Stage stage) throws Exception {
        Stage dialog = new Stage();
        dialog.setTitle("Optionale Registrierung");
        dialog.setAlwaysOnTop(true);
        dialog.setWidth(250);
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        //    emailField.setPrefWidth(125);
        TextField nameField = new TextField();
        nameField.setPromptText("Ihr Name");
        //      nameField.setPrefWidth(125);
        TextArea messageField = new TextArea();
        messageField.setPromptText("Optionale Nachricht");
//        messageField.setPrefWidth(240);
        Button send = new Button("Senden");
        send.setOnAction((eh) -> {
            sendPost(nameField.getText(), emailField.getText(), messageField.getText());
            dialog.close();
        });

        Button cancel = new Button("Überspringen");
        cancel.setOnAction((e) -> dialog.close());

        GridPane p = new GridPane();
        p.setHgap(5);
        p.setVgap(5);
        p.add(nameField, 0, 0);
        p.add(emailField, 1, 0);
        p.add(messageField, 0, 1, 2, 1);
        p.add(new FlowPane(5, 0, send, cancel), 0, 2, 2, 1);

        Scene scene = new Scene(p);
        dialog.setScene(scene);
        dialog.showAndWait();

        EventQueue.invokeLater(() -> {
            swingClient.show("(Sample) - Mandant:" + lookup(MandatorSupporter.class).loadMandator().getCompany().getName(), getParameters());
        });
    }

    private void sendPost(String name, String email, String message) {
        try {
            String url = "http://gg-net.de/registerDw/register-email.php";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            //add reuqest header
            con.setRequestMethod("POST");
            String urlParameters = "name=" + URLEncoder.encode(name, "UTF-8") + "&email=" + URLEncoder.encode(email, "UTF-8")
                    + "&message=" + URLEncoder.encode(message, "UTF-8");
            // Send post request
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
            }
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
        } catch (IOException ex) {
            ExceptionUtil.show(null, ex);
        }

    }
}
