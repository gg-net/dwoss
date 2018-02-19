/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.report.ee.tryout;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Date;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import org.junit.Test;

import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.rules.*;


/**
 *
 * @author jens.papenhagen
 */
public class ReportLineHtmlTryout {

    private boolean complete = false;

    @Test
    public void tryout() throws InterruptedException, InvocationTargetException, MalformedURLException {
        ReportLine rl = new ReportLine();
        rl.setActual(new Date());
        rl.setContractor(TradeName.EBAY);
        rl.setContractorPartNo("123.131");
        rl.setCustomerId(12322);
        rl.setDescription("AMD E Series E-450 (1.65 Ghz), Memory (in MB): 4096, AMD Radeon HD 6000 Series"
                + "6320, Festplatte(n): 320GB HDD, Optische(s) Laufwerk(e): DVD Super Multi, Display:"
                + "15.6\" (39,62 cm), Crystal Bright, HD (1366x768), 16:9, , Farbe: grau, Ausstattung:"
                + "Webcam, WLAN b + g + n, Kartenleser, Videokonnektor(en) : HDMI, VGA, Windows 7"
                + "Home Premium 64");
        rl.setDocumentIdentifier("SR_00001");
        rl.setDocumentType(DocumentType.INVOICE);
        rl.setDossierIdentifier("DW00110");
        rl.setDossierId(110);
        rl.setInvoiceAddress("Max Mustermann, Musterstrasse 22, 20031 Hamburg");
        rl.setMfgDate(new Date());
        rl.setName("Acer Aspire 5250-4504G32Mnkk (NX.RJYED.004)");
        rl.setPartNo("LX.AAA12.312");
        rl.setPositionType(PositionType.UNIT);
        rl.setProductBrand(TradeName.ACER);
        rl.setProductGroup(ProductGroup.NOTEBOOK);
        rl.setProductName("Aspire 5250-4504G32Mnkk");
        rl.setRefurbishId("13213");
        rl.setReportingDate(new Date());
        rl.setSalesChannel(SalesChannel.RETAILER);
        rl.setSerial("AAAAABBBABABABADFSA23423");

        new JFXPanel(); // Implicit start the platform.

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("HtmlViewer");
            WebView view = new WebView();
            view.getEngine().loadContent(Css.toHtml5WithStyle(rl.toHtml()));
            BorderPane p = new BorderPane(view);
            Scene scene = new Scene(p, Color.ALICEBLUE);
            stage.setScene(scene);
            stage.showAndWait();

            complete = true;
        });
        while (!complete) {
            Thread.sleep(500);
        }
    }

}
