/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.redtapext.ui.cao.jasper;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.ResultProducer;

/**
 * An simple approach to JasperViewer in JavaFX.
 *
 * @author Gustavo Fragoso, Oliver Guenther
 * @version 3.3
 */
public class JasperFxView extends StackPane implements Consumer<JasperFxViewData>, ResultProducer<JasperFxViewResult> {

    private final Button btnPrint;

    private final Button btnSave;

    private final Button btnBackPage;

    private final Button btnFirstPage;

    private final Button btnNextPage;

    private final Button btnLastPage;

    private final Button btnZoomIn;

    private final Button btnZoomOut;

    private final Button btnEmail;

    private final ImageView report;

    private final Label lblReportPages;

    private Stage view;

    private TextField txtPage;

    private JasperPrint jasperPrint;

    private int imageHeight = 0;

    private int imageWidth = 0;

    private int reportPages = 0;

    private final SimpleIntegerProperty currentPage = new SimpleIntegerProperty(this, "currentPage", 1);

    private JasperFxViewData in;

    private boolean correctlyBriefed = false;

    public JasperFxView() {

        setOnKeyPressed((e) -> {
            if ( e.isControlDown() && e.getCode() == KeyCode.P ) print();
        });

        btnPrint = new Button(null, new ImageView(getClass().getResource("printer.png").toExternalForm()));
        btnSave = new Button(null, new ImageView(getClass().getResource("save.png").toExternalForm()));
        btnEmail = new Button(null, new ImageView(getClass().getResource("mail.png").toExternalForm()));
        btnBackPage = new Button(null, new ImageView(getClass().getResource("backimg.png").toExternalForm()));
        btnFirstPage = new Button(null, new ImageView(getClass().getResource("firstimg.png").toExternalForm()));
        btnNextPage = new Button(null, new ImageView(getClass().getResource("nextimg.png").toExternalForm()));
        btnLastPage = new Button(null, new ImageView(getClass().getResource("lastimg.png").toExternalForm()));
        btnZoomIn = new Button(null, new ImageView(getClass().getResource("zoomin.png").toExternalForm()));
        btnZoomOut = new Button(null, new ImageView(getClass().getResource("zoomout.png").toExternalForm()));

        btnPrint.setPrefSize(30, 30);

        btnSave.setPrefSize(30, 30);
        btnEmail.setPrefSize(30, 30);
        btnBackPage.setPrefSize(30, 30);
        btnFirstPage.setPrefSize(30, 30);
        btnNextPage.setPrefSize(30, 30);
        btnLastPage.setPrefSize(30, 30);
        btnZoomIn.setPrefSize(30, 30);
        btnZoomOut.setPrefSize(30, 30);

        btnPrint.setOnAction(event -> print());
        btnSave.setOnAction(event -> saveToFile());
        btnEmail.setOnAction(e -> mail());

        btnBackPage.setOnAction(event -> renderPage(getCurrentPage() - 1));
        btnFirstPage.setOnAction(event -> renderPage(1));
        btnNextPage.setOnAction(event -> renderPage(getCurrentPage() + 1));
        btnLastPage.setOnAction(event -> renderPage(reportPages));
        btnZoomIn.setOnAction(event -> zoom(0.15));
        btnZoomOut.setOnAction(event -> zoom(-0.15));

        txtPage = new TextField("1");
        txtPage.setPrefSize(40, 30);
        txtPage.setOnAction(event -> {
            try {
                int page = Integer.parseInt(txtPage.getText());
                renderPage(((page > 0 && page <= reportPages) ? page : 1));
            } catch (NumberFormatException e) {
                renderPage(1);
            }
        });

        lblReportPages = new Label("/ 1");

        HBox menu = new HBox(5);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(5));
        menu.setPrefHeight(50.0);
        menu.getChildren().addAll(btnPrint, btnSave, btnEmail, btnFirstPage, btnBackPage, txtPage,
                lblReportPages, btnNextPage, btnLastPage, btnZoomIn, btnZoomOut);

        // This imageview will preview the pdf inside scrollpane
        report = new ImageView();
        report.setFitHeight(imageHeight);
        report.setFitWidth(imageWidth);

        // Centralizing the ImageView on Scrollpane
        Group contentGroup = new Group();
        contentGroup.getChildren().add(report);

        StackPane stack = new StackPane(contentGroup);
        stack.setAlignment(Pos.CENTER);
        stack.setStyle("-fx-background-color: gray");

        ScrollPane scroll = new ScrollPane(stack);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        BorderPane bp = new BorderPane();
        
