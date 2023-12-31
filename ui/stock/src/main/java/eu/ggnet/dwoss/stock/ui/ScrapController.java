/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.ui;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import eu.ggnet.dwoss.stock.api.SimpleStockUnit;
import eu.ggnet.dwoss.stock.ui.ScrapController.Scrapen;
import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;

@Dependent
public class ScrapController implements FxController, Consumer<Map<String, SimpleStockUnit>>, ResultProducer<Scrapen> {

    public static class Scrapen {

        private final List<Long> ids;

        private final String comment;

        public Scrapen(List<Long> ids, String comment) {
            this.ids = ids;
            this.comment = comment;
        }

        public List<Long> ids() {
            return ids;
        }

        public String comment() {
            return comment;
        }

        @Override
        public String toString() {
            return "Scrapen{" + "ids=" + ids + ", comment=" + comment + '}';
        }

    }

    @FXML
    private ListView<Map.Entry<String, SimpleStockUnit>> unitsListView;

    @FXML
    private TextField reasonTextField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button executeButton;

    @Bind(SHOWING)
    private final BooleanProperty showingProperty = new SimpleBooleanProperty();

    private boolean ok = false;

    private List<Long> resultIds;

    @FXML
    void initialize() {
        unitsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        unitsListView.setCellFactory((ListView<Entry<String, SimpleStockUnit>> param) -> new ListCell<>() {
            @Override
            protected void updateItem(Entry<String, SimpleStockUnit> item, boolean empty) {
                super.updateItem(item, empty);
                if ( !empty ) {
                    StringBuilder s = new StringBuilder(item.getKey());
                    s.append(" - ");
                    if ( item.getValue() == null ) s.append("nicht verschrottbar, kein Gerät gefunden");
                    else if ( item.getValue().stockTransaction().isPresent() )
                        s.append("nicht verschrottbar, Gerät auf einer Umfuhr oder RollIn (Stock Transaktion)");
                    else if ( item.getValue().onLogicTransaction() )
                        s.append("nicht verschrottbar, Gerät auf einem offenen Kundenauftrag (Logischer Transaktion)");
                    else s.append("verschrottbar");
                    setText(s.toString());
                }
            }
        });

        executeButton.disableProperty().bind(
                Bindings.createBooleanBinding(() -> reasonTextField.getText().trim().isEmpty(), reasonTextField.textProperty()));

        executeButton.setOnAction(e -> {
            ok = true;
            showingProperty.setValue(false);
        });
        cancelButton.setOnAction(e -> showingProperty.set(false));
    }

    @Override
    public void accept(Map<String, SimpleStockUnit> in) {
        unitsListView.getItems().addAll(in.entrySet());
        resultIds = in.values().stream()
                .filter(v -> v != null)
                .filter(v -> v.stockTransaction().isEmpty())
                .filter(v -> !v.onLogicTransaction())
                .map(v -> v.id())
                .collect(Collectors.toList());
    }

    @Override
    public Scrapen getResult() {
        if ( !ok ) return null;
        return new Scrapen(resultIds, reasonTextField.getText());
    }
}
