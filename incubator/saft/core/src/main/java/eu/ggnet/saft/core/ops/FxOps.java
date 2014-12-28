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
package eu.ggnet.saft.core.ops;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import eu.ggnet.saft.api.Accessable;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.all.DescriptiveConsumerRunner;
import eu.ggnet.saft.core.all.SelectionEnhancer;
import eu.ggnet.saft.core.authorisation.Guardian;
import eu.ggnet.saft.core.fx.FxSaft;

/**
 * Java Fx features of Ops.
 * <p>
 * @author oliver.guenther
 */
public class FxOps {

    /**
     * Returns a EventHandler useful for Lists to active the global default handling.
     * <p>
     * @param <T>
     * @param selectionModel the selection model.
     * @return a event handler, which will call the default action on double click.
     */
    public static <T> EventHandler<MouseEvent> defaultMouseEventOf(SelectionModel<T> selectionModel) {
        return (mouseEvent) -> {
            if ( !selectionModel.isEmpty()
                    && mouseEvent.getButton().equals(MouseButton.PRIMARY)
                    && mouseEvent.getClickCount() == 2 ) {
                T item = selectionModel.getSelectedItem();
                Optional<DescriptiveConsumerRunner<T>> optionalRunner = Ops.defaultOf(item);
                if ( optionalRunner.isPresent() && authorised(optionalRunner.get()) ) optionalRunner.get().run();
            }
        };
    }

    public static <T> ContextMenu contextMenuOf(SelectionModel<T> selectionModel) {
        return contextMenuOf(selectionModel, null);
    }

    public static <T> ContextMenu contextMenuOf(SelectionModel<T> selectionModel, SelectionEnhancer<T> filter) {
        ContextMenu menu = FxSaft.dispatch(() -> new ContextMenu());
        final MenuItem noAction = new MenuItem("No Context Action");
        noAction.setDisable(true);
        menu.getItems().add(noAction);
        final List<MenuItem> lastItems = Collections.synchronizedList(new ArrayList<>());
        menu.setOnHidden(e -> {
            menu.getItems().removeAll(lastItems);
            if ( menu.getItems().isEmpty() ) menu.getItems().add(noAction);
        });
        menu.setOnShowing((ev) -> {
            if ( selectionModel.isEmpty() ) return;
            T t = selectionModel.getSelectedItem();

            List<DescriptiveConsumerRunner<?>> actions = Ops.staticOf(t, filter);
            lastItems.clear();
            if ( !actions.isEmpty() ) menu.getItems().remove(noAction);

            List<MenuItem> menuItems = actions
                    .stream()
                    .filter(a -> authorised(a.consumer()))
                    .map(a -> toMenuItem(a))
                    .collect(Collectors.toList());

            // -- Nice, a progress bar for the factories.
            ProgressIndicator bar = new ProgressIndicator();
            bar.setPrefHeight(18);
            MenuItem progressItem = new CustomMenuItem(bar);

            menuItems.add(progressItem);
            lastItems.addAll(menuItems);
            menu.getItems().addAll(menuItems);
            bar.prefWidthProperty().bind(menu.getScene().widthProperty().subtract(50));
            ForkJoinPool.commonPool().execute(() -> {
                List<MenuItem> dynamicItems = Ops
                        .dynamicOf(t, filter)
                        .stream()
                        .filter(a -> authorised(a.consumer()))
                        .map(a -> toMenuItem(a))
                        .collect(Collectors.toList());
                Platform.runLater(() -> {
                    if ( !menu.getItems().containsAll(menuItems) ) return; // The menu is all ready invisible again. don't do anything.
                    menu.getItems().remove(progressItem);
                    lastItems.remove(progressItem);
                    bar.prefWidthProperty().unbind();
                    if ( !dynamicItems.isEmpty() ) menu.getItems().remove(noAction);
                    lastItems.addAll(dynamicItems);
                    menu.getItems().addAll(dynamicItems);
                });
            });
        });

        return menu;
    }

    private static MenuItem toMenuItem(DescriptiveConsumerRunner action) {
        MenuItem item = new MenuItem(action.title());
        item.setOnAction(e -> action.run());
        return item;
    }

    private static boolean authorised(Object o) {
        if ( !(o instanceof Accessable) ) return true; // Not even something with rights.
        if ( !Client.hasFound(Guardian.class) ) return true; // No guardian, rights are ignored.
        return Client.lookup(Guardian.class).getRights().contains(((Accessable)o).getNeededRight());
    }

}
