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
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Callback;

import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ee.ViewReportResult;
import eu.ggnet.dwoss.report.ee.ViewReportResult.Type;
import eu.ggnet.dwoss.report.ee.api.ReportExporter;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.saft.api.IdSupplier;
import eu.ggnet.dwoss.core.widget.Dl;
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
    public static class ResultPane extends Dialog<SelectableViewReportResult> implements Consumer<SelectableViewReportResult> {

        private SelectableViewReportResult reportResult;

        public ResultPane() {
            setHeaderText("Report erstellen ?");
            getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
            setResizable(true);
            setResultConverter((type) -> type.getButtonData() == OK_DONE ? reportResult : null);
        }

        @Override
        public void accept(SelectableViewReportResult reportResult) {
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

        StringProperty refPriceProperty = new SimpleStringProperty();

        StringProperty priceProperty = new SimpleStringProperty();

        StringProperty purchasePriceProperty = new SimpleStringProperty();

        StringProperty marginProperty = new SimpleStringProperty();

        StringProperty refPricePercentageProperty = new SimpleStringProperty();

        StringProperty marginPercentageProperty = new SimpleStringProperty();

        /**
         * Updates all properties to represent the value in a readable formated manner.
         */
        public void update() {
            marginPercentageProperty.set(marginPercentage);
            refPricePercentageProperty.set(referencePricePercentage);
            marginProperty.set(NumberFormat.getCurrencyInstance().format(margin));
            purchasePriceProperty.set(NumberFormat.getCurrencyInstance().format(purchasePrice));
            priceProperty.set(NumberFormat.getCurrencyInstance().format(price));
            refPriceProperty.set(NumberFormat.getCurrencyInstance().format(referencePrice));
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

    private SelectableViewReportResult selectAbleReportResult;

    private final Map<TableView<SelectableReportLine>, TableSummary> tableSummarys = new HashMap<>();

    private final BooleanProperty viewmode = new SimpleBooleanProperty(true);

    public ReportController() {
    }

    public Button getFullExportButton() {
        return fullExportButton;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameLabel.setText("NO REPORT RESULT !! result =" + selectAbleReportResult);
    }

    private List<TableColumn<SelectableReportLine, ?>> getColumnModel() {
        List<TableColumn<SelectableReportLine, ?>> columns = new ArrayList<>();
        TableColumn<SelectableReportLine, Boolean> selectionColumn = new TableColumn("Report");
        selectionColumn.setCellValueFactory(p -> {
            p.getValue().selectionProperty.addListener((o, ov, nv) -> buildSummary(selectionColumn.getTableView()));
            return p.getValue().selectionProperty;
        });
        selectionColumn.setCellFactory(p -> new CheckBoxTableCell<>());
        columns.add(selectionColumn);
        columns.addAll(Arrays.asList(toTableLineColumn("Datum", cell -> new ReadOnlyStringWrapper(Utils.ISO_DATE.format(cell.getValue().reportLine.getReportingDate())).getReadOnlyProperty()),
                toTableLineColumn("SopoNr.", cell -> new ReadOnlyStringWrapper(cell.getValue().reportLine.getRefurbishId()).getReadOnlyProperty()),
                toTableLineColumn("ArtikelNr.", cell -> new ReadOnlyStringWrapper(cell.getValue().reportLine.getPartNo()).getReadOnlyProperty()),
                toTableLineColumn("Bezeichnung", cell -> new ReadOnlyStringWrapper(cell.getValue().reportLine.getName()).getReadOnlyProperty()),
                toTableLineColumn("Seriennummer", cell -> new ReadOnlyStringWrapper(cell.getValue().reportLine.getSerial()).getReadOnlyProperty()),
                toTableLineColumn("MFGDate", cell -> new ReadOnlyStringWrapper(cell.getValue().reportLine.getMfgDate() == null ? "" : Utils.ISO_DATE.format(cell.getValue().reportLine.getMfgDate())).getReadOnlyProperty()),
                toCurrencyColumn("Manufacturer CP", cell -> new ReadOnlyDoubleWrapper(cell.getValue().reportLine.getManufacturerCostPrice()).getReadOnlyProperty()),
                toCurrencyColumn("Contractor RP", cell -> new ReadOnlyDoubleWrapper(cell.getValue().reportLine.getContractorReferencePrice()).getReadOnlyProperty()),
                toCurrencyColumn("VK", cell -> new ReadOnlyDoubleWrapper(cell.getValue().reportLine.getPrice()).getReadOnlyProperty()),
                toCurrencyColumn("EK", cell -> new ReadOnlyDoubleWrapper(cell.getValue().reportLine.getPurchasePrice()).getReadOnlyProperty()),
                toCurrencyColumn("Marge", cell -> new ReadOnlyDoubleWrapper(cell.getValue().reportLine.getPrice() - cell.getValue().reportLine.getPurchasePrice()).getReadOnlyProperty()),
                toTableLineColumn("Rechnungsaddresse", cell -> new ReadOnlyStringWrapper(cell.getValue().reportLine.getInvoiceAddress()).getReadOnlyProperty()),
                toTableLineColumn("DocumentType", cell -> new ReadOnlyStringWrapper(
                cell.getValue().reportLine.getDocumentTypeName() + cell.getValue().reportLine.getWorkflowStatus().sign).getReadOnlyProperty()),
                toTableLineColumn("Lieferanten ArtikelNr.", cell -> new ReadOnlyStringWrapper(cell.getValue().reportLine.getContractorPartNo()).getReadOnlyProperty())
        ));
        return columns;
    }

    private String formatPercentage(double percentage) {
        if ( percentage == 0d || Double.isNaN(percentage) ) return "0 %";
        return NF.format(percentage * 100) + " %";
    }

    private TableColumn<SelectableReportLine, String> toTableLineColumn(String header, Callback<CellDataFeatures<SelectableReportLine, String>, ObservableValue<String>> callback) {
        TableColumn<SelectableReportLine, String> tc = new TableColumn();
        tc.setText(header);
        tc.setCellValueFactory(callback);
        return tc;
    }

    private TableColumn<SelectableReportLine, Number> toCurrencyColumn(String header, Callback<CellDataFeatures<SelectableReportLine, Number>, ObservableValue<Number>> callback) {
        TableColumn<SelectableReportLine, Number> tc = new TableColumn<>();
        tc.setText(header);
        tc.setCellValueFactory(callback);
        tc.setCellFactory(p -> new CurrencyCell());
        return tc;
    }

    private ViewReportResult filterRelevantLines() {
        return selectAbleReportResult == null
                ? null
                : new ViewReportResult(
                        getSelectedLines(selectAbleReportResult.getLines()),
                        selectAbleReportResult.getParameter());
    }

    /**
     * Returns a copy of all relevant report lines.
     * A ReportLine is relevant if {@link ReportLine#addedToReportProperty} isd true.
     * <p>
     * @return a copy of all relevant report lines.
     */
    private EnumMap<Type, NavigableSet<ReportLine>> getSelectedLines(EnumMap<Type, NavigableSet<SelectableReportLine>> lines) {
        EnumMap<Type, NavigableSet<ReportLine>> result = new EnumMap<>(Type.class);
        for (Type keySet : Type.allReportable()) {
            if ( lines.get(keySet) == null ) continue;
            result.put(keySet, lines.get(keySet).stream().filter((sl) -> sl.selectionProperty.get()).map(sl -> sl.reportLine).collect(Collectors.toCollection(() -> new TreeSet<>())));
        }
        return result;
    }

    /**
     * Builds the TableSummary for a TableView.
     * <p>
     * @param lines the TableView a TableSummary is built for.
     * @return the TableSummary for a TableView
     */
    private TableSummary buildSummary(TableView<SelectableReportLine> lines) {
        TableSummary sum = tableSummarys.get(lines) == null ? new TableSummary() : tableSummarys.get(lines);
        sum.clear();
        for (SelectableReportLine line : lines.getItems()) {
            if ( !line.selectionProperty.get() ) continue;
            sum.referencePrice += line.reportLine.getManufacturerCostPrice();
            sum.purchasePrice += line.reportLine.getPurchasePrice();
            sum.margin += (line.reportLine.getPrice() - line.reportLine.getPurchasePrice());
            sum.price += line.reportLine.getPrice();
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
        l.textProperty().bind(summary.refPriceProperty);
        grid.addColumn(0, new Label("Reference Price"), l);

        l = new Label();
        l.textProperty().bind(summary.refPricePercentageProperty);
        grid.addColumn(1, new Label("Reference Price Percentage"), l);

        l = new Label();
        l.textProperty().bind(summary.priceProperty);
        grid.addColumn(2, new Label("Price"), l);

        l = new Label();
        l.textProperty().bind(summary.purchasePriceProperty);
        grid.addColumn(3, new Label("Purchase Price"), l);

        l = new Label();
        l.textProperty().bind(summary.marginProperty);
        grid.addColumn(4, new Label("Margin"), l);

        l = new Label();
        l.textProperty().bind(summary.marginPercentageProperty);
        grid.addColumn(5, new Label("Margin Percentage"), l);

        grid.getChildren().forEach((node) -> GridPane.setHalignment(node, HPos.RIGHT));
        return grid;
    }

    @Override
    public void accept(In in) {
        initReportData(in.reportResult, in.viewmode);
    }

    /**
     * Initialize model data.
     * <p>
     * In viewmode only display concerning actions are allowed. Creation of reports is disabled.
     * <p>
     * @param in       the result containing report data
     * @param viewmode is this only viewmode or not.
     */
    public void initReportData(ViewReportResult in, boolean viewmode) {
        this.viewmode.set(viewmode);
        createButton.disableProperty().bind(this.viewmode);

        exportButton.setDisable(!Dl.remote().contains(ReportExporter.class));

        this.selectAbleReportResult = new SelectableViewReportResult(in);
        nameLabel.setText(selectAbleReportResult.getParameter().reportName());
        fromDateLabel.setText(Utils.ISO_DATE.format(selectAbleReportResult.getParameter().start()));
        toDateLabel.setText(Utils.ISO_DATE.format(selectAbleReportResult.getParameter().end()));

        selectAbleReportResult.getLines().keySet().stream().map((Type type) -> {

            //built tab
            Tab t = new Tab(type.name());

            TableView<SelectableReportLine> table = new TableView<>(FXCollections.observableArrayList(selectAbleReportResult.getLines().get(type)));
            table.getColumns().addAll(getColumnModel());
            table.setEditable(true);
            table.getSelectionModel().setSelectionMode(MULTIPLE);

            MenuItem select = new MenuItem("Select");
            select.setOnAction(e -> {
                table.getSelectionModel().getSelectedItems().forEach(i -> i.selectionProperty.set(true));
                table.getSelectionModel().clearSelection();
            });
            MenuItem deselet = new MenuItem("Deselect");
            deselet.setOnAction(e -> {
                table.getSelectionModel().getSelectedItems().forEach(i -> i.selectionProperty.set(false));
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
                    .eval(() -> selectAbleReportResult, () -> new ResultPane())
                    .opt()
                    .ifPresent(r -> Ui.progress().call(() -> {
                Dl.remote().lookup(ReportAgent.class).store(
                        new Report(r.getParameter().reportName(), r.getParameter().contractor(), r.getParameter().start(), r.getParameter().end(), r.getParameter().viewMode()),
                        getSelectedLines(r.getLines()).values().stream().flatMap(Collection::stream).map(ReportLine::toStorable).collect(Collectors.toList()));
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
