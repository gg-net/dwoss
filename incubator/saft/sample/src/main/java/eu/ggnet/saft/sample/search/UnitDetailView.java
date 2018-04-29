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

import eu.ggnet.saft.core.ui.Title;

/**
 *
 * @author oliver.guenther
 */
@Title("Unit {id}")
public class UnitDetailView extends BorderPane implements Consumer<MicroUnit> {

    private final TextField header;

    private final TextArea body;

    private final ProgressIndicator progressIndicator;

    public UnitDetailView() {
        header = new TextField();
        setTop(header);
        body = new TextArea();
        progressIndicator = new ProgressIndicator();
        setCenter(new StackPane(body,progressIndicator));
    }

    @Override
    public void accept(MicroUnit mu) {
        header.setText(mu.shortDescription());
        ForkJoinPool.commonPool().execute(() -> {
            Unit unit = VirtualDataSource.findUnit(mu.uniqueUnitId);
            Platform.runLater(() -> {
                body.setText(unit.description);
                progressIndicator.setVisible(false);
            });
        });
    }

}
