/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.search.ui;

import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.web.WebView;

import eu.ggnet.saft.core.Ui;

import static javafx.geometry.Pos.CENTER_RIGHT;

/**
 * Html Pane for Strings.
 * <p>
 * @author oliver.guenther
 */
public class HtmlPane extends BorderPane implements Consumer<String> {

    private WebView webView;

    public HtmlPane() {
        Button close = new Button("Schließen");
        close.setOnAction(e -> Ui.closeWindowOf(this));
        Button print = new Button("Drucken");
        print.setOnAction((ActionEvent event) -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if ( job == null ) return;
            boolean cont = job.showPrintDialog(null);
            if ( !cont ) return;
            webView.getEngine().print(job);
            job.endJob();
        });

        // new WebView must happen on the FxThead.
        dispatch(() -> {
            setPadding(new Insets(5));
            webView = new WebView();
            setCenter(webView);
        });

        FlowPane fp = new FlowPane(print, close);
        fp.setHgap(5);
        fp.setAlignment(CENTER_RIGHT);
        setBottom(fp);
    }

    @Override
    public void accept(String content) {      
        dispatch(() -> webView.getEngine().loadContent(content));
    }

    private void dispatch(Runnable r) {
        if ( Platform.isFxApplicationThread() ) r.run();
        else Platform.runLater(r);
    }
}
