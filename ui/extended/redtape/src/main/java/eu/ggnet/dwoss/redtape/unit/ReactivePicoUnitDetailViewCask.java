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
package eu.ggnet.dwoss.redtape.unit;

import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

import eu.ggnet.dwoss.redtape.UnitOverseer;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.authorisation.Guardian;
import eu.ggnet.saft.core.fx.FxSaft;
import eu.ggnet.saft.core.ops.Ops;

import static javafx.scene.text.Font.font;

/**
 * A View which reacts on selected Pico Units.
 * <p>
 * @author oliver.guenther
 */
@Frame
@Title("Reactive Geräte Ansicht")
public class ReactivePicoUnitDetailViewCask extends BorderPane implements Consumer<PicoUnit>, ClosedListener, Initialiser {

    private final Label head;

    private final WebView webView;

    private final ProgressIndicator progressIndicator;

    public ReactivePicoUnitDetailViewCask() {
        head = new Label("No UniqueUnit");
        head.setFont(font(20));
        setTop(head);
        webView = FxSaft.dispatch(() -> new WebView());
        progressIndicator = new ProgressIndicator();
        setCenter(new StackPane(webView, progressIndicator));
        progressIndicator.setVisible(false);
    }

    @Override
    public void accept(PicoUnit pu) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(true);
            head.setText("(" + pu.id() + ") " + pu.shortDescription);
        });
        Ui.exec(() -> {
            String content = Client.lookup(UnitOverseer.class).toDetailedHtml(pu.uniqueUnitId, Client.lookup(Guardian.class).getUsername());
            Platform.runLater(() -> {
                webView.getEngine().loadContent(content);
                progressIndicator.setVisible(false);
            });
            return null;
        });

    }

    @Override
    public void initialise() {
        Ops.registerSelectListener(this);
    }

    @Override
    public void closed() {
        Ops.unregisterSelectListener(this);
    }

}
