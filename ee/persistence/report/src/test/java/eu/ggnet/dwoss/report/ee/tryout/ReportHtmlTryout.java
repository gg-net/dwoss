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
import java.text.ParseException;
import java.util.Date;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import org.junit.Test;

import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.common.ee.Css;
import eu.ggnet.dwoss.core.system.Utils;

import static eu.ggnet.dwoss.common.api.values.TradeName.ALSO;

/**
 *
 * @author jens.papenhagen
 */
public class ReportHtmlTryout {

    private boolean complete = false;

    private static Date _2011_10_01;

    private static Date _2011_10_07;

    static {
        try {
            _2011_10_01 = Utils.ISO_DATE.parse("2011-10-01");
            _2011_10_07 = Utils.ISO_DATE.parse("2011-10-07");
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void tryout() throws InterruptedException, InvocationTargetException, MalformedURLException {
        Report report = new Report("TestReport", ALSO, _2011_10_01, _2011_10_07);
        report.setComment("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

        new JFXPanel(); // Implicit start the platform.

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("HtmlViewer");
            WebView view = new WebView();
            view.getEngine().loadContent(Css.toHtml5WithStyle(report.toHtml()));
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
