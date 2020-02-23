/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.assembly.client.support;

import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import org.slf4j.Logger;

import eu.ggnet.dwoss.mandator.api.service.ClientLocation;
import eu.ggnet.dwoss.mandator.api.service.MandatorService;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.stock.spi.ActiveStock;
import eu.ggnet.saft.core.Dl;

/**
 * Toolbar object to select the active stock.
 *
 * @author oliver.guenther
 */
public class ActiveStockSelectorToolbarPane extends VBox {

    private final ComboBox<Integer> stockComboBox;

    @Inject
    private Logger log;

    private Map<Integer, PicoStock> idToStockMap;

    public ActiveStockSelectorToolbarPane() {
        stockComboBox = new ComboBox<>();
        // Todo: reconsider copy past. (remember, don't use the same instance)
        stockComboBox.setCellFactory((view) -> new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                if ( empty ) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(Optional.ofNullable(idToStockMap.get(id)).map(ps -> ps.shortDescription).orElse("Stock does not exist"));
                }
            }
        });
        stockComboBox.setButtonCell(new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                if ( empty ) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(Optional.ofNullable(idToStockMap.get(id)).map(ps -> ps.shortDescription).orElse("Stock does not exist"));
                }
            }
        });
        stockComboBox.setPrefWidth(120);
    }

    @PostConstruct
    public void init() {
        idToStockMap = Dl.remote().optional(StockApi.class)
                .map(StockApi::findAllStocks)
                .orElse(Collections.emptyList())
                .stream().collect(Collectors.toMap((ps) -> ps.id, (ps) -> ps));
        stockComboBox.getItems().addAll(idToStockMap.keySet());
        stockComboBox.getSelectionModel().selectFirst();

        log.debug("<init> set all stocks to {}", idToStockMap);

        if ( Dl.remote().contains(MandatorService.class) ) {
            try {
                Set<InetAddress> adresses = Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
                        .flatMap(ni -> Collections.list(ni.getInetAddresses()).stream()).collect(Collectors.toCollection(HashSet::new));
                int stockId = Dl.remote().lookup(MandatorService.class).getLocationStockId(new ClientLocation(adresses));
                stockComboBox.getSelectionModel().select(stockId);
                PicoStock ps = idToStockMap.get(stockId);
                Dl.local().optional(ActiveStock.class).ifPresent(as -> as.setActiveStock(ps));
                log.debug("<init> set active stock to {}", ps);
            } catch (SocketException ex) {
                log.warn("Cound not find Network Interfaces, no selection of active stock", ex);
            }
        }

        stockComboBox.setOnAction(e -> {
            PicoStock ps = idToStockMap.get(stockComboBox.getSelectionModel().getSelectedItem());
            log.debug("setOnAction() set active stock to {}", ps);
            Dl.local().optional(ActiveStock.class).ifPresent(as -> as.setActiveStock(ps));
        });

        setAlignment(Pos.CENTER);
        getChildren().addAll(new Label("Aktives Lager:"), stockComboBox);
    }

}
