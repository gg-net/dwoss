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

import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.Css;
import eu.ggnet.dwoss.core.widget.HtmlPane;
import eu.ggnet.dwoss.redtape.api.RedTapeApi;
import eu.ggnet.dwoss.redtape.api.SanityResult;
import eu.ggnet.dwoss.stock.api.SimpleStockUnit;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.stock.spi.ActiveStock;
import eu.ggnet.dwoss.uniqueunit.api.*;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.experimental.Ops;
import eu.ggnet.saft.experimental.auth.Guardian;
import eu.ggnet.saft.experimental.ops.SelectionEnhancer;
import eu.ggnet.saft.experimental.ops.Selector;

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

    private final Selector<SimpleUniqueUnit> selector; // No clear needed. This Panel is in the MainFrame.

    public UnitAvailabilityPane() {
        searchField = new TextField();
        clearButton = new Button("Liste leeren");

        HBox top = new HBox(5, new Label(" SopoNr:"), searchField, clearButton);
        top.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        results = FXCollections.observableArrayList();

        ListView<Result> resultListView = new ListView<>(results);
        resultListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        resultListView.setCellFactory((ListView<Result> view) -> new ListCell<Result>() {
            @Override
            protected void updateItem(Result r, boolean empty) {
                super.updateItem(r, empty);
                setText(null);
                if ( empty ) {
                    setGraphic(null);
                } else {
                    /*
                    SopoNr: 12322 verfügbar / nicht verfügbar
                    (Fehler: Redtape Sanity check) (optional)
                    Im Lager: ... / Auf Transaction (optional)
                     */
                    Color color = Color.YELLOW;
                    Text sopoNr = new Text("SopoNr: ");
                    sopoNr.setFont(VERDANA);
                    Text sopoNrValue = new Text(r.refurbishId);
                    sopoNrValue.setFont(VERDANA_BOLD);
                    Text status = new Text(" existiert nicht");
                    status.setFont(VERDANA_ITALIC);
                    Text line2 = new Text();
                    Text line3 = new Text();

                    if ( r.uniqueUnit.isPresent() ) { // If a uu exist, the default is not avialable
                        SimpleUniqueUnit uu = r.uniqueUnit.get();
                        status.setText(" nicht verfügbar");
                        color = Color.RED;
                        // Update refurbishid display incl. last soponur
                        sopoNrValue.setText(uu.refurbishedId() + uu.lastRefurbishId().map(l -> " (war " + l + ")").orElse(""));
                        if ( r.stockUnit.isPresent() ) {
                            SimpleStockUnit su = r.stockUnit.get();
                            line3.setText(su.stockTransaction()
                                    .map(t -> "\nAuf " + t.shortDescription())
                                    .or(() -> su.stock().map(s -> "\nAuf " + s.shortDescription)).orElse(""));
                            if ( !su.onLogicTransaction() && r.sanityResult.map(SanityResult::blocked).orElse(false) ) {
                                line2.setText("\n" + r.sanityResult.get().details());
                            } else if ( !su.onLogicTransaction() ) {
                                status.setText(" verfügbar");
                                color = Dl.local().optional(ActiveStock.class)
                                        .map(ActiveStock::getActiveStock)
                                        .map(as -> Objects.equals(as.id, su.stock().map(s -> s.id).orElse(-1)) ? Color.LIGHTGREEN : Color.CYAN)
                                        .orElse(Color.CYAN);
                            } // default is allready set.
                        }
                    }

                    TextFlow tf = new TextFlow(sopoNr, sopoNrValue, status, line2, line3);
                    tf.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                    setGraphic(tf);
                }
            }

        });

        // TODO: After CDIing the full application, replace this through CDI Events
        SelectionEnhancer<SimpleUniqueUnit> selectionEnhancer = (SimpleUniqueUnit selected) -> {
            if ( selected != null )
                return Arrays.asList(new PicoUnit((int)selected.id(), "SopoNr:" + selected.refurbishedId()));
            return Collections.EMPTY_LIST;
        };
        selector = Ops.seletor(SimpleUniqueUnit.class, selectionEnhancer);

        resultListView.getSelectionModel().selectedItemProperty()
                .addListener((ov, o, n) -> Optional.ofNullable(n).map(r -> r.uniqueUnit.orElse(null)).ifPresent(uu -> selector.selected(uu)));
        // --------------------------------------------------------------------------

        resultListView.setOnMouseClicked((var e) -> {
            Result sr = resultListView.getSelectionModel().getSelectedItem();
            if ( sr == null ) return;
            if ( sr.uniqueUnit.isEmpty() ) return;
            SimpleUniqueUnit uu = sr.uniqueUnit.get();

            if ( e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2 ) {
                Ui.build().id(uu.refurbishedId()).fx().show(() -> Css.toHtml5WithStyle(Dl.remote().lookup(UniqueUnitApi.class).findAsHtml(uu.id(), Dl.local().lookup(Guardian.class).getUsername())), () -> new HtmlPane());
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
