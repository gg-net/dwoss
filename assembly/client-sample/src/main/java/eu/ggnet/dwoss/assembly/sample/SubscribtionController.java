/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Guenther
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

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.common.ExceptionUtil;

import lombok.Setter;

/**
 *
 * @author pascal.perau
 */
public class SubscribtionController implements Initializable {

    @FXML
    Label introduction;

    @FXML
    ImageView ggnetLogo;

    @FXML
    ImageView dwossLogo;

    @FXML
    TextField name;

    @FXML
    TextField mail;

    @FXML
    TextArea message;

    @FXML
    Button sendMessageButton;

    @FXML
    ProgressBar progress;

    @Setter
    Stage stage;

    static URL loadDwossLogo() {
        return SubscribtionController.class.getResource("projectavatar.png");
    }

    static URL loadGgnetLogo() {
        return SubscribtionController.class.getResource("ggnetlogo.png");
    }

    static URL loadFxml() {
        return SubscribtionController.class.getResource("SubscribtionView.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        introduction.setWrapText(true);
        introduction.setText(""
                + "Vielen Dank für ihr Interesse an  der Deutschen Warenwirtschaft Open Source Software (DWOSS).\n\n"
                + "Dies ist eine Testversion, die bei jedem Start neue Testdaten generiert. "
                + "Änderungen und von ihnen erstellte Daten gehen beim beenden der Applikation verloren.\n\n"
                + "Bei Fragen, Anregunden oder weiterführende Informationen für Unterstützung seitens der GG-Net GmbH "
                + "füllen sie gern die in diesem Fenter abgebildete Form aus und Schicken diese per Knopfdruck ab.");

        try {
            dwossLogo.setImage(new Image(loadDwossLogo().openStream()));
            ggnetLogo.setImage(new Image(loadGgnetLogo().openStream()));
        } catch (IOException ex) {
            Logger.getLogger(SubscribtionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        sendMessageButton.setOnAction((eh) -> {
            if ( validateFields(name, mail, message) ) {
                progress.setProgress(-1);
                sendPost(name.getText(), mail.getText(), message.getText());
                progress.setProgress(100.);
                stage.close();
            }
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

    private boolean validateFields(TextInputControl... nodes) {
        boolean allValid = true;
        for (TextInputControl node1 : nodes) {
            if ( StringUtils.isBlank(node1.getText()) ) {
                node1.setStyle("-fx-text-box-border: red;");
                allValid = false;
            } else node1.setStyle(null);
        }
        return allValid;
    }
}
