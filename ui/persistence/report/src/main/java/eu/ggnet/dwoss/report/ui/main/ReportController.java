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
package eu.ggnet.dwoss.report.ui.main;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Callback;

import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ee.ViewReportResult;
import eu.ggnet.dwoss.report.ee.ViewReportResult.Type;
import eu.ggnet.dwoss.report.ee.api.ReportExporter;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.core.system.Utils;
import eu.ggnet.saft.api.IdSupplier;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.*;

import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;
import static javafx.scene.control.SelectionMode.MULTIPLE;

/**
 * Controller for the report view.
 *
 * @author pascal.perau
 */
@Frame
@Title("Report Ansicht : {id}")
@StoreLocation
public class ReportController implements Initializable, FxController, Consumer<ReportController.In> {

    @Override
    public void accept(In in) {
        initReportData(in.reportResult, in.viewmode);
    }

    public static class In implements IdSupplier {

        private final ViewReportResult reportResult;

        private final boolean viewmode;

        public In(ViewReportResult reportResult, boolean viewmode) {
            this.reportResult = reportResult;
            this.viewmode = viewmode;
        }

        @Override
        public String id() {
            return reportResult.getParameter().reportName();
        }

    }

    @Title("Wollen Sie wirklich diesen Report erstellen?")
    public static class ResultPane extends Dialog<ViewReportResult> implements Consumer<ViewReportResult> {

        private ViewReportResult reportResult;

        public ResultPane() {
            setHeaderText("Report erstellen ?");
            getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
            setResizable(true);
            setResultConverter((type) -> type.getButtonData() == OK_DONE ? reportResult : null);
        }

        @Override
        public void accept(ViewReportResult reportResult) {
            this.reportResult = reportResult;
            String infoLine = "Name: " + reportResult.getParameter().reportName();
            infoLine += "\nStart: " + Utils.ISO_DATE.format(reportResult.getParameter().start());
            infoLine += "\nEnde: " + Utils.ISO_DATE.format(reportResult.getParameter().end());
            setContentText(infoLine);
        }

    }

    private class TableSummary {

        double referencePrice;

        String referencePricePercentage = "";

        double price;

        double purchasePrice;

        double margin;

        String marginPercentage = "";

        StringProperty refPriceP = new SimpleStringProperty();

        StringProperty priceP = new SimpleStringProperty();

        StringProperty purchasePriceP = new SimpleStringProperty();

        StringProperty marginP = new SimpleStringProperty();

        StringProperty refPricePercentageP = new SimpleStringProperty();

        StringProperty marginPercentageP = new SimpleStringProperty();

        /**
         * Updates all properties to represent the value in a readable formated manner.
         */
        public void update() {
            marginPercentageP.set(marginPercentage);
            refPricePercentageP.set(referencePricePercentage);
            marginP.set(NumberFormat.getCurrencyInstance().format(margin));
            purchasePriceP.set(NumberFormat.getCurrencyInstance().format(purchasePrice));
            priceP.set(NumberFormat.getCurrencyInstance().format(price));
            refPriceP.set(NumberFormat.getCurrencyInstance().format(referencePrice));
        }

        /**
         * Sets all non propertie values to a default.
         * Call update afterwards for property updates.
         */
        public void clear() {
            referencePrice = 0;
            price = 0;
            purchasePrice = 0;
            margin = 0;
            referencePricePercentage = "0";
            marginPercentage = "0";
        }

    }

    private final NumberFormat NF = new DecimalFormat(",##0.00");

    @FXML
    AnchorPane mainPane;

    @FXML
    Label nameLabel;

    @FXML
    Label fromDateLabel;

    @FXML
    Label toDateLabel;

    @FXML
    TabPane tabPane;

    @FXML
    Button createButton;

    @FXML
    Button exportButton;

    @FXML
    Button fullExportButton;

    private ViewReportResult reportResult;

    private final Map<TableView<ReportLine>, TableSummary> tableSummarys = new HashMap<>();

    private final BooleanProperty viewmode = new SimpleBooleanProperty(true);

    public ReportController() {
    }

    public Button getFullExportButton() {
        return fullExportButton;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameLabel.setText("NO REPORT RESULT !! result =" + reportResult);
    }

