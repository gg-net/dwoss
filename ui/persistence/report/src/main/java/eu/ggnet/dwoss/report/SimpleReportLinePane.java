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
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.ListChangeListener.Change;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import org.apache.commons.lang3.StringUtils;
import org.metawidget.swing.SwingMetawidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.report.ReportAgent.SearchParameter;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.MetawidgetConfig;
import eu.ggnet.saft.api.ui.Frame;
import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Workspace;

import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.scene.control.SelectionMode.MULTIPLE;

/**
 *
 * @author oliver.guenther
 */
@Frame
@Title("Report Daten")
public class SimpleReportLinePane extends BorderPane {

    private final Logger L = LoggerFactory.getLogger(SimpleReportLine.class);

    private final TableView<SimpleReportLine> table;

    private final ObservableList<SimpleReportLine> model;

    private final ProgressBar progressBar;

    private final ProgressIndicator progressIndicator;

    private final Text status;

    private final DoubleProperty referencePriceProperty = new SimpleDoubleProperty(0);

    private boolean deleteConfirmed = false;

    public SimpleReportLinePane() {
        model = FXCollections.observableArrayList();

        //building the Searchar on Top 
        Label searchRefurbishIdLabel = new Label("RefurbishId:");
        final TextField searchRefurbishIdField = new TextField();
        Button searchButton = new Button("Search");
        EventHandler<ActionEvent> eh = (e) -> {
            load(new SearchParameter(searchRefurbishIdField.getText()));
        };
        searchButton.setOnAction(eh);
        searchRefurbishIdField.setOnAction(eh);

        HBox top = new HBox();
        top.setSpacing(5);
        top.setAlignment(CENTER_LEFT);
        Label selectedRefPrice = new Label("Selected Reference Price:");
        selectedRefPrice.setStyle("-fx-font-weight: bold;");

        Label referencePriceLabel = new Label("0");
        referencePriceLabel.textProperty().bind(referencePriceProperty.asString("%,.2f €"));
        top.getChildren().addAll(searchRefurbishIdLabel, searchRefurbishIdField, searchButton, selectedRefPrice, referencePriceLabel);

        setTop(top);

        //building context Menu for right mouse button for this table
        MenuItem editComment = new MenuItem("Edit Comment");
        editComment.setOnAction((ActionEvent event) -> {
            openCommentEdit();
        });
        MenuItem deleldComment = new MenuItem("Delet Comment");
        deleldComment.setOnAction((ActionEvent event) -> {
            openCommentDelet();
        });

        //TODO add more Item like delete/copy/cut/paste SimpleReportLine to the contextmenu
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(editComment, deleldComment);

        //build all the Colums 
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
        table.getColumns().addAll(
                reportingDate,
                refurbishId,
                partNo,
                productName,
                contractor,
                amount,
                contractorReferencePrice,
                price,
                purchasePrice,
                documentType,
                positionType,
                id,
                unqiueUnitId);
        table.setItems(model);
        table.getSelectionModel().setSelectionMode(MULTIPLE);

        //updated the selectet reference price on the top
        table.getSelectionModel().getSelectedIndices().addListener((Change<? extends Integer> c) -> {
            referencePriceProperty.set(
                    table.getSelectionModel().getSelectedItems().stream()
                            .mapToDouble(line -> line.getContractorReferencePrice()).sum()
            );
        });
        //adding a RowFactory to show the Comment of a SimpleReportLine as a Tooltip
        table.setRowFactory((view) -> new TableRow<SimpleReportLine>() {
            private Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(SimpleReportLine item, boolean empty) {
                super.updateItem(item, empty);
                tooltip.setText(item == null ? "" : item.getComment());
                setTooltip(tooltip);
            }
        });

        /**
         * open the Detail Dialog on doubleclick.
         * allways close the Context Menu. To catch miss-clicks on open Context Menu
         */
        table.setOnMouseClicked((event) -> {
            if ( event.getButton().equals(MouseButton.PRIMARY) ) {
                contextMenu.hide();
                if ( event.getClickCount() == 2 ) {
                    openDetailView(table.getSelectionModel().getSelectedItem().getId());
                }
            }
        });

        //open the Contextmenu next to the mouse cursor 
        table.setOnContextMenuRequested((ContextMenuEvent event) -> {
            contextMenu.show(table, event.getScreenX(), event.getScreenY());
        });

        setCenter(table);

        //show the Progressbar on the lower end of the Window
        progressBar = new ProgressBar();
        progressBar.setMinWidth(200);
        progressBar.setVisible(false);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(20, 20);
        progressIndicator.setVisible(false);

        HBox progress = new HBox();
        progress.getChildren().addAll(progressBar, progressIndicator);
        BorderPane lower = new BorderPane();
        status = new Text();

        lower.setRight(progress);
        lower.setCenter(status);

        setBottom(lower);
    }

