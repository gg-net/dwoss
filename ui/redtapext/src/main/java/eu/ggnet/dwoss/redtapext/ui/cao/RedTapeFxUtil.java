/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.redtapext.ui.cao;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.swing.JComponent;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.ConfirmationDialog;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtapext.ui.cao.common.PositionListCell;
import eu.ggnet.dwoss.redtapext.ui.cao.document.position.PositionViewCask;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_TRANSACTION_FOR_SINGLE_UNIT;
import static javafx.stage.Modality.WINDOW_MODAL;

/**
 * Extracted from View, cause of some package clashes.
 *
 * @author oliver.guenther
 */
public class RedTapeFxUtil {

    private final static Logger L = LoggerFactory.getLogger(RedTapeFxUtil.class);

    public static void positionFxList(JFXPanel jfxp, ObservableList<Position> positions) {

        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane, javafx.scene.paint.Color.ALICEBLUE);
        final ListView<Position> positionsFxList = new ListView<>();
        MultipleSelectionModel<Position> selectionModel = positionsFxList.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        positionsFxList.setCellFactory(new PositionListCell.Factory());
        positionsFxList.setItems(positions);
        positionsFxList.setContextMenu(RedTapeFxUtil.contextMenuOf(jfxp, selectionModel, eu.ggnet.dwoss.core.widget.Dl.local().lookup(Guardian.class)));
        positionsFxList.setOnMouseClicked(e -> {
            if ( !positionsFxList.getSelectionModel().isEmpty() && e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY ) {
                Ui.build(jfxp).fx().show(() -> positionsFxList.getSelectionModel().getSelectedItem(), () -> new PositionViewCask());
            }
        });

        pane.setCenter(positionsFxList);
        jfxp.setScene(scene);
    }

    public static ContextMenu contextMenuOf(JComponent parent, SelectionModel<Position> selectionModel, Guardian guardian) {
        // ContextMenu menu = SaftUtil.dispatchFx(() -> new ContextMenu());
        // should work
        ContextMenu menu = new ContextMenu();

        final javafx.scene.control.MenuItem noAction = new javafx.scene.control.MenuItem("No Context Action");
        noAction.setDisable(true);
        menu.getItems().add(noAction);

        menu.setOnHidden(e -> {
            L.debug("contextMenuOf() hidding");
            menu.getItems().clear();
            menu.getItems().add(noAction);
        });

        menu.setOnShowing((ev) -> {
            if ( selectionModel.isEmpty() ) return;
            if ( selectionModel.getSelectedItem().getUniqueUnitId() == 0 ) return; // no unit

            // Progress while calling the api
            ProgressIndicator bar = new ProgressIndicator();
            bar.setPrefHeight(18);
            MenuItem progressItem = new CustomMenuItem(bar);

            int uuid = selectionModel.getSelectedItem().getUniqueUnitId();
            String rid = selectionModel.getSelectedItem().getRefurbishedId();

            CompletableFuture
                    .runAsync(() -> {
                        menu.getItems().clear(); // Remove no Action.
                        menu.getItems().add(progressItem);
                    }, Platform::runLater)
                    .thenApplyAsync(v -> Dl.remote().lookup(StockApi.class).findByUniqueUnitId(uuid), UiCore.getExecutor())
                    .thenAcceptAsync(ssu -> {
                        if ( ssu == null || ssu.possibleDestinations().isEmpty() ) {
                            menu.getItems().remove(progressItem);
                            var m = new MenuItem("Keine Umfuhr möglich");
                            m.setDisable(true);
                            menu.getItems().add(m);
                            return;
                        }

                        menu.getItems().addAll(
                                ssu.possibleDestinations().stream().map((PicoStock ps) -> {
                                    MenuItem m = new MenuItem("Umfuhr von " + ssu.stock().get().shortDescription + " nach " + ps.shortDescription);
                                    m.setOnAction(e -> {
                                        Ui.build(parent).title("Umfuhr").modality(WINDOW_MODAL).dialog()
                                                .eval(() -> {
                                                    return new ConfirmationDialog<PicoStock>("Umfuhr für " + rid, "Umfuhr für " + rid + " von " + ssu.stock().get().shortDescription + " nach " + ps.shortDescription + " durchführen ?", ps);
                                                }).cf()
                                                .thenAcceptAsync(v -> {
                                                    Ui.exception().wrap(()
                                                            -> Dl.remote().lookup(StockApi.class).perpareTransferByUniqueUnitIds(Arrays.asList((long)uuid), ps.id, guardian.getUsername(), "Umfuhr direkt durch Nutzer erzeugt"));
                                                }).handle(Ui.handler());
                                    });
                                    m.setDisable(!guardian.hasRight(CREATE_TRANSACTION_FOR_SINGLE_UNIT));
                                    return m;
                                }).collect(Collectors.toList())
                        );
                        menu.getItems().remove(progressItem);

                    }, Platform::runLater)
                    .handle(Ui.handler());
        });

        return menu;
    }

}