    private List<TableColumn<ReportLine, ?>> getColumnModel() {
        List<TableColumn<ReportLine, ?>> columns = new ArrayList<>();
        TableColumn<ReportLine, Boolean> column = new TableColumn("Report");
        column.setCellValueFactory(p -> {
            p.getValue().addedToReportProperty().addListener((o, ov, nv) -> buildSummary(column.getTableView()));
            return p.getValue().addedToReportProperty();
        });
        column.setCellFactory(p -> new CheckBoxTableCell<>());
        columns.add(column);
        columns.addAll(Arrays.asList(toTableLineColumn("Datum", cell -> new ReadOnlyStringWrapper(Utils.ISO_DATE.format(cell.getValue().getReportingDate())).getReadOnlyProperty()),
                        toTableLineColumn("SopoNr.", cell -> new ReadOnlyStringWrapper(cell.getValue().getRefurbishId()).getReadOnlyProperty()),
                        toTableLineColumn("ArtikelNr.", cell -> new ReadOnlyStringWrapper(cell.getValue().getPartNo()).getReadOnlyProperty()),
                        toTableLineColumn("Bezeichnung", cell -> new ReadOnlyStringWrapper(cell.getValue().getName()).getReadOnlyProperty()),
                        toTableLineColumn("Seriennummer", cell -> new ReadOnlyStringWrapper(cell.getValue().getSerial()).getReadOnlyProperty()),
                        toTableLineColumn("MFGDate", cell -> new ReadOnlyStringWrapper(cell.getValue().getMfgDate() == null ? "" : Utils.ISO_DATE.format(cell.getValue().getMfgDate())).getReadOnlyProperty()),
                        toCurrencyColumn("Manufacturer CP", cell -> cell.getValue().manufacturerCostPriceProperty()),
                        toCurrencyColumn("Contractor RP", cell -> cell.getValue().contractorReferencePriceProperty()),
                        toCurrencyColumn("VK", cell -> cell.getValue().priceProperty()),
                        toCurrencyColumn("EK", cell -> cell.getValue().purchasePriceProperty()),
                        toCurrencyColumn("Marge", cell -> new ReadOnlyDoubleWrapper(cell.getValue().getPrice() - cell.getValue().getPurchasePrice()).getReadOnlyProperty()),
                        toTableLineColumn("Rechnungsaddresse", cell -> new ReadOnlyStringWrapper(cell.getValue().getInvoiceAddress()).getReadOnlyProperty()),
                        toTableLineColumn("DocumentType", cell -> new ReadOnlyStringWrapper(
                        cell.getValue().getDocumentTypeName() + cell.getValue().getWorkflowStatus().sign).getReadOnlyProperty()),
                        toTableLineColumn("Lieferanten ArtikelNr.", cell -> new ReadOnlyStringWrapper(cell.getValue().getContractorPartNo()).getReadOnlyProperty())
                ));
        return columns;
    }

    private String formatPercentage(double percentage) {
        if ( percentage == 0d || Double.isNaN(percentage) ) return "0 %";
        return NF.format(percentage * 100) + " %";
    }

    private TableColumn<ReportLine, String> toTableLineColumn(String header, Callback<CellDataFeatures<ReportLine, String>, ObservableValue<String>> callback) {
        TableColumn tc = new TableColumn();
        tc.setText(header);
        tc.setCellValueFactory(callback);
        return tc;
    }

    private TableColumn<ReportLine, Number> toCurrencyColumn(String header, Callback<CellDataFeatures<ReportLine, Number>, ObservableValue<Number>> callback) {
        TableColumn<ReportLine, Number> tc = new TableColumn<>();
        tc.setText(header);
        tc.setCellValueFactory(callback);
        tc.setCellFactory(p -> new CurrencyCell());
        return tc;
    }

    private ViewReportResult filterRelevantLines() {
        return reportResult == null
                ? null
                : new ViewReportResult(
                        reportResult.getRelevantLines(),
                        reportResult.getParameter());
    }

    /**
     * Builds the TableSummary for a TableView.
     * <p>
     * @param lines the TableView a TableSummary is built for.
     * @return the TableSummary for a TableView
     */
    private TableSummary buildSummary(TableView<ReportLine> lines) {
        TableSummary sum = tableSummarys.get(lines) == null ? new TableSummary() : tableSummarys.get(lines);
        sum.clear();
        for (ReportLine line : lines.getItems()) {
            if ( !line.isAddedToReport() ) continue;
            sum.referencePrice += line.getManufacturerCostPrice();
            sum.purchasePrice += line.getPurchasePrice();
            sum.margin += (line.getPrice() - line.getPurchasePrice());
            sum.price += line.getPrice();
        }

        if ( sum.price != 0 && sum.referencePrice != 0 )
            sum.referencePricePercentage = formatPercentage(sum.price / sum.referencePrice);
        if ( sum.margin != 0 && sum.purchasePrice != 0 )
            sum.marginPercentage = formatPercentage(sum.margin / sum.purchasePrice);

        sum.update();
        return sum;
    }

