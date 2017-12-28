/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.redtapext.ui.cao.document.position;

import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.format.PositionFormater;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.stock.format.StockUnitFormater;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.format.UniqueUnitFormater;
import eu.ggnet.saft.Client;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.Title;
import eu.ggnet.saft.core.ui.FxSaft;

import static javafx.scene.text.Font.font;

/**
 * Default Action
 * <p>
 * @author oliver.guenther
 */
@Title("View Position")
public class PositionViewCask extends BorderPane implements Consumer<Position> {

    private final Label head;

    private final WebView webView;

    private final ProgressIndicator progressIndicator;

    public PositionViewCask() {
        head = new Label("No Position");
        head.setFont(font(20));
        setTop(head);
        webView = FxSaft.dispatch(() -> new WebView());
        progressIndicator = new ProgressIndicator();
        setCenter(new StackPane(webView, progressIndicator));
    }

    @Override
    public void accept(Position pos) {
        head.setText("(" + pos.getId() + ") " + pos.getName());
        Ui.exec(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append(PositionFormater.toHtmlDetailed(pos)).append("<br />");
            if ( pos.getType() == PositionType.UNIT ) {
                StockUnit su = Client.lookup(StockAgent.class).findStockUnitByUniqueUnitIdEager(pos.getUniqueUnitId());
                UniqueUnit uu = Client.lookup(UniqueUnitAgent.class).findByIdEager(UniqueUnit.class, pos.getUniqueUnitId());
                if ( su != null ) sb.append(StockUnitFormater.detailedTransactionToHtml(su));
                if ( uu != null ) sb.append(UniqueUnitFormater.toHtmlPriceInformation(uu.getPrices(), uu.getPriceHistory()))
                            .append(UniqueUnitFormater.toHtmlUniqueUnitHistory(uu));
            }
            Platform.runLater(() -> {
                webView.getEngine().loadContent(sb.toString());
                progressIndicator.setVisible(false);
            });
            return null;
        });

    }

}
