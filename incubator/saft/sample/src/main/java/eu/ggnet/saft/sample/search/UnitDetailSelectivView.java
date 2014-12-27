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
package eu.ggnet.saft.sample.search;

import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ops.Ops;

import static java.lang.Double.MAX_VALUE;

/**
 *
 * @author oliver.guenther
 */
@Title("Unit Viewer")
public class UnitDetailSelectivView extends BorderPane implements Consumer<MicroUnit>, ClosedListener, Initialiser {

    private final TextField header;

    private final TextArea body;

    private final ProgressIndicator progressIndicator;

    public UnitDetailSelectivView() {
        header = new TextField();
        header.setPrefWidth(MAX_VALUE);
        setTop(header);
        body = new TextArea();
        progressIndicator = new ProgressIndicator();
        setCenter(new StackPane(body, progressIndicator));
        reset();
    }

    @Override
    public void accept(MicroUnit mu) {
        reset();
        if ( mu != null ) set(mu);
    }

    private void reset() {
        header.setText("No Unit Selected");
        body.setText("");
        progressIndicator.setVisible(false);
    }

    private void set(MicroUnit mu) {
        header.setText(mu.shortDescription());
        Platform.runLater(() -> progressIndicator.setVisible(true));
        ForkJoinPool.commonPool().execute(() -> {
            Unit unit = VirtualDataSource.findUnit(mu.uniqueUnitId);
            Platform.runLater(() -> {
                body.setText(unit.description);
                progressIndicator.setVisible(false);
            });
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