        bp.setTop(menu);
        bp.setCenter(scroll);
        getChildren().add(bp);
        setPrefSize(1024, 768);

    }

    /**
     * Set the currentPage property value
     *
     * @param pageNumber Page number
     */
    public void setCurrentPage(int pageNumber) {
        currentPage.set(pageNumber);
    }

    /**
     * Get the currentPage property value
     *
     * @return Current page value
     */
    public int getCurrentPage() {
        return currentPage.get();
    }

    /**
     * Get the currentPage property
     *
     * @return currentPage property
     */
    public SimpleIntegerProperty currentPageProperty() {
        return currentPage;
    }

    private void mail() {
        ProgressIndicator pi = new ProgressIndicator();
        pi.setMaxSize(100, 100);
        getChildren().add(pi);
        setDisabled(true);
        
        Ui.exec(() -> {
            try {
                in.mailCallback().get().run();
                correctlyBriefed = true;
                Ui.build(this).alert("Dokument per Mail versendet");
            } catch (Exception ex) {
                Ui.handle(ex);
            } finally {
                Platform.runLater(() -> {
                    JasperFxView.this.getChildren().remove(pi);
                    JasperFxView.this.setDisabled(false);
                });
            }
        });
    }

    private void print() {
        EventQueue.invokeLater(() -> {
            try {
                JasperPrintManager.printReport(jasperPrint, true);
                correctlyBriefed = true;
            } catch (JRException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void saveToFile() {
        ExtensionFilter pdf = new ExtensionFilter("Portable Document Format", "*.pdf");
        ExtensionFilter html = new ExtensionFilter("HyperText Markup Language", "*.html");
        ExtensionFilter xml = new ExtensionFilter("eXtensible Markup Language", "*.xml");
        ExtensionFilter xls = new ExtensionFilter("Microsoft Excel 2007", "*.xls");
        ExtensionFilter xlsx = new ExtensionFilter("Microsoft Excel 2016", "*.xlsx");

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save As");
        chooser.getExtensionFilters().addAll(pdf, html, xml, xls, xlsx);
        chooser.setSelectedExtensionFilter(pdf);

        File file = chooser.showSaveDialog(view);

        if ( file != null ) {
            List<String> selectedExtension = chooser.getSelectedExtensionFilter().getExtensions();
            exportTo(file, selectedExtension.get(0));
        }
    }

    /**
     * When the user reach first or last page he cannot go forward or backward
     *
     * @param pageNumber Page number
     */
    private void disableUnnecessaryButtons(int pageNumber) {
        boolean isFirstPage = (pageNumber == 1);
        boolean isLastPage = (pageNumber == reportPages);

        btnBackPage.setDisable(isFirstPage);
        btnFirstPage.setDisable(isFirstPage);
        btnNextPage.setDisable(isLastPage);
        btnLastPage.setDisable(isLastPage);
        btnEmail.setDisable(!in.mailCallback().isPresent());
    }

    // ***********************************************
    // Export Utilities
    // ***********************************************
    /**
     * Choose the right export method for each file extension
     *
     * @param file      File
     * @param extension File extension
     */
    private void exportTo(File file, String extension) {
        switch (extension) {
            case "*.pdf":
                exportToPdf(file);
                break;
            case "*.html":
                exportToHtml(file);
                break;
            case "*.xml":
                exportToXml(file);
                break;
            case "*.xls":
                exportToXls(file);
                break;
            case "*.xlsx":
                exportToXlsx(file);
                break;
            default:
                exportToPdf(file);
        }
    }

    /**
     * Export report to html file
     */
    public void exportToHtml(File file) {
        try {
            JasperExportManager.exportReportToHtmlFile(jasperPrint, file.getPath());
        } catch (JRException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Export report to Pdf file
     */
    public void exportToPdf(File file) {
        try {
            JasperExportManager.exportReportToPdfFile(jasperPrint, file.getPath());
        } catch (JRException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Export report to old Microsoft Excel file
     */
    public void exportToXls(File file) {
        try {
            JRXlsExporter exporter = new JRXlsExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
            exporter.exportReport();
        } catch (JRException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Export report to Microsoft Excel file
     */
    public void exportToXlsx(File file) {
        try {
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
            exporter.exportReport();
        } catch (JRException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Export report to XML file
     */
    public void exportToXml(File file) {
        try {
            JasperExportManager.exportReportToXmlFile(jasperPrint, file.getPath(), false);
        } catch (JRException ex) {
            throw new RuntimeException(ex);
        }
    }

    // ***********************************************
    // Image related methods
    // ***********************************************
    /**
     * Renderize page to image
     *
     * @param pageNumber Page number
     * @throws JRException
     */
    private Image pageToImage(int pageNumber) {
        try {
            float zoom = (float)1.33;
            BufferedImage image = (BufferedImage)JasperPrintManager.printPageToImage(jasperPrint, pageNumber - 1, zoom);
            WritableImage fxImage = new WritableImage(imageHeight, imageWidth);

            return SwingFXUtils.toFXImage(image, fxImage);
        } catch (JRException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Render specific page on screen
     *
     * @param pageNumber
     */
    private void renderPage(int pageNumber) {
        setCurrentPage(pageNumber);
        disableUnnecessaryButtons(pageNumber);
        txtPage.setText(Integer.toString(pageNumber));
        report.setImage(pageToImage(pageNumber));
    }

    /**
     * Scale image from ImageView
     *
     * @param factor Zoom factor
     */
    public void zoom(double factor) {
        report.setScaleX(report.getScaleX() + factor);
        report.setScaleY(report.getScaleY() + factor);
        report.setFitHeight(imageHeight + factor);
        report.setFitWidth(imageWidth + factor);
    }

    @Override
    public void accept(JasperFxViewData in) {
        this.in = in;
        this.jasperPrint = in.jasperPrint();

        imageHeight = jasperPrint.getPageHeight() + 284;
        imageWidth = jasperPrint.getPageWidth() + 201;
        reportPages = jasperPrint.getPages().size();
        lblReportPages.setText("/ " + reportPages);

        if ( reportPages > 0 ) {
            renderPage(1);
        }
    }

    @Override
    public JasperFxViewResult getResult() {
        return new JasperFxViewResult.Builder().document(in.document()).correctlyBriefed(correctlyBriefed).build();
    }

}
