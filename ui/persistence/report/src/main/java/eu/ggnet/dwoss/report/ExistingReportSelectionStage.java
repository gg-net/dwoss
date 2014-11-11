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

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import eu.ggnet.dwoss.report.ReportAgent;
import eu.ggnet.dwoss.report.ReportAgent.ViewReportResult;
import eu.ggnet.dwoss.report.entity.Report;

import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.common.ExceptionUtil;

import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class ExistingReportSelectionStage {

    private final Stage stage;

    private ListView<Report> reportListView;

    private ObservableList<Report> reports;

    private FilteredList<Report> filteredReports;

    private ComboBox<TradeName> typeBox;

    public ExistingReportSelectionStage() {
        List<Report> allReports = lookup(ReportAgent.class).findAll(Report.class);
        Label reportTypeLabel = new Label("Reporttyp:");

        typeBox = new ComboBox<>(FXCollections.observableList(allReports
                .stream()
                .map(Report::getType)
                .distinct()
                .collect(Collectors.toList())));
        typeBox.getSelectionModel().selectFirst();
        typeBox.valueProperty().addListener((ob, ov, newValue) -> filteredReports.setPredicate(report -> report.getType() == newValue));

        VBox vbox = new VBox(10.);
        vbox.getChildren().addAll(reportTypeLabel, typeBox);

        reports = FXCollections.observableList(allReports);

        filteredReports = new FilteredList<>(reports, (Report r) -> r.getType() == typeBox.valueProperty().get());
        reportListView = new ListView<>(filteredReports);
        reportListView.setCellFactory(new ReportListCell.Factory());

        Button button = new Button("OK");
        button.setOnAction((x) -> showReportView(lookup(ReportAgent.class).findReportResult(reportListView.getSelectionModel().getSelectedItem().getId())));
        BorderPane pane = new BorderPane(reportListView, vbox, null, button, null);

        stage = new Stage();
        stage.setTitle("Raw Report Data");
        stage.setScene(new Scene(pane));
    }

    private void showReportView(ViewReportResult reportResult) {
        if ( reportResult == null ) return;

        try {
            FXMLLoader loader = new FXMLLoader(ReportController.class.getResource("ReportView.fxml"));
            Stage reportViewStage = new Stage();
            reportViewStage.setTitle("Report Managment");
            AnchorPane root = (AnchorPane)loader.load();
            reportViewStage.setScene(new Scene(root, Color.ALICEBLUE));
            loader.<ReportController>getController().initReportData(reportResult, true);
            reportViewStage.show();
        } catch (IOException ex) {
            ExceptionUtil.show(null, ex);
        }
    }

    public void show() {
        stage.show();
    }

    public void showAndWait() {
        stage.showAndWait();
    }

}
