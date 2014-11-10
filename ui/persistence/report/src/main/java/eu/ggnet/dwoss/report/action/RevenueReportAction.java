package eu.ggnet.dwoss.report.action;

import java.awt.event.ActionEvent;

import eu.ggnet.saft.core.authorisation.AccessableAction;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.*;
import javafx.stage.Stage;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_DAILY_BASED_REVENUE_REPORT;

/**
 * Acton that leads to the Reveue Report Page.
 * <p>
 * <p>
 * @author pascal.perau
 */
// TODO: For now disabled
public class RevenueReportAction extends AccessableAction {

    class Browser extends Region {

        final WebView browser = new WebView();

        final WebEngine webEngine = browser.getEngine();

        public Browser() {
//            webEngine.load("ADDESS_TO_THE_WEB_BACKEND");
//            getChildren().add(browser);

        }

        @Override
        protected void layoutChildren() {
            double w = getWidth();
            double h = getHeight();
            layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
        }

        @Override
        protected double computePrefWidth(double height) {
            return 750;
        }

        @Override
        protected double computePrefHeight(double width) {
            return 500;
        }
    }

    public RevenueReportAction() {
        super(CREATE_DAILY_BASED_REVENUE_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = new Stage();
                stage.setTitle("Web View");
                stage.setScene(new Scene(new Browser(), 750, 500, Color.web("#666970")));
                stage.show();
            }
        });

    }

}
