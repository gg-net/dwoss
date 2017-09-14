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

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.saft.core.Client;

import static java.time.ZoneId.systemDefault;

/**
 *
 * @author pascal.perau
 */
public class ReportSelectionPane extends BorderPane implements Consumer<List<Report>> {

    private final ListView<Report> reportListView;

    private ObservableList<Report> reports;

    private FilteredList<Report> filteredReports;

    private final ComboBox<TradeName> typeBox;

    private final ComboBox<Integer> yearBox;

    private boolean deleteConfirmed = false;

    public ReportSelectionPane() {
        Label reportTypeLabel = new Label("Reporttyp:");
        reportTypeLabel.setStyle("-fx-font-weight: bold;");

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
                        && Objects.equals(LocalDateTime.ofInstant(report.getStartingDate().toInstant(), systemDefault()).getYear(), yearBox.getValue()) //using autoboxing and object equals for safety
                );
            }
        });
        /**
         * Adding a listener for the type dropdrown. 
         * for selecting all reports with the selected year AND the selected trademark.
         */
        yearBox.valueProperty().addListener((ob, ov, newValue) -> {
            if ( filteredReports != null ) {
                filteredReports.setPredicate(report -> LocalDateTime.ofInstant(report.getStartingDate().toInstant(), systemDefault()).getYear() == newValue
                        && report.getType() == typeBox.getValue());
            }
        });

        MenuItem editComment = new MenuItem("Edit Name");
        editComment.setOnAction((ActionEvent event) -> {
            openNameEdit();
        });

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(editComment);

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

        setCenter(reportListView);
        setTop(hbox);
    }

    public void openNameEdit() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        Label label1 = new Label("Name: ");
        TextArea textarea = new TextArea();
        textarea.setText(reportListView.getSelectionModel().getSelectedItem().getName());
        textarea.setEditable(true);
        textarea.setWrapText(true);

        VBox vb = new VBox(label1, textarea);
        grid.add(vb, 0, 0);

        Dialog<ButtonType> dialog = new Dialog<>();
        final DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(grid);
        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        Button okButton = (Button)dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("Save");

        textarea.textProperty().addListener((event, oldValue, newValue) -> {
            if ( oldValue != null && newValue != null ) {
                if ( oldValue.length() > newValue.length() && newValue.length() <= 3 && !deleteConfirmed ) {
                    Alert alert = new Alert(AlertType.INFORMATION, "Do you really want to delete the comment?", ButtonType.OK);
                    alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                        deleteConfirmed = true;
                    });
                }
            }
        });

        Platform.runLater(() -> {
            textarea.requestFocus();
            textarea.end();
        });
        dialog.showAndWait().filter(response -> response == ButtonType.OK)
                .ifPresent(response -> storeName(reportListView.getSelectionModel().getSelectedItem(), textarea.getText()));
    }

    private void storeName(Report report, String input) {
        if ( Client.lookup(ReportAgent.class).updateReportName(report.getOptLock(), report.getId(), input) ) {
            reportListView.getSelectionModel().getSelectedItem().setName(input);
            reportListView.refresh();
            System.out.println("Given ID: " + report.getId() + ", given input: " + input);
        }
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
                .map(r -> LocalDateTime.ofInstant(r.getStartingDate().toInstant(), systemDefault()).getYear())
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

}
