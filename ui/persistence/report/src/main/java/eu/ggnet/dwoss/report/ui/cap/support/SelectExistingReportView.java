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
package eu.ggnet.dwoss.report.ui.cap.support;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.report.ReportAgent;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.report.ui.main.ReportListCell;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.Client;

import lombok.Value;

import static java.time.ZoneId.systemDefault;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;

/**
 *
 * @author pascal.perau
 */
@Title("Report zur Ansicht auswählen")
public class SelectExistingReportView extends BorderPane implements Consumer<List<Report>>, ResultProducer<Long> {

    @Value
    private final static class EditResult {

        private final Report.OptimisticKey key;

        private final String text;

    }

    @Value
    private final static class UpdateResult {

        private final boolean successful;

        private final String text;
    }

    private final ListView<Report> reportListView;

    private ObservableList<Report> reports;

    private FilteredList<Report> filteredReports;

    private final ComboBox<TradeName> typeBox;

    private final ComboBox<Integer> yearBox;

    private boolean ok = false;

    public SelectExistingReportView() {
        Label reportTypeLabel = new Label("Reporttyp:");
        //    reportTypeLabel.setStyle("-fx-font-weight: bold;");

        typeBox = new ComboBox<>();
        yearBox = new ComboBox<>();

        /**
         * Adding a listener for the type dropdrown.
         * for selecting all reports with the selected trademark AND the selected year.
         * the .getStartingDate() of report giveback a Date Object
         * what only can be casted to a primitiv int and the value
         * of the selected item of the dropdown for the year give back a Integer.
         */
        typeBox.valueProperty().addListener((ob, ov, newValue) -> {
            if ( filteredReports != null ) {

                filteredReports.setPredicate(report -> report.getType() == newValue
                        && Objects.equals(LocalDate.from(Instant.ofEpochMilli(report.getStartingDate().getTime()).atZone(systemDefault())).getYear(), yearBox.getValue()) //using autoboxing and object equals for safety
                );
            }
        });
        /**
         * Adding a listener for the type dropdrown.
         * for selecting all reports with the selected year AND the selected trademark.
         */
        yearBox.valueProperty().addListener((ob, ov, newValue) -> {
            if ( filteredReports != null ) {
                filteredReports.setPredicate(report -> LocalDate.from(Instant.ofEpochMilli(report.getStartingDate().getTime()).atZone(systemDefault())).getYear() == newValue
                        && report.getType() == typeBox.getValue());
            }
        });

        MenuItem editName = new MenuItem("Edit Name");
        editName.setOnAction((ActionEvent event) -> {
            openNameEdit();
        });

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(editName);

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10, 100, 5, 10));
        hbox.setSpacing(10);
        hbox.getChildren().addAll(reportTypeLabel, typeBox, yearBox);

        reportListView = new ListView<>();
        reportListView.setCellFactory(new ReportListCell.Factory());
        reportListView.setOnContextMenuRequested((ContextMenuEvent event) -> {
            contextMenu.show(reportListView, event.getScreenX(), event.getScreenY());
        });
        reportListView.setTooltip(null);

        Button okButton = new Button("Ok");
        okButton.setOnAction(e -> {
            ok = true;
            Ui.closeWindowOf(this);
        });
        Button cancelButton = new Button("Abbrechnen");
        cancelButton.setOnAction(e -> Ui.closeWindowOf(this));

        FlowPane bottom = new FlowPane(5, 5, okButton, cancelButton);
        bottom.setAlignment(Pos.BOTTOM_RIGHT);
        bottom.setPadding(new Insets(5));

        setTop(hbox);
        setCenter(reportListView);
        setBottom(bottom);
//        setPrefSize(200, 200);
    }

    public void openNameEdit() {

        Label label1 = new Label("Name: ");
        TextField textField = new TextField();
        final Report selectedReport = reportListView.getSelectionModel().getSelectedItem();
        textField.setText(selectedReport.getName());
        textField.deselect();

        VBox vb = new VBox(label1, textField);

        Dialog<EditResult> dialog = new Dialog<>();
        dialog.setHeaderText("Report Namen ändern");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(vb);
        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, new ButtonType("Save", ButtonData.OK_DONE));
        dialog.setResizable(true);

        dialog.setResultConverter((type) -> type.getButtonData() == OK_DONE ? new EditResult(selectedReport.toKey(), textField.getText()) : null);

        Ui.exec(() -> {
            Ui.dialog().parent(this).eval(() -> dialog)
                    .map(r -> Client.lookup(ReportAgent.class).updateReportName(r.getKey(), r.getText()))
                    .filter(Ui.failure().parent(this)::handle)
                    .ifPresent(r -> {
                        reportListView.getSelectionModel().getSelectedItem().setName(r.getPayload());
                        reportListView.refresh();
                    });
        });

    }

    @Override
    public void accept(List<Report> allReports) {
        reports = FXCollections.observableList(allReports);
        filteredReports = new FilteredList<>(reports);
        reportListView.setItems(filteredReports);

        ObservableList<TradeName> typeItems = FXCollections.observableList(allReports
                .stream()
                .map(Report::getType)
                .distinct()
                .collect(Collectors.toList()));
        typeBox.setItems(typeItems);
        typeBox.getSelectionModel().selectFirst(); // select triggers tradename filter

        ObservableList<Integer> yearItems = FXCollections.observableList(allReports
                .stream()
                .map(r -> LocalDate.from(Instant.ofEpochMilli(r.getStartingDate().getTime()).atZone(systemDefault())).getYear())
                .distinct()
                .collect(Collectors.toList())
        );
        Collections.reverse(yearItems); //newest date on the top oldest on the button

        yearBox.setItems(yearItems);
        yearBox.getSelectionModel().selectFirst(); // select triggers year filter
    }

    public Report selected() {
        return reportListView.getSelectionModel().getSelectedItem();
    }

    @Override
    public Long getResult() {
        if ( ok && reportListView.getSelectionModel().getSelectedItem() != null ) return reportListView.getSelectionModel().getSelectedItem().getId();
        return null;
    }

}