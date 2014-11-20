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

import java.awt.EventQueue;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import javax.swing.*;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.metawidget.swing.SwingMetawidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.report.ReportAgent.SearchParameter;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.MetawidgetConfig;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Workspace;

import static eu.ggnet.saft.core.Client.*;
import static javafx.scene.control.SelectionMode.MULTIPLE;

/**
 *
 * @author oliver.guenther
 */
public class SimpleReportLineStage {

    private final Logger L = LoggerFactory.getLogger(SimpleReportLine.class);

    private final Stage stage;

    private final TableView<SimpleReportLine> table;

    private final ObservableList<SimpleReportLine> model;

    private final ProgressBar progressBar;

    private final ProgressIndicator progressIndicator;

    private final Text status;

    private final DoubleProperty referencePriceProperty = new SimpleDoubleProperty(0);

    public SimpleReportLineStage() {
        model = FXCollections.observableArrayList();

        TableColumn<SimpleReportLine, Long> id = new TableColumn<>("Id");
        id.setCellValueFactory(new PropertyValueFactory("id"));
        TableColumn<SimpleReportLine, String> refurbishId = new TableColumn<>("RefurbishId");
        refurbishId.setCellValueFactory(new PropertyValueFactory("refurbishId"));
        refurbishId.setMinWidth(110);
        TableColumn<SimpleReportLine, Date> reportingDate = new TableColumn<>("Reported");
        reportingDate.setCellValueFactory(new PropertyValueFactory("reportingDate"));
        reportingDate.setMinWidth(110);
        TableColumn<SimpleReportLine, Long> unqiueUnitId = new TableColumn<>("UniqueUnit Id");
        unqiueUnitId.setCellValueFactory(new PropertyValueFactory("uniqueUnitId"));
        TableColumn<SimpleReportLine, TradeName> contractor = new TableColumn<>("contractor");
        contractor.setCellValueFactory(new PropertyValueFactory("contractor"));
        TableColumn<SimpleReportLine, String> partNo = new TableColumn<>("PartNo");
        partNo.setCellValueFactory(new PropertyValueFactory("partNo"));
        partNo.setMinWidth(110);
        TableColumn<SimpleReportLine, String> productName = new TableColumn<>("productName");
        productName.setCellValueFactory(new PropertyValueFactory("productName"));
        TableColumn<SimpleReportLine, Double> amount = new TableColumn<>("amount");
        amount.setCellValueFactory(new PropertyValueFactory("amount"));
        TableColumn<SimpleReportLine, Double> price = new TableColumn<>("price");
        price.setCellValueFactory(new PropertyValueFactory("price"));
        TableColumn<SimpleReportLine, Double> purchasePrice = new TableColumn<>("purchasePrice");
        purchasePrice.setCellValueFactory(new PropertyValueFactory("purchasePrice"));
        TableColumn<SimpleReportLine, Double> contractorReferencePrice = new TableColumn<>("Ref.Price");
        contractorReferencePrice.setCellValueFactory(new PropertyValueFactory("contractorReferencePrice"));
        TableColumn<SimpleReportLine, DocumentType> documentType = new TableColumn<>("documentType");
        documentType.setCellValueFactory(new PropertyValueFactory("documentType"));
        TableColumn<SimpleReportLine, PositionType> positionType = new TableColumn<>("positionType");
        positionType.setCellValueFactory(new PropertyValueFactory("positionType"));

        table = new TableView<>();
        table.getColumns().addAll(reportingDate, refurbishId, partNo, productName, contractor, amount, contractorReferencePrice, price, purchasePrice, documentType, positionType, unqiueUnitId, id);
        table.setItems(model);
        table.getSelectionModel().setSelectionMode(MULTIPLE);
        table.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double ref = 0;
                for (SimpleReportLine srl : table.getSelectionModel().getSelectedItems()) {
                    ref += srl.getContractorReferencePrice();
                }
                referencePriceProperty.set(ref);
            }
        });
        table.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if ( mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2 ) {
                    openDetailView(table.getSelectionModel().getSelectedItem().getId());
                }
            }
        });

        Label searchRefurbishIdLabel = new Label("RefurbishId:");
        final TextField searchRefurbishIdField = new TextField();
        Button searchButton = new Button("Search");
        EventHandler<ActionEvent> eh = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                load(new SearchParameter(searchRefurbishIdField.getText()));
            }
        };

        searchButton.setOnAction(eh);
        searchRefurbishIdField.setOnAction(eh);

        HBox top = new HBox();
        top.getChildren().addAll(searchRefurbishIdLabel, searchRefurbishIdField, searchButton);

        VBox right = new VBox();
        right.getChildren().add(new Label("Ref Price"));
        Label referencePriceLabel = new Label("0");
        referencePriceLabel.textProperty().bind(referencePriceProperty.asString("%,.2f €"));
        right.getChildren().add(referencePriceLabel);

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(top);
        mainPane.setCenter(table);
        mainPane.setRight(right);

        progressBar = new ProgressBar();
        progressBar.setMinWidth(200);
        progressBar.setVisible(false);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(20, 20);
        progressIndicator.setVisible(false);
        HBox progress = new HBox();
        progress.getChildren().addAll(progressBar, progressIndicator);
        status = new Text();

        BorderPane lower = new BorderPane();
        lower.setRight(progress);
        lower.setCenter(status);

        mainPane.setBottom(lower);

        stage = new Stage();
        stage.setTitle("Raw Report Data");
        stage.setScene(new Scene(mainPane));
    }

    public void openDetailView(final long reportLineId) {
        CompletableFuture
                .supplyAsync(() -> {
                    ReportLine rl = lookup(ReportAgent.class).findById(ReportLine.class, reportLineId);
                    SwingMetawidget mw = MetawidgetConfig.newSwingMetaWidget(true, 2, ProductGroup.class, TradeName.class, SalesChannel.class, DocumentType.class, PositionType.class, ReportLine.WorkflowStatus.class);
                    mw.setReadOnly(true);
                    mw.setToInspect(rl);
                    return mw;
                })
                .handle((mw, u) -> {
                    EventQueue.invokeLater(() -> {
                        if ( u != null ) u.printStackTrace(); // FIXME !!!!
                        JDialog dialog = new JDialog(lookup(Workspace.class).getMainFrame(), "Details für Reportline");
                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dialog.getContentPane().add(mw);
                        dialog.pack();
                        dialog.setSize(dialog.getSize().width, dialog.getSize().height + 50);
                        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(lookup(Workspace.class).getMainFrame()));
                        dialog.setVisible(true);
                    });
                    return null;
                });
    }

    public void load(final ReportAgent.SearchParameter search) {
        // TODO: Implement Cancel
        Task<Void> task = new Task<Void>() {

            private void updateResult(final Collection<SimpleReportLine> partial) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        model.addAll(partial);
                    }
                });
            }

            @Override
            protected Void call() throws Exception {
                L.info("Starting Loader");
                model.clear();
                final long max = lookup(ReportAgent.class).count(search);
                updateProgress(0, max);
                List<SimpleReportLine> partial;
                int amount = 1;
                int last = 0;
                do {
                    long t1 = System.currentTimeMillis();
                    partial = Client.lookup(ReportAgent.class).findSimple(search, last, amount);
                    long t2 = System.currentTimeMillis();
                    last += amount;
                    updateMessage("Loaded from " + last + " by " + amount);
                    updateProgress(last, max);
                    if ( t2 - t1 < 400 ) amount += (t2 - t1) / 10; // Loadingspeed ist limited to 0.5s per call.
                    updateResult(partial);
                } while (!partial.isEmpty() && !isCancelled());
                updateMessage("");
                return null;
            }

        };
        task.exceptionProperty().addListener(new ChangeListener<Throwable>() {

            @Override
            public void changed(ObservableValue<? extends Throwable> observable, Throwable oldValue, Throwable newValue) {
                throw new RuntimeException("Exception in Task", newValue);
            }
        });
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressBar.visibleProperty().bind(task.runningProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        status.textProperty().bind(task.messageProperty());

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    public void show() {
        stage.show();
    }

    public void showAndWait() {
        stage.showAndWait();
    }

}
