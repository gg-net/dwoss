/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.tryout;

import java.util.Date;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import eu.ggnet.dwoss.stock.entity.*;
import eu.ggnet.dwoss.stock.format.StockTransactionFormater;

/**
 *
 * @author oliver.guenther
 */
public class StockTransactionFormaterTryout extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stock s1 = new Stock(0, "Lager0");
        Stock s2 = new Stock(1, "Lager1");
        StockTransaction st1 = new StockTransaction(StockTransactionType.ROLL_IN);
        st1.setDestination(s1);
        st1.addStatus(new StockTransactionStatus(StockTransactionStatusType.PREPARED, new Date()));
        st1.addStatus(new StockTransactionStatus(StockTransactionStatusType.COMPLETED, new Date()));
        st1.setComment("Bla Bla");
        st1.addUnit(new StockUnit("123456", "Acer Aspire 4412", 1));
        st1.addUnit(new StockUnit("AA231", "Der Apfel", 0));

        WebView view = new WebView();
        view.getEngine().loadContent(StockTransactionFormater.toHtml(st1));
        primaryStage.setScene(new Scene(new BorderPane(view)));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