    /**
     * Save the new Comment to the Database
     * TODO
     * something like --> update dw_report.ReportLine set comment="test" where id=9;
     *
     * @param report
     * @param input
     */
    public void storeComment(SimpleReportLine report, String input) {
        if ( Client.lookup(ReportAgent.class).updateReportName(report.getOptLock(), report.getId(), input) ) {
            table.getSelectionModel().getSelectedItem().setComment(input);
            table.refresh();
            System.out.println("Given ID: " + report.getId() + ", given input: " + input);
        }
    }

    /**
     * open Dialog for Comment.
     * An small Textarea with a Save and Cancle Button
     */
    public void openCommentEdit() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        Label label1 = new Label("Comment: ");
        TextArea textarea = new TextArea();
        textarea.setText(table.getSelectionModel().getSelectedItem().getComment());
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
                .ifPresent(response -> storeComment(table.getSelectionModel().getSelectedItem(), textarea.getText()));

    }

    /**
     * open Dialog for deleting the comment.
     * only Cancle and Ok Button to informate the user abbout that the deleting is permanent
     */
    public void openCommentDelet() {
        String input = "";
        GridPane grid = new GridPane();
        Label label1 = new Label("Do you really want to delete the comment? ");
        grid.add(label1, 0, 0);

        Dialog<ButtonType> dialog = new Dialog<>();
        final DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(grid);
        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.showAndWait().filter(response -> response == ButtonType.OK)
                .ifPresent(response -> storeComment(table.getSelectionModel().getSelectedItem(), input));

    }

    /**
     * Open the DetailView
     * this get autogenerated by Metawidget in Swing
     *
     * @param reportLineId
     */
    public void openDetailView(final long reportLineId) {
        CompletableFuture
                .supplyAsync(() -> {
                    ReportLine rl = Client.lookup(ReportAgent.class).findById(ReportLine.class, reportLineId);
                    SwingMetawidget mw = MetawidgetConfig.newSwingMetaWidget(true, 2, ProductGroup.class,
                            TradeName.class,
                            SalesChannel.class,
                            DocumentType.class,
                            PositionType.class,
                            ReportLine.WorkflowStatus.class
                    );
                    mw.setReadOnly(true);
                    mw.setToInspect(rl);
                    return mw;
                })
                .handle((mw, u) -> {
                    EventQueue.invokeLater(() -> {
                        if ( u != null ) {
                            u.printStackTrace(); // FIXME !!!!
                        }

                        JDialog dialog = new JDialog(Client.lookup(Workspace.class).getMainFrame(), "Details für Reportline");
                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dialog.getContentPane().add(mw);
                        dialog.pack();
                        dialog.setSize(dialog.getSize().width, dialog.getSize().height + 50);
                        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(Client.lookup(Workspace.class).getMainFrame()));
                        dialog.setVisible(true);
                    });
                    return null;
                });
    }

    /**
     * Load the Search result into the tablemodle
     * Loadingspeed ist limited to 0.5s per call. (why ?)
     *
     * @param search
     */
    public void load(final ReportAgent.SearchParameter search) {
        // TODO: Implement Cancel
        Task<Void> task = new Task<Void>() {

            private void updateResult(final Collection<SimpleReportLine> partial) {
                Platform.runLater(() -> {
                    model.addAll(partial);
                });
            }

            @Override
            protected Void call() throws Exception {
                L.info("Starting Loader");
                model.clear();
                final long max = Client.lookup(ReportAgent.class).count(search);
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
                    //limited the speed
                    if ( t2 - t1 < 400 ) {
                        amount += (t2 - t1) / 10;
                    }
                    updateResult(partial);
                } while (!partial.isEmpty() && !isCancelled());
                updateMessage("");
                return null;
            }
        };

        task.exceptionProperty().addListener((ov, o, n) -> {
            throw new RuntimeException("Exception in Task", n);
        });

        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressBar.visibleProperty().bind(task.runningProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        status.textProperty().bind(task.messageProperty());

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

}
