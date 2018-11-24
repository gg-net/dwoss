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

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ui.neo.SelectDefaultEmailCommunicationView.SelectionResult;
import eu.ggnet.saft.core.ui.ResultProducer;

import lombok.Value;

/**
 *
 * @author oliver.guenther
 */
public class SelectDefaultEmailCommunicationView extends BorderPane implements Consumer<List<Communication>>, ResultProducer<SelectionResult> {

    @Value
    public static class SelectionResult {

        private final Communication defaultEmailCommunication;
    }

    private VBox radioButtons;

    private ToggleGroup toggleGroup;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public SelectDefaultEmailCommunicationView() {
        accept(Collections.emptyList());
    }

    @Override
    public void accept(List<Communication> in) {
        List<RadioButton> buttons = new ArrayList<>();
        toggleGroup = new ToggleGroup();
        RadioButton none = new RadioButton("Keine!");
        none.setUserData(null);
        none.setToggleGroup(toggleGroup);
        buttons.add(none);
        for (Communication comm : in) {
            RadioButton rb = new RadioButton(comm.getIdentifier());
            rb.setUserData(comm);
            rb.setToggleGroup(toggleGroup);
            buttons.add(rb);
        }
        radioButtons = new VBox();
        radioButtons.getChildren().addAll(buttons);
    }

    @Override
    public SelectionResult getResult() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
