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
package eu.ggnet.dwoss.assembly.client.support;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import org.slf4j.Logger;

import eu.ggnet.dwoss.core.widget.event.UserChange;
import eu.ggnet.saft.core.Ui;

/**
 * Manager for the RightsToolBar Node.
 *
 * @author oliver.guenther
 */
@ApplicationScoped // Can only be added to classe with non final methods. And everything extending node has final methodes.
public class RightsToolbarManager {

    public static class ToolbarNode extends HBox {

        private final TextField userField;

        private Optional<UserChange> userChange = Optional.empty();

        public ToolbarNode() {
            Label userLabel = new Label("User: ");
            userField = new TextField();
            userField.setDisable(true);
            Button rightButton = new Button("Rechte");
            rightButton.setOnAction(e -> {
                userChange.ifPresent(uc -> {
                    Ui.build(this).title("User: " + uc.username()).fx().show(() -> {
                        return new StackPane(new TextFlow(
                                uc.allowedRights().stream().map(r -> new Text(" - " + r.toName() + "\n"))
                                        .collect(Collectors.toList()).toArray(new Text[0])
                        ));
                    });
                });
            });
            setAlignment(Pos.CENTER);
            getChildren().addAll(userLabel, userField, rightButton);
        }

    }

    @Inject
    private Logger log;

    @Inject
    private ToolbarNode tn;

    public void changeUser(@Observes UserChange userChange) {
        log.debug("changeUser(userChange={})", userChange);
        Platform.runLater(() -> {
            tn.userField.setText(userChange.username());
        });
        tn.userChange = Optional.of(userChange);
    }

    public ToolbarNode createNode() {
        return tn;
    }

}
