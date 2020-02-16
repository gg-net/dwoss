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
package eu.ggnet.dwoss.redtapext.ui.cap;

import java.util.Objects;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.redtape.api.RedTapeApi;
import eu.ggnet.dwoss.redtape.api.SanityResult;
import eu.ggnet.dwoss.stock.api.SimpleStockUnit;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.stock.spi.ActiveStock;
import eu.ggnet.dwoss.uniqueunit.api.SimpleUniqueUnit;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.saft.core.Dl;

import static javafx.scene.text.FontPosture.ITALIC;

/**
 * A Ui Element to verify the status of of any refurbished unit quickly.
 *
 * @author oliver.guenther
 */
public class UnitAvailabilityPane extends BorderPane {

    private static class Result {

        private final String refurbishId;

        private final Optional<SimpleUniqueUnit> uniqueUnit;

        private final Optional<SimpleStockUnit> stockUnit;

        private final Optional<SanityResult> sanityResult;

        private Result(String refurbishId, Optional<SimpleUniqueUnit> uniqueUnit, Optional<SimpleStockUnit> stockUnit, Optional<SanityResult> sanityResult) {
            this.refurbishId = Objects.requireNonNull(refurbishId);
            this.uniqueUnit = Objects.requireNonNull(uniqueUnit);
            this.stockUnit = Objects.requireNonNull(stockUnit);
            this.sanityResult = Objects.requireNonNull(sanityResult);
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

    }

    private final static Font VERDANA = Font.font("Verdana", 12);

    private final static Font VERDANA_BOLD = Font.font("Verdana", FontWeight.BOLD, 12);

    private final static Font VERDANA_ITALIC = Font.font("Verdana", ITALIC, 12);

    private final TextField searchField;

    private final Button clearButton;

    private final ObservableList<Result> results;

    public UnitAvailabilityPane() {
        searchField = new TextField();
        clearButton = new Button("Liste leeren");

        HBox top = new HBox(5, new Label("SopoNr:"), searchField, clearButton);
        top.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        results = FXCollections.observableArrayList();

        ListView<Result> resultListView = new ListView<>(results);

        resultListView.setCellFactory((ListView<Result> view) -> new ListCell<Result>() {
            @Override
            protected void updateItem(Result r, boolean empty) {
                super.updateItem(r, empty);
                setText(null);
                if ( empty ) {
                    setGraphic(null);
                } else {
                    Color color = Color.YELLOW;
                    Text sopoNr = new Text("SopoNr: ");
                    sopoNr.setFont(VERDANA);
                    Text sopoNrValue = new Text(r.refurbishId);
                    sopoNrValue.setFont(VERDANA_BOLD);
                    Text status = r.uniqueUnit.map(uu -> new Text("\n" + uu.shortDescription())).orElse(new Text(" existiert nicht"));
                    status.setFont(VERDANA_ITALIC);

                    if ( r.uniqueUnit.map(uu -> uu.lastRefurbishId()).isPresent() ) {
                        SimpleUniqueUnit suu = r.uniqueUnit.get();
                        sopoNrValue.setText(suu.refurbishedId() + suu.lastRefurbishId().map(l -> " (war " + l + ") ").orElse(" "));
                    }

                    if ( r.stockUnit.map(su -> su.onLogicTransaction()).orElse(true) ) {
                        color = Color.RED;

                    } else {

                        Dl.local().optional(ActiveStock.class)
                                .map(ActiveStock::getActiveStock)
                                .map(as -> Objects.equals(as.id, r.stockUnit.get().stock().get().id) ? Color.GREEN : Color.CYAN)
                                .orElse(Color.CYAN);
                    }

                    TextFlow tf = new TextFlow(sopoNr, sopoNrValue, status);

                    tf.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                    setGraphic(tf);
                }
            }

        });

        searchField.setOnAction((e) -> {
            String refurbishedId = searchField.getText().trim();

            Optional<SimpleUniqueUnit> suu = Optional.ofNullable(Dl.remote().lookup(UniqueUnitApi.class).findByRefurbishedId(refurbishedId));
            Optional<SimpleStockUnit> ssu = Dl.remote().optional(StockApi.class).filter(sa -> suu.isPresent()).map(sa -> sa.findByUniqueUnitId(suu.get().id()));
            Optional<SanityResult> sr = Dl.remote().optional(RedTapeApi.class).filter(sa -> suu.isPresent()).map(rt -> rt.sanityCheck(suu.get().id()));

            Result r = new Result(refurbishedId, suu, ssu, sr);
            LoggerFactory.getLogger(UnitAvailabilityPane.class).debug("search({}) found {}", refurbishedId, r);
            results.add(0, r);
            searchField.setText("");
        });

        setTop(top);
        setCenter(resultListView);

    }

}
