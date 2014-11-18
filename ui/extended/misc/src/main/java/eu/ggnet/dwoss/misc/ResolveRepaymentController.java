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
package eu.ggnet.dwoss.misc;

import java.awt.EventQueue;
import java.net.URL;
import java.util.*;

import javax.swing.JOptionPane;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import eu.ggnet.dwoss.common.AbstractAccessCos;
import eu.ggnet.dwoss.common.ExceptionUtil;
import eu.ggnet.dwoss.misc.op.ResolveRepayment;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.core.Workspace;

import static eu.ggnet.saft.core.Client.lookup;
import static javafx.scene.control.SelectionMode.MULTIPLE;

/**
 *
 * @author bastian.venz
 */
public class ResolveRepaymentController implements Initializable {

    @FXML
    private TableView<ReportLine> reportLineTable;

    @FXML
    private TextField sopoField;

    private final DoubleProperty referencePriceProperty = new SimpleDoubleProperty(0);

    private TradeName contractor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        TableColumn<ReportLine, Long> id = new TableColumn<>("Id");
        id.setCellValueFactory(new PropertyValueFactory("id"));
        TableColumn<ReportLine, String> refurbishId = new TableColumn<>("RefurbishId");
        refurbishId.setCellValueFactory(new PropertyValueFactory("refurbishId"));
        refurbishId.setMinWidth(110);
        TableColumn<ReportLine, Date> reportingDate = new TableColumn<>("Reported");
        reportingDate.setCellValueFactory(new PropertyValueFactory("reportingDate"));
        reportingDate.setMinWidth(110);
        TableColumn<ReportLine, Long> unqiueUnitId = new TableColumn<>("UniqueUnit Id");
        unqiueUnitId.setCellValueFactory(new PropertyValueFactory("uniqueUnitId"));
        TableColumn<ReportLine, TradeName> contractor = new TableColumn<>("contractor");
        contractor.setCellValueFactory(new PropertyValueFactory("contractor"));
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

        reportLineTable.getColumns().addAll(reportingDate, refurbishId, partNo, productName, contractor,
                amount, contractorReferencePrice, price, purchasePrice, documentType, positionType, unqiueUnitId, id);

        reportLineTable.getSelectionModel().setSelectionMode(MULTIPLE);
        reportLineTable.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double ref = reportLineTable.getSelectionModel().getSelectedItems().stream()
                        .map((srl) -> srl.getContractorReferencePrice()).reduce(0., (interimResult, elem) -> interimResult + elem);
                referencePriceProperty.set(ref);
            }
        });
        reportLineTable.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if ( mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2 ) {
                String refurbishId1 = reportLineTable.getSelectionModel().getSelectedItem().getRefurbishId();
                sopoField.setText(refurbishId1);
            }
        });
    }

    public void setContractor(TradeName contractor) {
        this.contractor = contractor;
        List<ReportLine> repaymentLines = lookup(ResolveRepayment.class).getRepaymentLines(contractor);
        reportLineTable.setItems(FXCollections.observableList(repaymentLines));
    }

    @FXML
    public void handleResolveButtonAction() {
        if ( sopoField.getText().isEmpty() ) {
            sopoField.setText(" IDENTIFIER EINGEBEN!!!!!");
            return;
        }
        new Thread(() -> {
            try {
                lookup(ResolveRepayment.class).resolveUnit(sopoField.getText(), contractor, lookup(AbstractAccessCos.class).getUsername());
                JOptionPane.showMessageDialog(null, "Repayment Resolved");
            } catch (UserInfoException ex) {
                EventQueue.invokeLater(() -> ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex));
            }
        }).start();
    }

    @FXML
    public void handleCancelButtonAction() {
        sopoField.getScene().getWindow().hide();
    }

    public static URL loadFxml() {
        return ResolveRepaymentController.class.getResource("ResolveRepayment.fxml");
    }
}
