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
package eu.ggnet.dwoss.report.ui;

import java.util.*;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.HtmlPane;
import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ee.ReportAgent.SearchParameter;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.Frame;
import eu.ggnet.saft.core.ui.Title;

import jakarta.enterprise.context.Dependent;

import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.scene.control.SelectionMode.MULTIPLE;

/**
 *
 * @author oliver.guenther
 */
@Dependent
@Frame
@Title("Report Daten")
public class RawReportView extends BorderPane {
    
    public final static String ONCE_KEY = "RawReport";

    private final Logger L = LoggerFactory.getLogger(SimpleReportLine.class);

    private final TableView<SimpleReportLine> table;

    private final ObservableList<SimpleReportLine> model;

    private final ProgressBar progressBar;

    private final ProgressIndicator progressIndicator;

    private final Text status;

    private final DoubleProperty referencePriceProperty = new SimpleDoubleProperty(0);

    private Tooltip tooltip = new Tooltip();

    public RawReportView() {
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
        editComment.setOnAction(e -> openCommentEdit());

        MenuItem deleldComment = new MenuItem("Delet Comment");
        deleldComment.setOnAction(e -> openCommentDelete());

        //TODO add more Item like delete/copy/cut/paste SimpleReportLine to the contextmenu
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(editComment, deleldComment);

        //build all the Colums
        TableColumn<SimpleReportLine, Number> id = new TableColumn<>("Id");
        id.setCellValueFactory(v -> new ReadOnlyLongWrapper(v.getValue().getId()).getReadOnlyProperty());

        TableColumn<SimpleReportLine, String> refurbishId = new TableColumn<>("RefurbishId");
        refurbishId.setCellValueFactory(v -> new ReadOnlyStringWrapper(v.getValue().getRefurbishId()).getReadOnlyProperty());
        refurbishId.setMinWidth(110);

        TableColumn<SimpleReportLine, Date> reportingDate = new TableColumn<>("Reported");
        reportingDate.setCellValueFactory(v -> new ReadOnlyObjectWrapper<>(v.getValue().getReportingDate()).getReadOnlyProperty());
        reportingDate.setMinWidth(110);

        TableColumn<SimpleReportLine, Number> unqiueUnitId = new TableColumn<>("UniqueUnit Id");
        unqiueUnitId.setCellValueFactory(v -> new ReadOnlyLongWrapper(v.getValue().getUniqueUnitId()).getReadOnlyProperty());

        TableColumn<SimpleReportLine, TradeName> contractor = new TableColumn<>("contractor");
        contractor.setCellValueFactory(v -> new ReadOnlyObjectWrapper<>(v.getValue().getContractor()).getReadOnlyProperty());

        TableColumn<SimpleReportLine, String> partNo = new TableColumn<>("PartNo");
        partNo.setCellValueFactory(v -> new ReadOnlyStringWrapper(v.getValue().getPartNo()).getReadOnlyProperty());
        partNo.setMinWidth(110);

        TableColumn<SimpleReportLine, String> productName = new TableColumn<>("productName");
        productName.setCellValueFactory(v -> new ReadOnlyStringWrapper(v.getValue().getProductName()).getReadOnlyProperty());

        TableColumn<SimpleReportLine, Number> amount = new TableColumn<>("amount");
        amount.setCellValueFactory(v -> new ReadOnlyDoubleWrapper(v.getValue().getAmount()).getReadOnlyProperty());

        TableColumn<SimpleReportLine, Number> price = new TableColumn<>("price");
        price.setCellValueFactory(v -> new ReadOnlyDoubleWrapper(v.getValue().getPrice()).getReadOnlyProperty());

        TableColumn<SimpleReportLine, Number> purchasePrice = new TableColumn<>("purchasePrice");
        purchasePrice.setCellValueFactory(v -> new ReadOnlyDoubleWrapper(v.getValue().getPurchasePrice()).getReadOnlyProperty());

        TableColumn<SimpleReportLine, Number> contractorReferencePrice = new TableColumn<>("Ref.Price");
        contractorReferencePrice.setCellValueFactory(v -> new ReadOnlyDoubleWrapper(v.getValue().getContractorReferencePrice()).getReadOnlyProperty());

        TableColumn<SimpleReportLine, DocumentType> documentType = new TableColumn<>("documentType");
        documentType.setCellValueFactory(v -> new ReadOnlyObjectWrapper<>(v.getValue().getDocumentType()).getReadOnlyProperty());

        TableColumn<SimpleReportLine, PositionType> positionType = new TableColumn<>("positionType");
        positionType.setCellValueFactory(v -> new ReadOnlyObjectWrapper<>(v.getValue().getPositionType()).getReadOnlyProperty());

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
                if ( event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null ) {
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
     *
     * @param line
     * @param input
     */
    public void storeComment(SimpleReportLine line, String input) {
        if ( Dl.remote().lookup(ReportAgent.class).updateReportLineComment(line.getOptLock(), line.getId(), input) ) {
            table.getSelectionModel().getSelectedItem().setComment(input);
            table.refresh();
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
        Ui.exec(() -> {
            Ui.build().parent(this).dialog().eval(() -> dialog)
                    .opt()
                    .filter(result -> result == ButtonType.OK)
                    .ifPresent(response -> storeComment(table.getSelectionModel().getSelectedItem(), textarea.getText()));
        });
    }

    /**
     * open Dialog for deleting the comment.
     * only Cancle and Ok Button to informate the user abbout that the deleting is permanent
     */
    public void openCommentDelete() {
        String input = "";
        GridPane grid = new GridPane();
        Label label1 = new Label("Möchten Sie den Kommentar löschen ?");
        grid.add(label1, 0, 0);

        Dialog<ButtonType> dialog = new Dialog<>();
        final DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(grid);
        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Ui.build(this).dialog().eval(() -> dialog).opt().filter(response -> response == ButtonType.OK)
                .ifPresent(response -> storeComment(table.getSelectionModel().getSelectedItem(), input));

    }

    /**
     * Open the DetailView
     * this get autogenerated by Metawidget in Swing
     *
     * @param reportLineId
     */
    public void openDetailView(final long reportLineId) {
        Ui.exec(() -> {
            Ui.build(this).fx().show(() -> Dl.remote().lookup(ReportAgent.class).findById(ReportLine.class, reportLineId).toHtml(), () -> new HtmlPane());
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
                final long max = Dl.remote().lookup(ReportAgent.class).count(search);
                updateProgress(0, max);
                List<SimpleReportLine> partial;
                int amount = 1;
                int last = 0;
                do {
                    long t1 = System.currentTimeMillis();
                    partial = Dl.remote().lookup(ReportAgent.class).findSimple(search, last, amount);
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
