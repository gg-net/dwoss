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
package tryout.search;

import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import eu.ggnet.saft.core.ui.ClosedListener;
import eu.ggnet.saft.core.ui.Title;
import eu.ggnet.dwoss.core.widget.Ops;

/**
 *
 * @author oliver.guenther
 */
@Title("Dossier Viewer")
public class DossierDetailSelectivView extends BorderPane implements Consumer<MicroDossier>, ClosedListener {

    private final TextField header;

    private final TextArea body;

    private final ProgressIndicator progressIndicator;

    public DossierDetailSelectivView() {
        header = new TextField();
        setTop(header);
        body = new TextArea();
        progressIndicator = new ProgressIndicator();
        Ops.registerSelectListener(this);
        setCenter(new StackPane(body, progressIndicator));
        reset();
    }

    @Override
    public void accept(MicroDossier mu) {
        reset();
        if ( mu != null ) set(mu);
    }

    private void reset() {
        header.setText("No Dossier Selected");
        body.setText("");
        progressIndicator.setVisible(false);
    }

    private void set(MicroDossier mu) {
        header.setText(mu.shortDescription());
        Platform.runLater(() -> progressIndicator.setVisible(true));
        ForkJoinPool.commonPool().execute(() -> {
            Dossier dossier = VirtualDataSource.findDossier(mu.dossierId);
            Platform.runLater(() -> {
                body.setText(dossier.description);
                progressIndicator.setVisible(false);
            });
        });
    }

    @Override
    public void closed() {
        Ops.unregisterSelectListener(this);
    }

}
