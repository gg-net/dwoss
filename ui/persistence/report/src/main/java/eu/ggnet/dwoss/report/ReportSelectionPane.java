/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.report;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.rules.TradeName;

/**
 *
 * @author pascal.perau
 */
public class ReportSelectionPane extends BorderPane implements Consumer<List<Report>> {

    private final ListView<Report> reportListView;

    private ObservableList<Report> reports;

    private FilteredList<Report> filteredReports;

    private final ComboBox<TradeName> typeBox;

    public ReportSelectionPane() {
        Label reportTypeLabel = new Label("Reporttyp:");
        typeBox = new ComboBox<>();
        typeBox.valueProperty().addListener((ob, ov, newValue) -> {
            if ( filteredReports != null ) filteredReports.setPredicate(report -> report.getType() == newValue);
        });

        VBox vbox = new VBox(10.);
        vbox.getChildren().addAll(reportTypeLabel, typeBox);

        reportListView = new ListView<>();
        reportListView.setCellFactory(new ReportListCell.Factory());

        setCenter(reportListView);
        setTop(vbox);
    }

    @Override
    public void accept(List<Report> allReports) {
        reports = FXCollections.observableList(allReports);

        ObservableList<TradeName> typeItems = FXCollections.observableList(allReports
                .stream()
                .map(Report::getType)
                .distinct()
                .collect(Collectors.toList()));
        typeBox.setItems(typeItems);
        typeBox.getSelectionModel().selectFirst();

        filteredReports = new FilteredList<>(reports, (Report r) -> r.getType() == typeBox.valueProperty().get());
        reportListView.setItems(filteredReports);
    }

    public Report selected() {
        return reportListView.getSelectionModel().getSelectedItem();
    }

}
