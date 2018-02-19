/*
 * Copyright (C) 2014 bastian.venz
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
package eu.ggnet.dwoss.misc.repayment;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import eu.ggnet.dwoss.misc.ee.ResolveRepayment;
import eu.ggnet.dwoss.misc.ee.ResolveRepayment.ResolveResult;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.core.auth.Guardian;
import eu.ggnet.saft.core.ui.AlertType;

import static javafx.scene.control.SelectionMode.MULTIPLE;

/**
 *
 * @author bastian.venz
 */
@Title("Resolve Repayment")
public class ResolveRepaymentController implements Initializable, FxController, Consumer<TradeName> {

    @FXML
    private TableView<ReportLine> reportLineTable;

    @FXML
    private TextField sopoField;

    @FXML
    private TextArea commentField;

    @FXML
    private Button resolveButton;

    private final DoubleProperty referencePriceProperty = new SimpleDoubleProperty(0);

    private TradeName contractor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        TableColumn<ReportLine, Long> id = new TableColumn<>("Id");
        id.setCellValueFactory((p) -> new SimpleObjectProperty<>(p.getValue().getId()));
        TableColumn<ReportLine, String> refurbishId = new TableColumn<>("RefurbishId");
        refurbishId.setCellValueFactory(new PropertyValueFactory("refurbishId"));
        refurbishId.setMinWidth(110);
        TableColumn<ReportLine, Date> reportingDate = new TableColumn<>("Reported");
        reportingDate.setCellValueFactory(new PropertyValueFactory("reportingDate"));
        reportingDate.setMinWidth(110);
        TableColumn<ReportLine, Long> unqiueUnitId = new TableColumn<>("UniqueUnit Id");
        unqiueUnitId.setCellValueFactory(new PropertyValueFactory("uniqueUnitId"));
        TableColumn<ReportLine, TradeName> contractorColumn = new TableColumn<>("contractor");
        contractorColumn.setCellValueFactory(new PropertyValueFactory("contractor"));
        TableColumn<ReportLine, String> partNo = new TableColumn<>("PartNo");
        partNo.setCellValueFactory(new PropertyValueFactory("partNo"));
        partNo.setMinWidth(110);
        TableColumn<ReportLine, String> productName = new TableColumn<>("productName");
        productName.setCellValueFactory(new PropertyValueFactory("productName"));
        TableColumn<ReportLine, Double> amount = new TableColumn<>("amount");
        amount.setCellValueFactory(new PropertyValueFactory("amount"));
        TableColumn<ReportLine, Double> price = new TableColumn<>("price");
        price.setCellValueFactory(new PropertyValueFactory("price"));
        TableColumn<ReportLine, Double> purchasePrice = new TableColumn<>("purchasePrice");
        purchasePrice.setCellValueFactory(new PropertyValueFactory("purchasePrice"));
        TableColumn<ReportLine, Double> contractorReferencePrice = new TableColumn<>("Ref.Price");
        contractorReferencePrice.setCellValueFactory(new PropertyValueFactory("contractorReferencePrice"));
        TableColumn<ReportLine, DocumentType> documentType = new TableColumn<>("documentType");
        documentType.setCellValueFactory(new PropertyValueFactory("documentType"));
        TableColumn<ReportLine, PositionType> positionType = new TableColumn<>("positionType");
        positionType.setCellValueFactory(new PropertyValueFactory("positionType"));

        reportLineTable.getColumns().addAll(reportingDate, refurbishId, partNo, productName, contractorColumn,
                amount, contractorReferencePrice, price, purchasePrice, documentType, positionType, unqiueUnitId, id);

        reportLineTable.getSelectionModel().setSelectionMode(MULTIPLE);
        reportLineTable.getSelectionModel().selectedIndexProperty().addListener((ov, o, n) -> {
            double ref = reportLineTable.getSelectionModel().getSelectedItems().stream()
                    .map((srl) -> srl.getContractorReferencePrice()).reduce(0., (interimResult, elem) -> interimResult + elem);
            referencePriceProperty.set(ref);
        });
        reportLineTable.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2 ) {
                String refurbishId1 = reportLineTable.getSelectionModel().getSelectedItem().getRefurbishId();
                sopoField.setText(refurbishId1);
            }
        });
    }

    @Override
    public void accept(TradeName contractor) {
        this.contractor = contractor;
        List<ReportLine> repaymentLines = Dl.remote().lookup(ResolveRepayment.class).getRepaymentLines(contractor);
        reportLineTable.setItems(FXCollections.observableList(repaymentLines));
    }

    @FXML
    public void handleResolveButtonAction() {
        if ( sopoField.getText().isEmpty() ) {
            sopoField.setText(" IDENTIFIER EINGEBEN!!!!!");
            return;
        }
        resolveButton.setDisable(true);
        ForkJoinPool.commonPool().execute(() -> {
            try {
                ResolveResult result = Dl.remote().lookup(ResolveRepayment.class).resolveUnit(sopoField.getText(), contractor, Dl.local().lookup(Guardian.class).getUsername(), commentField.getText());
                Ui.build().alert().title("Repayment resolved")
                        .parent(sopoField)
                        .message("Gutschrift gegenüber " + contractor.getName() + " aufgelöst")
                        .nl("Stock: " + result.stockMessage)
                        .nl("RedTape: " + result.redTapeMessage)
                        .nl("Report: " + result.reportMessage)
                        .show(AlertType.INFO);
                reset();
            } catch (UserInfoException ex) {
                Ui.handle(ex);
            } finally {
                reset();
            }
        });
    }

    private void reset() {
        Platform.runLater(() -> {
            resolveButton.setDisable(false);
            sopoField.setText("");
            commentField.setText("");
        });
    }

}
