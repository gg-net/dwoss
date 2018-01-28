/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ui.old;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import eu.ggnet.dwoss.customer.ee.entity.Customer.ExternalSystem;
import eu.ggnet.saft.api.ui.Title;

import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;

/**
 *
 * @author oliver.guenther
 */
@Title("Kundennummern hinzufügen")
public class AdditionalCustomerIdsView extends Dialog<Map<ExternalSystem, String>> implements Consumer<Map<ExternalSystem, String>> {

    private final Map<ExternalSystem, StringProperty> idProperties = new HashMap<>();

    public AdditionalCustomerIdsView() {
        setHeaderText("Weiter Kundennummern hinzufügen");
        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));


        for (int i = 0; i < ExternalSystem.values().length; i++) {
            TextField input = new TextField();
            input.setPromptText("Nicht gesetzt");
            grid.add(new Label(ExternalSystem.values()[i].toString()), 0, i);
            grid.add(input, 1, i);
            idProperties.put(ExternalSystem.values()[i], input.textProperty());
        }
        getDialogPane().setContent(grid);

        setResultConverter((ButtonType bt) -> {
            if ( bt.getButtonData() != OK_DONE ) return null;
            return idProperties.entrySet().stream().filter(e -> e.getValue().isNotEmpty().get()).collect(Collectors.toMap(Entry::getKey, e -> e.getValue().get()));
        });
    }

    @Override
    public void accept(Map<ExternalSystem, String> in) {
        in.forEach((ExternalSystem t, String u) -> idProperties.get(t).set(u));
    }

}
