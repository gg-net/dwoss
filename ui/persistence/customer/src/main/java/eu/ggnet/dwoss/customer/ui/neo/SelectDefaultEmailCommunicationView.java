/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ui.neo;

import java.util.*;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ui.neo.SelectDefaultEmailCommunicationView.Selection;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.ResultProducer;

/**
 * Ui to sellect the default email address.
 *
 * @author oliver.guenther
 */
public class SelectDefaultEmailCommunicationView extends BorderPane implements Consumer<Selection>, ResultProducer<Optional<Communication>> {

    /**
     * Used for input of accept.
     * Ui needs all communications and the selected one. An empty optional means no selection.
     */
    public static class Selection {

        public Selection(List<Communication> allEmailCommunications, Optional<Communication> defaultEmailCommunication) {
            this.allEmailCommunications = Objects.requireNonNull(allEmailCommunications, "allEmailCommunications must not be null");
            this.defaultEmailCommunication = Objects.requireNonNull(defaultEmailCommunication, "defaultEmailCommunication must not be null");
        }

        public static Selection empty() {
            return new Selection(new ArrayList<>(), Optional.empty());
        }

        private final List<Communication> allEmailCommunications;

        public final Optional<Communication> defaultEmailCommunication;

    }

    private VBox radioButtons;

    private ToggleGroup toggleGroup;

    private boolean ok = false;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public SelectDefaultEmailCommunicationView() {
        accept(Selection.empty());
        Button okButton = new Button("Speichern");
        okButton.setOnAction(e -> {
            ok = true;
            Ui.closeWindowOf(SelectDefaultEmailCommunicationView.this);
        });
        Button cancel = new Button("Abbrechen");
        cancel.setOnAction(e -> {
            ok = false;
            Ui.closeWindowOf(SelectDefaultEmailCommunicationView.this);
        });

        FlowPane fp = new FlowPane(5, 5, okButton, cancel);
        setBottom(fp);
        setPadding(new Insets(5));
    }

    @Override
    public void accept(Selection in) {
        List<RadioButton> buttons = new ArrayList<>();
        toggleGroup = new ToggleGroup();
        RadioButton none = new RadioButton("Keine!");
        none.setUserData(null);
        none.setToggleGroup(toggleGroup);
        none.setSelected(true);
        buttons.add(none);
        for (Communication comm : in.allEmailCommunications) {
            RadioButton rb = new RadioButton(comm.getIdentifier());
            rb.setUserData(comm);
            rb.setToggleGroup(toggleGroup);
            buttons.add(rb);
            if ( comm.equals(in.defaultEmailCommunication) ) rb.setSelected(true);
        }

        // Ui Change
        if ( radioButtons != null ) getChildren().remove(radioButtons);
        radioButtons = new VBox();
        radioButtons.getChildren().addAll(buttons);
        setCenter(radioButtons);
    }

    @Override
    public Optional<Communication> getResult() {
        if ( ok ) return Optional.ofNullable((Communication)toggleGroup.getSelectedToggle().getUserData());
        return null;
    }

}
