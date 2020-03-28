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

import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.Css;
import eu.ggnet.dwoss.core.widget.HtmlPane;
import eu.ggnet.dwoss.redtape.api.RedTapeApi;
import eu.ggnet.dwoss.redtape.api.UnitAvailability;
import eu.ggnet.dwoss.stock.spi.ActiveStock;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.dwoss.core.widget.Ops;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.ops.SelectionEnhancer;
import eu.ggnet.dwoss.core.widget.ops.Selector;

import static javafx.scene.text.FontPosture.ITALIC;

/**
 * A Ui Element to verify the status of of any refurbished unit quickly.
 *
 * @author oliver.guenther
 */
public class UnitAvailabilityPane extends BorderPane {

    private final static Font VERDANA = Font.font("Verdana", 12);

    private final static Font VERDANA_BOLD = Font.font("Verdana", FontWeight.BOLD, 12);

    private final static Font VERDANA_ITALIC = Font.font("Verdana", ITALIC, 12);

    private final TextField searchField;

    private final Button clearButton;

    private final ObservableList<UnitAvailability> results = FXCollections.observableArrayList();

    private final Selector<UnitAvailability> selector; // No clear needed. This Panel is in the MainFrame.

    public UnitAvailabilityPane() {
        searchField = new TextField();
        clearButton = new Button("Liste leeren");
        clearButton.setOnAction(e -> results.clear());

        HBox top = new HBox(5, new Label(" SopoNr:"), searchField, clearButton);
        top.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        ListView<UnitAvailability> resultListView = new ListView<>(results);
        resultListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        resultListView.setCellFactory((ListView<UnitAvailability> view) -> new ListCell<UnitAvailability>() {
            @Override
            protected void updateItem(UnitAvailability ua, boolean empty) {
                super.updateItem(ua, empty);
                setText(null);
                if ( empty ) {
                    setGraphic(null);
                } else {
                    // Default assumtion non existend.
                    /*
                    SopoNr: 12322 verfügbar / nicht verfügbar
                    (Fehler: Redtape Sanity check) (optional)
                    Im Lager: ... / Auf Transaction (optional)
                     */
                    Color color = Color.YELLOW;
                    Text sopoNr = new Text("SopoNr: ");
                    sopoNr.setFont(VERDANA);
                    Text sopoNrValue = new Text(ua.refurbishId());
                    sopoNrValue.setFont(VERDANA_BOLD);
                    Text status = new Text(" existiert nicht");
                    status.setFont(VERDANA_ITALIC);
                    Text line2 = new Text();
                    Text line3 = new Text();

                    if ( ua.exists() ) { // If a uu exist, the default is not avialable
                        status.setText(" nicht verfügbar");
                        color = Color.RED;
                        // Update refurbishid display incl. last soponur
                        sopoNrValue.setText(ua.refurbishId() + ua.lastRefurbishId().map(l -> " (war " + l + ")").orElse(""));
                        ua.stockInformation().ifPresent(si -> line3.setText("\n" + si));
                        ua.conflictDescription().ifPresent(cd -> line2.setText("\n" + cd));
                    }
                    if ( ua.available() ) {
                        status.setText(" verfügbar");
                        color = Dl.local().optional(ActiveStock.class)
                                .map(ActiveStock::getActiveStock)
                                .map(as -> Objects.equals(as.id, ua.stockId().orElse(-1)) ? Color.LIGHTGREEN : Color.CYAN)
                                .orElse(Color.CYAN);
                    }

                    TextFlow tf = new TextFlow(sopoNr, sopoNrValue, status, line2, line3);
                    tf.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                    setGraphic(tf);
                }
            }

        });

        // TODO: After CDIing the full application, replace this through CDI Events
        SelectionEnhancer<UnitAvailability> selectionEnhancer = (UnitAvailability selected) -> {
            if ( selected != null && selected.uniqueUnitId().isPresent() )
                return Arrays.asList(new PicoUnit(selected.uniqueUnitId().get().intValue(), "SopoNr:" + selected.refurbishId()));
            return Collections.EMPTY_LIST;
        };
        selector = Ops.seletor(UnitAvailability.class, selectionEnhancer);

        resultListView.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> selector.selected(n));
        // --------------------------------------------------------------------------

        resultListView.setOnMouseClicked((e) -> {
            UnitAvailability ua = resultListView.getSelectionModel().getSelectedItem();
            if ( ua == null ) return;
            if ( !ua.uniqueUnitId().isPresent() ) return;
            if ( e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2 ) {
                Ui.build().id(ua.refurbishId()).fx().show(() -> Css.toHtml5WithStyle(Dl.remote().lookup(UniqueUnitApi.class).findAsHtml(ua.uniqueUnitId().get(),
                        Dl.local().lookup(Guardian.class).getUsername())), () -> new HtmlPane());
            }
        });

        searchField.setOnAction((e) -> {
            String refurbishedId = searchField.getText().trim();
            UnitAvailability ua = Dl.remote().lookup(RedTapeApi.class).findUnitByRefurbishIdAndVerifyAviability(refurbishedId);
            LoggerFactory.getLogger(UnitAvailabilityPane.class).debug("search({}) found {}", refurbishedId, ua);
            results.add(0, ua);
            searchField.setText("");
        });

        setTop(top);
        setCenter(resultListView);

    }

}