    /**
     * Builds the GridPane for a summary.
     * <p>
     * @param summary the TableSUmmary a GridPane is built for.
     * @return the GridPane for a summary.
     */
    private GridPane buildSummaryPanel(TableSummary summary) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(8));
        grid.setAlignment(Pos.BOTTOM_RIGHT);
        grid.setHgap(20);
        grid.setVgap(10);

        Label l = new Label();
        l.textProperty().bind(summary.refPriceP);
        grid.addColumn(0, new Label("Reference Price"), l);

        l = new Label();
        l.textProperty().bind(summary.refPricePercentageP);
        grid.addColumn(1, new Label("Reference Price Percentage"), l);

        l = new Label();
        l.textProperty().bind(summary.priceP);
        grid.addColumn(2, new Label("Price"), l);

        l = new Label();
        l.textProperty().bind(summary.purchasePriceP);
        grid.addColumn(3, new Label("Purchase Price"), l);

        l = new Label();
        l.textProperty().bind(summary.marginP);
        grid.addColumn(4, new Label("Margin"), l);

        l = new Label();
        l.textProperty().bind(summary.marginPercentageP);
        grid.addColumn(5, new Label("Margin Percentage"), l);

        for (Node get : grid.getChildren()) {
            GridPane.setHalignment(get, HPos.RIGHT);
        }
        return grid;
    }

    /**
     * Initialize model data.
     * <p>
     * In viewmode only display concerning actions are allowed. Creation of reports is disabled.
     * <p>
     * @param reportResult the result containing report data
     * @param viewmode     is this only viewmode or not.
     */
    public void initReportData(ViewReportResult reportResult, boolean viewmode) {
        this.viewmode.set(viewmode);
        createButton.disableProperty().bind(this.viewmode);

        exportButton.setDisable(!Dl.remote().contains(ReportExporter.class));

        this.reportResult = reportResult;
        nameLabel.setText(reportResult.getParameter().reportName());
        fromDateLabel.setText(Utils.ISO_DATE.format(reportResult.getParameter().start()));
        toDateLabel.setText(Utils.ISO_DATE.format(reportResult.getParameter().end()));

        reportResult.getLines().keySet().stream().map((Type type) -> {
            for (ReportLine reportLine : reportResult.getLines().get(type)) {
                reportLine.setAddedToReport(true);
            }

            //built tab
            Tab t = new Tab(type.name());

            //built table
            TableView<ReportLine> table = new TableView<>(FXCollections.observableArrayList(reportResult.getLines().get(type)));
            table.getColumns().addAll(getColumnModel());
            table.setEditable(true);
            table.getSelectionModel().setSelectionMode(MULTIPLE);

            MenuItem select = new MenuItem("Select");
            select.setOnAction(e -> {
                table.getSelectionModel().getSelectedItems().forEach(i -> i.setAddedToReport(true));
                table.getSelectionModel().clearSelection();
            });
            MenuItem deselet = new MenuItem("Deselect");
            deselet.setOnAction(e -> {
                table.getSelectionModel().getSelectedItems().forEach(i -> i.setAddedToReport(false));
                table.getSelectionModel().clearSelection();
            });

            ContextMenu cm = new ContextMenu();
            cm.getItems().add(select);
            cm.getItems().add(deselet);

            table.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent t1) -> {
                if ( t1.getButton() == MouseButton.SECONDARY ) {
                    cm.show(table, t1.getScreenX(), t1.getScreenY());
                }
            });

            //built summary
            TableSummary sum = buildSummary(table);
            GridPane grid = buildSummaryPanel(sum);

            //map reference
            tableSummarys.put(table, sum);

            //add content
            t.setContent(new BorderPane(table, null, null, grid, null));
            return t;
        }).forEach((t) -> {
            tabPane.getTabs().add(t);
        });
    }

    @FXML
    public void handleCreateButtonAction() {
        Ui.exec(() -> {
            Ui.build().parent(mainPane).dialog()
                    .eval(() -> reportResult, () -> new ResultPane())
                    .opt()
                    .ifPresent(r -> Ui.progress().call(() -> {
                Dl.remote().lookup(ReportAgent.class).store(
                        new Report(r.getParameter().reportName(), r.getParameter().contractor(), r.getParameter().start(),r.getParameter().end(), r.getParameter().viewMode()),
                        r.getRelevantLines().values().stream().flatMap(Collection::stream).map(ReportLine::toStorable).collect(Collectors.toList()));
                Platform.runLater(() -> viewmode.set(true));
                return null;
            }));
        });
    }

    @FXML
    public void handleExportButtonAction() {
        Ui.osOpen(Dl.remote().lookup(ReportExporter.class).toFullXls(filterRelevantLines()).toTemporaryFile());
    }

    @FXML
    public void handleFullExportButtonAction() {
        Ui.osOpen(XlsExporter.toFullXls(filterRelevantLines()));
    }

    public static URL loadFxml() {
        return ReportController.class.getResource("ReportView.fxml");
    }
}
